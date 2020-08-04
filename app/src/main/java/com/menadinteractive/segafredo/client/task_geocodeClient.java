package com.menadinteractive.segafredo.client;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Handler;

import com.menadinteractive.segafredo.db.TableClient;
import com.menadinteractive.segafredo.db.TableClient.structClient;


public class task_geocodeClient extends AsyncTask<structClient, Void, Boolean>{
	/** Constants */
	public static final int MESSAGE_GEOCODING_FINISHED_SUCCESS = 8000;
	public static final int MESSAGE_GEOCODING_FINISHED_FAIL = 8001;
	public static final int MESSAGE_RETRIEVEADDRESS_SUCCESS = 8002;
	
	
	Context context;
	Handler handler;
	Geocoder geoCoder;
	Location fromLocationPoint;
	
	public task_geocodeClient(Context context, Handler handler,Location fromLocationPoint){
		this.context = context;
		this.handler = handler;
		this.fromLocationPoint=fromLocationPoint;
		geoCoder = new Geocoder(context, Locale.getDefault());
	}
	
	@Override
	protected Boolean doInBackground(structClient... params) {
		try {
			boolean result = false;
			structClient client = params[0];
			if (fromLocationPoint!=null)
			{
				Address address = getAddress(fromLocationPoint);
				client.VILLE=address.getLocality();
				client.CP=address.getPostalCode();
				client.ADR1=address.getAddressLine(0);
				return true;
			}
			else
			{
				
				Address address = getCoordinate(client);
				if(address != null){
					client.LAT = String.valueOf(address.getLatitude());
					client.LON = String.valueOf(address.getLongitude());
					
					result = true;
				}
				else{
					client.LAT="0";
					client.LON="0";
				}
			}
			return result;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return false;
	}

	
	
	@Override
	protected void onPostExecute(Boolean result) {
		if(handler != null){
			if (fromLocationPoint==null)
			{
				if(result)
					handler.sendEmptyMessage(MESSAGE_GEOCODING_FINISHED_SUCCESS);
				else
					handler.sendEmptyMessage(MESSAGE_GEOCODING_FINISHED_FAIL);
			}
			else
			{
				if(result)
					handler.sendEmptyMessage(MESSAGE_RETRIEVEADDRESS_SUCCESS);
			}
			
		}
	}

	private Address getCoordinate(TableClient.structClient client){
		List<Address> address;
		try {
			if(client.VILLE == null)
				client.VILLE = "";
			
			
			address = geoCoder.getFromLocationName(client.ADR1 + ", " + client.CP + ", "+ client.VILLE+ ", " + "FR", 1);
			if(address != null && address.size()>0){
				return address.get(0);
			}
			else{
				address = geoCoder.getFromLocationName(client.ADR2 + ", " + client.CP + ", "+ client.VILLE+ ", " + "FR", 1);
				if(address != null && address.size()>0){
					return address.get(0);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private Address getAddress(Location loc){
		List<Address> address;
		try {
			
			
			
		address = geoCoder.getFromLocation(loc.getLatitude(),loc.getLongitude(),1);
		if(address != null && address.size()>0){
				return address.get(0);
			}
		
		} catch (IOException e) {
			e.printStackTrace();
			
		}
		return null;
	}
}
