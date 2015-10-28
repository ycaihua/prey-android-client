package com.prey.actions.geofences;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

/**
 * Created by oso on 23-10-15.
 */
public class PendingInstance {

    private PendingIntent pendingIntent;
    private PendingInstance(Context ctx){
        Intent intent = new Intent(ctx, GeofenceTransitionsIntentService.class);
        pendingIntent=PendingIntent.getService(ctx, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private static PendingInstance instance=null;

    public static PendingInstance getInstance(Context ctx){
        if(instance==null){
            instance=new PendingInstance(ctx);
        }
        return instance;
    }

    public PendingIntent getPendingIntent() {
        return pendingIntent;
    }

    public void cancel(){
        pendingIntent.cancel();
    }
}
