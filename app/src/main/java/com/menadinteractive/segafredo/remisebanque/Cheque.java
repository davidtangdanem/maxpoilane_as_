package com.menadinteractive.segafredo.remisebanque;

import java.util.ArrayList;

import com.menadinteractive.segafredo.encaissement.Encaissement;

public class Cheque {
	private String numeroSouche;
	public String getNumeroSouche(){return numeroSouche;}
	public void setNumeroSouche(String _numeroSouche){numeroSouche = _numeroSouche;}
	
	private String numeroCheque;
	public String getNumeroCheque(){return numeroCheque;}
	public void setNumeroCheque(String _numeroCheque){numeroCheque = _numeroCheque;}
	
	private String libelleBanque;
	public String getLibelleBanque(){return libelleBanque;}
	public void setLibelleBanque(String _libelleBanque){libelleBanque = _libelleBanque;}
	
	private String identifiantBanque;
	public String getIdentifiantBanque(){return identifiantBanque;}
	public void setIdentifiantBanque(String _identifiantBanque){identifiantBanque = _identifiantBanque;}
	
	private String dateCheque;
	public String getDateCheque(){return dateCheque;}
	public void setDateCheque(String _dateCheque){dateCheque = _dateCheque;}
	
	private float montant;
	public float getMontant(){return montant;}
	public void setMontant(float _montant){montant = _montant;}
	
	private boolean checked = false;
	public boolean isChecked(){return checked;}
	public void setChecked(boolean _checked){checked = _checked;}
	
	private String identifiant;
	public String getIdentifiant(){return identifiant;}
	public void setIdentifiant(String _identifiant){identifiant = _identifiant;}
	
	
	public Cheque(){}
	
	public Cheque(String _numeroCheque, String _libelleBanque, String _identifiantBanque, String _dateCheque, float _montant,String _identifiant,String _numsouche){
		numeroCheque = _numeroCheque;
		libelleBanque = _libelleBanque;
		identifiantBanque = _identifiantBanque;
		dateCheque = _dateCheque;
		montant = _montant;
		identifiant=_identifiant;
		numeroSouche=_numsouche;
	}
	
	public Cheque(String _numeroCheque, String _libelleBanque, String _identifiantBanque, String _dateCheque, 
			float _montant, boolean _checked,String _numsouche){
		numeroCheque = _numeroCheque;
		libelleBanque = _libelleBanque;
		identifiantBanque = _identifiantBanque;
		dateCheque = _dateCheque;
		checked = _checked;
		montant = _montant;
		numeroSouche=_numsouche;
	}
	
	/**
	 * Retourne une liste de chèque construit à partir des encaissements
	 * @param mEncaissementsCheque
	 * @return
	 */
	public static ArrayList<Cheque> getChequeFromEncaissements(
			ArrayList<Encaissement> mEncaissementsCheque) {
		
		if(mEncaissementsCheque == null || mEncaissementsCheque.size() == 0) return null;
		
		ArrayList<Cheque> cheques = new ArrayList<Cheque>();
		
		for(Encaissement enc : mEncaissementsCheque){
			Cheque cheque = new Cheque(enc.getCheque(), enc.getBanque(), enc.getCodeBanque(), enc.getDateCheque(), enc.getMontant(),enc.getIdentifiant(),enc.getNumeroSouche());		
			cheques.add(cheque);
		}
		
		return cheques;
	}
	
}
