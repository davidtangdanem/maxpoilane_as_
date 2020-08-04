package com.menadinteractive.segafredo.db;

import java.util.ArrayList;

import android.database.Cursor;
import android.database.SQLException;

import com.menadinteractive.segafredo.communs.Fonctions;
import com.menadinteractive.segafredo.communs.Global;
import com.menadinteractive.segafredo.encaissement.Encaissement;

public class dbKD98Encaissement extends dbKD{

	public final int KD_TYPE = 98;

	//Code client
	public final String FIELD_ENC_CODE_CLIENT = fld_kd_cli_code;
	//Identifiant
	public final String FIELD_ENC_IDENTIFIANT = fld_kd_dat_data19;
	//Code agent
	public final String FIELD_ENC_CODE_AGENT = fld_kd_dat_idx02;
	//Montant
	public final String FIELD_ENC_MONTANT = fld_kd_dat_data01;
	//Date
	public final String FIELD_ENC_DATE = fld_kd_dat_data02;
	//Type paiement
	public final String FIELD_ENC_TYPE_PAIEMENT = fld_kd_dat_data03;
	//Type document
	public final String FIELD_ENC_TYPEDOC = fld_kd_dat_data07;

	//Selon le type du paiement gérer les informations de paiement	
	//Si chèque
	//Banque 
	public final String FIELD_ENC_BANQUE = fld_kd_dat_data04;
	//Agence
	public final String FIELD_ENC_AGENCE = fld_kd_dat_data05;
	//Numéro de compte
	public final String FIELD_ENC_NUMERO_COMPTE = fld_kd_dat_data06;
	//Chèque
	public final String FIELD_ENC_CHEQUE = fld_kd_dat_data08;
	//Nom Client Signature
	public final String FIELD_ENC_SIGNATAIRE = fld_kd_dat_data09;
	//Filename Signature
	public final String FIELD_ENC_FILENAME_SIGNATURE = fld_kd_dat_data10;
	//Etat ligne envoyé au serveur
	public final String FIELD_ENC_ETAT = fld_kd_dat_data11;

	//Etat Remise en banque
	public final String FIELD_ENC_ETAT_RBANQUE = fld_kd_dat_data14;
	//Code banque
	public final String FIELD_ENC_CODE_BANQUE = fld_kd_dat_data15;
	//Date Chèque
	public final String FIELD_ENC_DATE_CHEQUE = fld_kd_dat_data16;
	//Date Chèque
	public final String FIELD_ENC_ISPRINTED = fld_kd_dat_data17;
	//Numéro de souche
	public final String FIELD_ENC_NUMSOUCHE = fld_kd_dat_data18;


	MyDB db;

	public dbKD98Encaissement(MyDB _db) {
		super();
		db = _db;
	}

	/**
	 * Suppression d'une ligne par son identifiant
	 * @param identifiant
	 * @return
	 */
	public boolean deleteEncaissement(String identifiant){

		if(identifiant.equals("")) return false;
		String query = "";
		try {
			query = "DELETE from " + TABLENAME + " where "+
					fld_kd_dat_type+"='"+KD_TYPE+"' and "

					+ this.FIELD_ENC_IDENTIFIANT + "='" + identifiant + "'";

			db.conn.execSQL(query);
			
			Global.dbLog.Insert("Encaissement type 98", "deleteEncaissement", "", "Requete: "
					+ query, "", "");

			//			Global.dbLog.Insert("Encaissement", "Delete", "", "Identifiant:" + identifiant, "",
			//					"");

			return true;
		} catch (SQLException ex) {
			Global.dbLog.Insert("Encaissement type 98", "deleteEncaissement", "", "Requete: "
					+ query, "Exception : "+ex.getLocalizedMessage(), "");
		}		
		return false;
	}


	/**
	 * Récupération d'une ligne par son identifiant
	 * @param identifiant
	 * @return
	 */
	public Encaissement selectEncaissement(String identifiant, String codeClient, String validation){
		if (identifiant.equals(""))
			return null;

		String query = "SELECT * FROM " + TABLENAME + " where "
				+ fld_kd_dat_type + "=" + KD_TYPE + " and "
				+ this.FIELD_ENC_IDENTIFIANT + "=" + "'" + identifiant + "' and "
				+ this.FIELD_ENC_CODE_CLIENT + "=" + "'" + codeClient + "'";


		Cursor cur = db.conn.rawQuery(query, null);
		if (cur.moveToNext() && !identifiant.equals("")) {
			Encaissement encaissement = new Encaissement();
			encaissement.setIdentifiant(this.giveFld(cur, this.FIELD_ENC_IDENTIFIANT));
			encaissement.setAgence(this.giveFld(cur, this.FIELD_ENC_AGENCE));
			encaissement.setBanque(this.giveFld(cur, this.FIELD_ENC_BANQUE));
			encaissement.setCheque(this.giveFld(cur, this.FIELD_ENC_CHEQUE));
			encaissement.setDate(this.giveFld(cur, this.FIELD_ENC_DATE));
			encaissement.setMontant(Fonctions.convertToFloat(this.giveFld(cur, this.FIELD_ENC_MONTANT)));
			encaissement.setNumeroCompte(this.giveFld(cur, this.FIELD_ENC_NUMERO_COMPTE));
			encaissement.setTypePaiement(this.giveFld(cur, this.FIELD_ENC_TYPE_PAIEMENT));
			encaissement.setCodeClient(this.giveFld(cur, this.FIELD_ENC_CODE_CLIENT));
			encaissement.setSignataire(this.giveFld(cur, this.FIELD_ENC_SIGNATAIRE));
			encaissement.setFilenameSignature(this.giveFld(cur, this.FIELD_ENC_FILENAME_SIGNATURE));
			encaissement.setCodeBanque(this.giveFld(cur, this.FIELD_ENC_CODE_BANQUE));
			encaissement.setDateCheque(this.giveFld(cur, this.FIELD_ENC_DATE_CHEQUE));
			encaissement.setNumeroSouche(this.giveFld(cur, this.FIELD_ENC_NUMSOUCHE));


			String numeroSouche = Global.dbKDEncaisserFacture.getNumeroSoucheFromIdentifiantEncaissement(encaissement.getIdentifiant());
			if(numeroSouche != null) encaissement.setNumeroSouche(numeroSouche);

			cur.close();//mv

			if(validation != null){
				if(Global.dbKDEncaisserFacture.getValidationEncaissementFromNumero(
						encaissement.getIdentifiant()).equals(validation)) return encaissement;
				else return null;
			}else return encaissement;
		} else
		{
			if (cur!=null)
				cur.close();
			return null;
		}
	}

	/**
	 * Récupération d'une ligne par son identifiant
	 * @param identifiant
	 * @return
	 */
	public Encaissement selectEncaissement(String identifiant, String validation){
		if (identifiant.equals(""))
			return null;

		String query = "SELECT * FROM " + TABLENAME + " where "
				+ fld_kd_dat_type + "=" + KD_TYPE + " and "
				+ this.FIELD_ENC_IDENTIFIANT + "=" + "'" + identifiant + "'";

		Cursor cur = db.conn.rawQuery(query, null);
		if (cur.moveToNext() && !identifiant.equals("")) {
			Encaissement encaissement = new Encaissement();
			encaissement.setIdentifiant(this.giveFld(cur, this.FIELD_ENC_IDENTIFIANT));
			encaissement.setAgence(this.giveFld(cur, this.FIELD_ENC_AGENCE));
			encaissement.setBanque(this.giveFld(cur, this.FIELD_ENC_BANQUE));
			encaissement.setCheque(this.giveFld(cur, this.FIELD_ENC_CHEQUE));
			encaissement.setDate(this.giveFld(cur, this.FIELD_ENC_DATE));
			encaissement.setMontant(Fonctions.convertToFloat(this.giveFld(cur, this.FIELD_ENC_MONTANT)));
			encaissement.setNumeroCompte(this.giveFld(cur, this.FIELD_ENC_NUMERO_COMPTE));
			encaissement.setTypePaiement(this.giveFld(cur, this.FIELD_ENC_TYPE_PAIEMENT));
			encaissement.setCodeClient(this.giveFld(cur, this.FIELD_ENC_CODE_CLIENT));
			encaissement.setSignataire(this.giveFld(cur, this.FIELD_ENC_SIGNATAIRE));
			encaissement.setFilenameSignature(this.giveFld(cur, this.FIELD_ENC_FILENAME_SIGNATURE));
			encaissement.setCodeBanque(this.giveFld(cur, this.FIELD_ENC_CODE_BANQUE));
			encaissement.setDateCheque(this.giveFld(cur, this.FIELD_ENC_DATE_CHEQUE));
			encaissement.setNumeroSouche(this.giveFld(cur, this.FIELD_ENC_NUMSOUCHE));

			
			cur.close();//mv

			if(validation != null){
				if(Global.dbKDEncaisserFacture.getValidationEncaissementFromNumero(
						encaissement.getIdentifiant()).equals(Encaissement.NON_VALIDE)) return encaissement;
				else return null;
			}else return encaissement;
		} else
		{
			if (cur!=null)
				cur.close();
			return null;
		}
	}

	/**
	 * Récupère les encaissements du type de paiement passé en paramètre
	 * @param typePaiement
	 * @return
	 */
	public ArrayList<Encaissement> getEncaissementsFromTypePaiement(
			String typePaiement) {
		if (typePaiement.equals(""))
			return null;

		ArrayList<Encaissement> encaissements = new ArrayList<Encaissement>();
		String stFitre="";
		if(typePaiement.equals(Encaissement.TYPE_CHEQUE))
		{
			stFitre=" and ( "+this.FIELD_ENC_DATE+">="+Fonctions.getDay_Monday_Courant()+" and "+this.FIELD_ENC_DATE+"<="+Fonctions.getDay_Sunday_Courant()+")";

		}
		String query = "SELECT * FROM " + TABLENAME + " where "
				+ fld_kd_dat_type + "=" + KD_TYPE + " and "
				+ this.FIELD_ENC_TYPE_PAIEMENT + "=" + "'" + typePaiement + "' and ( "
				+ this.FIELD_ENC_ETAT_RBANQUE + "=" + "'0' or "
				+ this.FIELD_ENC_ETAT_RBANQUE + "=" + "'' ) "+stFitre;


		Cursor cur = db.conn.rawQuery(query, null);
		while (cur.moveToNext()) {
			Encaissement encaissement = new Encaissement();
			encaissement.setIdentifiant(this.giveFld(cur, this.FIELD_ENC_IDENTIFIANT));
			encaissement.setAgence(this.giveFld(cur, this.FIELD_ENC_AGENCE));
			encaissement.setBanque(this.giveFld(cur, this.FIELD_ENC_BANQUE));
			encaissement.setCheque(this.giveFld(cur, this.FIELD_ENC_CHEQUE));
			encaissement.setDate(this.giveFld(cur, this.FIELD_ENC_DATE));
			encaissement.setMontant(Fonctions.convertToFloat(this.giveFld(cur, this.FIELD_ENC_MONTANT)));
			encaissement.setNumeroCompte(this.giveFld(cur, this.FIELD_ENC_NUMERO_COMPTE));
			encaissement.setTypePaiement(this.giveFld(cur, this.FIELD_ENC_TYPE_PAIEMENT));
			encaissement.setCodeClient(this.giveFld(cur, this.FIELD_ENC_CODE_CLIENT));
			encaissement.setSignataire(this.giveFld(cur, this.FIELD_ENC_SIGNATAIRE));
			encaissement.setFilenameSignature(this.giveFld(cur, this.FIELD_ENC_FILENAME_SIGNATURE));
			encaissement.setCodeBanque(this.giveFld(cur, this.FIELD_ENC_CODE_BANQUE));
			encaissement.setDateCheque(this.giveFld(cur, this.FIELD_ENC_DATE_CHEQUE));
			encaissement.setNumeroSouche(this.giveFld(cur, this.FIELD_ENC_NUMSOUCHE));

			encaissements.add(encaissement);
		} 

		if (cur!=null)
			cur.close();

		return encaissements;
	}

	/**
	 * Insertion d'une ligne
	 * @param encaissement
	 * @return
	 */
	public boolean insertEncaissement(Encaissement encaissement, String codeAgent){
		String query = null;

		try{
			query = "INSERT INTO "
					+ TABLENAME
					+ " ("
					+ dbKD.fld_kd_dat_type
					+ ","
					+ this.FIELD_ENC_IDENTIFIANT
					+ ","
					+ this.FIELD_ENC_CODE_AGENT
					+ ","
					+ this.FIELD_ENC_AGENCE
					+ ","
					+ this.FIELD_ENC_BANQUE
					+ ","
					+ this.FIELD_ENC_CHEQUE
					+ ","
					+ this.FIELD_ENC_DATE
					+ ","
					+ this.FIELD_ENC_MONTANT
					+ ","
					+ this.FIELD_ENC_NUMERO_COMPTE
					+ ","
					+ this.FIELD_ENC_TYPE_PAIEMENT
					+ ","
					+ this.FIELD_ENC_TYPEDOC
					+ ","
					+ this.FIELD_ENC_CODE_CLIENT
					+ ","
					+ this.FIELD_ENC_SIGNATAIRE
					+ ","
					+ this.FIELD_ENC_FILENAME_SIGNATURE
					+ ","
					+ this.FIELD_ENC_ETAT
					+ ","
					+ this.FIELD_ENC_ETAT_RBANQUE
					+ ","
					+ this.FIELD_ENC_CODE_BANQUE
					+ ","
					+ this.FIELD_ENC_DATE_CHEQUE
					+ ","
					+ this.FIELD_ENC_ISPRINTED
					+ " ) VALUES ( "
					+ "'"+String.valueOf(KD_TYPE)+ "'"
					+ ","
					+ "'"+MyDB.controlFld(encaissement.getIdentifiant())+"'"
					+ ","
					+ "'"+MyDB.controlFld(codeAgent)+"'"
					+ ","
					+ "'"+MyDB.controlFld(encaissement.getAgence())+"'"
					+ ","
					+ "'"+MyDB.controlFld(encaissement.getBanque())+"'"
					+ ","
					+ "'"+MyDB.controlFld(encaissement.getCheque())+"'"
					+ ","
					+ "'"+MyDB.controlFld(encaissement.getDate())+"'"
					+ ","
					+ "'"+encaissement.getMontant()+"'"
					+ ","
					+ "'"+MyDB.controlFld(encaissement.getNumeroCompte())+"'"
					+ ","
					+ "'"+MyDB.controlFld(encaissement.getTypePaiement())+"'"
					+ ","
					+ "'"+MyDB.controlFld(encaissement.getTypeDocument())+"'"
					+ ","
					+ "'"+MyDB.controlFld(encaissement.getCodeClient())+"'"
					+ ","
					+ "'"+MyDB.controlFld(encaissement.getSignataire())+"'"
					+ ","
					+ "'"+MyDB.controlFld(encaissement.getFilenameSignatureSignataire())+"'"
					+ ","
					+ "'0'"
					+ ","
					+ "'0'"
					+ ","
					+ "'"+MyDB.controlFld(encaissement.getCodeBanque())+"'"
					+ ","
					+ "'"+MyDB.controlFld(encaissement.getDateCheque())+"'"
					+ ","
					+ "'0'"
					+ ")";

			db.conn.execSQL(query);
			
			Global.dbLog.Insert("Encaissement type 98", "insertEncaissement", "", "Requete: "
					+ query, "", "");
			return true;
		}catch(SQLException ex){
			return false;
		}
	}

	/**
	 * Mise à jour de l'encaissement si encaissement libre, avec le signataire et le filename de la signature
	 * @param signataire
	 * @param filename
	 * @return
	 */
	public boolean updateSignataireFilenameSignatureOfEncaissement(String numeroEncaissement, String signataire, String filename){
		try
		{
			String query="update  "+
					TABLENAME+	
					" set "
					+ FIELD_ENC_SIGNATAIRE
					+ "='"+signataire+"'"
					+ ", " 
					+ FIELD_ENC_FILENAME_SIGNATURE
					+ "='"+filename+"'"
					+ " where "+
					dbKD.fld_kd_dat_type+"='"+KD_TYPE+"' and "+

					FIELD_ENC_IDENTIFIANT
					+"="+numeroEncaissement;

			db.conn.execSQL(query);
			
			Global.dbLog.Insert("Encaissement type 98", "updateSignataireFilenameSignatureOfEncaissement", "", "Requete: "
					+ query, "", "");
			return true;
		}
		catch(Exception ex)
		{
			Global.lastErrorMessage=ex.getLocalizedMessage();
		}
		return false;
	}
	public boolean updateRemiseEnBanqueOfEncaissement_Cheque(String stChequeIdentifiant, String etat){
		try
		{


			String query="update  "+
					TABLENAME+	
					" set "
					+ FIELD_ENC_ETAT_RBANQUE
					+ "='"+etat+"' "		
					+ " where "+
					dbKD.fld_kd_dat_type+"='"+KD_TYPE+"' and "+
					FIELD_ENC_IDENTIFIANT
					+"='"+stChequeIdentifiant+"'";



			db.conn.execSQL(query);
			
			Global.dbLog.Insert("Encaissement type 98", "updateRemiseEnBanqueOfEncaissement_Cheque", "", "Requete: "
					+ query, "", "");
			return true;
		}
		catch(Exception ex)
		{
			Global.lastErrorMessage=ex.getLocalizedMessage();
		}
		return false;
	}

	public boolean updateRemiseEnBanqueOfEncaissement_Espece( String typepaiement){
		String query = "";
		try
		{


			query="update  "+
					TABLENAME+	
					" set "
					+ FIELD_ENC_ETAT_RBANQUE
					+ "='1' "		
					+ " where "+
					dbKD.fld_kd_dat_type+"='"+KD_TYPE+"' and "+


					FIELD_ENC_TYPE_PAIEMENT
					+"='"+typepaiement+"'  ";


			db.conn.execSQL(query);
			
			Global.dbLog.Insert("Encaissement type 98", "updateRemiseEnBanqueOfEncaissement_Espece", "", "Requete: "
					+ query, "", "");
			return true;
		}
		catch(Exception ex)
		{
			Global.lastErrorMessage=ex.getLocalizedMessage();
			Global.dbLog.Insert("Encaissement type 98", "updateRemiseEnBanqueOfEncaissement_Espece", "", "Requete: "
					+ query, "Exception : "+ex.getLocalizedMessage(), "");
		}
		return false;
	}
	public boolean updateEtatOfEncaissement( ){
		try
		{


			String query="update  "+
					TABLENAME+	
					" set "
					+ FIELD_ENC_ETAT
					+ "='1' "		
					+ " where "+
					dbKD.fld_kd_dat_type+"='"+KD_TYPE+"' and "+


					FIELD_ENC_ETAT
					+"='0'  ";


			db.conn.execSQL(query);
			
			Global.dbLog.Insert("Encaissement type 98", "updateEtatOfEncaissement", "", "Requete: "
					+ query, "", "");
			return true;
		}
		catch(Exception ex)
		{
			Global.lastErrorMessage=ex.getLocalizedMessage();
		}
		return false;
	}

	/**
	 * Retourne l'identifiant maximum pour les encaissements
	 * @return
	 */
	public String selectMaxIdentifiant(){
		String query = "SELECT max("+this.FIELD_ENC_IDENTIFIANT+") FROM " + TABLENAME;

		Cursor cur = db.conn.rawQuery(query, null);
		if (cur.moveToNext()) {
			String v=cur.getString(0);
			if (cur!=null)
				cur.close();//mv
			return v;
		}
		if (cur!=null)
			cur.close();//mv
		return null;
	}
	public boolean DeleteOfEncaissement( ){
		try
		{
			ArrayList<String> ids = new ArrayList<String>();
			//on sélectionne les numéros d'encaissement qui vont etre supprimé
			String queryEnc = "select "+FIELD_ENC_IDENTIFIANT+" from "+TABLENAME+" where "+
					dbKD.fld_kd_dat_type+"='"+KD_TYPE+"' and ("+
					FIELD_ENC_ETAT_RBANQUE
					+"='1'  or "+ FIELD_ENC_DATE+"<"+Fonctions.getDay_Monday_Courant()+")";

			Cursor cur = db.conn.rawQuery(queryEnc, null);
			if(cur != null){
				while (cur.moveToNext()) {
					String result = cur.getString(0);
					if(result != null && !result.equals("")) ids.add(result);
				}
				cur.close();
			}
			
			for(String id : ids){
				Global.dbKDEncaisserFacture.deleteLineFromNumeroEncaissement(id);
			}

			String query="DELETE  FROM "+
					TABLENAME+	

					" where "+
					dbKD.fld_kd_dat_type+"='"+KD_TYPE+"' and ("+

					FIELD_ENC_ETAT_RBANQUE
					+"='1'  or "+ FIELD_ENC_DATE+"<"+Fonctions.getDay_Monday_Courant()+")";


			db.conn.execSQL(query);
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
		try
		{		
			query="update  "+
					TABLENAME+	
					" set "
					+ FIELD_ENC_NUMSOUCHE
					+ "='"+numSouche+"' "		
					+ " where "+
					dbKD.fld_kd_dat_type+"='"+KD_TYPE+"' and "+			
					FIELD_ENC_IDENTIFIANT
					+"='"+identifiant+"'  ";


			db.conn.execSQL(query);
			
			Global.dbLog.Insert("Encaissement type 98", "updateNumeroSoucheEncaissement", "", "Requete: "
					+ query, "", "");
			return true;
		}
		catch(Exception ex)
		{
			Global.lastErrorMessage=ex.getLocalizedMessage();
			Global.dbLog.Insert("Encaissement type 98", "updateNumeroSoucheEncaissement", "", "Requete: "
					+ query, "Exception : "+ex.getLocalizedMessage(), "");
		}
		return false;
	}

	//Champs pour l'impression
	public boolean setPrint(String codeclient,String numsouche) {
		try {
			String query = "update  " + TABLENAME 
					+" set "+FIELD_ENC_ISPRINTED+"='1'"
					+ " where "
					+ fld_kd_dat_type + "=" + KD_TYPE + " and "
					+ this.FIELD_ENC_CODE_CLIENT + "=" + "'" + codeclient + "' and "
					+ this.FIELD_ENC_NUMSOUCHE+ "=" + "'" + numsouche + "' ";

			db.conn.execSQL(query);

			Global.dbLog.Insert("Encaissement", "setprint", "", "Numsouche:" + numsouche, "",
					"");

			return true;
		} catch (Exception ex) {

		}
		return false;
	}

	public boolean isPrintOk(String codecli)
	{
		String query = "SELECT count(*) FROM " + TABLENAME + " where "
				+ fld_kd_dat_type + "=" + KD_TYPE + " and "
				+ this.FIELD_ENC_CODE_CLIENT+ "=" + "'"+codecli+"' and "
				+ this.FIELD_ENC_ISPRINTED +"='0'";

		Cursor cur = db.conn.rawQuery(query, null);
		if(cur.moveToNext()){
			int result = cur.getInt(0);
			if (result == 0)
				return true;
			return false;
		}

		return true;


	}

	public ArrayList<Encaissement> getEncaissementNotPrint(){
		String query = "SELECT * FROM " + TABLENAME + " where "
				+ fld_kd_dat_type + "=" + KD_TYPE + " and "
				+ this.FIELD_ENC_ISPRINTED+ "=" + "'0'";

		ArrayList<Encaissement> encaissements = new ArrayList<Encaissement>();
		Cursor cur = db.conn.rawQuery(query, null);
		if(cur != null){
			while(cur.moveToNext()){
				Encaissement result = getEncaissementFormCursor(cur);
				if(result != null) encaissements.add(result);
			}
			cur.close();
		}

		return encaissements;
	}

	Encaissement getEncaissementFormCursor(Cursor cur){
		if(cur == null) return null;

		Encaissement encaissement = new Encaissement();
		encaissement.setIdentifiant(this.giveFld(cur, this.FIELD_ENC_IDENTIFIANT));
		encaissement.setAgence(this.giveFld(cur, this.FIELD_ENC_AGENCE));
		encaissement.setBanque(this.giveFld(cur, this.FIELD_ENC_BANQUE));
		encaissement.setCheque(this.giveFld(cur, this.FIELD_ENC_CHEQUE));
		encaissement.setDate(this.giveFld(cur, this.FIELD_ENC_DATE));
		encaissement.setMontant(Fonctions.convertToFloat(this.giveFld(cur, this.FIELD_ENC_MONTANT)));
		encaissement.setNumeroCompte(this.giveFld(cur, this.FIELD_ENC_NUMERO_COMPTE));
		encaissement.setTypePaiement(this.giveFld(cur, this.FIELD_ENC_TYPE_PAIEMENT));
		encaissement.setCodeClient(this.giveFld(cur, this.FIELD_ENC_CODE_CLIENT));
		encaissement.setSignataire(this.giveFld(cur, this.FIELD_ENC_SIGNATAIRE));
		encaissement.setFilenameSignature(this.giveFld(cur, this.FIELD_ENC_FILENAME_SIGNATURE));
		encaissement.setCodeBanque(this.giveFld(cur, this.FIELD_ENC_CODE_BANQUE));
		encaissement.setDateCheque(this.giveFld(cur, this.FIELD_ENC_DATE_CHEQUE));
		encaissement.setNumeroSouche(this.giveFld(cur, this.FIELD_ENC_NUMSOUCHE));

		return encaissement;
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
	
	public int countNonTransmises(){
		int result = 0;

		String query = "select count(*) from "+TABLENAME+" where "
				+ fld_kd_dat_type + "=" + KD_TYPE + " and "
				+ FIELD_ENC_ETAT +"='0'";
		
		Cursor cur = db.conn.rawQuery(query, null);
		if(cur != null && cur.moveToNext()){
			result = cur.getInt(0);
			cur.close();
		}
		return result;
	}
}
