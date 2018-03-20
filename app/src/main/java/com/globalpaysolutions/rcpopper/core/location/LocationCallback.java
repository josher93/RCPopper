package com.globalpaysolutions.rcpopper.core.location;

import android.location.Location;

/**
 * Created by Josué Chávez on 15/3/2018.
 */

public interface LocationCallback
{
    void onLocationChanged(Location location);
    void onLocationApiManagerConnected(Location location);
    void onLocationApiManagerDisconnected();
}
