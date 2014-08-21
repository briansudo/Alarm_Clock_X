package com.brianysu.alarmclockx;

import android.app.Activity;
import android.app.Fragment;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.brianysu.alarmclockx.data.AlarmContract.AlarmEntry;

import java.util.Calendar;

/**
 * Fragment that creates a new alarm.
 */
public class CreateAlarmFragment extends Fragment {

    private static final String TAG = CreateAlarmFragment.class.getSimpleName();
    static final int RINGTONE_SELECT = 1;

    private View mRootView;
    private AlarmModel mAlarm = new AlarmModel();
    private BinaryRepeatedDate mRepeated = new BinaryRepeatedDate();

    public static Fragment newInstance() {
        CreateAlarmFragment fragment = new CreateAlarmFragment();
        return fragment;
    }

    public CreateAlarmFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_create_alarm, container, false);
        configCreateAlarmButton();
        configRingtoneView();
        configRepeatedDaysTextViews();
        return mRootView;
    }

    private void configCreateAlarmButton() {
        Button mCreateAlarmButton = (Button) mRootView.findViewById(R.id.create_alarm_button);
        mCreateAlarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNewAlarm();
            }
        });
    }

    private void configRepeatedDaysTextViews() {
        int[] daysOfWeekTextViews = {
                R.id.sunday_selector_textview,
                R.id.monday_selector_textview,
                R.id.tuesday_selector_textview,
                R.id.wednesday_selector_textview,
                R.id.thursday_selector_textview,
                R.id.friday_selector_textview,
                R.id.saturday_selector_textview
        };
        for (int i = Calendar.SUNDAY; i <= Calendar.SATURDAY; i++) {
            final TextView t = (TextView) mRootView.findViewById(daysOfWeekTextViews[i - 1]);
            final int y = i;
            t.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mRepeated.isRepeated(y)) {
                        mRepeated.setDate(y, false);
                        t.setTextColor(getResources().getColor(android.R.color.secondary_text_dark));
                    } else {
                        mRepeated.setDate(y, true);
                        t.setTextColor(getResources().getColor(R.color.day_selected));
                    }
                }
            });
        }
    }

    private void configRingtoneView() {
        final LinearLayout ringToneContainer =
                (LinearLayout) mRootView.findViewById(R.id.ringtone_selector_view);
        ringToneContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
                startActivityForResult(i, RINGTONE_SELECT);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_OK) {
            switch (requestCode) {
                case 1: {
                    Uri uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
                    mAlarm.setRingtone(uri.toString());
                    TextView ringtoneName =
                            (TextView) mRootView.findViewById(R.id.ringtone_name_textview);
                    ringtoneName.setText(
                            RingtoneManager.getRingtone(getActivity(), uri)
                                    .getTitle(getActivity()));
                    break;
                }
                default: {
                    break;
                }
            }
        }
    }

    private void createNewAlarm() {
        EditText alarmEditText = (EditText) mRootView.findViewById(R.id.alarm_name_edittext);
        String alarmName = alarmEditText.getText().toString();

        TimePicker timePicker = (TimePicker) mRootView.findViewById(R.id.timePicker);
        int hour = timePicker.getCurrentHour();
        int min = timePicker.getCurrentMinute();

        ContentValues alarmValues = new ContentValues();
        alarmValues.put(AlarmEntry.COLUMN_NAME, alarmName);
        alarmValues.put(AlarmEntry.COLUMN_HOUR, hour);
        alarmValues.put(AlarmEntry.COLUMN_MINUTE, min);
        alarmValues.put(AlarmEntry.COLUMN_REPEATED_DAYS, mRepeated.getDaysInt());
        alarmValues.put(AlarmEntry.COLUMN_TONE, mAlarm.getRingtone());
        alarmValues.put(AlarmEntry.COLUMN_ENABLED, 1);

        Uri alarmInsertUri = getActivity().getContentResolver()
                .insert(AlarmEntry.CONTENT_URI, alarmValues);

        long id = ContentUris.parseId(alarmInsertUri);

        AlarmUtility.setAlarms(getActivity());

        Intent returnIntent = new Intent();
        returnIntent.putExtra(AlarmUtility.ALARM_ID, id);
        getActivity().setResult(Activity.RESULT_OK, returnIntent);
        getActivity().finish();
    }


}
