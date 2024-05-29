package com.citex.twelve_step_recovery.ui.meetings;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.citex.twelve_step_recovery.R;
import com.citex.twelve_step_recovery.network.NetworkConnection;

public class MeetingLondonFragment extends Fragment {

    private WebView webview;
    private ProgressBar progressBar;

    /**
     * Creates and returns the view hierarchy associated with the fragment.
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     * @return Return the View for the fragment's UI, or null.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the meeting webview layout.
        return inflater.inflate(R.layout.fragment_meetings_webview, container, false);
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
        webview = view.findViewById(R.id.webview_meeting_map);
        progressBar = view.findViewById(R.id.progress_loader);

        // Check internet connection.
        if(NetworkConnection.isNetworkAvailable(getActivity())) {

            // Override WebViewClient to catch progress events.
            webview.setWebViewClient(new MeetingLondonFragment.WebViewClient());

            // Enable JavaScript.
            WebSettings webSettings = webview.getSettings();
            webSettings.setJavaScriptEnabled(true);

            // Fit horizontal.
            webview.getSettings().setLoadWithOverviewMode(true);
            webview.getSettings().setUseWideViewPort(true);

            // Load webpage.
            webview.loadUrl("https://aa-london.com/onlinemeetingsearch");

        }
        else {
            // Show no network error.
            progressBar.setVisibility(View.GONE);
            TextView textError;
            textError = view.findViewById(R.id.text_error);
            textError.setText(R.string.no_network);
            textError.setVisibility(View.VISIBLE);
        }
    }


    public class WebViewClient extends android.webkit.WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // Open zoom / whatsapp links with app.
            if(url.contains("zoom.us") || url.contains("whatsapp:")) {
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(i);
            }
            else
                view.loadUrl(url);
            return true;
        }
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            progressBar.setVisibility(View.GONE);
            webview.setVisibility(View.VISIBLE);
        }
    }
}