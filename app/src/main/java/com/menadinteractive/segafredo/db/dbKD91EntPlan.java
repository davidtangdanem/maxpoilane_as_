package com.menadinteractive.segafredo.db;

import java.util.ArrayList;

import android.database.Cursor;

import com.menadinteractive.segafredo.communs.DateCode;
import com.menadinteractive.segafredo.communs.Fonctions;
import com.menadinteractive.segafredo.communs.Global;

/**
 * Vue Entete de plans dans la table KD
 * @author Marc VOUAUX
 *
 */
public class dbKD91EntPlan extends dbKD{

	public final String GLOBAL="GLOB";
	public final int KD_TYPE = 91;
	public final String FIELD_CODESOC = fld_kd_soc_code;//si GLOBAL alors questionnaire hors client, dans menu principal
	public final String FIELD_CODEREP = fld_kd_dat_idx01;
	public final String FIELD_CODEPLAN = fld_kd_cde_code;//code plan
	public final String FIELD_LBL = fld_kd_dat_data01;
	public final String FIELD_ISTODO = fld_kd_dat_data02;//oblig
	public final String FIELD_ISACTIF = fld_kd_dat_data03;//
	public final String FIELD_ISSHARE = fld_kd_dat_data04;////partag� avec d'autres admin
	public final String FIELD_DATEFROM = fld_kd_dat_data05;//TYPEVIS
	public final String FIELD_DATETO = fld_kd_dat_data06;//TYPECDE
	public final String FIELD_LISTVRP = fld_kd_dat_data07;//VRP concern�s
	public final String FIELD_COMMENT= fld_kd_dat_data09;
	public final String FIELD_LISTGROUPES = fld_kd_dat_data10;   //groupes de clients concernes 
	public final String FIELD_DATECREAT= fld_kd_dat_data11;
	public final String FIELD_USERCREAT= fld_kd_dat_data12;
	public final String FIELD_DATEMODIF= fld_kd_dat_data13;
	public final String FIELD_USERMODIF= fld_kd_dat_data14;
	public final String FIELD_FLAG        = fld_kd_dat_data08; //1 = modifi�, 2=delete

	
	//structure qui permet de GET/SET des donn�es vers ou � la classe 
	static public class data
	{
		public data()
		{
			CODESOC="";
			CODEREP="";
			CODEPLAN="";
			LBL="";
			ISTODO="";
			ISACTIF="";
			ISSHARE="";
			DATEFROM="";
			DATETO="";
			LISTVRP="";
			LISTGROUPES="";
			COMMENT="";
			DATECREAT="";
			USERCREAT="";
			DATEMODIF="";
			USERMODIF="";
			FLAG="";
		}
		
		public String CODESOC;
		public String CODEREP;
		public String CODEPLAN;
		public String LBL;
		public String ISTODO;
		public String ISACTIF;
		public String ISSHARE;
		public String DATEFROM;
		public String DATETO;
		public String LISTVRP;
		public String LISTGROUPES;
		public String COMMENT;
		public String DATECREAT;
		public String USERCREAT;
		public String DATEMODIF;
		public String USERMODIF;
		public String FLAG;

	}
	
	MyDB db;
	public dbKD91EntPlan(MyDB _db)
	{
		super();
		db=_db;	
	}


	public int Count()
	{

		try
		{
			String query="select count(*) from "+TABLENAME+" where "+
					fld_kd_dat_type +"='"+KD_TYPE+"'";

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

	/**
	 * @author marcvouaux
	 * donne un num�ro de plan inexistant
	 * @return
	 */
	public String GetNumPlan(String numplan)
	{
		//si on a deja un numero de plan on le renvoi
		if (!numplan.equals(""))
			return numplan;
		
		DateCode dt=new DateCode();
		//Code rep(5c) +hhmm+AAMMJJ
		String StValeur="";
		boolean existe=true;		
		int i=0;
		while(existe==true)
		{
			StValeur="PLAN"+dt.ToCode();


			existe=false;
			/*
			 * Check dans Kems_data si il existe d�j� 
			 */
			String query="";
			query="select * from "+
					dbKD.TABLENAME+
					" where "+
					dbKD.fld_kd_dat_type+"='"+KD_TYPE+"' AND "+
					dbKD.fld_kd_cde_code+"='"+StValeur+"'";

			//dbKD40CompteurTarif dbCompteur=new dbKD40CompteurTarif(db);

			Cursor cur=db.conn.rawQuery (query,null);
			if (cur!=null)
			{
				if(cur.moveToNext())
				{
					existe=true;	
				}

			}
			i++;
		}

		return StValeur;		
	}
	

	public ArrayList<data> load()
	{
		ArrayList<data> datas=new ArrayList<data>();
		
		String query="select * from "+this.TABLENAME+
				" where "+
				this.fld_kd_dat_type+
				"='"+KD_TYPE+"'"+
				" and ("+
				this.FIELD_FLAG+
				"<>'2' or "+
				this.FIELD_FLAG+
				" is null)";
				
		Cursor cur=db.conn.rawQuery (query,null);
		if (cur.moveToNext() )
		{
			do
			{
				data mydata=new data();
				mydata.CODESOC=this.giveFld(cur,this.FIELD_CODESOC );
				mydata.CODEPLAN=this.giveFld(cur,this.FIELD_CODEPLAN );
				mydata.CODEREP=this.giveFld(cur,this.FIELD_CODEREP );
				mydata.COMMENT=this.giveFld(cur,this.FIELD_COMMENT );
				mydata.DATECREAT=this.giveFld(cur,this.FIELD_DATECREAT );
				mydata.DATEFROM=this.giveFld(cur,this.FIELD_DATEFROM );
				mydata.DATEMODIF=this.giveFld(cur,this.FIELD_DATEMODIF );
				mydata.DATETO=this.giveFld(cur,this.FIELD_DATETO );
				mydata.ISACTIF=this.giveFld(cur,this.FIELD_ISACTIF );
				mydata.ISSHARE=this.giveFld(cur,this.FIELD_ISSHARE) ;
				mydata.ISTODO=this.giveFld(cur,this.FIELD_ISTODO );
				mydata.LBL=this.giveFld(cur,this.FIELD_LBL );
				mydata.LISTGROUPES=this.giveFld(cur,this.FIELD_LISTGROUPES );
				mydata.LISTVRP=this.giveFld(cur,this.FIELD_LISTVRP );
				mydata.USERCREAT=this.giveFld(cur,this.FIELD_USERCREAT );
				mydata.USERMODIF=this.giveFld(cur,this.FIELD_USERMODIF );
				mydata.FLAG=this.giveFld(cur,this.FIELD_FLAG);
				
				datas.add(mydata);
			}while(cur.moveToNext());
		}
		return datas;
	}
	
	//ne charge que les plans actif ou un plan en particulier
	public ArrayList<data> loadActive(String numplan,String enseigne,String codesoc)
	{
		ArrayList<data> datas=new ArrayList<data>();
		
		String query="select * from "+this.TABLENAME+
				" where "+
				this.fld_kd_dat_type+
				"='"+KD_TYPE+"'"+
				" and ("+
				this.FIELD_FLAG+
				"<>'2' or "+
				this.FIELD_FLAG+" is null) "+
				" and ";
				if (numplan.equals(""))
					query+=this.FIELD_ISACTIF+"='1'";
				else
					query+=this.FIELD_CODEPLAN+"='"+numplan+"' ";
				if (Fonctions.GetStringDanem(codesoc).equals(Global.dbKDEntPlan.GLOBAL))
					query+=" and ("+this.FIELD_CODESOC+"='"+Global.dbKDEntPlan.GLOBAL+"') ";	
				else if (!Fonctions.GetStringDanem(codesoc).equals(""))
					query+=" and ("+this.FIELD_CODESOC+"='"+codesoc+"' or "+this.FIELD_CODESOC+"=''"+" or "+this.FIELD_CODESOC+" is null "+ ") ";
								
				if (!Fonctions.GetStringDanem(enseigne).equals(""))
					query+=" and ("+this.FIELD_LISTGROUPES+" like '%;"+enseigne+";%' or "+this.FIELD_LISTGROUPES+"=''"+" or "+this.FIELD_LISTGROUPES+" is null or "+this.FIELD_LISTGROUPES+"=';'"+ ") ";
				
				query+=" order by "+Global.dbKDEntPlan.FIELD_CODEPLAN;
		Cursor cur=db.conn.rawQuery (query,null);
		if (cur.moveToNext() )
		{
			do
			{
				data mydata=new data();
				mydata.CODESOC=this.giveFld(cur,this.FIELD_CODESOC );
				mydata.CODEPLAN=this.giveFld(cur,this.FIELD_CODEPLAN );
				mydata.CODEREP=this.giveFld(cur,this.FIELD_CODEREP );
				mydata.COMMENT=this.giveFld(cur,this.FIELD_COMMENT );
				mydata.DATECREAT=this.giveFld(cur,this.FIELD_DATECREAT );
				mydata.DATEFROM=this.giveFld(cur,this.FIELD_DATEFROM );
				mydata.DATEMODIF=this.giveFld(cur,this.FIELD_DATEMODIF );
				mydata.DATETO=this.giveFld(cur,this.FIELD_DATETO );
				mydata.ISACTIF=this.giveFld(cur,this.FIELD_ISACTIF );
				mydata.ISSHARE=this.giveFld(cur,this.FIELD_ISSHARE) ;
				mydata.ISTODO=this.giveFld(cur,this.FIELD_ISTODO );
				mydata.LBL=this.giveFld(cur,this.FIELD_LBL );
				mydata.LISTGROUPES=this.giveFld(cur,this.FIELD_LISTGROUPES );
				mydata.LISTVRP=this.giveFld(cur,this.FIELD_LISTVRP );
				mydata.USERCREAT=this.giveFld(cur,this.FIELD_USERCREAT );
				mydata.USERMODIF=this.giveFld(cur,this.FIELD_USERMODIF );
				mydata.FLAG=this.giveFld(cur,this.FIELD_FLAG );
				datas.add(mydata);
			}while(cur.moveToNext());
		}
		return datas;
	}
	/**
	 * @author Marc VOUAUX
	 * @param ent
	 * @param numcde
	 * @param stBuf
	 * @return
	 */
	public boolean load(data ent,String numcde)
	{
		if (numcde.equals("")) return true;

		String query="SELECT * FROM "+
				TABLENAME+
				" where "+
				fld_kd_dat_type+"="+KD_TYPE+
				" and "+
				this.FIELD_CODEPLAN+"="+
				"'"+numcde+"'";

		Cursor cur=db.conn.rawQuery (query,null);
		if (cur.moveToNext() && !numcde.equals(""))
		{
			ent.CODESOC =this.giveFld(cur,this.FIELD_CODESOC );
			ent.CODEPLAN =this.giveFld(cur,this.FIELD_CODEPLAN );
			ent.CODEREP =this.giveFld(cur,this.FIELD_CODEREP );
			ent.COMMENT =this.giveFld(cur,this.FIELD_COMMENT );
			ent.DATECREAT=this.giveFld(cur,this.FIELD_DATECREAT);
			ent.DATEFROM=this.giveFld(cur,this.FIELD_DATEFROM );
			ent.DATETO =this.giveFld(cur,this.FIELD_DATETO);
			ent.ISACTIF =this.giveFld(cur,this.FIELD_ISACTIF );
			ent.ISSHARE  =this.giveFld(cur,this.FIELD_ISSHARE  );
			ent.ISTODO =this.giveFld(cur,this.FIELD_ISTODO );
			ent.LBL=this.giveFld(cur,this.FIELD_LBL);
			ent.LISTGROUPES  =this.giveFld(cur,this.FIELD_LISTGROUPES );
			ent.LISTVRP  =this.giveFld(cur,this.FIELD_LISTVRP );
			ent.USERCREAT  =this.giveFld(cur,this.FIELD_USERCREAT);
			ent.USERMODIF  =this.giveFld(cur,this.FIELD_USERMODIF);

			ent.FLAG  =this.giveFld(cur,this.FIELD_FLAG);
		}
		else
			return false;
		return true;
	}
	/**
	 * sauvegarde de la cde
	 * @author Marc VOUAUX
	 * @param ent
	 * @param numcde
	 * @param stBuf
	 * @return
	 */
	public boolean save(data ent)
	{
		try
		{
			String query="SELECT * FROM "+
					TABLENAME+
					" where "+
					fld_kd_dat_type+"="+KD_TYPE+
					" and "+
					this.FIELD_CODEPLAN+"="+
					"'"+ent.CODEPLAN+"'";

			Cursor cur=db.conn.rawQuery (query,null);
			if (cur.moveToNext() && !ent.CODEPLAN.equals(""))
			{


				query="UPDATE "+TABLENAME+
						" set "+
						this.FIELD_CODEREP+"="+
						"'"+MyDB.controlFld(ent.CODEREP) +"',"+
						this.FIELD_CODESOC+"="+
						"'"+MyDB.controlFld(ent.CODESOC) +"',"+
						this.FIELD_COMMENT+"="+
						"'"+MyDB.controlFld(ent.COMMENT) +"',"+
						this.FIELD_DATEMODIF+"="+
						"'"+MyDB.controlFld(ent.DATEMODIF) +"',"+
						this.FIELD_DATETO+"="+
						"'"+MyDB.controlFld(ent.DATETO) +"',"+
						this.FIELD_DATEFROM+"="+
						"'"+MyDB.controlFld(ent.DATEFROM) +"',"+
						this.FIELD_ISACTIF+"="+
						"'"+MyDB.controlFld(ent.ISACTIF) +"',"+
						this.FIELD_ISSHARE+"="+
						"'"+MyDB.controlFld(ent.ISSHARE) +"',"+
						this.FIELD_ISTODO+"="+
						"'"+MyDB.controlFld(ent.ISTODO) +"',"+
						this.FIELD_LBL+"="+
						"'"+MyDB.controlFld(ent.LBL) +"',"+
						this.FIELD_LISTGROUPES+"="+
						"'"+MyDB.controlFld(ent.LISTGROUPES) +"',"+
						this.FIELD_LISTVRP+"="+
						"'"+MyDB.controlFld(ent.LISTVRP) +"',"+
						this.FIELD_FLAG+"="+
						"'"+MyDB.controlFld(ent.FLAG) +"',"+
						this.FIELD_USERMODIF+"="+
						"'"+MyDB.controlFld(ent.USERMODIF) +"' "+
						
						" where "+
						FIELD_CODEPLAN+
						"='"+ent.CODEPLAN+"' "+
						" and "+
						dbKD.fld_kd_dat_type+
						"='"+KD_TYPE+"' ";


				;	  		

				db.conn.execSQL(query);
			}
			else		  
			{	  		
				query="INSERT INTO " + TABLENAME +" ("+
						dbKD.fld_kd_dat_type+","+
						this.FIELD_CODEPLAN+","+
						this.FIELD_CODESOC+","+
						this.FIELD_CODEREP+","+   	  		
						this.FIELD_COMMENT+","+
						this.FIELD_DATECREAT+","+
						this.FIELD_DATEFROM+","+
						this.FIELD_DATEMODIF+","+
						this.FIELD_DATETO+","+
						this.FIELD_ISACTIF+","+
						this.FIELD_ISSHARE+","+
						this.FIELD_ISTODO+","+
						this.FIELD_LBL+","+
						this.FIELD_LISTGROUPES+","+
						this.FIELD_LISTVRP+","+
						this.FIELD_USERCREAT+","+
						this.FIELD_FLAG+","+
						this.FIELD_USERMODIF+" "+  
					
						") VALUES ("+
						String.valueOf(KD_TYPE)+","+
						"'"+MyDB.controlFld(ent.CODEPLAN)+"',"+
						"'"+MyDB.controlFld(ent.CODESOC)+"',"+
						"'"+MyDB.controlFld(ent.CODEREP)+"',"+
						"'"+MyDB.controlFld(ent.COMMENT)+"',"+
						"'"+MyDB.controlFld(ent.DATECREAT)+"',"+
						"'"+MyDB.controlFld(ent.DATEFROM)+"',"+
						"'"+MyDB.controlFld(ent.DATEMODIF)+"',"+
						"'"+MyDB.controlFld(ent.DATETO)+"',"+
						"'"+MyDB.controlFld(ent.ISACTIF)+"',"+
						"'"+MyDB.controlFld(ent.ISSHARE)+"',"+
						"'"+MyDB.controlFld(ent.ISTODO)+"',"+
						"'"+MyDB.controlFld(ent.LBL)+"',"+
						"'"+MyDB.controlFld(ent.LISTGROUPES)+"',"+
						"'"+MyDB.controlFld(ent.LISTVRP)+"',"+
						"'"+MyDB.controlFld(ent.USERCREAT)+"',"+
						"'"+MyDB.controlFld(ent.FLAG)+"',"+
						"'"+MyDB.controlFld(ent.USERMODIF)+"' "+
		
						")";

				db.conn.execSQL(query);
			}
		}
		catch(Exception ex)
		{
			Global.lastErrorMessage=ex.getLocalizedMessage();
			return false;
		}

		return true;
	}

	/*
	 * on deflag
	 */
	public boolean deModified()
	{
		try
		{
			String query="update  "+
					TABLENAME+	
					" set "+
					this.FIELD_FLAG+
					"='0'"+
					" where "+
					dbKD.fld_kd_dat_type
					+"="+KD_TYPE;

			db.conn.execSQL(query);
			return true;
		}
		catch(Exception ex)
		{
			Global.lastErrorMessage=ex.getLocalizedMessage();
		}
		return false;
	}
	
	public boolean Clear(StringBuilder err)
	{
		String query="Delete from "+
				TABLENAME+
				" where "+
				fld_kd_dat_type+
				"="+
				KD_TYPE;


		return db.execSQL(query, err);


	}	
	public boolean deleteAll(StringBuffer err)
	{
		try
		{
			String query="DELETE from "+
					TABLENAME+		
					" where "+
					dbKD.fld_kd_dat_type
					+"="+KD_TYPE;

			db.conn.execSQL(query);
			return true;
		}
		catch(Exception ex)
		{
			err.append(ex.getMessage());
		}
		return false;
	}

}