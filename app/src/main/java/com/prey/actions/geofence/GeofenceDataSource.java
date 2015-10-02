package com.prey.actions.geofence;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.prey.PreyLogger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by oso on 28-09-15.
 */
public class GeofenceDataSource {

    // Database fields
    private SQLiteDatabase database;
    private GeofenceOpenHelper dbHelper;
    private String[] allColumns = new String[]{
            GeofenceOpenHelper.COLUMN_ID,
            GeofenceOpenHelper.COLUMN_NAME,
            GeofenceOpenHelper.COLUMN_LATITUDE,
            GeofenceOpenHelper.COLUMN_LONGITUDE,
            GeofenceOpenHelper.COLUMN_RADIUS,
            GeofenceOpenHelper.COLUMN_TYPE,
            GeofenceOpenHelper.COLUMN_EXPIRES
    };
    public GeofenceDataSource(Context context) {
        dbHelper = new GeofenceOpenHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public void createGeofence(Geofence geofence) {
        ContentValues values = new ContentValues();
        values.put(GeofenceOpenHelper.COLUMN_ID, geofence.getId());
        values.put(GeofenceOpenHelper.COLUMN_NAME, geofence.getName());
        values.put(GeofenceOpenHelper.COLUMN_LATITUDE, geofence.getLatitude());
        values.put(GeofenceOpenHelper.COLUMN_LONGITUDE, geofence.getLongitude());
        values.put(GeofenceOpenHelper.COLUMN_RADIUS, geofence.getRadius());
        values.put(GeofenceOpenHelper.COLUMN_TYPE, geofence.getType());
        values.put(GeofenceOpenHelper.COLUMN_EXPIRES, geofence.getExpires());
        try {
            PreyLogger.i("___geo:" + geofence.toString());
            database.insert(GeofenceOpenHelper.GEOFENCE_TABLE_NAME, null,values);
        }catch (Exception e){
            try {
                String selection = GeofenceOpenHelper.COLUMN_ID + " = ?";
                String[] selectionArgs = { geofence.getId() };
                database.update(GeofenceOpenHelper.GEOFENCE_TABLE_NAME, values, selection, selectionArgs);
            }catch (Exception e1) {
                PreyLogger.i("error db:" + e1.getMessage());
            }
        }
    }

    public void deleteGeofence(String id) {
        String selection = GeofenceOpenHelper.COLUMN_ID + " = ?";
        String[] selectionArgs = { id };
        database.delete(GeofenceOpenHelper.GEOFENCE_TABLE_NAME, selection,selectionArgs);
    }

    public List<Geofence> getAllGeofences() {
        List<Geofence> geofences = new ArrayList<>();
        Cursor cursor = database.query(GeofenceOpenHelper.GEOFENCE_TABLE_NAME,
                allColumns, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Geofence geofence = cursorToGeofence(cursor);
            geofences.add(geofence);
            cursor.moveToNext();
        }
        cursor.close();
        return geofences;
    }

    public Geofence getGeofences(String id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Geofence geofence = null;

        String selection = GeofenceOpenHelper.COLUMN_ID + " = ?";

        String[] selectionArgs = { id };

        Cursor cursor = database.query(GeofenceOpenHelper.GEOFENCE_TABLE_NAME,
                allColumns, selection, selectionArgs, null, null, null);
        cursor.moveToFirst();
        if (!cursor.isAfterLast()) {
            geofence = cursorToGeofence(cursor);

        }
        cursor.close();
        return geofence;
    }


    private Geofence cursorToGeofence(Cursor cursor) {
        Geofence geofence = new Geofence();
        geofence.setId(cursor.getString(0));
        geofence.setName(cursor.getString(1));
        geofence.setLatitude(cursor.getDouble(2));
        geofence.setLongitude(cursor.getDouble(3));
        geofence.setRadius(cursor.getFloat(4));
        geofence.setType(cursor.getString(5));
        geofence.setExpires(cursor.getInt(6));

        PreyLogger.i("id:"+geofence.getId()+" lat:"+geofence.getLatitude()+" lng:"+geofence.getLongitude());
        return geofence;
    }

}
