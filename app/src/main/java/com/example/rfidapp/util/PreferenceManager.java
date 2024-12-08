package com.example.rfidapp.util;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceManager {
    private static SharedPreferences.Editor editor;
    private static SharedPreferences preferences;

    private PreferenceManager() {
    }

    public static void init(Context context) {
        if (preferences == null) {
            preferences = context.getSharedPreferences(context.getPackageName(), 0);
        }
    }

    public static String getStringValue(String str) {
        return preferences.getString(str, "");
    }

    public static void setStringValue(String str, String str2) {
        SharedPreferences.Editor edit = preferences.edit();
        editor = edit;
        edit.putString(str, str2);
        editor.apply();
    }

    public static boolean getBoolValue(String str) {
        return preferences.getBoolean(str, false);
    }

    public static void setBoolValue(String str, boolean z) {
        SharedPreferences.Editor edit = preferences.edit();
        editor = edit;
        edit.putBoolean(str, z);
        editor.apply();
    }

    public static Integer getIntValue(String str) {
        return Integer.valueOf(preferences.getInt(str, 0));
    }

    public static void setIntValue(String str, Integer num) {
        SharedPreferences.Editor edit = preferences.edit();
        editor = edit;
        edit.putInt(str, num.intValue()).apply();
    }

    public static void logout() {
        SharedPreferences.Editor edit = preferences.edit();
        editor = edit;
        edit.clear();
        editor.apply();
        editor.commit();
    }
}
