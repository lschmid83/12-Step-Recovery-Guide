package com.citex.twelve_step_recovery.ui.meetings;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.NumberPicker;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

public class MeetingFilterKilometersPickerDialog extends DialogFragment {
    private NumberPicker.OnValueChangeListener valueChangeListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Setup NumberPicker.
        final NumberPicker numberPicker = new NumberPicker(getActivity());
        final String milesUnitValues[] = { "1", "4", "8", "16" };
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(milesUnitValues.length - 1);
        numberPicker.setDisplayedValues(milesUnitValues);

        // Build Dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Select the kilometers distance filter:");
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