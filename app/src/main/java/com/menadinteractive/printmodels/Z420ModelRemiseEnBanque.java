package com.menadinteractive.printmodels;

import java.util.ArrayList;

import android.content.Context;

import com.menadinteractive.segafredo.communs.Fonctions;
import com.menadinteractive.segafredo.communs.Global;
import com.menadinteractive.segafredo.db.TableClient;
import com.menadinteractive.segafredo.db.dbKD981RetourBanqueEnt.structRetourBanque;
import com.menadinteractive.segafredo.db.dbSiteListeLogin;
import com.menadinteractive.segafredo.db.TableClient.structClient;
import com.menadinteractive.segafredo.db.dbSiteListeLogin.structlistLogin;
import com.menadinteractive.segafredo.encaissement.Encaissement;
import com.menadinteractive.segafredo.remisebanque.Cheque;

public class Z420ModelRemiseEnBanque {
	
	Context context;

	public Z420ModelRemiseEnBanque(Context c)
	{
		context=c;
	}
	public String getRemiseEnBanque(String numRemise, ArrayList<Cheque> cheques, String codeVRP)
	{	 
		String completeLine="";

		if(numRemise == null || numRemise.equals("")) return null;
		
		structRetourBanque remiseBanque = Global.dbKDRetourBanqueEnt.getRemiseBanque(numRemise);

		String line ="";
		int taillepage=1200+40;
		if(cheques != null && cheques.size() > 0) taillepage += 40*cheques.size();
		//1500
		line+="^XA^MNN^LL"+taillepage+"^XZ^XA^JUS^XZ";
		line+="^XA";
		line+="";
		line+="^CI8";
		line+=" ^CF0,40";
		line+="^FO170,50^FDSEGAFREDO ZANETTI FRANCE^FS";
		line+="^CF0,40";
		line+="^FO10,100^FH^FD(_15) Remise en banque^FS ";
		line+="^CF0,30";
		line+="^FO500,100^FDImprimé le : ^FS";
		line+="^FO650,100^FD IMPR_DATEIMPR^FS";
		line+="^FO10,140^FDN : IMPR_NUM_REMISE ^FS";
		line+="";
		line+="^FO10,170^FDDate : IMPR_DATEREB^FS";
		line+="";
		line+="^CF0,26";
		line+="^FO10,280^FDCommercial : ^FS";
		line+="^FO170,280^FDIMPR_NOMPRENOM^FS";
		line+="^FO450,280^FDBanque :^FS";
		line+="^FO590,280^FDIMPR_BANQUE^FS";
		line+="^FO450,310^FDAgence/N :^FS";
		line+="^FO590,310^FDIMPR_NUMEROAGENCE^FS";

		//Ligne des chèques
		int y = 400;
		
		line+="^CF0,24";
		line+="^FO50,"+y+"^GB200,40,1^FS";
		line+="^FO110,"+(y+5)+"^FDN Chèque^FS";
		line+="^FO250,"+y+"^GB200,40,1^FS";
		line+="^FO280,"+(y+5)+"^FDBanque^FS";
		line+="^FO450,"+y+"^GB140,40,1^FS";
		line+="^FO480,"+(y+5)+"^FDDate^FS";
		line+="^FO590,"+y+"^GB140,40,1^FS";
		line+="^FO592,"+(y+5)+"^FDMontant en _15^FS";
		
		y=y+40;
		
		if(cheques != null && cheques.size() > 0){
			StringBuffer sbCh=new StringBuffer();
			y=imprimeLineCheque(y, cheques,sbCh);
			line+=sbCh;
		}

		y=y+40;

		//Ligne montant regle
		line+="^CF0,30";
		line+="^FO420,"+(y+50 )+"^FDTotal chèques : ^FS";
		line+="^FO650,"+(y+50 )+"^FDIMPR_CHEQUES^FS";
		
		y=y+50;
		line+="^CF0,30";
		line+="^FO420,"+(y+50 )+"^FDEspèces : ^FS";
		line+="^FO650,"+(y+50 )+"^FDIMPR_ESPECES^FS";
		
		y=y+100;
		line+="^CF0,30";
		line+="^FO420,"+(y+50 )+"^FDTotal à déposer : ^FS";
		line+="^FO650,"+(y+50 )+"^FDIMPR_DEPOSE^FS";
		y=y+50;

		//Ligne commentaire
		//		line+="^CF0,30";
		//		line+="^FO50,"+(y+90 )+"^GB570,50,1^FS";
		//		line+="^FO70,"+(y+95 )+"^FDIMPR_COMMENTAIRE^FS";

		y=y+55;

		//Signature agent et cachet commercial et signature
		line+="^CF0,24";
		line+="^FO10,"+(y+90 )+"^GB312,140,1^FS";
		line+="^FO10,"+(y+90 )+"^GB312,40,1^FS";
		line+="^FO60,"+(y +95)+"^FDSIGNATURE AGENT^FS";
	//	line+="^FO400,"+(y+90 )+"^GB400,140,1^FS";
	//	line+="^FO400,"+(y +90)+"^GB400,40,1^FS";
	//	line+="^FO410,"+(y+95 )+"^FDCACHET COMMERCIAL ET SIGNATURE^FS";

		y=y+95;

		line+="^FO270, "+(y+175 )+"^FDSegafredo Zanetti France S.A.S^FS";
		line+="^FO50, "+(y+205 )+"^FD14 BD industriel CS10047 - 76301 SOTTEVILLE-LES-ROUEN CEDEX^FS";
		line+="^FO140, "+(y+235 )+"^FDS.A.S AU CAPITAL DE 8 500 000 eu - R.C ROUEN 65 B 120 ^FS";
		line+="^FO300, "+(y+265 )+"^FDSIRET 650 501 208 00013^FS";
		line+="^FO150, "+(y+295 )+"^FDAPE 1083 Z - N Identification T.V.A : FR 18 650 501 208 ^FS";

		line+="^XZ";

		//NOM DU COMMERCIAL
		dbSiteListeLogin login=new dbSiteListeLogin(Global.dbParam.getDB());
		structlistLogin structlogin=new structlistLogin();
		String nomPrenomCommercial=login.getLblNom(codeVRP);
		
		//Récupération de la banque
		String banque = Global.dbParam.getLblAllSoc(Global.dbParam.PARAM_BANQUEREMISE, remiseBanque.CODEBANQUE);
		String agence = Global.dbParam.getComment(Global.dbParam.PARAM_BANQUEREMISE, remiseBanque.CODEBANQUE);

		float espece = Fonctions.GetStringToFloatDanem(remiseBanque.MNT_ESPECE);
		float cheque = Fonctions.GetStringToFloatDanem(remiseBanque.MNT_CHEQUE);
		float total = Fonctions.GetStringToFloatDanem(remiseBanque.MNT_TOTAL);

		completeLine=line;
		completeLine=completeLine.replace("IMPR_DATEIMPR",Fonctions.YYYYMMDD_to_dd_mm_yyyy(Fonctions.getYYYYMMDD()));
		completeLine=completeLine.replace("IMPR_NUM_REMISE", remiseBanque.NUM_SOUCHE);	
		completeLine=completeLine.replace("IMPR_DATEREB", Fonctions.YYYYMMDD_to_dd_mm_yyyy(remiseBanque.DATE)); 
		completeLine=completeLine.replace("IMPR_NOMPRENOM", nomPrenomCommercial); 	
		completeLine=completeLine.replace("IMPR_BANQUE", banque);  
		completeLine=completeLine.replace("IMPR_NUMEROAGENCE", agence);  
		completeLine=completeLine.replace("IMPR_CHEQUES", Fonctions.GetFloatToStringFormatDanem(cheque, "0.00"));  
		completeLine=completeLine.replace("IMPR_ESPECES", Fonctions.GetFloatToStringFormatDanem(espece, "0.00"));  
		completeLine=completeLine.replace("IMPR_DEPOSE",Fonctions.GetFloatToStringFormatDanem(total, "0.00"));

		completeLine=Z420ModelInventaire.utfToAscii(completeLine);
		return completeLine;

	}
	
	int imprimeLineCheque(int currentY,ArrayList<Cheque> cheques,StringBuffer sb)
	{
		//taille d'une ligne
		int LINE_HEIGHT=40;
		String line="";
		line+="^CF0,24";
		line+="^FO50,BORDER^GB200,40,1^FS";
		line+="^FO110,ESPACE^FDIMPR_NUMERO_CHEQUE^FS";
		line+="^FO250,BORDER^GB200,40,1^FS";
		line+="^FO255,ESPACE^FDIMPR_CBANQUE^FS";
		line+="^FO450,BORDER^GB140,40,1^FS";
		line+="^FO455,ESPACE^FDIMPR_DATECHEQUE^FS";
		line+="^FO590,BORDER^GB140,40,1^FS";
		line+="^FO610,ESPACE^FDIMPR_CMONTANT^FS";

		String totaline="";
		int nbrline=0;
		double resteDu = 0.00;
		for(Cheque c : cheques){
			String detail=line;

			detail=detail.replace("IMPR_NUMERO_CHEQUE",c.getNumeroCheque());
			//si la longueur est supérieur à 14 alors on coupe à 14 caracteres
			String banque = "";
			if(c.getLibelleBanque().length() > 14) banque = c.getLibelleBanque().substring(0, 13);
			else banque  = c.getLibelleBanque();
			detail=detail.replace("IMPR_CBANQUE", banque);
			detail=detail.replace("IMPR_DATECHEQUE", Fonctions.YYYYMMDD_to_dd_mm_yyyy(c.getDateCheque()));
			detail=detail.replace("IMPR_CMONTANT", Fonctions.GetDoubleToStringFormatDanem(c.getMontant(), "0.00"));

			detail=detail.replace("BORDER", (currentY)+"");
			detail=detail.replace("ESPACE", (currentY+5)+"");
			currentY+=LINE_HEIGHT;
			totaline+=detail;
			nbrline++;
		}

		sb.append(totaline);
		//currentY=currentY+(nbrline*LINE_HEIGHT);

		return currentY;
	}
}
