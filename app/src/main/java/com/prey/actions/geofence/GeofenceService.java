package com.prey.actions.geofence;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.support.v4.content.LocalBroadcastManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.*;
import com.prey.PreyConfig;
import com.prey.PreyLogger;
import com.prey.actions.location.PreyLocation;
import com.prey.actions.location.PreyLocationManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by oso on 30-09-15.
 */
public class GeofenceService extends IntentService {


    public static final String ACTION_LOCATION_UPDATED = "location_updated";
    public static final String ACTION_REQUEST_LOCATION = "request_location";


    public static IntentFilter getLocationUpdatedIntentFilter() {
        return new IntentFilter(GeofenceService.ACTION_LOCATION_UPDATED);
    }





    public static void requestLocation(Context context) {
        Intent intent = new Intent(context, GeofenceService.class);
        intent.setAction(GeofenceService.ACTION_REQUEST_LOCATION);
        context.startService(intent);
    }




    public GeofenceService() {
        super(PreyConfig.TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String action = intent != null ? intent.getAction() : null;
        PreyLogger.i("UtilityService action:"+action);

        if (ACTION_REQUEST_LOCATION.equals(action)) {
            requestLocationInternal();
        } else if (ACTION_LOCATION_UPDATED.equals(action)) {
            locationUpdated(intent);
        }else{
            requestLocationInternal();
        }
    }


    public static final int GOOGLE_API_CLIENT_TIMEOUT_S = 20; // 10 seconds
    public static final String GOOGLE_API_CLIENT_ERROR_MSG =
            "Failed to connect to GoogleApiClient (error code = %d)";


    /**
     * Called when a location update is requested
     */
    private void requestLocationInternal() {
        PreyLogger.i("___________XXX____________");
        PreyLogger.i("_______________________"+ACTION_REQUEST_LOCATION);
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .build();

        // It's OK to use blockingConnect() here as we are running in an
        // IntentService that executes work on a separate (background) thread.
        ConnectionResult connectionResult = googleApiClient.blockingConnect(
                GOOGLE_API_CLIENT_TIMEOUT_S, TimeUnit.SECONDS);

        if (connectionResult.isSuccess() && googleApiClient.isConnected()) {
            /*
            Intent locationUpdatedIntent = new Intent(this, UtilityService.class);
            locationUpdatedIntent.setAction(ACTION_LOCATION_UPDATED);

            // Send last known location out first if available
            Location location = FusedLocationApi.getLastLocation(googleApiClient);
            if (location != null) {
                Intent lastLocationIntent = new Intent(locationUpdatedIntent);
                lastLocationIntent.putExtra(
                        FusedLocationProviderApi.KEY_LOCATION_CHANGED, location);
                startService(lastLocationIntent);
            }

            // Request new location
            LocationRequest mLocationRequest = new LocationRequest()
                    .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
            FusedLocationApi.requestLocationUpdates(
                    googleApiClient, mLocationRequest,
                    PendingIntent.getService(this, 0, locationUpdatedIntent, 0));


            */
            GeofenceDataSource datasource=new GeofenceDataSource(this);
            datasource.open();
            List<Geofence> geofences = datasource.getAllGeofences();
            datasource.close();

            List<com.google.android.gms.location.Geofence> mGeofenceList = new ArrayList<com.google.android.gms.location.Geofence>();
            for (int i = 0; geofences != null && i < geofences.size(); i++) {
                Geofence geo = geofences.get(i);
                PreyLogger.i("id:"+geo.getId()+" lat:"+geo.getLatitude()+" long:"+ geo.getLongitude()+" ra:"+geo.getRadius()+" type:"+geo.getType());
                int transitionTypes;
                if ("in".equals(geo.getType())) {
                    transitionTypes = com.google.android.gms.location.Geofence.GEOFENCE_TRANSITION_ENTER;
                } else {
                    if ("out".equals(geo.getType())) {
                        transitionTypes = com.google.android.gms.location.Geofence.GEOFENCE_TRANSITION_EXIT;
                    } else {
                        transitionTypes = com.google.android.gms.location.Geofence.GEOFENCE_TRANSITION_ENTER |
                                com.google.android.gms.location.Geofence.GEOFENCE_TRANSITION_EXIT;
                    }
                }

                mGeofenceList.add(new com.google.android.gms.location.Geofence.Builder()
                        .setRequestId(geo.getId())
                        .setCircularRegion(geo.getLatitude(), geo.getLongitude(), geo.getRadius())
                        .setExpirationDuration(geo.getExpires())
                        .setTransitionTypes(transitionTypes)
                        .build());
            }
            GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
            builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
            builder.addGeofences(mGeofenceList);
            GeofencingRequest geofencingRequest = builder.build();
            LocationServices.GeofencingApi.addGeofences(
                    googleApiClient,
                    geofencingRequest,
                    getGeofencePendingIntent(this)
            ).setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(Status status) {
                    if (!status.isSuccess()) {
                        PreyLogger.i("________________"+ GeofenceErrorMessages.getErrorString(getApplicationContext(), status.getStatusCode()));
                    }
                }
            });

            googleApiClient.disconnect();

        } else {
            PreyLogger.i(String.format(GOOGLE_API_CLIENT_ERROR_MSG,
                    connectionResult.getErrorCode()));
        }
    }

    private PendingIntent getGeofencePendingIntent(Context ctx) {
        Intent intent = new Intent(ctx, GeofenceTransitionsIntentService.class);
        return PendingIntent.getService(ctx, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public static final int MOBILE_NOTIFICATION_ID = 100;
    /**
     * Called when the location has been updated
     */
    private void locationUpdated(Intent intent) {
        PreyLogger.i(ACTION_LOCATION_UPDATED);
        PreyLogger.i("________________________");
        // Extra new location
        Location location =
                intent.getParcelableExtra(FusedLocationProviderApi.KEY_LOCATION_CHANGED);

        if (location != null) {

            PreyLocationManager.getInstance(getApplicationContext()).setLastLocation(new PreyLocation(location));

            PreyLogger.i("___________location lat"+location.getLatitude()+" lng:"+location.getLongitude());

            // Send a local broadcast so if an Activity is open it can respond
            // to the updated location
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        }
    }





}

