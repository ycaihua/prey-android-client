package com.prey.install;

import android.content.Context;
import android.os.Build;

 
import com.prey.PreyConfig;
import com.prey.PreyEmail;
import com.prey.PreyLogger; 
import com.prey.backwardcompatibility.AboveCupcakeSupport;

public class PreyInstallRemoteThread extends Thread {
	private Context ctx;

	public PreyInstallRemoteThread(Context ctx) {
		this.ctx = ctx;
	}

	public void run() {
		try {
			String email = PreyEmail.getEmail(ctx);
			String notificationId = PreyConfig.getPreyConfig(ctx).getNotificationId();
			String model = Build.MODEL;
			String vendor = "Google";
			if (!PreyConfig.getPreyConfig(ctx).isCupcake())
				vendor = AboveCupcakeSupport.getDeviceVendor();
 	
			PreyLogger.i("email:"+email+" vendor:"+vendor+" model:"+model+" notificationId:"+notificationId);
			/*
			String deviceType = PreyUtils.getDeviceType(ctx);
			PreyAccountData accountData = null;
			accountData = PreyWebServices.getInstance().registerNewDeviceToAccount(ctx, email, "prueba", deviceType);
			PreyConfig.getPreyConfig(ctx).saveAccount(accountData);
			*/
		} catch (Exception e) {
			PreyLogger.i("Error: " + e.getMessage());

		}
	}

}
