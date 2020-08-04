package com.menadinteractive.segafredo.db;

import android.database.Cursor;

import com.menadinteractive.segafredo.communs.Fonctions;
import com.menadinteractive.segafredo.communs.Global;

public class dbHistoFactureEnt extends DBMain{

	public static  String TABLENAME="HISTOFACENT";
	
	public final static String FLD_CODVRP		= "CODVRP";
	public final static String FLD_NUMDOC		= "NUMDOC";
	public final static String FLD_CODECLIENT	= "CODECLIENT";//fonction en question
	public final static String FLD_DATEFAC		= "DATEFAC";
	public final static String FLD_DATEECHEANCE = "DATEECHEANCE"; //message � logger
	public final static String FLD_REMISE 		= "REMISE";//exception g�n�r�e si erreur
	public final static String FLD_MNTHT 		= "MNTHT";//complement d'info
	public final static String FLD_MNTTVA		= "MNTTVA"; //user id
	public final static String FLD_MNTTTC		= "MNTTTC";


	
	public static final String TABLE_CREATE =
		"CREATE TABLE "+TABLENAME+
	    " (	[CODVRP] [varchar](20) NULL,"+
	    "	[NUMDOC] [varchar](50) NULL,"+
	    " 	[CODECLIENT] [varchar](50) NULL,"+
	    " 	[DATEFAC] [varchar](15) NULL,"+
	    " 	[DATEECHEANCE] [varchar](15) NULL,"+
	    " 	[REMISE] [varchar](15) NULL,"+
	    " 	[MNTHT] [varchar](15) NULL,"+
	    " 	[MNTTVA] [varchar](15) NULL,"+
	    " 	[MNTTTC] [varchar](15) NULL"+
	    " )";

 
	
	
	MyDB db;
	public dbHistoFactureEnt(MyDB _db)
	{
		db=_db;	
	}
	

 
	/**
	 * @author Marc VOUAUX
	 * @param err
	 * @return
	 */
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
	 
	/**
	 * @author Marc VOUAUX
	 * @return
	 */
	public int Count()
	{

		try
		{
			String query="select count(*) from "+TABLENAME;
			Cursor cur=db.conn.rawQuery(query, null);
			if (cur.moveToNext())
			{
				int c=cur.getInt(0);
				cur.close(); 
				return c;
			}
			cur.close();
			return 0;
		}
		catch(Exception ex)
		{
			return -1;
		}
	
	}	
}
