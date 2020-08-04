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
import com.menadinteractive.segafredo.db.dbKD543LinInventaire;
import com.menadinteractive.segafredo.db.dbKD83EntCde;
import com.menadinteractive.segafredo.db.dbKD451RetourMachineclient.structRetourMarchineClient;
import com.menadinteractive.segafredo.db.dbKD543LinInventaire.structPassePlat;
import com.menadinteractive.segafredo.db.dbKD83EntCde.structEntCde;
import com.menadinteractive.segafredo.db.dbKD84LinCde;
import com.menadinteractive.segafredo.db.dbKD84LinCde.structLinCde;
import com.menadinteractive.segafredo.db.dbSiteListeLogin;
import com.menadinteractive.segafredo.db.dbSiteListeLogin.structlistLogin;
import com.menadinteractive.segafredo.plugins.Espresso;

public class Z420ModelRetourMachine {

	Context context;
	
	public Z420ModelRetourMachine(Context c)
	{
		context=c;
	}
 
	public String getRetourEx( String codecli  )
	{	 
		String completeLine="";
		
		dbKD451RetourMachineclient ret=new dbKD451RetourMachineclient(Global.dbParam.getDB());
		
		ArrayList<structRetourMarchineClient> sret=ret.load(codecli );
		int nbrline=sret.size();
		if (nbrline==0) return "";
		
		String numdoc=sret.get(0).NUMDOC;
				
		int taillepage=1000+50*(nbrline*2);//on double pour les commentaires
		
		String line ="";
 
		//1500
		line+="^XA^MNN^LL"+taillepage+"^XZ^XA^JUS^XZ";
		line+="^XA";
		line+="";
		line+="^CI8";
		line+="^CF0,25\n" + 
				"^FO240,50^FDSEGAFREDO ZANETTI FRANCE  ^FS\n" + 
				"^FO290,90^FDRETOUR DE MACHINE^FS\n" + 
				"^FO25,140^FDDate : IMPR_DATEIMPR^FS \n" + 
				"^FO450,140^FDImprimé le : IMPR_DATEHEURE^FS \n" + 
				" ^FO25,170^FDNum retour : IMPR_NUMDOC^FS\n" + 
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
				"^FO5,500^GB80,100,1^FS\n" + 
				"^FO18,510^FDCode^FS\n" + 
				"^FO18,550^FDarticle^FS\n" + 
				"\n" + 
				"^FO85,500^GB300,100,1^FS\n" + 
				"^FO150,510^FDDésignation^FS\n" + 
				"\n" + 
				"\n" + 
				"^FO385,500^GB145,100,1^FS\n" + 
				"^FO406,510^FDNum SZ^FS\n" + 
				"\n" + 
				"^FO530,500^GB275,100,1^FS\n" + 
				"^FO545,510^FDNuméro série fabriquant^FS\n" ;
		
		//les lignes de facture
		StringBuffer sb=new StringBuffer();
		int y=imprimeLine(600, codecli ,sb);
		line+=sb;
				 
		y+=50;
		line+="\n" + 
				"^CF0,24\n" + 
				"\n" + 
				"^FO10,"+y+"^GB312,140,1^FS\n" + 
				"^FO10,"+y+"^GB312,40,1^FS\n" + 
				"^FO60,"+(y+10)+"^FDSIGNATURE AGENT^FS\n" + 
				"\n" + 
				"^FO400,"+y+"^GB400,140,1^FS\n" + 
				"^FO400,"+y+"^GB400,40,1^FS\n" + 
				"^FO410,"+(y+10)+"^FDCACHET COMMERCIAL ET SIGNATURE^FS\n" + 
				"\n" + 
				"^FO270, "+(y+200)+"^FDSegafredo Zanetti France S.A.S^FS\n" + 
				"^FO100, "+(y+230)+"^FD14 BD industriel CS10047 - 76301 SOTTEVILLE-LES-ROUEN CEDEX^FS\n" + 
				"^FO140, "+(y+260)+"^FDS.A.S AU CAPITAL DE 8 500 000_15 - R.C ROUEN 65 B 120 ^FS\n" + 
				"^FO300, "+(y+290)+"^FDSIRET 650 501 208 00013^FS\n" + 
				"^FO150, "+(y+320)+"^FDAPE 1083 Z -  Identification T.V.A : FR 18 650 501 208 ^FS\n" + 
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
	    completeLine=completeLine.replace("IMPR_DATEIMPR",Fonctions.YYYYMMDD_to_dd_mm_yyyy(Fonctions.getYYYYMMDD()));
	    completeLine=completeLine.replace("IMPR_DATEHEURE",Fonctions.YYYYMMDDhhmmss_to_dd_mm_yyyy_hh_mm_ss(Fonctions.getYYYYMMDDhhmmss()));
	    completeLine=completeLine.replace("IMPR_NUMDOC",numdoc);
	    completeLine=completeLine.replace("IMPR_NOMPRENOM",   Fonctions.replaceSpecialsChars(login.getLblNom(rep))); 
	    completeLine=completeLine.replace("IMPR_NUMCLI", codecli);  
	    completeLine=completeLine.replace("IMPR_RAISOC", Fonctions.replaceSpecialsChars(structcli.ENSEIGNE));  
	    completeLine=completeLine.replace("IMPR_ADR1",Fonctions.replaceSpecialsChars( structcli.ADR1));  
	    completeLine=completeLine.replace("IMPR_ADR2",Fonctions.replaceSpecialsChars( structcli.ADR2));
	    completeLine=completeLine.replace("IMPR_NOM", Fonctions.replaceSpecialsChars(structcli.NOM));
	    completeLine=completeLine.replace("IMPR_CPVILLE",Fonctions.replaceSpecialsChars(structcli.CP+" "+structcli.VILLE));  
	    completeLine=completeLine.replace("IMPR_SIRET", structcli.SIRET);  
		
	     
	    completeLine=Z420ModelInventaire.utfToAscii(completeLine);
	    
	    Log.d("TAG", completeLine);
	    return completeLine;
		 
 
	}
	int imprimeLine(int currentY,String codecli, StringBuffer sb)
	{
		//taille d'une ligne
		int LINE_HEIGHT=50;
		String line="";
		line+="^CF0,25\n" + 
				 
				"^FO5,600^GB80,50,1^FS\n" + 
				"^FO10,610^FDIMPR_CODEART^FS\n" + 
				" \n" + 
				"\n" + 
				"^FO85,600^GB300,50,1^FS\n" + 
				"^FO100,610^FDIMPR_DESIGNATION^FS\n" + 
				"\n" + 
				"\n" + 
				"^FO385,600^GB145,50,1^FS\n" + 
				"^FO400,610^FDIMPR_NUMSZ^FS\n" + 
				"\n" + 
				"^FO530,600^GB275,50,1^FS\n" + 
				"^FO545,610^FDIMPR_NUMSERIE^FS";
		 
		
		dbKD451RetourMachineclient lin=new dbKD451RetourMachineclient(Global.dbParam.getDB());
		ArrayList<structRetourMarchineClient>  lines=lin.load(codecli);
		String totaline="";
		int nbrline=0;
		String comment="";
		String totalComment="";
		for (int i=0;i<lines.size();i++)
		{
			String detail=line;
			detail=detail.replace("IMPR_CODEART", lines.get(i).CODEART);
			detail=detail.replace("IMPR_DESIGNATION",Fonctions.Left(lines.get(i).DESIGNATION,19));
			detail=detail.replace("IMPR_NUMSZ",lines.get(i).NUMSZ);
			detail=detail.replace("IMPR_NUMSERIE", lines.get(i).NUMSERIE);
 
			comment=lines.get(i).COMMENTAIRE;
			if (comment.equals("")==false);
				totalComment+=((lines.get(i).NUMSZ+" : "+comment+"|"));

			detail=detail.replace("600", currentY+"");
			detail=detail.replace("610", (currentY+10)+"");
		 
			currentY+=LINE_HEIGHT;
			totaline+=detail;
			nbrline++;
		}
		int i=0;
		while((comment=Fonctions.GiveFld(totalComment, i++, "|", true)).equals("")==false)
		{
			if (i==1)
			{
				line="^FO10,610^FDRemarques : ^FS";
				line=line.replace("610", (currentY)+"");
				totaline+=line;
				currentY+=25;
			}
			line="^FO10,610^FD"+comment+"^FS";
			line=line.replace("610", (currentY)+"");
			 
			totaline+=line;
			currentY+=25;
		}
		sb.append(totaline);
		//currentY=currentY+(nbrline*LINE_HEIGHT);
		
		return currentY;
	}
	 
}
