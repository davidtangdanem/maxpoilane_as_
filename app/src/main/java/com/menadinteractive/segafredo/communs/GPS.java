package com.menadinteractive.segafredo.communs;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.maps.GeoPoint;

import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.NetworkOnMainThreadException;
import android.text.format.Time;
import android.util.Log;


public class GPS extends Service implements LocationListener{

	private final Context mContext;

	// flag for GPS status
	boolean isGPSEnabled = false;

	private GPSInterface gpsi;

	private String mMethod;

	public String getMethodUseForLocation(){
		return mMethod;
	}

	public void setGPSInterface(GPSInterface _gpsi){
		this.gpsi = _gpsi;
	}

	public boolean isGPSEnabled(){
		return isGPSEnabled;
	}

	// flag for network status
	boolean isNetworkEnabled = false;

	public boolean isNetworkEnabled(){
		return isNetworkEnabled;
	}

	// flag for GPS status
	boolean canGetLocation = false;

	Location location;
	double latitude;
	double longitude;
	GeoPoint geoPoint;

	public GeoPoint getGeoPoint(){
		return geoPoint;
	}

	static final long TWO_MNUTES = 120000;

	// The minimum distance to change Updates in meters
	private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters

	// The minimum time between updates in milliseconds
	private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute

	// Declaring a Location Manager
	protected LocationManager locationManager;

	public GPS(Context context) {
		this.mContext = context;
		GeoPoint p = getLocation();
		mMethod = "GPS";
		if(p != null){
			latitude = p.getLatitudeE6()/1E6;
			longitude = p.getLongitudeE6()/1E6;
			location.setLatitude(latitude);
			location.setLongitude(longitude);
			canGetLocation = true;
		}else canGetLocation = false;
	}

	public GPS(Context context, String adress) {
		this.mContext = context;
		String adressTrim = adress.trim();
		GeoPoint p = getLatLongFromAddress(adressTrim);
		mMethod="NETWORK";
		if(p != null){
			latitude = p.getLatitudeE6()/1E6;
			longitude = p.getLongitudeE6()/1E6;
			canGetLocation = true;			
		}else canGetLocation = false;
	}

	private GeoPoint getLatLongFromAddress(String address)
	{      
		long time= System.currentTimeMillis();
		Log.i("tag", "time : "+time);
		try 
		{
			// getting network status
			isNetworkEnabled = checkInternetConnection(mContext);
			
			GeoPoint p = null;
			
			time= System.currentTimeMillis();
			Log.i("tag", "time 4 : "+time);
			//on lance une tache asynchrone pour récupérer les coordonnées
			GeoTask geoTask = new GeoTask(mContext);
			geoTask.execute(address);
			StringBuilder result = geoTask.get();
			time=System.currentTimeMillis();
			Log.i("tag", "time 3 : "+time);
			//si le résultat est nul on procède différemment
			if(result == null){
				Geocoder geoCoder = new Geocoder(mContext); 
				List<Address> addresses = geoCoder.getFromLocationName(address , 1);
				if (addresses.size() > 0) 
				{            
					p = new GeoPoint(
							(int) (addresses.get(0).getLatitude() * 1E6), 
							(int) (addresses.get(0).getLongitude() * 1E6));
					geoPoint = p;
					return p;
				}
			}
			
			p = getGeoPointFromTaskResult(result);

			geoPoint = p;
			time=System.currentTimeMillis();
			Log.i("tag", "time 4 : "+time);
			return p;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			geoPoint = null;
			return null;
		}

	}

	public GeoPoint getLocation() {
		GeoPoint p = null;
		try {
			Location locationGPS = null;
			Location locationNet = null;

			locationManager = (LocationManager) mContext
					.getSystemService(LOCATION_SERVICE);

			// getting GPS status
			isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

			// getting network status
			isNetworkEnabled = checkInternetConnection(mContext);

			if (!isGPSEnabled && !isNetworkEnabled) {
				// no network provider is enabled
			} else {
				this.canGetLocation = true;
				// First get location from Network Provider
				if (isNetworkEnabled) {
					locationManager.requestLocationUpdates(
							LocationManager.NETWORK_PROVIDER,
							MIN_TIME_BW_UPDATES,
							MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
					if (locationManager != null) {
						locationNet = locationManager
								.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
					}
				}
				// if GPS Enabled get lat/long using GPS Services
				if (isGPSEnabled) {
					if (location == null) {
						locationManager.requestLocationUpdates(
								LocationManager.GPS_PROVIDER,
								MIN_TIME_BW_UPDATES,
								MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
						if (locationManager != null) {
							locationGPS = locationManager
									.getLastKnownLocation(LocationManager.GPS_PROVIDER);
						}
					}
				}
			}

			long GPSLocationTime = 0;
			if (null != locationGPS) { GPSLocationTime = locationGPS.getTime(); }

			long NetLocationTime = 0;

			if (null != locationNet) {
				NetLocationTime = locationNet.getTime();
			}

			if ( 0 < GPSLocationTime - NetLocationTime) {
				location =  locationGPS;
			}
			else {
				location = locationNet;
			}

			Time now = new Time();
			now.setToNow();

			//si l'�cart de temps entre l'heure actuelle et l'heure de la derni�re position alors location = null
			if(location != null && location.getTime() - System.currentTimeMillis() < TWO_MNUTES){
				p = new GeoPoint(
						(int) (location.getLatitude() * 1E6), 
						(int) (location.getLongitude() * 1E6));
			}else{ p = null; location = null;}

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		geoPoint = p;
		return p;
	}

	/**
	 * Stop using GPS listener
	 * Calling this function will stop using GPS in your app
	 * */
	public void stopUsingGPS(){
		if(locationManager != null){
			locationManager.removeUpdates(GPS.this);
		}       
	}

	/**
	 * Function to get latitude
	 * */
	public double getLatitude(){
		if(geoPoint != null) latitude = geoPoint.getLatitudeE6() / 1E6;
		return latitude;
	}

	/**
	 * Function to get longitude
	 * */
	public double getLongitude(){
		if(geoPoint != null) longitude = geoPoint.getLongitudeE6() / 1E6;
		return longitude;
	}

	/**
	 * Function to get latitude E6
	 * */
	public double getLatitudeE6(){
		if(geoPoint != null){
			latitude = geoPoint.getLatitudeE6();
		}

		// return latitude
		return latitude;
	}

	/**
	 * Function to get longitude E6
	 * */
	public double getLongitudeE6(){
		if(geoPoint != null){
			longitude = geoPoint.getLongitudeE6();
		}

		// return longitude
		return longitude;
	}

	/**
	 * Function to check GPS/wifi enabled
	 * @return boolean
	 * */
	public boolean canGetLocation() {
		return this.canGetLocation;
	}


	@Override
	public void onLocationChanged(Location location) {
	}

	@Override
	public void onProviderDisabled(String provider) {
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	public static boolean checkInternetConnection(Context context) {

		ConnectivityManager conMgr = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

		// v�rification de la connexion internet et de sa dosponibilit�
		if (conMgr.getActiveNetworkInfo() != null
				&& conMgr.getActiveNetworkInfo().isAvailable()
				&& conMgr.getActiveNetworkInfo().isConnected()) {
			return true;
		} else return false;

	}

	public void dialogCancel(){
		gpsi.dialogCancel();
	}

	public interface GPSInterface {
		public void dialogCancel();
	}

	/**
	 * Récupère le résultat de la localisation d'une adresse sous forme de StringBuilder
	 * @param String address
	 * @return StringBuilder result
	 * @exception ClientProtocolException
	 * @exception IOException
	 * @exception NetworkOnMainThreadException
	 */
	private StringBuilder getJSONValueFromAddress(String address) throws ClientProtocolException, IOException, NetworkOnMainThreadException{
		String uri = "http://maps.google.com/maps/api/geocode/json?address=" +
				address.replace(" ", "%20") + "&sensor=false";

		HttpGet httpGet = new HttpGet(uri);
		HttpClient client = new DefaultHttpClient();
		HttpResponse response;
		StringBuilder stringBuilder = new StringBuilder();

		try {
			response = client.execute(httpGet);
			HttpEntity entity = response.getEntity();
			InputStream stream = entity.getContent();
			int b;
			while ((b = stream.read()) != -1) {
				stringBuilder.append((char) b);
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NetworkOnMainThreadException e) {
			e.printStackTrace();
		}
		
		return stringBuilder;
	}

	/**
	 * Retourne un GeoPoint contenant la longitude et la latitude à partir d'un StringBuilder
	 * @param StringBuilder result
	 * @return GeoPoint geoPoint
	 * @exception JSONException
	 */
	private GeoPoint getGeoPointFromTaskResult(StringBuilder stringBuilder) throws JSONException{
		GeoPoint geoPoint = null;
		
		JSONObject jsonObject = new JSONObject();
		try {
			
			jsonObject = new JSONObject(stringBuilder.toString());
			Double lng = ((JSONArray)jsonObject.get("results")).getJSONObject(0)
					.getJSONObject("geometry").getJSONObject("location")
					.getDouble("lng");

			Double lat = ((JSONArray)jsonObject.get("results")).getJSONObject(0)
					.getJSONObject("geometry").getJSONObject("location")
					.getDouble("lat");

			geoPoint = new GeoPoint(
					(int) (lat * 1E6), 
					(int) (lng * 1E6));

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return geoPoint;
	}

	private class GeoTask extends AsyncTask<String, Void, StringBuilder> {
		ProgressDialog dialog;
		
		public GeoTask(Context ctx) {
			dialog = new ProgressDialog(ctx);
			dialog.setMessage("Recherche de l'adresse");
			dialog.show();
			Log.i("tag", "Lancement dialog");
		}
		
		@Override
		protected void onPreExecute(){
			super.onPreExecute();
		}
		
		@Override
		protected StringBuilder doInBackground(String... urls) {			
			try{
				return getJSONValueFromAddress(urls[0]);
			}catch(Exception ex){
				return null;
			}
		}
		
		@Override
		protected void onPostExecute(StringBuilder result) {
			//ringProgressDialog.dismiss();
			if(dialog.isShowing()){
				dialog.dismiss();
			}
		}
	}
}


