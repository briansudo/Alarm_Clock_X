package com.brianysu.alarmclockx;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.support.v13.app.FragmentPagerAdapter;

import com.brianysu.alarmclockx.alarm.AlarmsFragment;

import java.util.Locale;

/**
 * A {@link android.support.v13.app.FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    private Context mContext;

    public SectionsPagerAdapter(Context c, FragmentManager fm) {
        super(fm);
        mContext = c;
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        if (TimeFragment.sClock != null) TimeFragment.sClock.cancel();
        switch (position) {
            case 0:
                return TimeFragment.newInstance();
            case 1:
                return AlarmsFragment.newInstance();
        }
        return PlaceholderFragment.newInstance(position + 1);
    }

    @Override
    public int getCount() {
        // Show 3 total pages.
        return 4;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        Locale l = Locale.getDefault();
        switch (position) {
            case 0:
                return mContext.getString(R.string.title_section1).toUpperCase(l);
            case 1:
                return mContext.getString(R.string.title_section2).toUpperCase(l);
            case 2:
                return mContext.getString(R.string.title_section3).toUpperCase(l);
        }
        return null;
    }

    public int getIconTitle(int position) {
        switch (position) {
            case 0:
                return R.drawable.ic_action_time;
            case 1:
                return R.drawable.ic_action_alarms;
            case 2:
                return R.drawable.ic_action_picture;
            case 3:
                return R.drawable.ic_action_settings;
        }
        return 0;
    }
}
