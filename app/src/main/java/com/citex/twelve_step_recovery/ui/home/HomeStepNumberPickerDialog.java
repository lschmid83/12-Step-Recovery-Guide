package com.citex.twelve_step_recovery.ui.home;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.NumberPicker;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

public class HomeStepNumberPickerDialog extends DialogFragment {
    private NumberPicker.OnValueChangeListener valueChangeListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Setup NumberPicker.
        final NumberPicker numberPicker = new NumberPicker(getActivity());
        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(12);

        // Set default value.
        int defaultValue = getArguments().getInt("default_value");
        numberPicker.setValue(defaultValue);

        // Build Dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Select the step you are on:");
        builder.setPositiveButton("OK", (dialog, which) -> valueChangeListener.onValueChange(numberPicker,
                numberPicker.getValue(), numberPicker.getValue()));
        builder.setNegativeButton("CANCEL", (dialog, which) -> {
        });
        builder.setView(numberPicker);
        return builder.create();
    }

    public void setValueChangeListener(NumberPicker.OnValueChangeListener valueChangeListener) {
        this.valueChangeListener = valueChangeListener;
    }
}