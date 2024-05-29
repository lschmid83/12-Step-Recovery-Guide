package com.citex.twelve_step_recovery.ui.meetings;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.citex.twelve_step_recovery.MainActivity;
import com.citex.twelve_step_recovery.R;
import com.citex.twelve_step_recovery.databinding.FragmentMeetingMapFilterBinding;

import java.util.Arrays;
import java.util.Objects;

public class MeetingFilterFragment extends Fragment {

    private MeetingFilterViewModel meetingMapFilterViewModel;
    private FragmentMeetingMapFilterBinding binding;
    private MeetingFilter meetingFilter;
    public static boolean loaded = false;

    private final String[] weekdayValues = { "Any Day", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday" };
    private final String[] searchUnitValues = { "Miles", "Kilometers" };
    private final String[] milesUnitValues = { "1", "3", "5", "10" };
    private final String[] kilometersUnitValues = { "1", "4", "8", "16" };

    /**
     * Creates and returns the view hierarchy associated with the fragment.
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     * @return Return the View for the fragment's UI, or null.
     */
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        // Initialize database.
        meetingFilter = new MeetingFilter(getContext());
        loaded = true;

        // Setup ActionBar.
        if (getActivity() != null) {
            ActionBar mActionBar = ((MainActivity) getActivity()).getSupportActionBar();
            if (mActionBar != null) {
                mActionBar.setIcon(R.drawable.ic_filter_24dp);
                mActionBar.setDisplayUseLogoEnabled(true);
                mActionBar.setDisplayShowHomeEnabled(true);
                mActionBar.setDisplayShowCustomEnabled(false);
                mActionBar.setTitle("  " + getResources().getString(R.string.meetings_filter));
            }
        }

        // Setup View Binding.
        binding = FragmentMeetingMapFilterBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Setup ViewModel.
        meetingMapFilterViewModel = new ViewModelProvider(this).get(MeetingFilterViewModel.class);

        // Get AA filter value from database.
        int switchAaDbValue = meetingFilter.getProgramDb(MeetingFilterDbContract.MeetingFilterEntry.COLUMN_NAME_AA);
        meetingMapFilterViewModel.setProgramAA(switchAaDbValue != 0);

        final SwitchCompat aaFilter = binding.switchFilterAa;
        meetingMapFilterViewModel.getProgramAA().observe(getViewLifecycleOwner(), aaFilter::setChecked);

        // Get CA filter value from database.
        int switchCaDbValue = meetingFilter.getProgramDb(MeetingFilterDbContract.MeetingFilterEntry.COLUMN_NAME_CA);
        meetingMapFilterViewModel.setProgramCA(switchCaDbValue != 0);

        final SwitchCompat caFilter = binding.switchFilterCa;
        meetingMapFilterViewModel.getProgramCA().observe(getViewLifecycleOwner(), caFilter::setChecked);

        // Get NA filter value from database.
        int switchNaDbValue = meetingFilter.getProgramDb(MeetingFilterDbContract.MeetingFilterEntry.COLUMN_NAME_NA);
        meetingMapFilterViewModel.setProgramNA(switchNaDbValue != 0);

        final SwitchCompat naFilter = binding.switchFilterNa;
        meetingMapFilterViewModel.getProgramNA().observe(getViewLifecycleOwner(), naFilter::setChecked);

        // Get NA filter value from database.
        int switchOaDbValue = meetingFilter.getProgramDb(MeetingFilterDbContract.MeetingFilterEntry.COLUMN_NAME_OA);
        meetingMapFilterViewModel.setProgramOA(switchOaDbValue != 0);

        final SwitchCompat oaFilter = binding.switchFilterOa;
        meetingMapFilterViewModel.getProgramOA().observe(getViewLifecycleOwner(), oaFilter::setChecked);

        // Get weekday value from database.
        int textWeekdayDbValue = meetingFilter.getWeekdayDb();
        meetingMapFilterViewModel.setWeekday(weekdayValues[textWeekdayDbValue]);

        final TextView textWeekdayFilter = binding.textFilterWeekday;
        meetingMapFilterViewModel.getWeekday().observe(getViewLifecycleOwner(), textWeekdayFilter::setText);

        // Get search units value from database.
        String textSearchUnitsDbValue = meetingFilter.getSearchUnitsDb();
        meetingMapFilterViewModel.setSearchUnits(textSearchUnitsDbValue);

        final TextView textSearchUnitsFilter = binding.textFilterSearchUnits;
        meetingMapFilterViewModel.getSearchUnits().observe(getViewLifecycleOwner(), textSearchUnitsFilter::setText);

        // Get search range value from database.
        int textSearchRangeDbValue = meetingFilter.getSearchRangeDb();
        String searchRangeUnit = "miles";
        if(textSearchUnitsDbValue.equals("Kilometers"))
            searchRangeUnit = "km";
        meetingMapFilterViewModel.setSearchRange(textSearchRangeDbValue + " " + searchRangeUnit);

        final TextView textSearchRangeFilter = binding.textFilterSearchRange;
        meetingMapFilterViewModel.getSearchRange().observe(getViewLifecycleOwner(), textSearchRangeFilter::setText);

        // Get Wheelchair accessibility filter value from database.
        int switchWheelchairAccessibilityDbValue = meetingFilter.getProgramDb(MeetingFilterDbContract.MeetingFilterEntry.COLUMN_NAME_WHEELCHAIR);
        meetingMapFilterViewModel.setWheelchairAccessibility(switchWheelchairAccessibilityDbValue != 0);

        final SwitchCompat switchWheelchairAccessibility = binding.switchFilterWheelchair;
        meetingMapFilterViewModel.getWheelchairAccessibility().observe(getViewLifecycleOwner(), switchWheelchairAccessibility::setChecked);

        return root;
    }

    /**
     * Called immediately after onCreateView() has returned, but before any saved state has been
     * restored in to the view.
     * @param view The View returned by onCreateView().
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous
     *                           saved state as given here. This value may be null.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        // AA program switch.
        SwitchCompat switchAA = view.findViewById(R.id.switch_filter_aa);
        switchAA.setOnCheckedChangeListener((buttonView, isChecked) -> {
            meetingFilter.setProgramDb(MeetingFilterDbContract.MeetingFilterEntry.COLUMN_NAME_AA, isChecked);
            meetingMapFilterViewModel.setProgramAA(isChecked);
        });

        // CA program switch.
        SwitchCompat switchCA = view.findViewById(R.id.switch_filter_ca);
        switchCA.setOnCheckedChangeListener((buttonView, isChecked) -> {
            meetingFilter.setProgramDb(MeetingFilterDbContract.MeetingFilterEntry.COLUMN_NAME_CA, isChecked);
            meetingMapFilterViewModel.setProgramCA(isChecked);
        });

        // NA program switch.
        SwitchCompat switchNA = view.findViewById(R.id.switch_filter_na);
        switchNA.setOnCheckedChangeListener((buttonView, isChecked) -> {
            meetingFilter.setProgramDb(MeetingFilterDbContract.MeetingFilterEntry.COLUMN_NAME_NA, isChecked);
            meetingMapFilterViewModel.setProgramNA(isChecked);
        });

        // OA program switch.
        SwitchCompat switchOA = view.findViewById(R.id.switch_filter_oa);
        switchOA.setOnCheckedChangeListener((buttonView, isChecked) -> {
            meetingFilter.setProgramDb(MeetingFilterDbContract.MeetingFilterEntry.COLUMN_NAME_OA, isChecked);
            meetingMapFilterViewModel.setProgramOA(isChecked);
        });

        // Display weekday number picker on click.
        TextView textFilterWeekday = view.findViewById(R.id.text_filter_weekday);
        textFilterWeekday.setOnClickListener(v -> {

            // Show weekday filter picker dialog.
            MeetingFilterWeekdayPickerDialog meetingFilterWeekdayPickerDialog = new MeetingFilterWeekdayPickerDialog();
            if (getActivity() != null) {

                // Set default value.
                Bundle args = new Bundle();
                args.putInt("default_value", Arrays.asList(weekdayValues).indexOf(meetingMapFilterViewModel.getWeekday().getValue()));
                meetingFilterWeekdayPickerDialog.setArguments(args);

                meetingFilterWeekdayPickerDialog.show(getActivity().getSupportFragmentManager(), "Weekday Picker");
            }

            // Set OnValueChanged listener.
            meetingFilterWeekdayPickerDialog.setValueChangeListener((numberPicker, i, i1) -> {
                meetingFilter.setWeekdayDb(i);
                meetingMapFilterViewModel.setWeekday(weekdayValues[i]);
            });
        });

        // Display search units picker on click.
        TextView textFilterSearchUnits = view.findViewById(R.id.text_filter_search_units);
        if(getActivity() != null) {
            textFilterSearchUnits.setOnClickListener(v -> new MaterialDialog.Builder(getActivity())
                    .items(searchUnitValues)
                    .itemsCallback((dialog, view1, i, text) -> {

                        if (meetingFilter.getSearchUnitsDb().equals("Miles") && searchUnitValues[i].equals("Kilometers")) {

                            // Miles to KM.
                            if (meetingFilter.getSearchRangeDb() == 1)
                                meetingFilter.setSearchRangeDb(1);
                            else if (meetingFilter.getSearchRangeDb() == 3)
                                meetingFilter.setSearchRangeDb(4);
                            else if (meetingFilter.getSearchRangeDb() == 5)
                                meetingFilter.setSearchRangeDb(8);
                            else if (meetingFilter.getSearchRangeDb() == 10)
                                meetingFilter.setSearchRangeDb(16);
                        } else {
                            // KM to miles.
                            if (meetingFilter.getSearchRangeDb() == 1)
                                meetingFilter.setSearchRangeDb(1);
                            else if (meetingFilter.getSearchRangeDb() == 4)
                                meetingFilter.setSearchRangeDb(3);
                            else if (meetingFilter.getSearchRangeDb() == 8)
                                meetingFilter.setSearchRangeDb(5);
                            else if (meetingFilter.getSearchRangeDb() == 16)
                                meetingFilter.setSearchRangeDb(10);
                        }

                        meetingMapFilterViewModel.setSearchUnits(searchUnitValues[i]);
                        meetingFilter.setSearchUnitsDb(searchUnitValues[i]);

                        String unit = "miles";
                        if (meetingFilter.getSearchUnitsDb().equals("Kilometers"))
                            unit = "km";
                        meetingMapFilterViewModel.setSearchRange(meetingFilter.getSearchRangeDb() + " " + unit);
                    }).itemsGravity(GravityEnum.CENTER)
                    .titleGravity(GravityEnum.CENTER)
                    .show());
        }

        // Display search range picker on click.
        TextView textFilterSearchRange = view.findViewById(R.id.text_filter_search_range);
        textFilterSearchRange.setOnClickListener(v -> {

            if(Objects.equals(meetingMapFilterViewModel.getSearchUnits().getValue(), "Miles")) {

                // Show search units filter picker dialog.
                MeetingFilterMilesPickerDialog meetingFilterMilesPickerDialog = new MeetingFilterMilesPickerDialog();
                if (getActivity() != null) {

                    // Set default value.
                    Bundle args = new Bundle();
                    args.putInt("default_value", Arrays.asList(milesUnitValues).indexOf(meetingMapFilterViewModel.getSearchUnits().getValue()));
                    meetingFilterMilesPickerDialog.setArguments(args);

                    meetingFilterMilesPickerDialog.show(getActivity().getSupportFragmentManager(), "Search Miles Picker");
                }

                // Set OnValueChanged listener.
                meetingFilterMilesPickerDialog.setValueChangeListener((numberPicker, i, i1) -> {
                    meetingFilter.setSearchRangeDb(Integer.parseInt(milesUnitValues[i]));
                    meetingMapFilterViewModel.setSearchRange(milesUnitValues[i] + " miles");
                });
            }
            else {

                // Show search units filter picker dialog.
                MeetingFilterKilometersPickerDialog meetingFilterKilometersPickerDialog = new MeetingFilterKilometersPickerDialog();
                if (getActivity() != null) {

                    // Set default value.
                    Bundle args = new Bundle();
                    args.putInt("default_value", Arrays.asList(milesUnitValues).indexOf(meetingMapFilterViewModel.getSearchUnits().getValue()));
                    meetingFilterKilometersPickerDialog.setArguments(args);

                    meetingFilterKilometersPickerDialog.show(getActivity().getSupportFragmentManager(), "Search Kilometers Picker");
                }

                // Set OnValueChanged listener.
                meetingFilterKilometersPickerDialog.setValueChangeListener((numberPicker, i, i1) -> {
                    meetingFilter.setSearchRangeDb(Integer.parseInt(kilometersUnitValues[i]));
                    meetingMapFilterViewModel.setSearchRange(kilometersUnitValues[i] + " km");
                });
            }
        });

        // Wheelchair accessibility switch.
        SwitchCompat switchWheelchair = view.findViewById(R.id.switch_filter_wheelchair);
        switchWheelchair.setOnCheckedChangeListener((buttonView, isChecked) -> {
            meetingFilter.setProgramDb(MeetingFilterDbContract.MeetingFilterEntry.COLUMN_NAME_WHEELCHAIR, isChecked);
            meetingMapFilterViewModel.setWheelchairAccessibility(isChecked);
        });
    }


    /**
     * Called when the view previously created by onCreateView() has been detached from the
     * fragment. The next time the fragment needs to be displayed, a new view will be created.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}