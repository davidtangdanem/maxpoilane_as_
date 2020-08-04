package com.menadinteractive.segafredo.db;

import java.util.ArrayList;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;

import com.menadinteractive.segafredo.communs.Fonctions;

public class dbKD730FacturesDues extends dbKD {

	/**
	 * Initialisation variable
	 */
	public final static int KD_TYPE = 730;
	public final String FIELD_DATATYPE 	= fld_kd_dat_type;
	public final String FIELD_TYPEDOC 	= fld_kd_dat_idx03;
	public final String FIELD_CODVRP 	= fld_kd_dat_idx01;
	static public    final String FIELD_NUMDOC 	= fld_kd_dat_idx02;
	static public final String FIELD_CODECLIENT = fld_kd_cli_code;
	static public    final String FIELD_DATEDOC 	= fld_kd_dat_idx04;
	public final String FIELD_DATEECH 	= fld_kd_dat_data01;
	public final String FIELD_ISLOCAL 	= fld_kd_dat_data02;//1-0 si facture dues generes en locale
	public final String FIELD_REMISE 	= fld_kd_val_data31;
	public final String FIELD_MNTHT 	= fld_kd_val_data32;
	public final String FIELD_MNTTVA 	= fld_kd_val_data33;
	public final String FIELD_MNTTTC 	= fld_kd_val_data34;
	public final String FIELD_MNTDU 	= fld_kd_val_data35;

	 
	MyDB db;
	public dbKD730FacturesDues(MyDB _db)
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
				int i=cur.getInt(0);
				if (cur!=null)
					cur.close();
				return i;
			}
			
			if (cur!=null)
				cur.close();
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
				int i=cur.getInt(0);
				if (cur!=null)
					cur.close();
				return i;
			}
			if (cur!=null)
				cur.close();
			return 0;
		}
		catch(Exception ex)
		{
			return -1;
		}
		
	} 
	
	 public ArrayList<Bundle> get(  String orderby) {
	 
		String query = "SELECT * FROM " + TABLENAME_HISTO +" histo "+
				" inner join "+TableClient.TABLENAME+" cli "+
				" on histo."+FIELD_CODECLIENT+"=cli."+TableClient.FIELD_CODE+
				" where " +
				fld_kd_dat_type + "=" + KD_TYPE +
				 
				" order by "+ orderby+"  ";
				
		ArrayList<Bundle>  bus=new ArrayList<Bundle>();
		Cursor cur = db.conn.rawQuery(query, null);
		while (cur.moveToNext() ) {
			Bundle b=new Bundle();
			b.putString(FIELD_DATATYPE 	, cur.getString(cur.getColumnIndex(FIELD_DATATYPE 	)));
			b.putString(FIELD_TYPEDOC 	, cur.getString(cur.getColumnIndex(FIELD_TYPEDOC 	)));
		 
			String  icon="facture";
		 
			b.putString("ICON" 	, icon );
			b.putString(FIELD_CODVRP 	, cur.getString(cur.getColumnIndex(FIELD_CODVRP 	)));
			b.putString(FIELD_NUMDOC 	, cur.getString(cur.getColumnIndex(FIELD_NUMDOC 	)));
			b.putString(FIELD_CODECLIENT, cur.getString(cur.getColumnIndex(FIELD_CODECLIENT)));
			b.putString(TableClient.FIELD_NOM, cur.getString(cur.getColumnIndex(TableClient.FIELD_NOM)));
			b.putString(TableClient.FIELD_ENSEIGNE, cur.getString(cur.getColumnIndex(TableClient.FIELD_ENSEIGNE)));
			
			b.putString(FIELD_DATEDOC 	, Fonctions.YYYY_MM_DD_to_dd_mm_yyyy(cur.getString(cur.getColumnIndex(FIELD_DATEDOC 	))));
			b.putString(FIELD_DATEECH 	, Fonctions.YYYY_MM_DD_to_dd_mm_yyyy(cur.getString(cur.getColumnIndex(FIELD_DATEECH 	))));
			b.putString(FIELD_REMISE 	, cur.getString(cur.getColumnIndex(FIELD_REMISE 	)));
			b.putString(FIELD_MNTHT 	, cur.getString(cur.getColumnIndex(FIELD_MNTHT 	)));
			b.putString(FIELD_MNTTVA 	, cur.getString(cur.getColumnIndex(FIELD_MNTTVA 	)));
			b.putString(FIELD_MNTTTC 	, cur.getString(cur.getColumnIndex(FIELD_MNTTTC 	)));
			b.putString(FIELD_MNTDU	, cur.getString(cur.getColumnIndex(FIELD_MNTDU	)));
			b.putString("YYYYMMDD"	, cur.getString(cur.getColumnIndex(FIELD_DATEDOC	)));
			
			bus.add(b); 
		}  
		if (cur!=null)
			cur.close();
		return bus;
	}
	 
	 /**
	  * Récupération des factures dues pour un client
	  * @param String codeClient
	  * @return bundle
	  */
	 public ArrayList<Bundle> getFacturesDuesFromCodeClient(String codeClient) {
		 
			String query = "SELECT * FROM " + TABLENAME_HISTO +" histo "+
					" inner join "+TableClient.TABLENAME+" cli "+
					" on histo."+FIELD_CODECLIENT+"=cli."+TableClient.FIELD_CODE+
					" where " +
					fld_kd_dat_type + "=" + KD_TYPE +
					" and histo."+FIELD_CODECLIENT+"='"+codeClient+"'" +
					" order by "+ FIELD_DATEDOC+"  ";
					
			ArrayList<Bundle>  bus=new ArrayList<Bundle>();
			Cursor cur = db.conn.rawQuery(query, null);
			while (cur.moveToNext() ) {
				Bundle b=new Bundle();
				b.putString(FIELD_DATATYPE 	, cur.getString(cur.getColumnIndex(FIELD_DATATYPE 	)));
				b.putString(FIELD_TYPEDOC 	, cur.getString(cur.getColumnIndex(FIELD_TYPEDOC 	)));
			 
				String  icon="facture";
			 
				b.putString("ICON" 	, icon );
				b.putString(FIELD_CODVRP 	, cur.getString(cur.getColumnIndex(FIELD_CODVRP 	)));
				b.putString(FIELD_NUMDOC 	, cur.getString(cur.getColumnIndex(FIELD_NUMDOC 	)));
				b.putString(FIELD_CODECLIENT, cur.getString(cur.getColumnIndex(FIELD_CODECLIENT)));
				b.putString(TableClient.FIELD_NOM, cur.getString(cur.getColumnIndex(TableClient.FIELD_NOM)));
				b.putString(FIELD_DATEDOC 	, Fonctions.YYYY_MM_DD_to_dd_mm_yyyy(cur.getString(cur.getColumnIndex(FIELD_DATEDOC 	))));
				b.putString(FIELD_DATEECH 	, Fonctions.YYYY_MM_DD_to_dd_mm_yyyy(cur.getString(cur.getColumnIndex(FIELD_DATEECH 	))));
				b.putString(FIELD_REMISE 	, cur.getString(cur.getColumnIndex(FIELD_REMISE 	)));
				b.putString(FIELD_MNTHT 	, cur.getString(cur.getColumnIndex(FIELD_MNTHT 	)));
				b.putString(FIELD_MNTTVA 	, cur.getString(cur.getColumnIndex(FIELD_MNTTVA 	)));
				b.putString(FIELD_MNTTTC 	, cur.getString(cur.getColumnIndex(FIELD_MNTTTC 	)));
				b.putString(FIELD_MNTDU	, cur.getString(cur.getColumnIndex(FIELD_MNTDU	)));
				b.putString("YYYYMMDD"	, cur.getString(cur.getColumnIndex(FIELD_DATEDOC	)));
				
				
				bus.add(b); 
			}  
			if (cur!=null)
				cur.close();
			return bus;
		}
	 
	 /**
	  * Récupération d'une facture du à partir du numéro de document
	  * @param String codeClient
	  * @return bundle
	  */
	 public Bundle getFactureDueFromNumDoc(String numDoc) {
		 
			String query = "SELECT * FROM " + TABLENAME_HISTO +" histo "+
					" inner join "+TableClient.TABLENAME+" cli "+
					" on histo."+FIELD_CODECLIENT+"=cli."+TableClient.FIELD_CODE+
					" where " +
					fld_kd_dat_type + "=" + KD_TYPE +
					" and histo."+FIELD_NUMDOC+"='"+numDoc+"'" +
					" order by "+ FIELD_DATEDOC+"  ";
					
			Bundle b = new Bundle();
			Cursor cur = db.conn.rawQuery(query, null);
			if (cur.moveToNext() ) {
				b.putString(FIELD_DATATYPE 	, cur.getString(cur.getColumnIndex(FIELD_DATATYPE 	)));
				b.putString(FIELD_TYPEDOC 	, cur.getString(cur.getColumnIndex(FIELD_TYPEDOC 	)));
			 
				String  icon="facture";
			 
				b.putString("ICON" 	, icon );
				b.putString(FIELD_CODVRP 	, cur.getString(cur.getColumnIndex(FIELD_CODVRP 	)));
				b.putString(FIELD_NUMDOC 	, cur.getString(cur.getColumnIndex(FIELD_NUMDOC 	)));
				b.putString(FIELD_CODECLIENT, cur.getString(cur.getColumnIndex(FIELD_CODECLIENT)));
				b.putString(TableClient.FIELD_NOM, cur.getString(cur.getColumnIndex(TableClient.FIELD_NOM)));
				b.putString(FIELD_DATEDOC 	, Fonctions.YYYY_MM_DD_to_dd_mm_yyyy(cur.getString(cur.getColumnIndex(FIELD_DATEDOC 	))));
				b.putString(FIELD_DATEECH 	, Fonctions.YYYY_MM_DD_to_dd_mm_yyyy(cur.getString(cur.getColumnIndex(FIELD_DATEECH 	))));
				b.putString(FIELD_REMISE 	, cur.getString(cur.getColumnIndex(FIELD_REMISE 	)));
				b.putString(FIELD_MNTHT 	, cur.getString(cur.getColumnIndex(FIELD_MNTHT 	)));
				b.putString(FIELD_MNTTVA 	, cur.getString(cur.getColumnIndex(FIELD_MNTTVA 	)));
				b.putString(FIELD_MNTTTC 	, cur.getString(cur.getColumnIndex(FIELD_MNTTTC 	)));
				b.putString(FIELD_MNTDU	, cur.getString(cur.getColumnIndex(FIELD_MNTDU	))); 
				b.putString("YYYYMMDD"	, cur.getString(cur.getColumnIndex(FIELD_DATEDOC	)));
			}  
			if (cur!=null)
				cur.close();
			return b;
		}
	 
	 public boolean insert(dbKD83EntCde.structEntCde ent)
	 {
		 try{
			 String query="INSERT INTO "+TABLENAME_HISTO+"("+
					FIELD_DATATYPE+","+
					FIELD_CODECLIENT+","+
					FIELD_DATEDOC+","+
					FIELD_MNTDU+","+
					FIELD_MNTHT+","+
					FIELD_MNTTTC+","+
					FIELD_MNTTVA+","+
					FIELD_NUMDOC+","+
					FIELD_REMISE+","+
					FIELD_ISLOCAL+","+
					FIELD_TYPEDOC+") values ("+
						KD_TYPE+","+
					"'"+ent.CODECLI+"',"+
					"'"+Fonctions.YYYYMMDD_to_yyyy_mm_dd(ent.DATECDE)+"',"+
					"'"+ent.MNTTC+"',"+
					"'"+ent.MNTHT+"',"+
					"'"+ent.MNTTC+"',"+
					"'"+ent.MNTTVA1+ent.MNTTVA2+"',"+
					"'"+ent.CODECDE+"',"+
					"'"+ent.REMISE+"',"+
					"'1',"+
					"'"+ent.TYPEDOC+"')";
					
			  db.conn.execSQL(query);
			  
			  return true;
		 }
		 catch(Exception ex)
		 {
			 
		 }
		 return false;
	 }
	 
	 public float sumLocalFA(String codeClient)
	 {
		 //TODO éventuellement erreur, faire la somme du montant total TTC
		 try{
			 String query = "SELECT "+
					 		"sum("+FIELD_MNTTTC+") somme "+
					 	" FROM " + TABLENAME_HISTO +" histo "+
						 
						" where " +
						fld_kd_dat_type + "=" + KD_TYPE +
						" and histo."+FIELD_CODECLIENT+"='"+codeClient+"'" +
						" and histo."+FIELD_TYPEDOC+"='"+TableSouches.TYPEDOC_FACTURE+"'" ;
			 
			  
				Cursor cur = db.conn.rawQuery(query, null);
				if (cur.moveToNext() ) {
			 
					float f=cur.getFloat(cur.getColumnIndex("somme" 	));
					
					if (cur!=null)
						cur.close();
					
					return f;
				}
				
				if (cur!=null)
					cur.close();
					
		 }catch(Exception ex)
		 {
			 
		 }
		 return 0.00f;
	 }
}
