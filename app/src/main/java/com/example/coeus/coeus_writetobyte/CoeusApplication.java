package com.example.coeus.coeus_writetobyte;

import android.app.Application;

import com.example.coeus.coeus_writetobyte.activities.MainActivity;

import java.io.File;

import proccessingPackage.IO;

import io.realm.Realm;

/**
 * Created by victor on 1/11/19.
 */

public class CoeusApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
    }

}
