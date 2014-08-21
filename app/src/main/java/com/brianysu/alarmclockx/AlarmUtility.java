package com.brianysu.alarmclockx;


import android.content.Context;
import android.util.Log;

import com.brianysu.alarmclockx.data.AlarmContract.AlarmEntry;

public class AlarmUtility {

    public static final String TAG = AlarmEntry.class.getSimpleName();

    public static final String ALARM_ID = "alarm_id";

    public static int deleteAlarmById(Context c, int id) {
        int result = c.getContentResolver().delete(
                AlarmEntry.CONTENT_URI,
                AlarmEntry._ID + " = '" + id + "'",
                null
        );
        Log.d(TAG, "Deleted Alarm Id: " + id);
        return result;
    }

}
