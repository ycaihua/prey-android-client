/*******************************************************************************
 * Created by Carlos Yaconi
 * Copyright 2012 Fork Ltd. All rights reserved.
 * License: GPLv3
 * Full license at "/LICENSE"
 ******************************************************************************/
package com.prey.net;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.json.JSONObject;

import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;

import com.prey.FileConfigReader;
import com.prey.PreyAccountData;
import com.prey.PreyConfig;
import com.prey.PreyLogger;
import com.prey.PreyPhone;
import com.prey.R;
import com.prey.PreyPhone.Hardware;
import com.prey.PreyPhone.Wifi;
import com.prey.actions.HttpDataService;
import com.prey.actions.PreyActionsRunner;
import com.prey.events.Event;
import com.prey.exceptions.NoMoreDevicesAllowedException;
import com.prey.exceptions.PreyException;
import com.prey.json.actions.location.PreyLocation;
import com.prey.json.parser.JSONParser;
import com.prey.net.http.EntityFile;

public class PreyWebServices {

	private static PreyWebServices _instance = null;

	private PreyWebServices() {

	}

	public static PreyWebServices getInstance() {
		if (_instance == null)
			_instance = new PreyWebServices();
		return _instance;
	}

	public PreyAccountData registerNewDeviceWithApiKeyEmail(Context ctx, String apiKey, String email, String deviceType) throws PreyException {
		String deviceId = "";
		PreyHttpResponse responseDevice = registerNewDevice(ctx, apiKey, deviceType);
		String xmlDeviceId = responseDevice.getResponseAsString();
		// if json
		if (xmlDeviceId.contains("{\"key\"")) {
			try {
				JSONObject jsnobject = new JSONObject(xmlDeviceId);
				deviceId = jsnobject.getString("key");
			} catch (Exception e) {
			}
		}
		PreyAccountData newAccount = new PreyAccountData();
		newAccount.setApiKey(apiKey);
		newAccount.setDeviceId(deviceId);
		newAccount.setEmail(email);
		newAccount.setPassword("");
		return newAccount;

	}

	public PreyAccountData registerNewDeviceToAccount(Context ctx, String email, String password, String deviceType) throws PreyException {
		PreyLogger.d("email:"+email+" password:"+password);
		PreyConfig preyConfig = PreyConfig.getPreyConfig(ctx);
		HashMap<String, String> parameters = new HashMap<String, String>();
		PreyHttpResponse response=null;
		String xml;
		try {
			String apiv2=FileConfigReader.getInstance(ctx).getApiV2();
			String url=PreyConfig.getPreyConfig(ctx).getPreyUrl().concat(apiv2).concat("profile.xml");
			PreyLogger.d("url:"+url);
			response=PreyRestHttpClient.getInstance(ctx).get(url, parameters, preyConfig, email, password);
			xml = response.getResponseAsString(); 
		} catch (IOException e) {
			PreyLogger.e("Error!",e);
			throw new PreyException(ctx.getText(R.string.error_communication_exception).toString(), e);
		}
		String status="";
		if(response!=null&&response.getStatusLine()!=null){
			status="["+response.getStatusLine().getStatusCode()+"]";
		}
		if (!xml.contains("<key")){
			throw new PreyException(ctx.getString(R.string.error_cant_add_this_device,status));
		}

		int from;
		int to;
		String apiKey;
		try {
			from = xml.indexOf("<key>") + 5;
			to = xml.indexOf("</key>");
			apiKey = xml.substring(from, to);
		} catch (Exception e) {
			throw new PreyException(ctx.getString(R.string.error_cant_add_this_device,status));
		}
		String deviceId =null;
		PreyHttpResponse responseDevice = registerNewDevice(ctx, apiKey, deviceType);
		String xmlDeviceId=responseDevice.getResponseAsString();
		//if json
		if (xmlDeviceId.contains("{\"key\"") ){
			try{
				JSONObject jsnobject = new JSONObject(xmlDeviceId);
				deviceId=jsnobject.getString("key");
			}catch(Exception e){
				
			}
		}
		PreyAccountData newAccount = new PreyAccountData();
		newAccount.setApiKey(apiKey);
		newAccount.setDeviceId(deviceId);
		newAccount.setEmail(email);
		newAccount.setPassword(password);
		return newAccount;

	}
	private PreyHttpResponse registerNewDevice(Context ctx, String api_key, String deviceType) throws PreyException {
		 

		String model = Build.MODEL;
		String vendor = "Google";
		if (!PreyConfig.getPreyConfig(ctx).isCupcakeOrAbove()){
			vendor = Build.MANUFACTURER;
		}

		HashMap<String, String> parameters = new HashMap<String, String>();
		parameters.put("api_key", api_key);
		parameters.put("title", vendor + " " + model);
		parameters.put("device_type", deviceType);
		parameters.put("os", "Android");
		parameters.put("os_version", Build.VERSION.RELEASE);
		parameters.put("referer_device_id", "");
		parameters.put("plan", "free");
		parameters.put("model_name", model);
		parameters.put("vendor_name", vendor);

		parameters = increaseData(ctx, parameters);
		TelephonyManager mTelephonyMgr = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
		// String imsi = mTelephonyMgr.getSubscriberId();
		String imei = mTelephonyMgr.getDeviceId();
		parameters.put("physical_address", imei);

		PreyHttpResponse response = null;
		try {
			String apiv2 = FileConfigReader.getInstance(ctx).getApiV2();
			String url = PreyConfig.getPreyConfig(ctx).getPreyUrl().concat(apiv2).concat("devices.json");
			PreyLogger.d("url:" + url);
			response = PreyRestHttpClient.getInstance(ctx).post(url, parameters);
			PreyLogger.d("response:" + response.getStatusLine() + " " + response.getResponseAsString());
			// No more devices allowed

			if ((response.getStatusLine().getStatusCode() == 302) || (response.getStatusLine().getStatusCode() == 422) || (response.getStatusLine().getStatusCode() == 403)) {
				throw new NoMoreDevicesAllowedException(ctx.getText(R.string.set_old_user_no_more_devices_text).toString());
			}
			if (response.getStatusLine().getStatusCode() > 299) {
				throw new PreyException(ctx.getString(R.string.error_cant_add_this_device, "[" + response.getStatusLine().getStatusCode() + "]"));
			}
		} catch (IOException e) {
			throw new PreyException(ctx.getText(R.string.error_communication_exception).toString(), e);
		}

		return response;
	}
	
	/**
	 * Register a new account and get the API_KEY as return In case email is
	 * already registered, this service will return an error.
	 * 
	 * @throws PreyException
	 * 
	 */
	public PreyAccountData registerNewAccount(Context ctx, String name, String email, String password, String deviceType) throws PreyException {

		HashMap<String, String> parameters = new HashMap<String, String>();
		
		parameters.put("name", name);
		parameters.put("email", email);
		parameters.put("password", password);
		parameters.put("password_confirmation", password);
		parameters.put("country_name", Locale.getDefault().getDisplayCountry());

		
		PreyHttpResponse response=null;
		String xml;
		try {
			String apiv2=FileConfigReader.getInstance(ctx).getApiV2();
			String url=PreyConfig.getPreyConfig(ctx).getPreyUrl().concat(apiv2).concat("signup.json");
			//String url=PreyConfig.getPreyConfig(ctx).getPreyUiUrl().concat("users.xml");
			response=PreyRestHttpClient.getInstance(ctx).post(url, parameters);
			xml = response.getResponseAsString();
		} catch (IOException e) {
			throw new PreyException(ctx.getText(R.string.error_communication_exception).toString(), e);
		}
		
		String apiKey="";
		if (xml.contains("\"key\"") ){
			try{
				JSONObject jsnobject = new JSONObject(xml);
				apiKey=jsnobject.getString("key");
			}catch(Exception e){
				
			}
		} else{
			if (response!=null&&response.getStatusLine()!=null&&response.getStatusLine().getStatusCode()>299){
				throw new PreyException(ctx.getString(R.string.error_cant_add_this_device,"["+response.getStatusLine().getStatusCode()+"]"));
			}else{	
				throw new PreyException(ctx.getString(R.string.error_cant_add_this_device,""));		
			}
		}

		PreyHttpResponse responseDevice = registerNewDevice(ctx, apiKey, deviceType);
		String xmlDeviceId=responseDevice.getResponseAsString();
		String deviceId = null;
		if (xmlDeviceId.contains("{\"key\"") ){
			try{
				JSONObject jsnobject = new JSONObject(xmlDeviceId);
				deviceId=jsnobject.getString("key");
			}catch(Exception e){
				
			}
		}else{
			throw new PreyException(ctx.getString(R.string.error_cant_add_this_device,""));		
		}

		PreyAccountData newAccount = new PreyAccountData();
		newAccount.setApiKey(apiKey);
		newAccount.setDeviceId(deviceId);
		newAccount.setEmail(email);
		newAccount.setPassword(password);
		newAccount.setName(name);
		return newAccount;
	}

	public PreyHttpResponse setPushRegistrationId(Context ctx, String regId) {
		// this.updateDeviceAttribute(ctx, "notification_id", regId);
		HttpDataService data = new HttpDataService("notification_id");
		data.setList(false);
		data.setKey("notification_id");
		data.setSingleData(regId);
		ArrayList<HttpDataService> dataToBeSent = new ArrayList<HttpDataService>();
		dataToBeSent.add(data);
		PreyHttpResponse response = PreyWebServices.getInstance().sendPreyHttpData(ctx, dataToBeSent);
		if (response != null && response.getStatusLine() != null && response.getStatusLine().getStatusCode() == 200) {
			PreyLogger.d("c2dm registry id set succesfully");
		}
		return response;
	}

	public PreyHttpResponse sendPreyHttpData(Context ctx, ArrayList<HttpDataService> dataToSend) {
		PreyConfig preyConfig = PreyConfig.getPreyConfig(ctx);

		Map<String, String> parameters = new HashMap<String, String>();
		List<EntityFile> entityFiles = new ArrayList<EntityFile>();
		for (HttpDataService httpDataService : dataToSend) {
			if (httpDataService != null) {
				parameters.putAll(httpDataService.getDataAsParameters());
				if (httpDataService.getEntityFiles() != null && httpDataService.getEntityFiles().size() > 0) {
					entityFiles.addAll(httpDataService.getEntityFiles());
				}
			}
		}
		Hardware hardware = new PreyPhone(ctx).getHardware();
		parameters.put("hardware_attributes[ram_size]", "" + hardware.getTotalMemory());

		// parameters.put("notification_id", preyConfig.getNotificationId());

		PreyHttpResponse preyHttpResponse = null;
		try {
			String url = getDataUrlJson(ctx);
			PreyLogger.d("URL:" + url);

			if (entityFiles.size() == 0)
				preyHttpResponse = PreyRestHttpClient.getInstance(ctx).postAutentication(url, parameters, preyConfig);
			else
				preyHttpResponse = PreyRestHttpClient.getInstance(ctx).postAutentication(url, parameters, preyConfig, entityFiles);
			// PreyLogger.d("Data sent_: " +
			// preyHttpResponse.getResponseAsString());
		} catch (Exception e) {
			PreyLogger.e("Data wasn't send", e);
		}
		return preyHttpResponse;
	}

	public HashMap<String, String> increaseData(Context ctx, HashMap<String, String> parameters) {
		PreyPhone phone = new PreyPhone(ctx);
		Hardware hardware = phone.getHardware();
		String prefix = "hardware_attributes";
		parameters.put(prefix + "[uuid]", hardware.getUuid());
		parameters.put(prefix + "[bios_vendor]", hardware.getBiosVendor());
		parameters.put(prefix + "[bios_version]", hardware.getBiosVersion());
		parameters.put(prefix + "[mb_vendor]", hardware.getMbVendor());
		parameters.put(prefix + "[mb_serial]", hardware.getMbSerial());
		parameters.put(prefix + "[mb_model]", hardware.getMbModel());
		parameters.put(prefix + "[cpu_model]", hardware.getCpuModel());
		parameters.put(prefix + "[cpu_speed]", hardware.getCpuSpeed());
		parameters.put(prefix + "[cpu_cores]", hardware.getCpuCores());
		parameters.put(prefix + "[ram_size]", "" + hardware.getTotalMemory());
		parameters.put(prefix + "[serial_number]", hardware.getSerialNumber());
		int nic = 0;
		Wifi wifi = phone.getWifi();
		if (wifi != null) {
			prefix = "hardware_attributes[network]";
			parameters.put(prefix + "[nic_" + nic + "][name]", wifi.getName());
			parameters.put(prefix + "[nic_" + nic + "][interface_type]", wifi.getInterfaceType());
			parameters.put(prefix + "[nic_" + nic + "][ip_address]", wifi.getIpAddress());
			parameters.put(prefix + "[nic_" + nic + "][gateway_ip]", wifi.getGatewayIp());
			parameters.put(prefix + "[nic_" + nic + "][netmask]", wifi.getNetmask());
			parameters.put(prefix + "[nic_" + nic + "][mac_address]", wifi.getMacAddress());
		}
		return parameters;
	}

	private String getDeviceUrlApiv2(Context ctx) throws PreyException {
		PreyConfig preyConfig = PreyConfig.getPreyConfig(ctx);
		String deviceKey = preyConfig.getDeviceID();
		if (deviceKey == null || deviceKey == "")
			throw new PreyException("Device key not found on the configuration");
		String apiv2 = FileConfigReader.getInstance(ctx).getApiV2();
		String url = PreyConfig.getPreyConfig(ctx).getPreyUrl().concat(apiv2).concat("devices/").concat(deviceKey);
		return url;
	}

	private String getDeviceUrlJson(Context ctx) throws PreyException {
		return getDeviceUrlApiv2(ctx).concat(".json");
	}

	private String getReportUrlJson(Context ctx) throws PreyException {
		return getDeviceUrlApiv2(ctx).concat("/reports.json");
	}

	public String getFileUrlJson(Context ctx) throws PreyException {
		return getDeviceUrlApiv2(ctx).concat("/files");
	}

	public String getDataUrlJson(Context ctx) throws PreyException {
		return getDeviceUrlApiv2(ctx).concat("/data.json");
	}

	private String getEventsUrlJson(Context ctx) throws PreyException {
		return getDeviceUrlApiv2(ctx).concat("/events");
	}

	private String getResponseUrlJson(Context ctx) throws PreyException {
		return getDeviceUrlApiv2(ctx).concat("/response");
	}

	public List<JSONObject> getActionsJsonToPerform(Context ctx) throws PreyException {
		String url = getDeviceUrlJson(ctx);
		PreyLogger.d("url:" + url);
		List<JSONObject> lista = new JSONParser().getJSONFromUrl(ctx, url);
		return lista;
	}
	public String sendNotifyActionResultPreyHttp(Context ctx,   Map<String, String> params) {
		PreyConfig preyConfig = PreyConfig.getPreyConfig(ctx);
		String response = null;   
		try {			
			String url=getResponseUrlJson(ctx);
			PreyHttpResponse httpResponse = PreyRestHttpClient.getInstance(ctx).postAutentication(url, params,preyConfig);
			response=httpResponse.toString();
			PreyLogger.d("Notify Action Result sent: " + response);
		} catch (Exception e) {
			PreyLogger.e("Notify Action Result wasn't send",e);
		}  
		return response;
	} 
	
	public PreyLocation getLocation(Context ctx,List<Wifi>listWifi) throws Exception{
		PreyLocation location=null;
		String url=googleLookup(listWifi);
		PreyLogger.d("location url:"+url);
		PreyHttpResponse response= PreyRestHttpClient.getInstance(ctx).getDefault(url);
		String responseAsString=response.getResponseAsString();
		PreyLogger.d("location resp:"+responseAsString);
		if (response.getStatusLine().getStatusCode()==200){
			if (responseAsString!=null&&responseAsString.indexOf("OK")>=0){
				location=new PreyLocation();
				JSONObject jsnobject = new JSONObject(response.getResponseAsString());
				String accuracy=jsnobject.getString("accuracy");
				JSONObject jsnobjectLocation = jsnobject.getJSONObject("location");
				String lat=jsnobjectLocation.getString("lat");
				String lng=jsnobjectLocation.getString("lng");
				location.setLat(Double.parseDouble(lat));
				location.setLng(Double.parseDouble(lng));
				location.setAccuracy(Float.parseFloat(accuracy));
			}
		}
		return location;
	}
	
	private String googleLookup(List<Wifi> listwifi){
		String queryString = "https://maps.googleapis.com/maps/api/browserlocation/json?browser=firefox&sensor=true";
		try {
			for(int i=0;listwifi!=null&&i<listwifi.size();i++){
				String ssid=listwifi.get(i).getSsid();
				ssid=ssid.replaceAll(" ", "%20");	
				queryString+="&wifi=mac:";
				queryString+=listwifi.get(i).getMacAddress();
				queryString+="%7C";
				queryString+="ssid:";
				queryString+=ssid;
				queryString+="%7C";
				queryString+="ss:";
				queryString+=listwifi.get(i).getSignalStrength();
				
			}	
		} catch (Exception e) {
		}
		return queryString;	
	}
	
	public PreyHttpResponse sendPreyHttpReport(Context ctx, List<HttpDataService> dataToSend) {
		PreyConfig preyConfig = PreyConfig.getPreyConfig(ctx);
		Map<String, String> parameters = new HashMap<String, String>();
		List<EntityFile> entityFiles=new ArrayList<EntityFile>();
		for (HttpDataService httpDataService : dataToSend) {
			if (httpDataService!=null){
				parameters.putAll(httpDataService.getReportAsParameters());
				if (httpDataService.getEntityFiles()!=null&&httpDataService.getEntityFiles().size()>0){
					entityFiles.addAll(httpDataService.getEntityFiles());
				}
			}
		}
		PreyHttpResponse preyHttpResponse=null;
		try {
			String url =getReportUrlJson(ctx);
			PreyLogger.d("report url:"+url);
			if (entityFiles==null||entityFiles.size()==0)
				preyHttpResponse = PreyRestHttpClient.getInstance(ctx).postAutentication(url, parameters, preyConfig);
			else
				preyHttpResponse = PreyRestHttpClient.getInstance(ctx).postAutentication(url, parameters, preyConfig,entityFiles);
			PreyLogger.i("Report sent: " + preyHttpResponse.getResponseAsString());
		} catch (Exception e) {
			PreyLogger.e("Report wasn't send:"+e.getMessage(),e);
		} 
		return preyHttpResponse;
	}
	
	public void sendPreyHttpEvent(Context ctx, Event event, JSONObject jsonObject){
		try {
			String url =getEventsUrlJson(ctx)+".json";
			Map<String, String> parameters = new HashMap<String, String>();
			parameters.put("name", event.getName());
			parameters.put("info", event.getInfo());
			
			PreyLogger.d("sendPreyHttpEvent url:"+url);
			PreyLogger.d("name:"+event.getName()+" info:"+event.getInfo());
			
			//Toast.makeText(ctx, "Event:"+event.getName(), Toast.LENGTH_LONG).show();
			String status=jsonObject.toString();
			PreyHttpResponse preyHttpResponse= PreyRestHttpClient.getInstance(ctx).postStatusAutentication(url, status, parameters, PreyConfig.getPreyConfig(ctx));
			runActionJson(ctx,preyHttpResponse);
		} catch (Exception e) {
			PreyLogger.i("message:"+e.getMessage());
			PreyLogger.e("Event wasn't send",e);
		} 
	}
	
	public void runActionJson(Context ctx,PreyHttpResponse preyHttpResponse) throws Exception{
		StringBuilder jsonString=PreyRestHttpClient.getInstance(ctx).getStringHttpResponse(preyHttpResponse.getResponse());
		if (jsonString!=null&&jsonString.length()>0){
			List<JSONObject> jsonObjectList=new JSONParser().getJSONFromTxt(ctx, jsonString.toString());
			if (jsonObjectList!=null&&jsonObjectList.size()>0){
				PreyActionsRunner.runActionJson(ctx,jsonObjectList);
			}
		}
	}
	
	public boolean checkPassword(Context ctx, String email, String password) throws PreyException {
		String xml = this.checkPassword(email, password, ctx);
		return xml.contains("<key");
	}

	public String checkPassword(String email, String password, Context ctx) throws PreyException {
		PreyConfig preyConfig = PreyConfig.getPreyConfig(ctx);
		HashMap<String, String> parameters = new HashMap<String, String>();
		String xml;
		try {
			xml = PreyRestHttpClient.getInstance(ctx).get(PreyConfig.getPreyConfig(ctx).getPreyUrl().concat("profile.xml"), parameters, preyConfig, email, password)
					.getResponseAsString();
		} catch (IOException e) {
			throw new PreyException(ctx.getText(R.string.error_communication_exception).toString(), e);
		}

		return xml;
	}
	
	public String getDeviceWebControlPanelUiUrl(Context ctx) throws PreyException {
		PreyConfig preyConfig = PreyConfig.getPreyConfig(ctx);
		String deviceKey = preyConfig.getDeviceID();
		if (deviceKey == null || deviceKey == "")
			throw new PreyException("Device key not found on the configuration");
		String apiv2=FileConfigReader.getInstance(ctx).getApiV2();
		return PreyConfig.getPreyConfig(ctx).getPreyUrl().concat(apiv2).concat("devices/").concat(deviceKey);
	}
	public String deleteDevice(Context ctx) throws PreyException {
		PreyConfig preyConfig = PreyConfig.getPreyConfig(ctx);
		HashMap<String, String> parameters = new HashMap<String, String>();
		String xml;
		try {
			String url=this.getDeviceWebControlPanelUiUrl(ctx);
			PreyHttpResponse response=PreyRestHttpClient.getInstance(ctx)
					.delete(url, parameters, preyConfig);
			PreyLogger.d(response.toString());
			xml = response.getResponseAsString();

		} catch (IOException e) {
			throw new PreyException(ctx.getText(R.string.error_communication_exception).toString(), e);
		}
		return xml;
	}
}
