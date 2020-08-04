package com.menadinteractive.segafredo.db;

import java.util.ArrayList;

import android.database.Cursor;
import android.database.SQLException;

import com.menadinteractive.segafredo.communs.DateCode;
import com.menadinteractive.segafredo.communs.Global;
import com.menadinteractive.segafredo.db.dbKD981RetourBanqueEnt.structRetourBanque;


public class dbKD982RetourBanqueLin extends dbKD{

	public final int KD_TYPE = 982;
	
	//Identifiant
	public final String FIELD_RLBQ_IDENTIFIANT = fld_kd_dat_idx01;	
	//numero de commande
	public final String FIELD_RLBQ_NUMCDE = fld_kd_dat_idx02;
	//code banque
	public final String FIELD_RLBQ_NOCHEQUE = fld_kd_dat_data01;
	
	//code banque
	public final String FIELD_RLBQ_NUMCHEQUE = fld_kd_dat_data02;
	//Montant
	public final String FIELD_RLBQ_MONTANT = fld_kd_dat_data03;
	//Banque
	public final String FIELD_RLBQ_BANQUE = fld_kd_dat_data04;
	//Banque
	public final String FIELD_RLBQ_DATE = fld_kd_dat_data05;
		
			
	//Banque
	public final String FIELD_RLBQ_SOUCHEENCAISSEMENT = fld_kd_cde_code;//le nuéro de souche lors de l'encaissement
			
		
		
	//Etat ligne envoyé au serveur
	public final String FIELD_RLBQ_ETAT = fld_kd_dat_data11;

	MyDB db;

	public dbKD982RetourBanqueLin(MyDB _db) {
		super();
		db = _db;
	}
	
	static public class structRetourBanqueLin {
		public structRetourBanqueLin()
		{	
			super();

			NUMCDE ="";
			IDENTIFIANT="";
			NOCHEQUE ="";
			ETAT="";
			NUMCHEQUE="";
			MONTANT="";
			BANQUE="";
			DATE="";
			
			
		
		      
		}
		
		public String NUMCDE  ;
		public String IDENTIFIANT   ;
		public String NOCHEQUE   ; 
		public String ETAT;
		public String NUMCHEQUE;
		public String MONTANT;
		public String BANQUE;
		public String DATE;
		public String SOUCHEENCAISSEMENT;
		
		
	}
	

	

	
	/**
	 * Insertion d'une ligne
	 * @param encaissement
	 * @return
	 */
	public boolean insertRetourBanqueLin(structRetourBanqueLin retourbanque){
		String query = null;

		try{
			
			query = "INSERT INTO "
					+ TABLENAME
					+ " ("
					+ dbKD.fld_kd_dat_type
					+ ","
					+ this.FIELD_RLBQ_NUMCDE
					+ ","
					+ this.FIELD_RLBQ_IDENTIFIANT
					+ ","
					+ this.FIELD_RLBQ_NOCHEQUE
					+ ","
					+ this.FIELD_RLBQ_ETAT
					+ ","
					+ this.FIELD_RLBQ_NUMCHEQUE
					+ ","
					+ this.FIELD_RLBQ_MONTANT
					+ ","
					+ this.FIELD_RLBQ_BANQUE
					+ ","
					+ this.FIELD_RLBQ_SOUCHEENCAISSEMENT
					+ ","
					+ this.FIELD_RLBQ_DATE
					
					+ " ) VALUES ( "
					+ "'"+String.valueOf(KD_TYPE)+ "'"
					+ ","
					+ "'"+retourbanque.NUMCDE+"'"	
					+ ","
					+ "'"+retourbanque.IDENTIFIANT+"'"
					+ ","
					+ "'"+retourbanque.NOCHEQUE+"'"
					+ ","
					+ "'0'"
					+ ","
					+ "'"+retourbanque.NUMCHEQUE+"'"
					+ ","
					+ "'"+retourbanque.MONTANT+"'"
					+ ","
					+ "'"+MyDB.controlFld(retourbanque.BANQUE)+"'"
					+ ","
					+ "'"+MyDB.controlFld(retourbanque.SOUCHEENCAISSEMENT)+"'"
					+ ","
					+ "'"+MyDB.controlFld(retourbanque.DATE)+"'"
					
					+ ")";

			db.conn.execSQL(query);
			return true;
		}catch(SQLException ex){
			return false;
		}
	}
	public boolean DeleteRetourBanqueLin( ){
		try
		{
			
			
			String query="DELETE  FROM "+
					TABLENAME+	
						
					 " where "+
					 dbKD.fld_kd_dat_type+"='"+KD_TYPE+"'  ";
					

			db.conn.execSQL(query);
			return true;
		}
		catch(Exception ex)
		{
			Global.lastErrorMessage=ex.getLocalizedMessage();
		}
		return false;
	}
	
	public boolean deleteLineRemise(String numRemise){
		try
		{
			
			
			String query="DELETE  FROM "+
					TABLENAME+	
						
					 " where "+
					 dbKD.fld_kd_dat_type+"='"+KD_TYPE+"'  and " +
					this.FIELD_RLBQ_NUMCDE+"='"+numRemise+"'";
					

			db.conn.execSQL(query);
			return true;
		}
		catch(Exception ex)
		{
			Global.lastErrorMessage=ex.getLocalizedMessage();
		}
		return false;
	}
	
	public ArrayList<structRetourBanqueLin> getLineRemiseEnBanque(String numCde){

		ArrayList<structRetourBanqueLin> lines = new ArrayList<dbKD982RetourBanqueLin.structRetourBanqueLin>();

			String query="";
			query="select * from "+
					dbKD.TABLENAME+
					" where "+
					dbKD.fld_kd_dat_type+"='"+KD_TYPE+"' AND "+
					FIELD_RLBQ_NUMCDE+"='"+numCde+"'";

			Cursor cur=db.conn.rawQuery (query,null);
			if (cur!=null)
			{
				structRetourBanqueLin line;
				while (cur.moveToNext())
				{
					line = new structRetourBanqueLin();
					line.BANQUE = this.giveFld(cur,this.FIELD_RLBQ_BANQUE);
					line.DATE = this.giveFld(cur,this.FIELD_RLBQ_DATE);
					line.ETAT = this.giveFld(cur,this.FIELD_RLBQ_ETAT);
					line.IDENTIFIANT = this.giveFld(cur,this.FIELD_RLBQ_IDENTIFIANT);
					line.MONTANT = this.giveFld(cur,this.FIELD_RLBQ_MONTANT);
					line.NOCHEQUE = this.giveFld(cur,this.FIELD_RLBQ_NOCHEQUE);
					line.NUMCDE = this.giveFld(cur,this.FIELD_RLBQ_NUMCDE);
					line.NUMCHEQUE = this.giveFld(cur,this.FIELD_RLBQ_NUMCHEQUE);
					line.SOUCHEENCAISSEMENT = this.giveFld(cur,this.FIELD_RLBQ_SOUCHEENCAISSEMENT);
					lines.add(line);
				}
				cur.close();
			}
			
			return lines;
	}
	
}
