package com.menadinteractive.segafredo.communs;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;

import android.util.Log;

public class MyParser {
		private static String TAG = "Parser"; 
		private static String[] splittedContent={"0"};
		public static final String  SEPARATEUR=";";
		public static final String  SEPARATEUR_SEMICOLON =";";
		public static final String  SEPARATEUR_PIPE="|";
		public static final String  SEPARATEUR_TRAIT="-";


		/** FIXME : est-ce qu'il peut y avoir des cas o� splittedContent est ovverid� par 2 appels � MyParser ?
		 * peut-etre cr�er une instance au lieu d'utiliser l'objet en static */


		public static String[] splitFields(String stream, String sep){
			return stream.split("["+sep+"]");
		}

		public static String extractField(String chainesep, int fld, String sep)
		{

			try
			{
				if (chainesep != null){
					splittedContent=chainesep.split("\\"+sep);
					return splittedContent[fld].toString();
				}
				else{
					if(splittedContent != null)
						return splittedContent[fld].toString();
				}
				return "";
			}
			catch(Exception ex)
			{
				String err=ex.getMessage();
				Log.d(TAG, "error in extract field : "+err);
				return "";
			}

		}
		static public boolean parseBoolean(String s){
			boolean result = false;

			if(s != null && (s.equals("o") == true || s.equals("1") == true))
				result = true;

			return result;
		}

		public static byte[] convertFileToByteArray(File f){
			byte[] b;
			try{
				int length = (int)f.length();
				b = new byte[length];
				FileInputStream fin = new FileInputStream(f);
				DataInputStream din = new DataInputStream(fin);
				din.read(b);
				return b;
			}catch(Exception ex){}
			return new byte[1];

		}
}
