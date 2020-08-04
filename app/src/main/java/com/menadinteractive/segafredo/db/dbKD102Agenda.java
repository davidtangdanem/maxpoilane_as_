package com.menadinteractive.segafredo.db;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


import com.menadinteractive.segafredo.communs.Fonctions;
import com.menadinteractive.segafredo.communs.Global;
import com.menadinteractive.segafredo.plugins.Espresso;

import android.content.Context;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;



public class dbKD102Agenda extends dbKD{
	
	public final int KD_TYPE = 102;
	public final String FIELD_SOC_CODE      = fld_kd_soc_code;
	public final String FIELD_CODEREP     	= fld_kd_dat_idx01;
	public final String FIELD_ID     		= fld_kd_dat_idx02;
	public final String FIELD_CODECLI      	= fld_kd_cli_code;
	public final String FIELD_OBJET   		= fld_kd_dat_data01;
	public final String FIELD_DESCRIPTION   = fld_kd_dat_data02;
	public final String FIELD_DTDEBUT       = fld_kd_dat_data03;
	public final String FIELD_DTFIN         = fld_kd_dat_data04;
	public final String FIELD_DURATION      = fld_kd_dat_data05;
	public final String FIELD_FLAG        	= fld_kd_dat_data08; //1 = modifi�, 2=delete
	
	MyDB db;
	public dbKD102Agenda(MyDB _db)
	{
		super();
		db=_db;	
	}

	static public class passePlat {
		public static final String FIELD_FLAG = null;
		public passePlat()
		{		
			FIELD_SOC_CODE ="";     
			FIELD_ID     		="";
			FIELD_CODECLI      	="";
			FIELD_OBJET   ="";
			FIELD_DESCRIPTION   ="";
			FIELD_DTDEBUT       ="";
			FIELD_DTFIN         ="";
			FIELD_DURATION      ="";
			FIELD_CODEREP="";
		}

		public String FIELD_SOC_CODE     ; 
		public String FIELD_ID     		;
		public String FIELD_CODEREP     		;
		public String FIELD_CODECLI     ; 	
		public String FIELD_OBJET; 
		public String FIELD_DESCRIPTION; 
		public String FIELD_DTDEBUT ;      
		public String FIELD_DTFIN  ;
		public String FIELD_DURATION  ;
  
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
				int i= cur.getInt(0);
				if (cur!=null)
					cur.close();//MV 26/03/2015
				return i;
			}
			
			if (cur!=null)
				cur.close();//MV 26/03/2015
			return 0;
		}
		catch(Exception ex)
		{
			return -1;
		}

	}
	public int countModified()
	{

		try
		{
			String query="select count(*) from "+TABLENAME+" where "+
					fld_kd_dat_type +"='"+KD_TYPE+"'"+
					" and "+
					this.FIELD_FLAG+"<>'"+ KDSYNCHRO_RESET+  "'" ;
					
						

			Cursor cur=db.conn.rawQuery(query, null);
			if (cur.moveToNext())
			{
				int i= cur.getInt(0);
				if (cur!=null)
					cur.close();//MV 26/03/2015
				return i;
			}
			
			if (cur!=null)
				cur.close();//MV 26/03/2015
			return 0;
		}
		catch(Exception ex)
		{
			return -1;
		}

	}
	
	
	public boolean save(passePlat ent)
	{
		try
		{
			//on efface l'existant et on sauve
			//delete(ent.FIELD_ID);
				
			String query="INSERT INTO " + TABLENAME +" ("+
					dbKD.fld_kd_dat_type+","+
					this.FIELD_SOC_CODE   		+","+
					this.FIELD_CODECLI   		+","+ 
					this.FIELD_CODEREP   		+","+ 
					this.FIELD_OBJET			  +","+ 
					this.FIELD_DESCRIPTION      +","+  
					this.FIELD_DTDEBUT    		+","+
					this.FIELD_DTFIN  			+","+
					this.FIELD_ID 				+","+
					this.FIELD_FLAG+""+

	  		") VALUES ("+
	  		String.valueOf(KD_TYPE)+","+
	  		"'"+ent.FIELD_SOC_CODE+"',"+
	  		"'"+ent.FIELD_CODECLI+"',"+
	  		"'"+ent.FIELD_CODEREP+"',"+
	  		"'"+MyDB.controlFld(ent.FIELD_OBJET)+"',"+
	  		"'"+MyDB.controlFld(ent.FIELD_DESCRIPTION)+"',"+
	  		"'"+ent.FIELD_DTDEBUT+"',"+
	  		"'"+ent.FIELD_DTFIN+"',"+
	  		"'"+passePlat.FIELD_FLAG+"',"+
	  		"'"+KDSYNCHRO_UPDATE +"')";
			
			Global.dbLog.Insert("DB 102AGENDA","SAVE","",query, "","");
			db.conn.execSQL(query);
	
		}
		catch(Exception ex)
		{
			Global.lastErrorMessage=(ex.getMessage());
			return false;
		}

		return true;
	}

	public boolean deleteAll()
	{
		try
		{
			String query="DELETE from "+
					TABLENAME+		
					" where "+
					
					dbKD.fld_kd_dat_type+
					"='"+KD_TYPE+"' ";

			db.conn.execSQL(query);
			return true;
		}
		catch(Exception ex)
		{
			Global.lastErrorMessage=(ex.getMessage());
		}
		return false;
	}
	
	public boolean delete(String id)
	{
		try
		{
			String query="DELETE from "+
					TABLENAME+		
					" where "+
					FIELD_ID+
					"='"+id+"'"+
					
					" and "+
					dbKD.fld_kd_dat_type+
					"='"+KD_TYPE+"' ";
			Global.dbLog.Insert("DB 102AGENDA","DELETE","",query, "","");
			db.conn.execSQL(query);
			return true;
		}
		catch(Exception ex)
		{
			Global.lastErrorMessage=(ex.getMessage());
		}
		return false;
	}
	public boolean resetFlag(StringBuffer err)
	{
		try
		{
			String query="UPDATE "+
					TABLENAME+		
					" SET "+
					dbKD.fld_kd_dat_data08+
					"='"+KDSYNCHRO_RESET+"'"+
					" where "+
					dbKD.fld_kd_dat_type+
					"='"+KD_TYPE+"' ";

			db.conn.execSQL(query);
			return true;
		}
		catch(Exception ex)
		{
			err.append(ex.getMessage());
		}
		return false;
	}
	public boolean DeleteFlag(String Codeclient,String stcoderayon,StringBuffer err)
	{
		try
		{
			String query="UPDATE "+
					TABLENAME+		
					" SET "+
					dbKD.fld_kd_dat_data08+
					"='"+KDSYNCHRO_DELETE+"'"+
					" where "+
					dbKD.fld_kd_dat_type+
					"='"+KD_TYPE+"' and "+
					dbKD.fld_kd_cli_code+
					"='"+Codeclient+"'"+
					" and "+
					dbKD.fld_kd_dat_data01+
					"='"+stcoderayon+"' "+
					" ";

			db.conn.execSQL(query);
			return true;
		}
		catch(Exception ex)
		{
			err.append(ex.getMessage());
		}
		return false;
	}
	
	public Cursor getAllEvents(String applicationKey,Context c){
		Cursor result = null;
		try {
			String[] PROJECTION=new String[] {
					BaseColumns._ID,
					CalendarContract.Events.TITLE,
					CalendarContract.Events.DESCRIPTION,
					CalendarContract.Events.DTSTART,
					CalendarContract.Events.DTEND,
					CalendarContract.Events.DURATION

			};
			String where = CalendarContract.Events.TITLE +" LIKE '%"+applicationKey+"[%'"
					+" AND "+Events.DELETED+" = '0' AND "+Events.DTSTART+">="+Agenda_getFrom( AgendaToKD_getDateFrom());
			result = c.getContentResolver().query(CalendarContract.Events.CONTENT_URI, PROJECTION, where, null, CalendarContract.Events.DTSTART+" DESC");

			deleteAll();//on efface tout et on re-rempli
			if(result != null && result.moveToFirst()){
				while(result.isAfterLast() == false)
				{
					
					
				//	Debug.Log(result.getString(result.getColumnIndex(CalendarContract.Events.TITLE)));
					passePlat pp=new passePlat();
					String title=result.getString(result.getColumnIndex(CalendarContract.Events.TITLE));
					pp.FIELD_OBJET=title;
					pp.FIELD_DESCRIPTION=result.getString(result.getColumnIndex(CalendarContract.Events.DESCRIPTION));
					pp.FIELD_CODECLI=Fonctions.GiveFld(title, 1, "[", true);
					pp.FIELD_CODECLI=Fonctions.GiveFld(pp.FIELD_CODECLI, 0, "]", true);
					pp.FIELD_ID=result.getString(result.getColumnIndex(BaseColumns._ID));
					pp.FIELD_DTDEBUT=result.getString(result.getColumnIndex(CalendarContract.Events.DTSTART));
					pp.FIELD_DTFIN=result.getString(result.getColumnIndex(CalendarContract.Events.DTEND));
					pp.FIELD_DURATION=result.getString(result.getColumnIndex(CalendarContract.Events.DURATION));
					pp.FIELD_CODEREP=Preferences.getValue(c, Espresso.LOGIN, "0");
					
					DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd HHmmss");
					pp.FIELD_DTDEBUT = dateFormat.format(Fonctions.convertToLong(pp.FIELD_DTDEBUT));
					pp.FIELD_DTFIN = dateFormat.format(Fonctions.convertToLong(pp.FIELD_DTFIN));
					pp.FIELD_SOC_CODE="1";
					
					Global.dbKDAgenda.save(pp);
					result.moveToNext();
				}
				result.close();

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}


		return result;
	}
	
	/*
	 * 	renvoi la date à partir de laquelle on renvoi les agenda
	 * j-7
	 * 
	 */
	String AgendaToKD_getDateFrom()
	{
		String date=Fonctions.getYYYYMMDD(-7);
		
		return date;
	}
	
	long Agenda_getFrom(String yyyymmdd)
	{
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
			Date date = format.parse(yyyymmdd+" 00:00:00");
			long timestamp = date.getTime();
			
			return timestamp;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}
	
	public String queryDeleteFromOnServer(Context c)
	{
		String del="delete from "+TABLENAME+
				" where "+FIELD_DTDEBUT+">='"+AgendaToKD_getDateFrom()+"'"+
				" and "+dbKD.fld_kd_dat_type+"="+KD_TYPE+
				" and "+FIELD_CODEREP+"='"+Preferences.getValue(c, Espresso.LOGIN, "0")+"'";
		return del;
	}
	public void RDVClient(ArrayList<String> ValueRDV,String codeclient)
	{
		String dataDay=Fonctions.getYYYYMMDD();
		String NextRDV="";
		String LastRDV="";
		String date="";

		boolean bexiste=false;
		
			String query="SELECT * from "+
					TABLENAME+	
					" where "+
					" "+
					dbKD.fld_kd_dat_type+
					"='"+KD_TYPE+"' "+
					" and "+
					dbKD.fld_kd_cli_code+
					"='"+codeclient+"' and "+
					" ( "+
					this.FIELD_FLAG+"<>'"+ KDSYNCHRO_RESET+  "' and "+this.FIELD_FLAG+"<>'"+ KDSYNCHRO_DELETE+  "' ) "  +
					"order by  "+FIELD_DTDEBUT+" desc ";
			
		try {
			Cursor cur=db.conn.rawQuery(query, null);
			//if(cur != null && cur.moveToFirst()){
			
				while (cur.moveToNext())
				//while(cur.isAfterLast() == false)
				{
						bexiste=true;
						date = cur.getString(cur.getColumnIndex(this.FIELD_DTDEBUT));
						if(Fonctions.GetStringToIntDanem(Fonctions.Left(date, 8))>=Fonctions.GetStringToIntDanem(dataDay))
						{
							ValueRDV.add("Prochain rendez-vous : "+Fonctions.YYYYMMDDhhmmss_to_dd_mm_yyyy_hh_mm_ss2(date)  );
							ValueRDV.add("1");
						}
						else
						{
							ValueRDV.add("Dernier rendez-vous : "+Fonctions.YYYYMMDDhhmmss_to_dd_mm_yyyy_hh_mm_ss2(date)  );
							ValueRDV.add("0");
						}
						
						//cur.moveToNext();
						break;
				}
			//}
			
				
				if (cur!=null)
					cur.close();//MV 26/03/2015
			if(bexiste==false)
			{
				ValueRDV.add("");
				ValueRDV.add("");	
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			if(bexiste==false)
			{
				ValueRDV.add("");
				ValueRDV.add("");	
			}
		}
		
	}
		

}
