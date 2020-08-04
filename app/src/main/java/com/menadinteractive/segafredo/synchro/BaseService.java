package com.menadinteractive.segafredo.synchro;

import com.menadinteractive.maxpoilane.Debug;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class BaseService  extends Service{
	/** Constants */
	public static final int MESSAGE_PROCESS_NEXT_SCENARIO = 204;
	public static final String UPDATE_UI_PROGRESS_BAR_INTENT = "com.menadinteractive.chanflor.intent.action.UPDATE_UI_PROGRESS_BAR";
	public static final String UPDATE_UI_POINT_OF_INTERESTS = "com.menadinteractive.chanflor.intent.action.UPDATE_UI_POINT_OF_INTERESTS";

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	protected void broadcastUIUpdatePI(){
		broadcastAction(UPDATE_UI_POINT_OF_INTERESTS);
	}

	protected void broadcastUIProgressBar(){
		broadcastAction(UPDATE_UI_PROGRESS_BAR_INTENT);

	}

	private void broadcastAction(String action){
		Debug.Log("broadcast : "+action);
		Intent i = new Intent();
		i.setAction(action);
		this.sendBroadcast(i);
	}

}
