package com.prey.actions.retrievals;

/**
 * Created by oso on 28-10-15.
 */

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.prey.PreyLogger;

public class WebServerService extends Service {

    private WebServer server = null;

    @Override
    public void onCreate() {
        PreyLogger.i( "Creating and starting httpService");
        super.onCreate();
        Thread thread = new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    server = new WebServer(getApplicationContext());
                    server.startServer();
                } catch (Exception e) {
                    PreyLogger.e("error:"+e.getMessage(),e);
                }
            }
        });

        thread.start();
    }

    @Override
    public void onDestroy() {
        PreyLogger.i( "Destroying httpService");
        server.stopServer();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
