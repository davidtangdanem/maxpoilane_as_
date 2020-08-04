package com.menadinteractive.segafredo.db;

import java.util.ArrayList;

import android.database.Cursor;

import com.menadinteractive.segafredo.communs.Fonctions;
import com.menadinteractive.segafredo.communs.Global;

/**
 * Donn�es saisies par les users
 * @author Marc VOUAUX
 *
 */
public class dbKD93PlanFiller extends dbKD{

	public final int KD_TYPE = 93;

	public final String FIELD_CODESOC = fld_kd_soc_code;//
	public final String FIELD_CODECLI = fld_kd_cli_code;//code plan (key)
	public final String FIELD_CODECDE = fld_kd_cde_code;//code cde associ� 
	public final String FIELD_CODEREP = fld_kd_dat_idx01;
	public final String FIELD_CODEQUEST = fld_kd_dat_idx02;//code quest
	public final String FIELD_REPONSE = fld_kd_dat_idx03;
	public final String FIELD_CODEPLAN = fld_kd_dat_idx04;//code plan
	public final String FIELD_DATEKEY = fld_kd_dat_idx05;//date (key)
	public final String FIELD_LBL = fld_kd_dat_data01;
	public final String FIELD_ISTODO = fld_kd_dat_data02;//oblig
	public final String FIELD_ISACTIF = fld_kd_dat_data03;//
	public final String FIELD_TYPE = fld_kd_dat_data04;//0=O/N, 1=Int, 2=Texte
	public final String FIELD_COMMENT= fld_kd_dat_data09;
	public final String FIELD_DATECREAT= fld_kd_dat_data11;
	public final String FIELD_DATEMODIF= fld_kd_dat_data13;
	public final String FIELD_SEND= fld_kd_dat_data14; //1/0 � envoyer

	//structure qui permet de GET/SET des donn�es vers ou � la classe 
	static public class data
	{
		public data()
		{
			CODESOC="";
			CODECDE="";
			CODECLI="";
			CODEREP="";
			CODEPLAN="";
			CODEQUEST="";
			REPONSE="";
			LBL="";
			ISTODO="";
			ISACTIF="";
			TYPE="";
			COMMENT="";
			DATECREAT="";
			DATEMODIF="";
			DATEKEY="";
			SEND="";
		}
		
		public String CODESOC;
		public String DATEKEY;
		public String REPONSE;
		public String CODEREP;
		public String CODECDE;
		public String CODECLI;
		public String CODEPLAN;
		public String CODEQUEST;
		public String LBL;
		public String ISTODO;
		public String ISACTIF;
		public String TYPE;
		public String COMMENT;
		public String DATECREAT;
		public String DATEMODIF;
		public String SEND;
	}
	
	MyDB db;
	public dbKD93PlanFiller(MyDB _db)
	{
		super();
		db=_db;	
	}

	public boolean isQuest(String codecli)
	{
		String query="select * from "+this.TABLENAME+
			" where "+
			this.fld_kd_dat_type+
			"='"+KD_TYPE+"'  and "+
			this.FIELD_CODECLI+"='"+codecli+"' ";
			
		Cursor cur=db.conn.rawQuery (query,null);
		if (cur.moveToNext() )
		{
			return true;
		}
		return false;
	}
	

	/**
	 * charge tous les plans repondus, 1 lignes par plans diff�rents
	 * pour faire le r�sum�
	 * @param codecli
	 * @param datekey
	 * @return
	 */
	public ArrayList<data> loadDistinctPlan(String codecli)
	{
		ArrayList<data> datas=new ArrayList<data>();
		
		String query="select distinct "+
				this.FIELD_CODEREP+","+
				"max("+this.FIELD_DATEMODIF+") as dm,"+
				"min("+this.FIELD_DATECREAT+") as dc "+
				
				" from "+this.TABLENAME+
				" where "+
				this.fld_kd_dat_type+
				"='"+KD_TYPE+"'";
		
				if (codecli.equals("")==false)
				{
					query+=" and "+
							this.FIELD_CODECLI+"='"+codecli+"' ";
				}

		
		Cursor cur=db.conn.rawQuery (query,null);
		if (cur.moveToNext() )
		{
			do
			{
				data mydata=new data();
				mydata.CODEREP=this.giveFld(cur,this.FIELD_CODEREP );
				mydata.DATECREAT=this.giveFld(cur,"dc" );
				mydata.DATEMODIF=this.giveFld(cur,"dm" );
			//	mydata.LBL=this.giveFld(cur,"compte" );//on utilise ce champ pour retourner le compte de question dans le questionnaire
				datas.add(mydata);
			}while(cur.moveToNext());
		}
		return datas;
	}
	

	
	/**
	 * charge un questionnairepour un client pour un jour
	 * @param codecli
	 * @param datekey
	 * @return
	 */
	public ArrayList<data> load(String codecli,String datekey)
	{
		ArrayList<data> datas=new ArrayList<data>();
		
		String query="select * from "+this.TABLENAME+
				" where "+
				this.fld_kd_dat_type+
				"='"+KD_TYPE+"'"+
				" and "+
				this.FIELD_CODECLI+"='"+codecli+"' "+
				" and "+
				this.FIELD_DATEKEY+"='"+datekey+"' ";
		
		Cursor cur=db.conn.rawQuery (query,null);
		if (cur.moveToNext() )
		{
			do
			{
				data mydata=new data();
				mydata.CODEPLAN=this.giveFld(cur,this.FIELD_CODEPLAN );
				mydata.CODEREP=this.giveFld(cur,this.FIELD_CODEREP );
				mydata.CODEQUEST=this.giveFld(cur,this.FIELD_CODEQUEST );
				mydata.COMMENT=this.giveFld(cur,this.FIELD_COMMENT );
				mydata.DATECREAT=this.giveFld(cur,this.FIELD_DATECREAT );
				mydata.DATEMODIF=this.giveFld(cur,this.FIELD_DATEMODIF );
				mydata.ISACTIF=this.giveFld(cur,this.FIELD_ISACTIF );
				mydata.TYPE=this.giveFld(cur,this.FIELD_TYPE) ;
				mydata.ISTODO=this.giveFld(cur,this.FIELD_ISTODO );
				mydata.LBL=this.giveFld(cur,this.FIELD_LBL );
				mydata.REPONSE=this.giveFld(cur,this.FIELD_REPONSE );
				mydata.CODECDE=this.giveFld(cur,this.FIELD_CODECDE );
				mydata.SEND=this.giveFld(cur,this.FIELD_SEND );
				
				datas.add(mydata);
			}while(cur.moveToNext());
		}
		return datas;
	}
	/**
	 * charge une reponse
	 * @author Marc VOUAUX
	 * @param ent
	 * @param numcde
	 * @param stBuf
	 * @return
	 */
	public boolean load(data mydata,String codecli,String datekey,String codequest,String codeplan)
	{
		String query="SELECT * FROM "+
				TABLENAME+
				" where "+
				fld_kd_dat_type+"="+KD_TYPE+
				" and "+
				this.FIELD_CODEPLAN+"="+
				"'"+codeplan+"'"+
				" and "+
				this.FIELD_CODECLI+"="+
				"'"+codecli+"'"+
				" and "+
				this.FIELD_CODEQUEST+"="+
				"'"+codequest+"'"+
				" and "+
				this.FIELD_DATEKEY+"="+
				"'"+datekey+"'";
		
		Cursor cur=db.conn.rawQuery (query,null);
		if (cur.moveToNext() )
		{	
			mydata.CODEPLAN=this.giveFld(cur,this.FIELD_CODEPLAN );
			mydata.CODEREP=this.giveFld(cur,this.FIELD_CODEREP );
			mydata.CODEQUEST=this.giveFld(cur,this.FIELD_CODEQUEST );
			mydata.COMMENT=this.giveFld(cur,this.FIELD_COMMENT );
			mydata.DATECREAT=this.giveFld(cur,this.FIELD_DATECREAT );
			mydata.DATEMODIF=this.giveFld(cur,this.FIELD_DATEMODIF );
			mydata.ISACTIF=this.giveFld(cur,this.FIELD_ISACTIF );
			mydata.TYPE=this.giveFld(cur,this.FIELD_TYPE) ;
			mydata.ISTODO=this.giveFld(cur,this.FIELD_ISTODO );
			mydata.LBL=this.giveFld(cur,this.FIELD_LBL );
			mydata.REPONSE=this.giveFld(cur,this.FIELD_REPONSE );
			mydata.CODECDE=this.giveFld(cur,this.FIELD_CODECDE );
			mydata.SEND=this.giveFld(cur,this.FIELD_SEND );

		}
		else
			return false;
		return true;
	}
	/**
	 * sauvegarde 
	 * @author Marc VOUAUX
	 * @param ent
	 * @param numcde
	 * @param stBuf
	 * @return
	 */
	public boolean save(data ent)
	{
		try
		{
			String query="SELECT * FROM "+
					TABLENAME+
					" where "+
					fld_kd_dat_type+"="+KD_TYPE+
					" and "+
					this.FIELD_CODEPLAN+"="+
					"'"+ent.CODEPLAN+"'"+
					" and "+
					this.FIELD_CODEQUEST+"="+
					"'"+ent.CODEQUEST+"'"+
					" and "+
					this.FIELD_CODECLI+"="+
					"'"+ent.CODECLI+"'"+
					" and "+
					this.FIELD_DATEKEY+"="+
					"'"+ent.DATEKEY+"'";

			Cursor cur=db.conn.rawQuery (query,null);
			if (cur.moveToNext() && !ent.CODEPLAN.equals(""))
			{


				query="UPDATE "+TABLENAME+
						" set "+
						this.FIELD_CODEREP+"="+
						"'"+MyDB.controlFld(ent.CODEREP) +"',"+
						this.FIELD_COMMENT+"="+
						"'"+MyDB.controlFld(ent.COMMENT) +"',"+
						this.FIELD_DATEMODIF+"="+
						"'"+MyDB.controlFld(ent.DATEMODIF) +"',"+
						this.FIELD_ISACTIF+"="+
						"'"+MyDB.controlFld(ent.ISACTIF) +"',"+
						this.FIELD_TYPE+"="+
						"'"+MyDB.controlFld(ent.TYPE) +"',"+
						this.FIELD_ISTODO+"="+
						"'"+MyDB.controlFld(ent.ISTODO) +"',"+
						this.FIELD_REPONSE+"="+
						"'"+MyDB.controlFld(ent.REPONSE) +"',"+
						this.FIELD_SEND+"="+
						"'"+MyDB.controlFld(ent.SEND) +"',"+
						this.FIELD_LBL+"="+
						"'"+MyDB.controlFld(ent.LBL) +"'"+
						
						" where "+
						FIELD_CODEPLAN+
						"='"+ent.CODEPLAN+"' "+
						" and "+
						dbKD.fld_kd_dat_type+
						"='"+KD_TYPE+"' "+
						" and "+
						this.FIELD_CODEQUEST+"="+
						"'"+ent.CODEQUEST+"'"+
						" and "+
						this.FIELD_CODECLI+"="+
						"'"+ent.CODECLI+"'"+
						" and "+
						this.FIELD_DATEKEY+"="+
						"'"+ent.DATEKEY+"'";	  		

				db.conn.execSQL(query);
			}
			else		  
			{	  		
				query="INSERT INTO " + TABLENAME +" ("+
						dbKD.fld_kd_dat_type+","+
						this.FIELD_CODECLI+","+
						this.FIELD_CODEPLAN+","+
						this.FIELD_CODEREP+","+   	  		
						this.FIELD_COMMENT+","+
						this.FIELD_DATECREAT+","+
						this.FIELD_CODEQUEST+","+
						this.FIELD_DATEMODIF+","+
						this.FIELD_ISACTIF+","+
						this.FIELD_TYPE+","+
						this.FIELD_ISTODO+","+
						this.FIELD_LBL+","+
						this.FIELD_CODECDE+","+
						this.FIELD_REPONSE+","+
						this.FIELD_SEND+","+
						this.FIELD_DATEKEY+" "+  
					
						") VALUES ("+
						String.valueOf(KD_TYPE)+","+
						"'"+MyDB.controlFld(ent.CODECLI)+"',"+
						"'"+MyDB.controlFld(ent.CODEPLAN)+"',"+
						"'"+MyDB.controlFld(ent.CODEREP)+"',"+
						"'"+MyDB.controlFld(ent.COMMENT)+"',"+
						"'"+MyDB.controlFld(ent.DATECREAT)+"',"+
						"'"+MyDB.controlFld(ent.CODEQUEST)+"',"+
						"'"+MyDB.controlFld(ent.DATEMODIF)+"',"+
						"'"+MyDB.controlFld(ent.ISACTIF)+"',"+
						"'"+MyDB.controlFld(ent.TYPE)+"',"+
						"'"+MyDB.controlFld(ent.ISTODO)+"',"+
						"'"+MyDB.controlFld(ent.LBL)+"',"+
						"'"+MyDB.controlFld(ent.CODECDE)+"',"+
						"'"+MyDB.controlFld(ent.REPONSE)+"',"+
						"'"+MyDB.controlFld(ent.SEND)+"',"+
						"'"+MyDB.controlFld(ent.DATEKEY)+"' "+
		
						")";

				db.conn.execSQL(query);
			}
		}
		catch(Exception ex)
		{
			Global.lastErrorMessage=ex.getLocalizedMessage();
			return false;
		}

		return true;
	}

	/**
	 * efface tous les questionnaires de tous les jours sauf aujourd'hui
	 * @param datekey
	 * @return
	 */
	public boolean deleteSent()
	{
		try
		{
			String datekey=Fonctions.getYYYYMMDD();
			String query="DELETE from "+
					TABLENAME+		
					" where "+
					dbKD.fld_kd_dat_type+
					"='"+this.KD_TYPE+"'"+
					" and "+
					this.FIELD_SEND+"="+
					"'1'"+
					" and "+FIELD_DATEKEY+"<>'"+datekey+"'";

			db.conn.execSQL(query);
			return true;
		}
		catch(Exception ex)
		{
			Global.lastErrorMessage=ex.getLocalizedMessage();
		}
		return false;
	}
	
	/*
	 * on compte les quests � envoyer sauf aujourd'hui
	 */
	public int countSendable()
	{

		try
		{
			String datekey=Fonctions.getYYYYMMDD();
			String query="select  count(*) from "+TABLENAME+" where "+
					fld_kd_dat_type +"='"+KD_TYPE+"'"+
					" and "+FIELD_DATEKEY+"<>'"+datekey+"'";

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
	
	/**
	 * le remplissage a t'il deja commenc� ?
	 * @param soc_code
	 * @param cli_code
	 * @param datekey
	 * @return
	 */
	public boolean isFillingStarted(String soc_code,String cli_code,String datekey)
	{
		try {
			String query="select * from "+this.TABLENAME+
					" where "+
					this.fld_kd_dat_type+
					"='"+KD_TYPE+"'"+
					" and "+
					this.FIELD_CODESOC+"='"+soc_code+"' "+
					" and "+
					this.FIELD_CODECLI+"='"+cli_code+"' "+
					" and "+
					this.FIELD_DATEKEY+"='"+datekey+"' ";
			
			Cursor cur=db.conn.rawQuery (query,null);
			if (cur.moveToNext() )
			{
				cur.close();
				return true;
			}
			return false;
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		return false;
	}

}
