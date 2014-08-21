package com.brianysu.alarmclockx;


import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.test.AndroidTestCase;
import android.util.Log;

import com.brianysu.alarmclockx.data.AlarmContract.AlarmEntry;

public class TestProvider extends AndroidTestCase {

    public static final String LOG_TAG = TestProvider.class.getSimpleName();

    // brings our database to an empty state
    public void deleteAllRecords() {
        mContext.getContentResolver().delete(
                AlarmEntry.CONTENT_URI,
                null,
                null
        );

        Cursor cursor = mContext.getContentResolver().query(
                AlarmEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals(0, cursor.getCount());
        cursor.close();
    }

    // Since we want each test to start with a clean slate, run deleteAllRecords
    // in setUp (called by the test runner before each test).
    public void setUp() {
        deleteAllRecords();
    }

    public void testInsertReadProvider() {

        ContentValues testValues = TestDb.createSampleAlarm();

        Uri alarmUri = mContext.getContentResolver().insert(AlarmEntry.CONTENT_URI, testValues);
        long alarmRowId = ContentUris.parseId(alarmUri);

        // Verify we got a row back.
        assertTrue(alarmRowId != -1);

        // Data's inserted.  IN THEORY.  Now pull some out to stare at it and verify it made
        // the round trip.

        // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                AlarmEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );

        TestDb.validateCursor(cursor, testValues);

        // Now see if we can successfully query if we include the row id
        cursor = mContext.getContentResolver().query(
                AlarmEntry.buildAlarmUri(alarmRowId),
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );

        TestDb.validateCursor(cursor, testValues);

    }

    public void testGetType() {
        // content://com.example.android.sunshine.app/weather/
        String type = mContext.getContentResolver().getType(AlarmEntry.CONTENT_URI);

        // content://com.example.android.sunshine.app/alarm/
        // vnd.android.cursor.dir/com.example.android.sunshine.app/alarm
        assertEquals(AlarmEntry.CONTENT_TYPE, type);

        // content://com.example.android.sunshine.app/alarm/1
        type = mContext.getContentResolver().getType(AlarmEntry.buildAlarmUri(1L));
        // vnd.android.cursor.item/com.example.android.sunshine.app/alarm
        assertEquals(AlarmEntry.CONTENT_ITEM_TYPE, type);
    }

    public void testUpdateAlarm() {
        // Create a new map of values, where column names are the keys
        ContentValues values = TestDb.createSampleAlarm();

        Uri alarmUri = mContext.getContentResolver().
                insert(AlarmEntry.CONTENT_URI, values);
        long alarmRowId = ContentUris.parseId(alarmUri);

        // Verify we got a row back.
        assertTrue(alarmRowId != -1);
        Log.d(LOG_TAG, "New row id: " + alarmRowId);

        ContentValues updatedValues = new ContentValues(values);
        updatedValues.put(AlarmEntry._ID, alarmRowId);
        updatedValues.put(AlarmEntry.COLUMN_NAME, "Santa's Village");
        updatedValues.put(AlarmEntry.COLUMN_REPEATED_DAYS, Integer.parseInt("0000001", 2));
        updatedValues.put(AlarmEntry.COLUMN_MINUTE, "36");
        updatedValues.put(AlarmEntry.COLUMN_HOUR, "1");
        updatedValues.put(AlarmEntry.COLUMN_TONE, "Magic");
        updatedValues.put(AlarmEntry.COLUMN_ENABLED, 0);


        int count = mContext.getContentResolver().update(
                AlarmEntry.CONTENT_URI, updatedValues, AlarmEntry._ID + "= ?",
                new String[] { Long.toString(alarmRowId)});

        assertEquals(count, 1);

        // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                AlarmEntry.buildAlarmUri(alarmRowId),
                null,
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null // sort order
        );

        TestDb.validateCursor(cursor, updatedValues);
    }

    // Make sure we can still delete after adding/updating stuff
    public void testDeleteRecordsAtEnd() {
        deleteAllRecords();
    }

    // Inserts both the location and weather data for the Kalamazoo data set.
    public long insertSampleData() {
        ContentValues values = TestDb.createSampleAlarm2();
        Uri alarmInsertUri = mContext.getContentResolver()
                .insert(AlarmEntry.CONTENT_URI, values);
        assertTrue(alarmInsertUri != null);

        return ContentUris.parseId(alarmInsertUri);

    }

    public void testUpdateAndReadAlarm() {
        long alarmRowId = insertSampleData();
        String newName = "Harry Potter";

        // Make an update to one value.
        ContentValues alarmUpdate = new ContentValues();
        alarmUpdate.put(AlarmEntry.COLUMN_NAME, newName);

        mContext.getContentResolver().update(
                AlarmEntry.CONTENT_URI, alarmUpdate, null, null);

        // A cursor is your primary interface to the query results.
        Cursor alarmCursor = mContext.getContentResolver().query(
                AlarmEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        // Make the same update to the full ContentValues for comparison.
        ContentValues alarmAltered = TestDb.createSampleAlarm2();
        alarmAltered.put(AlarmEntry.COLUMN_NAME, newName);
        alarmAltered.put(AlarmEntry._ID, alarmRowId);

        TestDb.validateCursor(alarmCursor, alarmAltered);
    }

    public void testRemoveAndReadAlarm() {
        long alarmRowId = insertSampleData();

        mContext.getContentResolver().delete(AlarmEntry.CONTENT_URI,
                AlarmEntry._ID + " = " + alarmRowId, null);

        // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                AlarmEntry.buildAlarmUri(alarmRowId),
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );

        assertFalse(cursor.moveToFirst());
    }
}
