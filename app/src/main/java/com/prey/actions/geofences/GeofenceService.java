package com.prey.actions.geofences;

import android.app.IntentService;
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
import com.prey.json.UtilJson;
import com.prey.net.PreyWebServices;

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
        PreyLogger.i("___________[GeofenceService onHandleIntent]____________");
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
        PreyLogger.i("___________[GeofenceService requestLocationInternal]____________");
        PreyLogger.i("_______________________" + ACTION_REQUEST_LOCATION);
        GoogleApiClient googleApiClient =null;

        try {

            googleApiClient = new GoogleApiClient.Builder(this)
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
                //GeofenceDataSource datasource=new GeofenceDataSource(this);
                //datasource.open();
                //List<Geofence> geofences = datasource.getAllGeofences();
                //datasource.close();
                List<GeofenceDto> geofences = GeofecenceParse.getJSONFromUrl(this);

                List<com.google.android.gms.location.Geofence> mGeofenceList = new ArrayList<com.google.android.gms.location.Geofence>();
                String info2="[";
                for (int i = 0; geofences != null && i < geofences.size(); i++) {
                    GeofenceDto geo = geofences.get(i);
                    PreyLogger.i("======= id:" + geo.getId() + " lat:" + geo.getLatitude() + " long:" + geo.getLongitude() + " ra:" + geo.getRadius() + " type:" + geo.getType());
                    int transitionTypes;
                    if ("in".equals(geo.getType())) {
                        transitionTypes = Geofence.GEOFENCE_TRANSITION_ENTER;
                    } else {
                        if ("out".equals(geo.getType())) {
                            transitionTypes = Geofence.GEOFENCE_TRANSITION_EXIT;
                        } else {
                            transitionTypes = Geofence.GEOFENCE_TRANSITION_ENTER |
                                    Geofence.GEOFENCE_TRANSITION_EXIT;
                        }
                    }
                    info2=geo.getId();
                    if(i+1 < geofences.size()){
                        info2=",";
                    }
                    mGeofenceList.add(new com.google.android.gms.location.Geofence.Builder()
                            .setRequestId(geo.getId())
                            .setCircularRegion(geo.getLatitude(), geo.getLongitude(), geo.getRadius())
                            .setExpirationDuration(geo.getExpires())
                            .setTransitionTypes(transitionTypes)
                            .build());
                }
                info2="]";
                PreyLogger.i("info2:"+info2);
                PreyWebServices.getInstance().sendNotifyActionResultPreyHttp(getApplication(), UtilJson.makeMapParam("start", "geofencing", "started",info2));

                GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
                builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
                builder.addGeofences(mGeofenceList);
                GeofencingRequest geofencingRequest = builder.build();
                LocationServices.GeofencingApi.addGeofences(
                        googleApiClient,
                        geofencingRequest,
                        PendingInstance.getInstance(this).getPendingIntent()
                ).setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        PreyLogger.i("___________________");
                        PreyLogger.i("___________________");
                        PreyLogger.i("___________________");
                        PreyLogger.i("status:" + status);



                        if (!status.isSuccess()) {
                            PreyLogger.i("________________" + GeofenceErrorMessages.getErrorString(getApplicationContext(), status.getStatusCode()));
                        }
                    }
                });
                PreyWebServices.getInstance().sendNotifyActionResultPreyHttp(getApplication(), UtilJson.makeMapParam("start", "geofencing", "stopped"));


            } else {
                PreyLogger.i(String.format(GOOGLE_API_CLIENT_ERROR_MSG,
                        connectionResult.getErrorCode()));
            }
        }catch(Exception e){
            PreyLogger.e("Error, causa:"+e.getMessage(),e);

            PreyWebServices.getInstance().sendNotifyActionResultPreyHttp(getApplication(), UtilJson.makeMapParam("start", "geofencing", "failed", e.getMessage()));
        }finally {
            if (googleApiClient!=null){
                googleApiClient.disconnect();
            }
        }
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

