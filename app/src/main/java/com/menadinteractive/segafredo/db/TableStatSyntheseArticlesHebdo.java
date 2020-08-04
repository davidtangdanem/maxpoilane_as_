package com.menadinteractive.segafredo.db;



import java.util.ArrayList;

import android.database.Cursor;

 

//stock chez le client
public class TableStatSyntheseArticlesHebdo<passeplat> extends DBMain{
	static public String TABLENAME="SYNTHESEARTHEBDO";

	public static final String FIELD_CODVRP = "CODVRP";
	public static final String FIELD_CODART = "CODART";
	public static final String FIELD_QTEV= "QTEV";
	public static final String FIELD_QTEG= "QTEG";
	public static final String FIELD_MNTHT= "MNTHT";

	public static String getFullFieldName(String field){
		return TABLENAME+"."+field;
	}

	public static final String TABLE_CREATE="CREATE TABLE ["+TABLENAME+"] ("+
			" ["+FIELD_CODVRP+"] [nvarchar](30) NULL" + COMMA +
			" ["+FIELD_CODART+"] [nvarchar](50) NULL" + COMMA +
			" ["+FIELD_QTEV+"] [nvarchar](10) NULL" + COMMA +
			" ["+FIELD_QTEG+"] [nvarchar](10) NULL" + COMMA +
			" ["+FIELD_MNTHT+"] [nvarchar](10) NULL" + 
			")";

	 
	static public class passePlat
	{
		
		
		public String FIELD_CODART = "";        
		public String FIELD_QTEV = "";
		public String FIELD_QTEG = "";
		public String FIELD_NOMART = "";  
		public String FIELD_MNTHT = "";  
		 
	}
	
	MyDB db;
	public TableStatSyntheseArticlesHebdo(MyDB _db)
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

	public ArrayList<passePlat> load(String stFam ){
		 
		String query = "SELECT syn.*,pro."+dbSiteProduit.FIELD_PRO_NOMART1+
				" FROM "+TABLENAME+
				" syn left join "+
				dbSiteProduit.TABLENAME+ " pro on "+
				" pro."+dbSiteProduit.FIELD_PRO_CODART+"=syn."+FIELD_CODART+
				" FILTER "+
				" order by syn"+"."+FIELD_CODART;

		
		if (stFam.equals("")) 
			query=query.replace("FILTER", "");
		else 
			query=query.replace("FILTER", " where "+dbSiteProduit.FIELD_PRO_CODFAM+"='"+stFam+"' ");
		
		Cursor cursor =  db.conn.rawQuery(query, null);

		
		
		ArrayList<passePlat> arr=new ArrayList<passePlat>();
		while (cursor.moveToNext())
		{

			passePlat pp=new passePlat();
			pp.FIELD_CODART= giveFld(cursor, FIELD_CODART);
			pp.FIELD_MNTHT= giveFld(cursor, FIELD_MNTHT);
			pp.FIELD_QTEG= giveFld(cursor, FIELD_QTEG);
			pp.FIELD_QTEV= giveFld(cursor, FIELD_QTEV);
			pp.FIELD_NOMART= giveFld(cursor, dbSiteProduit.FIELD_PRO_NOMART1);
			
			arr.add(pp);
		}

		return arr;
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
