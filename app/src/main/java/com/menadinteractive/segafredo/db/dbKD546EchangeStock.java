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
import com.menadinteractive.segafredo.db.dbKD84LinCde.structLinCde;
import com.menadinteractive.segafredo.db.dbSiteProduit.structArt;
import com.menadinteractive.segafredo.plugins.Espresso;



/**
 * Vue ligne de cde dans la table KD
 * @author Marc VOUAUX
 *
 */
public class dbKD546EchangeStock extends dbKD {

	public final static int 	KD_TYPE= 546;
	public final String 		FIELD_SOCCODE 		= fld_kd_soc_code;
	public final static  String FIELD_PROCODE 		= fld_kd_pro_code;
	public final static  String FIELD_NUMDOC		= fld_kd_cde_code;//numero d'inv
	public final String 		FIELD_REPCODE 		= fld_kd_dat_idx01;
	public final String 		FIELD_DATE 		= fld_kd_dat_idx02;
	public final String 		FIELD_TYPEPIECE 	= fld_kd_dat_idx03;//(H)header , ((L) line
	public final String 		FIELD_CODABAR 		= fld_kd_dat_idx04;
	public final String 		FIELD_NUMLIGNE 	= fld_kd_dat_data01;//la ligne 0 fait aussi office d'ent�te
	public final String 		FIELD_DESIGNATION 	= fld_kd_dat_data02;
	public final String 		FIELD_UV 			= fld_kd_dat_data03;
	public final static String 	FIELD_QTE 			= fld_kd_dat_data04;
	public final String 		FIELD_DLC			= fld_kd_dat_data07;
	public final String 		FIELD_DEPOT 		= fld_kd_dat_data10;
	public final String 		FIELD_LOT 			= fld_kd_dat_data11;
	public final String 		FIELD_SERIE 		= fld_kd_dat_data12;
	public final String 		FIELD_COMMENT1 	= fld_kd_dat_data13; 
	public final String 		FIELD_RECEVANT 			= fld_kd_dat_data14;//Code recvant 
	public final String 		FIELD_ISPRINT 		= fld_kd_dat_data15;//inventaire imprim� non modifiable

	
	static public final String TYPEPIECE_LIN="L";
	static public final String TYPEPIECE_HDR="H";
	
	MyDB db;
	public dbKD546EchangeStock(MyDB _db)
	{
		super();
		db=_db;	
	}

	 static public class structPassePlat {
		public structPassePlat()
		{	
			FIELD_SOCCODE 		="";
			FIELD_PROCODE 		="";
			FIELD_REPCODE 		="";
			FIELD_DATE 		="";
			FIELD_TYPEPIECE 	="";
			FIELD_CODABAR 		="";
			FIELD_NUMLIGNE 	="";
			FIELD_DESIGNATION 	="";
			FIELD_UV 			="";
			FIELD_QTE 			="";
 
            FIELD_LOT 			="";
			FIELD_SERIE 		="";
			FIELD_COMMENT1 		="";
			FIELD_DLC="";
			FIELD_DEPOT="";
		}

		public String FIELD_SOCCODE 		;
		public String FIELD_PROCODE 		;
		public String FIELD_NUMDOC 		;
		public String FIELD_REPCODE 		;
		public String FIELD_DATE 		;
		public String FIELD_TYPEPIECE 	;
		public String FIELD_CODABAR 	;	
		public String FIELD_NUMLIGNE 	;
		public String FIELD_DESIGNATION ;	
		public String FIELD_UV 			;
		public String FIELD_QTE 			;
		public String FIELD_LOT 			;
		public String FIELD_SERIE 		;
		public String FIELD_COMMENT1 		;
 
		public String FIELD_DLC;
		public String FIELD_DEPOT;
		public String FIELD_RECEVANT;
		public String FIELD_ISPRINT;
	}

	public int Count(boolean inTmp)
	{

		try
		{
			String table=TABLENAME;
			if (inTmp)
				table=TABLENAME_TEMP2;

			String query="select count(*) from "+table+" where "+
					fld_kd_dat_type +"='"+KD_TYPE+"'"+
					 
						" and "+
						 this.FIELD_TYPEPIECE+"='"+TYPEPIECE_LIN+  "'";



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
				this.FIELD_PROCODE+"="+
				"'"+codeart+"'"+
				" and "+
				this.FIELD_TYPEPIECE+"='"+TYPEPIECE_LIN+  "'";
		

		Cursor cur=db.conn.rawQuery (query,null);
		if (cur.moveToNext() && !codeart.equals(""))
		{
			fillStructLin(ent,cur);
	
		}
		else
		{
			dbSiteProduit.structArt art=new structArt();
			Global.dbProduit.getProduit(codeart, art, new StringBuilder());
			ent.FIELD_CODABAR=art.EAN;
			ent.FIELD_COMMENT1="";
			ent.FIELD_DATE=Fonctions.getYYYYMMDDhhmmss();
			ent.FIELD_DEPOT=depot;
			ent.FIELD_DESIGNATION=art.NOMART1;
			ent.FIELD_DLC="";
			ent.FIELD_LOT="";
			ent.FIELD_NUMLIGNE="";
			ent.FIELD_PROCODE=art.CODART;
			ent.FIELD_QTE="";
	 
			ent.FIELD_REPCODE=login;
			ent.FIELD_SERIE="";
			ent.FIELD_SOCCODE=soccode;
			ent.FIELD_TYPEPIECE="";
			ent.FIELD_UV=art.PCB;
					
			
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
		ent.FIELD_CODABAR 	=this.giveFld(cur,this.FIELD_CODABAR	);
		ent.FIELD_COMMENT1 	=this.giveFld(cur,this.FIELD_COMMENT1 	);
		ent.FIELD_DATE 	=this.giveFld(cur,this.FIELD_DATE 	);
		ent.FIELD_DESIGNATION 	=this.giveFld(cur,this.FIELD_DESIGNATION 	);
		ent.FIELD_LOT 	=this.giveFld(cur,this.FIELD_LOT 	);
		ent.FIELD_NUMLIGNE 	=this.giveFld(cur,this.FIELD_NUMLIGNE 	);
		ent.FIELD_PROCODE 		=this.giveFld(cur,this.FIELD_PROCODE 		);
		ent.FIELD_QTE 	=this.giveFld(cur,this.FIELD_QTE 	);
 
		ent.FIELD_REPCODE 	=this.giveFld(cur,this.FIELD_REPCODE 	);
		ent.FIELD_SERIE 	=this.giveFld(cur,this.FIELD_SERIE 	);
		ent.FIELD_SOCCODE =this.giveFld(cur,this.FIELD_SOCCODE );
		ent.FIELD_TYPEPIECE 			=this.giveFld(cur,this.FIELD_TYPEPIECE 			);
		ent.FIELD_UV 			=this.giveFld(cur,this.FIELD_UV 			);
		ent.FIELD_DEPOT			=this.giveFld(cur,this.FIELD_DEPOT 			);
		ent.FIELD_RECEVANT			=this.giveFld(cur,this.FIELD_RECEVANT			);
		ent.FIELD_ISPRINT		=this.giveFld(cur,this.FIELD_ISPRINT			);
		ent.FIELD_NUMDOC		=this.giveFld(cur,this.FIELD_NUMDOC			);
	}
	 
	public List<structPassePlat> load()
	{

		String query="SELECT * FROM "+
				TABLENAME_TEMP2+
				" where "+
				fld_kd_dat_type+"="+KD_TYPE+
				" and "+
					 
						 this.FIELD_TYPEPIECE+"='"+TYPEPIECE_LIN+  "' "+
				" order by "+
				this.FIELD_NUMLIGNE;

		List<structPassePlat> lines=new ArrayList<structPassePlat>();
		
		Cursor cur=db.conn.rawQuery (query,null);
		while (cur.moveToNext() )
		{
			structPassePlat ent=new structPassePlat();

			fillStructLin(ent,cur);
			lines.add(ent);
		}
		
		cur.close();
		return lines;
	}
	
	
	public  structPassePlat loadHdr()
	{

		String query="SELECT * FROM "+
				TABLENAME_TEMP2+
				" where "+
				fld_kd_dat_type+"="+KD_TYPE+
				" and "+
				FIELD_TYPEPIECE+"='"+TYPEPIECE_HDR+ "'";
				 

		 structPassePlat lin=null;
		
		 Cursor cur=db.conn.rawQuery (query,null);
		if (cur.moveToNext() )
		{
			lin=new  structPassePlat();

			fillStructLin(lin,cur);
		 
		}
		
		cur.close();
		return lin;
	}

	/**
	 * @author Marc VOUAUX
	 * @param ent
	 * @param numcde
	 * @param stBuf
	 * @return
	 */
	public boolean save(structPassePlat ent,String codeart, StringBuffer stBuf)
	{
		try
		{
		 
			
			String query="SELECT * FROM "+
					TABLENAME_TEMP2+
					" where "+
					fld_kd_dat_type+"="+KD_TYPE+

					" and "+
					this.FIELD_PROCODE+"="+
					"'"+codeart+"'";
			
	

			Cursor cur=db.conn.rawQuery (query,null);
			if (cur.moveToNext())
			{

				query="UPDATE "+TABLENAME_TEMP2+
						" set "+
						this.FIELD_CODABAR+"="+
						"'"+ent.FIELD_CODABAR+"',"+
						this.FIELD_NUMDOC+"="+
						"'"+ent.FIELD_NUMDOC+"',"+
						this.FIELD_COMMENT1+"="+
						"'"+ent.FIELD_COMMENT1 +"',"+
						this.FIELD_DATE+"="+
						"'"+ent.FIELD_DATE+"',"+
						this.FIELD_DESIGNATION+"="+
						"'"+MyDB.controlFld(ent.FIELD_DESIGNATION)+"',"+
						this.FIELD_LOT+"="+
						"'"+ent.FIELD_LOT+"',"+
						this.FIELD_NUMLIGNE+"="+
						"'"+ent.FIELD_NUMLIGNE+"',"+
						this.FIELD_PROCODE+"="+
						"'"+ent.FIELD_PROCODE+"',"+
						this.FIELD_QTE+"="+
						"'"+MyDB.controlFld(ent.FIELD_QTE)+"',"+
			 
						this.FIELD_REPCODE+"="+
						"'"+MyDB.controlFld(ent.FIELD_REPCODE)+"',"+
						this.FIELD_SERIE+"="+
						"'"+MyDB.controlFld(ent.FIELD_SERIE)+"',"+
						this.FIELD_SOCCODE+"="+
						"'"+ent.FIELD_SOCCODE+"',"+
						this.FIELD_TYPEPIECE+"="+
						"'"+ent.FIELD_TYPEPIECE+"',"+
						this.FIELD_DEPOT+"="+
						"'"+ent.FIELD_DEPOT+"',"+
						this.FIELD_UV+"="+
						"'"+ent.FIELD_UV+"' "+
						
						" where "+
						fld_kd_dat_type+"="+KD_TYPE+
				

		  			" and "+
		  			this.FIELD_PROCODE+"="+
		  			"'"+codeart+"'";
				
				
				db.conn.execSQL(query);
				
			

				Global.dbLog.Insert("Inv","Save Update","","Requete: "+query, "","");
			}
			else		  
			{	  		
				query="INSERT INTO " + TABLENAME_TEMP2 +" ("+
						dbKD.fld_kd_dat_type+","+
						this.FIELD_CODABAR 	+","+
						this.FIELD_NUMDOC 	+","+
						this.FIELD_COMMENT1 	+","+
						this.FIELD_DATE 	+","+   	  		
						this.FIELD_DESIGNATION 	+","+
						this.FIELD_LOT 	+","+
						this.FIELD_NUMLIGNE 	+","+
						this.FIELD_PROCODE 		+","+
						this.FIELD_QTE 	+","+
				 
						this.FIELD_REPCODE 	+","+
						this.FIELD_SERIE 	+","+
						this.FIELD_SOCCODE +","+
						this.FIELD_TYPEPIECE 			+","+
						this.FIELD_DEPOT 			+","+
						this.FIELD_UV 		+""+
						
						") VALUES ("+
						String.valueOf(KD_TYPE)+","+
						"'"+MyDB.controlFld(ent.FIELD_CODABAR)	+"',"+
						"'"+MyDB.controlFld(ent.FIELD_NUMDOC)	+"',"+
						"'"+MyDB.controlFld(ent.FIELD_COMMENT1 )	+"',"+
						"'"+MyDB.controlFld(ent.FIELD_DATE )	+"',"+
						"'"+MyDB.controlFld(ent.FIELD_DESIGNATION )	+"',"+
						"'"+MyDB.controlFld(ent.FIELD_LOT )	+"',"+
						"'"+ent.FIELD_NUMLIGNE 		+"',"+
						"'"+ent.FIELD_PROCODE 	+"',"+
						"'"+ent.FIELD_QTE 	+"',"+
					 
						"'"+ent.FIELD_REPCODE 	+"',"+
						"'"+MyDB.controlFld(ent.FIELD_SERIE )+"',"+
						"'"+MyDB.controlFld(ent.FIELD_SOCCODE 		)	+"',"+
						"'"+ent.FIELD_TYPEPIECE 		+"',"+
						"'"+ent.FIELD_DEPOT		+"',"+
						"'"+ent.FIELD_UV 		+"'"+
						
						")";

				db.conn.execSQL(query);
				//on met � kour le RL

				
				//Global.dbLog.Insert("Ligne","Save","","Numcde: "+numcde+" - code article: "+ ent.PROCODE+" - QTECDE: "+ ent.QTECDE+" - QTEGR: "+ ent.QTEGR+" - PRIXMODIF: "+ ent.PRIXMODIF+" - REMISEMODIF: "+ ent.REMISEMODIF+" - MNTTOTALHT: "+ent.MNTTOTALHT, "","");
				Global.dbLog.Insert("Inv","Save Insert","","Requete: "+query, "","");
				
			}
			cur.close();
		}
		catch(Exception ex)
		{
			stBuf.setLength(0);
			stBuf.append(ex.getMessage());
			return false;
		}

		return true;
	}
	 
	 
	public boolean saveHdr(structPassePlat ent )
	{
		try
		{
		 
			
			String query="SELECT * FROM "+
					TABLENAME_TEMP2+
					" where "+
					fld_kd_dat_type+"="+KD_TYPE+

					" and "+
					this.FIELD_TYPEPIECE+"="+
					"'"+TYPEPIECE_HDR+"'";
			
	

			Cursor cur=db.conn.rawQuery (query,null);
			if (cur.moveToNext())
			{

				query="UPDATE "+TABLENAME_TEMP2+
						" set "+
						this.FIELD_NUMDOC+"="+
						"'"+ent.FIELD_NUMDOC+"',"+
						this.FIELD_RECEVANT+"="+
						"'"+ent.FIELD_RECEVANT+"',"+
						this.FIELD_DEPOT+"="+
						"'"+ent.FIELD_DEPOT+"',"+
						this.FIELD_COMMENT1+"="+
						"'"+MyDB.controlFld(ent.FIELD_COMMENT1) +"',"+
						this.FIELD_DATE+"="+
						"'"+ent.FIELD_DATE+"'"+
						 
						
						" where "+
						fld_kd_dat_type+"="+KD_TYPE+
				

		  				" and "+
					this.FIELD_TYPEPIECE+"="+
					"'"+TYPEPIECE_HDR+"'";
				
				
				db.conn.execSQL(query);
				
			

				Global.dbLog.Insert("Inv","Save Update","","Requete: "+query, "","");
			}
			else		  
			{	  		
				query="INSERT INTO " + TABLENAME_TEMP2 +" ("+
						dbKD.fld_kd_dat_type+","+
			 
						this.FIELD_NUMDOC 	+","+
						this.FIELD_COMMENT1 	+","+
						this.FIELD_DATE 	+","+   	  		
						this.FIELD_RECEVANT 	+","+
						this.FIELD_DEPOT	+","+
						this.FIELD_TYPEPIECE	+","+
						this.FIELD_REPCODE 	+""+
						 
						
						") VALUES ("+
						String.valueOf(KD_TYPE)+","+
				 
						"'"+MyDB.controlFld(ent.FIELD_NUMDOC)	+"',"+
						"'"+MyDB.controlFld(ent.FIELD_COMMENT1 )	+"',"+
						"'"+MyDB.controlFld(ent.FIELD_DATE )	+"',"+
						"'"+MyDB.controlFld(ent.FIELD_RECEVANT)	+"',"+
						"'"+MyDB.controlFld(ent.FIELD_DEPOT)	+"',"+
						"'"+TYPEPIECE_HDR	+"',"+
						"'"+MyDB.controlFld(ent.FIELD_REPCODE)	+"'"+
						 
						")";

				db.conn.execSQL(query);
				//on met � kour le RL

				
				//Global.dbLog.Insert("Ligne","Save","","Numcde: "+numcde+" - code article: "+ ent.PROCODE+" - QTECDE: "+ ent.QTECDE+" - QTEGR: "+ ent.QTEGR+" - PRIXMODIF: "+ ent.PRIXMODIF+" - REMISEMODIF: "+ ent.REMISEMODIF+" - MNTTOTALHT: "+ent.MNTTOTALHT, "","");
				Global.dbLog.Insert("Inv","Save Insert","","Requete: "+query, "","");
				
			}
			cur.close();
		}
		catch(Exception ex)
		{
	 
			return false;
		}

		return true;
	}
	public boolean saveHdrPrint()
	{
		try
		{
		 
			
		 

				String query="UPDATE "+TABLENAME_TEMP2+
						" set "+
						this.FIELD_ISPRINT+"="+
						"'true' " +
						
						" where "+
						fld_kd_dat_type+"="+KD_TYPE+
				

		  				" and "+
					this.FIELD_TYPEPIECE+"="+
					"'"+TYPEPIECE_HDR+"'";
				
				
				db.conn.execSQL(query);
 
		}
		catch(Exception ex)
		{
	 
			return false;
		}

		return true;
	}
	public boolean delete(  String codeart )
	{
		try
		{
			
			
			
			String query="DELETE from "+
					TABLENAME_TEMP2+		
					" where "+
					fld_kd_dat_type+"="+KD_TYPE+" and "+
					this.FIELD_PROCODE+
					"='"+codeart+"' "
					;
		
			db.conn.execSQL(query);
			//Global.dbLog.Insert("Ligne","Delete","","Numcde: "+numcde+" - code article: "+ codeart, "","");
			Global.dbLog.Insert("Inv","Delete","","Requete: "+query, "","");
			
			return true;
		}
		catch(Exception ex)
		{
	 
		}
		return false;
	}
	 
	//inventaire transmis, on met � jour les zones
	public boolean Reset()
	{
		try {
			String query="delete from "+
				TABLENAME+
				" where "+
				fld_kd_dat_type+"="+KD_TYPE 
				  ;
					
			db.conn.execSQL(query);
			
			//on efface les lignes dont le stock = 0, car plus dans le camion
			query="delete from "+
				TABLENAME+
				" where "+
				fld_kd_dat_type+"="+KD_TYPE+
				" and "+
				FIELD_TYPEPIECE+"='"+ TYPEPIECE_HDR+"'";
					
			db.conn.execSQL(query);
			
			Global.dbLog.Insert("Inv","Reset","","Requete: "+query, "","");
		} catch (SQLException e) {
			return false;
		}

		return true;
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
	
	/*
	 * si au moins une colonne qt� est renseign�e c'est que l'on a un inventaire en cours
	 */
 

	public boolean isTermine()
	{
		String query="SELECT * FROM "+
				TABLENAME_TEMP2+
				" where "+
				fld_kd_dat_type+"="+KD_TYPE+

				" and "+
				this.FIELD_TYPEPIECE+"='"+TYPEPIECE_HDR+  "'";
				;
		
		Cursor cur=db.conn.rawQuery (query,null);
		if (cur.moveToNext())
		{
			return true;
		}
		return false;
	}
 
 
}
