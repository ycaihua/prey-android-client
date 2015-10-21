/*******************************************************************************
 * Created by Orlando Aliaga
 * Copyright 2015 Prey Inc. All rights reserved.
 * License: GPLv3
 * Full license at "/LICENSE"
 ******************************************************************************/
package com.prey.json.actions;

import android.content.Context;

import com.prey.PreyLogger;
import com.prey.actions.geofence.GeofecenceParse;
import com.prey.actions.geofence.Geofence;
import com.prey.actions.geofence.GeofenceDataSource;
import com.prey.actions.geofence.GeofenceStatus;
import com.prey.actions.geofence.GoogleGeofencingHelper;
import com.prey.actions.observer.ActionResult;
import com.prey.events.Event;
import com.prey.events.manager.EventThread;
import com.prey.json.UtilJson;
import com.prey.net.PreyHttpResponse;
import com.prey.net.PreyWebServices;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Geofencing  {



    public void start(Context ctx, List<ActionResult> list, JSONObject parameters) {
        GeofenceDataSource datasource=null;
        try {
            PreyLogger.d("started Geofencing");
            datasource=new GeofenceDataSource(ctx);
            PreyWebServices.getInstance().sendNotifyActionResultPreyHttp(ctx, UtilJson.makeMapParam("start", "geofencing", "started"));
            List<Geofence> listGeo=GeofecenceParse.getJSONFromUrl(ctx);
            PreyLogger.d("list Geo zone in size:"+(listGeo==null?0:listGeo.size()));
            GoogleGeofencingHelper helper=new GoogleGeofencingHelper(ctx);
            List<GeofenceStatus> listGeofenceStatus=helper.compare(listGeo,datasource);
            PreyLogger.d("list Geo status size:"+(listGeofenceStatus==null?0:listGeofenceStatus.size()));

            List<Geofence> listCreateUpdate=new ArrayList<Geofence>();
            List<Geofence> listDelete=new ArrayList<Geofence>();

            for (int i = 0; listGeofenceStatus != null && i < listGeofenceStatus.size(); i++) {
                GeofenceStatus geofenceStatus = (GeofenceStatus) listGeofenceStatus.get(i);
                switch (geofenceStatus.getStatus()){
                    case GeofenceStatus.CREATE_OR_UPDATE_ZONE:
                        listCreateUpdate.add(geofenceStatus.getGeofence());
                        break;
                    case GeofenceStatus.DELETE_ZONE:
                        listDelete.add(geofenceStatus.getGeofence());
                        break;
                    default: break;
                }
            }
            PreyLogger.d("list Geo create or update size:" + (listCreateUpdate==null?0:listCreateUpdate.size()));
            helper.startGeofences(ctx, listCreateUpdate, datasource);
            PreyLogger.d("list Geo delete size:" + (listDelete == null ? 0 : listDelete.size()));
            helper.stopGeofences(ctx, listDelete, datasource);
            PreyWebServices.getInstance().sendNotifyActionResultPreyHttp(ctx, UtilJson.makeMapParam("start", "geofencing", "stopped"));
            PreyLogger.d("stopped Geofencing");
        } catch (Exception e) {
            PreyWebServices.getInstance().sendNotifyActionResultPreyHttp(ctx, UtilJson.makeMapParam("start", "geofencing", "failed", e.getMessage()));
        } finally {
            if(datasource!=null){
                datasource.close();
            }
        }
    }

    public void stop(Context ctx, List<ActionResult> list, JSONObject parameters) {
        start(ctx,list,parameters);
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
            helper.startGeofences(ctx, list,datasource);
        } catch (Exception e) {
            PreyWebServices.getInstance().sendNotifyActionResultPreyHttp(ctx, UtilJson.makeMapParam("start", "geofencing", "failed", e.getMessage()));
        } finally {
            if(datasource!=null){
                datasource.close();
            }
        }
    }

}