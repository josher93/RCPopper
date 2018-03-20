package com.globalpaysolutions.rcpopper.presenters.interfaces;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Josué Chávez on 15/3/2018.
 */

public interface IPopPresenter
{
    void setInitialViewsState();
    void chekcLocationServiceEnabled();
    void checkPermissions();

    void connnectToLocationService();
    void onMapReady();
    void disconnectFromLocationService();

    void intializeGeolocation();
    void chestsQuery(LatLng pLocation);
    void updateChestsCriteria(LatLng pLocation);
    void writeChestLocation(String type);

    void checkSignin();
    void popLocation(LatLng location);

    void deleteChest(String firebaseKey, String chestType);
}
