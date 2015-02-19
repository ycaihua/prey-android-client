/*******************************************************************************
 * Created by Carlos Yaconi
 * Copyright 2012 Fork Ltd. All rights reserved.
 * License: GPLv3
 * Full license at "/LICENSE"
 ******************************************************************************/
package com.prey.activities;

import android.content.Intent;

public class SetupActivity extends PreyActivity {

	@Override
	public void onBackPressed() {
		Intent intent = new Intent(SetupActivity.this, LoginActivity.class);
		startActivity(intent);
		finish();
	}

}
