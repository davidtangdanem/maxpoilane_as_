package com.menadinteractive.segafredo.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
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

public class TourneeOnDemandAdapter  extends ArrayAdapter<Log>{


	static class Holder{
		TextView text1;
		TextView text2;
		ImageView iv1;
		TextView tv_ville;
		TextView tv_tel;
		TextView tv_stock_mini;
		TextView tv_consoparsemaine;
		TextView tv_datelastvis_et_stock;
		TextView tv_consodepuisdervis;
		TextView tv_mntht;
	}

	Context context;
	ArrayList<Bundle> data;
	private LayoutInflater mInflater;
	Marker marker;


	public TourneeOnDemandAdapter(Context context, int textViewResourceId,
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
			convertView = mInflater.inflate(R.layout.item_tournee_ondemand, null);
			holder = new Holder();
			holder.text1 = (TextView) convertView.findViewById(R.id.text1);
			holder.iv1 = (ImageView) convertView.findViewById(R.id.iv1);
			holder.text2 = (TextView) convertView.findViewById(R.id.text2);
			holder.tv_ville = (TextView) convertView.findViewById(R.id.tv_ville);
			holder.tv_tel = (TextView) convertView.findViewById(R.id.tv_tel);
			holder.tv_stock_mini = (TextView) convertView.findViewById(R.id.tv_stock_mini);
			holder.tv_consoparsemaine = (TextView) convertView.findViewById(R.id.tv_consoparsemaine);
			holder.tv_datelastvis_et_stock = (TextView) convertView.findViewById(R.id.tv_datelastvis_et_stock);
			holder.tv_consodepuisdervis = (TextView) convertView.findViewById(R.id.tv_consodepuisdervis);
			holder.tv_mntht = (TextView) convertView.findViewById(R.id.tv_mntht);
			
			convertView.setTag(holder);
		}
		else {
			holder = (Holder)convertView.getTag();
		}
		structClient client = (structClient) model.getClient();
		structVisite visite = (structVisite) model.getVisite();
		holder.text1.setText(client.ENSEIGNE);
		holder.iv1.setBackgroundDrawable(marker.getMarker(client.ICON, client.COULEUR, client.IMPORTANCE, client.STATUT, visite.DEJA_VU));
		int stockEstime = 0;
		try{
			stockEstime = Integer.valueOf(model.getTournee().stock_mini)+ Integer.valueOf(model.getTournee().deltaAvecStockMini);
		}
		catch(Exception e){
			
		}
		holder.text2.setText(context.getString(R.string.Stock_esime)+" : "+String.valueOf(stockEstime));
		holder.text2.setTextColor(Color.parseColor(client.COULEUR));
		holder.tv_ville.setText(context.getString(R.string.Ville)+" : "+client.VILLE);
		holder.tv_tel.setText(context.getString(R.string.Tel)+" : "+client.TEL1);
		holder.tv_stock_mini.setText(context.getString(R.string.Stock_mini)+" : "+model.getTournee().stock_mini);
		holder.tv_consoparsemaine.setText(context.getString(R.string.Consommation_par_semaine)+" : "+model.getTournee().consoparsemaine);
		holder.tv_datelastvis_et_stock.setText(context.getString(R.string.derniere_visite)+": "+Fonctions.getDD_MM_YYYY(model.getTournee().datelastvis)+" ,"+context.getString(R.string.stock_de)+": "+model.getTournee().stock_lastvis);
		holder.tv_consodepuisdervis.setText(context.getString(R.string.Consommation_depuis_derniere_visite)+" : "+model.getTournee().consodepuisdervis);
		holder.tv_mntht.setText(context.getString(R.string.mntht)+" : "+model.getTournee().mntht);
	
		return convertView;

	}


}
