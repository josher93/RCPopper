package com.globalpaysolutions.rcpopper.interactors.interfaces;

import com.firebase.geofire.GeoLocation;
import com.globalpaysolutions.rcpopper.interactors.PopListener;
import com.globalpaysolutions.rcpopper.presenters.PopPresenterImpl;
import com.globalpaysolutions.rcpopper.ui.activities.Pop;

/**
 * Created by Josué Chávez on 15/3/2018.
 */

public interface IPopInteractor
{
    void initializeGeolocation();
    void goldPointsQuery(GeoLocation pLocation, double pRadius);
    void goldPointsUpdateCriteria(GeoLocation pLocation, double pRadius);
    void silverPointsQuery(GeoLocation pLocation, double pRadius);
    void silverPointsUpdateCriteria(GeoLocation pLocation, double pRadius);
    void bronzePointsQuery(GeoLocation pLocation, double pRadius);
    void bronzePointsUpdateCriteria(GeoLocation pLocation, double pRadius);
    void wildcardPointsQuery(GeoLocation pLocation, double pRadius);
    void wildcardPointsUpdateCriteria(GeoLocation pLocation, double pRadius);
    void insertGoldChestData(GeoLocation geoLocation);
    void insertGoldChest(String key, GeoLocation geoLocation, PopListener listener);
    void insertSilverChestData(GeoLocation geoLocation);
    void insertSilverChest(String key, GeoLocation geoLocation, PopListener listener);
    void insertBronzeChestData(GeoLocation geoLocation);
    void insertBronzeChest(String insertedKey, GeoLocation geoLocation, PopListener listener);
    void insertWildcardChestData(GeoLocation geoLocation);
    void insertWildcardChest(String insertedKey, GeoLocation geoLocation, PopListener listener);
    void deleteGoldChest(String firebaseKey, PopListener listener);
    void deleteSilverChest(String firebaseKey, PopListener listener);
    void deleteBronzeChest(String firebaseKey, PopListener listener);
    void deleteWildcardChest(String firebaseKey, PopListener listener);
    void detachFirebaseListeners();


}
