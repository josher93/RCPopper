package com.globalpaysolutions.rcpopper.models.data;

import io.realm.RealmObject;

/**
 * Created by Josué Chávez on 14/3/2018.
 */

public class Settings extends RealmObject
{
    private boolean authenticated;

    public boolean isAuthenticated()
    {
        return authenticated;
    }

    public void setAuthenticated(boolean authenticated)
    {
        this.authenticated = authenticated;
    }
}
