package com.brianysu.alarmclockx.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Defines tables and columns for the alarm database
 */
public class AlarmContract {

    public static final String CONTENT_AUTHORITY = "com.brianysu.alarmclockx";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_ALARM = "alarm";

    public static final class AlarmEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_ALARM).build();

        public static final String TABLE_NAME = "alarm";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_HOUR = "hour";
        public static final String COLUMN_MINUTE = "minute";
        public static final String COLUMN_REPEATED_DAYS = "days";
        public static final String COLUMN_TONE = "tone";
        public static final String COLUMN_ENABLED = "isEnabled";

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_ALARM;

        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_ALARM;

        public static Uri buildAlarmUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
