package com.brianysu.alarmclockx;

import android.test.AndroidTestCase;
import android.util.Log;

import com.brianysu.alarmclockx.alarm.BinaryRepeatedDate;

import java.util.Calendar;

/**
 * Test BinaryRepeatedDate
 */
public class TestBinaryRepeatedDate extends AndroidTestCase {

    private static final String TAG = TestBinaryRepeatedDate.class.getSimpleName();

    public void testOne() {
        BinaryRepeatedDate b = new BinaryRepeatedDate();
        b.setTuesday(true);
        Log.d(TAG, "Day is currently " + Integer.toBinaryString(b.getDaysInt()));
        b.setSunday(true);
        Log.d(TAG, "Day is currently " + Integer.toBinaryString(b.getDaysInt()));
        b.setSaturday(true);
        Log.d(TAG, "Day is currently " + Integer.toBinaryString(b.getDaysInt()));
        assertEquals(false, b.isRepeated(Calendar.MONDAY));
        assertEquals(true, b.isRepeated(Calendar.TUESDAY));
        assertEquals(false, b.isRepeated(Calendar.WEDNESDAY));
        assertEquals(false, b.isRepeated(Calendar.THURSDAY));
        assertEquals(false, b.isRepeated(Calendar.FRIDAY));
        assertEquals(true, b.isRepeated(Calendar.SATURDAY));
        assertEquals(true, b.isRepeated(Calendar.SUNDAY));
    }
}
