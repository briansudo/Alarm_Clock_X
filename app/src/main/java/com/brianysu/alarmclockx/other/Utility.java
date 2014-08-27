package com.brianysu.alarmclockx.other;

public class Utility {

    public static String formatTwoDigits(int num) {
        return String.format("%02d", num);
    }

    public static String formatHour(int hour) {
        return String.valueOf(hour);
    }

    public static String formatMin(int min) {
        return formatTwoDigits(min);
    }

    public static String formatTime(int hour, int min) {
        return String.format("%d:%02d", hour, min);
    }
}
