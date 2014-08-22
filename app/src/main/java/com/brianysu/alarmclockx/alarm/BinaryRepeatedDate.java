package com.brianysu.alarmclockx.alarm;

import com.brianysu.alarmclockx.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Binary representation of days repeated.
 * 1010000 = Sunday and Tuesday repeated
 * 0000001 = Saturday repeated
 * 0011010 = Tuesday, Wednesday, and Friday repeated
 **/
public class BinaryRepeatedDate {

    int[] daysOfWeekStrings = {
            R.string.sunday,
            R.string.monday,
            R.string.tuesday,
            R.string.wednesday,
            R.string.thursday,
            R.string.friday,
            R.string.saturday
    };

    /** Representation of days repeated. **/
    private int days;

    /** Create new instance with no days repeated. **/
    public BinaryRepeatedDate() {
        days = 0;
    }

    /** Create new instance with days repeated equal to DAYS. **/
    public BinaryRepeatedDate(int days) {
        this.days = days;
    }

    public void setDate(int day, boolean on) {
        if (on) {
            days |= (Integer.parseInt("1000000", 2) >>> (day - 1));
        } else {
            int all = Integer.parseInt("1111111", 2);
            all = (int) (all - Math.pow(2, 7 - day));
            days &= all;
        }
    }

    public void setSunday(boolean on) {
        if (on) days |= Integer.parseInt("1000000", 2);
        else days &= Integer.parseInt("0111111", 2);
    }

    public void setMonday(boolean on) {
        if (on) days |= Integer.parseInt("0100000", 2);
        else days &= Integer.parseInt("1011111", 2);
    }

    public void setTuesday(boolean on) {
        if (on) days |= Integer.parseInt("0010000", 2);
        else days &= Integer.parseInt("1101111", 2);
    }

    public void setWednesday(boolean on) {
        if (on) days |= Integer.parseInt("0001000", 2);
        else days &= Integer.parseInt("1110111", 2);
    }

    public void setThursday(boolean on) {
        if (on) days |= Integer.parseInt("0000100", 2);
        else days &= Integer.parseInt("1111011", 2);
    }

    public void setFriday(boolean on) {
        if (on) days |= Integer.parseInt("0000010", 2);
        else days &= Integer.parseInt("1111101", 2);
    }

    public void setSaturday(boolean on) {
        if (on) days |= Integer.parseInt("0000001", 2);
        else days &= Integer.parseInt("1111110", 2);
    }

    public boolean getSunday() {
        return (days & Integer.parseInt("1000000", 2)) > 0;
    }

    public boolean getMonday() {
        return (days & Integer.parseInt("0100000", 2)) > 0;
    }

    public boolean getTuesday() {
        return (days & Integer.parseInt("0010000", 2)) > 0;
    }

    public boolean getWednesday() {
        return (days & Integer.parseInt("0001000", 2)) > 0;
    }

    public boolean getThursday() {
        return (days & Integer.parseInt("0000100", 2)) > 0;
    }

    public boolean getFriday() {
        return (days & Integer.parseInt("0000010", 2)) > 0;
    }

    public boolean getSaturday() {
        return (days & Integer.parseInt("0000001", 2)) > 0;
    }

    public List<String> getDays() {
        List<String> daysRepeated = new ArrayList<String>();
        String[] daysOfWeek = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        int mask = Integer.parseInt("1000000", 2);
        int daysCopy = days;
        for (int i = 0; i < 7; i++) {
            if ((daysCopy & mask) > 0) {
                daysRepeated.add(daysOfWeek[i]);
            }
            mask = mask >>> 1;
        }
        return daysRepeated;
    }

    /** Returns the integer representation of the repeated days binary String. **/
    public int getDaysInt() {
        return days;
    }

    /** Returns true if dayOfWeek = Calendar.SUNDAY for example is repeated. **/
    public boolean isRepeated(int dayOfWeek) {
        return (days & (Integer.parseInt("1000000") >>> (dayOfWeek - 1))) > 0;
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        List<String> days = getDays();
        for (int i = 0; i < days.size(); i++) {
            b.append(days.get(i));
            if (i + 1 < days.size()) {
                b.append(", ");
            }
        }
        return b.toString();
    }

}
