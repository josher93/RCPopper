package com.globalpaysolutions.rcpopper.interactors;

import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by Josué Chávez on 13/3/2018.
 */

public interface AuthListener
{
    void onFacebookEmailSuccess(String pEmail, LoginResult pLoginResult);
    void onFacebookEmailError();
    void onGraphLoginSuccess(LoginResult pLoginResult);
    void onGraphLoginError(FacebookException pException);
    void onFirebaseAuthSuccess(FirebaseUser firebaseUser);
    void onFirebaseAuthError();

}
