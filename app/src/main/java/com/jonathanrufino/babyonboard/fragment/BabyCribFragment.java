package com.jonathanrufino.babyonboard.fragment;

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

import com.jonathanrufino.babyonboard.R;
import com.jonathanrufino.babyonboard.view.TimerDialog;
import com.jonathanrufino.babyonboard.model.BabyCrib;
import com.jonathanrufino.babyonboard.networking.APIClient;
import com.jonathanrufino.babyonboard.networking.APIInterface;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BabyCribFragment extends Fragment implements NumberPicker.OnValueChangeListener, View.OnClickListener {

    @SuppressWarnings("unused")
    public static final String TAG = BabyCribFragment.class.getSimpleName();

    private CardView vibrationCV;
    private CardView frontCV;
    private CardView assisYCV;

    private APIInterface apiInterface;
    private String movement;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_babycrib, container, false);

        vibrationCV = view.findViewById(R.id.vibration_cv);
        vibrationCV.setOnClickListener(this);
        frontCV = view.findViewById(R.id.front_cv);
        frontCV.setOnClickListener(this);
        assisYCV = view.findViewById(R.id.side_cv);
        assisYCV.setOnClickListener(this);

        return view;
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
                    Toast.makeText(getActivity(), "Berço em movimentação", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Não foi possível iniciar a movimentação.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<BabyCrib> call, @NonNull Throwable t) {
                Toast.makeText(getActivity(), "Não foi possível conectar ao berço.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void showNumberPicker(View view) {
        TimerDialog newFragment = new TimerDialog();
        newFragment.setValueChangeListener(this);
        newFragment.show(getActivity().getSupportFragmentManager(), "timer");
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

        showNumberPicker(view);
    }

    @Override
    public void onStart() {
        super.onStart();

        apiInterface = APIClient.getClient().create(APIInterface.class);

        Call<BabyCrib> callMovement = apiInterface.getMovement();
        callMovement.enqueue(new Callback<BabyCrib>() {
            @Override
            public void onResponse(@NonNull Call<BabyCrib> call, @NonNull Response<BabyCrib> response) {
                BabyCrib movement = response.body();

                if (movement != null) {
                    Toast.makeText(getActivity(), "O besço já está em movimento, aguarde.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Não existem registros de movimentação.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<BabyCrib> call, @NonNull Throwable t) {
                Toast.makeText(getActivity(), "Não foi possível conectar ao berço.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
