package com.menadinteractive.segafredo.db;

import java.util.ArrayList;

import android.database.Cursor;

import com.menadinteractive.segafredo.communs.DateCode;
import com.menadinteractive.segafredo.communs.Global;

/**
 * Vue Entete de plans dans la table KD
 * @author Marc VOUAUX
 *
 */
public class dbKD92LinPlan extends dbKD{

	public final int KD_TYPE = 92;

	public final String FIELD_CODESOC = fld_kd_soc_code;//code plan
	public final String FIELD_CODEPLAN = fld_kd_cde_code;//code plan
	public final String FIELD_CODEREP = fld_kd_dat_idx01;
	public final String FIELD_CODEQUEST = fld_kd_dat_idx02;//code quest
	public final String FIELD_LBL = fld_kd_dat_data01;
	public final String FIELD_ISTODO = fld_kd_dat_data02;//oblig
	public final String FIELD_ISACTIF = fld_kd_dat_data03;//
	public final String FIELD_TYPE = fld_kd_dat_data04;//0=O/N, 1=Int, 2=Texte
	public final String FIELD_CHOIX = fld_kd_dat_data05;//separateur ;
	public final String FIELD_FLAG        = fld_kd_dat_data08; //1 = modifi�, 2=delete
	public final String FIELD_COMMENT= fld_kd_dat_data09;
	public final String FIELD_DATECREAT= fld_kd_dat_data11;
	public final String FIELD_USERCREAT= fld_kd_dat_data12;
	public final String FIELD_DATEMODIF= fld_kd_dat_data13;
	public final String FIELD_USERMODIF= fld_kd_dat_data14;

	
	final static public String type_bool="0";
	final static public String type_int="1";
	final static public String type_text="2";
	final static public String type_float="3";
	final static public String type_note5="4";
	final static public String type_choix="5";
	
	//structure qui permet de GET/SET des donn�es vers ou � la classe 
	static public class data
	{

		
		public data()
		{
			CODEREP="";
			CODEPLAN="";
			CODEQUEST="";
			LBL="";
			ISTODO="";
			ISACTIF="";
			TYPE="";
			CHOIX="";
			COMMENT="";
			DATECREAT="";
			USERCREAT="";
			DATEMODIF="";
			USERMODIF="";
			object=null;
			FLAG="";
			CODESOC="";

		}
		public String CODESOC;
		public String CODEREP;
		public String CODEPLAN;
		public String CODEQUEST;
		public String LBL;
		public String ISTODO;
		public String ISACTIF;
		public String TYPE;
		public String CHOIX;
		public String COMMENT;
		public String DATECREAT;
		public String USERCREAT;
		public String DATEMODIF;
		public String USERMODIF;
		public Object object;
		public String FLAG;

	}
	
	MyDB db;
	public dbKD92LinPlan(MyDB _db)
	{
		super();
		db=_db;	
	}


	public int Count(String numcde)
	{

		try
		{
			String query="select count(*) from "+TABLENAME+" where "+
					fld_kd_dat_type +"='"+KD_TYPE+"'"+
					" and "+
					dbKD.fld_kd_cde_code+
					"='"+numcde+"'";

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
	public String GetNumQuest(String numquest)
	{
		//si on a deja un numero de plan on le renvoi
		if (!numquest.equals(""))
			return numquest;

		DateCode dt=new DateCode();
		//Code rep(5c) +hhmm+AAMMJJ
		String StValeur="";
		boolean existe=true;		
		int i=0;
		while(existe==true)
		{
	

			StValeur="PLANQ"+dt.ToCode()+i;


			existe=false;
			/*
			 * Check dans Kems_data si il existe d�j� 
			 */
			String query="";
			query="select * from "+
					dbKD.TABLENAME+
					" where "+
					dbKD.fld_kd_dat_type+"='"+KD_TYPE+"' AND "+
					this.FIELD_CODEQUEST+"='"+StValeur+"'";

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
	

	
	public ArrayList<data> load(String numplan)
	{
		ArrayList<data> datas=new ArrayList<data>();
		
		String query="select * from "+this.TABLENAME+
				" where "+
				this.fld_kd_dat_type+
				"='"+KD_TYPE+"'"+
				" and "+
				this.FIELD_CODEPLAN+"='"+numplan+"'"+
				" and "+
				this.FIELD_ISACTIF+"='"+1+"'"+
				" and ("+
				this.FIELD_FLAG+
				"<>'2' or "+
				this.FIELD_FLAG+" is null)"+
				" order by "+this.FIELD_CODEQUEST;
				
		Cursor cur=db.conn.rawQuery (query,null);
		if (cur.moveToNext() )
		{
			do
			{
				data mydata=new data();
				mydata.CODESOC=this.giveFld(cur,this.FIELD_CODESOC);
				mydata.CODEPLAN=this.giveFld(cur,this.FIELD_CODEPLAN );
				mydata.CODEREP=this.giveFld(cur,this.FIELD_CODEREP );
				mydata.CODEQUEST=this.giveFld(cur,this.FIELD_CODEQUEST );
				mydata.COMMENT=this.giveFld(cur,this.FIELD_COMMENT );
				mydata.DATECREAT=this.giveFld(cur,this.FIELD_DATECREAT );
				mydata.DATEMODIF=this.giveFld(cur,this.FIELD_DATEMODIF );
				mydata.ISACTIF=this.giveFld(cur,this.FIELD_ISACTIF );
				mydata.TYPE=this.giveFld(cur,this.FIELD_TYPE) ;
				mydata.CHOIX=this.giveFld(cur,this.FIELD_CHOIX) ;
				mydata.ISTODO=this.giveFld(cur,this.FIELD_ISTODO );
				mydata.LBL=this.giveFld(cur,this.FIELD_LBL );
				mydata.USERCREAT=this.giveFld(cur,this.FIELD_USERCREAT );
				mydata.USERMODIF=this.giveFld(cur,this.FIELD_USERMODIF );
				mydata.FLAG=this.giveFld(cur,this.FIELD_FLAG);
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
	public boolean load(data ent,String numcde,String codequest)
	{
		if (numcde.equals("")) return true;

		String query="SELECT * FROM "+
				TABLENAME+
				" where "+
				fld_kd_dat_type+"="+KD_TYPE+
				" and "+
				this.FIELD_CODEPLAN+"="+
				"'"+numcde+"'"+
				" and "+
				this.FIELD_CODEQUEST+"="+
				"'"+codequest+"'";
		

		Cursor cur=db.conn.rawQuery (query,null);
		if (cur.moveToNext() && !numcde.equals(""))
		{
			ent.CODEPLAN =this.giveFld(cur,this.FIELD_CODEPLAN );
			ent.CODEREP =this.giveFld(cur,this.FIELD_CODEREP );
			ent.CODEQUEST =this.giveFld(cur,this.FIELD_CODEQUEST );
			ent.COMMENT =this.giveFld(cur,this.FIELD_COMMENT );
			ent.DATECREAT=this.giveFld(cur,this.FIELD_DATECREAT);

			ent.ISACTIF =this.giveFld(cur,this.FIELD_ISACTIF );
			ent.TYPE  =this.giveFld(cur,this.FIELD_TYPE );
			ent.CHOIX  =this.giveFld(cur,this.FIELD_CHOIX );
			ent.ISTODO =this.giveFld(cur,this.FIELD_ISTODO );
			ent.LBL=this.giveFld(cur,this.FIELD_LBL);
			ent.USERCREAT  =this.giveFld(cur,this.FIELD_USERCREAT);
			ent.USERMODIF  =this.giveFld(cur,this.FIELD_USERMODIF);

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
					"'"+ent.CODEPLAN+"'"+
					" and "+
					this.FIELD_CODEQUEST+"="+
					"'"+ent.CODEQUEST+"'";

			Cursor cur=db.conn.rawQuery (query,null);
			if (cur.moveToNext() && !ent.CODEPLAN.equals(""))
			{


				query="UPDATE "+TABLENAME+
						" set "+
						this.FIELD_CODEREP+"="+
						"'"+MyDB.controlFld(ent.CODEREP) +"',"+
						this.FIELD_COMMENT+"="+
						"'"+MyDB.controlFld(ent.COMMENT) +"',"+
						this.FIELD_DATEMODIF+"="+
						"'"+MyDB.controlFld(ent.DATEMODIF) +"',"+
						this.FIELD_ISACTIF+"="+
						"'"+MyDB.controlFld(ent.ISACTIF) +"',"+
						this.FIELD_TYPE+"="+
						"'"+MyDB.controlFld(ent.TYPE) +"',"+
						this.FIELD_CHOIX+"="+
						"'"+MyDB.controlFld(ent.CHOIX) +"',"+
						this.FIELD_ISTODO+"="+
						"'"+MyDB.controlFld(ent.ISTODO) +"',"+
						this.FIELD_LBL+"="+
						"'"+MyDB.controlFld(ent.LBL) +"',"+
						this.FIELD_FLAG+"="+
						"'"+MyDB.controlFld(ent.FLAG) +"',"+

						this.FIELD_USERMODIF+"="+
						"'"+MyDB.controlFld(ent.USERMODIF) +"' "+
						
						" where "+
						FIELD_CODEPLAN+
						"='"+ent.CODEPLAN+"' "+
						" and "+
						dbKD.fld_kd_dat_type+
						"='"+KD_TYPE+"' "+
						" and "+
						this.FIELD_CODEQUEST+"="+
						"'"+ent.CODEQUEST+"'";


				;	  		

				db.conn.execSQL(query);
			}
			else		  
			{	  		
				query="INSERT INTO " + TABLENAME +" ("+
						dbKD.fld_kd_dat_type+","+
						this.FIELD_CODEPLAN+","+
						this.FIELD_CODEREP+","+   	  		
						this.FIELD_COMMENT+","+
						this.FIELD_DATECREAT+","+
						this.FIELD_CODEQUEST+","+
						this.FIELD_DATEMODIF+","+
						this.FIELD_ISACTIF+","+
						this.FIELD_TYPE+","+
						this.FIELD_CHOIX+","+
						this.FIELD_ISTODO+","+
						this.FIELD_LBL+","+
						this.FIELD_FLAG+","+
						this.FIELD_USERCREAT+","+
						this.FIELD_USERMODIF+" "+  
					
						") VALUES ("+
						String.valueOf(KD_TYPE)+","+
						"'"+MyDB.controlFld(ent.CODEPLAN)+"',"+
						"'"+MyDB.controlFld(ent.CODEREP)+"',"+
						"'"+MyDB.controlFld(ent.COMMENT)+"',"+
						"'"+MyDB.controlFld(ent.DATECREAT)+"',"+
						"'"+MyDB.controlFld(ent.CODEQUEST)+"',"+
						"'"+MyDB.controlFld(ent.DATEMODIF)+"',"+
						"'"+MyDB.controlFld(ent.ISACTIF)+"',"+
						"'"+MyDB.controlFld(ent.TYPE)+"',"+
						"'"+MyDB.controlFld(ent.CHOIX)+"',"+
						"'"+MyDB.controlFld(ent.ISTODO)+"',"+
						"'"+MyDB.controlFld(ent.LBL)+"',"+
						"'"+MyDB.controlFld(ent.FLAG)+"',"+
						"'"+MyDB.controlFld(ent.USERCREAT)+"',"+
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
	public boolean deleteAll(String numcde)
	{
		try
		{
			String query="DELETE from "+
					TABLENAME+		
					" where "+
					dbKD.fld_kd_dat_type
					+"="+KD_TYPE+
					" and "+
					dbKD.fld_kd_cde_code+
					"='"+numcde+"'";

			db.conn.execSQL(query);
			return true;
		}
		catch(Exception ex)
		{
			Global.lastErrorMessage=ex.getLocalizedMessage();
		}
		return false;
	}

}
