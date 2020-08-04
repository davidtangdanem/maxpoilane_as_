package com.menadinteractive.printmodels;

import java.util.ArrayList;

import android.content.Context;

import com.menadinteractive.segafredo.communs.Fonctions;
import com.menadinteractive.segafredo.communs.Global;
import com.menadinteractive.segafredo.db.TableClient;
import com.menadinteractive.segafredo.db.TableClient.structClient;
import com.menadinteractive.segafredo.db.dbSiteListeLogin;
import com.menadinteractive.segafredo.db.dbSiteListeLogin.structlistLogin;
import com.menadinteractive.segafredo.encaissement.Facture;

public class Z420ModelReleveFacture {
	Context context;
	float totalSolde = 0;

	public Z420ModelReleveFacture(Context c)
	{
		context=c;
	}
	public String getReleveFacture(ArrayList<Facture> factures, String codecli, String codeVRP)
	{	 
		String completeLine="";

		if(factures == null) return null;

		String line ="";
		int taillepage=700+40*factures.size();
		//1500
		line+="^XA^MNN^LL"+taillepage+"^XZ^XA^JUS^XZ";
		line+="^XA";
		line+="";
		line+="^CI8";
		line+=" ^CF0,40";
		line+="^FO170,50^FDSEGAFREDO ZANETTI FRANCE^FS";
		line+="^CF0,40";
		line+="^FO10,100^FH^FDRELEVE DE FACTURES (_15)^FS ";
		line+="^CF0,30";
		line+="^FO10,140^FDDate : IMPR_DATEIMPR ^FS";
		line+="";
		line+="^FO10,270^FDIMPR_HEUREIMPR^FS";
		line+="";
		line+="^FO680,170^FDIMPR_ORIGINAL^FS ";
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
		
		int y = 500;
		
		line+="^CF0,24";
		line+="^FO25,"+y+"^GB115,40,1^FS";
		line+="^FO31,"+(y+5)+"^FDDate doc^FS";
		line+="^FO140,"+y+"^GB150,40,1^FS";
		line+="^FO145,"+(y+5)+"^FDDescription^FS";
		line+="^FO290,"+y+"^GB150,40,1^FS";
		line+="^FO295,"+(y+5)+"^FDN doc^FS";
		line+="^FO440,"+y+"^GB115,40,1^FS";
		line+="^FO441,"+(y+5)+"^FDEchéance^FS";
		line+="^FO555,"+y+"^GB95,40,1^FS";
		line+="^FO560,"+(y+5)+"^FDMnt (_15)^FS";
		line+="^FO650,"+y+"^GB95,40,1^FS";
		line+="^FO655,"+(y+5)+"^FDSolde (_15)^FS";
		
		y=y+40;

		//Ligne des factures
		StringBuffer sbCh=new StringBuffer();
		y=imprimeLineReleveFacture(y, factures,sbCh);
		line+=sbCh;


		//Ligne solde client
		line+="^CF0,24";
		line+="^FO480,"+(y+5)+"^FDSolde client^FS";
		line+="^FO650,"+y+"^GB95,40,1^FS";
		line+="^FO655,"+(y+5)+"^FDIMPR_TOTAL^FS";

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

		completeLine=line;
		completeLine=completeLine.replace("IMPR_DATEIMPR",Fonctions.YYYYMMDD_to_dd_mm_yyyy(Fonctions.getYYYYMMDD())+"  "+Fonctions.gethh_mm());
		completeLine=completeLine.replace("IMPR_HEUREIMPR","");
		completeLine=completeLine.replace("IMPR_ORIGINAL", "Original");	    
		completeLine=completeLine.replace("IMPR_NOMPRENOM", Fonctions.replaceSpecialsChars(nomPrenomCommercial)); 
		completeLine=completeLine.replace("IMPR_NUMCLI", codecli);  
		completeLine=completeLine.replace("IMPR_RAISOC",Fonctions.replaceSpecialsChars( structcli.ENSEIGNE));  
		completeLine=completeLine.replace("IMPR_ADR1", Fonctions.replaceSpecialsChars(structcli.ADR1));  
		completeLine=completeLine.replace("IMPR_ADR2", Fonctions.replaceSpecialsChars(structcli.ADR2)); 
		completeLine=completeLine.replace("IMPR_NOM",Fonctions.replaceSpecialsChars( structcli.NOM)); 
		completeLine=completeLine.replace("IMPR_CPVILLE",Fonctions.replaceSpecialsChars(structcli.CP+" "+structcli.VILLE));  
		completeLine=completeLine.replace("IMPR_SIRET", structcli.SIRET);
		completeLine=completeLine.replace("IMPR_TOTAL", Fonctions.GetFloatToStringFormatDanem(totalSolde, "0.00"));

		completeLine=Z420ModelInventaire.utfToAscii(completeLine);
		return completeLine;

	}

	int imprimeLineReleveFacture(int currentY,ArrayList<Facture> factures,StringBuffer sb)
	{
		//taille d'une ligne
		int LINE_HEIGHT=40;
		String line="";
		line+="^CF0,24";
		line+="^FO25,ESPACE^GB115,40,1^FS";
		line+="^FO26,SAUT^FDIMPR_DOCD^FS";
		line+="^FO140,ESPACE^GB150,40,1^FS";
		line+="^FO145,SAUT^FDIMPR_DESCRIPTION^FS";
		line+="^FO290,ESPACE^GB150,40,1^FS";
		line+="^FO295,SAUT^FDIMPR_NUMERODOC^FS";
		line+="^FO440,ESPACE^GB115,40,1^FS";
		line+="^FO441,SAUT^FDIMPR_ECHEANCE^FS";
		line+="^FO555,ESPACE^GB95,40,1^FS";
		line+="^FO560,SAUT^FDIMPR_MONTANT^FS";
		line+="^FO650,ESPACE^GB95,40,1^FS";
		line+="^FO655,SAUT^FDIMPR_SOLDE^FS";

		String totaline="";
		int nbrline=0;
		double resteDu = 0.00;
		for(Facture f : factures){
			String detail=line;
			
			//on récupère le type du document
			String description = Global.dbParam.getLblAllSoc(Global.dbParam.PARAM_TYPDOC, f.getTypeFacture());

			detail=detail.replace("IMPR_DOCD",f.getDateFacture());
			detail=detail.replace("IMPR_DESCRIPTION", description);
			detail=detail.replace("IMPR_NUMERODOC", f.getNumDoc());
			detail=detail.replace("IMPR_ECHEANCE", f.getDateEcheance());
			detail=detail.replace("IMPR_MONTANT", Fonctions.GetDoubleToStringFormatDanem(f.getMontantTTC(), "0.00"));
			float montantFacture = 0;
			if(f.getTypeFacture().equals(Facture.TYPE_AVOIR)) montantFacture = -f.getMontantDu();
			else montantFacture = f.getMontantDu();
			detail=detail.replace("IMPR_SOLDE", Fonctions.GetDoubleToStringFormatDanem(montantFacture, "0.00"));
			
			//calcul du total solde
			if(f.getTypeFacture().equals(Facture.TYPE_AVOIR)) totalSolde -= f.getMontantDu();
			else totalSolde += f.getMontantDu();
			

			detail=detail.replace("ESPACE", currentY+"");
			detail=detail.replace("SAUT", (currentY+5)+"");
			currentY+=LINE_HEIGHT;
			totaline+=detail;
			nbrline++;


		}

		sb.append(totaline);
		//currentY=currentY+(nbrline*LINE_HEIGHT);

		return currentY;
	}

}
