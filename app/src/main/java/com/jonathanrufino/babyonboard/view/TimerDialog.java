package com.jonathanrufino.babyonboard.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.widget.NumberPicker;

import com.jonathanrufino.babyonboard.R;

public class TimerDialog extends DialogFragment {

    private NumberPicker.OnValueChangeListener valueChangeListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final NumberPicker numberPicker = new NumberPicker(getActivity());
        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(10);
        numberPicker.setValue(5);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(numberPicker)
                .setTitle(getActivity().getString(R.string.start_movement))
                .setMessage(getActivity().getString(R.string.duration_time))
                .setPositiveButton(getActivity().getString(R.string.start), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        valueChangeListener.onValueChange(numberPicker,
                                numberPicker.getValue(), numberPicker.getValue());
                    }
                })
                .setNegativeButton(getActivity().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();
                    }
                });

        return builder.create();
    }

    public void setValueChangeListener(NumberPicker.OnValueChangeListener valueChangeListener) {
        this.valueChangeListener = valueChangeListener;
    }
}
