package com.globalpaysolutions.rcpopper.views;

import android.view.View;

import com.globalpaysolutions.rcpopper.models.DialogContent;

/**
 * Created by Josué Chávez on 13/3/2018.
 */

public interface AuthView
{
    void initializeViews();
    void showLoadingDialog(String content);
    void hideLoadingDialog();
    void navigatePop();
    void showGenericDialog(DialogContent content, View.OnClickListener listener);
}
