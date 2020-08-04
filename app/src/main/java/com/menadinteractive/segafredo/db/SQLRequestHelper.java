package com.menadinteractive.segafredo.db;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.android.maps.GeoPoint;
import com.menadinteractive.maxpoilane.Debug;
import com.menadinteractive.segafredo.communs.Fonctions;
import com.menadinteractive.segafredo.communs.Global;

public class SQLRequestHelper {
	SQLiteDatabase conn;

	/** VIEW */
	String ENVACANCE_VIEW = "enVacanceView";

	public SQLRequestHelper(SQLiteDatabase conn){
		this.conn = conn;
	}

	public Cursor getClients(Date now, GeoPoint centerPosition, int radiusMeter, boolean isOpenedOnly, boolean toVisit,String codeSociete){
		GeoPoint[] extremeGeoPoints = getExtremeGeopoint(centerPosition, radiusMeter);
		GeoPoint minGeopoint = extremeGeoPoints[0];
		GeoPoint maxGeopoint = extremeGeoPoints[1];

		/*  It works
		String query = "SELECT *"
				+" FROM "+TableClient.TABLENAME
				+" WHERE "+TableClient.getFullFieldName(TableClient.FIELD_LAT)+" BETWEEN '"+minGeopoint.getLatitudeE6()+"' AND '"+maxGeopoint.getLatitudeE6()+"'"
				+" AND "+TableClient.getFullFieldName(TableClient.FIELD_LON)+" BETWEEN '"+minGeopoint.getLongitudeE6()+"' AND '"+maxGeopoint.getLongitudeE6()+"'";
		 */

		String query = "SELECT *"
				+" FROM "+TableClient.TABLENAME
				+" WHERE 1=1";

		if(isOpenedOnly){
			createEnVacanceView(now);
			query = "SELECT *"
					+" FROM "+TableClient.TABLENAME
					+" LEFT JOIN "+TableHoraire.TABLENAME
					+" ON "+TableHoraire.getFullFieldName(TableHoraire.FIELD_CODE)+" = "+TableClient.getFullFieldName(TableClient.FIELD_CODE)
					+" LEFT JOIN "+ ENVACANCE_VIEW
					+" ON "+TableClient.getFullFieldName(TableClient.FIELD_CODE)+" = "+ENVACANCE_VIEW+"."+TableFermeture.FIELD_CODE_ALIAS
					+" AND "+ENVACANCE_VIEW+"."+TableFermeture.FIELD_CODE_ALIAS+" IS NULL"
					+" AND "+getConditionOnHoraire(now, "1=1");
			

			//			if(isOpenedOnly)
			//				query+=" AND "+getConditionOnHoraire(now);	
		}
		else{


			query = "SELECT *"
					+" FROM "+TableClient.TABLENAME
					+" LEFT JOIN "+TableHoraire.TABLENAME
					+" ON "+TableHoraire.getFullFieldName(TableHoraire.FIELD_CODE)+" = "+TableClient.getFullFieldName(TableClient.FIELD_CODE);
					
		}
		
		query+=" WHERE 1=1 ";
		
		if(toVisit)
			query+=" AND "+TableClient.getFullFieldName(TableClient.FIELD_TOVISIT)+"='1'";
		
		query+=" AND CAST("+TableClient.getFullFieldName(TableClient.FIELD_LAT)+" as Integer) BETWEEN "+minGeopoint.getLatitudeE6()+" AND "+maxGeopoint.getLatitudeE6()
					+" AND CAST("+TableClient.getFullFieldName(TableClient.FIELD_LON)+" as Integer) BETWEEN "+minGeopoint.getLongitudeE6()+" AND "+maxGeopoint.getLongitudeE6();
	 
		if(codeSociete!= null && !codeSociete.equals(""))
			query+=" AND "+TableClient.getFullFieldName(TableClient.FIELD_SOC_CODE)+" = '"+codeSociete+"'";

		//		getConditionOnFermeture(now);

		Debug.Log("requete2", query);
		//		if(!Fonctions.isEmptyOrNull(limit)){
		//			query +=" LIMIT "+limit;
		//		}
		return this.conn.rawQuery(query, null);


	}

	private void createEnVacanceView(Date now){
		GregorianCalendar calendar = new java.util.GregorianCalendar(); 
		calendar.setTime( now ); 

		//		Debug.Log("DAY_OF_MONTH : "+calendar.get(Calendar.DAY_OF_MONTH));
		//		Debug.Log("MONTH : "+calendar.get(Calendar.MONTH));

		String MMjj = Fonctions.pad(calendar.get(Calendar.MONTH)+1)+Fonctions.pad(calendar.get(Calendar.DAY_OF_MONTH));
		String currentMMjj = "0"+MMjj;
		String nextYearMMjj = "1"+MMjj;

		String query = "SELECT distinct "+TableFermeture.getFullFieldName(TableFermeture.FIELD_CODE)+" as "+TableFermeture.FIELD_CODE_ALIAS
				+" FROM "+TableFermeture.TABLENAME
				+" WHERE '"+currentMMjj+"' BETWEEN "+TableFermeture.getFullFieldName(TableFermeture.FIELD_DU)+" AND "+TableFermeture.getFullFieldName(TableFermeture.FIELD_AU)
				+" OR '"+nextYearMMjj+"' BETWEEN "+TableFermeture.getFullFieldName(TableFermeture.FIELD_DU)+" AND "+TableFermeture.getFullFieldName(TableFermeture.FIELD_AU);

		String dropViewQuery =  "DROP VIEW IF EXISTS "+ENVACANCE_VIEW;
		String createViewQuery = "CREATE VIEW "+ENVACANCE_VIEW+" AS "+query;
		conn.execSQL(dropViewQuery);
		conn.execSQL(createViewQuery);
		Debug.Log(createViewQuery);
	}

	/*
	public String getConditionOnFermeture(Date now){
		String result = "1=1";
		try{
			GregorianCalendar calendar = new java.util.GregorianCalendar(); 
			calendar.setTime( now ); 

			Debug.Log("DAY_OF_MONTH : "+calendar.get(Calendar.DAY_OF_MONTH));
			Debug.Log("MONTH : "+calendar.get(Calendar.MONTH));

			String MMjj = Fonctions.pad(calendar.get(Calendar.MONTH)+1)+Fonctions.pad(calendar.get(Calendar.DAY_OF_MONTH));
			String currentMMjj = "0"+MMjj;
			String nextYearMMjj = "1"+MMjj;

		}
		catch(Exception e){
			Debug.StackTrace(e);
			result = "1=1";
		}
		return result;
	}
	*/

	public String getConditionOnHoraire(Date now, String defaultCondition){
		String result = defaultCondition;
		try{
			/** L'objet Calendrier va nous permettre de récupérer le jour de la semaine 0=SUNDAY, 1=MONDAY...*/
			//		Calendar nowCalendar = Calendar.getInstance();
			GregorianCalendar calendar = new java.util.GregorianCalendar(); 
			calendar.setTime( now ); 
			Debug.Log("Day of week : "+calendar.get(Calendar.DAY_OF_WEEK));

			/** Récupération de l'heure 
			 * http://www.tutorialspoint.com/java/java_date_time.htm*/
			String timeHHmm = new SimpleDateFormat("HHmm").format(now);
			String dDayHHmm = "0"+timeHHmm;
			String yesterdayHHmm = "1"+timeHHmm;


			switch(calendar.get(Calendar.DAY_OF_WEEK)){
			case Calendar.MONDAY:
				result = TableHoraire.getFullFieldName(TableHoraire.FIELD_LUNDI_AM_FIN)+" <> '00000'"
						+" AND ("
						+" ("+ TableHoraire.getFullFieldName(TableHoraire.FIELD_LUNDI_AM_DEBUT)+"<='"+dDayHHmm+"'"
						+" AND "+ TableHoraire.getFullFieldName(TableHoraire.FIELD_LUNDI_AM_FIN)+">= '"+dDayHHmm+"')"
						+" OR ("+ TableHoraire.getFullFieldName(TableHoraire.FIELD_LUNDI_PM_DEBUT)+"<= '"+dDayHHmm+"'"
						+" AND "+ TableHoraire.getFullFieldName(TableHoraire.FIELD_LUNDI_PM_FIN)+">= '"+dDayHHmm+"')"
						+" OR ("+ TableHoraire.getFullFieldName(TableHoraire.FIELD_DIMANCHE_PM_FIN)+">= '"+yesterdayHHmm+"')"
						+" OR ("+TableHoraire.getFullFieldName(TableHoraire.FIELD_LUNDI_AM_FIN)+" is null )"
						+" )";
				break;
			case Calendar.TUESDAY:
				result = TableHoraire.getFullFieldName(TableHoraire.FIELD_MARDI_AM_FIN)+" <> '00000'"
						+" AND ("
						+" ("+ TableHoraire.getFullFieldName(TableHoraire.FIELD_MARDI_AM_DEBUT)+"<='"+dDayHHmm+"'"
						+" AND "+ TableHoraire.getFullFieldName(TableHoraire.FIELD_MARDI_AM_FIN)+">= '"+dDayHHmm+"')"
						+" OR ("+ TableHoraire.getFullFieldName(TableHoraire.FIELD_MARDI_PM_DEBUT)+"<= '"+dDayHHmm+"'"
						+" AND "+ TableHoraire.getFullFieldName(TableHoraire.FIELD_MARDI_PM_FIN)+">= '"+dDayHHmm+"')"
						+" OR ("+ TableHoraire.getFullFieldName(TableHoraire.FIELD_LUNDI_PM_FIN)+">= '"+yesterdayHHmm+"')"
						+" OR ("+TableHoraire.getFullFieldName(TableHoraire.FIELD_MARDI_AM_FIN)+" is null )"
						+" )";
				break;

			case Calendar.WEDNESDAY:
				result = TableHoraire.getFullFieldName(TableHoraire.FIELD_MERCREDI_AM_FIN)+" <> '00000'"
						+" AND ("
						+" ("+ TableHoraire.getFullFieldName(TableHoraire.FIELD_MERCREDI_AM_DEBUT)+"<='"+dDayHHmm+"'"
						+" AND "+ TableHoraire.getFullFieldName(TableHoraire.FIELD_MERCREDI_AM_FIN)+">= '"+dDayHHmm+"')"
						+" OR ("+ TableHoraire.getFullFieldName(TableHoraire.FIELD_MERCREDI_PM_DEBUT)+"<= '"+dDayHHmm+"'"
						+" AND "+ TableHoraire.getFullFieldName(TableHoraire.FIELD_MERCREDI_PM_FIN)+">= '"+dDayHHmm+"')"
						+" OR ("+ TableHoraire.getFullFieldName(TableHoraire.FIELD_MARDI_PM_FIN)+">= '"+yesterdayHHmm+"')"
						+" OR ("+TableHoraire.getFullFieldName(TableHoraire.FIELD_MERCREDI_AM_FIN)+" is null )"
						+" )";
				break;
			case Calendar.THURSDAY:
				result = TableHoraire.getFullFieldName(TableHoraire.FIELD_JEUDI_AM_FIN)+" <> '00000'"
						+" AND ("
						+" ("+ TableHoraire.getFullFieldName(TableHoraire.FIELD_JEUDI_AM_DEBUT)+"<='"+dDayHHmm+"'"
						+" AND "+ TableHoraire.getFullFieldName(TableHoraire.FIELD_JEUDI_AM_FIN)+">= '"+dDayHHmm+"')"
						+" OR ("+ TableHoraire.getFullFieldName(TableHoraire.FIELD_JEUDI_PM_DEBUT)+"<= '"+dDayHHmm+"'"
						+" AND "+ TableHoraire.getFullFieldName(TableHoraire.FIELD_JEUDI_PM_FIN)+">= '"+dDayHHmm+"')"
						+" OR ("+ TableHoraire.getFullFieldName(TableHoraire.FIELD_MERCREDI_PM_FIN)+">= '"+yesterdayHHmm+"')"
						+" OR ("+TableHoraire.getFullFieldName(TableHoraire.FIELD_JEUDI_AM_FIN)+" is null )"
						+" )";
				break;
			case Calendar.FRIDAY:
				result = TableHoraire.getFullFieldName(TableHoraire.FIELD_VENDREDI_AM_FIN)+" <> '00000'"
						+" AND ("
						+" ("+ TableHoraire.getFullFieldName(TableHoraire.FIELD_VENDREDI_AM_DEBUT)+"<='"+dDayHHmm+"'"
						+" AND "+ TableHoraire.getFullFieldName(TableHoraire.FIELD_VENDREDI_AM_FIN)+">= '"+dDayHHmm+"')"
						+" OR ("+ TableHoraire.getFullFieldName(TableHoraire.FIELD_VENDREDI_PM_DEBUT)+"<= '"+dDayHHmm+"'"
						+" AND "+ TableHoraire.getFullFieldName(TableHoraire.FIELD_VENDREDI_PM_FIN)+">= '"+dDayHHmm+"')"
						+" OR ("+ TableHoraire.getFullFieldName(TableHoraire.FIELD_JEUDI_PM_FIN)+">= '"+yesterdayHHmm+"')"
						+" OR ("+TableHoraire.getFullFieldName(TableHoraire.FIELD_VENDREDI_AM_FIN)+" is null )"
						+" )";
				break;
			case Calendar.SATURDAY:
				result = TableHoraire.getFullFieldName(TableHoraire.FIELD_SAMEDI_AM_FIN)+" <> '00000'"
						+" AND ("
						+" ("+ TableHoraire.getFullFieldName(TableHoraire.FIELD_SAMEDI_AM_DEBUT)+"<='"+dDayHHmm+"'"
						+" AND "+ TableHoraire.getFullFieldName(TableHoraire.FIELD_SAMEDI_AM_FIN)+">= '"+dDayHHmm+"')"
						+" OR ("+ TableHoraire.getFullFieldName(TableHoraire.FIELD_SAMEDI_PM_DEBUT)+"<= '"+dDayHHmm+"'"
						+" AND "+ TableHoraire.getFullFieldName(TableHoraire.FIELD_SAMEDI_PM_FIN)+">= '"+dDayHHmm+"')"
						+" OR ("+ TableHoraire.getFullFieldName(TableHoraire.FIELD_VENDREDI_PM_FIN)+">= '"+yesterdayHHmm+"')"
						+" OR ("+TableHoraire.getFullFieldName(TableHoraire.FIELD_SAMEDI_AM_FIN)+" is null )"
						+" )";
				break;
			case Calendar.SUNDAY:
				result = TableHoraire.getFullFieldName(TableHoraire.FIELD_DIMANCHE_AM_FIN)+" <> '00000'"
						+" AND ("
						+" ("+ TableHoraire.getFullFieldName(TableHoraire.FIELD_DIMANCHE_AM_DEBUT)+"<='"+dDayHHmm+"'"
						+" AND "+ TableHoraire.getFullFieldName(TableHoraire.FIELD_DIMANCHE_AM_FIN)+">= '"+dDayHHmm+"')"
						+" OR ("+ TableHoraire.getFullFieldName(TableHoraire.FIELD_DIMANCHE_PM_DEBUT)+"<= '"+dDayHHmm+"'"
						+" AND "+ TableHoraire.getFullFieldName(TableHoraire.FIELD_DIMANCHE_PM_FIN)+">= '"+dDayHHmm+"')"
						+" OR ("+ TableHoraire.getFullFieldName(TableHoraire.FIELD_SAMEDI_PM_FIN)+">= '"+yesterdayHHmm+"')"
						+" OR ("+TableHoraire.getFullFieldName(TableHoraire.FIELD_DIMANCHE_AM_FIN)+" is null )"
						+" )";
				break;




			default:
				break;
			}
		}
		catch(Exception e){
			Debug.StackTrace(e);
			result = defaultCondition;
		}
		return result;
	}


	/** Radius */
	private double getRadiusLatitudeInDegree(GeoPoint centerPosition, int radiusMeter){
		//	1° latitude = 111120m
		//  1° longitude = 111120m*cos(latitude)
		return (double)radiusMeter/111120;
	}

	private double getRadiusLongitudeInDegree(GeoPoint centerPosition, int radiusMeter){
		//	1° latitude = 111120m
		//  1° longitude = 111120m*cos(latitude)
		return (radiusMeter/Math.cos(Math.toRadians((double)(Double.valueOf(centerPosition.getLatitudeE6())/1E6))))/(111120);
	}


	/**
	 * This function returns an array of 2 GeoPoints which are the extreme GeoPoint position for a given radius and center Position on Earth
	 * The first GeoPoint contains the min values
	 * The second GeoPoint contains the max values
	 * @param centerPosition
	 * @param radiusMeter
	 * @return
	 */
	private GeoPoint[] getExtremeGeopoint(GeoPoint centerPosition, int radiusMeter){
		GeoPoint[] result = new GeoPoint[2];

		double latitudeDelta = getRadiusLatitudeInDegree(centerPosition, radiusMeter);
		double longitudeDelta = getRadiusLongitudeInDegree(centerPosition, radiusMeter);


		Double latitudeMin = (double)(Double.valueOf(centerPosition.getLatitudeE6())/1E6) - latitudeDelta; 
		Double latitudeMax = (double)(Double.valueOf(centerPosition.getLatitudeE6())/1E6) + latitudeDelta; 
		Double longitudeMin = (double)(Double.valueOf(centerPosition.getLongitudeE6())/1E6) - longitudeDelta; 
		Double longitudeMax = (double)(Double.valueOf(centerPosition.getLongitudeE6())/1E6) + longitudeDelta; 


		int latitudeMinE6 = (int)(latitudeMin*1E6); 
		int latitudeMaxE6 = (int)(latitudeMax*1E6); 
		int longitudeMinE6 = (int)(longitudeMin*1E6); 
		int longitudeMaxE6 = (int)(longitudeMax*1E6); 

		GeoPoint minGeopoint = new GeoPoint(latitudeMinE6, longitudeMinE6);
		GeoPoint maxGeopoint = new GeoPoint(latitudeMaxE6, longitudeMaxE6);

		result[0] = minGeopoint;
		result[1] = maxGeopoint;
		return result;
	}
	
	public static String getFrenchDay(Date now){
		String result = "";
		GregorianCalendar calendar = new java.util.GregorianCalendar(); 
		calendar.setTime( now );
		switch(calendar.get(Calendar.DAY_OF_WEEK)){
		case Calendar.MONDAY:
			result = "LUNDI";
			break;
		case Calendar.TUESDAY:
			result = "MARDI";
			break;
		case Calendar.WEDNESDAY:
			result = "MERCREDI";
			break;
		case Calendar.THURSDAY:
			result = "JEUDI";
			break;
		case Calendar.FRIDAY:
			result = "VENDREDI";
			break;
		case Calendar.SATURDAY:
			result = "SAMEDI";
			break;
		case Calendar.SUNDAY:
			result = "DIMANCHE";
			break;
		}
		
		return result;
	}
	public Cursor getClientsTournee(String jourpassage, boolean toVisit, String zone){
	

		String query = "SELECT *"
				+" FROM "+TableClient.TABLENAME
				+" WHERE "+TableClient.getFullFieldName(TableClient.FIELD_JOURPASSAGE)+"='"+jourpassage+"'"
				+" AND "+TableClient.getFullFieldName(TableClient.FIELD_ZONE)+"='"+zone+"'";
		
		if(toVisit)
			query+=" AND "+TableClient.getFullFieldName(TableClient.FIELD_TOVISIT)+"='1'";

		Debug.Log("requete2",query);
		
		return this.conn.rawQuery(query, null);

	}
	
	public boolean isClientOpen(String codeClient, boolean checkHoraire, boolean checkFermeture){
		boolean result = false;
		boolean checkHoraireResult = true;
		boolean checkFermetureResult = true;
		
//		Date now = new Date(1352152870000L);  //Mon Nov 5 22:01:10 2012 GMT
		Date now = new Date();
		
		
		if(checkHoraire)
			checkHoraireResult = isClientOpenedHoraire(codeClient, now);
		
		if(checkFermeture)
			checkFermetureResult = isClientOpenedFermeture(codeClient, now);
		
		if(checkHoraireResult && checkFermetureResult)
			result = true;
		
		
		
		return result;
	}
	
	public boolean isClientOpenedFermeture(String codeClient){

		return isClientOpenedFermeture(codeClient, new Date());
	}
	
	private boolean isClientOpenedFermeture(String codeClient,Date now){
			boolean result = true;
			GregorianCalendar calendar = new java.util.GregorianCalendar(); 
			calendar.setTime( now ); 


			String MMjj = Fonctions.pad(calendar.get(Calendar.MONTH)+1)+Fonctions.pad(calendar.get(Calendar.DAY_OF_MONTH));
			String currentMMjj = "0"+MMjj;
			String nextYearMMjj = "1"+MMjj;

			String query = "SELECT distinct "+TableFermeture.getFullFieldName(TableFermeture.FIELD_CODE)+" as "+TableFermeture.FIELD_CODE_ALIAS
					+" FROM "+TableFermeture.TABLENAME
					+" WHERE "+TableFermeture.FIELD_CODE_ALIAS+"='"+codeClient+"'"
					+ "AND ('"+currentMMjj+"' BETWEEN "+TableFermeture.getFullFieldName(TableFermeture.FIELD_DU)+" AND "+TableFermeture.getFullFieldName(TableFermeture.FIELD_AU)
					+" OR '"+nextYearMMjj+"' BETWEEN "+TableFermeture.getFullFieldName(TableFermeture.FIELD_DU)+" AND "+TableFermeture.getFullFieldName(TableFermeture.FIELD_AU)+")";

			
			
			
			Cursor cursor = this.conn.rawQuery(query, null);
			if(cursor != null && cursor.moveToFirst() && cursor.getCount()>0){
				result = false;
			}
			
			if(cursor !=  null)
				cursor.close();
			
//			Debug.Log("TAG3", query);
//			Debug.Log("TAG3", result+"");
			return result;
	}
	
	private boolean isClientOpenedHoraire(String codeClient,Date now){
		boolean result = false;
		

		String query = "SELECT * "
				+" FROM "+TableHoraire.TABLENAME
				+" WHERE "+TableHoraire.FIELD_CODE+"='"+codeClient+"'"
				+" AND "+getConditionOnHoraire(now, "1=1");

		
		Cursor cursor = this.conn.rawQuery(query, null);
		if(cursor != null && cursor.moveToFirst() && cursor.getCount()>0){
			result = true;
		}
		
		if(cursor !=  null)
			cursor.close();
	
//		Debug.Log("TAG3", query);
//		Debug.Log("TAG3", result+"");
		
		return result;
}
	
	
	public void updateClientsTournee(String jourpassage){
		
		String query = "UPDATE "+TableClient.TABLENAME
				+" SET "+TableClient.FIELD_JOURPASSAGE+"='"+jourpassage+"'";
				
		this.conn.execSQL(query);
	}
	
	
	
	

	
		
		


	public static String getCodeTournee(String yyyymmdd)
	{
		try
		{
			String PairImpair="0";
			String dayOfWeek="";
			SimpleDateFormat sdf;
			Calendar cal;
			java.util.Date date;
			int week;
			// String sample = "20121123";
			//  sdf = new SimpleDateFormat("MM/dd/yyyy");
			sdf = new SimpleDateFormat("yyyyMMdd");
			date = sdf.parse(yyyymmdd);
			cal = Calendar.getInstance();
			cal.setTime(date);
			week = cal.get(Calendar.WEEK_OF_YEAR);
			int day=cal.get(Calendar.DAY_OF_WEEK);
			day=convertDayAndroidToDayMS(day);
			dayOfWeek=String.valueOf(day);
			if (week%2==0) PairImpair="0";
			else PairImpair="1";
			//return dayOfWeek+PairImpair;
			return dayOfWeek;
		}
		catch(Exception ex)
		{
			Debug.Log( ex.getLocalizedMessage());
		}
		return "00";
	}


	//android Dim=1, MS dim = 0;
	private static int convertDayAndroidToDayMS(int androDay)
	{
		androDay-=1;
		return androDay;
	}

}
