package com.prey.batch;

import java.util.Calendar;
import java.util.Date;

import com.prey.PreyAccountData;
import com.prey.PreyConfig;
import com.prey.PreyLogger;
import com.prey.PreyScheduled;
import com.prey.PreyUtils;
import com.prey.managers.PreyWifiManager;
import com.prey.net.PreyWebServices;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class PreyBatchReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context ctx, Intent intent) {
		boolean isDeviceRegistered = isThisDeviceAlreadyRegisteredWithPrey(ctx);
		boolean isConnectionExists = false;
		boolean isOnline = false;
		try {
			isConnectionExists = PreyConfig.getPreyConfig(ctx).isConnectionExists();
			isOnline = PreyWifiManager.getInstance(ctx).isOnline();
		} catch (Exception e) {

		}
		// if This Device not Registered With Prey
		if (!isDeviceRegistered) {
			//
			if (isThereBatchInstallationKey(ctx)){
				// if connection exists
				if(valida(ctx)){
					long lastBatchStartDate=new Date().getTime();
					PreyLogger.d("____lastBatchStartDate:"+lastBatchStartDate);
					PreyConfig.getPreyConfig(ctx).setBatchDate(lastBatchStartDate);
					
					if (isConnectionExists) {
						// if there is connection, verify if online
						if (isOnline) {
							installBatch(ctx);	
						}
					}
				}
			}
		}
	}

	private boolean isThisDeviceAlreadyRegisteredWithPrey(Context context) {
		return PreyConfig.getPreyConfig(context).isThisDeviceAlreadyRegisteredWithPrey(false);
	}

	private boolean isThereBatchInstallationKey(Context context) {
		String apiKeyBatch=PreyConfig.getPreyConfig(context).getApiKeyBatch();
		return (apiKeyBatch!=null&&!"".equals(apiKeyBatch));
	}
	
	private void installBatch(Context ctx){
		final Context context=ctx;
		new Thread(){
            public void run() {
            	String apiKeyBatch =PreyConfig.getPreyConfig(context).getApiKeyBatch();
            	String emailBatch  = PreyConfig.getPreyConfig(context).getEmailBatch();
            	String deviceType= PreyUtils.getDeviceType(context);
            	try{
            		PreyAccountData accountData = PreyWebServices.getInstance().registerNewDeviceWithApiKeyEmail(context, apiKeyBatch,emailBatch,deviceType);
            		PreyConfig.getPreyConfig(context).saveAccount(accountData);
            		PreyScheduled.getInstance(context);
            	}catch(Exception e){
			
            	}
            }
        }.start();
	}
	
	
	public boolean valida(Context ctx){
		long batchDate=PreyConfig.getPreyConfig(ctx).getBatchDate();
		PreyLogger.d("batchDate:"+batchDate);
		if(batchDate!=0){
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(batchDate);
			cal.add(Calendar.MINUTE, 2);
			long timeMore=cal.getTimeInMillis();
			PreyLogger.d("timM:"+timeMore);
			Date nowDate=new Date();
			long now = nowDate.getTime();
			PreyLogger.d("now_:"+now);
			PreyLogger.d("now>=timeMore:"+(now>=timeMore));
			return (now>=timeMore);
		}
		return true;
	}

}
