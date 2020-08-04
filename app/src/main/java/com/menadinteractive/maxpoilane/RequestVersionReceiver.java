package com.menadinteractive.maxpoilane;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;

public class RequestVersionReceiver extends BroadcastReceiver {
	public static final String RESPONSE_VERSION_INTENT = "com.menadinteractive.chanflor.intent.action.RESPONSE_VERSION";
	public static final String VERSION = "com.menadinteractive.chanflor.VERSION";

	@Override
	public void onReceive(Context context, Intent intent) {
		Intent i = new Intent();
		i.setAction(RESPONSE_VERSION_INTENT);
		StringBuffer sbVer=new StringBuffer();
		int version = getVersion(context, sbVer);
		i.putExtra(VERSION, version);
		Debug.Log("intent received : version : "+version);
		context.sendBroadcast(i);
	}




	static public int getVersion(Context c, StringBuffer sbVer) {
		int v = 0;
		try {
			v = c.getPackageManager().getPackageInfo(c.getApplicationInfo().packageName, 0).versionCode;
			String version=c.getPackageManager().getPackageInfo(c.getApplicationInfo().packageName, 0).versionName;

			String ver=String.valueOf(v)+"."+version;
			sbVer.append(ver);
			ver=ver.replace(".", "");
			return Integer.parseInt(ver);

		} catch (NameNotFoundException e) {
			// Huh? Really?
		}
		return -1;
	}

}
