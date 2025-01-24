package com.citex.twelve_step_recovery.ui.meetings;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.citex.twelve_step_recovery.R;
import com.citex.twelve_step_recovery.network.NetworkConnection;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.android.gms.tasks.OnTokenCanceledListener;

public class MeetingMapFragment extends Fragment {

    private WebView webview;
    private ProgressBar progressBar;
    private FusedLocationProviderClient fusedLocationClient;
    private MeetingFilter meetingFilter;
    private Boolean locationPermission = false;
    private String searchBarText;

    // Register the permissions callback, which handles the user's response to the
    // system permissions dialog. Save the return value, an instance of
    // ActivityResultLauncher, as an instance variable.
    @SuppressLint("MissingPermission")
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    loadSearchResultsWithGpsLocation();
                }
            });

    /**
     * Creates and returns the view hierarchy associated with the fragment.
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     * @return Return the View for the fragment's UI, or null.
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Initialize database.
        meetingFilter = new MeetingFilter(getContext());

        searchBarText = "";

        // Initialize fused location client.
        if(getActivity() != null)
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

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
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        // Find view components.
        webview = view.findViewById(R.id.webview_meeting_map);
        progressBar = view.findViewById(R.id.progress_loader);

        initializeWebViewClient();

        // Check internet connection.
        if(NetworkConnection.isNetworkAvailable(getActivity())) {

            // Check GPS location permission.
            if(getActivity() != null) {
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
                    requestPermissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION);
                } else {
                    loadSearchResultsWithGpsLocation();
                }
            }
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

    /**
     * Refreshes the map view button.
     * @param actionBar The ActionBar.
     */
    public void refreshMapViewButton(ActionBar actionBar) {

        // Set map / list view icon.
        int mapChecked = meetingFilter.getMapDb();
        ImageButton mapButton = actionBar.getCustomView().findViewById(R.id.image_map_view);
        mapButton.setBackgroundResource(0);
        if(mapChecked == 0)
            mapButton.setImageResource(R.drawable.ic_location_24dp);
        else
            mapButton.setImageResource(R.drawable.ic_list_24dp);
    }

    /**
     * Initialize action bar events. Called from MeetingsFragment where ActionBar is initialized.
     * @param actionBar ActionBar
     */
    public void initActionBar(ActionBar actionBar) {

        refreshMapViewButton(actionBar);

        // Switch between map / list view.
        ImageButton mapButton = actionBar.getCustomView().findViewById(R.id.image_map_view);
        mapButton.setOnClickListener(v -> {

            progressBar.setVisibility(View.VISIBLE);
            webview.setVisibility(View.GONE);

            if(meetingFilter.getMapDb() == 0)
                meetingFilter.setMapDb(1);
            else
                meetingFilter.setMapDb(0);

            refreshMapViewButton(actionBar);

            if(searchBarText.length() > 0) {
                loadSearchResultsWithSearchLocation(searchBarText);
            }
            else {
                if (locationPermission) {
                    loadSearchResultsWithGpsLocation();
                }
            }
        });

        // Search bar enter event.
        EditText searchBar = actionBar.getCustomView().findViewById(R.id.edittext_search_bar);
        searchBar.setOnKeyListener((v, keyCode, event) -> {
            // If the event is a key-down event on the "enter" button
            if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                    (keyCode == KeyEvent.KEYCODE_ENTER)) {

                searchBarText = searchBar.getText().toString();

                if(searchBarText.length() > 0) {
                    loadSearchResultsWithSearchLocation(searchBar.getText().toString());
                }
                else {
                    if (locationPermission) {
                        loadSearchResultsWithGpsLocation();
                    }
                }
                return true;
            }
            return false;
        });

        // Show map filter fragment.
        ImageView imageSearchFilter = actionBar.getCustomView().findViewById(R.id.image_search_filter);
        imageSearchFilter.setOnClickListener(v -> {
            if (getActivity() != null) {
                NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_activity_main);
                navController.navigate(R.id.action_navigation_meetings_to_navigation_meetings_map_filter);
            }
        });
    }

    /**
     * Loads map into webview without GPS location.
     */
    @SuppressLint("SetJavaScriptEnabled")
    private void loadMapWithoutGpsLocation() {

        // Load webpage.
        webview.clearView();
        webview.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webview.loadUrl("https://www.recoverymeetingfinder.com/mobile-map");
    }

    /**
     * Loads map into webview with current GPS location.
     */
    @SuppressLint({"MissingPermission", "SetJavaScriptEnabled"})
    public void loadSearchResultsWithGpsLocation() {

        locationPermission = true;
        webview.clearView();

        if(getActivity() != null) {

            fusedLocationClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, new CancellationToken() {
                @Override
                public boolean isCancellationRequested() {
                    return false;
                }
                @NonNull
                @Override
                public CancellationToken onCanceledRequested(@NonNull OnTokenCanceledListener onTokenCanceledListener) {
                    return new CancellationTokenSource().getToken();
                }
            }).addOnSuccessListener(location -> {

                if (location != null) {
                    MeetingFilter.MeetingFilterDetails filterDetails = meetingFilter.getMeetingFilterDetails();

                    String component = "mobile-search-results";
                    if (meetingFilter.getMapDb() == 1)
                        component = "mobile-map";

                    // Load webpage.
                    webview.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
                    webview.loadUrl("https://www.recoverymeetingfinder.com/" + component + "?" + "" +
                            "type=" + filterDetails.Program +
                            "&region=all" +
                            "&day=" + filterDetails.Weekday +
                            "&distance=" + filterDetails.SearchRange +
                            "&units=" + filterDetails.SearchUnits +
                            "&accessibility=" + filterDetails.Accessibility +
                            "&lat=" + location.getLatitude() +
                            "&lng=" + location.getLongitude());

                } else {
                    loadMapWithoutGpsLocation();
                }
            });
        }
    }

    /**
     * Load map into webview with location string.
     */
    private void loadSearchResultsWithSearchLocation(String location) {

        webview.clearView();
        MeetingFilter.MeetingFilterDetails filterDetails = meetingFilter.getMeetingFilterDetails();

        String component = "mobile-search-results";
        if(meetingFilter.getMapDb() == 1)
            component = "mobile-map";

        // Load webpage.
        webview.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webview.loadUrl("https://www.recoverymeetingfinder.com/" + component + "?" + "" +
                "type=" + filterDetails.Program +
                "&region=all" +
                "&location=" + location +
                "&day=" + filterDetails.Weekday +
                "&distance=" + filterDetails.SearchRange +
                "&units=" + filterDetails.SearchUnits +
                "&accessibility=" + filterDetails.Accessibility +
                "&lat=0" +
                "&lng=0");
    }

    /**
     * Initializes webview with client handler and JS enabled.
     */
    @SuppressLint("SetJavaScriptEnabled")
    private void initializeWebViewClient() {

        // Enable JavaScript.
        WebSettings webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(true);

        // Fit horizontal.
        webview.getSettings().setLoadWithOverviewMode(true);
        webview.getSettings().setUseWideViewPort(true);

        // Override WebViewClient to catch progress event.s
        webview.setWebViewClient(new WebViewClient());

    }

    /*
    private void addBookmarkDb(Integer bookmarkId) {

        // Get database.
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(BookmarksDbContract.BookmarksEntry.COLUMN_NAME_BOOKMARK_ID, bookmarkId);

        // Insert the new row, returning the primary key value of the new row
        db.insert(BookmarksDbContract.BookmarksEntry.TABLE_NAME, null, values);
    }

    private int getBookmarkDb(int bookmarkId) {

        // Get database.
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Set columns to retrieve.
        String[] projection = {
                BookmarksDbContract.BookmarksEntry._ID,
                BookmarksDbContract.BookmarksEntry.COLUMN_NAME_BOOKMARK_ID
        };

        // Filter results WHERE "title" = 'My Title'
        String selection = BookmarksDbContract.BookmarksEntry.COLUMN_NAME_BOOKMARK_ID + " = ?";
        String[] selectionArgs = {String.valueOf(bookmarkId)};

        // How you want the results sorted in the resulting Cursor
        String sortOrder = BookmarksDbContract.BookmarksEntry._ID + " DESC";

        Cursor cursor = db.query(
                BookmarksDbContract.BookmarksEntry.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,           // don't group the rows
                null,            // don't filter by row groups
                sortOrder               // The sort order
        );

        List<Integer> itemIds = new ArrayList<>();
        while (cursor.moveToNext()) {
            int itemId = cursor.getInt(
                    cursor.getColumnIndexOrThrow(BookmarksDbContract.BookmarksEntry.COLUMN_NAME_BOOKMARK_ID));
            itemIds.add(itemId);
        }
        cursor.close();

        if(itemIds.size() > 0)
            return itemIds.get(0);
        else
            return 0;
    }*/

    /**
     * Override of default WebViewClient.
     */
    private class WebViewClient extends android.webkit.WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // Open Google map links with app.
            if(url.contains("https://www.google.com/maps") || url.contains("sms:") ||
                    url.contains("mailto:") || url.contains("whatsapp:") || url.contains("https://what3words.com")) {
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(i);
            }
            /*
            else if(url.contains("bookmark:")) {

                // Get bookmark meeting ID.
                String meetingId = url.split(":")[1];

                // Display message.
                Toast.makeText(getContext(), "Bookmarked", Toast.LENGTH_LONG).show();

                // Check if ID already exists.
                int bookmarkId = getBookmarkDb(Integer.valueOf(meetingId));

                // Add ID to database.
                if(bookmarkId == 0)
                    addBookmarkDb(Integer.valueOf(meetingId));
            }
            else if(url.contains("calendar:")) {
                Toast.makeText(getContext(), "Added to calendar", Toast.LENGTH_LONG).show();
            }
            else {
                view.loadUrl(url);
            }*/
            return true;
        }
        @Override
        public void onPageFinished(WebView view, String url) {
            progressBar.setVisibility(View.GONE);
            webview.setVisibility(View.VISIBLE);
            super.onPageFinished(view, url);
        }
    }
}