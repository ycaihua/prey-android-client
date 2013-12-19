package com.prey.receivers;

import java.util.Date;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.prey.PreyConfig;
import com.prey.PreyLogger;

public class PreyInstallRemoteReceiver extends BroadcastReceiver {

	private Date lastExecution=null;
 

    @Override
    public void onReceive(Context ctx, Intent intent) {
        PreyLogger.i("Broadcast - Action received: "+intent.getAction());
        PreyConfig preyConfig=PreyConfig.getPreyConfig(ctx);
        boolean configured=preyConfig.isThisDeviceAlreadyRegisteredWithPrey(false);
        if (configured){
        	
        }else{
        	if (registered || !lastExecutionminutes()){
		  		sleep();
		  	}else{
		  		registerDevice();// # sends device activation email
		  	}
        }
        lastExecution = timeNow();
    }

    public void registerDevice(){
    	
    }
    
    public Date timeNow(){
		return new Date();
	}
    
	public boolean lastExecutionminutes(){
		return lastExecution==null;
	}
     
}
