package com.globalpaysolutions.rcpopper.models.data;

import io.realm.RealmObject;

/**
 * Created by Josué Chávez on 14/3/2018.
 */

public class Popper extends RealmObject
{
    private String facebookID;
    private String firstname;
    private String middlename;
    private String lastname;
    private String email;
    private String firebaseUid;

    public String getFacebookID()
    {
        return facebookID;
    }

    public void setFacebookID(String facebookID)
    {
        this.facebookID = facebookID;
    }

    public String getFirstname()
    {
        return firstname;
    }

    public void setFirstname(String firstname)
    {
        this.firstname = firstname;
    }

    public String getMiddlename()
    {
        return middlename;
    }

    public void setMiddlename(String middlename)
    {
        this.middlename = middlename;
    }

    public String getLastname()
    {
        return lastname;
    }

    public void setLastname(String lastname)
    {
        this.lastname = lastname;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getFirebaseUid()
    {
        return firebaseUid;
    }

    public void setFirebaseUid(String firebaseUid)
    {
        this.firebaseUid = firebaseUid;
    }

}
