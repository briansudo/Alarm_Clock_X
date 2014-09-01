package com.brianysu.alarmclockx.alarm;

import android.media.RingtoneManager;

import java.util.Calendar;

/**
 * Represents an alarm
 */
public class AlarmModel implements Comparable {

    // Refers to AlarmEntry ID in database
    int id;
    String name;
    int hour, min;
    // Uri of the ringtone chosen for the alarm in String form
    String ringtone;
    int repeatedDays;
    int enabled;

    public AlarmModel(int id, String name, int hour, int min, String ringtone, int repeatedDays,
                      int enabled) {
        this.id = id;
        this.name = name;
        this.hour = hour;
        this.min = min;
        this.ringtone = ringtone;
        this.repeatedDays = repeatedDays;
        this.enabled = enabled;
    }

    public AlarmModel() {
        ringtone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM).toString();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public String getRingtone() {
        return ringtone;
    }

    public void setRingtone(String ringtone) {
        this.ringtone = ringtone;
    }

    public int getRepeatedDays() {
        return repeatedDays;
    }

    public void setRepeatedDays(int repeatedDays) {
        this.repeatedDays = repeatedDays;
    }

    public int getEnabled() {
        return enabled;
    }

    public boolean isEnabled() {
        return enabled == 1;
    }

    public void setEnabled(int enabled) {
        this.enabled = enabled;
    }

    /** Returns true if dayOfWeek = Calendar.SUNDAY for example is repeated. **/
    public boolean isRepeated(int dayOfWeek) {
        BinaryRepeatedDate b = new BinaryRepeatedDate(repeatedDays);
        return b.isRepeated(dayOfWeek);
    }

    @Override
    public int compareTo(Object o) {
        // these values are the current time
        final int nowDay = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        final int nowHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        final int nowMinute = Calendar.getInstance().get(Calendar.MINUTE);

        Calendar thisTime = Calendar.getInstance();
        thisTime.set(Calendar.HOUR_OF_DAY, this.getHour());
        thisTime.set(Calendar.MINUTE, this.getMin());
        thisTime.set(Calendar.SECOND, 0);

        AlarmModel oAlarm = (AlarmModel) o;
        Calendar oTime = Calendar.getInstance();
        oTime.set(Calendar.HOUR_OF_DAY, oAlarm.getHour());
        oTime.set(Calendar.MINUTE, oAlarm.getMin());
        oTime.set(Calendar.SECOND, 0);

        return 0;
    }
}
