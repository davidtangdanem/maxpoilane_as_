package com.menadinteractive.segafredo.tasks;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Handler;

import com.menadinteractive.maxpoilane.Debug;
import com.menadinteractive.maxpoilane.R;
import com.menadinteractive.segafredo.carto.CartoMapActivity;
import com.menadinteractive.segafredo.communs.Fonctions;
import com.menadinteractive.segafredo.communs.Global;
import com.menadinteractive.segafredo.db.TableClient.structClient;

public class GeocodeTask extends AsyncTask<Void, Integer, Boolean> {
	Context context;
	Handler handler = null;
	Geocoder geoCoder;
	boolean geocodeAllClients = true;

	int progressionFailed = 0;
	int numberOfSuccessfullyGeocodedClients = 0;
	String mail = "";
	StringBuffer sb = new StringBuffer();


	public GeocodeTask(Context context, Handler handler){
		super();
		this.context = context;
		this.handler = handler;
		this.geoCoder = new Geocoder(context, Locale.getDefault());
	}

	public GeocodeTask(Context context, Handler handler, boolean geocodeAllClients){
		this(context, handler);
		this.geocodeAllClients = geocodeAllClients;
	}

	public void onPreExecute() {
		if(handler != null)
			handler.sendEmptyMessage(CartoMapActivity.MESSAGE_ACTION_BAR_PROGRESS_BAR);
	}


	@Override
	protected Boolean doInBackground(Void... arg) {
		boolean result_sendMail = false;
		Cursor clients = null;

		Debug.Log("TAG4", "Do in Background");
		if(geocodeAllClients){
			clients = Global.dbClient.getAll(null, null);
		}
		else{
			clients = Global.dbClient.getAllNotGeocoded(null, null);
		}

		progressionFailed = 0;


		if(clients != null && clients.moveToFirst()){
			while(clients.isAfterLast() == false)
			{
				structClient client = Global.dbClient.load(clients);
				Address address = getCoordinate(client);

				if(address != null){
					//						Log.d("TAG", "lat/lon : "+address.getLatitude()+"/"+address.getLongitude()+" for client : "+client.CODECLIENT);
					saveCoordinate(client, address);
					numberOfSuccessfullyGeocodedClients++;
					
					
					publishProgress(0);
				}
				else{
					progressionFailed++;
					sb.append(client.CODE+" - "+client.NOM+"\n");
					publishProgress(0);
				}

				clients.moveToNext();
			}
			clients.close();
		}

		if(progressionFailed > 0){
			result_sendMail = true;
			mail = sb.toString();
		}
		return result_sendMail;
	}
	@Override
	protected void onProgressUpdate(Integer... progress) {

	}

	@Override
	protected void onPostExecute(Boolean result_sendMail) {
		if(result_sendMail){
			Fonctions.sendEmailMsg(context, "adressemail@mail.com", mail, context.getString(R.string.list_clients_failed_geocode));
		}
		else{
			
		}
		if(handler != null){
			handler.sendEmptyMessage(CartoMapActivity.MESSAGE_CLEAR_TASK_GEOCODE);
			handler.sendEmptyMessage(CartoMapActivity.MESSAGE_ACTION_BAR_PROGRESS_BAR);

		}

	}


	private void saveCoordinate(structClient client, Address address){
		double lat = (double)(address.getLatitude()*1E6);
		double lon = (double)(address.getLongitude()*1E6);
		client.LAT = String.valueOf(Math.round(lat));
		client.LON = String.valueOf(Math.round(lon));
		Global.dbClient.save(client, true);
	}


	private Address getCoordinate(structClient client){
		List<Address> address;

		try {
			address = geoCoder.getFromLocationName(client.ADR1 + ", " + client.CP + ", "+ client.VILLE, 1);
			if(address != null && address.size()>0){
				Debug.Log("TAG4", address.get(0).toString());
				return address.get(0);
			}
			else{
				address = geoCoder.getFromLocationName(client.ADR2 + ", " + client.CP + ", "+ client.VILLE, 1);
				if(address != null && address.size()>0){
					Debug.Log("TAG4", address.get(0).toString());
					return address.get(0);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}


