package com.menadinteractive.segafredo.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.menadinteractive.maxpoilane.R;
import com.menadinteractive.segafredo.communs.Fonctions;
import com.menadinteractive.segafredo.encaissement.Encaissement;

public class EncaissementAdapter extends ArrayAdapter<Encaissement>{

	Context mContext;
	boolean isScreenFacture;
	ArrayList<Encaissement> encaissements;

	public EncaissementAdapter(Context context, int textViewResourceId,
			ArrayList<Encaissement> _encaissements, boolean _isScreenFacture) {
		super(context, textViewResourceId, _encaissements);
		mContext = context;
		encaissements = _encaissements;
		isScreenFacture = _isScreenFacture;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.item_list_encaissement, parent, false);

		//selon si la position est pair ou impair on change la courleur de l'item
		if(position % 2 == 0) rowView.setBackgroundColor(mContext.getResources().getColor(R.color.list_pair));
		else rowView.setBackgroundColor(mContext.getResources().getColor(R.color.list_impair));

		TextView tvMontantEncaissement = (TextView) rowView.findViewById(R.id.tvMontantEncaissement);
		TextView tvTypePaiement = (TextView) rowView.findViewById(R.id.tvTypePaiement);
		TextView tvDateEncaissement = (TextView) rowView.findViewById(R.id.tvDateEncaissement);
		View vSquare = (View) rowView.findViewById(R.id.vSquare);

		final Encaissement currentEnc = encaissements.get(position);

		tvMontantEncaissement.setText(Fonctions.GetFloatToStringFormatDanem(currentEnc.getMontant(), "0.00"));
		tvTypePaiement.setText(currentEnc.getTypePaiement());

		if(currentEnc.getTypePaiement() != null && currentEnc.getTypePaiement().equals(Encaissement.TYPE_CHEQUE)){
			tvDateEncaissement.setText(Fonctions.getDD_MM_YYYY(currentEnc.getDateCheque()));
		}else{
			tvDateEncaissement.setText(Fonctions.getDD_MM_YYYY(currentEnc.getDate()));
		}



		if(isScreenFacture){
			//on affiche la pastille de couleur
			if(currentEnc.getColor() != null && !currentEnc.getColor().equals("")) {
				GradientDrawable bgShape = (GradientDrawable)vSquare.getBackground();
				bgShape.setColor(Color.parseColor(currentEnc.getColor()));
			}
		}else{
			vSquare.setVisibility(View.GONE);
		}

		rowView.setTag(currentEnc.getIdentifiant());

		return rowView;

	}

}
