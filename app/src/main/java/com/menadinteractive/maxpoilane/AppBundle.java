package com.menadinteractive.maxpoilane;

import android.os.Bundle;

public class AppBundle {
	Bundle bundle;

	
	public static final String ApplicationName="ApplicationName";
	public static final String ApplicationKey="ApplicationKey";
	public static final String AdresseClient="AdresseClient";
	public static final String NameClient="NameClient";
	public static final String AccesDirect="AccesDirect";
	public static final String CodeClient="CodeClient";

	public AppBundle(){
		bundle = new Bundle();
	}

	public AppBundle(Bundle bundle){
		this();
		if(bundle != null)
			this.bundle = bundle;
	}

	public Bundle getBundle(){
		return bundle;
	}

	public void flushBundle(){
		bundle.clear();
	}

	/** code client */
	public void removeCodeClient(){
		bundle.remove(CodeClient);
	}
	public String getAccesDirect(){
		try {
			String accesdirect = bundle.getString(AccesDirect);
			if(accesdirect == null )
				accesdirect = "";

			return accesdirect;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return "";
		}
	}
	public void setCodeClient(String codeClient){
		bundle.putString(CodeClient, codeClient);
	}

	public String getCodeClient(){
		String codeClient = bundle.getString(CodeClient);
		if(codeClient == null || (codeClient != null && codeClient.equals("")))
			codeClient = null;

		return codeClient;
	}
	public String getApplicationName(){
		String applicationName = bundle.getString(ApplicationName);
		if(applicationName == null || (applicationName != null && applicationName.equals("")))
			applicationName = null;

		return applicationName;
	}
	 
	/** adresse client */
	public void removeAdresseClient(){
		bundle.remove(AdresseClient);
	}

	public void setAdresseClient(String adresseClient){
		bundle.putString(AdresseClient, adresseClient);
	}

	public String getAdresseClient(){
		String adresseClient = bundle.getString(AdresseClient);
		if(adresseClient == null || (adresseClient != null && adresseClient.equals("")))
			adresseClient = null;

		return adresseClient;
	}
	
	/** nom du client */
	public void removeNameClient(){
		bundle.remove(NameClient);
	}

	public void setNameClient(String nameClient){
		bundle.putString(NameClient, nameClient);
	}

	public String getNameClient(){
		String nameClient = bundle.getString(NameClient);
		if(nameClient == null || (nameClient != null && nameClient.equals("")))
			nameClient = null;

		return nameClient;
	}
	/** applicationKey */
	public void removeApplicationKey(){
		bundle.remove(ApplicationKey);
	}

	public void setApplicationKey(String applicationKey){
		bundle.putString(ApplicationKey, applicationKey);
	}

	public String getApplicationKey(){
		String applicationKey = bundle.getString(ApplicationKey);
		if(applicationKey == null || (applicationKey != null && applicationKey.equals("")))
			applicationKey = null;

		return applicationKey;
	}
	
}
