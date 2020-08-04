package com.menadinteractive.segafredo.carto;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

import com.google.android.maps.MapView;
import com.menadinteractive.maxpoilane.Debug;

public class CartoMapView extends MapView{

	public CartoMapView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	public CartoMapView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CartoMapView(Context context, String apiKey) {
		super(context, apiKey);
	}

	int currentZoomLevel=1;

	@Override
	public void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
		if (getZoomLevel() != currentZoomLevel) {

			currentZoomLevel = getZoomLevel();
			Debug.Log("ZOOOMED : "+currentZoomLevel);
		}
	}


	public int getRadiusOfMapViewInMeter(){
		/* level 20  (zoom max) => 220m
		 * level 19 => 440 m
		 * ...
		 */
		//		int result = 220;
		int zoomLevel = getZoomLevel();
		/*
		if(zoomLevel < 12)
			zoomLevel = 12;
		 */

		//		Debug.Log("radius : "+getMeterFromLevel(zoomLevel)/2 +" for level "+zoomLevel);
//		return getMeterFromLevel(zoomLevel)/3;
		Debug.Log("radius : "+getMeterFromLevel(zoomLevel));
		// On divise par une valeur arbitraire la distance calculée car cette distance calculée est expérimentale et on ne prend pas en compte ni la courbure de la Terre
		// ni la position à partir de laquelle on fait le calcul
		return getMeterFromLevel(zoomLevel)/2;
	}

	private int getMeterFromLevel(int level){
		int result = 220;
		for(int i = 20; i>level; i--){
			result = result *2;
		}
		return result;
	}

	/**
	 * Déterminer la distance entre 2 points sélectionnés sur la carte !
	 * M'a permis de déterminer la distance en mètre de la zone en fonction du zoom
	 * level 20  (zoom max) => 220m
	 * level 19 => 440 m
	 * ...
	 */
	/*
	static boolean isFirstArray = true;
	float[] results = new float[3];
	GeoPoint first;
	GeoPoint second;
	@Override
	public boolean onTouchEvent(MotionEvent event) {

		if (event.getAction() == MotionEvent.ACTION_UP) {
			if(isFirstArray){
				first = getProjection().fromPixels((int) event.getX(),
					(int) event.getY());
				isFirstArray = false;
			}
			else{
				second = getProjection().fromPixels((int) event.getX(),
						(int) event.getY());

				Location.distanceBetween(first.getLatitudeE6()/1E6, first.getLongitudeE6()/1E6, second.getLatitudeE6()/1E6, second.getLongitudeE6()/1E6, results);
				if(results != null && results.length > 0){
					Debug.Log("min distance : "+results[0]+" for zoom : "+currentZoomLevel);
				}
					isFirstArray = true;
			}



		}
		getRadiusOfMapView();
		return super.onTouchEvent(event);
	}
	 */


	/**
	 * 
	 * Long click Listener !!
	 *
	 */
	// http://www.kind-kristiansen.no/2011/android-handling-longpresslongclick-on-map-revisited/



}
