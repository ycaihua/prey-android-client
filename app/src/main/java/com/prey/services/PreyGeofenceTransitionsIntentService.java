/*******************************************************************************
 * Created by Orlando Aliaga
 * Copyright 2015 Prey Inc. All rights reserved.
 * License: GPLv3
 * Full license at "/LICENSE"
 ******************************************************************************/
package com.prey.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.prey.PreyConfig;
import com.prey.PreyLogger;
import com.prey.R;
import com.prey.actions.HttpDataService;
import com.prey.actions.location.LocationUtil;
import com.prey.events.Event;
import com.prey.events.manager.EventThread;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PreyGeofenceTransitionsIntentService extends IntentService {

    public PreyGeofenceTransitionsIntentService() {
        super(PreyConfig.TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        PreyLogger.i("PreyGeofenceTransitionsIntentService intent");
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            String errorMessage = PreyGeofenceErrorMessages.getErrorString(this,
                    geofencingEvent.getErrorCode());
            PreyLogger.i(errorMessage);
            return;
        }

        HttpDataService location = LocationUtil.dataLocation(this);

        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
                geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();
            notifyGeofenceTransition(
                    this,
                    geofenceTransition,
                    triggeringGeofences,
                    location
            );
        } else {
            PreyLogger.i(getString(R.string.geofence_transition_invalid_type, geofenceTransition));
        }
    }

    private void notifyGeofenceTransition(
            Context context,
            int geofenceTransition,
            List<Geofence> triggeringGeofences, HttpDataService location) {
        ArrayList triggeringGeofencesIdsList = new ArrayList();
        for (Geofence geofence : triggeringGeofences) {
            triggeringGeofencesIdsList.add(geofence.getRequestId());
            try {
                Event event = new Event();
                if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER)
                    event.setName("geofencing_in");
                else
                    event.setName("geofencing_out");
                JSONObject info = new JSONObject();
                info.put("id", geofence.getRequestId());
                info.put("lat", location.getDataList().get(LocationUtil.LAT));
                info.put("lng", location.getDataList().get(LocationUtil.LNG));
                info.put("accuracy", location.getDataList().get(LocationUtil.ACC));
                info.put("method", location.getDataList().get(LocationUtil.METHOD));
                event.setInfo(info.toString());
                JSONObject jsonObjectStatus = new JSONObject();
                new EventThread(this, event, jsonObjectStatus).start();
            } catch (Exception e) {
            }
        }

    }


}
