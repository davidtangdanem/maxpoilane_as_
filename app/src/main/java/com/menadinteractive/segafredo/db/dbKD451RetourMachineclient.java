package com.menadinteractive.segafredo.db;

import java.util.ArrayList;

import android.database.Cursor;

import com.menadinteractive.segafredo.communs.Fonctions;
import com.menadinteractive.segafredo.communs.Global;
import com.menadinteractive.segafredo.db.dbKD83EntCde.structEntCde;

public class dbKD451RetourMachineclient extends dbKD {
	
	public final int KD_TYPE = 451;
	public final static String FIELD_RMC_SOCCODE =  fld_kd_soc_code;
	public final static String FIELD_RMC_NUMDOC = fld_kd_cde_code;
	public final static String FIELD_RMC_CODEART = fld_kd_pro_code;
	public final static String FIELD_RMC_CODEREP = fld_kd_dat_idx01;
	public final static String FIELD_RMC_CODECLI = fld_kd_cli_code;// code cli
	public final static String FIELD_RMC_NUMSZ = fld_kd_dat_data01;
	public final static String FIELD_RMC_COMMENTAIRE = fld_kd_dat_data02;
	public final static String FIELD_RMC_DATE = fld_kd_dat_data03;
	public final static String FIELD_RMC_NUMSERIE = fld_kd_dat_data04;
	public final static String FIELD_RMC_ISPRINTED = fld_kd_dat_data05;//1-0
	public final static String FIELD_RMC_DESIGNATION = fld_kd_dat_data06;
	public final static String FIELD_RMC_SENT = fld_kd_dat_data07;//envoyé au serveur 1-0
	
	MyDB db;

	public dbKD451RetourMachineclient(MyDB _db) {
		super();
		db = _db;
	}

	/**
	 * 
	 * compte le nombre de cde
	 */
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
					+ this.FIELD_RMC_CODECLI + "='" + codecli + "'"+
					" and "+FIELD_RMC_SENT+" is null";

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

	

	 static public class structRetourMarchineClient {
		public structRetourMarchineClient() {
			SOCCODE = "";
			CODEREP = "";
			CODECLI = "";
			NUMSZ = "";
			COMMENTAIRE = "";
			DATE = "";
			NUMSERIE="";
			CODEART="";

		}
		public String NUMDOC;
		public String SOCCODE;
		public String CODEREP;
		public String CODEART;
		public String CODECLI;
		public String NUMSZ;
		public String COMMENTAIRE;
		public String DATE;
		public String NUMSERIE;
		public String DESIGNATION;
		public String ISPRINTED;
	}

 
	public boolean load(structRetourMarchineClient ent, String codeclient,String SZ,String numserie, StringBuffer stBuf) {
		
		String query = "SELECT * FROM " + TABLENAME + " where "
				+ fld_kd_dat_type + "=" + KD_TYPE + " and "
				+ this.FIELD_RMC_CODECLI + "=" + "'" + codeclient + "' and "
				+ this.FIELD_RMC_NUMSZ + "=" + "'" + SZ + "' and "
				+ this.FIELD_RMC_NUMSERIE + "=" + "'" + numserie + "'  ";

		Cursor cur = db.conn.rawQuery(query, null);
		if (cur.moveToNext() ) {
			ent=get(ent,cur);
		} else
			return false;
		return true;
	}
	
	public  ArrayList<structRetourMarchineClient>    load( String codeclient  ) {
		
		String query = "SELECT * FROM " + TABLENAME + " where "
				+ fld_kd_dat_type + "=" + KD_TYPE + " and "
				+ this.FIELD_RMC_CODECLI + "=" + "'" + codeclient + "'  " +
				 " and "+FIELD_RMC_SENT+" is null ";
				

		ArrayList<structRetourMarchineClient> ents=new ArrayList< structRetourMarchineClient>();
		
		Cursor cur = db.conn.rawQuery(query, null);
		while (cur.moveToNext() ) {
			structRetourMarchineClient ent=new structRetourMarchineClient();
			
			ent=get(ent,cur);
			
			ents.add(ent);

		}
		return ents;
	}
	public structRetourMarchineClient get(structRetourMarchineClient ent, Cursor cur){
 		
		ent.NUMDOC = this.giveFld(cur, this.FIELD_RMC_NUMDOC);
		ent.CODEART = this.giveFld(cur, this.FIELD_RMC_CODEART);
		ent.SOCCODE = this.giveFld(cur, this.FIELD_RMC_SOCCODE);
		ent.CODECLI = this.giveFld(cur, this.FIELD_RMC_CODECLI);
		ent.CODEREP = this.giveFld(cur, this.FIELD_RMC_CODEREP);
		ent.NUMSZ = this.giveFld(cur, this.FIELD_RMC_NUMSZ);
		ent.DESIGNATION = this.giveFld(cur, this.FIELD_RMC_DESIGNATION);
		ent.NUMSERIE = this.giveFld(cur, this.FIELD_RMC_NUMSERIE);
		ent.COMMENTAIRE = this.giveFld(cur, this.FIELD_RMC_COMMENTAIRE);
		ent.DATE = this.giveFld(cur, this.FIELD_RMC_DATE);
		ent.ISPRINTED = this.giveFld(cur, this.FIELD_RMC_ISPRINTED);
		
		return ent;
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
	public boolean save(structRetourMarchineClient ent, String codeclient,String SZ,String numserie, StringBuffer stBuf) {
		try {
			String query = "SELECT * FROM " + TABLENAME + " where "
					+ fld_kd_dat_type + "=" + KD_TYPE + " and "
					+ this.FIELD_RMC_CODECLI + "=" + "'" + codeclient + "' and "
					+ this.FIELD_RMC_NUMSZ + "=" + "'" + SZ + "' and  "
					+ this.FIELD_RMC_NUMSERIE + "=" + "'" + numserie + "'   ";
			

			Cursor cur = db.conn.rawQuery(query, null);
			if (cur.moveToNext() ) {
				
				query = "UPDATE "
						+ TABLENAME
						+ " set "
						+ this.FIELD_RMC_COMMENTAIRE
						+ "="
						+ "'"
						+ MyDB.controlFld(ent.COMMENTAIRE)
						+ "',"
						+ this.FIELD_RMC_DATE
						+ "="
						+ "'"
						+ MyDB.controlFld(ent.DATE)
						+ "' where "
						+ this.FIELD_RMC_CODECLI + "=" + "'" + codeclient + "' and "
						+ this.FIELD_RMC_NUMSZ + "=" + "'" + SZ + "' and  "
						+ this.FIELD_RMC_NUMSERIE + "=" + "'" + numserie + "'   "
				
						+ dbKD.fld_kd_dat_type + "='" + KD_TYPE + "' ";

				db.conn.execSQL(query);
				
			} else {
				query = "INSERT INTO "
						+ TABLENAME
						+ " ("
						+ dbKD.fld_kd_dat_type
						+ ","
						+ this.FIELD_RMC_SOCCODE
						+ ","
						+ this.FIELD_RMC_NUMDOC
						+ ","
						+ this.FIELD_RMC_CODECLI
						+ ","
						+ this.FIELD_RMC_CODEREP
						+ ","
						+ this.FIELD_RMC_NUMSZ
						+ ","
						+ this.FIELD_RMC_COMMENTAIRE
						+ ","
						+ this.FIELD_RMC_CODEART
						+ ","
						 + this.FIELD_RMC_DESIGNATION
						+ ","
						+ this.FIELD_RMC_DATE
						+ ","
						+ this.FIELD_RMC_NUMSERIE
						 + ") VALUES ("
						+ String.valueOf(KD_TYPE) + "," 
						+ "'" + ent.SOCCODE+ "',"
						+ "'" + ent.NUMDOC+ "',"
					    + "'" +ent.CODECLI + "',"
						+ "'" + ent.CODEREP + "',"
						+ "'" +MyDB.controlFld(ent.NUMSZ)+ "',"
						+ "'" +MyDB.controlFld(ent.COMMENTAIRE)+ "',"
						+ "'" +MyDB.controlFld(ent.CODEART)+ "',"
						+ "'" +MyDB.controlFld(ent.DESIGNATION)+ "',"
						+ "'" +MyDB.controlFld(ent.DATE)+ "',"
						+ "'" +MyDB.controlFld(ent.NUMSERIE)+ "'"
						+ ")";

				db.conn.execSQL(query);
				
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
	public boolean deleteCde(String codeclient, String SZ,String numserie, StringBuffer err) {
		try {
			String query = "DELETE from " + TABLENAME + " where "
					+ fld_kd_dat_type + "=" + KD_TYPE + " and "
					+ this.FIELD_RMC_CODECLI + "=" + "'" + codeclient + "' and "
					+ this.FIELD_RMC_NUMSZ + "=" + "'" + SZ + "' and "
					+ this.FIELD_RMC_NUMSERIE + "=" + "'" + numserie + "'  ";
			
			
			db.conn.execSQL(query);

			

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
	
	
	public boolean setPrint(String codeclient,String numsouche) {
		try {
			String query = "update  " + TABLENAME 
					+" set "+FIELD_RMC_ISPRINTED+"='1'"
					+ " where "
					+ fld_kd_dat_type + "=" + KD_TYPE + " and "
					+ this.FIELD_RMC_CODECLI + "=" + "'" + codeclient + "' and "
					+ this.FIELD_RMC_NUMDOC+ "=" + "'" + numsouche + "' ";
			
			
			db.conn.execSQL(query);

		
			return true;
		} catch (Exception ex) {
		 
		}
		return false;
	}
	public boolean setSentToServer( ) {
		try {
			String query = "update  " + TABLENAME 
					+" set "+FIELD_RMC_SENT+"='1'"
					+ " where "
					+ fld_kd_dat_type + "=" + KD_TYPE + " and "
					+ this.FIELD_RMC_SENT + " is null " ;
			
			
			db.conn.execSQL(query);

		
			return true;
		} catch (Exception ex) {
		 
		}
		return false;
	}
 		public boolean isPrintOk(String codecli,String numsouche)
		{
			
			String query = "SELECT * FROM " + TABLENAME + " where "
					+ fld_kd_dat_type + "=" + KD_TYPE + " and "
					+ this.FIELD_RMC_CODECLI+ "=" + "'"+codecli+"' and "+
					FIELD_RMC_NUMDOC+"='"+numsouche+"'";
			
			Cursor cur = db.conn.rawQuery(query, null);
			if(cur.moveToNext()){
				structRetourMarchineClient entete =new structRetourMarchineClient();
				 get(entete,cur);
				if (entete.ISPRINTED.equals("1"))
						return true;
				return false;
			}
			
			return true;
		}
 		
 		//indique si une piece est en attente d'envoi, � cause d'un plantage avant la fin par exemple
 		public structRetourMarchineClient getPieceNotPrint()
 		{
 			
 			String query = "SELECT * FROM " + TABLENAME + " where "
 					+ fld_kd_dat_type + "=" + KD_TYPE + " and ("
 					+ this.FIELD_RMC_ISPRINTED+ "=" + "'0' or "+this.FIELD_RMC_ISPRINTED+" is null)";
 			
 			Cursor cur = db.conn.rawQuery(query, null);
 			if(cur.moveToNext()){
 				structRetourMarchineClient entete =new structRetourMarchineClient();
 				 get (entete, cur);
 				return entete;
 			}
 			
 			return null;
 		}
 		public structRetourMarchineClient getPieceNotSend(String codecli)
 		{
 			
 			String query = "SELECT * FROM " + TABLENAME + " where "
 					+ fld_kd_dat_type + "=" + KD_TYPE + " and "
 					+ this.FIELD_RMC_ISPRINTED+ "='1' "+
 					" and "+FIELD_RMC_CODECLI+"='"+codecli+"'"+
 					" and "+FIELD_RMC_SENT+ " is null ";
 			
 			Cursor cur = db.conn.rawQuery(query, null);
 			if(cur.moveToNext()){
 				structRetourMarchineClient entete =new structRetourMarchineClient();
 				 get (entete, cur);
 				return entete;
 			}
 			
 			return null;
 		}
}
