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


/**
 * 
 * Please take the latest Helper from the original Project (Expresso Perfect)
 *
 */

public class Printanem {
	public static final String AUTHORITY = "com.menadinteractive.printer";
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);
	public static final String CONTENT_TYPE_BTABLE = "vnd.menad.printer/btable";

	public static final String PARAM_TYPEPRINT="typeprint";
	public static final String TYPE_BTABLE="btable";	
	public static final String PARAM_ISSIGN="issign";
	public static final String PARAM_PATHFILE="pathfile";


	public static final String APP="printanem2.apk";

	public static final String CodeClient="CodeClient";
	public static int availableVersion = 0;

	/** get response version */
	public static final String RESPONSE_VERSION_INTENT = "com.menadinteractive.expressoperfect.intent.action.RESPONSE_VERSION";
	public static final String REQUEST_VERSION_INTENT = "com.menadinteractive.expressoperfect.intent.action.REQUEST_VERSION";
	public static final String VERSION = "com.menadinteractive.expressoperfect.VERSION";

	public static  void showPrinterApp(Activity context, int requestCode,String pathfiletoprint, String typeprinter,boolean issign){
		try{
			Uri accountUri = Printanem.CONTENT_URI;
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setDataAndType(accountUri, Printanem.CONTENT_TYPE_BTABLE);
			Bundle b = new Bundle();
			b.putString(PARAM_TYPEPRINT, typeprinter); 
			b.putBoolean(PARAM_ISSIGN, issign);//on lance la signature avant ?
			b.putString(PARAM_PATHFILE, pathfiletoprint);//on lance la signature avant ?
						
			intent.putExtras(b);
			
			context.startActivityForResult(intent, requestCode);
		}
		catch (Exception e){
			//Debug.StackTrace(e);
		}
	}
	




	/** Added from Negos */
	public static boolean isNewVersionAvailable(int currentVersion)
	{
		String versionDispo=Global.dbParam.getLblAllSoc(Global.dbParam.PARAM_MINVER, "VERPR");
		//Debug.Log("new version VERPR: "+versionDispo);
		int availableVersion = Fonctions.convertToInt(versionDispo);
		//Debug.Log("available version :"+availableVersion);
		if(availableVersion > currentVersion){
			Printanem.availableVersion = availableVersion;
			return true;
		}
		return false;	
	}
	
	static String packageName = "com.menadinteractive.printer";
	
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
