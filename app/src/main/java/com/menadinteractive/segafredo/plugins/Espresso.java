package com.menadinteractive.segafredo.plugins;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import com.menadinteractive.maxpoilane.Debug;
import com.menadinteractive.segafredo.communs.Fonctions;
import com.menadinteractive.segafredo.communs.Global;


/**
 * 
 * This is a helper class
 * Put it in the main project !
 *
 */
public class Espresso {
	public static final String AUTHORITY = "com.menadinteractive.chanflor";
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);
	public static final String CONTENT_TYPE = "vnd.menad.cursor.dir/espresso";
	public static final String CONTENT_ITEM_TYPE = "vnd.menad.cursor.item/espresso";

	public static final String LOGIN="login";
	public static final String MACPRINTER="macprinter";
	public static final String INVPARTIEL="invpartiel";
	public static final String LIVREUR="livreur";
	public static final String PREF_MODETEST="modetest";
	public static final String REG_DATEDERINV="DATEDERINV";
	public static final String REG_DATEDERINV_RAPPEL="DATEDERINV_RAPPEL";
	public static final String PREF_SOUCHE="souches";
	//public static final String PREF_SSOUCHE="souche";
	public static final String PREF_RESET="reset";
	public static final String PREF_RESETLOG="resetlog";
	public static final String PASSWORD="password";
	public static final String DEPOT="depot";
	public static final String WS_URL="ws_url";
	public static final String IP="ip";
	public static final String SSL="SSL";
	public static final String TIMER_MONITORING_GPS_SECONDS="timer_monitoring_gps";
	public static final String TIMER_SYNCHRO_SECONDS="timer_synchro";
	public static final String TIMER_UPDATE_PI_SECONDS="timer_update_map";
	public static final String CODE_CLIENT="code_client";
	public static final String TEXTTOPRINT="TEXTTOPRINT";
	public static final String CODE_SOCIETE="soc_code";
	public static final String ZONE="zone";

	public static final String TIMER_FIND_NEAREST_POINT_SECONDS="timer_nearest_point";
	public static final String TIMER_FIND_NEAREST_POINT_SECONDS_DEFAULT="5";

	public static final String CENTER_MAP_TO_SELF="centerMapToSelf";
	public static final String MAP_MODE_TOURNEE="mapModeTournee";

	public static final String APP="espresso2.apk";

	public static final String CodeClient="CodeClient";
	public static int availableVersion = 0;

	/** get response version */
	public static final String RESPONSE_VERSION_INTENT = "com.menadinteractive.chanflor.intent.action.RESPONSE_VERSION";
	public static final String REQUEST_VERSION_INTENT = "com.menadinteractive.chanflor.intent.action.REQUEST_VERSION";
	public static final String VERSION = "com.menadinteractive.chanflor.VERSION";

	public static  void showExpressoMap(Activity context, int requestCode, String socCode, String login, String password, String ws_url, String timerGps, String timerSync, String timerUpdateUI){
		try{
			Uri accountUri = Espresso.CONTENT_URI;
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setDataAndType(accountUri, Espresso.CONTENT_TYPE);
			Bundle b = new Bundle();
			b.putString(LOGIN, login);
			b.putString(CODE_SOCIETE, socCode);
			b.putString(PASSWORD, password);
			b.putString(WS_URL, ws_url);
			b.putString(TIMER_MONITORING_GPS_SECONDS, timerGps);
			b.putString(TIMER_SYNCHRO_SECONDS, timerSync);
			b.putString(TIMER_UPDATE_PI_SECONDS, timerUpdateUI);
			intent.putExtras(b);
			context.startActivityForResult(intent, requestCode);
		}
		catch (Exception e){
			Debug.StackTrace(e);
		}
	}
	
	public static  void showExpressoMap(Activity context, int requestCode, String socCode, String login, String password, String ws_url, String timerGps, String timerSync, String timerUpdateUI, String codeClient){
		try{
			Uri accountUri = Espresso.CONTENT_URI;
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setDataAndType(accountUri, Espresso.CONTENT_TYPE);
			Bundle b = new Bundle();
			b.putString(LOGIN, login);
			b.putString(CODE_SOCIETE, socCode);
			b.putString(PASSWORD, password);
			b.putString(WS_URL, ws_url);
			b.putString(TIMER_MONITORING_GPS_SECONDS, timerGps);
			b.putString(TIMER_SYNCHRO_SECONDS, timerSync);
			b.putString(TIMER_UPDATE_PI_SECONDS, timerUpdateUI);
			b.putString(CODE_CLIENT, codeClient);
			intent.putExtras(b);
			context.startActivityForResult(intent, requestCode);
		}
		catch (Exception e){
			Debug.StackTrace(e);
		}
	}

	public static void requestExpressoVersion(Context context){
		Intent intent=new Intent();
		intent.setAction(REQUEST_VERSION_INTENT);
		context.sendBroadcast(intent);
	}

	public static IntentFilter getVersionFilter(){
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(RESPONSE_VERSION_INTENT);  
		return intentFilter;
	}

	/** Added from Negos */
	public static boolean isExpressoNewVersionAvailable(int currentVersion)
	{
		String versionDispo=Global.dbParam.getLblAllSoc(Global.dbParam.PARAM_MINVER, "VEREX");
		Debug.Log("new version VEREX: "+versionDispo);
		int availableVersion = Fonctions.convertToInt(versionDispo);
		Debug.Log("available version :"+availableVersion);
		if(availableVersion > currentVersion){
			Espresso.availableVersion = availableVersion;
			return true;
		}
		return false;	
	}
	
	
	static String packageName = "com.menadinteractive.chanflor";
	public static Drawable getAppIcon(Context context){
		Drawable result = null;
		try{
			result =context.getPackageManager().getApplicationIcon(packageName);
		} catch (Exception e) {
			Debug.StackTrace(e);
		}
		return result;
	}
	
	
	 

}
