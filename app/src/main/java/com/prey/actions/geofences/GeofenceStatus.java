package com.prey.actions.geofences;

/**
 * Created by oso on 19-10-15.
 */
public class GeofenceStatus {

    private int status;
    private GeofenceDto geofence;
    public static final int CREATE_OR_UPDATE_ZONE=1;

    public static final int DELETE_ZONE=2;


    public GeofenceStatus(){

    }
    public GeofenceStatus(int status,GeofenceDto geofence){
        this.status = status;
        this.geofence = geofence;
    }
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public GeofenceDto getGeofence() {
        return geofence;
    }

    public void setGeofence(GeofenceDto geofence) {
        this.geofence = geofence;
    }
}
