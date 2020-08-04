package com.menadinteractive.segafredo.communs;

import hirondelle.date4j.DateTime;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.menadinteractive.maxpoilane.ExternalStorage;
import com.menadinteractive.maxpoilane.R;
import com.menadinteractive.segafredo.db.MyDB;
import com.menadinteractive.segafredo.db.SQLRequestHelper;

import java.math.BigDecimal;

public class Fonctions {

	//transform une string sans s�parateur abcde0123456 en AB:CD:E0:12:34:56
	static public  String   StringToMAC(String s)
	{
		if (s.length()!=12) return "";
		
		s=s.toUpperCase();
		String s1=Fonctions.Mid(s, 0, 2);
		String s2=Fonctions.Mid(s, 2, 2);
		String s3=Fonctions.Mid(s, 4, 2);
		String s4=Fonctions.Mid(s, 6, 2);
		String s5=Fonctions.Mid(s, 8, 2);
		String s6=Fonctions.Mid(s, 10, 2);
		
		s=s1+":"+s2+":"+s3+":"+s4+":"+s5+":"+s6;
		
		return s;
	
	}
	public static boolean isNumeric(String str)
	{
	  return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
	}
	static public String getYYYYMMDD_MOINS(int ndelay)
	{
		final Calendar c = Calendar.getInstance( ); 

		int mYear = c.get(Calendar.YEAR); 
		int mMonth = c.get(Calendar.MONTH); 
		int mDay = c.get(Calendar.DAY_OF_MONTH);


		// entrée de la nouvelle date (dans 30 jours)
		c.set(mYear, mMonth, mDay -ndelay);

		// obtention des éléments de cette nlle date

		mYear = c.get(Calendar.YEAR); 
		mMonth = c.get(Calendar.MONTH)+1; 
		mDay = c.get(Calendar.DAY_OF_MONTH);


		String Date=String.format("%04d%02d%02d", mYear,mMonth,mDay);

		return Date;



	}
	static public String replaceSpecialsChars(String val)
	{
		val=val.replace("É", "E");
		val=val.replace("È", "E");
		val=val.replace("Ù", "U");
		val=val.replace("Ô", "O");
		val=val.replace("Û", "U");
		val=val.replace("Æ", "AE");
		val=val.replace("Â", "A");
		val=val.replace("Á", "A");
		
		return val;
	}
	
	static public String getBundleValue(Bundle bu, String key)
	{
		String val="";
		try
		{
			val = bu.getString(key);
			if (val==null) val="";
			return val;
		}
		catch(Exception ex)
		{
			val="";	
		}
		return val;
	}
	static public String gethhmmssEx(int increment)
	{
		int val=convertToInt(gethhmmss())+increment;

		return getInToStringDanem(val);

	}
	static public void setFont(Context c, int id,String fontname)
	{
		 TextView tv=(TextView)((Activity)c).findViewById(id);
		 setFont(c,tv,fontname);
	}
	static public void setFont(Context c, TextView tv,String fontname)
	{
		Typeface face=Typeface.createFromAsset(c.getAssets(), fontname);
		 tv.setTypeface(face);
	}
	static public long convertToLong(String val)
	{
		try
		{
			if(val==null)
			{
				val="0";
			}
			if(val.equals(""))
			{
				val="0";
			}
			val=val.replace(",", ".");

			return Long.valueOf(val);
		}
		catch(Exception ex)
		{

		}
		return 0;
	}	
	/*
	 * convertie une chaine hexa color #FFOOOO en int
	 */
	public static int converColorStringToInt(String dieseString)
	{
		try
		{
			int c=Color.parseColor(dieseString);
			return c;
		}
		catch(Exception ex)
		{
			return 0;
		}
	}

	/**
	 * donne une longueur max � un edittext
	 * @param et
	 * @param maxlenth
	 * @return
	 */
	public static boolean setMaxLenth(EditText et, int maxlenth)
	{
		try
		{
			InputFilter[] FilterArray = new InputFilter[1];
			FilterArray[0] = new InputFilter.LengthFilter(maxlenth);
			et.setFilters(FilterArray);

			return true;
		}
		catch(Exception ex)
		{

		}
		return false;
	}
	public static boolean createDirectory(String dir)
	{

		try {
			String PATH = Environment.getExternalStorageDirectory() +"/"+dir;
			PATH=PATH.replace("//", "/");
			File file = new File(PATH);
			return file.mkdirs();
		} catch (Exception e) {

			e.printStackTrace();
		}
		return false;
	}
	public static String readTextFromResource(Context c, int resourceID)
	{
		InputStream raw = c.getResources().openRawResource(resourceID);
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		int i;
		try
		{
			i = raw.read();
			while (i != -1)
			{
				stream.write(i);
				i = raw.read();
			}
			raw.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return stream.toString();
	}

	/**
	 * @author marcvouaux
	 * charge une image dans une imageview
	 * @param imageView
	 * @param path
	 * @param fileName
	 * @return
	 */
	public static boolean fileToImageView( ImageView imageView, String path,String fileName){
		File jpgFile = new File(path+"/"+fileName);
		Bitmap thumbnail ;

		if(jpgFile.exists()){
			Log.d("TAG", "file exist "+fileName);
			try {
				thumbnail = BitmapFactory.decodeStream(new FileInputStream(jpgFile), null, null);

				imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
				imageView.setImageBitmap(thumbnail);

				return true;
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.d("TAG", "error decoding"+e.toString());
			}
		}

		return false;

	}
	public static int getscrOrientation(Activity c) {
		Display getOrient = c.getWindowManager().getDefaultDisplay();

		int orientation = getOrient.getOrientation();

		// Sometimes you may get undefined orientation Value is 0
		// simple logic solves the problem compare the screen
		// X,Y Co-ordinates and determine the Orientation in such cases
		if (orientation == Configuration.ORIENTATION_UNDEFINED) {

			Configuration config = c.getResources().getConfiguration();
			orientation = config.orientation;

			if (orientation == Configuration.ORIENTATION_UNDEFINED) {
				// if height and widht of screen are equal then
				// it is square orientation
				if (getOrient.getWidth() == getOrient.getHeight()) {
					orientation = Configuration.ORIENTATION_SQUARE;
				} else { // if widht is less than height than it is portrait
					if (getOrient.getWidth() < getOrient.getHeight()) {
						orientation = Configuration.ORIENTATION_PORTRAIT;
					} else { // if it is not any of the above it will defineitly
						// be landscape
						orientation = Configuration.ORIENTATION_LANDSCAPE;
					}
				}
			}
		}
		return orientation; // return value 1 is portrait and 2 is Landscape
		// Mode
	}
	/**
	 * Transforme un dateicker en monthpicker
	 * @author Marc VOUAUX
	 * @param datePicker
	 */
	static public void setMonthPicker(DatePicker datePicker)
	{
		try {
			Field f[] = datePicker.getClass().getDeclaredFields();
			for (Field field : f) {
				if (field.getName().equals("mDayPicker")) {
					field.setAccessible(true);
					Object dayPicker = new Object();
					dayPicker = field.get(datePicker);
					((View) dayPicker).setVisibility(View.GONE);
				}
			}
		} catch (SecurityException e) {

		} 
		catch (IllegalArgumentException e) {

		} catch (IllegalAccessException e) {

		}
	}
	/**
	 * Transforme un dateicker en yearpicker
	 * @author Marc VOUAUX
	 * @param datePicker
	 */
	static public void setYearPicker(DatePicker datePicker)
	{
		try {
			Field f[] = datePicker.getClass().getDeclaredFields();
			for (Field field : f) {
				String fldname=field.getName();
				if (field.getName().equals("mDayPicker") || field.getName().equals("mMonthPicker")
						|| field.getName().equals("mMonthSpinner")
						|| field.getName().equals("mDaySpinner")) {
					field.setAccessible(true);
					Object dayPicker = new Object();
					dayPicker = field.get(datePicker);
					((View) dayPicker).setVisibility(View.GONE);
				}
			}
		} catch (SecurityException e) {

		} 
		catch (IllegalArgumentException e) {

		} catch (IllegalAccessException e) {

		}
	}
	static private String[] splitted1={"0"};
	static public String GiveFld(String chainesep, int fld,String sep,boolean reset)
	{

		try
		{
			if (reset)
				splitted1=chainesep.split("\\"+sep);


			String test;
			test=splitted1[fld].toString();


			return splitted1[fld].toString();
		}
		catch(Exception ex)
		{
			String err=ex.getMessage();
			return "";
		}



	}
	static public String GiveFld2(String chainesep, String sep,boolean reset)
	{

		try
		{
			if (reset)
				splitted1=chainesep.split("\\"+sep);

			return splitted1.toString();
		}
		catch(Exception ex)
		{
			String err=ex.getMessage();
			return "";
		}



	}
	static public float convertToFloat(String val)
	{
		try
		{
			if(val==null)
			{
				val="0";
			}
			if(val.equals(""))
			{
				val="0";
			}
			val=val.replace(",", ".");
			BigDecimal fval=    new    BigDecimal(String.valueOf(val));
			return (float)Fonctions.round(fval.floatValue(),2);
		}
		catch(Exception ex)
		{

		}
		return 0;
	}
	static public int convertToInt(String val)
	{
		try
		{
			if(val==null)
			{
				val="0";
			}
			if(val.equals(""))
			{
				val="0";
			}
			val=val.replace(",", ".");

			return Integer.parseInt(val);
		}
		catch(Exception ex)
		{

		}
		return 0;
	}	
	static public boolean convertToBool(String val)
	{
		try
		{
			if(val==null)
			{
				val="0";
			}
			if(val.equals(""))
			{
				val="0";
			}
			val=val.replace(",", ".");

			return Boolean.parseBoolean(val);
		}
		catch(Exception ex)
		{

		}
		return false;
	}
	/**
	 * @author Marc VOUAUX
	 * @param tab
	 * @param idx
	 * permet de retourner sans erreur un item d'un tableau
	 * @return
	 */
	public static String getStringArrayValue(String []tab,int idx)
	{
		try
		{
			return tab[idx];	
		}
		catch(Exception ex)
		{
			return "";
		}

	}
	static public void FurtiveMessageBox(Context c,String message){
		Toast.makeText(c,message,Toast.LENGTH_SHORT).show();
	}
	
	public static void makeSpecialToast(Context context, int idText, int duration, int position){
		Toast toast = Toast.makeText(context, context.getString(idText), duration);
		toast.setGravity(position, 0, 0);
		toast.show();
	}

	/**
	 * Show message by alert
	 * @param message
	 * @param c current context
	 */
	static public void _showAlert(final String message, final Context c) {
		AlertDialog.Builder builder = new AlertDialog.Builder(c);
		builder.setMessage(message);
		builder.setPositiveButton(android.R.string.ok,
				new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// do nothing
			}
		});
		AlertDialog dialog = builder.create();
		dialog.show();
	}


	/**
	 * Show context on UiThread
	 * @param message
	 * @param current context
	 */
	static public void FurtiveMessageBox(String message, final Context c){
		Toast.makeText(c,message,Toast.LENGTH_LONG).show();
	}

	/**
	 * retourne le type d'ecran
	 * http://developer.android.com/guide/practices/screens_support.html
	 */
	static public int MEDIUMDPI_LARGE=0;
	static public int HIGHDPI_NORMAL=1;
	static public int MEDIUMDPI_XLARGE=2;

	public static int getResol(Activity c)
	{
		DisplayMetrics dm=new DisplayMetrics();
		Display display = c.getWindowManager().getDefaultDisplay(); 
		c.getWindowManager().getDefaultDisplay().getMetrics(dm);

		int so=Fonctions.getscrOrientation(c);
		//if (so!=Configuration.ORIENTATION_LANDSCAPE)
		//if (dm.)

		//Configuration.SCREENLAYOUT_SIZE_XLARGE;
		//xlarge screens are at least 960dp x 720dp
		if (dm.widthPixels>=960 && dm.heightPixels>=720)
			return Configuration.SCREENLAYOUT_SIZE_XLARGE;

		return 0;
		/*
	    if (dm.density==1 && 
	    		( (dm.heightPixels==752 || dm.heightPixels==768) && (dm.widthPixels==1280 || dm.widthPixels==1024 )) 
	    		)
	    	return MEDIUMDPI_XLARGE;

	   if (dm.density==1 && 
			   ((dm.heightPixels==800 || dm.heightPixels==854) && (dm.widthPixels==480)) ||
			   (dm.heightPixels==1024 || dm.widthPixels==600)
	   		)
	    	return MEDIUMDPI_LARGE;

	    return HIGHDPI_NORMAL;
		 */
	}
	/**
	 * aujourd'hui en hhmmss
	 * @author Marc VOUAUX
	 * @return
	 */
	static public String gethhmmss()
	{
		final Calendar c = Calendar.getInstance( ); 

		int mHour = c.get(Calendar.HOUR_OF_DAY);
		int mMin= c.get(Calendar.MINUTE);
		int mSec= c.get(Calendar.SECOND);

		String heure=String.format("%02d%02d%02d",mHour,mMin,mSec);

		return heure;

	}
	static public String gethh_mm()
	{
		final Calendar c = Calendar.getInstance( ); 

		int mHour = c.get(Calendar.HOUR_OF_DAY);
		int mMin= c.get(Calendar.MINUTE);
		
	//	String heure=String.format("%02d%02d%02d",mHour,mMin);
		
		
		String heure=String.format("%02d",mHour );
		String minute=String.format("%02d",mMin);
	

		String heureF=heure+":"+minute+"" ;


		return heureF;

	}
	/**
	 * aujourd'hui en yyyymmddhhmmss
	 * @author Marc VOUAUX
	 * @return
	 */
	static public String getYYYYMMDDhhmmss()
	{
		final Calendar c = Calendar.getInstance( ); 

		int mYear = c.get(Calendar.YEAR); 
		int mMonth = c.get(Calendar.MONTH)+1; 
		int mDay = c.get(Calendar.DAY_OF_MONTH);
		int mHour = c.get(Calendar.HOUR_OF_DAY);
		int mMin= c.get(Calendar.MINUTE);
		int mSec= c.get(Calendar.SECOND);

		String heure=String.format("%04d%02d%02d%02d%02d%02d", mYear,mMonth,mDay,mHour,mMin,mSec);

		return heure;

	}

	/*
	 * format yyyy-mm-dd hh:mm:ss
	 */
	static public String getYYYYMMDDhhmmss(Date now)
	{
		Calendar c = new GregorianCalendar(); 
		c.setTime( now ); 
		int mYear = c.get(Calendar.YEAR); 
		int mMonth = c.get(Calendar.MONTH)+1; 
		int mDay = c.get(Calendar.DAY_OF_MONTH);
		int mHour = c.get(Calendar.HOUR_OF_DAY);
		int mMin= c.get(Calendar.MINUTE);
		int mSec= c.get(Calendar.SECOND);

		String heure=String.format("%04d-%02d-%02d %02d:%02d:%02d", mYear,mMonth,mDay,mHour,mMin,mSec);

		return heure;

	}

	static public String getDD_MM_YYYY()
	{
		final Calendar c = Calendar.getInstance( ); 

		int mYear = c.get(Calendar.YEAR); 
		int mMonth = c.get(Calendar.MONTH)+1; 
		int mDay = c.get(Calendar.DAY_OF_MONTH);

		String day=String.format("%02d",mDay );
		String month=String.format("%02d",mMonth);
		String year=String.format("%04d",mYear);


		String heure=day+"/"+month+"/"+year ;

		return heure;



	}
	
	static public String getYYYYMMDD(long timeStamp_ms)
	{
		final Date d = new Date(timeStamp_ms);
		Calendar c = Calendar.getInstance( );
		c.setTime(d);
		

		int mYear = c.get(Calendar.YEAR); 
		int mMonth = c.get(Calendar.MONTH)+1; 
		int mDay = c.get(Calendar.DAY_OF_MONTH);

		String day=String.format("%02d",mDay );
		String month=String.format("%02d",mMonth);
		String year=String.format("%04d",mYear);


		String result=year+month+day ;

		return result;



	}
	
	static public String getWEEKDAY_DD_MM_YYYY(long timeStamp_ms)
	{
		return SQLRequestHelper.getFrenchDay(new Date(timeStamp_ms))+" "+getDD_MM_YYYY(timeStamp_ms);
	}
	
	
	static public String getDD_MM_YYYY(long timeStamp_ms)
	{
		final Date d = new Date(timeStamp_ms);
		Calendar c = Calendar.getInstance( );
		c.setTime(d);
		
		
		

		int mYear = c.get(Calendar.YEAR); 
		int mMonth = c.get(Calendar.MONTH)+1; 
		int mDay = c.get(Calendar.DAY_OF_MONTH);

		String day=String.format("%02d",mDay );
		String month=String.format("%02d",mMonth);
		String year=String.format("%04d",mYear);


		String heure=day+"/"+month+"/"+year ;

		return heure;



	}
	
	
	static public String getDD_MM_YYYY(int lastday)
	{
		final Calendar c = Calendar.getInstance( ); 

		int mYear = c.get(Calendar.YEAR); 
		int mMonth = c.get(Calendar.MONTH)+1; 
		int mDay = lastday;

		String day=String.format("%02d",mDay );
		String month=String.format("%02d",mMonth);
		String year=String.format("%04d",mYear);


		String heure=day+"/"+month+"/"+year ;

		return heure;



	}
	
	static public String getDD_MM_YYYY(String YYYYMMDD)
	{

		try{
			String year=YYYYMMDD.substring(0, 4);
			String month=YYYYMMDD.substring(4, 6);
			String day=YYYYMMDD.substring(6, 8);
			return day+"/"+month+"/"+year;
		}
		catch(Exception e){

		}
		return YYYYMMDD;


	}
	
	
	static public String getYYYYMMDD()
	{
		final Calendar c = Calendar.getInstance( ); 

		int mYear = c.get(Calendar.YEAR); 
		int mMonth = c.get(Calendar.MONTH)+1; 
		int mDay = c.get(Calendar.DAY_OF_MONTH);



		String heure=String.format("%04d%02d%02d", mYear,mMonth,mDay);

		return heure;



	}
	static public String getYYYYMMDD(String dd_mm_yyyy)
	{
		if(dd_mm_yyyy == null || dd_mm_yyyy.length() < 10) return null;
		
		String jour;
		String mois;
		String annee;
		
		String[] tab = dd_mm_yyyy.split("/");
		
		if(tab.length == 3){
			jour = tab[0];
			mois = tab[1];
			annee = tab[2];
		}else return null;
		
		return annee+mois+jour;
	}
	static public String getYYYY()
	{
		final Calendar c = Calendar.getInstance( ); 

		int mYear = c.get(Calendar.YEAR); 



		String Annee=String.format("%04d", mYear);

		return Annee;



	}
	static public String getMM()
	{
		final Calendar c = Calendar.getInstance( ); 

		int mMonth = c.get(Calendar.MONTH)+1; 



		String Mois=String.format("%02d", mMonth);

		return Mois;



	}
	/**
	 * aujourd'hui en yyyymmdd
	 * @return
	 */
	static public String getDD()
	{
		final Calendar c = Calendar.getInstance( ); 

		int mDay = c.get(Calendar.DAY_OF_MONTH);



		String Day=String.format("%02d", mDay);

		return Day;



	}
	static public String getJourSemaine()
	{
		final Calendar c = Calendar.getInstance( ); 

		int mDayOfweek = c.get(Calendar.DAY_OF_WEEK);



		String stDayOfweek=String.format("%02d", mDayOfweek);

		return stDayOfweek;



	}
	static public String getJourdeAnne()
	{
		final Calendar c = Calendar.getInstance( ); 

		int mDayOfYear = c.get(Calendar.DAY_OF_YEAR);



		String stDayOfYear=String.format("%03d", mDayOfYear);

		return stDayOfYear;



	}

	static public String getDay_Of_Year()
	{
		String stDayofYear="111";
		try
		{
			Calendar calendar1 = Calendar.getInstance();
			int year = calendar1.get(Calendar.YEAR);
			int month = calendar1.get(Calendar.MONTH);
			int day = calendar1.get(Calendar.DAY_OF_MONTH);

			Calendar calendar2 = new GregorianCalendar(year, month, day);
			int doy2 = calendar2.get(Calendar.DAY_OF_YEAR);

			stDayofYear=Fonctions.getInToStringDanem(doy2);
		}
		catch(Exception ex)
		{

		}
		return stDayofYear;

	}
	static public String getDay_Of_Week()
	{
		String stDayofWeek="111";
		try
		{
			Calendar calendar1 = Calendar.getInstance();
			int year = calendar1.get(Calendar.YEAR);
			int month = calendar1.get(Calendar.MONTH);
			int day = calendar1.get(Calendar.DAY_OF_MONTH);

			Calendar calendar2 = new GregorianCalendar(year, month, day);
			int doy2 = calendar2.get(Calendar.DAY_OF_WEEK);



			stDayofWeek=Fonctions.getInToStringDanem(doy2);
		}
		catch(Exception ex)
		{

		}
		return stDayofWeek;

	}
	static public String getDay_Monday_Courant()
	{
		String stDayofWeek="111";
		try
		{
			Calendar cal = Calendar.getInstance();
			Date date = new Date();
			cal.setTime(date);
			cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
			int mYear = cal.get(Calendar.YEAR); 
	       	int mMonth = cal.get(Calendar.MONTH)+1; 
			int mDay = cal.get(Calendar.DAY_OF_MONTH);

			stDayofWeek=String.format("%04d%02d%02d", mYear,mMonth,mDay);

			
		}
		catch(Exception ex)
		{

		}
		return stDayofWeek;

	}
	static public String getDay_Sunday_Courant()
	{
		String stDayofWeek="111";
		try
		{
			Calendar cal = Calendar.getInstance();
			Date date = new Date();
			cal.setTime(date);
			cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
			int mYear = cal.get(Calendar.YEAR); 
	       	int mMonth = cal.get(Calendar.MONTH)+1; 
			int mDay = cal.get(Calendar.DAY_OF_MONTH);

			stDayofWeek=String.format("%04d%02d%02d", mYear,mMonth,mDay);

			
		}
		catch(Exception ex)
		{

		}
		return stDayofWeek;

	}
	static public String getWeek_Of_Year()
	{
		String stWeekofYear="111";
		try
		{
			Calendar calendar1 = Calendar.getInstance();
			int year = calendar1.get(Calendar.YEAR);
			int month = calendar1.get(Calendar.MONTH);
			int day = calendar1.get(Calendar.DAY_OF_MONTH);

			Calendar calendar2 = new GregorianCalendar(year, month, day);
			int doy2 = calendar2.get(Calendar.WEEK_OF_YEAR);

			stWeekofYear=Fonctions.getInToStringDanem(doy2);
		}
		catch(Exception ex)
		{

		}
		return stWeekofYear;

	}
	static public String getYYYYMMDD_Datecde(int ndelay)
	{
		final Calendar c = Calendar.getInstance( ); 

		int mYear = c.get(Calendar.YEAR); 
		int mMonth = c.get(Calendar.MONTH); 
		int mDay = c.get(Calendar.DAY_OF_MONTH);


		// entrée de la nouvelle date (dans 30 jours)
		c.set(mYear, mMonth, mDay +ndelay);

		// obtention des éléments de cette nlle date

		mYear = c.get(Calendar.YEAR); 
		mMonth = c.get(Calendar.MONTH)+1; 
		mDay = c.get(Calendar.DAY_OF_MONTH);


		String Date=String.format("%04d%02d%02d", mYear,mMonth,mDay);

		return Date;



	}

	/**
	 * transformation de date
	 * @author Marc VOUAUX
	 * @param YYYYMMDDhhmmss
	 * @return
	 */
	static public String YYYYMMDDhhmmss_to_dd_mm_yyyy_hh_mm_ss(String YYYYMMDDhhmmss)
	{
		try
		{
			if (YYYYMMDDhhmmss.length()!=14)
				return "";

			String year=YYYYMMDDhhmmss.substring(0, 4);
			String month=YYYYMMDDhhmmss.substring(4, 6);
			String day=YYYYMMDDhhmmss.substring(6, 8);
			String hour=YYYYMMDDhhmmss.substring(8, 10);
			String min=YYYYMMDDhhmmss.substring(10, 12);
			String sec=YYYYMMDDhhmmss.substring(12, 14);

			return day+"/"+month+"/"+year+" "+hour+":"+min+":"+sec;
		}
		catch(Exception ex)
		{

		}
		return "";

	}
	/**
	 * @author Marc VOUAUX
	 * @param YYYYMMDD
	 * @return
	 */
	static public String YYYYMMDD_to_dd_mm_yyyy(String YYYYMMDD)
	{
		try
		{
			if (YYYYMMDD.length()!=8)
				return "";

			String year=YYYYMMDD.substring(0, 4);
			String month=YYYYMMDD.substring(4, 6);
			String day=YYYYMMDD.substring(6, 8);

			return day+"/"+month+"/"+year;
		}
		catch(Exception ex)
		{
			return "";
		}


	}
	/**
	 * @author Marc VOUAUX
	 * @param YYYYMMDD
	 * @return
	 */
	static public String YYYYMMDD_to_yyyy_mm_dd(String YYYYMMDD)
	{
		try
		{
			if (YYYYMMDD.length()!=8)
				return "";

			String year=YYYYMMDD.substring(0, 4);
			String month=YYYYMMDD.substring(4, 6);
			String day=YYYYMMDD.substring(6, 8);

			return year+"/"+month+"/"+day;
		}
		catch(Exception ex)
		{
			return "";
		}


	}
	static public String dd_mm_yyyy_TO_yyyymmdd(String dd_mm_yyyy)
	{
		try
		{


			String year=dd_mm_yyyy.substring(6, 10);
			String month=dd_mm_yyyy.substring(3, 5);
			String day=dd_mm_yyyy.substring(0, 2);

			return year+month+day;
		}
		catch(Exception ex)
		{
			return "";
		}


	}
	static public String YYYY_MM_DD_to_dd_mm_yyyy(String YYYYMMDD)
	{
		try
		{
			if (YYYYMMDD.length()!=10)
				return "";

			String year=YYYYMMDD.substring(0, 4);
			String month=YYYYMMDD.substring(5, 7);
			String day=YYYYMMDD.substring(8, 10);

			return day+"/"+month+"/"+year;
		}
		catch(Exception ex)
		{
			return "";
		}


	}
	static public String hhmm_to_hh_point_mm(String hhmm)
	{
		try
		{
			if (hhmm.length()!=4)
				return "";

			String heure=hhmm.substring(0, 2);
			String minute=hhmm.substring(2, 4);

			return heure+":"+minute;
		}
		catch(Exception ex)
		{
			return "";
		}


	}
	static public void sendEmailMsg(Context c, String to, String text, String Subject)
	{
		Intent sendIntent = new Intent(Intent.ACTION_SEND);
		sendIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{to});
		sendIntent.putExtra(Intent.EXTRA_TEXT, text);
		sendIntent.putExtra(Intent.EXTRA_SUBJECT, Subject);
		sendIntent.setType("message/rfc822");
		c.startActivity(Intent.createChooser(sendIntent, "Envoyer un message"));
	}
	static public void sendEmailMsg2(Context c, String to, String cc, String cci, String text, String Subject)
	{
		Intent sendIntent = new Intent(Intent.ACTION_SEND);
		sendIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{to});
		sendIntent.putExtra(Intent.EXTRA_CC, new String[]{cc});
		sendIntent.putExtra(Intent.EXTRA_BCC, new String[]{cci});
		sendIntent.putExtra(Intent.EXTRA_TEXT, text);
		sendIntent.putExtra(Intent.EXTRA_SUBJECT, Subject);
		sendIntent.setType("message/rfc822");
		c.startActivity(Intent.createChooser(sendIntent, "Envoyer un message"));
	}
	/*
	 * Transform� un String en  Double
	 */
	static public double GetStringToDoubleDanem(String stvaleur)
	{
		try
		{
			double dValeur=0;
			if(stvaleur==null)
			{
				stvaleur="0";
			}
			if(stvaleur.equals(""))
			{
				stvaleur="0";
			}
			stvaleur=stvaleur.replace(",", ".");
			dValeur=Double.parseDouble(stvaleur);

			return dValeur;
		}
		catch(Exception ex)
		{
			return 0;
		}




	}
	/*
	 * Transform� un String en  float
	 */
	static public float GetStringToFloatDanem(String stvaleur)
	{
		try
		{
			float dValeur=0;
			if(stvaleur==null)
			{
				stvaleur="0";
			}
			if(stvaleur.equals(""))
			{
				stvaleur="0";
			}
			stvaleur=stvaleur.replace(",", ".");
			//dValeur=Float.parseDouble(stvaleur);
			dValeur=Float.parseFloat(stvaleur);

			return dValeur;
		}
		catch(Exception ex)
		{
			return 0;
		}




	}
	/*
	 * Transform� un String en  Int
	 */
	static public int GetStringToInt2Danem(String stvaleur)
	{
		try
		{
			double dValeur=0;
			dValeur=Fonctions.GetStringToDoubleDanem(stvaleur);

			DecimalFormat nf = new DecimalFormat("#");
			nf.setMaximumFractionDigits(Integer.MAX_VALUE);
			nf.setParseIntegerOnly(true);
			stvaleur=nf.format(dValeur);
			stvaleur=stvaleur.replace(",", ".");






			int iValeur=0;
			if(stvaleur==null)
			{
				stvaleur="0";
			}
			if(stvaleur.equals(""))
			{
				stvaleur="0";
			}
			stvaleur=stvaleur.replace(",", ".");
			iValeur=Integer.parseInt(stvaleur);


			return iValeur;

		}
		catch(Exception ex)
		{
			return 0;
		}

	}
	/*
	 * Transform� un String en  Int
	 */
	static public int GetStringToIntDanem(String stvaleur)
	{
		try
		{
			int iValeur=0;
			if(stvaleur==null)
			{
				stvaleur="0";
			}
			if(stvaleur.equals(""))
			{
				stvaleur="0";
			}
			stvaleur=stvaleur.replace(",", ".");
			iValeur=Integer.parseInt(stvaleur);


			return iValeur;

		}
		catch(Exception ex)
		{
			return 0;
		}

	}
	/*
	 * Enlev� le '.' dans un String 
	 */
	static public String GetStringDanem(String stvaleur)
	{
		String SValeurInt="";
		try
		{
			if(stvaleur==null)
			{
				stvaleur="";

			}
			SValeurInt=stvaleur.replace(",", ".");


			return SValeurInt;
		}
		catch(Exception ex)
		{
			return "";
		}





	}
	/*
	 * Format double en String avec param�tre du Decimal 
	 * ex: stlong="0.0000" alors valeur avec 4 d�cimal apr�s la virgule
	 */
	static public String GetDoubleToStringFormatDanem(double dValeur, String stlong)
	{
		String stValeur="";


		try
		{
			DecimalFormat dcFormat = new DecimalFormat(stlong);
			stValeur=dcFormat.format(dValeur);
			stValeur=stValeur.replace(",", ".");



			return stValeur;
		}
		catch(Exception ex)
		{
			return "";
		}





	}
	/*
	 * Format float en String avec param�tre du Decimal 
	 * ex: stlong="0.0000" alors valeur avec 4 d�cimal apr�s la virgule
	 */
	static public String GetFloatToStringFormatDanem(float dValeur, String stlong)
	{
		String stValeur="";
		try
		{
			DecimalFormat dcFormat = new DecimalFormat(stlong);
			stValeur=dcFormat.format(dValeur);
			stValeur=stValeur.replace(",", ".");



			return stValeur;
		}
		catch(Exception ex)
		{
			return "";
		}





	}
	/*
	 * Transform� un Int en String 
	 */
	static public String getInToStringDanem(int iValeur)
	{
		String stValeur="";

		try
		{
			stValeur=Integer.toString(iValeur);



			return stValeur;
		}
		catch(Exception ex)
		{
			return "";
		}


	}
	//BigDecimal.ROUND_HALF_UP
	public static double round(double unrounded, int precision, int roundingMode)
	{
		BigDecimal bd = new BigDecimal(unrounded);
		BigDecimal rounded = bd.setScale(precision, roundingMode);
		return rounded.doubleValue();
	}
	public static double round(double unrounded, int precision)
	{
		BigDecimal bd = new BigDecimal(unrounded);
		BigDecimal rounded = bd.setScale(precision, BigDecimal.ROUND_HALF_UP);
		return rounded.doubleValue();
	}
	/*
	 * Transform� un long en String 
	 */
	static public String getLongToStringDanem(long lValeur)
	{
		String stValeur="";

		try
		{
			stValeur=Long.toString(lValeur);


			return stValeur;
		}
		catch(Exception ex)
		{
			return "";
		}


	}
	/**
	 * @author Marc VOUAUX
	 * retourne le numver en entier
	 * 
	 * @param c
	 * @return
	 */
	static public int getVersion(Context c,StringBuffer sbVer) {
		int v = 0;
		try {
			v = c.getPackageManager().getPackageInfo(c.getApplicationInfo().packageName, 0).versionCode;
			String version=c.getPackageManager().getPackageInfo(c.getApplicationInfo().packageName, 0).versionName;

			String ver=String.valueOf(v)+"."+version;
			sbVer.append(ver);
			ver=ver.replace(".", "");
			return Integer.parseInt(ver);

		} catch (NameNotFoundException e) {
			// Huh? Really?
		}
		return -1;
	}

	static public String getPrixVenteTTC(String PrixVenteHT)
	{
		double prixventeHt=0;
		double tva=1.196; //tva 19.6
		double prixventeTTC=0;
		String GetPrixVenteTTC="";
		prixventeTTC=Fonctions.GetStringToDoubleDanem(PrixVenteHT)*tva;
		GetPrixVenteTTC =Fonctions.GetDoubleToStringFormatDanem(prixventeTTC, "0.00");

		int v = 0;
		try {
			prixventeTTC=prixventeHt*tva;

		} catch (Exception ex) {
			// Huh? Really?
		}

		return GetPrixVenteTTC;
	}
	static public String Left(String val,int len)
	{
		try
		{
			if (len>val.length()) len=val.length()-0;
			String chaine=val.substring(0,len);

			return chaine;
		}
		catch(Exception ex)
		{
			return "";	
		}
	}
	static public String Mid(String val,int start,int len)
	{
		try
		{
			if (start+len>val.length()) len=val.length()-start;
			String chaine=val.substring(start,start+len);

			return chaine;
		}
		catch(Exception ex)
		{
			return "";	
		}
	}
	static public String Right(String val,int len)
	{
		try
		{
			int lendebut=0;
			int lenfin=0;
		 	if (val.length()>len) lendebut=val.length()-len;

		 

			lenfin=lendebut+len;


			String chaine=val.substring(lendebut,lenfin);

			return chaine;
		}
		catch(Exception ex)
		{
			return "";	
		}
	}
	static public String Right2(String val,int len)
	{
		try
		{
			int lendebut=0;
			int lenfin=0;
			lendebut=val.length()-len;

			lenfin=lendebut+len;


			String chaine=val.substring(lendebut,lenfin);

			return chaine;
		}
		catch(Exception ex)
		{
			return "";	
		}
	}
	static public String ReplaceGuillemet(String val)
	{
		String toto="";

		try
		{
			//on double les quotes si il y a une quote    		
			toto=val.replace("�",".");
			toto=val.replace("\r\n"," ");
			toto=val.replace("\r"," ");
			toto=val.replace("\n"," ");
			return toto;
		}
		catch(Exception ex)
		{

		}
		return "";
	}
	static public String ReplaceEspace(String val)
	{
		String toto="";

		try
		{
			//on double les quotes si il y a une quote    		
			toto=val.replace(" ","");
			return toto;
		}
		catch(Exception ex)
		{

		}
		return "";
	}
	static public String ReplaceVirgule(String val)
	{
		String toto="";

		try
		{
			//on double les quotes si il y a une quote    		
			toto=val.replace(",",".");
			return toto;
		}
		catch(Exception ex)
		{

		}
		return "";
	}
	public static boolean Longeur(String s,int Max){
		if(Max==0)
			Max=255;
		int tailleString=0;

		boolean result = true;
		if(s != null && s.equals("") == true)
		{

		}
		else
		{
			tailleString=s.length();
			if(tailleString>Max)
				result = false;

		}

		return result;
	}
	public static int LastDayOfMonth(int nMonth)
	{
		switch(nMonth)
		{
		case 1 :
		case 3 :
		case 5 :
		case 7 :
		case 8 :
		case 10 :
		case 12 :
			return 31;
		case 2 :
			return 28;
		case 4 :
		case 6 :
		case 9 :
		case 11 :
			return 30;
		default :
			return -1;

		}
	}
	static public String AddSpace(String val,int leng, int Calibre)// 0 : a gauhe - 1: a droite
	{
		String toto="";


		try
		{
			if(Calibre==1)//calibre � droite
			{
				toto = val.format("%"+leng+"s", val);

			}
			else
			{
				toto = val.format("%-"+leng+"s", val);

			}

			return toto;
		}
		catch(Exception ex)
		{

		}
		return "";
	}

	public static ArrayList antiDoublons(ArrayList al) {

		ArrayList al2 = new ArrayList();
		for (int i=0; i<al.size(); i++) {
			Object o = al.get(i);
			if (!al2.contains(o))
				al2.add(o);
		}
		al = null;
		return al2;

	}

	public static boolean isEmptyOrNull(String s){
		boolean result = true;
		if(s != null && s.equals("") == false && s.length()>0)
			result = false;
		return result;
	}

	public static String pad(int c) {
		if (c >= 10)
			return String.valueOf(c);
		else
			return "0" + String.valueOf(c);
	}


	public static String padDistance(Context context, String distance){
		String result = distance + " "+context.getString(R.string.m);
		
		if(distance != null && distance.length()>3){
			result = distance.substring(0, distance.length()-3);
			if(distance.length() == 4)
				result +=","+distance.substring(1, 2);
			result +=" "+context.getString(R.string.km);
		}
		
//		Debug.Log("requete", "DISTANCE before/after : "+distance+" / "+result);
		return result;
	}

	static public String ymd_to_YYYYMMDD(int y,int m, int d)
	{


		String heure=String.format("%04d%02d%02d", y,m,d);

		return heure;

	}
	
	/**
	 * Permet d'arrondir un nombre à la précision choisie (exemple 100 = 0.00, 1000 = 0.000 etc)
	 * @param int precision choisie
	 * @param double nombre à arrondir
	 * @return double
	 */
	public static Double roundz(int precision, Double nombre){
		Double arrondi = 0.0;
		if(precision % 10 == 0){
			double value = (double) precision;
			arrondi = (double) (Math.round(nombre * value)/value);
		}else return 0.0;
		return arrondi;
	}
	static public boolean WriteProfileString(Context c, String key, 
			String val) 
	{
		String app=c.getString(R.string.app_name);
		SharedPreferences settings = c.getSharedPreferences(app, 0);
	      SharedPreferences.Editor editor = settings.edit();
	      editor.putString(key, val);

	      // Commit the edits!
	      return editor.commit();
	}
	static public String GetProfileString(Context c, String key, 
			String val) 
	{
		String app=c.getString(R.string.app_name);
		 SharedPreferences settings = c.getSharedPreferences(app, 0);
	     String result = settings.getString(key, val);
	     
	     return result;
	}
	
	//on recup un fichier en HTTP
	static public	int getHHTTPFile(String apkurl,String apkname)
		{
			File outputFile =null;
			try {

				URL url = new URL(apkurl);
				HttpURLConnection c = (HttpURLConnection) url.openConnection();
				c.setRequestMethod("GET");
				//si ics on ne fait pas cette fonction
				if (android.os.Build.VERSION.SDK_INT >= 14)
				{
					
				}
				else
					c.setDoOutput(true);
				
				c.connect();

				String PATH = ExternalStorage.getFolderPath(ExternalStorage.DOWNLOAD_FOLDER);
				File file = new File(PATH);
				file.mkdirs();
				  outputFile = new File(file, apkname);
				FileOutputStream fos = new FileOutputStream(outputFile);

				InputStream is = c.getInputStream();

				byte[] buffer = new byte[1024];
				int len1 = 0;
				while ((len1 = is.read(buffer)) != -1) {
					fos.write(buffer, 0, len1);
				}
				fos.close();
				is.close();// till here, it works fine - .apk is download to my
							// sdcard in download file
				return 1;
			} catch (IOException e) {
				// Toast.makeText(getApplicationContext(), "Update error!",
				// Toast.LENGTH_LONG).show();
				deleteFile(outputFile);
				return 0;
			} catch (Exception e) {
				return 0;
				// Toast.makeText(getApplicationContext(), "Update error!",
				// Toast.LENGTH_LONG).show();
			}
			
		}
	static public boolean deleteFile(File f)
		{
			try
			{
		 
				return f.delete();
			}
			catch(Exception ex)
			{
				return false;
			}
			
		}


	public static void DeleteFiles(String path,String fichier)
	{
		try{
			FileChecker fc=new FileChecker();
			String contain=Fonctions.GiveFld(fichier,0,".",true);
			String ext=Fonctions.GiveFld(fichier,1,".",false);
			contain=contain.replace("*","");
			ext=ext.replace("*","");
			fc.deleteFile(path,contain,ext);
		}
		catch(Exception ex){
			
		}
	}
	public static boolean IsFileExist(String path,String fichier)
	{
		try {
		
			String contain=Fonctions.GiveFld(fichier,0,".",true);
			String ext=Fonctions.GiveFld(fichier,1,".",false);
			contain=contain.replace("*","");
			ext=ext.replace("*","");
			
			FileChecker f=new FileChecker();
			return f.isFileExist(path,ext,contain);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	static public String YYYYMMDDhhmmss_to_dd_mm_yyyy_hh_mm_ss2(String YYYYMMDDhhmmss)
	{
		try
		{
			if (YYYYMMDDhhmmss.length()!=15)
				return "";

			String year=YYYYMMDDhhmmss.substring(0, 4);
			String month=YYYYMMDDhhmmss.substring(4, 6);
			
			String day=YYYYMMDDhhmmss.substring(7, 9);
			String hour=YYYYMMDDhhmmss.substring(9, 11);
			String min=YYYYMMDDhhmmss.substring(11, 13);
			String sec=YYYYMMDDhhmmss.substring(13, 15);

			return day+"/"+month+"/"+year+" "+hour+":"+min+":"+sec;
		}
		catch(Exception ex)
		{

		}
		return "";

	}
	
	 
	static public String YYYYMMDD_PLUSe(String date,int adddays,int addmonths,boolean endofmonth)
	{
		try
		{
			//la date de d�part
			int day=Integer.parseInt(   date.substring(6,8));
			int month=Integer.parseInt(   date.substring(4,6))+addmonths;
			int year=Integer.parseInt(   date.substring(0,4));
			
			
			Calendar calendar2 = new GregorianCalendar();
			calendar2.clear();
	         
	         // on ajoute le nombre de mois demand�
			calendar2.set(Calendar.YEAR,year);
			calendar2.set(Calendar.MONTH,month );
			calendar2.set(Calendar.DAY_OF_MONTH,day);
	         
	         //on cherche le dernier jour du mois
			if (endofmonth)
			{

				calendar2.set(Calendar.DAY_OF_MONTH, calendar2.getActualMaximum(Calendar.DAY_OF_MONTH));
				day = calendar2.get(Calendar.DAY_OF_MONTH);
				
				if (adddays>0)
				{
					calendar2.set(year, month+1 ,  adddays);
				}
			}
	         //finalement on ajoute le nombre de jour demand�
			if (adddays>0)
			{
				calendar2.set(year, month , day+ adddays);
			}
			
			
			year = calendar2.get(Calendar.YEAR); 
			month = calendar2.get(Calendar.MONTH); 
			day = calendar2.get(Calendar.DAY_OF_MONTH);
	         
			String stday=String.format("%02d",day );
			String stmonth=String.format("%02d",month);
			String styear=String.format("%04d",year);


			String aaaammjj=styear+""+stmonth+""+stday ;
			
			return aaaammjj;

		}
		catch(Exception ex)
		{
			return date;
		}
		
	}
	static public String YYYYMMDD_PLUSex(String date,int adddays,int addmonths,boolean endofmonth)
	{
		try
		{
			date=Fonctions.YYYYMMDD_to_yyyy_mm_dd(date).replace("/", "-");
			
			DateTime dt=new DateTime(date);
			
			if (addmonths>0)
			{
				DateTime.DayOverflow dof = null ;
				dt=dt.plus(0, addmonths, 0, 0, 0, 0, 0,dof);
			}
			
			if (endofmonth)
				dt=dt.getEndOfMonth();
			
			if (adddays>0)
			{
				DateTime.DayOverflow dof = null ;
				dt=dt.plus(0, 0, adddays, 0, 0, 0, 0,dof);
			}
		 
			String j=dt.getDay()+"";
			j=Fonctions.Right("0"+j,2);
			String m=Fonctions.Right("0"+dt.getMonth(),2);
			String y=dt.getYear()+"";
			String aaaammjj=y+m+j;
			
			return aaaammjj;

		}
		catch(Exception ex)
		{
			return date;
		}
		
	}
	static public String gethhmm()
	{
		
		return Left( gethhmmss(),4);

	}
	public final static boolean isValidEmail(CharSequence target) 
	{
		if (TextUtils.isEmpty(target)) 
		{ return false; 
		}
		else 
		{ return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches(); 
		} 
	}
	static public void sendEmailBaseDonneegPieceJointe(Activity c,String login, String to, String cc, String cci, String text, String Subject)
	{
		String DATABASE_NAME =MyDB.backupDB(MyDB.source, login);
	 
 

		Intent sendIntent = new Intent(Intent.ACTION_SEND);
		sendIntent = new Intent(android.content.Intent.ACTION_SEND_MULTIPLE);
		sendIntent.setType("text/plain");

		//sendIntent.setClassName("com.google.android.gm","com.google.android.gm.ComposeActivityGmail");
		sendIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{to});
		sendIntent.putExtra(Intent.EXTRA_CC, new String[]{cc});
		sendIntent.putExtra(Intent.EXTRA_BCC, new String[]{cci});
		sendIntent.putExtra(Intent.EXTRA_TEXT, text);
		sendIntent.putExtra(Intent.EXTRA_SUBJECT, Subject);
		ArrayList<Uri> uris = new ArrayList<Uri>();
		String[] filePaths = new String[] {Environment.getExternalStorageDirectory() + "/" + DATABASE_NAME};
		for (String file : filePaths)
		{
			File fileIn = new File(file);
			Uri u = Uri.fromFile(fileIn);
			uris.add(u);
		}
		sendIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);

		// sendIntent.putExtra(Intent.EXTRA_STREAM,path);
		sendIntent.setType("message/rfc822");
		c.startActivityForResult(Intent.createChooser(sendIntent, "Envoyer un message"),9944);


	//	dest.delete();

	}
}
