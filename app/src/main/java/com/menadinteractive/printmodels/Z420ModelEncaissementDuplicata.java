package com.menadinteractive.printmodels;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.menadinteractive.maxpoilane.BaseActivity;
import com.menadinteractive.segafredo.communs.Fonctions;
import com.menadinteractive.segafredo.communs.Global;
import com.menadinteractive.segafredo.db.TableClient;
import com.menadinteractive.segafredo.db.TableClient.structClient;
import com.menadinteractive.segafredo.db.Preferences;
import com.menadinteractive.segafredo.db.TableSouches;
import com.menadinteractive.segafredo.db.dbKD451RetourMachineclient;
import com.menadinteractive.segafredo.db.dbKD452ComptageMachineclient;
import com.menadinteractive.segafredo.db.dbKD452ComptageMachineclient.structComptageMachineClient;
import com.menadinteractive.segafredo.db.dbKD543LinInventaire;
import com.menadinteractive.segafredo.db.dbKD83EntCde;
import com.menadinteractive.segafredo.db.dbKD451RetourMachineclient.structRetourMarchineClient;
import com.menadinteractive.segafredo.db.dbKD543LinInventaire.structPassePlat;
import com.menadinteractive.segafredo.db.dbKD729HistoDocuments;
import com.menadinteractive.segafredo.db.dbKD83EntCde.structEntCde;
import com.menadinteractive.segafredo.db.dbKD84LinCde;
import com.menadinteractive.segafredo.db.dbKD84LinCde.structLinCde;
import com.menadinteractive.segafredo.db.dbSiteListeLogin;
import com.menadinteractive.segafredo.db.dbSiteListeLogin.structlistLogin;
import com.menadinteractive.segafredo.plugins.Espresso;

public class Z420ModelEncaissementDuplicata {

	Context context;
	
	public Z420ModelEncaissementDuplicata(Context c)
	{
		context=c;
	}
 
	public String get( String codecli,String numsouche  )
	{	 
		String completeLine="";
		
		dbKD729HistoDocuments ret=new dbKD729HistoDocuments(Global.dbParam.getDB());
		ArrayList<Bundle> sret=ret.getReglementFromSouche(numsouche);
		 
		int nbrline=sret.size();
		if (nbrline==0) return "";
		
		String numdoc=sret.get(0).getString(dbKD729HistoDocuments.FIELD_NUMDOC);
				
		int taillepage=860; 
		
		String line ="";
 
		//1500
		line+="^XA^MNN^LL"+taillepage+"^XZ^XA^JUS^XZ";
		line+="^XA";
		line+="";
		line+="^CI8";
		line+="^CF0,25\n" + 
				"^FO240,50^FDSEGAFREDO ZANETTI FRANCE  ^FS\n" + 
				"^FO210,90^FDDUPLICATA D'ENCAISSEMENT^FS\n" + 
				"^FO25,140^FDDate : IMPR_DATEIMPR^FS \n" + 
				"^FO450,140^FDImprimé le : IMPR_DATEHEURE^FS \n" + 
				" ^FO25,170^FDNum : IMPR_NUMDOC^FS\n" + 
				"\n" + 
				"^FO25,200^FDCommercial : ^FS\n" + 
				"^FO180,200^FDIMPR_NOMPRENOM^FS\n" + 
				"^FO450,200^FDN Client :^FS\n" + 
				"^FO550,200^FDIMPR_NUMCLI^FS\n" + 
				"^FO450,230^FDIMPR_RAISOC^FS\n" + 
				"^FO450,260^FDIMPR_NOM^FS\n" + 
				"^FO450,290^FDIMPR_ADR1^FS\n" + 
				"^FO450,320^FDIMPR_CPVILLE^FS\n" + 
				"^FO450,370^FDSiret : IMPR_SIRET^FS\n" + 
				"\n" ;
		
		line+="^CF0,25\n" + 
				 
				"^FO148,450^FDEn espèce : IMPR_MNTESPECE_15^FS\n" + 
	 
				"\n" + 
				 
				"^FO148,500^FDEn chèque : IMPR_MNTCHEQUE_15^FS\n" + 
				"\n" + 
				"\n" + 
			 
				"^FO148,550^FDTotal encaissé : IMPR_MNTTOTAL_15^FS\n" + 
				"\n" ;
			  
				 
		
	 
		int y=620;
	 
		line+="\n" + 
				 
				"^FO270, "+(y )+"^FDSegafredo Zanetti France S.A.S^FS\n" + 
				"^FO100, "+(y+30)+"^FD14 BD industriel CS10047 - 76301 SOTTEVILLE-LES-ROUEN CEDEX^FS\n" + 
				"^FO140, "+(y+60)+"^FDS.A.S AU CAPITAL DE 8 500 000_15 - R.C ROUEN 65 B 120 ^FS\n" + 
				"^FO300, "+(y+90)+"^FDSIRET 650 501 208 00013^FS\n" + 
				"^FO150, "+(y+120)+"^FDAPE 1083 Z -  Identification T.V.A : FR 18 650 501 208 ^FS\n" + 
				"\n" + 
				"^XZ";

		
	    
	    
	    //COORD DU CLIENT
	    TableClient cli=new TableClient(Global.dbParam.getDB());
	    structClient structcli=new structClient();
	    if (cli.getClient(codecli, structcli, new StringBuilder())==false)
	    {
	    	return "";
	    }
	    String rep = Preferences.getValue(context, Espresso.LOGIN, "0");
		    
	    //NOM DU COMMERCIAL
	    dbSiteListeLogin login=new dbSiteListeLogin(Global.dbParam.getDB());
	    structlistLogin structlogin=new structlistLogin();
	    login.getLblNom(rep);
	    
	  
	    completeLine=line;
	    completeLine=completeLine.replace("IMPR_DATEIMPR",sret.get(0).getString(dbKD729HistoDocuments.FIELD_DATEDOC) );
	    completeLine=completeLine.replace("IMPR_MNTTOTAL",  Fonctions.GetDoubleToStringFormatDanem(Fonctions.convertToFloat(sret.get(0).getString(dbKD729HistoDocuments.FIELD_ENC_MNTOTAL) ),"0.00"));
	    completeLine=completeLine.replace("IMPR_MNTCHEQUE", Fonctions.GetDoubleToStringFormatDanem(Fonctions.convertToFloat(sret.get(0).getString(dbKD729HistoDocuments.FIELD_ENC_MNTCHQ ) ),"0.00"));		
	    completeLine=completeLine.replace("IMPR_MNTESPECE", Fonctions.GetDoubleToStringFormatDanem(Fonctions.convertToFloat(sret.get(0).getString(dbKD729HistoDocuments.FIELD_ENC_MNTESP) ),"0.00"));
		
	    
	    completeLine=completeLine.replace("IMPR_DATEHEURE",Fonctions.YYYYMMDDhhmmss_to_dd_mm_yyyy_hh_mm_ss(Fonctions.getYYYYMMDDhhmmss()));
	    completeLine=completeLine.replace("IMPR_NUMDOC",numdoc);
	    completeLine=completeLine.replace("IMPR_NOMPRENOM",  Fonctions.replaceSpecialsChars( login.getLblNom(rep))); 
	    completeLine=completeLine.replace("IMPR_NUMCLI", codecli);  
	    completeLine=completeLine.replace("IMPR_RAISOC",Fonctions.replaceSpecialsChars( structcli.ENSEIGNE));  
	    completeLine=completeLine.replace("IMPR_ADR1", Fonctions.replaceSpecialsChars(structcli.ADR1));  
	    completeLine=completeLine.replace("IMPR_ADR2",Fonctions.replaceSpecialsChars( structcli.ADR2)); 
	    completeLine=completeLine.replace("IMPR_NOM", Fonctions.replaceSpecialsChars(structcli.NOM)); 
	    completeLine=completeLine.replace("IMPR_CPVILLE",Fonctions.replaceSpecialsChars(structcli.CP+" "+structcli.VILLE));  
	    completeLine=completeLine.replace("IMPR_SIRET", structcli.SIRET);  
		
	     
	    completeLine=Z420ModelInventaire.utfToAscii(completeLine);
	    
	    Log.d("TAG", completeLine);
	    return completeLine;
		 
 
	}
	 
	 
}
