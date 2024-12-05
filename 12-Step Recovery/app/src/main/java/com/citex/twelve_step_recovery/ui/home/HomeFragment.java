package com.citex.twelve_step_recovery.ui.home;

import android.app.DatePickerDialog;
import android.content.ContentValues;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import android.telephony.TelephonyManager;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.afollestad.materialdialogs.BuildConfig;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;

import com.citex.twelve_step_recovery.MainActivity;
import com.citex.twelve_step_recovery.R;
import com.citex.twelve_step_recovery.database.DbHelper;
import com.citex.twelve_step_recovery.databinding.FragmentHomeBinding;
import com.citex.twelve_step_recovery.ui.home.daily_reflection.DailyReflectionFragment;
import com.jakewharton.threetenabp.AndroidThreeTen;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.threeten.bp.Period;
import org.threeten.bp.ZoneId;
import org.threeten.bp.ZonedDateTime;
import org.threeten.bp.format.DateTimeFormatter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;
    private DbHelper dbHelper;
    private int dailyImageId;
    private ImageView dailyImage;
    private int TotalDailyImages = 544;
    private int DailyImageLoadCount = 0;
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

        getContext().getTheme().applyStyle(R.style.Theme_RecoveryMeetingFinder, true);

        // Initialize AndroidThreeTen date parsing library.
        AndroidThreeTen.init(getActivity());

        // Initialize database.
        dbHelper = new DbHelper(getContext());

        // Setup ActionBar.
        if (getActivity() != null) {
            ActionBar mActionBar = ((MainActivity) getActivity()).getSupportActionBar();
            if (mActionBar != null) {
                mActionBar.setIcon(R.drawable.ic_home_24dp);
                mActionBar.setDisplayUseLogoEnabled(true);
                mActionBar.setDisplayShowHomeEnabled(true);
                mActionBar.setDisplayShowCustomEnabled(false);
                mActionBar.setTitle("  " + getResources().getString(R.string.home));
            }
        }

        // Setup View Binding.
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Setup ViewModel.
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        final TextView textSobrietyDate = binding.textSobrietyDate;
        homeViewModel.getSobrietyDate().observe(getViewLifecycleOwner(), textSobrietyDate::setText);

        // Get sobriety date from database.
        Date sobrietyDate = getSobrietyDateDb();

        // Set ViewModel.
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd yyyy", Locale.UK);
        homeViewModel.setSobrietyDate(sdf.format(sobrietyDate));

        final TextView textDaysSober = binding.textTimeSober;
        homeViewModel.getTimeSober().observe(getViewLifecycleOwner(), textDaysSober::setText);

        final TextView textDaysSoberLabel = binding.textTimeSoberLabel;
        homeViewModel.getTimeSoberLabel().observe(getViewLifecycleOwner(), textDaysSoberLabel::setText);

        int timeSoberFormat = getCounterFormatDb();
        if(timeSoberFormat == 0)
            homeViewModel.setTimeSoberLabel("Days");
        else if(timeSoberFormat == 1)
            homeViewModel.setTimeSoberLabel("Years");

        // Set time sober.
        SpannableString timeSober = calculateTimeSober(sobrietyDate);
        homeViewModel.setTimeSober(timeSober);

        final TextView textStep = binding.textStep;
        homeViewModel.getStep().observe(getViewLifecycleOwner(), textStep::setText);

        final TextView textStepDescription = binding.textStepDescription;
        homeViewModel.getStepDescription().observe(getViewLifecycleOwner(), textStepDescription::setText);

        // Get step from database.
        int step = getStepDb();

        // Set step.
        homeViewModel.setStep(String.valueOf(step));
        homeViewModel.setStepDescription(getStepDescription(step));

        // Set device country code in database.
        String countryCode = getCountryCode();
        setCountryCodeDb(countryCode);

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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        // Display DatePicker dialog on sobriety date click.
        TextView textSobrietyDateLabel = view.findViewById(R.id.text_sobriety_date_label );
        textSobrietyDateLabel.setOnClickListener(v -> {
            DisplaySetSobrietyDatePicker();
        });
        TextView textSobrietyDate= view.findViewById(R.id.text_sobriety_date);
        textSobrietyDate.setOnClickListener(v -> {
            DisplaySetSobrietyDatePicker();
        });

        // Display format picker on days sober click.
        TextView textDaysSoberLabel = view.findViewById(R.id.text_time_sober_label);
        textDaysSoberLabel.setOnClickListener(v -> {
            DisplayTimeSoberFormatDialog();
        });
        TextView textDaysSober = view.findViewById(R.id.text_time_sober);
        textDaysSober.setOnClickListener(v -> {
            DisplayTimeSoberFormatDialog();
        });


        // Display number picker on step click.
        TextView textStepLabel = view.findViewById(R.id.text_step_label);
        textStepLabel.setOnClickListener(v -> {
            DisplayStepPickerDialog();
        });
        TextView textStep = view.findViewById(R.id.text_step);
        textStep.setOnClickListener(v -> {
            DisplayStepPickerDialog();
        });

        // Display DailyReflectionFragment.
        TextView textDailyReflection;
        textDailyReflection = view.findViewById(R.id.text_daily_reflection);
        textDailyReflection.setOnClickListener(v -> {
            if (getActivity() != null) {
                NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_activity_main);
                navController.navigate(R.id.action_navigation_home_to_navigation_daily_reflection);
            }
        });

        // Display ThoughtForTheDayFragment.
        TextView textThoughtForTheDay;
        textThoughtForTheDay = view.findViewById(R.id.text_thought_for_the_day);
        textThoughtForTheDay.setOnClickListener(v -> {
            if (getActivity() != null) {
                NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_activity_main);
                navController.navigate(R.id.action_navigation_home_to_navigation_thought_for_the_day);
            }
        });

        // Display PrayersFragment.
        TextView textPrayers;
        textPrayers = view.findViewById(R.id.text_just_for_today);
        textPrayers.setOnClickListener(v -> {
            if (getActivity() != null) {
                NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_activity_main);
                navController.navigate(R.id.action_navigation_home_to_navigation_just_for_today);
            }
        });

        // Display ReadingsIndexFragment.
        TextView textReadings;
        textReadings = view.findViewById(R.id.text_readings);
        textReadings.setOnClickListener(v -> {
            if (getActivity() != null) {

                Bundle bundle = new Bundle();
                bundle.putString("title", "Readings");
                bundle.putString("indexFilename", "readings.csv");

                NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_activity_main);
                navController.navigate(R.id.action_navigation_home_to_navigation_readings, bundle);
            }
        });

        // Load daily image with Picasso.
        dailyImageId = new Random().nextInt(TotalDailyImages + 1);
        dailyImage = view.findViewById(R.id.image_daily_image);
        setShareButtonOnClickListener(dailyImage);
        LoadDailyImage(false);

        // Share daily image.
        TextView textShare;
        textShare = view.findViewById(R.id.text_daily_image_share);
        setShareButtonOnClickListener(textShare);

        // Privacy policy link.
        TextView textPrivacyLabel = view.findViewById(R.id.text_privacy_policy);
        textPrivacyLabel.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("title", "Privacy Policy");
            bundle.putString("indexFilename", "privacy_policy.html");
            NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_activity_main);
            navController.navigate(R.id.action_navigation_home_to_navigation_declarations_content, bundle);
        });

        // Health declaration link.
        TextView textHealthDeclaration = view.findViewById(R.id.text_health_declaration);
        textHealthDeclaration.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("title", "Health Declaration");
            bundle.putString("indexFilename", "health_declaration.html");
            NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_activity_main);
            navController.navigate(R.id.action_navigation_home_to_navigation_declarations_content, bundle);
        });

        // Website link.
        TextView textWebsite = view.findViewById(R.id.text_website);
        textWebsite.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.recoverymeetingfinder.com"));
            startActivity(browserIntent);
        });

        // Email link.
        TextView textEmail = view.findViewById(R.id.text_email);
        textEmail.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("mailto:support@recoverymeetingfinder.com"));
            startActivity(browserIntent);
        });
    }

    private void LoadDailyImage(boolean loadDefault) {
        Picasso.get().load("file:///android_asset/daily-image/" + dailyImageId  + ".jpg")
                .transform(new RoundedCornersTransformation(20,0))
                .into(dailyImage, new Callback() {
                            @Override
                            public void onSuccess() {
                                //Success image already loaded into the view
                            }
                            @Override
                            public void onError(Exception e) {
                                if(loadDefault) {
                                    DailyImageLoadCount = 0;
                                    Picasso.get().load(R.drawable.daily_image)
                                            .transform(new RoundedCornersTransformation(20,0))
                                            .into(dailyImage);
                                    return;
                                }

                                DailyImageLoadCount++;
                                if(DailyImageLoadCount < 2)
                                {
                                    LoadDailyImage(false);
                                }
                                else
                                {
                                    LoadDailyImage(true);
                                }

                            }
                        }
                );
    }

    /**
     * Displays the set sobriety date picker.
     */
    public void DisplaySetSobrietyDatePicker() {

        // Set DateSet listener.
        DatePickerDialog.OnDateSetListener dateSetCallback = (view1, year, month, day) -> {

            // Initialize the Calendar with the date set in dialog.
            Calendar calendarSobrietyDate = Calendar.getInstance();
            calendarSobrietyDate.set(Calendar.YEAR, year);
            calendarSobrietyDate.set(Calendar.MONTH, month);
            calendarSobrietyDate.set(Calendar.DAY_OF_MONTH, day);

            // Set sobriety date.
            setSobrietyDateDb(calendarSobrietyDate.getTime());

            // Set ViewModel.
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd yyyy", Locale.UK);
            homeViewModel.setSobrietyDate(sdf.format(calendarSobrietyDate.getTime()));

            // Set days sober.
            SpannableString timeSober = calculateTimeSober(calendarSobrietyDate.getTime());
            homeViewModel.setTimeSober(timeSober);
        };

        // Get sobriety date from database.
        Date databaseSobrietyDate = getSobrietyDateDb();

        // Initialize calendar with sobriety date.
        Calendar calendarToday = Calendar.getInstance();
        calendarToday.setTime(databaseSobrietyDate);
        int year = calendarToday.get(Calendar.YEAR);
        int month = calendarToday.get(Calendar.MONTH);
        int day = calendarToday.get(Calendar.DAY_OF_MONTH);
        new DatePickerDialog(getActivity(), dateSetCallback, year, month, day).show();
    }

    /**
     * Displays the time sober days or years format dialog.
     */
    public void DisplayTimeSoberFormatDialog() {

        new MaterialDialog.Builder(getActivity())
                .items(new String[]{"Days", "Years"})
                .itemsCallback((dialog, view12, selection, text) -> {

                    // Set counter format ID in database.
                    setCounterFormatDb(selection);

                    // Set time sober label.
                    if (selection == 0) {
                        homeViewModel.setTimeSoberLabel("Days");
                    } else if (selection == 1) {
                        homeViewModel.setTimeSoberLabel("Years");
                    }

                    // Update time sober counter with formatting.
                    Date sobrietyDate = getSobrietyDateDb();
                    SpannableString timeSober = calculateTimeSober(sobrietyDate);
                    homeViewModel.setTimeSober(timeSober);

                }).itemsGravity(GravityEnum.CENTER)
                .titleGravity(GravityEnum.CENTER)
                .show();
    }


    /**
     * Displays the select step dialog.
     */
    public void DisplayStepPickerDialog() {
        // Show 12-step picker dialog.
        HomeStepNumberPickerDialog dialogStepNumberPicker = new HomeStepNumberPickerDialog();
        if (getActivity() != null) {

            // Set default value.
            Bundle args = new Bundle();
            args.putInt("default_value", getStepDb());
            dialogStepNumberPicker.setArguments(args);

            dialogStepNumberPicker.show(getActivity().getSupportFragmentManager(), "Step Picker");
        }

        // Set OnValueChanged listener.
        dialogStepNumberPicker.setValueChangeListener((numberPicker, i, i1) -> {

            // Set step in database.
            setStepDb(i);

            // Set ViewModel.
            homeViewModel.setStepDescription(getStepDescription(i));
            homeViewModel.setStep(String.valueOf(i));
        });
    }

    /**
     * Adds a OnClickListener to the share button which opens the share image intent.
     * @param view The View for the fragment's UI.
     */
    private void setShareButtonOnClickListener(View view) {

        view.setOnClickListener(v-> {
            InputStream istr;
            Bitmap bitmap = null;
            try {
                istr = getContext().getAssets().open("daily-image/" + dailyImageId  + ".jpg");
                bitmap = BitmapFactory.decodeStream(istr);
            } catch (IOException e) {
                bitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.daily_image);
            }

            Uri uri = saveImageToCache(bitmap);

            Intent i = new Intent(Intent.ACTION_SEND);
            i.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            i.setType("image/*");
            i.putExtra(Intent.EXTRA_STREAM, uri);
            startActivity(Intent.createChooser(i, "Share Image"));

            if(getActivity() != null) {
                List<ResolveInfo> resInfoList = getActivity().getPackageManager().queryIntentActivities(i, PackageManager.MATCH_DEFAULT_ONLY);
                for (ResolveInfo resolveInfo : resInfoList) {
                    String packageName = resolveInfo.activityInfo.packageName;
                    getActivity().grantUriPermission(packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }
            }
        });
    }

    /**
     * Called when the view previously created by onCreateView() has been detached from the
     * fragment. The next time the fragment needs to be displayed, a new view will be created.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    /**
     * Calculates a string containing the days sober or days, months and years depending on the
     * counter format.
     * @param sobrietyDate Date object containing sobriety date.
     */
    private SpannableString calculateTimeSober(Date sobrietyDate) {

        // Get current date as Calendar.
        Calendar calendarToday = Calendar.getInstance();

        // Get sobriety date as Calendar.
        Calendar calendarSobrietyDate = Calendar.getInstance();
        calendarSobrietyDate.setTime(sobrietyDate);

        int counterFormatId = getCounterFormatDb();

        // Define format.
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss").withZone(ZoneId.of("Europe/London"));

        // Date/time strings.
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.UK);
        String strEndDate = sdf.format(calendarToday.getTime());

        sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.UK);
        String strStartDate = sdf.format(calendarSobrietyDate.getTime());

        // Parse date/time strings into OffsetDateTime.
        ZonedDateTime startDate = ZonedDateTime.parse(strStartDate, formatter);
        ZonedDateTime endDate  = ZonedDateTime.parse(strEndDate, formatter);

        // Calculate period between `startDate` and `endDate`.
        Period period = Period.between(startDate.toLocalDate(), endDate.toLocalDate());

        if (counterFormatId == 0) { // Days
            long msDiff = calendarToday.getTimeInMillis() - calendarSobrietyDate.getTimeInMillis();
            long daysDiff = TimeUnit.MILLISECONDS.toDays(msDiff);
            return new SpannableString(Long.toString(daysDiff));
        }
        else if(counterFormatId == 1) { // Days, Months, Years
            int days = 0;
            if(period.getDays() > 0)
                days = period.getDays();

            String timeSober = period.getYears() + "Y " + period.getMonths() + "M " + period.getDays() + "D";
            SpannableString timeSoberSpan = new SpannableString(timeSober);

            float spanSize = 0.8f;

            int yearsIndex = timeSober.indexOf('Y');
            timeSoberSpan.setSpan(new RelativeSizeSpan(spanSize), yearsIndex,yearsIndex + 1, 0);

            int monthsIndex = timeSober.indexOf('M');
            timeSoberSpan.setSpan(new RelativeSizeSpan(spanSize), monthsIndex,monthsIndex + 1, 0);

            int dayIndex = timeSober.indexOf('D');
            timeSoberSpan.setSpan(new RelativeSizeSpan(spanSize), dayIndex,dayIndex + 1, 0);

            return timeSoberSpan;
        }
        return new SpannableString("");
    }

    public String getCountOfDays(String createdDateString, String expireDateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        Date createdConvertedDate = null, expireCovertedDate = null, todayWithZeroTime = null;
        try {
            createdConvertedDate = dateFormat.parse(createdDateString);
            expireCovertedDate = dateFormat.parse(expireDateString);

            Date today = new Date();

            todayWithZeroTime = dateFormat.parse(dateFormat.format(today));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        int cYear = 0, cMonth = 0, cDay = 0;

        if (createdConvertedDate.after(todayWithZeroTime)) {
            Calendar cCal = Calendar.getInstance();
            cCal.setTime(createdConvertedDate);
            cYear = cCal.get(Calendar.YEAR);
            cMonth = cCal.get(Calendar.MONTH);
            cDay = cCal.get(Calendar.DAY_OF_MONTH);

        } else {
            Calendar cCal = Calendar.getInstance();
            cCal.setTime(todayWithZeroTime);
            cYear = cCal.get(Calendar.YEAR);
            cMonth = cCal.get(Calendar.MONTH);
            cDay = cCal.get(Calendar.DAY_OF_MONTH);
        }


    /*Calendar todayCal = Calendar.getInstance();
    int todayYear = todayCal.get(Calendar.YEAR);
    int today = todayCal.get(Calendar.MONTH);
    int todayDay = todayCal.get(Calendar.DAY_OF_MONTH);
    */

        Calendar eCal = Calendar.getInstance();
        eCal.setTime(expireCovertedDate);

        int eYear = eCal.get(Calendar.YEAR);
        int eMonth = eCal.get(Calendar.MONTH);
        int eDay = eCal.get(Calendar.DAY_OF_MONTH);

        Calendar date1 = Calendar.getInstance();
        Calendar date2 = Calendar.getInstance();

        date1.clear();
        date1.set(cYear, cMonth, cDay);
        date2.clear();
        date2.set(eYear, eMonth, eDay);

        long diff = date2.getTimeInMillis() - date1.getTimeInMillis();

        float dayCount = (float) diff / (24 * 60 * 60 * 1000);

        return ("" + (int) dayCount + " Days");
    }


    /**
     * Sets the sobriety date in the database.
     * @param sobrietyDate Date object containing sobriety date.
     */
    private void setSobrietyDateDb(Date sobrietyDate) {

        // Get database.
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // New value for one column
        long dbSobrietyDate = sobrietyDate.getTime();
        ContentValues values = new ContentValues();
        values.put(HomeDbContract.HomeEntry.COLUMN_NAME_SOBRIETY_DATE, dbSobrietyDate);

        // Which row to update, based on the title
        String selection = HomeDbContract.HomeEntry._ID + " LIKE ?";
        String[] selectionArgs = {"1"};

        db.update(
                HomeDbContract.HomeEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs);
    }

    /***
     * Gets the sobriety date from the database.
     * @return Date object containing sobriety date.
     */
    private Date getSobrietyDateDb() {

        // Get database.
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Set columns to retrieve.
        String[] projection = {
                HomeDbContract.HomeEntry._ID,
                HomeDbContract.HomeEntry.COLUMN_NAME_SOBRIETY_DATE
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

        List<Long> itemIds = new ArrayList<>();
        while (cursor.moveToNext()) {
            long itemId = cursor.getLong(
                    cursor.getColumnIndexOrThrow(HomeDbContract.HomeEntry.COLUMN_NAME_SOBRIETY_DATE));
            itemIds.add(itemId);
        }
        cursor.close();

        // Set sobriety date.
        long sobrietyDbValue = itemIds.get(0);
        return new Date(sobrietyDbValue);
    }

    /***
     * Sets the step number in the database.
     * @param step Step number.
     */
    private void setStepDb(int step) {

        // Get database.
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // New value for one column
        ContentValues values = new ContentValues();
        values.put(HomeDbContract.HomeEntry.COLUMN_NAME_STEP_NUMBER, step);

        // Which row to update, based on the title
        String selection = HomeDbContract.HomeEntry._ID + " LIKE ?";
        String[] selectionArgs = {"1"};

        db.update(
                HomeDbContract.HomeEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs);
    }

    /**
     * Gets the step number from the database.
     * @return Step number.
     */
    private int getStepDb() {

        // Get database.
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Set columns to retrieve.
        String[] projection = {
                HomeDbContract.HomeEntry._ID,
                HomeDbContract.HomeEntry.COLUMN_NAME_STEP_NUMBER
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

        List<Integer> itemIds = new ArrayList<>();
        while (cursor.moveToNext()) {
            long itemId = cursor.getInt(
                    cursor.getColumnIndexOrThrow(HomeDbContract.HomeEntry.COLUMN_NAME_STEP_NUMBER));
            itemIds.add((int) itemId);
        }
        cursor.close();

        return itemIds.get(0);
    }

    /***
     * Sets the counter format in the database.
     * @param counterFormatId Counter format ID.
     */
    private void setCounterFormatDb(int counterFormatId) {

        // Get database.
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // New value for one column
        ContentValues values = new ContentValues();
        values.put(HomeDbContract.HomeEntry.COLUMN_NAME_COUNTER_FORMAT, counterFormatId);

        // Which row to update, based on the title
        String selection = HomeDbContract.HomeEntry._ID + " LIKE ?";
        String[] selectionArgs = {"1"};

        db.update(
                HomeDbContract.HomeEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs);
    }

    /**
     * Gets the counter format ID from the database.
     * @return Counter format ID.
     */
    private int getCounterFormatDb() {

        // Get database.
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Set columns to retrieve.
        String[] projection = {
                HomeDbContract.HomeEntry._ID,
                HomeDbContract.HomeEntry.COLUMN_NAME_COUNTER_FORMAT
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

        List<Integer> itemIds = new ArrayList<>();
        while (cursor.moveToNext()) {
            int itemId = cursor.getInt(
                    cursor.getColumnIndexOrThrow(HomeDbContract.HomeEntry.COLUMN_NAME_COUNTER_FORMAT));
            itemIds.add(itemId);
        }
        cursor.close();

        return itemIds.get(0);
    }

    /**
     * Saves the image as PNG to the app's cache directory.
     * @param image Bitmap to save.
     * @return Uri of the saved file or null
     */
    private Uri saveImageToCache(Bitmap image) {
        //TODO - Should be processed in another thread
        File imagesFolder = new File(getActivity().getCacheDir(), "images");
        Uri uri = null;
        try {
            imagesFolder.mkdirs();
            File file = new File(imagesFolder, "shared_image.png");

            FileOutputStream stream = new FileOutputStream(file);
            image.compress(Bitmap.CompressFormat.PNG, 90, stream);
            stream.flush();
            stream.close();
            uri = FileProvider.getUriForFile(getContext(), "com.citex.twelve_step_recovery.provider", file);

        } catch (IOException e) {
            Log.d(TAG, "IOException while trying to write file for sharing: " + e.getMessage());
        }
        return uri;
    }

    /***
     * Gets country code from ip-api.com or device locality if that fails and updates the database.
     * @return Country code.
     */
    private String getCountryCode() {
        TelephonyManager tm = (TelephonyManager)this.getContext().getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getNetworkCountryIso().toUpperCase();
    }

    /**
     * Updates the country code in the database table.
     * @param countryCode Country code.
     */
    private void setCountryCodeDb(String countryCode) {

        // Get database.
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // New value for one column
        ContentValues values = new ContentValues();
        values.put(HomeDbContract.HomeEntry.COLUMN_NAME_COUNTRY_CODE, countryCode);

        // Which row to update, based on the title
        String selection = HomeDbContract.HomeEntry._ID + " LIKE ?";
        String[] selectionArgs = {"1"};

        db.update(
                HomeDbContract.HomeEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs);
    }

    /**
     * Gets a description of a 12-step.
     * @param stepNumber Step number.
     * @return Description.
     */
    private String getStepDescription(int stepNumber) {
        String stepDescription = "";
        switch (stepNumber) {
            case 1: stepDescription = getResources().getString(R.string.step1); break;
            case 2: stepDescription = getResources().getString(R.string.step2); break;
            case 3: stepDescription = getResources().getString(R.string.step3); break;
            case 4: stepDescription = getResources().getString(R.string.step4); break;
            case 5: stepDescription = getResources().getString(R.string.step5); break;
            case 6: stepDescription = getResources().getString(R.string.step6); break;
            case 7: stepDescription = getResources().getString(R.string.step7); break;
            case 8: stepDescription = getResources().getString(R.string.step8); break;
            case 9: stepDescription = getResources().getString(R.string.step9); break;
            case 10: stepDescription = getResources().getString(R.string.step10); break;
            case 11: stepDescription = getResources().getString(R.string.step11); break;
            case 12: stepDescription = getResources().getString(R.string.step12); break;
            default: break;
        }
        return stepDescription;
    }
}