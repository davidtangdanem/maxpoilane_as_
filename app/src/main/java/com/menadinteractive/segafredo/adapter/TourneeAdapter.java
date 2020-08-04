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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.menadinteractive.maxpoilane.R;
import com.menadinteractive.segafredo.carto.Marker;
import com.menadinteractive.segafredo.communs.Fonctions;
import com.menadinteractive.segafredo.db.TableClient.structClient;
import com.menadinteractive.segafredo.db.dbKD100Visite.structVisite;

public class TourneeAdapter extends ArrayAdapter<Log>{


	static class Holder{
		TextView text1;
		ImageView iv1;
		LinearLayout ll_ferme;
		LinearLayout ll_ouvert;
		TextView tv_closingHour;
		RelativeLayout rl_root;
		TextView tv_openingHour;
	}

	Context context;
	ArrayList<Bundle> data;
	private LayoutInflater mInflater;
	Marker marker;


	public TourneeAdapter(Context context, int textViewResourceId,
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
			convertView = mInflater.inflate(R.layout.item_list_tournee, null);
			holder = new Holder();
			holder.text1 = (TextView) convertView.findViewById(R.id.text1);
			holder.iv1 = (ImageView) convertView.findViewById(R.id.iv1);
			
			holder.ll_ferme = (LinearLayout) convertView.findViewById(R.id.ll_ferme);
			holder.ll_ouvert = (LinearLayout) convertView.findViewById(R.id.ll_ouvert);
			holder.tv_closingHour = (TextView) convertView.findViewById(R.id.tv_closingHour);
			holder.tv_openingHour = (TextView) convertView.findViewById(R.id.tv_openingHour);
			holder.rl_root = (RelativeLayout) convertView.findViewById(R.id.rl_root);
			convertView.setTag(holder);
		}
		else {
			holder = (Holder)convertView.getTag();
		}
		structClient client = (structClient) model.getClient();
		structVisite visite = (structVisite) model.getVisite();
		String distance = model.getDistance();
		holder.text1.setText(client.ENSEIGNE+" "+context.getString(R.string.a)+" "+Fonctions.padDistance(context, distance));
//		holder.text1.setText(client.NOM+" "+context.getString(R.string.a)+" "+model.getfDistance());
		holder.iv1.setBackgroundDrawable(marker.getMarker(client.ICON, client.COULEUR, client.IMPORTANCE, client.STATUT, visite.DEJA_VU));
		

		if(model.isOpened()){
			holder.ll_ouvert.setVisibility(View.VISIBLE);
			holder.ll_ferme.setVisibility(View.GONE);
			holder.tv_closingHour.setText(model.getHoraire().getClosingHour(context));
			holder.tv_openingHour.setText("-");
		}
		else{
			holder.ll_ouvert.setVisibility(View.GONE);
			holder.ll_ferme.setVisibility(View.VISIBLE);
			holder.tv_closingHour.setText("-");
			holder.tv_openingHour.setText("( "+model.getHoraire().getOpeningHour(context)+" )");
		}
		
		//modif temporaire mv
		holder.ll_ferme.setVisibility(View.GONE);
		holder.ll_ouvert.setVisibility(View.GONE);
		
		if(model.isDejaVisite())
			holder.rl_root.setBackgroundColor(context.getResources().getColor(android.R.color.holo_red_light));
//		holder.rl_root.setBackgroundResource(R.drawable.action_call);
		else
			holder.rl_root.setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
//			holder.rl_root.setBackgroundResource(R.drawable.action_map);
		return convertView;

	}


}
