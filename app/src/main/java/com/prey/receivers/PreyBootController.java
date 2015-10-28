/*******************************************************************************
 * Created by Carlos Yaconi
 * Copyright 2015 Prey Inc. All rights reserved.
 * License: GPLv3
 * Full license at "/LICENSE"
 ******************************************************************************/
package com.prey.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.prey.PreyConfig;
import com.prey.PreyLogger;
import com.prey.json.actions.Report;
import com.prey.services.PreyDisablePowerOptionsService;

public class PreyBootController extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        PreyLogger.d("Boot finished. Starting Prey Boot Service");
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            String interval = PreyConfig.getPreyConfig(context).getIntervalReport();
            if (interval != null && !"".equals(interval)) {
                Report.run(context, Integer.parseInt(interval));
            }
            boolean disablePowerOptions = PreyConfig.getPreyConfig(context).isDisablePowerOptions();
            if (disablePowerOptions) {
                context.startService(new Intent(context, PreyDisablePowerOptionsService.class));
            } else {
                context.stopService(new Intent(context, PreyDisablePowerOptionsService.class));
            }
            PreyLogger.i("encendido");

/*
            Intent intent2=new Intent(context, GeofenceService.class);
            intent2.setAction(GeofenceService.ACTION_LOCATION_UPDATED);
            context.startService(new Intent(context, GeofenceService.class));
*/

        } else
            PreyLogger.e("Received unexpected intent " + intent.toString(), null);
    }


}
