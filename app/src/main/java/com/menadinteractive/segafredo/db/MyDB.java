package com.menadinteractive.segafredo.db;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

import com.menadinteractive.maxpoilane.Debug;
import com.menadinteractive.segafredo.communs.Fonctions;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

public class MyDB extends SQLiteOpenHelper 
{
	public SQLiteDatabase conn;
	public static final String DATABASE_NAME = "expresso.db";
	private static final int DATABASE_VERSION = 1;




	public MyDB(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);


	}
	//Tof, copy de la base sur la SD
	public static File source =  new File("/data/data/com.menadinteractive.maxpoilane/databases/" + DATABASE_NAME);
	public static File dest =  new File(Environment.getExternalStorageDirectory() + "/" + DATABASE_NAME );

	public static void copyFile(File sourceFile, File destFile) {

		FileChannel source = null;
		FileChannel destination = null;

		try {
			if (!destFile.exists()) {
				destFile.createNewFile();
			}

			source = new FileInputStream(sourceFile).getChannel();
			destination = new FileOutputStream(destFile).getChannel();
			destination.transferFrom(source, 0, source.size());

		} catch (Exception e) {
			/* handle exception... */
		} finally {
			try {
				if (source != null) {
					source.close();
				}
				if (destination != null) {
					destination.close();
				}
			} catch (Exception e) {
				/* handle exception... */
			}
		}
	}
	public static String backupDB(File sourceFile, String coderep) {
		
		String filename="fd_maxpoilane_"+coderep+"_"+Fonctions.getYYYYMMDD()+  ".danem";
		File destFile =  new File(Environment.getExternalStorageDirectory() + "/"+filename);
		
		FileChannel source = null;
		FileChannel destination = null;

		try {
			if (!destFile.exists()) {
				destFile.createNewFile();
			}

			
			source = new FileInputStream(sourceFile).getChannel();
			destination = new FileOutputStream(destFile).getChannel();
			destination.transferFrom(source, 0, source.size());

			
			return filename;
		} catch (Exception e) {
			/* handle exception... */
		} finally {
			try {
				if (source != null) {
					source.close();
				}
				if (destination != null) {
					destination.close();
				}
			} catch (Exception e) {
				/* handle exception... */
			}
		}
		return "";
	}

	@Override
	public void onCreate(SQLiteDatabase db) {




	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Debug.Log("Upgrading database from version " + oldVersion + " to "
				+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS uuu");
		onCreate(db);
	}

	/**
	 * Execute une requete INSERT, UPDATE etc... sans retour de cursor
	 * @author Marc VOUAUX
	 * @param query
	 * @param err
	 * @return true si ok
	 */
	public boolean execSQL(String query,StringBuilder err)
	{
		try
		{
			conn.execSQL(query);
		}
		catch(Exception ex)       	
		{
			err.setLength(0);
			err.append(ex.getMessage());
			//Debug.StackTrace(ex);
			return false;
		}
		return true;

	}

	public boolean compact(StringBuilder err)
	{
		try
		{
			return execSQL("VACUUM",err);

		}
		catch(Exception ex)
		{
			err.append(ex.getMessage());
		}
		return false;
	}
	static public String controlFld(String val)
	{
		String toto="";

		try
		{
			//on double les quotes si il y a une quote
			toto=val.replace("'","''");


			return toto;
		}
		catch(Exception ex)
		{

		}
		return "";
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
	static public String Replace2point(String val)
	{
		String toto="";

		try
		{
			//on double les quotes si il y a une quote

			toto=val.replace(":","");
			return toto;
		}
		catch(Exception ex)
		{

		}
		return "";
	}
	static public String AddSpace(String val,int leng, int Calibre)// 0 : a gauhe - 1: a droite
	{
		String toto="";


		try
		{
			if(Calibre==1)//calibre � droite
			{
				toto = String.format("%"+leng+"s", val);

			}
			else
			{
				toto = String.format("%-"+leng+"s", val);

			}

			return toto;
		}
		catch(Exception ex)
		{

		}
		return "";
	}

}


