package com.citex.twelve_step_recovery.ui.home.just_for_today;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.citex.twelve_step_recovery.MainActivity;
import com.citex.twelve_step_recovery.R;
import com.citex.twelve_step_recovery.databinding.FragmentJustForTodayBinding;
import com.citex.twelve_step_recovery.exceptions.ResourceUnavailableException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class JustForTodayFragment extends Fragment {

    private JustForTodayViewModel justForTodayViewModel;
    private static final String TAG = JustForTodayFragment.class.getName();

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
                mActionBar.setTitle("  " + getResources().getString(R.string.just_for_today));
            }
        }

        // Setup View Binding.
        FragmentJustForTodayBinding binding = FragmentJustForTodayBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        container.removeAllViews();

        // Setup ViewModel.
        justForTodayViewModel = new ViewModelProvider(this).get(JustForTodayViewModel.class);

        final TextView textDate = binding.textJustForTodayDate;
        justForTodayViewModel.getDate().observe(getViewLifecycleOwner(), textDate::setText);

        final TextView textHeaderTitle = binding.textJustForTodayHeaderTitle;
        justForTodayViewModel.getHeaderTitle().observe(getViewLifecycleOwner(), textHeaderTitle::setText);

        final TextView textHeaderPage = binding.textJustForTodayHeaderPage;
        justForTodayViewModel.getHeaderPage().observe(getViewLifecycleOwner(), textHeaderPage::setText);

        final TextView textHeaderContent = binding.textJustForTodayHeaderContent;
        justForTodayViewModel.getHeaderContent().observe(getViewLifecycleOwner(), textHeaderContent::setText);

        final TextView textContentTitle = binding.textJustForTodayContentTitle;
        justForTodayViewModel.getContentTitle().observe(getViewLifecycleOwner(), textContentTitle::setText);

        final TextView textContent = binding.textJustForTodayContent;
        justForTodayViewModel.getContent().observe(getViewLifecycleOwner(), textContent::setText);

        final TextView textQuote = binding.textJustForTodayQuote;
        justForTodayViewModel.getQuote().observe(getViewLifecycleOwner(), textQuote::setText);

        final TextView textCopyright = binding.textJustForTodayCopyright;
        justForTodayViewModel.getCopyright().observe(getViewLifecycleOwner(), textCopyright::setText);

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

        // Request Just for Today webpage.
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(() -> {

            // Get Just for Today reading
            JustForTodayModel justForTodayModel = null;
            String errorMessage = null;
            try {
                // Connect to jftna.org/jft/ just for today page.
                Document doc = Jsoup.connect("https://www.jftna.org/jft/").get();

                // Create model.
                justForTodayModel = new JustForTodayModel();
                Calendar calendar = Calendar.getInstance();
                Date dateTime = calendar.getTime();
                justForTodayModel.Date = new SimpleDateFormat("EEEE, MMMM dd", Locale.ENGLISH).format(dateTime.getTime());

                justForTodayModel.HeaderTitle = doc.getElementsByTag("td").get(1).text().trim();
                justForTodayModel.HeaderPage = doc.getElementsByTag("td").get(2).text().trim();
                justForTodayModel.HeaderContent = doc.getElementsByTag("td").get(3).text().trim();
                justForTodayModel.ContentTitle = doc.getElementsByTag("td").get(4).text().trim();
                justForTodayModel.Content = doc.getElementsByTag("td").get(5).text().trim();
                justForTodayModel.Quote = doc.getElementsByTag("td").get(6).text().trim();
                justForTodayModel.Copyright = doc.getElementsByTag("td").get(7).text().trim();

                // Through ResourceUnavailableException if data is missing.
                if(TextUtils.isEmpty(justForTodayModel.Date) || TextUtils.isEmpty(justForTodayModel.HeaderTitle) ||  TextUtils.isEmpty(justForTodayModel.HeaderPage) ||
                TextUtils.isEmpty(justForTodayModel.HeaderContent) || TextUtils.isEmpty(justForTodayModel.ContentTitle) ||
                        TextUtils.isEmpty(justForTodayModel.Content) || TextUtils.isEmpty(justForTodayModel.Quote) || TextUtils.isEmpty(justForTodayModel.Copyright)) {
                    throw new ResourceUnavailableException();
                }

            } catch(ResourceUnavailableException e) {
                Log.e(TAG, Log.getStackTraceString(e));
                errorMessage = getResources().getString(R.string.resource_unavailable);
            } catch (Exception e) {
                errorMessage = getResources().getString(R.string.no_network);
            }

            JustForTodayModel finalJustForTodayModel = justForTodayModel;
            String finalErrorMessage = errorMessage;
            handler.post(() -> {

                // Find progress layout.
                ConstraintLayout progressLayout;
                progressLayout = view.findViewById(R.id.progress_layout);

                // Find daily reflection layout.
                ConstraintLayout justForTodayLayout;
                justForTodayLayout = view.findViewById(R.id.just_for_today_layout);

                // Hide progress layout.
                progressLayout.setVisibility(View.GONE);

                if(finalJustForTodayModel != null) {

                    // Show daily reflection layout.
                    justForTodayLayout.setVisibility(View.VISIBLE);

                    // Initialize ViewModel.
                    justForTodayViewModel.setDate(Html.fromHtml(finalJustForTodayModel.Date).toString());
                    justForTodayViewModel.setHeaderTitle(Html.fromHtml(finalJustForTodayModel.HeaderTitle).toString());
                    justForTodayViewModel.setHeaderPage(Html.fromHtml(finalJustForTodayModel.HeaderPage).toString());
                    justForTodayViewModel.setHeaderContent(Html.fromHtml(finalJustForTodayModel.HeaderContent).toString());
                    justForTodayViewModel.setContentTitle(Html.fromHtml(finalJustForTodayModel.ContentTitle).toString());
                    justForTodayViewModel.setQuote(Html.fromHtml(finalJustForTodayModel.Quote).toString());
                    justForTodayViewModel.setContent(Html.fromHtml(finalJustForTodayModel.Content).toString());
                    justForTodayViewModel.setCopyright(Html.fromHtml(finalJustForTodayModel.Copyright).toString());
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
        textShare = view.findViewById(R.id.text_just_for_today_share);
        textShare.setOnClickListener(v -> {

            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            String shareBody = justForTodayViewModel.getDate().getValue() + "\r\n\r\n" +
                    justForTodayViewModel.getHeaderTitle().getValue()+ "\r\n\r\n" +
                    justForTodayViewModel.getHeaderPage().getValue() +  "\r\n\r\n" +
                    justForTodayViewModel.getHeaderContent().getValue() +  "\r\n\r\n" +
                    justForTodayViewModel.getContentTitle().getValue() + "\r\n\r\n" +
                    justForTodayViewModel.getContent().getValue() +  "\r\n\r\n" +
                    justForTodayViewModel.getQuote().getValue() +  "\r\n\r\n" +
                    justForTodayViewModel.getCopyright().getValue() + "\r\n\r\n" +
                    getResources().getString(R.string.share_link);
            sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
            startActivity(Intent.createChooser(sharingIntent, "Share via"));
        });
    }
}

