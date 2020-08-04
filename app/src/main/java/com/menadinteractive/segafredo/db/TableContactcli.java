package com.menadinteractive.segafredo.db;

import java.util.ArrayList;

import android.database.Cursor;
import android.os.Bundle;

import com.menadinteractive.segafredo.carto.Marker;
import com.menadinteractive.segafredo.communs.DateCode;
import com.menadinteractive.segafredo.communs.Fonctions;
import com.menadinteractive.segafredo.communs.Global;
import com.menadinteractive.segafredo.db.TableClient.structClient;
import com.menadinteractive.segafredo.plugins.Espresso;

public class TableContactcli extends DBMain{

	static public String TABLENAME="CONTACTCLI";
	static public String TABLENAME_SRV="T_NEGOS_CONTACTS_CLI";
	
	//public static final String INDEXNAME_CODE_CLI = "INDEX_CODE_CLI";
	//public static final String INDEXNAME_CODEVRP = "INDEX_CODE_VRP";
	//public static final String INDEXNAME_NOM_CLI = "INDEX_NOM_CLI";
	//public static final String INDEXNAME_JOUR_PASSAGE = "INDEX_JOUR_PASSAGE";
	//public static final String INDEXNAME_ZONE = "INDEX_ZONE";
	

	static public final String CONTACT_CREATION="C";
	static public final String CONTACT_MODIFICATION="M";
	static public final String CONTACT_SUPPRESSION="S";
//	static public final String CONTACT_SUPPRESSION_CREATTION="D";
	
	
//	static public final String FLAG_UPDATE="1";
	
	public static final String FIELD_CODECONTACT 		= "CODECONTACT";
	public static final String FIELD_CODECLIENT 		= "CODECLIENT";
	public static final String FIELD_NOM 				= "NOM";
	public static final String FIELD_PRENOM 			= "PRENOM";
	public static final String FIELD_MOBILE 			= "MOBILE";
	public static final String FIELD_EMAIL 				= "EMAIL";
	public static final String FIELD_CODEFONCTION		= "CODEFONCTION";
	public static final String FIELD_COMMENTAIRE 		= "COMMENTAIRE";
	public static final String FIELD_CODEVRP 		= "CODVRP";
	public static final String FIELD_FLAG			= "FLAG";//
	
	//Serveur
	public static final String FIELD_SRV_CODECONTACT 		= "CON_NAME_IDCONTACT";
	public static final String FIELD_SRV_CODECLIENT 		= "CON_CODECLIENT";
	public static final String FIELD_SRV_NOM 				= "CON_NOM";
	public static final String FIELD_SRV_PRENOM 			= "CON_PRENOM";
	public static final String FIELD_SRV_MOBILE 			= "CON_MOBILE";
	public static final String FIELD_SRV_EMAIL 				= "CON_EMAIL";
	public static final String FIELD_SRV_CODEFONCTION		= "CON_CODEFCT";
	public static final String FIELD_SRV_COMMENTAIRE 		= "CON_COMMENTAIRE";
	public static final String FIELD_SRV_FLAG			= "CON_FLAG";//
	
	
	
	
	
	public static String getFullFieldName(String field){
		return TABLENAME+"."+field;
	}

	
	
	public static final String TABLE_CREATE="CREATE TABLE ["+TABLENAME+"] ("+
			" ["+FIELD_CODECONTACT			+"] [nvarchar](50) NULL" 	+ COMMA +
			" ["+FIELD_CODECLIENT		+"] [nvarchar](50) NULL" 	+ COMMA +
			" ["+FIELD_NOM		+"] [nvarchar](255) NULL" 	+ COMMA +
			" ["+FIELD_PRENOM			+"] [nvarchar](255) NULL" 	+ COMMA +
			" ["+FIELD_MOBILE			+"] [nvarchar](50) NULL" 	+ COMMA +
			" ["+FIELD_EMAIL			+"] [nvarchar](255) NULL" 	+ COMMA +
			" ["+FIELD_CODEFONCTION			+"] [nvarchar](50) NULL" 	+ COMMA +
			" ["+FIELD_COMMENTAIRE		+"] [nvarchar](255) NULL"	+ COMMA +
			" ["+FIELD_CODEVRP		+"] [nvarchar](10) NULL"	 + COMMA +
			" ["+FIELD_FLAG		+"] [nvarchar](10) NULL"	 +
			
			
			
			")";

	static public class structContactcli
	{
		
		public String CODECONTACT = "";        
		public String CODECLIENT = "";
		public String NOM  = "";                  
		public String PRENOM = "";  
		public String MOBILE = "";  
		public String EMAIL = "";  
		public String CODEFONCTION = "";  
		public String COMMENTAIRE  = "";
		public String CODVRP  = "";
		public String FLAG  = "";
		
		@Override
		public String toString() {
			return "structContactcli [CODECONTACT=" + CODECONTACT + ", CODECLIENT=" + CODECLIENT
					+ ", NOM=" + NOM + ", PRENOM=" + PRENOM + ", MOBILE="
					+ MOBILE + ", EMAIL=" + EMAIL + ", CODEFONCTION=" + CODEFONCTION + ", COMMENTAIRE="
					+ COMMENTAIRE 					
					+ ", CODVRP=" + CODVRP
					+ ", FLAG=" + FLAG
					
					
					+"]";
			
			
		}
		
		
	}
	MyDB db;
	public TableContactcli(MyDB _db)
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
	public boolean getContact(String codeclient, String id,structContactcli cli,StringBuilder err)
	{	
		try
		{
			String query="";
			query="select * FROM "+
					TABLENAME+
					" where "+
					FIELD_CODECONTACT+
					"='"+id+"' "+
					" and "+
					FIELD_CODECLIENT+"='"+codeclient+"'";


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
String ee;
ee=ex.getLocalizedMessage();
		}

		return false;
	}
	void fillStruct(Cursor cur,structContactcli cli)
	{
		cli.CODECONTACT=cur.getString(cur.getColumnIndex(Global.dbContactcli.FIELD_CODECONTACT));
		cli.CODECLIENT=cur.getString(cur.getColumnIndex(Global.dbContactcli.FIELD_CODECLIENT));
		cli.NOM=cur.getString(cur.getColumnIndex(Global.dbContactcli.FIELD_NOM));
		cli.PRENOM=cur.getString(cur.getColumnIndex(Global.dbContactcli.FIELD_PRENOM));
		cli.MOBILE=cur.getString(cur.getColumnIndex(Global.dbContactcli.FIELD_MOBILE));
		cli.EMAIL=cur.getString(cur.getColumnIndex(Global.dbContactcli.FIELD_EMAIL));
		cli.CODEFONCTION=cur.getString(cur.getColumnIndex(Global.dbContactcli.FIELD_CODEFONCTION));
		cli.COMMENTAIRE=cur.getString(cur.getColumnIndex(Global.dbContactcli.FIELD_COMMENTAIRE));
		cli.CODVRP=cur.getString(cur.getColumnIndex(Global.dbContactcli.FIELD_CODEVRP));
		cli.FLAG=cur.getString(cur.getColumnIndex(Global.dbContactcli.FIELD_FLAG));		
	}
	public static final String KD_INSERT_STRING=
			"INSERT INTO "+
					TABLENAME_SRV+
					" ("+	
					
					FIELD_SRV_CODECONTACT 		+","+
					FIELD_SRV_CODECLIENT	  		+","+
					FIELD_SRV_NOM			+","+
					FIELD_SRV_PRENOM  			+","+
					FIELD_SRV_MOBILE	  			+","+
					FIELD_SRV_EMAIL				+","+
					FIELD_SRV_CODEFONCTION			+","+
					FIELD_SRV_COMMENTAIRE				+","+
					FIELD_SRV_FLAG  			+") VALUES ";
	
	
	
	public ArrayList<Bundle> getContactcliFilters(String codeclient,  String filter)
	{
		try
		{
			filter=MyDB.controlFld(filter);
			String query="";
			query="select * FROM "+
					TABLENAME +
					" where ("+ 
					FIELD_CODECONTACT+ " like '%"+filter+"%' "+
					" or "+
					FIELD_NOM+ " like '%"+filter+"%' "+
					" or "+
					FIELD_PRENOM+ " like '%"+filter+"%' "+
					" or "+
					FIELD_MOBILE+ " like '%"+filter+"%' "+
					" or "+
					FIELD_EMAIL+ " like '%"+filter+"%' ) and "+
					FIELD_CODECLIENT+
					"='"+codeclient+"'  and ( ("+FIELD_FLAG+"!='"+Global.dbContactcli.CONTACT_SUPPRESSION+"'  )  or "+FIELD_FLAG+" IS NULL )";
			
			
			query="select * FROM "+
					TABLENAME +
					  " where "+
					FIELD_CODECLIENT+
					"='"+codeclient+"'  ";
			
			
					
			

			ArrayList<Bundle>  clients=new ArrayList<Bundle>();
			Cursor cur=db.conn.rawQuery(query, null);
			while (cur.moveToNext())
			{
				Bundle cli=new Bundle();
				cli.putString(Global.dbContactcli.FIELD_CODECONTACT, cur.getString(cur.getColumnIndex(Global.dbContactcli.FIELD_CODECONTACT)));
				cli.putString(Global.dbContactcli.FIELD_CODECLIENT, cur.getString(cur.getColumnIndex(Global.dbContactcli.FIELD_CODECLIENT)));
				cli.putString(Global.dbContactcli.FIELD_NOM, cur.getString(cur.getColumnIndex(Global.dbContactcli.FIELD_NOM)));				
				cli.putString(Global.dbContactcli.FIELD_PRENOM, cur.getString(cur.getColumnIndex(Global.dbContactcli.FIELD_PRENOM)));
				cli.putString(Global.dbContactcli.FIELD_MOBILE, cur.getString(cur.getColumnIndex(Global.dbContactcli.FIELD_MOBILE)));
				cli.putString(Global.dbContactcli.FIELD_EMAIL, cur.getString(cur.getColumnIndex(Global.dbContactcli.FIELD_EMAIL)));
				cli.putString(Global.dbContactcli.FIELD_CODEFONCTION, Global.dbParam.getLblAllSoc(Global.dbParam.PARAM_FCTCONTACT, cur.getString(cur.getColumnIndex(Global.dbContactcli.FIELD_CODEFONCTION))));
				cli.putString(Global.dbContactcli.FIELD_COMMENTAIRE, cur.getString(cur.getColumnIndex(Global.dbContactcli.FIELD_COMMENTAIRE)));
				cli.putString(Global.dbContactcli.FIELD_CODEVRP, cur.getString(cur.getColumnIndex(Global.dbContactcli.FIELD_CODEVRP)));
				cli.putString(Global.dbContactcli.FIELD_FLAG, cur.getString(cur.getColumnIndex(Global.dbContactcli.FIELD_FLAG)));
				
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
	public boolean saveContactclient(structContactcli ent,String codecontactcli,String codeclient,String Etat,StringBuffer stBuf)
	{
		try
		{
			
			String query="SELECT * FROM "+
					TABLENAME+
					" where "+
					FIELD_CODECONTACT+"="+
					"'"+codecontactcli+"' and "+FIELD_CODECLIENT+"="+
					"'"+codeclient+"'  ";

			
			Cursor cur=db.conn.rawQuery (query,null);
			if (cur.moveToNext() )
			{
				query="UPDATE "+TABLENAME+
						" set "+
						FIELD_NOM+"="+
						"'"+MyDB.controlFld(ent.NOM )+"',"+
						FIELD_PRENOM+"="+
						"'"+MyDB.controlFld(ent.PRENOM )+"',"+
						FIELD_MOBILE+"="+
						"'"+MyDB.controlFld(ent.MOBILE) +"',"+
						FIELD_EMAIL+"="+
						"'"+MyDB.controlFld(ent.EMAIL )+"',"+
						FIELD_COMMENTAIRE+"="+
						"'"+MyDB.controlFld(ent.COMMENTAIRE )+"',"+
						FIELD_CODEFONCTION+"="+
						"'"+MyDB.controlFld(ent.CODEFONCTION )+"',"+
						FIELD_FLAG+"="+
						"'"+MyDB.controlFld(Etat )+"'"+
						" where "+
						FIELD_CODECONTACT+"="+
						"'"+codecontactcli+"' and "+FIELD_CODECLIENT+"="+
						"'"+codeclient+"'  ";

												
				
					;
				db.conn.execSQL(query);
				Global.dbLog.Insert("contact", "Save Insert", "", "Requete: "
						+ query, "", "");
			}
			else		  
			{	  
				
				
				query="INSERT INTO " + TABLENAME +" ("+
						FIELD_CODECONTACT+","+
						FIELD_CODECLIENT+","+
						FIELD_NOM+","+
						FIELD_PRENOM+","+
						FIELD_MOBILE+","+
						FIELD_EMAIL+","+
						FIELD_CODEFONCTION+","+
						FIELD_COMMENTAIRE+","+
						FIELD_CODEVRP+","+
						FIELD_FLAG+" "+
					
    	  		") VALUES ("+
    	  		"'"+MyDB.controlFld(codecontactcli)    +"',"+
    	  		"'"+MyDB.controlFld(codeclient)      +"',"+
    	  		"'"+MyDB.controlFld(ent.NOM)        +"',"+
    	  		"'"+MyDB.controlFld(ent.PRENOM)        +"',"+
    	  		"'"+MyDB.controlFld(ent.MOBILE) 		+"',"+
    	  		"'"+MyDB.controlFld(ent.EMAIL)	 	+"',"+
    	  		"'"+MyDB.controlFld(ent.CODEFONCTION )	+"',"+
    	  		"'"+MyDB.controlFld(ent.COMMENTAIRE) 		+"',"+
    	  		"'"+MyDB.controlFld(ent.CODVRP )		+"',"+
				"'"+MyDB.controlFld(Etat) 			+"'"+
    	  		")";

				db.conn.execSQL(query);
				Global.dbLog.Insert("Contact", "Save insert", "", "Requete: "
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
	public structContactcli load(Cursor cursor){
		structContactcli client = new structContactcli();
		if(cursor != null){
			
			client.CODECONTACT = giveFld(cursor, FIELD_CODECONTACT);
			client.CODECLIENT = giveFld(cursor, FIELD_CODECLIENT);
			client.NOM = giveFld(cursor, FIELD_NOM);
			client.PRENOM = giveFld(cursor, FIELD_PRENOM);
			client.MOBILE = giveFld(cursor, FIELD_MOBILE);
			client.EMAIL = giveFld(cursor, FIELD_EMAIL);
			client.CODEFONCTION = giveFld(cursor, FIELD_CODEFONCTION);
			client.COMMENTAIRE = giveFld(cursor, FIELD_COMMENTAIRE);
			client.CODVRP = giveFld(cursor, FIELD_CODEVRP);
			client.FLAG = giveFld(cursor, FIELD_FLAG);
		
		}

		return client;
	}

	public structContactcli load(String code){
		structContactcli client = new structContactcli();
		String query = "SELECT *"
				+" FROM "+TABLENAME
				+" WHERE "+getFullFieldName(FIELD_CODECONTACT)+" = '"+code+"'";

		Cursor cursor =  db.conn.rawQuery(query, null);

		if(cursor != null && cursor.moveToFirst()){
			
			client = load(cursor);
			cursor.close();
		}

		return client;
	}
	public String GetNumcontact(String codecli) {
		String query = "SELECT count(*) FROM "+
				TABLENAME+ 
				" where  ("+
			 
				FIELD_CODECLIENT+"='"+codecli+"')";
			 
		int result=0;
		Cursor cur = db.conn.rawQuery(query, null);
		if(cur != null && cur.moveToNext()){
			result = cur.getInt(0);
			cur.close();
		}

		return "Contact "+(result+1);
	}

	public int countNonTransmis(){
		int result = 0;

		String query = "SELECT count(*) FROM "+
				TABLENAME+ 
				" where  ("+
			 
				 FIELD_FLAG +
				"='"+CONTACT_CREATION+"' or "+
				 
				FIELD_FLAG +
					"='"+CONTACT_SUPPRESSION+"' or "+
				 
				 FIELD_FLAG +
				"='"+CONTACT_MODIFICATION+"')";
			 
		
		Cursor cur = db.conn.rawQuery(query, null);
		if(cur != null && cur.moveToNext()){
			result = cur.getInt(0);
			cur.close();
		}
		return result;
	}
	
	public boolean reset(String flag)
	{
		try
		{
			String query="update "+
					TABLENAME+	
					" set "+
					FIELD_FLAG+"='' "+
					" where "+
					FIELD_FLAG+
					"='"+flag+"'";
					 

			db.conn.execSQL(query);
			return true;
		}
		catch(Exception ex)
		{
			 
		}
		return false;
	}
	public boolean delete(String codecontact,String codeclient)
	{
		try
		{
			String query="delete from "+
					TABLENAME+	
					 
					" where "+
					FIELD_CODECLIENT+"='"+codeclient+"'" +
					" and "+
					FIELD_CODECONTACT+"='"+codecontact+"'";
					 

			db.conn.execSQL(query);
			return true;
		}
		catch(Exception ex)
		{
			 
		}
		return false;
	}
}
