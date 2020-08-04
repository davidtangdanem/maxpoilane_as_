package com.menadinteractive.segafredo.db;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.google.android.maps.GeoPoint;
import com.menadinteractive.maxpoilane.Debug;
import com.menadinteractive.segafredo.carto.Marker;
import com.menadinteractive.segafredo.communs.Fonctions;
import com.menadinteractive.segafredo.communs.Global;
import com.menadinteractive.segafredo.db.dbKD100Visite.structVisite;


public class TableClient extends DBMain{
	static public String TABLENAME="CLIENT";
	static public String TABLENAME_COMPLEMENTAIRE="T_NEGOS_CLIENTS_INFOCOMP";//info complemantaire client sur le serveur
	public static final String INDEXNAME_CODE_CLI = "INDEX_CODE_CLI";
	public static final String INDEXNAME_CODEVRP = "INDEX_CODE_VRP";
	public static final String INDEXNAME_NOM_CLI = "INDEX_NOM_CLI";
	public static final String INDEXNAME_JOUR_PASSAGE = "INDEX_JOUR_PASSAGE";
	public static final String INDEXNAME_ZONE = "INDEX_ZONE";
	
	public static final String JOUR_DE_PASSAGE = "J";
	public static final String MAX_WEEK = "9999999999";
	
	

	public static final String CLI_TYPE_CLIENT = "C";
	public static final String CLI_TYPE_PROSPECT = "L";
	
	static public final String CLI_CREATION="C";
	static public final String CLI_MODIFICATION="M";
	static public final String FLAG_UPDATE="1";
	
	public static final String FIELD_CODE 			= "CODE";
	public static final String FIELD_SOC_CODE 		= "SOC_CODE";
	public static final String FIELD_CODEVRP 		= "CODEVRP";
	public static final String FIELD_NOM 			= "NOM";//RAISON SOCIAL
	public static final String FIELD_ADR1 			= "ADR1";
	public static final String FIELD_ADR2 			= "ADR2";
	public static final String FIELD_TEL1 			= "TEL1";
	public static final String FIELD_FAX 			= "TEL2";//FAX
	public static final String FIELD_CP 			= "CP";
	public static final String FIELD_VILLE 			= "VILLE";
	public static final String FIELD_LAT 			= "LAT"; 
	public static final String FIELD_LON 			= "LON";
	public static final String FIELD_IS_GEOCODED 	= "IS_GEOCODED";
	public static final String FIELD_ICON 			= "ICON"; 
	public static final String FIELD_COULEUR 		= "COULEUR";//(liste)
	public static final String FIELD_TYPESAV 		= "TYPESAV";//(liste)
	public static final String FIELD_IMPORTANCE 	= "IMPORTANCE";
	public static final String FIELD_TOVISIT 		= "TOVISIT";
	public static final String FIELD_JOURPASSAGE 	= "JOURPASSAGE";
	public static final String FIELD_EMAIL 			= "EMAIL";
	public static final String FIELD_ZONE 			= "ZONE";
	public static final String FIELD_STATUT			= "STATUT";//FERM�, etc...
	public static final String FIELD_CONTACT_NOM	= "CONTACT_NOM";//NOM
	public static final String FIELD_CREAT			= "CREAT";//client cr�e � transmettre
	public static final String FIELD_CATCOMPT		= "CATCOMPT";
	public static final String FIELD_PAYS			= "PAYS";
	public static final String FIELD_SIRET			= "SIRET";
	public static final String FIELD_NUMTVA			= "NUMTVA";
	public static final String FIELD_COMMENT		= "COMMENT";	
	public static final String FIELD_GSM						= "GSM";
	public static final String FIELD_ENSEIGNE					= "ENSEIGNE";
	public static final String FIELD_TYPE						= "TYPE";// Client/prospect  (liste)
	public static final String FIELD_GROUPECLIENT				= "GROUPECLIENT";// GROUPE CLIENT (liste)
	public static final String FIELD_AGENT						= "AGENT";// AGENT (liste)
	public static final String FIELD_CIRCUIT					= "CIRCUIT"; //(liste)
	public static final String FIELD_JOURFERMETURE				= "JOURFERMETURE";//(multi liste)
	public static final String FIELD_MODEREGLEMENT				= "MODEREGLEMENT";//(liste)
	public static final String FIELD_MONTANTTOTALENCOURS		= "MONTANTTOTALENCOURS";
	public static final String FIELD_MONTANTTOTALFACTURESDUES	= "MONTANTTOTALFACTURESDUES";
	public static final String FIELD_MONTANTTOTALAVOIR			= "MONTANTTOTALAVOIR";
	public static final String FIELD_MONTANTTOTALPAIEMENT		= "MONTANTTOTALPAIEMENT";
	public static final String FIELD_TYPEETABLISSEMENT			= "TYPEETABLISSEMENT";
	public static final String FIELD_FREETEXT					= "FREETEXT";
	public static final String FIELD_EXONERATION				= "EXONERATION";
	public static final String FIELD_ACTIF						= "ETAT";//   Y/N
	public static final String FIELD_LUNDI						= "LUNDI";//   Y/N
	public static final String FIELD_MARDI						= "MARDI";//   Y/N
	public static final String FIELD_MERCREDI						= "MERCREDI";//   Y/N
	public static final String FIELD_JEUDI						= "JEUDI";//   Y/N
	public static final String FIELD_VENDREDI						= "VENDREDI";//   Y/N
	public static final String FIELD_SAMEDI						= "SAMEDI";//   Y/N
	
	public static final String FIELD_DATECREAT					= "DATECREAT";
	
	public static String getFullFieldName(String field){
		return TABLENAME+"."+field;
	}

	public static final String INDEX_CREATE_CODE_CLI="CREATE UNIQUE INDEX IF NOT EXISTS ["+INDEXNAME_CODE_CLI+"] "
			+ "ON ["+TABLENAME+"] (["+FIELD_CODE+"])";
	public static final String INDEX_CREATE_CODEVRP="CREATE INDEX IF NOT EXISTS ["+INDEXNAME_CODEVRP+"] "
			+ "ON ["+TABLENAME+"] (["+FIELD_CODEVRP+"])";
	public static final String INDEX_CREATE_NOM_CLI="CREATE INDEX IF NOT EXISTS ["+INDEXNAME_NOM_CLI+"] "
			+ "ON ["+TABLENAME+"] (["+FIELD_NOM+"])";
	public static final String INDEX_CREATE_JOUR_PASSAGE="CREATE INDEX IF NOT EXISTS ["+INDEXNAME_JOUR_PASSAGE+"] "
			+ "ON ["+TABLENAME+"] (["+FIELD_JOURPASSAGE+"])";
	public static final String INDEX_CREATE_ZONE="CREATE UNIQUE INDEX IF NOT EXISTS ["+INDEXNAME_ZONE+"] "
			+ "ON ["+TABLENAME+"] (["+FIELD_ZONE+"])";
	

	public static final String TABLE_CREATE="CREATE TABLE ["+TABLENAME+"] ("+
			" ["+FIELD_CODE			+"] [nvarchar](100) NULL" 	+ COMMA +
			" ["+FIELD_SOC_CODE		+"] [nvarchar](255) NULL" 	+ COMMA +
			" ["+FIELD_CODEVRP		+"] [nvarchar](255) NULL" 	+ COMMA +
			" ["+FIELD_NOM			+"] [nvarchar](255) NULL" 	+ COMMA +
			" ["+FIELD_ADR1			+"] [nvarchar](255) NULL" 	+ COMMA +
			" ["+FIELD_ADR2			+"] [nvarchar](255) NULL" 	+ COMMA +
			" ["+FIELD_CP			+"] [nvarchar](255) NULL" 	+ COMMA +
			" ["+FIELD_VILLE		+"] [nvarchar](255) NULL" 	+ COMMA +
			" ["+FIELD_LAT			+"] [nvarchar](255) NULL" 	+ COMMA +
			" ["+FIELD_LON			+"] [nvarchar](255) NULL" 	+ COMMA +
			" ["+FIELD_IS_GEOCODED	+"] [nvarchar](1) NULL" 	+ COMMA +
			" ["+FIELD_TEL1			+"] [nvarchar](255) NULL" 	+ COMMA +
			" ["+FIELD_FAX			+"] [nvarchar](255) NULL" 	+ COMMA +
			" ["+FIELD_ICON			+"] [nvarchar](255) NULL" 	+ COMMA +
			" ["+FIELD_COULEUR		+"] [nvarchar](255) NULL" 	+ COMMA +
			" ["+FIELD_IMPORTANCE	+"] [nvarchar](2) NULL"		+ COMMA +
			" ["+FIELD_JOURPASSAGE	+"] [nvarchar](255) NULL" 	+ COMMA +
			" ["+FIELD_EMAIL		+"] [nvarchar](255) NULL" 	+ COMMA +
			" ["+FIELD_TOVISIT		+"] [nvarchar](2) NULL"		+ COMMA +
			" ["+FIELD_ZONE			+"] [nvarchar](255) NULL"	+ COMMA +
			" ["+FIELD_CONTACT_NOM	+"] [nvarchar](255) NULL"	+ COMMA +
			" ["+FIELD_STATUT		+"] [nvarchar](2) NULL"	+ COMMA +
			" ["+FIELD_CATCOMPT		+"] [nvarchar](20) NULL"	+ COMMA +
			" ["+FIELD_PAYS			+"] [nvarchar](20) NULL"	+ COMMA +
			" ["+FIELD_SIRET		+"] [nvarchar](88) NULL"	+ COMMA +
			" ["+FIELD_NUMTVA		+"] [nvarchar](33) NULL"	+ COMMA +
			" ["+FIELD_COMMENT		+"] [nvarchar](255) NULL"	+ COMMA +
			" ["+FIELD_CREAT		+"] [nvarchar](1) NULL"	+ COMMA +
			" ["+FIELD_GSM		+"] [nvarchar](20) NULL"	+ COMMA +
			" ["+FIELD_ENSEIGNE		+"] [nvarchar](10) NULL"	+ COMMA +
			" ["+FIELD_TYPE		+"] [nvarchar](5) NULL"	+ COMMA +
			" ["+FIELD_GROUPECLIENT		+"] [nvarchar](5) NULL"	+ COMMA +
			" ["+FIELD_AGENT		+"] [nvarchar](5) NULL"	+ COMMA +
			" ["+FIELD_CIRCUIT		+"] [nvarchar](5) NULL"	+ COMMA +
			" ["+FIELD_JOURFERMETURE		+"] [nvarchar](100) NULL"	+ COMMA +
			" ["+FIELD_MODEREGLEMENT		+"] [nvarchar](30) NULL"	+ COMMA +
			" ["+FIELD_MONTANTTOTALENCOURS		+"] [nvarchar](30) NULL"	+ COMMA +
			" ["+FIELD_MONTANTTOTALFACTURESDUES		+"] [nvarchar](30) NULL"	+ COMMA +
			" ["+FIELD_MONTANTTOTALAVOIR		+"] [nvarchar](30) NULL"	+ COMMA +
			" ["+FIELD_MONTANTTOTALPAIEMENT		+"] [nvarchar](30) NULL"	+ COMMA +
			" ["+FIELD_TYPEETABLISSEMENT		+"] [nvarchar](5) NULL"	+ COMMA +
			" ["+FIELD_FREETEXT		+"] [nvarchar](255) NULL"	+ COMMA +
			" ["+FIELD_EXONERATION		+"] [nvarchar](1) NULL"	+ COMMA +
			" ["+FIELD_TYPESAV	+"] [nvarchar](20) NULL"	+ COMMA +
			" ["+FIELD_ACTIF		+"] [nvarchar](2) NULL"	+ COMMA +
			" ["+FIELD_LUNDI		+"] [nvarchar](200) NULL"	+ COMMA +
			" ["+FIELD_MARDI		+"] [nvarchar](200) NULL"	+ COMMA +
			" ["+FIELD_MERCREDI		+"] [nvarchar](200) NULL"	+ COMMA +
			" ["+FIELD_JEUDI		+"] [nvarchar](200) NULL"	+ COMMA +
			" ["+FIELD_VENDREDI		+"] [nvarchar](200) NULL"	+ COMMA +
			" ["+FIELD_SAMEDI		+"] [nvarchar](200) NULL"	+ COMMA +
			
 
			" ["+FIELD_DATECREAT		+"] [nvarchar](10) NULL"	+  
			
			")";

	static public class structClient
	{
		public String CODE = "";        
		public String SOC_CODE = "";
		public String CODEVRP = "";
		public String NOM  = "";                  
		public String ADR1 = "";  
		public String ADR2 = "";  
		public String CP = "";  
		public String VILLE = "";  
		public String LAT  = "";  
		public String LON  = "";
		public String TEL1  = ""; 
		public String FAX  = ""; 
		public String ICON = ""; 
		public String COULEUR	= ""; 
		public String IMPORTANCE ="";
		public String JOURPASSAGE ="";
		public String TOVISIT ="";
		public String EMAIL ="";
		public String ZONE ="";
		public String STATUT ="";		
		public String CONTACT_NOM ="";
		public String CREAT ="";
		public String PAYS ="";
		public String CATCOMPT ="";
		public String IS_GEOCODED="";
		public String SIRET ="";
		public String NUMTVA ="";
		public String COMMENT ="";
		
		public String GSM ="";
		public String ENSEIGNE ="";
		public String TYPE ="";
		public String GROUPECLIENT ="";
		public String AGENT ="";
		public String CIRCUIT ="";
		public String JOURFERMETURE ="";
		public String MODEREGLEMENT ="";
		public String MONTANTTOTALENCOURS ="";
		public String MONTANTTOTALFACTURESDUES ="";
		public String MONTANTTOTALAVOIR ="";
		public String MONTANTTOTALPAIEMENT ="";
		public String TYPEETABLISSEMENT ="";
		public String FREETEXT ="";
		public String EXONERATION ="";
		public String ETAT ="";
		public String DATECREAT ="";
		public String TYPESAV ="";	
		public String LUNDI ="";	
		public String MARDI ="";	
		public String MERCREDI ="";	
		public String JEUDI ="";	
		public String VENDREDI ="";	
		public String SAMEDI ="";	
		
		
		
		
		
		public String getIconName(){
			String result = "";
			String suffixe = Marker.getMarkerNameFromColor(COULEUR);
			if(!Fonctions.isEmptyOrNull(suffixe))
				result = ICON+"_"+suffixe+".png";
			return result;
		}

		public String getRawIconName(){
			return ICON+"_"+COULEUR;
		}

		public String getFullAddress(){
			return this.ADR1+" "+this.ADR2+" "+this.CP+" "+this.VILLE;
		}

		public double getIconSize(){
			double result = 1;
			if(!Fonctions.isEmptyOrNull(IMPORTANCE)){
				if(IMPORTANCE.equals("H"))
					result = 1.8;
				else if(IMPORTANCE.equals("M"))
					result = 1.4;
				if(IMPORTANCE.equals("L"))
					result = 1.0;
			}
			return result;
		}

		@Override
		public String toString() {
			return "structClient [CODE=" + CODE + ", SOC_CODE=" + SOC_CODE
					+ ", CODEVRP=" + CODEVRP + ", NOM=" + NOM + ", ADR1="
					+ ADR1 + ", ADR2=" + ADR2 + ", CP=" + CP + ", VILLE="
					+ VILLE + ", LAT=" + LAT + ", LON=" + LON + ", TEL1="
					+ TEL1 + ", TEL2=" + FAX + ", ICON=" + ICON + ", COULEUR="
					+ COULEUR + ", IMPORTANCE=" + IMPORTANCE + ", JOURPASSAGE="
					+ JOURPASSAGE + ", TOVISIT=" + TOVISIT + ", EMAIL=" + EMAIL
					+ ", ZONE=" + ZONE + ", STATUT=" + STATUT 
					+ ", CONTACT_NOM=" + CONTACT_NOM					
					+ ", CATCOMPT=" + CATCOMPT
					+ ", SIRET=" + SIRET
					+ ", NUMTVA=" + NUMTVA
					+ ", IS_GEOCODED=" + IS_GEOCODED
					+ ", COMMENT=" + COMMENT
					+ ", PAYS=" + PAYS
					+ ", CREAT=" + CREAT
					+ ", GSM=" + GSM
					+ ", ENSEIGNE=" + ENSEIGNE
					+ ", TYPE=" + TYPE
					+ ", TYPESAV=" + TYPESAV
					+ ", GROUPECLIENT=" + GROUPECLIENT
					+ ", AGENT=" + AGENT
					+ ", CIRCUIT=" + CIRCUIT
					+ ", JOURFERMETURE=" + JOURFERMETURE
					+ ", MODEREGLEMENT=" + MODEREGLEMENT
					+ ", MONTANTTOTALENCOURS=" + MONTANTTOTALENCOURS
					
					+ ", MONTANTTOTALFACTURESDUES=" + MONTANTTOTALFACTURESDUES
					+ ", MONTANTTOTALAVOIR=" + MONTANTTOTALAVOIR
					+ ", MONTANTTOTALPAIEMENT=" + MONTANTTOTALPAIEMENT
					+ ", TYPEETABLISSEMENT=" + TYPEETABLISSEMENT
					+ ", FREETEXT=" + FREETEXT
					+ ", EXONERATION=" + EXONERATION
					+ ", ETAT=" + ETAT
					+ ", LUNDI=" + LUNDI
					+ ", MARDI=" + MARDI
					+ ", MERCREDI=" + MERCREDI
					+ ", JEUDI=" + JEUDI
					+ ", VENDREDI=" + VENDREDI
					+ ", SAMEDI=" + SAMEDI
					
					+ ", DATECREAT=" + DATECREAT
					
					
					+"]";
			
			
		}
	} 

	MyDB db;
	public TableClient(MyDB _db)
	{
		super();
		db=_db;
	}
	public boolean clear(StringBuilder err)
	{
		try
		{
			String query="DROP TABLE IF EXISTS "+TABLENAME;
			//db.conn.delete(TABLENAME, null, null);
			db.execSQL(query, err);
			db.execSQL(TABLE_CREATE,err);
		}
		catch(Exception ex)
		{
			err.append(ex.getMessage());
			return false;	
		}
		return true;
	}
	static public boolean isActif(structClient cli)
	{
		if (cli.ETAT==null) return true;
		if (cli.ETAT.toUpperCase().equals("N"))
			return false;
		
		return true;
	}
	
	public structClient load(Cursor cursor){
		structClient client = new structClient();
		if(cursor != null){
//			Debug.Log("requete", " CODE IN LOADING : -"+giveFld(cursor, FIELD_CODE+"-"));
			
			
			client.SOC_CODE = giveFld(cursor, FIELD_SOC_CODE);
			client.CODE = giveFld(cursor, FIELD_CODE);
			client.CODEVRP = giveFld(cursor, FIELD_CODEVRP);
			client.NOM = giveFld(cursor, FIELD_NOM);
			client.ADR1 = giveFld(cursor, FIELD_ADR1);
			client.ADR2 = giveFld(cursor, FIELD_ADR2);
			client.CP = giveFld(cursor, FIELD_CP);
			client.VILLE = giveFld(cursor, FIELD_VILLE);
			client.LAT = giveFld(cursor, FIELD_LAT);
			client.LON = giveFld(cursor, FIELD_LON);
			client.TEL1 = giveFld(cursor, FIELD_TEL1);
			client.FAX = giveFld(cursor, FIELD_FAX);
			client.ICON = giveFld(cursor, FIELD_ICON);
			client.COULEUR = giveFld(cursor, FIELD_COULEUR);
			client.TYPESAV = giveFld(cursor, FIELD_TYPESAV);
			client.IMPORTANCE = giveFld(cursor, FIELD_IMPORTANCE);
			client.TOVISIT = giveFld(cursor, FIELD_TOVISIT);
			client.JOURPASSAGE = giveFld(cursor, FIELD_JOURPASSAGE);
			client.EMAIL = giveFld(cursor, FIELD_EMAIL);
			client.ZONE = giveFld(cursor, FIELD_ZONE);
			client.STATUT = giveFld(cursor, FIELD_STATUT);
			client.CONTACT_NOM = giveFld(cursor, FIELD_CONTACT_NOM);
	//		client.CONTACT_GSM = giveFld(cursor, FIELD_CONTACT_GSM);
			client.CATCOMPT = giveFld(cursor, FIELD_CATCOMPT);
			client.PAYS = giveFld(cursor, FIELD_PAYS);
			client.CREAT = giveFld(cursor, FIELD_CREAT);
			client.SIRET = giveFld(cursor, FIELD_SIRET);
			client.NUMTVA = giveFld(cursor, FIELD_NUMTVA);
			client.COMMENT = giveFld(cursor, FIELD_COMMENT);		
			//
			client.GSM = giveFld(cursor, FIELD_GSM);
			client.ENSEIGNE = giveFld(cursor, FIELD_ENSEIGNE);
			client.TYPE = giveFld(cursor, FIELD_TYPE);
			client.GROUPECLIENT = giveFld(cursor, FIELD_GROUPECLIENT);
			client.AGENT = giveFld(cursor, FIELD_AGENT);
			client.CIRCUIT = giveFld(cursor, FIELD_CIRCUIT);
			client.JOURFERMETURE = giveFld(cursor, FIELD_JOURFERMETURE);
			client.MODEREGLEMENT = giveFld(cursor, FIELD_MODEREGLEMENT);
			client.MONTANTTOTALENCOURS = giveFld(cursor, FIELD_MONTANTTOTALENCOURS);
			client.MONTANTTOTALFACTURESDUES = giveFld(cursor, FIELD_MONTANTTOTALFACTURESDUES);
			client.MONTANTTOTALAVOIR = giveFld(cursor, FIELD_MONTANTTOTALAVOIR);
			client.MONTANTTOTALPAIEMENT = giveFld(cursor, FIELD_MONTANTTOTALPAIEMENT);
			client.TYPEETABLISSEMENT = giveFld(cursor, FIELD_TYPEETABLISSEMENT);
			client.FREETEXT = giveFld(cursor, FIELD_FREETEXT);		
			client.EXONERATION = giveFld(cursor, FIELD_EXONERATION);
			client.ETAT = giveFld(cursor, FIELD_ACTIF);
			
			client.LUNDI = giveFld(cursor, FIELD_LUNDI);
			client.MARDI = giveFld(cursor, FIELD_MARDI);
			client.MERCREDI = giveFld(cursor, FIELD_MERCREDI);
			client.JEUDI = giveFld(cursor, FIELD_JEUDI);
			client.VENDREDI = giveFld(cursor, FIELD_VENDREDI);
			client.SAMEDI = giveFld(cursor, FIELD_SAMEDI);
			
			client.DATECREAT = giveFld(cursor, FIELD_DATECREAT);
			
		}

//		Debug.Log("requete", client.toString());

		return client;
	}

	public structClient load(String code){
		structClient client = new structClient();
		String query = "SELECT *"
				+" FROM "+TABLENAME
				+" WHERE "+getFullFieldName(FIELD_CODE)+" = '"+code+"'";

		Cursor cursor =  db.conn.rawQuery(query, null);

		if(cursor != null && cursor.moveToFirst()){
			
			client = load(cursor);
			cursor.close();
		}

		return client;
	}
	public int Count()
	{

		try
		{
			String query="select count(*) from "+TABLENAME;
			Cursor cur=db.conn.rawQuery(query, null);
			if (cur.moveToNext())
			{
				return cur.getInt(0);
			}
			return 0;
		}
		catch(Exception ex)
		{
			return -1;
		}

	}
	public int CountModified()
	{

		try
		{
			String query="SELECT count(*) FROM "+
					 TABLENAME+
					" where "+
					" ( "+
					Global.dbClient.FIELD_CREAT +
					"='"+ Global.dbClient.CLI_MODIFICATION+ "' ) ";
			
			Cursor cur=db.conn.rawQuery(query, null);
			if (cur.moveToNext())
			{
				return cur.getInt(0);
			}
			return 0;
		}
		catch(Exception ex)
		{
			return -1;
		}

	}
	/*
	 * String query="SELECT * FROM "+
					Global.dbClient.TABLENAME+
					" where "+
					" ( "+
					Global.dbClient.FIELD_CREAT +
					"='"+ Global.dbClient.CLI_MODIFICATION+ "' ) "	;
	 */
	public void save(structClient client, boolean update){
		if(update){
			delete(client);
		}
		String tagCreation=client.CREAT;
		if (client.CREAT!=null && !client.CREAT.equals(CLI_CREATION))
			tagCreation=CLI_MODIFICATION;
		//si l'ancien tag est C on y touche pas, sinon ou met M
		try
		{
			String query="INSERT INTO " + TABLENAME +" ("+
					FIELD_CODE 		
					+", "+ FIELD_SOC_CODE 	
					+", "+ FIELD_CODEVRP 	
					+", "+ FIELD_NOM 		
					+", "+ FIELD_ADR1 		
					+", "+ FIELD_ADR2 		
					+", "+ FIELD_TEL1 		
					+", "+ FIELD_FAX 		
					+", "+ FIELD_CP 		
					+", "+ FIELD_VILLE 		
					+", "+ FIELD_LAT 		
					+", "+ FIELD_LON 		
					+", "+ FIELD_IS_GEOCODED
					+", "+ FIELD_ICON 		
					+", "+ FIELD_COULEUR 
					+", "+ FIELD_TYPESAV
					+", "+ FIELD_IMPORTANCE 
					+", "+ FIELD_TOVISIT 	
					+", "+ FIELD_JOURPASSAGE
					+", "+ FIELD_EMAIL
					+", "+ FIELD_ZONE
					+", "+ FIELD_STATUT
					+", "+ FIELD_CONTACT_NOM
					+", "+ FIELD_CATCOMPT
					+", "+ FIELD_PAYS
					+", "+ FIELD_SIRET
					+", "+ FIELD_NUMTVA
					+", "+ FIELD_COMMENT
					+", "+ FIELD_CREAT
					+", "+ FIELD_GSM
					+", "+ FIELD_ENSEIGNE
					+", "+ FIELD_TYPE
					+", "+ FIELD_GROUPECLIENT
					+", "+ FIELD_AGENT
					+", "+ FIELD_CIRCUIT	
					+", "+ FIELD_JOURFERMETURE
					+", "+ FIELD_MODEREGLEMENT
					+", "+ FIELD_MONTANTTOTALENCOURS
					+", "+ FIELD_MONTANTTOTALFACTURESDUES
					+", "+ FIELD_MONTANTTOTALAVOIR
					+", "+ FIELD_MONTANTTOTALPAIEMENT
					+", "+ FIELD_TYPEETABLISSEMENT
					+", "+ FIELD_FREETEXT
					+", "+ FIELD_EXONERATION
					+", "+ FIELD_ACTIF
					+", "+ FIELD_DATECREAT
					+") VALUES ("+
					
			
		

					"'"+MyDB.controlFld(client.CODE)+"',"+
					"'"+MyDB.controlFld(client.SOC_CODE)+"',"+
					"'"+MyDB.controlFld(client.CODEVRP)+"',"+
					"'"+MyDB.controlFld(client.NOM)+"',"+
					"'"+MyDB.controlFld(client.ADR1)+"',"+
					"'"+MyDB.controlFld(client.ADR2)+"',"+
					"'"+MyDB.controlFld(client.TEL1)+"',"+
					"'"+MyDB.controlFld(client.FAX)+"',"+
					"'"+MyDB.controlFld(client.CP)+"',"+
					"'"+MyDB.controlFld(client.VILLE)+"',"+
					"'"+MyDB.controlFld(client.LAT)+"',"+
					"'"+MyDB.controlFld(client.LON)+"',"+
					"'"+"',"+
					"'"+MyDB.controlFld(client.ICON)+"',"+
					"'"+MyDB.controlFld(client.COULEUR)+"',"+
					"'"+MyDB.controlFld(client.TYPESAV)+"',"+
					"'"+MyDB.controlFld(client.IMPORTANCE)+"',"+
					"'"+MyDB.controlFld(client.TOVISIT)+"',"+
					"'"+MyDB.controlFld(client.JOURPASSAGE)+"',"+
					"'"+MyDB.controlFld(client.EMAIL)+"', "+
					"'"+MyDB.controlFld(client.ZONE)+"', "+
					"'"+MyDB.controlFld(client.STATUT)+"', "+				
					"'"+MyDB.controlFld(client.CONTACT_NOM)+"', "+
					"'"+MyDB.controlFld(client.CATCOMPT)+"', "+
					"'"+MyDB.controlFld(client.PAYS)+"', "+
					"'"+MyDB.controlFld(client.SIRET)+"', "+
					"'"+MyDB.controlFld(client.NUMTVA)+"', "+
					"'"+MyDB.controlFld(client.COMMENT)+"', "+
					"'"+tagCreation+"' ,"+
					"'"+MyDB.controlFld(client.GSM)+"', "+
					"'"+MyDB.controlFld(client.ENSEIGNE)+"', "+
					"'"+MyDB.controlFld(client.TYPE)+"', "+
					"'"+MyDB.controlFld(client.GROUPECLIENT)+"', "+
					"'"+MyDB.controlFld(client.AGENT)+"', "+
					"'"+MyDB.controlFld(client.CIRCUIT)+"', "+
					"'"+MyDB.controlFld(client.JOURFERMETURE)+"', "+
					"'"+MyDB.controlFld(client.MODEREGLEMENT)+"', "+
					"'"+MyDB.controlFld(client.MONTANTTOTALENCOURS)+"', "+			
					"'"+MyDB.controlFld(client.MONTANTTOTALFACTURESDUES)+"', "+
					"'"+MyDB.controlFld(client.MONTANTTOTALAVOIR)+"', "+
					"'"+MyDB.controlFld(client.MONTANTTOTALPAIEMENT)+"', "+
					"'"+MyDB.controlFld(client.TYPEETABLISSEMENT)+"', "+
					"'"+MyDB.controlFld(client.FREETEXT)+"', "+
					"'"+MyDB.controlFld(client.EXONERATION)+"', "+
					"'"+MyDB.controlFld(client.ETAT)+"', "+
					"'"+MyDB.controlFld(client.DATECREAT)+"' "+

					
					
					")";
			
			

			db.conn.execSQL(query);
		}
		catch(Exception ex)
		{
			Debug.StackTrace(ex);

		}
	}
	
	public boolean isCreation(String codecli)
	{
		String query="";
		query="select * from "+
				TABLENAME+
				" where "+
				FIELD_CODE+"='"+codecli+"'";

		Cursor cur=db.conn.rawQuery (query,null);
		if (cur!=null)
		{
			if(cur.moveToNext())
			{
				String creat=Fonctions.GetStringDanem(giveFld(cur, FIELD_CREAT));
				if (creat.equals(CLI_CREATION)==false) return false;
			}
			if (cur!=null)
				cur.close();
		}
		return true;
	}
	

	public boolean delete(structClient client)
	{
		try
		{
			String query="DELETE from "+TABLENAME		
					+" WHERE "+getFullFieldName(FIELD_CODEVRP)+" = '"+client.CODEVRP+"'"
					+" AND "+getFullFieldName(FIELD_CODE)+" = '"+client.CODE+"'";

			db.conn.execSQL(query);
			return true;
		}
		catch(Exception e)
		{
			Debug.StackTrace(e);
		}
		return false;
	}

	public boolean delete(String code)
	{
		try
		{
			String query="DELETE from "+TABLENAME		
					+" WHERE "+
					getFullFieldName(FIELD_CODE)+" = '"+code+"'";

			db.conn.execSQL(query);
			Global.dbLog.Insert("Client", "Delete", "", "Requete: "
					+ query, "", "");
			
			return true;
		}
		catch(Exception e)
		{
			Debug.StackTrace(e);
		}
		return false;
	}
	public Cursor getAll(String user, GeoPoint centerPosition, int radiusMeter){
		Double latitudeDelta = getLatitudeDegree(centerPosition, radiusMeter);
		Double longitudeDelta = getLongitudeDegree(centerPosition, radiusMeter);

		//		Log.d("TAG", "delta lat/long : "+latitudeDelta+" / "+longitudeDelta);
		Double latitudeMin = (double)(Double.valueOf(centerPosition.getLatitudeE6())/1E6) - latitudeDelta; 
		Double latitudeMax = (double)(Double.valueOf(centerPosition.getLatitudeE6())/1E6) + latitudeDelta; 
		Double longitudeMin = (double)(Double.valueOf(centerPosition.getLongitudeE6())/1E6) - longitudeDelta; 
		Double longitudeMax = (double)(Double.valueOf(centerPosition.getLongitudeE6())/1E6) + longitudeDelta; 

		//		String query = "SELECT "
		//				+dbKD94GeoCode.FIELD_CODECLI
		//				//				+dbKD94GeoCode.FIELD_LATITUDE+","
		//				//				+dbKD94GeoCode.FIELD_LONGITUDE
		//				+" FROM "+dbKD94GeoCode.TABLENAME
		//				+" WHERE "+Global.dbKDGeoCode.getFullFieldName(dbKD94GeoCode.fld_kd_dat_type)+" = '"+dbKD94GeoCode.KD_TYPE+"'"
		//				+" AND "+Global.dbKDGeoCode.getFullFieldName(dbKD94GeoCode.FIELD_CODEREP)+" = '"+Global.AXE_Ident.IDENT+"'"
		//				+" AND "+Global.dbKDGeoCode.getFullFieldName(dbKD94GeoCode.FIELD_CODECLI)+" <> '"+Global.AXE_Client.CODECLIENT+"'"
		//				+" AND CAST("+Global.dbKDGeoCode.getFullFieldName(dbKD94GeoCode.FIELD_LATITUDE)+" as Double) BETWEEN "+latitudeMin+" AND "+latitudeMax
		//				+" AND CAST("+Global.dbKDGeoCode.getFullFieldName(dbKD94GeoCode.FIELD_LONGITUDE)+" as Double) BETWEEN "+longitudeMin+" AND "+longitudeMax;




		Double latitudeMinE6 = latitudeMin*1E6; 
		Double latitudeMaxE6 = latitudeMax*1E6; 
		Double longitudeMinE6 = longitudeMin*1E6; 
		Double longitudeMaxE6 = longitudeMax*1E6; 

		String query = "SELECT *"
				+" FROM "+TABLENAME
				+" WHERE "+getFullFieldName(FIELD_CODEVRP)+" = '"+user+"'"
				+" AND (CAST("+getFullFieldName(FIELD_LAT)+" as Double) BETWEEN "+latitudeMinE6+" AND "+latitudeMaxE6
				+" AND CAST("+getFullFieldName(FIELD_LON)+" as Double) BETWEEN "+longitudeMinE6+" AND "+longitudeMaxE6 +")"
				+" or CAST("+getFullFieldName(FIELD_LAT)+" as Double)=0";

		//		if(!Fonctions.isEmptyOrNull(limit)){
		//			query +=" LIMIT "+limit;
		//		}
		return db.conn.rawQuery(query, null);


		//			if(result != null && result.moveToFirst()){
		//				while(result.isAfterLast() == false)
		//				{
		//					Debug.Log(result.getString(result.getColumnIndex(CalendarContract.Events.TITLE)));
		//
		//					result.moveToNext();
		//				}
		//				result.close();
		//
		//			}



	}


	public Cursor getAll(String codeSociete, String limit){
		String query = "SELECT *"
				+" FROM "+TABLENAME
				+" WHERE 1=1";
		if(!Fonctions.isEmptyOrNull(codeSociete))
			query+=" AND "+getFullFieldName(FIELD_SOC_CODE)+" = '"+codeSociete+"'";

		
		if(!Fonctions.isEmptyOrNull(limit)){
			query +=" LIMIT "+limit;
		}
		Debug.Log("TAG4", query);
		return db.conn.rawQuery(query, null);


		//			if(result != null && result.moveToFirst()){
		//				while(result.isAfterLast() == false)
		//				{
		//					Debug.Log(result.getString(result.getColumnIndex(CalendarContract.Events.TITLE)));
		//
		//					result.moveToNext();
		//				}
		//				result.close();
		//
		//			}



	}
	
	public Cursor getAllNotGeocoded(String codeSociete, String limit){
		String query = "SELECT *"
				+" FROM "+TABLENAME
				+" WHERE 1=1" 
				+" AND ("+getFullFieldName(FIELD_LAT)+" ='' or "+getFullFieldName(FIELD_LON)+" ='' )";
		
		if(!Fonctions.isEmptyOrNull(codeSociete))
			query+=" AND "+getFullFieldName(FIELD_SOC_CODE)+" = '"+codeSociete+"'";

		if(!Fonctions.isEmptyOrNull(limit)){
			query +=" LIMIT "+limit;
		}
		
		Debug.Log("TAG4", query);
		return db.conn.rawQuery(query, null);


		//			if(result != null && result.moveToFirst()){
		//				while(result.isAfterLast() == false)
		//				{
		//					Debug.Log(result.getString(result.getColumnIndex(CalendarContract.Events.TITLE)));
		//
		//					result.moveToNext();
		//				}
		//				result.close();
		//
		//			}



	}
	
	public GeoPoint getGeoPoint(String codeClient){
		GeoPoint result = null; 
		structClient client = load(codeClient);
		if(!Fonctions.isEmptyOrNull(client.CODE) && !Fonctions.isEmptyOrNull(client.LAT) && !Fonctions.isEmptyOrNull(client.LON))
			result = new GeoPoint(Integer.valueOf(client.LAT), Integer.valueOf(client.LON));
		
		return result;
	}
	public String getClientCodeTarif(String codeClient){
		structClient client = load(codeClient);
						
		return Fonctions.GetStringDanem(client.CATCOMPT);
	}

	/** Radius */
	private double getLatitudeDegree(GeoPoint centerPosition, int radiusMeter){
		//	1° latitude = 111120m
		//  1° longitude = 111120m*cos(latitude)
		return (double)radiusMeter/111120;
	}

	private double getLongitudeDegree(GeoPoint centerPosition, int radiusMeter){
		//	1° latitude = 111120m
		//  1° longitude = 111120m*cos(latitude)
		return (radiusMeter/Math.cos(Math.toRadians((double)(Double.valueOf(centerPosition.getLatitudeE6())/1E6))))/(111120);
	}

	
	public ArrayList<String> getZones(){
		ArrayList<String> result = new ArrayList<String>();
		String query = "SELECT distinct "+FIELD_ZONE
				+" FROM "+TABLENAME
				+" WHERE 1=1"
				+" ORDER BY "+FIELD_ZONE+" ASC";
		
		Cursor cursor = db.conn.rawQuery(query, null);
		if(cursor != null && cursor.moveToFirst()){
			while(cursor.isAfterLast() == false)
			{
				result.add(giveFld(cursor, FIELD_ZONE));
				cursor.moveToNext();
			}
			cursor.close();
		}
		return result;
	}
	public String GetCodeProspect(String user)
	{  
		String stNum="";

		boolean existe=true;		
		while(existe==true)
		{
			/**
			 * R�cup�ration du jour de l'ann�e
			 */
			String stDayofYear ="";
			String stYear ="";
			String minutes ="";

			GregorianCalendar gc = new GregorianCalendar();
			SimpleDateFormat Annee = new SimpleDateFormat("yyyy");
			SimpleDateFormat minute = new SimpleDateFormat("mms");

			stDayofYear =Fonctions.getDay_Of_Year();
			stYear=Annee.format(gc.getTime());
			minutes=minute.format(gc.getTime());

			stNum="NC"+user+stYear.substring(3, 4)+stDayofYear+minutes.substring(0, 3);
			//1+2+1+3+3



			existe=false;

			String query="";
			query="select * from "+
					TABLENAME+
					" where "+
					FIELD_CODE+"='"+stNum+"'";

			Cursor cur=db.conn.rawQuery (query,null);
			if (cur!=null)
			{
				if(cur.moveToNext())
				{
					existe=true;	
				}

			}
			if (cur!=null)
				cur.close();
		}

		return stNum;		
	}

	public boolean getClient(String id,structClient cli,StringBuilder err)
	{	
		try
		{
			String query="";
			query="select * FROM "+
					TABLENAME+
					" where "+
					FIELD_CODE+
					"='"+id+"'";


			Cursor cur=db.conn.rawQuery(query, null);
			if (cur.moveToNext())
			{
				fillStruct(cur,cli);
				if (cur!=null)
					cur.close();
				return true;
			}
			if (cur!=null)
				cur.close();

		}
		catch(Exception ex)
		{

		}

		return false;
	}
	/*
	 * 
	 * cherche un client par son nom
	 */
	public boolean getClientbyName(String nom,structClient cli,StringBuilder err)
	{	
		try
		{
			nom=MyDB.controlFld(nom);
			String query="";
			query="select * FROM "+
					TABLENAME+
					" where "+
					FIELD_ENSEIGNE+
					" like '%"+nom+"%'";


			Cursor cur=db.conn.rawQuery(query, null);
			if (cur.moveToNext())
			{
				fillStruct(cur,cli);
				if (cur!=null)
					cur.close();
				return true;
			}
			if (cur!=null)
				cur.close();

		}
		catch(Exception ex)
		{

		}

		return false;
	}
	public String[] getClients()
	{	
		try
		{
			String query="";
			query="select "+FIELD_ENSEIGNE+" FROM "+
					TABLENAME;
					

			ArrayList<String> liste=new ArrayList<String>();
			Cursor cur=db.conn.rawQuery(query, null);
			while (cur.moveToNext())
			{
				String nom		=giveFld(cur,this.FIELD_ENSEIGNE).trim();;
				liste.add(nom);
			}
			
			if (cur!=null)
				cur.close();
			return liste.toArray(new String[liste.size()]);

		}
		catch(Exception ex)
		{

		}

		return null;
	}
	
	void fillStruct(Cursor cur,structClient cli)
	{
		cli.ADR1=cur.getString(cur.getColumnIndex(Global.dbClient.FIELD_ADR1));
		cli.ADR2=cur.getString(cur.getColumnIndex(Global.dbClient.FIELD_ADR2));
		cli.CATCOMPT=cur.getString(cur.getColumnIndex(Global.dbClient.FIELD_CATCOMPT));
		cli.CODE=cur.getString(cur.getColumnIndex(Global.dbClient.FIELD_CODE));
		cli.CODEVRP=cur.getString(cur.getColumnIndex(Global.dbClient.FIELD_CODEVRP));
		cli.COMMENT=cur.getString(cur.getColumnIndex(Global.dbClient.FIELD_COMMENT));
//		cli.CONTACT_GSM=cur.getString(cur.getColumnIndex(Global.dbClient.FIELD_CONTACT_GSM));
		cli.CONTACT_NOM=cur.getString(cur.getColumnIndex(Global.dbClient.FIELD_CONTACT_NOM));
		cli.COULEUR=cur.getString(cur.getColumnIndex(Global.dbClient.FIELD_COULEUR));
		cli.TYPESAV=cur.getString(cur.getColumnIndex(Global.dbClient.FIELD_TYPESAV));
		cli.CP=cur.getString(cur.getColumnIndex(Global.dbClient.FIELD_CP));
		cli.EMAIL=cur.getString(cur.getColumnIndex(Global.dbClient.FIELD_EMAIL));
		cli.FAX=cur.getString(cur.getColumnIndex(Global.dbClient.FIELD_FAX));
		cli.ICON=cur.getString(cur.getColumnIndex(Global.dbClient.FIELD_ICON));
		cli.IMPORTANCE=cur.getString(cur.getColumnIndex(Global.dbClient.FIELD_IMPORTANCE));
		cli.IS_GEOCODED=cur.getString(cur.getColumnIndex(Global.dbClient.FIELD_IS_GEOCODED));
		cli.JOURPASSAGE=cur.getString(cur.getColumnIndex(Global.dbClient.FIELD_JOURPASSAGE));
		cli.LAT=cur.getString(cur.getColumnIndex(Global.dbClient.FIELD_LAT));
		cli.LON=cur.getString(cur.getColumnIndex(Global.dbClient.FIELD_LON));
		cli.NOM=cur.getString(cur.getColumnIndex(Global.dbClient.FIELD_NOM));
		cli.NUMTVA=cur.getString(cur.getColumnIndex(Global.dbClient.FIELD_NUMTVA));
		cli.PAYS=cur.getString(cur.getColumnIndex(Global.dbClient.FIELD_PAYS));
		cli.SIRET=cur.getString(cur.getColumnIndex(Global.dbClient.FIELD_SIRET));
		cli.SOC_CODE=cur.getString(cur.getColumnIndex(Global.dbClient.FIELD_SOC_CODE));
		cli.STATUT=cur.getString(cur.getColumnIndex(Global.dbClient.FIELD_STATUT));
		cli.TEL1=cur.getString(cur.getColumnIndex(Global.dbClient.FIELD_TEL1));
		cli.TOVISIT=cur.getString(cur.getColumnIndex(Global.dbClient.FIELD_TOVISIT));
		cli.VILLE=cur.getString(cur.getColumnIndex(Global.dbClient.FIELD_VILLE));
		cli.ZONE=cur.getString(cur.getColumnIndex(Global.dbClient.FIELD_ZONE));
		
		cli.CREAT=cur.getString(cur.getColumnIndex(Global.dbClient.FIELD_CREAT));
		
		cli.GSM=cur.getString(cur.getColumnIndex(Global.dbClient.FIELD_GSM));
		cli.ENSEIGNE=cur.getString(cur.getColumnIndex(Global.dbClient.FIELD_ENSEIGNE));
		cli.TYPE=cur.getString(cur.getColumnIndex(Global.dbClient.FIELD_TYPE));
		cli.GROUPECLIENT=cur.getString(cur.getColumnIndex(Global.dbClient.FIELD_GROUPECLIENT));
		cli.AGENT=cur.getString(cur.getColumnIndex(Global.dbClient.FIELD_AGENT));
		cli.CIRCUIT=cur.getString(cur.getColumnIndex(Global.dbClient.FIELD_CIRCUIT));
		cli.JOURFERMETURE=cur.getString(cur.getColumnIndex(Global.dbClient.FIELD_JOURFERMETURE));
		cli.MODEREGLEMENT=cur.getString(cur.getColumnIndex(Global.dbClient.FIELD_MODEREGLEMENT));
		cli.MONTANTTOTALENCOURS=cur.getString(cur.getColumnIndex(Global.dbClient.FIELD_MONTANTTOTALENCOURS));
		cli.MONTANTTOTALFACTURESDUES=cur.getString(cur.getColumnIndex(Global.dbClient.FIELD_MONTANTTOTALFACTURESDUES));
		cli.MONTANTTOTALAVOIR=cur.getString(cur.getColumnIndex(Global.dbClient.FIELD_MONTANTTOTALAVOIR));
		cli.MONTANTTOTALPAIEMENT=cur.getString(cur.getColumnIndex(Global.dbClient.FIELD_MONTANTTOTALPAIEMENT));
		
		cli.TYPEETABLISSEMENT=cur.getString(cur.getColumnIndex(Global.dbClient.FIELD_TYPEETABLISSEMENT));
		cli.FREETEXT=cur.getString(cur.getColumnIndex(Global.dbClient.FIELD_FREETEXT));
		cli.EXONERATION=cur.getString(cur.getColumnIndex(Global.dbClient.FIELD_EXONERATION));
		cli.ETAT=cur.getString(cur.getColumnIndex(Global.dbClient.FIELD_ACTIF));
		
		cli.LUNDI=cur.getString(cur.getColumnIndex(Global.dbClient.FIELD_LUNDI));
		cli.MARDI=cur.getString(cur.getColumnIndex(Global.dbClient.FIELD_MARDI));
		cli.MERCREDI=cur.getString(cur.getColumnIndex(Global.dbClient.FIELD_MERCREDI));
		cli.JEUDI=cur.getString(cur.getColumnIndex(Global.dbClient.FIELD_JEUDI));
		cli.VENDREDI=cur.getString(cur.getColumnIndex(Global.dbClient.FIELD_VENDREDI));
		cli.SAMEDI=cur.getString(cur.getColumnIndex(Global.dbClient.FIELD_SAMEDI));
		
		cli.DATECREAT=cur.getString(cur.getColumnIndex(Global.dbClient.FIELD_DATECREAT));
		
		
		
	}

	
	public boolean saveProspect(structClient ent,String codeclient,StringBuffer stBuf)
	{
		try
		{
			String tagCreation=getCreatValue(codeclient);
			if (!tagCreation.equals(CLI_CREATION))
				tagCreation=CLI_MODIFICATION;
			
			String query="SELECT * FROM "+
					TABLENAME+
					" where "+
					FIELD_CODE+"="+
					"'"+codeclient+"' ";

			//transforme lat et lon en entier
			if (Fonctions.GetStringDanem( ent.LAT ).contains("."))
			{
				float lat=Fonctions.convertToFloat(ent.LAT);
				int ilat= Integer.valueOf((int) (lat*1000000));
				
				float lon=Fonctions.convertToFloat(ent.LON);
				int ilon= Integer.valueOf((int) (lon*1000000));
				
				ent.LAT=String.valueOf(ilat);
				ent.LON=String.valueOf(ilon);
			}
			
			Cursor cur=db.conn.rawQuery (query,null);
			if (cur.moveToNext() )
			{
				query="UPDATE "+TABLENAME+
						" set "+
						FIELD_CODEVRP+"="+
						"'"+ent.CODEVRP+"',"+
						FIELD_NOM+"="+
						"'"+MyDB.controlFld(ent.NOM) +"',"+
						FIELD_ADR1+"="+
						"'"+MyDB.controlFld(ent.ADR1) +"',"+
						FIELD_ADR2+"="+
						"'"+MyDB.controlFld(ent.ADR2) +"',"+
						FIELD_TEL1+"="+
						"'"+MyDB.controlFld(ent.TEL1 )+"',"+
						FIELD_FAX+"="+
						"'"+MyDB.controlFld(ent.FAX )+"',"+
						FIELD_CP+"="+
						"'"+MyDB.controlFld(ent.CP )+"',"+
						FIELD_VILLE+"="+
						"'"+MyDB.controlFld(ent.VILLE )+"',"+
						FIELD_LAT+"="+
						"'"+MyDB.controlFld(ent.LAT )+"',"+
						FIELD_LON+"="+
						"'"+MyDB.controlFld(ent.LON) +"',"+
						FIELD_EMAIL+"="+
						"'"+MyDB.controlFld(ent.EMAIL) +"',"+
						FIELD_COMMENT+"="+
						"'"+MyDB.controlFld(ent.COMMENT) +"', "+
						FIELD_CREAT+"="+
						"'"+tagCreation+"', "+
						FIELD_GSM+"="+
						"'"+MyDB.controlFld(ent.GSM) +"', "+
						FIELD_TYPE+"="+
						"'"+MyDB.controlFld("C")+"', "+
						FIELD_TYPEETABLISSEMENT+"="+
						"'"+MyDB.controlFld("boulangerie") +"', "+
						FIELD_DATECREAT+"="+
						"'"+MyDB.controlFld(ent.DATECREAT )+"' "+
						" where "+
						FIELD_CODE+"="+
						"'"+codeclient+"' ";

				;	  		

				db.conn.execSQL(query);
				Global.dbLog.Insert("Client", "Save update", "", "Requete: "
						+ query, "", "");
			}
			else		  
			{	  		
				query="INSERT INTO " + TABLENAME +" ("+
						FIELD_CODE+","+
						FIELD_SOC_CODE+","+
						FIELD_CODEVRP+","+
						FIELD_NOM+","+
						FIELD_ADR1+","+
						FIELD_ADR2+","+
						FIELD_TEL1+","+
						FIELD_FAX+","+
						FIELD_CP+","+
						FIELD_VILLE+","+
						FIELD_LAT+","+
						FIELD_LON+","+
						FIELD_ICON+","+
						FIELD_COULEUR+","+
						FIELD_TYPESAV+","+
						FIELD_IMPORTANCE+","+
						FIELD_TOVISIT+","+
						FIELD_JOURPASSAGE+","+
						FIELD_EMAIL+","+
						FIELD_ZONE+","+
						FIELD_STATUT+","+
						FIELD_CONTACT_NOM+","+
				

						FIELD_PAYS+", "+
						FIELD_CATCOMPT+", "+
						
						FIELD_SIRET+", "+
						FIELD_NUMTVA+", "+
						FIELD_COMMENT+", "+

						FIELD_CREAT+", "+
						
						FIELD_GSM+", "+
						FIELD_ENSEIGNE+", "+
						FIELD_TYPE+", "+
						FIELD_GROUPECLIENT+", "+
						//FIELD_AGENT+", "+
						FIELD_CIRCUIT+", "+
						FIELD_JOURFERMETURE+", "+
						FIELD_MODEREGLEMENT+", "+
						
						FIELD_MONTANTTOTALENCOURS+", "+
						FIELD_MONTANTTOTALFACTURESDUES+", "+
						FIELD_MONTANTTOTALAVOIR+", "+
						FIELD_MONTANTTOTALPAIEMENT+", "+
						
						FIELD_TYPEETABLISSEMENT+", "+
						FIELD_FREETEXT+", "+
						FIELD_EXONERATION+", "+
						FIELD_ACTIF+", "+
						FIELD_DATECREAT+" "+
					
    	  		") VALUES ("+
    	  		"'"+MyDB.controlFld(ent.CODE)    +"',"+
    	  		"'"+MyDB.controlFld(ent.SOC_CODE)       +"',"+
    	  		"'"+MyDB.controlFld(ent.CODEVRP)      +"',"+
    	  		"'"+MyDB.controlFld(ent.NOM)        +"',"+
    	  		"'"+MyDB.controlFld(ent.ADR1)        +"',"+
    	  		"'"+MyDB.controlFld(ent.ADR2) 		+"',"+
    	  		"'"+MyDB.controlFld(ent.TEL1)	 	+"',"+
    	  		"'"+MyDB.controlFld(ent.FAX )	+"',"+
    	  		"'"+MyDB.controlFld(ent.CP) 		+"',"+
    	  		"'"+MyDB.controlFld(ent.VILLE )		+"',"+
    	  		"'"+MyDB.controlFld(ent.LAT) 		+"',"+
    	  		"'"+MyDB.controlFld(ent.LON)		 +"',"+
    	  		"'"+MyDB.controlFld(ent.ICON )		+"',"+
    	  		"'"+MyDB.controlFld(ent.COULEUR )			+"',"+
    	  		"'"+MyDB.controlFld(ent.TYPESAV)			+"',"+
    	  		"'"+MyDB.controlFld(ent.IMPORTANCE )			+"',"+
    	  		"'"+MyDB.controlFld(ent.TOVISIT )		+"',"+
    	  		"'"+MyDB.controlFld(ent.JOURPASSAGE )			+"',"+
    	  		"'"+MyDB.controlFld(ent.EMAIL )			+"',"+
    	  		"'"+MyDB.controlFld(ent.ZONE) 			+"',"+
    	  		"'"+MyDB.controlFld(ent.STATUT) 	+"',"+
    	  		"'"+MyDB.controlFld(ent.CONTACT_NOM) 		+"',"+
    			    	  		
				"'"+MyDB.controlFld(ent.PAYS) 			+"',"+
				"'"+MyDB.controlFld(ent.CATCOMPT) 			+"',"+


				"'"+MyDB.controlFld(ent.SIRET) 			+"',"+
				"'"+MyDB.controlFld(ent.NUMTVA) 			+"',"+
				"'"+MyDB.controlFld(ent.COMMENT) 			+"',"+
		
    	  		"'"+CLI_CREATION	+"',"+
    	  		"'"+MyDB.controlFld(ent.GSM) 			+"',"+
    	  		"'"+MyDB.controlFld(ent.ENSEIGNE) 			+"',"+
    	  		"'"+MyDB.controlFld(ent.TYPE) 			+"',"+
    	  		"'"+MyDB.controlFld(ent.GROUPECLIENT) 			+"',"+
    	  		//"'"+MyDB.controlFld(ent.AGENT) 			+"',"+
    	  		"'"+MyDB.controlFld(ent.CIRCUIT) 			+"',"+
    	  		"'"+MyDB.controlFld(ent.JOURFERMETURE) 			+"',"+
    	  		
				"'"+MyDB.controlFld(ent.MODEREGLEMENT) 			+"',"+
				"'"+MyDB.controlFld(ent.MONTANTTOTALENCOURS) 			+"',"+
				"'"+MyDB.controlFld(ent.MONTANTTOTALFACTURESDUES) 			+"',"+
				"'"+MyDB.controlFld(ent.MONTANTTOTALAVOIR) 			+"',"+
				"'"+MyDB.controlFld(ent.MONTANTTOTALPAIEMENT) 			+"',"+
				"'"+MyDB.controlFld(ent.TYPEETABLISSEMENT) 			+"',"+
				"'"+MyDB.controlFld(ent.FREETEXT) 			+"',"+
				"'"+MyDB.controlFld(ent.EXONERATION) 			+"',"+
				"'"+MyDB.controlFld(ent.ETAT) 			+"',"+
				"'"+MyDB.controlFld(ent.DATECREAT) 			+"'"+
   	  		")";

				db.conn.execSQL(query);
				Global.dbLog.Insert("Client", "Save insert", "", "Requete: "
						+ query, "", "");
			
			}
		}
		catch(Exception ex)
		{
			stBuf.setLength(0);
			stBuf.append(ex.getMessage());
			return false;
		}

		return true;
	}
	
	public boolean saveUpdateTypeclient(String typecli,String date,String codeclient,StringBuffer stBuf)
	{
		try
		{
			/*String tagCreation=getCreatValue(codeclient);
			if (!tagCreation.equals(CLI_CREATION))
				tagCreation=CLI_MODIFICATION;
			*/
			String query="SELECT * FROM "+
					TABLENAME+
					" where "+
					FIELD_CODE+"="+
					"'"+codeclient+"' ";
			
			Cursor cur=db.conn.rawQuery (query,null);
			if (cur.moveToNext() )
			{
				query="UPDATE "+TABLENAME+
						" set "+
						FIELD_TYPE+"="+
						"'"+MyDB.controlFld(typecli )+"', "+
						
												
						FIELD_DATECREAT+"="+
						"'"+MyDB.controlFld(date )+"' "+
						
						" where "+
						FIELD_CODE+"="+
						"'"+codeclient+"' ";

				;	  		

				db.conn.execSQL(query);
				Global.dbLog.Insert("Client", "Save update", "", "Requete: "
						+ query, "", "");
			}
					
		}
		catch(Exception ex)
		{
			stBuf.setLength(0);
			stBuf.append(ex.getMessage());
			return false;
		}

		return true;
	}
	public boolean saveUpdateEmail(String email,String date,String codeclient,StringBuffer stBuf)
	{
		try
		{
			String query="SELECT * FROM "+
					TABLENAME+
					" where "+
					FIELD_CODE+"="+
					"'"+codeclient+"' ";
			
			Cursor cur=db.conn.rawQuery (query,null);
			if (cur.moveToNext() )
			{
				query="UPDATE "+TABLENAME+
						" set "+
						FIELD_EMAIL+"="+
						"'"+MyDB.controlFld(email )+"', "+
						
												
						FIELD_DATECREAT+"="+
						"'"+MyDB.controlFld(date )+"' ,"+
						
							FIELD_CREAT+"="+
							"'"+MyDB.controlFld(CLI_MODIFICATION )+"' "+


						" where "+
						FIELD_CODE+"="+
						"'"+codeclient+"' ";

				;	  		

				db.conn.execSQL(query);
				Global.dbLog.Insert("Client", "Save update", "", "Requete: "
						+ query, "", "");
			}
					
		}
		catch(Exception ex)
		{
			stBuf.setLength(0);
			stBuf.append(ex.getMessage());
			return false;
		}

		return true;
	}
	public boolean updateLatLon(String lat,String lon, String codeclient )
	{
		try
		{
			//on update la table kd pour synchro avec le serveur
			dbKD732InfoClientComplementaires info=new dbKD732InfoClientComplementaires(db);
			info.save(codeclient, lat, lon);
			
			//on update la table client pour le fonctionnement local
		 
			String query="UPDATE "+TABLENAME+
					" set "+
					FIELD_LAT+"="+
					"'"+MyDB.controlFld(lat )+"', "+
					
					FIELD_LON+"="+
					"'"+MyDB.controlFld(lon )+"' "+
					
					" where "+
					FIELD_CODE+"="+
					"'"+codeclient+"' ";

			;	  		

			db.conn.execSQL(query);
		 
			
		}
		catch(Exception ex)
		{
		 
			Global.lastErrorMessage=(ex.getMessage());
			return false;
		}

		return true;
	}
	//recupere le code de l'icone grace au lbl
	public String getCodeIcon(String icon)
	{
		return Global.dbParam.getLblAllSocReverse(Global.dbParam.PARAM_CLIACTIV, icon);
	}
	public String getLblIcon(String codeIcon)
	{
		return Global.dbParam.getLblAllSoc(Global.dbParam.PARAM_CLIACTIV, codeIcon);
	}
	//
	public String getCodeTypeEtablissement(String typeetab)
	{
		return Global.dbParam.getLblAllSocReverse(Global.dbParam.PARAM_TYPEETABLISSEMENT, typeetab);
	}
	public String getCodeModeReglement(String moderegl)
	{
		return Global.dbParam.getLblAllSocReverse(Global.dbParam.PARAM_MODEREGLEMENT, moderegl);
	}
	public String getCodeJourFermeture(String jourferm)
	{
		return Global.dbParam.getLblAllSocReverse(Global.dbParam.PARAM_MODEREGLEMENT, jourferm);
	}
	public String getCodeCircuit(String codecircuit)
	{
		return Global.dbParam.getLblAllSocReverse(Global.dbParam.PARAM_CODECIRCUIT, codecircuit);
	}
	public String getCodeAgent(String codeagent)
	{
		return Global.dbParam.getLblAllSocReverse(Global.dbParam.PARAM_AGENT, codeagent);
	}
	public String getCodeGroupementclient(String groupeclient)
	{
		return Global.dbParam.getLblAllSocReverse(Global.dbParam.PARAM_GROUPEMENTCLIENT, groupeclient);
	}
	public String getCodeTypecli(String typecli)
	{
		return Global.dbParam.getLblAllSocReverse(Global.dbParam.PARAM_TYPECLI, typecli);
	}
	
	public String getCodeSAV(String sav)
	{
		return Global.dbParam.getLblAllSocReverse(Global.dbParam.PARAM_SAV, sav);
	}
	public int CountProspect()
	{

		try
		{
			String query="select count(*) from "+TABLENAME+
					" where "+FIELD_CREAT+"='"+CLI_CREATION+"'";
			Cursor cur=db.conn.rawQuery(query, null);
			if (cur.moveToNext())
			{
				return cur.getInt(0);
			}
			return 0;
		}
		catch(Exception ex)
		{
			return -1;
		}

	}
	
	public String getCreatValue(String codecli)
	{
		structClient cli=new structClient();
		getClient(codecli, cli, new StringBuilder());
		return Fonctions.GetStringDanem(cli.CREAT);
	}
	public String getTypeClientValue(String codecli)
	{
		structClient cli=new structClient();
		getClient(codecli, cli, new StringBuilder());
		return Fonctions.GetStringDanem(cli.TYPE);
	}
	
	
	public boolean CountCliParJourPassage(Bundle b, String curZone)
	{

		try
		{
			String query="select count(*) as cpt,"+FIELD_JOURPASSAGE+" as jpass from "+TABLENAME+
					" where "+FIELD_ZONE+"='"+curZone+"' "+
					" group by "+FIELD_JOURPASSAGE;
			Cursor cursor = db.conn.rawQuery(query, null);
			if(cursor != null && cursor.moveToFirst()){
				while(cursor.isAfterLast() == false)
				{
					String s1 = giveFld(cursor, "cpt") ;
					String s2 = giveFld(cursor, "jpass") ;
					b.putString(giveFld(cursor, "jpass") , giveFld(cursor, "cpt")) ;
					cursor.moveToNext();
				}
				cursor.close();
			}
			return true;		
		}
		catch(Exception ex)
		{
			return false;
		}

	}
	
	/*
	 * on deflag les prospects en creation apres la transmission afi de ne pas envoyer n fois au serveur l'ordre de cr�ation
	 */
	public void deFlagProspects()
	{
		String update="UPDATE "+TABLENAME+ 
				" set "+FIELD_CREAT+"='N'";
		
		db.conn.execSQL(update);
	
	}
	
	
	public ArrayList<Bundle> getClientsFilters( String filter,String order,String tourneeprincipal,String codetournee)
	{
		try
		{
			String whereTournee="";
			if(codetournee.equals(""))
			{
				if(!tourneeprincipal.equals(""))
				{
					whereTournee=" and "+FIELD_CODEVRP+ " = '"+tourneeprincipal+"' " ;
				}	
			}
			else
				whereTournee=" and "+FIELD_CODEVRP+ " = '"+codetournee+"' " ;
			
			String LIMIT= " LIMIT  200 ";
			filter=MyDB.controlFld(filter);
			String query="";
			query="select distinct case when cli.etat='Y' then '' else 'rouge' end isbloque,cli.*,histo."+dbKD729HistoDocuments.FIELD_CODECLIENT+" ishisto, facdues."+dbKD730FacturesDues.FIELD_CODECLIENT +" isfacdues, matos."+TableMaterielClient.FIELD_CODECLIENT+ " ismatos "+
					" FROM "+
					TABLENAME+
					" cli left join "+dbKD729HistoDocuments.TABLENAME_HISTO+
					" histo on "+FIELD_CODE+"=histo."+dbKD729HistoDocuments.FIELD_CODECLIENT+
					" and histo."+dbKD729HistoDocuments.fld_kd_dat_type+"="+dbKD729HistoDocuments.KD_TYPE+
					"     left join "+dbKD730FacturesDues.TABLENAME_HISTO+
					" facdues on "+FIELD_CODE+"=facdues."+dbKD730FacturesDues.FIELD_CODECLIENT+
					" and facdues."+dbKD730FacturesDues.fld_kd_dat_type+"="+dbKD730FacturesDues.KD_TYPE+
					" left join "+TableMaterielClient.TABLENAME+ " matos on "+
					" matos."+TableMaterielClient.FIELD_CODECLIENT+"=cli."+TableClient.FIELD_CODE+
					" where ("+
					FIELD_CODE+ " like '%"+filter+"%' "+
					" or "+
					FIELD_ENSEIGNE+ " like '%"+filter+"%' "+
					" or "+
					FIELD_NOM+ " like '%"+filter+"%' "+
					" or "+
					FIELD_VILLE+ " like '%"+filter+"%' "+
					" or "+
					FIELD_CP+ " like '%"+filter+"%' "+
					" or "+
					FIELD_ICON+ " like '%"+filter+"%' ) "+
					whereTournee
							+ " order by "+order +LIMIT;
					
			

			ArrayList<Bundle>  clients=new ArrayList<Bundle>();
			Cursor cur=db.conn.rawQuery(query, null);
			while (cur.moveToNext())
			{
				 		
						Bundle cli=new Bundle();
						cli.putString(Global.dbClient.FIELD_CODE, cur.getString(cur.getColumnIndex(Global.dbClient.FIELD_CODE)));
						cli.putString(Global.dbClient.FIELD_VILLE, cur.getString(cur.getColumnIndex(Global.dbClient.FIELD_VILLE)));
						cli.putString(Global.dbClient.FIELD_CP, cur.getString(cur.getColumnIndex(Global.dbClient.FIELD_CP)));				
						cli.putString(Global.dbClient.FIELD_NOM, cur.getString(cur.getColumnIndex(Global.dbClient.FIELD_NOM)));
						cli.putString(Global.dbClient.FIELD_ENSEIGNE, "Tournée : "+cur.getString(cur.getColumnIndex(Global.dbClient.FIELD_CODEVRP)));
						cli.putString(Global.dbClient.FIELD_ICON, cur.getString(cur.getColumnIndex(Global.dbClient.FIELD_ICON)));
						cli.putString(Global.dbClient.FIELD_COULEUR, cur.getString(cur.getColumnIndex(Global.dbClient.FIELD_COULEUR)));
						
						String histo=cur.getString(cur.getColumnIndex("ishisto"));
						if (histo!=null && histo.equals("")==false)     
								cli.putString("ishisto", "basic1_104_database_download");
						else
							cli.putString("ishisto", "");
						
						String facdues=cur.getString(cur.getColumnIndex("isfacdues"));
						if (facdues!=null && facdues.equals("")==false)     
							cli.putString("isfacdues", "basic2_018_money_coins");
						else
							cli.putString("isfacdues", "");
						
						String matos=cur.getString(cur.getColumnIndex("ismatos"));
						if (matos!=null && matos.equals("")==false)     
							cli.putString("ismatos", "basic2_292_tools_settings");
						else
							cli.putString("ismatos", "");
						
						String isgeocoded=cur.getString(cur.getColumnIndex(FIELD_LAT));
						if (isgeocoded!=null && isgeocoded.equals("")==false)     
							cli.putString("isgeocoded", "basic_2057_map");
						else
							cli.putString("isgeocoded", "");
						
						/*String isbloque=cur.getString(cur.getColumnIndex("isbloque"));
						if (isbloque!=null && isbloque.equals("")==false)     
							cli.putString("isbloque", "rouge");
						else
							cli.putString("isbloque", "");*/
						
				if( Global.dbKDVisite.isVisiteToday(cur.getString(cur.getColumnIndex(Global.dbClient.FIELD_CODE)), Fonctions.getYYYYMMDD())==true)
			 	{
			 		
					cli.putString("isbloque", "rouge");
				}
			 	else
			 	{
			 		String jourPassage = SQLRequestHelper.getCodeTournee(Fonctions.getYYYYMMDD());
					if(jourPassage.equals("1"))
					{
						if(!cur.getString(cur.getColumnIndex(Global.dbClient.FIELD_LUNDI)).equals("")   && !cur.getString(cur.getColumnIndex(Global.dbClient.FIELD_LUNDI)).equals(MAX_WEEK))
						{
							cli.putString("isbloque", "vert");
						}
						else
							cli.putString("isbloque", "");
				
						
					}
					if(jourPassage.equals("2"))
					{
						if(!cur.getString(cur.getColumnIndex(Global.dbClient.FIELD_MARDI)).equals("")   && !cur.getString(cur.getColumnIndex(Global.dbClient.FIELD_MARDI)).equals(MAX_WEEK))
						{
							cli.putString("isbloque", "vert");
						}
						else
							cli.putString("isbloque", "");
				
					}
					if(jourPassage.equals("3"))
					{
						if(!cur.getString(cur.getColumnIndex(Global.dbClient.FIELD_MERCREDI)).equals("")   && !cur.getString(cur.getColumnIndex(Global.dbClient.FIELD_MERCREDI)).equals(MAX_WEEK))
						{
							cli.putString("isbloque", "vert");
						}
						else
							cli.putString("isbloque", "");
				
					}
					if(jourPassage.equals("4"))
					{
						if(!cur.getString(cur.getColumnIndex(Global.dbClient.FIELD_JEUDI)).equals("")   && !cur.getString(cur.getColumnIndex(Global.dbClient.FIELD_JEUDI)).equals(MAX_WEEK))
						{
							cli.putString("isbloque", "vert");
						}
						else
							cli.putString("isbloque", "");
				
					}
					if(jourPassage.equals("5"))
					{
						if(!cur.getString(cur.getColumnIndex(Global.dbClient.FIELD_VENDREDI)).equals("")   && !cur.getString(cur.getColumnIndex(Global.dbClient.FIELD_VENDREDI)).equals(MAX_WEEK))
						{
							cli.putString("isbloque", "vert");
						}
						else
							cli.putString("isbloque", "");
				
					}
					if(jourPassage.equals("6"))
					{
						if(!cur.getString(cur.getColumnIndex(Global.dbClient.FIELD_SAMEDI)).equals("")   && !cur.getString(cur.getColumnIndex(Global.dbClient.FIELD_SAMEDI)).equals(MAX_WEEK))
						{
							cli.putString("isbloque", "vert");
						}
						else
							cli.putString("isbloque", "");
				
					}
			 	
			 	}
			 
						
						
						//basic_2057_map
					/*	Marker markerController=new Marker(c);
						
						Drawable d = markerController.getMarker(
								cur.getString(cur.getColumnIndex(Global.dbClient.FIELD_ICON)), cur.getString(cur.getColumnIndex(Global.dbClient.FIELD_COULEUR)),
										cur.getString(cur.getColumnIndex(Global.dbClient.FIELD_IMPORTANCE)), cur.getString(cur.getColumnIndex(Global.dbClient.FIELD_STATUT)),
								"0");
			*/
						
						clients.add(cli); 
				 		
				 	
						
						
			
			}
			if (cur!=null)
				cur.close();
			return clients;
		}
		catch(Exception ex)
		{

		}

		return null;
	}
	public String getModereglement(String code)
	{	
		String libelle="";
		
		try
		{
			String query="";
			query="select * FROM "+
					TABLENAME+
					" where "+
					TableClient.FIELD_CODE+
					"='"+code+"'";


			Cursor cur=db.conn.rawQuery(query, null);
			if (cur.moveToNext())
			{
				libelle=giveFld(cur,FIELD_MODEREGLEMENT          );
				
			}
			if (cur != null )
				cur.close();

		}
		catch(Exception ex)
		{
			libelle="";
		}

		return libelle;
	}
}
