package com.prey.actions.geofence;

/**
 * Created by oso on 28-09-15.
 */
public class Geofence {
    private String id;


    private String name ;
    private double latitude;
    private double longitude;
    private float radius;
    private String type;
    private int expires;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getExpires() {
        return expires;
    }

    public void setExpires(int expires) {
        this.expires = expires;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString(){
        StringBuffer sb=new StringBuffer();
        sb.append(" name:").append(name);
        sb.append(" latitude:").append(latitude);
        sb.append(" longitude:").append(longitude);
        sb.append(" radius:").append(radius);
        sb.append(" type:").append(type);
        sb.append(" expires:").append(expires);
        return sb.toString();
    }

    @Override
    public boolean equals(Object other){
        if (other == null) return false;
        if (other == this) return true;
        if (!(other instanceof Geofence))return false;
        Geofence otherMyClass = (Geofence)other;
        if(!this.getId().equals(otherMyClass.getId())) return false;
        if(!this.getName().equals(otherMyClass.getName())) return false;
        if(this.getLatitude()!=otherMyClass.getLatitude()) return false;
        if(this.getLongitude()!=otherMyClass.getLongitude()) return false;
        if(this.getRadius()!=otherMyClass.getRadius()) return false;
        if(this.getType()!=otherMyClass.getType()) return false;
        return true;
    }
}
