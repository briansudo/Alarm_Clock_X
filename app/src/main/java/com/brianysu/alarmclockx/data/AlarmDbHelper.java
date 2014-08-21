package com.brianysu.alarmclockx.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.brianysu.alarmclockx.data.AlarmContract.AlarmEntry;

public class AlarmDbHelper extends SQLiteOpenHelper {

    // If the database schema is changed, this must be incremented
    private static final int DATABASE_VERSION = 1;

    public static final String DATABASE_NAME = "alarm.db";

    private static final String SQL_CREATE_ALARM =
            "CREATE TABLE " + AlarmEntry.TABLE_NAME + " (" +
                    AlarmEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    AlarmEntry.COLUMN_NAME + " TEXT," +
                    AlarmEntry.COLUMN_HOUR + " INTEGER," +
                    AlarmEntry.COLUMN_MINUTE + " INTEGER," +
                    AlarmEntry.COLUMN_REPEATED_DAYS + " INTEGER," +
                    AlarmEntry.COLUMN_TONE + " TEXT," +
                    AlarmEntry.COLUMN_ENABLED + " BOOLEAN" + " )";

    private static final String SQL_DELETE_ALARM =
            "DROP TABLE IF EXISTS " + AlarmEntry.TABLE_NAME;

    public AlarmDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ALARM);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i2) {
        db.execSQL(SQL_DELETE_ALARM);
        onCreate(db);
    }
}
