package com.menadinteractive.printmodels;

import java.util.ArrayList;

import android.content.Context;

import com.menadinteractive.segafredo.communs.Fonctions;
import com.menadinteractive.segafredo.communs.Global;
import com.menadinteractive.segafredo.db.TableClient;
import com.menadinteractive.segafredo.db.TableClient.structClient;
import com.menadinteractive.segafredo.db.dbSiteListeLogin;
import com.menadinteractive.segafredo.db.dbSiteListeLogin.structlistLogin;
import com.menadinteractive.segafredo.encaissement.Encaissement;
import com.menadinteractive.segafredo.encaissement.Facture;
import com.menadinteractive.segafredo.remisebanque.Cheque;

public class Z420ModelEncaissement {

	Context context;

	public Z420ModelEncaissement(Context c)
	{
		context=c;
	}
	public String getEncaissement(ArrayList<Encaissement> encaissements, ArrayList<Cheque> cheques, String codecli, String codeVRP,String commentaire)
	{	 
		String completeLine="";

		if(encaissements == null) return null;

		String line ="";
		int taillepage=1300+40*encaissements.size();
		if(cheques != null && cheques.size() > 0) taillepage += 40*cheques.size();
		//1500
		line+="^XA^MNN^LL"+taillepage+"^XZ^XA^JUS^XZ";
		line+="^XA";
		line+="";
		line+="^CI8";
		line+=" ^CF0,40";
		line+="^FO170,50^FDSEGAFREDO ZANETTI FRANCE^FS";
		line+="^CF0,40";
		line+="^FO10,100^FH^FD(_15) Reçu^FS ";
		line+="^CF0,30";
		line+="^FO500,100^FDImprimé le : ^FS";
		line+="^FO650,100^FD IMPR_DATEIMPR^FS";
		line+="^FO10,140^FDN : IMPR_NUM_ENCAISSEMENT ^FS";
		line+="";
		line+="^FO10,170^FDDate : IMPR_DATEENC^FS";
		line+="";
		line+="^CF0,26";
		line+="^FO10,280^FDCommercial : ^FS";
		line+="^FO170,280^FDIMPR_NOMPRENOM^FS";
		line+="^FO450,280^FDN Client :^FS";
		line+="^FO560,280^FDIMPR_NUMCLI^FS";
		line+="^FO450,310^FDIMPR_RAISOC^FS";
		line+="^FO450,340^FDIMPR_NOM^FS";
		line+="^FO450,370^FDIMPR_ADR1^FS";
		line+="^FO450,400^FDIMPR_CPVILLE^FS";
		line+="^FO450,450^FDSiret : IMPR_SIRET^FS";
		line+="";
		line+="";

		//Ligne total espèces
		line+="^CF0,30";
		//line+="^FX MODE DE PAIEMENT";
		line+="^FO50,500^FDMontant espèces^FS";
		line+="^FO400,500^FDIMPR_ESPECE^FS";

		//Ligne total chèque
		line+="^FO50,550^FDMontant chèques^FS";
		line+="^FO400,550^FDIMPR_CHEQUE^FS";

		//Ligne des chèques
		int y = 600;
		if(cheques != null && cheques.size() > 0){
			StringBuffer sbCh=new StringBuffer();
			y=imprimeLineCheque(y, cheques,sbCh);
			line+=sbCh;
		}

		y=y+45;

		line+="^CF0,24";
		line+="^FO50,"+y+"^GB200,40,1^FS";
		line+="^FO110,"+(y+5)+"^FDN facture^FS";
		line+="^FO250,"+y+"^GB140,40,1^FS";
		line+="^FO280,"+(y+5)+"^FDSolde _15^FS";
		line+="^FO390,"+y+"^GB140,40,1^FS";
		line+="^FO420,"+(y+5)+"^FDRéglé _15^FS";
		line+="^FO530,"+y+"^GB140,40,1^FS";
		line+="^FO555,"+(y+5)+"^FDReste dû^FS";
		//line+="^FO50,640^GB200,40,1^FS";

		y=y+40;

		//Les lignes de encaissements
		StringBuffer sbEn=new StringBuffer();
		y=imprimeLineEncaissement(y, encaissements,sbEn);
		line+=sbEn;

		//Ligne montant regle
		line+="^CF0,30";
		line+="^FO420,"+(y+50 )+"^FDMontant réglé : ^FS";
		line+="^FO650,"+(y+50 )+"^FDIMPR_TOTAL^FS";

		//Ligne commentaire
		//		line+="^CF0,30";
		//		line+="^FO50,"+(y+90 )+"^GB570,50,1^FS";
		//		line+="^FO70,"+(y+95 )+"^FDIMPR_COMMENTAIRE^FS";

		y=y+55;
		String boutcommentaire="";
		line+="^FO60,"+(y+=35)+"^FDNote : ^FS";
		int l=0;
		do			
		{
			boutcommentaire=Fonctions.Mid(commentaire, 55*l,55);
			if (boutcommentaire.equals("")) break;
			line+="^FO60,"+(y+=30)+"^FD"+boutcommentaire+"^FS";
			l++;
		}while(1==1);
		

		//Signature agent et cachet commercial et signature
		line+="^CF0,24";
		line+="^FO10,"+(y+90 )+"^GB312,140,1^FS";
		line+="^FO10,"+(y+90 )+"^GB312,40,1^FS";
		line+="^FO60,"+(y +95)+"^FDSIGNATURE AGENT^FS";
		line+="^FO400,"+(y+90 )+"^GB400,140,1^FS";
		line+="^FO400,"+(y +90)+"^GB400,40,1^FS";
		line+="^FO410,"+(y+95 )+"^FDCACHET COMMERCIAL ET SIGNATURE^FS";

		y=y+95;
		line+="^FO10, "+(y+145 )+"^FDAucun escompte n'est accordé pour paiement comptant. ^FS";
		line+="^FO10, "+(y+175 )+"^FDPénalités de retard de paiement égales a 3 fois le taux de l'intérêt légal. ^FS";
		line+="^FO10, "+(y+205 )+"^FDIndemnité forfaitaire pour frais de recouvrement : 40. ^FS";
		line+="^FO270, "+(y+235 )+"^FDSegafredo Zanetti France S.A.S^FS";
		line+="^FO50, "+(y+265 )+"^FD14 BD industriel CS10047 - 76301 SOTTEVILLE-LES-ROUEN CEDEX^FS";
		line+="^FO140, "+(y+295 )+"^FDS.A.S AU CAPITAL DE 8 500 000 eu - R.C ROUEN 65 B 120 ^FS";
		line+="^FO300, "+(y+325 )+"^FDSIRET 650 501 208 00013^FS";
		line+="^FO150, "+(y+355 )+"^FDAPE 1083 Z - N Identification T.V.A : FR 18 650 501 208 ^FS";

		line+="^XZ";


		//COORD DU CLIENT
		TableClient cli=new TableClient(Global.dbParam.getDB());
		structClient structcli=new structClient();
		if (cli.getClient(codecli, structcli, new StringBuilder())==false)
		{
			return "";
		}

		//NOM DU COMMERCIAL
		dbSiteListeLogin login=new dbSiteListeLogin(Global.dbParam.getDB());
		structlistLogin structlogin=new structlistLogin();
		String nomPrenomCommercial=login.getLblNom(codeVRP);

		//Calcul total espece
		float totalEspece = 0;	

		//Calcul total cheque
		float totalCheque = 0;

		//Calcul total regle
		float totalRegle = 0;

		for(Encaissement enc : encaissements){
			if(enc.getTypePaiement() != null){
				if(enc.getTypePaiement().equals(Encaissement.TYPE_CHEQUE)) 
					totalCheque += Math.round(enc.getMontant()*100.0)/100.0;

				if(enc.getTypePaiement().equals(Encaissement.TYPE_ESPECE))
					totalEspece += Math.round(enc.getMontant()*100.0)/100.0;
			}
			totalRegle += enc.getMontant();
		}

		completeLine=line;
		completeLine=completeLine.replace("IMPR_DATEIMPR",Fonctions.YYYYMMDD_to_dd_mm_yyyy(Fonctions.getYYYYMMDD()));
		if(encaissements.size() > 0) 
			completeLine=completeLine.replace("IMPR_NUM_ENCAISSEMENT", encaissements.get(0).getNumeroSouche());
		completeLine=completeLine.replace("IMPR_DATEENC", Fonctions.YYYYMMDD_to_dd_mm_yyyy(encaissements.get(0).getDate()));
		completeLine=completeLine.replace("IMPR_ORIGINAL", "Original");	    
		completeLine=completeLine.replace("IMPR_NOMPRENOM", nomPrenomCommercial); 
		completeLine=completeLine.replace("IMPR_NUMCLI", codecli);  
		completeLine=completeLine.replace("IMPR_RAISOC", Fonctions.replaceSpecialsChars(structcli.ENSEIGNE));  
		completeLine=completeLine.replace("IMPR_ADR1", Fonctions.replaceSpecialsChars(structcli.ADR1));  
		completeLine=completeLine.replace("IMPR_ADR2", Fonctions.replaceSpecialsChars(structcli.ADR2));
		 completeLine=completeLine.replace("IMPR_NOM", Fonctions.replaceSpecialsChars(structcli.NOM));  
		completeLine=completeLine.replace("IMPR_CPVILLE",Fonctions.replaceSpecialsChars(structcli.CP+" "+structcli.VILLE));  
		completeLine=completeLine.replace("IMPR_SIRET", structcli.SIRET);
		completeLine=completeLine.replace("IMPR_TOTAL", Fonctions.GetFloatToStringFormatDanem(totalRegle, "0.00"));
		completeLine=completeLine.replace("IMPR_ESPECE", Fonctions.GetFloatToStringFormatDanem(totalEspece, "0.00"));
		completeLine=completeLine.replace("IMPR_CHEQUE", Fonctions.GetFloatToStringFormatDanem(totalCheque, "0.00"));
		completeLine=completeLine.replace("IMPR_COMMENTAIRE", "");


		completeLine=Z420ModelInventaire.utfToAscii(completeLine);
		return completeLine;

	}


	int imprimeLineEncaissement(int currentY,ArrayList<Encaissement> encaissements,StringBuffer sb)
	{
		//taille d'une ligne
		int LINE_HEIGHT=40;
		String line="";
		line+="^CF0,24";
		line+="^FO50,ESPACE^GB200,40,1^FS";
		line+="^FO110,SAUT^FDIMPR_NUMERO_FACTURE^FS";
		line+="^FO250,ESPACE^GB140,40,1^FS";
		line+="^FO280,SAUT^FDIMPR_SOLDE^FS";
		line+="^FO390,ESPACE^GB140,40,1^FS";
		line+="^FO420,SAUT^FDIMPR_REGLE^FS";
		line+="^FO530,ESPACE^GB140,40,1^FS";
		line+="^FO555,SAUT^FDIMPR_RESTE_DU^FS";
		//line+="^FO50,690^GB200,40,1^FS";

		String totaline="";
		int nbrline=0;
		double resteDu = 0.00;

		//on récupère les factures des encaissements
		ArrayList<Facture> factures = new ArrayList<Facture>();
		ArrayList<String> numerosFactures = new ArrayList<String>();
		ArrayList<Encaissement> acomptes = new ArrayList<Encaissement>();
		for(Encaissement enc : encaissements){
			for(String num : enc.getListNumerosFactures()){
				if(!numerosFactures.contains(num) && !num.equals(Encaissement.ENCAISSEMENT_NOFACTURE)) numerosFactures.add(num);
				if(num.equals(Encaissement.ENCAISSEMENT_NOFACTURE)) acomptes.add(enc);
			}
		}

		//on récupère les factures
		for(String numFac : numerosFactures){
			Facture fac = Facture.getFactureFromNumDoc(numFac);
			if(!fac.getTypeFacture().equals(Facture.TYPE_AVOIR)) factures.add(fac);
		}		

		for(Facture f : factures){
			String detail=line;

			//on recherche le montant réglé pour cette facture
			float montantRegle = 0;
			float resteDuFac = 0;
			for(Encaissement e : encaissements){
				if(f.getListNumerosEncaissement() != null && 
						e.getIdentifiant() != null && 
						f.getListNumerosEncaissement().contains(e.getIdentifiant())) montantRegle += e.getMontant();
			}

			if(montantRegle >= f.getMontantDu()){
				montantRegle = f.getMontantDu();
			}else {
				resteDuFac = f.getMontantDu() - montantRegle;
			}

			detail=detail.replace("IMPR_NUMERO_FACTURE",f.getNumDoc());
			detail=detail.replace("IMPR_SOLDE", Fonctions.GetFloatToStringFormatDanem(f.getMontantDu(),"0.00"));
			detail=detail.replace("IMPR_REGLE", Fonctions.GetFloatToStringFormatDanem(montantRegle, "0.00"));
			detail=detail.replace("IMPR_RESTE_DU", Fonctions.GetDoubleToStringFormatDanem(resteDuFac, "0.00"));

			detail=detail.replace("ESPACE", currentY+"");
			detail=detail.replace("SAUT", (currentY+5)+"");
			currentY+=LINE_HEIGHT;
			totaline+=detail;
			nbrline++;
		}

		//on ajoute les lignes acomptes s'il y en a 
		if(acomptes.size() > 0){

			for(Encaissement acompte : acomptes){
				String detail=line;
				detail=detail.replace("IMPR_NUMERO_FACTURE", "Acompte");
				detail=detail.replace("IMPR_SOLDE", "0.00");
				detail=detail.replace("IMPR_REGLE", Fonctions.GetFloatToStringFormatDanem(acompte.getMontant(), "0.00"));
				detail=detail.replace("IMPR_RESTE_DU", "0.00");

				detail=detail.replace("ESPACE", currentY+"");
				detail=detail.replace("SAUT", (currentY+5)+"");
				currentY+=LINE_HEIGHT;
				totaline+=detail;
				nbrline++;
			}
		}


		//		for(Encaissement enc : encaissements){
		//			String detail=line;
		//
		//			if(enc.getListNumerosFactures() != null && enc.getListNumerosFactures().size() > 0 
		//					&& !enc.getListNumerosFactures().get(0).equals(Encaissement.ENCAISSEMENT_NOFACTURE)){
		//
		//				for(String num : enc.getListNumerosFactures()){	
		//					detail=line;
		//					Facture fac = Facture.getFactureFromNumDoc(num);
		//					//calcul du reste du
		//					if(resteDu != 0.00) resteDu = Math.round(resteDu*100.0)/100.0 - Math.round(enc.getMontant()*100.0)/100.0;
		//					else resteDu = Math.round(fac.getMontantDu()*100.0)/100.0 - Math.round(enc.getMontant()*100.0)/100.0;
		//					detail=detail.replace("IMPR_NUMERO_FACTURE",num);
		//					detail=detail.replace("IMPR_SOLDE", Fonctions.GetFloatToStringFormatDanem(fac.getMontantDu(),"0.00"));
		//					detail=detail.replace("IMPR_REGLE", Fonctions.GetFloatToStringFormatDanem(enc.getMontant(), "0.00"));
		//					detail=detail.replace("IMPR_RESTE_DU", Fonctions.GetDoubleToStringFormatDanem(resteDu, "0.00"));
		//
		//					detail=detail.replace("ESPACE", currentY+"");
		//					detail=detail.replace("SAUT", (currentY+5)+"");
		//					currentY+=LINE_HEIGHT;
		//					totaline+=detail;
		//					nbrline++;
		//				}
		//
		//			}else{
		//				detail=detail.replace("IMPR_NUMERO_FACTURE","");
		//				detail=detail.replace("IMPR_SOLDE", "");
		//				detail=detail.replace("IMPR_REGLE",Fonctions.GetFloatToStringFormatDanem(enc.getMontant(),"0.00"));
		//				detail=detail.replace("IMPR_RESTE_DU", "");
		//
		//				detail=detail.replace("ESPACE", currentY+"");
		//				detail=detail.replace("SAUT", (currentY+5)+"");
		//				currentY+=LINE_HEIGHT;
		//				totaline+=detail;
		//				nbrline++;
		//			}
		//
		//		}

		sb.append(totaline);
		//currentY=currentY+(nbrline*LINE_HEIGHT);

		return currentY;
	}

	int imprimeLineCheque(int currentY,ArrayList<Cheque> cheques,StringBuffer sb)
	{
		//taille d'une ligne
		int LINE_HEIGHT=40;
		String line="";
		line+="^CF0,24";
		line+="^FO50,ESPACE^FDn^FS";
		line+="^FO110,ESPACE^FDIMPR_NUMERO_CHEQUE^FS";
		line+="^FO260,ESPACE^FDIMPR_CODEBANQUE^FS";
		line+="^FO550,ESPACE^FDdu^FS";
		line+="^FO590,ESPACE^FDIMPR_DATECHEQUE^FS";
		line+="^FO730,ESPACE^FDIMPR_CMONTANT^FS";
		//line+="^FO50,690^GB200,40,1^FS";

		String totaline="";
		int nbrline=0;
		double resteDu = 0.00;
		for(Cheque c : cheques){
			String detail=line;

			detail=detail.replace("IMPR_NUMERO_CHEQUE",c.getNumeroCheque());
			detail=detail.replace("IMPR_CODEBANQUE", Fonctions.Left(c.getLibelleBanque(),23));
			detail=detail.replace("IMPR_DATECHEQUE", Fonctions.YYYYMMDD_to_dd_mm_yyyy(c.getDateCheque()));
			detail=detail.replace("IMPR_CMONTANT", Fonctions.GetDoubleToStringFormatDanem(c.getMontant(), "0.00"));

			detail=detail.replace("ESPACE", currentY+"");
			currentY+=LINE_HEIGHT;
			totaline+=detail;
			nbrline++;


		}

		sb.append(totaline);
		//currentY=currentY+(nbrline*LINE_HEIGHT);

		return currentY;
	}

}
