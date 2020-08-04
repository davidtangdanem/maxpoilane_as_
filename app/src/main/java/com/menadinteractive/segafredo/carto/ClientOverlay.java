package com.menadinteractive.segafredo.carto;

import java.util.ArrayList;
import java.util.List;

import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;

import com.google.android.maps.ItemizedOverlay;
import com.menadinteractive.maxpoilane.Debug;
import com.menadinteractive.segafredo.communs.Global;

public class ClientOverlay  extends ItemizedOverlay<ClientItem>{
	/** Handler */
	Handler handler;

	/** Models */
	List<ClientItem> clients = null;

	public ClientOverlay(Drawable defaultMarker) {		
		// Attach the bottom center point of the drawable to map coordinates.
		//instead of super(defaultMarker);
		super(boundCenterBottom(defaultMarker));
		initModels();
		populate();
	}

	public ClientOverlay(Drawable defaultMarker, Handler h) {
		this(defaultMarker);
		this.handler = h;
	}

	private void initModels(){
		clients = new ArrayList<ClientItem>();
	}
	
	public void addClient(ClientItem client){
		clients.add(client);
		setLastFocusedIndex(-1);
		populate();
	}

	public void clearClients(){
		if(clients!= null)
			clients.clear();
		setLastFocusedIndex(-1);
		populate();
	}

	@Override
	protected ClientItem createItem(int i) {
		return clients.get(i);
	}

	@Override
	public int size() {
		return clients.size();
	}

	@Override
	protected boolean onTap(int index) {
		ClientItem item = clients.get(index);
		Debug.Log(item.clientCode);
		Global.clientSelectedOnMap = item;

		if(handler != null){
			Message m = new Message();
			m.what = CartoMapActivity.MESSAGE_CLIENT_SELECTED_ON_MAP;
			handler.sendMessage(m);
		}
		Debug.Log("requete", "onTap : "+item.getClientCode());
		
		
		/*
		if(handler != null){
			Message m = new Message();
			m.what = ZoneChalandiseMapActivity.MESSAGE_CLIENT_SELECTED_ON_MAP;
			m.arg1 = item.getPoint().getLatitudeE6();
			m.arg2 = item.getPoint().getLongitudeE6();
			Bundle b = new Bundle();
			b.putString(ZoneChalandiseMapActivity.BUNDLE_CLIENT_CODE_MESSAGE, item.getClientCode());
			m.setData(b);
			handler.sendMessage(m);
		}
		 */
		return true;
	}

	public List<ClientItem> getClients(){
		return clients;
	}

}
