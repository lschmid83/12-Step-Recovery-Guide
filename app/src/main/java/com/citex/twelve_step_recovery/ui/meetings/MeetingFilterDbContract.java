package com.citex.twelve_step_recovery.ui.meetings;

import android.provider.BaseColumns;

public final class MeetingFilterDbContract {
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private MeetingFilterDbContract() {}

    /* Inner class that defines the table contents */
    public static class MeetingFilterEntry implements BaseColumns {
        public static final String TABLE_NAME = "meeting_filter";
        public static final String COLUMN_NAME_MAP = "map";
        public static final String COLUMN_NAME_AA = "aa";
        public static final String COLUMN_NAME_CA = "ca";
        public static final String COLUMN_NAME_NA = "na";
        public static final String COLUMN_NAME_OA = "oa";
        public static final String COLUMN_NAME_WEEKDAY = "weekday";
        public static final String COLUMN_NAME_SEARCH_RANGE = "search_range";
        public static final String COLUMN_NAME_SEARCH_UNITS = "search_units";
        public static final String COLUMN_NAME_WHEELCHAIR = "wheelchair";

        public static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + MeetingFilterDbContract.MeetingFilterEntry.TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY," +
                        COLUMN_NAME_MAP + " INTEGER," +
                        COLUMN_NAME_AA + " INTEGER," +
                        COLUMN_NAME_CA + " INTEGER," +
                        COLUMN_NAME_NA + " INTEGER," +
                        COLUMN_NAME_OA + " INTEGER," +
                        COLUMN_NAME_WEEKDAY + " INTEGER," +
                        COLUMN_NAME_SEARCH_RANGE + " INTEGER," +
                        COLUMN_NAME_SEARCH_UNITS + " TEXT," +
                        COLUMN_NAME_WHEELCHAIR + " INTEGER)";

        public static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + MeetingFilterEntry.TABLE_NAME;
    }
}