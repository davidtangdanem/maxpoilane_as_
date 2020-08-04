package com.menadinteractive.negos.media;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.menadinteractive.maxpoilane.R;
import com.menadinteractive.segafredo.communs.Global;

public class MediaAdapter  extends ArrayAdapter<File>{

	private LayoutInflater mInflater;
	Context context;
	private Bitmap defaultBitmap;

	static class Holder{
		TextView tv;
		ImageView iv;
	}

	public MediaAdapter(Context context, int textViewResourceId,
			List<File> objects){
		super(context,0, objects);
		this.context = context;
		this.mInflater = LayoutInflater.from(context);
		this.defaultBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.addevent48);
	}
	
	public void setDefaultBitmap(Bitmap b){
		this.defaultBitmap = b;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final File model = getItem(position);
		Holder holder;

		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.itec_item_media, parent, false);
			holder = new Holder();
			holder.tv = (TextView) convertView.findViewById(R.id.tv);
			holder.iv = (ImageView) convertView.findViewById(R.id.iv);
			convertView.setTag(holder);
		}
		else {
			holder = (Holder)convertView.getTag();
		}
		
		holder.iv.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
		holder.iv.setImageBitmap(ItecMediaFile.decodeFile(model, defaultBitmap, 300));
		
		File f=new File(model.getAbsolutePath());
		 long size=f.length();
		 if (size>Global.maxMediaFileSize)
		 {
			 holder.tv.setText("TROP VOLUMINEUX, NE SERA PAS TRANSMIS, CHANGEZ LA RESOLUTION DANS L'APPLICATION CAMERA");
			 
		 }
		 else
		 {
			
			
			holder.tv.setText(getDD_MM_YYYY_MM_SS(model.lastModified()));
		 }
			

		return convertView;
	}

	static public String getDD_MM_YYYY_MM_SS(long timeStamp_ms)
	{
		final Date d = new Date(timeStamp_ms);
		Calendar c = Calendar.getInstance( );
		c.setTime(d);
		
		
		

		int mYear = c.get(Calendar.YEAR); 
		int mMonth = c.get(Calendar.MONTH)+1; 
		int mDay = c.get(Calendar.DAY_OF_MONTH);
		int mHour  = c.get(Calendar.HOUR_OF_DAY);
		int mMinute  = c.get(Calendar.MINUTE);
		

		String day=String.format("%02d",mDay );
		String month=String.format("%02d",mMonth);
		String year=String.format("%04d",mYear);
		String hour=String.format("%02d",mHour);
		String minute=String.format("%02d",mMinute);

		String heure=day+"/"+month+"/"+year+" "+hour+":"+minute ;

		return heure;



	}

}
