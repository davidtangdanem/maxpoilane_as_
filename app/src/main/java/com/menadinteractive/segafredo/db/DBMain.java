package com.menadinteractive.segafredo.db;

import android.database.Cursor;

public class DBMain {
	protected static final String COMMA = ", ";
	public DBMain()
	{

	}

	public void closeCursor(Cursor cursor){
		if(cursor != null)
			cursor.close();
	}

	public String giveFld(Cursor cur,String fld)
	{
		try
		{
			String val=cur.getString(cur.getColumnIndex(fld              ));
			if (val==null) val="";

			return val;
		}
		catch(Exception ex)
		{
//			Debug.Log("requete", "exception : "+ex.toString());
			return "";
		}
	}	

	public long giveLongFld(Cursor cur,String fld)
	{
		try
		{
			long val=cur.getLong(cur.getColumnIndex(fld              ));


			return val;
		}
		catch(Exception ex)
		{
			return -1;
		}
	}	

}