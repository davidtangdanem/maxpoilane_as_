package com.menadinteractive.segafredo.db;

import java.util.ArrayList;

import com.menadinteractive.segafredo.communs.Global;
import com.menadinteractive.segafredo.helper.Taxe;

import android.database.Cursor;
import android.util.Log;


public class TableTarif extends DBMain{
	static public String TABLENAME="TARIF";
	public static final String INDEXNAME="TAR1";

	public static final String FIELD_COD_CLI		= "COD_CLI";
	public static final String FIELD_COD_PRO		= "COD_PRO";
	public static final String FIELD_TX_TVA			= "TX_TVA";
	public static final String FIELD_PX_REF 		= "PX_REF";
	public static final String FIELD_PX_NET 		= "PX_NET";
	public static final String FIELD_PX_REF_P 		= "PX_REF_P";
	public static final String FIELD_PX_PROMO 		= "PX_PROMO";
	public static final String FIELD_TYP_PXN 		= "TYP_PXN";
	public static final String FIELD_TYP_PXP 		= "TYP_PXP";
	public static final String FIELD_C_PROMO 		= "C_PROMO";
	public static final String FIELD_FORCE			= "FORCE"; 
	public static final String FIELD_REM1			= "REM1";
	public static final String FIELD_REM2 			= "REM2";
	public static final String FIELD_REM3			= "REM3"; 
	public static final String FIELD_REM4 			= "REM4";
	public static final String FIELD_REM5 			= "REM5";
	public static final String FIELD_REM6			= "REM6";
	public static final String FIELD_COD_TAX1 		= "COD_TAX1";
	public static final String FIELD_COD_TAX2 		= "COD_TAX2";
	public static final String FIELD_COD_TAX3 		= "COD_TAX3";
	public static final String FIELD_CODREP 		= "CODREP";

	public static String getFullFieldName(String field){
		return TABLENAME+"."+field;
	}


	public static final String TABLE_CREATE="CREATE TABLE ["+TABLENAME+"] ("+
			" ["+FIELD_COD_CLI			+"] [varchar](50) NULL" 	+ COMMA +
			" ["+FIELD_COD_PRO		+"] [varchar](50) NULL" 	+ COMMA +
			" ["+FIELD_CODREP		+"] [varchar](50) NULL" 	+ COMMA +
			" ["+FIELD_TX_TVA		+"] [decimal](18,0) NULL" 	+ COMMA +
			" ["+FIELD_PX_REF			+"] [decimal](18,0) NULL" 	+ COMMA +
			" ["+FIELD_PX_NET			+"] [decimal](18,0) NULL" 	+ COMMA +
			" ["+FIELD_PX_REF_P			+"] [decimal](18,0) NULL" 	+ COMMA +
			" ["+FIELD_PX_PROMO			+"] [decimal](18,0) NULL" 	+ COMMA +
			" ["+FIELD_TYP_PXN		+"] [char](4) NULL" 	+ COMMA +
			" ["+FIELD_TYP_PXP			+"] [char](4) NULL" 	+ COMMA +
			" ["+FIELD_C_PROMO			+"] [varchar](50) NULL" 	+ COMMA +
			" ["+FIELD_FORCE	+"] [decimal](18,0) NULL" 	+ COMMA +
			" ["+FIELD_REM1			+"] [decimal](18,0) NULL" 	+ COMMA +
			" ["+FIELD_REM2			+"] [decimal](18,0) NULL" 	+ COMMA +
			" ["+FIELD_REM3			+"] [decimal](18,0) NULL" 	+ COMMA +
			" ["+FIELD_REM4		+"] [decimal](18,0) NULL" 	+ COMMA +
			" ["+FIELD_REM5	+"] [decimal](18,0) NULL"		+ COMMA +
			" ["+FIELD_REM6	+"] [decimal](18,0) NULL" 	+ COMMA +
			" ["+FIELD_COD_TAX1		+"] [int] NULL" 	+ COMMA +
			" ["+FIELD_COD_TAX2		+"] [int] NULL"		+ COMMA +
			" ["+FIELD_COD_TAX3			+"] [int] NULL"	+ 
			")";
	
	public static final String INDEX_CREATE="CREATE UNIQUE INDEX IF NOT EXISTS ["+INDEXNAME+"] ON ["+TABLENAME+"] (["+FIELD_COD_CLI+"], ["+FIELD_COD_PRO+"])";

	static public class structTarif
	{
		public String COD_CLI = "";        
		public String COD_PRO = "";
		public String TX_TVA = "";
		public String PX_REF  = "";                  
		public String PX_NET = "";  
		public String PX_REF_P = "";  
		public String PX_PROMO = "";  
		public String TYP_PXN = "";  
		public String TYP_PXP  = "";  
		public String C_PROMO  = "";
		public String FORCE  = ""; 
		public String REM1  = ""; 
		public String REM2 = ""; 
		public String REM3	= ""; 
		public String REM4 ="";
		public String REM5 ="";
		public String REM6 ="";
		public String COD_TAX1 ="";
		public String COD_TAX2 ="";
		public String COD_TAX3 ="";		

		@Override
		public String toString() {
			return "structTarif [COD_CLI=" + COD_CLI + ", COD_PRO=" + COD_PRO
					+ ", TX_TVA=" + TX_TVA + ", PX_REF=" + PX_REF + ", PX_NET="
					+ PX_NET + ", PX_REF_P=" + PX_REF_P + ", PX_PROMO=" + PX_PROMO + ", TYP_PXN="
					+ TYP_PXN + ", TYP_PXP=" + TYP_PXP + ", C_PROMO=" + C_PROMO + ", FORCE="
					+ FORCE + ", REM1=" + REM1 + ", REM2=" + REM2 + ", REM3="
					+ REM3 + ", REM4=" + REM4 + ", REM5="
					+ REM5 + ", REM6=" + REM6 + ", COD_TAX1=" + COD_TAX1
					+ ", COD_TAX2=" + COD_TAX2 + ", COD_TAX3=" + COD_TAX3 
					+"]";
		}

		/**
		 * Permet d'obtenir une liste de toutes les taxes à appliquer au tarif
		 * @return ArrayList<Taxe>
		 */
		public ArrayList<Taxe> getAllTaxesOfTarif(){
			ArrayList<Taxe> taxes = new ArrayList<Taxe>();

			//TVA
			Taxe tva = new Taxe("TVA", this.TX_TVA, "pourcentage");
			
			if(tva != null) taxes.add(tva);

			//cod_taxe
			Taxe taxe1 = null;
			Taxe taxe2 = null;
			Taxe taxe3 = null;
			if(!this.COD_TAX1.equals("")) taxe1 = Global.dbParam.getTaxeFromCodeTaxe(this.COD_TAX1);
			if(!this.COD_TAX2.equals("")) taxe2 = Global.dbParam.getTaxeFromCodeTaxe(this.COD_TAX2);
			if(!this.COD_TAX3.equals("")) taxe3 = Global.dbParam.getTaxeFromCodeTaxe(this.COD_TAX3);
			
			if(taxe1 != null) taxes.add(taxe1);
			if(taxe2 != null) taxes.add(taxe2);
			if(taxe3 != null) taxes.add(taxe3);
			
			return taxes;
		}
	} 

	MyDB db;
	public TableTarif(MyDB _db)
	{
		super();
		db=_db;
	}
	public boolean clear(StringBuilder err)
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

	public structTarif load(Cursor cursor){
		structTarif tarif = new structTarif();
		if(cursor != null){
			//			Debug.Log("requete", " CODE IN LOADING : -"+giveFld(cursor, FIELD_CODE+"-"));

			tarif.COD_CLI = giveFld(cursor, FIELD_COD_CLI);
			tarif.COD_PRO = giveFld(cursor, FIELD_COD_PRO);
			tarif.TX_TVA = giveFld(cursor, FIELD_TX_TVA);
			tarif.PX_REF = giveFld(cursor, FIELD_PX_REF);
			tarif.PX_NET = giveFld(cursor, FIELD_PX_NET);
			tarif.PX_REF_P = giveFld(cursor, FIELD_PX_REF_P);
			tarif.PX_PROMO = giveFld(cursor, FIELD_PX_PROMO);
			tarif.TYP_PXN = giveFld(cursor, FIELD_TYP_PXN);
			tarif.TYP_PXP = giveFld(cursor, FIELD_TYP_PXP);
			tarif.C_PROMO = giveFld(cursor, FIELD_C_PROMO);
			tarif.FORCE = giveFld(cursor, FIELD_FORCE);
			tarif.REM1 = giveFld(cursor, FIELD_REM1);
			tarif.REM2 = giveFld(cursor, FIELD_REM2);
			tarif.REM3 = giveFld(cursor, FIELD_REM3);
			tarif.REM4 = giveFld(cursor, FIELD_REM4);
			tarif.REM5 = giveFld(cursor, FIELD_REM5);
			tarif.REM6 = giveFld(cursor, FIELD_REM6);
			tarif.COD_TAX1 = giveFld(cursor, FIELD_COD_TAX1);
			tarif.COD_TAX2 = giveFld(cursor, FIELD_COD_TAX2);
			tarif.COD_TAX3 = giveFld(cursor, FIELD_COD_TAX3);

		}

		//		Debug.Log("requete", client.toString());

		return tarif;
	}

	public structTarif load(int cod_cli, int cod_pro){
		structTarif tarif = new structTarif();
		String query = "SELECT *"
				+" FROM "+TABLENAME
				+" WHERE "+getFullFieldName(FIELD_COD_CLI)+" = '"+cod_cli+"' "
				+ "and "+getFullFieldName(FIELD_COD_PRO)+" = '"+cod_pro+"'";

		Cursor cursor =  db.conn.rawQuery(query, null);

		if(cursor != null && cursor.moveToFirst()){

			tarif = load(cursor);
			cursor.close();
		}

		return tarif;
	}

	/**
	 * Permet de retourner tous les tarifs d'un client
	 * @param int
	 * @return Cursor
	 */
	public Cursor getTarifsFromCodeClient(int cod_cli){

		String query = "SELECT * FROM "+TABLENAME
				+" WHERE "+FIELD_COD_CLI+"='"+cod_cli+"'"
				+" ORDER BY "+FIELD_COD_CLI+" ASC";

		Cursor cursor = db.conn.rawQuery(query, null);

		return cursor;

	}

	/**
	 * Permet de retourner tous les tarifs d'un produit
	 * @param int
	 * @return Cursor
	 */
	public Cursor getTarifsFromCodeProduit(int cod_pro){

		String query = "SELECT * FROM "+TABLENAME
				+" WHERE "+FIELD_COD_PRO+"='"+cod_pro+"'"
				+" ORDER BY "+FIELD_COD_PRO+" ASC";

		Cursor cursor = db.conn.rawQuery(query, null);

		return cursor;

	}

	/**
	 * Permet de récupérer tous les tarifs de la table
	 * @return Cursor
	 */
	public Cursor getAllTarifs(){
		//TODO pour améliorer les performances, prendre uniquement les tarifs qui correspondent aux clients du vendeur connecté
		//faire une sous requete
		String query = "SELECT * FROM "+TABLENAME+ ";";		
		Cursor cursor = db.conn.rawQuery(query, null);		
		return cursor;
	}

	/**
	 * Permet d'obtenir une liste de structTarif à partir d'un cursor
	 * @param cursor
	 * @return ArrayList<structTarif>
	 */
	public ArrayList<structTarif> getListOfCursorTarif(Cursor cursor){
		ArrayList<structTarif> list = new ArrayList<TableTarif.structTarif>();

		cursor.moveToFirst();
		while(!cursor.isAfterLast()){
			structTarif tarif = new structTarif();
			fillStruct(cursor,tarif);
			list.add(tarif);
			cursor.moveToNext();
		}
		cursor.close();
		return list;

	}

	public int Count()
	{

		try
		{
			String query="select count(*) from "+TABLENAME;
			Cursor cur=db.conn.rawQuery(query, null);
			if (cur.moveToNext())
			{
				return cur.getInt(0);
			}
			return 0;
		}
		catch(Exception ex)
		{
			return -1;
		}

	}


	public boolean getTarif(String cod_cli,String codetarif, String cod_pro,structTarif tarif,StringBuilder err)
	{	
		try
		{
			//client 
			boolean exist=false;
			
			String query="";
			query="select * FROM "+
					TABLENAME+
					" where "+
					FIELD_COD_CLI+
					"='"+cod_cli+"' and " +
					FIELD_COD_PRO+
					"='"+cod_pro+"'";


			Cursor cur=db.conn.rawQuery(query, null);
			if (cur.moveToNext())
			{
				fillStruct(cur,tarif);
				exist=true;
				return true;
			}
			
			//tarif
			

			if(exist==false)
			{
				query="select * FROM "+
						TABLENAME+
						" where "+
						FIELD_COD_CLI+
						"='"+codetarif+"' and " +
						FIELD_COD_PRO+
						"='"+cod_pro+"'";
				 cur=db.conn.rawQuery(query, null);
				if (cur.moveToNext())
				{
					fillStruct(cur,tarif);
					return true;
				}
				
				
			}
		}
		catch(Exception ex)
		{

		}

		return false;
	}

	public boolean getTarifWithoutClient(int cod_pro,structTarif tarif,StringBuilder err)
	{	
		try
		{
			String query="";
			query="select * FROM "+
					TABLENAME+
					" where "+
					FIELD_COD_PRO+
					"='"+cod_pro+"' "
					+ "ORDER BY " +FIELD_PX_NET+ " desc LIMIT 1";


			Cursor cur=db.conn.rawQuery(query, null);
			if (cur.moveToNext())
			{
				fillStruct(cur,tarif);
				return true;
			}


		}
		catch(Exception ex)
		{

		}

		return false;
	}

	/**
	 * Permet de récupérer le total des taxes
	 * @return String
	 * @param String
	 * @param String
	 */
	public String getTaxeOfTarif(String cod_cli,String codetarif, String cod_pro){
		structTarif tarif = new structTarif();
		String taxeFinal = "";

		getTarif(cod_cli,codetarif, cod_pro, tarif, new StringBuilder());
		Double taxes = 0.0;
		String taxe1 = "";
		String taxe2 = "";
		String taxe3 = "";
		try{
			taxes = Double.valueOf(tarif.TX_TVA.replace(",", ".")); 

			int code1 = Integer.parseInt(tarif.COD_TAX1);
			int code2 = Integer.parseInt(tarif.COD_TAX2);
			int code3 = Integer.parseInt(tarif.COD_TAX3);
			taxe1 = Global.dbParam.getOMRFromCode(code1);
			taxe2 = Global.dbParam.getOMRFromCode(code2);
			taxe3 = Global.dbParam.getOMRFromCode(code3);
			if(!taxe1.equals("")) taxes += Double.valueOf(taxe1);
			if(!taxe2.equals("")) taxes += Double.valueOf(taxe2);
			if(!taxe3.equals("")) taxes += Double.valueOf(taxe3);
		}catch(NumberFormatException ex){

		}
		taxeFinal = taxes.toString();
		return taxeFinal;
	}

	public String calculateMontantTaxe(Float base, Taxe taxe){
		Double total = 0.0;
		Double based = (double) base;
		String valeur = "";
		if(taxe.getType().equals("pourcentage")){
			try{
				valeur = taxe.getValeur().replace(",", ".");
				total = (Double.valueOf(valeur) /100) * based;
				total = Math.round(total * 100.0)/100.0;
				return total.toString();
			}catch(NumberFormatException ex){

			}
		}else if(taxe.getValeur().equals("")){
			
		}
		return "";
	}

	void fillStruct(Cursor cur,structTarif tarif)
	{
		tarif.C_PROMO=cur.getString(cur.getColumnIndex(FIELD_C_PROMO));
		tarif.COD_CLI=cur.getString(cur.getColumnIndex(FIELD_COD_CLI));
		tarif.COD_PRO=cur.getString(cur.getColumnIndex(FIELD_COD_PRO));
		tarif.COD_TAX1=cur.getString(cur.getColumnIndex(FIELD_COD_TAX1));
		tarif.COD_TAX2=cur.getString(cur.getColumnIndex(FIELD_COD_TAX2));
		tarif.COD_TAX3=cur.getString(cur.getColumnIndex(FIELD_COD_TAX3));
		tarif.FORCE=cur.getString(cur.getColumnIndex(FIELD_FORCE));
		tarif.PX_NET=cur.getString(cur.getColumnIndex(FIELD_PX_NET));
		tarif.PX_PROMO=cur.getString(cur.getColumnIndex(FIELD_PX_PROMO));
		tarif.PX_REF=cur.getString(cur.getColumnIndex(FIELD_PX_REF));
		tarif.PX_REF_P=cur.getString(cur.getColumnIndex(FIELD_PX_REF_P));
		tarif.REM1=cur.getString(cur.getColumnIndex(FIELD_REM1));
		tarif.REM2=cur.getString(cur.getColumnIndex(FIELD_REM2));
		tarif.REM3=cur.getString(cur.getColumnIndex(FIELD_REM3));
		tarif.REM4=cur.getString(cur.getColumnIndex(FIELD_REM4));
		tarif.REM5=cur.getString(cur.getColumnIndex(FIELD_REM5));
		tarif.REM6=cur.getString(cur.getColumnIndex(FIELD_REM6));
		tarif.TX_TVA=cur.getString(cur.getColumnIndex(FIELD_TX_TVA));
		tarif.TYP_PXN=cur.getString(cur.getColumnIndex(FIELD_TYP_PXN));
		tarif.TYP_PXP=cur.getString(cur.getColumnIndex(FIELD_TYP_PXP));
	}

}
