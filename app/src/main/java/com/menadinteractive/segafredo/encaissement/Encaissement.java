package com.menadinteractive.segafredo.encaissement;

import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;

import android.content.Context;

import com.menadinteractive.maxpoilane.ExternalStorage;
import com.menadinteractive.segafredo.communs.Global;
import com.menadinteractive.segafredo.db.Preferences;
import com.menadinteractive.segafredo.plugins.Espresso;

public class Encaissement {

	//Type de paiement
	//Chèque CH
	public static String TYPE_CHEQUE = "CH";
	//Espèce ES
	public static String TYPE_ESPECE = "ES";
	//Avoir AV
	public static String TYPE_AVOIR = "AV";
	
	//Etat remise en banque
	//Enregistré
	public static String REB_SAVE = "1";
	//Non enregistré
	public static String REB_NOT_SAVE = "0";
	
	public static final String VALIDE = "1";
	public static final String NON_VALIDE = "0";

	public static final int TYPE_DELETE_ENCAISSEMENT = 9901;
	public static final String ENCAISSEMENT_NOFACTURE = "NOFACTURE";
	public static final String TYPE_DOCUMENT = "RG";
	public static String NUMERO_ENCAISSEMENT = "";

	String identifiant;
	public String getIdentifiant(){return identifiant;}
	public void setIdentifiant(String _identifiant){identifiant = _identifiant;}

	String codeClient;
	public String getCodeClient(){return codeClient;}
	public void setCodeClient(String _codeClient){codeClient = _codeClient;}

	String date;
	public String getDate(){return date;}
	public void setDate(String _date){date = _date;}

	String typePaiement;
	public String getTypePaiement(){return typePaiement;}
	public void setTypePaiement(String _typePaiement){typePaiement = _typePaiement;}

	String typeDocument = TYPE_DOCUMENT;
	public String getTypeDocument(){return typeDocument;}
	public void setTypeDocument(String _typeDocument){typeDocument = _typeDocument;}

	String banque;
	public String getBanque(){return banque;}
	public void setBanque(String _banque){banque = _banque;}

	String agence;
	public String getAgence(){return agence;}
	public void setAgence(String _agence){agence = _agence;}

	String numeroCompte;
	public String getNumeroCompte(){return numeroCompte;}
	public void setNumeroCompte(String _numeroCompte){numeroCompte = _numeroCompte;}

	String cheque;
	public String getCheque(){return cheque;}
	public void setCheque(String _cheque){cheque = _cheque;}

	float montant;
	public float getMontant(){return montant;}
	public void setMontant(float _montant){montant = _montant;}

	String color;
	public String getColor(){return color;}
	public void setColor(String _color){color = _color;}

	String signataire;
	public String getSignataire(){return signataire;}
	public void setSignataire(String _signataire){color = _signataire;}

	String filenameSignature;
	public String getFilenameSignatureSignataire(){return filenameSignature;}
	public void setFilenameSignature(String _filenameSignature){filenameSignature = _filenameSignature;}
	
	String numeroSouche;
	public String getNumeroSouche(){return numeroSouche;}
	public void setNumeroSouche(String _numeroSouche){numeroSouche = _numeroSouche;}
	
	String codeBanque;
	public String getCodeBanque(){return codeBanque;}
	public void setCodeBanque(String _codeBanque){codeBanque = _codeBanque;}
	
	String dateCheque;
	public String getDateCheque(){return dateCheque;}
	public void setDateCheque(String _dateCheque){dateCheque = _dateCheque;}

	//ArrayList de numéro d'encaissement
	ArrayList<String> numerosFactures;
	public ArrayList<String> getListNumerosFactures(){return numerosFactures;}
	public void setListNumerosFactures(ArrayList<String> _listNumerosFactures){ 
		numerosFactures = _listNumerosFactures;}

	public Encaissement(){}

	public Encaissement(String _identifiant, String _codeClient, String _date, String _typePaiement, String _banque,
			String _agence, String _numeroCompte, String _cheque, float _montant,String numSouche){
		identifiant = _identifiant;
		codeClient = _codeClient;
		date = _date;
		typePaiement = _typePaiement;
		banque = _banque;
		agence = _agence;
		numeroCompte = _numeroCompte;
		cheque = _cheque;
		montant = _montant;
		numeroSouche = numSouche;
	}

	/**
	 * Récupère un encaissement à l'aide de son identifiant
	 * @param identifiant
	 * @return Encaissement
	 */
	public static Encaissement getEncaissement(String identifiant, String codeClient, String validation){
		if(Global.dbKDEncaissement == null) return null;
		//récupération du tableau des numéros de facture pour cet encaissement
		Encaissement encaissement = Global.dbKDEncaissement.selectEncaissement(identifiant, codeClient, validation);
		if(encaissement == null) return null;
		//on charge le numero de souche
		encaissement.setNumeroSouche(Global.dbKDEncaisserFacture.getNumeroSoucheFromIdentifiantEncaissement(encaissement.getIdentifiant()));
		encaissement.setListNumerosFactures(
				Global.dbKDEncaisserFacture.getNumerosFactureFromEncaissement(encaissement));
		return encaissement;
	}

	public static Encaissement getEncaissement(String identifiant, String validation){
		if(Global.dbKDEncaissement == null) return null;
		//récupération du tableau des numéros de facture pour cet encaissement
		Encaissement encaissement = Global.dbKDEncaissement.selectEncaissement(identifiant, validation);
		if(encaissement == null) return null;
		//on charge le numero de souche
		encaissement.setNumeroSouche(Global.dbKDEncaisserFacture.getNumeroSoucheFromIdentifiantEncaissement(encaissement.getIdentifiant()));
		encaissement.setListNumerosFactures(
				Global.dbKDEncaisserFacture.getNumerosFactureFromEncaissement(encaissement));
		return encaissement;
	}

	/**
	 * Récupère un encaissement à partir d'une liste et de l'identifiant
	 * @param identifiant
	 * @param encaissements
	 * @return encaissement
	 */
	public static Encaissement getEncaissementFromList(String identifiant, ArrayList<Encaissement> encaissements){

		for(Encaissement encaissement : encaissements){
			if(encaissement.getIdentifiant().equals(identifiant)) return encaissement;
		}
		return null;
	}
	
	public static ArrayList<Encaissement> getEncaissementsFromTypePaiement(String typePaiement, ArrayList<Encaissement> encaissements){
		if (typePaiement == null)
			return null;

		ArrayList<Encaissement> encaissementsCheque = new ArrayList<Encaissement>();
		
		for(Encaissement e : encaissements){
			if(e.getTypePaiement().equals(typePaiement)) encaissementsCheque.add(e);
		}
		
		return encaissementsCheque;
	}
	
	public static ArrayList<Encaissement> getEncaissementsFromTypePaiement(String typePaiement){
		if (typePaiement == null)
			return null;
		
		return Global.dbKDEncaissement.getEncaissementsFromTypePaiement(typePaiement);
	}

	public static ArrayList<Encaissement> getEncaissementFromFacture(Facture facture, String codeClient, String validation){
		if (facture == null)
			return null;

		ArrayList<Encaissement> encaissements = new ArrayList<Encaissement>();
		ArrayList<String> numeros = Global.dbKDEncaisserFacture.getNumerosEncaissementFromFacture(facture);

		for(String num : numeros){
			Encaissement enc = getEncaissement(num, codeClient, validation);
			if(!containsEncaissement(encaissements, enc) && enc != null) encaissements.add(enc);
		}
		return encaissements;
	}

	public static boolean containsEncaissement(
			ArrayList<Encaissement> encaissements, Encaissement enc) {
		if(enc == null) return false;
		for (Encaissement e : encaissements){
			if(e == null) continue;
			if(e.getIdentifiant().equals(enc.getIdentifiant())) return true;
		}

		return false;
	}
	
	/**
	 * Supprime un encaissement à l'aide de son identifiant
	 * @param identifiant
	 * @return true si succes false sinon
	 */
	public static boolean deleteEncaissement(Context context, String identifiant, String codeClient, String validation){
		if(Global.dbKDEncaissement == null) return false;

		//on supprime la signature si elle existe
		Encaissement encaissement = Encaissement.getEncaissement(identifiant, validation);

		if(encaissement == null) return true;

		if(encaissement != null && encaissement.getFilenameSignatureSignataire() != null && !encaissement.getFilenameSignatureSignataire().equals("")){
			//on supprime la signature
			encaissement.deleteSignatureOfEncaissement(context);
		}

		//on récupère les numéros de factures liés à l'encaissement
		ArrayList<String> numerosFacture = Global.dbKDEncaisserFacture.getNumerosFactureFromId(identifiant);

		if(numerosFacture.size() == 1 && numerosFacture.get(0).equals(ENCAISSEMENT_NOFACTURE)){		
			boolean result = Global.dbKDEncaissement.deleteEncaissement(identifiant);
			boolean result2 = Global.dbKDEncaisserFacture.deleteLineFromNumeroEncaissement(identifiant);
			if(result && result2) return true;
			else return false;
		}else{
			//puis on récupère les numéros d'encaissements liés à ces factures dans la table EncaisserFacturer
			ArrayList<String> numerosEncaissement = Global.dbKDEncaisserFacture.getNumerosEncaissementsFromNumerosFacture(numerosFacture);
			//puis on supprime tous les encaissements à partir de la liste des numéros d'encaissements

			if(numerosEncaissement != null){
				int cpt = 0;
				for(String numEnc : numerosEncaissement){
					//on supprime la signature si elle existe
					Encaissement enc = Encaissement.getEncaissement(identifiant, validation);

					if(enc != null && enc.getFilenameSignatureSignataire() != null && !enc.getFilenameSignatureSignataire().equals("")){
						//on supprime la signature
						enc.deleteSignatureOfEncaissement(context);
					}
					boolean result = Global.dbKDEncaissement.deleteEncaissement(numEnc);
					boolean result2 = Global.dbKDEncaisserFacture.deleteLineFromNumeroEncaissement(numEnc);
					if(result && result2) cpt++;
				}
				if(cpt == numerosEncaissement.size()) return true;
				else return false;
			}
			return true;			
		}
	}

	/**
	 * Supprime la signature enregistrée pour un encaissement de type acompte
	 * @return
	 */
	public boolean deleteSignatureOfEncaissement(Context context){
		ExternalStorage externalStorage = new ExternalStorage(context, false);
		//File sdCard = Environment.getExternalStorageDirectory();
		File file = new File(externalStorage.getSignaturesFolder()+File.separator+ this.getIdentifiant()+"_encaissement"+".jpg");
		if(file.exists()){
			return file.delete();
		}
		return false;
	}

	/**
	 * Enregistre dans la base de données l'encaissement
	 * @return
	 */
	public Encaissement saveEncaissement(ArrayList<String> numerosFacture, String codeAgent){		
		if(Global.dbKDEncaissement == null) return null;
		if(Global.dbKDEncaisserFacture == null) return null;

		this.identifiant = generateNewIdentifiantEncaisserFacture(this.codeClient, false);

		if(this.identifiant != null && !this.identifiant.equals("")) {
			//sauvegarder les numéros de facture en lien avec l'encaissement dans dbkd99
			//sauf si un des numéros de facture est un avoir et qu'il n'y plus l'encaissement lié
			int cpt = 0;
			for(String num : numerosFacture){
				boolean result = Global.dbKDEncaisserFacture.insertEncaissement(generateNewIdentifiantEncaisserFacture(this.codeClient, true), 
						num, this.identifiant, this.numeroSouche);

				if(result) cpt++;
			}
			boolean result2 = Global.dbKDEncaissement.insertEncaissement(this, codeAgent);

			if(cpt == numerosFacture.size() && result2) return this;
			else return null;
		}
		else return null;
	}

	public static boolean updateForEncaissementAcompte(String numeroEncaissement, String signataire, String filenameSignature){
		if(Global.dbKDEncaissement == null) return false;

		return Global.dbKDEncaissement.updateSignataireFilenameSignatureOfEncaissement(
				numeroEncaissement, signataire, filenameSignature);
	}
	
	/**
	 * Validation des différents encaissement dépendant du numéro de souche passé en paramètre
	 * @param numeroSouche
	 * @param value
	 * @return boolean
	 */
	public static boolean updateValidation(String numeroSouche, String value,String commentaire){
		return Global.dbKDEncaisserFacture.updateValidationEncaissement(numeroSouche, value,commentaire);
	}

	/**
	 * Retourne un nouveau numéro identifiant incrémenté de 1
	 * @return String identifiant
	 */
	public static String generateNewIdentifiantEncaissement(){
		if(Global.dbKDEncaissement == null) return null;
		String maxIdentifiant = Global.dbKDEncaissement.selectMaxIdentifiant();
		if(maxIdentifiant == null || maxIdentifiant.equals("")) return "1";
		else{
			int max = 0;
			try{
				max = Integer.parseInt(maxIdentifiant);
			}catch(NumberFormatException ex){
				return "1";
			}
			max += 1;
			return Integer.toString(max);
		}
	}

	/**
	 * Retourne un nouveau numéro identifiant incrémenté de 1
	 * @return String identifiant
	 */
	public static String generateNewIdentifiantEncaisserFacture(String codeClient, boolean isEncaisserFacture){		
		//identifiant : code_client+timestamp
		String index = "";
		
		if(!isEncaisserFacture){
			index = "ENT";
		}else{
			index = "LIN";
		}
		index+=codeClient;
		
		long time= System.currentTimeMillis();
		
		return index+Long.toString(time);
	}

	public static float getTotalEncaissementFromList(ArrayList<Encaissement> encaissements){
		float total = 0;

		for(Encaissement e : encaissements){

			total += Math.round(e.getMontant()*100.0)/100.0;
		}
		return (float) (Math.round(total*100.0)/100.0);
	}

	static ArrayList<String> colors = new ArrayList<String>() {/**
	 * 
	 */
		private static final long serialVersionUID = 1L;

		{
			add("#313131");
			add("#0000ff");
			add("#008000");
			add("#cd3700");
			add("#ffd700");
			add("#b4155d");
			add("#99d9fa");
			add("#6e94a3");
			add("#ff8000");
			add("#dddddd");
		}};

		/**
		 * Attribut une couleur pour faire le lien entre encaissement et facture
		 */
		public static void attributeColorToEncaissementFacture(ArrayList<Encaissement> encaissements, ArrayList<Facture> factures){

			int indexColor = 1;
			for(Encaissement encaissement : encaissements){
				if(encaissement == null) continue;
				if(encaissement.getListNumerosFactures().size() <= 0 
						|| encaissement.getListNumerosFactures().get(0).equals(ENCAISSEMENT_NOFACTURE)){
					//on attribut la couleur 0 du tableau(noir)
					encaissement.setColor(colors.get(0));
				}else{
					if(encaissement.getColor() != null && !encaissement.getColor().equals("") 
							&& !encaissement.getColor().equals("null")) continue;
					else {
						if(encaissement.getColor() == null || encaissement.getColor().equals("") 
								|| encaissement.getColor().equals("null")) 
							encaissement.setColor(colors.get(indexColor));

						for(String num : encaissement.getListNumerosFactures()){
							Facture facture = Facture.getFactureFromList(num, factures);
							
							if(facture == null) continue;
							
							if(facture.getColor() != null && !facture.getColor().equals("")) continue;
							else{
								facture.setColor(colors.get(indexColor));
								for(String identifiant : facture.getListNumerosEncaissement()){
									Encaissement enc = Encaissement.getEncaissementFromList(identifiant, encaissements);
									if(enc != null){
										if(enc.getColor() != null && !enc.getColor().equals("") && !enc.getColor().equals("null")) continue;
										else {
											enc.setColor(colors.get(indexColor));
										}
									}
								}
							}
						}
					}
				}
				indexColor++;

			}
		}
		
		public static ArrayList<Facture> getFactureNonRegleFromListFactures(
				ArrayList<Facture> mFactures) {
			
			ArrayList<Facture> factures = new ArrayList<Facture>();
			
			for(Facture facture : mFactures){
				//on récupère les encaissements de la facture 
				float totalEncaissement = (float) (Math.round(Encaissement.getTotalEncaissementFromList(
						Encaissement.getEncaissementFromFacture(facture, facture.getCodeClient(), Encaissement.VALIDE))*100.0)/100.0);
				
				//s'il n'y a aucun réglement sur la facture je l'ajoute à la liste
				if(totalEncaissement == 0) factures.add(facture);
				
			}
					
			return factures;
		}
		
		public static ArrayList<String> getListeIdentifiantFromEncaissements(ArrayList<Encaissement> encaissements){
			ArrayList<String> identifiants = new ArrayList<String>();
			
			for(Encaissement e : encaissements){
				identifiants.add(e.getIdentifiant());
			}
			return identifiants;
		}
}
