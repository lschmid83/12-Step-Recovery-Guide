package com.citex.twelve_step_recovery.ui.home;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.citex.twelve_step_recovery.MainActivity;
import com.citex.twelve_step_recovery.R;
import com.citex.twelve_step_recovery.databinding.FragmentDeclarationsContentHtmlBinding;
import com.citex.twelve_step_recovery.databinding.FragmentReadingsContentHtmlBinding;

public class DeclarationsContentHtmlFragment extends Fragment {

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
        FragmentDeclarationsContentHtmlBinding binding = FragmentDeclarationsContentHtmlBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        container.removeAllViews();

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        // Open file in WebView.
        WebView webview = view.findViewById(R.id.webView_reading_content);
        WebSettings webSettings = webview.getSettings();
        webSettings.setDefaultFontSize(18);
        webview.loadUrl("file:///android_asset/" + getArguments().getString("indexFilename"));
        webview.setWebViewClient(new WebViewClient()
        {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url)
            {
                // Open PDF link in Fragment.
                String urlFileExtension = getFileExtension(url);
                if(urlFileExtension.equals("pdf")) {

                    Bundle bundle = new Bundle();
                    bundle.putString("indexFilename", "https://www.recoverymeetingfinder.com" + url.replace("file://", ""));

                    NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_activity_main);
                    navController.navigate(R.id.action_navigation_readings_content_html_to_navigation_readings_content_pdf, bundle);
                    return true;
                }

                // Open rest of URLS in default browser.
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
                return true;
            }
        });

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
