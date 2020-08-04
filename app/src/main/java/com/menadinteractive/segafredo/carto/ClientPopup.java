package com.menadinteractive.segafredo.carto;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.menadinteractive.maxpoilane.BaseActivity;
import com.menadinteractive.maxpoilane.R;
import com.menadinteractive.segafredo.client.ficheclient;
import com.menadinteractive.segafredo.communs.Fonctions;
import com.menadinteractive.segafredo.db.TableClient;
import com.menadinteractive.segafredo.db.TableClient.structClient;
import com.menadinteractive.segafredo.findcli.MenuCliActivity;

public class ClientPopup  implements OnClickListener{
	/** GUI */
	View popup;
	TextView tv_raisonsociale;
	Button b_close;
	TextView tv_address1;
	TextView tv_address2;
	TextView tv_cp;
	TextView tv_ville;
	TextView tv_codeclient;
	TextView tv_pays;
	Button b_call;
	Button b_mail;
	Button b_direction;
	Button b_select;

	/** */
	Context context;
	structClient structClient;
	Handler handler;

	public ClientPopup(Context context, Handler handler, structClient structClient){
		this.context = context;
		this.handler = handler;
		initGUI();
		popup.setVisibility(View.GONE);

		if(structClient != null)
			updatePopup(structClient, false);
		initListeners();
	}

	private void initGUI(){
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		popup = inflater.inflate(R.layout.carto_popup, null);
		tv_raisonsociale = (TextView) popup.findViewById(R.id.tv_raisonsociale);
		b_close = (Button) popup.findViewById(R.id.b_close);
		tv_address1 = (TextView) popup.findViewById(R.id.tv_address1);
		tv_address2 = (TextView) popup.findViewById(R.id.tv_address2);
		tv_cp = (TextView) popup.findViewById(R.id.tv_cp);
		tv_ville = (TextView) popup.findViewById(R.id.tv_ville);
		tv_codeclient = (TextView) popup.findViewById(R.id.tv_codeclient);
		tv_pays = (TextView) popup.findViewById(R.id.tv_pays);
		b_call = (Button) popup.findViewById(R.id.b_call);
		b_mail = (Button) popup.findViewById(R.id.b_mail);
		b_direction = (Button) popup.findViewById(R.id.b_direction);
		b_select = (Button) popup.findViewById(R.id.b_select);
	}

	public void updatePopup(structClient structClient1, boolean display){
		if(structClient1 != null){
			this.structClient = structClient1;
			tv_raisonsociale.setText(structClient.ENSEIGNE);
			tv_codeclient.setText(structClient.CODE);
			tv_address1.setText(structClient.ADR1);
			tv_address2.setText(structClient.ADR2);
			tv_cp.setText(structClient.CP);
			tv_ville.setText(structClient.VILLE);
			tv_pays.setText("FRANCE");

			if(display)
				setVisibility(View.VISIBLE);
		}
	}

	private void initListeners(){
		b_close.setOnClickListener(this);
		b_call.setOnClickListener(this);
		b_mail.setOnClickListener(this);
		b_direction.setOnClickListener(this);
		b_select.setOnClickListener(this);

	}

	public void setVisibility(int visibility){
		if(visibility == View.VISIBLE || visibility == View.GONE || visibility == View.INVISIBLE)
			popup.setVisibility(visibility);
	}

	public View getView(){
		return popup;
	}
	@Override
	public void onClick(View v) {
		if(v == b_close){
			setVisibility(View.GONE);
		}
		else if(v == b_call){
			Intent messIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+Fonctions.GetStringDanem(structClient.TEL1)));
			context.startActivity(messIntent);
		}
		else if(v == b_mail){
			//			Fonctions.sendEmailMsg(context,Fonctions.GetStringDanem(structClient.ADR_EMAIL),"","");
			if(handler != null){
				Message msg = new Message();
				msg.what = CartoMapActivity.MESSAGE_SHOW_MENU_POPUP;
				Bundle b = new Bundle();
				b.putString(TableClient.FIELD_CODE, structClient.CODE);
				msg.setData(b);
				handler.sendMessage(msg);
			}
		}
		else if(v == b_direction){
			String street = Fonctions.GetStringDanem(structClient.ADR1) + 
					" " + Fonctions.GetStringDanem(structClient.CP) + 
					" " + Fonctions.GetStringDanem(structClient.VILLE);

			if(Fonctions.GetStringDanem(structClient.ADR1) == "") street = Fonctions.GetStringDanem(structClient.ADR2) + 
					" " + Fonctions.GetStringDanem(structClient.CP) + 
					" " + Fonctions.GetStringDanem(structClient.VILLE);
			Intent intent = new Intent(Intent.ACTION_VIEW,
					Uri.parse("google.navigation:q=" + street));
			context.startActivity(intent);
		}
		else if(v == b_select){
			if(handler != null){
			/*	Message msg = new Message();
				msg.what = CartoMapActivity.MESSAGE_SELECT_CLIENT_BACK_NEGOS;
				Bundle b = new Bundle();
				b.putString(Espresso.CodeClient, structClient.CODE);
				msg.setData(b);
				handler.sendMessage(msg);
				*/
				launchFichecli(structClient.CODE);
				//launchMenuCli(structClient.CODE);
			}




		}

	}
	public void launchMenuCli(String codecli )
	{
		Intent intent = new Intent(context,		MenuCliActivity.class);
		Bundle b=new Bundle();
		b.putString("codecli",codecli);

		intent.putExtras(b);
		context.startActivity(intent);
	}
	void launchFichecli(String codecli)
	{
		Intent intent = new Intent(context,		ficheclient.class);
		Bundle b=new Bundle();
		b.putString("numProspect",codecli);

		intent.putExtras(b);
		context.startActivity(intent);
	}


}
