package com.brianysu.alarmclockx;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;

import com.brianysu.alarmclockx.data.AlarmContract;
import com.brianysu.alarmclockx.data.AlarmContract.AlarmEntry;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AlarmUtility {

    public static final String TAG = AlarmEntry.class.getSimpleName();

    public static final String ALARM_ID = "alarm_id";

    public static int deleteAlarmById(Context c, int id) {
        int result = c.getContentResolver().delete(
                AlarmEntry.CONTENT_URI,
                AlarmEntry._ID + " = '" + id + "'",
                null
        );
        Log.d(TAG, "Deleted Alarm Id: " + id);
        return result;
    }

    /**
     * Get all alarms in the form of a List<AlarmModel>
     */
    public static List<AlarmModel> getAlarms(Context c) {
        Cursor cursor = c.getContentResolver().query(
                AlarmContract.AlarmEntry.CONTENT_URI,
                null,
                null,
                null,
                null);

        List<AlarmModel> alarms = new ArrayList<AlarmModel>();
        while (cursor.moveToNext()) {
            int idColumn = cursor.getColumnIndex(AlarmEntry._ID);
            int nameColumn = cursor.getColumnIndex(AlarmEntry.COLUMN_NAME);
            int hourColumn = cursor.getColumnIndex(AlarmEntry.COLUMN_HOUR);
            int minColumn = cursor.getColumnIndex(AlarmEntry.COLUMN_MINUTE);
            int toneColumn = cursor.getColumnIndex(AlarmEntry.COLUMN_TONE);
            int repeatedColumn = cursor.getColumnIndex(AlarmEntry.COLUMN_REPEATED_DAYS);
            int enabledColumn = cursor.getColumnIndex(AlarmEntry.COLUMN_ENABLED);

            int id = cursor.getInt(idColumn);
            String name = cursor.getString(nameColumn);
            int hour = cursor.getInt(hourColumn);
            int min = cursor.getInt(minColumn);
            String tone = cursor.getString(toneColumn);
            int repeated = cursor.getInt(repeatedColumn);
            int enabled = cursor.getInt(enabledColumn);

            AlarmModel alarm = new AlarmModel(id, name, hour, min, tone, repeated, enabled);
            alarms.add(alarm);
        }
        return alarms;
    }

    @SuppressWarnings("NewApi")
    private static void setAlarm(Context context, Calendar calendar, PendingIntent pIntent) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pIntent);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pIntent);
        }
    }

    public static void setAlarms(Context context) {
        cancelAlarms(context);
        List<AlarmModel> alarms = AlarmUtility.getAlarms(context);
        for (AlarmModel alarm : alarms) {
            if (alarm.isEnabled()) {
                PendingIntent pIntent = createPendingIntent(context, alarm);
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, alarm.getHour());
                calendar.set(Calendar.MINUTE, alarm.getMin());
                calendar.set(Calendar.SECOND, 0);

                final int nowDay = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
                final int nowHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
                final int nowMinute = Calendar.getInstance().get(Calendar.MINUTE);
                boolean alarmSet = false;

                //First check if it's later in the week
                for (int dayOfWeek = Calendar.SUNDAY; dayOfWeek <= Calendar.SATURDAY; dayOfWeek++) {
                    if (alarm.isRepeated(dayOfWeek) &&
                            dayOfWeek >= nowDay &&
                            !(dayOfWeek == nowDay && alarm.getHour() < nowHour) &&
                            !(dayOfWeek == nowDay && alarm.getHour()
                                    == nowHour && alarm.getMin() <= nowMinute)) {
                        calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek);
                        setAlarm(context, calendar, pIntent);
                        alarmSet = true;
                        break;
                    }
                }

                //Else check if it's earlier in the week
                if (!alarmSet) {
                    for (int dayOfWeek = Calendar.SUNDAY; dayOfWeek <= Calendar.SATURDAY; ++dayOfWeek) {
                        if (alarm.isRepeated(dayOfWeek) &&
                                dayOfWeek <= nowDay) {
                            calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek);
                            calendar.add(Calendar.WEEK_OF_YEAR, 1); // make it next week

                            setAlarm(context, calendar, pIntent);
                            alarmSet = true;
                            break;
                        }
                    }
                }

                // the alarm does not repeat. so, set it at the next earliest time
                if (!alarmSet) {
                    if (calendar.compareTo(Calendar.getInstance()) > 0) {
                        setAlarm(context, calendar, pIntent);
                    } else {
                        if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
                            calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
                            calendar.set(Calendar.WEEK_OF_YEAR, 1);
                        } else {
                            calendar.set(Calendar.DAY_OF_WEEK,
                                    calendar.get(Calendar.DAY_OF_WEEK) + 1);
                            setAlarm(context, calendar, pIntent);
                        }
                    }
                    alarmSet = true;
                }
            }
        }
    }

    public static void cancelAlarms(Context context) {
        List<AlarmModel> alarms = AlarmUtility.getAlarms(context);
        Log.d(TAG, "Number of alarms: " + alarms.size());
        if (alarms != null) {
            for (AlarmModel alarm : alarms) {
                if (alarm.isEnabled()) {
                    PendingIntent pIntent = createPendingIntent(context, alarm);
                    AlarmManager alarmManager =
                            (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                    alarmManager.cancel(pIntent);
                }
            }
        }
    }

    public static PendingIntent createPendingIntent(Context context, AlarmModel model) {
        Intent i = new Intent(context, AlarmService.class);
        i.putExtra(AlarmEntry._ID, model.getId());
        i.putExtra(AlarmEntry.COLUMN_NAME, model.getName());
        i.putExtra(AlarmEntry.COLUMN_HOUR, model.getHour());
        i.putExtra(AlarmEntry.COLUMN_MINUTE, model.getMin());
        i.putExtra(AlarmEntry.COLUMN_TONE, model.getRingtone());
        i.putExtra(AlarmEntry.COLUMN_REPEATED_DAYS, model.getRepeatedDays());
        i.putExtra(AlarmEntry.COLUMN_ENABLED, model.getEnabled());
        return PendingIntent.getService(context, (int) model.getId(), i,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

}
