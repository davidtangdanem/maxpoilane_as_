package com.menadinteractive.commande;

import java.io.File;
import java.io.FileWriter;
import java.util.List;

import android.content.Context;
import android.net.Uri;

import com.menadinteractive.maxpoilane.ExternalStorage;
import com.menadinteractive.maxpoilane.R;
import com.menadinteractive.segafredo.communs.Fonctions;
import com.menadinteractive.segafredo.communs.Global;
import com.menadinteractive.segafredo.db.TableClient;
import com.menadinteractive.segafredo.db.dbKD83EntCde;
import com.menadinteractive.segafredo.db.dbKD84LinCde;
import com.menadinteractive.segafredo.db.dbSociete;



/**
 * genere le fichier d'impression pour printanem
 * @author marcvouaux
 *
 *DEBUT
SOC¤nom¤adr1¤adr2¤cp¤ville¤tel¤fax¤siret¤tva¤siteweb
HDR¤typecde¤numcde¤codecli¤rs¤adr1¤adr2¤cp¤ville¤tel¤fax¤mnt ht;¤mnt tva¤mnt ttc¤datecde¤datelivr
LIN¤codeart¤lbl¤qte¤pu ht¤total ht¤mention offert
LIN¤codeart¤lbl¤qte¤pu ht¤total ht¤mention offert
FIN
 */
public class printerFile {
	String numcde;
	String soccode;
	final String SOC ="SOC";
	final String HDR ="HDR";
	final String LIN="LIN";
	final String CR="\r\n";
	File _file;
	Context context;
	String filname="printanem.txt";
	String filenamePDF = "print.pdf"; 
	
	public printerFile(String numcde,String soccode,Context c)
	{
		context=c;	
		this.numcde=numcde;
		this.soccode=soccode;
	}
	
	public String createFile()
	{
		
		try {
			File file = new File(ExternalStorage.getFolderPath(ExternalStorage.ROOT_FOLDER)+filname);
			file.createNewFile();
			FileWriter filewriter = new FileWriter(file, false);
			filewriter.write(createTrame());
			filewriter.close();
			_file = file;
			return file.getAbsolutePath();
		} catch (Exception e) {
			Fonctions.FurtiveMessageBox(context, "err");
			// TODO: handle exception
		}
		return "";
		
	}
	
	
	
	 
	
	public Uri getUriFromFile(){
		Uri uri = null;
		if(_file != null) uri = Uri.fromFile(_file);
		return uri;
	}
	
	String createTrame()
	{
		String stLine="DEBUT"+CR+
				printSoc()+CR+
				printHdr()+CR+
				printLines()+
				"FIN";
		
		return stLine;
				
	}
	
	String createTicket(){
		
		dbKD83EntCde.structEntCde hdr=new dbKD83EntCde.structEntCde();
		TableClient.structClient cli=new TableClient.structClient();
		dbSociete.structSoc soc = new dbSociete.structSoc();
		
		List<dbKD84LinCde.structLinCde> lines = Global.dbKDLinCde.load(numcde,false);
		
		Global.dbKDEntCde.load(hdr, numcde, new StringBuffer(),false);
		cli=Global.dbClient.load(hdr.CODECLI);
		soc=Global.dbSoc.getSoc("0");		
		
		String typeDoc=Global.dbParam.getLblAllSoc(Global.dbParam.PARAM_TYPECDE, hdr.TYPEDOC);
		
		String s = "";
		
		s+= adresseSociete(soc);
		s+=CR;
		s+=enteteDoc(true, hdr, cli, typeDoc);
		s+=CR;
		s+=lines(lines);
		s+=CR;
		s+=PiedTotal(hdr);
		s+=CR;		
		
		s+=CR+CR+CR+CR;
		return s;
	}
	
	private String adresseSociete(dbSociete.structSoc soc){
		String s = "";
		
		/** Raison sociale*/
		s+= print(soc.FIELD_SOC_NOM);
		
		/** Adresse */
		s+= print(soc.FIELD_SOC_ADRESSE);

		/** Adresse */
		s+= print(soc.FIELD_SOC_ADRESSE2);
		
		/** Ville - CP */
		s+= print(soc.FIELD_SOC_CODEPOSTAL + " "+soc.FIELD_SOC_VILLE);

		s+= print("Tel "+soc.FIELD_SOC_TELEP);

		s+= print("Fax "+soc.FIELD_SOC_FAX);
		s+= print(soc.FIELD_SOC_SIRET);
		s+= print(soc.FIELD_SOC_TVA);
	
		return s;
	}
	
	private String coordClient(TableClient.structClient cli){
		String s = "";
		s+=print("A l'attention de");
		s+= print(cli.CODE.trim());
		s+= print(cli.NOM.trim());
		s+= print(cli.ADR1.trim() + cli.ADR2.trim());
		s+= print(cli.CP.trim()+" "+cli.VILLE.trim());
		s+= print("Tel.:"+cli.TEL1.trim());
		s+= print("Fax.:"+cli.FAX.trim());
		s+= print("--------------------------------");
	
		return s;
	}
	
	private String enteteDoc(boolean isCoordClient, dbKD83EntCde.structEntCde hdr, TableClient.structClient cli, String typedoc){
		String s = "";
		
		s+= print(typedoc);
		s+=CR;
		if (isCoordClient)
		{
			s+=coordClient(cli);
			s+=CR;
		}
		s+= print(hdr.DATECDE+ " "+hdr.CODECDE);
		s+= print("--------------------------------");
	
		return s;
	}
	
	private String lines(List<dbKD84LinCde.structLinCde> lines){
		String s = "";
		
		for (int i=0;i<lines.size();i++ )
		{
			dbKD84LinCde.structLinCde lin=lines.get(i);
			
			s+= print(lin.DESIGNATION);
			
			/*if (Fonctions.GetStringDanem(lin.MENTIONGRATUIT).equals(""))
			{*/
				s+= print(lin.PROCODE+" "+
						lin.QTECDE+" "+
						" x "+
						lin.MNTUNITNETHT+" "+
						lin.MNTTOTALHT
						);
			/*}
			else
			{
				s+= print(Fonctions.formatStringLng(lin.CODEART,9,true  )+
						Fonctions.formatStringLng(lin.QTE,4,false  )+
						" "+
						Fonctions.formatStringLng(lin.MENTIONGRATUIT,11,true  )
						);
			}*/
					
			s+= CR;
		}

		return s;
	}
	
	private String PiedTotal(dbKD83EntCde.structEntCde hdr)
	{
		String s="";
		
		s+=print( "--------------------------------");
		s+= print("TOTAL H.T." +
				hdr.MNTHT+" EU    ");
		s+= print("TOTAL T.V.A." +
				hdr.MNTTVA1+" EU    ");
		s+= print("TOTAL T.T.C." +
				hdr.MNTTC+" EU    ");
		s+=print( "________________________________");
		return s;
	}
	
	String print(String lin)
	{
		if(Fonctions.isEmptyOrNull(lin))
			return "";
		
		return lin+CR;
	}
	
	String printSoc()
	{
		dbSociete.structSoc soc=Global.dbSoc.getSoc(soccode);
		String line=SOC+m()+
				soc.FIELD_SOC_NOM+m()+
				soc.FIELD_SOC_ADRESSE+m()+
				soc.FIELD_SOC_ADRESSE2+m()+
				soc.FIELD_SOC_CODEPOSTAL+m()+
				soc.FIELD_SOC_VILLE+m()+
				soc.FIELD_SOC_TELEP+m()+
				soc.FIELD_SOC_FAX+m()+
				soc.FIELD_SOC_SIRET+m()+
				soc.FIELD_SOC_TVA+m()+
				soc.FIELD_SOC_WEB+m();
		
		return line;
	}
	
	String printHdr()
	{
		StringBuffer buf=new StringBuffer();
		StringBuilder bui=new StringBuilder();
		
		dbKD83EntCde.structEntCde hdr=new dbKD83EntCde.structEntCde();
		TableClient.structClient cli=new TableClient.structClient();
		
		
		Global.dbKDEntCde.load(hdr, numcde, buf,false);
		cli=Global.dbClient.load(hdr.CODECLI);
		
		String typeDoc=Global.dbParam.getLblAllSoc(Global.dbParam.PARAM_TYPECDE, hdr.TYPEDOC);
		String line=HDR+m()+
				typeDoc+m()+
				numcde+m()+
				hdr.CODECLI+m()+
				cli.NOM+m()+
				cli.ADR1+m()+
				cli.ADR2+m()+
				cli.CP+m()+
				cli.VILLE+m()+
				cli.TEL1+m()+
				cli.FAX+m()+
				Fonctions.GetDoubleToStringFormatDanem(hdr.MNTHT,"0.00") +m()+
				Fonctions.GetDoubleToStringFormatDanem(hdr.MNTTVA1,"0.00")+m()+
				Fonctions.GetDoubleToStringFormatDanem(hdr.MNTTC,"0.00")+m()+
				Fonctions.YYYYMMDD_to_dd_mm_yyyy(hdr.DATECDE)+m()+
				Fonctions.YYYYMMDD_to_dd_mm_yyyy(hdr.DATELIVR)+m();
		
		return line;
				
	}

	String printLines()
	{
		String totalLine="";
		List<dbKD84LinCde.structLinCde> lines=Global.dbKDLinCde.load(numcde,false);
		for (int i=0;i<lines.size();i++)
		{
			String mentionOffert="";
			dbKD84LinCde.structLinCde lin=lines.get(i);
			String line="";
			if (lin.QTECDE>0)
			{
					line=LIN+m()+
					lin.PROCODE+m()+
					lin.DESIGNATION+m()+
					//TODO attention à la gestion de la quantité
					//Fonctions.GetDoubleToStringFormatDanem( (double)(lin.QTECDE*Fonctions.convertToInt(lin.UV)),"0")  +m()+
					lin.QTECDE+m()+
					lin.PRIXMODIF+m()+
					Fonctions.GetDoubleToStringFormatDanem(lin.MNTTOTALHT ,"0.00") +m()+
					""+
					CR;
					
					totalLine+=line;
			}
			if (lin.QTEGR>0)
			{
				line=LIN+m()+
				lin.PROCODE+m()+
				lin.DESIGNATION+m()+
				Fonctions.GetDoubleToStringFormatDanem( (double)(lin.QTEGR*Fonctions.convertToInt(lin.UV)),"0")  +m()+
				//lin.QTEGR*Fonctions.convertToInt(lin.UV)+m()+
				lin.PRIXMODIF+m()+
				lin.MNTTOTALHT+m()+
				"OFFERT"+
				CR;
				
				totalLine+=line;
			}
			
			
					
		}
		
		return totalLine;
		
				
	}
	String m()
	{
		return context.getString(R.string.separateur_morpion);
	}
}
