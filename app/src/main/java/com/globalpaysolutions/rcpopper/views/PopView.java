package com.globalpaysolutions.rcpopper.views;

import android.content.DialogInterface;
import android.location.Location;

import com.globalpaysolutions.rcpopper.models.DialogContent;
import com.globalpaysolutions.rcpopper.models.MarkerData;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Josué Chávez on 15/3/2018.
 */

public interface PopView
{
    void renderMap();
    void addClickListeners();
    void displayActivateLocationDialog();
    void checkPermissions();
    void setInitialUserLocation(Location pLocation);
    void updateUserLocationOnMap(Location pLocation);
    void showLoadingDialog(String pLabel);
    void hideLoadingDialog();
    void addGoldPoint(MarkerData markerData, LatLng pLocation);
    void removeGoldPoint(String pKey);
    void addSilverPoint(MarkerData markerData, LatLng pLocation);
    void removeSilverPoint(String pKey);
    void addBronzePoint(MarkerData markerData, LatLng pLocation);
    void removeBronzePoint(String pKey);
    void showToast(String string);
    void showGenericDialog(DialogContent content, DialogInterface.OnClickListener listener);
}
