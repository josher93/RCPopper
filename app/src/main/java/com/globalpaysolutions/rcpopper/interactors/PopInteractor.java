package com.globalpaysolutions.rcpopper.interactors;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.globalpaysolutions.rcpopper.interactors.interfaces.IPopInteractor;
import com.globalpaysolutions.rcpopper.models.ChestData;
import com.globalpaysolutions.rcpopper.presenters.PopPresenterImpl;
import com.globalpaysolutions.rcpopper.utils.GeofireSingleton;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Josué Chávez on 15/3/2018.
 */

public class PopInteractor implements IPopInteractor
{
    private static final String TAG = PopPresenterImpl.class.getSimpleName();

    private Context mContext;
    private PopListener mListener;

    //Firebase
    private DatabaseReference mRootReference = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference mGoldPoints = mRootReference.child("locationGoldYCR");
    private DatabaseReference mGoldPointsData = mRootReference.child("locationGoldYCRData");

    private DatabaseReference mSilverPoints = mRootReference.child("locationSilverYCR");
    private DatabaseReference mSilverPointsData = mRootReference.child("locationSilverYCRData");

    private DatabaseReference mBronzePoints = mRootReference.child("locationBronzeYCR");
    private DatabaseReference mBronzePointsData = mRootReference.child("locationBronzeYCRData");

    private DatabaseReference mWildcardPoints = mRootReference.child("locationWildcardYCR");
    private DatabaseReference mWildcardPointsData = mRootReference.child("locationWildcardYCRData");

    //GeoFire Queries
    private static GeoQuery mGoldPointsQuery;
    private static GeoQuery mSilverPointsQuery;
    private static GeoQuery mBronzePointsQuery;

    public PopInteractor(Context context, PopListener listener)
    {
        this.mContext = context;
        this.mListener = listener;
    }


    @Override
    public void initializeGeolocation()
    {
        try
        {
            GeofireSingleton.getInstance().initializeReferences(mGoldPoints, mSilverPoints,
                    mBronzePoints, mWildcardPoints);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public void goldPointsQuery(GeoLocation pLocation, double pRadius)
    {
        new ExecuteGoldPointsQuery(pLocation, pRadius).execute();
    }

    @Override
    public void goldPointsUpdateCriteria(GeoLocation pLocation, double pRadius)
    {
        try
        {
            mGoldPointsQuery.setLocation(pLocation, pRadius);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public void silverPointsQuery(GeoLocation pLocation, double pRadius)
    {
        new ExecuteSilverPointsQuery(pLocation, pRadius).execute();
    }

    @Override
    public void silverPointsUpdateCriteria(GeoLocation pLocation, double pRadius)
    {
        try
        {
            mSilverPointsQuery.setLocation(pLocation, pRadius);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public void bronzePointsQuery(GeoLocation pLocation, double pRadius)
    {
        new ExecuteBronzePointsQuery(pLocation, pRadius).execute();
    }

    @Override
    public void bronzePointsUpdateCriteria(GeoLocation pLocation, double pRadius)
    {

        try
        {
            mBronzePointsQuery.setLocation(pLocation, pRadius);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public void insertGoldChestData(GeoLocation geoLocation)
    {
        try
        {
            ChestData chest = new ChestData();
            chest.setBrand("Claro");

            mGoldPointsData.push().setValue(chest, new DatabaseReference.CompletionListener()
            {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference)
                {
                    if(databaseError == null)
                    {
                        String key = new StringBuilder(databaseReference.getKey()).insert(0, "G").toString();
                        mListener.onGoldKeyDataInserted(key, geoLocation);
                    }
                    else
                    {
                        Log.e(TAG, "Error inserting Gold chest data: " + databaseError.getDetails());
                        mListener.onWriteDataError();
                    }
                }
            });


        }
        catch (Exception ex)
        {
            Log.e(TAG, "Error inserting Gold chest data: " + ex.getMessage());
        }
    }


    @Override
    public void detachFirebaseListeners()
    {
        try
        {
            mGoldPointsQuery.removeGeoQueryEventListener(goldPointsListener);
            mSilverPointsQuery.removeGeoQueryEventListener(silverPointsListener);
            mBronzePointsQuery.removeGeoQueryEventListener(bronzePointsListener);
        }
        catch (Exception ex)
        {
            Log.e(TAG, "Could not detach GeoQuery Event Listeners");
        }
    }

    @Override
    public void deleteGoldChest(String firebaseKey, PopListener listener)
    {
        try
        {
            GeofireSingleton.getInstance().getGoldPointReference()
                    .removeLocation(firebaseKey, (key, error) ->
            {
                if(error == null)
                {
                    Log.i(TAG, "Location deleted succesfully for chest " + key);
                    listener.onGoldChestDeleteSuccess(key);
                }
                else
                {
                    Log.e(TAG, "Error trying to delete location for chest " + key);
                    listener.onChestDeleteError();
                }

            });
        }
        catch (Exception ex)
        {
            Log.e(TAG, "Error deleting from firebase: " + ex.getMessage());
        }
    }

    @Override
    public void deleteSilverChest(String firebaseKey, PopListener listener)
    {
        try
        {
            GeofireSingleton.getInstance().getSilverPointReference()
                    .removeLocation(firebaseKey, (key, error) ->
                    {
                        if(error == null)
                        {
                            Log.i(TAG, "Location deleted succesfully for chest " + key);
                            listener.onSilverChestDeleteSuccess(key);
                        }
                        else
                        {
                            Log.e(TAG, "Error trying to delete location for chest " + key);
                            listener.onChestDeleteError();
                        }

                    });
        }
        catch (Exception ex)
        {
            Log.e(TAG, "Error deleting from firebase: " + ex.getMessage());
        }
    }

    @Override
    public void deleteBronzeChest(String firebaseKey, PopListener listener)
    {
        try
        {
            GeofireSingleton.getInstance().getBronzePointReference()
                    .removeLocation(firebaseKey, (key, error) ->
                    {
                        if(error == null)
                        {
                            Log.i(TAG, "Location deleted succesfully for chest " + key);
                            listener.onBronzeChestDeleteSuccess(key);
                        }
                        else
                        {
                            Log.e(TAG, "Error trying to delete location for chest " + key);
                            listener.onChestDeleteError();
                        }

                    });
        }
        catch (Exception ex)
        {
            Log.e(TAG, "Error deleting from firebase: " + ex.getMessage());
        }
    }

    @Override
    public void insertGoldChest(String insertedKey, GeoLocation geoLocation, PopListener listener)
    {
        try
        {
            GeofireSingleton.getInstance().getGoldPointReference()
                    .setLocation(insertedKey, geoLocation, (key, error) ->
                    {
                        if(error == null)
                        {
                            listener.onGoldInserted(key,geoLocation);
                        }
                        else
                        {
                            Log.e(TAG, "Error trying to insert location for Current Player " + key);
                        }

                    });
        }
        catch (Exception ex)
        {
            Log.e(TAG, "Location for current player could not be inserted: " + ex.getMessage());
        }
    }

    @Override
    public void insertSilverChestData(GeoLocation geoLocation)
    {
        try
        {
            ChestData chest = new ChestData();
            chest.setBrand("Claro");

            mSilverPointsData.push()
                    .setValue(chest, (databaseError, databaseReference) ->
                    {
                        if(databaseError == null)
                        {
                            String key = new StringBuilder(databaseReference.getKey()).insert(0, "S").toString();
                            mListener.onSilverKeyDataInserted(key, geoLocation);
                        }
                        else
                        {
                            Log.e(TAG, "Error inserting Silver chest data: " + databaseError.getDetails());
                            mListener.onWriteDataError();
                        }

                    });
        }
        catch (Exception ex)
        {
            Log.e(TAG, "Error inserting Gold chest data: " + ex.getMessage());
            mListener.onWriteDataError();
        }
    }

    @Override
    public void insertSilverChest(String insertedKey, GeoLocation geoLocation, PopListener listener)
    {
        try
        {
            GeofireSingleton.getInstance().getSilverPointReference()
                    .setLocation(insertedKey, geoLocation, (key, error) ->
                    {
                        if(error == null)
                        {
                            listener.onSilverInserted(key, geoLocation);
                        }
                        else
                        {
                            Log.e(TAG, "Error trying to insert location for Current Player " + key);
                        }

                    });
        }
        catch (Exception ex)
        {
            Log.e(TAG, "Location for current player could not be inserted: " + ex.getMessage());
        }
    }

    @Override
    public void insertBronzeChestData(GeoLocation geoLocation)
    {
        try
        {
            ChestData chest = new ChestData();
            chest.setBrand("Claro");

            mBronzePointsData.push()
                    .setValue(chest, (databaseError, databaseReference) ->
            {
                if(databaseError == null)
                {
                    String key = new StringBuilder(databaseReference.getKey()).insert(0, "B").toString();
                    mListener.onBronzeKeyDataInserted(key, geoLocation);
                }
                else
                {
                    Log.e(TAG, "Error inserting Bronze chest data: " + databaseError.getDetails());
                }

            });

        }
        catch (Exception ex)
        {
            Log.e(TAG, "Error inserting Gold chest data: " + ex.getMessage());
            mListener.onWriteDataError();
        }
    }

    @Override
    public void insertBronzeChest(String insertedKey, GeoLocation geoLocation, PopListener listener)
    {
        try
        {
            GeofireSingleton.getInstance().getBronzePointReference()
                    .setLocation(insertedKey, geoLocation, (key, error) ->
                    {
                        if(error == null)
                        {
                            listener.onBronzeInserted(key, geoLocation);
                        }
                        else
                        {
                            Log.e(TAG, "Error trying to insert location for Current Player " + key);
                        }

                    });
        }
        catch (Exception ex)
        {
            Log.e(TAG, "Location for current player could not be inserted: " + ex.getMessage());
        }
    }

    /*
    *
    *
    *   GEOQUERY EVENTS LISTENER
    *
    *
    * */
    private GeoQueryEventListener goldPointsListener = new GeoQueryEventListener()
    {

        @Override
        public void onKeyEntered(final String key, GeoLocation location)
        {
            LatLng geoLocation = new LatLng(location.latitude, location.longitude);
            mListener.onGoldKeyEntered(key, geoLocation);
        }

        @Override
        public void onKeyExited(String key)
        {
            mListener.onGoldKeyExited(key);
        }

        @Override
        public void onKeyMoved(String key, GeoLocation location)
        {
            Log.i(TAG, "GoldPoint: Key moved fired.");
        }

        @Override
        public void onGeoQueryReady()
        {
            mListener.onGoldGeoQueryReady();
            Log.i(TAG, "GoldPoint: GeoQuery ready fired.");
        }

        @Override
        public void onGeoQueryError(DatabaseError error)
        {
            Log.e(TAG, "GoldPoint: GeoFire Database error fired.");
        }
    };

    private GeoQueryEventListener silverPointsListener = new GeoQueryEventListener()
    {

        @Override
        public void onKeyEntered(final String key, GeoLocation location)
        {
            LatLng geoLocation = new LatLng(location.latitude, location.longitude);
            mListener.onSilverKeyEntered(key, geoLocation);
        }

        @Override
        public void onKeyExited(String key)
        {
            mListener.onSilverKeyExited(key);
        }

        @Override
        public void onKeyMoved(String key, GeoLocation location)
        {
            Log.i(TAG, "SilverPoint: Key moved fired");
        }

        @Override
        public void onGeoQueryReady()
        {
            mListener.onSilverGeoQueryReady();
            Log.i(TAG, "SilverPoint: GeoQuery ready fired");
        }

        @Override
        public void onGeoQueryError(DatabaseError error)
        {
            Log.e(TAG, "SilverPoint: Firebase DatabaseError fired");
        }
    };

    private GeoQueryEventListener bronzePointsListener = new GeoQueryEventListener()
    {

        @Override
        public void onKeyEntered(final String key, GeoLocation location)
        {
            LatLng geoLocation = new LatLng(location.latitude, location.longitude);
            mListener.onBronzeKeyEntered(key, geoLocation);
        }

        @Override
        public void onKeyExited(String key)
        {
            mListener.onBronzeKeyExited(key);
        }

        @Override
        public void onKeyMoved(String key, GeoLocation location)
        {
            Log.i(TAG, "BronzePoint: Warning, bronze point key moved fired!");
        }

        @Override
        public void onGeoQueryReady()
        {
            mListener.onBronzeGeoQueryReady();
            Log.i(TAG, "BronzePoint: GeoQuery for BronzePoint ready");
        }

        @Override
        public void onGeoQueryError(DatabaseError error)
        {
            Log.e(TAG, "BronzePoint: DatabaseError for BronzePoint fired");
        }
    };



    /*
    *******************************************************
    *
    *
    *   ASYNC TASKS
    *
    *******************************************************
    */

    private class ExecuteGoldPointsQuery extends AsyncTask<Void, Void, Void>
    {
        GeoLocation geoLocation;
        double radius;

        ExecuteGoldPointsQuery(GeoLocation pLocation, double pRadius)
        {
            this.geoLocation = pLocation;
            this.radius = pRadius;
        }

        @Override
        protected Void doInBackground(Void... params)
        {
            try
            {
                mGoldPointsQuery = GeofireSingleton.getInstance().getGoldPointReference().queryAtLocation(geoLocation, radius);
                mGoldPointsQuery.addGeoQueryEventListener(goldPointsListener);
            }
            catch (IllegalArgumentException ex)
            {
                ex.printStackTrace();
            }
            return null;
        }
    }

    private class ExecuteSilverPointsQuery extends AsyncTask<Void, Void, Void>
    {
        GeoLocation geoLocation;
        double radius;

        ExecuteSilverPointsQuery(GeoLocation pLocation, double pRadius)
        {
            this.geoLocation = pLocation;
            this.radius = pRadius;
        }

        @Override
        protected Void doInBackground(Void... params)
        {
            try
            {
                mSilverPointsQuery = GeofireSingleton.getInstance().getSilverPointReference().queryAtLocation(geoLocation, radius);
                mSilverPointsQuery.addGeoQueryEventListener(silverPointsListener);
            }
            catch (IllegalArgumentException ex)
            {
                ex.printStackTrace();
            }

            return null;
        }
    }

    private class ExecuteBronzePointsQuery extends AsyncTask<Void, Void, Void>
    {
        GeoLocation geoLocation;
        double radius;

        ExecuteBronzePointsQuery(GeoLocation pLocation, double pRadius)
        {
            this.geoLocation = pLocation;
            this.radius = pRadius;
        }

        @Override
        protected Void doInBackground(Void... params)
        {
            try
            {
                mBronzePointsQuery = GeofireSingleton.getInstance().getBronzePointReference().queryAtLocation(geoLocation, radius);
                mBronzePointsQuery.addGeoQueryEventListener(bronzePointsListener);
            }
            catch (IllegalArgumentException ex)
            {
                ex.printStackTrace();
            }
            return null;
        }
    }
}
