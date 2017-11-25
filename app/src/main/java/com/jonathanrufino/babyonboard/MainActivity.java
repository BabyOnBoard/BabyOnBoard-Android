package com.jonathanrufino.babyonboard;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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
import com.jonathanrufino.babyonboard.view.IFeedback;

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

    private Queue<Noise> noiseSamples;
    private APIInterface apiInterface;
    private Noise lastNoiseRegistry = new Noise(0, false, "", "");
    private boolean isRunning = false;
    private Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = findViewById(R.id.progress_bar);

        BottomNavigationView navigation = findViewById(R.id.bottom_navigation);
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                switch (item.getItemId()) {
                    case R.id.navigation_bodysigns:
                        transaction.replace(R.id.main_container, new BodySignsFragment(), BodySignsFragment.TAG).commit();
                        return true;
                    case R.id.navigation_babycrib:
                        transaction.replace(R.id.main_container, new BabyCribFragment(), BodySignsFragment.TAG).commit();
                        return true;
                    case R.id.navigation_connection:
                        transaction.replace(R.id.main_container, new ConnectionFragment(), BodySignsFragment.TAG).commit();
                        return true;
                }
                return false;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        String ip = SharedPreferencesHelper.getSharedPreferenceString(this, "ip", "");

        if (ip.isEmpty()) {
            showFragment(ConnectionFragment.TAG);
        } else {
            showFragment(BodySignsFragment.TAG);
        }

        noiseSamples = new LinkedList<>();
        noiseSamples.add(new Noise(0, false, "00/00/00", "00:00"));
        noiseSamples.add(new Noise(0, false, "00/00/00", "00:00"));
        noiseSamples.add(new Noise(0, false, "00/00/00", "00:00"));

        apiInterface = APIClient.getClient().create(APIInterface.class);

        final Handler handler = new Handler();
        timer = new Timer();
        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                if (!isRunning) {
                    handler.post(new Runnable() {
                        public void run() {
                            try {
                                Call<Noise> callNoise = apiInterface.getNoise();
                                callNoise.enqueue(new Callback<Noise>() {
                                    @Override
                                    public void onResponse(@NonNull Call<Noise> call, @NonNull Response<Noise> response) {
                                        Noise noise = response.body();

                                        if (noise != null) {
                                            analyzeNoise(noise);
                                        } else {
                                            Toast.makeText(getApplicationContext(), "Não existem registros de ruído", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onFailure(@NonNull Call<Noise> call, @NonNull Throwable t) {
                                        Toast.makeText(getApplicationContext(), "Não foi possível conectar ao berço", Toast.LENGTH_SHORT).show();
                                        // TODO Implementar uma forma de voltar
                                        // a realizar a requisição após conectar ao berço
                                    }
                                });
                            } catch (Exception e) {
                                Toast.makeText(getApplicationContext(), "Ocorreu um erro, tente novamente", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        };
        timer.schedule(doAsynchronousTask, 0, 5000);
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
                            isRunning = false;
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        } else {
            isRunning = false;
        }
    }

    private void showFragment(String tag) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment fragment = fm.findFragmentByTag(tag);

        if (fragment == null) {
            if (tag.equals(BodySignsFragment.TAG)) {
                fragment = new BodySignsFragment();
            } else if (tag.equals(BabyCribFragment.TAG)) {
                fragment = new BabyCribFragment();
            } else if (tag.equals(ConnectionFragment.TAG)) {
                fragment = new ConnectionFragment();
            }
        }

        ft.replace(R.id.main_container, fragment, tag);
        ft.commit();
    }

    @Override
    public void showMessage(String message) {
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
}
