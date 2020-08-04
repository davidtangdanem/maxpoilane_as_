package com.menadinteractive.segafredo.receiver;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.menadinteractive.maxpoilane.Debug;
import com.menadinteractive.segafredo.synchro.SynchroService;

public class SynchroReceiver extends BroadcastReceiver{

	public static String ACTION = "com.menadinteractive.chanflor.intent.action.QUERY_REMOTE_SYNCHRO";

	public static PendingIntent getPendingIntent(Context context){
		Intent i = new Intent();
		i.setAction(SynchroReceiver.ACTION);
		//		StringBuffer sbVer=new StringBuffer();
		//		int version = getVersion(context, sbVer);
		//		i.putExtra(VERSION, version);
		//		Debug.Log("intent received : version : "+version);
		//		context.sendBroadcast(i);




		//		Intent intent = new Intent(this, TimeAlarm.class);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0,
				i, PendingIntent.FLAG_CANCEL_CURRENT);

		return pendingIntent;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		try {
			SynchroService.triggerService(context, false, false);
			Debug.Log("request syncro : "+System.currentTimeMillis());
		} catch (Exception e) {
			Debug.StackTrace(e);
		}
	}

}
