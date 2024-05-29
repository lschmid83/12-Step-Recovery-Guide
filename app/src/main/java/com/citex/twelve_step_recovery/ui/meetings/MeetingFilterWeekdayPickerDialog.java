package com.citex.twelve_step_recovery.ui.meetings;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.NumberPicker;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

public class MeetingFilterWeekdayPickerDialog extends DialogFragment {
    private NumberPicker.OnValueChangeListener valueChangeListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Setup NumberPicker.
        final NumberPicker numberPicker = new NumberPicker(getActivity());
        final String formats[] = { "Any Day", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday" };
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(formats.length - 1);
        numberPicker.setDisplayedValues(formats);

        // Set default value.
        int defaultValue = getArguments().getInt("default_value");
        numberPicker.setValue(defaultValue);

        // Build Dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Select the weekday filter:");
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