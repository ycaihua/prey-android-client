/*******************************************************************************
 * Created by Orlando Aliaga
 * Copyright 2015 Prey Inc. All rights reserved.
 * License: GPLv3
 * Full license at "/LICENSE"
 ******************************************************************************/
package com.prey.json.actions;

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
import com.prey.R;
import com.prey.actions.observer.ActionResult;
import com.prey.events.Event;
import com.prey.events.manager.EventThread;
import com.prey.json.UtilJson;
import com.prey.net.PreyWebServices;
import com.prey.services.PreyGeofenceErrorMessages;
import com.prey.services.PreyGeofenceTransitionsIntentService;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Geofencing implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ResultCallback<Status> {

    private Context ctx;
    private GoogleApiClient mGoogleApiClient = null;

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(ctx)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }


    private void connectGoogleApiClient() {
        try {
            int i = 0;
            while (i < 10 && !mGoogleApiClient.isConnected()) {
                mGoogleApiClient.connect();
                i++;
                Thread.sleep(1000);
            }
        } catch (Exception e) {
        }
    }

    private void disconnectGoogleApiClient() {
        try {
            if (mGoogleApiClient != null) {
                mGoogleApiClient.disconnect();
            }
        } catch (Exception e) {
        }
    }

    public void start(Context ctx, List<ActionResult> list, JSONObject parameters) {
        try {
            this.ctx = ctx;
            PreyWebServices.getInstance().sendNotifyActionResultPreyHttp(ctx, UtilJson.makeMapParam("start", "geofencing", "started"));
            buildGoogleApiClient();
            connectGoogleApiClient();
            ArrayList<com.google.android.gms.location.Geofence> mGeofenceList = new ArrayList<com.google.android.gms.location.Geofence>();
            JSONArray locations = null;
            locations = parameters.getJSONArray("locations");
            for (int i = 0; locations != null && i < locations.length(); i++) {
                JSONObject location = (JSONObject) locations.get(i);
                String id = location.getString("id");
                double latitude = Double.parseDouble(location.getString("lat"));
                double longitude = Double.parseDouble(location.getString("lng"));
                float radius = Float.parseFloat(location.getString("radius"));
                String type = location.getString("type");
                int expires = Integer.parseInt(location.getString("expires"));

                PreyLogger.i("start id:" + id + " lat:" + latitude + " lon:" + longitude + " radius:" + radius + " type:" + type + " expires:" + expires);
                if(expires>0){
                    expires=expires*1000;
                }

                int transitionTypes;
                if ("in".equals(type)) {
                    transitionTypes = com.google.android.gms.location.Geofence.GEOFENCE_TRANSITION_ENTER;
                } else {
                    if ("out".equals(type)) {
                        transitionTypes = com.google.android.gms.location.Geofence.GEOFENCE_TRANSITION_EXIT;
                    } else {
                        transitionTypes = com.google.android.gms.location.Geofence.GEOFENCE_TRANSITION_ENTER |
                                com.google.android.gms.location.Geofence.GEOFENCE_TRANSITION_EXIT;
                    }
                }
                mGeofenceList.add(new com.google.android.gms.location.Geofence.Builder()
                        .setRequestId(id)
                        .setCircularRegion(latitude, longitude, radius)
                        .setExpirationDuration(expires)
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
                        getGeofencePendingIntent()
                ).setResultCallback(this);
            }
            PreyWebServices.getInstance().sendNotifyActionResultPreyHttp(ctx, UtilJson.makeMapParam("start", "geofencing", "stopped"));
        } catch (Exception e) {
            PreyWebServices.getInstance().sendNotifyActionResultPreyHttp(ctx, UtilJson.makeMapParam("start", "geofencing", "failed", e.getMessage()));
        } finally {
            disconnectGoogleApiClient();
        }
    }


    public void stop(Context ctx, List<ActionResult> list, JSONObject parameters) {
        try {
            this.ctx = ctx;
            PreyWebServices.getInstance().sendNotifyActionResultPreyHttp(ctx, UtilJson.makeMapParam("stop", "geofencing", "started"));
            buildGoogleApiClient();
            connectGoogleApiClient();

            ArrayList<String> geofenceRequestIds = new ArrayList<String>();
            JSONArray locations = null;
            locations = parameters.getJSONArray("locations");
            for (int i = 0; locations != null && i < locations.length(); i++) {
                JSONObject location = (JSONObject) locations.get(i);
                String id = location.getString("id");
                geofenceRequestIds.add(id);
            }

            LocationServices.GeofencingApi.removeGeofences(
                    mGoogleApiClient,
                    geofenceRequestIds
            ).setResultCallback(this);

            for (int i = 0; geofenceRequestIds != null && i < geofenceRequestIds.size(); i++) {
                String id = geofenceRequestIds.get(i);
                Event event = new Event();
                event.setName("geofencing_stop");
                JSONObject info = new JSONObject();
                info.put("id", id);
                PreyLogger.i("stop id:" + id);
                event.setInfo(info.toString());
                JSONObject jsonObjectStatus = new JSONObject();
                new EventThread(ctx, event, jsonObjectStatus).start();
            }
            PreyWebServices.getInstance().sendNotifyActionResultPreyHttp(ctx, UtilJson.makeMapParam("stop", "geofencing", "stopped"));
        } catch (Exception e) {
            PreyWebServices.getInstance().sendNotifyActionResultPreyHttp(ctx, UtilJson.makeMapParam("stop", "geofencing", "failed", e.getMessage()));
        } finally {
            disconnectGoogleApiClient();
        }
    }

    private PendingIntent getGeofencePendingIntent() {
        Intent intent = new Intent(ctx, PreyGeofenceTransitionsIntentService.class);
        return PendingIntent.getService(ctx, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        PreyLogger.i("Connected to GoogleApiClient");
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        PreyLogger.i("Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

    @Override
    public void onConnectionSuspended(int cause) {
        PreyLogger.i("Connection suspended");
    }

    public void onResult(Status status) {
        if (!status.isSuccess()) {
            PreyLogger.i(PreyGeofenceErrorMessages.getErrorString(ctx, status.getStatusCode()));
        }
    }

}
