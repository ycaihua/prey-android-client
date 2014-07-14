package com.prey.actions.location;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.location.Location;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.prey.PreyLogger;
import com.prey.actions.HttpDataService;
import com.prey.exceptions.PreyException;
import com.prey.json.UtilJson;
import com.prey.net.PreyWebServices;
import com.prey.services.LocationService;

public class LocationUtil {

	public static final String LAT = "lat";
	public static final String LNG = "lng";
	public static final String ACC = "accuracy";
	
	public static HttpDataService dataLocation(Context ctx) {
		int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(ctx);
		if (ConnectionResult.SUCCESS == resultCode) {
			return dataLocationNew(ctx);
		} else {
			return dataLocationOld(ctx);
		}
	}

	public static HttpDataService dataLocationNew(Context ctx) {
		HttpDataService data = new HttpDataService("location");
		HashMap<String, String> parametersMap = new HashMap<String, String>();
		PreyLocationUpdate.getInstance(ctx).startScan();
		Location location = null;
		int i = 0;
		try {
			data.setList(true);
			while (location == null) {
				location = PreyLocationUpdate.getInstance(ctx).getLocation();
				if (location != null) {
					parametersMap.put(LAT, Double.toString(location.getLatitude()));
					parametersMap.put(LNG, Double.toString(location.getLongitude()));
					parametersMap.put(ACC, Float.toString(location.getAccuracy()));
					data.addDataListAll(parametersMap);
				} else {
					try {
						Thread.sleep(2500);
					} catch (InterruptedException e) {
						throw new PreyException("Thread was intrrupted. Finishing Location NotifierAction", e);
					}
					if (i > 4) {
						return null;
					}
					i++;
				}
			}
			
		} catch (Exception e) {
			PreyLogger.e("Error causa:" + e.getMessage(), e);
			Map<String, String> parms = UtilJson.makeMapParam("get", "location", "failed", e.getMessage());
			PreyWebServices.getInstance().sendNotifyActionResultPreyHttp(ctx, parms);
		} finally {
			PreyLocationUpdate.getInstance(ctx).stopScan();
		}
		PreyLogger.i("data:"+data);
		return data;
	}

	public static HttpDataService dataLocationOld(Context ctx) {

		HttpDataService data = new HttpDataService("location");
		Intent intent = new Intent(ctx, LocationService.class);
		try {

			data.setList(true);

			ctx.startService(intent);
			boolean validLocation = false;
			PreyLocation lastLocation;
			HashMap<String, String> parametersMap = new HashMap<String, String>();
			int i = 0;
			while (!validLocation) {
				lastLocation = PreyLocationManager.getInstance(ctx).getLastLocation();
				if (lastLocation.isValid()) {
					validLocation = true;
					parametersMap.put(LAT, Double.toString(lastLocation.getLat()));
					parametersMap.put(LNG, Double.toString(lastLocation.getLng()));
					parametersMap.put(ACC, Float.toString(lastLocation.getAccuracy()));
				} else {
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						throw new PreyException("Thread was intrrupted. Finishing Location NotifierAction", e);
					}
					if (i > 2) {
						return null;
					}
					i++;
				}
			}

			data.addDataListAll(parametersMap);

			ctx.stopService(intent);

		} catch (Exception e) {
			PreyLogger.e("Error causa:" + e.getMessage(), e);
			Map<String, String> parms = UtilJson.makeMapParam("get", "location", "failed", e.getMessage());
			PreyWebServices.getInstance().sendNotifyActionResultPreyHttp(ctx, parms);
		} finally {
			ctx.stopService(intent);
		}
		PreyLogger.i("data:"+data);
		return data;
	}

}
