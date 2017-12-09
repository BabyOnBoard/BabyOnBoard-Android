package com.jonathanrufino.babyonboard.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.jonathanrufino.babyonboard.R;

public class SharedPreferencesHelper {

    public static void setSharedPreferenceString(Context context, String key, String value) {
        SharedPreferences settings = context.getSharedPreferences(
                context.getString(R.string.preferences_file), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String getSharedPreferenceString(Context context, String key, String defaultValue) {
        SharedPreferences settings = context.getSharedPreferences(
                context.getString(R.string.preferences_file), Context.MODE_PRIVATE);
        return settings.getString(key, defaultValue);
    }
}
