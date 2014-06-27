package com.prey.json.actions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;

import com.prey.PreyConfig;
import com.prey.PreyLogger;
import com.prey.actions.HttpDataService;
import com.prey.actions.observer.ActionResult;
import com.prey.net.PreyWebServices;

public class Update {

	
	public void get(Context ctx, List<ActionResult> lista, JSONObject parameters) {
		PreyLogger.i("get");
		HttpDataService data = new HttpDataService("update");
		data.setList(true);
		HashMap<String, String> parametersMap = new HashMap<String, String>();
		try{
		JSONArray options = parameters.getJSONArray("names");
		
		for(int i=0;i<options.length();i++){
			String key = options.getString(i);
			PreyLogger.i("key["+i+"]"+key);
			String value=PreyConfig.getPreyConfig(ctx).getValue(key);
			PreyLogger.i(key+"["+i+"]"+value);
			parametersMap.put(key, value);
			
			
		}
		}catch(Exception e){
			
		}
		data.addDataListAll(parametersMap);
		ArrayList<HttpDataService> dataToBeSent = new ArrayList<HttpDataService>();
		dataToBeSent.add(data);
		PreyWebServices.getInstance().sendPreyHttpData(ctx, dataToBeSent);
	}

	@SuppressWarnings("rawtypes")
	public void change(Context ctx, List<ActionResult> lista, JSONObject parameters) {
		PreyLogger.i("change");
		try{
		JSONArray options = parameters.getJSONArray("names");
		 
		for(int i=0;i<options.length();i++){
			JSONObject obj= (JSONObject)options.get(i);
			Iterator it=obj.keys();
			String key=(String)it.next();
			String value=obj.getString(key);
			PreyLogger.i(key+":"+value);
			PreyConfig.getPreyConfig(ctx).changeValue(key, value);
		}
		}catch(Exception e){
			
		}
	}
	
	
	//json="[{\"command\":\"change\",\"target\":\"update\",\"options\":{\"names\":[\"ACTIVATE_WIFI\":\"1\",\"DISABLE_POWER\":\"0\"]}}]"
	//json="[{\"command\":\"get\",\"target\":\"update\",\"options\":{\"names\":[\"ACTIVATE_WIFI\",\"DISABLE_POWER\"]}}]"
			
}
