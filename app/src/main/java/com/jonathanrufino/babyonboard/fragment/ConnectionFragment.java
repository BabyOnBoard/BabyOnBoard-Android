package com.jonathanrufino.babyonboard.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.jonathanrufino.babyonboard.R;
import com.jonathanrufino.babyonboard.model.Temperature;
import com.jonathanrufino.babyonboard.networking.APIClient;
import com.jonathanrufino.babyonboard.networking.APIInterface;
import com.jonathanrufino.babyonboard.util.IFeedback;
import com.jonathanrufino.babyonboard.util.SharedPreferencesHelper;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConnectionFragment extends Fragment {

    @SuppressWarnings("unused")
    public static final String TAG = ConnectionFragment.class.getSimpleName();

    private TextInputLayout ipLayout;
    private TextInputEditText ipInput;
    private TextView connectedIpTV;
    private Button connectB;

    private IFeedback iFeedback;
    private Context context;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_connection, container, false);

        ipLayout = view.findViewById(R.id.til_ip_address);
        ipInput = view.findViewById(R.id.et_ip_address);
        connectedIpTV = view.findViewById(R.id.connected_tv);
        connectB = view.findViewById(R.id.connect_b);
        connectB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iFeedback.showProgress();

                if (connectB.getText() == context.getString(R.string.disconnect)) {
                    SharedPreferencesHelper.setSharedPreferenceString(context, context.getString(R.string.shared_pref_ip), "");

                    connectedIpTV.setVisibility(View.GONE);
                    connectedIpTV.setText("");
                    ipLayout.setVisibility(View.VISIBLE);
                    connectB.setText(context.getString(R.string.connect));

                    iFeedback.notifyIPChanged();
                } else {
                    String ip = ipInput.getText().toString();

                    if (ip.isEmpty()) {
                        iFeedback.showToast(context.getString(R.string.empty_ip));
                    } else {
                        checkAPI(ip);
                    }
                }

                iFeedback.hideProgress();
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        this.context = context;

        try {
            iFeedback = (IFeedback) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement IDisplayFeedback");
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        String ip = SharedPreferencesHelper.getSharedPreferenceString(context, "ip", "");

        if (ip == null || ip.isEmpty()) {
            ipLayout.setVisibility(View.VISIBLE);
            connectedIpTV.setVisibility(View.GONE);
        } else {
            ipLayout.setVisibility(View.GONE);
            connectedIpTV.setVisibility(View.VISIBLE);
            connectedIpTV.setText(ip);
            connectB.setText(context.getString(R.string.disconnect));
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        iFeedback = null;
    }

    private void checkAPI(final String ip) {
        try {
            APIInterface apiInterface = APIClient.getClient(ip).create(APIInterface.class);

            Call<Temperature> callTemperature = apiInterface.getTemperature();
            callTemperature.enqueue(new Callback<Temperature>() {
                @Override
                public void onResponse(@NonNull Call<Temperature> call, @NonNull Response<Temperature> response) {
                    Temperature temperature = response.body();

                    if (temperature != null) {
                        SharedPreferencesHelper.setSharedPreferenceString(context, context.getString(R.string.shared_pref_ip), ip);

                        ipLayout.setVisibility(View.GONE);
                        connectedIpTV.setVisibility(View.VISIBLE);
                        connectedIpTV.setText(ip);
                        connectB.setText(context.getString(R.string.disconnect));

                        iFeedback.showToast("Conexão realizada com sucesso");
                        iFeedback.notifyIPChanged();
                    }

                    iFeedback.hideProgress();
                }

                @Override
                public void onFailure(@NonNull Call<Temperature> call, @NonNull Throwable t) {
                    iFeedback.hideProgress();
                    getDialog().show();
                }
            });
        } catch (IllegalArgumentException e) {
            iFeedback.hideProgress();
        }
    }

    private AlertDialog getDialog() {
        AlertDialog.Builder alertBuild = new AlertDialog.Builder(context)
                .setTitle("Não foi possível conectar com o berço")
                .setMessage("Certfique-se que o endereço do berço informado está correto ou que o berço está ligado.")
                .setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
        return alertBuild.create();
    }
}