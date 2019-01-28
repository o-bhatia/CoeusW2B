package com.example.coeus.coeus_writetobyte;

import android.app.Application;

import com.example.coeus.coeus_writetobyte.activities.MainActivity;

import java.io.File;

import proccessingPackage.IO;

import io.realm.Realm;

/**
 * Description: This is the Main class for Coeus, run
 * everytime an instance of the app is created. It contains
 * the onCreate method for Coeus and initializes the realm.io
 * database. Note: we did not comment the XML files since it
 * is redundant and unnecessary.
 *
 * Authors: Ojas Bhatia and Aumio Islam
 *
 * Date: January 10, 2019
 */

public class CoeusApplication extends Application {

    //the onCreate method runs everytime Coeus is started
    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
    }

}
