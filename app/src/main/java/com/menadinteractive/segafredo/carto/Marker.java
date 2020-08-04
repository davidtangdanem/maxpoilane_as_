package com.menadinteractive.segafredo.carto;

import java.util.HashMap;

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

import com.menadinteractive.maxpoilane.Debug;
import com.menadinteractive.maxpoilane.R;
import com.menadinteractive.segafredo.communs.Fonctions;

public class Marker {
	Context context;
	HashMap<String, Drawable> markers;

	/** couleur : NOT USED finally except BLEU*/
	public static String ROUGE = "#FF0000";
	public static String BLEU = "#2e8ced";
	public static String ORANGE ="#ff7c48";
	public static String GRIS = "#ececec";
	public static String VERT = "#00FF00";

	public Marker(Context context){
		this.context = context;
		this.markers = new HashMap<String, Drawable>();
	}

	public static String correspondanceColor(String color)
	{
		if (color==null)
			return GRIS;
		
		color=color.toLowerCase();
		
		if (color.contains("vert"))
			return VERT;
		
		if (color.contains("orange"))
			return ORANGE;
		
		if (color.contains("bleu"))
			return BLEU;
		
		if (color.contains("rouge"))
			return ROUGE;
		
		return GRIS;
	}
	public Drawable getMarker(String ressourceName, String color, String importance, String statut, String dejaVu){
		Drawable result = context.getResources().getDrawable(R.drawable.default_marker);

		color=correspondanceColor(color);
		
		int drawableId = getDrawableId(ressourceName);
		if(drawableId != 0){
			String drawableName = ressourceName+"_"+color+"_"+importance+"_"+statut+"_"+dejaVu;
			if(markers.containsKey(drawableName)){
				Debug.Log("existing drawable : "+drawableName);
				result = markers.get(drawableName);
			}
			else{
				Debug.Log("new drawable Name : "+drawableName);
				Drawable newDrawable = createColoredMarker(context.getResources().getDrawable(drawableId), color, statut, dejaVu);
				markers.put(drawableName, newDrawable);
				result = newDrawable;
			}
		}

		result.mutate();
		return result;
	}

	public int getDrawableId(String ressourceName){
		return context.getResources().getIdentifier(ressourceName, "drawable", context.getPackageName());
	}

	public static String getMarkerNameFromColor(String color){
		String result = "gris";
		if(!Fonctions.isEmptyOrNull(color)){
			if(color.equals(ROUGE))
				result = "rouge";
			else if(color.equals(BLEU))
				result = "bleu";
			else if(color.equals(ORANGE))
				result = "orange";
			else if(color.equals(GRIS))
				result = "gris";
			else if(color.equals(VERT))
				result = "vert";
		}

		return result;
	}


	public Drawable createColoredMarker(Drawable grayscaleMarker, String color, String statut, String dejaVu){
		Drawable result = context.getResources().getDrawable(R.drawable.default_marker);
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
			if(dejaVu != null && dejaVu.equals("1"))
				filter = new PorterDuffColorFilter(Color.parseColor(BLEU), PorterDuff.Mode.MULTIPLY);
				
			p.setColorFilter(filter);

			// extract the bitmap of the drawable
			Bitmap grayscaleBitmap = ((BitmapDrawable)grayscaleMarker).getBitmap();

			// Told the Canvas to draw with the Colored Paint the grayscaleBitmap into the newBitmap
			c.drawBitmap(grayscaleBitmap, 0, 0, p);
			
			
			// Superposition d'un drawable
			if(statut != null && !statut.equals("")){
				Paint p2 = new Paint();
				//ColorFilter filter2 = new PorterDuffColorFilter(Color.parseColor("#ff0000"), PorterDuff.Mode.MULTIPLY);
				//p2.setColorFilter(filter2);
				if(statut.equals("C"))
					c.drawBitmap(((BitmapDrawable)context.getResources().getDrawable(R.drawable.concurrent)).getBitmap(),0, 0, p2);
				else if(statut.equals("S"))
					c.drawBitmap(((BitmapDrawable)context.getResources().getDrawable(R.drawable.pasinteresse)).getBitmap(),0, 0, p2);
				if(statut.equals("F"))
					c.drawBitmap(((BitmapDrawable)context.getResources().getDrawable(R.drawable.ferme)).getBitmap(),0, 0, p2);
			}
			
			/*
			// Superposition d'un texte
			if(statut != null && !statut.equals("")){
				Paint p3 = new Paint();
				if(statut.equals("F"))
					p3.setColor(Color.parseColor("#d92e00"));
				else if(statut.equals("C"))
					p3.setColor(Color.parseColor("#003500"));
				else if(statut.equals("S"))
					p3.setColor(Color.parseColor("#0f1279"));
				c.drawText(statut, grayscaleMarker.getIntrinsicWidth()/2-10, grayscaleMarker.getIntrinsicHeight()/2-10, p3);
			}
			*/
			
			
			result = new BitmapDrawable(context.getResources(),newBitmap);

			// write the newBitmap onto the SD Card
			//			FileOutputStream out = new FileOutputStream( Environment.getExternalStorageDirectory() +File.separator+ExternalStorage.MARKER_FOLDER+File.separator+fileName);
			//			newBitmap.compress(Bitmap.CompressFormat.PNG, 90, out);

		} catch (Exception e) {
			Debug.StackTrace(e);
		}

		result.mutate();
		return result;

	}
}
