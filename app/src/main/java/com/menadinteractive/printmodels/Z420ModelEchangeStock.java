package com.menadinteractive.printmodels;

import java.util.List;

import android.content.Context;
import android.util.Log;

import com.menadinteractive.segafredo.communs.Fonctions;
import com.menadinteractive.segafredo.communs.Global;
import com.menadinteractive.segafredo.db.Preferences;
import com.menadinteractive.segafredo.db.dbKD543LinInventaire;
import com.menadinteractive.segafredo.db.dbKD543LinInventaire.structPassePlat;
import com.menadinteractive.segafredo.db.dbKD546EchangeStock;
import com.menadinteractive.segafredo.db.dbSiteListeLogin;
import com.menadinteractive.segafredo.db.dbSiteListeLogin.structlistLogin;
import com.menadinteractive.segafredo.plugins.Espresso;

public class Z420ModelEchangeStock {

	Context context;
	
	public Z420ModelEchangeStock(Context c)
	{
		context=c;
	}
	public String get( )
	{	 
		String completeLine="";
		dbKD546EchangeStock  lin=new dbKD546EchangeStock(Global.dbParam.getDB());
		int nbrLine=lin.Count(false);
	 	
		String line ="";
		int taillepage=600+30*nbrLine;
		//1500
		line+="^XA^MNN^LL"+taillepage+"^XZ^XA^JUS^XZ";
		line+="^XA";
		line+="";
		line+="^CI8";
		line+=" ^CF0,22";
		line+="^FO5,50^FDTRANSFERT DE STOCK  ^FS";
		line+="^FO5,75^FDMagasin cédant : IMPR_DEPOTINIT - IMPR_NOMDEPOTINIT^FS";
		line+="^FO5,100^FDMagasin recevant : IMPR_DEPOTCIBLE - IMPR_NOMDEPOTCIBLE^FS";
		line+="^FO5,125^FDDate du transfert : IMPR_DATE^FS";
		line+="^FO5,150^FDDate et heure d'impression du document : IMPR_DATHEURE^FS";
 
		line+="^FO5,175^FDRef DANEM : IMPR_NUMDOC^FS";
		line+="^FO5,225^GB815,2^FS";
	 //
		line+="^FO5,235^FD#^FS";
		line+="^FO50,235^FDCode^FS";
		line+="^FO200,235^FDLibellé^FS";
		line+="^FO630,235^FDUnité^FS";
		line+="^FO720,235^FDQté Transf.^FS";
		line+="";
		line+=" ^CF0,20";
		
		//les lignes 
		StringBuffer sb=new StringBuffer();
		int y=imprimeLine(270,sb);
		line+=sb;
		
		y+=50;
		line+="^FO5,"+y+"^FDNote:^FS";
		y+=40;
		line+="^FO5,"+y+"^FDIMPR_COMMENT^FS";
		
		line+="^CF0,20" ;

		y+=50;
		line+="^FO10,"+y+"^GB340,140,1^FS";
		line+="^FO10,"+y+"^GB340,40,1^FS" ;
		line+="^FO60,"+(y+5)+"^FDSIGNATURE AGENT CEDANT^FS" ; 
				
		line+="^FO400,"+y+"^GB340,140,1^FS"; 
		line+="^FO400,"+y+"^GB340,40,1^FS"; 
		line+="^FO410,"+(y+5)+"^FDSIGN. AGENT RECEVANT^FS";
		
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
	    completeLine=completeLine.replace("IMPR_NOMDEPOTINIT", login.getLblNom(rep));
	    
	    
	    String isagent="";
	    com.menadinteractive.segafredo.db.dbKD546EchangeStock.structPassePlat pp= lin.loadHdr();
	     
	    login.getLblNom(pp.FIELD_RECEVANT);
	    completeLine=completeLine.replace("IMPR_DEPOTINIT", rep);
	    completeLine=completeLine.replace("IMPR_NOMDEPOTINIT", login.getLblNom(rep));
	    
	    completeLine=completeLine.replace("IMPR_DEPOTCIBLE", pp.FIELD_RECEVANT);
	    completeLine=completeLine.replace("IMPR_NOMDEPOTCIBLE", login.getLblNom(pp.FIELD_RECEVANT));		 
	    completeLine=completeLine.replace("IMPR_NUMDOC", pp.FIELD_NUMDOC);	
	    completeLine=completeLine.replace("IMPR_COMMENT", pp.FIELD_COMMENT1);	
	    
	    completeLine=Z420ModelInventaire.utfToAscii(completeLine);
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
		 
		
		dbKD546EchangeStock  lin=new dbKD546EchangeStock(Global.dbParam.getDB());
		List<com.menadinteractive.segafredo.db.dbKD546EchangeStock.structPassePlat> lines=lin.load();
		String totaline="";
		int nbrline=0;
		for (int i=0;i<lines.size();i++)
		{
			String detail=line;
			detail=detail.replace("#",(i+1)+"");
			detail=detail.replace("Code", lines.get(i).FIELD_PROCODE);
			detail=detail.replace("Libelle",  Fonctions.Left(lines.get(i).FIELD_DESIGNATION,48) +"");
			detail=detail.replace("Unite",  lines.get(i).FIELD_UV);
			detail=detail.replace("Qte",  lines.get(i).FIELD_QTE);
		 	
			 
 
			detail=detail.replace("160", currentY+"");
 
			
			currentY+=LINE_HEIGHT;
			totaline+=detail;
			nbrline++;
		}
		
		sb.append(totaline);
		//currentY=currentY+(nbrline*LINE_HEIGHT);
		
		return currentY;
	}
 
	 
}
