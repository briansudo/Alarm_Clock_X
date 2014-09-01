package com.brianysu.alarmclockx.alarm;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.brianysu.alarmclockx.R;
import com.brianysu.alarmclockx.data.AlarmContract;

import java.util.Calendar;

/**
 * Dialog to edit the alarm in the list view.
 */
public class EditAlarmDialogFragment extends DialogFragment {

    private static final String TAG = EditAlarmDialogFragment.class.getSimpleName();
    private static final String ALARM_ID_PARAM = "alarm_id_param";
    static final int RINGTONE_SELECT = 1;

    private AlarmModel mAlarm;
    private int alarmId = -1;
    private BinaryRepeatedDate mRepeated;

    View mRootView;

    public static EditAlarmDialogFragment newInstance(int _alarmId) {
        EditAlarmDialogFragment fragment = new EditAlarmDialogFragment();
        Bundle args = new Bundle();
        args.putInt(ALARM_ID_PARAM, _alarmId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            alarmId = getArguments().getInt(ALARM_ID_PARAM);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "On resume called");
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        updateVariables();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // update the view with the contents of the selected alarm to edit
        updateView();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(mRootView)
        // Add action buttons
            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    // update the alarm
                    updateAlarm();
                }
            })
            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    EditAlarmDialogFragment.this.getDialog().cancel();
                }
            });
        return builder.create();
    }

    /**
     * Update mAlarm and mRepeated with the most up to date contents.
     */
    private void updateVariables() {
        // get the alarm
        mAlarm = AlarmUtility.getAlarm(getActivity(), alarmId);

        // the binary representation of the days repeated
        mRepeated = new BinaryRepeatedDate(mAlarm.getRepeatedDays());
    }

    /**
     * Update the view with the current alarm information.
     */
    private void updateView() {
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        mRootView = inflater.inflate(R.layout.dialog_edit_alarm, null);

        configAlarmNameView();
        configTimePickerView();
        configRepeatedDaysTextViews();
        configRingtoneView();
    }

    /**
     * Update the alarm name with the current alarm name, if any
     */
    private void configAlarmNameView() {
        EditText alarmEditText = (EditText) mRootView.findViewById(R.id.alarm_name_edittext);
        if (!mAlarm.getName().equals("")) {
            alarmEditText.setText(mAlarm.getName());
        }
    }

    /**
     * Update the time picker with the current alarm time
     */
    private void configTimePickerView() {
        TimePicker timePicker = (TimePicker) mRootView.findViewById(R.id.timePicker);
        timePicker.setCurrentHour(mAlarm.getHour());
        timePicker.setCurrentMinute(mAlarm.getMin());
    }

    /**
     * View that allows user to pick which dates the alarm will repeat on.
     */
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
            final int day = i;

            // set the day selected views based on the current alarm contents
            initDaySelectedView(t, day);

            // when a day is pressed, change the state of the repeated day
            t.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setDaySelectedView(t, day);
                }
            });
        }
    }

    /**
     * Init the repeated days of the textview
     */
    private void initDaySelectedView(TextView dayTextView, int day) {
        if (!mRepeated.isRepeated(day)) {
            dayTextView.setTextColor(getResources().getColor(android.R.color.secondary_text_dark));
        } else {
            Log.d(TAG, "Day #" + day + " is repeating.");
            dayTextView.setTextColor(getResources().getColor(R.color.day_selected));
        }
    }
    /**
     * Set the state of the day repeated textview.
     */
    private void setDaySelectedView(TextView dayTextView, int day) {
        if (mRepeated.isRepeated(day)) {
            mRepeated.setDate(day, false);
            dayTextView.setTextColor(getResources().getColor(android.R.color.secondary_text_dark));
            Log.d(TAG, "Day #" + day + " turned off.");
        } else {
            mRepeated.setDate(day, true);
            dayTextView.setTextColor(getResources().getColor(R.color.day_selected));
            Log.d(TAG, "Day #" + day + " turned on.");
        }
    }

    /**
     * When the ringtone option is clicked, create an implicit intent for the user to pick a tone.
     */
    private void configRingtoneView() {
        final LinearLayout ringToneContainer =
                (LinearLayout) mRootView.findViewById(R.id.ringtone_selector_view);

        // set the ringtone name view to the current ringtone name
        Uri currentToneUri = Uri.parse(mAlarm.getRingtone());
        TextView ringtoneName =
                (TextView) mRootView.findViewById(R.id.ringtone_name_textview);
        ringtoneName.setText(
                RingtoneManager.getRingtone(getActivity(), currentToneUri)
                        .getTitle(getActivity()));

        // select a new ringtone
        ringToneContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
                startActivityForResult(i, RINGTONE_SELECT);
            }
        });
    }

    /**
     * Get the ringtone selected for the alarm.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_OK) {
            switch (requestCode) {
                case RINGTONE_SELECT: {
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

    /**
     * Update the alarm in the database
     */
    private void updateAlarm() {
        EditText alarmEditText = (EditText) mRootView.findViewById(R.id.alarm_name_edittext);
        String alarmName = alarmEditText.getText().toString();

        TimePicker timePicker = (TimePicker) mRootView.findViewById(R.id.timePicker);
        int hour = timePicker.getCurrentHour();
        int min = timePicker.getCurrentMinute();

        ContentValues alarmValues = new ContentValues();
        alarmValues.put(AlarmContract.AlarmEntry.COLUMN_NAME, alarmName);
        alarmValues.put(AlarmContract.AlarmEntry.COLUMN_HOUR, hour);
        alarmValues.put(AlarmContract.AlarmEntry.COLUMN_MINUTE, min);
        alarmValues.put(AlarmContract.AlarmEntry.COLUMN_REPEATED_DAYS, mRepeated.getDaysInt());
        alarmValues.put(AlarmContract.AlarmEntry.COLUMN_TONE, mAlarm.getRingtone());

        AlarmUtility.updateAlarmInDb(getActivity(), alarmValues, alarmId);
    }
}
