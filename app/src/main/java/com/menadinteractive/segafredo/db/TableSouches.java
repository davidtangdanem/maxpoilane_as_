package com.menadinteractive.segafredo.db;

import android.content.Context;
import android.database.Cursor;

import com.menadinteractive.segafredo.communs.Fonctions;
import com.menadinteractive.segafredo.plugins.Espresso;

public class TableSouches extends DBMain{

	static public String TABLENAME="SOUCHES";
 
	public static final String TYPEDOC_FACTURE 			= "FA";
	public static final String TYPEDOC_AVOIR 			= "AV";
	public static final String TYPEDOC_BL 				= "BL";
	public static final String TYPEDOC_RETOUR 			= "RT";
	public static final String TYPEDOC_REGLEMENT 		= "RG";
	public static final String TYPEDOC_INVENTAIRE 		= "IV";
	public static final String TYPEDOC_ECHANGE			= "EL";
	public static final String TYPEDOC_PRETMACHINE		= "PM";
	public static final String TYPEDOC_RETOURMACHINE	= "RM";
	public static final String TYPEDOC_REMISEBANQUE		= "RB";
	
	public static final String FIELD_TYPEDOC				= "TYPEDOC";
	public static final String FIELD_CODVRP 				= "CODVRP";
	public static final String FIELD_NUMSOUCHE_TO			= "NUMSOUCHE_TO";
	public static final String FIELD_NUMSOUCHE_FROM			= "NUMSOUCHE_FROM";
	public static final String FIELD_NUMSOUCHE_COURANT 		= "NUMSOUCHE_COURANT";
 
 	Context m_context;
 	
	public static String getFullFieldName(String field){
		
		return TABLENAME+"."+field;
		
	}
	//efface les souche de la base de reg
	public void reset(String codvrp)
	{
		Fonctions.WriteProfileString(m_context, codvrp+TYPEDOC_FACTURE, "");
		Fonctions.WriteProfileString(m_context, codvrp+TYPEDOC_AVOIR, "");
		Fonctions.WriteProfileString(m_context, codvrp+TYPEDOC_BL, "");
		Fonctions.WriteProfileString(m_context, codvrp+TYPEDOC_RETOUR, "");
		Fonctions.WriteProfileString(m_context, codvrp+TYPEDOC_REGLEMENT, "");
		Fonctions.WriteProfileString(m_context, codvrp+TYPEDOC_INVENTAIRE, "");
		Fonctions.WriteProfileString(m_context, codvrp+TYPEDOC_ECHANGE, "");
		 
		Fonctions.WriteProfileString(m_context, codvrp+TYPEDOC_PRETMACHINE, "");
		Fonctions.WriteProfileString(m_context, codvrp+TYPEDOC_RETOURMACHINE, "");
		Fonctions.WriteProfileString(m_context, codvrp+TYPEDOC_REMISEBANQUE, "");
	 }
	
	public static final String TABLE_CREATE="CREATE TABLE ["+TABLENAME+"] ("+
			" ["+FIELD_TYPEDOC				+"] [nvarchar](50) NULL" 	+ COMMA +
			" ["+FIELD_CODVRP				+"] [nvarchar](50) NULL" 	+ COMMA +
			" ["+FIELD_NUMSOUCHE_TO			+"] [nvarchar](50) NULL" 	+ COMMA +
			" ["+FIELD_NUMSOUCHE_FROM		+"] [nvarchar](50) NULL" 	+ COMMA +
			" ["+FIELD_NUMSOUCHE_COURANT	+"] [nvarchar](50) NULL" 	+ 
			")";

	static public class passeplat
	{
		
		public String TYPEDOC = "";        
		public String CODVRP = "";
		public String  NUMSOUCHE_TO  = "";                  
		public String  NUMSOUCHE_FROM = "";  
		public String  NUMSOUCHE_COURANT = "";  
  	}
	MyDB db;
	public TableSouches(MyDB _db,Context c)
	{
		super();
		m_context=c;
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
	 
	public boolean get(String typedoc,String codvrp,passeplat cli)
	{	
		try
		{
			String query="";
			query="select * FROM "+
					TABLENAME+
					" where "+
					FIELD_TYPEDOC+
					"='"+typedoc+"'"+
					" and "+FIELD_CODVRP+"='"+codvrp+"'";


			Cursor cur=db.conn.rawQuery(query, null);
			if (cur.moveToNext())
			{
				fillStruct(cur,cli,typedoc,codvrp);
				if (cur!=null)
					cur.close();
				return true;
			}
			if (cur!=null)
				cur.close();

		}
		catch(Exception ex)
		{

		}

		return false;
	}
	

	public String get(String typedoc,String codvrp)
	{	
		try
		{
			String query="";
			query="select * FROM "+
					TABLENAME+
					" where "+
					FIELD_TYPEDOC+
					"='"+typedoc+"'"+
					" and "+FIELD_CODVRP+"='"+codvrp+"'";

			passeplat cli=new passeplat();
			Cursor cur=db.conn.rawQuery(query, null);
			if (cur.moveToNext())
			{
				fillStruct(cur,cli,typedoc,codvrp);
				if (cur!=null)
					cur.close();
				return cli.NUMSOUCHE_COURANT;
			}
			if (cur!=null)
				cur.close();

		}
		catch(Exception ex)
		{

		}

		return "";
	}
	
	void fillStruct(Cursor cur,passeplat cli,String typedoc,String codvrp)
	{
		cli.TYPEDOC=cur.getString(cur.getColumnIndex(FIELD_TYPEDOC));
		String soucheRegistry=Fonctions.GetProfileString(m_context, codvrp+typedoc, "");
		cli.NUMSOUCHE_FROM=cur.getString(cur.getColumnIndex(FIELD_NUMSOUCHE_FROM));
		cli.NUMSOUCHE_TO=cur.getString(cur.getColumnIndex(FIELD_NUMSOUCHE_TO));
		cli.NUMSOUCHE_COURANT=cur.getString(cur.getColumnIndex(FIELD_NUMSOUCHE_COURANT));
		
		//si on change de vrp ou que la souche local est vide on prend la souche de d�part
		if ((cli.NUMSOUCHE_COURANT==null || cli.NUMSOUCHE_COURANT.equals("") /*|| soucheRegistry.contains(codvrp)==false*/) && soucheRegistry.equals(""))
		{
			cli.NUMSOUCHE_COURANT=cli.NUMSOUCHE_FROM;
		}
		else
		{
			if (Fonctions.convertToInt(soucheRegistry)==0 || Fonctions.convertToLong(soucheRegistry)<Fonctions.convertToLong(cli.NUMSOUCHE_COURANT))
				cli.NUMSOUCHE_COURANT=(Fonctions.convertToLong(cli.NUMSOUCHE_COURANT)+1)+"";
			else
				cli.NUMSOUCHE_COURANT=(Fonctions.convertToLong(soucheRegistry))+"";
		}
		Fonctions.WriteProfileString(m_context, codvrp+typedoc, cli.NUMSOUCHE_COURANT);
	

		//if ((cli.NUMSOUCHE_COURANT==null || cli.NUMSOUCHE_COURANT.equals("") /*|| soucheRegistry.contains(codvrp)==false*/) )
		//{
		//	cli.NUMSOUCHE_COURANT=cli.NUMSOUCHE_FROM;

		//}

	}
	 
	public boolean incNum(String codvrp,String typedoc)
	{
		String val=Fonctions.GetProfileString(m_context, codvrp+typedoc, "");
		if (Fonctions.convertToLong(val)==0)
		{
			return false;
		}
		
		val=(Fonctions.convertToLong(val)+1)+"";
		
		
		return Fonctions.WriteProfileString(m_context, codvrp+typedoc, val);
		
		
	}
	public boolean incNumSouche(String codvrp,String typedoc,String valeur)
	{
				
		return Fonctions.WriteProfileString(m_context, codvrp+typedoc, valeur);
		
		
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

}
