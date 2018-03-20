package com.globalpaysolutions.rcpopper.presenters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.globalpaysolutions.rcpopper.R;
import com.globalpaysolutions.rcpopper.data.RealmManager;
import com.globalpaysolutions.rcpopper.interactors.AuthInteractor;
import com.globalpaysolutions.rcpopper.interactors.AuthListener;
import com.globalpaysolutions.rcpopper.models.DialogContent;
import com.globalpaysolutions.rcpopper.models.data.Popper;
import com.globalpaysolutions.rcpopper.presenters.interfaces.IAuthPresenter;
import com.globalpaysolutions.rcpopper.views.AuthView;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;

/**
 * Created by Josué Chávez on 13/3/2018.
 */

public class AuthPresenterImpl implements IAuthPresenter, AuthListener
{
    private static final String TAG = AuthPresenterImpl.class.getSimpleName();

    private Context mContext;
    private AuthView mView;
    private AuthInteractor mInteractor;
    private AppCompatActivity mActivity;
    private ProfileTracker mProfileTracker;

    public AuthPresenterImpl(Context context, AuthView view, AppCompatActivity activity)
    {
        this.mContext = context;
        this.mView = view;
        this.mActivity = activity;
        this.mInteractor = new AuthInteractor(mContext);
    }

    @Override
    public void setupFacebookAuth(LoginButton button)
    {
        mInteractor.initializeFacebook(this);

        button.setReadPermissions(Arrays.asList("email","public_profile"));
        mInteractor.authenticateFacebookUser(button, this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        mInteractor.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onFacebookEmailSuccess(String email, LoginResult loginResult)
    {
        try
        {
            Profile user = Profile.getCurrentProfile();

            //Saves to Realm
            Popper popper = new Popper();
            popper.setFacebookID(user.getId());
            popper.setFirstname(user.getFirstName());
            popper.setMiddlename(user.getMiddleName());
            popper.setLastname(user.getLastName());
            popper.setEmail(email);

            RealmManager.getInstance(mContext).savePopper(popper);

            //Auths on Firebase
            mInteractor.firebaseAuth(this, mActivity);

        }
        catch (Exception ex)
        {
            Log.e(TAG, "Error processing success email request: " + ex.getMessage());
            mView.hideLoadingDialog();
            mInteractor.facebookLogout();
        }

    }

    @Override
    public void onFacebookEmailError()
    {
        // Auths on Firebase even if retrieve email
        // failed, because email won't be necessary for
        // the rest of the process.
        mInteractor.firebaseAuth(this, mActivity);
    }

    @Override
    public void onGraphLoginSuccess(LoginResult loginResult)
    {
        try
        {
            mView.showLoadingDialog(mContext.getString(R.string.label_loading_please_wait));

            mProfileTracker = new ProfileTracker()
            {
                @Override
                protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile)
                {
                    if(currentProfile != null)
                    {
                        //Request email
                        mInteractor.requestUserEmail(AuthPresenterImpl.this, loginResult);
                    }
                }
            };

        }
        catch (Exception ex)
        {
            Log.e(TAG, "Error on login success: " + ex.getMessage());
        }
    }

    @Override
    public void onGraphLoginError(FacebookException pException)
    {
        //Deletes popper and logs out
        Profile profile = Profile.getCurrentProfile();
        if(profile != null)
            RealmManager.getInstance(mContext).deletePopper(profile.getId());

        mInteractor.facebookLogout();

        DialogContent content = new DialogContent();
        content.setTitle(mContext.getString(R.string.title_something_went_wrong));
        content.setContent(mContext.getString(R.string.label_something_went_wrong_try_again));
        content.setButton1(mContext.getString(R.string.button_accept));

        mView.hideLoadingDialog();
        mView.showGenericDialog(content, null);
    }

    @Override
    public void onFirebaseAuthSuccess(FirebaseUser firebaseUser)
    {
        mView.hideLoadingDialog();

        //Updates Popper on Realm
        RealmManager.getInstance(mContext)
                .updatePopperFirebaseUid(firebaseUser.getUid(), Profile.getCurrentProfile().getId());

        //Sets settings as authenticated
        RealmManager.getInstance(mContext).setAuthenticated(true);

        mView.navigatePop();
    }

    @Override
    public void onFirebaseAuthError()
    {
        try
        {
            mView.hideLoadingDialog();

            //Deletes popper and logs out
            RealmManager.getInstance(mContext).deletePopper(Profile.getCurrentProfile().getId());
            mInteractor.facebookLogout();

            DialogContent content = new DialogContent();
            content.setTitle(mContext.getString(R.string.title_something_went_wrong));
            content.setContent(mContext.getString(R.string.label_something_went_wrong_try_again));
            content.setButton1(mContext.getString(R.string.button_accept));

            mView.showGenericDialog(content, null);
        }
        catch (Exception ex)
        {
            Log.e(TAG, "Error auth on Firebase: " + ex.getMessage());
        }
    }


}
