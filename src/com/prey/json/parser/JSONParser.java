/*******************************************************************************
 * Created by Orlando Aliaga
 * Copyright 2012 Fork Ltd. All rights reserved.
 * License: GPLv3
 * Full license at "/LICENSE"
 ******************************************************************************/
package com.prey.json.parser;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.prey.PreyConfig;
import com.prey.PreyLogger;
import com.prey.net.PreyRestHttpClient;

public class JSONParser {
	JSONObject jObj;

	boolean error;

	private final static String COMMAND = "\"command\"";
	private final static String TARGET = "\"target\"";

	public JSONParser() {

	}

	public List<JSONObject> getJSONFromUrl(Context ctx, String url) {
		String sb = null;
		String json = null;

		PreyRestHttpClient preyRestHttpClient = PreyRestHttpClient.getInstance(ctx);
		try {
			sb = preyRestHttpClient.getStringUrl(url, PreyConfig.getPreyConfig(ctx));
			if (sb != null)
				json = sb.trim();
		} catch (Exception e) {
			PreyLogger.e("Error, causa:" + e.getMessage(), e);
			return null;
		}
		if ("[]".equals(json)) {
			return null;
		}
		return getJSONFromTxt(ctx, json);
	}

	public List<JSONObject> getJSONFromTxt(Context ctx, String json) {
		if ("Invalid data received".equals(json))
			return null;
		List<JSONObject> listaJson = new ArrayList<JSONObject>();
		json = "{\"prey\":" + json + "}";
		PreyLogger.d(json);
		try {
			JSONObject jsnobject = new JSONObject(json);
			JSONArray jsonArray = jsnobject.getJSONArray("prey");
			for (int i = 0; i < jsonArray.length(); i++) {
				String jsonCommand = jsonArray.get(i).toString();
				JSONObject explrObject = new JSONObject(jsonCommand);
				PreyLogger.i(explrObject.toString());
				listaJson.add(explrObject);
			}
		} catch (Exception e) {
			PreyLogger.e("error in parser:" + e.getMessage(), e);
		}
		return listaJson;
	}

	public List<JSONObject> getJSONFromTxt2(Context ctx, String json) {
		jObj = null;
		List<JSONObject> listaJson = new ArrayList<JSONObject>();
		List<String> listCommands = getListCommands(json);
		for (int i = 0; listCommands != null && i < listCommands.size(); i++) {
			String command = listCommands.get(i);
			try {
				jObj = new JSONObject(command);
				listaJson.add(jObj);
			} catch (JSONException e) {
				PreyLogger.e("JSON Parser, Error parsing data " + e.toString(), e);
			}
		}
		PreyLogger.i("json:" + json);
		// return JSON String
		return listaJson;
	}

	private List<String> getListCommands(String json) {
		if (json.indexOf("[{" + COMMAND) == 0) {
			return getListCommandsCmd(json);
		} else {
			return getListCommandsTarget(json);
		}
	}

	private List<String> getListCommandsTarget(String json) {
		json = json.replaceAll("nil", "{}");
		json = json.replaceAll("null", "{}");
		List<String> lista = new ArrayList<String>();
		int posicion = json.indexOf(TARGET);
		json = json.substring(posicion + 8);
		posicion = json.indexOf(TARGET);
		String command = "";
		while (posicion > 0) {
			command = json.substring(0, posicion);
			json = json.substring(posicion + 8);
			lista.add("{" + TARGET + cleanChar(command));
			posicion = json.indexOf("\"target\"");
		}
		lista.add("{" + TARGET + cleanChar(json));
		return lista;
	}

	private List<String> getListCommandsCmd(String json) {
		json = json.replaceAll("nil", "{}");
		json = json.replaceAll("null", "{}");
		List<String> lista = new ArrayList<String>();
		int posicion = json.indexOf(COMMAND);
		json = json.substring(posicion + 9);
		posicion = json.indexOf(COMMAND);
		String command = "";
		while (posicion > 0) {
			command = json.substring(0, posicion);
			json = json.substring(posicion + 9);
			lista.add("{" + COMMAND + cleanChar(command));
			posicion = json.indexOf("\"command\"");
		}
		lista.add("{" + COMMAND + cleanChar(json));
		return lista;
	}

	private String cleanChar(String json) {
		if (json != null) {
			json = json.trim();
			char c = json.charAt(json.length() - 1);
			while (c == '{' || c == ',' || c == ']') {
				json = json.substring(0, json.length() - 1);
				json = json.trim();
				c = json.charAt(json.length() - 1);
			}
		}
		return json;
	}
}
