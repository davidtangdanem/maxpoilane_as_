package com.menadinteractive.segafredo.db;

import java.util.ArrayList;

import android.database.Cursor;

import com.menadinteractive.segafredo.communs.Fonctions;
import com.menadinteractive.segafredo.communs.Global;

public class dbKD71User extends dbKD {

	/**
	 * Initialisation variable
	 */
	public final int KD_TYPE_IDENT = 71;
	public final String FIELD_IDENT_DATATYPE = fld_kd_dat_type;
	public final String FIELD_IDENT_IDENT = fld_kd_dat_idx01;
	public final String FIELD_IDENT_PASSWORD = fld_kd_dat_idx02;
	public final String FIELD_IDENT_SOCIETE = fld_kd_dat_idx03;
	public final String FIELD_IDENT_ADMINLEVEL = fld_kd_dat_data01;
	public final String FIELD_IDENT_IP = fld_kd_dat_data02;

	static public class structIdentifiant {
		public String IDENT;
		public String PASSWORD;
		public String SOCIETE;
		public String ADMINLEVEL;
		public String IP;
	}
	MyDB db;
	public dbKD71User(MyDB _db)
	{
		db=_db;
	}

	/*
	 * Save Donn�e
	 */

	public boolean SaveIdent(String ident,String password,String societe,String IP,StringBuffer stBuf)
	{
		try
		{
			String query="SELECT * FROM "+
					TABLENAME+
					" where "+
					FIELD_IDENT_DATATYPE+"="+KD_TYPE_IDENT+

					"";

			Cursor cur=db.conn.rawQuery (query,null);
			if (cur.moveToNext())
			{
				query="UPDATE "+
						TABLENAME+
						" SET "+
						FIELD_IDENT_IDENT+"='"+ident+"' "+" , "+
						FIELD_IDENT_PASSWORD+"='"+password+"',"+
						FIELD_IDENT_IP+"='"+IP+"',"+
						FIELD_IDENT_SOCIETE+"='"+societe+"'"+
						" WHERE "+
						FIELD_IDENT_DATATYPE+"="+KD_TYPE_IDENT;

				db.conn.execSQL(query);


			}
			else		  
			{
				query="INSERT INTO "+
						TABLENAME+
						"("+
						FIELD_IDENT_IDENT+","+
						FIELD_IDENT_PASSWORD+","+
						FIELD_IDENT_SOCIETE+","+
						FIELD_IDENT_IP+","+
						FIELD_IDENT_DATATYPE+
						")"+
						" VALUES "+
						"("+
						"'"+ident+"',"+
						"'"+password+"',"+
						"'"+societe+"',"+
						"'"+IP+"',"+

						KD_TYPE_IDENT+
						")";

				db.conn.execSQL(query);



			}

		}
		catch(Exception ex)
		{
			stBuf.setLength(0);
			stBuf.append(ex.getMessage());
			return false;
		}

		return true;

	}
/*
	public boolean SaveSociete(String ident,StringBuffer stBuf)
	{
		try
		{
			String Societe="";
			Societe=Fonctions.GetStringDanem(Global.dbParam.getLbl(Global.dbParam.PARAM_SOCIETE, Fonctions.GetStringDanem("0")));

			String query="SELECT * FROM "+
					TABLENAME+
					" where "+
					FIELD_IDENT_DATATYPE+"="+KD_TYPE_IDENT+

					"";

			Cursor cur=db.conn.rawQuery (query,null);
			if (cur.moveToNext())
			{
				query="UPDATE "+
						TABLENAME+
						" SET "+
						FIELD_IDENT_IDENT+"='"+ident+"' "+" , "+

		  		FIELD_IDENT_SOCIETE+"='"+Societe+"'"+
		  		" WHERE "+
		  		FIELD_IDENT_DATATYPE+"="+KD_TYPE_IDENT;

				db.conn.execSQL(query);


			}


		}
		catch(Exception ex)
		{
			stBuf.setLength(0);
			stBuf.append(ex.getMessage());
			return false;
		}

		return true;

	}
*/
	public boolean LoadIdent(structIdentifiant ident,StringBuilder stBuf)
	{
		try
		{

			String query="SELECT * FROM "+TABLENAME+" where "+FIELD_IDENT_DATATYPE+"="+KD_TYPE_IDENT;


			Cursor cur=db.conn.rawQuery (query,null);
			if (cur.moveToNext())
			{
				ident.IDENT=cur.getString(cur.getColumnIndex(fld_kd_dat_idx01));
				ident.PASSWORD=cur.getString(cur.getColumnIndex(fld_kd_dat_idx02));
				ident.SOCIETE=cur.getString(cur.getColumnIndex(fld_kd_dat_idx03));
				ident.IP=cur.getString(cur.getColumnIndex(this.FIELD_IDENT_IP));

				//on va cherche dans param si l'user est admin
				ArrayList<String> params=new ArrayList<String>();
				if (Global.dbParam.getCommentAndValueAndLblParam(params,Global.dbParam.PARAM_ADMIN,ident.IDENT)==true)
					ident.ADMINLEVEL=params.get(1);
				else
					ident.ADMINLEVEL="0";
				return true;
			}


		}
		catch(Exception ex)
		{
			stBuf.setLength(0);
			stBuf.append(ex.getMessage());
			return false;
		}

		return false;

	}
}
