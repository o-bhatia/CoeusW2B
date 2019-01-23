package com.example.coeus.coeus_writetobyte.managers;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.coeus.coeus_writetobyte.models.GMailUser;
import com.google.gson.Gson;


public final class PreferenceManager {

    private static final String FILE_NAME = "file";

    private static final String OBJECT_KEY = "object";
    private static final String DOCUMENTS_COUNT_KEY = "documentsCount";

    public static void saveObject(Context context, Object object) {
        SharedPreferences  mPrefs = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(object);
        prefsEditor.putString(OBJECT_KEY, json);
        prefsEditor.apply();
    }

    public static GMailUser loadObject(Context context) {
        Gson gson = new Gson();
        SharedPreferences sharedPreferences = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        String json  = sharedPreferences.getString(OBJECT_KEY, "");
        return gson.fromJson(json, GMailUser.class);
    }

    public static void saveFilesCount(Context context, int count) {
        SharedPreferences  mPrefs = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        prefsEditor.putInt(DOCUMENTS_COUNT_KEY, count);
        prefsEditor.apply();
    }

    public static int getFilesCount(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        int documentsCount = sharedPreferences.getInt( DOCUMENTS_COUNT_KEY, 0);
        return documentsCount;
    }

}
