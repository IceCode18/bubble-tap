package com.example.learn.config;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

import com.example.learn.R;


/**
 * Created by JHT.
 */

public class GameSettings extends PreferenceActivity {

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);
        addPreferencesFromResource(R.xml.prefs);
    }

    //this is a "facade" method to hide the nastiness
    //of Android's preferences API.

    /**
     * Settings for background music.
     * @param c - Context
     * @return
     */
    public static boolean getMusicOption(Context c) {
        return PreferenceManager.getDefaultSharedPreferences(c)
                .getBoolean("music_option", true);
    }

    /**
     * Returns boolean value which indicates if sound effects are enabled or not.
     * @param c - Context
     * @return
     */
    public static boolean getSoundEffectOption(Context c) {
        return PreferenceManager.getDefaultSharedPreferences(c)
                .getBoolean("soundFX_option", true);
    }

    /**
     * Returns int value for sphere move speed.
     * @param c
     * @return
     */
    public static int getMoveSpeed(Context c) {
        String speed = PreferenceManager.getDefaultSharedPreferences(c)
                .getString("move_speed", "6");
        return Integer.parseInt(speed);
    }

    /**
     * Returns String value of preferred background music.
     * @param c
     * @return
     */
    public static String getBgMusic(Context c) {
        String bgmusic = PreferenceManager.getDefaultSharedPreferences(c)
                .getString("background_music", "First Steps");
        return bgmusic;
    }
    public static String getMode(Context c) {
        String mode = PreferenceManager.getDefaultSharedPreferences(c)
                .getString("game_mode", c.getString(R.string.mode_counting));
        return mode;
    }
}
