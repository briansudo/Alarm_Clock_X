package com.brianysu.alarmclockx.alarm;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.brianysu.alarmclockx.R;
import com.brianysu.alarmclockx.data.AlarmContract.AlarmEntry;

/**
 * Displays a list of alarms. Loaders are used to automatically update the view when data
 * in the database changes.
 */
public class AlarmsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    // Loader ID
    private static final int ALARM_LOADER = 0;
    // REQUEST ID for creating a new alarm
    static final int CREATE_NEW_ALARM_REQUEST = 0;

    // Indices of the columns queried above, used to retrieve info in AlarmsAdapter
    public static final int COL_ALARM_ID = 0;
    public static final int COL_ALARM_NAME = 1;
    public static final int COL_ALARM_HOUR = 2;
    public static final int COL_ALARM_MIN = 3;
    public static final int COL_ALARM_REPEATED = 4;
    public static final int COL_ALARM_TONE = 5;
    public static final int COL_ALARM_ENABLED = 6;

    private View mRootView;
    private AlarmsAdapter mAdapter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(ALARM_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(
                getActivity(),
                AlarmEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        mAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mAdapter.swapCursor(null);
    }

    public static Fragment newInstance() {
        AlarmsFragment fragment = new AlarmsFragment();
        return fragment;
    }

    public AlarmsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.fragment_alarms, container, false);
        ListView mListView = (ListView) mRootView.findViewById(R.id.listview_alarm);
        mAdapter = new AlarmsAdapter(getActivity(), null, 0);
        mListView.setAdapter(mAdapter);
        return mRootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.alarmsfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_create_alarm) {
            Intent i = new Intent(getActivity(), CreateAlarmActivity.class);
            startActivityForResult(i, CREATE_NEW_ALARM_REQUEST);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
