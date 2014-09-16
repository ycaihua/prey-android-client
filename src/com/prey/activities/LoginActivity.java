/*******************************************************************************
 * Created by Carlos Yaconi
 * Copyright 2012 Fork Ltd. All rights reserved.
 * License: GPLv3
 * Full license at "/LICENSE"
 ******************************************************************************/
package com.prey.activities;

import android.app.NotificationManager;

import com.prey.PreyVerify;
import com.prey.R;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.prey.PreyConfig;
import com.prey.services.PreyDisablePowerOptionsService;

public class LoginActivity extends PasswordActivity {

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// ignore orientation change
		super.onConfigurationChanged(newConfig);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Delete notifications (in case Activity was started by one of them)
		NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		nm.cancel(R.string.preyForAndroid_name);
		startup();
		
		boolean disablePowerOptions = PreyConfig.getPreyConfig(getApplicationContext()).isDisablePowerOptions();
		if (disablePowerOptions) {
			startService(new Intent(getApplicationContext(), PreyDisablePowerOptionsService.class));
		}else{
			stopService(new Intent(getApplicationContext(), PreyDisablePowerOptionsService.class));
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		startup();
	}

	private void startup() {
		if (!isThisDeviceAlreadyRegisteredWithPrey()) {
			Intent intent =null;
			if(isMineduc()){
				intent = new Intent(LoginActivity.this, AddDeviceToAccountMineducActivity.class);
			}else{
				if (!isThereBatchInstallationKey()){
					intent = new Intent(LoginActivity.this, WelcomeActivity.class);
				
				}else{
					intent = new Intent(LoginActivity.this, WelcomeBatchActivity.class);
				}
			}
			startActivity(intent);
			finish();
		} else {
			PreyVerify.getInstance(this);
			 
				
			 

					showLoginMineduc();
			 
		}
	}
	
	private void showLoginMineduc() {
		setContentView(R.layout.login2);
	
		Button gotoCP = (Button) findViewById(R.id.login_btn_cp);
		 

		gotoCP.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				try{
					String url=PreyConfig.getPreyConfig(getApplicationContext()).getPreyPanelUrl();
					Intent browserIntent = new Intent("android.intent.action.VIEW", Uri.parse(url));
					startActivity(browserIntent);
				}catch(Exception e){
				}
			}
		});

	 
	}

	 
	 
	private boolean isThisDeviceAlreadyRegisteredWithPrey() {
		return getPreyConfig().isThisDeviceAlreadyRegisteredWithPrey(false);
	}

	
 
	
	private boolean isThereBatchInstallationKey() {
		String apiKeyBatch=getPreyConfig().getApiKeyBatch();
		return (apiKeyBatch!=null&&!"".equals(apiKeyBatch));
	}
	
	private boolean isMineduc(){
		return PreyConfig.getPreyConfig(this).isMineduc();
	}

}
