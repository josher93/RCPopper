package com.globalpaysolutions.rcpopper.ui.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.facebook.login.widget.LoginButton;
import com.globalpaysolutions.rcpopper.R;
import com.globalpaysolutions.rcpopper.models.DialogContent;
import com.globalpaysolutions.rcpopper.presenters.AuthPresenterImpl;
import com.globalpaysolutions.rcpopper.utils.NavigationFlags;
import com.globalpaysolutions.rcpopper.views.AuthView;

public class Auth extends AppCompatActivity implements AuthView
{
    private static final String TAG = Auth.class.getSimpleName();

    //Views and layouts
    LoginButton btnLogin;
    ProgressDialog mProgressDialog;

    //MVP
    AuthPresenterImpl mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        btnLogin = (LoginButton) findViewById(R.id.btnLogin);

        mPresenter = new AuthPresenterImpl(this, this, this);
        mPresenter.setupFacebookAuth(btnLogin);
    }

    @Override
    public void initializeViews()
    {

    }

    @Override
    public void showLoadingDialog(String content)
    {
        try
        {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(content);
            mProgressDialog.show();
            mProgressDialog.setCanceledOnTouchOutside(false);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public void hideLoadingDialog()
    {
        try
        {
            if (mProgressDialog != null && mProgressDialog.isShowing())
            {
                mProgressDialog.dismiss();
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public void navigatePop()
    {
        try
        {
            Intent pop = new Intent(this, Pop.class);
            NavigationFlags.addFlags(pop);
            startActivity(pop);
        }
        catch (Exception ex)
        {
            Log.e(TAG, "Error navigating to Pop.class: " + ex.getMessage());
        }
    }

    @Override
    public void showGenericDialog(DialogContent content, View.OnClickListener listener)
    {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle(content.getTitle());
        alertDialog.setMessage(content.getContent());
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, content.getButton1(),
                new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        mPresenter.onActivityResult(requestCode, resultCode, data);
    }
}
