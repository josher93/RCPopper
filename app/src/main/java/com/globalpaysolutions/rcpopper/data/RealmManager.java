package com.globalpaysolutions.rcpopper.data;

import android.content.Context;
import android.util.Log;

import com.globalpaysolutions.rcpopper.models.data.Popper;
import com.globalpaysolutions.rcpopper.models.data.Settings;

import io.realm.Realm;

/**
 * Created by Josué Chávez on 14/3/2018.
 */

public class RealmManager
{
    private static final String TAG = RealmManager.class.getSimpleName();

    private static RealmManager mSingleton;
    private static Context mContext;
    private Realm mRealm;

    private RealmManager(Context context)
    {
        RealmManager.mContext = context;
        mRealm = Realm.getDefaultInstance();
    }

    public static synchronized RealmManager getInstance(Context context)
    {
        if(mSingleton == null)
        {
            mSingleton = new RealmManager(context);
        }
        return mSingleton;
    }

    public void savePopper(final Popper savePopper)
    {
        mRealm.executeTransaction(new Realm.Transaction()
        {
            @Override
            public void execute(Realm realm)
            {
                Popper popper = mRealm.where(Popper.class)
                        .equalTo("facebookID", savePopper.getFacebookID())
                        .findFirst();

                if(popper == null)
                {
                    // Creates new Popper
                    popper = realm.createObject(Popper.class);
                    popper.setFacebookID(savePopper.getFacebookID());
                    popper.setFirstname(savePopper.getFirstname());
                    popper.setMiddlename(savePopper.getMiddlename());
                    popper.setLastname(savePopper.getLastname());
                    popper.setEmail(savePopper.getEmail());
                    popper.setFirebaseUid(savePopper.getFirebaseUid());
                }
                else
                {
                    // Updates popper
                    popper.setFacebookID(savePopper.getFacebookID());
                    popper.setFirstname(savePopper.getFirstname());
                    popper.setMiddlename(savePopper.getMiddlename());
                    popper.setLastname(savePopper.getLastname());
                    popper.setEmail(savePopper.getEmail());
                    popper.setFirebaseUid(savePopper.getFirebaseUid());
                }
            }
        });
    }

    public Popper getPopper(String facebookID)
    {
        Popper popper = new Popper();

        try
        {
            popper = mRealm.where(Popper.class)
                    .equalTo("facebookID", facebookID)
                    .findFirst();
        }
        catch (Exception ex)
        {
            Log.e(TAG, "Error returning saved Popper: " + ex.getMessage());
        }

        return popper;
    }

    public void deletePopper(final String facebookID)
    {
        try
        {
            mRealm.executeTransaction(new Realm.Transaction()
            {
                @Override
                public void execute(Realm realm)
                {
                    Popper popper = mRealm.where(Popper.class).equalTo("facebookID", facebookID).findFirst();
                    if(popper != null)
                        popper.deleteFromRealm();
                }
            });
        }
        catch (Exception ex)
        {
            Log.e(TAG, "Error deleting popper from Realm: " + ex.getMessage());
        }
    }

    public void updatePopperFirebaseUid(final String firebaseUid, String facebookID)
    {
        /*
        ::: Example::::
        try(Realm realmInstance = Realm.getDefaultInstance()) {
            realmInstance.executeTransaction((realm) -> realm.insertOrUpdate(dog));
        }*/

        try(Realm r = Realm.getDefaultInstance())
        {
            r.executeTransaction((realm) -> {
                Popper popper = realm.where(Popper.class).equalTo("facebookID", facebookID).findFirst();
                if(popper != null)
                {
                    popper.setFirebaseUid(firebaseUid);
                }
            });
        }
    }

    public void setAuthenticated(boolean authenticated)
    {
        try (Realm realmInstance = Realm.getDefaultInstance())
        {
            realmInstance.executeTransaction(realm -> {
                Settings settings = realm.where(Settings.class).findFirst();
                if(settings == null)
                {
                    settings = realm.createObject(Settings.class);
                    settings.setAuthenticated(authenticated);
                }
                else
                {
                    settings.setAuthenticated(authenticated);
                }
            });
        }
    }

    public boolean isAuthenticated()
    {
        boolean authenticated = false;

        try(Realm realmInstance = Realm.getDefaultInstance())
        {
            Settings settings = realmInstance.where(Settings.class).findFirst();
            if(settings != null)
                authenticated = settings.isAuthenticated();
        }

        return authenticated;
    }



}
