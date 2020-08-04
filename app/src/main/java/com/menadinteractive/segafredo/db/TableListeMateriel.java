package com.menadinteractive.segafredo.db;

import java.util.ArrayList;

import android.database.Cursor;
import android.os.Bundle;

import com.menadinteractive.segafredo.communs.Global;


public class TableListeMateriel extends DBMain{

	static public String TABLENAME="LISTEMATERIEL";

	public static final String FIELD_LM_CODEMACHINE		= "LM_CODEMACHINE";
	public static final String FIELD_LM_NOMMACHINE		= "LM_NOMMACHINE";
	public static final String FIELD_LM_CATEGOMACHINE	= "LM_CATEGMACHINE";
	

	public static String getFullFieldName(String field){
		return TABLENAME+"."+field;
	}


	public static final String TABLE_CREATE="CREATE TABLE ["+TABLENAME+"] ("+
			" ["+FIELD_LM_CODEMACHINE			+"] [varchar](50) NULL" 	+ COMMA +
			" ["+FIELD_LM_NOMMACHINE		+"] [varchar](250) NULL" 	+ COMMA +
			" ["+FIELD_LM_CATEGOMACHINE			+"] [varchar](50) NULL"	+ 
			")";
	
	
	static public class structListeMateriel
	{
		
		
		public String LM_CODEMACHINE = "";        
		public String LM_NOMMACHINE = "";
		public String LM_CATEGMACHINE = "";
		
		@Override
		public String toString() {
			return "structMaterielClient [LM_CODEMACHINE=" + LM_CODEMACHINE + ", LM_NOMMACHINE=" + LM_NOMMACHINE
					+  ", LM_CATEGOMACHINE=" + LM_CATEGMACHINE 
					+"]";
		}
	}
		
	MyDB db;
	public TableListeMateriel(MyDB _db)
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

	public structListeMateriel load(Cursor cursor){
		structListeMateriel ListeMateriel = new structListeMateriel();
		if(cursor != null){
			
		
			
			ListeMateriel.LM_CODEMACHINE = giveFld(cursor, FIELD_LM_CODEMACHINE);
			ListeMateriel.LM_NOMMACHINE = giveFld(cursor, FIELD_LM_NOMMACHINE);
			ListeMateriel.LM_CATEGMACHINE = giveFld(cursor, FIELD_LM_CATEGOMACHINE);
		
		}

	
		return ListeMateriel;
	}
	

	public structListeMateriel load(String categorie){
		structListeMateriel ListeMateriel = new structListeMateriel();
		String query = "SELECT *"
				+" FROM "+TABLENAME
				+" WHERE "+getFullFieldName(FIELD_LM_CATEGOMACHINE)+" = "+categorie+" ";

		Cursor cursor =  db.conn.rawQuery(query, null);

		if(cursor != null && cursor.moveToFirst()){

			ListeMateriel = load(cursor);
			cursor.close();
		}

		return ListeMateriel;
	}
	

	/**
	 * Permet d'obtenir une liste de structTarif à partir d'un cursor
	 * @param cursor
	 * @return ArrayList<structTarif>
	 */
	public ArrayList<structListeMateriel> getListOfCursorListeMateriel(Cursor cursor){
		ArrayList<structListeMateriel> list = new ArrayList<TableListeMateriel.structListeMateriel>();

		cursor.moveToFirst();
		while(!cursor.isAfterLast()){
			structListeMateriel ListeMateriel = new structListeMateriel();
			fillStruct(cursor,ListeMateriel);
			list.add(ListeMateriel);
			cursor.moveToNext();
		}
		cursor.close();
		return list;

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


	

	void fillStruct(Cursor cur,structListeMateriel ListeMateriel)
	{
		
		ListeMateriel.LM_CODEMACHINE=cur.getString(cur.getColumnIndex(FIELD_LM_CODEMACHINE));
		ListeMateriel.LM_NOMMACHINE=cur.getString(cur.getColumnIndex(FIELD_LM_NOMMACHINE));
		ListeMateriel.LM_CATEGMACHINE=cur.getString(cur.getColumnIndex(FIELD_LM_CATEGOMACHINE));
		
		
	}
	public ArrayList<Bundle> getListeMaterielFilters(String categorie,  String filter)
	{
		try
		{
			filter=MyDB.controlFld(filter);
			String query="";
			query="select * FROM "+
					TABLENAME +
					" where "+
					FIELD_LM_CATEGOMACHINE+
					"='"+categorie+"'  ";
			
			ArrayList<Bundle>  materiel=new ArrayList<Bundle>();
			Cursor cur=db.conn.rawQuery(query, null);
			while (cur.moveToNext())
			{
				Bundle cli=new Bundle();
				cli.putString(Global.dbListeMateriel.FIELD_LM_CODEMACHINE,cur.getString(cur.getColumnIndex(Global.dbListeMateriel.FIELD_LM_CODEMACHINE)));
				cli.putString(Global.dbListeMateriel.FIELD_LM_NOMMACHINE, cur.getString(cur.getColumnIndex(Global.dbListeMateriel.FIELD_LM_NOMMACHINE)));
				cli.putString(Global.dbListeMateriel.FIELD_LM_CATEGOMACHINE, cur.getString(cur.getColumnIndex(Global.dbListeMateriel.FIELD_LM_CATEGOMACHINE)));
			
				materiel.add(cli); 
			}
			if (cur!=null)
				cur.close();
			return materiel;
		}
		catch(Exception ex)
		{
			String err="";
			err=ex.getMessage();
			
		}

		return null;
	}
	
	public String getListeMateriel_Nom(String categorie,  String codearticle)
	{
		try
		{
			String stNom="";
			String query="";
			query="select * FROM "+
					TABLENAME +
					" where "+
					FIELD_LM_CATEGOMACHINE+
					"='"+categorie+"' and "+
					FIELD_LM_CODEMACHINE+
					"='"+codearticle+"'  ";
			
			ArrayList<Bundle>  materiel=new ArrayList<Bundle>();
			Cursor cur=db.conn.rawQuery(query, null);
			while (cur.moveToNext())
			{
				stNom=cur.getString(cur.getColumnIndex(Global.dbListeMateriel.FIELD_LM_NOMMACHINE));
			
			}
			if (cur!=null)
				cur.close();
			return stNom;
		}
		catch(Exception ex)
		{
			String err="";
			err=ex.getMessage();
			
		}

		return null;
	}
	
}
