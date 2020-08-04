package com.menadinteractive.negos.media;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.menadinteractive.segafredo.communs.Fonctions;

public class ItecMediaFile {
/*
 
 #define	PREFIX_SIG				L"SIGNATURE_signature_0001"
#define	PREFIX_CROQUIS			L"CROQUIS_croquis"
#define	PREFIX_PREOPE			L"AVENT_photo"
#define	PREFIX_POSTOPE			L"APENT_photo"
#define	PREFIX_AUDIO			L"MESSAGE_message"
#define EXT_BMP					L".bmp" 
#define EXT_JPG					L".jpg" 
#define EXT_AUDIO				L".*" 

stId=m_stCodeSite+L"-"+m_stNbrInter+L"- -"+m_stNbrLin

TAG_IDXFILE= index autoincrement  //4digits

stNewFilename.Format(_T("%s\\%s-%s_%s.%s"), directory ,stId, PREFIX_*, TAG_IDXFILE, EXT_*);
 
 */
	
	public static final String PREFIX_SIG		= "SIGNATURE_signature_0001";
	public static final String PREFIX_CROQUIS 	= "CROQUIS_croquis";
	public static final String PREFIX_PREOPE 	= "AVANT_photo";
	
	public static final String PREFIX_POSTOPE 	= "APRES_photo";
	public static final String PREFIX_AUDIO 	= "MESSAGE_message";
	public static final String PREFIX_PHOTOCLIENT	= "CLIENT_photo";
	public static final String PREFIX_PHOTOPRODUIT	= "PRODUIT_photo";
	public static final String PREFIX_PHOTOMEA	= "MEA_photo";
	public static final String PREFIX_IDENT 	= "IDENTIFIANT_photo";
	
	
	public static final String EXT_BMP			= ".bmp";
	public static final String EXT_JPG			= ".jpg";
	public static final String EXT_AUDIO		= ".wav";
	
	
	public static String generateStId(String stCodeSite, String stNbrInter, String stNbrLin){
		return String.format("%s-%s-%s", stCodeSite, stNbrInter, stNbrLin);
	}
	public static String generateIdent(String patern,String ext){
		return String.format("%s%s",patern,ext);
	}
	public static String generateFileName(String stId, String prefix, String tag_IdxFile, String ext){
		return String.format("%s-%s_%s%s", stId,prefix, tag_IdxFile, ext);
	}
	public static String generateFileName2(String numcde,String type,String coderep,String codecli,String codesoc,String idx,String ext){
		return String.format("%s-%s-%s-%s-%s-%s-%s%s", 
				Fonctions.getYYYYMMDDhhmmss(),
				numcde,
				type,
				coderep,
				codecli,
				codesoc,
				idx,
				ext
				);
	}
	
	public static String generateRegex(String stId, String prefix){
		return String.format("%s-%s", stId, prefix);
	}
	
	/*
	 * Un nom de fichier a ce format 
	 * 266-1500- -529-AVENT_photo_0001.jpg
	 * 
	 * On extrait les 4 charactères avant l'extension
	 * Dans cet exemple, getIndex renverra 1
	 */
	public static int getIndex(File f){
		try{
			String fileName = f.getName();
			String sIndex = fileName.substring(fileName.length()-8, fileName.length()-4);
			Log.d("requete", "INDEX : "+sIndex);
			return Integer.valueOf(sIndex);
		}
		catch(Exception e){
			return 0;
		}
	}
	
	/**
	 * Decode une Image à la taille souhaitee
	 * @param f
	 * @param defaultBitmap
	 * @return
	 */
	 public static Bitmap decodeFile(File f, Bitmap defaultBitmap, int imageMaxSizePx /** 300 px*/){
	        try {
	            //decode image size
	            BitmapFactory.Options o = new BitmapFactory.Options();
	            o.inJustDecodeBounds = true;
	            BitmapFactory.decodeStream(new FileInputStream(f),null,o);
	            o.inTempStorage = new byte[16*1024];
	            
	            //Find the correct scale value. It should be the power of 2.
	            final int REQUIRED_SIZE=imageMaxSizePx;
	            int width_tmp=o.outWidth, height_tmp=o.outHeight;
//	            int original_width = o.outWidth;
//	            int original_height = o.outHeight;
//	            long fileSize = (int) f.length()/1000;
	            int scale=1;
	            while(true){
	                if(width_tmp/2<REQUIRED_SIZE || height_tmp/2<REQUIRED_SIZE)
	                    break;
	                width_tmp/=2;
	                height_tmp/=2;
	                scale++;
	            }

	            //decode with inSampleSize
	            BitmapFactory.Options o2 = new BitmapFactory.Options();
	            o2.inSampleSize=scale;
	            Log.d("TAG", "scale : "+o.outWidth+"px x "+o.outHeight+"px to "+scale + " "+width_tmp+"/"+height_tmp);
	            
//	            int decoded_height = height_tmp;
//	            int decodeded_width = width_tmp;
	            Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
	           
	            return bitmap;
	        } catch (FileNotFoundException e) {
	        	 Log.d("TAG", "scale not found! "+e.toString());
	        }
	        catch(Exception e){
	        	Log.d("TAG", "Error! "+e.toString());
	        	return defaultBitmap;
	        }
	        return defaultBitmap;
	    }
	
}
