/*******************************************************************************
 * Created by Orlando Aliaga
 * Copyright 2012 Fork Ltd. All rights reserved.
 * License: GPLv3
 * Full license at "/LICENSE"
 ******************************************************************************/
package com.prey.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.prey.PreyLogger;
import com.prey.actions.PreyActionsRunnner;
 

public class PreyRunnerService extends Service {

	private final IBinder mBinder = new LocalBinder();
	public static boolean running = false;
 

	/**
	 * Class for clients to access. Because we know this service always runs in
	 * the same process as its clients, we don't need to deal with IPC.
	 */
	public class LocalBinder extends Binder {
		PreyRunnerService getService() {
			return PreyRunnerService.this;
		}
	}

	@Override
	public void onCreate() {
		super.onCreate();
 
	}
	
	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		String cmd=null;
		try{ 
			cmd=intent.getExtras().getString("cmd");
		}catch(Exception e){}
		PreyLogger.d("PreyRunnerService has been started...:"+cmd);
		PreyActionsRunnner exec = new PreyActionsRunnner(cmd);
		running = true;
		exec.run(PreyRunnerService.this);
	}

	@Override
	public void onDestroy() {
		PreyLogger.d("********************");
		PreyLogger.d("PreyRunnerService is going to be destroyed");
		 
		//ActionsController.getInstance(PreyRunnerService.this).finishRunningJosb();
		running = false;
		PreyLogger.d("PreyRunnerService has been destroyed");
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return mBinder;
	}

}
