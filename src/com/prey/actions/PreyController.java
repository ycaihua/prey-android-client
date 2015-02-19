/*******************************************************************************
 * Created by Carlos Yaconi
 * Copyright 2012 Fork Ltd. All rights reserved.
 * License: GPLv3
 * Full license at "/LICENSE"
 ******************************************************************************/
/*******************************************************************************
 * Created by Carlos Yaconi
 * Copyright 2012 Fork Ltd. All rights reserved.
 * License: GPLv3
 * Full license at "/LICENSE"
 ******************************************************************************/
package com.prey.actions;

import android.content.Context;
import android.content.Intent;

import com.prey.PreyConfig;
import com.prey.services.PreyRunnerService;
 
public class PreyController {

	public static void startPrey(Context ctx) {
		startPrey(ctx,null);
	}
	
	public static void startPrey(Context ctx,String cmd) {
		if (PreyConfig.getPreyConfig(ctx).isThisDeviceAlreadyRegisteredWithPrey()){
			PreyConfig.getPreyConfig(ctx).setRun(true);
			final Context context = ctx;
			final String command=cmd;
			new Thread(new Runnable() {
				public void run() {
					Intent intentStart= new Intent(context, PreyRunnerService.class);
					if(command!=null ){
						intentStart.putExtra("cmd", command);
					}
					context.startService(intentStart);
				}
			}).start();
		}
	}

	public static void stopPrey(Context ctx) {
		ctx.stopService(new Intent(ctx, PreyRunnerService.class));
	}
}
