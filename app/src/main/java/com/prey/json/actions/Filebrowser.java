package com.prey.json.actions;

import android.content.Context;
import android.content.Intent;
import android.os.Looper;
import android.os.StrictMode;

import com.prey.PreyLogger;
import com.prey.actions.HttpDataService;
import com.prey.actions.observer.ActionResult;
import com.prey.actions.retrievals.WebServerService;
import com.prey.actions.retrievals.WebServerServiceSingleton;
import com.prey.json.JsonAction;
import com.prey.json.UtilJson;
import com.prey.net.PreyWebServices;
import com.prey.services.LocationService;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by oso on 28-10-15.
 */
public class Filebrowser extends JsonAction {

    public HttpDataService run(Context ctx, List<ActionResult> list, JSONObject parameters) {
        return null;
    }

    public void start(Context ctx, List<ActionResult> list, JSONObject parameters) {


        Intent intent = new Intent(ctx, WebServerService.class);
        WebServerServiceSingleton.getInstance().setIntent(intent);
        ctx.startService(intent);
        PreyWebServices.getInstance().sendNotifyActionResultPreyHttp(ctx, UtilJson.makeMapParam("start", "alarm", "stopped"));

    }

    public void stop(Context ctx, List<ActionResult> list, JSONObject options) {
        Intent intent = WebServerServiceSingleton.getInstance().getIntent();
        ctx.stopService(intent);
        PreyWebServices.getInstance().sendNotifyActionResultPreyHttp(ctx, UtilJson.makeMapParam("start", "alert", "stopped"));
    }

}
