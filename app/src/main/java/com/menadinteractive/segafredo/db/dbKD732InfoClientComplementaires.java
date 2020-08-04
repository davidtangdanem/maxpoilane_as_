package com.menadinteractive.segafredo.db;

import java.util.ArrayList;

import com.menadinteractive.segafredo.communs.Fonctions;
import com.menadinteractive.segafredo.communs.Global;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;

public class dbKD732InfoClientComplementaires extends dbKD {

	/**
	 * Initialisation variable
	 */
	public final static int KD_TYPE = 732;
	public final String FIELD_DATATYPE 	= fld_kd_dat_type;
	public final String FIELD_CODVRP 	= fld_kd_dat_idx01;
	public final static String FIELD_CODECLIENT 	= fld_kd_cli_code;
	public final String FIELD_LAT 		= fld_kd_dat_data01;
	public final String FIELD_LON 		= fld_kd_dat_data02;


	 
	MyDB db;
	public dbKD732InfoClientComplementaires(MyDB _db)
	{
		db=_db;
	}
 
	 
	public boolean clear(StringBuilder err)
	{
		try
		{
			String query="delete from "+TABLENAME+
					" where "+fld_kd_dat_type+"="+KD_TYPE;
			//db.conn.delete(TABLENAME, null, null);
			db.execSQL(query, err);
 
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
			String query="select count(*) from "+TABLENAME+
					" where "+fld_kd_dat_type+"="+KD_TYPE;
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
	 	
	public boolean save(String codeclient,String lat,String lon)
	{
		try
		{
		 
			String query="SELECT * FROM "+
					TABLENAME+
					" where "+
					FIELD_CODECLIENT+"="+
					"'"+codeclient+"' "+
					" and "+
					FIELD_DATATYPE+"="+KD_TYPE;
			
			Cursor cur=db.conn.rawQuery (query,null);
			if (cur.moveToNext() )
			{
				query="UPDATE "+TABLENAME+
						" set "+
						FIELD_LAT+"="+
						"'"+MyDB.controlFld(lat )+"', "+
						
												
						FIELD_LON+"="+
						"'"+MyDB.controlFld(lon )+"' "+
						
						" where "+
						FIELD_CODECLIENT+"="+
						"'"+codeclient+"' ";

				;	  		

				db.conn.execSQL(query);
				Global.dbLog.Insert("Client", "Save update", "", "Requete: "
						+ query, "", "");
			}
			else
			{
				query="INSERT INTO "+TABLENAME+
						"("+
						FIELD_DATATYPE+","+
						FIELD_CODECLIENT+","+
						FIELD_LAT+","+
						FIELD_LON+")"+
						" values "+
						"("+
						KD_TYPE+","+
						"'"+codeclient+"',"+
						"'"+lat+"',"+
						"'"+lon+"'"+
						")";
				 
				db.conn.execSQL(query);
				Global.dbLog.Insert("Client", "Save insert", "", "Requete: "
						+ query, "", "");
			}
					
		}
		catch(Exception ex)
		{
			Log.d("TAG", ex.getLocalizedMessage());
			return false;
		}

		return true;
	}
	 
}
