package com.citex.twelve_step_recovery.ui.meetings;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.citex.twelve_step_recovery.database.DbHelper;

import java.util.ArrayList;
import java.util.List;

public class MeetingFilter {

    private final DbHelper dbHelper;

    public MeetingFilter(Context context) {
        // Initialize database.
        dbHelper = new DbHelper(context);
    }

    /**
     * Gets the map view filter column from the database.
     * @return Map view value.
     */
    public int getMapDb() {

        // Get database.
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Set columns to retrieve.
        String[] projection = {
                MeetingFilterDbContract.MeetingFilterEntry._ID,
                MeetingFilterDbContract.MeetingFilterEntry.COLUMN_NAME_MAP
        };

        // Filter results WHERE "title" = 'My Title'
        String selection = MeetingFilterDbContract.MeetingFilterEntry._ID + " = ?";
        String[] selectionArgs = {"1"};

        // How you want the results sorted in the resulting Cursor
        String sortOrder = MeetingFilterDbContract.MeetingFilterEntry._ID + " DESC";

        Cursor cursor = db.query(
                MeetingFilterDbContract.MeetingFilterEntry.TABLE_NAME,   // The table to query
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
                    cursor.getColumnIndexOrThrow(MeetingFilterDbContract.MeetingFilterEntry.COLUMN_NAME_MAP));
            itemIds.add(itemId);
        }
        cursor.close();

        return itemIds.get(0);
    }


    /***
     * Sets the map view filter column in the database.
     * @param checked Is the map toggled.
     */
    public void setMapDb(int checked) {

        // Get database.
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // New value for one column
        ContentValues values = new ContentValues();
        values.put(MeetingFilterDbContract.MeetingFilterEntry.COLUMN_NAME_MAP, checked);

        // Which row to update, based on the title
        String selection = MeetingFilterDbContract.MeetingFilterEntry._ID + " LIKE ?";
        String[] selectionArgs = {"1"};

        db.update(
                MeetingFilterDbContract.MeetingFilterEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs);
    }

    /***
     * Sets the program filter column in the database.
     * @param checked Is the switch checked.
     */
    public void setProgramDb(String columnName, boolean checked) {

        // Get database.
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // New value for one column
        ContentValues values = new ContentValues();
        values.put(columnName, checked);

        // Which row to update, based on the title
        String selection = MeetingFilterDbContract.MeetingFilterEntry._ID + " LIKE ?";
        String[] selectionArgs = {"1"};

        db.update(
                MeetingFilterDbContract.MeetingFilterEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs);
    }

    /**
     * Gets the program filter column from the database.
     * @return Program value.
     */
    public int getProgramDb(String columnName) {

        // Get database.
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Set columns to retrieve.
        String[] projection = {
                MeetingFilterDbContract.MeetingFilterEntry._ID,
                columnName
        };

        // Filter results WHERE "title" = 'My Title'
        String selection = MeetingFilterDbContract.MeetingFilterEntry._ID + " = ?";
        String[] selectionArgs = {"1"};

        // How you want the results sorted in the resulting Cursor
        String sortOrder = MeetingFilterDbContract.MeetingFilterEntry._ID + " DESC";

        Cursor cursor = db.query(
                MeetingFilterDbContract.MeetingFilterEntry.TABLE_NAME,   // The table to query
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
                    cursor.getColumnIndexOrThrow(columnName));
            itemIds.add(itemId);
        }
        cursor.close();

        return itemIds.get(0);
    }

    /***
     * Sets the weekday filter column in the database.
     * @param value The weekday ID.
     */
    public void setWeekdayDb(int value) {

        // Get database.
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // New value for one column
        ContentValues values = new ContentValues();
        values.put(MeetingFilterDbContract.MeetingFilterEntry.COLUMN_NAME_WEEKDAY, value);

        // Which row to update, based on the title
        String selection = MeetingFilterDbContract.MeetingFilterEntry._ID + " LIKE ?";
        String[] selectionArgs = {"1"};

        db.update(
                MeetingFilterDbContract.MeetingFilterEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs);
    }

    /**
     * Gets the weekday filter column from the database.
     * @return Weekday value.
     */
    public int getWeekdayDb() {

        // Get database.
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Set columns to retrieve.
        String[] projection = {
                MeetingFilterDbContract.MeetingFilterEntry._ID,
                MeetingFilterDbContract.MeetingFilterEntry.COLUMN_NAME_WEEKDAY
        };

        // Filter results WHERE "title" = 'My Title'
        String selection = MeetingFilterDbContract.MeetingFilterEntry._ID + " = ?";
        String[] selectionArgs = {"1"};

        // How you want the results sorted in the resulting Cursor
        String sortOrder = MeetingFilterDbContract.MeetingFilterEntry._ID + " DESC";

        Cursor cursor = db.query(
                MeetingFilterDbContract.MeetingFilterEntry.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,           // don't group the rows
                null,            // don't filter by row groups
                sortOrder               // The sort order
        );

        List<Integer> itemIds = new ArrayList<>();
        while (cursor.moveToNext()) {
            long itemId = cursor.getLong(
                    cursor.getColumnIndexOrThrow(MeetingFilterDbContract.MeetingFilterEntry.COLUMN_NAME_WEEKDAY));
            itemIds.add((int) itemId);
        }
        cursor.close();

        return itemIds.get(0);
    }

    /**
     * Gets the search units column from the database.
     * @return Search units value.
     */
    public String getSearchUnitsDb() {

        // Get database.
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Set columns to retrieve.
        String[] projection = {
                MeetingFilterDbContract.MeetingFilterEntry._ID,
                MeetingFilterDbContract.MeetingFilterEntry.COLUMN_NAME_SEARCH_UNITS
        };

        // Filter results WHERE "title" = 'My Title'
        String selection = MeetingFilterDbContract.MeetingFilterEntry._ID + " = ?";
        String[] selectionArgs = {"1"};

        // How you want the results sorted in the resulting Cursor
        String sortOrder = MeetingFilterDbContract.MeetingFilterEntry._ID + " DESC";

        Cursor cursor = db.query(
                MeetingFilterDbContract.MeetingFilterEntry.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,           // don't group the rows
                null,            // don't filter by row groups
                sortOrder               // The sort order
        );

        List<String> itemIds = new ArrayList<>();
        while (cursor.moveToNext()) {
            String itemId = cursor.getString(
                    cursor.getColumnIndexOrThrow(MeetingFilterDbContract.MeetingFilterEntry.COLUMN_NAME_SEARCH_UNITS));
            itemIds.add(itemId);
        }
        cursor.close();

        return itemIds.get(0);
    }

    /***
     * Sets the search units column in the database.
     * @param  searchUnits The search unit type.
     */
    public void setSearchUnitsDb(String searchUnits) {

        // Get database.
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // New value for one column
        ContentValues values = new ContentValues();
        values.put(MeetingFilterDbContract.MeetingFilterEntry.COLUMN_NAME_SEARCH_UNITS, searchUnits);

        // Which row to update, based on the title
        String selection = MeetingFilterDbContract.MeetingFilterEntry._ID + " LIKE ?";
        String[] selectionArgs = {"1"};

        db.update(
                MeetingFilterDbContract.MeetingFilterEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs);
    }

    /**
     * Gets the search range filter column from the database.
     * @return Search range value.
     */
    public int getSearchRangeDb() {

        // Get database.
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Set columns to retrieve.
        String[] projection = {
                MeetingFilterDbContract.MeetingFilterEntry._ID,
                MeetingFilterDbContract.MeetingFilterEntry.COLUMN_NAME_SEARCH_RANGE
        };

        // Filter results WHERE "title" = 'My Title'
        String selection = MeetingFilterDbContract.MeetingFilterEntry._ID + " = ?";
        String[] selectionArgs = {"1"};

        // How you want the results sorted in the resulting Cursor
        String sortOrder = MeetingFilterDbContract.MeetingFilterEntry._ID + " DESC";

        Cursor cursor = db.query(
                MeetingFilterDbContract.MeetingFilterEntry.TABLE_NAME,   // The table to query
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
                    cursor.getColumnIndexOrThrow(MeetingFilterDbContract.MeetingFilterEntry.COLUMN_NAME_SEARCH_RANGE));
            itemIds.add(itemId);
        }
        cursor.close();

        return itemIds.get(0);
    }

    /***
     * Sets the search range column in the database.
     * @param  searchRange The search range.
     */
    public void setSearchRangeDb(int searchRange) {

        // Get database.
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // New value for one column
        ContentValues values = new ContentValues();
        values.put(MeetingFilterDbContract.MeetingFilterEntry.COLUMN_NAME_SEARCH_RANGE, searchRange);

        // Which row to update, based on the title
        String selection = MeetingFilterDbContract.MeetingFilterEntry._ID + " LIKE ?";
        String[] selectionArgs = {"1"};

        db.update(
                MeetingFilterDbContract.MeetingFilterEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs);
    }

    public MeetingFilterDetails getMeetingFilterDetails() {

        MeetingFilterDetails meetingFilterDetails = new MeetingFilterDetails();

        // Program.
        int programAA = getProgramDb(MeetingFilterDbContract.MeetingFilterEntry.COLUMN_NAME_AA);
        int programCA = getProgramDb(MeetingFilterDbContract.MeetingFilterEntry.COLUMN_NAME_CA);
        int programNA = getProgramDb(MeetingFilterDbContract.MeetingFilterEntry.COLUMN_NAME_NA);
        int programOA = getProgramDb(MeetingFilterDbContract.MeetingFilterEntry.COLUMN_NAME_OA);

        if(programAA == 0 && programCA == 0 && programNA == 0 && programOA == 0)
            meetingFilterDetails.Program = "all";

        else {

            meetingFilterDetails.Program = "";

            if (programAA != 0)
                meetingFilterDetails.Program += "1,";

            if(programCA != 0)
                meetingFilterDetails.Program += "2,";

            if(programNA != 0)
                meetingFilterDetails.Program += "3,";

            if(programOA != 0)
                meetingFilterDetails.Program += "4,";

            meetingFilterDetails.Program = meetingFilterDetails.Program.substring(0, meetingFilterDetails.Program.length() - 1);
        }

        // Weekday.
        int weekday = getWeekdayDb();
        if(weekday == 0)
            meetingFilterDetails.Weekday = "all";
        else
            meetingFilterDetails.Weekday = String.valueOf(weekday);

        // Search range.
        meetingFilterDetails.SearchRange = String.valueOf(getSearchRangeDb());

        // Search units.
        if(getSearchUnitsDb().equals("Miles"))
            meetingFilterDetails.SearchUnits= "miles";
        else
            meetingFilterDetails.SearchUnits = "km";

        // Wheelchair accessibility.
        int wheelchair = getProgramDb(MeetingFilterDbContract.MeetingFilterEntry.COLUMN_NAME_WHEELCHAIR);
        if(wheelchair == 1)
            meetingFilterDetails.Accessibility = "wheelchair";

        return meetingFilterDetails;
    }

    public class MeetingFilterDetails {
        public String Program;
        public String Weekday;
        public String SearchRange;
        public String SearchUnits;
        public String Accessibility;
    }
}
