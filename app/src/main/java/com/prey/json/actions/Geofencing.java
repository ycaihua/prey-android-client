/*******************************************************************************
 * Created by Orlando Aliaga
 * Copyright 2015 Prey Inc. All rights reserved.
 * License: GPLv3
 * Full license at "/LICENSE"
 ******************************************************************************/
package com.prey.json.actions;

import android.content.Context;
import android.content.Intent;

import com.prey.PreyLogger;
import com.prey.actions.geofences.GeofecenceParse;
import com.prey.actions.geofences.GeofenceDto;
import com.prey.actions.geofences.GeofenceDataSource;
import com.prey.actions.geofences.GeofenceService;
import com.prey.actions.geofences.GeofenceStatus;
import com.prey.actions.geofences.GoogleGeofencingHelper;
import com.prey.actions.observer.ActionResult;
import com.prey.events.Event;
import com.prey.events.manager.EventThread;
import com.prey.json.UtilJson;
import com.prey.net.PreyWebServices;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Geofencing  {


    public void start(Context ctx, List<ActionResult> list, JSONObject parameters) {
        Intent intent = new Intent(ctx, GeofenceService.class);
        intent.setAction(GeofenceService.ACTION_REQUEST_LOCATION);
        ctx.startService(intent);
    }
    public void start2(Context ctx, List<ActionResult> list, JSONObject parameters) {
        GeofenceDataSource datasource=null;
        try {
            PreyLogger.d("started Geofencing");
            datasource=new GeofenceDataSource(ctx);
            PreyWebServices.getInstance().sendNotifyActionResultPreyHttp(ctx, UtilJson.makeMapParam("start", "geofencing", "started"));
            List<GeofenceDto> listGeo=GeofecenceParse.getJSONFromUrl(ctx);
            PreyLogger.d("list Geo zone in size:"+(listGeo==null?0:listGeo.size()));
            GoogleGeofencingHelper helper=new GoogleGeofencingHelper(ctx);
            List<GeofenceStatus> listGeofenceStatus=helper.compare(listGeo,datasource);
            PreyLogger.d("list Geo status size:"+(listGeofenceStatus==null?0:listGeofenceStatus.size()));

            List<GeofenceDto> listCreateUpdate=new ArrayList<GeofenceDto>();
            List<GeofenceDto> listDelete=new ArrayList<GeofenceDto>();

            for (int i = 0; listGeofenceStatus != null && i < listGeofenceStatus.size(); i++) {
                GeofenceStatus geofenceStatus = (GeofenceStatus) listGeofenceStatus.get(i);
                PreyLogger.d("status:"+geofenceStatus.getStatus());
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
            PreyLogger.d("list Geo create or update size:" + (listCreateUpdate == null ? 0 : listCreateUpdate.size()));
            if(listCreateUpdate!=null&&listCreateUpdate.size()>0) {
                helper.startGeofences(ctx, listCreateUpdate, datasource);
            }
            PreyLogger.d("list Geo delete size:" + (listDelete == null ? 0 : listDelete.size()));
            if(listDelete!=null&&listDelete.size()>0) {
                helper.stopGeofences(ctx, listDelete, datasource);
            }
            PreyWebServices.getInstance().sendNotifyActionResultPreyHttp(ctx, UtilJson.makeMapParam("start", "geofencing", "stopped"));
            PreyLogger.d("stopped Geofencing");
        } catch (Exception e) {
            PreyLogger.e("error geo:"+e.getMessage(),e);
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
            List<GeofenceDto> list = datasource.getAllGeofences();
            PreyLogger.i("list:" + list.size());
            for(int i=0;list!=null&&i<list.size();i++){
                GeofenceDto geofence=(GeofenceDto)list.get(i);
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