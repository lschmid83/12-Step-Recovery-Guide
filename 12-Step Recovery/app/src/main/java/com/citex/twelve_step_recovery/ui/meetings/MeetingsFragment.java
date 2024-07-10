package com.citex.twelve_step_recovery.ui.meetings;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.citex.twelve_step_recovery.MainActivity;
import com.citex.twelve_step_recovery.R;
import com.citex.twelve_step_recovery.database.DbHelper;
import com.citex.twelve_step_recovery.databinding.FragmentMeetingsBinding;
import com.citex.twelve_step_recovery.ui.home.HomeDbContract;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

public class MeetingsFragment extends Fragment {

    private FragmentMeetingsBinding binding;
    private DbHelper dbHelper;
    private String countryCode;
    private String[] tabs = new String[] {"Map", "London", "Worldwide"};
    private MeetingMapFragment meetingMapFragment;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        getContext().getTheme().applyStyle(R.style.Theme_Meetings, true);

        // Initialize database.
        dbHelper = new DbHelper(getContext());

        // Get country code.
        countryCode = getCountryCodeDb();

        // If not GB only show Worldwide tab.
        if(!countryCode.equals("GB")) {
            tabs = new String[] {"Worldwide"};
        }

        // Setup View Binding.
        binding = FragmentMeetingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Set ViewPager for tab view.
        if(getActivity() != null) {
            binding.viewPager.setAdapter(new ViewPagerFragmentAdapter(getActivity()));
            new TabLayoutMediator(binding.tabLayout, binding.viewPager,
                    (tab, position) -> tab.setText(tabs[position])).attach();
        }

        // Disable swiping. (Affects WebView scrolling).
        binding.viewPager.setUserInputEnabled(false);

        return root;
    }

    @Override
    public void onResume(){
        super.onResume();

        // Hack... Recreate view pager to reload map on MeetingFilterFragment backpress.
        try {
            if(MeetingFilterFragment.loaded) {
                binding.viewPager.setAdapter(new ViewPagerFragmentAdapter(getActivity()));
                MeetingFilterFragment.loaded = false;
            }
        }
        catch(Exception e) {}
    }

    /**
     * Called immediately after onCreateView() has returned, but before any saved state has been
     * restored in to the view.
     * @param view The View returned by onCreateView().
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous
     *                           saved state as given here. This value may be null.
     */
    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        // Find view components.
        ViewPager2 viewpager = view.findViewById(R.id.view_pager);

        viewpager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {

            @Override
            public void onPageSelected(int position) {

                if(countryCode.equals("GB")) {
                    if (position == 0) {

                        // Setup ActionBar
                        if (getActivity() != null) {
                            ActionBar mActionBar = ((MainActivity) getActivity()).getSupportActionBar();
                            if (mActionBar != null) {

                                // Hide default ActionBar.
                                mActionBar.setDisplayUseLogoEnabled(false);
                                mActionBar.setDisplayShowHomeEnabled(false);

                                // Initialize the custom action bar layout.
                                LayoutInflater mInflater = LayoutInflater.from(getActivity());
                                @SuppressLint("InflateParams") View mCustomView = mInflater.inflate(R.layout.fragment_meetings_action_bar, null);
                                mActionBar.setCustomView(mCustomView);
                                mActionBar.setDisplayShowCustomEnabled(true);

                                // Initialize action bar events.
                                if(meetingMapFragment != null)
                                    meetingMapFragment.initActionBar(mActionBar);
                            }
                        }
                    } else if (position == 1) {

                        // Setup ActionBar.
                        if (getActivity() != null) {
                            ActionBar mActionBar = ((MainActivity) getActivity()).getSupportActionBar();
                            if (mActionBar != null) {
                                mActionBar.setIcon(R.drawable.ic_meeting_room_24dp);
                                mActionBar.setDisplayUseLogoEnabled(true);
                                mActionBar.setDisplayShowHomeEnabled(true);
                                mActionBar.setDisplayShowCustomEnabled(false);
                                mActionBar.setTitle("  " + getResources().getString(R.string.meetings_london));
                            }
                        }
                    } else if (position == 2) {

                        // Setup ActionBar.
                        if (getActivity() != null) {
                            ActionBar mActionBar = ((MainActivity) getActivity()).getSupportActionBar();
                            if (mActionBar != null) {
                                mActionBar.setIcon(R.drawable.ic_meeting_room_24dp);
                                mActionBar.setDisplayUseLogoEnabled(true);
                                mActionBar.setDisplayShowHomeEnabled(true);
                                mActionBar.setDisplayShowCustomEnabled(false);
                                mActionBar.setTitle("  " + getResources().getString(R.string.meetings_worldwide));
                            }
                        }
                    }
                }
                else {

                    // Setup ActionBar.
                    if (getActivity() != null) {
                        ActionBar mActionBar = ((MainActivity) getActivity()).getSupportActionBar();
                        if (mActionBar != null) {
                            mActionBar.setIcon(R.drawable.ic_meeting_room_24dp);
                            mActionBar.setDisplayUseLogoEnabled(true);
                            mActionBar.setDisplayShowHomeEnabled(true);
                            mActionBar.setDisplayShowCustomEnabled(false);
                            mActionBar.setTitle("  " + getResources().getString(R.string.meetings_worldwide));
                        }
                    }
                }

                super.onPageSelected(position);
            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private class ViewPagerFragmentAdapter extends FragmentStateAdapter {

        public ViewPagerFragmentAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {

            if(countryCode.equals("GB")) {
                switch (position) {
                    case 0: {
                        meetingMapFragment = new MeetingMapFragment();
                        return meetingMapFragment;
                    }
                    case 1:
                        return new MeetingLondonFragment();
                    case 2:
                        return new MeetingWorldwideFragment();
                }
            }
            return new MeetingWorldwideFragment();
        }

        @Override
        public int getItemCount() {
            return tabs.length;
        }
    }

    /***
     * Gets the sobriety date from the database.
     * @return Date object containing sobriety date.
     */
    private String getCountryCodeDb() {

        // Get database.
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Set columns to retrieve.
        String[] projection = {
                HomeDbContract.HomeEntry._ID,
                HomeDbContract.HomeEntry.COLUMN_NAME_COUNTRY_CODE
        };

        // Filter results WHERE "title" = 'My Title'
        String selection = HomeDbContract.HomeEntry._ID + " = ?";
        String[] selectionArgs = {"1"};

        // How you want the results sorted in the resulting Cursor
        String sortOrder = HomeDbContract.HomeEntry._ID + " DESC";

        Cursor cursor = db.query(
                HomeDbContract.HomeEntry.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,           // don't group the rows
                null,            // don't filter by row groups
                sortOrder               // The sort order
        );

        List<String> itemIds = new ArrayList<>();
        while (cursor.moveToNext()) {
            String itemId = cursor.getString(
                    cursor.getColumnIndexOrThrow(HomeDbContract.HomeEntry.COLUMN_NAME_COUNTRY_CODE));
            itemIds.add(itemId);
        }
        cursor.close();

        return itemIds.get(0);
    }
}