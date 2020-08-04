package com.menadinteractive.segafredo.db;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import android.content.Context;
import android.database.Cursor;

import com.menadinteractive.maxpoilane.Debug;
import com.menadinteractive.maxpoilane.R;

public class TableHoraire extends DBMain{
	static public String TABLENAME="HORAIRE";

	public static final String FIELD_CODE_ALIAS = "HORAIRECODE";
	
	public static final String FIELD_CODE = "HCODECLI";
	public static final String FIELD_CODEVRP = "CODEVRP";

	public static final String FIELD_LUNDI_AM_DEBUT = "LUN_AD";
	public static final String FIELD_LUNDI_AM_FIN = "LUN_AF";
	public static final String FIELD_LUNDI_PM_DEBUT = "LUN_PD";
	public static final String FIELD_LUNDI_PM_FIN = "LUN_PF";

	public static final String FIELD_MARDI_AM_DEBUT = "MAR_AD";
	public static final String FIELD_MARDI_AM_FIN = "MAR_AF";
	public static final String FIELD_MARDI_PM_DEBUT = "MAR_PD";
	public static final String FIELD_MARDI_PM_FIN = "MAR_PF";

	public static final String FIELD_MERCREDI_AM_DEBUT = "MER_AD";
	public static final String FIELD_MERCREDI_AM_FIN = "MER_AF";
	public static final String FIELD_MERCREDI_PM_DEBUT = "MER_PD";
	public static final String FIELD_MERCREDI_PM_FIN = "MER_PF";

	public static final String FIELD_JEUDI_AM_DEBUT = "JEU_AD";
	public static final String FIELD_JEUDI_AM_FIN = "JEU_AF";
	public static final String FIELD_JEUDI_PM_DEBUT = "JEU_PD";
	public static final String FIELD_JEUDI_PM_FIN = "JEU_PF";	

	public static final String FIELD_VENDREDI_AM_DEBUT = "VEN_AD";
	public static final String FIELD_VENDREDI_AM_FIN = "VEN_AF";
	public static final String FIELD_VENDREDI_PM_DEBUT = "VEN_PD";
	public static final String FIELD_VENDREDI_PM_FIN = "VEN_PF";

	public static final String FIELD_SAMEDI_AM_DEBUT = "SAM_AD";
	public static final String FIELD_SAMEDI_AM_FIN = "SAM_AF";
	public static final String FIELD_SAMEDI_PM_DEBUT = "SAM_PD";
	public static final String FIELD_SAMEDI_PM_FIN = "SAM_PF";

	public static final String FIELD_DIMANCHE_AM_DEBUT = "DIM_AD";
	public static final String FIELD_DIMANCHE_AM_FIN = "DIM_AF";
	public static final String FIELD_DIMANCHE_PM_DEBUT = "DIM_PD";
	public static final String FIELD_DIMANCHE_PM_FIN = "DIM_PF";

	//	public static final String FIELD_JOUR = "JOUR";  //"0" = lundi, "6"=dimanche
	//	public static final String FIELD_OUVERT= "OUVERT";
	//	public static final String FIELD_AM_DEBUT= "AM_DEBUT";
	//	public static final String FIELD_AM_FIN= "AM_FIN";
	//	public static final String FIELD_PM_DEBUT= "PM_DEBUT";
	//	public static final String FIELD_PM_FIN= "PM_FIN";

	public static String getFullFieldName(String field){
		return TABLENAME+"."+field; 
	}


	public static final String TABLE_CREATE="CREATE TABLE ["+TABLENAME+"] ("+
			" ["+FIELD_CODE+"] [nvarchar](100) NULL" + COMMA +
			" ["+FIELD_CODEVRP+"] [nvarchar](255) NULL" + COMMA +

			" ["+FIELD_LUNDI_AM_DEBUT+"] [nvarchar](5) NULL" + COMMA +
			" ["+FIELD_LUNDI_AM_FIN+"] [nvarchar](5) NULL" + COMMA +
			" ["+FIELD_LUNDI_PM_DEBUT+"] [nvarchar](5) NULL" + COMMA +
			" ["+FIELD_LUNDI_PM_FIN+"] [nvarchar](5) NULL" + COMMA +

			" ["+FIELD_MARDI_AM_DEBUT+"] [nvarchar](5) NULL" + COMMA +
			" ["+FIELD_MARDI_AM_FIN+"] [nvarchar](5) NULL" + COMMA +
			" ["+FIELD_MARDI_PM_DEBUT+"] [nvarchar](5) NULL" + COMMA +
			" ["+FIELD_MARDI_PM_FIN+"] [nvarchar](5) NULL" + COMMA +

			" ["+FIELD_MERCREDI_AM_DEBUT+"] [nvarchar](5) NULL" + COMMA +
			" ["+FIELD_MERCREDI_AM_FIN+"] [nvarchar](5) NULL" + COMMA +
			" ["+FIELD_MERCREDI_PM_DEBUT+"] [nvarchar](5) NULL" + COMMA +
			" ["+FIELD_MERCREDI_PM_FIN+"] [nvarchar](5) NULL" + COMMA +

			" ["+FIELD_JEUDI_AM_DEBUT+"] [nvarchar](5) NULL" + COMMA +
			" ["+FIELD_JEUDI_AM_FIN+"] [nvarchar](5) NULL" + COMMA +
			" ["+FIELD_JEUDI_PM_DEBUT+"] [nvarchar](5) NULL" + COMMA +
			" ["+FIELD_JEUDI_PM_FIN+"] [nvarchar](5) NULL" + COMMA +

			" ["+FIELD_VENDREDI_AM_DEBUT+"] [nvarchar](5) NULL" + COMMA +
			" ["+FIELD_VENDREDI_AM_FIN+"] [nvarchar](5) NULL" + COMMA +
			" ["+FIELD_VENDREDI_PM_DEBUT+"] [nvarchar](5) NULL" + COMMA +
			" ["+FIELD_VENDREDI_PM_FIN+"] [nvarchar](5) NULL" + COMMA +

			" ["+FIELD_SAMEDI_AM_DEBUT+"] [nvarchar](5) NULL" + COMMA +
			" ["+FIELD_SAMEDI_AM_FIN+"] [nvarchar](5) NULL" + COMMA +
			" ["+FIELD_SAMEDI_PM_DEBUT+"] [nvarchar](5) NULL" + COMMA +
			" ["+FIELD_SAMEDI_PM_FIN+"] [nvarchar](5) NULL" + COMMA +

			" ["+FIELD_DIMANCHE_AM_DEBUT+"] [nvarchar](5) NULL" + COMMA +
			" ["+FIELD_DIMANCHE_AM_FIN+"] [nvarchar](5) NULL" + COMMA +
			" ["+FIELD_DIMANCHE_PM_DEBUT+"] [nvarchar](5) NULL" + COMMA +
			" ["+FIELD_DIMANCHE_PM_FIN+"] [nvarchar](5) NULL" +



//			" ["+FIELD_JOUR+"] [nvarchar](8) NULL" + COMMA +
//			" ["+FIELD_OUVERT+"] [nvarchar](1) NULL" + COMMA +
//			" ["+FIELD_AM_DEBUT+"] [nvarchar](255) NULL" + COMMA +
//			" ["+FIELD_AM_FIN+"] [nvarchar](255) NULL" + COMMA +
//			" ["+FIELD_PM_DEBUT+"] [nvarchar](255) NULL" + COMMA +
//			" ["+FIELD_PM_FIN+"] [nvarchar](255) NULL"+
")";

	static public class structHoraire
	{
		public String CODE = "";
		public String CODEVRP = "";


		public String LUNDI_AM_DEBUT = "";
		public String LUNDI_AM_FIN = "";
		public String LUNDI_PM_DEBUT = "";
		public String LUNDI_PM_FIN = "";

		public String MARDI_AM_DEBUT = "";
		public String MARDI_AM_FIN = "";
		public String MARDI_PM_DEBUT = "";
		public String MARDI_PM_FIN = "";

		public String MERCREDI_AM_DEBUT = "";
		public String MERCREDI_AM_FIN = "";
		public String MERCREDI_PM_DEBUT = "";
		public String MERCREDI_PM_FIN = "";

		public String JEUDI_AM_DEBUT = "";
		public String JEUDI_AM_FIN = "";
		public String JEUDI_PM_DEBUT = "";
		public String JEUDI_PM_FIN = "";

		public String VENDREDI_AM_DEBUT = "";
		public String VENDREDI_AM_FIN = "";
		public String VENDREDI_PM_DEBUT = "";
		public String VENDREDI_PM_FIN = "";

		public String SAMEDI_AM_DEBUT = "";
		public String SAMEDI_AM_FIN = "";
		public String SAMEDI_PM_DEBUT = "";
		public String SAMEDI_PM_FIN = "";

		public String DIMANCHE_AM_DEBUT = "";
		public String DIMANCHE_AM_FIN = "";
		public String DIMANCHE_PM_DEBUT = "";
		public String DIMANCHE_PM_FIN = "";
		//		public String FIELD_JOUR  = "";                  
		//		public String FIELD_OUVERT = "";  
		//		public String FIELD_AM_DEBUT = "";  
		//		public String FIELD_AM_FIN = "";  
		//		public String FIELD_PM_DEBUT = "";  
		//		public String FIELD_PM_FIN  = "";

		public String getClosingHour(Context context){
			String result = "-";
			GregorianCalendar calendar = new java.util.GregorianCalendar(); 
			calendar.setTime( new Date() );
			boolean am = true;
			if(calendar.get(Calendar.AM_PM) == Calendar.PM)
				am = false;

			result = getClosingHour(context, calendar.get(Calendar.DAY_OF_WEEK), am);
			Debug.Log("TAG3", "AAAAA : "+result);
			return result;
		}
		
		public String getClosingHour(Context context, int CalendarDAY, boolean am){
			String result="-";
			
			switch(CalendarDAY){
			case Calendar.MONDAY:
				if(am){
					String to = getFormatedHoraire(LUNDI_AM_FIN);
					if(!to.equals(""))
						result = to;
				}
				else{
					String to = getFormatedHoraire(LUNDI_PM_FIN);
					if(!to.equals(""))
						result = to;
				}
				break;

			case Calendar.TUESDAY:
				if(am){
					String to = getFormatedHoraire(MARDI_AM_FIN);
					if(!to.equals(""))
						result = to;
				}
				else{
					String to = getFormatedHoraire(MARDI_PM_FIN);
					if(!to.equals(""))
						result = to;
				}
				break;

			case Calendar.WEDNESDAY:
				if(am){
					String to = getFormatedHoraire(MERCREDI_AM_FIN);
					if(!to.equals(""))
						result = to;
				}
				else{
					String to = getFormatedHoraire(MERCREDI_PM_FIN);
					if(!to.equals(""))
						result = to;
				}
				break;

			case Calendar.THURSDAY:
				if(am){
					String to = getFormatedHoraire(JEUDI_AM_FIN);
					if(!to.equals(""))
						result = to;
				}
				else{
					String to = getFormatedHoraire(JEUDI_PM_FIN);
					if(!to.equals(""))
						result = to;
				}
				break;

			case Calendar.FRIDAY:
				if(am){
					String to = getFormatedHoraire(VENDREDI_AM_FIN);
					if(!to.equals(""))
						result = to;
				}
				else{
					String to = getFormatedHoraire(VENDREDI_PM_FIN);
					if(!to.equals(""))
						result = to;
				}
				break;

			case Calendar.SATURDAY:
				if(am){
					String to = getFormatedHoraire(SAMEDI_AM_FIN);
					if(!to.equals(""))
						result = to;
				}
				else{
					String to = getFormatedHoraire(SAMEDI_PM_FIN);
					if(!to.equals(""))
						result = to;
				}
				break;

			case Calendar.SUNDAY:
				if(am){
					String to = getFormatedHoraire(DIMANCHE_AM_FIN);
					if(!to.equals(""))
						result = to;
				}
				else{
					String to = getFormatedHoraire(DIMANCHE_PM_FIN);
					if(!to.equals(""))
						result = to;
				}
				break;



			}

			return result;
		}
		
		
		
		public String getOpeningHour(Context context){
			String result = "-";
			GregorianCalendar calendar = new java.util.GregorianCalendar(); 
			calendar.setTime( new Date() );
			boolean am = true;
			if(calendar.get(Calendar.AM_PM) == Calendar.PM)
				am = false;

			result = getOpeningHour(context, calendar.get(Calendar.DAY_OF_WEEK), am);
			Debug.Log("TAG3", "AAAAA : "+result);
			return result;
		}
		
		public String getOpeningHour(Context context, int CalendarDAY, boolean am){
			String result="-";
			
			switch(CalendarDAY){
			case Calendar.MONDAY:
				if(am){
					String to = getFormatedHoraire(LUNDI_AM_DEBUT);
					if(!to.equals(""))
						result = to;
				}
				else{
					String to = getFormatedHoraire(LUNDI_PM_DEBUT);
					if(!to.equals(""))
						result = to;
				}
				break;

			case Calendar.TUESDAY:
				if(am){
					String to = getFormatedHoraire(MARDI_AM_DEBUT);
					if(!to.equals(""))
						result = to;
				}
				else{
					String to = getFormatedHoraire(MARDI_PM_DEBUT);
					if(!to.equals(""))
						result = to;
				}
				break;

			case Calendar.WEDNESDAY:
				if(am){
					String to = getFormatedHoraire(MERCREDI_AM_DEBUT);
					if(!to.equals(""))
						result = to;
				}
				else{
					String to = getFormatedHoraire(MERCREDI_PM_DEBUT);
					if(!to.equals(""))
						result = to;
				}
				break;

			case Calendar.THURSDAY:
				if(am){
					String to = getFormatedHoraire(JEUDI_AM_DEBUT);
					if(!to.equals(""))
						result = to;
				}
				else{
					String to = getFormatedHoraire(JEUDI_PM_DEBUT);
					if(!to.equals(""))
						result = to;
				}
				break;

			case Calendar.FRIDAY:
				if(am){
					String to = getFormatedHoraire(VENDREDI_AM_DEBUT);
					if(!to.equals(""))
						result = to;
				}
				else{
					String to = getFormatedHoraire(VENDREDI_PM_DEBUT);
					if(!to.equals(""))
						result = to;
				}
				break;

			case Calendar.SATURDAY:
				if(am){
					String to = getFormatedHoraire(SAMEDI_AM_DEBUT);
					if(!to.equals(""))
						result = to;
				}
				else{
					String to = getFormatedHoraire(SAMEDI_PM_DEBUT);
					if(!to.equals(""))
						result = to;
				}
				break;

			case Calendar.SUNDAY:
				if(am){
					String to = getFormatedHoraire(DIMANCHE_AM_DEBUT);
					if(!to.equals(""))
						result = to;
				}
				else{
					String to = getFormatedHoraire(DIMANCHE_PM_DEBUT);
					if(!to.equals(""))
						result = to;
				}
				break;



			}

			return result;
		}
		
		
		
		public String getFormatedFullHoraire(Context context, int CalendarDAY, boolean am){
			String de = context.getString(R.string.de)+" ";
			String a  = " "+context.getString(R.string.a)+" ";
			String result="-";
			switch(CalendarDAY){
			case Calendar.MONDAY:
				if(am){
					String from = getFormatedHoraire(LUNDI_AM_DEBUT);
					String to = getFormatedHoraire(LUNDI_AM_FIN);
					if(!from.equals("") && !to.equals(""))
						result = de+from+a+to;
				}
				else{
					String from = getFormatedHoraire(LUNDI_PM_DEBUT);
					String to = getFormatedHoraire(LUNDI_PM_FIN);
					if(!from.equals("") && !to.equals(""))
						result = de+from+a+to;
				}
				break;

			case Calendar.TUESDAY:
				if(am){
					String from = getFormatedHoraire(MARDI_AM_DEBUT);
					String to = getFormatedHoraire(MARDI_AM_FIN);
					if(!from.equals("") && !to.equals(""))
						result = de+from+a+to;
				}
				else{
					String from = getFormatedHoraire(MARDI_PM_DEBUT);
					String to = getFormatedHoraire(MARDI_PM_FIN);
					if(!from.equals("") && !to.equals(""))
						result = de+from+a+to;
				}
				break;

			case Calendar.WEDNESDAY:
				if(am){
					String from = getFormatedHoraire(MERCREDI_AM_DEBUT);
					String to = getFormatedHoraire(MERCREDI_AM_FIN);
					if(!from.equals("") && !to.equals(""))
						result = de+from+a+to;
				}
				else{
					String from = getFormatedHoraire(MERCREDI_PM_DEBUT);
					String to = getFormatedHoraire(MERCREDI_PM_FIN);
					if(!from.equals("") && !to.equals(""))
						result = de+from+a+to;
				}
				break;

			case Calendar.THURSDAY:
				if(am){
					String from = getFormatedHoraire(JEUDI_AM_DEBUT);
					String to = getFormatedHoraire(JEUDI_AM_FIN);
					if(!from.equals("") && !to.equals(""))
						result = de+from+a+to;
				}
				else{
					String from = getFormatedHoraire(JEUDI_PM_DEBUT);
					String to = getFormatedHoraire(JEUDI_PM_FIN);
					if(!from.equals("") && !to.equals(""))
						result = de+from+a+to;
				}
				break;

			case Calendar.FRIDAY:
				if(am){
					String from = getFormatedHoraire(VENDREDI_AM_DEBUT);
					String to = getFormatedHoraire(VENDREDI_AM_FIN);
					if(!from.equals("") && !to.equals(""))
						result = de+from+a+to;
				}
				else{
					String from = getFormatedHoraire(VENDREDI_PM_DEBUT);
					String to = getFormatedHoraire(VENDREDI_PM_FIN);
					if(!from.equals("") && !to.equals(""))
						result = de+from+a+to;
				}
				break;

			case Calendar.SATURDAY:
				if(am){
					String from = getFormatedHoraire(SAMEDI_AM_DEBUT);
					String to = getFormatedHoraire(SAMEDI_AM_FIN);
					if(!from.equals("") && !to.equals(""))
						result = de+from+a+to;
				}
				else{
					String from = getFormatedHoraire(SAMEDI_PM_DEBUT);
					String to = getFormatedHoraire(SAMEDI_PM_FIN);
					if(!from.equals("") && !to.equals(""))
						result = de+from+a+to;
				}
				break;

			case Calendar.SUNDAY:
				if(am){
					String from = getFormatedHoraire(DIMANCHE_AM_DEBUT);
					String to = getFormatedHoraire(DIMANCHE_AM_FIN);
					if(!from.equals("") && !to.equals(""))
						result = de+from+a+to;
				}
				else{
					String from = getFormatedHoraire(DIMANCHE_PM_DEBUT);
					String to = getFormatedHoraire(DIMANCHE_PM_FIN);
					if(!from.equals("") && !to.equals(""))
						result = de+from+a+to;
				}
				break;



			}


			return result;
		}

		public String getFormatedHoraire(String horaire){
			String result = "";
			if(horaire != null && horaire.length() == 5 && !horaire.equals("00000")){
				result = horaire.substring(1, 3)+":"+horaire.substring(3, 5);
			}
			return result;
		}
	}
	public structHoraire load(Cursor cursor){
		structHoraire horaire = new structHoraire();
		if(cursor != null){
			horaire.CODE = giveFld(cursor, FIELD_CODE);
			horaire.CODEVRP = giveFld(cursor, FIELD_CODEVRP);

			horaire.LUNDI_AM_DEBUT = giveFld(cursor, FIELD_LUNDI_AM_DEBUT);
			horaire.LUNDI_AM_FIN = giveFld(cursor, FIELD_LUNDI_AM_FIN);
			horaire.LUNDI_PM_DEBUT = giveFld(cursor, FIELD_LUNDI_PM_DEBUT);
			horaire.LUNDI_PM_FIN = giveFld(cursor, FIELD_LUNDI_PM_FIN);

			horaire.MARDI_AM_DEBUT = giveFld(cursor, FIELD_MARDI_AM_DEBUT);
			horaire.MARDI_AM_FIN = giveFld(cursor, FIELD_MARDI_AM_FIN);
			horaire.MARDI_PM_DEBUT = giveFld(cursor, FIELD_MARDI_PM_DEBUT);
			horaire.MARDI_PM_FIN = giveFld(cursor, FIELD_MARDI_PM_FIN);

			horaire.MERCREDI_AM_DEBUT = giveFld(cursor, FIELD_MERCREDI_AM_DEBUT);
			horaire.MERCREDI_AM_FIN = giveFld(cursor, FIELD_MERCREDI_AM_FIN);
			horaire.MERCREDI_PM_DEBUT = giveFld(cursor, FIELD_MERCREDI_PM_DEBUT);
			horaire.MERCREDI_PM_FIN = giveFld(cursor, FIELD_MERCREDI_PM_FIN);

			horaire.JEUDI_AM_DEBUT = giveFld(cursor, FIELD_JEUDI_AM_DEBUT);
			horaire.JEUDI_AM_FIN = giveFld(cursor, FIELD_JEUDI_AM_FIN);
			horaire.JEUDI_PM_DEBUT = giveFld(cursor, FIELD_JEUDI_PM_DEBUT);
			horaire.JEUDI_PM_FIN = giveFld(cursor, FIELD_JEUDI_PM_FIN);

			horaire.VENDREDI_AM_DEBUT = giveFld(cursor, FIELD_VENDREDI_AM_DEBUT);
			horaire.VENDREDI_AM_FIN = giveFld(cursor, FIELD_VENDREDI_AM_FIN);
			horaire.VENDREDI_PM_DEBUT = giveFld(cursor, FIELD_VENDREDI_PM_DEBUT);
			horaire.VENDREDI_PM_FIN = giveFld(cursor, FIELD_VENDREDI_PM_FIN);

			horaire.SAMEDI_AM_DEBUT = giveFld(cursor, FIELD_SAMEDI_AM_DEBUT);
			horaire.SAMEDI_AM_FIN = giveFld(cursor, FIELD_SAMEDI_AM_FIN);
			horaire.SAMEDI_PM_DEBUT = giveFld(cursor, FIELD_SAMEDI_PM_DEBUT);
			horaire.SAMEDI_PM_FIN = giveFld(cursor, FIELD_SAMEDI_PM_FIN);

			horaire.DIMANCHE_AM_DEBUT = giveFld(cursor, FIELD_DIMANCHE_AM_DEBUT);
			horaire.DIMANCHE_AM_FIN = giveFld(cursor, FIELD_DIMANCHE_AM_FIN);
			horaire.DIMANCHE_PM_DEBUT = giveFld(cursor, FIELD_DIMANCHE_PM_DEBUT);
			horaire.DIMANCHE_PM_FIN = giveFld(cursor, FIELD_DIMANCHE_PM_FIN);
		}
		return horaire;
	}

	public structHoraire load(String code){
		structHoraire horaire = new structHoraire();
		String query = "SELECT *"
				+" FROM "+TABLENAME
				+" WHERE "+getFullFieldName(FIELD_CODE)+" = '"+code+"'";

		Cursor cursor =  db.conn.rawQuery(query, null);

		if(cursor != null){
			cursor.moveToFirst();
			horaire = load(cursor);
			cursor.close();
		}

		return horaire;
	}


	MyDB db;
	public TableHoraire(MyDB _db)
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

	/*
 Table horaire/jour : 
code client; Jour; Ouvert O/N/ heure debut matin;heure fin matin;heure debut apres midi;heure fin apres midi;
	 */
}
