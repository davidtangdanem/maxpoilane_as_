package com.menadinteractive.segafredo.client;

import com.menadinteractive.maxpoilane.R;
import com.menadinteractive.segafredo.communs.GPS;
import com.menadinteractive.segafredo.communs.GPS.GPSInterface;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

public class GeolocalisationClient extends Activity{

	LinearLayout ll_localisation;
	LinearLayout ll_get_position;
	Button button_localisation;
	Button button_get_position;
	GPS gps;
	String latitude;
	String longitude;
	String adress;

	Boolean gpsState;

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_geolocalisation_client);

		ll_localisation = (LinearLayout)findViewById(R.id.ll_localisation);
		ll_get_position = (LinearLayout)findViewById(R.id.ll_get_position);
		button_localisation = (Button)findViewById(R.id.button_localisation);
		button_get_position = (Button)findViewById(R.id.button_get_position);

		initListeners();

		Bundle bundle = this.getIntent().getExtras();
		adress = bundle.getString("adress_client");
		
		gpsState = false;
	}

	@Override
	public void onResume(){
		super.onResume();

		if(gpsState == true){
			gpsState = false;
			gps = new GPS(GeolocalisationClient.this);
			initDialogCancel(gps);
			if(gps.isGPSEnabled() && gps.isNetworkEnabled()){

				if(gps.canGetLocation()){
					sendResult(Double.toString(gps.getLongitude()), Double.toString(gps.getLatitude()));
					gps.stopUsingGPS();
					gpsState = false;
				}else{
					sendResultFalse();
				}
			}

			if(gps.isGPSEnabled() || gps.isNetworkEnabled()){
				if(gps.canGetLocation()){
					sendResult(Double.toString(gps.getLongitude()), Double.toString(gps.getLatitude()));
					gps.stopUsingGPS();
					gpsState = false;
				}else{
					sendResultFalse();
				}
			}
		}
	}

	private void initListeners(){
		button_get_position.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				gps = new GPS(GeolocalisationClient.this);
				initDialogCancel(gps);
				if(gps.isGPSEnabled() && gps.isNetworkEnabled()){

					if(gps.canGetLocation()){
						sendResult(Double.toString(gps.getLongitude()), Double.toString(gps.getLatitude()));
						gps.stopUsingGPS();
					}else{
						sendResultFalse();
					}
				}else if(gps.isGPSEnabled() || gps.isNetworkEnabled()){
					showSettingsAlert(gps);
					gpsState = true;
				}else showSettingsAlert(gps);
			}
		});

		button_localisation.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				geocode(false);
			}
		});
	}

	void geocode(boolean onlyifgpsenabled)
	{
		GPS gps = new GPS(GeolocalisationClient.this, adress);
		initDialogCancel(gps);
		if(gps.isNetworkEnabled()){
			if(gps.canGetLocation()){
				sendResult(Double.toString(gps.getLongitude()), Double.toString(gps.getLatitude()));
				gps.stopUsingGPS();
			}else{
				sendResultFalse();
			}
		}else if (onlyifgpsenabled==false)
			{
			showSettingsAlert(gps);
			}
	}
	
	private void sendResult(String longitude, String latitude){
		Intent returnIntent = new Intent();
		Bundle bundle = new Bundle();
		bundle.putBoolean("exists_result", true);
		bundle.putString("longitude", longitude);
		bundle.putString("latitude", latitude);
		returnIntent.putExtras(bundle);
		setResult(RESULT_OK,returnIntent);
		finish();
	}

	private void sendResultFalse(){
		Intent returnIntent = new Intent();
		Bundle bundle = new Bundle();
		bundle.putBoolean("exists_result", false);
		returnIntent.putExtras(bundle);
		setResult(RESULT_OK,returnIntent);
		finish();
	}
	
	public void initDialogCancel(GPS gps){
		gps.setGPSInterface(new GPSInterface() {
			
			@Override
			public void dialogCancel() {
				onResume();
				
			}
		});
	}
	
	/**
	 * Function to show settings alert dialog
	 * On pressing Settings button will launch Settings Options
	 * */
	public void showSettingsAlert(GPS gps){

		String title = "";
		String message = "";
		String positiveButton = "";
		String negativeButton = "";
		Boolean show = false;
		String settings = "";

		if(!gps.isGPSEnabled() && gps.getMethodUseForLocation().equals("GPS") && gps.isNetworkEnabled()){
			show = true;
			title = getString(R.string.dialog_title_gps_disabled);
			message = getString(R.string.dialog_message_gps_disabled);
			positiveButton = getString(R.string.dialog_pb_gps_disabled_oui);
			negativeButton = getString(R.string.dialog_nb_gps_disabled_non); 
			settings=Settings.ACTION_LOCATION_SOURCE_SETTINGS;
		}

		if(gps.isGPSEnabled() && gps.getMethodUseForLocation().equals("GPS") && !gps.isNetworkEnabled()){
			show = true;
			title = getString(R.string.dialog_title_network_disabled);
			message = getString(R.string.dialog_message_network_disabled_precision);
			positiveButton = getString(R.string.dialog_pb_gps_disabled_oui);
			negativeButton = getString(R.string.dialog_nb_gps_disabled_non); 
			settings=Settings.ACTION_WIFI_SETTINGS;
		}

		if(gps.getMethodUseForLocation().equals("GPS") && !gps.isNetworkEnabled() && !gps.isGPSEnabled()){
			show = true;
			title = getString(R.string.dialog_title_gps_disabled);
			message = getString(R.string.dialog_title_gps_disabled);
			positiveButton = getString(R.string.dialog_pb_gps_disabled_parameter);
			negativeButton = getString(R.string.dialog_nb_gps_disabled_cancel); 
			settings=Settings.ACTION_LOCATION_SOURCE_SETTINGS;
		}

		if(!gps.getMethodUseForLocation().equals("GPS") && !gps.isNetworkEnabled()){
			show = true;
			title = getString(R.string.dialog_title_network_disabled);
			message = getString(R.string.dialog_message_network_disabled);
			positiveButton = getString(R.string.dialog_pb_gps_disabled_parameter);
			negativeButton = getString(R.string.dialog_nb_gps_disabled_cancel); 
			settings=Settings.ACTION_WIFI_SETTINGS;
		}

		final String SettingsCmd=settings;

		if(show == true && !SettingsCmd.equals("")){
			AlertDialog.Builder alertDialog = new AlertDialog.Builder(GeolocalisationClient.this);

			// Setting Dialog Title
			alertDialog.setTitle(title);

			// Setting Dialog Message
			alertDialog.setMessage(message);

			// On pressing Settings button
			alertDialog.setPositiveButton(positiveButton, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int which) {
					Intent intent = new Intent(SettingsCmd);
					GeolocalisationClient.this.startActivity(intent);
					dialog.dismiss();
				}
			});

			// on pressing cancel button
			alertDialog.setNegativeButton(negativeButton, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
					//dialogCancel();
					GeolocalisationClient.this.onResume();
					dialog.dismiss();
				}
			});

			// Showing Alert Message
			alertDialog.show();
		}
	}

}
