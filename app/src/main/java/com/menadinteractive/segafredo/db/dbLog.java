package com.menadinteractive.segafredo.db;

import com.menadinteractive.maxpoilane.app;
import com.menadinteractive.segafredo.communs.Fonctions;
import com.menadinteractive.segafredo.communs.Global;
import com.menadinteractive.segafredo.plugins.Espresso;

import android.database.Cursor;
import android.util.Log;

public class dbLog extends DBMain{
public static  String TABLENAME="kems_log";
	
	public final static String FLD_LOG_DATE= "datelog";
	public final static String FLD_LOG_ECRAN= "ecran";
	public final static String FLD_LOG_FONCTION= "fonction";//fonction en question
	public final static String FLD_LOG_PARAMS= "parametres";
	public final static String FLD_LOG_MESSAGE = "message"; //message � logger
	public final static String FLD_LOG_EXCEPTION = "exception";//exception g�n�r�e si erreur
	public final static String FLD_LOG_COMPLEMENT = "complement";//complement d'info
	public final static String FLD_LOG_USERID= "userid"; //user id
	public final static String FLD_LOG_VERSION= "version";


	
	public static final String TABLE_CREATE =
		"CREATE TABLE "+TABLENAME+
	    " (	[datelog] [varchar](20) NULL,"+
	    "	[ecran] [varchar](50) NULL,"+
	    " 	[fonction] [varchar](50) NULL,"+
	    " 	[parametres] [varchar](250) NULL,"+
	    " 	[exception] [varchar](900) NULL,"+
	    " 	[message] [varchar](900) NULL,"+
	    " 	[complement] [varchar](500) NULL,"+
	    " 	[userid] [varchar](50) NULL"+
	    " )";

	public static final String INSERT_STRING=
		"INSERT INTO T_NEGOS_LOG"+
		" ("+
		FLD_LOG_DATE  		+","+
		FLD_LOG_VERSION 		+","+
		FLD_LOG_ECRAN  		+","+
		FLD_LOG_FONCTION  	+","+
		FLD_LOG_PARAMS  	+","+
		FLD_LOG_MESSAGE  	+","+
		FLD_LOG_EXCEPTION  +","+
		FLD_LOG_COMPLEMENT +","+
		FLD_LOG_USERID+		") VALUES ";
	
	
	MyDB db;
	public dbLog(MyDB _db)
	{
		db=_db;	
	}
	

	public boolean   Insert( String ecran,String fonction,String params,String message, String exception,String complement)
	{
		{
			//String coderep=Preferences.getValue( app.getAppContext(), Espresso.LOGIN, "0");
			String coderep="";
			
			StringBuilder err=new StringBuilder();
			try
			{
				String query="";
				query="INSERT INTO "+
					TABLENAME+
					"("+
					FLD_LOG_DATE+","+
					FLD_LOG_ECRAN+","+
					FLD_LOG_FONCTION+","+
					FLD_LOG_PARAMS+","+
					FLD_LOG_MESSAGE+","+
					FLD_LOG_EXCEPTION+","+
					FLD_LOG_USERID+","+
					FLD_LOG_COMPLEMENT+")"+
					" VALUES "+
					"("+
					"'"+Fonctions.getYYYYMMDDhhmmss()+"',"+
					"'"+ecran+"',"+
					"'"+MyDB.controlFld(fonction)+"',"+			
					"'"+params+"',"+
					"'"+MyDB.controlFld(message)+"',"+
					"'"+exception+"',"+
					"'"+coderep+"',"+
					"'"+complement+"')";
				
				Log.d("TAG", query);
					return db.execSQL(query, err)	;	
			}
			catch(Exception ex)
			{
				err.append(ex.getMessage());
				Global.lastErrorMessage=ex.getLocalizedMessage();
				Log.d("TAG", Global.lastErrorMessage);
			}
		}
		return false;
	}
	/**
	 * @author Marc VOUAUX
	 * @param err
	 * @return
	 */
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
	public boolean delete()
	{
		StringBuilder err=new StringBuilder();
		try
		{

			
			String query="";
			query="delete from "+
				TABLENAME;
			
				return db.execSQL(query, err)	;	
		}
		catch(Exception ex)
		{
			err.append(ex.getMessage());
			Global.lastErrorMessage=ex.getLocalizedMessage();
		}
		return false;
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
