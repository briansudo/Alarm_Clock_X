package com.brianysu.alarmclockx.alarm;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;

/**
 * Utility methods for ringing and vibrating the alarm
 */
public class AlarmRingUtility {

    private static final String TAG = AlarmRingUtility.class.getSimpleName();

    /**
     * Start ringing the alarm using TONE and return the MediaPlayer instance
     */
    public static MediaPlayer startAlarmRing(Context c, String tone) {
        MediaPlayer mPlayer = new MediaPlayer();
        Uri toneUri;
        try {
            if (tone != null && !tone.equals("")) {
                toneUri = Uri.parse(tone);
            } else {
                toneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            }
        } catch (Exception e) {
            toneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        }
        try {
            if (toneUri != null) {
                mPlayer.setDataSource(c, toneUri);
                mPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
                mPlayer.setLooping(true);
                mPlayer.prepare();
                mPlayer.start();
            }
        } catch (Exception e) {
            // do nothing
        }
        return mPlayer;
    }
}
