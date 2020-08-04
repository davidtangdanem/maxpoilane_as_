package com.menadinteractive.segafredo.db;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.preference.Preference;

import com.menadinteractive.inventaire.InventaireActivity;
import com.menadinteractive.segafredo.communs.Fonctions;
import com.menadinteractive.segafredo.communs.Global;
import com.menadinteractive.segafredo.db.dbKD543LinInventaire.structPassePlat;
import com.menadinteractive.segafredo.db.dbKD84LinCde.structLinCde;
import com.menadinteractive.segafredo.db.dbSiteProduit.structArt;
import com.menadinteractive.segafredo.plugins.Espresso;



/**
 * Vue ligne de cde dans la table KD
 * @author Marc VOUAUX
 *STOCK theorique calcul� SUR LE SERVEUR
 */
public class dbKD545StockTheoSrv extends dbKD {

	public final static int KD_TYPE= 545;
	public final String FIELD_LIGNEINV_SOCCODE 		= fld_kd_soc_code;
	public final static String FIELD_LIGNEINV_PROCODE 		= fld_kd_pro_code;
	public final String FIELD_LIGNEINV_REPCODE 		= fld_kd_dat_idx01;
	public final String FIELD_LIGNEINV_DATE 		= fld_kd_dat_idx02;
	 
	public final static String FIELD_LIGNEINV_QTETHEO 		= fld_kd_dat_data06;
	 

	MyDB db;
	public dbKD545StockTheoSrv(MyDB _db)
	{
		super();
		db=_db;	
	}

	 static public class structPassePlat {
		public structPassePlat()
		{	
			FIELD_LIGNEINV_SOCCODE 		="";
			FIELD_LIGNEINV_PROCODE 		="";
			FIELD_LIGNEINV_REPCODE 		="";
			FIELD_LIGNEINV_DATE 		="";
			FIELD_LIGNEINV_TYPEPIECE 	="";
			FIELD_LIGNEINV_CODABAR 		="";
			FIELD_LIGNEINV_NUMLIGNE 	="";
			FIELD_LIGNEINV_DESIGNATION 	="";
			FIELD_LIGNEINV_UV 			="";
			FIELD_LIGNEINV_QTE 			="";
			FIELD_LIGNEINV_QTETEMP 		= "";
			FIELD_LIGNEINV_QTETHEO 		= "";
            FIELD_LIGNEINV_LOT 			="";
			FIELD_LIGNEINV_SERIE 		="";
			FIELD_LIGNEINV_COMMENT1 		="";
			FIELD_LIGNEINV_DLC="";
			FIELD_LIGNEINV_DEPOT="";
		}

		public String FIELD_LIGNEINV_SOCCODE 		;
		public String FIELD_LIGNEINV_PROCODE 		;
		public String FIELD_LIGNEINV_REPCODE 		;
		public String FIELD_LIGNEINV_DATE 		;
		public String FIELD_LIGNEINV_TYPEPIECE 	;
		public String FIELD_LIGNEINV_CODABAR 	;	
		public String FIELD_LIGNEINV_NUMLIGNE 	;
		public String FIELD_LIGNEINV_DESIGNATION ;	
		public String FIELD_LIGNEINV_UV 			;
		public String FIELD_LIGNEINV_QTE 			;
		public String FIELD_LIGNEINV_LOT 			;
		public String FIELD_LIGNEINV_SERIE 		;
		public String FIELD_LIGNEINV_COMMENT1 		;
		public String FIELD_LIGNEINV_QTETEMP 	;
		public String FIELD_LIGNEINV_QTETHEO ;
		public String FIELD_LIGNEINV_DLC;
		public String FIELD_LIGNEINV_DEPOT;
	}

	public int Count(boolean inTmp)
	{

		try
		{
			String table=TABLENAME;
			if (inTmp)
				table=TABLENAME_TEMP2;

			String query="select count(*) from "+table+" where "+
					fld_kd_dat_type +"='"+KD_TYPE+"'";



			Cursor cur=db.conn.rawQuery(query, null);
			int result = 0;
			if (cur.moveToNext())
			{
				result = cur.getInt(0);

			}
			cur.close();
			return result;
		}
		catch(Exception ex)
		{
			return -1;
		}

	}

	public void decrementeStock(String numcde,Context act)
	{
		List<structLinCde> arts=Global.dbKDLinCde.load(numcde,false);
		for (int i=0;i<arts.size();i++)
		{
			structPassePlat pp=new structPassePlat();
			 load(pp, arts.get(i).PROCODE, new StringBuffer(),
					Preferences.getValue(act, Espresso.DEPOT,"0"),
					Preferences.getValue(act, Espresso.LOGIN,"0"),
					Preferences.getValue(act, Espresso.CODE_SOCIETE,"0"));
			
		 
		 
			int qteStock=0;
			
			if (arts.get(i).TYPEPIECE.equals(TableSouches.TYPEDOC_FACTURE) || 
					arts.get(i).TYPEPIECE.equals(TableSouches.TYPEDOC_BL)	||
					arts.get(i).TYPEPIECE.equals(TableSouches.TYPEDOC_AVOIR) )
				qteStock=Fonctions.convertToInt( pp.FIELD_LIGNEINV_QTETHEO)-
					((int)arts.get(i).QTECDE+(int)arts.get(i).QTEGR );
			
			else
				qteStock=Fonctions.convertToInt( pp.FIELD_LIGNEINV_QTETHEO)+
				((int)arts.get(i).QTECDE+(int)arts.get(i).QTEGR );
			
			
	 
			
			String update="update "+TABLENAME+
					" set "+FIELD_LIGNEINV_QTETHEO+"='"+qteStock+"' where "+
					FIELD_LIGNEINV_PROCODE+"='"+arts.get(i).PROCODE+"'"+
					" and "+fld_kd_dat_type +"='"+KD_TYPE+"'";
			
			db.execSQL(update, new StringBuilder());
			
			
		}
				
	}
	/**
	 * charge un article du panier de la cde numcde
	 * @author Marc VOUAUX
	 * @param ent
	 * @param numcde
	 * @param stBuf
	 * @return
	 */
	public boolean load(structPassePlat ent,String codeart,StringBuffer stBuf,String depot,String login,String soccode)
	{
	
		String query="SELECT * FROM "+
				TABLENAME_TEMP2+
				" where "+
				fld_kd_dat_type+"="+KD_TYPE+
				" and "+
				this.FIELD_LIGNEINV_PROCODE+"="+
				"'"+codeart+"'"
				;
		

		Cursor cur=db.conn.rawQuery (query,null);
		if (cur.moveToNext() && !codeart.equals(""))
		{
			fillStructLin(ent,cur);
	
		}
		else
		{
			dbSiteProduit.structArt art=new structArt();
			Global.dbProduit.getProduit(codeart, art, new StringBuilder());
			ent.FIELD_LIGNEINV_CODABAR=art.EAN;
			ent.FIELD_LIGNEINV_COMMENT1="";
			ent.FIELD_LIGNEINV_DATE=Fonctions.getYYYYMMDDhhmmss();
			ent.FIELD_LIGNEINV_DEPOT=depot;
			ent.FIELD_LIGNEINV_DESIGNATION=art.NOMART1;
			ent.FIELD_LIGNEINV_DLC="";
			ent.FIELD_LIGNEINV_LOT="";
			ent.FIELD_LIGNEINV_NUMLIGNE="";
			ent.FIELD_LIGNEINV_PROCODE=art.CODART;
			ent.FIELD_LIGNEINV_QTE="";
			ent.FIELD_LIGNEINV_QTETEMP="";
			ent.FIELD_LIGNEINV_QTETHEO="0";
			ent.FIELD_LIGNEINV_REPCODE=login;
			ent.FIELD_LIGNEINV_SERIE="";
			ent.FIELD_LIGNEINV_SOCCODE=soccode;
			ent.FIELD_LIGNEINV_TYPEPIECE="";
			ent.FIELD_LIGNEINV_UV=art.PCB;
					
			
			cur.close();
			return false;
		}
		cur.close();
		return true;
	}

	
	
	/*
	 * rempli la structure 
	 */
	void fillStructLin(structPassePlat ent,Cursor cur)
	{
	 
		ent.FIELD_LIGNEINV_DATE 	=this.giveFld(cur,this.FIELD_LIGNEINV_DATE 	);
	 
		ent.FIELD_LIGNEINV_PROCODE 		=this.giveFld(cur,this.FIELD_LIGNEINV_PROCODE 		);
	 
		ent.FIELD_LIGNEINV_QTETHEO 	=this.giveFld(cur,this.FIELD_LIGNEINV_QTETHEO 	);
		ent.FIELD_LIGNEINV_REPCODE 	=this.giveFld(cur,this.FIELD_LIGNEINV_REPCODE 	);
	 
		ent.FIELD_LIGNEINV_SOCCODE =this.giveFld(cur,this.FIELD_LIGNEINV_SOCCODE );
	 
	}
	 
 
	/**
	 * Efface les types lignes
	 * @author Marc VOUAUX
	 * @param type
	 * @param table
	 * @return
	 */
	public boolean clear(boolean inTemp,StringBuilder err)
	{
		try
		{
			String table=TABLENAME;
			if (inTemp)
				table=TABLENAME_TEMP2;
			super.clear(db, KD_TYPE, table, err);
			return true;
		}
		catch(Exception ex)
		{
			err.append(ex.getMessage());
			return false;
		}
	}
	
 
	 
}
