package com.menadinteractive.maxpoilane;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;

import com.menadinteractive.maxpoilane.R;
import com.menadinteractive.segafredo.carto.Marker;

public class ExternalStorage {
	public static String ROOT_FOLDER = "/maxpoilane/";
	public static String MARKER_FOLDER = ROOT_FOLDER+"Marker/";
	public static String STAT_FOLDER = ROOT_FOLDER+"Stat/";
	public static String MEDIA_FOLDER = ROOT_FOLDER+"media/";
	public static String DOWNLOAD_FOLDER = ROOT_FOLDER+"download/";
	public static String SIGNATURE_FOLDER = ROOT_FOLDER+"Signatures/";
	public static String SIGNATURE_FOLDER2 = ROOT_FOLDER+"Signatures";
	
	
	//	/** couleur */
	//	public static String ROUGE = "#ff0000";
	//	public static String BLEU = "#2e8ced";
	//	public static String ORANGE ="#ff7c48";
	//	public static String GRIS = "#ececec";
	//	public static String VERT = "#00ff00";

	Context context;

	public ExternalStorage(Context context, boolean duplicateMarkersOnSD){
		this.context = context;
		createFolders();
		if(duplicateMarkersOnSD)
			createMarkers(false);
	}

	public static boolean isFileExist(String path){
		try {
			File f = new File(path);
			return f.isFile();
		} catch (Exception e) {
			Debug.StackTrace(e);
		}
		return false;
	}

	private void createFolders(){
		createFolder(ROOT_FOLDER);
		createFolder(MARKER_FOLDER);
		createFolder(STAT_FOLDER);
		createFolder(MEDIA_FOLDER);
		createFolder(DOWNLOAD_FOLDER);
		createFolder(SIGNATURE_FOLDER);
	}

	public static String getFolderPath(String dir){
		return Environment.getExternalStorageDirectory() +File.separator+dir;	
	}

	private boolean createFolder(String dir){
		try {
			String PATH = Environment.getExternalStorageDirectory() +File.separator+dir;
			File file = new File(PATH);
			return file.mkdirs();
		} catch (Exception e) {
			Debug.StackTrace(e);
		}
		return false;
	}

	private void createMarkers(boolean force){
		/** boulangerie */
		createMarker(R.drawable.boulangerie, "boulangerie_vert.png", Marker.VERT, force);
		createMarker(R.drawable.boulangerie, "boulangerie_rouge.png", Marker.ROUGE, force);
		createMarker(R.drawable.boulangerie, "boulangerie_orange.png", Marker.ORANGE, force);
		createMarker(R.drawable.boulangerie, "boulangerie_gris.png", Marker.GRIS, force);
		createMarker(R.drawable.boulangerie, "boulangerie_bleu.png", Marker.BLEU, force);

		/** essence */
		createMarker(R.drawable.essence, "essence_vert.png", Marker.VERT, force);
		createMarker(R.drawable.essence, "essence_rouge.png", Marker.ROUGE, force);
		createMarker(R.drawable.essence, "essence_orange.png", Marker.ORANGE, force);
		createMarker(R.drawable.essence, "essence_gris.png", Marker.GRIS, force);
		createMarker(R.drawable.essence, "essence_bleu.png", Marker.BLEU, force);

		/** powerd_place */
		createMarker(R.drawable.powerd_place, "powerd_place_vert.png", Marker.VERT, force);
		createMarker(R.drawable.powerd_place, "powerd_place_rouge.png",Marker.ROUGE, force);
		createMarker(R.drawable.powerd_place, "powerd_place_orange.png", Marker.ORANGE, force);
		createMarker(R.drawable.powerd_place, "powerd_place_gris.png", Marker.GRIS, force);
		createMarker(R.drawable.powerd_place, "powerd_place_bleu.png", Marker.BLEU, force);

		/** restaurant */
		createMarker(R.drawable.restaurant, "restaurant_vert.png", Marker.VERT, force);
		createMarker(R.drawable.restaurant, "restaurant_rouge.png", Marker.ROUGE, force);
		createMarker(R.drawable.restaurant, "restaurant_orange.png", Marker.ORANGE, force);
		createMarker(R.drawable.restaurant, "restaurant_gris.png", Marker.GRIS, force);
		createMarker(R.drawable.restaurant, "restaurant_bleu.png", Marker.BLEU, force);

		/** sandwich */
		createMarker(R.drawable.sandwich, "sandwich_vert.png", Marker.VERT, force);
		createMarker(R.drawable.sandwich, "sandwich_rouge.png", Marker.ROUGE, force);
		createMarker(R.drawable.sandwich, "sandwich_orange.png", Marker.ORANGE, force);
		createMarker(R.drawable.sandwich, "sandwich_gris.png", Marker.GRIS, force);
		createMarker(R.drawable.sandwich, "sandwich_bleu.png", Marker.BLEU, force);

		/** sport */
		createMarker(R.drawable.sport, "sport_vert.png", Marker.VERT, force);
		createMarker(R.drawable.sport, "sport_rouge.png", Marker.ROUGE, force);
		createMarker(R.drawable.sport, "sport_orange.png", Marker.ORANGE, force);
		createMarker(R.drawable.sport, "sport_gris.png", Marker.GRIS, force);
		createMarker(R.drawable.sport, "sport_bleu.png", Marker.BLEU, force);

		/** supermarket */
		createMarker(R.drawable.supermarket, "supermarket_vert.png", Marker.VERT, force);
		createMarker(R.drawable.supermarket, "supermarket_rouge.png", Marker.ROUGE, force);
		createMarker(R.drawable.supermarket, "supermarket_orange.png", Marker.ORANGE, force);
		createMarker(R.drawable.supermarket, "supermarket_gris.png", Marker.GRIS, force);
		createMarker(R.drawable.supermarket, "supermarket_bleu.png", Marker.BLEU, force);

		/** tabac */
		createMarker(R.drawable.tabac, "tabac_vert.png", Marker.VERT, force);
		createMarker(R.drawable.tabac, "tabac_rouge.png", Marker.ROUGE, force);
		createMarker(R.drawable.tabac, "tabac_orange.png", Marker.ORANGE, force);
		createMarker(R.drawable.tabac, "tabac_gris.png", Marker.GRIS, force);
		createMarker(R.drawable.tabac, "tabac_bleu.png", Marker.BLEU, force);

	}

	private void createMarker(int drawableID, String fileName, String color, boolean force){
		if(force || !ExternalStorage.isFileExist(Environment.getExternalStorageDirectory() +File.separator+ExternalStorage.MARKER_FOLDER+File.separator+fileName)){
			createColoredMarker(context.getResources().getDrawable(drawableID), fileName, color);
		}

	}

	public Drawable getMarker(String fileName){
		Drawable d = null;
		if(ExternalStorage.isFileExist(Environment.getExternalStorageDirectory() +File.separator+ExternalStorage.MARKER_FOLDER+File.separator+fileName))
			d= Drawable.createFromPath(Environment.getExternalStorageDirectory() +File.separator+ExternalStorage.MARKER_FOLDER+File.separator+fileName);
		return d;
	}

	public void copyFile(InputStream in, OutputStream out) throws IOException {
		byte[] buffer = new byte[1024];
		int read;
		while((read = in.read(buffer)) != -1){
			out.write(buffer, 0, read);
		}
	}

	/**
	 * Récupère le bitmap d'un drawable et le copie sur la carte SD
	 * @param d
	 * @param path
	 */
	public void copyDrawable(Drawable d, String path){
		try {
			FileOutputStream out = new FileOutputStream(path);
			Bitmap bitmap = ((BitmapDrawable)d).getBitmap();
			bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
		} catch (Exception e) {
			Debug.StackTrace(e);
		}
	}






	public void createColoredMarker(Drawable grayscaleMarker, String fileName, String color){
		try {
			// A new Bitmap to hold the pixels
			Bitmap newBitmap = Bitmap.createBitmap(grayscaleMarker.getIntrinsicWidth(), grayscaleMarker.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);

			// a Canvas to host the draw calls (writing into the bitmap)
			Canvas c = new Canvas(newBitmap);

			// un pinceau : to describe the colors and styles for the drawing
			Paint p = new Paint();
			// Le mode Mode.OVERLAY ajoute la couleur sur tout ce qui est transparent !
			//PorterDuff.Mode.SRC : toute l'image est remplie de la couleur
			ColorFilter filter = new PorterDuffColorFilter(Color.parseColor(color), PorterDuff.Mode.MULTIPLY);
			p.setColorFilter(filter);

			// extract the bitmap of the drawable
			Bitmap grayscaleBitmap = ((BitmapDrawable)grayscaleMarker).getBitmap();

			// Told the Canvas to draw with the Colored Paint the grayscaleBitmap into the newBitmap
			c.drawBitmap(grayscaleBitmap, 0, 0, p);

			// write the newBitmap onto the SD Card
			FileOutputStream out = new FileOutputStream( Environment.getExternalStorageDirectory() +File.separator+ExternalStorage.MARKER_FOLDER+File.separator+fileName);
			newBitmap.compress(Bitmap.CompressFormat.PNG, 90, out);

		} catch (Exception e) {
			Debug.StackTrace(e);
		}
	}

	public String getSignaturesFolder(){
		return Environment.getExternalStorageDirectory().toString()+File.separator+SIGNATURE_FOLDER;
	}

}
