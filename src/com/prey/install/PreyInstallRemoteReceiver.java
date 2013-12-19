package com.prey.install;

import java.util.Calendar;
import java.util.Date;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

 
import com.prey.PreyConfig;
 
import com.prey.PreyLogger;
 

public class PreyInstallRemoteReceiver extends BroadcastReceiver {

	private boolean registered=false;

    @Override
    public void onReceive(Context ctx, Intent intent) {
        PreyLogger.i("Broadcast - Action received: "+intent.getAction());
        PreyConfig preyConfig=PreyConfig.getPreyConfig(ctx);
        boolean configured=preyConfig.isThisDeviceAlreadyRegisteredWithPrey(false);
        if (configured){
        	//sleep
        }else{
        	if (registered || !lastExecutionminutes(ctx)){
		  		//sleep();
        		//PreyLogger.i("____install");
		  	}else{
		  		registerDevice(ctx);// # sends device activation email
		  	}
        }
    }

    public void registerDevice(Context ctx){
    	new PreyInstallRemoteThread(ctx).start();
    	registered=true;
	}
    
    public Date timeNow(){
		return new Date();
	}
    
	public boolean lastExecutionminutes(Context ctx){
		Calendar cal=Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.HOUR_OF_DAY,-2);
		long leastTwoHours=cal.getTimeInMillis();
		long installtionDate=PreyConfig.getPreyConfig(ctx).getInstallationDate();
		PreyLogger.i("installtionDate:"+installtionDate);
		PreyLogger.i("leastTwoHours:"+leastTwoHours);
		if(installtionDate>leastTwoHours){
				return true;
		}
		return false;
	}
     
}
