package com.menadinteractive.segafredo.carto;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;
import com.menadinteractive.segafredo.db.TableClient.structClient;

public class ClientItem  extends OverlayItem{

	String clientCode;
	structClient client;

	public ClientItem(GeoPoint point, String title, String snippet) {
		super(point, title, snippet);
	}

	public ClientItem(String clientCode, GeoPoint point, String title, String snippet) {
		this(point, title, snippet);
		this.clientCode = clientCode;
		this.client = null;
	}


	public String getClientCode() {
		return clientCode;
	}

	public void setClient(structClient client){
		this.client = client;
	}

	public structClient getClient(){
		return this.client;
	}

}
