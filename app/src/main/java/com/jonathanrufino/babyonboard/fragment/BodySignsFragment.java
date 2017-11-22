package com.jonathanrufino.babyonboard.fragment;

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
import com.jonathanrufino.babyonboard.model.Breathing;
import com.jonathanrufino.babyonboard.model.Heartbeats;
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bodysigns, container, false);

        streamingWV = view.findViewById(R.id.wv_streaming);
        heartbeatsTV = view.findViewById(R.id.tv_heartbeats);
        temperatureTV = view.findViewById(R.id.tv_temperature);
        breathingTV = view.findViewById(R.id.tv_breathing);

        apiInterface = APIClient.getClient().create(APIInterface.class);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        streamingWV.setWebViewClient(new WebViewClient());
        streamingWV.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        streamingWV.loadUrl("http://0.0.0.0:8081");

        final Handler handler = new Handler();
        Timer timer = new Timer();
        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            Call<Heartbeats> callHeartbeats = apiInterface.getHeartbeats();
                            callHeartbeats.enqueue(new Callback<Heartbeats>() {
                                @Override
                                public void onResponse(@NonNull Call<Heartbeats> call, @NonNull Response<Heartbeats> response) {
                                    Heartbeats heartbeats = response.body();
                                    if (heartbeats != null) {
                                        heartbeatsTV.setText(String.format("%s bpm", String.valueOf(heartbeats.getBeats())));
                                    } else {
                                        Toast.makeText(getActivity(), "Não existem registros de batimentos cardíacos.", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(@NonNull Call<Heartbeats> call, @NonNull Throwable t) {
                                    Toast.makeText(getActivity(), "Não foi possível conectar ao berço.", Toast.LENGTH_SHORT).show();

                                }
                            });

                            Call<Temperature> callTemperature = apiInterface.getTemperature();
                            callTemperature.enqueue(new Callback<Temperature>() {
                                @Override
                                public void onResponse(@NonNull Call<Temperature> call, @NonNull Response<Temperature> response) {
                                    Temperature temperature = response.body();
                                    if (temperature != null) {
                                        temperatureTV.setText(String.format("%s ºC", temperature.getTemperature()));
                                    } else {
                                        Toast.makeText(getActivity(), "Não existem registros de temperatura", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(@NonNull Call<Temperature> call, @NonNull Throwable t) {
                                    Toast.makeText(getActivity(), "Não foi possível conectar ao berço", Toast.LENGTH_SHORT).show();
                                }
                            });

                            Call<Breathing> callBreathing = apiInterface.getBreathing();
                            callBreathing.enqueue(new Callback<Breathing>() {
                                @Override
                                public void onResponse(@NonNull Call<Breathing> call, @NonNull Response<Breathing> response) {
                                    Breathing breathing = response.body();
                                    if (breathing != null) {
                                        if (breathing.isBreathing()) {
                                            breathingTV.setText("Respirando");
                                        } else {
                                            breathingTV.setText("Sem Respiração");
                                        }
                                    } else {
                                        Toast.makeText(getActivity(), "Não existem registros de respiração.", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(@NonNull Call<Breathing> call, @NonNull Throwable t) {
                                    Toast.makeText(getActivity(), "Não foi possível conectar ao berço.", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } catch (Exception e) {
                            Toast.makeText(getActivity(), "Ocorreu um erro, tente novamente", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        };
        timer.schedule(doAsynchronousTask, 0, 5000);
    }
}
