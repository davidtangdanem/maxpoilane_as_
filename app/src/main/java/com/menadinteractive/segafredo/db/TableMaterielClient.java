package com.menadinteractive.segafredo.db;

import java.util.ArrayList;

import android.R;
import android.database.Cursor;
import android.os.Bundle;

import com.menadinteractive.segafredo.communs.Global;
import com.menadinteractive.segafredo.helper.Taxe;

public class TableMaterielClient extends DBMain{

	static public String TABLENAME="MATERIELCLIENT";

	public static final String FIELD_CODECLIENT		= "PAR_CODECLIENT";
	public static final String FIELD_CODEART		= "PAR_CODEART";
	public static final String FIELD_LIBART			= "PAR_LIBART";
	public static final String FIELD_PLAQUESZ 		= "PAR_PLAQUESZ";
	public static final String FIELD_NUMSERIEFAB 		= "PAR_NUMSERIEFAB";
	public static final String FIELD_CODVRP 		= "PAR_CODVRP";


	public static String getFullFieldName(String field){
		return TABLENAME+"."+field;
	}


	public static final String TABLE_CREATE="CREATE TABLE ["+TABLENAME+"] ("+
			" ["+FIELD_CODECLIENT			+"] [varchar](50) NULL" 	+ COMMA +
			" ["+FIELD_CODEART		+"] [varchar](50) NULL" 	+ COMMA +
			" ["+FIELD_LIBART		+"] [varchar](255) NULL" 	+ COMMA +
			" ["+FIELD_PLAQUESZ		+"] [varchar](255)NULL" 	+ COMMA +
			" ["+FIELD_NUMSERIEFAB			+"] [varchar](255) NULL" 	+ COMMA +
			" ["+FIELD_CODVRP			+"] [varchar](50) NULL"	+ 
			")";
	
	
	static public class structMaterielClient
	{
		
		
		public String PAR_CODECLIENT = "";        
		public String PAR_CODEART = "";
		public String PAR_LIBART = "";
		public String PAR_PLAQUESZ  = "";                  
		public String PAR_NUMSERIEFAB = "";  
		public String PAR_CODVRP = "";  
		
		@Override
		public String toString() {
			return "structClient [PAR_CODECLIENT=" + PAR_CODECLIENT + ", PAR_CODEART=" + PAR_CODEART
					+ ", PAR_LIBART=" + PAR_LIBART + ", PAR_PLAQUESZ=" + PAR_PLAQUESZ + ", PAR_NUMSERIEFAB="
					+ PAR_NUMSERIEFAB +  ", PAR_CODVRP=" + PAR_CODVRP 
					+"]";
		}
	}
		
	MyDB db;
	public TableMaterielClient(MyDB _db)
	{
		super();
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

	public structMaterielClient load(Cursor cursor){
		structMaterielClient MaterielClient = new structMaterielClient();
		if(cursor != null){
			
		
			MaterielClient.PAR_CODECLIENT = giveFld(cursor, FIELD_CODECLIENT);
			MaterielClient.PAR_CODEART = giveFld(cursor, FIELD_CODEART);
			MaterielClient.PAR_LIBART = giveFld(cursor, FIELD_LIBART);
			MaterielClient.PAR_PLAQUESZ = giveFld(cursor, FIELD_PLAQUESZ);
			MaterielClient.PAR_NUMSERIEFAB = giveFld(cursor, FIELD_NUMSERIEFAB);
			MaterielClient.PAR_CODVRP = giveFld(cursor, FIELD_CODVRP);
		

		}

	
		return MaterielClient;
	}
	

	public structMaterielClient load(String cod_cli, String cod_pro){
		structMaterielClient MaterielClient = new structMaterielClient();
		String query = "SELECT *"
				+" FROM "+TABLENAME
				+" WHERE "+getFullFieldName(FIELD_CODECLIENT)+" = "+cod_cli+" "
				+ "and "+getFullFieldName(FIELD_CODEART)+" = "+cod_pro+"";

		Cursor cursor =  db.conn.rawQuery(query, null);

		if(cursor != null && cursor.moveToFirst()){

			MaterielClient = load(cursor);
			cursor.close();
		}

		return MaterielClient;
	}
	public structMaterielClient load(String cod_cli){
		structMaterielClient MaterielClient = new structMaterielClient();
		String query = "SELECT *"
				+" FROM "+TABLENAME
				+" WHERE "+getFullFieldName(FIELD_CODECLIENT)+" = "+cod_cli+" ";
		
		
		Cursor cursor =  db.conn.rawQuery(query, null);

		if(cursor != null && cursor.moveToFirst()){

			MaterielClient = load(cursor);
			cursor.close();
		}

		return MaterielClient;
	}

	

	/**
	 * Permet d'obtenir une liste de structTarif à partir d'un cursor
	 * @param cursor
	 * @return ArrayList<structTarif>
	 */
	public ArrayList<structMaterielClient> getListOfCursorMaterielClient(Cursor cursor){
		ArrayList<structMaterielClient> list = new ArrayList<TableMaterielClient.structMaterielClient>();

		cursor.moveToFirst();
		while(!cursor.isAfterLast()){
			structMaterielClient MaterielClient = new structMaterielClient();
			fillStruct(cursor,MaterielClient);
			list.add(MaterielClient);
			cursor.moveToNext();
		}
		cursor.close();
		return list;

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


	

	void fillStruct(Cursor cur,structMaterielClient materiel)
	{
		
		
		materiel.PAR_CODECLIENT=cur.getString(cur.getColumnIndex(FIELD_CODECLIENT));
		materiel.PAR_CODECLIENT=cur.getString(cur.getColumnIndex(FIELD_CODEART));
		materiel.PAR_LIBART=cur.getString(cur.getColumnIndex(FIELD_LIBART));
		materiel.PAR_PLAQUESZ=cur.getString(cur.getColumnIndex(FIELD_PLAQUESZ));
		materiel.PAR_NUMSERIEFAB=cur.getString(cur.getColumnIndex(FIELD_NUMSERIEFAB));
		materiel.PAR_CODVRP=cur.getString(cur.getColumnIndex(FIELD_CODVRP));
		
	}
	public ArrayList<Bundle> getMaterielClientFilters(String codeclient,  String filter)
	{
		try
		{
			filter=MyDB.controlFld(filter);
			String query="";
			query="select * FROM "+
					TABLENAME +" left join "+
					dbKD451RetourMachineclient.TABLENAME+
					" on "+
					dbKD451RetourMachineclient.FIELD_RMC_CODEART+"="+FIELD_CODEART+
					" and "+
					dbKD451RetourMachineclient.FIELD_RMC_CODECLI+"="+FIELD_CODECLIENT+
					" and "+
					dbKD451RetourMachineclient.FIELD_RMC_NUMSERIE+"="+FIELD_NUMSERIEFAB+
					" and "+
					dbKD451RetourMachineclient.FIELD_RMC_NUMSZ+"="+FIELD_PLAQUESZ+
					
					" where ("+ 
					FIELD_LIBART+ " like '%"+filter+"%' "+
					" or "+
					FIELD_NUMSERIEFAB+ " like '%"+filter+"%' "+
					" or "+
					FIELD_PLAQUESZ+ " like '%"+filter+"%' "+
					" or "+
					FIELD_CODEART+ " like '%"+filter+"%' ) and "+
					FIELD_CODECLIENT+
					"='"+codeclient+"'    ";
			
			
			ArrayList<Bundle>  materiel=new ArrayList<Bundle>();
			Cursor cur=db.conn.rawQuery(query, null);
			while (cur.moveToNext())
			{
				Bundle cli=new Bundle();
				cli.putString(Global.dbMaterielClient.FIELD_CODECLIENT,cur.getString(cur.getColumnIndex(Global.dbMaterielClient.FIELD_CODECLIENT)));
				cli.putString(Global.dbMaterielClient.FIELD_CODEART, cur.getString(cur.getColumnIndex(Global.dbMaterielClient.FIELD_CODEART)));
				cli.putString(Global.dbMaterielClient.FIELD_LIBART, cur.getString(cur.getColumnIndex(Global.dbMaterielClient.FIELD_LIBART)));
				cli.putString(Global.dbMaterielClient.FIELD_PLAQUESZ, cur.getString(cur.getColumnIndex(Global.dbMaterielClient.FIELD_PLAQUESZ)));
				cli.putString(Global.dbMaterielClient.FIELD_NUMSERIEFAB, cur.getString(cur.getColumnIndex(Global.dbMaterielClient.FIELD_NUMSERIEFAB)));
				cli.putString(Global.dbMaterielClient.FIELD_CODVRP, cur.getString(cur.getColumnIndex(Global.dbMaterielClient.FIELD_CODVRP)));
	 				
				String isdeleted=cur.getString(cur.getColumnIndex(dbKD451RetourMachineclient.FIELD_RMC_DATE));
				if (isdeleted==null || isdeleted.equals(""))
					cli.putString(dbKD451RetourMachineclient.FIELD_RMC_DATE,"" );
				else
					cli.putString(dbKD451RetourMachineclient.FIELD_RMC_DATE,"success48_optionmenu");
					
				String issent=cur.getString(cur.getColumnIndex(dbKD451RetourMachineclient.FIELD_RMC_SENT));
				if (issent!=null && issent.equals("1"))
					continue;
					
				
				materiel.add(cli); 
			}
			if (cur!=null)
				cur.close();
			return materiel;
		}
		catch(Exception ex)
		{
			String err="";
			err=ex.getMessage();
			
		}

		return null;
	}
	
}
