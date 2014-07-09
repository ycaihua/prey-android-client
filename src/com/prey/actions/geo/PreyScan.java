package com.prey.actions.geo;

import java.util.ArrayList;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

 


 

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient; 
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationStatusCodes;

public class PreyScan implements GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener,LocationClient.OnAddGeofencesResultListener {

	private static PreyScan instance=null;
	
	public static PreyScan getInstance(Context context){
		if(instance==null){
			instance=new PreyScan(context);
		}
		return instance;
	} 
	
	private Context context;
    private LocationClient locationClient;
    private LocationRequest locationRequest;
	
	private PreyScan(Context context) {
		this.context=context;
	}
	
	
	  /**
     * Call this to start a scan - don't forget to stop the scan once it's done.
     * Note the scan will not start immediately, because it needs to establish a connection with Google's servers - you'll be notified of this at onConnected
     */
    public void startScan(){
    	 locationClient = new LocationClient(context, this, this);
         locationClient.connect();
    }

    public void stopScan(){
    	if (null != locationClient) {
            locationClient.disconnect();
        }
    }
	
	@Override
	public void onConnectionFailed(ConnectionResult result) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		locationRequest = LocationRequest.create();
        locationRequest.setInterval(5 * 50 * 1000);
        locationRequest.setFastestInterval(5 * 50 * 1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        Intent intent = new Intent(context, LocationIntentService.class);
        PendingIntent pendingIntent = PendingIntent.getService(context, 1, intent, 0);
        locationClient.requestLocationUpdates(locationRequest, pendingIntent);

        ArrayList<Store> storeList = getStoreList();
        if (null != storeList && storeList.size() > 0) {
            ArrayList<Geofence> geofenceList = new ArrayList<Geofence>();
            for (Store store : storeList) {
                float radius = (float) store.radius;
                Geofence geofence = new Geofence.Builder()
                        .setRequestId(store.id)
                        .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                        .setCircularRegion(store.latitude, store.longitude, radius)
                        .setExpirationDuration(Geofence.NEVER_EXPIRE)
                        .build();

                geofenceList.add(geofence);
            }

            PendingIntent geoFencePendingIntent = PendingIntent.getService(context, 0,
                    new Intent(context, GeofenceIntentService.class), PendingIntent.FLAG_UPDATE_CURRENT);
            locationClient.addGeofences(geofenceList, geoFencePendingIntent, this);
        }
		
	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
		
	}
	
	 private ArrayList<Store> getStoreList() {
	        ArrayList<Store> storeList = new ArrayList<Store>();
	       // for (int i = 0; i < 20; i++) {
	            Store store = new Store();
	            store.id = "OFICINA";
	            store.address = "Las Urbinas, #53";
	            store.latitude = -33.4223;
	            store.longitude = -70.6119;
	            store.radius = 100.0D;

	            storeList.add(store);
	            
	            Store store2 = new Store();
	            store2.id = "ESQUINA";
	            store2.address = "Esquina";
	            store2.latitude = -33.4226;
	            store2.longitude = -70.6111;
	            store2.radius = 100.0D;
	            
	            storeList.add(store2);
	            Store store3 = new Store();
	            store3.id = "SANFELIPE";
	            store3.address = "San Felipe";
	            store3.latitude = -33.752251;
	            store3.longitude = -70.720041;
	            store3.radius = 1000.0D;
	            storeList.add(store3);
	            
	            Store store4 = new Store();
	            store4.id = "TERMINAL";
	            store4.address = "Terminal";
	            store4.latitude = -33.445099;
	            store4.longitude = -70.658689;
	            store4.radius = 100.0D;
	            
	            storeList.add(store4);
	            
	            Store store5 = new Store();
	            store5.id = "PLAZAITALIA";
	            store5.address = "italia";
	            store5.latitude = -33.436647;
	            store5.longitude = -70.634130;
	            store5.radius = 100.0D;
	            
	            storeList.add(store5);
	      //  }

	        return storeList;
	    }


	@Override
	public void onAddGeofencesResult(int statusCode, String[] geofenceRequestIds) {
		  if (LocationStatusCodes.SUCCESS == statusCode) {
	            //todo check geofence status
	        } else {

	        }
		
	}

}
