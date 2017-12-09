package com.jonathanrufino.babyonboard.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import com.jonathanrufino.babyonboard.R;
import com.jonathanrufino.babyonboard.util.SharedPreferencesHelper;
import com.jonathanrufino.babyonboard.model.Breathing;
import com.jonathanrufino.babyonboard.model.Heartbeats;
import com.jonathanrufino.babyonboard.model.Movement;
import com.jonathanrufino.babyonboard.model.Temperature;
import com.jonathanrufino.babyonboard.networking.APIClient;
import com.jonathanrufino.babyonboard.networking.APIInterface;

import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BodySignsFragment extends Fragment {

    @SuppressWarnings("unused")
    public static final String TAG = BodySignsFragment.class.getSimpleName();

    private WebView streamingWV;
    private TextView heartbeatsTV;
    private TextView temperatureTV;
    private TextView breathingTV;

    private APIInterface apiInterface;
    private Context context;
    private boolean isAbsent;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bodysigns, container, false);

        streamingWV = view.findViewById(R.id.wv_streaming);
        heartbeatsTV = view.findViewById(R.id.tv_heartbeats);
        temperatureTV = view.findViewById(R.id.tv_temperature);
        breathingTV = view.findViewById(R.id.tv_breathing);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        this.context = context;
    }

    @Override
    public void onStart() {
        super.onStart();

        streamingWV.setWebViewClient(new WebViewClient());
        streamingWV.getSettings().setLoadWithOverviewMode(true);
        streamingWV.getSettings().setUseWideViewPort(true);
        streamingWV.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);

        String ip = SharedPreferencesHelper.getSharedPreferenceString(context, "ip", "");

        if (ip == null || ip.isEmpty()) {
            updateText(heartbeatsTV, "Desconectado");
            updateText(temperatureTV, "Desconectado");
            updateText(breathingTV, "Desconectado");
        } else {
            streamingWV.loadUrl(String.format("http://%s:8081", ip));
            apiInterface = APIClient.getClient(ip).create(APIInterface.class);

            final Handler handler = new Handler();
            Timer timer = new Timer();
            TimerTask doAsynchronousTask = new TimerTask() {
                @Override
                public void run() {
                    handler.post(new Runnable() {
                        public void run() {
                            try {
                                Call<Breathing> callBreathing = apiInterface.getBreathing();
                                callBreathing.enqueue(new Callback<Breathing>() {
                                    @Override
                                    public void onResponse(@NonNull Call<Breathing> call, @NonNull Response<Breathing> response) {
                                        Breathing breathing = response.body();

                                        if (breathing != null && breathing.getStatus() != null) {
                                            switch (breathing.getStatus()) {
                                                case "breathing":
                                                    updateText(breathingTV, "Respirando");
                                                    isAbsent = false;
                                                    break;
                                                case "no_breathing":
                                                    updateText(breathingTV, "Sem Respiração");
                                                    isAbsent = false;
                                                    break;
                                                case "absent":
                                                    updateText(breathingTV, "Criança Ausente");
                                                    isAbsent = true;
                                                    break;
                                            }
                                        } else {
                                            updateText(breathingTV, "Desconectado");
                                        }
                                    }

                                    @Override
                                    public void onFailure(@NonNull Call<Breathing> call, @NonNull Throwable t) {
                                        updateText(breathingTV, "Desconectado");
                                    }
                                });

                                if (isAbsent) {
                                    updateText(heartbeatsTV, "Criança Ausente");
                                    updateText(temperatureTV, "Criança Ausente");
                                } else {
                                    getMovement();
                                    getHeartbeats();
                                    getTemperature();
                                }
                            } catch (Exception e) {
                                Toast.makeText(context, "Configure sua conexão com o berço",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            };
            timer.schedule(doAsynchronousTask, 0, 5000);
        }
    }

    private void getTemperature() {
        Call<Temperature> callTemperature = apiInterface.getTemperature();
        callTemperature.enqueue(new Callback<Temperature>() {
            @Override
            public void onResponse(@NonNull Call<Temperature> call, @NonNull Response<Temperature> response) {
                Temperature temperature = response.body();

                if (temperature != null && temperature.getTemperature() != null) {
                    updateText(temperatureTV, String.format("%s ºC", temperature.getTemperature()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<Temperature> call, @NonNull Throwable t) {
                updateText(temperatureTV, "Desconectado");
            }
        });
    }

    private void getHeartbeats() {
        Call<Heartbeats> callHeartbeats = apiInterface.getHeartbeats();
        callHeartbeats.enqueue(new Callback<Heartbeats>() {
            @Override
            public void onResponse(@NonNull Call<Heartbeats> call, @NonNull Response<Heartbeats> response) {
                Heartbeats heartbeats = response.body();

                if (heartbeats != null && heartbeats.getBeats() != 0) {
                    updateText(heartbeatsTV, String.format("%s bpm", String.valueOf(heartbeats.getBeats())));
                }
            }

            @Override
            public void onFailure(@NonNull Call<Heartbeats> call, @NonNull Throwable t) {
                updateText(heartbeatsTV, "Desconectado");
            }
        });
    }

    private void getMovement() {
        Call<Movement> callMovement = apiInterface.getMovement();
        callMovement.enqueue(new Callback<Movement>() {
            @Override
            public void onResponse(@NonNull Call<Movement> call, @NonNull Response<Movement> response) {
                Movement movement = response.body();

                if (movement != null && !movement.getMovement().isEmpty()) {
                    if (movement.getRemainingTime() > 0) {
                        updateText(breathingTV, "Berço em movimento");
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Movement> call, @NonNull Throwable t) {

            }
        });
    }

    private void updateText(TextView textView, String text) {
        if (text.length() > 10) {
            textView.setTextSize(30);
        } else {
            textView.setTextSize(40);
        }

        textView.setText(text);
    }
}
