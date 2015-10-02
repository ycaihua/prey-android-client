/*******************************************************************************
 * Created by Orlando Aliaga
 * Copyright 2015 Prey Inc. All rights reserved.
 * License: GPLv3
 * Full license at "/LICENSE"
 ******************************************************************************/
package com.prey.json.actions;

import android.content.Context;

import com.prey.PreyLogger;
import com.prey.actions.geofence.Geofence;
import com.prey.actions.geofence.GeofenceDataSource;
import com.prey.actions.geofence.GoogleGeofencingHelper;
import com.prey.actions.observer.ActionResult;
import com.prey.events.Event;
import com.prey.events.manager.EventThread;
import com.prey.json.UtilJson;
import com.prey.net.PreyWebServices;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Geofencing  {

    public void start(Context ctx, List<ActionResult> list, JSONObject parameters) {
        GeofenceDataSource datasource=null;
        try {
            datasource=new GeofenceDataSource(ctx);
            PreyWebServices.getInstance().sendNotifyActionResultPreyHttp(ctx, UtilJson.makeMapParam("start", "geofencing", "started"));
            JSONArray locations = null;
            locations = parameters.getJSONArray("locations");
            datasource.open();
            List<Geofence>lista=new ArrayList<Geofence>();
            for (int i = 0; locations != null && i < locations.length(); i++) {
                JSONObject location = (JSONObject) locations.get(i);
                Geofence geofence=new Geofence();
                geofence.setId(location.getString("id"));
                geofence.setName(location.getString("id"));
                geofence.setLatitude(Double.parseDouble(location.getString("lat")));
                geofence.setLongitude(Double.parseDouble(location.getString("lng")));
                geofence.setRadius(Float.parseFloat(location.getString("radius")));
                geofence.setType(location.getString("type"));
                geofence.setExpires(Integer.parseInt(location.getString("expires")));
                if(geofence.getExpires()>0){
                    geofence.setExpires(1000*100);
                }
                datasource.createGeofence(geofence);
                lista.add(geofence);
            }
            GoogleGeofencingHelper helper=new GoogleGeofencingHelper(ctx);
            helper.startGeofences(ctx,lista);
            PreyWebServices.getInstance().sendNotifyActionResultPreyHttp(ctx, UtilJson.makeMapParam("start", "geofencing", "stopped"));
        } catch (Exception e) {
            PreyWebServices.getInstance().sendNotifyActionResultPreyHttp(ctx, UtilJson.makeMapParam("start", "geofencing", "failed", e.getMessage()));
        } finally {
            if(datasource!=null){
                datasource.close();
            }
        }
    }


    public void stop(Context ctx, List<ActionResult> list, JSONObject parameters) {
        GeofenceDataSource datasource=null;
        try {
            datasource=new GeofenceDataSource(ctx);
            PreyWebServices.getInstance().sendNotifyActionResultPreyHttp(ctx, UtilJson.makeMapParam("stop", "geofencing", "started"));
            ArrayList<String> requestIds = new ArrayList<String>();
            JSONArray locations = null;
            locations = parameters.getJSONArray("locations");
            for (int i = 0; locations != null && i < locations.length(); i++) {
                JSONObject location = (JSONObject) locations.get(i);
                String id = location.getString("id");
                requestIds.add(id);
                datasource.deleteGeofence(id);
            }
            GoogleGeofencingHelper helper=new GoogleGeofencingHelper(ctx);
            helper.stopGeofences(ctx,requestIds);
            datasource.open();
            for (int i = 0; requestIds != null && i < requestIds.size(); i++) {
                String id = requestIds.get(i);
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

            if(datasource!=null){
                datasource.close();
            }
        }
    }

    public static void run(Context ctx) {
        GeofenceDataSource datasource=null;
        try {


            datasource=new GeofenceDataSource(ctx);
            datasource.open();
            List<Geofence> list = datasource.getAllGeofences();
            PreyLogger.i("list:" + list.size());
            for(int i=0;list!=null&&i<list.size();i++){
                Geofence geofence=(Geofence)list.get(i);
                PreyLogger.i(geofence.toString());
            }
            GoogleGeofencingHelper helper=new GoogleGeofencingHelper(ctx);
            helper.startGeofences(ctx, list);
        } catch (Exception e) {
            PreyWebServices.getInstance().sendNotifyActionResultPreyHttp(ctx, UtilJson.makeMapParam("start", "geofencing", "failed", e.getMessage()));
        } finally {
            if(datasource!=null){
                datasource.close();
            }
        }
    }

}