package com.menadinteractive.segafredo.plugins;

import com.menadinteractive.segafredo.communs.Fonctions;
import com.menadinteractive.segafredo.communs.Global;
import com.menadinteractive.segafredo.db.TableClient;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;


public class Agendanem {
	public static final String AUTHORITY = "com.menadinteractive.agenda";
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);
	public static final String CONTENT_TYPE = "vnd.menad.cursor.dir/agenda";
	public static final String CONTENT_ITEM_TYPE = "vnd.menad.cursor.item/agenda";

	public static final String ApplicationName="ApplicationName";
	public static final String ApplicationKey="ApplicationKey";
	public static final String CodeClient="CodeClient";
	public static final String AdresseClient="AdresseClient";
	public static final String NameClient="NameClient";

	public static final String APP="agendanem2.apk";

	/** get response version */
	public static final String RESPONSE_VERSION_INTENT = "com.menadinteractive.agendanem.intent.action.RESPONSE_VERSION";
	public static final String REQUEST_VERSION_INTENT = "com.menadinteractive.agendanem.intent.action.REQUEST_VERSION";
	public static final String VERSION = "com.menadinteractive.agendanem.VERSION";
	
	/** get codeClients list */
	public static final String RESPONSE_CLIENTS_SCHEDULED_INTENT = "com.menadinteractive.agendanem.intent.action.RESPONSE_CLIENTS_SCHEDULED";
	public static final String REQUEST_CLIENTS_SCHEDULED_INTENT = "com.menadinteractive.agendanem.intent.action.REQUEST_CLIENTS_SCHEDULED";
	public static final String CLIENTS_SCHEDULED = "com.menadinteractive.agendanem.CLIENTS_SCHEDULED";
	
	public static int availableVersion = 0;
	public static int currentVersion = 0;
	
	public static IntentFilter getVersionFilter(){
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(RESPONSE_VERSION_INTENT);  
		return intentFilter;
	}
	
	public static IntentFilter getClientsScheduledFilter(){
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(RESPONSE_CLIENTS_SCHEDULED_INTENT);
		return intentFilter;
	}



	public static  void showAllEvents(Activity context, int requestCode, String applicationName, String applicationKey){
		try{
			Uri accountUri = Agendanem.CONTENT_URI;
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setDataAndType(accountUri, Agendanem.CONTENT_TYPE);
			Bundle b = new Bundle();
			b.putString(ApplicationName, applicationName);
			b.putString(ApplicationKey, applicationKey);
			intent.putExtras(b);
			context.startActivityForResult(intent, requestCode);
		}
		catch (Exception e){
			//Debug.StackTrace(e);
		}
	}



	public static  void showEvents(Activity context, int requestCode, String applicationName, String applicationKey, TableClient.structClient client){
		try{
			Uri accountUri = Agendanem.CONTENT_URI;
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setDataAndType(accountUri, Agendanem.CONTENT_ITEM_TYPE);
			Bundle b = new Bundle();
			b.putString(ApplicationName, applicationName);
			b.putString(ApplicationKey, applicationKey);
			b.putString(CodeClient, client.CODE);
			b.putString(AdresseClient, client.getFullAddress());
			b.putString(NameClient, client.NOM);
			intent.putExtras(b);
			context.startActivityForResult(intent, requestCode);
		}
		catch (Exception e){
		//	Debug.StackTrace(e);
		}
	}

	public static void requestAgendanemVersion(Context context){
		Intent intent=new Intent();
		intent.setAction(REQUEST_VERSION_INTENT);
		context.sendBroadcast(intent);
	}
	
	public static void requestScheduledClients(Context context, String applicationKey){
		Intent intent=new Intent();
		intent.setAction(REQUEST_CLIENTS_SCHEDULED_INTENT);
		Bundle b = new Bundle();
		b.putString(ApplicationKey, applicationKey);
		intent.putExtras(b);
		context.sendBroadcast(intent);
	}
	
	public static boolean isAgendanemNewVersionAvailable(int currentVersion)
	{
		String versionDispo=Global.dbParam.getLblAllSoc(Global.dbParam.PARAM_MINVER, "VERAG");
		int availableVersion = Fonctions.convertToInt(versionDispo);
		//Debug.Log("available version :"+availableVersion);
		if(availableVersion > currentVersion){
			Agendanem.availableVersion = availableVersion;
			return true;
		}
		return false;	
	}
	
	static String packageName = "com.menadinteractive.agendanem";
	
	
	public static Drawable getAppIcon(Context context){
		Drawable result = null;
		try{
			result =context.getPackageManager().getApplicationIcon(packageName);
		} catch (Exception e) {
		//	Debug.StackTrace(e);
		}
		return result;
	}
	
	public static int getVersion(Context c, StringBuffer sbVer) {
		int v = 0;
		try {
			v = c.getPackageManager().getPackageInfo(packageName, 0).versionCode;
			String version=c.getPackageManager().getPackageInfo(packageName, 0).versionName;

			String ver=String.valueOf(v)+"."+version;
			sbVer.append(ver);
			ver=ver.replace(".", "");
			return Integer.parseInt(ver);

		} catch (NameNotFoundException e) {
			// Huh? Really?
		}
		return 0;
	}
	
}
