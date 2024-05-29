package com.citex.twelve_step_recovery.ui.home.readings;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import com.citex.twelve_step_recovery.MainActivity;
import com.citex.twelve_step_recovery.R;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.citex.twelve_step_recovery.databinding.FragmentReadingsIndexBinding;
import com.citex.twelve_step_recovery.recyclerview.RecyclerViewAdapter;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.enums.CSVReaderNullFieldIndicator;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class ReadingsIndexFragment extends Fragment implements RecyclerViewAdapter.ItemClickListener {

    private ArrayList<ReadingsIndexModel> readingsIndexCsv;
    private static final String TAG = ReadingsIndexFragment.class.getName();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        // Setup ActionBar.
        if(getActivity() != null) {
            ActionBar mActionBar = ((MainActivity) getActivity()).getSupportActionBar();
            if(mActionBar != null) {
                mActionBar.setIcon(R.drawable.ic_menu_book_24dp);
                mActionBar.setDisplayUseLogoEnabled(true);
                mActionBar.setDisplayShowHomeEnabled(true);
                mActionBar.setDisplayShowCustomEnabled(false);
                mActionBar.setTitle("  " + getArguments().getString("title"));
            }
        }

        // Setup View Binding.
        FragmentReadingsIndexBinding binding = FragmentReadingsIndexBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        container.removeAllViews();

        // Create the RecyclerView.
        RecyclerView recyclerView = root.findViewById(R.id.recyclerview_readings);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(root.getContext()));

        ArrayList<String> readings = new ArrayList<>();
        readingsIndexCsv = new ArrayList<>();

        // Read index.
        try {
            AssetManager assetManager = getActivity().getAssets();
            InputStream csvInputStream = assetManager.open(getArguments().getString("indexFilename"));
            CSVReader reader = new CSVReaderBuilder(new InputStreamReader(csvInputStream))
                    .withFieldAsNull(CSVReaderNullFieldIndicator.EMPTY_SEPARATORS)
                    .withSkipLines(1)
                    .build();
            String[] nextLine;

            while ((nextLine = reader.readNext()) != null) {
                readings.add(nextLine[1]);
                ReadingsIndexModel readingsIndexModel = new ReadingsIndexModel();
                readingsIndexModel.Id = Integer.parseInt(nextLine[0]);
                readingsIndexModel.Title = nextLine[1];
                readingsIndexModel.Filename = nextLine[2];
                readingsIndexModel.Book = Boolean.parseBoolean(nextLine[3]);
                readingsIndexCsv.add(readingsIndexModel);
            }
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }

        // Setup the RecyclerViewAdapter.
        RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(readings);
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

    @Override
    public void onItemClick(View view, int position) {

        // Find prayer in array list.
        ReadingsIndexModel readingIndex = null;
        for (int i = 0; i < readingsIndexCsv.size(); i++) {
            if (i == position)
                readingIndex = readingsIndexCsv.get(i);
        }

        if (getActivity() != null) {

            if (readingIndex != null) {

                Bundle bundle = new Bundle();
                bundle.putString("title", getArguments().getString("title"));
                bundle.putString("indexFilename", readingIndex.Filename);

                String indexExtension = getFileExtension(readingIndex.Filename);

                if(indexExtension.equals("csv")) {

                    if(readingIndex.Book == true) // Book index
                    {
                        NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_activity_main);
                        navController.navigate(R.id.action_navigation_readings_index_to_navigation_readings_index_book, bundle);
                    }
                    else {
                        NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_activity_main);
                        navController.navigate(R.id.action_navigation_readings_index_self, bundle);
                    }
                }
                else if(indexExtension.equals("html")) {
                    NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_activity_main);
                    navController.navigate(R.id.action_navigation_readings_index_to_navigation_readings_content_html, bundle);
                }
                else if(indexExtension.equals("pdf")) {
                    NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_activity_main);
                    navController.navigate(R.id.action_navigation_readings_index_to_navigation_readings_content_pdf, bundle);
                }
            }
        }
    }

    private String getFileExtension(String filename) {
        String extension = "";

        int i = filename.lastIndexOf('.');
        if (i > 0) {
            extension = filename.substring(i+1);
        }

        return extension;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
