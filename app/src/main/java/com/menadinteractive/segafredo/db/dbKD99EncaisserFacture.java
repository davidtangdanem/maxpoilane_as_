package com.menadinteractive.segafredo.db;

import java.util.ArrayList;

import android.database.Cursor;
import android.database.SQLException;

import com.menadinteractive.segafredo.communs.Fonctions;
import com.menadinteractive.segafredo.communs.Global;
import com.menadinteractive.segafredo.encaissement.Encaissement;
import com.menadinteractive.segafredo.encaissement.Facture;

public class dbKD99EncaisserFacture extends dbKD{

	public final int KD_TYPE = 99;

	//Identifiant
	public final String FIELD_ENCFAC_IDENTIFIANT = fld_kd_dat_data18;	
	//Numéro de facture
	public final String FIELD_ENCFAC_NUMERO_FACTURE = fld_kd_dat_data01;
	//Numéro d'encaissement
	public final String FIELD_ENCFAC_NUMERO_ENCAISSEMENT = fld_kd_dat_data02;
	public final String FIELD_ENCFAC_COMMENTAIRE= fld_kd_dat_data04;
	//Type document
	public final String FIELD_ENCFAC_TYPEDOC = fld_kd_dat_data07;
	//Etat ligne envoyé au serveur
	public final String FIELD_ENCFAC_ETAT = fld_kd_dat_data11;
	//Enregistrement de la souche
	public final String FIELD_ENCFAC_NUMERO_SOUCHE = fld_kd_dat_data12;
	//Validation de l'encaissement
	public final String FIELD_ENCFAC_VALIDE = fld_kd_dat_data13;

	//Etat Remise en banque
	public final String FIELD_ENCFAC_ETAT_RBANQUE = fld_kd_dat_data14;


	MyDB db;

	public dbKD99EncaisserFacture(MyDB _db) {
		super();
		db = _db;
	}

	/**
	 * Suppression d'une ligne par son identifiant
	 * @param identifiant
	 * @return
	 */
	public boolean deleteLineFromEncaissement(String identifiant){

		if(identifiant.equals("")) return false;

		try {
			String query = "DELETE from " + TABLENAME + " where "+
					fld_kd_dat_type+"='"+KD_TYPE+"' and "

					+ this.FIELD_ENCFAC_IDENTIFIANT + "='" + identifiant + "'";

			db.conn.execSQL(query);
			
			Global.dbLog.Insert("Encaissement type 99", "deleteLineFromEncaissement", "", "Requete: "
					+ query, "", "");

			//			Global.dbLog.Insert("Encaissement", "Delete", "", "Identifiant:" + identifiant, "",
			//					"");

			return true;
		} catch (SQLException ex) {

		}		
		return false;
	}

	/**
	 * Suppression ds lignes du code identifiant encaissement
	 * @param identifiant
	 * @return
	 */
	public boolean deleteLineFromNumeroEncaissement(String identifiantEncaissement){

		if(identifiantEncaissement.equals("")) return false;

		try {
			String query = "DELETE from " + TABLENAME + " where "
					+	dbKD.fld_kd_dat_type+"='"+KD_TYPE+"' and "

					+ this.FIELD_ENCFAC_NUMERO_ENCAISSEMENT + "='" + identifiantEncaissement + "'";

			db.conn.execSQL(query);
			
			Global.dbLog.Insert("Encaissement type 99", "deleteLineFromNumeroEncaissement", "", "Requete: "
					+ query, "", "");

			//			Global.dbLog.Insert("Encaissement", "Delete", "", "Identifiant:" + identifiantEncaissement, "",
			//					"");

			return true;
		} catch (SQLException ex) {

		}		
		return false;
	}

	/**
	 * Suppression ds lignes du code identifiant facture
	 * @param identifiant
	 * @return
	 */
	public boolean deleteLineFromNumeroFacture(String identifiantFacture){

		if(identifiantFacture.equals("")) return false;

		try {
			String query = "DELETE from " + TABLENAME + " where "+
					dbKD.fld_kd_dat_type+"='"+KD_TYPE+"' and "

					+ this.FIELD_ENCFAC_NUMERO_FACTURE + "='" + identifiantFacture + "'";

			db.conn.execSQL(query);

			//			Global.dbLog.Insert("Facture", "Delete", "", "Identifiant:" + identifiantFacture, "",
			//					"");

			return true;
		} catch (SQLException ex) {

		}		
		return false;
	}

	/**
	 * Récupère la liste des numéros de facture pour l'encaissement passé en paramètre
	 * @param encaissement
	 * @return ArrayList<String> numéros facture
	 */
	public ArrayList<String> getNumerosFactureFromEncaissement(Encaissement encaissement){
		if (encaissement == null)
			return null;

		String query = "SELECT * FROM " + TABLENAME + " where "
				+ fld_kd_dat_type + "=" + KD_TYPE + " and "
				+ this.FIELD_ENCFAC_NUMERO_ENCAISSEMENT + "=" + "'" + encaissement.getIdentifiant() + "'";

		ArrayList<String> array = new ArrayList<String>();

		Cursor cur = db.conn.rawQuery(query, null);
		while (cur.moveToNext()) {
			array.add(this.giveFld(cur, this.FIELD_ENCFAC_NUMERO_FACTURE));
		}
		if (cur!=null)
			cur.close();//mv
		return array;
	}

	/**
	 * Récupération du numéro souche à partir de l'identifiant
	 * @param identifiantEncaissement
	 * @return
	 */
	public String getNumeroSoucheFromIdentifiantEncaissement(String identifiantEncaissement){
		if(identifiantEncaissement == null) return null;

		String result = null;

		String query = "SELECT distinct("+this.FIELD_ENCFAC_NUMERO_SOUCHE+") FROM " + TABLENAME + " where "
				+ fld_kd_dat_type + "=" + KD_TYPE + " and "
				+ this.FIELD_ENCFAC_IDENTIFIANT + "=" + "'" + identifiantEncaissement + "'";

		Cursor cur = db.conn.rawQuery(query, null);
		if (cur.moveToNext()) {
			result = cur.getString(0);
		}
		if (cur!=null)
			cur.close();//mv
		return result;
	}

	/**
	 * Récupère la liste des numéros d'encaissement pour la facture passée en paramètre
	 * @param facture
	 * @return ArrayList<String> numéros encaissement
	 */
	public ArrayList<String> getNumerosEncaissementFromFacture(Facture facture){
		if (facture == null)
			return null;

		String query = "SELECT * FROM " + TABLENAME + " where "
				+ fld_kd_dat_type + "=" + KD_TYPE + " and "
				+ this.FIELD_ENCFAC_NUMERO_FACTURE + "=" + "'" + facture.getNumDoc() + "'";

		ArrayList<String> array = new ArrayList<String>();

		Cursor cur = db.conn.rawQuery(query, null);
		while (cur.moveToNext()) {
			array.add(this.giveFld(cur, this.FIELD_ENCFAC_NUMERO_ENCAISSEMENT));
		}
		if (cur!=null)
			cur.close();//mv
		return array;
	}

	/**
	 * Récupère la liste des numéros d'encaissement à partir du numéro de facture passée en paramètre
	 * @param facture
	 * @return ArrayList<String> numéros encaissement
	 */
	public ArrayList<String> getNumerosEncaissementFromNumeroFacture(String numDoc){
		if (numDoc == null)
			return null;

		String query = "SELECT * FROM " + TABLENAME + " where "
				+ fld_kd_dat_type + "=" + KD_TYPE + " and "
				+ this.FIELD_ENCFAC_NUMERO_FACTURE + "=" + "'" + numDoc + "'";

		ArrayList<String> array = new ArrayList<String>();

		Cursor cur = db.conn.rawQuery(query, null);
		while (cur.moveToNext()) {
			array.add(this.giveFld(cur, this.FIELD_ENCFAC_NUMERO_ENCAISSEMENT));
		} 
		if (cur!=null)
			cur.close();//mv
		return array;
	}


	/**
	 * Récupère le numéro identifiant grâce au numéro de facture et au numéro d'encaissement
	 * @param numeroFacture
	 * @param numeroEncaissement
	 * @return identifiant
	 */
	public String getIdentifiantFromNumeroFactureEncaissement(String numeroFacture, String numeroEncaissement){
		if (numeroFacture.equals("") && numeroEncaissement.equals(""))
			return null;

		String query = "SELECT * FROM " + TABLENAME + " where "
				+ fld_kd_dat_type + "=" + KD_TYPE + " and "
				+ this.FIELD_ENCFAC_NUMERO_ENCAISSEMENT + "=" + "'" + numeroEncaissement + "'" 
				+ " and " + this.FIELD_ENCFAC_NUMERO_FACTURE + "="+"'"+ numeroFacture+"'";

		Cursor cur = db.conn.rawQuery(query, null);
		if (cur.moveToNext()) {
			String v= this.giveFld(cur, this.FIELD_ENCFAC_IDENTIFIANT);
			if (cur!=null)
				cur.close();//mv
			return v;
		} else
		{
			if (cur!=null)
				cur.close();//mv

			return null;
		}
	}


	/**
	 * Insertion d'une ligne
	 * @param encaissement
	 * @return
	 */
	public boolean insertEncaissement(String identifiant, String numeroFacture, String numeroEncaissement, String numeroSouche){
		String query = null;

		try{
			query = "SELECT * FROM " + TABLENAME + " where "
					+ fld_kd_dat_type + "=" + KD_TYPE + " and "
					+ this.FIELD_ENCFAC_IDENTIFIANT + "=" + "'" + identifiant+ "' AND "
					+ this.FIELD_ENCFAC_NUMERO_FACTURE + "=" + "'" + numeroFacture + "' AND "
					+ this.FIELD_ENCFAC_NUMERO_ENCAISSEMENT + "=" + "'" + numeroEncaissement + "' AND "
					+ this.FIELD_ENCFAC_NUMERO_SOUCHE + "=" + "'" + numeroSouche + "'  ";

			Cursor cur = db.conn.rawQuery(query, null);
			if (cur.moveToNext() ) {

			}
			else
			{
				query = "INSERT INTO "
						+ TABLENAME
						+ " ("
						+ dbKD.fld_kd_dat_type
						+ ","
						+ this.FIELD_ENCFAC_IDENTIFIANT
						+ ","
						+ this.FIELD_ENCFAC_NUMERO_FACTURE
						+ ","
						+ this.FIELD_ENCFAC_NUMERO_ENCAISSEMENT
						+ ","
						+ this.FIELD_ENCFAC_TYPEDOC
						+ ","
						+ this.FIELD_ENCFAC_ETAT
						+ ","
						+ this.FIELD_ENCFAC_NUMERO_SOUCHE
						+ ","
						+ this.FIELD_ENCFAC_VALIDE
						+ ","
						+ this.FIELD_ENCFAC_ETAT_RBANQUE

						+ " ) VALUES ( "
						+ "'"+String.valueOf(KD_TYPE) +"'"
						+ ","
						+ "'"+identifiant+"'"
						+ ","
						+ "'"+numeroFacture+"'"
						+ ","
						+ "'"+numeroEncaissement+"'"
						+ ","
						+ "'"+Encaissement.TYPE_DOCUMENT+"'"
						+ ","
						+ "'0'"
						+ ","
						+ "'"+numeroSouche+"'"
						+ ","
						+ "'0'"
						+ ","
						+ "'0'"
						+ ")";

				db.conn.execSQL(query);	
				
				Global.dbLog.Insert("Encaissement type 99", "insertEncaissement", "", "Requete: "
						+ query, "", "");
			}

			return true;
		}catch(SQLException ex){
			return false;
		}
	}

	/**
	 * Retourne l'identifiant maximum
	 * @return
	 */
	public String selectMaxIdentifiant(){
		String query = "SELECT max(CAST("+this.FIELD_ENCFAC_IDENTIFIANT+" as Int)) FROM " + TABLENAME + " where "+this.fld_kd_dat_type+"='"+KD_TYPE+"'";

		Cursor cur = db.conn.rawQuery(query, null);
		if (cur.moveToNext()) {
			String v= cur.getString(0);
			if (cur!=null)
				cur.close();//mv
			return v;
		}
		if (cur!=null)
			cur.close();//mv
		return null;
	}

	/**
	 * Récupère la liste des numéros de facture pour l'identifiant d'encaissement passé en paramètre
	 * @param String identifiant
	 * @return ArrayList<String> numéros facture
	 */
	public ArrayList<String> getNumerosFactureFromId(String identifiant) {
		if (identifiant == null) return null;
		if(identifiant.equals("")) return null;

		String query = "SELECT * FROM " + TABLENAME + " where "
				+ fld_kd_dat_type + "=" + KD_TYPE + " and "
				+ this.FIELD_ENCFAC_NUMERO_ENCAISSEMENT + "=" + "'" + identifiant + "'";

		ArrayList<String> array = new ArrayList<String>();

		Cursor cur = db.conn.rawQuery(query, null);
		while (cur.moveToNext()) {
			array.add(this.giveFld(cur, this.FIELD_ENCFAC_NUMERO_FACTURE));
		}
		if (cur!=null)
			cur.close();//mv
		return array;
	}

	/**
	 * Récupère l'état de la validation pour l'encaissement
	 * @param String identifiant
	 * @return ArrayList<String> numéros facture
	 */
	public String getValidationEncaissementFromNumero(String numeroEnc) {
		if (numeroEnc == null) return null;
		if(numeroEnc.equals("")) return null;

		String validation = "";

		String query = "SELECT "+this.FIELD_ENCFAC_VALIDE+" FROM " + TABLENAME + " where "
				+ fld_kd_dat_type + "=" + KD_TYPE + " and "
				+ this.FIELD_ENCFAC_NUMERO_ENCAISSEMENT + "=" + "'" + numeroEnc + "'";

		Cursor cur = db.conn.rawQuery(query, null);
		if (cur.moveToNext()) {
			validation = Fonctions.GetStringDanem(cur.getString(0));
		}
		if (cur!=null)
			cur.close();//mv
		return validation;
	}

	public ArrayList<String> getNumerosEncaissementsFromNumerosFacture(
			ArrayList<String> numerosFacture) {

		if (numerosFacture == null) return null;

		if(numerosFacture.size() <= 0) return null;

		String query = "SELECT * FROM " + TABLENAME + " where "
				+ fld_kd_dat_type + "=" + KD_TYPE + " and";

		int cpt = 0;

		String or = " (";
		for(String num : numerosFacture){
			or += " "+this.FIELD_ENCFAC_NUMERO_FACTURE+"='"+num+"'";
			if(numerosFacture.size() - 1 > cpt) or += " or";
			cpt++;
		}
		or+=")";

		query+=or;

		ArrayList<String> array = new ArrayList<String>();

		Cursor cur = db.conn.rawQuery(query, null);
		while (cur.moveToNext()) {
			String value = this.giveFld(cur, this.FIELD_ENCFAC_NUMERO_ENCAISSEMENT);
			if(!array.contains(value))array.add(value);
		} 
		if (cur!=null)
			cur.close();//mv
		return array;
	}

	/**
	 * Mise à jour du champ de validation de l'encaissement
	 * @param signataire
	 * @param filename
	 * @return
	 */
	public boolean updateValidationEncaissement(String numeroSouche, String value,String commentaire){

		if(numeroSouche == null || value == null) return false;
		if(!value.equals("1") && !value.equals("2")) return false;

		try
		{
			String query="update  "+
					TABLENAME+	
					" set "
					+ FIELD_ENCFAC_VALIDE
					+ "='"+value+"',"
					+ FIELD_ENCFAC_COMMENTAIRE
					+ "='"+MyDB.controlFld(commentaire)+"'"

					+ " where "+
					dbKD.fld_kd_dat_type+"="+KD_TYPE+" and "+

					FIELD_ENCFAC_NUMERO_SOUCHE
					+"='"+numeroSouche+"'";

			db.conn.execSQL(query);
			
			Global.dbLog.Insert("Encaissement type 99", "updateValidationEncaissement", "", "Requete: "
					+ query, "", "");
			return true;
		}
		catch(Exception ex)
		{
			Global.lastErrorMessage=ex.getLocalizedMessage();
		}
		return false;
	}
	public boolean updateEtatOfEncaissementFacture( ){
		try
		{


			String query="update  "+
					TABLENAME+	
					" set "
					+ FIELD_ENCFAC_ETAT
					+ "='1' "		
					+ " where "+
					dbKD.fld_kd_dat_type+"='"+KD_TYPE+"' and "+


					FIELD_ENCFAC_ETAT
					+"='0'  ";


			db.conn.execSQL(query);
			
			Global.dbLog.Insert("Encaissement type 99", "updateEtatOfEncaissementFacture", "", "Requete: "
					+ query, "", "");
			return true;
		}
		catch(Exception ex)
		{
			Global.lastErrorMessage=ex.getLocalizedMessage();
		}
		return false;
	}

	public boolean updateNumeroSoucheEncaissement(String numSouche, String identifiant){
		String query = "";
		try{

			query="update  "+
					TABLENAME+	
					" set "
					+ FIELD_ENCFAC_NUMERO_SOUCHE
					+ "='"+numSouche+"' "		
					+ " where "+
					dbKD.fld_kd_dat_type+"='"+KD_TYPE+"' and "+			
					FIELD_ENCFAC_NUMERO_ENCAISSEMENT
					+"='"+identifiant+"'  ";


			Global.dbLog.Insert("Encaissement type 99", "updateNumeroSoucheEncaissement", "Souche :"+numSouche+" / Identifiant"+identifiant, "Requete: "
					+ query, "", "");
			db.conn.execSQL(query);
			return true;
		}
		catch(Exception ex)
		{
			Global.lastErrorMessage=ex.getLocalizedMessage();
			Global.dbLog.Insert("Encaissement type 99", "updateNumeroSoucheEncaissement", "Souche :"+numSouche+" / Identifiant"+identifiant, "Requete: "
					+ query, "Exception : "+ex.getLocalizedMessage(), "");
		}
		return false;
	}
	public boolean updateNumeroSoucheEncaissementEx(String numSouche, ArrayList<String> factures){
		String query = "";
		String facture = "";
		try
		{

			for(int i=0;i<factures.size();i++){
				query="update  "+
						TABLENAME+	
						" set "
						+ FIELD_ENCFAC_NUMERO_SOUCHE
						+ "='"+numSouche+"' "		
						+ " where "+
						dbKD.fld_kd_dat_type+"='"+KD_TYPE+"' and "+			
						FIELD_ENCFAC_NUMERO_FACTURE
						+"='"+factures.get(i)+"'";

				facture = factures.get(i);
				db.conn.execSQL(query);
				
				Global.dbLog.Insert("Encaissement type 99", "updateNumeroSoucheEncaissementEx", "", "Requete: "
						+ query, "", "");
			}

			return true;
		}
		catch(Exception ex)
		{
			Global.lastErrorMessage=ex.getLocalizedMessage();
			Global.dbLog.Insert("Encaissement type 99", "updateNumeroSoucheEncaissement", "Souche :"+numSouche+" / Numero facture"+facture, "Requete: "
					+ query, "Exception : "+ex.getLocalizedMessage(), "");
		}
		return false;
	}

	public boolean DeleteOfEncaissementFacture( ){
		try
		{


			String query="DELETE  FROM "+
					TABLENAME+	

					" where "+
					dbKD.fld_kd_dat_type+"='"+KD_TYPE+"' and "+

					FIELD_ENCFAC_ETAT_RBANQUE
					+"='1'   ";


			db.conn.execSQL(query);
			return true;
		}
		catch(Exception ex)
		{
			Global.lastErrorMessage=ex.getLocalizedMessage();
		}
		return false;
	}

	public boolean DeleteAll( ){
		try
		{


			String query="DELETE  FROM "+
					TABLENAME+	

					" where "+
					dbKD.fld_kd_dat_type+"='"+KD_TYPE+"' ";


			db.conn.execSQL(query);
			return true;
		}
		catch(Exception ex)
		{
			Global.lastErrorMessage=ex.getLocalizedMessage();
		}
		return false;
	}

}
