package com.menadinteractive.segafredo.db;

import java.util.ArrayList;

import com.menadinteractive.segafredo.communs.Fonctions;
import com.menadinteractive.segafredo.communs.Global;
import com.menadinteractive.segafredo.db.TableClient.structClient;

import android.database.Cursor;
import android.os.Bundle;

public class dbKD729HistoDocuments extends dbKD {

	/**
	 * Initialisation variable
	 */
	public final static int KD_TYPE = 729;
	public final String FIELD_DATATYPE 	= fld_kd_dat_type;
	public final static String FIELD_TYPEDOC 	= fld_kd_dat_idx03;
	public final String FIELD_CODVRP 	= fld_kd_dat_idx01;
	public final static String FIELD_NUMDOC 	= fld_kd_dat_idx02;
	public final static String FIELD_CODECLIENT = fld_kd_cli_code;
	public final static String FIELD_DATEDOC 	= fld_kd_dat_idx04;
	public final String FIELD_DATEECH 	= fld_kd_dat_data01;
	public final String FIELD_REMISE 	= fld_kd_val_data31;
	public final String FIELD_MNTHT 	= fld_kd_val_data32;
	public final String FIELD_MNTTVA 	= fld_kd_val_data33;
	public final String FIELD_MNTTTC 	= fld_kd_val_data34;

	//différent pour les encaissements
	public final static String FIELD_ENC_MNTOTAL 	= fld_kd_val_data34;
	public final static String FIELD_ENC_MNTCHQ 	= fld_kd_val_data33;
	public final static String FIELD_ENC_MNTESP 	= fld_kd_val_data32;
	
	MyDB db;
	public dbKD729HistoDocuments(MyDB _db)
	{
		db=_db;
	}


	public boolean clear(StringBuilder err)
	{
		try
		{
			String query="delete from "+TABLENAME_HISTO+
					" where "+fld_kd_dat_type+"="+KD_TYPE;
			//db.conn.delete(TABLENAME, null, null);
			db.execSQL(query, err);

		}
		catch(Exception ex)
		{
			err.append(ex.getMessage());
			return false;	
		}
		return true;
	}
	public int Count()
	{
		try
		{
			String query="select count(*) from "+TABLENAME_HISTO+
					" where "+fld_kd_dat_type+"="+KD_TYPE;
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
	public int Count(String codecli)
	{
		try
		{
			String query="select count(*) from "+TABLENAME_HISTO+
					" where "+fld_kd_dat_type+"="+KD_TYPE+
					" and "+FIELD_CODECLIENT+"='"+codecli+"'";
			
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
//

	public ArrayList<Bundle> get(String codecli ,String numdoc ,String typedoc) {

		String query = "SELECT * FROM " + TABLENAME_HISTO + " where "
				+ fld_kd_dat_type + "=" + KD_TYPE ;
			 
		if (codecli.equals("")==false)
			query+=" and "+FIELD_CODECLIENT+"='"+codecli+"'";
		if (numdoc.equals("")==false)
			query+=" and "+FIELD_NUMDOC+"='"+numdoc+"'";
		if (typedoc.equals("")==false)
			query+=" and "+FIELD_TYPEDOC+"='"+typedoc+"'";
		
		query+=" order by "+ FIELD_DATEDOC+" desc";

		ArrayList<Bundle>  bus=new ArrayList<Bundle>();
		Cursor cur = db.conn.rawQuery(query, null);
		while (cur.moveToNext() ) {
			Bundle b=new Bundle();
			b.putString(FIELD_DATATYPE 	, cur.getString(cur.getColumnIndex(FIELD_DATATYPE 	)));
			b.putString(FIELD_TYPEDOC 	, cur.getString(cur.getColumnIndex(FIELD_TYPEDOC 	)));
			typedoc=cur.getString(cur.getColumnIndex(FIELD_TYPEDOC 	));
			String icon="";

			if (typedoc.toUpperCase().equals(TableSouches.TYPEDOC_FACTURE)) icon="facture";
			if (typedoc.toUpperCase().equals(TableSouches.TYPEDOC_BL)) icon="bl";
			if (typedoc.toUpperCase().equals(TableSouches.TYPEDOC_AVOIR)) icon="avoir";
			if (typedoc.toUpperCase().equals(TableSouches.TYPEDOC_RETOUR)) icon="retour";
			if (typedoc.toUpperCase().equals(TableSouches.TYPEDOC_REGLEMENT)) icon="basic2_018_money_coins";
			
			b.putString("ICON" 	, icon );
			b.putString(FIELD_CODVRP 	, cur.getString(cur.getColumnIndex(FIELD_CODVRP 	)));
			b.putString(FIELD_NUMDOC 	, cur.getString(cur.getColumnIndex(FIELD_NUMDOC 	)));
			b.putString(FIELD_CODECLIENT, cur.getString(cur.getColumnIndex(FIELD_CODECLIENT)));
			b.putString(FIELD_DATEDOC 	, Fonctions.YYYY_MM_DD_to_dd_mm_yyyy(cur.getString(cur.getColumnIndex(FIELD_DATEDOC 	))));
			b.putString(FIELD_DATEECH 	,  Fonctions.YYYY_MM_DD_to_dd_mm_yyyy(cur.getString(cur.getColumnIndex(FIELD_DATEECH 	))));
			b.putString(FIELD_REMISE 	, cur.getDouble(cur.getColumnIndex(FIELD_REMISE 	))+"");
			b.putString(FIELD_MNTHT 	, cur.getDouble(cur.getColumnIndex(FIELD_MNTHT 	))+"");
			b.putString(FIELD_MNTTVA 	, cur.getDouble(cur.getColumnIndex(FIELD_MNTTVA 	))+"");
			b.putString(FIELD_MNTTTC 	, cur.getDouble(cur.getColumnIndex(FIELD_MNTTTC 	))+"");
			b.putString("YYYYMMDD"	, cur.getString(cur.getColumnIndex(FIELD_DATEDOC	)));

			bus.add(b); 
		}  

		return bus;
	}

	public ArrayList<Bundle> getAvoirFromCodeClient(String codecli) {

		String query = "SELECT * FROM " + TABLENAME_HISTO + " where "
				+ fld_kd_dat_type + "=" + KD_TYPE ;
		if (!codecli.equals(""))
			query+=" and "+FIELD_CODECLIENT+"='"+codecli+"'";

		query+=" and "+FIELD_TYPEDOC+"='AV'";

		query+=" order by "+ FIELD_DATEDOC+" desc";

		ArrayList<Bundle>  bus=new ArrayList<Bundle>();
		Cursor cur = db.conn.rawQuery(query, null);
		while (cur.moveToNext() ) {
			Bundle b=new Bundle();
			b.putString(FIELD_DATATYPE 	, cur.getString(cur.getColumnIndex(FIELD_DATATYPE 	)));
			b.putString(FIELD_TYPEDOC 	, cur.getString(cur.getColumnIndex(FIELD_TYPEDOC 	)));
			String typedoc=cur.getString(cur.getColumnIndex(FIELD_TYPEDOC 	));
			String icon="";

			if (typedoc.toUpperCase().equals(TableSouches.TYPEDOC_FACTURE)) icon="facture";
			if (typedoc.toUpperCase().equals(TableSouches.TYPEDOC_BL)) icon="bl";
			if (typedoc.toUpperCase().equals(TableSouches.TYPEDOC_AVOIR)) icon="avoir";
			if (typedoc.toUpperCase().equals(TableSouches.TYPEDOC_RETOUR)) icon="retour";
			b.putString("ICON" 	, icon );
			b.putString(FIELD_CODVRP 	, cur.getString(cur.getColumnIndex(FIELD_CODVRP 	)));
			b.putString(FIELD_NUMDOC 	, cur.getString(cur.getColumnIndex(FIELD_NUMDOC 	)));
			b.putString(FIELD_CODECLIENT, cur.getString(cur.getColumnIndex(FIELD_CODECLIENT)));
			b.putString(FIELD_DATEDOC 	, Fonctions.YYYY_MM_DD_to_dd_mm_yyyy(cur.getString(cur.getColumnIndex(FIELD_DATEDOC 	))));
			b.putString(FIELD_DATEECH 	,  Fonctions.YYYY_MM_DD_to_dd_mm_yyyy(cur.getString(cur.getColumnIndex(FIELD_DATEECH 	))));
			b.putString(FIELD_REMISE 	, cur.getDouble(cur.getColumnIndex(FIELD_REMISE 	))+"");
			b.putString(FIELD_MNTHT 	, cur.getDouble(cur.getColumnIndex(FIELD_MNTHT 	))+"");
			b.putString(FIELD_MNTTVA 	, cur.getDouble(cur.getColumnIndex(FIELD_MNTTVA 	))+"");
			b.putString(FIELD_MNTTTC 	, cur.getDouble(cur.getColumnIndex(FIELD_MNTTTC 	))+"");
			b.putString("YYYYMMDD"	, cur.getString(cur.getColumnIndex(FIELD_DATEDOC	)));

			bus.add(b); 
		}  

		return bus;
	}
	public ArrayList<Bundle> getReglementFromSouche(String souche) {

		String query = "SELECT * FROM " + TABLENAME_HISTO + " where "
				+ fld_kd_dat_type + "=" + KD_TYPE ;
		
		query+=" and "+FIELD_NUMDOC+"='"+souche+"'";

		query+=" and "+FIELD_TYPEDOC+"='"+TableSouches.TYPEDOC_REGLEMENT+"' ";

		query+=" order by "+ FIELD_DATEDOC+" desc";

		ArrayList<Bundle>  bus=new ArrayList<Bundle>();
		Cursor cur = db.conn.rawQuery(query, null);
		while (cur.moveToNext() ) {
			Bundle b=new Bundle();
			b.putString(FIELD_DATATYPE 	, cur.getString(cur.getColumnIndex(FIELD_DATATYPE 	)));
			b.putString(FIELD_TYPEDOC 	, cur.getString(cur.getColumnIndex(FIELD_TYPEDOC 	)));
		 
			b.putString(FIELD_CODVRP 	, cur.getString(cur.getColumnIndex(FIELD_CODVRP 	)));
			b.putString(FIELD_NUMDOC 	, cur.getString(cur.getColumnIndex(FIELD_NUMDOC 	)));
			b.putString(FIELD_CODECLIENT, cur.getString(cur.getColumnIndex(FIELD_CODECLIENT)));
			b.putString(FIELD_DATEDOC 	, Fonctions.YYYY_MM_DD_to_dd_mm_yyyy(cur.getString(cur.getColumnIndex(FIELD_DATEDOC 	))));
			b.putString(FIELD_DATEECH 	,  Fonctions.YYYY_MM_DD_to_dd_mm_yyyy(cur.getString(cur.getColumnIndex(FIELD_DATEECH 	))));
			b.putString(FIELD_REMISE 	, cur.getDouble(cur.getColumnIndex(FIELD_REMISE 	))+"");
			b.putString(FIELD_MNTHT 	, cur.getDouble(cur.getColumnIndex(FIELD_MNTHT 	))+"");
			b.putString(FIELD_MNTTVA 	, cur.getDouble(cur.getColumnIndex(FIELD_MNTTVA 	))+"");
			b.putString(FIELD_MNTTTC 	, cur.getDouble(cur.getColumnIndex(FIELD_MNTTTC 	))+"");
 
			bus.add(b); 
		}  

		return bus;
	}

	/**
	 * Récupère un document à partir du numéro du document
	 * @param numDoc
	 * @return bundle document
	 */
	public Bundle getDocumentFromNumDoc(String numDoc){
		String query = "SELECT * FROM " + TABLENAME_HISTO + " where "
				+ fld_kd_dat_type + "=" + KD_TYPE ;
		if (numDoc.equals("")==false)
			query+=" and "+FIELD_NUMDOC+"='"+numDoc+"'";

		query+=" order by "+ FIELD_DATEDOC+" desc";

		Bundle b=new Bundle();

		Cursor cur = db.conn.rawQuery(query, null);
		while (cur.moveToNext() ) {

			b.putString(FIELD_DATATYPE 	, cur.getString(cur.getColumnIndex(FIELD_DATATYPE 	)));
			b.putString(FIELD_TYPEDOC 	, cur.getString(cur.getColumnIndex(FIELD_TYPEDOC 	)));
			String typedoc=cur.getString(cur.getColumnIndex(FIELD_TYPEDOC 	));
			String icon="";

			if (typedoc.toUpperCase().equals(TableSouches.TYPEDOC_FACTURE)) icon="facture";
			if (typedoc.toUpperCase().equals(TableSouches.TYPEDOC_BL)) icon="bl";
			if (typedoc.toUpperCase().equals(TableSouches.TYPEDOC_AVOIR)) icon="avoir";
			if (typedoc.toUpperCase().equals(TableSouches.TYPEDOC_RETOUR)) icon="retour";
			b.putString("ICON" 	, icon );
			b.putString(FIELD_CODVRP 	, cur.getString(cur.getColumnIndex(FIELD_CODVRP 	)));
			b.putString(FIELD_NUMDOC 	, cur.getString(cur.getColumnIndex(FIELD_NUMDOC 	)));
			b.putString(FIELD_CODECLIENT, cur.getString(cur.getColumnIndex(FIELD_CODECLIENT)));
			b.putString(FIELD_DATEDOC 	, Fonctions.YYYY_MM_DD_to_dd_mm_yyyy(cur.getString(cur.getColumnIndex(FIELD_DATEDOC 	))));
			b.putString(FIELD_DATEECH 	,  Fonctions.YYYY_MM_DD_to_dd_mm_yyyy(cur.getString(cur.getColumnIndex(FIELD_DATEECH 	))));
			b.putString(FIELD_REMISE 	, cur.getDouble(cur.getColumnIndex(FIELD_REMISE 	))+"");
			b.putString(FIELD_MNTHT 	, cur.getDouble(cur.getColumnIndex(FIELD_MNTHT 	))+"");
			b.putString(FIELD_MNTTVA 	, cur.getDouble(cur.getColumnIndex(FIELD_MNTTVA 	))+"");
			b.putString(FIELD_MNTTTC 	, cur.getDouble(cur.getColumnIndex(FIELD_MNTTTC 	))+"");
			b.putString("YYYYMMDD"	, cur.getString(cur.getColumnIndex(FIELD_DATEDOC	)));
		}  

		return b;
	}
	public boolean delete_duplicata(String numcde,StringBuffer err)
	{
		try
		{
			String query="DELETE from "+
					TABLENAME_DUPLICATA+		
					" where "+
					fld_kd_dat_type+"='"+ dbKD83EntCde.KD_TYPE+ "' ";

			db.conn.execSQL(query);
			
			return true;
		}
		catch(Exception ex)
		{
			err.append(ex.getMessage());
		}
		return false;
	}
	
	public void Copy_Duplicata(String numcde,String codeclient)
	{
		
		try
		{
			String streglement ="";
			streglement=Global.dbClient.getModereglement(codeclient);
			
			//on efface l'existant et on sauve
			StringBuffer er=new StringBuffer();
			delete_duplicata(numcde,er);
			
			String query = "SELECT * FROM " + TABLENAME_HISTO + " where "
					+ fld_kd_dat_type + "=" + KD_TYPE + " and "
					+ this.FIELD_NUMDOC + "=" + "'" +numcde+ "'  ";
			
			Cursor cur = db.conn.rawQuery(query, null);
			if (cur.moveToNext() ) {
				
				query="INSERT INTO " + TABLENAME_DUPLICATA +" ("+
						dbKD.fld_kd_dat_type+","+
						dbKD83EntCde.FIELD_ENTCDE_CODEREP   		+","+
						dbKD83EntCde.FIELD_ENTCDE_CODECDE   		+","+ 
						dbKD83EntCde.FIELD_ENTCDE_CODECLI       	+","+  
						dbKD83EntCde.FIELD_ENTCDE_DATECDE			+","+
						dbKD83EntCde.FIELD_ENTCDE_ECHEANCE  		+","+
						dbKD83EntCde.FIELD_ENTCDE_REMISE 			+","+
						dbKD83EntCde.FIELD_ENTCDE_MNTHT 			+","+
						dbKD83EntCde.FIELD_ENTCDE_MNTTVA1  			+","+
						dbKD83EntCde.FIELD_ENTCDE_MNTTC  			+","+
						dbKD83EntCde.FIELD_ENTCDE_R_REGL			+""+
						
						 
						
		  		") VALUES ("+
		  		dbKD83EntCde.KD_TYPE+","+
		  		"'"+cur.getString(cur.getColumnIndex(FIELD_CODVRP 	))+"',"+
		  		"'"+cur.getString(cur.getColumnIndex(FIELD_NUMDOC 	))+"',"+
		  		"'"+cur.getString(cur.getColumnIndex(FIELD_CODECLIENT 	))+"',"+
		  		"'"+Fonctions.Left(cur.getString(cur.getColumnIndex(FIELD_DATEDOC 	)), 4)+Fonctions.Mid(cur.getString(cur.getColumnIndex(FIELD_DATEDOC 	)), 5, 2)+Fonctions.Right(cur.getString(cur.getColumnIndex(FIELD_DATEDOC 	)), 2)+"',"+
		  		"'"+Fonctions.Left(cur.getString(cur.getColumnIndex(FIELD_DATEECH 	)), 4)+Fonctions.Mid(cur.getString(cur.getColumnIndex(FIELD_DATEECH 	)), 5, 2)+Fonctions.Right(cur.getString(cur.getColumnIndex(FIELD_DATEECH 	)), 2)+"',"+
		  		"'"+Fonctions.ReplaceVirgule(cur.getString(cur.getColumnIndex(FIELD_REMISE 	)))+"',"+
		  		"'"+Fonctions.ReplaceVirgule(cur.getString(cur.getColumnIndex(FIELD_MNTHT 	)))+"',"+
		  		"'"+Fonctions.ReplaceVirgule(cur.getString(cur.getColumnIndex(FIELD_MNTTVA 	)))+"',"+
		  		"'"+Fonctions.ReplaceVirgule(cur.getString(cur.getColumnIndex(FIELD_MNTTTC 	)))+"',"+
		  		"'"+streglement+"' )";
		  		
				
				db.conn.execSQL(query);
				
			}
			else
			{
				
				 
			}
		
	
		}
		catch(Exception ex)
		{
			Global.lastErrorMessage=(ex.getMessage());
		}

		
		
	}
	String Echeance_duplicata(String reglement , String date)
	{
		String Echeance="";
		
		String regledecalcalule=Global.dbParam.getComment(Global.dbParam.PARAM_MODEREGLEMENT,reglement);
	    if (regledecalcalule.equals("")==false)
	    {
 	    	
	    	Echeance =Global.dbParam.calcDateEcheance(date,regledecalcalule);
	     }
	    else
	    {
	    	Echeance = date;
	    }
		return Echeance;
		
	}
	
}
