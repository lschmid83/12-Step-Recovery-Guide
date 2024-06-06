package com.citex.twelve_step_recovery.ui.home.thought_for_the_day;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;

import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.citex.twelve_step_recovery.MainActivity;
import com.citex.twelve_step_recovery.R;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.citex.twelve_step_recovery.databinding.FragmentThoughtForTheDayBinding;
import com.citex.twelve_step_recovery.ui.home.daily_reflection.DailyReflectionFragment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ThoughtForTheDayFragment extends Fragment {

    private ThoughtForTheDayViewModel thoughtForTheDayViewModel;
    private static final String TAG = DailyReflectionFragment.class.getName();

    /**
     * Creates and returns the view hierarchy associated with the fragment.
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     * @return Return the View for the fragment's UI, or null.
     */
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        // Setup ActionBar.
        if(getActivity() != null) {
            ActionBar mActionBar = ((MainActivity) getActivity()).getSupportActionBar();
            if(mActionBar != null) {
                mActionBar.setIcon(R.drawable.ic_bubble_24dp);
                mActionBar.setDisplayUseLogoEnabled(true);
                mActionBar.setDisplayShowHomeEnabled(true);
                mActionBar.setDisplayShowCustomEnabled(false);
                mActionBar.setTitle("  " + getResources().getString(R.string.thought_for_the_day));
            }
        }

        // Setup View Binding.
        FragmentThoughtForTheDayBinding binding = FragmentThoughtForTheDayBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        container.removeAllViews();

        // Setup ViewModel.
        thoughtForTheDayViewModel = new ViewModelProvider(this).get(ThoughtForTheDayViewModel.class);

        final TextView textDate = binding.textThoughtForTheDayDate;
        thoughtForTheDayViewModel.getDate().observe(getViewLifecycleOwner(), textDate::setText);

        final TextView textThought = binding.textThoughtForTheDayContent;
        thoughtForTheDayViewModel.getThought().observe(getViewLifecycleOwner(), textThought::setText);

        final TextView textMeditation = binding.textMeditationForTheDayContent;
        thoughtForTheDayViewModel.getMeditation().observe(getViewLifecycleOwner(), textMeditation::setText);

        final TextView textPrayer = binding.textPrayerForTheDayContent;
        thoughtForTheDayViewModel.getPrayer().observe(getViewLifecycleOwner(), textPrayer::setText);

        final TextView textCopyright = binding.textThoughtForTheDayCopyright;
        thoughtForTheDayViewModel.getCopyright().observe(getViewLifecycleOwner(), textCopyright::setText);

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
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
        setThoughtForTheDay(view);
        setShareButtonOnClickListener(view);
    }

    /**
     * Called when the view previously created by onCreateView() has been detached from the
     * fragment. The next time the fragment needs to be displayed, a new view will be created.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    /**
     * Gets the thought of the day from hazeldenbettyford.org web service and updates the ViewModel.
     * @param view The View returned by onCreateView().
     */
    private void setThoughtForTheDay(View view) {

        // Request thought for the day API method using Volley.
        Calendar calendarToday = Calendar.getInstance();
        int year = calendarToday.get(Calendar.YEAR);
        int month = calendarToday.get(Calendar.MONTH) + 1;
        int day = calendarToday.get(Calendar.DAY_OF_MONTH);

        String url = "https://www.hazeldenbettyford.org/content/hbff/us/en/thought-for-the-day/jcr:content/root/container/container/thoughtdaysection." + year + "-" + month + "-" + day + ".json";
        if(getActivity()!= null) {

            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.GET, url, null, response -> {

                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        // Find progress layout.
                        ConstraintLayout progressLayout;
                        progressLayout = view.findViewById(R.id.progress_layout);

                        // Hide progress layout.
                        progressLayout.setVisibility(View.GONE);

                        try {

                            ThoughtForTheDayModel thoughtForTheDayModel = ParseThoughtForTheDay(response);
                            thoughtForTheDayViewModel.setDate(thoughtForTheDayModel.Date);
                            thoughtForTheDayViewModel.setThought(thoughtForTheDayModel.Thought);
                            thoughtForTheDayViewModel.setMeditation(thoughtForTheDayModel.Meditation);
                            thoughtForTheDayViewModel.setPrayer(thoughtForTheDayModel.Prayer);
                            thoughtForTheDayViewModel.setCopyright(thoughtForTheDayModel.Copyright);

                            // Find daily reflection layout.
                            ConstraintLayout thoughtForTheDayLayout;
                            thoughtForTheDayLayout = view.findViewById(R.id.thought_for_the_day_layout);

                            // Show daily reflection layout.
                            thoughtForTheDayLayout.setVisibility(View.VISIBLE);

                        } catch (Exception e) {

                            Log.e(TAG, Log.getStackTraceString(e));

                            // Display error message.
                            TextView textError;
                            textError = view.findViewById(R.id.text_error);
                            textError.setText(getResources().getString(R.string.resource_unavailable));
                            textError.setVisibility(View.VISIBLE);
                        }

                    }, error -> {

                        // Find progress layout.
                        ConstraintLayout progressLayout;
                        progressLayout = view.findViewById(R.id.progress_layout);

                        // Hide progress layout.
                        progressLayout.setVisibility(View.GONE);

                        // Display error message.
                        TextView textError;
                        textError = view.findViewById(R.id.text_error);
                        textError.setText(getResources().getString(R.string.no_network));
                        textError.setVisibility(View.VISIBLE);
                    });

            requestQueue.add(jsonObjectRequest);
        }
    }

    /**
     * Parses the JSON array containing the thought for the day into the ThoughtForTheDayModel.
     * @param thoughtForTheDayJsonResponse JSON response.
     * @return ThoughtForTheDayModel.
     * @throws Exception Parsing exception.
     */
    private ThoughtForTheDayModel ParseThoughtForTheDay(JSONObject thoughtForTheDayJsonResponse) throws Exception {

        // Parse JSON into object.
        JSONArray jsonArray = thoughtForTheDayJsonResponse.getJSONArray("data");
        JSONObject jsonObject = jsonArray.getJSONObject(0);

        String fullThought = jsonObject.getString("reading");

        // Create model.
        ThoughtForTheDayModel thoughtForTheDayModel = new ThoughtForTheDayModel();

        // Set date.
        Calendar calendar = Calendar.getInstance();
        Date dateTime = calendar.getTime();
        thoughtForTheDayModel.Date = new SimpleDateFormat("EEEE, MMMM dd", Locale.ENGLISH).format(dateTime.getTime());

        // Parse thought for the day.
        String[] fullThoughtSplit = fullThought.split("Thought for the Day");
        thoughtForTheDayModel.Thought = fullThoughtSplit[1].split("Meditation for the Day")[0].replaceAll("<.*?>", "");
        thoughtForTheDayModel.Thought = thoughtForTheDayModel.Thought.replaceAll("&nbsp;", "").trim();

        // Parse meditation of the day.
        fullThoughtSplit = fullThought.split("Meditation for the Day");
        thoughtForTheDayModel.Meditation = fullThoughtSplit[1].split("Prayer for the Day")[0].replaceAll("<.*?>", "");
        thoughtForTheDayModel.Meditation = thoughtForTheDayModel.Meditation.replaceAll("&nbsp;", "").trim();

        // Parse prayer of the day.
        fullThoughtSplit = fullThought.split("Prayer for the Day");
        thoughtForTheDayModel.Prayer = fullThoughtSplit[1].replaceAll("<.*?>", "");
        thoughtForTheDayModel.Prayer = thoughtForTheDayModel.Prayer.replaceAll("&nbsp;", "").trim();

        // Parse copyright.
        thoughtForTheDayModel.Copyright = jsonObject.getString("copyrightText").replaceAll("<.*?>", "");;

        return thoughtForTheDayModel;
    }

    /**
     * Adds a OnClickListener to the share button which opens the share thought for the day intent.
     * @param view The View for the fragment's UI.
     */
    private void setShareButtonOnClickListener(View view) {

        // Share Thought for the Day.
        TextView textShare;
        textShare = view.findViewById(R.id.text_thought_for_the_day_share);
        textShare.setOnClickListener(v -> {

            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            String shareBody = thoughtForTheDayViewModel.getDate().getValue() + "\r\n\r\n" +
                    "Thought for the Day" + "\r\n\r\n" +
                    thoughtForTheDayViewModel.getThought().getValue()+ "\r\n\r\n" +
                    "Meditation for the Day" + "\r\n\r\n" +
                    thoughtForTheDayViewModel.getMeditation().getValue() + "\r\n\r\n" +
                    "Prayer for the Day" + "\r\n\r\n" +
                    thoughtForTheDayViewModel.getPrayer().getValue() + "\r\n\r\n" +
                    thoughtForTheDayViewModel.getCopyright().getValue() + "\r\n\r\n" +
                    getResources().getString(R.string.share_link);
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
            startActivity(Intent.createChooser(sharingIntent, "Share via"));
        });
    }
}
