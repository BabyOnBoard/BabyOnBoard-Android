package com.jonathanrufino.babyonboard;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.jonathanrufino.babyonboard.fragment.BabyCribFragment;
import com.jonathanrufino.babyonboard.fragment.BodySignsFragment;
import com.jonathanrufino.babyonboard.fragment.ConnectionFragment;
import com.jonathanrufino.babyonboard.model.Noise;
import com.jonathanrufino.babyonboard.networking.APIClient;
import com.jonathanrufino.babyonboard.networking.APIInterface;
import com.jonathanrufino.babyonboard.util.IFeedback;
import com.jonathanrufino.babyonboard.util.SharedPreferencesHelper;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements IFeedback {

    @SuppressWarnings("unused")
    public static final String TAG = MainActivity.class.getSimpleName();

    private ProgressBar progressBar;

    private APIInterface apiInterface;
    private Queue<Noise> noiseSamples;
    private Noise lastNoiseRegistry = new Noise(0, false, "");
    private boolean isRunning = false;
    private Timer timer;
    private String ip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = findViewById(R.id.progress_bar);

        BottomNavigationView navigation = findViewById(R.id.bottom_navigation);
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_bodysigns:
                        displayFragment(new BodySignsFragment());
                        return true;
                    case R.id.navigation_babycrib:
                        displayFragment(new BabyCribFragment());
                        return true;
                    case R.id.navigation_connection:
                        displayFragment(new ConnectionFragment());
                        return true;
                }
                return false;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        showProgress();

        ip = SharedPreferencesHelper.getSharedPreferenceString(this, getApplicationContext().getString(R.string.shared_pref_ip), "");

        if (ip == null || ip.isEmpty()) {
            displayFragment(new ConnectionFragment());
        } else {
            displayFragment(new BodySignsFragment());

            apiInterface = APIClient.getClient(ip).create(APIInterface.class);
            startNoiseAquisition();
        }

        hideProgress();
    }

    private void startNoiseAquisition() {
        noiseSamples = new LinkedList<>();
        noiseSamples.add(new Noise(0, false, "00/00/00"));
        noiseSamples.add(new Noise(0, false, "00/00/00"));
        noiseSamples.add(new Noise(0, false, "00/00/00"));

        final Handler handler = new Handler();
        timer = new Timer();
        TimerTask requestTask = new TimerTask() {
            @Override
            public void run() {
                if (!isRunning) {
                    handler.post(new Runnable() {
                        public void run() {
                            try {
                                Call<Noise> callNoise = apiInterface.getNoise();
                                callNoise.enqueue(new Callback<Noise>() {
                                    @Override
                                    public void onResponse(@NonNull Call<Noise> call,
                                                           @NonNull Response<Noise> response) {
                                        Noise noise = response.body();

                                        if (noise != null && noise.getDatetime() != null) {
                                            analyzeNoise(noise);
                                        }
                                    }

                                    @Override
                                    public void onFailure(@NonNull Call<Noise> call,
                                                          @NonNull Throwable t) {
                                        timer.cancel();

                                        AlertDialog.Builder builder = new AlertDialog.Builder(
                                                MainActivity.this)
                                                .setTitle("Falha na conexão")
                                                .setMessage("Não foi possível conectar com o berço. Verifique se a identificação informada está correta ou se o berço está ligado.")
                                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        dialogInterface.dismiss();
                                                    }
                                                });
                                        AlertDialog alert = builder.create();
                                        alert.show();
                                    }
                                });
                            } catch (Exception e) {
                                showToast("Ocorreu um erro, tente novamente");
                            }
                        }
                    });
                }
            }
        };
        timer.schedule(requestTask, 0, 5000);
    }

    private void analyzeNoise(Noise newSample) {
        if (newSample.getId() == lastNoiseRegistry.getId()) {
            return;
        }

        lastNoiseRegistry = newSample;
        noiseSamples.add(newSample);
        noiseSamples.remove();

        Iterator it = noiseSamples.iterator();
        int noiseCounter = 0;

        while (it.hasNext()) {
            Noise noise = (Noise) it.next();

            if (noise.isCrying()) {
                noiseCounter++;
            }
        }

        if (noiseCounter == 3) {
            isRunning = true;

            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Bebê Chorando")
                    .setMessage("Detectamos um possível choro do bebê.")
                    .setNegativeButton("Ignorar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            timer.cancel();
                        }
                    })
                    .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            noiseSamples.remove();
                            noiseSamples.remove();
                            noiseSamples.remove();
                            isRunning = false;
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        } else {
            isRunning = false;
        }
    }

    @Override
    public void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void displayFragment(Fragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.main_container, fragment);
        ft.commit();
    }

    @Override
    public void notifyIPChanged() {
        ip = SharedPreferencesHelper.getSharedPreferenceString(this, getApplicationContext().getString(R.string.shared_pref_ip), "");

        if (ip == null || ip.isEmpty()) {
            timer.cancel();
        } else {
            apiInterface = APIClient.getClient(ip).create(APIInterface.class);
            startNoiseAquisition();
        }
    }
}
