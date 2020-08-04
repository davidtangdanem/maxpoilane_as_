package com.menadinteractive.segafredo.db;

import java.util.ArrayList;

import android.database.Cursor;

import com.menadinteractive.segafredo.communs.Fonctions;

public class dbSociete extends DBMain{
public String TABLENAME="SITE_SOCIETE";
	
	public final String FIELD_SOC_CODE        	="CODE" 	 		;                 
	public final String FIELD_SOC_NOM      		="NOM" 		;                  
	public final String FIELD_SOC_ADRESSE       ="ADRESSE"  	; 
	public final String FIELD_SOC_ADRESSE2       ="ADRESSE2"  	; 
	public final String FIELD_SOC_CODEPOSTAL    ="CODEPOSTAL"  	;  
	public final String FIELD_SOC_VILLE       	="VILLE"  	;  
	public final String FIELD_SOC_PAYS       	="PAYS"  	;  
	public final String FIELD_SOC_SIRET       	="SIRET"  	;  
	public final String FIELD_SOC_TELEP       	="TELEP"  	; 
	public final String FIELD_SOC_FAX       	="FAX"  	; 
	public final String FIELD_SOC_EMAIL       	="MAIL"  	; 
	public final String FIELD_SOC_WEB      		="WEB"  	; 
	public final String FIELD_SOC_NUMTVA     	="TVA"  	; 
	
	public static final String TABLE_CREATE=
			"CREATE TABLE [SITE_SOCIETE]("+
					"		[CODE] [nvarchar](20) NULL,"+
					"		[NOM] [nvarchar](50) NULL,"+
					"		[ADRESSE] [nvarchar](100) NULL,"+
					"		[ADRESSE2] [nvarchar](100) NULL,"+
					"		[CODEPOSTAL] [nvarchar](30) NULL,"+
					"		[VILLE] [nvarchar](60) NULL,"+
					"		[PAYS] [nvarchar](255) NULL,"+
					"		[SIRET] [nvarchar](70) NULL,"+
					"		[TVA] [nvarchar](70) NULL,"+
					"		[TELEP] [nvarchar](30) NULL,"+
					"		[FAX] [nvarchar](30)  NULL,"+
					"		[WEB] [nvarchar](70)  NULL,"+
					"		[MAIL] [nvarchar](80) NULL"+
					
					")";

	static public class structSoc
	{
		public String FIELD_SOC_CODE        	="" 	 		;                 
		public String FIELD_SOC_NOM      		="" 		;                  
		public String FIELD_SOC_ADRESSE       =""  	;  
		public String FIELD_SOC_ADRESSE2       =""  	;  
		public String FIELD_SOC_CODEPOSTAL    =""  	;  
		public String FIELD_SOC_VILLE       	=""  	;  
		public String FIELD_SOC_PAYS       	=""  	;  
		public String FIELD_SOC_SIRET       	=""  	;  
		public String FIELD_SOC_TELEP       	=""  	; 
		public String FIELD_SOC_FAX       	=""  	; 
		public String FIELD_SOC_EMAIL       	=""  	; 
		public String FIELD_SOC_WEB      		=""  	; 
		public String FIELD_SOC_TVA      		=""  	; 
		
	}
	
	MyDB db;
	public dbSociete(MyDB _db)
	{
		super();
		db=_db;
	}
	public boolean Clear(StringBuilder err)
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
	
	public ArrayList<String[]> getSocs(String codesocSel)
	{
		ArrayList<String[]> array=new ArrayList<String[]>();

		try
		{
			String query="select * from "+TABLENAME;
			if (codesocSel.equals("")==false)
			{
				query+=" where "+
					this.FIELD_SOC_CODE+"='"+codesocSel+"'";
			}
			Cursor cur=db.conn.rawQuery(query, null);
			while (cur.moveToNext())
			{
				String codesoc=Fonctions.GetStringDanem(cur.getString(cur.getColumnIndex(this.FIELD_SOC_CODE))).trim();
				String rs=Fonctions.GetStringDanem(cur.getString(cur.getColumnIndex(this.FIELD_SOC_NOM)));

				String []tabField=new String[2];
				
				tabField[0]=codesoc;
				tabField[1]=rs;
				array.add(tabField);
				
			}
			return array;
		}
		catch(Exception ex)
		{
			return array;
		}
	}
	
	public structSoc getSoc(String soc_code)
	{
		try
		{
			String query="select * from "+TABLENAME+ " where "+this.FIELD_SOC_CODE+" = '"+ soc_code+"'";
			structSoc soc=new structSoc();
			
			Cursor cur=db.conn.rawQuery(query, null);
			if (cur.moveToNext())
			{
				soc.FIELD_SOC_CODE=Fonctions.GetStringDanem(cur.getString(cur.getColumnIndex(this.FIELD_SOC_CODE))).trim();
				soc.FIELD_SOC_NOM=Fonctions.GetStringDanem(cur.getString(cur.getColumnIndex(this.FIELD_SOC_NOM)));
				soc.FIELD_SOC_ADRESSE=Fonctions.GetStringDanem(cur.getString(cur.getColumnIndex(this.FIELD_SOC_ADRESSE)));
				soc.FIELD_SOC_ADRESSE2=Fonctions.GetStringDanem(cur.getString(cur.getColumnIndex(this.FIELD_SOC_ADRESSE2)));
				soc.FIELD_SOC_CODEPOSTAL=Fonctions.GetStringDanem(cur.getString(cur.getColumnIndex(this.FIELD_SOC_CODEPOSTAL)));
				soc.FIELD_SOC_EMAIL=Fonctions.GetStringDanem(cur.getString(cur.getColumnIndex(this.FIELD_SOC_EMAIL)));
				soc.FIELD_SOC_FAX=Fonctions.GetStringDanem(cur.getString(cur.getColumnIndex(this.FIELD_SOC_FAX)));
				soc.FIELD_SOC_SIRET=Fonctions.GetStringDanem(cur.getString(cur.getColumnIndex(this.FIELD_SOC_SIRET)));
				soc.FIELD_SOC_PAYS=Fonctions.GetStringDanem(cur.getString(cur.getColumnIndex(this.FIELD_SOC_PAYS)));
				soc.FIELD_SOC_TELEP=Fonctions.GetStringDanem(cur.getString(cur.getColumnIndex(this.FIELD_SOC_TELEP)));
				soc.FIELD_SOC_VILLE=Fonctions.GetStringDanem(cur.getString(cur.getColumnIndex(this.FIELD_SOC_VILLE)));
				soc.FIELD_SOC_WEB=Fonctions.GetStringDanem(cur.getString(cur.getColumnIndex(this.FIELD_SOC_WEB)));
				soc.FIELD_SOC_TVA=Fonctions.GetStringDanem(cur.getString(cur.getColumnIndex(this.FIELD_SOC_NUMTVA)));
					}
			return soc;
		}
		catch(Exception ex)
		{
			return null;
		}
	}
	public String  GetEmailSociete()
	{

		String stEmailSoc="";
		try
		{
			String query="select * from "+TABLENAME;
			Cursor cur=db.conn.rawQuery(query, null);
			if (cur.moveToNext())
			{
				stEmailSoc=Fonctions.GetStringDanem(cur.getString(cur.getColumnIndex(this.FIELD_SOC_EMAIL)));
			}
			
		}
		catch(Exception ex)
		{
			return stEmailSoc;
		}

		return stEmailSoc;
	}
	
}
