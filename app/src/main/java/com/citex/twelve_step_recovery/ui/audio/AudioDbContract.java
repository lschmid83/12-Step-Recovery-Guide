package com.citex.twelve_step_recovery.ui.audio;

import android.provider.BaseColumns;

public final class AudioDbContract {

    private AudioDbContract() {}

    /* Inner class that defines the table contents */
    public static class AudioEntry implements BaseColumns {

        public static final String TABLE_NAME = "audio";
        public static final String COLUMN_NAME_FILENAME = "filename";
        public static final String COLUMN_NAME_FILE_INDEX = "file_index";
        public static final String COLUMN_NAME_PROGRESS = "progress";

        public static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY," +
                        COLUMN_NAME_FILENAME + " TEXT," +
                        COLUMN_NAME_FILE_INDEX + " INTEGER," +
                        COLUMN_NAME_PROGRESS + " INTEGER)";

        public static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + AudioEntry.TABLE_NAME;
    }
}