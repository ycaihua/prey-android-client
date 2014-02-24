/*******************************************************************************
 * Created by Carlos Yaconi
 * Copyright 2012 Fork Ltd. All rights reserved.
 * License: GPLv3
 * Full license at "/LICENSE"
 ******************************************************************************/
package com.prey.activities;

 
 
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
 
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
 
 
import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import com.prey.PreyAccountData;
import com.prey.PreyConfig;
import com.prey.PreyLogger;
import com.prey.R; 
import com.prey.exceptions.PreyException;
import com.prey.net.PreyWebServices;
 
public class WelcomeSmartpayActivity extends PreyActivity {

	private String error = null;
	
	private String applicationNumber = null;
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// ignore orientation change
		super.onConfigurationChanged(newConfig);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_account_batch);

		Button ok = (Button) findViewById(R.id.new_account_btn_ok);
		
		ok.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				applicationNumber = ((EditText) findViewById(R.id.new_account_application_number)).getText().toString();
				 

				if (applicationNumber.equals(""))
					Toast.makeText(WelcomeSmartpayActivity.this, R.string.error_all_fields_are_required, Toast.LENGTH_LONG).show();
				else {
					new AddDeviceToApiKeyBatch().execute(getPreyConfig().getApiKeyBatch(),getPreyConfig().getEmailBatch(),applicationNumber, getDeviceType());
				}
			}
		});
	 
	 
	}

	
 

	private class AddDeviceToApiKeyBatch extends AsyncTask<String, Void, Void> {
		@Override
		protected void onPreExecute() {
			
		}
		
		@Override
		protected Void doInBackground(String... data) {
			try {
				error = null;
				Context ctx=WelcomeSmartpayActivity.this;
				String apiKey=data[0];
				String email=data[1];
				String appNumber=data[2];
				String deviceType=data[3];
				PreyLogger.i("apiKey:"+apiKey+" email:"+email+" appNumber:"+appNumber+" deviceType:"+deviceType);
				PreyAccountData accountData =PreyWebServices.getInstance().registerNewDeviceWithApiKeyEmailApplicationNumber(ctx, apiKey, email, appNumber, deviceType);
				getPreyConfig().saveAccount(accountData);
			} catch (PreyException e) {
				error = e.getMessage();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void unused) {
			if (error == null) {
				String message = getString(R.string.device_added_congratulations_text);
				Bundle bundle = new Bundle();
				bundle.putString("message", message);
				GoogleAnalyticsTracker.getInstance().trackEvent(
						"Device",  // Category
			            "Added",  // Action
			            "", // Label
			            1);
				PreyConfig.getPreyConfig(WelcomeSmartpayActivity.this).setCamouflageSet(true);
				Intent intent = new Intent(WelcomeSmartpayActivity.this, PermissionInformationBatchActivity.class);
				intent.putExtras(bundle);
				startActivity(intent);
				finish();
			}
		}
	}
	
	 
}