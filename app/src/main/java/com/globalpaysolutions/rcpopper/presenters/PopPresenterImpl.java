package com.globalpaysolutions.rcpopper.presenters;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.firebase.geofire.GeoLocation;
import com.globalpaysolutions.rcpopper.R;
import com.globalpaysolutions.rcpopper.core.location.GoogleLocationApiManager;
import com.globalpaysolutions.rcpopper.core.location.LocationCallback;
import com.globalpaysolutions.rcpopper.data.RealmManager;
import com.globalpaysolutions.rcpopper.interactors.PopInteractor;
import com.globalpaysolutions.rcpopper.interactors.PopListener;
import com.globalpaysolutions.rcpopper.models.DialogContent;
import com.globalpaysolutions.rcpopper.models.MarkerData;
import com.globalpaysolutions.rcpopper.presenters.interfaces.IPopPresenter;
import com.globalpaysolutions.rcpopper.ui.activities.Auth;
import com.globalpaysolutions.rcpopper.utils.Constants;
import com.globalpaysolutions.rcpopper.utils.NavigationFlags;
import com.globalpaysolutions.rcpopper.views.PopView;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Josué Chávez on 15/3/2018.
 */

public class PopPresenterImpl implements IPopPresenter, LocationCallback, PopListener
{
    private static final String TAG = PopPresenterImpl.class.getSimpleName();

    private int mTimesUpdates;
    private Context mContext;
    private PopView mView;
    private AppCompatActivity mActivity;
    private PopInteractor mInteractor;
    private LatLng mCurrentLocation;
    private GoogleLocationApiManager mGoogleLocationApiManager;

    public PopPresenterImpl(Context context, PopView view, AppCompatActivity activity)
    {
        this.mTimesUpdates = 0;
        this.mContext = context;
        this.mView = view;
        this.mActivity = activity;
        this.mInteractor = new PopInteractor(mContext, this);

        this.mGoogleLocationApiManager = new GoogleLocationApiManager(activity, mContext, Constants.FOUR_METTERS_DISPLACEMENT);
        this.mGoogleLocationApiManager.setLocationCallback(this);
    }

    @Override
    public void setInitialViewsState()
    {
        this.mView.renderMap();
        this.mView.addClickListeners();
    }

    @Override
    public void chekcLocationServiceEnabled()
    {
        LocationManager locationManager;
        boolean gpsEnabled = false;
        boolean networkEnabled = false;

        try
        {
            locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
            gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            networkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        if(!gpsEnabled && !networkEnabled)
        {
            DialogContent content = new DialogContent();
            content.setTitle(mContext.getString(R.string.dialog_title_activate_location));
            content.setContent(mContext.getString(R.string.dialog_content_activate_location));
            content.setButton1(mContext.getString(R.string.button_accept));

            mView.showGenericDialog(content, null);
        }
    }

    @Override
    public void checkPermissions()
    {
        mView.checkPermissions();
    }

    @Override
    public void connnectToLocationService()
    {
        mGoogleLocationApiManager.connect();
    }

    @Override
    public void onMapReady()
    {
        connnectToLocationService();
    }

    @Override
    public void disconnectFromLocationService()
    {
        mGoogleLocationApiManager.disconnect();
    }

    @Override
    public void intializeGeolocation()
    {
        try
        {
            mInteractor.initializeGeolocation();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public void chestsQuery(LatLng pLocation)
    {
        GeoLocation location = new GeoLocation(pLocation.latitude, pLocation.longitude);
        mInteractor.goldPointsQuery(location, Constants.GOLD_CHESTS_QUERY_RADIUS_KM);
        mInteractor.silverPointsQuery(location, Constants.SILVER_CHESTS_QUERY_RADIUS_KM);
        mInteractor.bronzePointsQuery(location, Constants.BRONZE_CHESTS_QUERY_RADIUS_KM);
        mInteractor.wildcardPointsQuery(location, Constants.GOLD_CHESTS_QUERY_RADIUS_KM);
    }

    @Override
    public void updateChestsCriteria(LatLng pLocation)
    {
        GeoLocation location = new GeoLocation(pLocation.latitude, pLocation.longitude);
        mInteractor.goldPointsUpdateCriteria(location, Constants.GOLD_CHESTS_QUERY_RADIUS_KM);
        mInteractor.silverPointsUpdateCriteria(location, Constants.SILVER_CHESTS_QUERY_RADIUS_KM);
        mInteractor.bronzePointsUpdateCriteria(location, Constants.BRONZE_CHESTS_QUERY_RADIUS_KM);
        mInteractor.wildcardPointsUpdateCriteria(location, Constants.GOLD_CHESTS_QUERY_RADIUS_KM);
    }

    @Override
    public void writeChestLocation(String type)
    {
        try
        {
            //Inserts data first, then location
            if(mCurrentLocation != null)
            {
                GeoLocation geoLocation = new GeoLocation(mCurrentLocation.latitude, mCurrentLocation.longitude);
                switch (type)
                {
                    case Constants.CHEST_TYPE_GOLD:
                        mInteractor.insertGoldChestData(geoLocation);
                        break;
                    case Constants.CHEST_TYPE_SILVER:
                        mInteractor.insertSilverChestData(geoLocation);
                        break;
                    case Constants.CHEST_TYPE_BRONZE:
                        mInteractor.insertBronzeChestData(geoLocation);
                        break;
                    case Constants.CHEST_TYPE_WILDCARD:
                        mInteractor.insertWildcardChestData(geoLocation);
                        break;
                }
            }
        }
        catch (Exception ex)
        {
            Log.e(TAG, "Error writing chest location: " + ex.getMessage());
        }
    }

    @Override
    public void checkSignin()
    {
        try
        {
            if(!RealmManager.getInstance(mContext).isAuthenticated())
            {
                Intent auth = new Intent(mActivity, Auth.class);
                NavigationFlags.addFlags(auth);
                mContext.startActivity(auth);
            }
        }
        catch (Exception ex)
        {
            Log.e(TAG, "Error checking authentication: " + ex.getMessage());
        }
    }

    @Override
    public void popLocation(LatLng location)
    {
        try
        {
            mCurrentLocation = location;
        }
        catch (Exception ex)
        {
            Log.e(TAG, "Couldn't process location from map: " + ex.getMessage());
        }
    }

    @Override
    public void deleteChest(String firebaseKey, String chestType)
    {
        try
        {
            switch (chestType)
            {
                case Constants.CHEST_TYPE_GOLD:
                    mInteractor.deleteGoldChest(firebaseKey, this);
                    break;
                case  Constants.CHEST_TYPE_SILVER:
                    mInteractor.deleteSilverChest(firebaseKey, this);
                    break;
                case Constants.CHEST_TYPE_BRONZE:
                    mInteractor.deleteBronzeChest(firebaseKey, this);
                    break;
                case Constants.CHEST_TYPE_WILDCARD:
                    mInteractor.deleteWildcardChest(firebaseKey, this);
                    break;
            }

        }
        catch (Exception ex)
        {
            Log.e(TAG, "Error on delete from firebase: " + ex.getMessage());
        }
    }

    /*
    *
    *
    *       LOCATION CALLBACK
    *
    * */

    @Override
    public void onLocationChanged(Location location)
    {
        try
        {
            if(location != null)
            {
                mTimesUpdates = mTimesUpdates +1;

                if(mTimesUpdates <= 2)
                    mView.updateUserLocationOnMap(location);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public void onLocationApiManagerConnected(Location location)
    {
        try
        {
            if(location != null)
            {
                mView.setInitialUserLocation(location);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public void onLocationApiManagerDisconnected()
    {

    }

    @Override
    public void onGoldKeyEntered(String key, LatLng location)
    {
        MarkerData markerData = new MarkerData();
        markerData.setKey(key);
        markerData.setType(Constants.CHEST_TYPE_GOLD);

        mView.addGoldPoint(markerData, location);
    }

    @Override
    public void onGoldKeyExited(String pKey)
    {
        mView.removeGoldPoint(pKey);
    }

    @Override
    public void onGoldGeoQueryReady()
    {

    }

    @Override
    public void onGoldKeyDataInserted(String key, GeoLocation geoLocation)
    {
        mInteractor.insertGoldChest(key, geoLocation, this);
    }

    @Override
    public void onGoldInserted(String key, GeoLocation geoLocation)
    {
        LatLng location = new LatLng(geoLocation.latitude, geoLocation.longitude);

        MarkerData markerData = new MarkerData();
        markerData.setKey(key);
        markerData.setType(Constants.CHEST_TYPE_GOLD);
        mView.addGoldPoint(markerData, location);
    }

    @Override
    public void onGoldChestDeleteSuccess(String key)
    {
        mView.removeGoldPoint(key);
    }

    @Override
    public void onChestDeleteError()
    {
        mView.showToast(mContext.getString(R.string.error_removing_chest_try_again));
    }

    @Override
    public void onSilverKeyEntered(String key, LatLng location)
    {
        MarkerData markerData = new MarkerData();
        markerData.setKey(key);
        markerData.setType(Constants.CHEST_TYPE_SILVER);

        mView.addSilverPoint(markerData, location);
    }

    @Override
    public void onSilverKeyExited(String pKey)
    {
        mView.removeSilverPoint(pKey);
    }

    @Override
    public void onSilverGeoQueryReady()
    {

    }

    @Override
    public void onSilverKeyDataInserted(String insertedKey, GeoLocation geoLocation)
    {
        mInteractor.insertSilverChest(insertedKey, geoLocation, this);
    }

    @Override
    public void onSilverInserted(String key, GeoLocation geoLocation)
    {
        LatLng location = new LatLng(geoLocation.latitude, geoLocation.longitude);

        MarkerData markerData = new MarkerData();
        markerData.setKey(key);
        markerData.setType(Constants.CHEST_TYPE_SILVER);
        mView.addSilverPoint(markerData, location);
    }

    @Override
    public void onSilverChestDeleteSuccess(String key)
    {
        mView.removeSilverPoint(key);
    }

    @Override
    public void onBronzeKeyEntered(String pKey, LatLng pLocation)
    {
        MarkerData markerData = new MarkerData();
        markerData.setKey(pKey);
        markerData.setType(Constants.CHEST_TYPE_BRONZE);
        mView.addBronzePoint(markerData, pLocation);
    }

    @Override
    public void onBronzeKeyExited(String pKey)
    {
        mView.removeBronzePoint(pKey);
    }

    @Override
    public void onBronzeGeoQueryReady()
    {

    }

    @Override
    public void onBronzeKeyDataInserted(String insertedKey, GeoLocation geoLocation)
    {
        mInteractor.insertBronzeChest(insertedKey, geoLocation, this);
    }

    @Override
    public void onBronzeInserted(String key, GeoLocation geoLocation)
    {
        LatLng location = new LatLng(geoLocation.latitude, geoLocation.longitude);
        MarkerData markerData = new MarkerData();
        markerData.setKey(key);
        markerData.setType(Constants.CHEST_TYPE_BRONZE);

        mView.addBronzePoint(markerData, location);
    }

    @Override
    public void onBronzeChestDeleteSuccess(String key)
    {
        mView.removeBronzePoint(key);
    }

    @Override
    public void onWildcardKeyEntered(String pKey, LatLng pLocation)
    {
        MarkerData markerData = new MarkerData();
        markerData.setKey(pKey);
        markerData.setType(Constants.CHEST_TYPE_WILDCARD);
        mView.addWildcardPoint(markerData, pLocation);
    }

    @Override
    public void onWildcardKeyExited(String pKey)
    {
        mView.removeWildcardPoint(pKey);
    }

    @Override
    public void onWildcardGeoQueryReady()
    {

    }

    @Override
    public void onWildcardKeyDataInserted(String insertedKey, GeoLocation geoLocation)
    {
        mInteractor.insertWildcardChest(insertedKey, geoLocation, this);
    }

    @Override
    public void onWildcardInserted(String key, GeoLocation geoLocation)
    {
        LatLng location = new LatLng(geoLocation.latitude, geoLocation.longitude);

        MarkerData markerData = new MarkerData();
        markerData.setKey(key);
        markerData.setType(Constants.CHEST_TYPE_WILDCARD);
        mView.addWildcardPoint(markerData, location);
    }

    @Override
    public void onWildcardChestDeleteSuccess(String key)
    {
        mView.removeWildcardPoint(key);
    }

    @Override
    public void onWriteDataError()
    {
        mView.showToast(mContext.getString(R.string.error_adding_chest_try_again));
    }
}
