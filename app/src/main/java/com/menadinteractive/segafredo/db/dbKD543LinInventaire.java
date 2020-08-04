package com.menadinteractive.segafredo.db;

import java.util.ArrayList;
import java.util.List;

import android.R.string;
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
public class dbKD543LinInventaire extends dbKD {

	public final static int 	KD_TYPE= 543;
	public final String 		FIELD_LIGNEINV_SOCCODE 		= fld_kd_soc_code;
	public final static  String FIELD_LIGNEINV_PROCODE 		= fld_kd_pro_code;
	public final static  String FIELD_LIGNEINV_NUMDOC		= fld_kd_cde_code;//numero d'inv
	public final String 		FIELD_LIGNEINV_REPCODE 		= fld_kd_dat_idx01;
	public final String 		FIELD_LIGNEINV_DATE 		= fld_kd_dat_idx02;
	public final String 		FIELD_LIGNEINV_TYPEPIECE 	= fld_kd_dat_idx03;//(H)header , ((L) line
	public final String 		FIELD_LIGNEINV_CODABAR 		= fld_kd_dat_idx04;
	public final String 		FIELD_LIGNEINV_NUMLIGNE 	= fld_kd_dat_data01;//la ligne 0 fait aussi office d'ent�te
	public final String 		FIELD_LIGNEINV_DESIGNATION 	= fld_kd_dat_data02;
	public final String 		FIELD_LIGNEINV_UV 			= fld_kd_dat_data03;
	public final static String 	FIELD_LIGNEINV_QTE 			= fld_kd_dat_data04;
	public final String 		FIELD_LIGNEINV_QTETEMP 		= fld_kd_dat_data05;
	public final String 		FIELD_LIGNEINV_QTETHEO 		= fld_kd_dat_data06;
	public final String 		FIELD_LIGNEINV_DLC			= fld_kd_dat_data07;
	public final String 		FIELD_LIGNEINV_DEPOT 		= fld_kd_dat_data10;
	public final String 		FIELD_LIGNEINV_LOT 			= fld_kd_dat_data11;
	public final String 		FIELD_LIGNEINV_SERIE 		= fld_kd_dat_data12;
	public final String 		FIELD_LIGNEINV_COMMENT1 	= fld_kd_dat_data13; 
	public final String 		FIELD_LIGNEINV_DUO 			= fld_kd_dat_data14;//inventaire fait en duo 
	public final String 		FIELD_LIGNEINV_ISPRINT 		= fld_kd_dat_data15;//inventaire imprim� non modifiable

	
	static public final String TYPEPIECE_LIN="L";
	static public final String TYPEPIECE_HDR="H";
	
	MyDB db;
	public dbKD543LinInventaire(MyDB _db)
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
		public String FIELD_LIGNEINV_NUMDOC 		;
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
		public String FIELD_LIGNEINV_DUO;
		public String FIELD_LIGNEINV_ISPRINT;
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
						Global.dbKDLinInv.FIELD_LIGNEINV_QTE+"<>''"+
						" and "+
						 this.FIELD_LIGNEINV_TYPEPIECE+"='"+TYPEPIECE_LIN+  "'";



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
				this.FIELD_LIGNEINV_PROCODE+"="+
				"'"+codeart+"'"+
				" and "+
				this.FIELD_LIGNEINV_TYPEPIECE+"='"+TYPEPIECE_LIN+  "'";
		

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
		ent.FIELD_LIGNEINV_CODABAR 	=this.giveFld(cur,this.FIELD_LIGNEINV_CODABAR	);
		ent.FIELD_LIGNEINV_COMMENT1 	=this.giveFld(cur,this.FIELD_LIGNEINV_COMMENT1 	);
		ent.FIELD_LIGNEINV_DATE 	=this.giveFld(cur,this.FIELD_LIGNEINV_DATE 	);
		ent.FIELD_LIGNEINV_DESIGNATION 	=this.giveFld(cur,this.FIELD_LIGNEINV_DESIGNATION 	);
		ent.FIELD_LIGNEINV_LOT 	=this.giveFld(cur,this.FIELD_LIGNEINV_LOT 	);
		ent.FIELD_LIGNEINV_NUMLIGNE 	=this.giveFld(cur,this.FIELD_LIGNEINV_NUMLIGNE 	);
		ent.FIELD_LIGNEINV_PROCODE 		=this.giveFld(cur,this.FIELD_LIGNEINV_PROCODE 		);
		ent.FIELD_LIGNEINV_QTE 	=this.giveFld(cur,this.FIELD_LIGNEINV_QTE 	);
		ent.FIELD_LIGNEINV_QTETHEO 	=this.giveFld(cur,this.FIELD_LIGNEINV_QTETHEO 	);
		ent.FIELD_LIGNEINV_REPCODE 	=this.giveFld(cur,this.FIELD_LIGNEINV_REPCODE 	);
		ent.FIELD_LIGNEINV_SERIE 	=this.giveFld(cur,this.FIELD_LIGNEINV_SERIE 	);
		ent.FIELD_LIGNEINV_SOCCODE =this.giveFld(cur,this.FIELD_LIGNEINV_SOCCODE );
		ent.FIELD_LIGNEINV_TYPEPIECE 			=this.giveFld(cur,this.FIELD_LIGNEINV_TYPEPIECE 			);
		ent.FIELD_LIGNEINV_UV 			=this.giveFld(cur,this.FIELD_LIGNEINV_UV 			);
		ent.FIELD_LIGNEINV_DEPOT			=this.giveFld(cur,this.FIELD_LIGNEINV_DEPOT 			);
		ent.FIELD_LIGNEINV_DUO			=this.giveFld(cur,this.FIELD_LIGNEINV_DUO			);
		ent.FIELD_LIGNEINV_ISPRINT		=this.giveFld(cur,this.FIELD_LIGNEINV_ISPRINT			);
		ent.FIELD_LIGNEINV_NUMDOC		=this.giveFld(cur,this.FIELD_LIGNEINV_NUMDOC			);
	}
	 
	public List<structPassePlat> load()
	{

		String query="SELECT * FROM "+
				TABLENAME_TEMP2+
				" where "+
				fld_kd_dat_type+"="+KD_TYPE+
				" and "+
						Global.dbKDLinInv.FIELD_LIGNEINV_QTE+"<>''"+
						" and "+
						 this.FIELD_LIGNEINV_TYPEPIECE+"='"+TYPEPIECE_LIN+  "' "+
				" order by "+
				this.FIELD_LIGNEINV_NUMLIGNE;

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
				FIELD_LIGNEINV_TYPEPIECE+"='"+TYPEPIECE_HDR+ "'";
				 

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
					this.FIELD_LIGNEINV_PROCODE+"="+
					"'"+codeart+"'";
			
	

			Cursor cur=db.conn.rawQuery (query,null);
			if (cur.moveToNext())
			{

				query="UPDATE "+TABLENAME_TEMP2+
						" set "+
						this.FIELD_LIGNEINV_CODABAR+"="+
						"'"+ent.FIELD_LIGNEINV_CODABAR+"',"+
						this.FIELD_LIGNEINV_NUMDOC+"="+
						"'"+ent.FIELD_LIGNEINV_NUMDOC+"',"+
						this.FIELD_LIGNEINV_COMMENT1+"="+
						"'"+ent.FIELD_LIGNEINV_COMMENT1 +"',"+
						this.FIELD_LIGNEINV_DATE+"="+
						"'"+ent.FIELD_LIGNEINV_DATE+"',"+
						this.FIELD_LIGNEINV_DESIGNATION+"="+
						"'"+MyDB.controlFld(ent.FIELD_LIGNEINV_DESIGNATION)+"',"+
						this.FIELD_LIGNEINV_LOT+"="+
						"'"+ent.FIELD_LIGNEINV_LOT+"',"+
						this.FIELD_LIGNEINV_NUMLIGNE+"="+
						"'"+ent.FIELD_LIGNEINV_NUMLIGNE+"',"+
						this.FIELD_LIGNEINV_PROCODE+"="+
						"'"+ent.FIELD_LIGNEINV_PROCODE+"',"+
						this.FIELD_LIGNEINV_QTE+"="+
						"'"+MyDB.controlFld(ent.FIELD_LIGNEINV_QTE)+"',"+
						this.FIELD_LIGNEINV_QTETHEO+"="+
						"'"+MyDB.controlFld(ent.FIELD_LIGNEINV_QTETHEO)+"',"+
						this.FIELD_LIGNEINV_REPCODE+"="+
						"'"+MyDB.controlFld(ent.FIELD_LIGNEINV_REPCODE)+"',"+
						this.FIELD_LIGNEINV_SERIE+"="+
						"'"+MyDB.controlFld(ent.FIELD_LIGNEINV_SERIE)+"',"+
						this.FIELD_LIGNEINV_SOCCODE+"="+
						"'"+ent.FIELD_LIGNEINV_SOCCODE+"',"+
						this.FIELD_LIGNEINV_TYPEPIECE+"="+
						"'"+ent.FIELD_LIGNEINV_TYPEPIECE+"',"+
						this.FIELD_LIGNEINV_DEPOT+"="+
						"'"+ent.FIELD_LIGNEINV_DEPOT+"',"+
						this.FIELD_LIGNEINV_UV+"="+
						"'"+ent.FIELD_LIGNEINV_UV+"' "+
						
						" where "+
						fld_kd_dat_type+"="+KD_TYPE+
				

		  			" and "+
		  			this.FIELD_LIGNEINV_PROCODE+"="+
		  			"'"+codeart+"'";
				
				
				db.conn.execSQL(query);
				
			

				Global.dbLog.Insert("Inv","Save Update","","Requete: "+query, "","");
			}
			else		  
			{	  		
				query="INSERT INTO " + TABLENAME_TEMP2 +" ("+
						dbKD.fld_kd_dat_type+","+
						this.FIELD_LIGNEINV_CODABAR 	+","+
						this.FIELD_LIGNEINV_NUMDOC 	+","+
						this.FIELD_LIGNEINV_COMMENT1 	+","+
						this.FIELD_LIGNEINV_DATE 	+","+   	  		
						this.FIELD_LIGNEINV_DESIGNATION 	+","+
						this.FIELD_LIGNEINV_LOT 	+","+
						this.FIELD_LIGNEINV_NUMLIGNE 	+","+
						this.FIELD_LIGNEINV_PROCODE 		+","+
						this.FIELD_LIGNEINV_QTE 	+","+
						this.FIELD_LIGNEINV_QTETHEO 	+","+
						this.FIELD_LIGNEINV_REPCODE 	+","+
						this.FIELD_LIGNEINV_SERIE 	+","+
						this.FIELD_LIGNEINV_SOCCODE +","+
						this.FIELD_LIGNEINV_TYPEPIECE 			+","+
						this.FIELD_LIGNEINV_DEPOT 			+","+
						this.FIELD_LIGNEINV_UV 		+""+
						
						") VALUES ("+
						String.valueOf(KD_TYPE)+","+
						"'"+MyDB.controlFld(ent.FIELD_LIGNEINV_CODABAR)	+"',"+
						"'"+MyDB.controlFld(ent.FIELD_LIGNEINV_NUMDOC)	+"',"+
						"'"+MyDB.controlFld(ent.FIELD_LIGNEINV_COMMENT1 )	+"',"+
						"'"+MyDB.controlFld(ent.FIELD_LIGNEINV_DATE )	+"',"+
						"'"+MyDB.controlFld(ent.FIELD_LIGNEINV_DESIGNATION )	+"',"+
						"'"+MyDB.controlFld(ent.FIELD_LIGNEINV_LOT )	+"',"+
						"'"+ent.FIELD_LIGNEINV_NUMLIGNE 		+"',"+
						"'"+ent.FIELD_LIGNEINV_PROCODE 	+"',"+
						"'"+ent.FIELD_LIGNEINV_QTE 	+"',"+
						"'"+ent.FIELD_LIGNEINV_QTETHEO 	+"',"+
						"'"+ent.FIELD_LIGNEINV_REPCODE 	+"',"+
						"'"+MyDB.controlFld(ent.FIELD_LIGNEINV_SERIE )+"',"+
						"'"+MyDB.controlFld(ent.FIELD_LIGNEINV_SOCCODE 		)	+"',"+
						"'"+ent.FIELD_LIGNEINV_TYPEPIECE 		+"',"+
						"'"+ent.FIELD_LIGNEINV_DEPOT		+"',"+
						"'"+ent.FIELD_LIGNEINV_UV 		+"'"+
						
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
	public boolean save2(structPassePlat ent,String codeart, StringBuffer stBuf)
	{
		try
		{
			String query="SELECT * FROM "+
					TABLENAME_TEMP2+
					" where "+
					fld_kd_dat_type+"="+KD_TYPE+

					" and "+
					this.FIELD_LIGNEINV_PROCODE+"="+
					"'"+codeart+"'";
			
	

			Cursor cur=db.conn.rawQuery (query,null);
			if (cur.moveToNext())
			{

				query="UPDATE "+TABLENAME_TEMP2+
						" set "+
						
						this.FIELD_LIGNEINV_QTETHEO+"="+
						"'"+MyDB.controlFld(ent.FIELD_LIGNEINV_QTETHEO)+"'"+
						
						
						" where "+
						fld_kd_dat_type+"="+KD_TYPE+
				

		  			" and "+
		  			this.FIELD_LIGNEINV_PROCODE+"="+
		  			"'"+codeart+"'";
				
				
				db.conn.execSQL(query);
				
			

				Global.dbLog.Insert("Inv","Save2 Update","","Requete: "+query, "","");
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
					this.FIELD_LIGNEINV_TYPEPIECE+"="+
					"'"+TYPEPIECE_HDR+"'";
			
	

			Cursor cur=db.conn.rawQuery (query,null);
			if (cur.moveToNext())
			{

				query="UPDATE "+TABLENAME_TEMP2+
						" set "+
						this.FIELD_LIGNEINV_NUMDOC+"="+
						"'"+ent.FIELD_LIGNEINV_NUMDOC+"',"+
						this.FIELD_LIGNEINV_DUO+"="+
						"'"+ent.FIELD_LIGNEINV_DUO+"',"+
						this.FIELD_LIGNEINV_DEPOT+"="+
						"'"+ent.FIELD_LIGNEINV_DEPOT+"',"+
						this.FIELD_LIGNEINV_COMMENT1+"="+
						"'"+ent.FIELD_LIGNEINV_COMMENT1 +"',"+
						this.FIELD_LIGNEINV_DATE+"="+
						"'"+ent.FIELD_LIGNEINV_DATE+"'"+
						 
						
						" where "+
						fld_kd_dat_type+"="+KD_TYPE+
				

		  				" and "+
					this.FIELD_LIGNEINV_TYPEPIECE+"="+
					"'"+TYPEPIECE_HDR+"'";
				
				
				db.conn.execSQL(query);
				
			

				Global.dbLog.Insert("Inv","Save Update","","Requete: "+query, "","");
			}
			else		  
			{	  		
				query="INSERT INTO " + TABLENAME_TEMP2 +" ("+
						dbKD.fld_kd_dat_type+","+
			 
						this.FIELD_LIGNEINV_NUMDOC 	+","+
						this.FIELD_LIGNEINV_COMMENT1 	+","+
						this.FIELD_LIGNEINV_DATE 	+","+   	  		
						this.FIELD_LIGNEINV_DUO 	+","+
						this.FIELD_LIGNEINV_DEPOT	+","+
						this.FIELD_LIGNEINV_TYPEPIECE	+","+
						this.FIELD_LIGNEINV_REPCODE 	+""+
						 
						
						") VALUES ("+
						String.valueOf(KD_TYPE)+","+
				 
						"'"+MyDB.controlFld(ent.FIELD_LIGNEINV_NUMDOC)	+"',"+
						"'"+MyDB.controlFld(ent.FIELD_LIGNEINV_COMMENT1 )	+"',"+
						"'"+MyDB.controlFld(ent.FIELD_LIGNEINV_DATE )	+"',"+
						"'"+MyDB.controlFld(ent.FIELD_LIGNEINV_DUO)	+"',"+
						"'"+MyDB.controlFld(ent.FIELD_LIGNEINV_DEPOT)	+"',"+
						"'"+TYPEPIECE_HDR	+"',"+
						"'"+MyDB.controlFld(ent.FIELD_LIGNEINV_REPCODE)	+"'"+
						 
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
						this.FIELD_LIGNEINV_ISPRINT+"="+
						"'true' " +
						
						" where "+
						fld_kd_dat_type+"="+KD_TYPE+
				

		  				" and "+
					this.FIELD_LIGNEINV_TYPEPIECE+"="+
					"'"+TYPEPIECE_HDR+"'";
				
				
				db.conn.execSQL(query);
 
		}
		catch(Exception ex)
		{
	 
			return false;
		}

		return true;
	}
	public boolean delete(String numcde, String codeart,String numlin,StringBuffer err,String codePanachage)
	{
		try
		{
			
			
			
			String query="DELETE from "+
					TABLENAME_TEMP2+		
					" where "+
					fld_kd_dat_type+"="+KD_TYPE+" and "+
					this.FIELD_LIGNEINV_PROCODE+
					"='"+codeart+"' "
					;
		
			db.conn.execSQL(query);
			//Global.dbLog.Insert("Ligne","Delete","","Numcde: "+numcde+" - code article: "+ codeart, "","");
			Global.dbLog.Insert("Inv","Delete","","Requete: "+query, "","");
			
			return true;
		}
		catch(Exception ex)
		{
			err.append(ex.getMessage());
		}
		return false;
	}
	 
	//inventaire transmis, on met � jour les zones
	public boolean Reset(String msg)
	{
		try {
/*			String query="UPDATE "+TABLENAME_TEMP2+
					" set "+
					this.FIELD_LIGNEINV_QTETHEO+"="+this.FIELD_LIGNEINV_QTE+","+
					this.FIELD_LIGNEINV_QTETEMP+"=''," +
					this.FIELD_LIGNEINV_QTE+"=''"+		
					" where "+
					fld_kd_dat_type+"="+KD_TYPE+
					" and "+
					FIELD_LIGNEINV_QTE+"<>''";
			
			db.conn.execSQL(query);

			//on efface les lignes dont le stock = 0, car plus dans le camion
			query="delete from "+
				TABLENAME+
				" where "+
				fld_kd_dat_type+"="+KD_TYPE+
				" and "+
				FIELD_LIGNEINV_QTETHEO+"='0'";
					
			db.conn.execSQL(query);
			*/
			//on efface les lignes dont le stock = 0, car plus dans le camion
			String query="delete from "+
				TABLENAME+
				" where "+
				fld_kd_dat_type+"="+KD_TYPE;
				
					
			db.conn.execSQL(query);
			
			Global.dbLog.Insert("Inv",msg,"","Requete: "+query, "","");
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
	public boolean isInventaireEnCours()
	{
		String query="SELECT * FROM "+
				TABLENAME_TEMP2+
				" where "+
				fld_kd_dat_type+"="+KD_TYPE+

				" and "+
				this.FIELD_LIGNEINV_QTE+"<>''";
				;
		
		Cursor cur=db.conn.rawQuery (query,null);
		if (cur.moveToNext())
		{
			return true;
		}
		return false;
	}

	
	/*
	 * on controle que toutes les lignes d'inventaires sont bien renseign�e
	 */
	public boolean isInventaireTermine()
	{
		String query="SELECT count(*) cnt FROM "+
				TABLENAME_TEMP2+ " theo "+
				" inner join "+
				TABLENAME_TEMP2+" inv "+
				" on inv."+FIELD_LIGNEINV_PROCODE+"=theo."+dbKD545StockTheoSrv.FIELD_LIGNEINV_PROCODE +
			
				" where "+
				" inv."+fld_kd_dat_type+"="+KD_TYPE+
				" and theo."+fld_kd_dat_type+"="+dbKD545StockTheoSrv.KD_TYPE+
				" and "+
				" inv."+this.FIELD_LIGNEINV_TYPEPIECE+"='"+TYPEPIECE_LIN+  "'"+
				" and "+
				" inv."+this.FIELD_LIGNEINV_QTE+"<>''";
		
		Cursor cur=db.conn.rawQuery (query,null);
		if (cur.moveToNext())
		{
			int cntinv=Fonctions.convertToInt(this.giveFld(cur,"cnt"	));
			
			
			cur.close();
			
			dbKD545StockTheoSrv theo=new dbKD545StockTheoSrv(db);
			
			int cntStockAInv=theo.Count(false);
			
			if (cntStockAInv==cntinv)
				return true;
			
			return false;
		}
		return false;
	}

	
	
	
	//l'inventaire doitêtre réalisé à partir du 25 du mois
	public static boolean isTimeToDoInvent(Context c)
	{
		String datederinv=Fonctions.GetProfileString(c, Espresso.REG_DATEDERINV, "");
		String datederinv_rappel=Fonctions.GetProfileString(c, Espresso.REG_DATEDERINV_RAPPEL, "");
		String today=Fonctions.getYYYYMMDD();
		
		//si on a deja rappelé aujourd'hui, on ne rappel pas
		if (datederinv_rappel.equals(today))
			return false;
		
	 	int yy_derinv=Fonctions.convertToInt(Fonctions.Mid(datederinv, 0, 4));
		int mm_derinv=Fonctions.convertToInt(Fonctions.Mid(datederinv, 4, 2));
		int dd_derinv=Fonctions.convertToInt(Fonctions.Mid(datederinv, 6, 2));
		
		int yy_today=Fonctions.convertToInt(Fonctions.Mid(today, 0, 4));
		int mm_today=Fonctions.convertToInt(Fonctions.Mid(today, 4, 2));
		int dd_today=Fonctions.convertToInt(Fonctions.Mid(today, 6, 2));
		
		Fonctions.WriteProfileString(c, Espresso.REG_DATEDERINV_RAPPEL, today);
		if (mm_today>=25)
		{
			if (mm_derinv!=mm_today)
				return true;
			
			if (yy_derinv!=yy_today)
				return true;
			
			if (mm_derinv<25)
				return true;
		}
		
		return false;
		 
		
	}
}
