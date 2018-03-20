package com.globalpaysolutions.rcpopper.interactors;

import com.firebase.geofire.GeoLocation;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Josué Chávez on 15/3/2018.
 */

public interface PopListener
{
    //GeoFire GoldPoints
    void onGoldKeyEntered(String pKey, LatLng pLocation);
    void onGoldKeyExited(String pKey);
    void onGoldGeoQueryReady();
    void onGoldKeyDataInserted(String key, GeoLocation geoLocation);
    void onGoldInserted(String key, GeoLocation geoLocation);
    void onGoldChestDeleteSuccess(String key);
    void onChestDeleteError();

    //GeoFire SilverPoints
    void onSilverKeyEntered(String pKey, LatLng pLocation);
    void onSilverKeyExited(String pKey);
    void onSilverGeoQueryReady();
    void onSilverKeyDataInserted(String insertedKey, GeoLocation geoLocation);
    void onSilverInserted(String key, GeoLocation geoLocation);
    void onSilverChestDeleteSuccess(String key);

    //GeoFire BronzePoints
    void onBronzeKeyEntered(String pKey, LatLng pLocation);
    void onBronzeKeyExited(String pKey);
    void onBronzeGeoQueryReady();
    void onBronzeKeyDataInserted(String insertedKey, GeoLocation geoLocation);
    void onBronzeInserted(String key, GeoLocation geoLocation);
    void onBronzeChestDeleteSuccess(String key);

    void onWriteDataError();
}
