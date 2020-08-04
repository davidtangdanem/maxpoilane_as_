package com.menadinteractive.segafredo.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import android.database.Cursor;
import android.os.Bundle;

import com.menadinteractive.segafredo.communs.Fonctions;
import com.menadinteractive.segafredo.helper.Taxe;

public class dbParam extends DBMain {

	public static String TABLENAME="kems_param";
	public static String INDEXNAME_CODEREC="INDEX_CODEREC";

	public final String FLD_PARAM_SOC_CODE = "soc_code";
	public final String FLD_PARAM_TABLE = "prm_table";
	public final static String FLD_PARAM_CODEREC = "prm_coderec";
	public final String FLD_PARAM_LBL = "prm_lbl";
	public final String FLD_PARAM_COMMENT = "prm_comment";
	public final String FLD_PARAM_ACTIF = "prm_actif";
	public final String FLD_PARAM_VALUE = "prm_value";
	public final String FLD_PARAM_ORDER = "prm_order";

	public final String PARAM_RAYON = "RAYON";
	public final String PARAM_TYPO = "TYPO";
	public final String PARAM_FAM1 = "FAM1";
	public final String PARAM_FAM2 = "FAM2";
	public final String PARAM_FAM3 = "FAM3";
	public final String PARAM_FAM4 = "FAM4";
	public final String PARAM_FAM5 = "FAM5";
	public final String PARAM_FAM6 = "FAM6";
	public final String PARAM_FAM7 = "FAM7";//Createur
	public final String PARAM_GAM = "GAM";
	public final String PARAM_UNIV = "UNIV";
	public final String PARAM_TYPECDE= "TYPECDE";
	public final String PARAM_CONDLIVR= "CONDLIVR";
	public final String PARAM_CLIFAM= "CLIFAM";
	public final String PARAM_JOURF= "JOURF";
	public final String PARAM_TVA= "TVA";
	public final String PARAM_OMR="DF";
	public final String PARAM_ALARME= "ALARME";
	public final String PARAM_FRAIS= "FRAIS";
	public final String PARAM_OUINON= "OUINON";
	public final String PARAM_TYPEVISITE= "TYPEVISITE";
	public final String PARAM_SOCIETE= "SOCIETE";
	public final String PARAM_COMT_TYPE= "COMTTYPE";
	public final String PARAM_MINVER= "MINVER";
	public final String PARAM_LISTVRP= "LISTVRP";

	public final String PARAM_ETATSUIV= "ETATSUIV";
	public final String PARAM_TCA= "TCA";
	public final String PARAM_TETAT= "TETAT";
	public final String PARAM_TFREQ= "TFREQ";
	public final String PARAM_TJVIS= "TJVIS";
	public final String PARAM_TPOID= "TPOID";
	public final String PARAM_TYPDOC= "TYPDOC";
	public final String PARAM_WSAUTH= "WSAUTH";
	public final String PARAM_DO_TYPE= "DO_TYPE";
	public final String PARAM_CAT_COMPTA= "CAT_COMPTA";
	public final String PARAM_CAT_TARIF= "CAT_TARIF";
	public final String PARAM_ARTGP= "ARTGP";
	public final String PARAM_ADMIN= "ADMIN";
	public final String PARAM_PAYS= "PAYS";
	public final String PARAM_CLIACTIV= "CLIACTIV";//= CODEACTIVITE de la table client
	public final String PARAM_JOURPASSAGE= "JOURPASSAGE";//= Jour de passage
	public final String PARAM_CODETOURNEE= "CODETOURNEE";//= code tournee ou zone
	public final String PARAM_LINCHOIX1= "LINCHOIX1";//Combo choix1 dans ligne article
	public final String PARAM_LINCHOIX2= "LINCHOIX2";//Combo choix2 dans ligne article
	
	public final String PARAM_TYPEACTIVITE= "TYPEACTIVITE";//Combo Type activite
	public final String PARAM_TYPEETABLISSEMENT= "TYPEETABLISSEMENT";//Combo TYPEETABLISSEMENT
	public final String PARAM_MODEREGLEMENT= "MODERGL";//dans prm_comment, la regle : N (pas fin de mois) ou E (fin de mois) / nbr de mois / nbr de jours. Exemple : E/1/10 = 30 jours fin de mois le 10  
	public final String PARAM_JOURFERMETURE= "JOURFERMETURE";//Combo JOURFERMETURE
	public final String PARAM_CODECIRCUIT= "CIRCUITLIV";//Combo 
	public final String PARAM_AGENT= "AGENT";//Combo AGENT
	public final String PARAM_GROUPEMENTCLIENT= "GROUPECLI";//Combo 
	public final String PARAM_TYPECLI= "TYPECLI";//Combo 	
	public final String PARAM_SAV= "SAV";//Combo SAV
	public final String PARAM_BANQUE = "BANQUE";//Banque
	public final String PARAM_FCTCONTACT= "FCTCONTACT";//Combo 
	public final String PARAM_BANQUEREMISE= "BANQUEREMISE";//Combo 
	public final String PARAM_LIVREUR= "LIVREUR";//Combo 
	
	
	
	public final String PARAM_PRODFAMREMISE= "PRODFAMREMISE";//Combo choix2 dans ligne article
	
	public static final String TABLE_CREATE =
			"CREATE TABLE [kems_param]("+
					" 	[soc_code] [varchar](5) NULL,"+
					"	[prm_table] [varchar](10) NULL,"+
					" 	[prm_coderec] [varchar](20) NULL,"+
					" 	[prm_lbl] [varchar](50) NULL,"+
					" 	[prm_comment] [varchar](255) NULL,"+
					" 	[prm_actif] [varchar](1) NULL,"+
					" 	[prm_value] [varchar](50) NULL,"+
					" 	[prm_order] [int] NULL"+
					" )";
	
	public static final String INDEX_CREATE_CODEREC="CREATE INDEX IF NOT EXISTS ["+INDEXNAME_CODEREC+"] "
			+ "ON ["+TABLENAME+"] (["+FLD_PARAM_CODEREC+"])";


	MyDB db;
	public dbParam(MyDB _db)
	{
		db=_db;	
	}
	public MyDB getDB()
	{
		return db;
	}
	public String getComment(String codeTable,String codeRec)
	{
		try
		{
			String query="SELECT * FROM "+
					TABLENAME+
					" WHERE "+
					this.FLD_PARAM_TABLE+
					"="+
					"'"+codeTable+"'"+	
					" and "+
					 
					this.FLD_PARAM_CODEREC+
					"="+
					"'"+codeRec+"'";

			String prm_lbl		="";


			Cursor cur=db.conn.rawQuery(query, null);
			while (cur.moveToNext())
			{

				prm_lbl		=giveFld(cur,this.FLD_PARAM_COMMENT);

				cur.close();
				return prm_lbl;

			}
		}
		catch(Exception ex)
		{
			return "";
		}

		return "";
	}
	
	public String getLblAllSoc(String codeTable,String codeRec)
	{
		try
		{
			String query="SELECT * FROM "+
					TABLENAME+
					" WHERE "+
					this.FLD_PARAM_TABLE+
					"="+
					"'"+codeTable+"'"+	

					" and "+
					this.FLD_PARAM_CODEREC+
					"="+
					"'"+codeRec+"'";

			String prm_lbl		="";


			Cursor cur=db.conn.rawQuery(query, null);
			while (cur.moveToNext())
			{

				prm_lbl		=giveFld(cur,this.FLD_PARAM_LBL);

				cur.close();
				return prm_lbl;

			}
		}
		catch(Exception ex)
		{

		}
		return "";
	}
	public String getLblAllSocReverse(String codeTable,String lbl)
	{
		try
		{
			String query="SELECT * FROM "+
					TABLENAME+
					" WHERE "+
					this.FLD_PARAM_TABLE+
					"="+
					"'"+codeTable+"'"+	

					" and "+
					this.FLD_PARAM_LBL+
					"="+
					"'"+lbl+"'";

			String prm_lbl		="";


			Cursor cur=db.conn.rawQuery(query, null);
			while (cur.moveToNext())
			{

				prm_lbl		=giveFld(cur,this.FLD_PARAM_CODEREC);

				cur.close();
				return prm_lbl;

			}
		}
		catch(Exception ex)
		{

		}
		return "";
	}
	
	/**
	 * Récupération des coderec et lbl à partir d'un code table
	 * @param codeTable
	 * @param 1 for asc, 0 for desc
	 * @return
	 */
	public LinkedHashMap<String, String> getAllRecLblFromTable(String codeTable, int order)
	{
		String Order = "";
		if(order == 0){
			Order = "DESC";
		}else if(order == 1){
			Order = "ASC";
		}else return null;
		
		LinkedHashMap<String, String> h = new LinkedHashMap<String, String>();
		try
		{
			String query="SELECT * FROM "+
					TABLENAME+
					" WHERE "+
					this.FLD_PARAM_TABLE+
					"="+
					"'"+codeTable+"'"+
					" order by "+
					this.FLD_PARAM_LBL + " "+Order;

			Cursor cur=db.conn.rawQuery(query, null);
			while (cur.moveToNext())
			{
				String code = giveFld(cur,FLD_PARAM_CODEREC);
				String lbl = giveFld(cur,this.FLD_PARAM_LBL);				
				h.put(code, lbl);
			}
			cur.close();
		}
		catch(Exception ex)
		{

		}
		return h;
	}
	
	/**
	 * Permet de récupérer le taux de taxe OMR par rapport à un code
	 * @param int code
	 * @return String
	 */
	public String getOMRFromCode(int code){
		String taxe = "";
		try
		{
			String query="SELECT * FROM "+
					TABLENAME+
					" WHERE "+
					this.FLD_PARAM_TABLE+
					"="+
					"'"+PARAM_OMR+"'"+	
					" and "+
					this.FLD_PARAM_CODEREC+
					"="+
					""+code+"";


			Cursor cur=db.conn.rawQuery(query, null);
			while (cur.moveToNext())
			{

				taxe=giveFld(cur,this.FLD_PARAM_VALUE);
				cur.close();
				return taxe;

			}
		}
		catch(Exception ex)
		{

		}
		return "";
	}
	
	/**
	 * Permet de récupérer l'objet Taxe à partir du code taxe
	 * @return Taxe
	 * @param String
	 */
	public Taxe getTaxeFromCodeTaxe(String cod_taxe){
		try
		{
			String query="SELECT * FROM "+
					TABLENAME+
					" WHERE "+
					this.FLD_PARAM_TABLE+
					"="+
					"'"+PARAM_OMR+"'"+	
					" and "+
					this.FLD_PARAM_CODEREC+
					"="+
					""+cod_taxe+"";


			Cursor cur=db.conn.rawQuery(query, null);
			Taxe taxe;
			while (cur.moveToNext())
			{
				taxe = new Taxe(cod_taxe, giveFld(cur,this.FLD_PARAM_LBL), giveFld(cur,this.FLD_PARAM_VALUE), "pourcentage");
				cur.close();
				return taxe;

			}
		}
		catch(Exception ex)
		{

		}
		return null;
	}
	
	
 

	public boolean getCommentAndValueAndLblParam(ArrayList<String> ValueExtGetEntTarif,String codeTable, String stFitre)
	{
		boolean bres=false;
		try
		{
			String query="SELECT * FROM "+
					TABLENAME+
					" WHERE "+
					this.FLD_PARAM_TABLE+
					"="+
					"'"+codeTable+"'"+	

					" and "+
					this.FLD_PARAM_CODEREC+
					"="+
					"'"+stFitre+"'";



			Cursor cur=db.conn.rawQuery(query, null);
			while (cur.moveToNext())
			{	
				ValueExtGetEntTarif.add(cur.getString(cur.getColumnIndex(this.FLD_PARAM_COMMENT)));
				ValueExtGetEntTarif.add(cur.getString(cur.getColumnIndex(this.FLD_PARAM_VALUE)));
				ValueExtGetEntTarif.add(cur.getString(cur.getColumnIndex(this.FLD_PARAM_LBL)));
				ValueExtGetEntTarif.add(cur.getString(cur.getColumnIndex(this.FLD_PARAM_CODEREC)));
				bres=true;
			}
			cur.close();
		}
		catch(Exception ex)
		{
			bres= false;
		}

		return bres;
	}


 
	public boolean Clear(StringBuilder err)
	{

		try
		{

			String query="DROP TABLE IF EXISTS "+TABLENAME;
			//db.conn.delete(TABLENAME, null, null);
			db.execSQL(query, err);


			db.execSQL(TABLE_CREATE,err);

		}
		catch(Exception ex)
		{
			err.append(ex.getMessage());
			return false;	
		}
		return true;

	}	
	/**
	 * @author Marc VOUAUX
	 * @return
	 */
	public int Count()
	{

		try
		{
			String query="select count(*) from "+TABLENAME;
			Cursor cur=db.conn.rawQuery(query, null);
			if (cur.moveToNext())
			{
				int c=cur.getInt(0);
				cur.close(); 
				return c;
			}
			cur.close();
			return 0;
		}
		catch(Exception ex)
		{
			return -1;
		}

	}	
 
	/*
	 * retourne la taille max des lbl d'une table param
	 */
	public int getMaxLenLbl(String table)
	{
		String query="select "+
				" max(length("+FLD_PARAM_LBL+")) as maxlen "+
				" from "+
				TABLENAME+
				" where "+
				FLD_PARAM_TABLE+"='"+table+"' ";

		int maxlen =0;
		Cursor cur=db.conn.rawQuery(query, null);
		if (cur.moveToNext())
		{
			maxlen	=Fonctions.convertToInt(  giveFld(cur,"maxlen"));
		}
		cur.close();


		return maxlen;
	}
	public boolean getRecordsFiltreAllSoc(String codeTable,ArrayList<Bundle> liste,String FiltreInverse)
	{
		try
		{
			/*
			 * Vidage des donn�es du spinner
			 */
			liste.clear();

			String query="SELECT * FROM "+
					TABLENAME+
					" WHERE "+
					this.FLD_PARAM_TABLE+
					"="+
					"'"+codeTable+"'"+		
					" and "+
					
					this.FLD_PARAM_ACTIF+
					"="+
					"'1'"+
					" ORDER BY "+
					this.FLD_PARAM_ORDER;

			String prm_coderec	="";
			String prm_lbl		="";
			String prm_comment	="";
			String prm_value	="";
			
		
			Cursor cur=db.conn.rawQuery(query, null);
			while (cur.moveToNext())
			{

				prm_coderec	=giveFld(cur,this.FLD_PARAM_CODEREC);
				prm_lbl		=giveFld(cur,this.FLD_PARAM_LBL);
				prm_comment	=giveFld(cur,this.FLD_PARAM_COMMENT);
				prm_value	=giveFld(cur,this.FLD_PARAM_VALUE);



				if(prm_value.equals(FiltreInverse))
				{

				}
				else
				{
					Bundle bundle = new Bundle();
					bundle.putString(FLD_PARAM_CODEREC, prm_coderec);
					bundle.putString(FLD_PARAM_LBL, prm_lbl);
					bundle.putString(FLD_PARAM_COMMENT, prm_comment);
					bundle.putString(FLD_PARAM_VALUE, prm_value);


					//String res=prm_coderec+"�"+prm_lbl+"�"+prm_comment+"�"+prm_value;

					liste.add(bundle);

				}


			}
			cur.close();
		}
		catch(Exception ex)
		{
			return false;
		}



		return true;
	}
	/**
	 * Remplir le spinner sans le code soci�t�
	 * @param codeTable
	 * @param liste
	 * @return
	 */
	public boolean getRecord2s(String codeTable,ArrayList<Bundle> liste,boolean addBlanc)
	{
		try
		{
			/*
			 * Vidage des donn�es du spinner
			 */
		
			liste.clear();

			String query="SELECT * FROM "+
					TABLENAME+
					" WHERE "+
					this.FLD_PARAM_TABLE+
					"="+
					"'"+codeTable+"'"+	
					" and "+
					this.FLD_PARAM_ACTIF+
					"="+
					"'1'"+
					" ORDER BY "+
					this.FLD_PARAM_LBL;

			String prm_coderec	="";
			String prm_lbl		="";
			String prm_comment	="";
			String prm_value	="";
			
			if (addBlanc)
			{
				Bundle bundleblanc = new Bundle();
				bundleblanc.putString(FLD_PARAM_CODEREC, prm_coderec);
				bundleblanc.putString(FLD_PARAM_LBL, prm_lbl);
				bundleblanc.putString(FLD_PARAM_COMMENT, prm_comment);
				bundleblanc.putString(FLD_PARAM_VALUE, prm_value);
				liste.add(bundleblanc);
			}

			Cursor cur=db.conn.rawQuery(query, null);
			while (cur.moveToNext())
			{

				prm_coderec	=giveFld(cur,this.FLD_PARAM_CODEREC);
				prm_lbl		=giveFld(cur,this.FLD_PARAM_LBL);
				prm_comment	=giveFld(cur,this.FLD_PARAM_COMMENT);
				prm_value	=giveFld(cur,this.FLD_PARAM_VALUE);


				Bundle bundle = new Bundle();
				bundle.putString(FLD_PARAM_CODEREC, prm_coderec);
				bundle.putString(FLD_PARAM_LBL, prm_lbl);
				bundle.putString(FLD_PARAM_COMMENT, prm_comment);
				bundle.putString(FLD_PARAM_VALUE, prm_value);
				liste.add(bundle);

			}
			
			cur.close();
		}
		catch(Exception ex)
		{
			return false;
		}


		return true;
	}
	
	public boolean getFamActives(ArrayList<Bundle> liste)
	{
		try
		{
			/*
			 * Vidage des donn�es du spinner
			 */
			liste.clear();

			String query="SELECT  prm_lbl, prm_coderec,prm_comment,prm_value FROM "+
					TABLENAME+
					" as fam "+
					" WHERE "+
					this.FLD_PARAM_TABLE+
					"="+
					"'"+PARAM_FAM1+"'"+		
					" and "+
					this.FLD_PARAM_ACTIF+"<>'0' "+
					
					" ORDER BY prm_lbl";
					 

			String prm_coderec	="";
			String prm_lbl		="";
			String prm_comment	="";
			String prm_value	="";
			
		
			Cursor cur=db.conn.rawQuery(query, null);
			while (cur.moveToNext())
			{

				prm_coderec	=giveFld(cur,this.FLD_PARAM_CODEREC);
				prm_lbl		=giveFld(cur,this.FLD_PARAM_LBL);
				prm_comment	=giveFld(cur,this.FLD_PARAM_COMMENT);
				prm_value	=giveFld(cur,this.FLD_PARAM_VALUE);
			
				Bundle bundle = new Bundle();
				bundle.putString(FLD_PARAM_CODEREC, prm_coderec);
				bundle.putString(FLD_PARAM_LBL, prm_lbl);
				bundle.putString(FLD_PARAM_COMMENT, prm_comment);
				bundle.putString(FLD_PARAM_VALUE, prm_value);

				liste.add(bundle);
			}
			cur.close();
		}
		catch(Exception ex)
		{
			return false;
		}



		return true;
	}
	public boolean getLivreurActives(ArrayList<Bundle> liste)
	{
		try
		{
			/*
			 * Vidage des donn�es du spinner
			 */
			liste.clear();

			String query="SELECT  prm_lbl, prm_coderec,prm_comment,prm_value FROM "+
					TABLENAME+
					" as fam "+
					" WHERE "+
					this.FLD_PARAM_TABLE+
					"="+
					"'"+PARAM_LIVREUR+"'"+		
					" and "+
					this.FLD_PARAM_ACTIF+"<>'0' "+
					
					" ORDER BY prm_lbl";
					 

			String prm_coderec	="";
			String prm_lbl		="";
			String prm_comment	="";
			String prm_value	="";
			
		
			Cursor cur=db.conn.rawQuery(query, null);
			while (cur.moveToNext())
			{

				prm_coderec	=giveFld(cur,this.FLD_PARAM_CODEREC);
				prm_lbl		=giveFld(cur,this.FLD_PARAM_LBL);
				prm_comment	=giveFld(cur,this.FLD_PARAM_COMMENT);
				prm_value	=giveFld(cur,this.FLD_PARAM_VALUE);
			
				Bundle bundle = new Bundle();
				bundle.putString(FLD_PARAM_CODEREC, prm_coderec);
				bundle.putString(FLD_PARAM_LBL, prm_lbl);
				bundle.putString(FLD_PARAM_COMMENT, prm_comment);
				bundle.putString(FLD_PARAM_VALUE, prm_value);

				liste.add(bundle);
			}
			cur.close();
		}
		catch(Exception ex)
		{
			return false;
		}



		return true;
	}
	public String getLblAllSoc(String codeTable,String codeRec,String  value)
	{
		try
		{
			String query="SELECT * FROM "+
					TABLENAME+
					" WHERE "+
					this.FLD_PARAM_TABLE+
					"="+
					"'"+codeTable+"'"+	
					" and "+
					this.FLD_PARAM_VALUE+
					"="+
					"'"+value+"'"+
					" and "+
					this.FLD_PARAM_CODEREC+
					"="+
					"'"+codeRec+"'";

			String prm_lbl		="";


			Cursor cur=db.conn.rawQuery(query, null);
			while (cur.moveToNext())
			{

				prm_lbl		=giveFld(cur,this.FLD_PARAM_LBL);

				cur.close();
				return prm_lbl;

			}
			cur.close();
		}
		catch(Exception ex)
		{
			
		}
		return "";
	}
	
	//dans prm_comment, la regle : 
	//N (pas fin de mois) ou E (fin de mois) / nbr de mois / nbr de jours. 
	//Exemple : E/1/10 = 30 jours fin de mois le 10
	public String calcDateEcheance(String fromdate,String regle)
	{
		boolean bfdm=false;
		String fdm=Fonctions.GiveFld(regle, 0, ";", true);
		int ndm=Fonctions.convertToInt(Fonctions.GiveFld(regle, 1 , ";", false));
		int nj=Fonctions.convertToInt(Fonctions.GiveFld(regle, 2 , ";", false));
		
		if (fdm.equals("E"))
			bfdm=true;
		
		String newdate=Fonctions.YYYYMMDD_PLUSex(fromdate, nj, ndm,bfdm);
		
		
		
		return newdate;
	}
}
