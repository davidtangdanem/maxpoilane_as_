package com.menadinteractive.segafredo.db;

import java.util.ArrayList;

import android.database.Cursor;

public class TableFermeture  extends DBMain{
	static public String TABLENAME="FERMETURE";

	public static final String FIELD_CODE = "CODE";
	public static final String FIELD_CODEVRP = "CODEVRP";
	public static final String FIELD_DU = "DU";
	public static final String FIELD_AU= "AU";



	/** Le champ ci-dessous est utilisé comme alias pour la requete !*/
	public static final String FIELD_CODE_ALIAS = "FERMETURECODE";


	public static String getFullFieldName(String field){
		return TABLENAME+"."+field; 
	}


	public static final String TABLE_CREATE="CREATE TABLE ["+TABLENAME+"] ("+
			" ["+FIELD_CODE+"] [nvarchar](100) NULL" + COMMA +
			" ["+FIELD_CODEVRP+"] [nvarchar](255) NULL" + COMMA +
			" ["+FIELD_DU+"] [nvarchar](255) NULL" + COMMA +
			" ["+FIELD_AU+"] [nvarchar](255) NULL"+
			")";

	static public class structFermeture
	{
		public String FIELD_CODE = "";                 
		public String FIELD_DU  = "";                  
		public String FIELD_AU = "";

		public String getFormatedFullDate(){
			String result = "";
			String from = getFormatedDate(FIELD_DU);
			String to = getFormatedDate(FIELD_AU);
			if(!from.equals("") && !to.equals(""))
				result = "du "+from+" au "+to;
			return result;
		}
		public String getFormatedDate(String date){
			String result = "";
			if(date != null && date.length() == 5 && !date.equals("00000")){
				result = date.substring(3, 5)+"/"+date.substring(1, 3);
			}
			return result;
		}
	}

	MyDB db;
	public TableFermeture(MyDB _db)
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

	public structFermeture load(Cursor cursor){
		structFermeture fermeture = new structFermeture();
		if(cursor != null){
			fermeture.FIELD_CODE = giveFld(cursor, FIELD_CODE);
			fermeture.FIELD_DU = giveFld(cursor, FIELD_DU);
			fermeture.FIELD_AU = giveFld(cursor, FIELD_AU);
		}
		return fermeture;
	}

	public ArrayList<structFermeture> load(String code){
		ArrayList<structFermeture> fermetures = new ArrayList<structFermeture>();;

		String query = "SELECT *"
				+" FROM "+TABLENAME
				+" WHERE "+getFullFieldName(FIELD_CODE)+" = '"+code+"'";

		Cursor cursor =  db.conn.rawQuery(query, null);


		if(cursor != null && cursor.moveToFirst()){
			while(cursor.isAfterLast() == false)
			{
				structFermeture fermeture = load(cursor);
				fermetures.add(fermeture);
				cursor.moveToNext();
			}
			cursor.close();

		}
		return fermetures;
	}

	public String getAllFermetures(String code){
		String result = "";
		ArrayList<structFermeture> fermetures = load(code);
		//		Debug.Log("fermeture size : "+fermetures.size());
		for (int i = 0; i < fermetures.size(); i++){
			if(i >0)
				result +="   et   ";

			structFermeture f = fermetures.get(i);
			result +=f.getFormatedFullDate();
		}


		return result;
	}


	/*
	 Table periode fermeture
Code client;Du;Au
	 */
}
