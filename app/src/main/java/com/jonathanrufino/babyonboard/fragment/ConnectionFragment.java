package com.jonathanrufino.babyonboard.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.jonathanrufino.babyonboard.R;
import com.jonathanrufino.babyonboard.SharedPreferencesHelper;
import com.jonathanrufino.babyonboard.view.IFeedback;

public class ConnectionFragment extends Fragment {

    @SuppressWarnings("unused")
    public static final String TAG = ConnectionFragment.class.getSimpleName();

    private TextInputEditText ipInput;
    private Button connectB;

    private Context context;
    private IFeedback feedback;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_connection, container, false);

        context = getActivity();

        ipInput = view.findViewById(R.id.et_ip_address);
        connectB = view.findViewById(R.id.connect_b);
        connectB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                feedback.showProgress();

                String ip = ipInput.getText().toString();

                if (ip.isEmpty()) {
                    feedback.hideProgress();
                    feedback.showMessage(context.getString(R.string.empty_ip));
                } else {

                    SharedPreferencesHelper.setSharedPreferenceString(context, "ip", ip);
//                    feedback.hideProgress();
                }
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            feedback = (IFeedback) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement IDisplayFeedback");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        feedback = null;
    }
}
