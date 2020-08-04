package com.menadinteractive.segafredo.db;

import java.io.File;
import java.util.ArrayList;

import android.database.Cursor;
import android.database.SQLException;
import android.provider.ContactsContract.CommonDataKinds.Identity;

import com.menadinteractive.segafredo.communs.DateCode;
import com.menadinteractive.segafredo.communs.Global;
import com.menadinteractive.segafredo.encaissement.Encaissement;

public class dbKD981RetourBanqueEnt extends dbKD{

	public final int KD_TYPE = 981;

	//numero de remise en banque
	public final String FIELD_RHBQ_NUMCDE = fld_kd_dat_idx02;	
	//Identifiant
	public final String FIELD_RHBQ_IDENTIFIANT = fld_kd_dat_idx01;
	//Numéro de souche
	public final String FIELD_RHBQ_SOUCHE = fld_kd_cde_code;	
	//code banque
	public final String FIELD_RHBQ_CODEBANQUE = fld_kd_dat_data01;
	//Date
	public final String FIELD_RHBQ_DATE = fld_kd_dat_data02;
	//Montant total
	public final String FIELD_RHBQ_MNT_TOTAL = fld_kd_dat_data03;
	//Montant cheque
	public final String FIELD_RHBQ_MNT_CHEQUE = fld_kd_dat_data04;
	//Montant Espece
	public final String FIELD_RHBQ_MNT_ESPECE = fld_kd_dat_data05;

	//Email
	public final String FIELD_RHBQ_EMAIL = fld_kd_dat_data06;

	//Fax
	public final String FIELD_RHBQ_FAX = fld_kd_dat_data07;


	//Etat ligne envoyé au serveur
	public final String FIELD_RHBQ_ETAT = fld_kd_dat_data11;

	//Imprimé
	public final String FIELD_RHBQ_ISPRINTED = fld_kd_dat_data17;


	MyDB db;

	public dbKD981RetourBanqueEnt(MyDB _db) {
		super();
		db = _db;
	}

	static public class structRetourBanque {
		public structRetourBanque()
		{	
			super();

			NUMCDE ="";
			IDENTIFIANT ="";
			CODEBANQUE ="";
			DATE="";
			MNT_TOTAL ="";
			MNT_CHEQUE="";
			MNT_ESPECE="";
			ETAT="";
			EMAIL="";
			FAX="";
			NUM_SOUCHE="";

		}

		public String NUMCDE  ;
		public String IDENTIFIANT   ;
		public String CODEBANQUE   ; 
		public String DATE        ;
		public String MNT_TOTAL        ;
		public String MNT_CHEQUE        ;  
		public String MNT_ESPECE="";
		public String ETAT="";
		public String EMAIL="";
		public String FAX="";
		public String NUM_SOUCHE="";


	}





	/**
	 * Insertion d'une ligne
	 * @param encaissement
	 * @return
	 */
	public boolean insertRetourBanque(structRetourBanque retourbanque){
		String query = null;

		try{

			query = "INSERT INTO "
					+ TABLENAME
					+ " ("
					+ dbKD.fld_kd_dat_type
					+ ","
					+ this.FIELD_RHBQ_NUMCDE
					+ ","
					+ this.FIELD_RHBQ_IDENTIFIANT
					+ ","
					+ this.FIELD_RHBQ_CODEBANQUE
					+ ","
					+ this.FIELD_RHBQ_DATE
					+ ","
					+ this.FIELD_RHBQ_MNT_TOTAL
					+ ","
					+ this.FIELD_RHBQ_MNT_CHEQUE
					+ ","
					+ this.FIELD_RHBQ_MNT_ESPECE
					+ ","
					+ this.FIELD_RHBQ_ETAT
					+ ","
					+ this.FIELD_RHBQ_EMAIL
					+ ","
					+ this.FIELD_RHBQ_FAX
					+ ","
					+ this.FIELD_RHBQ_ISPRINTED
					+ " ) VALUES ( "
					+ "'"+String.valueOf(KD_TYPE)+ "'"
					+ ","
					+ "'"+retourbanque.NUMCDE+"'"	
					+ ","
					+ "'"+retourbanque.IDENTIFIANT+"'"
					+ ","
					+ "'"+retourbanque.CODEBANQUE+"'"
					+ ","
					+ "'"+retourbanque.DATE+"'"
					+ ","
					+ "'"+retourbanque.MNT_TOTAL+"'"
					+ ","
					+ "'"+retourbanque.MNT_CHEQUE+"'"
					+ ","
					+ "'"+retourbanque.MNT_ESPECE+"'"
					+ ","
					+ "'0'"
					+ ","
					+ "'"+MyDB.controlFld(retourbanque.EMAIL)+"'"
					+ ","
					+ "'"+MyDB.controlFld(retourbanque.FAX)+"'"
					+ ","
					+ "'0'"
					+ ")";

			db.conn.execSQL(query);
			return true;
		}catch(SQLException ex){
			return false;
		}
	}

	public String GetNumRetourBanque(String id)
	{
		//si on a deja un numero de retour banque on le renvoi


		DateCode dt=new DateCode();
		//Code rep(5c) +hhmm+AAMMJJ
		String StValeur="";
		boolean existe=true;		
		int i=0;
		while(existe==true)
		{
			StValeur="RBQ"+id+dt.ToCode();


			existe=false;
			/*
			 * Check dans Kems_data si il existe d�j� 
			 */
			String query="";
			query="select * from "+
					dbKD.TABLENAME+
					" where "+
					dbKD.fld_kd_dat_type+"='"+KD_TYPE+"' AND "+
					dbKD.fld_kd_cde_code+"='"+StValeur+"'";


			Cursor cur=db.conn.rawQuery (query,null);
			if (cur!=null)
			{
				if(cur.moveToNext())
				{
					existe=true;	
				}
				cur.close();
			}
			i++;
		}

		return StValeur;		
	}

	public boolean DeleteRetourBanqueEnt( ){
		try
		{


			String query="DELETE  FROM "+
					TABLENAME+	

					" where "+
					dbKD.fld_kd_dat_type+"="+KD_TYPE;


			db.conn.execSQL(query);
			return true;
		}
		catch(Exception ex)
		{
			Global.lastErrorMessage=ex.getLocalizedMessage();
		}
		return false;
	}

	public boolean deleteRemiseEnBanqueFromNum(String numRemise, ArrayList<String> idEncaissement){
		try
		{
			
			String query="DELETE  FROM "+
					TABLENAME+	

					" where "+
					dbKD.fld_kd_dat_type+"='"+KD_TYPE+"'  and " +
					this.FIELD_RHBQ_NUMCDE+"='"+numRemise+"'";

			db.conn.execSQL(query);
			
			Global.dbKDRetourBanqueLin.deleteLineRemise(numRemise);
			
			//on gère les flag de l'encaissement
			if(idEncaissement != null && idEncaissement.size() > 0){
				for(String id : idEncaissement){
					Global.dbKDEncaissement.updateRemiseEnBanqueOfEncaissement_Cheque(id, Encaissement.REB_NOT_SAVE);
				}
			}
			return true;
		}
		catch(Exception ex)
		{
			Global.lastErrorMessage=ex.getLocalizedMessage();
		}
		return false;
	}

	public structRetourBanque getRemiseBanque(String numRemise){

		structRetourBanque remise = null;

		String query="";
		query="select * from "+
				dbKD.TABLENAME+
				" where "+
				dbKD.fld_kd_dat_type+"='"+KD_TYPE+"' AND "+
				dbKD.fld_kd_dat_idx02+"='"+numRemise+"'";


		Cursor cur=db.conn.rawQuery (query,null);
		if (cur!=null)
		{
			if(cur.moveToNext())
			{
				remise = new structRetourBanque();
				remise.CODEBANQUE = this.giveFld(cur,this.FIELD_RHBQ_CODEBANQUE);
				remise.DATE = this.giveFld(cur,this.FIELD_RHBQ_DATE);
				remise.EMAIL = this.giveFld(cur,this.FIELD_RHBQ_EMAIL);
				remise.ETAT = this.giveFld(cur,this.FIELD_RHBQ_ETAT);
				remise.FAX = this.giveFld(cur,this.FIELD_RHBQ_FAX);
				remise.IDENTIFIANT = this.giveFld(cur,this.FIELD_RHBQ_IDENTIFIANT);
				remise.MNT_CHEQUE = this.giveFld(cur,this.FIELD_RHBQ_MNT_CHEQUE);
				remise.MNT_ESPECE = this.giveFld(cur,this.FIELD_RHBQ_MNT_ESPECE);
				remise.MNT_TOTAL = this.giveFld(cur,this.FIELD_RHBQ_MNT_TOTAL);
				remise.NUM_SOUCHE = this.giveFld(cur,this.FIELD_RHBQ_SOUCHE);
				remise.NUMCDE = this.giveFld(cur,this.FIELD_RHBQ_NUMCDE);
			}
			cur.close();
		}
		return remise;
	}

	public void updateNumSouche(String numSouche, String numRemise){

		String query = "update " + TABLENAME +
				" set "+this.FIELD_RHBQ_SOUCHE+"='"+numSouche+"'"+
				" where "
				+ fld_kd_dat_type + "=" + KD_TYPE + " and "
				+ this.FIELD_RHBQ_NUMCDE + "='" + numRemise + "'";

		db.conn.execSQL(query);

	}

	//Champs pour l'impression
	public boolean setPrint(String numCde,String numsouche) {
		try {
			String query = "update  " + TABLENAME 
					+" set "+FIELD_RHBQ_ISPRINTED+"='1'"
					+ " where "
					+ fld_kd_dat_type + "=" + KD_TYPE + " and "
					+ this.FIELD_RHBQ_NUMCDE + "=" + "'" + numCde + "' and "
					+ this.FIELD_RHBQ_SOUCHE+ "=" + "'" + numsouche + "' ";

			db.conn.execSQL(query);

			Global.dbLog.Insert("Retour machine", "setprint", "", "Numsouche:" + numsouche, "",
					"");

			return true;
		} catch (Exception ex) {

		}
		return false;
	}

	public boolean isPrintOk(String numCde)
	{
		String query = "SELECT count(*) FROM " + TABLENAME + " where "
				+ fld_kd_dat_type + "=" + KD_TYPE + " and "
				+ this.FIELD_RHBQ_NUMCDE+ "=" + "'"+numCde+"' and "
				+ this.FIELD_RHBQ_ISPRINTED+"='1'";

		Cursor cur = db.conn.rawQuery(query, null);
		if(cur.moveToNext()){
			int result = cur.getInt(0);
			if(result > 0) return true;
			return false;
		}

		return true;


	}

	public int countRemiseEnBanqueNonTransmises(){
		int result = 0;

		String query = "select count(*) from "+TABLENAME+" where "
				+ fld_kd_dat_type + "=" + KD_TYPE + " and "
				+ FIELD_RHBQ_ETAT +"='0'";
		
		Cursor cur = db.conn.rawQuery(query, null);
		if(cur != null && cur.moveToNext()){
			result = cur.getInt(0);
			cur.close();
		}
		return result;
	}
	
	structRetourBanque getRemiseEnBanqueEntFromCursor(Cursor cur){
		if(cur == null) return null;
		
		structRetourBanque remise = new structRetourBanque();
		remise.CODEBANQUE = this.giveFld(cur,this.FIELD_RHBQ_CODEBANQUE);
		remise.DATE = this.giveFld(cur,this.FIELD_RHBQ_DATE);
		remise.EMAIL = this.giveFld(cur,this.FIELD_RHBQ_EMAIL);
		remise.ETAT = this.giveFld(cur,this.FIELD_RHBQ_ETAT);
		remise.FAX = this.giveFld(cur,this.FIELD_RHBQ_FAX);
		remise.IDENTIFIANT = this.giveFld(cur,this.FIELD_RHBQ_IDENTIFIANT);
		remise.MNT_CHEQUE = this.giveFld(cur,this.FIELD_RHBQ_MNT_CHEQUE);
		remise.MNT_ESPECE = this.giveFld(cur,this.FIELD_RHBQ_MNT_ESPECE);
		remise.MNT_TOTAL = this.giveFld(cur,this.FIELD_RHBQ_MNT_TOTAL);
		remise.NUM_SOUCHE = this.giveFld(cur,this.FIELD_RHBQ_SOUCHE);
		remise.NUMCDE = this.giveFld(cur,this.FIELD_RHBQ_NUMCDE);
		
		return remise;
	}
	
	public ArrayList<structRetourBanque> getRemiseNotPrint(){
		String query = "SELECT * FROM " + TABLENAME + " where "
				+ fld_kd_dat_type + "=" + KD_TYPE + " and "
				+ this.FIELD_RHBQ_ISPRINTED+ "=" + "'0'";

		ArrayList<structRetourBanque> remises = new ArrayList<structRetourBanque>();
		Cursor cur = db.conn.rawQuery(query, null);
		if(cur != null){
			while(cur.moveToNext()){
				structRetourBanque result = getRemiseEnBanqueEntFromCursor(cur);
				if(result != null) remises.add(result);
			}
			cur.close();
		}

		return remises;
	}

}
