package com.menadinteractive.segafredo.db;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Preferences {
	public static final String PREFS_NAME = "Espresso_prefs";


	public static final String KEY_FIRST_LAUNCH = "firstLaunch";
	public static final String KEY_DATE_TOURNEE = "dateTournee";
	public static final String KEY_DATE_TOURNEE_ON_DEMAND = "dateTourneeOnDemand";
	public static final String KEY_DATE_TOURNEE_ZONE = "dateTourneeZone";
	// Other keys are the located in ExpressoPerfect helper class



	public static boolean isFirstLaunch(Context context){
		SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
		boolean isFirstLaunch = settings.getBoolean(KEY_FIRST_LAUNCH, true);
		return isFirstLaunch;
	}

	public static void setFirstLaunch(Context context, boolean isFirstLaunch){
		SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor prefEditor = settings.edit();
		prefEditor.putBoolean(KEY_FIRST_LAUNCH, isFirstLaunch);
		prefEditor.commit();
	}

	public static String getValue(Context context, String key, String defaultValue){
/*		SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
		String value = settings.getString(key, defaultValue);
		return value;
		*/
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getString(key, defaultValue);
	}

	public static void setValue(Context context, String key, String value){
/*		SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor prefEditor = settings.edit();
		prefEditor.putString(key, value);
		prefEditor.commit();
		*/
	    SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(key, value);
        editor.commit();
	}

	public static boolean getValueBoolean(Context context, String key, boolean defaultValue){
/*		SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
		boolean value = settings.getBoolean(key, defaultValue);
		return value;
	*/	
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getBoolean(key, defaultValue);
	}

	public static void setValueBoolean(Context context, String key, boolean value){
/*		SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor prefEditor = settings.edit();
		prefEditor.putBoolean(key, value);
		prefEditor.commit();
		*/
	    SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(key, value);
        editor.commit();
	}
	
	public static int getValueInt(Context context, String key, int defaultValue){
/*		SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
		int value = settings.getInt(key, defaultValue);
		return value;
		*/
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getInt(key, defaultValue);
	}

	public static void setValueInt(Context context, String key, int value){
/*		SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor prefEditor = settings.edit();
		prefEditor.putInt(key, value);
		prefEditor.commit();
		*/
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(key, value);
        editor.commit();
	}
	
	public static long getValueLong(Context context, String key, long defaultValue){
/*		SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
		long value = settings.getLong(key, defaultValue);
		return value;
		*/
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getLong(key, defaultValue);
	}

	public static void setValueLong(Context context, String key, long value){
/*		SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor prefEditor = settings.edit();
		prefEditor.putLong(key, value);
		prefEditor.commit();
		*/
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = settings.edit();
        editor.putLong(key, value);
        editor.commit();
	}
	
}
