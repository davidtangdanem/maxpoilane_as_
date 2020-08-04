package com.menadinteractive.printmodels;

import java.util.List;

import android.content.Context;

import com.menadinteractive.segafredo.communs.Fonctions;
import com.menadinteractive.segafredo.communs.Global;
import com.menadinteractive.segafredo.db.TableClient;
import com.menadinteractive.segafredo.db.TableClient.structClient;
import com.menadinteractive.segafredo.db.dbKD83EntCde;
import com.menadinteractive.segafredo.db.dbKD83EntCde.structEntCde;
import com.menadinteractive.segafredo.db.dbKD84LinCde;
import com.menadinteractive.segafredo.db.dbKD84LinCde.structLinCde;
import com.menadinteractive.segafredo.db.dbSiteListeLogin;
import com.menadinteractive.segafredo.db.dbSiteListeLogin.structlistLogin;

public class Z420ModelFacture {

	Context context;
	boolean m_bduplicata=false;
	
	public Z420ModelFacture(Context c)
	{
		context=c;
	}
	public String getFacture(String numfact,String codecli,boolean bduplicata,String typedoc)
	{	 
		String completeLine="";
		m_bduplicata=bduplicata;
		dbKD84LinCde lin=new dbKD84LinCde(Global.dbParam.getDB());
		int nbrLine=lin.Count_Numcde(numfact, m_bduplicata);
		
		
		   //remplacementdes zones variables
	    //on va chercher les �l�ments de la facture
	    dbKD83EntCde cde=new dbKD83EntCde(Global.dbParam.getDB());
	    structEntCde structcde=new structEntCde();
	    if (cde.load(structcde, numfact, new StringBuffer(),m_bduplicata)==false)
	    {
	    	return "";
	    }
	    
	    
		
		String line ="";
		int taillepage=1500+40*nbrLine;
		//1500
		line+="^XA^MNN^LL"+taillepage+"^XZ^XA^JUS^XZ";
		line+="^XA";
		line+="";
		line+="^CI8";
		line+=" ^CF0,40";
		line+="^FO200,50^FDSEGAFREDO ZANETTI FRANCE^FS";
		line+="^CF0,40";
		line+="^FO10,100^FH^FD(_15) IMPR_TYPEDOC^FS ";
		line+="^CF0,30";
		line+="^FO500,100^FDImprimé le : ^FS";
		line+="^FO650,100^FD IMPR_DATEIMPR^FS";
		line+="^FO10,140^FDn : IMPR_NUMFACT ^FS";
		line+="";
		line+="^FO10,170^FDDate : IMPR_DATEFACT^FS";
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
		line+="^CF0,24";
		line+="^FO5,500^GB80,100,1^FS";
		line+="^FO18,510^FDCode^FS";
		line+="^FO16,550^FDarticle^FS";
		line+="";
		line+="^FO85,500^GB240,100,1^FS";
		line+="^FO150,510^FDDésignation^FS";
		line+="^FO140,550^FDNotes/Barcode^FS";
		line+="";
		line+="^FO325,500^GB40,100,1^FS";
		line+="^FO326,510^FDUnit^FS";
		line+="";
		line+="^FO365,500^GB68,100,1^FS";
		line+="^FO395,510^FDQté^FS";
		line+="^FO375,550^FDVend.^FS";
		line+="";
		line+="^FO433,500^GB84,100,1^FS";
		line+="^FO450,510^FDP.U.^FS";
		line+="^FO450,550^FDH.T.^FS";
		line+="";
		line+="^FO517,500^GB64,100,1^FS";
		line+="^FO540,510^FD%^FS";
		line+="^FO525,550^FDremis^FS";
		line+="";
		line+="^FO581,500^GB96,100,1^FS";
		line+="^FO600,510^FDTotal^FS";
		line+="^FO606,550^FDH.T.^FS";
		line+="";
		line+="^FO677,500^GB64,100,1^FS";
		line+="^FO700,550^FD%^FS";
		line+="^FO685,510^FDT.V.A.^FS";
		line+="";
		line+="^FO741,500^GB64,100,1^FS";
		
		//les lignes de facture
		StringBuffer sb=new StringBuffer();
		int y=imprimeLine(600, numfact,sb);
		line+=sb;
		
		//le tableau TVA
		line+="^CF0,26";
		line+="^FO5,"+(y)+"^GB576,40,1^FS";
		line+="^FO581,"+(y)+"^GB224,40,1^FS";
		line+="^CF0,26";
		line+="^FO406,"+(y+60)+"^GB80,100,1^FS";
		line+="^FO435,"+(y+70)+"^FD%^FS";
		line+="^FO420,"+(y+110)+"^FDT.V.A.^FS";
		line+="^FO486,"+(y+60)+"^GB96,100,1^FS";
		line+="^FO510,"+(y+70)+"^FDTotal^FS";
		line+="^FO510,"+(y+110)+"^FDT.V.A.^FS";
		line+="^FO582,"+(y+60)+"^GB112,100,1^FS";
		line+="^FO610,"+(y+70)+"^FDTotal^FS";
		line+="^FO620,"+(y+110)+"^FDH.T.^FS";
		line+="^FO694,"+(y+60)+"^GB112,100,1^FS";		       
		line+="^FO720,"+(y+70)+"^FDTotal^FS";
		line+="^FO725,"+(y+110)+"^FDT.T.C.^FS";
		y=y+110+50;
		
		
		//les lignes de TVA
		sb.setLength(0);
		y=imprimeTableauTVA(y, numfact,sb);
		line+=sb;				

		line+="^FO694,"+(y)+"^GB112,50,1^FS";
		line+="^FO720,"+(y+10)+"^FDIMPR_TOTALTTC^FS";
		line+="^FO582,"+(y )+"^GB112,50,1^FS";
		line+="^FO610,"+(y+10)+"^FDIMPR_TOTALHT^FS";
		line+="^FO486,"+(y+0)+"^GB96,50,1^FS";
		line+="^FO510,"+(y+10)+"^FDIMPR_TOTALTVA^FS";
		y=y+60;
		
		String commentaire="Note :"+structcde.COMMENTCDE;
 
		String boutcommentaire="";
		//line+="^FO60,"+(y+=50)+"^FDNote : ^FS";
		int l=0;
		do			
		{
			boutcommentaire=Fonctions.Mid(commentaire, 63*l,63);
			if (boutcommentaire.equals("")) break;
			line+="^FO60,"+(y+=30)+"^FD"+boutcommentaire+"^FS";
			l++;
		}while(1==1);
		
		
		y=y+60;
		if(m_bduplicata==true)
		{
			
		}
		else
		{
			line+="^FO0,"+(y )+"^FDConditions de paiement : ^FS";
			line+="^FO300,"+(y )+"^FDIMPR_MODEREGL^FS";
				
		}
		 line+="^FO0,"+(y +30)+"^FDDate d'échéance : ^FS";
		line+="^FO300,"+(y+30 )+"^FDIMPR_DATEECH^FS";
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
		line+="^FO10, "+(y+205 )+"^FDIndemnité forfaitaire pour frais de recouvrement : 40_15 ^FS";
		line+="^FO270, "+(y+235 )+"^FDSegafredo Zanetti France S.A.S^FS";
		line+="^FO50, "+(y+265 )+"^FD14 BD industriel CS10047 - 76301 SOTTEVILLE-LES-ROUEN CEDEX^FS";
		line+="^FO140, "+(y+295 )+"^FDS.A.S AU CAPITAL DE 8 500 000_15 - R.C ROUEN 65 B 120 ^FS";
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
	    structcde.CODEREP=login.getLblNom(structcde.CODEREP);
	    
	    String stTypeDoc="Original";
		if(m_bduplicata==true)
			stTypeDoc="Duplicata";
		
	    
	    completeLine=line;
	    completeLine=completeLine.replace("IMPR_DATEIMPR",Fonctions.YYYYMMDD_to_dd_mm_yyyy(Fonctions.getYYYYMMDD()));
	    completeLine=completeLine.replace("IMPR_NUMFACT", numfact);
	    completeLine=completeLine.replace("IMPR_DATEFACT", Fonctions.YYYYMMDD_to_dd_mm_yyyy(structcde.DATECDE));
	    completeLine=completeLine.replace("IMPR_ORIGINAL", stTypeDoc);	    
	    completeLine=completeLine.replace("IMPR_NOMPRENOM", structcde.CODEREP); 
	    completeLine=completeLine.replace("IMPR_NUMCLI", codecli);  
	    completeLine=completeLine.replace("IMPR_RAISOC", Fonctions.replaceSpecialsChars(structcli.ENSEIGNE));  
	    completeLine=completeLine.replace("IMPR_ADR1", Fonctions.replaceSpecialsChars(structcli.ADR1));  
	    completeLine=completeLine.replace("IMPR_ADR2", Fonctions.replaceSpecialsChars(structcli.ADR2));  
	    completeLine=completeLine.replace("IMPR_NOM", Fonctions.replaceSpecialsChars(structcli.NOM));  
	    completeLine=completeLine.replace("IMPR_CPVILLE",Fonctions.replaceSpecialsChars(structcli.CP+" "+structcli.VILLE));  
	    completeLine=completeLine.replace("IMPR_SIRET", structcli.SIRET);  
	    completeLine=completeLine.replace("IMPR_TOTALTTC", Fonctions.GetDoubleToStringFormatDanem(structcde.MNTTC,"0.00"));  
	    completeLine=completeLine.replace("IMPR_TOTALHT", Fonctions.GetDoubleToStringFormatDanem(structcde.MNTHT,"0.00")+"");  
	    completeLine=completeLine.replace("IMPR_TOTALTVA", Fonctions.GetDoubleToStringFormatDanem((structcde.MNTTVA1+structcde.MNTTVA2),"0.00")+"");  
	    completeLine=completeLine.replace("IMPR_DATEECH", Fonctions.YYYYMMDD_to_dd_mm_yyyy(structcde.ECHEANCE));  
	    completeLine=completeLine.replace("IMPR_MODEREGL", Global.dbParam.getLblAllSoc(Global.dbParam.PARAM_MODEREGLEMENT, structcde.R_REGL) );  
	    completeLine=completeLine.replace("IMPR_TYPEDOC", Global.dbParam.getLblAllSoc(Global.dbParam.PARAM_TYPDOC, typedoc));  
	 
	 /*   //MODE DE REGLEMENT
	    ArrayList<Bundle> bregl=new ArrayList<Bundle>();
	    if (Global.dbParam.getRecord2s(Global.dbParam.PARAM_MODEREGLEMENT, bregl, false))
	    {
	    	completeLine=completeLine.replace("IMPR_MODEREGL",  Fonctions.getBundleValue(bregl.get(0), Global.dbParam.FLD_PARAM_LBL)); 
	    	String regledecalcalule=Fonctions.getBundleValue(bregl.get(0), Global.dbParam.FLD_PARAM_COMMENT);
	    	String dateech=Global.dbParam.calcDateEcheance(structcde.DATECDE,regledecalcalule);
	    	completeLine=completeLine.replace("IMPR_DATEECH", Fonctions.YYYYMMDD_to_dd_mm_yyyy(dateech)); 
	    }
	    else
	    {
	    	completeLine=completeLine.replace("IMPR_MODEREGL", ""); 
	    	completeLine=completeLine.replace("IMPR_DATEECH", ""); 
	    }*/
	    
/*	    byte ptext[];
		try {
			ptext = completeLine.getBytes(  "ISO_8859_1");
			 completeLine = new String(ptext, "UTF_8"); 
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	*/   
	    
	    completeLine=Z420ModelInventaire.utfToAscii(completeLine);
	    return completeLine;
		 
 
	}
	

	int imprimeLine(int currentY,String numfact,StringBuffer sb)
	{
		//taille d'une ligne
		int LINE_HEIGHT=40;
		String line="";
		line+="^CF0,24";
		line+="^FO5,600^GB80,40,1^FS";
		line+="^FO10,605^FDIMPR_CODEART^FS";
		line+=" ";
		line+="^FO85,600^GB240,40,1^FS";
		line+="^FO90,605^FDIMPR_LBLART^FS";
		line+=" ";
		line+="^FO325,600^GB40,40,1^FS";
		line+="^FO330,605^FDIMPR_UNIT^FS";
		line+="";
		line+="^FO365,600^GB68,40,1^FS";
		line+="^FO385,605^FDIMPR_QTE^FS";
		line+="";
		line+="^FO433,600^GB84,40,1^FS";
		line+="^FO435,605^FDIMPR_PUHT^FS";
		line+="";

		line+="^FO517,600^GB64,40,1^FS";
		line+="^FO525,605^FDIMPR_REMISE^FS";
		line+="";
		line+="^FO581,600^GB96,40,1^FS";
		line+="^FO590,605^FDIMPR_TOTALHT^FS";
		line+="";
		line+="^FO677,600^GB64,40,1^FS";
		line+="^FO685,605^FDIMPR_TAUXTVA^FS";
		line+="";
		line+="^FO741,600^GB64,40,1^FS";
		line+="^FO747,605^FDIMPR_GRAT^FS";
		 
		
		dbKD84LinCde lin=new dbKD84LinCde(Global.dbParam.getDB());
		List<structLinCde> lines=lin.load(numfact,m_bduplicata);
		String totaline="";
		int nbrline=0;
		for (int i=0;i<lines.size();i++)
		{
			String detail=line;
			detail=detail.replace("IMPR_CODEART",Fonctions.Left(lines.get(i).PROCODE,6));
			detail=detail.replace("IMPR_LBLART",Fonctions.Left(lines.get(i).DESIGNATION,19));
			detail=detail.replace("IMPR_UNIT",Fonctions.Left(lines.get(i).UV,2));
			detail=detail.replace("IMPR_QTE", Fonctions.GetDoubleToStringFormatDanem(lines.get(i).QTECDE,"0")+"");
			detail=detail.replace("IMPR_PUHT", Fonctions.GetDoubleToStringFormatDanem(lines.get(i).MNTUNITHT,"0.00")+"");
			detail=detail.replace("IMPR_REMISE", Fonctions.GetDoubleToStringFormatDanem(lines.get(i).REMISEMODIF,"0.00")+"");
			detail=detail.replace("IMPR_TOTALHT", Fonctions.GetDoubleToStringFormatDanem(lines.get(i).MNTTOTALHT,"0.00")+"");
			detail=detail.replace("IMPR_TAUXTVA", lines.get(i).TAUX+"");
			
			String gr=lines.get(i).QTEGR+"";
			if (lines.get(i).QTEGR>0.0) 
				gr=Fonctions.GetDoubleToStringFormatDanem(Fonctions.convertToFloat(gr),"0")+"CG";
			else
				gr="";
			detail=detail.replace("IMPR_GRAT", gr);
			detail=detail.replace("600", currentY+"");
			detail=detail.replace("605", (currentY+5)+"");
			
			currentY+=LINE_HEIGHT;
			totaline+=detail;
			nbrline++;
		}
		
		sb.append(totaline);
		//currentY=currentY+(nbrline*LINE_HEIGHT);
		
		return currentY;
	}
	int imprimeTableauTVA(int currentY,String numfact,StringBuffer sb)
	{
		//taille d'une ligne
		int LINE_HEIGHT=50;
		
		String line="";
		line+="^FO694,800^GB112,50,1^FS";
		line+="^FO720,810^FDIMPR_TTC^FS";
 
		line+="^FO582,800^GB112,50,1^FS";
		line+="^FO610,810^FDIMPR_HT^FS";
 
		line+="^FO486,800^GB96,50,1^FS";
		line+="^FO490,810^FDIMPR_TVA^FS";
 
		line+="^FO406,800^GB80,50,1^FS";
		line+="^FO425,810^FDIMPR_TAUX^FS";
		 
	
		dbKD84LinCde lin=new dbKD84LinCde(Global.dbParam.getDB());
		List<structLinCde> lines=lin.loadTotauxTVA(numfact,m_bduplicata);
		String totaline="";
		int nbrline=0;
		for (int i=0;i<lines.size();i++)
		{
			String detail=line;
			detail=detail.replace("IMPR_TAUX", Fonctions.GetDoubleToStringFormatDanem(lines.get(i).TAUX,"0.00"));
			detail=detail.replace("IMPR_TVA", Fonctions.GetDoubleToStringFormatDanem(lines.get(i).MNTTVA,"0.00")+"");
			detail=detail.replace("IMPR_HT", Fonctions.GetDoubleToStringFormatDanem(lines.get(i).MNTTOTALHT,"0.00")+"");
			detail=detail.replace("IMPR_TTC", Fonctions.GetDoubleToStringFormatDanem(lines.get(i).MNTTOTALTTC,"0.00")+"");
			detail=detail.replace("800", currentY+"");
			detail=detail.replace("810", (currentY+10)+"");
			
			currentY+=LINE_HEIGHT;
			totaline+=detail;
			nbrline++;
		}
		
		sb.append(totaline);
		//currentY=currentY+(nbrline*LINE_HEIGHT);
		
		return currentY;
	}
 
 
}
