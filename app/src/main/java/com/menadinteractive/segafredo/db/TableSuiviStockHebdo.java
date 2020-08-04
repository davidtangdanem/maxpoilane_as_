package com.menadinteractive.segafredo.db;



import java.util.ArrayList;

import android.database.Cursor;

 

//stock chez le client
public class TableSuiviStockHebdo<passeplat> extends DBMain{
	static public String TABLENAME="SUIVISTOCKHEBDO";

	public static final String FIELD_CODVRP 		= "CODVRP";
	public static final String FIELD_CODART 			= "CODART";
	public static final String FIELD_STOCKJ1		= "StockJ1";
	public static final String FIELD_QFACT			= "QFact";
	public static final String FIELD_QAVOIR			= "QAvoir";
	public static final String FIELD_QIN			= "QIn";
	public static final String FIELD_QOUT			= "QOut";
	public static final String FIELD_DEBUTPERIODE	=  "DebutPeriode";
	public static final String FIELD_FINPERIODE		= "FinPeriode";

	public static String getFullFieldName(String field){
		return TABLENAME+"."+field;
	}

	public static final String TABLE_CREATE="CREATE TABLE ["+TABLENAME+"] ("+
			" ["+FIELD_CODVRP 		+"] [nvarchar](30) NULL" + COMMA +
			" ["+FIELD_CODART 		+"] [nvarchar](50) NULL" + COMMA +
			" ["+FIELD_STOCKJ1		+"] [nvarchar](10) NULL" + COMMA +
			" ["+FIELD_QFACT		+"] [nvarchar](10) NULL" + COMMA +
			" ["+FIELD_QAVOIR		+"] [nvarchar](10) NULL" + COMMA +
			" ["+FIELD_QIN			+"] [nvarchar](10) NULL" + COMMA +
			" ["+FIELD_QOUT			+"] [nvarchar](10) NULL" + COMMA +
			" ["+FIELD_DEBUTPERIODE	+"] [nvarchar](30) NULL" + COMMA +
			" ["+FIELD_FINPERIODE	+"] [nvarchar](30) NULL" + 
			")";

	 
	static public class passePlat
	{
		
		
		public String FIELD_CODVRP 		= "";        
		public String FIELD_CODART 		= "";
		public String FIELD_STOCKJ1		= "";
		public String FIELD_QFACT		= "";  
		public String FIELD_QAVOIR		= "";  
		public String FIELD_QIN			= "";        
		public String FIELD_QOUT			= "";
		public String FIELD_DEBUTPERIODE	= "";
		public String FIELD_FINPERIODE	= "";  
		public String FIELD_NOMART	= "";  
		 
	}
	
	MyDB db;
	public TableSuiviStockHebdo(MyDB _db)
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
			pp.FIELD_CODVRP 		= giveFld(cursor, FIELD_CODVRP 		);
			pp.FIELD_CODART 		= giveFld(cursor, FIELD_CODART 		);
			pp.FIELD_STOCKJ1		= giveFld(cursor, FIELD_STOCKJ1		);
			pp.FIELD_QFACT			= giveFld(cursor, FIELD_QFACT			);
			pp.FIELD_QAVOIR			= giveFld(cursor, FIELD_QAVOIR			);
			pp.FIELD_QIN			= giveFld(cursor, FIELD_QIN			);
			pp.FIELD_QOUT			= giveFld(cursor, FIELD_QOUT		);	
			pp.FIELD_DEBUTPERIODE	= giveFld(cursor, FIELD_DEBUTPERIODE	);
			pp.FIELD_FINPERIODE		= giveFld(cursor, FIELD_FINPERIODE		);
 
			pp.FIELD_NOMART		= giveFld(cursor, dbSiteProduit.FIELD_PRO_NOMART1);
			
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
