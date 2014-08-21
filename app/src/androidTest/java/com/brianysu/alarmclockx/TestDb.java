package com.brianysu.alarmclockx;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import com.brianysu.alarmclockx.data.AlarmContract.AlarmEntry;
import com.brianysu.alarmclockx.data.AlarmDbHelper;

import java.util.Map;
import java.util.Set;

public class TestDb extends AndroidTestCase {

    private static final String TAG = TestDb.class.getSimpleName();

    public void testCreateDb() throws Throwable {
        mContext.deleteDatabase(AlarmDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new AlarmDbHelper(
                this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());
        db.close();
    }

    public void testInsertReadDb() {

        // If there's an error in those massive SQL table creation Strings,
        // errors will be thrown here when you try to get a writable database.
        AlarmDbHelper dbHelper = new AlarmDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues alarmValues = createSampleAlarm();

        long alarmRowId = db.insert(AlarmEntry.TABLE_NAME, null, alarmValues);
        assertTrue(alarmRowId != -1);

        // A cursor is your primary interface to the query results.
        Cursor alarmCursor = db.query(
                AlarmEntry.TABLE_NAME,  // Table to Query
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null  // sort order
        );

        validateCursor(alarmCursor, alarmValues);

        dbHelper.close();
    }

    public void testInsertReadDb2() {
        AlarmDbHelper dbHelper = new AlarmDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Fantastic.  Now that we have a location, add some alarm!
        ContentValues alarmValues2 = createSampleAlarm2();

        long alarmRowId2 = db.insert(AlarmEntry.TABLE_NAME, null, alarmValues2);
        assertTrue(alarmRowId2 != -1);

        // A cursor is your primary interface to the query results.
        Cursor alarmCursor2 = db.query(
                AlarmEntry.TABLE_NAME,  // Table to Query
                null, // leaving "columns" null just returns all the columns.
                AlarmEntry._ID + " = ?", // cols for "where" clause
                new String[] {alarmRowId2 + ""}, // values for "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null  // sort order
        );
        validateCursor(alarmCursor2, alarmValues2);

        dbHelper.close();
    }

    static ContentValues createSampleAlarm() {
        ContentValues testValues = new ContentValues();
        testValues.put(AlarmEntry.COLUMN_NAME, "");
        testValues.put(AlarmEntry.COLUMN_HOUR, 8);
        testValues.put(AlarmEntry.COLUMN_MINUTE, 0);
        testValues.put(AlarmEntry.COLUMN_REPEATED_DAYS, Integer.parseInt("0", 2));
        testValues.put(AlarmEntry.COLUMN_TONE, "");
        testValues.put(AlarmEntry.COLUMN_ENABLED, 1); // true == 1

        return testValues;
    }

    static ContentValues createSampleAlarm2() {
        ContentValues testValues = new ContentValues();
        testValues.put(AlarmEntry.COLUMN_NAME, "Poo");
        testValues.put(AlarmEntry.COLUMN_HOUR, 4);
        testValues.put(AlarmEntry.COLUMN_MINUTE, 45);
        testValues.put(AlarmEntry.COLUMN_REPEATED_DAYS, Integer.parseInt("0001100", 2));
        testValues.put(AlarmEntry.COLUMN_TONE, "Waka Waka");
        testValues.put(AlarmEntry.COLUMN_ENABLED, 0); // true == 1

        return testValues;
    }

    
    static void validateCursor(Cursor valueCursor, ContentValues expectedValues) {

        assertTrue(valueCursor.moveToFirst());

        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse(idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals(expectedValue, valueCursor.getString(idx));
        }
        valueCursor.close();
    }
}


