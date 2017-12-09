package com.jonathanrufino.babyonboard.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.jonathanrufino.babyonboard.MainActivity;
import com.jonathanrufino.babyonboard.R;
import com.jonathanrufino.babyonboard.model.BabyCrib;
import com.jonathanrufino.babyonboard.model.Movement;
import com.jonathanrufino.babyonboard.networking.APIClient;
import com.jonathanrufino.babyonboard.networking.APIInterface;
import com.jonathanrufino.babyonboard.util.SharedPreferencesHelper;
import com.jonathanrufino.babyonboard.util.TimerDialog;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BabyCribFragment extends Fragment implements NumberPicker.OnValueChangeListener, View.OnClickListener {

    @SuppressWarnings("unused")
    public static final String TAG = BabyCribFragment.class.getSimpleName();

    private CardView vibrationCV;
    private CardView frontCV;
    private CardView sideCV;

    private APIInterface apiInterface;
    private Context context;
    private String movement;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_babycrib, container, false);

        vibrationCV = view.findViewById(R.id.vibration_cv);
        vibrationCV.setOnClickListener(this);
        frontCV = view.findViewById(R.id.front_cv);
        frontCV.setOnClickListener(this);
        sideCV = view.findViewById(R.id.side_cv);
        sideCV.setOnClickListener(this);

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

        String ip = SharedPreferencesHelper.getSharedPreferenceString(context, context.getString(R.string.shared_pref_ip), "");

        if (ip != null && !ip.isEmpty()) {
            apiInterface = APIClient.getClient(ip).create(APIInterface.class);

            Call<Movement> callMovement = apiInterface.getMovement();
            callMovement.enqueue(new Callback<Movement>() {
                @Override
                public void onResponse(@NonNull Call<Movement> call, @NonNull Response<Movement> response) {
                    Movement movement = response.body();

                    if (movement != null) {
                        if (movement.getRemainingTime() > 0) {
                            getDialog().show();

                            switch (movement.getMovement()) {
                                case "vibration":
                                    disableMovementControl(true);
                                    break;
                                case "front":
                                    disableMovementControl(true);
                                    break;
                                case "side":
                                    disableMovementControl(true);
                                    break;
                            }
                        } else {
                            disableMovementControl(false);
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Movement> call, @NonNull Throwable t) {
                    Toast.makeText(context, "Não foi possível conectar ao berço.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.vibration_cv:
                movement = "vibration";
                break;
            case R.id.front_cv:
                movement = "front";
                break;
            case R.id.side_cv:
                movement = "side";
                break;
            default:
                movement = "resting";
                break;
        }

        showNumberPicker();
    }

    @Override
    public void onValueChange(NumberPicker numberPicker, int i, int i1) {
        BabyCrib babyCrib = new BabyCrib(movement, numberPicker.getValue());

        Call<BabyCrib> callMovement = apiInterface.setMovement(babyCrib);
        callMovement.enqueue(new Callback<BabyCrib>() {
            @Override
            public void onResponse(@NonNull Call<BabyCrib> call, @NonNull Response<BabyCrib> response) {
                BabyCrib movement = response.body();

                if (movement != null && !movement.getStatus().equals("resting")) {
                    Toast.makeText(context, "Berço em movimentação", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Não foi possível iniciar a movimentação.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<BabyCrib> call, @NonNull Throwable t) {
                Toast.makeText(context, "Não foi possível conectar ao berço.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void showNumberPicker() {
        TimerDialog newFragment = new TimerDialog();
        newFragment.setValueChangeListener(this);
        newFragment.show(((MainActivity) context).getSupportFragmentManager(), "timer");
    }

    private AlertDialog getDialog() {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context)
                .setTitle("Berço em Movimento")
                .setMessage("O berço já está em movimento, aguarde o termíno da movimentação")
                .setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
        return alertBuilder.create();
    }

    private void disableMovementControl(boolean disable) {
        if (disable) {
            vibrationCV.setEnabled(false);
            frontCV.setEnabled(false);
            sideCV.setEnabled(false);
        } else {
            vibrationCV.setEnabled(true);
            frontCV.setEnabled(true);
            sideCV.setEnabled(true);
        }
    }
}
