package com.menadinteractive.segafredo.db;

import java.util.ArrayList;

import android.database.Cursor;
import android.os.Bundle;

import com.menadinteractive.maxpoilane.R;
import com.menadinteractive.segafredo.communs.Global;
import com.menadinteractive.segafredo.communs.assoc;
import com.menadinteractive.segafredo.db.dbKD451RetourMachineclient.structRetourMarchineClient;

public class dbKD452ComptageMachineclient extends dbKD{

	public final int KD_TYPE = 452;
	public final String FIELD_CMC_SOCCODE = this.fld_kd_soc_code;
	public final String FIELD_CMC_CODEREP = fld_kd_dat_idx01;
	public final String FIELD_CMC_NUMSOUCHE = fld_kd_cde_code;
	public final String FIELD_CMC_CODECLI = fld_kd_cli_code;// code cli
	public final String FIELD_CMC_CODEART = fld_kd_pro_code;
	public final String FIELD_CMC_NUMSERIEFABRICANT = fld_kd_dat_data01;
	public final String FIELD_CMC_NUMSERIESZ = fld_kd_dat_data02;
	public final String FIELD_CMC_COMMENTAIRE = fld_kd_dat_data03;
	public final String FIELD_CMC_DATE = fld_kd_dat_data04;
	public final String FIELD_CMC_CATEGORIE = fld_kd_dat_data05;
	public final String FIELD_CMC_LBL_ART = fld_kd_dat_data06;
	public final String FIELD_CMC_ISPRINTED= fld_kd_dat_data07;
	 
	MyDB db;

	public dbKD452ComptageMachineclient(MyDB _db) {
		super();
		db = _db;
	}

	public boolean isPrintOk(String codecli,String numsouche)
	{
		
		String query = "SELECT * FROM " + TABLENAME + " where "
				+ fld_kd_dat_type + "=" + KD_TYPE + " and "
				+ this.FIELD_CMC_CODECLI+ "=" + "'"+codecli+"' and "+
				FIELD_CMC_NUMSOUCHE+"='"+numsouche+"'";
		
		Cursor cur = db.conn.rawQuery(query, null);
		if(cur.moveToNext()){
			structComptageMachineClient entete =new structComptageMachineClient();
			 get(entete,cur);
			if (entete.ISPRINTED.equals("1"))
					return true;
			return false;
		}
		
		return true;
	}
	public structComptageMachineClient get(structComptageMachineClient ent, Cursor cur){
 		
		ent.CODEART = this.giveFld(cur, this.FIELD_CMC_CODEART);
		ent.CODECLI = this.giveFld(cur, this.FIELD_CMC_CODECLI);
		ent.CODEREP = this.giveFld(cur, this.FIELD_CMC_CODEREP);
		ent.COMMENTAIRE = this.giveFld(cur, this.FIELD_CMC_COMMENTAIRE);
		ent.DATE = this.giveFld(cur,FIELD_CMC_DATE);
		ent.CATEGORIE = this.giveFld(cur, this.FIELD_CMC_CATEGORIE);
		ent.LBL_ART = this.giveFld(cur, this.FIELD_CMC_LBL_ART);
		ent.NUMSERIEFABRICANT = this.giveFld(cur, this.FIELD_CMC_NUMSERIEFABRICANT);
		ent.NUMSERIESZ = this.giveFld(cur, this.FIELD_CMC_NUMSERIESZ);
		ent.NUMSOUCHE = this.giveFld(cur, this.FIELD_CMC_NUMSOUCHE);
		ent.SOCCODE = this.giveFld(cur, this.FIELD_CMC_SOCCODE);
		ent.ISPRINTED = this.giveFld(cur, this.FIELD_CMC_ISPRINTED);
		
		return ent;
	}
	public int Count() {

		try {
			String query = "select count(*) from " + TABLENAME + " where "
					+ fld_kd_dat_type + "='" + KD_TYPE + "'";

			Cursor cur = db.conn.rawQuery(query, null);
			if (cur.moveToNext()) {
				return cur.getInt(0);
			}
			return 0;
		} catch (Exception ex) {
			return -1;
		}

	}

	
	public int Count(String codecli) {

		try {
			String query = "select count(*) from " + TABLENAME + " where "
					+ fld_kd_dat_type + "='" + KD_TYPE + "'" + " and "
					+ this.FIELD_CMC_CODECLI + "='" + codecli + "'";

			Cursor cur = db.conn.rawQuery(query, null);
			if (cur.moveToNext()) {
				int count=cur.getInt(0);
				return count;
			}
			return 0;
		} catch (Exception ex) {
			return -1;
		}

	}

	

	 static public class structComptageMachineClient {
		public structComptageMachineClient() {
			
			SOCCODE = "";
			CODEREP = "";
			CODECLI = "";
			CODEART="";
			NUMSERIEFABRICANT = "";
			NUMSERIESZ = "";
			DATE = "";
			NUMSOUCHE="";
			CATEGORIE="";
			COMMENTAIRE="";
			LBL_ART="";
			

		}

		public String SOCCODE;
		public String CODEREP;
		public String CODECLI;
		public String CODEART;
		public String NUMSERIEFABRICANT;
		public String NUMSERIESZ;
		public String DATE;
		public String NUMSOUCHE;
		public String CATEGORIE;
		public String COMMENTAIRE;
		public String LBL_ART;
		public String ISPRINTED;
		
	}

	 public boolean setPrint(String codeclient,String numsouche) {
			try {
				String query = "update  " + TABLENAME 
						+" set "+FIELD_CMC_ISPRINTED+"='1'"
						+ " where "
						+ fld_kd_dat_type + "=" + KD_TYPE + " and "
						+ this.FIELD_CMC_CODECLI + "=" + "'" + codeclient + "' and "
						+ this.FIELD_CMC_NUMSOUCHE+ "=" + "'" + numsouche + "' ";
				
				
				db.conn.execSQL(query);

				Global.dbLog.Insert("pret machine", "setprint", "", "Numsouche:" + numsouche, "",
						"");

				return true;
			} catch (Exception ex) {
			 
			}
			return false;
		}
	public boolean load(structComptageMachineClient ent, String codeclient,String numeroeSZ,String numeroserie, StringBuffer stBuf) {
		
		String query = "SELECT * FROM " + TABLENAME + " where "
				+ fld_kd_dat_type + "=" + KD_TYPE + " and "
				+ this.FIELD_CMC_CODECLI + "=" + "'" + codeclient + "' and "
				+ this.FIELD_CMC_NUMSERIESZ + "=" + "'" + numeroeSZ + "' and "
				+ this.FIELD_CMC_NUMSERIEFABRICANT + "=" + "'" + numeroserie + "'  ";
				

		Cursor cur = db.conn.rawQuery(query, null);
		if (cur.moveToNext() ) {
			 get(ent,cur);
		

		} else
			return false;
		return true;
	}
 
	public ArrayList<Bundle> getListeMateriel(String codeclient)
	{
		try
		{
			String query = "SELECT * FROM " + TABLENAME + " where "
					+ fld_kd_dat_type + "=" + KD_TYPE + " and "
					+ this.FIELD_CMC_CODECLI + "=" + "'" + codeclient + "'  ";	
			
			ArrayList<Bundle>  materiel=new ArrayList<Bundle>();
			Cursor cur=db.conn.rawQuery(query, null);
			while (cur.moveToNext())
			{
				
			
				
				Bundle cli=new Bundle();
			
				cli.putString(Global.dbKDComptageMachineClient.FIELD_CMC_COMMENTAIRE,this.giveFld(cur, this.FIELD_CMC_COMMENTAIRE));
				cli.putString(Global.dbKDComptageMachineClient.FIELD_CMC_NUMSOUCHE,this.giveFld(cur, this.FIELD_CMC_NUMSOUCHE));
				cli.putString(Global.dbKDComptageMachineClient.FIELD_CMC_CODEART,this.giveFld(cur, this.FIELD_CMC_CODEART));
				cli.putString(Global.dbKDComptageMachineClient.FIELD_CMC_LBL_ART,this.giveFld(cur, this.FIELD_CMC_LBL_ART));
				cli.putString(Global.dbKDComptageMachineClient.FIELD_CMC_NUMSERIESZ,this.giveFld(cur, this.FIELD_CMC_NUMSERIESZ));
				cli.putString(Global.dbKDComptageMachineClient.FIELD_CMC_NUMSERIEFABRICANT, this.giveFld(cur, this.FIELD_CMC_NUMSERIEFABRICANT));
				cli.putString(Global.dbKDComptageMachineClient.FIELD_CMC_CATEGORIE, Global.dbParam.getLblAllSoc(Global.dbParam.PARAM_FAM3, this.giveFld(cur, this.FIELD_CMC_CATEGORIE)));
				
				materiel.add(cli); 
			}
			if (cur!=null)
				cur.close();
			return materiel;
		}
		catch(Exception ex)
		{
			String err="";
			err=ex.getMessage();
			
		}

		return null;
	}
	//indique si une piece est en attente d'envoi, � cause d'un plantage avant la fin par exemple
		public structComptageMachineClient getPieceNotPrint()
		{
			
			String query = "SELECT * FROM " + TABLENAME + " where "
					+ fld_kd_dat_type + "=" + KD_TYPE + " and ("
					+ this.FIELD_CMC_ISPRINTED+ "=" + "'0' or "+this.FIELD_CMC_ISPRINTED+" is null)";
			
			Cursor cur = db.conn.rawQuery(query, null);
			if(cur.moveToNext()){
				structComptageMachineClient entete =new structComptageMachineClient();
				 get (entete, cur);
				return entete;
			}
			
			return null;
		}
	public boolean loadAll(structComptageMachineClient ent, String codeclient, StringBuffer stBuf) {
		
		String query = "SELECT * FROM " + TABLENAME + " where "
				+ fld_kd_dat_type + "=" + KD_TYPE + " and "
				+ this.FIELD_CMC_CODECLI + "=" + "'" + codeclient + "'  ";	

		Cursor cur = db.conn.rawQuery(query, null);
		if (cur.moveToNext() ) {
			 get(ent,cur);
			
		

		} else
			return false;
		return true;
	}

	
	 
	/**
	 * sauvegarde de la cde
	 * 
	 * @author Marc VOUAUX
	 * @param ent
	 * @param numcde
	 * @param stBuf
	 * @return
	 */
	public boolean save(structComptageMachineClient ent, String codeclient,String numeroSZ,String numeroserie, StringBuffer stBuf) {
		try {
			String query = "SELECT * FROM " + TABLENAME + " where "
					+ fld_kd_dat_type + "=" + KD_TYPE + " and "
					+ this.FIELD_CMC_CODECLI + "=" + "'" + codeclient + "' and "
					+ this.FIELD_CMC_NUMSERIESZ + "=" + "'" + numeroSZ + "'  and "
					+ this.FIELD_CMC_NUMSERIEFABRICANT + "=" + "'" + numeroserie + "'  ";
			

			Cursor cur = db.conn.rawQuery(query, null);
			if (cur.moveToNext() ) {
				
				query = "UPDATE "
						+ TABLENAME
						+ " set "
						+ this.FIELD_CMC_NUMSERIEFABRICANT+ "="+ "'"+ MyDB.controlFld(ent.NUMSERIEFABRICANT)+ "', "
						+ this.FIELD_CMC_CATEGORIE+ "="+ "'"+ MyDB.controlFld(ent.CATEGORIE)+ "' ,"
						+ this.FIELD_CMC_NUMSOUCHE+ "="+ "'"+ MyDB.controlFld(ent.NUMSOUCHE)+ "', "
						+ this.FIELD_CMC_COMMENTAIRE+ "="+ "'"+ MyDB.controlFld(ent.COMMENTAIRE)+ "', "
						+ this.FIELD_CMC_CODEART+ "="+ "'"+ MyDB.controlFld(ent.CODEART)+ "', "
						+ this.FIELD_CMC_LBL_ART+ "="+ "'"+ MyDB.controlFld(ent.LBL_ART)+ "', "
						+ this.FIELD_CMC_DATE+ "="+ "'"+ MyDB.controlFld(ent.DATE)
						+ "' where "
						+ this.FIELD_CMC_CODECLI + "=" + "'" + codeclient + "' and "
						+ this.FIELD_CMC_NUMSERIESZ + "=" + "'" + numeroSZ + "'  and "
						+ this.FIELD_CMC_NUMSERIEFABRICANT + "=" + "'" + numeroserie + "' and "
						+ dbKD.fld_kd_dat_type + "='" + KD_TYPE + "' ";

				db.conn.execSQL(query);
				Global.dbLog.Insert("Comptage Machine client", "Save Update", "", "Requete: "
						+ query, "", "");
			} else {
				query = "INSERT INTO "
						+ TABLENAME
						+ " ("
						+ dbKD.fld_kd_dat_type
						+ ","
						+ this.FIELD_CMC_SOCCODE
						+ ","
						+ this.FIELD_CMC_CODECLI
						+ ","
						+ this.FIELD_CMC_CODEREP
						+ ","
						+ this.FIELD_CMC_CODEART
						+ ","
						+ this.FIELD_CMC_LBL_ART
						+ ","
						+ this.FIELD_CMC_NUMSERIEFABRICANT
						+ ","
						+ this.FIELD_CMC_NUMSERIESZ
						+ ","	
						+ this.FIELD_CMC_CATEGORIE
						+ ","
						+ this.FIELD_CMC_NUMSOUCHE
						+ ","
						+ this.FIELD_CMC_COMMENTAIRE
						+ ","
						
						+ this.FIELD_CMC_DATE
						 + ") VALUES ("
						+ String.valueOf(KD_TYPE) + "," 
						+ "'" + ent.SOCCODE+ "',"
					    + "'" +ent.CODECLI + "',"
						+ "'" + ent.CODEREP + "',"
						+ "'" + ent.CODEART + "',"
						+ "'" +MyDB.controlFld(ent.LBL_ART)+ "',"
						+ "'" +MyDB.controlFld(ent.NUMSERIEFABRICANT)+ "',"
						+ "'" +MyDB.controlFld(ent.NUMSERIESZ)+ "',"
						+ "'" +MyDB.controlFld(ent.CATEGORIE)+ "',"
						+ "'" +MyDB.controlFld(ent.NUMSOUCHE)+ "',"
						+ "'" +MyDB.controlFld(ent.COMMENTAIRE)+ "',"
						
						+ "'" +MyDB.controlFld(ent.DATE)+ "'"
						
						+ ")";

				db.conn.execSQL(query);
				Global.dbLog.Insert("Comptage machine client", "Save Insert", "", "Requete: "
						+ query, "", "");
			}
			
			
		} catch (Exception ex) {
			stBuf.setLength(0);
			stBuf.append(ex.getMessage());
			return false;
		}

		return true;
	}

	/**
	 * efface tout ce qui concerne cette cde
	 * @param numcde
	 * @param codecli
	 * @param err
	 * @return
	 */
	public boolean deleteComptage(String codeclient, String numeroSZ,String numserie, StringBuffer err) {
		try {
			String query = "DELETE from " + TABLENAME + " where "
					+ fld_kd_dat_type + "=" + KD_TYPE + " and "
					+ this.FIELD_CMC_CODECLI + "=" + "'" + codeclient + "' and "
					+ this.FIELD_CMC_NUMSERIESZ + "=" + "'" + numeroSZ + "'  and "
					+ this.FIELD_CMC_NUMSERIEFABRICANT+ "=" + "'" + numserie + "'   ";
			
			
			
			db.conn.execSQL(query);

			Global.dbLog.Insert("Comptage machine", "Delete", "", "Numserie:" + numeroSZ, "",
					"");

			return true;
		} catch (Exception ex) {
			err.append(ex.getMessage());
		}
		return false;
	}

	
	public boolean deleteAll(StringBuffer err) {
		try {
			String query = "DELETE from " + TABLENAME + " where "
					+ dbKD.fld_kd_dat_type + "=" + KD_TYPE;

			db.conn.execSQL(query);
			return true;
		} catch (Exception ex) {
			err.append(ex.getMessage());
		}
		return false;
	}
	public structComptageMachineClient getPieceNotSend(String codecli)
		{
			
			String query = "SELECT * FROM " + TABLENAME + " where "
					+ fld_kd_dat_type + "=" + KD_TYPE + " and "
					+ this.FIELD_CMC_ISPRINTED+ "='1' "+
					" and "+FIELD_CMC_CODECLI+"='"+codecli+"'";
			
			Cursor cur = db.conn.rawQuery(query, null);
			if(cur.moveToNext()){
				structComptageMachineClient entete =new structComptageMachineClient();
				 get (entete, cur);
				return entete;
			}
			
			return null;
		}
	

}
