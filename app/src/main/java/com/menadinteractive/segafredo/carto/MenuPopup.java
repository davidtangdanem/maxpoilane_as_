package com.menadinteractive.segafredo.carto;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.menadinteractive.commande.commandeActivity;
import com.menadinteractive.histo.HistoDocumentsActivity;
import com.menadinteractive.maxpoilane.Debug;
import com.menadinteractive.negos.media.ItecMediaActivity;
import com.menadinteractive.negos.media.ItecMediaFile;
import com.menadinteractive.negos.quest.pac_plans_filler;
import com.menadinteractive.maxpoilane.R;
import com.menadinteractive.segafredo.adapter.MenuPopupAdapter;
import com.menadinteractive.segafredo.adapter.MenuPopupItem;
import com.menadinteractive.segafredo.client.ficheclient;
import com.menadinteractive.segafredo.communs.Fonctions;
import com.menadinteractive.segafredo.communs.Global;
import com.menadinteractive.segafredo.db.SQLRequestHelper;
import com.menadinteractive.segafredo.db.TableClient;
import com.menadinteractive.segafredo.db.TableSouches;
import com.menadinteractive.segafredo.db.TableClient.structClient;
import com.menadinteractive.segafredo.db.dbKD100Visite.structVisite;
import com.menadinteractive.segafredo.findcli.MenuCliActivity;
import com.menadinteractive.segafredo.plugins.Espresso;

public class MenuPopup implements OnClickListener, OnItemClickListener{
	/** Constants */
	public static final Long TIMER_PROGRESS_BAR = 10L;

	/** GUI */
	View popup;
	TextView iv_nom;
	ImageView iv_close;
	ImageView iv_stock;
	GridView gridview;
	LinearLayout ll_horaires;
	Horaire lundiHoraire;
	Horaire mardiHoraire;
	Horaire mercrediHoraire;
	Horaire jeudiHoraire;
	Horaire vendrediHoraire;
	Horaire samediHoraire;
	Horaire dimancheHoraire;
	TextView tv_fermetures;
	ProgressBar pb_close;
	LinearLayout ll_stock;
	ListView lv_stock;
	Button b_commande;
	Button b_quest;
	ImageButton ib_center;
	Button b_toggle_ferme;
	Button b_toggle_vu;
	Button b_toggle_commande;
	Button b_toggle_fichecli;
	Button b_toggle_photo;
	
	String m_codeCli;
	String m_socCode;
	
	final int LAUNCH_CDE = 454;

	/** GUI Horaires / Fermeture */
	static class Horaire{
		TextView tv_day;
		TextView am;
		TextView pm;

		public Horaire(View parent, int layoutID){
			init(parent, layoutID);
		}

		public void init(View parent, int layoutID){
			RelativeLayout horaire = (RelativeLayout) parent.findViewById(layoutID);
			tv_day = (TextView) horaire.findViewById(R.id.tv_day);
			am = (TextView) horaire.findViewById(R.id.am);
			pm = (TextView) horaire.findViewById(R.id.pm);
		}

		public void update(String day, String horaireAM, String horairePM){
			tv_day.setText(day);
			am.setText(horaireAM);
			pm.setText(horairePM);
		}
	}

	/** */
	Context context;
	ArrayList<MenuPopupItem> items;
	MenuPopupAdapter adapter;
	structClient structClient;
	structVisite visite = new structVisite();
	ProgressBarTask progressBarTask;
	Handler handler;
	Marker marker;
	String jourPassage ="";

	public MenuPopup(Context context, Handler handler, structClient structClient){
		this.context = context;
		this.handler = handler;
		initGUI();
		initModels();


		if(structClient != null)
		{
			updatePopup(structClient, false);
			m_codeCli=structClient.CODE;
			m_socCode=structClient.SOC_CODE;
		}
		initListeners();

	


	}

	private void initiateProgressBar(){
		if(progressBarTask != null){
			cancelProgressBar();
		}
		progressBarTask = new ProgressBarTask();
		progressBarTask.execute(TIMER_PROGRESS_BAR);
	}

	public void updatePopup(structClient structClient, boolean display){
		if(display)
			setVisibility(View.VISIBLE);
		
		this.structClient = structClient;
		m_codeCli=structClient.CODE;
		m_socCode=structClient.SOC_CODE;
		
		this.visite.DEJA_VU = "";
		Global.dbKDVisite.load(this.visite, structClient.CODE, jourPassage, new StringBuffer());
		
		iv_nom.setText(structClient.ENSEIGNE);
		iv_stock.setBackgroundDrawable(marker.getMarker(structClient.ICON, structClient.COULEUR, structClient.IMPORTANCE, structClient.STATUT, visite.DEJA_VU));

		gridview.setVisibility(View.VISIBLE);
		ll_horaires.setVisibility(View.GONE);
		ll_stock.setVisibility(View.GONE);
		
		
		if(this.visite != null && this.visite.DEJA_VU != null){
			if(this.visite.DEJA_VU.equals("1"))
				b_toggle_vu.setText(R.string.AVoir);
			else
				b_toggle_vu.setText(R.string.Vu);
		}
		
		if(structClient.STATUT != null){
			if(structClient.STATUT.equals("F"))
				b_toggle_ferme.setText(R.string.CestOuvert);
			else
				b_toggle_ferme.setText(R.string.CestFerme);
		}
		
		Debug.Log("requete", this.structClient.toString());
		initiateProgressBar();
	}


	private void initModels(){
		this.marker = new Marker(context);
		jourPassage = SQLRequestHelper.getCodeTournee(Fonctions.getYYYYMMDD());
		items = new ArrayList<MenuPopupItem>();
		items.add(new MenuPopupItem(R.string.Stock_et_commande, R.drawable.basic1_104_database_download));
		items.add(new MenuPopupItem(R.string.Naviguer, R.drawable.menu_nav));
		items.add(new MenuPopupItem(R.string.Appeler, R.drawable.menu_appeler));
		items.add(new MenuPopupItem(R.string.ficheclient, R.drawable.basic2_105_user));
		adapter = new MenuPopupAdapter(this.context, R.layout.item_menu, items);
		gridview.setAdapter(adapter);


	}
	private void initGUI(){
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		popup = inflater.inflate(R.layout.popup, null);
		iv_nom = (TextView) popup.findViewById(R.id.iv_nom);
		iv_close = (ImageView) popup.findViewById(R.id.iv_close);
		iv_stock = (ImageView) popup.findViewById(R.id.iv_stock);
		gridview = (GridView) popup.findViewById(R.id.gridview);
		ll_horaires = (LinearLayout) popup.findViewById(R.id.ll_horaires);
		tv_fermetures = (TextView) popup.findViewById(R.id.tv_fermetures);
		pb_close = (ProgressBar) popup.findViewById(R.id.pb_close);
		b_toggle_ferme = (Button) popup.findViewById(R.id.b_toggle_ferme);
		b_toggle_vu = (Button) popup.findViewById(R.id.b_toggle_vu);
		b_toggle_commande = (Button) popup.findViewById(R.id.b_toggle_commande);
		b_toggle_fichecli = (Button) popup.findViewById(R.id.b_toggle_fichecli);
		b_toggle_photo = (Button) popup.findViewById(R.id.b_toggle_photo);
		
		lundiHoraire 	= new Horaire(popup, R.id.lundi);
		mardiHoraire 	= new Horaire(popup, R.id.mardi);
		mercrediHoraire = new Horaire(popup, R.id.mercredi);
		jeudiHoraire 	= new Horaire(popup, R.id.jeudi);
		vendrediHoraire = new Horaire(popup, R.id.vendredi);
		samediHoraire 	= new Horaire(popup, R.id.samedi);
		dimancheHoraire = new Horaire(popup, R.id.dimanche);


		ll_stock = (LinearLayout) popup.findViewById(R.id.ll_stock);
		lv_stock = (ListView) popup.findViewById(R.id.lv_stock);
		b_commande = (Button) popup.findViewById(R.id.b_commande);
		b_quest = (Button) popup.findViewById(R.id.b_toggle_questions);
		ib_center = (ImageButton) popup.findViewById(R.id.ib_center);
		popup.setVisibility(View.GONE);
	}
	private void initListeners(){
		iv_close.setOnClickListener(this);
		gridview.setOnItemClickListener(this);
		b_commande.setOnClickListener(this);
		b_quest.setOnClickListener(this);
		ib_center.setOnClickListener(this);
		b_toggle_ferme.setOnClickListener(this);
		b_toggle_vu.setOnClickListener(this);
		b_toggle_commande.setOnClickListener(this);
		b_toggle_fichecli.setOnClickListener(this);
		b_toggle_photo.setOnClickListener(this);
	}

	public void setVisibility(int visibility){
		if(visibility == View.VISIBLE || visibility == View.GONE || visibility == View.INVISIBLE)
			popup.setVisibility(visibility);
	}

	public View getView(){
		return popup;
	}

	private void closePopup(){
		cancelProgressBar();
		if(gridview.isShown())
			setVisibility(View.GONE);
		else if(ll_horaires.isShown()){
			initiateProgressBar();
			gridview.setVisibility(View.VISIBLE);
			ll_horaires.setVisibility(View.GONE);
		}
		else if(ll_stock.isShown()){
			initiateProgressBar();
			gridview.setVisibility(View.VISIBLE);
			ll_stock.setVisibility(View.GONE);
		}
	}
	
	@Override
	public void onClick(View v) {
		if(v == iv_close){
			closePopup();

		}
		else if(v == b_commande || v == b_toggle_commande){
			if(handler != null){
				//launchCde();
				launchMenuCli();
			}
		}
		else if(v == b_quest){
			if(handler != null){
				launchQuest(context, m_codeCli, "", "");
			}
		}
		else if( v == b_toggle_fichecli){
			if(handler != null){
				 launchFichecli();
			}
		}
		else if( v == b_toggle_photo){
			if(handler != null){
				 launchMedia();
			}
		}
		else if(v == ib_center){
			if(handler != null){
				Message msg = new Message();
				msg.what = CartoMapActivity.MESSAGE_ANIMATE_TO;
				Bundle b = new Bundle();
				b.putString(TableClient.FIELD_CODE, structClient.CODE);
				msg.setData(b);
				handler.sendMessage(msg);
				closePopup();
			}
		}
		else if(v == b_toggle_ferme){
			if(structClient.STATUT != null && structClient.STATUT.equals("F")){
				structClient.STATUT="I";
			}
			else{
				structClient.STATUT="F";
				CartoMapActivity.updateVisite(structClient, true,context);
			}
			
			
			Global.dbClient.save(structClient, true);
	//		BroadcastIntent.setCodeAlarm(context, structClient.CODE, structClient.STATUT);
			updateUIFull();
			closePopup();
		}
		else if(v == b_toggle_vu){
			CartoMapActivity.updateVisite(structClient, false,context);
			
			updateUIFull();
			closePopup();
		}
		

	}
	static public void launchHisto(Context c,String codecli,String soccode,String numcdeforduplication,String typedocforduplication)
	{
		Intent intent = new Intent(c,		HistoDocumentsActivity.class);
		Bundle b=new Bundle();
		b.putString(Espresso.CODE_CLIENT,codecli);
		b.putString(Espresso.CODE_SOCIETE,soccode);
		b.putString("m_numDocForDuplication",numcdeforduplication);
		b.putString("m_typeDocForDuplication",typedocforduplication);
		intent.putExtras(b);
		c.startActivity (intent);
		
	}
 
	void launchMedia()
	{
		launchMediaActivity("fichecli", m_codeCli,ItecMediaFile.PREFIX_PHOTOCLIENT,context);
	}
	 
	public  void launchMediaActivity(String numOperation, String pattern, String mediaType,Context act){
		 Intent i = new Intent( act, ItecMediaActivity.class);
	 
		 
			Bundle bundle = new Bundle();
			bundle.putString(ItecMediaActivity.EXTRA_ITEC_NUM_OPERATION,numOperation);
			bundle.putString(ItecMediaActivity.EXTRA_ITEC_PATTERN,pattern);
			bundle.putString(ItecMediaActivity.EXTRA_ITEC_MEDIA_TYPE,mediaType);
			bundle.putString("codecli",m_codeCli);
			bundle.putString("codesoc",m_socCode);
			i.putExtras(bundle);
			act.startActivity(i);
	 }
	 
	void launchFichecli()
	{
		Intent intent = new Intent(context,		ficheclient.class);
		Bundle b=new Bundle();
		b.putString("numProspect",m_codeCli);

		intent.putExtras(b);
		context.startActivity(intent);
	}
	void launchCde()
	{
		Intent intent = new Intent(context,		commandeActivity.class);
		Bundle b=new Bundle();
		b.putString("codecli",m_codeCli);
		b.putString("soccode",m_socCode);
		b.putString("typedoc",TableSouches.TYPEDOC_FACTURE);
		intent.putExtras(b);
		context.startActivity(intent);
	}
	 
	void launchMenuCli()
	{
		Intent intent = new Intent(context,		MenuCliActivity.class);
		Bundle b=new Bundle();
		b.putString("codecli",m_codeCli);
		b.putString("soccode",m_socCode);
		b.putString("fromcarto","1");
		intent.putExtras(b);
		context.startActivity(intent);
	}
	
	private void updateUIFull(){
		if(handler != null){
			Message m = new Message();
			m.what = CartoMapActivity.MESSAGE_UPDATE_PI;
			m.arg1 = CartoMapActivity.MESSAGE_UPDATE_PI_ARG1_FORCE;
			handler.sendMessage(m);
			handler.sendEmptyMessage(CartoMapActivity.MESSAGE_UPDATE_LIST_TOURNEE);
			handler.sendEmptyMessage(CartoMapActivity.MESSAGE_FIND_NEAREST_POI);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		final MenuPopupItem l = (MenuPopupItem)parent.getAdapter().getItem(position);
		if(l.getLabelId() == R.string.Naviguer){
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
		else if(l.getLabelId() == R.string.Appeler){
			Intent messIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+Fonctions.GetStringDanem(structClient.TEL1)));
			context.startActivity(messIntent);
		}
		else if(l.getLabelId() == R.string.ficheclient){
	/*		gridview.setVisibility(View.GONE);
			ll_horaires.setVisibility(View.VISIBLE);
			initiateProgressBar();

			structHoraire horaire = Global.dbHoraire.load(structClient.CODE);


			lundiHoraire.update(context.getString(R.string.Lundi)+": ", horaire.getFormatedFullHoraire(this.context, Calendar.MONDAY, true), horaire.getFormatedFullHoraire(this.context,Calendar.MONDAY, false));
			mardiHoraire.update(context.getString(R.string.Mardi)+": ", horaire.getFormatedFullHoraire(this.context, Calendar.TUESDAY, true), horaire.getFormatedFullHoraire(this.context,Calendar.TUESDAY, false));
			mercrediHoraire.update(context.getString(R.string.Mercredi)+": ", horaire.getFormatedFullHoraire(this.context, Calendar.WEDNESDAY, true), horaire.getFormatedFullHoraire(this.context, Calendar.WEDNESDAY, false)); 
			jeudiHoraire.update(context.getString(R.string.Jeudi)+": ", horaire.getFormatedFullHoraire(this.context, Calendar.THURSDAY, true), horaire.getFormatedFullHoraire(this.context, Calendar.THURSDAY, false)); 	
			vendrediHoraire.update(context.getString(R.string.Vendredi)+": ", horaire.getFormatedFullHoraire(this.context, Calendar.FRIDAY, true), horaire.getFormatedFullHoraire(this.context, Calendar.FRIDAY, false)); 
			samediHoraire.update(context.getString(R.string.Samedi)+": ", horaire.getFormatedFullHoraire(this.context,Calendar.SATURDAY, true), horaire.getFormatedFullHoraire(this.context, Calendar.SATURDAY, false)); 	
			dimancheHoraire.update(context.getString(R.string.Dimanche)+": ", horaire.getFormatedFullHoraire(this.context,Calendar.SUNDAY, true), horaire.getFormatedFullHoraire(this.context, Calendar.SUNDAY, false)); 


			tv_fermetures.setText(Global.dbFermeture.getAllFermetures(structClient.CODE));
*/
			launchMenuCli();
		}
		else if(l.getLabelId() == R.string.Stock_et_commande){
/*			gridview.setVisibility(View.GONE);
			ll_stock.setVisibility(View.VISIBLE);
			initiateProgressBar();
			
			ArrayList<structStock> stocks = Global.dbStock.getAllAsList(structClient.CODE);
//			ArrayList<structStock> stocks = Global.dbStock.getAllAsList("400006B3MCOT");
			
			
			for (structStock a : stocks){
				Debug.Log(a.toString());
			}
			StockAdapter stockAdapter = new StockAdapter(context, 0, stocks);
			lv_stock.setAdapter(stockAdapter);
			*/
			launchHisto(context,m_codeCli,m_socCode,"","");
			//launchMedia();
		}

	}
 
	private void cancelProgressBar(){
		if (progressBarTask != null && progressBarTask.getStatus() != AsyncTask.Status.FINISHED)
			progressBarTask.cancel(true);

	}
	/** AsyncTask for ProgressBar Animation */
	private class ProgressBarTask extends AsyncTask<Long, Integer, Void> {
		int speed = 8;
		long endTimeSeconds;
		boolean isCanceled = false;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pb_close.setProgress(0);

		}

		@Override
		protected Void doInBackground(Long... params) {
			endTimeSeconds = params[0];
			for(int i =0; i< ( endTimeSeconds*speed); i++){
				if(isCancelled()){
					isCanceled = true;
					break;
				}
				publishProgress(i);

				try {
					Thread.sleep(((long)1000/speed));
				} catch (InterruptedException e) {
					Debug.StackTrace(e);
				}

			}

			return null;
		}



		@Override
		protected void onProgressUpdate(Integer... values) {
			pb_close.setMax((int) (endTimeSeconds*speed));
			pb_close.setProgress(values[0]);
		}

		@Override
		protected void onPostExecute(Void result) {

			if(!isCanceled){

			}
			if(handler != null)
				handler.sendEmptyMessage(CartoMapActivity.MESSAGE_HIDE_MENU_POPUP);
			progressBarTask = null;
		}


	}
	 static public void launchQuest(Context c,String codecli,String codesoc,String enseigne)
		{
			if (Global.dbKDEntPlan.loadActive("",enseigne,codesoc).size()==0)
			{
				return;
			}
			
			Bundle bundle = new Bundle();
			bundle.putString("codecli",codecli);
			bundle.putString("codesoc", codesoc);
			bundle.putString("enseigne",enseigne);


			Intent i = new Intent(c, pac_plans_filler.class);
			i.putExtras(bundle);
			c.startActivity (i);	
			
	/*		Questionem.showQuestFiller(act, 502, "Negos",
					Global.dbTwin.Nom(Global.dbTwin.TWIN_SOCIETE, "-1"),bundle);
	*/
			
		}

}
