package com.prey.actions.geofence;

/**
 * Created by oso on 19-10-15.
 */
public class GeofenceStatus {

    private int status;
    private Geofence geofence;
    public static final int CREATE_OR_UPDATE_ZONE=1;

    public static final int DELETE_ZONE=2;


    public GeofenceStatus(){

    }
    public GeofenceStatus(int status,Geofence geofence){
        this.status = status;
        this.geofence = geofence;
    }
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Geofence getGeofence() {
        return geofence;
    }

    public void setGeofence(Geofence geofence) {
        this.geofence = geofence;
    }
}
