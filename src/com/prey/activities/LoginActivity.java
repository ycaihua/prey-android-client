/*******************************************************************************
 * Created by Carlos Yaconi
 * Copyright 2012 Fork Ltd. All rights reserved.
 * License: GPLv3
 * Full license at "/LICENSE"
 ******************************************************************************/
package com.prey.activities;

import com.prey.PreyConfig;
import com.prey.PreyStatus;
import com.prey.R;
import com.prey.backwardcompatibility.FroyoSupport;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class LoginActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		startup();

	}

	private void startup() {
		if (!PreyConfig.getPreyConfig(this).isThisDeviceAlreadyRegisteredWithPrey()) {
			welcome();
		} else {
			showLogin();
		}
	}

	private void showLogin() {
		setContentView(R.layout.login);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		Button gotoSettings = (Button) findViewById(R.id.login_btn_settings);

		if (!FroyoSupport.getInstance(this).isAdminActive()) {
			String h1 = getString(R.string.device_not_ready_h1);
			String h2 = getString(R.string.device_not_ready_h2);
			TextView textH1 = (TextView) findViewById(R.id.device_ready_h1_text);
			TextView textH2 = (TextView) findViewById(R.id.device_ready_h2_text);
			textH1.setText(h1);
			textH2.setText(h2);
		}

		try {
			Button gotoCP = (Button) findViewById(R.id.login_btn_cp);
			gotoCP.setOnClickListener(new Button.OnClickListener() {
				public void onClick(View v) {
					try {
						String url = PreyConfig.getPreyConfig(getApplicationContext()).getPreyPanelUrl();
						Intent browserIntent = new Intent("android.intent.action.VIEW", Uri.parse(url));
						startActivity(browserIntent);
					} catch (Exception e) {
					}
				}
			});
		} catch (Exception e) {
		}

		gotoSettings.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				if (!PreyStatus.getInstance().isPreyConfigurationActivityResume()) {
					Intent intent = new Intent(LoginActivity.this, CheckPasswordActivity.class);
					startActivity(intent);
				} else {
					Intent intent = new Intent(LoginActivity.this, PreyConfigurationActivity.class);
					startActivity(intent);
				}
			}
		});
	}

	protected void welcome() {
		setContentView(R.layout.welcome);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		try {
			Button newUser = (Button) findViewById(R.id.btn_welcome_newuser);

			newUser.setOnClickListener(new Button.OnClickListener() {
				public void onClick(View v) {
					Intent intent = new Intent(getApplicationContext(), CreateAccountActivity.class);
					startActivity(intent);
					finish();
				}
			});
		} catch (Exception e) {
		}

		try {
			Button oldUser = (Button) findViewById(R.id.btn_welcome_olduser);
			oldUser.setOnClickListener(new Button.OnClickListener() {
				public void onClick(View v) {
					Intent intent = new Intent(getApplicationContext(), AddDeviceToAccountActivity.class);
					startActivity(intent);
					finish();
				}
			});
		} catch (Exception e) {
		}
	}
}
