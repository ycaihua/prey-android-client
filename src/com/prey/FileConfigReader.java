/*******************************************************************************
 * Created by Carlos Yaconi
 * Copyright 2012 Fork Ltd. All rights reserved.
 * License: GPLv3
 * Full license at "/LICENSE"
 ******************************************************************************/
package com.prey;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import android.content.Context;
import android.content.res.Resources.NotFoundException;

public class FileConfigReader {

	private static FileConfigReader _instance = null;
	private Properties properties;

	private FileConfigReader(Context ctx) {
		try {
			PreyLogger.d("Loading config properties from file...");
			properties = new Properties();
			InputStream is = ctx.getResources().openRawResource(R.raw.config);
			properties.load(is);
			is.close();
			PreyLogger.d("Config: " + properties);

		} catch (NotFoundException e) {
			PreyLogger.e("Config file wasn't found", e);
		} catch (IOException e) {
			PreyLogger.e("Couldn't read config file", e);
		}
	}

	public static FileConfigReader getInstance(Context ctx) {
		if (_instance == null)
			_instance = new FileConfigReader(ctx);
		return _instance;
	}

	public String getGcmId() {
		return properties.getProperty("gcm-id");
	}

	public String getGcmIdPrefix() {
		return properties.getProperty("gcm-id-prefix");
	}

	public String getApiV2() {
		return properties.getProperty("api-v2");
	}

	public String getPreyDomain() {
		return properties.getProperty("prey-domain");
	}

	public String getPreySubdomain() {
		return properties.getProperty("prey-subdomain");
	}

	public String getPreyCampaign() {
		return properties.getProperty("prey-campaign");
	}

	public String getPreyPanel() {
		return properties.getProperty("prey-panel");
	}
}
