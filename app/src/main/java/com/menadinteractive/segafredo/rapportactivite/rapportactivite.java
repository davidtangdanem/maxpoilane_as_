package com.menadinteractive.segafredo.rapportactivite;

import java.util.ArrayList;
import java.util.Calendar;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.menadinteractive.agendanem.AgendaListActivity;
import com.menadinteractive.agendanem.AgendaViewActivity;
import com.menadinteractive.maxpoilane.BaseActivity;
import com.menadinteractive.maxpoilane.R;
import com.menadinteractive.segafredo.carto.CartoMapActivity;
import com.menadinteractive.segafredo.communs.Fonctions;
import com.menadinteractive.segafredo.communs.Global;
import com.menadinteractive.segafredo.db.Preferences;
import com.menadinteractive.segafredo.db.TableClient.structClient;
import com.menadinteractive.segafredo.db.dbKD101ClientVu.structClientvu;
import com.menadinteractive.segafredo.plugins.Agendanem;
import com.menadinteractive.segafredo.plugins.Espresso;
import com.menadinteractive.segafredo.synchro.SynchroService;

public class rapportactivite extends BaseActivity implements
		OnItemSelectedListener, OnClickListener {

	TextView TextViewDate;
	TextView TextViewHeure;
	TextView TextViewTypeActivite;
	TextView TextViewCommentaire;
	TextView TextViewNextDate;

	Button bNextDate;
	EditText EditDate;
	EditText EditHeure;
	EditText EditCommentaire;
	ArrayList<Bundle> idTypeActivite = null;
	int m_ilongueurComt=255;

	// String m_stvaleurDebut;
	// String m_stvaleurFin;
	
	ImageButton ibGo;
	ImageButton ibStop;
	TimePicker tpAvant;
	TimePicker tpApres;
	

	private ProgressDialog m_ProgressDialog = null;
	/** Task */
	String m_codeclient = "";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		SynchroService.setPaused(true);
		idTypeActivite = new ArrayList<Bundle>();

		Bundle bundle = this.getIntent().getExtras();
		m_codeclient = bundle.getString("codeclient");

		setContentView(R.layout.activity_rapportactivite);
		initActionBar();

		InitTextView();
		setListeners();
		initListeners();

		Loadinformation();

	}

	void setListeners() {

		bNextDate.setOnClickListener(this);
	}

	void Loadinformation() {

		structClientvu vu = new structClientvu();
		StringBuffer stBuf = new StringBuffer();
		Global.dbKDClientVu.load(vu, m_codeclient, Fonctions.getYYYYMMDD(),
				stBuf);
		String stdate = "";
		String stheuredebut = "";
		String stheurefin = "";
		

		if (vu.DATE.equals("")) {

			stdate = Fonctions.getDD_MM_YYYY();
		} else {
			stdate = Fonctions.getDD_MM_YYYY(vu.DATE);

		}

		if (vu.HEUREDEBUT.equals("")) {

			stheuredebut = Fonctions.gethh_mm();
		} else
			stheuredebut = vu.HEUREDEBUT;
		
		HHMM_to_TimePicker(this, R.id.timePickerHourDebut, stheuredebut);
		HHMM_to_TimePicker(this, R.id.timePickerHourFin, stheurefin);
	

		setEditViewText(this, R.id.EditDate, stdate);
		
		setEditViewText(this, R.id.EditCommentaire, vu.COMMENTAIRE);

		fillTypeActivite(Fonctions.GetStringDanem(vu.TYPEACT));

	}

	void showProgressDialog() {
		m_ProgressDialog = ProgressDialog.show(this, "Veuillez patienter",
				"traitement en cours", true);
	}

	public void hideProgressDialog() {
		if (m_ProgressDialog != null)
			m_ProgressDialog.dismiss();
	}

	private void initActionBar() {
		ActionBar actionBar = getActionBar();
		actionBar.setSubtitle(R.string.rapport_titre);
	}

	@Override
	protected void onStart() {
		super.onStart();

	}

	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();

		addMenu(menu, android.R.string.cancel,
				android.R.drawable.ic_menu_close_clear_cancel);
		addMenu(menu, android.R.string.ok, android.R.drawable.ic_menu_save);
		// addMenu(menu, R.string.supprimer, android.R.drawable.ic_menu_delete);

		return true;
	}

	private void record(ArrayList<String> ValueMessage, StringBuffer buff) {

		if (ControleAvantSauvegarde(ValueMessage, buff) == true) {
			if (1 == 1) {
				if (save(false, buff) == true) {
					finish();
				} else {
					Fonctions.FurtiveMessageBox(this, buff.toString());
				}
			}

		} else
			Fonctions.FurtiveMessageBox(this,
					Fonctions.GetStringDanem(ValueMessage.get(0)));

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		StringBuffer buff = new StringBuffer();
		ArrayList<String> ValueMessage = new ArrayList();
		switch (item.getItemId()) {
		case R.string.prospect_positionner:

			return true;
		case android.R.string.cancel:
			Cancel();
			return true;

		case android.R.string.ok:
			record(ValueMessage, buff);
			return true;

		case R.string.prospect_prendrecde:// si on veut prendre commande avant
											// m�me de remplir les coord
			// on sauve le prospect (obligŽ pour que tt fonctionne)
			// on va en prise de cde
			// on revient et on contr™le
			// comme c'est une sauvegarde forcŽe on donne un nom au client par
			// dŽfaut si non renseignŽ
			if (this.getEditViewText(this, R.id.EditRaisonsocial).equals("")) {
				this.setEditViewText(this, R.id.EditRaisonsocial,
						getString(R.string.prospect_nomdefaut));
			}

			break;
		/*
		 * case R.string.commande_quest:
		 * MenuPopup.launchQuest(this,numProspect,"",""); break;
		 */
		case R.string.geocode_prospect:

			break;
		default:
			return super.onOptionsItemSelected(item);
		}
		return super.onOptionsItemSelected(item);
	}

	void Cancel() {

		this.finish(0);

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK
				|| keyCode == KeyEvent.KEYCODE_HOME) {
			Cancel();

			return false;
		}

		else
			return super.onKeyDown(keyCode, event);
	}

	String getTypeActivite() {
		String pays = "";
		int pos = this.getSpinnerSelectedIdx(this, R.id.spinnerTypeActivite);
		if (pos > -1)
			try {
				pays = idTypeActivite.get(pos).getString(
						Global.dbParam.FLD_PARAM_CODEREC);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		return pays;
	}

	void finish(int i) {
		SynchroService.setPaused(false);
		finish();
	}

	/**
	 * Creation du menu
	 */
	static final private int MENU_ITEM = Menu.FIRST;

	public boolean save(boolean temporary, StringBuffer stbuff) {
		try {

			structClientvu vu = new structClientvu();

			vu.SOC_CODE = "1";
			vu.CODEREP = Preferences.getValue(this, Espresso.LOGIN, "0");
			vu.CODECLI = m_codeclient;
			vu.TYPEACT = getTypeActivite();
			String stDate = this.getEditViewText(this, R.id.EditDate);
			stDate = Fonctions.Right(stDate, 4) + Fonctions.Mid(stDate, 3, 2)
					+ Fonctions.Left(stDate, 2);

			vu.DATE = stDate;
			
			vu.HEUREDEBUT= BaseActivity.TimePicker_to_HHMM(this, R.id.timePickerHourDebut);
			vu.HEUREFIN= BaseActivity.TimePicker_to_HHMM(this, R.id.timePickerHourFin);
			String stcommentaire="";
			stcommentaire=Fonctions.ReplaceGuillemet(this.getEditViewText(this,R.id.EditCommentaire));
			
			vu.COMMENTAIRE = stcommentaire;
			StringBuffer buffer = new StringBuffer();
			if (Global.dbKDClientVu.save2(vu) == false) {
				Fonctions.FurtiveMessageBox(
						this,
						getString(R.string.unable_to_save) + ":"
								+ buffer.toString());
				return false;
			}

			Fonctions.FurtiveMessageBox(this,
					getString(R.string.save_successfull));

			structClient cli=new structClient();
			cli.CODE=m_codeclient;
			CartoMapActivity.updateVisite(cli, true ,this);
			return true;

		} catch (Exception ex) {
			return false;

		}

	}

	public boolean ControleAvantSauvegarde(ArrayList<String> ValueMessage,
			StringBuffer stBuf) {
		boolean bres = true;

		String stmessage = "";

		try {

			if (getTypeActivite().equals("")) {

				stmessage = "" + TextViewTypeActivite.getText().toString()
						+ " " + getString(R.string.prospect_obligatoire);
				ValueMessage.add(stmessage);
				return false;
			}
			if(ControleHoraire()==true)
			{
				ValueMessage.add(getString(R.string.rapport_heurefinInfheuredebut));
				return false;
			}

		} catch (Exception ex) {
			stBuf.setLength(0);
			stBuf.append(ex.getMessage());
			return false;

		}
		return bres;

	}

	void fillTypeActivite(String selVal) {
		try {
			// selVal=Global.dbClient.getCodeTypeEtablissement(selVal);

			if (Global.dbParam.getRecord2s(Global.dbParam.PARAM_TYPEACTIVITE,
					this.idTypeActivite, true) == true) {

				int pos = 0;
				String[] items = new String[idTypeActivite.size()];
				for (int i = 0; i < idTypeActivite.size(); i++) {

					items[i] = idTypeActivite.get(i).getString(
							Global.dbParam.FLD_PARAM_LBL);

					String codeRec = idTypeActivite.get(i).getString(
							Global.dbParam.FLD_PARAM_CODEREC);

					if (selVal.equals(codeRec)) {
						pos = i;
					}

				}

				Spinner spinner = (Spinner) findViewById(R.id.spinnerTypeActivite);

				//ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				//		android.R.layout.simple_spinner_item, items);
				ArrayAdapter<String>  adapter = new ArrayAdapter<String>(this, R.layout.spinner_item_text, items);
				
				adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				spinner.setAdapter(adapter);

				spinner.setSelection(pos);

			}

		} catch (Exception ex) {

		}

	}

	/**
	 * renvoi l'index sï¿½lectionnï¿½ dans un spinner
	 * 
	 * @param et
	 * @return
	 */
	public int getSpinnerSelectedIdx(Spinner et) {
		try {
			int pos = et.getSelectedItemPosition();
			return pos;

		} catch (Exception ex) {

		}
		return -1;
	}

	void InitTextView() {

		/*
		 * TextView TextViewDate ; TextView TextViewHeure ; TextView
		 * TextViewTypeActivite ; TextView TextViewCommentaire ; TextView
		 * TextViewNextDate;
		 * 
		 * 
		 * EditText EditDate; EditText EditHeure; EditText EditCommentaire;
		 */
		ArrayList<Bundle> idTypeActivite = null;

		// Date
		TextViewDate = (TextView) findViewById(R.id.TexteDate);
		// Heure
		TextViewHeure = (TextView) findViewById(R.id.TexteHeure);
		// Type activite
		TextViewTypeActivite = (TextView) findViewById(R.id.TexteTypeActivite);
		// Commentaire
		TextViewCommentaire = (TextView) findViewById(R.id.TexteCommentaire);
		// Next date
		TextViewNextDate = (TextView) findViewById(R.id.TexteNextDate);

		bNextDate = (Button) findViewById(R.id.bNextDate);
		EditDate = (EditText) this.findViewById(R.id.EditDate);
		EditHeure = (EditText) this.findViewById(R.id.EditHeure);
		EditCommentaire = (EditText) this.findViewById(R.id.EditCommentaire);
		Spinner spFonction = (Spinner) this
				.findViewById(R.id.spinnerTypeActivite);
		
		InputFilter[] FilterArrayCmt = new InputFilter[1];

		FilterArrayCmt[0] = new InputFilter.LengthFilter(
				m_ilongueurComt);
		EditCommentaire.setFilters(FilterArrayCmt);
		
		ibGo=(ImageButton)findViewById(R.id.imageButtonGo);
		ibStop=(ImageButton)findViewById(R.id.imageButtonStop);
		
		tpAvant=(TimePicker) this.findViewById(R.id.timePickerHourDebut);
		tpApres=(TimePicker) this.findViewById(R.id.timePickerHourFin);
		tpAvant.setEnabled(true);
		tpAvant.setIs24HourView(true); 
		tpApres.setIs24HourView(true); 
		final Calendar c = Calendar.getInstance();
       int hour = c.get(Calendar.HOUR_OF_DAY);
       int minute = c.get(Calendar.MINUTE);
       
		tpAvant.setCurrentHour(hour);
		tpAvant.setCurrentMinute(minute);
		tpApres.setCurrentHour(hour);
		tpApres.setCurrentMinute(minute);
		

	}
	void initListeners()
	{
			
	
		
		ibGo.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				HHMM_to_TimePicker(tpAvant,Fonctions.gethhmm());
			}
		});
		ibStop.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
			HHMM_to_TimePicker(tpApres,Fonctions.gethhmm());
			
			}
		});
		
	}
	private void UI_updateProgressBar() {

	}

	public void onItemSelected(AdapterView<?> parent, View view, int pos,
			long id) {
		switch (parent.getId()) {

		}
		// An item was selected. You can retrieve the selected item using
		// parent.getItemAtPosition(pos)
		// m_stFam = idFam.get(pos).getString(
		// Global.dbParam.FLD_PARAM_CODEREC);
		// PopulateList() ;
	}

	public void onNothingSelected(AdapterView<?> parent) {
		// Another interface callback
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == bNextDate) {
			if (android.os.Build.VERSION.SDK_INT >= 14
				/*	&& ApplicationLauncher
							.isAgendanemAvailable(rapportactivite.this)*/) {
				
							launchAgenda(this, m_codeclient);
				// Non n√©cessaire d'afficher la boite de dialogue de
				// envclient car ancien m√©canisme
				// afficherDialogDate(null);
			}

		}

	}

	public static void launchAgenda(Activity a, String m_codeclient)
	{
		 StringBuilder err = new StringBuilder();
			structClient client = new structClient();
			
			Bundle b = new Bundle();
			b.putString(Agendanem.ApplicationName, "segafredo");
			b.putString(Agendanem.ApplicationKey, "SZF");
			
			if (Global.dbClient.getClient(m_codeclient, client, err))
			{	 
			
				b.putString(Agendanem.CodeClient, client.CODE);
				b.putString(Agendanem.AdresseClient, client.getFullAddress());
				b.putString(Agendanem.NameClient, client.NOM);
				Intent intent = new Intent(a,
						AgendaViewActivity.class);
				intent.putExtras(b);
				a.startActivityForResult(intent, LAUNCH_AGENDA);
			}
			else
			{
				Intent intent = new Intent(a,
						AgendaListActivity.class);
				intent.putExtras(b);
				a.startActivityForResult(intent, LAUNCH_AGENDA);
			}
	}
	Boolean ControleHoraire()
	 {
		 boolean bres=false;
		 String stHeuredebut="";
		 String stHeurefin="";
		 int iheuredebut=0;
		 int iheurefin=0;
		 
		 
		 stHeuredebut=TimePicker_to_HHMM(this, R.id.timePickerHourDebut);
		 stHeurefin= TimePicker_to_HHMM(this, R.id.timePickerHourFin);
		 iheuredebut=Fonctions.GetStringToIntDanem(stHeuredebut);
		 iheurefin=Fonctions.GetStringToIntDanem(stHeurefin);
			
		
		 if(iheurefin<iheuredebut)
		 {
			 
			 bres=true; 
		 }
	 
		 
		 return bres; 
		 
	 }
}
