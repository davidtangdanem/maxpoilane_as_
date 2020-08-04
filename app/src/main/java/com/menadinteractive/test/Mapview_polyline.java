package com.menadinteractive.test;

import java.util.List;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.os.Bundle;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;
import com.menadinteractive.maxpoilane.BaseActivity;
import com.menadinteractive.maxpoilane.Debug;
import com.menadinteractive.maxpoilane.R;

public class Mapview_polyline extends BaseActivity {

	MapView mapView;
	List<Overlay> mapOverlays;
	Projection projection;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Debug.Log("onCreate");


		if (com.menadinteractive.maxpoilane.BuildConfig.DEBUG) {
			setContentView(R.layout.activity_carto_polyline_debug);
		} else {
			setContentView(R.layout.activity_carto_polyline_debug);
		}
		
		mapView = (MapView) findViewById(R.id.mv_carto);
	    mapView.setBuiltInZoomControls(true);
	    
	    mapOverlays = mapView.getOverlays();        
	    projection = mapView.getProjection();
	    mapOverlays.add(new MyOverlay());  
	}
	
	@Override
	protected boolean isRouteDisplayed() {
	    return false;
	}
	
	class MyOverlay extends Overlay{

	    public MyOverlay(){

	    }   

	    public void draw(Canvas canvas, MapView mapv, boolean shadow){
	        super.draw(canvas, mapv, shadow);

	        Paint   mPaint = new Paint();
	        mPaint.setDither(true);
	        mPaint.setColor(Color.RED);
	        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
	        mPaint.setStrokeJoin(Paint.Join.ROUND);
	        mPaint.setStrokeCap(Paint.Cap.ROUND);
	        mPaint.setStrokeWidth(5);
	       

	    //    GeoPoint gP1 = new GeoPoint(19240000,-99120000);
	    //    GeoPoint gP2 = new GeoPoint(37423157, -122085008);

	        GeoPoint gPT1_1 = new GeoPoint(49421790,2821400);
	        GeoPoint gPT1_2 = new GeoPoint(49421870,2821570);
	        
	        GeoPoint gPT2_1 = new GeoPoint(49421160,2819380);
	        GeoPoint gPT2_2 = new GeoPoint(49421740,2821010);
	        GeoPoint gPT2_3 = new GeoPoint(49421790,2821400);
	        
	        GeoPoint gPT3_1 = new GeoPoint(49420440,2819270);
	        GeoPoint gPT3_2 = new GeoPoint(49420700,2819070);
	        GeoPoint gPT3_3 = new GeoPoint(49420860,2819060);
	        GeoPoint gPT3_4 = new GeoPoint(49420990,2819260);
	        GeoPoint gPT3_5 = new GeoPoint(49421470,2820650);
	        GeoPoint gPT3_6 = new GeoPoint(49421790,2821400);
	 /*       
	        Point pT1_1 = new Point();
	        Point pT1_2 = new Point();
	        Path path = new Path();

	        projection.toPixels(gPT1_1, pT1_1);
	        projection.toPixels(gPT1_2, pT1_2);

	        path.moveTo(pT1_2.x, pT1_2.y);
	        path.lineTo(pT1_1.x,pT1_1.y);

	        canvas.drawPath(path, mPaint);
	        */
	        drawBetween(gPT1_1,gPT1_2,canvas,mPaint);
	        
	        drawBetween(gPT2_1,gPT2_2,canvas,mPaint);
	        drawBetween(gPT2_2,gPT2_3,canvas,mPaint);
	        
	        drawBetween(gPT3_1,gPT3_2,canvas,mPaint);
	        drawBetween(gPT3_2,gPT3_3,canvas,mPaint);
	        drawBetween(gPT3_3,gPT3_4,canvas,mPaint);
	        drawBetween(gPT3_4,gPT3_5,canvas,mPaint);
	        drawBetween(gPT3_5,gPT3_6,canvas,mPaint);

	    }
	    
	    void drawBetween(GeoPoint from, GeoPoint to, Canvas canvas, Paint mPaint)
	    {
	    	 	Point pT1_1 = new Point();
		        Point pT1_2 = new Point();
		        Path path = new Path();

		        projection.toPixels(from, pT1_1);
		        projection.toPixels(to, pT1_2);

		        path.moveTo(pT1_2.x, pT1_2.y);
		        path.lineTo(pT1_1.x,pT1_1.y);
		        
		        
		        drawPolylineArrow(canvas,mPaint, pT1_1.x,pT1_1.y,pT1_2.x,pT1_2.y,20,10 );
		        canvas.drawPath(path, mPaint);
		        
		        
	    }
	
        
	    private void drawPolylineArrow(Canvas g,Paint mPaint, int x1, int y1, int x2, int y2, int headLength, int headwidth){
	    	 
			double theta1;
			//calculate the length of the line - convert from Object to Integer to int value
			
 
			int deltaX = (x2-x1);
			int deltaY = (y2-y1);
 
 
			double theta  = Math.atan((double)(deltaY)/(double)(deltaX));
 
			if (deltaX < 0.0){
				theta1 = theta+Math.PI;  //If theta is negative make it positive
			}
			else{
				theta1 = theta;  //else leave it alone
			}
 
			int lengthdeltaX =- (int)(Math.cos(theta1)*headLength);
			int lengthdeltaY =- (int)(Math.sin(theta1)*headLength);
			int widthdeltaX  =  (int)(Math.sin(theta1)*headwidth);
			int widthdeltaY  =  (int)(Math.cos(theta1)*headwidth);
 
			g.drawLine(x2,y2,x2+lengthdeltaX+widthdeltaX,y2+lengthdeltaY-widthdeltaY,mPaint);
			g.drawLine(x2,y2,x2+lengthdeltaX-widthdeltaX,y2+lengthdeltaY+widthdeltaY,mPaint);
			Path path=new Path();
			path.moveTo(x1,y1);
		    path.lineTo(x2,y2);
	//		g.drawPolyline(xPoints, yPoints, xPoints.length);
	//		g.drawLine(x2,y2,x2+lengthdeltaX+widthdeltaX,y2+lengthdeltaY-widthdeltaY);
	//		g.drawLine(x2,y2,x2+lengthdeltaX-widthdeltaX,y2+lengthdeltaY+widthdeltaY);
 
	}//end drawPolylineArrow
	    double calculateBearing(long A,long B) 
	    { 
	        double lat1 = DegtoRad(A); 
	        long lon1 = A; 
	        double lat2 = DegtoRad(B); 
	        long lon2 = B; 
	        double dLon = DegtoRad(lon2-lon1); 
	        double y = Math.sin(dLon) * Math.cos(lat2); 
	        double d1=(Math.cos(lat1)*Math.sin(lat2));
	        double x = d1  - (Math.sin(lat1)*Math.cos(lat2)*Math.cos(dLon)); 
	        double brng = (RadtoDeg(Math.atan2(y, x))+360)%360; 
	        return brng; 
	    } 
	    double DegtoRad(long x) 
	    { 
	        return (double)(x*Math.PI/180); 
	    } 
	    
	    double RadtoDeg(double x) 
	    { 
	        return x*180/Math.PI; 
	    } 
	}
}
