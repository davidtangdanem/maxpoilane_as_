package com.menadinteractive.segafredo.helper;

public class Taxe{
	String mCode;
	String mLibelle;
	String mValeur;
	String mType;
	
	public String getCode(){
		return mCode;
	}
	
	public String getLibelle(){
		return mLibelle;
	}
	
	public String getValeur(){
		return mValeur;
	}
	
	public String getType(){
		return mType;
	}
	
	public Taxe(String code, String libelle, String valeur, String type){
		mCode = code;
		mLibelle = libelle;
		mValeur = valeur;
		mType = type;
	}
	
	public Taxe(String libelle, String valeur, String type){
		mLibelle = libelle;
		mValeur = valeur;
		mType = type;
	}
	
	public Taxe(String valeur, String type){
		mValeur = valeur;
		mType = type;
	}
}
