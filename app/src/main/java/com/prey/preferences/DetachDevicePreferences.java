/*******************************************************************************
 * Created by Carlos Yaconi
 * Copyright 2015 Prey Inc. All rights reserved.
 * License: GPLv3
 * Full license at "/LICENSE"
 ******************************************************************************/
package com.prey.preferences;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.widget.Toast;

import com.prey.PreyConfig;
import com.prey.activities.LoginActivity;
import com.prey.exceptions.PreyException;
import com.prey.net.PreyWebServices;
import com.prey.R;

public class DetachDevicePreferences extends DialogPreference {
    Context ctx = null;

    public DetachDevicePreferences(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.ctx = context;
    }

    public DetachDevicePreferences(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.ctx = context;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        super.onClick(dialog, which);
        if (which == DialogInterface.BUTTON_POSITIVE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                new DetachDevice().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            else
                new DetachDevice().execute();
        }
    }

    public class DetachDevice extends AsyncTask<Void, Void, Void> {

        private String error = null;
        ProgressDialog progressDialog = null;

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(getContext());
            progressDialog.setMessage(getContext().getText(R.string.preferences_detach_dettaching_message).toString());
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... unused) {
            try {PreyConfig.getPreyConfig(getContext()).unregisterC2dm(false); } catch (Exception e) { error = e.getMessage();}
            try {   PreyConfig.getPreyConfig(getContext()).setSecurityPrivilegesAlreadyPrompted(false);} catch (Exception e) {}


            try {   PreyConfig.getPreyConfig(getContext()).setProtectAccount(false);} catch (Exception e) {error = e.getMessage();}
            try {   PreyConfig.getPreyConfig(getContext()).setProtectPrivileges(false);} catch (Exception e) {error = e.getMessage();}
            try {   PreyConfig.getPreyConfig(getContext()).setProtectTour(false);} catch (Exception e) {error = e.getMessage();}
            try {   PreyConfig.getPreyConfig(getContext()).setProtectReady(false);} catch (Exception e) {error = e.getMessage();}

            try {  PreyWebServices.getInstance().deleteDevice(ctx);} catch (Exception e) {error = e.getMessage();}
            try {    PreyConfig.getPreyConfig(getContext()).wipeData();} catch (Exception e) {error = e.getMessage();}


            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            try {
                progressDialog.dismiss();
            } catch (Exception e) {
            }
            if (error != null) {
                Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
                showDialog(new Bundle());
            } else {
                Intent welcome = new Intent(getContext(), LoginActivity.class);
                getContext().startActivity(welcome);
            }
        }

    }
}
