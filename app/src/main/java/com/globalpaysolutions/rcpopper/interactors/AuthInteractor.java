package com.globalpaysolutions.rcpopper.interactors;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.globalpaysolutions.rcpopper.interactors.interfaces.IAuthInteractor;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Josué Chávez on 13/3/2018.
 */

public class AuthInteractor implements IAuthInteractor
{
    private static final String TAG = AuthInteractor.class.getSimpleName();

    private Context mContext;
    private CallbackManager mCallbackManager;
    private FirebaseAuth mFirebaseAuth;
    //private ProfileTracker mProfileTracker;

    public AuthInteractor(Context context)
    {
        this.mContext = context;
    }

    @Override
    public void initializeFacebook(final AuthListener listener)
    {
        mCallbackManager = CallbackManager.Factory.create();
        mFirebaseAuth = FirebaseAuth.getInstance();

        AccessTokenTracker accessTokenTracker = new AccessTokenTracker()
        {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken)
            {
                if(oldAccessToken != null)
                    Log.i(TAG, "OldAccessToken: " + oldAccessToken.toString());

                if(currentAccessToken != null)
                    Log.i(TAG, "CurrentAccessTokenChanged: " + currentAccessToken.toString());
            }
        };
    }

    @Override
    public void authenticateFacebookUser(LoginButton button, final AuthListener listener)
    {
        button.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>()
        {
            @Override
            public void onSuccess(LoginResult loginResult)
            {
                listener.onGraphLoginSuccess(loginResult);
            }

            @Override
            public void onCancel()
            {
                Log.i(TAG, "Authenticate facebook user cancelled.");
            }

            @Override
            public void onError(FacebookException error)
            {
                listener.onGraphLoginError(error);
            }
        });
    }

    @Override
    public void requestUserEmail(final AuthListener listener, final LoginResult loginResult)
    {
        GraphRequest mGraphRequest = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback()
        {
            @Override
            public void onCompleted(JSONObject me, GraphResponse response)
            {
                if (response.getError() == null)
                {
                    try
                    {
                        String facebookEmail = !me.isNull("email") ? me.getString("email") : "";
                        listener.onFacebookEmailSuccess(facebookEmail, loginResult);
                    }
                    catch (JSONException ex)
                    {
                        ex.printStackTrace();
                    }
                }
                else
                {
                    listener.onFacebookEmailError();
                }
            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "email, name");
        mGraphRequest.setParameters(parameters);
        mGraphRequest.executeAsync();
    }

    @Override
    public void facebookLogout()
    {
        LoginManager.getInstance().logOut();
        Log.i(TAG, "Logged out from Facebook");
    }

    @Override
    public void firebaseAuth(final AuthListener listener, AppCompatActivity activity)
    {
        try
        {
            mFirebaseAuth.signInAnonymously()
                    .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>()
                    {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task)
                        {
                            if (task.isSuccessful())
                            {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInAnonymously:success");
                                listener.onFirebaseAuthSuccess(mFirebaseAuth.getCurrentUser());
                            }
                            else
                            {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInAnonymously:failure", task.getException());
                                listener.onFirebaseAuthError();
                            }
                        }
                    });
        }
        catch (Exception ex)
        {
            Log.e(TAG, "Error authenticating user on Firebase anonymously: " + ex.getMessage());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
