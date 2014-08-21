package com.brianysu.alarmclockx;

import android.app.Fragment;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Fragment that displays the time and the next alarm
 */
public class TimeFragment extends Fragment {

    private static final String TAG = TimeFragment.class.getSimpleName();

    static ClockUpdater sClock;

    private TextView mHourTextView, mMinTextView, mSecondsTextView;

    public static TimeFragment newInstance() {
        TimeFragment fragment = new TimeFragment();
        sClock = fragment.new ClockUpdater();
        return fragment;
    }
    public TimeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mRootView = inflater.inflate(R.layout.fragment_time, container, false);
        mHourTextView = (TextView) mRootView.findViewById(R.id.time_hour_textview);
        mMinTextView = (TextView) mRootView.findViewById(R.id.time_min_textview);
        mSecondsTextView = (TextView) mRootView.findViewById(R.id.time_seconds_textview);
        return mRootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (sClock == null) sClock = new ClockUpdater();
        sClock.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        sClock.cancel();
    }

    class ClockUpdater extends CountDownTimer {

        private static final int LONG_LONG_TIME = 1000 * 60 * 24;

        public ClockUpdater() {
            super(LONG_LONG_TIME, 1000);
        }

        @Override
        public void onTick(long l) {

            Time t = new Time();
            t.setToNow();
            Log.v(TAG, t.hour + ":" + t.minute + ":" + t.second);
            mHourTextView.setText(String.valueOf(t.hour));
            mMinTextView.setText(Utility.formatTwoDigits(t.minute));
            mSecondsTextView.setText(Utility.formatTwoDigits(t.second));
        }

        @Override
        public void onFinish() {
        }
    }
}
