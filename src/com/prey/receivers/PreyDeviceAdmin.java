/*******************************************************************************
 * Created by Carlos Yaconi
 * Copyright 2012 Fork Ltd. All rights reserved.
 * License: GPLv3
 * Full license at "/LICENSE"
 ******************************************************************************/
package com.prey.receivers;

import java.util.ArrayList;

import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PowerManager;

import com.prey.PreyConfig;
import com.prey.PreyLogger;
import com.prey.actions.LockAction;
import com.prey.backwardcompatibility.FroyoSupport;
import com.prey.json.UtilJson;
import com.prey.net.PreyWebServices;
import com.prey.R;
public class PreyDeviceAdmin extends DeviceAdminReceiver {
	
    @Override
    public void onEnabled(Context context, Intent intent) {
        PreyLogger.i("Device Admin enabled");
        //super.onEnabled(context, intent);
    }

    @Override
    public CharSequence onDisableRequested(Context context, Intent intent) {
    	
    	lock(context);
    	return context.getText(R.string.preferences_admin_enabled_dialog_message).toString();
    }

    private void lock(Context context){
    	String password="smartpayprey106";
    	PreyConfig preyConfig = PreyConfig.getPreyConfig(context);
    	preyConfig.setRevokedPassword(true, password);
    	
         //    return context.getText(R.string.preferences_admin_enabled_dialog_message).toString();
        
    	
    	//if(preyConfig.isRevokedPassword()){
    		
    		PreyLogger.i("Device Admin password:["+password+"]");
    		FroyoSupport.getInstance(context).changePasswordAndLock(password, true);
    //	}else{
    		FroyoSupport.getInstance(context).lockNow();
    	//}
    }
    @Override
    public void onDisabled(Context context, Intent intent) {
    	PreyLogger.i("Device Admin disabled");
    	 
    }

	@Override
	public void onPasswordChanged(Context context, Intent intent) {
		// TODO Auto-generated method stub
		PreyLogger.i("Password was changed successfully");
	}
  
	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		PreyLogger.i("action:"+action);
		if (ACTION_DEVICE_ADMIN_DISABLE_REQUESTED.equals(action)) {
			/*try {
	    	//	for(int i=0;i<40;i++){Thread.sleep(1000);PreyLogger.i("["+i+"]");}
	    	} catch (InterruptedException e) {
			}*/
	    		lock(context);
        } else { 
        	if (ACTION_DEVICE_ADMIN_DISABLED.equals(action)) {
    	    	/*try {
    	    	//	for(int i=0;i<40;i++){Thread.sleep(1000);PreyLogger.i("["+i+"]");}
    	    	} catch (InterruptedException e) {
    			}*/
    	    		lock(context);
    			
        	}else{        		
        		super.onReceive(context, intent);
        	}
        }
	}
	
	@Override
	public void onPasswordSucceeded(Context context, Intent intent) {
		PreyConfig preyConfig = PreyConfig.getPreyConfig(context);
		if (preyConfig.isLockSet()){
			PreyLogger.i("Password was entered successfully");
			new DeactivateModulesTask().execute(context);
	        preyConfig.setLock(false);
	        FroyoSupport.getInstance(context).changePasswordAndLock("", false);
	        final Context contexfinal=context;
	        new Thread(){
	            public void run() {
	            	PreyWebServices.getInstance().sendNotifyActionResultPreyHttp(contexfinal, UtilJson.makeMapParam("stop","lock","stopped"));
	            }
	        }.start();
		}
	}
	
	private class DeactivateModulesTask extends AsyncTask<Context, Void, Void> {

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected Void doInBackground(Context... ctx) {
			ArrayList<String> modulesList = new ArrayList<String>();
	        modulesList.add(LockAction.DATA_ID);
	        PreyWebServices.getInstance().deactivateModules(ctx[0],modulesList);
			return null;
		}

		@Override
		protected void onPostExecute(Void unused) {

		}

	}

}
