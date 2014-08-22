package com.brianysu.alarmclockx;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.brianysu.alarmclockx.other.ClockUpdater;

/**
 * Fragment that displays the time and the next alarm
 */
public class TimeFragment extends Fragment {

    private static final String TAG = TimeFragment.class.getSimpleName();

    static ClockUpdater sClock;

    private View mRootView;

    public static TimeFragment newInstance() {
        TimeFragment fragment = new TimeFragment();
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
        mRootView = inflater.inflate(R.layout.fragment_time, container, false);
        sClock = new ClockUpdater(mRootView);
        return mRootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (sClock == null) sClock = new ClockUpdater(mRootView);
        sClock.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        sClock.cancel();
    }

}
