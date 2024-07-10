package com.citex.twelve_step_recovery.database;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.citex.twelve_step_recovery.ui.audio.AudioDbContract;
import com.citex.twelve_step_recovery.ui.home.HomeDbContract;
import com.citex.twelve_step_recovery.ui.meetings.MeetingFilterDbContract;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.enums.CSVReaderNullFieldIndicator;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Random;

public class DbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "rmf.db";
    private static final String TAG = DbHelper.class.getName();
    private Context context;

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    public void onCreate(SQLiteDatabase db) {

        // Create tables.
        db.execSQL(HomeDbContract.HomeEntry.SQL_CREATE_ENTRIES);
        db.execSQL(MeetingFilterDbContract.MeetingFilterEntry.SQL_CREATE_ENTRIES);
        db.execSQL(AudioDbContract.AudioEntry.SQL_CREATE_ENTRIES);

        // Insert initial rows tables.
        Calendar calendarToday = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.UK);

        db.execSQL("insert into " + HomeDbContract.HomeEntry.TABLE_NAME + "(" +
                HomeDbContract.HomeEntry._ID + "," +
                HomeDbContract.HomeEntry.COLUMN_NAME_SOBRIETY_DATE + "," +
                HomeDbContract.HomeEntry.COLUMN_NAME_STEP_NUMBER + "," +
                HomeDbContract.HomeEntry.COLUMN_NAME_COUNTER_FORMAT + "," +
                HomeDbContract.HomeEntry.COLUMN_NAME_COUNTRY_CODE + ") " +
                "values(" +
                1 + "," +
                calendarToday.getTimeInMillis() + "," +
                1 + "," +
                0 + "," +
                "'UK')");

        db.execSQL("insert into " + MeetingFilterDbContract.MeetingFilterEntry.TABLE_NAME + "(" +
                MeetingFilterDbContract.MeetingFilterEntry._ID + "," +
                MeetingFilterDbContract.MeetingFilterEntry.COLUMN_NAME_MAP + "," +
                MeetingFilterDbContract.MeetingFilterEntry.COLUMN_NAME_AA + "," +
                MeetingFilterDbContract.MeetingFilterEntry.COLUMN_NAME_CA + "," +
                MeetingFilterDbContract.MeetingFilterEntry.COLUMN_NAME_NA + "," +
                MeetingFilterDbContract.MeetingFilterEntry.COLUMN_NAME_OA + "," +
                MeetingFilterDbContract.MeetingFilterEntry.COLUMN_NAME_WEEKDAY + "," +
                MeetingFilterDbContract.MeetingFilterEntry.COLUMN_NAME_SEARCH_RANGE + "," +
                MeetingFilterDbContract.MeetingFilterEntry.COLUMN_NAME_SEARCH_UNITS + "," +
                MeetingFilterDbContract.MeetingFilterEntry.COLUMN_NAME_WHEELCHAIR + ") " +
                "values(" +
                1 + "," +
                1 + "," +
                1 + "," +
                1 + "," +
                1 + "," +
                1 + "," +
                0 + "," + // All days
                5 + "," +
                "'Miles'," +
                0 + ")");

        // Read audio filenames and insert rows.
        int index = 1;
        try {
            AssetManager assetManager = context.getAssets();
            InputStream csvInputStream = assetManager.open("audio.csv");
            CSVReader reader = new CSVReaderBuilder(new InputStreamReader(csvInputStream))
                    .withFieldAsNull(CSVReaderNullFieldIndicator.EMPTY_SEPARATORS)
                    .withSkipLines(1)
                    .build();
            String[] nextLine;

            while ((nextLine = reader.readNext()) != null) {

                db.execSQL("insert into " + AudioDbContract.AudioEntry.TABLE_NAME + "(" +
                        AudioDbContract.AudioEntry._ID + "," +
                        AudioDbContract.AudioEntry.COLUMN_NAME_FILENAME + "," +
                        AudioDbContract.AudioEntry.COLUMN_NAME_FILE_INDEX + "," +
                        AudioDbContract.AudioEntry.COLUMN_NAME_PROGRESS + ") " +
                        "values(" +
                        index + "," +
                        "'" + nextLine[2] + "'" + "," +
                        1 + "," +
                        0 +  ")");
                index++;
            }
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(HomeDbContract.HomeEntry.SQL_DELETE_ENTRIES);
        db.execSQL(MeetingFilterDbContract.MeetingFilterEntry.SQL_DELETE_ENTRIES);
        db.execSQL(AudioDbContract.AudioEntry.SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}