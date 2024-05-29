package com.citex.twelve_step_recovery.ui.program;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.citex.twelve_step_recovery.MainActivity;
import com.citex.twelve_step_recovery.R;
import com.citex.twelve_step_recovery.databinding.FragmentProgramBinding;

public class ProgramFragment extends Fragment {

    private ProgramViewModel programViewModel;
    private FragmentProgramBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        // Setup ActionBar.
        if(getActivity() != null) {
            ActionBar mActionBar = ((MainActivity) getActivity()).getSupportActionBar();
            if(mActionBar != null) {
                mActionBar.setIcon(R.drawable.ic_checklist_24dp);
                mActionBar.setDisplayUseLogoEnabled(true);
                mActionBar.setDisplayShowHomeEnabled(true);
                mActionBar.setDisplayShowCustomEnabled(false);
                mActionBar.setTitle("  " + getResources().getString(R.string.program));
            }
        }

        // Setup View Binding.
        binding = FragmentProgramBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Setup ViewModel.
        programViewModel = new ViewModelProvider(this).get(ProgramViewModel.class);
        final TextView textView = binding.textProgram;
        programViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}