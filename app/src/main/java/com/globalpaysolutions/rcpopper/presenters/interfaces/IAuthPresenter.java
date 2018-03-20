package com.globalpaysolutions.rcpopper.presenters.interfaces;

import android.content.Intent;

import com.facebook.login.widget.LoginButton;

/**
 * Created by Josué Chávez on 13/3/2018.
 */

public interface IAuthPresenter
{
    void setupFacebookAuth(LoginButton button);

    void onActivityResult(int requestCode, int resultCode, Intent data);
}
