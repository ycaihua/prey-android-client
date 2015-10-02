package com.prey.actions.geofence;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;


import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.prey.PreyLogger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by oso on 29-09-15.
 */
public class GoogleGeofencingHelper   {


    private Context ctx = null;

    public GoogleGeofencingHelper(Context ctx) {
        this.ctx = ctx;
        buildGoogleApiClient(ctx);
    }


    private synchronized GoogleApiClient buildGoogleApiClient(Context ctx) {
        GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder(ctx)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {
                        PreyLogger.i("________________Connected to GoogleApiClient");
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        PreyLogger.i("________________Connection suspended");
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult connectionResult) {
                        PreyLogger.i("________________Connection failed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());
                    }
                })
                .addApi(LocationServices.API)
                .build();

        return mGoogleApiClient;
    }


    private PendingIntent getGeofencePendingIntent(Context ctx) {
        Intent intent = new Intent(ctx, GeofenceTransitionsIntentService.class);
        return PendingIntent.getService(ctx, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public GoogleApiClient connectGoogleApiClient(Context ctx) {
        GoogleApiClient mGoogleApiClient=null;
        try {
            mGoogleApiClient=buildGoogleApiClient(ctx);
            int i = 0;
            while (i < 50 && !mGoogleApiClient.isConnected()) {
                mGoogleApiClient.connect();
                i++;
                Thread.sleep(1000);
                if(i%10==0){
                    buildGoogleApiClient(ctx);
                }
                PreyLogger.i("___["+i+"] sleep");
            }
        } catch (Exception e) {
            PreyLogger.i("error:"+e.getMessage());
        }
        return mGoogleApiClient;
    }

    public void disconnectGoogleApiClient(GoogleApiClient mGoogleApiClient) {
        try {
            if (mGoogleApiClient != null) {
                mGoogleApiClient.disconnect();
            }
        } catch (Exception e) {
            PreyLogger.i("error:"+e.getMessage());
        }
    }


    public void startGeofences(final Context ctx, List<Geofence> geofences) {
        GoogleApiClient mGoogleApiClient=null;
        try {

            mGoogleApiClient=connectGoogleApiClient(ctx);

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
            if (mGoogleApiClient.isConnected()) {
                LocationServices.GeofencingApi.addGeofences(
                        mGoogleApiClient,
                        geofencingRequest,
                        getGeofencePendingIntent(ctx)
                ).setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        if (!status.isSuccess()) {
                            PreyLogger.i("________________"+ GeofenceErrorMessages.getErrorString(ctx, status.getStatusCode()));
                        }
                    }
                });
            }else{
                PreyLogger.i("not connect mGoogleApiClient 3");
            }
        } catch (Exception e) {
            PreyLogger.i("error,causa:"+e.getMessage());
        } finally {
            disconnectGoogleApiClient(mGoogleApiClient);
        }

    }

    public void stopGeofences(final Context ctx, List<String> geofences) {
        GoogleApiClient mGoogleApiClient=null;
        try {

            mGoogleApiClient=connectGoogleApiClient(ctx);



            ArrayList<String> requestIds = new ArrayList<String>();
            for (int i = 0; geofences != null && i < geofences.size(); i++) {

                requestIds.add( geofences.get(i));
            }

            LocationServices.GeofencingApi.removeGeofences(
                    mGoogleApiClient,
                    requestIds
            ).setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(Status status) {
                    if (!status.isSuccess()) {
                        PreyLogger.i("________________"+ GeofenceErrorMessages.getErrorString(ctx, status.getStatusCode()));
                    }
                }
            });
        } catch (Exception e) {
            PreyLogger.i("error,causa:"+e.getMessage());
        } finally {
            disconnectGoogleApiClient(mGoogleApiClient);
        }
    }


}
