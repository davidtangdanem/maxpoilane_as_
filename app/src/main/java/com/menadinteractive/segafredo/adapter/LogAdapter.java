package com.menadinteractive.segafredo.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.menadinteractive.maxpoilane.R;
import com.menadinteractive.segafredo.carto.Marker;
import com.menadinteractive.segafredo.communs.Fonctions;
import com.menadinteractive.segafredo.db.TableClient.structClient;
import com.menadinteractive.segafredo.db.dbKD100Visite.structVisite;

public class LogAdapter extends ArrayAdapter<Log>{


	static class Holder{
		TextView text1;
		ImageView iv1;
	}

	Context context;
	ArrayList<Bundle> data;
	private LayoutInflater mInflater;
	Marker marker;


	public LogAdapter(Context context, int textViewResourceId,
			List<Log> objects) {
		super(context,0, objects);
		this.context = context;
		this.mInflater = LayoutInflater.from(context);
		this.marker = new Marker(context);
	}


	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final Log model = (Log)getItem(position);
		Holder holder;

		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.item_list, null);
			holder = new Holder();
			holder.text1 = (TextView) convertView.findViewById(R.id.text1);
			holder.iv1 = (ImageView) convertView.findViewById(R.id.iv1);
			convertView.setTag(holder);
		}
		else {
			holder = (Holder)convertView.getTag();
		}
		structClient client = (structClient) model.getClient();
		structVisite visite = (structVisite) model.getVisite();
		String distance = model.getDistance();
		holder.text1.setText(client.ENSEIGNE+" "+context.getString(R.string.a)+" "+Fonctions.padDistance(context, distance));
		holder.iv1.setBackgroundDrawable(marker.getMarker(client.ICON, client.COULEUR, client.IMPORTANCE, client.STATUT, visite.DEJA_VU));
		return convertView;

	}


}
