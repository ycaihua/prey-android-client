package com.prey.actions.geofences;

import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;



import com.google.android.gms.location.LocationServices;


import com.google.android.gms.location.GeofencingRequest;

import com.prey.PreyLogger;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by oso on 29-09-15.
 */
public class GoogleGeofencingHelper implements
        ResultCallback<Status> {


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


    @Override
    public void onResult(Status status) {
        String toastMessage;
        // PRES 4
        if (status.isSuccess()) {
            toastMessage = "Success: We Are Monitoring Our Fences";
        } else {
            toastMessage = "Error: We Are NOT Monitoring Our Fences";
        }
        Toast.makeText(ctx, toastMessage,Toast.LENGTH_SHORT).show();
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

    public static final long GEOFENCE_EXPIRATION_IN_HOURS = 12;

    public static final long GEOFENCE_EXPIRATION_IN_MILLISECONDS =
            GEOFENCE_EXPIRATION_IN_HOURS * 60 * 60 * 1000;

    public void startGeofences(final Context ctx, List<GeofenceDto> geofences,GeofenceDataSource datasource) {
        GoogleApiClient mGoogleApiClient=null;
        try {

            mGoogleApiClient=connectGoogleApiClient(ctx);

            List<com.google.android.gms.location.Geofence> mGeofenceList = new ArrayList<com.google.android.gms.location.Geofence>();
            for (int i = 0; geofences != null && i < geofences.size(); i++) {
                GeofenceDto geo = geofences.get(i);
                PreyLogger.i("__[START]___________id:"+geo.getId()+" name:"+geo.getName()+" lat:"+geo.getLatitude()+" long:"+ geo.getLongitude()+" ra:"+geo.getRadius()+" type:"+geo.getType()+" expires:"+geo.getExpires());
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

                        .setRequestId("k"+geo.getId())

                        .setCircularRegion(geo.getLatitude(), geo.getLongitude(), geo.getRadius())

                        .setExpirationDuration(GEOFENCE_EXPIRATION_IN_MILLISECONDS)

                        .setTransitionTypes(transitionTypes)
                        .build());
            }

            GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
            builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_DWELL);
            builder.addGeofences(mGeofenceList);
            GeofencingRequest geofencingRequest = builder.build();

            if (mGoogleApiClient.isConnected()) {
                PreyLogger.i("---->isConnected");
                try {
                    PendingResult<Status> result = LocationServices.GeofencingApi.addGeofences(
                            mGoogleApiClient,
                            geofencingRequest,
                            PendingInstance.getInstance(ctx).getPendingIntent()
                    );
                    result.setResultCallback(this);
                }catch (Exception e ){
                    PreyLogger.i("error ---->isConnected:"+e.getMessage());
                }
            }else{
                PreyLogger.i("not connect mGoogleApiClient 3");
            }
        } catch (Exception e) {
            PreyLogger.i("error,causa:"+e.getMessage());
        } finally {
            disconnectGoogleApiClient(mGoogleApiClient);
        }

    }

    public void stopGeofences(final Context ctx, List<GeofenceDto> geofences,GeofenceDataSource datasource) {
        GoogleApiClient mGoogleApiClient=null;
        try {

            mGoogleApiClient=connectGoogleApiClient(ctx);



            ArrayList<String> requestIds = new ArrayList<String>();
            for (int i = 0; geofences != null && i < geofences.size(); i++) {
                PreyLogger.i("__[STOP]___________id:"+geofences.get(i).getId());
                requestIds.add(geofences.get(i).getId());
            }

            PendingResult<Status> result = LocationServices.GeofencingApi.removeGeofences(
                    mGoogleApiClient,
                    requestIds
            );
            result.setResultCallback(this);

        } catch (Exception e) {
            PreyLogger.i("error,causa:"+e.getMessage());
        } finally {
            disconnectGoogleApiClient(mGoogleApiClient);
        }
    }


    public List<GeofenceStatus> compare(List<GeofenceDto> geofences,GeofenceDataSource datasource){
        List<GeofenceStatus> listStatus=new ArrayList<>();
        List<GeofenceDto> listAllBD=datasource.getAllGeofences();
        PreyLogger.d("listAllBD size;"+(listAllBD==null?0:listAllBD.size()));
        for (int i = 0; listAllBD != null && i < listAllBD.size(); i++) {
            boolean include=false;
            for (int j = 0; geofences != null && j < geofences.size(); j++) {
                if (listAllBD.get(i).getId().equals(geofences.get(j).getId())){
                    include=true;
                    break;
                }
            }
            if(!include){
                listStatus.add(new GeofenceStatus(GeofenceStatus.DELETE_ZONE,listAllBD.get(i)));
            }
        }

        for (int i = 0; geofences != null && i < geofences.size(); i++) {
            GeofenceDto geofence=geofences.get(i);
            GeofenceDto geofenceBD=datasource.getGeofences(geofence.getId());
            if (geofenceBD!=null ){
                if(geofence.getId().equals(geofenceBD.getId()) &&!geofence.equals(geofenceBD)) {
                    listStatus.add(new GeofenceStatus(GeofenceStatus.CREATE_OR_UPDATE_ZONE,geofence));
                }
            }else{
                listStatus.add(new GeofenceStatus(GeofenceStatus.CREATE_OR_UPDATE_ZONE,geofence));
            }
        }
        return listStatus;
    }


}
