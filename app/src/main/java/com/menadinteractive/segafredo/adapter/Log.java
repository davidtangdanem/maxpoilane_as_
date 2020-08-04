package com.menadinteractive.segafredo.adapter;

import java.util.ArrayList;

import com.google.android.maps.GeoPoint;
import com.menadinteractive.segafredo.db.TableClient.structClient;
import com.menadinteractive.segafredo.db.TableFermeture.structFermeture;
import com.menadinteractive.segafredo.db.TableHoraire.structHoraire;
import com.menadinteractive.segafredo.db.dbKD100Visite.structVisite;
import com.menadinteractive.segafredo.tasks.onDemandTask.structTournee;

public class Log implements java.lang.Comparable{
	structClient client;
	String distance;
	GeoPoint geoPoint;
	structHoraire horaire;
	ArrayList<structFermeture> fermetures;
	boolean isOpened = true;
	boolean isFermeture = false;
	boolean dejaVisite = false;
	structTournee tournee;
	float fDistance;
	structVisite visite = new structVisite();


	public structClient getClient() {
		return client;
	}
	public void setClient(structClient client) {
		this.client = client;
	}
	public String getDistance() {
		return distance;
	}
	public void setDistance(String distance) {
		this.distance = distance;
	}
	public GeoPoint getGeoPoint() {
		return geoPoint;
	}
	public void setGeoPoint(GeoPoint geoPoint) {
		this.geoPoint = geoPoint;
	}
	
	public structHoraire getHoraire() {
		return horaire;
	}
	public void setHoraire(structHoraire horaire) {
		this.horaire = horaire;
	}
	public ArrayList<structFermeture> getFermetures() {
		return fermetures;
	}
	public void setFermetures(ArrayList<structFermeture> fermetures) {
		this.fermetures = fermetures;
	}
	
	
	public boolean isOpened() {
		return isOpened;
	}
	public void setOpened(boolean isOpened) {
		this.isOpened = isOpened;
	}
	
	
	
	
	public float getfDistance() {
		return fDistance;
	}
	public void setfDistance(float fDistance) {
		this.fDistance = fDistance;
	}
	public structTournee getTournee() {
		return tournee;
	}
	public void setTournee(structTournee tournee) {
		this.tournee = tournee;
	}
	public boolean isFermeture() {
		return isFermeture;
	}
	public void setFermeture(boolean isFermeture) {
		this.isFermeture = isFermeture;
	}
	public boolean isDejaVisite() {
		return dejaVisite;
	}
	public void setDejaVisite(boolean dejaVisite) {
		this.dejaVisite = dejaVisite;
	}
	
	
	
	public structVisite getVisite() {
		return visite;
	}
	public void setVisite(structVisite visite) {
		this.visite = visite;
	}
	@Override
	public int compareTo(Object another) {
//		int nombre1 = Integer.valueOf(((Log) another).getDistance()); 
//		int nombre2 = Integer.valueOf(this.getDistance()); 
//		if (nombre1 > nombre2)  return -1; 
//		else if(nombre1 == nombre2) return 0; 
//		else return 1; 
		
		
		float nombre1 = ((Log) another).getfDistance(); 
		float nombre2 = this.getfDistance(); 
		if (nombre1 > nombre2)  return -1; 
		else if(nombre1 == nombre2) return 0; 
		else return 1; 
	}


}
