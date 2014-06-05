package com.unique.whizzdo.application;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by carlos on 6/5/14.
 */
public class SettingsHelper {
    private final static String PREFERENCES_NAME_SETTINGS = "settings";
    private final static String SETTINGS_FLASH_ON = "flash_on";


    public static boolean isSnapFlash(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME_SETTINGS, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(SETTINGS_FLASH_ON, false);
    }

    public static void setSnapFlash(boolean isFlash, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME_SETTINGS, Context.MODE_PRIVATE);
        sharedPreferences.edit().putBoolean(SETTINGS_FLASH_ON, isFlash).commit();
    }
}
