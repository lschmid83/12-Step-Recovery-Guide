package com.citex.twelve_step_recovery.ui.home.daily_reflection;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import com.citex.twelve_step_recovery.MainActivity;
import com.citex.twelve_step_recovery.R;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.citex.twelve_step_recovery.databinding.FragmentDailyReflectionBinding;
import com.citex.twelve_step_recovery.exceptions.ResourceUnavailableException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DailyReflectionFragment extends Fragment {

    private DailyReflectionViewModel dailyReflectionViewModel;
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
                mActionBar.setIcon(R.drawable.ic_library_books_24dp);
                mActionBar.setDisplayUseLogoEnabled(true);
                mActionBar.setDisplayShowHomeEnabled(true);
                mActionBar.setDisplayShowCustomEnabled(false);
                mActionBar.setTitle("  " + getResources().getString(R.string.daily_reflection));
            }
        }

        // Setup View Binding.
        FragmentDailyReflectionBinding binding = FragmentDailyReflectionBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        container.removeAllViews();

        // Setup ViewModel.
        dailyReflectionViewModel = new ViewModelProvider(this).get(DailyReflectionViewModel.class);

        final TextView textDate = binding.textDailyReflectionDate;
        dailyReflectionViewModel.getDate().observe(getViewLifecycleOwner(), textDate::setText);

        final TextView textHeaderTitle = binding.textDailyReflectionHeaderTitle;
        dailyReflectionViewModel.getHeaderTitle().observe(getViewLifecycleOwner(), textHeaderTitle::setText);

        final TextView textHeaderContent = binding.textDailyReflectionHeaderContent;
        dailyReflectionViewModel.getHeaderContent().observe(getViewLifecycleOwner(), textHeaderContent::setText);

        final TextView textContentTitle = binding.textDailyReflectionContentTitle;
        dailyReflectionViewModel.getContentTitle().observe(getViewLifecycleOwner(), textContentTitle::setText);

        final TextView textContent = binding.textDailyReflectionContent;
        dailyReflectionViewModel.getContent().observe(getViewLifecycleOwner(), textContent::setText);

        final TextView textCopyright = binding.textDailyReflectionCopyright;
        dailyReflectionViewModel.getCopyright().observe(getViewLifecycleOwner(), textCopyright::setText);

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

        setDailyReflection(view);
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
     * Gets the daily reflection from the aa.org website and updates the ViewModel.
     * @param view The View returned by onCreateView().
     */
    private void setDailyReflection(View view)  {

        // Request daily reflection webpage.
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(() -> {

            // Get daily reflection
            DailyReflectionModel dailyReflectionModel = null;
            String errorMessage = null;
            try {
                // Connect to aa.org daily reflection page.
                Document doc = Jsoup.connect("https://www.aa.org/pages/en_US/daily-reflection").get();

                // Create model.
                dailyReflectionModel = new DailyReflectionModel();
                Calendar calendar = Calendar.getInstance();
                Date dateTime = calendar.getTime();
                dailyReflectionModel.Date = new SimpleDateFormat("EEEE, MMMM dd", Locale.ENGLISH).format(dateTime.getTime());
                dailyReflectionModel.HeaderTitle = doc.getElementsByClass("field--name-title").text().trim();

                // Select first "field--name-body" class element.
                Element element = doc.getElementsByClass("field--name-body").first();

                // Select <p> elements.
                Elements elements = element.select("p");

                dailyReflectionModel.HeaderContent = elements.get(0).text().trim();
                dailyReflectionModel.ContentTitle = elements.get(1).text().trim();

                // Create string array to number of <p> elements.
                String [] content = new String[elements.size() - 2];

                // Initialize content array.
                for(int i = 0; i < elements.size() - 2; i++) {
                    content[i] = elements.get(i + 2).text().trim();
                }

                // Add content to full string with line breaks.
                StringBuilder contentFull = new StringBuilder();
                for(int i = 0; i < content.length; i++)
                {
                    if(i > 0 && !content[i-1].equals(""))
                        contentFull.append("\r\n\r\n");

                    if(!content[i].equals("")) {
                        contentFull.append(content[i]);
                    }
                }

                dailyReflectionModel.Content = contentFull.toString().trim();
                dailyReflectionModel.Copyright = doc.getElementsByClass("copyright-block").text();

                // Through ResourceUnavailableException if data is missing.
                if(TextUtils.isEmpty(dailyReflectionModel.Date) || TextUtils.isEmpty(dailyReflectionModel.HeaderTitle) ||
                        TextUtils.isEmpty(dailyReflectionModel.HeaderContent) || TextUtils.isEmpty(dailyReflectionModel.ContentTitle) ||
                        TextUtils.isEmpty(dailyReflectionModel.Content) || TextUtils.isEmpty(dailyReflectionModel.Copyright)) {
                    throw new ResourceUnavailableException();
                }

            } catch(ResourceUnavailableException e) {
                Log.e(TAG, Log.getStackTraceString(e));
                errorMessage = getResources().getString(R.string.resource_unavailable);
            } catch (Exception e) {
                errorMessage = getResources().getString(R.string.no_network);
            }

            DailyReflectionModel finalDailyReflectionModel = dailyReflectionModel;
            String finalErrorMessage = errorMessage;
            handler.post(() -> {

                // Find progress layout.
                ConstraintLayout progressLayout;
                progressLayout = view.findViewById(R.id.progress_layout);

                // Find daily reflection layout.
                ConstraintLayout dailyReflectionLayout;
                dailyReflectionLayout = view.findViewById(R.id.daily_reflection_layout);

                // Hide progress layout.
                progressLayout.setVisibility(View.GONE);

                if(finalDailyReflectionModel != null) {

                    // Show daily reflection layout.
                    dailyReflectionLayout.setVisibility(View.VISIBLE);

                    // Initialize ViewModel.
                    dailyReflectionViewModel.setDate(finalDailyReflectionModel.Date);
                    dailyReflectionViewModel.setHeaderTitle(finalDailyReflectionModel.HeaderTitle);
                    dailyReflectionViewModel.setHeaderContent(finalDailyReflectionModel.HeaderContent);
                    dailyReflectionViewModel.setContentTitle(finalDailyReflectionModel.ContentTitle);
                    dailyReflectionViewModel.setContent(finalDailyReflectionModel.Content);
                    dailyReflectionViewModel.setCopyright(finalDailyReflectionModel.Copyright);
                }
                else {
                    // Display error message.
                    TextView textError;
                    textError = view.findViewById(R.id.text_error);
                    textError.setText(finalErrorMessage);
                    textError.setVisibility(View.VISIBLE);
                }
            });
        });
    }

    /**
     * Adds a OnClickListener to the share button which opens the share daily reflection intent.
     * @param view The View for the fragment's UI.
     */
    private void setShareButtonOnClickListener(View view) {

        // Share daily reflection.
        TextView textShare;
        textShare = view.findViewById(R.id.text_daily_reflection_share);
        textShare.setOnClickListener(v -> {

            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            String shareBody = dailyReflectionViewModel.getDate().getValue() + "\r\n\r\n" +
                    dailyReflectionViewModel.getHeaderTitle().getValue()+ "\r\n\r\n" +
                    dailyReflectionViewModel.getHeaderContent().getValue() +  "\r\n\r\n" +
                    dailyReflectionViewModel.getContentTitle().getValue() + "\r\n\r\n" +
                    dailyReflectionViewModel.getContent().getValue() +  "\r\n\r\n" +
                    dailyReflectionViewModel.getCopyright().getValue() + "\r\n\r\n" +
                    getResources().getString(R.string.share_link);
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
            startActivity(Intent.createChooser(sharingIntent, "Share via"));
        });
    }
}

