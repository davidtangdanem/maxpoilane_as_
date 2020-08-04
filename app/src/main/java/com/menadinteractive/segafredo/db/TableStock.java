package com.menadinteractive.segafredo.db;

import java.util.ArrayList;

import com.menadinteractive.maxpoilane.Debug;

import android.database.Cursor;

//stock chez le client
public class TableStock extends DBMain{
	static public String TABLENAME="STOCK";

	public static final String FIELD_SOC_CODE = "SOC_CODE";
	public static final String FIELD_CLI_CODE = "CLI_CODE";
	public static final String FIELD_PRO_CODE = "PRO_CODE";
	public static final String FIELD_PRO_LABEL = "PRO_LABEL";
	public static final String FIELD_STOCK = "STOCK";

	public static String getFullFieldName(String field){
		return TABLENAME+"."+field;
	}

	public static final String TABLE_CREATE="CREATE TABLE ["+TABLENAME+"] ("+
			" ["+FIELD_SOC_CODE+"] [nvarchar](100) NULL" + COMMA +
			" ["+FIELD_CLI_CODE+"] [nvarchar](255) NULL" + COMMA +
			" ["+FIELD_PRO_CODE+"] [nvarchar](255) NULL" + COMMA +
			" ["+FIELD_PRO_LABEL+"] [nvarchar](255) NULL" + COMMA +
			" ["+FIELD_STOCK+"] [nvarchar](255) NULL" + 
			")";

	static public class structStock
	{
		public String SOC_CODE = "";        
		public String CLI_CODE = "";
		public String PRO_CODE = "";
		public String PRO_LABEL  = "";                  
		public String STOCK = "";
		
		@Override
		public String toString() {
			return "structStock [SOC_CODE=" + SOC_CODE + ", CLI_CODE="
					+ CLI_CODE + ", PRO_CODE=" + PRO_CODE + ", PRO_LABEL="
					+ PRO_LABEL + ", STOCK=" + STOCK + "]";
		}
	}

	MyDB db;
	public TableStock(MyDB _db)
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

	public structStock load(Cursor cursor){
		structStock stock = new structStock();
		if(cursor != null){
			stock.SOC_CODE = giveFld(cursor, FIELD_SOC_CODE);
			stock.CLI_CODE = giveFld(cursor, FIELD_CLI_CODE);
			stock.PRO_CODE = giveFld(cursor, FIELD_PRO_CODE);
			stock.PRO_LABEL = giveFld(cursor, FIELD_PRO_LABEL);
			stock.STOCK = giveFld(cursor, FIELD_STOCK);
		}
		return stock;
	}

	public Cursor getAll(String cliCode){
		String query = "SELECT *"
				+" FROM "+TABLENAME
				+" WHERE "+getFullFieldName(FIELD_CLI_CODE)+" = '"+cliCode+"'"
				+" ORDER BY CAST("+getFullFieldName(FIELD_STOCK)+" as Integer) DESC";

		Debug.Log(query);
		Cursor cursor =  db.conn.rawQuery(query, null);
		return cursor;
	}

	public ArrayList<structStock> getAllAsList(String cliCode){
		ArrayList<structStock> result = new ArrayList<TableStock.structStock>();
		Cursor cursor = getAll(cliCode);

		if(cursor != null && cursor.moveToFirst()){
			while(cursor.isAfterLast() == false)
			{
				structStock stock = load(cursor);
				result.add(stock);
				cursor.moveToNext();
			}
			cursor.close();
		}
		return result;
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
}
