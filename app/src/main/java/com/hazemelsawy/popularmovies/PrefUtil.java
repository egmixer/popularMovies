package com.hazemelsawy.popularmovies;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefUtil {
    private final static String PREFERENCES_NAME = "com.hazemelsawy.popularmovies.PREFERENCES";
    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;
    private static PrefUtil instance;

    private PrefUtil(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public static PrefUtil with(Context context) {
        if (instance == null) {
            instance = new PrefUtil(context);
        }
        return instance;
    }


    public static void saveStringToPref(String string, String key) {
        editor.putString(key, string).apply();
    }

    public static String getStringFromPref(String key) {
        return sharedPreferences.getString(key, "");
    }

}