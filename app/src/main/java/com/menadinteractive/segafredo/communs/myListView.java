package com.menadinteractive.segafredo.communs;

import java.util.ArrayList;

import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.menadinteractive.segafredo.carto.Marker;

public class myListView extends ListView  {

	public static int TYPE_TEXTVIEW=0;
	public static int TYPE_IMGVIEW=1;
	public static int TYPE_TEXTVIEW_FLOAT2=2;
	
	Handler m_handler;
	private listAdapter m_adapter;
	Context m_context;
	ArrayList<assoc> m_assoc;
	int m_layout;
	ArrayList<Bundle> m_colNames;
	Typeface faceRegular,faceBold;
	
	public myListView(Context context) {
		super(context);
		
		m_context=context;
		init();
	}
 
	public myListView(Context context, AttributeSet attrs, int defStyle) {
	    super(context, attrs, defStyle);
	    m_context=context;
	    init();
	}

	public myListView(Context context, AttributeSet attrs) {
	    super(context, attrs);
	    m_context=context;
	    init();
	}
	void init()
	{
		  faceBold=Typeface.createFromAsset(m_context.getAssets(), Global.FONT_BOLD);
		  faceRegular=Typeface.createFromAsset(m_context.getAssets(), Global.FONT_REGULAR);
		  
		//MV 27/03/2015, semble ameliorer le scroll
		 setCacheColorHint(Color.TRANSPARENT); // not sure if this is required for you. 
		 setFastScrollEnabled(true);
		 setScrollingCacheEnabled(false);
	}
	
	public void attachValues(int layout,ArrayList<Bundle> b,ArrayList<assoc> ass,Handler h)
	{
		m_handler=h;
		m_assoc=ass;
		m_layout=layout;
		m_colNames=b;
		this.m_adapter = new listAdapter(m_context, m_layout, m_colNames);
		
		
		 m_colNames= new ArrayList<Bundle>();


		 setAdapter(this.m_adapter);
		  setOnItemClickListener(new ListSelection());
	}
	 
	 
	 
	private class listAdapter extends ArrayAdapter<Bundle> {

		private ArrayList<Bundle> items;

		public listAdapter(Context context, int textViewResourceId,
				ArrayList<Bundle> items) {
			super(m_context, textViewResourceId, items);
			this.items = items;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			if (v == null) {
				LayoutInflater vi = (LayoutInflater) m_context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(m_layout, null);
			}

			Bundle o = items.get(position);

			
			if (o != null) {
				for (int i=0;i<m_assoc.size();i++)
				{
					if (m_assoc.get(i).typeCtrl==TYPE_TEXTVIEW || m_assoc.get(i).typeCtrl==TYPE_TEXTVIEW_FLOAT2)
					{
						TextView t1=(TextView) v.findViewById(m_assoc.get(i).idCtrl);
						String h=m_assoc.get(i).bkgColor;
						String c=Fonctions.getBundleValue(o,h);
						if (c.equals(""))
							t1.setBackgroundColor(Color.TRANSPARENT);
						else
						{
							String color=Marker.correspondanceColor(c);
							t1.setBackgroundColor(Color.parseColor(color));
						}
							
						if(t1.getTypeface()!=null){
							   if(t1.getTypeface().getStyle()==Typeface.BOLD || t1.getTypeface().getStyle()==Typeface.ITALIC){
								   t1.setTypeface(faceBold);
							   }
							   else
								   t1.setTypeface(faceRegular);
							}
						else
						{
							t1.setTypeface(faceRegular);
						}
						
						if (m_assoc.get(i).typeCtrl==TYPE_TEXTVIEW)
							t1.setText(o.getString(m_assoc.get(i).dbFld));
						if (m_assoc.get(i).typeCtrl==TYPE_TEXTVIEW_FLOAT2)
						{
							float f=Fonctions.convertToFloat(o.getString(m_assoc.get(i).dbFld));
							String s=Fonctions.GetDoubleToStringFormatDanem(f,"0.00");
							t1.setText(s);	
						}
						if (o.getString(m_assoc.get(i).dbFld) != null && o.getString(m_assoc.get(i).dbFld).equals("") && m_assoc.get(i).goneifempty==true)
						{
							t1.setVisibility(View.GONE);
						}
						else
							t1.setVisibility(View.VISIBLE);
							
					}
					if (m_assoc.get(i).typeCtrl==TYPE_IMGVIEW)
					{
						try {
							ImageView imageView = (ImageView) v.findViewById(m_assoc.get(i).idCtrl);
						//	imageView.setBackgroundColor(Color.GREEN);
							if (m_assoc.get(i).dbFld!=null && m_assoc.get(i).dbFld.equals("")==false)
							{
							   String uri = "drawable/"+o.getString(m_assoc.get(i).dbFld);

							    // int imageResource = R.drawable.icon;
							    int imageResource = getResources().getIdentifier(uri, null, m_context.getPackageName());

							    imageView.setVisibility(View.VISIBLE);
							    try {
									Drawable image = getResources().getDrawable(imageResource);
									imageView.setImageDrawable(image);
									
									if (m_assoc.get(i).bkgColor.equals(""))
										imageView.setBackgroundColor(Color.TRANSPARENT);
									else
									{
										String h=m_assoc.get(i).bkgColor;
										String c=o.getString(h);
										if (c.equals(""))
											imageView.setBackgroundColor(Color.TRANSPARENT);
										else
										{
											String color=Marker.correspondanceColor(c);
											imageView.setBackgroundColor(Color.parseColor(color));
										}
									}
								} catch (Exception e) {
									imageView.setVisibility(View.INVISIBLE);
									e.printStackTrace();
								}
							    
							}
							else
							{
								imageView.setVisibility(View.INVISIBLE);
							}
							Bundle b=new Bundle();
							b.putInt("position", position);
							b.putInt("id", i);
							imageView.setTag(b);
							
							imageView.setOnClickListener(new OnClickListener() {
								
								@Override
								public void onClick(View v) {
									Bundle b=(Bundle) v.getTag();
									
									int i=b.getInt("id");
 
									Message m=new Message();
									m.what=m_assoc.get(i).idCtrl;
							  
									m.setData(b);
									m_handler.sendMessage(m);
									
								}
							});
						} catch (NotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				 
			}
			return v;
		}

	}


	private class ListSelection implements OnItemClickListener
	 {
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Bundle b=new Bundle();
		b.putInt("position", position);

		Message m=new Message();
		m.what=parent.getId();
  
		m.setData(b);
		m_handler.sendMessage(m);
		
		
	}
	 }
	
	
}
