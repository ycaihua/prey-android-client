/*******************************************************************************
 * Created by Carlos Yaconi
 * Copyright 2012 Fork Ltd. All rights reserved.
 * License: GPLv3
 * Full license at "/LICENSE"
 ******************************************************************************/
package com.prey.actions;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;

import com.prey.PreyConfig;
import com.prey.PreyLogger;
import com.prey.services.PreyRunnerService;
import com.prey.util.ClassUtil;
import com.prey.exceptions.PreyException;
import com.prey.json.parser.JSONParser;
import com.prey.managers.PreyConnectivityManager;
import com.prey.managers.PreyTelephonyManager;
import com.prey.net.PreyWebServices;

public class PreyActionsRunner implements Runnable {

	private Context ctx;
	private String body;
	private String version;
	private String cmd;

	public PreyActionsRunner(Context context, String cmd) {
		this.ctx = context;
		this.body = "";
		this.version = "";
		this.cmd = cmd;
	}

	public PreyActionsRunner(Context context, String body, String version) {
		this.ctx = context;
		this.body = body;
		this.version = version;
	}

	public void run() {
		execute();
	}

	public void execute() {
	 
		if (PreyConfig.getPreyConfig(ctx).isThisDeviceAlreadyRegisteredWithPrey()) {
			PreyTelephonyManager preyTelephony = PreyTelephonyManager.getInstance(ctx);
			PreyConnectivityManager preyConnectivity = PreyConnectivityManager.getInstance(ctx);
			boolean connection = false;
			try {
				List<JSONObject> jsonObject = null;
				
				connection = preyTelephony.isDataConnectivityEnabled() || preyConnectivity.isConnected();
					 
				if (connection) {
					try {
						if (cmd == null || "".equals(cmd)) {
							jsonObject = PreyActionsRunner.getInstructions(ctx);
						} else {
							jsonObject = getInstructionsNewThread(ctx, cmd);
						}
					} catch (Exception e) {
					}
					PreyLogger.d("version:" + version + " body:" + body);
					if (jsonObject == null || jsonObject.size() == 0) {
						PreyLogger.d("nothing");
					} else {
						PreyLogger.d("runInstructions");
						runInstructions(jsonObject);
					}
				}
			} catch (Exception e) {
				PreyLogger.e("Error, because:" + e.getMessage(), e);
			}
			PreyLogger.d("Prey execution has finished!!");
		 
		}
		ctx.stopService(new Intent(ctx, PreyRunnerService.class));
	}

	private static List<JSONObject> getInstructionsNewThread(Context ctx, String cmd) throws PreyException {
		List<JSONObject> jsonObject = new JSONParser().getJSONFromTxt(ctx, "[" + cmd + "]");
		final Context context = ctx;
		new Thread(new Runnable() {
			public void run() {
				try {
					PreyLogger.d("_________New Thread");
					PreyActionsRunner.getInstructions(context);
				} catch (PreyException e) {
				}
			}
		}).start();
		return jsonObject;
	}

	private static List<JSONObject> getInstructions(Context ctx) throws PreyException {
		PreyLogger.d("______________________________");
		PreyLogger.d("_______getInstructions________");
		List<JSONObject> jsonObject = null;
		try {
			jsonObject = PreyWebServices.getInstance().getActionsJsonToPerform(ctx);
		} catch (PreyException e) {
			PreyLogger.e("Exception getting device's xml instruction set", e);
			throw e;
		}
		return jsonObject;
	}

	private List<HttpDataService> runInstructions(List<JSONObject> jsonObject) throws PreyException {
		List<HttpDataService> listData = null;
		listData = runActionJson(ctx, jsonObject);
		return listData;
	}
	

	 public static List<HttpDataService> runActionJson(Context ctx, List<JSONObject> jsonObjectList) {
         List<HttpDataService> listData=new ArrayList<HttpDataService>();
         
         int size=jsonObjectList==null?-1:jsonObjectList.size();
         PreyLogger.i("runActionJson size:"+size);
         try{
        	 if(size>=0&&PreyConfig.getPreyConfig(ctx).isNextAlert()){
        		 PreyConfig.getPreyConfig(ctx).setNextAlert(false);
        	 	Settings.System.putString(ctx.getContentResolver(),Settings.System.NEXT_ALARM_FORMATTED,"");
        	 }
         }catch(Exception e){} 
         
         try {
                 for(int i=0;jsonObjectList!=null&&i<jsonObjectList.size();i++){
                         JSONObject jsonObject=jsonObjectList.get(i);
                         PreyLogger.d("jsonObject:"+jsonObject);
                         String nameAction = jsonObject.getString("target");
                         String methodAction = jsonObject.getString("command");
                         JSONObject parametersAction =null;
                         try{
                                 parametersAction = jsonObject.getJSONObject("options");
                         }catch(JSONException e){
                                 
                         }
                         PreyLogger.d("nameAction:"+nameAction+" methodAction:"+methodAction);
                                         
                         List<ActionResult> lista = new ArrayList<ActionResult>();
                         listData=ClassUtil.execute(ctx, lista, nameAction, methodAction, parametersAction,listData);
                         /*if (lista!=null&&lista.size() > 0) {
                                 for (ActionResult result : lista) {
                                         dataToBeSent.add(result.getDataToSend());
                                 }
                         }*/
                 }
                 /*if(dataToBeSent!=null&&dataToBeSent.size()>0){
                         PreyWebServices.getInstance().sendPreyHttpReport(ctx, listData);
                 }*/
                 
                 return listData;
         } catch (JSONException e) {
                 PreyLogger.e("Error, causa:" + e.getMessage(), e);
         }
         return null;
	 }
 

}
