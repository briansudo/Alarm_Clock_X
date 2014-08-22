package com.brianysu.alarmclockx.alarm;

import android.media.RingtoneManager;

/**
 * Represents an alarm
 */
public class AlarmModel {

    int id;
    String name;
    int hour, min;
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
}
