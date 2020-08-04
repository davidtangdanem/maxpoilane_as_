package com.menadinteractive.segafredo.db;

import java.util.ArrayList;
import java.util.List;

import com.menadinteractive.segafredo.communs.Fonctions;
import com.menadinteractive.segafredo.communs.Global;

import android.database.Cursor;
import android.database.SQLException;



/**
 * Vue ligne de cde dans la table KD
 * @author Marc VOUAUX
 *
 */
public class dbKD84LinCde extends dbKD{

	public final static int KD_TYPE= 84;
	public final static String FIELD_LIGNECDE_SOCCODE 		= fld_kd_soc_code;
	public final static String FIELD_LIGNECDE_CLICODE 		= fld_kd_cli_code;
	public final static String FIELD_LIGNECDE_CDECODE 		= fld_kd_cde_code;
	public final static String FIELD_LIGNECDE_VISCODE 		= fld_kd_vis_code;
	public final static String FIELD_LIGNECDE_PROCODE 		= fld_kd_pro_code;
	public final static String FIELD_LIGNECDE_REPCODE 		= fld_kd_dat_idx01;
	public final static String FIELD_LIGNECDE_DATE 		= fld_kd_dat_idx02;
	public final static String FIELD_LIGNECDE_TYPEPIECE 	= fld_kd_dat_idx03;
	public final static String FIELD_LIGNECDE_CODABAR 		= fld_kd_dat_idx04;
	public final static String FIELD_LIGNECDE_CODEPANACHAGE= fld_kd_dat_idx05; //code panachage pour le tarif qt� group�
	public final static String FIELD_LIGNECDE_TYPECDEVALEUR= fld_kd_dat_idx06; //si - alors les qte sont en n�gatif ainsi que le montant
	public final static String FIELD_LIGNECDE_NUMLIGNE 	= fld_kd_dat_data01;
	public final static String FIELD_LIGNECDE_DESIGNATION 	= fld_kd_dat_data02;
	public final static String FIELD_LIGNECDE_UV 			= fld_kd_dat_data03;
	public final static String FIELD_LIGNECDE_QTECDE 		= fld_kd_dat_data04;
	public final static String FIELD_LIGNECDE_PRIX 		= fld_kd_dat_data05;
	public final static String FIELD_LIGNECDE_MNTUNITHT 	= fld_kd_dat_data06;
	public final static String FIELD_LIGNECDE_REMISE 		= fld_kd_dat_data07;
	public final static String FIELD_LIGNECDE_MNTUNITNETHT = fld_kd_dat_data08;
	public final static String FIELD_LIGNECDE_MNTTOTALHT 	= fld_kd_dat_data09;
	public final static String FIELD_LIGNECDE_MNTTOTALTTC 	= fld_kd_dat_data10;
	public final static String FIELD_LIGNECDE_LOT 			= fld_kd_dat_data11;
	public final static String FIELD_LIGNECDE_SERIE 		= fld_kd_dat_data12;
	public final static String FIELD_LIGNECDE_COMMENT1 	= fld_kd_dat_data13;    
	public final static String FIELD_LIGNECDE_QTEGR		= fld_kd_dat_data14;
	public final static String FIELD_LIGNECDE_PRIXMODIF	= fld_kd_dat_data15;
	public final static String FIELD_LIGNECDE_REMISEMODIF	= fld_kd_dat_data16;
	public final static String FIELD_LIGNECDE_MNTUNITTTC	= fld_kd_dat_data17;
	public final static String FIELD_LIGNECDE_NUMLIGNE_EXT	= fld_kd_dat_data18;
	public final static String FIELD_LIGNECDE_TAUX			= fld_kd_dat_data19;
	public final static String FIELD_LIGNECDE_CODETVA		= fld_kd_dat_data20;
	public final static String FIELD_LIGNECDE_TYPECHECKBOX	= fld_kd_dat_data21; //0 - rien, 1 - Bloquer remise - 2 - Prix promo
	public final static String FIELD_LIGNECDE_COMMENT2		= fld_kd_dat_data22; 
	public final static String FIELD_LIGNECDE_COMMENT3		= fld_kd_dat_data23; 
	public final static String FIELD_LIGNECDE_CFGNUMLIGNEMERE = fld_kd_dat_data24;//si article compos� par le configurateur, ligne mere associ�e
	public final static String FIELD_LIGNECDE_CFGPRODPERE = fld_kd_dat_data25;//code article enrichi ISCOMPOSE=N ou code art princ si ISCOMPOSE=O
	public final static String FIELD_LIGNECDE_CFGISCOMPOSE = fld_kd_dat_data26;//est ce un article configur� O/N
	public final static String FIELD_LIGNECDE_CFGCODEQUEST = fld_kd_dat_data27;//code 
	public final static String FIELD_LIGNECDE_SELECTCHOIXTARIF = fld_kd_dat_data28;//Choix Code Tarif ( combo ) 	
	public final static String FIELD_LIGNECDE_LINCHOIX1 = fld_kd_dat_data29;//PARAM_LINCHOIX1
	public final static String FIELD_LIGNECDE_LINCHOIX2 = fld_kd_dat_data30;//PARAM_LINCHOIX2
	public final static String FIELD_LIGNECDE_PRIXINITIAL = fld_kd_val_data31;// Prix initial
	public final static String FIELD_LIGNECDE_REMISEINITIAL = fld_kd_val_data32;//Remise initial
	public final static String FIELD_LIGNECDE_TAXE1 = fld_kd_val_data33;// taxe 1
	public final static String FIELD_LIGNECDE_TAXE2 = fld_kd_val_data34;// taxe 2
	public final static String FIELD_LIGNECDE_TAXE3 = fld_kd_val_data35;// taxe 3
	public final static String FIELD_LIGNECDE_MNTTAXE1 = fld_kd_val_data36;// montant taxe 1
	public final static String FIELD_LIGNECDE_MNTTAXE2 = fld_kd_val_data37;// montant taxe 2
	public final static String FIELD_LIGNECDE_MNTTAXE3 = fld_kd_val_data38;// montant taxe 3
	public final static String FIELD_LIGNECDE_MNTTVA = fld_kd_val_data39;// montant tva

	MyDB db;
	public dbKD84LinCde(MyDB _db)
	{
		super();
		db=_db;	
	}

	static public class structLinCde
	{
		public structLinCde()
		{	
			SOCCODE 		="";
			CLICODE 		="";
			CDECODE 		="";
			VISCODE 		="";
			PROCODE 		="";
			REPCODE 		="";
			DATE 			="";
			TYPEPIECE 		="";
			CODABAR 		="";
			NUMLIGNE 		="";
			DESIGNATION 	="";
			UV 				="";
			QTECDE 			=0;
			PRIX 			=0.0f;
			MNTUNITHT 		=0.0f;
			REMISE 			=0.0f;
			MNTUNITNETHT 	=0.0f;
			MNTTOTALHT 		=0.0f;
			MNTTOTALTTC 	=0.0f;
			LOT 			="";
			SERIE 			="";
			COMMENT1 		="";
			COMMENT2 		="";
			COMMENT3 		="";
			QTEGR			=0;
			PRIXMODIF		=0.0f;
			REMISEMODIF		=0.0f;
			MNTUNITTTC		=0.0f;
			TAXE1			=0.0f;
			TAXE2			=0.0f;
			TAXE3			=0.0f;
			MNTTAXE1		=0.0f;
			MNTTAXE2		=0.0f;
			MNTTAXE3		=0.0f;
			MNTTVA			=0.0f;
			NUMLIGNE_EXT	="";
			TAUX			=0.0f;
			CODETVA		="";
			TYPECHECKBOX	="";
			CODEPANACHAGE	="";
			FIELD_LIGNECDE_LINCHOIX1="";
			PRIXINITIAL 	= "" ;
			REMISEINITIAL 	= "" ;

		}

		public String SOCCODE 	;
		public String CLICODE 	;	
		public String CDECODE 	;	
		public String VISCODE 	;	
		public String PROCODE 	;	
		public String REPCODE 	;	
		public String DATE 		;
		public String TYPEPIECE ;	
		public String CODABAR 	;	
		public String NUMLIGNE 	;
		public String DESIGNATION; 	
		public String UV 			;
		//public int QTECDE 		;
		public float QTECDE 		;

		public Float PRIX 		;
		public Float MNTUNITHT ;	
		public Float REMISE 	;	
		public Float MNTUNITNETHT; 
		public Float MNTTOTALHT 	;
		public Float MNTTOTALTTC 	;
		public String LOT 			;
		public String SERIE 		;
		public String COMMENT1 		;
		public String COMMENT2 		;
		public String COMMENT3 		;
		//public int QTEGR	;
		public float QTEGR	;

		public Float PRIXMODIF	;
		public Float REMISEMODIF;	
		public Float MNTUNITTTC	;
		public String NUMLIGNE_EXT 	;
		public Float TAUX 	;
		public String CODETVA;
		public String TYPECHECKBOX;
		public String CODEPANACHAGE;
		public String TYPECDEVALEUR;
		public String FIELD_LIGNECDE_LINCHOIX1="";

		public String PRIXINITIAL  ;
		public String REMISEINITIAL  ;

		public Float MNTTVA;
		public Float TAXE1;
		public Float TAXE2;
		public Float TAXE3;
		
		public Float MNTTAXE1;
		public Float MNTTAXE2;
		public Float MNTTAXE3;
	}

	public int mCount(boolean inTmp)
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

	/**
	 * Count avec comme param�tre Numcde
	 */
	public int Count_Numcde(String Numcde,boolean inTmp)
	{

		try
		{
			String table=TABLENAME;
			if (inTmp)
				table=TABLENAME_DUPLICATA;

			String query="select count(*) from "+table+" where "+
					fld_kd_dat_type +"='"+KD_TYPE+"' AND "+
					this.FIELD_LIGNECDE_CDECODE+"="+
					"'"+Numcde+"' ";

			/*String query="select count(*) from "+table+" where "+
					fld_kd_dat_type +"='"+KD_TYPE+"' AND "+
					this.FIELD_LIGNECDE_CDECODE+"="+
					"'"+Numcde+"' and ( "+this.FIELD_LIGNECDE_CFGISCOMPOSE+"='O' or "+this.FIELD_LIGNECDE_CFGISCOMPOSE+"='' or "+this.FIELD_LIGNECDE_CFGISCOMPOSE+" is null )";

			 */

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
	public String  TestCount_Numcde(String Numcde,boolean inTmp)
	{

		try
		{
			String table=TABLENAME;
			if (inTmp)
				table=TABLENAME_TEMP2;

			String query="select * from "+table+" where "+
					fld_kd_dat_type +"='"+KD_TYPE+"' AND "+
					this.FIELD_LIGNECDE_CDECODE+"="+
					"'"+Numcde+"' ";

			/*String query="select count(*) from "+table+" where "+
					fld_kd_dat_type +"='"+KD_TYPE+"' AND "+
					this.FIELD_LIGNECDE_CDECODE+"="+
					"'"+Numcde+"' and ( "+this.FIELD_LIGNECDE_CFGISCOMPOSE+"='O' or "+this.FIELD_LIGNECDE_CFGISCOMPOSE+"='' or "+this.FIELD_LIGNECDE_CFGISCOMPOSE+" is null )";

			 */

			Cursor cur=db.conn.rawQuery(query, null);
			String result = "";
			if (cur.moveToNext())
			{
				result =
						giveFld(cur, FIELD_LIGNECDE_CFGISCOMPOSE);

			}
			cur.close();
			return result;
		}
		catch(Exception ex)
		{
			return "";
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
	public boolean load(structLinCde ent,String numcde,String codeart,String numlin,StringBuffer stBuf)
	{
		if (numlin==null) numlin="0";
		if(numcde == null)
			return true;
		if (numcde.equals("")) return true;

		String query="SELECT * FROM "+
				TABLENAME_TEMP2+
				" where "+
				fld_kd_dat_type+"="+KD_TYPE+
				" and "+
				this.FIELD_LIGNECDE_CDECODE+"="+
				"'"+numcde+"'"+
				" and "+
				this.FIELD_LIGNECDE_PROCODE+"="+
				"'"+codeart+"'";

		if (!numlin.equals("0"))
		{
			query+=" and "+
					this.FIELD_LIGNECDE_NUMLIGNE+"="+
					"'"+numlin+"'";
		}

		Cursor cur=db.conn.rawQuery (query,null);
		if (cur.moveToNext() && !numcde.equals(""))
		{
			fillStructLin(ent,cur);

		}
		else{
			cur.close();
			return false;
		}
		cur.close();
		return true;
	}

	public boolean loadQuestResponse(structLinCde ent,String numcde,String codeartEnrichi,String numlin,String codeQuest,String socCode)
	{
		if (numlin==null) numlin="0";
		if(numcde == null)
			return true;


		String query="SELECT * FROM "+
				TABLENAME_TEMP2+
				" where "+
				fld_kd_dat_type+"="+KD_TYPE+
				" and "+
				this.FIELD_LIGNECDE_CDECODE+"="+
				"'"+numcde+"'"+
				" and "+
				this.FIELD_LIGNECDE_CFGPRODPERE+"="+
				"'"+codeartEnrichi+"'"+
				" and "+
				this.FIELD_LIGNECDE_CFGNUMLIGNEMERE+"="+
				"'"+numlin+"'"+
				" and "+
				this.FIELD_LIGNECDE_SOCCODE+"="+
				"'"+socCode+"'"+
				" and "+
				this.FIELD_LIGNECDE_CFGCODEQUEST+"="+
				"'"+codeQuest+"'";



		Cursor cur=db.conn.rawQuery (query,null);
		if (cur.moveToNext() && !numcde.equals(""))
		{
			fillStructLin(ent,cur);

		}
		else{
			cur.close();
			return false;
		}
		cur.close();
		return true;
	}

	/*
	 * rempli la structure 
	 */
	void fillStructLin(structLinCde ent,Cursor cur)
	{
		ent.SOCCODE 	=this.giveFld(cur,this.FIELD_LIGNECDE_SOCCODE	);
		ent.CLICODE 	=this.giveFld(cur,this.FIELD_LIGNECDE_CLICODE 	);
		ent.CDECODE 	=this.giveFld(cur,this.FIELD_LIGNECDE_CDECODE 	);
		ent.VISCODE 	=this.giveFld(cur,this.FIELD_LIGNECDE_VISCODE 	);
		ent.PROCODE 	=this.giveFld(cur,this.FIELD_LIGNECDE_PROCODE 	);
		ent.REPCODE 	=this.giveFld(cur,this.FIELD_LIGNECDE_REPCODE 	);
		ent.DATE 		=this.giveFld(cur,this.FIELD_LIGNECDE_DATE 		);
		ent.TYPEPIECE 	=this.giveFld(cur,this.FIELD_LIGNECDE_TYPEPIECE 	);
		ent.CODABAR 	=this.giveFld(cur,this.FIELD_LIGNECDE_CODABAR 	);
		ent.NUMLIGNE 	=this.giveFld(cur,this.FIELD_LIGNECDE_NUMLIGNE 	);
		ent.DESIGNATION =this.giveFld(cur,this.FIELD_LIGNECDE_DESIGNATION );
		ent.UV 			=this.giveFld(cur,this.FIELD_LIGNECDE_UV 			);
		ent.QTECDE 		=Fonctions.convertToFloat(this.giveFld(cur,this.FIELD_LIGNECDE_QTECDE 		));
		ent.PRIX 		=Fonctions.convertToFloat(this.giveFld(cur,this.FIELD_LIGNECDE_PRIX 		));
		ent.MNTUNITHT 	=Fonctions.convertToFloat(this.giveFld(cur,this.FIELD_LIGNECDE_MNTUNITHT 	));
		ent.REMISE 		=Fonctions.convertToFloat(this.giveFld(cur,this.FIELD_LIGNECDE_REMISE 		));
		ent.MNTUNITNETHT=Fonctions.convertToFloat(this.giveFld(cur,this.FIELD_LIGNECDE_MNTUNITNETHT));
		ent.MNTTOTALHT 	=Fonctions.convertToFloat(this.giveFld(cur,this.FIELD_LIGNECDE_MNTTOTALHT 	));
		ent.MNTTOTALTTC =Fonctions.convertToFloat(this.giveFld(cur,this.FIELD_LIGNECDE_MNTTOTALTTC ));
		ent.LOT 		=this.giveFld(cur,this.FIELD_LIGNECDE_LOT 		);
		ent.SERIE 		=this.giveFld(cur,this.FIELD_LIGNECDE_SERIE 		);
		ent.COMMENT1 	=this.giveFld(cur,this.FIELD_LIGNECDE_COMMENT1 	);
		ent.COMMENT2 	=this.giveFld(cur,this.FIELD_LIGNECDE_COMMENT2 	);
		ent.COMMENT3 	=this.giveFld(cur,this.FIELD_LIGNECDE_COMMENT3 	);
		ent.QTEGR		=Fonctions.convertToFloat(this.giveFld(cur,this.FIELD_LIGNECDE_QTEGR		));
		ent.PRIXMODIF	=Fonctions.convertToFloat(this.giveFld(cur,this.FIELD_LIGNECDE_PRIXMODIF	));
		ent.REMISEMODIF	=Fonctions.convertToFloat(this.giveFld(cur,this.FIELD_LIGNECDE_REMISEMODIF	));
		ent.MNTUNITTTC	=Fonctions.convertToFloat(this.giveFld(cur,this.FIELD_LIGNECDE_MNTUNITTTC	));
		ent.NUMLIGNE_EXT =this.giveFld(cur,this.FIELD_LIGNECDE_NUMLIGNE_EXT	);
		ent.TAUX		 =Fonctions.convertToFloat(this.giveFld(cur,this.FIELD_LIGNECDE_TAUX));
		ent.CODETVA		 =this.giveFld(cur,this.FIELD_LIGNECDE_CODETVA);
		ent.TYPECHECKBOX		 =this.giveFld(cur,this.FIELD_LIGNECDE_TYPECHECKBOX);
		ent.CODEPANACHAGE		 =this.giveFld(cur,this.FIELD_LIGNECDE_CODEPANACHAGE);
		ent.TYPECDEVALEUR		 =this.giveFld(cur,this.FIELD_LIGNECDE_TYPECDEVALEUR);

		ent.FIELD_LIGNECDE_LINCHOIX1		 =this.giveFld(cur,this.FIELD_LIGNECDE_LINCHOIX1);


		ent.PRIXINITIAL		 =this.giveFld(cur,this.FIELD_LIGNECDE_PRIXINITIAL);
		ent.REMISEINITIAL		 =this.giveFld(cur,this.FIELD_LIGNECDE_REMISEINITIAL);

		ent.MNTTVA		 =Fonctions.convertToFloat(this.giveFld(cur,this.FIELD_LIGNECDE_MNTTVA));
		ent.TAXE1		 =Fonctions.convertToFloat(this.giveFld(cur,this.FIELD_LIGNECDE_TAXE1));
		ent.TAXE2		 =Fonctions.convertToFloat(this.giveFld(cur,this.FIELD_LIGNECDE_TAXE2));
		ent.TAXE3		 =Fonctions.convertToFloat(this.giveFld(cur,this.FIELD_LIGNECDE_TAXE3));
		
		ent.MNTTAXE1		 =Fonctions.convertToFloat(this.giveFld(cur,this.FIELD_LIGNECDE_MNTTAXE1));
		ent.MNTTAXE2		 =Fonctions.convertToFloat(this.giveFld(cur,this.FIELD_LIGNECDE_MNTTAXE2));
		ent.MNTTAXE3		 =Fonctions.convertToFloat(this.giveFld(cur,this.FIELD_LIGNECDE_MNTTAXE3));

	}
 
	public List<structLinCde> load(String numcde,boolean inTmp)
	{
		if(numcde == null)
			return null;
		if (numcde.equals("")) return null;
		
		String table=TABLENAME;
		if (inTmp)
			table=TABLENAME_DUPLICATA;

		String query="SELECT * FROM "+
				table+
				" where "+
				fld_kd_dat_type+"="+KD_TYPE+
				" and "+
				this.FIELD_LIGNECDE_CDECODE+"="+
				"'"+numcde+"'"+

				" order by "+
				this.FIELD_LIGNECDE_NUMLIGNE;

		List<structLinCde> lines=new ArrayList<structLinCde>();

		Cursor cur=db.conn.rawQuery (query,null);
		while (cur.moveToNext() && !numcde.equals(""))
		{
			structLinCde ent=new structLinCde();

			fillStructLin(ent,cur);
			lines.add(ent);
		}

		cur.close();
		return lines;
	}
	 
	public List<structLinCde> loadTotauxTVA(String numcde,boolean inTmp)
	{
		if(numcde == null)
			return null;
		if (numcde.equals("")) return null;
		
		
		String table=TABLENAME;
		if (inTmp)
			table=TABLENAME_DUPLICATA;

		String query="SELECT "+
				this.FIELD_LIGNECDE_TAUX+","+
				"sum("+FIELD_LIGNECDE_MNTTVA +") "+FIELD_LIGNECDE_MNTTVA+","+
				"sum("+FIELD_LIGNECDE_MNTTOTALHT +") "+FIELD_LIGNECDE_MNTTOTALHT+","+
				"sum("+FIELD_LIGNECDE_MNTTOTALTTC +") "+FIELD_LIGNECDE_MNTTOTALTTC+" "+
				" FROM "+
				table+
				" where "+
				fld_kd_dat_type+"="+KD_TYPE+
				" and "+
				this.FIELD_LIGNECDE_CDECODE+"="+
				"'"+numcde+"'"+
				" group by "+
				this.FIELD_LIGNECDE_TAUX+
				" order by "+
				this.FIELD_LIGNECDE_TAUX;

		List<structLinCde> lines=new ArrayList<structLinCde>();

		Cursor cur=db.conn.rawQuery (query,null);
		while (cur.moveToNext() && !numcde.equals(""))
		{
			structLinCde ent=new structLinCde();

			fillStructLin(ent,cur);
			lines.add(ent);
		}

		cur.close();
		return lines;
	}

	/**
	 * @author Marc VOUAUX
	 * @param ent
	 * @param numcde
	 * @param stBuf
	 * @return
	 */
	public boolean save(structLinCde ent,String numcde,String codeart, String numlin, StringBuffer stBuf)
	{
		try
		{
			String query="SELECT * FROM "+
					TABLENAME_TEMP2+
					" where "+
					fld_kd_dat_type+"="+KD_TYPE+
					" and "+
					this.FIELD_LIGNECDE_CDECODE+"="+
					"'"+numcde+"'"+
					" and "+
					this.FIELD_LIGNECDE_PROCODE+"="+
					"'"+codeart+"'";

			if (!numlin.equals("0"))
			{
				query+=" and "+
						this.FIELD_LIGNECDE_NUMLIGNE+"="+
						"'"+numlin+"'";
			}

			Cursor cur=db.conn.rawQuery (query,null);
			if (cur.moveToNext() && !numcde.equals(""))
			{
				float oldQteARemplacer=
						Fonctions.convertToFloat(giveFld(cur, FIELD_LIGNECDE_QTECDE))+
						Fonctions.convertToFloat(this.giveFld(cur, FIELD_LIGNECDE_QTEGR));

				query="UPDATE "+TABLENAME_TEMP2+
						" set "+
						this.FIELD_LIGNECDE_QTECDE+"="+
						"'"+ent.QTECDE+"',"+
						this.FIELD_LIGNECDE_PRIX+"="+
						"'"+ent.PRIX +"',"+
						this.FIELD_LIGNECDE_MNTUNITHT+"="+
						"'"+ent.MNTUNITHT+"',"+
						this.FIELD_LIGNECDE_REMISE+"="+
						"'"+ent.REMISE+"',"+
						this.FIELD_LIGNECDE_MNTUNITNETHT+"="+
						"'"+ent.MNTUNITNETHT+"',"+
						this.FIELD_LIGNECDE_MNTTOTALHT+"="+
						"'"+ent.MNTTOTALHT+"',"+
						this.FIELD_LIGNECDE_MNTTOTALTTC+"="+
						"'"+ent.MNTTOTALTTC+"',"+
						this.FIELD_LIGNECDE_COMMENT1+"="+
						"'"+MyDB.controlFld(ent.COMMENT1)+"',"+
						this.FIELD_LIGNECDE_COMMENT2+"="+
						"'"+MyDB.controlFld(ent.COMMENT2)+"',"+
						this.FIELD_LIGNECDE_COMMENT3+"="+
						"'"+MyDB.controlFld(ent.COMMENT3)+"',"+
						this.FIELD_LIGNECDE_QTEGR+"="+
						"'"+ent.QTEGR+"',"+
						this.FIELD_LIGNECDE_PRIXMODIF+"="+
						"'"+ent.PRIXMODIF+"',"+
						this.FIELD_LIGNECDE_REMISEMODIF+"="+
						"'"+ent.REMISEMODIF+"',"+
						this.FIELD_LIGNECDE_MNTUNITTTC+"="+
						"'"+ent.MNTUNITTTC+"',"+
						this.FIELD_LIGNECDE_CODEPANACHAGE+"="+
						"'"+ent.CODEPANACHAGE+"',"+
						this.FIELD_LIGNECDE_TYPECDEVALEUR+"="+
						"'"+ent.TYPECDEVALEUR+"',"+
						//this.FIELD_LIGNECDE_NUMLIGNE_EXT+"="+
						//"'"+ent.NUMLIGNE_EXT+"',"+
						this.FIELD_LIGNECDE_TAUX+"="+
						"'"+ent.TAUX+"',"+
						
						this.FIELD_LIGNECDE_TAXE1+"="+
						"'"+ent.TAXE1+"',"+
						this.FIELD_LIGNECDE_TAXE2+"="+
						"'"+ent.TAXE2+"',"+
						this.FIELD_LIGNECDE_TAXE3+"="+
						"'"+ent.TAXE3+"',"+
						this.FIELD_LIGNECDE_MNTTAXE1+"="+
						"'"+ent.MNTTAXE1+"',"+
						this.FIELD_LIGNECDE_MNTTAXE2+"="+
						"'"+ent.MNTTAXE2+"',"+
						this.FIELD_LIGNECDE_MNTTAXE3+"="+
						"'"+ent.MNTTAXE3+"',"+
						this.FIELD_LIGNECDE_MNTTVA+"="+
						"'"+ent.MNTTVA+"',"+
						this.FIELD_LIGNECDE_CODETVA+"="+
						"'"+ent.CODETVA+"' ,"+

						this.FIELD_LIGNECDE_LINCHOIX1+"="+
						"'"+ent.FIELD_LIGNECDE_LINCHOIX1+"' ,"+

						this.FIELD_LIGNECDE_PRIXINITIAL+"="+
						"'"+ent.PRIXINITIAL+"' ,"+
						this.FIELD_LIGNECDE_REMISEINITIAL+"="+
						"'"+ent.REMISEINITIAL+"' ,"+

						this.FIELD_LIGNECDE_TYPECHECKBOX+"="+
						"'"+ent.TYPECHECKBOX+"' "+
						" where "+
						fld_kd_dat_type+"="+KD_TYPE+
						" and "+
						this.FIELD_LIGNECDE_CDECODE+"="+
						"'"+numcde+"'"+

		  			" and "+
		  			this.FIELD_LIGNECDE_PROCODE+"="+
		  			"'"+codeart+"'";

				if (!numlin.equals("0"))
				{
					query+=" and "+
							this.FIELD_LIGNECDE_NUMLIGNE+"="+
							"'"+numlin+"'";
				}
				db.conn.execSQL(query);

				ent.QTECDE+=ent.QTEGR;

				Global.dbLog.Insert("Ligne","Save Update","","Requete: "+query, "","");
			}
			else		  
			{	  		
				query="INSERT INTO " + TABLENAME_TEMP2 +" ("+
						dbKD.fld_kd_dat_type+","+
						this.FIELD_LIGNECDE_SOCCODE 	+","+
						this.FIELD_LIGNECDE_CLICODE 	+","+
						this.FIELD_LIGNECDE_CDECODE 	+","+   	  		
						this.FIELD_LIGNECDE_VISCODE 	+","+
						this.FIELD_LIGNECDE_PROCODE 	+","+
						this.FIELD_LIGNECDE_REPCODE 	+","+
						this.FIELD_LIGNECDE_DATE 		+","+
						this.FIELD_LIGNECDE_TYPEPIECE 	+","+
						this.FIELD_LIGNECDE_CODABAR 	+","+
						this.FIELD_LIGNECDE_NUMLIGNE 	+","+
						this.FIELD_LIGNECDE_DESIGNATION +","+
						this.FIELD_LIGNECDE_UV 			+","+
						this.FIELD_LIGNECDE_QTECDE 		+","+
						this.FIELD_LIGNECDE_PRIX 		+","+
						this.FIELD_LIGNECDE_MNTUNITHT 	+","+
						this.FIELD_LIGNECDE_REMISE 		+","+  
						this.FIELD_LIGNECDE_MNTUNITNETHT+","+
						this.FIELD_LIGNECDE_MNTTOTALHT 	+","+    	  		
						this.FIELD_LIGNECDE_MNTTOTALTTC +","+
						this.FIELD_LIGNECDE_LOT 		+","+
						this.FIELD_LIGNECDE_SERIE 		+","+
						this.FIELD_LIGNECDE_COMMENT1 	+","+
						this.FIELD_LIGNECDE_COMMENT2 	+","+
						this.FIELD_LIGNECDE_COMMENT3 	+","+
						this.FIELD_LIGNECDE_QTEGR		+","+
						this.FIELD_LIGNECDE_PRIXMODIF	+","+
						this.FIELD_LIGNECDE_REMISEMODIF	+","+
						this.FIELD_LIGNECDE_MNTUNITTTC	+","+
						this.FIELD_LIGNECDE_NUMLIGNE_EXT+","+
						this.FIELD_LIGNECDE_TAUX+","+
						
						this.FIELD_LIGNECDE_TAXE1+","+
						this.FIELD_LIGNECDE_TAXE2+","+
						this.FIELD_LIGNECDE_TAXE3+","+
						
						this.FIELD_LIGNECDE_MNTTVA+","+
						this.FIELD_LIGNECDE_MNTTAXE1+","+
						this.FIELD_LIGNECDE_MNTTAXE2+","+
						this.FIELD_LIGNECDE_MNTTAXE3+","+

						this.FIELD_LIGNECDE_CODETVA+","+
						this.FIELD_LIGNECDE_CODEPANACHAGE+","+
						this.FIELD_LIGNECDE_TYPECDEVALEUR+","+

						this.FIELD_LIGNECDE_LINCHOIX1+","+
						this.FIELD_LIGNECDE_PRIXINITIAL+","+
						this.FIELD_LIGNECDE_REMISEINITIAL+","+


						this.FIELD_LIGNECDE_TYPECHECKBOX+""+
						") VALUES ("+
						String.valueOf(KD_TYPE)+","+
						"'"+MyDB.controlFld(ent.SOCCODE)	+"',"+
						"'"+MyDB.controlFld(ent.CLICODE )	+"',"+
						"'"+numcde+"',"+
						"'"+MyDB.controlFld(ent.VISCODE )	+"',"+
						"'"+MyDB.controlFld(ent.PROCODE )	+"',"+
						"'"+MyDB.controlFld(ent.REPCODE )	+"',"+
						"'"+ent.DATE 		+"',"+
						"'"+ent.TYPEPIECE 	+"',"+
						"'"+ent.CODABAR 	+"',"+
						"'"+ent.NUMLIGNE 	+"',"+
						"'"+MyDB.controlFld(ent.DESIGNATION )+"',"+
						"'"+MyDB.controlFld(ent.UV 		)	+"',"+
						"'"+ent.QTECDE 		+"',"+
						"'"+ent.PRIX 		+"',"+
						"'"+ent.MNTUNITHT 	+"',"+    
						"'"+ent.REMISE 		+"',"+   
						"'"+ent.MNTUNITNETHT+"',"+
						"'"+ent.MNTTOTALHT 	+"',"+
						"'"+ent.MNTTOTALTTC +"',"+
						"'"+MyDB.controlFld(ent.LOT 	)	+"',"+
						"'"+MyDB.controlFld(ent.SERIE 	)	+"',"+
						"'"+MyDB.controlFld(ent.COMMENT1 )	+"',"+
						"'"+MyDB.controlFld(ent.COMMENT2 )	+"',"+
						"'"+MyDB.controlFld(ent.COMMENT3 )	+"',"+
						"'"+ent.QTEGR		+"',"+
						"'"+ent.PRIXMODIF	+"',"+
						"'"+ent.REMISEMODIF	+"',"+
						"'"+ent.MNTUNITTTC	+"',"+  
						"'"+ent.NUMLIGNE_EXT	+"',"+  
						"'"+ent.TAUX	+"',"+ 
						
						"'"+ent.TAXE1	+"',"+
						"'"+ent.TAXE2	+"',"+
						"'"+ent.TAXE3	+"',"+
						
						"'"+ent.MNTTVA	+"',"+
						"'"+ent.MNTTAXE1	+"',"+
						"'"+ent.MNTTAXE2	+"',"+
						"'"+ent.MNTTAXE3	+"',"+
						
						"'"+ent.CODETVA	+"',"+  
						"'"+ent.CODEPANACHAGE	+"',"+  
						"'"+ent.TYPECDEVALEUR	+"',"+

						"'"+ent.FIELD_LIGNECDE_LINCHOIX1	+"',"+
						"'"+ent.PRIXINITIAL	+"',"+
						"'"+ent.REMISEINITIAL	+"',"+
						
						"'"+ent.TYPECHECKBOX	+"'"+ 
						")";

				db.conn.execSQL(query);
				//on met � kour le RL

				ent.QTECDE+=ent.QTEGR;

				//Global.dbLog.Insert("Ligne","Save","","Numcde: "+numcde+" - code article: "+ ent.PROCODE+" - QTECDE: "+ ent.QTECDE+" - QTEGR: "+ ent.QTEGR+" - PRIXMODIF: "+ ent.PRIXMODIF+" - REMISEMODIF: "+ ent.REMISEMODIF+" - MNTTOTALHT: "+ent.MNTTOTALHT, "","");
				Global.dbLog.Insert("Ligne","Save Insert","","Requete: "+query, "","");

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

	public int LoadNumLine(structLinCde ent,String numcde,String codeart, int numlin,StringBuffer stBuf)
	{

		int m_nNbLigne = numlin;

		//dbKD40CompteurTarif dbCompteur=new dbKD40CompteurTarif(db);
		try
		{
			String query;
			query="SELECT * FROM "+
					TABLENAME_TEMP2+
					" where "+
					fld_kd_dat_type+"="+KD_TYPE+
					" and "+
					this.FIELD_LIGNECDE_CDECODE+"="+
					"'"+numcde+"'"+
					" ORDER BY "+
					this.FIELD_LIGNECDE_NUMLIGNE;

			Cursor cur=db.conn.rawQuery (query,null);
			if (cur.moveToNext())
			{
				m_nNbLigne =Fonctions.GetStringToIntDanem(cur.getString(cur.getColumnIndex(this.FIELD_LIGNECDE_NUMLIGNE)));
			}
			cur.close();

		}
		catch(Exception ex)
		{
			return 0;

		}
		m_nNbLigne += 500;

		return m_nNbLigne;

	}
	public int LastNumLine(structLinCde lin,String numcde,StringBuffer stBuf)
	{
		int m_nNbLigne=0;
		try
		{
			String query;
			query="SELECT * FROM "+
					TABLENAME_TEMP2+
					" where "+
					fld_kd_dat_type+"="+KD_TYPE+
					" and "+
					this.FIELD_LIGNECDE_CDECODE+"="+
					"'"+numcde+"'"+
					" ORDER BY "+
					this.FIELD_LIGNECDE_NUMLIGNE+
					" DESC ";

			Cursor cur=db.conn.rawQuery (query,null);
			if (cur.moveToNext())
			{
				m_nNbLigne =Fonctions.GetStringToIntDanem(cur.getString(cur.getColumnIndex(this.FIELD_LIGNECDE_NUMLIGNE))) ;
			}
			cur.close();

		}
		catch(Exception ex)
		{
			return m_nNbLigne;

		}


		return m_nNbLigne;

	}

	public int CalculNbrUV( String numcde )
	{
		float nbrUV=0;
		try
		{
			String query="SELECT * FROM "+
					TABLENAME_TEMP2+
					" where "+
					fld_kd_dat_type+"="+KD_TYPE+
					" and "+
					this.FIELD_LIGNECDE_CDECODE+"="+
					"'"+numcde+"'";

			Cursor cur=db.conn.rawQuery (query,null);
			if (cur!=null && !numcde.equals(""))
			{
				while(cur.moveToNext())
				{
					String qte=cur.getString(cur.getColumnIndex(this.FIELD_LIGNECDE_QTECDE));
					String gr=cur.getString(cur.getColumnIndex(this.FIELD_LIGNECDE_QTEGR));
					
					 
					nbrUV+=(Fonctions.convertToFloat(qte)-Fonctions.convertToFloat(gr));
				}
			}
			cur.close();
			}
		catch(Exception ex)
		{
 
		}

		return (int)nbrUV;
	}
	
	public double CalculTotalHT( String numcde )
	{
		//Global.dbSageTarifGen.CalcPanachageQte(numcde,Global.AXE_Client.CODECLIENT,Global.AXE_Client.CATTARIF,"");
		double dMontantHT=0;
		double dMontantHTLigne=0;
 	 
		try
		{
			String query="SELECT * FROM "+
					TABLENAME_TEMP2+
					" where "+
					fld_kd_dat_type+"="+KD_TYPE+
					" and "+
					this.FIELD_LIGNECDE_CDECODE+"="+
					"'"+numcde+"'"+
					" ORDER BY "+
					this.FIELD_LIGNECDE_PROCODE;

			Cursor cur=db.conn.rawQuery (query,null);
			if (cur!=null && !numcde.equals(""))
			{
				while(cur.moveToNext())
				{
					String val=cur.getString(cur.getColumnIndex(this.FIELD_LIGNECDE_MNTTOTALHT));
					dMontantHTLigne=Fonctions.round(Fonctions.convertToFloat(val),2);

					dMontantHT+=dMontantHTLigne;

				}
			}
			cur.close();
			}
		catch(Exception ex)
		{
 
		}

		return dMontantHT;
	}
	public double CalculTotalTVA( String numcde )
	{
		//Global.dbSageTarifGen.CalcPanachageQte(numcde,Global.AXE_Client.CODECLIENT,Global.AXE_Client.CATTARIF,"");
		double dMontantHT=0;
		double dMontantHTLigne=0;
 	 
		try
		{
			String query="SELECT * FROM "+
					TABLENAME_TEMP2+
					" where "+
					fld_kd_dat_type+"="+KD_TYPE+
					" and "+
					this.FIELD_LIGNECDE_CDECODE+"="+
					"'"+numcde+"'"+
					" ORDER BY "+
					this.FIELD_LIGNECDE_PROCODE;

			Cursor cur=db.conn.rawQuery (query,null);
			if (cur!=null && !numcde.equals(""))
			{
				while(cur.moveToNext())
				{
					dMontantHTLigne=Fonctions.convertToFloat(cur.getString(cur.getColumnIndex(this.FIELD_LIGNECDE_MNTTVA)));

					dMontantHT+=dMontantHTLigne;

				}
			}
			cur.close();
			}
		catch(Exception ex)
		{
 
		}

		return dMontantHT;
	}
	public boolean delete(String numcde, String codeart,String numlin,StringBuffer err,String codePanachage)
	{
		try
		{



			String query="DELETE from "+
					TABLENAME_TEMP2+		
					" where "+
					fld_kd_dat_type+"="+KD_TYPE+" and "+
					this.FIELD_LIGNECDE_PROCODE+
					"='"+codeart+"' "+
					" and "+
					this.FIELD_LIGNECDE_CDECODE+
					"='"+numcde+"'";

			if (!numlin.equals("0"))
			{
				query+=" and "+
						this.FIELD_LIGNECDE_NUMLIGNE+"="+
						"'"+numlin+"'";
			}
			db.conn.execSQL(query);
			//Global.dbLog.Insert("Ligne","Delete","","Numcde: "+numcde+" - code article: "+ codeart, "","");
			Global.dbLog.Insert("Ligne","Delete","","Requete: "+query, "","");
			//on efface les composants si c'est un compos�
 
			return true;
		}
		catch(Exception ex)
		{
			err.append(ex.getMessage());
		}
		return false;
	}
	public boolean deleteNumcde(String numcde,StringBuffer err)
	{
		try
		{
			String query="DELETE from "+
					TABLENAME_TEMP2+		
					" where "+
					fld_kd_dat_type+"="+KD_TYPE+" and "+
					this.FIELD_LIGNECDE_CDECODE+
					"='"+numcde+"'";

			db.conn.execSQL(query);
			Global.dbLog.Insert("Ligne","DeleteNumcde","","Numcde: "+numcde, "","");
			return true;
		}
		catch(Exception ex)
		{
			err.append(ex.getMessage());
		}
		return false;
	}
	
	public boolean deleteNumcde_Savecde(String numcde,StringBuffer err)
	{
		try
		{
			String query="DELETE from "+
					TABLENAME_SAVE+		
					" where "+
					fld_kd_dat_type+"="+KD_TYPE+" and "+
					this.FIELD_LIGNECDE_CDECODE+
					"='"+numcde+"'";

			db.conn.execSQL(query);
			return true;
		}
		catch(Exception ex)
		{
			err.append(ex.getMessage());
		}
		return false;
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
