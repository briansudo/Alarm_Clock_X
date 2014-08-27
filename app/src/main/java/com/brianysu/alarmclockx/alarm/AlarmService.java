package com.brianysu.alarmclockx.alarm;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.brianysu.alarmclockx.data.AlarmContract;

public class AlarmService extends Service {

    private static final String TAG = AlarmService.class.getSimpleName();

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * Start the AlarmScreen activity from an explicit intent
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        AlarmUtility.setAlarms(this);
        Log.d(TAG, "ALARM IS SOUNDING");
        Intent alarmIntent = new Intent(getBaseContext(), AlarmScreen.class);
        String tone = alarmIntent.getStringExtra(AlarmContract.AlarmEntry.COLUMN_TONE);
        alarmIntent.putExtra(AlarmContract.AlarmEntry.COLUMN_TONE, tone);
        alarmIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (intent != null) alarmIntent.putExtras(intent);
        getApplication().startActivity(alarmIntent);
        return super.onStartCommand(intent, flags, startId);
    }
}