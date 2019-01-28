package com.example.coeus.coeus_writetobyte.managers;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.coeus.coeus_writetobyte.models.GmailUser;
import com.google.gson.Gson;

/**
 * Description: This class contains methods associated
 * with saving and storing files created and GmailUser object.
 *
 * Author: Ojas Bhatia
 *
 * Last updated: January 10, 2019
 */

public final class PreferenceManager {

    //declaring Strings for file name and GmailUser object
    private static final String FILE_NAME = "file";

    private static final String OBJECT_KEY = "object";
    private static final String DOCUMENTS_COUNT_KEY = "documentsCount";

   //mutator method saves GmailUser object
    public static void saveObject(Context context, Object object) {
        SharedPreferences  mPrefs = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(object);
        prefsEditor.putString(OBJECT_KEY, json);
        prefsEditor.apply();
    }

    //accessor method returns json key of GmailUser
    public static GmailUser loadObject(Context context) {
        Gson gson = new Gson();
        SharedPreferences sharedPreferences = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        String json  = sharedPreferences.getString(OBJECT_KEY, "");
        return gson.fromJson(json, GmailUser.class);
    }

    //mutator method saves the number of files created
    public static void saveFilesCount(Context context, int count) {
        SharedPreferences  mPrefs = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        prefsEditor.putInt(DOCUMENTS_COUNT_KEY, count);
        prefsEditor.apply();
    }

    //accessor method returns number of files created
    public static int getFilesCount(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        int documentsCount = sharedPreferences.getInt( DOCUMENTS_COUNT_KEY, 0);
        return documentsCount;
    }

}
