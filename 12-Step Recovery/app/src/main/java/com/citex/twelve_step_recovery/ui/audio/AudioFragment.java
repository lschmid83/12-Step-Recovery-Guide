package com.citex.twelve_step_recovery.ui.audio;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.citex.twelve_step_recovery.MainActivity;
import com.citex.twelve_step_recovery.R;
import com.citex.twelve_step_recovery.databinding.FragmentAudioBinding;
import com.citex.twelve_step_recovery.recyclerview.RecyclerViewAdapter;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.enums.CSVReaderNullFieldIndicator;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class AudioFragment extends Fragment implements RecyclerViewAdapter.ItemClickListener {

    private ArrayList<String[]> audioCsv;

    private static final String TAG = AudioFragment.class.getName();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        // Setup ActionBar.
        if(getActivity() != null) {
            ActionBar mActionBar = ((MainActivity) getActivity()).getSupportActionBar();
            if(mActionBar != null) {
                mActionBar.setIcon(R.drawable.ic_headphones_24dp);
                mActionBar.setDisplayUseLogoEnabled(true);
                mActionBar.setDisplayShowHomeEnabled(true);
                mActionBar.setDisplayShowCustomEnabled(false);
                mActionBar.setTitle("  " + getResources().getString(R.string.audio));
            }
        }

        // Setup View Binding.
        FragmentAudioBinding binding = FragmentAudioBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        container.removeAllViews();

        // Create the RecyclerView.
        RecyclerView recyclerView = root.findViewById(R.id.recyclerview_audio);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(root.getContext()));

        ArrayList<String> audio = new ArrayList<>();
        audioCsv = new ArrayList<>();

        // Read audio.csv.
        try {
            AssetManager assetManager = getActivity().getAssets();
            InputStream csvInputStream = assetManager.open("audio.csv");
            CSVReader reader = new CSVReaderBuilder(new InputStreamReader(csvInputStream))
                    .withFieldAsNull(CSVReaderNullFieldIndicator.EMPTY_SEPARATORS)
                    .withSkipLines(1)
                    .build();
            String[] nextLine;

            while ((nextLine = reader.readNext()) != null) {
                audio.add(nextLine[2]);
                audioCsv.add(nextLine);
            }
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }

        // Setup the RecyclerViewAdapter.
        RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(audio);
        recyclerView.setAdapter(recyclerViewAdapter);

        // Setup the onClickListener.
        recyclerViewAdapter.setClickListener(this);
        recyclerView.setAdapter(recyclerViewAdapter);

        // Add dividers between list items.
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        return root;
    }

    /**
     * Callback method to be invoked when an item in this AdapterView has been clicked.
     * Implementers can call getItemAtPosition(position) if they need to access the data associated
     * with the selected item.
     * @param view The View for the fragment's UI.
     * @param position The index of the item clicked.
     */
    @Override
    public void onItemClick(View view, int position) {

        // Find audio book in array list.
        String[] audio = null;
        for(int i = 0; i < audioCsv.size(); i++) {
            if(i == position)
                audio = audioCsv.get(i);
        }

        if(getActivity() != null) {

            // Add prayer reading to fragment arguments.
            if(audio != null) {

                Bundle bundle = new Bundle();
                bundle.putString("audioTitle", audio[2]);
                bundle.putString("audioContentsFilename", audio[3]);
                bundle.putString("audioImage", audio[4]);

                // Display PrayerReadingFragment.
                NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_activity_main);
                navController.navigate(R.id.action_navigation_audio_to_navigation_audio_contents, bundle);
            }
        }
    }

    /**
     * Called when the view previously created by onCreateView() has been detached from the
     * fragment. The next time the fragment needs to be displayed, a new view will be created.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}