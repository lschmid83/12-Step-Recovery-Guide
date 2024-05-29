package com.citex.twelve_step_recovery.ui.home;

import android.provider.BaseColumns;

public final class HomeDbContract {
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private HomeDbContract() {}

    /* Inner class that defines the table contents */
    public static class HomeEntry implements BaseColumns {
        public static final String TABLE_NAME = "home";
        public static final String COLUMN_NAME_SOBRIETY_DATE = "sobriety_date";
        public static final String COLUMN_NAME_STEP_NUMBER = "step_number";
        public static final String COLUMN_NAME_COUNTER_FORMAT = "counter_format";
        public static final String COLUMN_NAME_DAILY_IMAGE_DATE = "daily_image_date";
        public static final String COLUMN_NAME_DAILY_IMAGE_ID = "daily_image_id";
        public static final String COLUMN_NAME_COUNTRY_CODE = "country_code";

        public static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY," +
                        COLUMN_NAME_SOBRIETY_DATE + " INTEGER," +
                        COLUMN_NAME_STEP_NUMBER + " INTEGER," +
                        COLUMN_NAME_COUNTER_FORMAT + " INTEGER," +
                        COLUMN_NAME_DAILY_IMAGE_DATE + " TEXT," +
                        COLUMN_NAME_DAILY_IMAGE_ID + " INTEGER," +
                        COLUMN_NAME_COUNTRY_CODE + " TEXT)";

        public static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + HomeEntry.TABLE_NAME;
    }
}