package com.prey;

import android.annotation.SuppressLint;
import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.prey.beta.actions.PreyBetaController;

public class PreyScheduled {

	private static PreyScheduled instance=null;
	private ScheduledExecutorService scheduler =null;
	
	private PreyScheduled(Context context){
		run(context);
	}
	
	public static PreyScheduled getInstance(Context context){
		if (instance==null){
			instance=new PreyScheduled(context);
		}
		return instance;
	}
	
	private SimpleDateFormat sdf=new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
	
	@SuppressLint("NewApi")
	private void run(Context context) {
		if (PreyEmail.getEmail(context) == null) {
			scheduler = Executors.newSingleThreadScheduledExecutor();
			final Context ctx=context;
			scheduler.scheduleAtFixedRate(new Runnable() {
				public void run() {
					PreyLogger.i("PreyScheduled:"+sdf.format(new Date()));
					PreyBetaController.startPrey(ctx);
				}
			}, 1, 4, TimeUnit.MINUTES);
		}

	}

}
