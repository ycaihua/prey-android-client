package com.prey.actions.geofences;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.prey.PreyLogger;

/**
 * Created by oso on 29-09-15.
 */
public class GeofenceOpenHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 6;

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "_name";
    public static final String COLUMN_LATITUDE = "_latitude";
    public static final String COLUMN_LONGITUDE = "_longitude";
    public static final String COLUMN_RADIUS = "_radius";
    public static final String COLUMN_TYPE = "_type";
    public static final String COLUMN_EXPIRES = "_expires";


    private static final String DATABASE_NAME = "Geofence.db";


    public static final String GEOFENCE_TABLE_NAME = "geofence";
    private static final String GEOFENCE_TABLE_CREATE =
            "CREATE TABLE " + GEOFENCE_TABLE_NAME + " (" +
                    COLUMN_ID + " TEXT NOT NULL PRIMARY KEY, " +
                    COLUMN_NAME + " TEXT NOT NULL," +
                    COLUMN_LATITUDE + " REAL NOT NULL," +
                    COLUMN_LONGITUDE + " REAL NOT NULL," +
                    COLUMN_RADIUS + " REAL NOT NULL," +
                    COLUMN_TYPE + " TEXT NOT NULL," +
                    COLUMN_EXPIRES + " INTEGER NOT NULL" +
                    ");";

    public GeofenceOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(GEOFENCE_TABLE_CREATE);
        }catch (Exception e ){
            PreyLogger.i("Error en crear tabla");
        }
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            db.execSQL("DROP TABLE IF EXISTS " + GEOFENCE_TABLE_NAME);
        }catch (Exception e ){
            PreyLogger.i("Error en borrar tabla");
        }
        onCreate(db);
    }

}
