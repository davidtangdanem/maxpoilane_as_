package com.menadinteractive.segafredo.synchro;

import java.util.ArrayList;
import java.util.Date;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Message;

import com.menadinteractive.maxpoilane.Debug;
import com.menadinteractive.maxpoilane.app;
import com.menadinteractive.segafredo.communs.Fonctions;
import com.menadinteractive.segafredo.communs.MyWS;
import com.menadinteractive.segafredo.db.Preferences;
import com.menadinteractive.segafredo.plugins.Espresso;

public class GpsMonitorService extends BaseService{
	public static boolean isLaunched = false;
	protected app m_appState;
	private Handler handler;

	public static long lastUpdate = 0;
	public static long updatePlannedIn = 0;



	/** Scenario */
	ArrayList<String> scenarios;
	private static String currentScenario="";
	final String scenario_monitor_gps = "GPSPOS";

	public static void triggerService(Context context){
		Debug.Log("TriggerService GPS Monitoring ");

		if(!isLaunched){
			Intent intentService = new Intent(context, GpsMonitorService.class);
			context.startService(intentService);
		}
		else{
			Debug.Log("Service already running on "+currentScenario);
		}
	}

	public static boolean isServiceRunning(){
		return isLaunched;
	}

	private void initScenrios(){
		scenarios = new ArrayList<String>();
		scenarios.add(scenario_monitor_gps);
	}
	@Override
	public void onCreate() {
		super.onCreate();
		Debug.Log("TAG2", "Service On Create GPS Monitoring");
		isLaunched = true;
		m_appState = ((app)getApplicationContext());
		initScenrios();
		if(handler == null)
			handler = getHandler();

		handler.sendEmptyMessage(MESSAGE_PROCESS_NEXT_SCENARIO);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		isLaunched = false;
		broadcastUIProgressBar();
		Debug.Log("TAG2", "Service GPS On Destroy");

	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);

	}
	private void stopService(){
		stopSelf();
	}

	/** Handler */
	Handler getHandler(){
		return new Handler(){
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);

				switch(msg.what) {
				case MESSAGE_PROCESS_NEXT_SCENARIO:
					lastUpdate = System.currentTimeMillis();
					broadcastUIProgressBar();
					if(scenarios.size()>0){
						String nextScenario = scenarios.remove(0);
						currentScenario = nextScenario;
						Debug.Log("TAG2", "process next scenario (GPS Monitor Service): "+nextScenario);
						//						WS_onThread("POWERD", "RED", "ExecExInsertHdr", scenario_fermetures);
						if(currentScenario != null && currentScenario.equals(scenario_monitor_gps)){
							LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
							Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
							if(location != null){
								String date = Fonctions.getYYYYMMDDhhmmss(new Date(location.getTime()));
								Debug.Log("TAG2", "Date : "+date);
								Debug.Log("TAG2", "Date from location: "+Fonctions.getYYYYMMDDhhmmss(new Date(location.getTime())));
								Debug.Log("TAG2", "Location : "+location.toString());
								String login = Preferences.getValue(GpsMonitorService.this, Espresso.LOGIN, "0");
								String password = Preferences.getValue(GpsMonitorService.this, Espresso.PASSWORD, "0");
								WS_onThread(login, password, "SUM_SendLocationCoordEx2", nextScenario, String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()), date, String.valueOf((int)(location.getSpeed()*3.6)));
							}
						}
					}
					else{
						stopSelf();
					}
					//					broadcastUIProgressBar();
					break;

					//				case MESSAGE_INVALIDATE_MAP:
					//					if(mapView != null)
					//						mapView.invalidate();
					//				break;
				}

			}
		};
	}


	private void WS_onThread(final String user, final String pwd, final String wsFonction, final String scenario, final String lat, final String lon, final String date, final String vitesse){
		try
		{
			new Thread( ){

				@Override
				public void run() 
				{
					this.setName("Thread_"+scenario);
					WS(user, pwd, wsFonction, scenario, lat, lon, date, vitesse);
				}
			}.start();			 


		}
		catch(Exception ex)
		{

		}
	}

	private void WS(String user, String pwd, String wsFonction, String scenario,  String lat, String lon, String date, String vitesse){
		StringBuilder err=new StringBuilder();

/*
		MyWS.Result resultWS=new MyWS.Result();
		resultWS=MyWS.WSSendGPS(user,pwd,wsFonction,scenario,"",lat,lon,date,vitesse,false);
		Debug.Log("TAG2", "WS");
		Debug.Log(resultWS.toString());
		Debug.Log(resultWS.isConnSuccess()+"");
		Debug.Log(resultWS.getContent());
		if (resultWS.isConnSuccess()==false)
		{
			if(handler != null)
				handler.sendEmptyMessage(MESSAGE_PROCESS_NEXT_SCENARIO);
		}

*/


	}


}
