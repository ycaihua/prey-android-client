/*******************************************************************************
 * Created by Carlos Yaconi
 * Copyright 2012 Fork Ltd. All rights reserved.
 * License: GPLv3
 * Full license at "/LICENSE"
 ******************************************************************************/
package com.prey;

import com.prey.managers.PreyConnectivityManager;
import com.prey.net.PreyWebServices;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.os.Build;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;

public class PreyConfig {

	private Context ctx;
	private static PreyConfig instance=null;
	
	public static final String TAG = "PREY";
	public static final String VERSION_PREY_DEFAULT="1.2.6";
	public static final String PREFS_DEVICE_ID = "DEVICE_ID";
	public static final String NOTIFICATION_ID="NOTIFICATION_ID";
	public static final String PREFS_API_KEY="PREFS_API_KEY";
	public static final String NEXT_ALERT="NEXT_ALERT";
	public static final String PREFS_IS_MISSING="PREFS_IS_MISSING";
	public static final String INTERVAL_REPORT="INTERVAL_REPORT";
	public static final String LAST_REPORT_START_DATE="LAST_REPORT_START_DATE";
	public static final String PREFS_SECURITY_PROMPT_SHOWN="PREFS_SECURITY_PROMPT_SHOWN";
	public static final String PREFS_DISABLE_POWER_OPTIONS="PREFS_DISABLE_POWER_OPTIONS";
	public static final String PREFS_EMAIL="PREFS_EMAIL";
	public static final String SIM_SERIAL_NUMBER="SIM_SERIAL_NUMBER";
	public static final String PREVIOUS_SSID="PREVIOUS_SSID";
	public static final String LAST_EVENT="LAST_EVENT";
	public static final String PREFS_ACCOUNT_VERIFIED="PREFS_ACCOUNT_VERIFIED";
	public static final String IS_CAMOUFLAGE_SET="IS_CAMOUFLAGE_SET";
	public static final String PREFS_SCHEDULED="PREFS_SCHEDULED";
	public static final String WEB_CAM="webcam";
	
	//Set false in production
	public static final boolean LOG_DEBUG_ENABLED = false;
	
	// Set to 1000 * 60 in production.
	public static final long DELAY_MULTIPLIER = 1000 * 60; 
	
	// the minimum time interval for GPS notifications, in milliseconds (default 60000).
	public static final long LOCATION_PROVIDERS_MIN_REFRESH_INTERVAL = 10000;
	
	// the minimum distance interval for GPS notifications, in meters (default 20)
	public static final float LOCATION_PROVIDERS_MIN_REFRESH_DISTANCE = 20;
	
	// max "age" in ms of last location (default 120000).
	public static final float LAST_LOCATION_MAX_AGE = 30000;
	
	private boolean froyoOrAbove;
	private boolean cupcakeOrAbove;
	private boolean gingerbreadOrAbove;
	private boolean kitKatOrAbove;
	private boolean jellyBeanOrAbove;
	private boolean iceCreamOrAbove;
	private boolean honeycombOrAbove;
	private boolean eclairOrAbove;
	private boolean run;
	private boolean registerC2dm=false;
	private String apiKey;
	private String deviceID;
	private boolean nextAlert; 
	private boolean missing;
	private boolean locked;
	private boolean disablePowerOptions;
	private String simSerialNumber;
	private String previousSsid;
	private String lastEvent;
	private String email;
	private boolean camouflageSet;
	private String notificationId;
	private boolean securityPrivilegesAlreadyPrompted;

	private PreyConfig(Context ctx) {
		this.ctx = ctx;
		this.kitKatOrAbove = Integer.parseInt(Build.VERSION.SDK) >= 19;
		this.jellyBeanOrAbove = Integer.parseInt(Build.VERSION.SDK) >= 16;
		this.iceCreamOrAbove = Integer.parseInt(Build.VERSION.SDK) >= 15;
		this.honeycombOrAbove = Integer.parseInt(Build.VERSION.SDK) >= 13;
		this.gingerbreadOrAbove = Integer.parseInt(Build.VERSION.SDK) >= 9;
		this.froyoOrAbove = Integer.parseInt(Build.VERSION.SDK) >= 8;
		this.eclairOrAbove = Integer.parseInt(Build.VERSION.SDK) >=5;
		this.cupcakeOrAbove = Integer.parseInt(Build.VERSION.SDK) == 3;
		
		this.deviceID = getString(PreyConfig.PREFS_DEVICE_ID, "");
		this.apiKey = getString(PreyConfig.PREFS_API_KEY, "");
		
		this.nextAlert=getBoolean(PreyConfig.NEXT_ALERT, false);
		this.email = getString(PreyConfig.PREFS_EMAIL, "");
	}
	
	public static PreyConfig getPreyConfig(Context ctx) {
		if (instance==null){
			instance = new PreyConfig(ctx);
		}
		return instance;
	}
	
	private void saveString(String key, String value){
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(ctx);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(key, value);
		editor.commit();
	}
	
	private String getString(String key, String defaultValue){
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(ctx);
		return settings.getString(key, defaultValue);
	}

	
	private void saveBoolean(String key, boolean value){
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(ctx);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}
	private boolean getBoolean(String key, boolean defaultValue){
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(ctx);
		return settings.getBoolean(key, defaultValue);
	}
	
	private void saveLong(String key, long value){
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(ctx);
		SharedPreferences.Editor editor = settings.edit();
		editor.putLong(key, value);
		editor.commit();
	}
	
	private long getLong(String key, long defaultValue){
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(ctx);
		return settings.getLong(key, defaultValue);
	}
	
	public void registerC2dm(){
		if (PreyEmail.getEmail(this.ctx) != null) {
			PreyLogger.d("______________________");
			PreyLogger.d("______________________");
			PreyLogger.d("___ registerC2dm _____");
			PreyLogger.d("______________________");
			PreyLogger.d("______________________");
			PreyLogger.d("______________________");
			Intent registrationIntent = new Intent("com.google.android.c2dm.intent.REGISTER");
			registrationIntent.putExtra("app", PendingIntent.getBroadcast(this.ctx, 0, new Intent(), 0)); // boilerplate
			String gcmId= FileConfigReader.getInstance(this.ctx).getGcmId();
			//PreyLogger.i("gcmId:"+gcmId);
			registrationIntent.putExtra("sender",gcmId);
			this.ctx.startService(registrationIntent);
		}
	}
	
	public void unregisterC2dm(boolean updatePrey){
		if (updatePrey)
			PreyWebServices.getInstance().setPushRegistrationId(ctx, "");
		Intent unregIntent = new Intent("com.google.android.c2dm.intent.UNREGISTER");
		unregIntent.putExtra("app", PendingIntent.getBroadcast(this.ctx, 0, new Intent(), 0));
		this.ctx.startService(unregIntent);
		
	}
	
	public String getPreyVersion() {
		String versionName=VERSION_PREY_DEFAULT;
		try{
			PackageInfo pinfo =ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), 0);
		 	versionName = pinfo.versionName;
		}catch(Exception e){
		}
		return versionName;
	}
	
	public boolean isThisDeviceAlreadyRegisteredWithPrey() {
		String deviceId = getString(PreyConfig.PREFS_DEVICE_ID, null);
		return deviceId != null;
	}
	
	public boolean isFroyoOrAbove() {
		return froyoOrAbove;
	}

	public boolean isCupcakeOrAbove() {
		return cupcakeOrAbove;
	}

	public boolean isGingerbreadOrAbove() {
		return gingerbreadOrAbove;
	}
	
	public boolean isKitKatOrAbove() {
		return kitKatOrAbove;
	}
	public boolean isJellyBeanOrAbove() {
		return jellyBeanOrAbove;
	}
	public boolean isIceCreamOrAbove() {
		return iceCreamOrAbove;
	}
	public boolean isHoneycombOrAbove() {
		return honeycombOrAbove;
	}
	public boolean isEclairOrAbove() {
		return eclairOrAbove;
	}
	
	public boolean isRun() {
		return run;
	}

	public void setRun(boolean run) {
		this.run = run;
	}
	
	
	public void setNotificationId(String notificationId){
		this.notificationId=notificationId;
		saveString(PreyConfig.NOTIFICATION_ID, notificationId);
	}
	
	public String getNotificationId(){
		return getString(PreyConfig.NOTIFICATION_ID,notificationId);
	}
	
	public void setRegisterC2dm(boolean registerC2dm){
		this.registerC2dm=registerC2dm;
	}
	public boolean isRegisterC2dm(){
		return registerC2dm;
	}
	
	private static final String HTTP="https://";
	
	public String getPreyUrl() {
		String domain = FileConfigReader.getInstance(this.ctx).getPreyDomain();
		String subdomain = FileConfigReader.getInstance(this.ctx).getPreySubdomain();
		return HTTP.concat(subdomain).concat(".").concat(domain).concat("/");
	}
	
	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
		this.saveString(PreyConfig.PREFS_API_KEY, apiKey);
	}
	public String getDeviceID() {
		return deviceID; 
	}

	public void setDeviceID(String deviceID) {
		this.deviceID = deviceID;
		this.saveString(PreyConfig.PREFS_DEVICE_ID, deviceID);
	}
	
	public void setNextAlert(boolean nextAlert){
		this.nextAlert=nextAlert;
		saveBoolean(PreyConfig.NEXT_ALERT, nextAlert);
	}
	
	public boolean isNextAlert(){
		return nextAlert;
	}
	
	public boolean isConnectionExists() {
		boolean isConnectionExists = false;
		// There is wifi connexion?
		if (PreyConnectivityManager.getInstance(ctx).isWifiConnected()) {
			isConnectionExists = true;
		}
		// if there is no connexion wifi, verify mobile connection?
		if (!isConnectionExists && PreyConnectivityManager.getInstance(ctx).isMobileConnected()) {
			isConnectionExists = true;
		}
		return isConnectionExists;
	}
	
	public boolean isMissing() {
		return missing;
	}

	public void setMissing(boolean missing) {
		this.missing = missing;
		this.saveString(PreyConfig.PREFS_IS_MISSING, Boolean.valueOf(missing).toString());
	}
	public String getIntervalReport(){
		return getString(PreyConfig.INTERVAL_REPORT, null);
	}
	
	public void setIntervalReport(String intervalReport) {
		this.saveString(PreyConfig.INTERVAL_REPORT,intervalReport);
	}
	
	public void setLastReportStartDate(long lastReportStartDate){
		saveLong(PreyConfig.LAST_REPORT_START_DATE, lastReportStartDate);
	}
	
	public long getLastReportStartDate(){
		return getLong(PreyConfig.LAST_REPORT_START_DATE, 0);
	}
	public boolean isLockSet() {
		return locked;
	}
	public void setLock(boolean locked) {
		this.locked = locked;
	}
	
	public void setSecurityPrivilegesAlreadyPrompted(boolean securityPrivilegesAlreadyPrompted) {
		this.securityPrivilegesAlreadyPrompted = securityPrivilegesAlreadyPrompted;
		saveBoolean(PreyConfig.PREFS_SECURITY_PROMPT_SHOWN, securityPrivilegesAlreadyPrompted);
	}
	
	public boolean isSecurityPrivilegesAlreadyPrompted(){
		return getBoolean(PreyConfig.PREFS_SECURITY_PROMPT_SHOWN, securityPrivilegesAlreadyPrompted);
	}
	
	public boolean getDisablePowerOptions() {
		return disablePowerOptions;
	}

	public void setDisablePowerOptions(boolean disablePowerOptions) {
		this.disablePowerOptions = disablePowerOptions;
		saveBoolean(PreyConfig.PREFS_DISABLE_POWER_OPTIONS, disablePowerOptions);
	}
	
	public boolean isDisablePowerOptions(){
		return disablePowerOptions;
	}
	public String getPreyPanelUrl() {
		String panel = FileConfigReader.getInstance(this.ctx).getPreyPanel();
		String url=HTTP.concat(panel).concat(".").concat(getPreyDomain()).concat("/").concat(getPreyCampaign());
		PreyLogger.i(url);
		return url;
	}
 
	public void saveAccount(PreyAccountData accountData) {
		this.saveSimInformation();
		setDeviceID(accountData.getDeviceId());
		setApiKey( accountData.getApiKey());
		setEmail( accountData.getEmail());
		setMissing(Boolean.valueOf(accountData.isMissing()));
	}

	public void saveSimInformation() {
		TelephonyManager telephonyManager = (TelephonyManager)ctx.getSystemService(Context.TELEPHONY_SERVICE);
		String simSerial=telephonyManager.getSimSerialNumber();
		if(simSerial!=null){
			this.setSimSerialNumber(simSerial);
		}
		 
	}
	public String getPreyDomain() {
		return FileConfigReader.getInstance(this.ctx).getPreyDomain();
	}
	
	public String getPreyCampaign() {
		return FileConfigReader.getInstance(this.ctx).getPreyCampaign();
	}
	
	public String getSimSerialNumber(){
		return this.getString(PreyConfig.SIM_SERIAL_NUMBER,simSerialNumber);
	}
	
	public void setSimSerialNumber(String simSerialNumber) {
		this.simSerialNumber=simSerialNumber;
		this.saveString(PreyConfig.SIM_SERIAL_NUMBER,simSerialNumber);
	}
	
	public boolean isSimChanged() {
		TelephonyManager telephonyManager = (TelephonyManager)ctx.getSystemService(Context.TELEPHONY_SERVICE);
		String simSerial=telephonyManager.getSimSerialNumber();
		PreyLogger.i("simSerial:"+simSerial+" actual:"+this.simSerialNumber);
		if (this.simSerialNumber==null||"".equals(this.simSerialNumber)){
			if(simSerial!=null&&!"".equals(simSerial)){
				this.setSimSerialNumber(simSerial);
			}
			return false;
		}
		if(simSerial!=null&&!"".equals(simSerial)&&!simSerial.equals(this.getSimSerialNumber())){
			return true;
		}
		return false;
	}
	
	public void setPreviousSsid(String previousSsid) {
		this.saveString(PreyConfig.PREVIOUS_SSID, previousSsid);
	}
	
	public String getPreviousSsid() {
		return getString(PreyConfig.PREVIOUS_SSID, previousSsid);
	}
	
	public void setLastEvent(String lastEvent) {
		this.lastEvent = lastEvent;
		saveString(PreyConfig.LAST_EVENT, lastEvent);
	}
	
	public String getLastEvent() {
		return getString(PreyConfig.LAST_EVENT, lastEvent);
	}
	
	public void setEmail(String email) {
		this.email = email;
		saveString(PreyConfig.PREFS_EMAIL, lastEvent);
	}
	
	public String getEmail() {
		return getString(PreyConfig.PREFS_EMAIL, email);
	}
	
	public void setAccountVerified() {
		saveBoolean(PreyConfig.PREFS_ACCOUNT_VERIFIED, true);
	}

	public boolean isAccountVerified() {
		return getBoolean(PreyConfig.PREFS_ACCOUNT_VERIFIED, false);
	}
	
	public void setCamouflageSet(boolean camouflageSet){
		this.camouflageSet=camouflageSet;
		this.saveBoolean(PreyConfig.IS_CAMOUFLAGE_SET,camouflageSet);
	}
	
	public boolean isCamouflageSet(){
		return getBoolean(PreyConfig.IS_CAMOUFLAGE_SET,camouflageSet);
	}
	
	public void wipeData() {
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(ctx);
		SharedPreferences.Editor editor = settings.edit();
		editor.clear();
		editor.commit();
	}

}

