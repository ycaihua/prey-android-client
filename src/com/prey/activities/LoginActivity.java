package com.prey.activities;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

public class LoginActivity extends Activity {


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		startup();
		
	}
	
	private void startup() {
		 
	}
}
