package com.prey.actions.location;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
 
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

public class PreyLocationUpdate implements LocationListener, GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener {

	
    // Milliseconds per second
    public static final int MILLISECONDS_PER_SECOND = 1000;

    // The update interval
    public static final int UPDATE_INTERVAL_IN_SECONDS = 5;

    // A fast interval ceiling
    public static final int FAST_CEILING_IN_SECONDS = 1;

    // Update interval in milliseconds
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS =
            MILLISECONDS_PER_SECOND * UPDATE_INTERVAL_IN_SECONDS;

    // A fast ceiling of update intervals, used when the app is visible
    public static final long FAST_INTERVAL_CEILING_IN_MILLISECONDS =
            MILLISECONDS_PER_SECOND * FAST_CEILING_IN_SECONDS;
    
	private static PreyLocationUpdate instance = null;

	public static PreyLocationUpdate getInstance(Context context) {
		if (instance == null) {
			instance = new PreyLocationUpdate(context);
		}
		return instance;
	}

	private Context context;
	private LocationClient locationClient;
	private LocationRequest locationRequest;

	private Location location=null;
	
	public void startScan() {
		location=null;
		locationClient = new LocationClient(context, this, this);
		locationClient.connect();
	}

	public void stopScan() {
		
		stopPeriodicUpdates();
		if (null != locationClient) {
			locationClient.disconnect();
		}
	}
	
	
    private void startPeriodicUpdates() {

    	locationClient.requestLocationUpdates(locationRequest, this);
        
    }

    /**
     * In response to a request to stop updates, send a request to
     * Location Services
     */
    private void stopPeriodicUpdates() {
    	locationClient.removeLocationUpdates(this);
       
    }

	private PreyLocationUpdate(Context context) {
		this.context = context;
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onConnected(Bundle connectionHint) {
		// TODO Auto-generated method stub
		  // Create a new global location parameters object
		locationRequest = LocationRequest.create();

        /*
         * Set the update interval
         */
		locationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

        // Use high accuracy
		locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // Set the interval ceiling to one minute
		locationRequest.setFastestInterval(FAST_INTERVAL_CEILING_IN_MILLISECONDS);

		startPeriodicUpdates();

	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		this.location=location;
	}

	public Location getLocation() {
		return location;
	}
 

}
