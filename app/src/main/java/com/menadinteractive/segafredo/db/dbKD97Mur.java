package com.menadinteractive.segafredo.db;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import android.database.Cursor;

import com.menadinteractive.maxpoilane.Debug;
import com.menadinteractive.segafredo.communs.Fonctions;

public class dbKD97Mur extends dbKD{
	public static final int KD_TYPE = 97;
	public final String FIELD_CODECLI = fld_kd_cli_code;
	public final String FIELD_CODEREP = fld_kd_dat_idx01;
	public final String FIELD_ACTION_TYPE = fld_kd_dat_data01; // Not used yet !  Ajout client, prise de commande...
	public final String FIELD_TIMESTAMP_MS = fld_kd_dat_data02; 
	public final String FIELD_LIBELLE = fld_kd_dat_data03;
	public final String fIELD_FLAG = fld_kd_dat_data08; //1 = modification/insertion; 2=delete; 0=pas de mise à jour
	
	
	public static String ACTION_DEFAULT = "";
	public static String ACTION_CLIENT = "0";
	public static String ACTION_COMMANDE = "1";

	
	MyDB db;
	public dbKD97Mur(MyDB _db)
	{
		super();
		db=_db;	
	}

	static public class structMur {
		public structMur()
		{	
			super();
			CODEREP = "";      
			CODECLI = "";       
			ACTION  = "";       
			TIMESTAMP_MS = String.valueOf(System.currentTimeMillis()); 
			LIBELLE = "";      
			FLAG = "";         
		}
		
		
		
		public structMur(String cODEREP, String cODECLI, String aCTION,
				String tIMESTAMP_MS, String lIBELLE) {
			this();
			CODEREP = cODEREP;
			CODECLI = cODECLI;
			ACTION = aCTION;
			TIMESTAMP_MS = tIMESTAMP_MS;
			LIBELLE = lIBELLE;
		}
		
		public structMur(String cODEREP, String cODECLI, String aCTION, String lIBELLE) {
			this();
			CODEREP = cODEREP;
			CODECLI = cODECLI;
			ACTION = aCTION;
			LIBELLE = lIBELLE;
		}


		public String CODEREP ;
		public String CODECLI; 
		public String ACTION ;
		public String TIMESTAMP_MS ;
		public String LIBELLE ;
		public String FLAG ;
	}
	
	
	public int Count()
	{

		try
		{
			String query="select count(*) from "+TABLENAME+" where "+
					fld_kd_dat_type +"='"+KD_TYPE+"'";

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
	
	public int countModified()
	{

		try
		{
			String query="select count(*) from "+TABLENAME+" where "+
					fld_kd_dat_type +"='"+KD_TYPE+"'"+
					" and "+
					this.fIELD_FLAG+"<>'"+ KDSYNCHRO_RESET+  "'" ;
					
						

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
	
	public boolean isDownloadRequired(String stcodeclient, long timeMS){
		boolean result = true;
		structMur newerMur = new structMur();
		
		if(load(newerMur, stcodeclient, new StringBuffer(), false)){
			long now = System.currentTimeMillis();
			
			long latest = Fonctions.convertToLong(newerMur.TIMESTAMP_MS);
			
			Debug.Log("now - laster > timeMS "+now+" - "+latest+" > "+timeMS);
			Debug.Log("now - laster > timeMS "+(now - latest)+" > "+timeMS);
			if((now - latest)<timeMS)
				result = false;
		}
		
		return result;
	}
	
	public boolean isDownloadRequired(String stcodeclient, String timestampMS){
		boolean result = true;
		structMur newerMur = new structMur();
		
		if(load(newerMur, stcodeclient, new StringBuffer(), false)){
			GregorianCalendar nowCalendar = new java.util.GregorianCalendar(); 
			nowCalendar.setTime( new Date(Fonctions.convertToLong(timestampMS)) );
			
			GregorianCalendar newestCalendar = new java.util.GregorianCalendar(); 
			newestCalendar.setTime( new Date(Fonctions.convertToLong(newerMur.TIMESTAMP_MS)) );
			
			if(nowCalendar.get(Calendar.DAY_OF_MONTH) == newestCalendar.get(Calendar.DAY_OF_MONTH)){
				result = false;
			}
		}
		
		return result;
	}
	
	public boolean load(structMur ent,String stcodeclient,StringBuffer stBuf, boolean isOldest)
	{

		String query="SELECT * FROM "+
				TABLENAME+
				" where "+
				fld_kd_dat_type+"="+KD_TYPE+
				" and "+
				this.FIELD_CODECLI+"="+
				"'"+stcodeclient+"' ";
		if(isOldest)
			query+=" ORDER BY "+FIELD_TIMESTAMP_MS+" ASC";
		else //newer
			query+=" ORDER BY "+FIELD_TIMESTAMP_MS+" DESC";
		
		Cursor cur=db.conn.rawQuery (query,null);
		if (cur.moveToNext())
		{
			ent.CODEREP =this.giveFld(cur,this.FIELD_CODEREP );
			ent.CODECLI =this.giveFld(cur,this.FIELD_CODECLI );
			ent.ACTION =this.giveFld(cur,this.FIELD_ACTION_TYPE );
			ent.TIMESTAMP_MS =this.giveFld(cur,this.FIELD_TIMESTAMP_MS );
			ent.LIBELLE=this.giveFld(cur,this.FIELD_LIBELLE);
			ent.FLAG=this.giveFld(cur,this.fIELD_FLAG);
		}
		else
		{
			return false;
		}
		return true;
	}
	
	public boolean save(structMur ent,String stcodeclient,StringBuffer stBuf)
	{
		return save(ent,stcodeclient,KDSYNCHRO_UPDATE, stBuf);
	}
	
	
	public boolean save(structMur ent,String stcodeclient,String flag, StringBuffer stBuf)
	{
		try
		{

			if(ent != null && ent.CODEREP != null /*&& !ent.CODEREP.contains("test") MV*/){
				String query="INSERT INTO " + TABLENAME +" ("+
						dbKD.fld_kd_dat_type+","+
						this.FIELD_CODEREP+","+
						this.FIELD_CODECLI+","+ 
						this.FIELD_ACTION_TYPE+","+   
						this.FIELD_TIMESTAMP_MS+","+ 
						this.FIELD_LIBELLE+","+
						this.fIELD_FLAG+""+

	  		") VALUES ("+
	  		String.valueOf(KD_TYPE)+","+
	  		"'"+ent.CODEREP+"',"+
	  		"'"+stcodeclient+"',"+
	  		"'"+ent.ACTION+"',"+
	  		"'"+ent.TIMESTAMP_MS+"',"+
	  		"'"+MyDB.controlFld( ent.LIBELLE)+"',"+
	  		"'"+flag +"')";
				db.conn.execSQL(query);
			}
		}
		catch(Exception ex)
		{
			stBuf.setLength(0);
			stBuf.append(ex.getMessage());
			Debug.StackTrace(ex);
			return false;
		}

		return true;
	}
	
	
	public Cursor getMur(String stcodeclient){
		String query="SELECT * FROM "+
				TABLENAME+
				" where "+
				fld_kd_dat_type+"="+KD_TYPE+
				" and "+
				this.FIELD_CODECLI+"="+
				"'"+stcodeclient+"' ORDER BY "+FIELD_TIMESTAMP_MS+" DESC";
		
		return db.conn.rawQuery (query,null);
	}
	
	public boolean delete(String Codeclient,StringBuffer err)
	{
		try
		{
			String query="DELETE from "+
					TABLENAME+		
					" where "+
					dbKD.fld_kd_cli_code+
					"='"+Codeclient+"'"+
					" and "+
					dbKD.fld_kd_dat_type+
					"='"+KD_TYPE+"' ";

			db.conn.execSQL(query);
			return true;
		}
		catch(Exception ex)
		{
			err.append(ex.getMessage());
		}
		return false;
	}
	public boolean resetFlag(StringBuffer err)
	{
		try
		{
			String query="UPDATE "+
					TABLENAME+		
					" SET "+
					dbKD.fld_kd_dat_data08+
					"='"+KDSYNCHRO_RESET+"'"+
					" where "+
					dbKD.fld_kd_dat_type+
					"='"+KD_TYPE+"' ";

			db.conn.execSQL(query);
			return true;
		}
		catch(Exception ex)
		{
			err.append(ex.getMessage());
		}
		return false;
	}
	
	public boolean clear(MyDB db, int type,String table, String contrainte, StringBuilder err)
	{
		try
		{
			String query="delete from "+
					table+
					" where "+
					fld_kd_dat_type+"="+type
					+contrainte;


			Debug.Log("DELETE : "+query);
			return db.execSQL(query, err); 



		}
		catch(Exception ex)
		{
			Debug.StackTrace(ex);
			err.append(ex.getMessage());
			return false;
		}	
	}
	
	
}
