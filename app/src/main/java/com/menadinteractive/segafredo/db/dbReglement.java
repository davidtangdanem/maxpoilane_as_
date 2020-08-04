package com.menadinteractive.segafredo.db;

import java.util.ArrayList;

import com.menadinteractive.segafredo.communs.Fonctions;
import com.menadinteractive.segafredo.communs.Global;

import android.database.Cursor;
import android.os.Bundle;

public class dbReglement extends DBMain{
	
/*	public String TABLENAME="SITE_REGLEMENT";
	
	public final String FIELD_RGL_SOC_CODE        	="SOC_CODE" ;	 
	public final String FIELD_RGL_CODE        	="CODE" 	 		;                 
	public final String FIELD_RGL_N_REGLEMENT      		="N_REGLEMENT" 		;                  
	public final String FIELD_RGL_R_INTITULE       ="R_INTITULE"  	;  
	public final String FIELD_RGL_ER_CONDITION    ="ER_CONDITION"  	;  
	public final String FIELD_RGL_LBL_CONDITION       	="LBL_CONDITION"  	;  
	public final String FIELD_RGL_NB_JOURS       	="NB_JOURS"  	;  
	public final String FIELD_RGL_PAYS       	="RGPAYS"  	; 
	public final String FIELD_RGL_ESCOMPTE       	="ESCOMPTE"  	; //valeur de l'escompte
	
	public final String FLD_PARAMRGL_CODEREC = "prm_coderec";
	public final String FLD_PARAMRGL_LBL = "prm_lbl";
	
	public static final String TABLE_CREATE=
			"CREATE TABLE [SITE_REGLEMENT]("+
					"		[SOC_CODE] [nvarchar](10) NULL,"+
					"		[CODE] [nvarchar](100) NULL,"+
					"		[N_REGLEMENT] [nvarchar](255) NULL,"+
					"		[R_INTITULE] [nvarchar](255) NULL,"+
					"		[ER_CONDITION] [nvarchar](255) NULL,"+
					"		[LBL_CONDITION] [nvarchar](255) NULL,"+
					"		[NB_JOURS] [nvarchar](255) NULL,"+
					"		[RGPAYS] [nvarchar](20) NULL,"+
					"		[ESCOMPTE] [nvarchar](20) NULL"+
					")";

	
	MyDB db;
	public dbReglement(MyDB _db)
	{
		super();
		db=_db;
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
	public String getCodereglement(String regl,String cond,String nbjour)
	{
		String coderegl="";
		try
		{
	
			boolean bres=true;
			
	
			String query="SELECT * FROM "+
					TABLENAME+
					" WHERE "+
					this.FIELD_RGL_N_REGLEMENT+
					"="+
					"'"+regl+"' and "+
					this.FIELD_RGL_ER_CONDITION+
					"="+
					"'"+cond+"' and "+
					this.FIELD_RGL_NB_JOURS+
					"="+
					"'"+nbjour+"' "+
					" and "+
					this.FIELD_RGL_SOC_CODE+
					"="+
					"'"+Global.AXE_Client.SOC_CODE+"'";
			
			
			Cursor cur=db.conn.rawQuery(query, null);
			while (cur.moveToNext())
			{

				bres=true;
				coderegl	=giveFld(cur,this.FIELD_RGL_CODE);
				
			
			}
			cur.close();
		}
		catch(Exception ex)
		{
			return coderegl;
		}



		return coderegl;
	}
	public boolean getreglCondnbjour(String coderegl,ArrayList<String> Value)
	{
		try
		{
	
			boolean bres=true;
			String m_regl="";
			String m_condi="";
			String m_nbjour="";
			String escompte="0";

			String query="SELECT * FROM "+
					TABLENAME+
					" WHERE "+
					this.FIELD_RGL_CODE+
					"="+
					"'"+coderegl+"'"+
					" and "+
					this.FIELD_RGL_SOC_CODE+
					"="+
					"'"+Global.AXE_Client.SOC_CODE+"'";

		
			Cursor cur=db.conn.rawQuery(query, null);
			while (cur.moveToNext())
			{

				bres=true;
				m_regl	=giveFld(cur,this.FIELD_RGL_N_REGLEMENT);
				m_condi		=giveFld(cur,this.FIELD_RGL_ER_CONDITION);
				m_nbjour	=giveFld(cur,this.FIELD_RGL_NB_JOURS);
				escompte	=giveFld(cur,this.FIELD_RGL_ESCOMPTE);

				Value.add(Fonctions.GetStringDanem(m_regl));
				Value.add(Fonctions.GetStringDanem(m_condi));
				Value.add(Fonctions.GetStringDanem(m_nbjour));
			
			}
			cur.close();
		}
		catch(Exception ex)
		{
			return false;
		}



		return true;
	}
	
	
	public boolean getLblReglement(String pays, ArrayList<Bundle> liste)
	{
		boolean res=_getLblReglement(pays,liste);
		if(res==false)
			res=_getLblReglement("",liste);
		
		return res;
	}
	public boolean _getLblReglement(String pays,ArrayList<Bundle> liste)
	{
		try
		{
			
			liste.clear();

			String query="SELECT * FROM "+
					TABLENAME+
					" where  "+
					this.FIELD_RGL_SOC_CODE+
					"="+
					"'"+Global.AXE_Client.SOC_CODE+"'";
					if (!pays.equals(""))
						query+=" and "+this.FIELD_RGL_PAYS+"='"+pays+"' ";
					else
						query+=" and ( "+ this.FIELD_RGL_PAYS+"='' OR "+ this.FIELD_RGL_PAYS+" IS NULL )";
					query+=" ORDER BY "+this.FIELD_RGL_CODE;

			String prm_coderec	="";
			String prm_lbl		="";
			String prm_lblreglement		="";
			String prm_lblcondition	="";
			String prm_lbljours	="";
			String prm_escompte="0";
			
			if (Global.dbTwin.Affiche2(Global.dbTwin.load_affiche(Global.dbTwin.TWIN_CDE_ENT_REGLEMENT_BLANC),true)==true)
			{
				Bundle bundleR_CondRegl = new Bundle();
				bundleR_CondRegl.putString(FLD_PARAMRGL_CODEREC, "");
				bundleR_CondRegl.putString(FLD_PARAMRGL_LBL,"");
				liste.add(bundleR_CondRegl);
			}
		
		
			
			Cursor cur=db.conn.rawQuery(query, null);
			while (cur.moveToNext())
			{

				prm_coderec	=giveFld(cur,this.FIELD_RGL_CODE);
				prm_lblreglement		=giveFld(cur,this.FIELD_RGL_R_INTITULE);
				prm_lblcondition	=giveFld(cur,this.FIELD_RGL_LBL_CONDITION);
				prm_lbljours	=giveFld(cur,this.FIELD_RGL_NB_JOURS);
				prm_escompte	=giveFld(cur,this.FIELD_RGL_ESCOMPTE);
				
				prm_lbl=prm_lblreglement+ " - "+prm_lbljours+" "+ prm_lblcondition;
				Bundle bundle = new Bundle();
				bundle.putString(FLD_PARAMRGL_CODEREC, prm_coderec);
				bundle.putString(FLD_PARAMRGL_LBL, prm_lbl);
				


				liste.add(bundle);

			}
			cur.close();
			if (liste.size()==0) return false;
		}
		catch(Exception ex)
		{
			return false;
		}


		return true;
	}
	public String getEscompteReglement(String pays,String coderegl)
	{
		String prm_escompte="0";
		try
		{
			

			String query="SELECT * FROM "+
					TABLENAME+
					" where  "+
					this.FIELD_RGL_SOC_CODE+
					"="+
					"'"+Global.AXE_Client.SOC_CODE+"' AND "+this.FIELD_RGL_CODE+"='"+coderegl+"' ";
					if (!pays.equals(""))
						query+=" and "+this.FIELD_RGL_PAYS+"='"+pays+"' ";
					else
						query+=" and ( "+ this.FIELD_RGL_PAYS+"='' OR "+ this.FIELD_RGL_PAYS+" IS NULL )";
					
		
			Cursor cur=db.conn.rawQuery(query, null);
			while (cur.moveToNext())
			{
				prm_escompte	=giveFld(cur,this.FIELD_RGL_ESCOMPTE);
			}
			cur.close();
			
		}
		catch(Exception ex)
		{
			return prm_escompte;
		}


		return prm_escompte;
	}
	public String  getStlblreglement(String code)
	{
		String stvaleur="";
		try
		{
			
			
			String query="SELECT * FROM "+
					TABLENAME+
					"  "+
					" WHERE "+
					this.FIELD_RGL_CODE+"='"+code+"'"+
					" and "+
					this.FIELD_RGL_SOC_CODE+
					"="+
					"'"+Global.AXE_Client.SOC_CODE+"'";

			String prm_coderec	="";
			String prm_lbl		="";
			String prm_lblreglement		="";
			String prm_lblcondition	="";
			String prm_lbljours	="";
			
		
			Cursor cur=db.conn.rawQuery(query, null);
			while (cur.moveToNext())
			{

				prm_coderec	=giveFld(cur,this.FIELD_RGL_CODE);
				prm_lblreglement		=giveFld(cur,this.FIELD_RGL_R_INTITULE);
				prm_lblcondition	=giveFld(cur,this.FIELD_RGL_LBL_CONDITION);
				prm_lbljours	=giveFld(cur,this.FIELD_RGL_NB_JOURS);

				stvaleur=prm_lblreglement+ " - "+prm_lbljours+" "+ prm_lblcondition;
				
				


				

			}
			cur.close();
		}
		catch(Exception ex)
		{
			return stvaleur;
		}


		return stvaleur;
	}*/

}
