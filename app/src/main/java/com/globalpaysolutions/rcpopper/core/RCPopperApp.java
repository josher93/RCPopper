package com.globalpaysolutions.rcpopper.core;

import android.app.Application;

import com.google.firebase.FirebaseApp;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by Josué Chávez on 14/3/2018.
 */

public class RCPopperApp extends Application
{
    private static final String TAG = RCPopperApp.class.getSimpleName();
    private static RCPopperApp mSingleton;

    public static RCPopperApp getInstance()
    {
        return mSingleton;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        mSingleton = this;
        FirebaseApp.initializeApp(this);

        //Realm
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder().name("rcpopper.realm").build();
        Realm.setDefaultConfiguration(config);
    }

    @Override
    public void onLowMemory()
    {
        super.onLowMemory();
    }

    @Override
    public void onTerminate()
    {
        super.onTerminate();
    }
}
