package com.brianysu.alarmclockx.other;

import android.os.CountDownTimer;
import android.text.format.Time;
import android.view.View;
import android.widget.TextView;

import com.brianysu.alarmclockx.R;

/**
 * Update a time based on the current hour, min, second.
 */
public class ClockUpdater extends CountDownTimer {

    private static final int LONG_LONG_TIME = 1000 * 60 * 24;

    TextView mHourTextView, mMinTextView, mSecondsTextView;

    public ClockUpdater(View rootView) {
        super(LONG_LONG_TIME, 1000);
        mHourTextView = (TextView) rootView.findViewById(R.id.time_hour_textview);
        mMinTextView = (TextView) rootView.findViewById(R.id.time_min_textview);
        mSecondsTextView = (TextView) rootView.findViewById(R.id.time_seconds_textview);
    }

    /**
     * Every second, update the time.
     */
    @Override
    public void onTick(long l) {
        Time t = new Time();
        t.setToNow();
        mHourTextView.setText(String.valueOf(t.hour));
        mMinTextView.setText(Utility.formatTwoDigits(t.minute));
        mSecondsTextView.setText(Utility.formatTwoDigits(t.second));
    }

    @Override
    public void onFinish() {
    }
}
