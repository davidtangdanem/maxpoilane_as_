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
import com.menadinteractive.segafredo.db.dbKD451RetourMachineclient;
import com.menadinteractive.segafredo.db.dbKD543LinInventaire;
import com.menadinteractive.segafredo.db.dbKD543LinInventaire.structPassePlat;
import com.menadinteractive.segafredo.db.dbKD83EntCde;
import com.menadinteractive.segafredo.db.dbKD451RetourMachineclient.structRetourMarchineClient;
import com.menadinteractive.segafredo.db.dbKD83EntCde.structEntCde;
import com.menadinteractive.segafredo.db.dbKD84LinCde;
import com.menadinteractive.segafredo.db.dbKD84LinCde.structLinCde;
import com.menadinteractive.segafredo.db.dbSiteListeLogin;
import com.menadinteractive.segafredo.db.dbSiteListeLogin.structlistLogin;
import com.menadinteractive.segafredo.plugins.Espresso;

public class Z420ModelInventaire {

	Context context;
	
	public Z420ModelInventaire(Context c)
	{
		context=c;
	}
	public String get( )
	{	 
		String completeLine="";
		dbKD543LinInventaire lin=new dbKD543LinInventaire(Global.dbParam.getDB());
		int nbrLine=lin.Count(false);
	 	
		String line ="";
		int taillepage=600+30*nbrLine;
		//1500
		line+="^XA^MNN^LL"+taillepage+"^XZ^XA^JUS^XZ";
		line+="^XA";
		line+="";
		line+="^CI8";
		line+=" ^CF0,22";
		line+="^FO5,50^FDINVENTAIRE DEPOT:  IMPR_DEPOTINIT - IMPR_NOMDEPOT^FS";
		line+="^FO5,75^FDDate de l'inventaire : IMPR_DATE^FS";
		line+="^FO5,100^FDDate et heure d'impression du document : IMPR_DATHEURE^FS";
		line+="^FO5,125^FDEffectué en présence agent général : IMPR_AGENT^FS";
		line+="^FO5,150^FDRef DANEM : IMPR_NUMDOC^FS";
		line+="^FO5,190^GB815,2^FS";
	 //
		line+="^FO5,210^FD#^FS";
		line+="^FO50,210^FDCode^FS";
		line+="^FO200,210^FDLibellé^FS";
		line+="^FO630,210^FDUnité^FS";
		line+="^FO720,210^FDQté cptée^FS";
		
		line+="";
		line+=" ^CF0,20";
		
		//les lignes 
		StringBuffer sb=new StringBuffer();
		int y=imprimeLine(260,sb);
		line+=sb;
		
		y+=50;
		line+="^FO5,"+y+"^FDNote:^FS";
		y+=40;
		line+="^FO5,"+y+"^FDIMPR_COMMENT^FS";
		
		line+="^CF0,30" ;

		y+=50;
		line+="^FO10,"+y+"^GB340,140,1^FS";
		line+="^FO10,"+y+"^GB340,40,1^FS" ;
		line+="^FO60,"+(y+5)+"^FDSIGNATURE AGENT^FS" ; 
				
		line+="^FO400,"+y+"^GB340,140,1^FS"; 
		line+="^FO400,"+y+"^GB340,40,1^FS"; 
		line+="^FO410,"+(y+5)+"^FDSIGN. AGENT GENERAL^FS";
		
		line+="^XZ";
		
		
		
		String rep = Preferences.getValue(context, Espresso.LOGIN, "0");
	    //NOM DU COMMERCIAL
	    dbSiteListeLogin login=new dbSiteListeLogin(Global.dbParam.getDB());
	    structlistLogin structlogin=new structlistLogin();
	    login.getLblNom(rep);
	    
	  
	    
	    completeLine=line;
	    completeLine=completeLine.replace("IMPR_DATE",Fonctions.YYYYMMDD_to_dd_mm_yyyy(Fonctions.getYYYYMMDD()));
	    completeLine=completeLine.replace("IMPR_DATHEURE",Fonctions.YYYYMMDDhhmmss_to_dd_mm_yyyy_hh_mm_ss(Fonctions.getYYYYMMDDhhmmss()));
	    completeLine=completeLine.replace("IMPR_DEPOTINIT", rep);
	    completeLine=completeLine.replace("IMPR_NOMDEPOT", login.getLblNom(rep));
	    
	    
	    String isagent="";
	    structPassePlat pp= lin.loadHdr();
	    if (Fonctions.convertToBool(pp.FIELD_LIGNEINV_DUO))
	    	isagent="Oui";
	    else
	    	isagent="Non";
	    
	    completeLine=completeLine.replace("IMPR_AGENT", isagent);		 
	    completeLine=completeLine.replace("IMPR_NUMDOC", pp.FIELD_LIGNEINV_NUMDOC);	
	    completeLine=completeLine.replace("IMPR_COMMENT", pp.FIELD_LIGNEINV_COMMENT1);	
	    
	    completeLine=utfToAscii(completeLine);
	    Log.d("TAG",completeLine);
	    return completeLine;
		 
 
	}
	

	int imprimeLine(int currentY, StringBuffer sb)
	{
		//taille d'une ligne
		int LINE_HEIGHT=30;
		String line="";
		line+="^FO5,160^FD#^FS";
		line+="^FO50,160^FDCode^FS";
		line+="^FO200,160^FDLibelle^FS";
		line+="^FO620,160^FDUnite^FS";
		line+="^FO730,160^FDQte^FS";
		 
		
		dbKD543LinInventaire lin=new dbKD543LinInventaire(Global.dbParam.getDB());
		List<structPassePlat> lines=lin.load();
		String totaline="";
		int nbrline=0;
		for (int i=0;i<lines.size();i++)
		{
			String detail=line;
			detail=detail.replace("#",(i+1)+"");
			detail=detail.replace("Code", lines.get(i).FIELD_LIGNEINV_PROCODE);
			detail=detail.replace("Libelle",  Fonctions.Left(lines.get(i).FIELD_LIGNEINV_DESIGNATION,48) +"");
			detail=detail.replace("Unite",  lines.get(i).FIELD_LIGNEINV_UV);
			detail=detail.replace("Qte",  lines.get(i).FIELD_LIGNEINV_QTE);
		 	
			 
 
			detail=detail.replace("160", currentY+"");
 
			
			currentY+=LINE_HEIGHT;
			totaline+=detail;
			nbrline++;
		}
		
		sb.append(totaline);
		//currentY=currentY+(nbrline*LINE_HEIGHT);
		
		return currentY;
	}
 
	//doc ZPL page  156
	static public String utfToAscii(String chaine)
	{
		chaine=chaine.replace("é", "_7B");
		chaine=chaine.replace("è", "_7D");
		chaine=chaine.replace("ê", "_5D");
		chaine=chaine.replace("à", "_40");
		chaine=chaine.replace("ô", "_60");
		chaine=chaine.replace("â", "_5B");
		chaine=chaine.replace("ç", "_5C");
		chaine=chaine.replace("î", "_5E");
		chaine=chaine.replace("ù", "_7C");
		chaine=chaine.replace("û", "_7E");
		chaine=chaine.replace("^FS", "^FS\n");
		chaine=chaine.replace("^FD", "^FH^FD");
		return chaine;
		
	}
}
