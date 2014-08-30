package com.brianysu.alarmclockx.alarm;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;

import com.brianysu.alarmclockx.data.AlarmContract;
import com.brianysu.alarmclockx.data.AlarmContract.AlarmEntry;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Utility functions for accessing, creating, updating, and deleting alarms
 */
public class AlarmUtility {

    public static final String TAG = AlarmEntry.class.getSimpleName();

    public static final String ALARM_ID = "alarm_id";

    /**
     * ===== GETTING ALARM(S) FROM THE DATABASE AND CREATING ALARMMODEL OBJECTS ======
     */
    /**
     * Get all alarms in the form of a List<AlarmModel>
     */
    public static List<AlarmModel> getAlarms(Context context) {
        Cursor cursor = context.getContentResolver().query(
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

    /**
     * Get a specific alarm by id
     */
    public static AlarmModel getAlarm(Context context, int _id) {
        Cursor cursor = context.getContentResolver().query(
                AlarmContract.AlarmEntry.CONTENT_URI,
                null,
                AlarmEntry._ID + " = '" + _id + "'",
                null,
                null);

        if (cursor.moveToNext()) {
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
            Log.d(TAG, "Got alarm id: " + id);
            return alarm;
        } else {
            return null;
        }
    }

    /**
     * ===================== SETTING ALARMS IN THE ALARM MANAGER ==================
     */

    /**
     * Create an alarm in the alarm manager using the given time in CALENDAR with the given
     * PINTENT
     */
    @SuppressWarnings("NewApi")
    private static void setAlarm(Context context, Calendar calendar, PendingIntent pIntent) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pIntent);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pIntent);
        }
    }

    /**
     * Create alarms for every alarm entry in the database
     */
    public static void setAlarms(Context context) {
        List<AlarmModel> alarms = AlarmUtility.getAlarms(context);
        Log.d(TAG, context.toString() + " is the context for set alarms");
        for (AlarmModel alarm : alarms) {
            if (alarm.isEnabled()) {
                createAlarmInAM(context, alarm);
            }
        }
    }

    /**
     * Register an alarm in alarm manager at the time corresponding to ALARM's time
     */
    public static void createAlarmInAM(Context context, AlarmModel alarm) {
        PendingIntent pIntent = createPendingIntent(context, alarm);
        // calendar is set to the alarm's hour and minute
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, alarm.getHour());
        calendar.set(Calendar.MINUTE, alarm.getMin());
        calendar.set(Calendar.SECOND, 0);

        // these values are the current time
        final int nowDay = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        final int nowHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        final int nowMinute = Calendar.getInstance().get(Calendar.MINUTE);

        // if the alarm is set, then we're done with this alarm
        boolean alarmSet = false;

        // First check if it's later in the week
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

    /**
     * Create an alarm in the alarm manager by querying alarm info
     * from the content provider using ID
     */
    public static void createAlarm(Context context, int id) {
        createAlarmInAM(context, getAlarm(context, id));
    }

    /**
     * ======= CANCELING, RESETTING, AND DELETING ALARMS ==========
     */

    /**
     * Cancel all alarms.
     */
    public static void cancelAlarms(Context context) {
        Log.d(TAG, context.toString() + " is the context for cancel alarms");
        List<AlarmModel> alarms = AlarmUtility.getAlarms(context);
        int i = 0;
        for (AlarmModel alarm : alarms) {
                PendingIntent pIntent = createPendingIntent(context, alarm);
                AlarmManager alarmManager =
                        (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                alarmManager.cancel(pIntent);
            i++;
        }
        Log.d(TAG, "Deleted " + i + " alarms");
    }

    /**
     * Reset all alarms, canceling all and re-enabling alarms that are enabled.
     */
    public static void resetAllAlarms(Context context) {
        cancelAlarms(context);
        setAlarms(context);
    }

    /**
     * Delete an alarm from Alarm Manager and database by its id
     */
    public static void deleteAlarm(Context context, int id) {
        deleteAlarmFromAM(context, id);
        deleteAlarmFromDb(context, id);
    }

    /**
     * Delete an alarm from Alarm Manager
     */
    public static void deleteAlarmFromAM(Context context, int id) {
        AlarmModel alarm = getAlarm(context, id);
        PendingIntent pIntent = createPendingIntent(context, alarm);
        AlarmManager alarmManager =
                (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pIntent);
    }

    /**
     * Delete an alarm from the db using the alarm's id
     */
    public static int deleteAlarmFromDb(Context c, int id) {
        int result = c.getContentResolver().delete(
                AlarmEntry.CONTENT_URI,
                AlarmEntry._ID + " = '" + id + "'",
                null
        );
        Log.d(TAG, "Deleted Alarm Id: " + id);
        return result;
    }

    /**
     * ========= UPDATING THE ENABLED STATE OF THE ALARM =========
     */

    /**
     * Update the enabled state of the alarm
     */
    public static int updateEnabled(Context context, int id, boolean enabled) {
        int result = updateEnabledInDb(context, id, enabled);
        if (enabled) {
            enableAlarmInAM(context, id);
        } else {
            disableAlarmInAM(context, id);
        }
        return result;
    }

    /**
     * Update the enabled state of the alarm, depending on ENABLED and alarm id.
     * Returns the id of the row modified.
     */
    public static int updateEnabledInDb(Context context, int id, boolean enabled) {
        ContentValues values = new ContentValues();
        values.put(AlarmEntry._ID, id);
        values.put(AlarmEntry.COLUMN_ENABLED, enabled);
        int result = context.getContentResolver().update(
                AlarmContract.AlarmEntry.CONTENT_URI,
                values,
                AlarmEntry._ID + " = '" + id + "'",
                null
        );
        return result;
    }

    /**
     * Enable the alarm in the alarm manager
     */
    public static void disableAlarmInAM(Context context, int id) {
        deleteAlarmFromAM(context, id);
    }

    /**
     * Enable the alarm in the alarm manager
     */
    public static void enableAlarmInAM(Context context, int id) {
        createAlarmInAM(context, getAlarm(context, id));
    }


    /**
     * ====== CREATE A PENDING INTENT TO ACCESS/CREATE ALARMS IN THE ALARM MANAGER ========
     */

    /**
     * Create a pending intent for the alarm manager.
     */
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
