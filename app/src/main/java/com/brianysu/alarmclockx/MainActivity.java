package com.brianysu.alarmclockx;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.brianysu.alarmclockx.alarm.AlarmUtility;
import com.brianysu.alarmclockx.data.AlarmContract;


public class MainActivity extends Activity implements ActionBar.TabListener {


    private static final String TAG = MainActivity.class.getSimpleName();
    static final int CREATE_NEW_ALARM_REQUEST = 0;

    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up the action bar.
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(this, getFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setIcon(mSectionsPagerAdapter.getIconTitle(i))
                            .setTabListener(this));
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        if (requestCode == CREATE_NEW_ALARM_REQUEST) {
            if (resultCode == RESULT_OK) {
                long id = data.getLongExtra(AlarmUtility.ALARM_ID, -1);

                if (id == -1) {
                    Log.e(TAG, "No new alarm has been created");
                    return;
                }

                Cursor cursor = getContentResolver().query(
                        AlarmContract.AlarmEntry.CONTENT_URI,
                        null,
                        AlarmContract.AlarmEntry._ID + " = ?",
                        new String[]{id + ""},
                        null);

                if (cursor.moveToFirst()) {
                    int hourIndex = cursor.getColumnIndex(AlarmContract.AlarmEntry.COLUMN_HOUR);
                    int minIndex = cursor.getColumnIndex(AlarmContract.AlarmEntry.COLUMN_MINUTE);
                    String _hour = cursor.getString(hourIndex);
                    String _min = cursor.getString(minIndex);
                    if (_min.length() == 1) {
                        _min = "0" + _min;
                    }
                    String time = String.format("%s:%s", _hour, _min);
                    Toast.makeText(this, "New alarm created at " + time, Toast.LENGTH_SHORT).show();
                } else {
                    Log.d(TAG, "Alarm creation failed");
                }
            }
        }

    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }
}
