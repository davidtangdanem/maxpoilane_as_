package com.menadinteractive.segafredo.plugins;

import com.menadinteractive.segafredo.communs.Fonctions;
import com.menadinteractive.segafredo.communs.Global;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;


public class Questionem {
	public static final String AUTHORITY = "com.menadinteractive.quest";
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);
	public static final String CONTENT_QUEST_FILLER = "vnd.menad.cursor.dir/quest";
	public static final String CONTENT_QUEST_MAKER = "vnd.menad.cursor.item/quest";

	public static final String ApplicationName="ApplicationName";
	public static final String ApplicationKey="ApplicationKey";
	public static final String CodeClient="CodeClient";
	public static final String AdresseClient="AdresseClient";
	public static final String NameClient="NameClient";

	public static final String APP="questionem2.apk";

	/** get response version */
	public static final String RESPONSE_VERSION_INTENT = "com.menadinteractive.quest.intent.action.RESPONSE_VERSION";
	public static final String REQUEST_VERSION_INTENT = "com.menadinteractive.quest.intent.action.REQUEST_VERSION";
	public static final String VERSION = "com.menadinteractive.quest.VERSION";
	
	public static int availableVersion = 0;
	public static int currentVersion = 0;
	
	public static IntentFilter getVersionFilter(){
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(RESPONSE_VERSION_INTENT);  
		return intentFilter;
	}

	public static void requestQuestionemVersion(Context context){
		Intent intent=new Intent();
		intent.setAction(REQUEST_VERSION_INTENT);
		context.sendBroadcast(intent);
	}
	

	public static boolean isQuestionemNewVersionAvailable(int currentVersion)
	{
		String versionDispo=Global.dbParam.getLblAllSoc(Global.dbParam.PARAM_MINVER, "VERQU");
		int availableVersion = Fonctions.convertToInt(versionDispo);
		//Debug.Log("available version :"+availableVersion);
		if(availableVersion > currentVersion){
			Questionem.availableVersion = availableVersion;
			return true;
		}
		return false;	
	}
	
	public static  void showQuestMaker(Activity context, int requestCode, String applicationName, String applicationKey, Bundle params){
		try{
			Uri accountUri = Questionem.CONTENT_URI;
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setDataAndType(accountUri, Questionem.CONTENT_QUEST_MAKER);

			intent.putExtras(params);
			context.startActivityForResult(intent, requestCode);
		}
		catch (Exception e){
		//	Debug.StackTrace(e);
		}
	}
	public static  void showQuestFiller(Activity context, int requestCode, String applicationName, String applicationKey, Bundle params){
		try{
			Uri accountUri = Questionem.CONTENT_URI;
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setDataAndType(accountUri, Questionem.CONTENT_QUEST_FILLER);

			intent.putExtras(params);
			context.startActivityForResult(intent, requestCode);
		}
		catch (Exception e){
		//	Debug.StackTrace(e);
		}
	}
	
	static String packageName = "com.menadinteractive.quest";
	
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
