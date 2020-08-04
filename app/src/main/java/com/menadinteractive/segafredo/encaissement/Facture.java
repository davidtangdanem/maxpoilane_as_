package com.menadinteractive.segafredo.encaissement;

import java.util.ArrayList;
import java.util.Comparator;

import android.os.Bundle;

import com.menadinteractive.segafredo.communs.Fonctions;
import com.menadinteractive.segafredo.communs.Global;
import com.menadinteractive.segafredo.db.TableSouches;
import com.menadinteractive.segafredo.db.dbKD729HistoDocuments;
import com.menadinteractive.segafredo.db.dbKD730FacturesDues;
import com.menadinteractive.segafredo.db.dbKD83EntCde;

public class Facture {

	//Type de facture (document)
	//Chèque CH
	public static String TYPE_CHEQUE = "CH";
	//Espèce ES
	public static String TYPE_ESPECE = "ES";
	//Avoir AV
	public static String TYPE_AVOIR = "AV";

	String numDoc;
	public String getNumDoc(){return numDoc;}
	public void setNumDoc(String _numDoc){numDoc = _numDoc;}

	String codeClient;
	public String getCodeClient(){return codeClient;}
	public void setCodeClient(String _codeClient){codeClient = _codeClient;}

	String dateFacture;
	public String getDateFacture(){return dateFacture;}
	public void setDateFacture(String _dateFacture){dateFacture = _dateFacture;}
	
	String dateFactureTri;
	public String getDateFactureTri(){return dateFactureTri;}
	public void setDateFactureTri(String _dateFactureTri){dateFactureTri = _dateFactureTri;}

	String dateEcheance;
	public String getDateEcheance(){return dateEcheance;}
	public void setDateEcheance(String _dateEcheance){dateEcheance = _dateEcheance;}

	String remise;
	public String getRemise(){return remise;}
	public void setRemise(String _remise){remise = _remise;}

	float montantHT;
	public float getMontantHT(){return montantHT;}
	public void setMontantHT(float _montantHT){montantHT = _montantHT;}

	float montantTVA;
	public float getMontantTVA(){return montantTVA;}
	public void setMontantTVA(float _montantTVA){montantTVA = _montantTVA;}

	float montantTTC;
	public float getMontantTTC(){return montantTTC;}
	public void setMontantTTC(float _montantTTC){montantTTC = _montantTTC;}

	float montantDu;
	public float getMontantDu(){return montantDu;}
	public void setMontantDu(float _montantDu){montantDu = _montantDu;}

	String typeFacture = TableSouches.TYPEDOC_FACTURE;
	public String getTypeFacture(){return typeFacture;}
	public void setTypeFacture(String _typeFacture){typeFacture = _typeFacture;}

	boolean isChecked;
	public boolean getIsChecked(){return isChecked;}
	public void setIsChecked(boolean _isChecked){isChecked = _isChecked;}

	boolean isEnded = false;
	public boolean getIsEnded(){return isEnded;}
	public void setIsEnded(boolean _isEnded){isEnded = _isEnded;}

	boolean isFactureDue = false;
	public boolean getIsFactureDue(){return isFactureDue;}
	public void setIsFactureDue(boolean _isFactureDue){isFactureDue = _isFactureDue;}

	String color;
	public String getColor(){return color;}
	public void setColor(String _color){color = _color;}

	//ArrayList de numéro d'encaissement
	ArrayList<String> numerosEncaissement;
	public ArrayList<String> getListNumerosEncaissement(){return numerosEncaissement;}
	public void setListNumerosEncaissement(ArrayList<String> _listNumerosEncaissement){ 
		numerosEncaissement = _listNumerosEncaissement;}

	public Facture(String _numDoc, String _codeClient, String _dateFacture, String _dateEcheance, 
			String _remise, float _montantHT, float _montantTVA, float _montantTTC, float _montantDu, boolean _isFactureDu){
		numDoc = _numDoc;
		codeClient = _codeClient;
		dateFacture = _dateFacture;
		dateEcheance = _dateEcheance;
		remise = _remise;
		montantHT = _montantHT;
		montantTVA = _montantTVA;
		montantTTC = _montantTTC;
		montantDu = _montantDu;
		isFactureDue = _isFactureDu;
		
		dateFactureTri = Fonctions.getYYYYMMDD(dateFacture);
	}

	public Facture(String _numDoc, String _codeClient, String _dateFacture, String _dateEcheance, 
			String _remise, float _montantHT, float _montantTVA, float _montantTTC, float _montantDu, boolean _isFactureDu,
			String _type){
		numDoc = _numDoc;
		codeClient = _codeClient;
		dateFacture = _dateFacture;
		dateEcheance = _dateEcheance;
		remise = _remise;
		montantHT = _montantHT;
		montantTVA = _montantTVA;
		montantTTC = _montantTTC;
		montantDu = _montantDu;
		isFactureDue = _isFactureDu;
		typeFacture = _type;
		
		dateFactureTri = Fonctions.getYYYYMMDD(dateFacture);
	}

	public Facture(){}

	/**
	 * Récupère la liste des factures d'un client grâce au code client
	 * @param codeClient
	 * @return ArrayList<Facture> list
	 */
	public static ArrayList<Facture> getFacturesFromDB(String codeClient){
		ArrayList<Facture> factures = new ArrayList<Facture>();
		Facture facture;

		//on récupère les factures venant de la table histo
		ArrayList<Bundle> bFacturesDues = Global.dbKDFacturesDues.getFacturesDuesFromCodeClient(codeClient);

		for(Bundle b : bFacturesDues){
			facture = new Facture(b.getString(Global.dbKDFacturesDues.FIELD_NUMDOC), 
					codeClient,
					b.getString(Global.dbKDFacturesDues.FIELD_DATEDOC), 
					b.getString(Global.dbKDFacturesDues.FIELD_DATEECH),
					b.getString(Global.dbKDFacturesDues.FIELD_REMISE), 
					Fonctions.convertToFloat(b.getString(Global.dbKDFacturesDues.FIELD_MNTHT)),
					Fonctions.convertToFloat(b.getString(Global.dbKDFacturesDues.FIELD_MNTTVA)), 
					Fonctions.convertToFloat(b.getString(Global.dbKDFacturesDues.FIELD_MNTTTC)),
					Fonctions.convertToFloat(b.getString(Global.dbKDFacturesDues.FIELD_MNTDU)),
					true,
					b.getString(Global.dbKDFacturesDues.FIELD_TYPEDOC));		

			facture.setListNumerosEncaissement(Global.dbKDEncaisserFacture.getNumerosEncaissementFromFacture(facture));
			if(facture.getListNumerosEncaissement() != null && facture.getListNumerosEncaissement().size() > 0) 
				facture.setIsEnded(true);
			factures.add(facture);
		}

		//on récupère également les avoirs
		ArrayList<Bundle> bAvoir = Global.dbKDHistoDocuments.getAvoirFromCodeClient(codeClient);

		for(Bundle b : bAvoir){
			facture = new Facture(b.getString(Global.dbKDHistoDocuments.FIELD_NUMDOC),
					codeClient,
					b.getString(Global.dbKDHistoDocuments.FIELD_DATEDOC),
					b.getString(Global.dbKDHistoDocuments.FIELD_DATEECH),
					b.getString(Global.dbKDHistoDocuments.FIELD_REMISE),
					Fonctions.convertToFloat(b.getString(Global.dbKDHistoDocuments.FIELD_MNTHT)),
					Fonctions.convertToFloat(b.getString(Global.dbKDHistoDocuments.FIELD_MNTTVA)),
					Fonctions.convertToFloat(b.getString(Global.dbKDHistoDocuments.FIELD_MNTTTC)),
					Fonctions.convertToFloat(b.getString(Global.dbKDHistoDocuments.FIELD_MNTTTC)), 
					false,
					b.getString(Global.dbKDHistoDocuments.FIELD_TYPEDOC));

			facture.setListNumerosEncaissement(Global.dbKDEncaisserFacture.getNumerosEncaissementFromFacture(facture));
			if(facture.getListNumerosEncaissement() != null && facture.getListNumerosEncaissement().size() > 0) 
				facture.setIsEnded(true);
			factures.add(facture);
		}

		return factures;
	}

	/**
	 * Récupère la facture grâce au numéro de document
	 * @param numDoc
	 * @return Facture facture
	 */
	public static Facture getFactureFromNumDoc(String numDoc){

		Facture facture = null; 

		Bundle b = Global.dbKDFacturesDues.getFactureDueFromNumDoc(numDoc);

		if(b.size() > 0){
			facture = new Facture(b.getString(Global.dbKDFacturesDues.FIELD_NUMDOC), 
					b.getString(dbKD730FacturesDues.FIELD_CODECLIENT),
					b.getString(Global.dbKDFacturesDues.FIELD_DATEDOC), 
					b.getString(Global.dbKDFacturesDues.FIELD_DATEECH),
					b.getString(Global.dbKDFacturesDues.FIELD_REMISE), 
					Fonctions.convertToFloat(b.getString(Global.dbKDFacturesDues.FIELD_MNTHT)),
					Fonctions.convertToFloat(b.getString(Global.dbKDFacturesDues.FIELD_MNTTVA)), 
					Fonctions.convertToFloat(b.getString(Global.dbKDFacturesDues.FIELD_MNTTTC)),
					Fonctions.convertToFloat(b.getString(Global.dbKDFacturesDues.FIELD_MNTDU)),
					true, b.getString(Global.dbKDFacturesDues.FIELD_TYPEDOC));

			facture.setListNumerosEncaissement(Global.dbKDEncaisserFacture.getNumerosEncaissementFromFacture(facture));
			if(facture.getListNumerosEncaissement() != null && facture.getListNumerosEncaissement().size() > 0) 
				facture.setIsEnded(true);
		}else{
			b = Global.dbKDHistoDocuments.getDocumentFromNumDoc(numDoc);

			if(b.size() > 0){
				facture = new Facture(b.getString(Global.dbKDHistoDocuments.FIELD_NUMDOC), 
						b.getString(dbKD729HistoDocuments.FIELD_CODECLIENT),
						b.getString(Global.dbKDHistoDocuments.FIELD_DATEDOC), 
						b.getString(Global.dbKDHistoDocuments.FIELD_DATEECH),
						b.getString(Global.dbKDHistoDocuments.FIELD_REMISE), 
						Fonctions.convertToFloat(b.getString(Global.dbKDHistoDocuments.FIELD_MNTHT)),
						Fonctions.convertToFloat(b.getString(Global.dbKDHistoDocuments.FIELD_MNTTVA)), 
						Fonctions.convertToFloat(b.getString(Global.dbKDHistoDocuments.FIELD_MNTTTC)),
						Fonctions.convertToFloat(b.getString(Global.dbKDHistoDocuments.FIELD_MNTTTC)),
						false, b.getString(Global.dbKDHistoDocuments.FIELD_TYPEDOC));

				facture.setListNumerosEncaissement(Global.dbKDEncaisserFacture.getNumerosEncaissementFromFacture(facture));
				if(facture.getListNumerosEncaissement() != null && facture.getListNumerosEncaissement().size() > 0) 
					facture.setIsEnded(true);
			}
		}

		return facture;
	}

	/**
	 * Récupère une facture à partir d'une liste et de l'identifiant
	 * @param identifiant
	 * @param factures
	 * @return facture
	 */
	public static Facture getFactureFromList(String identifiant, ArrayList<Facture> factures){

		for(Facture facture : factures){
			if(facture.getNumDoc().equals(identifiant)) return facture;
		}
		return null;
	}
	
	public static class Comparators {

		public static Comparator<Facture> DATE = new Comparator<Facture>() {
			@Override
			public int compare(Facture o1, Facture o2) {
				if (o1 == null) {
					return (o2 == null) ? 1 : -1;
				}
				if (o2 == null) {
					return -1;
				}
				if (o1.getDateFactureTri() == null) {
					return (o2.getDateFactureTri() == null) ? -1 : 1;
				}
				if (o2.getDateFactureTri() == null) {
					return -1;
				}
				if (o1.getDateFactureTri().equals("")) {
					return (o2.getDateFactureTri().equals("")) ? -1 : 1;
				}
				if (o2.getDateFactureTri().equals("")) {
					return -1;
				}
				int type = o1.getTypeFacture().compareTo(o2.getTypeFacture());  
		        if (type != 0) {  
		            return type;  
		        }  
				return o2.getDateFactureTri().compareTo(o1.getDateFactureTri());
			}
		};
	}
}
