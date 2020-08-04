package com.menadinteractive.segafredo.db;

import android.content.Context;
import android.database.Cursor;

import com.menadinteractive.segafredo.communs.Fonctions;
import com.menadinteractive.segafredo.communs.Global;
import com.menadinteractive.segafredo.db.dbKD100Visite.structVisite;
import com.menadinteractive.segafredo.plugins.Espresso;

public class dbKD101ClientVu  extends dbKD{

	public final int KD_TYPE = 101;
	public final String FIELD_SOC_CODE      = fld_kd_soc_code;
	public final String FIELD_CODECLI      	= fld_kd_cli_code;
	public final String FIELD_CODEREP      	= fld_kd_dat_idx01;
	public final String FIELD_VU          	= fld_kd_dat_data01;//0/1
	public final String FIELD_DATE          = fld_kd_dat_data02;//YYYYMMDD
	public final String FIELD_HEUREDEBUT    = fld_kd_dat_data03;//hhmm
	public final String FIELD_TYPEACT       = fld_kd_dat_data04;
	public final String FIELD_COMMENTAIRE   = fld_kd_dat_data05;
	public final String FIELD_NEXTDATE      = fld_kd_dat_data06;
	public final String FIELD_HEUREFIN      = fld_kd_dat_data07;//hhmm
	public final String fIELD_FLAG        	= fld_kd_dat_data08; //1 = modifi�, 2=delete
	
	MyDB db;
	public dbKD101ClientVu(MyDB _db)
	{
		super();
		db=_db;	
	}
	static public class structClientvu {
		public structClientvu()
		{	
			super();

			DATE ="";
			SOC_CODE ="";
			CODECLI ="";
			CODEREP ="";
			VU ="";
			FLAG="";
			DATE="";
			HEUREDEBUT="";
			HEUREFIN="";
			TYPEACT="";
			COMMENTAIRE="";
			NEXTDATE="";        
		}
		
		public String DATE  ;
		public String SOC_CODE   ;
		public String CODECLI   ; 
		public String CODEREP        ;
		public String VU        ;
		public String FLAG        ;  
		public String HEUREDEBUT="";
		public String HEUREFIN="";
		public String TYPEACT="";
		public String COMMENTAIRE="";
		public String NEXTDATE="";
	}
	

	static public class passePlat {
		public passePlat()
		{
			
			DATE ="";
			SOC_CODE ="";
			CODECLI ="";
			CODEREP ="";
			VU ="";
			FLAG="";
			DATE="";
			HEUREDEBUT="";
			HEUREFIN="";
			TYPEACT="";
			COMMENTAIRE="";
			NEXTDATE="";
			
			
			
			
			
		}

		public String DATE  ;
		public String SOC_CODE   ;
		public String CODECLI   ; 
		public String CODEREP        ;
		public String VU        ;
		public String FLAG        ;  
		public String HEUREDEBUT="";
		public String HEUREFIN="";
		public String TYPEACT="";
		public String COMMENTAIRE="";
		public String NEXTDATE="";
		
	}
	public int Count()
	{

		try
		{
			String query="select count(*) from "+TABLENAME+" where "+
					fld_kd_dat_type +"='"+KD_TYPE+"'";

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
	public int countModified()
	{

		try
		{
			String query="select count(*) from "+TABLENAME+" where "+
					fld_kd_dat_type +"='"+KD_TYPE+"'"+
					" and "+
					this.fIELD_FLAG+"<>'"+ KDSYNCHRO_RESET+  "'" ;
					
						

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
	public boolean load(structClientvu ent,String stcodeclient, String date, StringBuffer stBuf)
	{

		String query="SELECT * FROM "+
				TABLENAME+
				" where "+
				fld_kd_dat_type+"="+KD_TYPE+
				" and "+this.FIELD_CODECLI+"="+"'"+stcodeclient+"' "
				+" and "+this.FIELD_DATE+"='"+date+"'";
		
		Cursor cur=db.conn.rawQuery (query,null);
		if(cur != null && cur.moveToFirst() )
		{
			ent.SOC_CODE =this.giveFld(cur,this.FIELD_SOC_CODE );	
			ent.CODECLI =this.giveFld(cur,this.FIELD_CODECLI );
			ent.CODEREP =this.giveFld(cur,this.FIELD_CODEREP );
			ent.VU =this.giveFld(cur,this.FIELD_VU );
			ent.DATE =this.giveFld(cur,this.FIELD_DATE );
			ent.HEUREDEBUT =this.giveFld(cur,this.FIELD_HEUREDEBUT );
			ent.HEUREFIN =this.giveFld(cur,this.FIELD_HEUREFIN );		
			ent.TYPEACT =this.giveFld(cur,this.FIELD_TYPEACT );
			ent.COMMENTAIRE =this.giveFld(cur,this.FIELD_COMMENTAIRE );
			ent.NEXTDATE =this.giveFld(cur,this.FIELD_NEXTDATE );
			ent.FLAG=this.giveFld(cur,this.fIELD_FLAG);
			
			cur.close();
		}
		else
		{
			return false;
		}
		return true;
	}
	
	
	public boolean isVu(String stcodeclient,String codesoc,String date)
	{

		String query="SELECT * FROM "+
				TABLENAME+
				" where "+
				fld_kd_dat_type+"="+KD_TYPE+
				" and "+
				this.FIELD_CODECLI+"="+
				"'"+stcodeclient+"' "+
				" and "+
				this.FIELD_DATE+"="+
				"'"+date+"' "+
				" and "+
				this.FIELD_SOC_CODE+"="+
				"'"+codesoc+"' ";

		Cursor cur=db.conn.rawQuery (query,null);
		if (cur.moveToNext())
		{
			
			String vu   	=this.giveFld(cur,this.FIELD_VU    );
			if ( Fonctions.convertToBool(vu))
				return true;
		
		}

		return false;
	}



	public boolean save(passePlat ent)
	{
		try
		{
			//on efface l'existant et on sauve
			//delete(ent.CODECLI,ent.SOC_CODE,ent.DATE);
			
			String query = "SELECT * FROM " + TABLENAME + " where "
					+ fld_kd_dat_type + "=" + KD_TYPE + " and "
					+ this.FIELD_CODECLI + "=" + "'" + ent.CODECLI + "' AND "
					+ this.FIELD_SOC_CODE + "=" + "'" + ent.SOC_CODE + "' AND "
					+ this.FIELD_DATE + "=" + "'" + ent.DATE + "'  ";
			
			Cursor cur = db.conn.rawQuery(query, null);
			if (cur.moveToNext() ) {
				
				query = "UPDATE "
						+ TABLENAME
						+ " set "
						+ this.FIELD_HEUREDEBUT
						+ "="
						+ "'"
						+ MyDB.controlFld(ent.HEUREDEBUT)
						+ "',"
						+ this.FIELD_HEUREFIN
						+ "="
						+ "'"
						+ MyDB.controlFld(ent.HEUREFIN)
						+ "',"
						+ this.FIELD_TYPEACT
						+ "="
						+ "'"
						+ MyDB.controlFld(ent.TYPEACT)
						+ "',"
						+ this.FIELD_COMMENTAIRE
						+ "="
						+ "'"
						+ MyDB.controlFld(ent.COMMENTAIRE)
						+ "',"
						+ this.fIELD_FLAG
						+ "="
						+ "'"
						+ KDSYNCHRO_UPDATE
						+ "' "
						+ " where "
						+ fld_kd_dat_type + "=" + KD_TYPE + " and "
						+ this.FIELD_CODECLI + "=" + "'" + ent.CODECLI + "' AND "
						+ this.FIELD_SOC_CODE + "=" + "'" + ent.SOC_CODE + "' AND "
						+ this.FIELD_DATE + "=" + "'" + ent.DATE + "'  ";

				db.conn.execSQL(query);
				
			}
			else
			{
				
				 query="INSERT INTO " + TABLENAME +" ("+
						dbKD.fld_kd_dat_type+","+
						this.FIELD_SOC_CODE   		+","+
						this.FIELD_CODECLI   		+","+ 
						this.FIELD_CODEREP       		+","+  
						this.FIELD_VU    			+","+
						this.FIELD_DATE  			+","+
						this.FIELD_HEUREDEBUT  			+","+
						this.FIELD_HEUREFIN  			+","+
						this.FIELD_TYPEACT  			+","+
						this.FIELD_COMMENTAIRE  			+","+
						this.FIELD_NEXTDATE  			+","+
						this.fIELD_FLAG+""+

		  		") VALUES ("+
		  		String.valueOf(KD_TYPE)+","+
		  		"'"+ent.SOC_CODE+"',"+
		  		"'"+ent.CODECLI+"',"+
		  		"'"+ent.CODEREP+"',"+
		  		"'"+ent.VU+"',"+
		  		"'"+ent.DATE+"',"+
		  		"'"+ent.HEUREDEBUT+"',"+
		  		"'"+ent.HEUREFIN+"',"+
		  		"'"+ent.TYPEACT+"',"+
		  		"'"+ent.COMMENTAIRE+"',"+
		  		"'"+ent.NEXTDATE+"',"+
		  		"'"+KDSYNCHRO_UPDATE +"')";
				
				db.conn.execSQL(query);
			}
		
	
		}
		catch(Exception ex)
		{
			Global.lastErrorMessage=(ex.getMessage());
			return false;
		}

		return true;
	}
	
	public boolean save2(structClientvu ent)
	{
		
			try
			{
				//on efface l'existant et on sauve
				//delete(ent.CODECLI,ent.SOC_CODE,ent.DATE);
				
				String query = "SELECT * FROM " + TABLENAME + " where "
						+ fld_kd_dat_type + "=" + KD_TYPE + " and "
						+ this.FIELD_CODECLI + "=" + "'" + ent.CODECLI + "' AND "
						+ this.FIELD_SOC_CODE + "=" + "'" + ent.SOC_CODE + "' AND "
						+ this.FIELD_DATE + "=" + "'" + ent.DATE + "'  ";
				
				Cursor cur = db.conn.rawQuery(query, null);
				if (cur.moveToNext() ) {
					
					query = "UPDATE "
							+ TABLENAME
							+ " set "
							+ this.FIELD_HEUREDEBUT
							+ "="
							+ "'"
							+ MyDB.controlFld(ent.HEUREDEBUT)
							+ "',"
							+ this.FIELD_HEUREFIN
							+ "="
							+ "'"
							+ MyDB.controlFld(ent.HEUREFIN)
							+ "',"
							+ this.FIELD_TYPEACT
							+ "="
							+ "'"
							+ MyDB.controlFld(ent.TYPEACT)
							+ "',"
							+ this.FIELD_COMMENTAIRE
							+ "="
							+ "'"
							+ MyDB.controlFld(ent.COMMENTAIRE)
							+ "',"
							+ this.fIELD_FLAG
							+ "="
							+ "'"
							+ KDSYNCHRO_UPDATE
							+ "' "
							+ " where "
							+ fld_kd_dat_type + "=" + KD_TYPE + " and "
							+ this.FIELD_CODECLI + "=" + "'" + ent.CODECLI + "' AND "
							+ this.FIELD_SOC_CODE + "=" + "'" + ent.SOC_CODE + "' AND "
							+ this.FIELD_DATE + "=" + "'" + ent.DATE + "'  ";

					db.conn.execSQL(query);
					
				}
				else
				{
					
					 query="INSERT INTO " + TABLENAME +" ("+
							dbKD.fld_kd_dat_type+","+
							this.FIELD_SOC_CODE   		+","+
							this.FIELD_CODECLI   		+","+ 
							this.FIELD_CODEREP       		+","+  
							this.FIELD_VU    			+","+
							this.FIELD_DATE  			+","+
							this.FIELD_HEUREDEBUT  			+","+
							this.FIELD_HEUREFIN  			+","+
							this.FIELD_TYPEACT  			+","+
							this.FIELD_COMMENTAIRE  			+","+
							this.FIELD_NEXTDATE  			+","+
							this.fIELD_FLAG+""+

			  		") VALUES ("+
			  		String.valueOf(KD_TYPE)+","+
			  		"'"+ent.SOC_CODE+"',"+
			  		"'"+ent.CODECLI+"',"+
			  		"'"+ent.CODEREP+"',"+
			  		"'"+ent.VU+"',"+
			  		"'"+ent.DATE+"',"+
			  		"'"+ent.HEUREDEBUT+"',"+
			  		"'"+ent.HEUREFIN+"',"+
			  		"'"+ent.TYPEACT+"',"+
			  		"'"+ent.COMMENTAIRE+"',"+
			  		"'"+ent.NEXTDATE+"',"+
			  		"'"+KDSYNCHRO_UPDATE +"')";
					
					db.conn.execSQL(query);
				}
			
		
			}
			catch(Exception ex)
			{
				Global.lastErrorMessage=(ex.getMessage());
				return false;
			}

			return true;
	}

	public boolean delete(String Codeclient,String codesoc,String date)
	{
		try
		{
			String query="DELETE from "+
					TABLENAME+		
					" where "+
					FIELD_CODECLI+
					"='"+Codeclient+"'"+
					" and "+
					FIELD_SOC_CODE+
					"='"+codesoc+"' "+
					" and "+
					FIELD_DATE+
					"='"+date+"' "+
					" and "+
					dbKD.fld_kd_dat_type+
					"='"+KD_TYPE+"' ";

			db.conn.execSQL(query);
			return true;
		}
		catch(Exception ex)
		{
			Global.lastErrorMessage=(ex.getMessage());
		}
		return false;
	}
	public boolean resetFlag(StringBuffer err)
	{
		try
		{
			String query="UPDATE "+
					TABLENAME+		
					" SET "+
					dbKD.fld_kd_dat_data08+
					"='"+KDSYNCHRO_RESET+"'"+
					" where "+
					dbKD.fld_kd_dat_type+
					"='"+KD_TYPE+"' ";

			db.conn.execSQL(query);
			return true;
		}
		catch(Exception ex)
		{
			err.append(ex.getMessage());
		}
		return false;
	}
	public boolean DeleteFlag(String Codeclient,String stcoderayon,StringBuffer err)
	{
		try
		{
			String query="UPDATE "+
					TABLENAME+		
					" SET "+
					dbKD.fld_kd_dat_data08+
					"='"+KDSYNCHRO_DELETE+"'"+
					" where "+
					dbKD.fld_kd_dat_type+
					"='"+KD_TYPE+"' and "+
					dbKD.fld_kd_cli_code+
					"='"+Codeclient+"'"+
					" and "+
					dbKD.fld_kd_dat_data01+
					"='"+stcoderayon+"' "+
					" ";

			db.conn.execSQL(query);
			return true;
		}
		catch(Exception ex)
		{
			err.append(ex.getMessage());
		}
		return false;
	}
	
	public boolean deleteAll()
	{
		try
		{
			String query="DELETE from "+
					TABLENAME+		
					" where "+
					dbKD.fld_kd_dat_type+
					"='"+KD_TYPE+"' ";

			db.conn.execSQL(query);
			return true;
		}
		catch(Exception ex)
		{
			Global.lastErrorMessage=(ex.getMessage());
		}
		return false;
	}
	public String queryDeleteFromOnServer(Context c,String codeclient,String date,String user)
	{
		String del="delete from "+TABLENAME+
				" where "+FIELD_CODECLI+"='"+codeclient+"'"+
				" and "+FIELD_DATE+"='"+date+"'"+
				" and "+dbKD.fld_kd_dat_type+"="+KD_TYPE+
				" and "+FIELD_CODEREP+"='"+user+"'";
		return del;
	}

	
}
