package com.globalpaysolutions.rcpopper.interactors.interfaces;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.globalpaysolutions.rcpopper.interactors.AuthListener;

/**
 * Created by Josué Chávez on 13/3/2018.
 */

public interface IAuthInteractor
{
    void initializeFacebook(AuthListener listener);
    void authenticateFacebookUser(LoginButton button, AuthListener listener);
    void requestUserEmail(AuthListener authPresenter, LoginResult loginResult);
    void facebookLogout();
    void firebaseAuth(AuthListener listener, AppCompatActivity activity);
    void onActivityResult(int requestCode, int resultCode, Intent data);
}
