package com.prey.actions.geo;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.prey.PreyLogger;

public class LocationIntentService extends IntentService {
    public static final String LOCATION_UPDATE_INTENT_SERVICE = "LocationIntentService";

    public LocationIntentService() {
        super(LOCATION_UPDATE_INTENT_SERVICE);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Location location = intent.getParcelableExtra(LocationClient.KEY_LOCATION_CHANGED);

        generateNotification("Fused Location", location.getLatitude() + " " + location.getLongitude());
    }

    private void generateNotification(String title, String content) {
    	Toast.makeText(this, title+" "+content, Toast.LENGTH_LONG).show();;
    	 /*
        long when = System.currentTimeMillis();
        Intent notifyIntent = new Intent(this, MainActivity.class);
        notifyIntent.putExtra("title", title);
        notifyIntent.putExtra("content", content);
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);

       
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.dac_logo)
                        .setContentTitle(title)
                        .setContentText(content)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true)
                        .setDefaults(Notification.DEFAULT_SOUND)
                        .setWhen(when);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify((int) when, builder.build());*/
    }


}
