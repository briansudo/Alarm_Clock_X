package com.brianysu.alarmclockx.alarm;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.support.v4.view.GestureDetectorCompat;
import android.text.format.Time;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.brianysu.alarmclockx.R;
import com.brianysu.alarmclockx.data.AlarmContract.AlarmEntry;
import com.brianysu.alarmclockx.other.ClockUpdater;
import com.brianysu.alarmclockx.other.Utility;

/**
 * Activity that gets launched when the alarm rings.
 */
public class AlarmScreen extends Activity implements
        GestureDetector.OnGestureListener {

    private static final String TAG = AlarmScreen.class.getSimpleName();

    private GestureDetectorCompat mDetector;
    private PowerManager.WakeLock mWakeLock;
    private MediaPlayer mPlayer;
    private static final int WAKELOCK_TIMEOUT = 60 * 1000;
    private View mRootView;
    private ClockUpdater sClock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_screen);

        // Get the root view to access the views within it
        mRootView = getWindow().getDecorView().findViewById(android.R.id.content);

        // Hide the action bar for this view
        ActionBar bar = getActionBar();
        if (bar != null) bar.hide();

        // Set the next alarm time to now
        setNextAlarmTime();

        //Ensure wakelock release if it hasn't been released within 60 seconds
        Runnable releaseWakelock = new Runnable() {

            @Override
            public void run() {
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

                if (mWakeLock != null && mWakeLock.isHeld()) {
                    mWakeLock.release();
                }
            }
        };
        new Handler().postDelayed(releaseWakelock, WAKELOCK_TIMEOUT);

        String tone = getIntent().getStringExtra(AlarmEntry.COLUMN_TONE);
        mPlayer = AlarmUtility.startAlarmRing(this, tone);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.alarm_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Set the window to keep screen on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

        // Acquire wakelock
        PowerManager pm = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
        if (mWakeLock == null) {
            mWakeLock = pm.newWakeLock((PowerManager.FULL_WAKE_LOCK | PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP), TAG);
        }

        if (!mWakeLock.isHeld()) {
            mWakeLock.acquire();
        }

        // Set the time of the clock to the current time
        if (sClock == null) sClock = new ClockUpdater(mRootView);
        sClock.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Wakelock is needed for the alarm to appear when the phone screen is blank
        if (mWakeLock != null && mWakeLock.isHeld()) {
            mWakeLock.release();
        }
        sClock.cancel();
    }

    /** Dismiss the alarm when the user swipes. */
    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
                            float distanceY) {
        // If the user has swiped up more than 30, then disable the alarm
        if (Math.abs(distanceY) >= 30) {
            Log.d(TAG, "onScroll: " + distanceY);
            mPlayer.stop();
            sClock.cancel();
            finish();
        }
        return true;
    }

    // Set the next alarm time to now
    private void setNextAlarmTime() {
        Time now = new Time();
        now.setToNow();
        TextView nextAlarmHour = (TextView) findViewById(R.id.time_next_alarm_hour_textview);
        TextView nextAlarmMin = (TextView) findViewById(R.id.time_next_alarm_min_textview);
        nextAlarmHour.setText(String.valueOf(now.hour));
        nextAlarmMin.setText(Utility.formatMin(now.minute));
    }







    /** Necessary Overrides for GestureListener **/
    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float v, float v2) {
        return false;
    }
}
