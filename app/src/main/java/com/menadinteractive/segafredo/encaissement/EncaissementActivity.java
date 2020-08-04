package com.menadinteractive.segafredo.encaissement;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.Map;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.menadinteractive.maxpoilane.BaseActivity;
import com.menadinteractive.maxpoilane.R;
import com.menadinteractive.segafredo.adapter.EncaissementAdapter;
import com.menadinteractive.segafredo.communs.Fonctions;
import com.menadinteractive.segafredo.communs.Global;
import com.menadinteractive.segafredo.db.Preferences;
import com.menadinteractive.segafredo.db.TableClient.structClient;
import com.menadinteractive.segafredo.plugins.Espresso;

public class EncaissementActivity extends BaseActivity{

	structClient client;

	TextView tvNomClient, tvCodeClient, tvMontantFacture, tvNoEncaissementsTitle, tvListeEncaissementsTitle, tvDate,
	tvMontantFactureTitre, tvAgenceTitle, tvNumeroCompteTitle;
	EditText etMontantEntre, etCheque, etNumeroCompte, etAgence;
	RadioGroup rgTypeEncaissement;
	RadioButton rbEspece, rbCheque;
	Button bDatePicker, bSaveEncaissement;
	LinearLayout llSecondPartOne, llSecondPartTwo;
	ListView lvEncaissement;
	Spinner sBanque;

	DialogFragment dateFragment;
	Calendar mCalendar;

	ArrayList<String> numerosFacture;
	ArrayList<Facture> mFactures;
	ArrayList<Encaissement> mEncaissements;

	EncaissementAdapter mAdapter;

	String typePaiement = null, dateCheque = null;

	String identifiantEncaissementDelete = "", codeClient = ""; 

	ArrayList<String> idEncaissementsSave;

	float montantFactures = 0, resteFacture = 0;

	LinkedHashMap<String, String> hBanque;

	Context mContext; 

	int year, month, day;

	boolean isEncaissementPartiel = false;
	boolean isAcompte = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_encaissement);

		mContext = this;

		numerosFacture = new ArrayList<String>();

		//on récupère les informations du bundle
		Intent intent = getIntent();
		Bundle b = new Bundle();
		codeClient = null;
		if(intent != null) b = intent.getExtras();
		if(b != null){
			codeClient = b.getString(Espresso.CODE_CLIENT);
			numerosFacture = b.getStringArrayList(ReglementActivity.NUMEROS_FACTURE);
		}

		initGui();
		initListeners();

		idEncaissementsSave = new ArrayList<String>();

		//on charge le client
		client = Global.dbClient.load(codeClient);

		initClient();	
		loadFactures();
		if(numerosFacture.size() > 0) initTotalFactures();

		//s'il y a des avoirs dans les numéros de facture alors on enregistre les encaissements
		saveAvoirsAsEncaissement();

		initListEncaissements();
		if(numerosFacture.size() > 0) displayTotal();
		else{
			isAcompte = true;
			displayAcompte();
		}

		displayListEncaissementsOrFields(true);

		initSpinnerBanque();

		initDate();

		if(numerosFacture.size() == 1) isEncaissementPartiel = true;
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	/**
	 * Initialisation des listener
	 */
	private void initListeners() {

		initRadioGroup();

		lvEncaissement.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String identifiant = "";
				if(view.getTag() != null){				
					identifiant = view.getTag().toString();
					identifiantEncaissementDelete = identifiant;
					MessageYesNo(getString(R.string.suppression_encaissement), Encaissement.TYPE_DELETE_ENCAISSEMENT);
				}

			}
		});

		bDatePicker.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				//on affiche la dialogue date picker
				//showDialog(999);
				showDatePickerDialog(v);
			}
		});

		bSaveEncaissement.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				clickSaveEncaissement();	
			}
		});

	}

	/**
	 * Initialisation de l'interface
	 */
	private void initGui() {
		tvNomClient = (TextView) findViewById(R.id.tvNomClient);
		tvCodeClient = (TextView) findViewById(R.id.tvCodeClient);
		tvMontantFacture = (TextView) findViewById(R.id.tvMontantFacture);
		tvNoEncaissementsTitle = (TextView) findViewById(R.id.tvNoEncaissementsTitle);
		tvListeEncaissementsTitle = (TextView) findViewById(R.id.tvListeEncaissementsTitle);
		tvDate = (TextView) findViewById(R.id.tvDate);
		tvMontantFactureTitre = (TextView) findViewById(R.id.tvMontantFactureTitre);
		tvAgenceTitle = (TextView) findViewById(R.id.tvAgenceTitle);
		tvNumeroCompteTitle = (TextView) findViewById(R.id.tvNumeroCompteTitle);

		rbCheque = (RadioButton) findViewById(R.id.rbCheque);
		rbEspece = (RadioButton) findViewById(R.id.rbEspece);
		lvEncaissement = (ListView) findViewById(R.id.lvEncaissement);
		etMontantEntre = (EditText) findViewById(R.id.etMontantEntre);
		etCheque = (EditText) findViewById(R.id.etCheque);
		etNumeroCompte = (EditText) findViewById(R.id.etNumeroCompte);
		etAgence = (EditText) findViewById(R.id.etAgence);
		llSecondPartOne = (LinearLayout) findViewById(R.id.llSecondPartOne);
		llSecondPartTwo = (LinearLayout) findViewById(R.id.llSecondPartTwo);
		sBanque = (Spinner) findViewById(R.id.sBanque);
		rgTypeEncaissement = (RadioGroup) findViewById(R.id.rgTypeEncaissement);
		bDatePicker = (Button) findViewById(R.id.bDatePicker);
		bSaveEncaissement = (Button) findViewById(R.id.bSaveEncaissement);

	}

	/**
	 * Initialisation du spinner des banques dans param
	 */
	private void initSpinnerBanque() {
		hBanque = new LinkedHashMap<String, String>();
		if(Global.dbParam != null){
			hBanque = Global.dbParam.getAllRecLblFromTable(Global.dbParam.PARAM_BANQUE, 1);
			if(hBanque != null && hBanque.size() > 0){
				Collection<String> col = hBanque.values();
				ArrayList<String> list = new ArrayList<String>(col);
				list.add(0, "");
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
						android.R.layout.simple_spinner_item, list);
				// Specify the layout to use when the list of choices appears
				adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				// Apply the adapter to the spinner
				sBanque.setAdapter(adapter);
			}
		}
	}
	
	void resetChequeFields(){
		etCheque.setText("");
		etNumeroCompte.setText("");
		etAgence.setText("");
		sBanque.setSelection(0);
		initDate();
	}

	/**
	 * Initialise l'edittext avec le montant total des factures
	 */
	private void initETMontantFactures(float montant) {
		montant = (float) (Math.round(montant*100.0)/100.0);
		etMontantEntre.setText(Fonctions.GetFloatToStringFormatDanem(montant, "0.00"));
		if(montant == 0 || montant == 0.0) etMontantEntre.setEnabled(false);
		else etMontantEntre.setEnabled(true);
	}

	/**
	 * Initialisation des informations du client
	 */
	private void initClient() {
		if(client != null){
			tvNomClient.setText(client.NOM);
			tvCodeClient.setText(client.CODE);
		}
	}

	/**
	 * Affichage de la page en mode acompte client
	 */
	private void displayAcompte() {
		tvMontantFacture.setVisibility(View.GONE);
		tvMontantFactureTitre.setText(getString(R.string.encaissement_montant_titre_acompte));	

		//On passe les champs obligatoires en rouge
	//	if(isAcompte){
	//		tvAgenceTitle.setTextColor(getResources().getColor(R.color.red));
	//		tvNumeroCompteTitle.setTextColor(getResources().getColor(R.color.red));
	//	}
	}

	@Override
	public void onBackPressed() {
		float totalEncaissement = 0;
		totalEncaissement = Encaissement.getTotalEncaissementFromList(mEncaissements);

		if(totalEncaissement >= montantFactures || isEncaissementPartiel) finish();
		else{
			Fonctions.makeSpecialToast(mContext, R.string.total_non_encaisser, Toast.LENGTH_SHORT, Gravity.CENTER);
		}	    
	}

	/**
	 * Chargement des factures
	 */
	private void loadFactures(){
		//on récupère les factures grace aux numéros de facture
		mFactures = new ArrayList<Facture>();
		for(String num : numerosFacture){
			mFactures.add(Facture.getFactureFromNumDoc(num)); 
		}	

		Collections.sort(mFactures, Facture.Comparators.DATE);
	}

	/**
	 * Initialisation du total des factures
	 */
	private void initTotalFactures(){
		float totalFacture = 0;
		float total = 0;
		for(Facture facture : mFactures){
			//			if(!facture.getIsFactureDue()){			
			//				if(!facture.getTypeFacture().equals(Facture.TYPE_AVOIR)){
			//					totalFacture += Math.round(facture.getMontantDu()*100.0)/100.0;
			//				}
			//			}
			//			else totalFacture += Math.round(facture.getMontantDu()*100.0)/100.0;

			//			if(!facture.getTypeFacture().equals(Facture.TYPE_AVOIR)) total += Math.round(facture.getMontantDu()*100.0)/100.0;
			//			else total += Math.round(facture.getMontantDu()*100.0)/100.0;
			if(!facture.getTypeFacture().equals(Facture.TYPE_AVOIR)) totalFacture += Math.round(facture.getMontantDu()*100.0)/100.0;
		}

		montantFactures = (float) (Math.round(totalFacture*100.0)/100.0);
		tvMontantFacture.setText(Fonctions.GetFloatToStringFormatDanem(montantFactures, "0.00"));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//		addMenu(menu, R.string.encaissement_enregistrer, android.R.drawable.ic_menu_save);
		addMenu(menu, R.string.encaissement_terminer, android.R.drawable.ic_menu_save);
		addMenu(menu, R.string.commande_annuler, android.R.drawable.ic_menu_close_clear_cancel);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		float totalEncaissement = 0;

		switch (item.getItemId()) {
		case R.string.encaissement_enregistrer:

			clickSaveEncaissement();
			return true;
		case R.string.encaissement_terminer:
			totalEncaissement = Encaissement.getTotalEncaissementFromList(mEncaissements);

			if(totalEncaissement >= montantFactures || isEncaissementPartiel) finish();
			else{
				Fonctions.makeSpecialToast(mContext, R.string.total_non_encaisser, Toast.LENGTH_SHORT, Gravity.CENTER);
			}	   
			return true;
		case R.string.commande_annuler:
			if(idEncaissementsSave.size() > 0){
				for(String id : idEncaissementsSave){
					Encaissement.deleteEncaissement(mContext, id, codeClient, Encaissement.NON_VALIDE);
				}
			}
			finish();
			Fonctions.makeSpecialToast(mContext, R.string.encaissements_annuler, Toast.LENGTH_SHORT, Gravity.CENTER);
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void clickSaveEncaissement() {
		float totalEncaissement = Encaissement.getTotalEncaissementFromList(mEncaissements);

		//on vérifie que le montant ne soit pas égale à 0.0
		if(!etMontantEntre.getText().toString().equals("")){
			float rentre = Fonctions.convertToFloat(etMontantEntre.getText().toString());
			if(rentre == 0 && (mFactures.size() == 0 || (mFactures.size() > 0 
					&& mFactures.get(0).getNumDoc().equals(Encaissement.ENCAISSEMENT_NOFACTURE)))){			
				Fonctions.makeSpecialToast(mContext, R.string.montant_nul, Toast.LENGTH_SHORT, Gravity.CENTER);
				return;					
			}else if(rentre == 0 && totalEncaissement == montantFactures){
				Fonctions.makeSpecialToast(mContext, R.string.montant_total_encaisse, Toast.LENGTH_SHORT, Gravity.CENTER);
				return;
			}else if(rentre == 0 && totalEncaissement != montantFactures){
				Fonctions.makeSpecialToast(mContext, R.string.montant_total_encaisse_zero, Toast.LENGTH_SHORT, Gravity.CENTER);
				return;
			}
		}

		//on vérifie si un montant a bien été saisi s'il est correcte
		if(etMontantEntre.getText().toString().equals("")){
			Fonctions.makeSpecialToast(mContext, R.string.no_montant, Toast.LENGTH_SHORT, Gravity.CENTER);
			return;
		}

		if(!checkMontant()) return;

		//on controle si un type a bien été choisi
		if(!checkTypeSelected()){
			Fonctions.makeSpecialToast(mContext, R.string.type_not_selected, Toast.LENGTH_SHORT, Gravity.CENTER);
			return;
		}

		//on vérifie que les champs si type chèque sont bien remplis
		if(!checkFieldFilled()) return;

		//on reset le radiogroup
		initRadioGroup();

		//on sauvegarde la ligne encaissement
		Encaissement encaissement = saveEncaissement();

		//on rafraichit la liste des encaissements
		initListEncaissements();
		
		//on remet à 0 les champs de cheque
		if(encaissement.getTypePaiement().equals(Encaissement.TYPE_CHEQUE)) resetChequeFields();

		//on change le reste à encaisser
		totalEncaissement = Encaissement.getTotalEncaissementFromList(mEncaissements);
		if(montantFactures >= totalEncaissement){
			float reste = montantFactures - totalEncaissement;
			resteFacture = (float) (Math.round(reste*100.0)/100.0);
			initETMontantFactures(resteFacture);
		}

		//on change de route si on est en mode acompte
		if(isAcompte){
			//on lance l'activity qui permet d'enregistrer le nom du client + la signature
			//ensuite gérer l'impression
			//l'encaissement sera enregistré sur l'écran
			launchValidationActivity(encaissement.getIdentifiant());
		}
	}

	protected MenuItem addMenu(Menu menu, int stringID, int iconID){
		MenuItem item  = menu.add(Menu.NONE, stringID, Menu.NONE, stringID);
		int size = menu.size() - 1;
		if(iconID != -1)
			menu.getItem(size).setIcon(iconID);
		menu.getItem(size).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM|MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		return item;
	}

	protected MenuItem addMenu(Menu menu, int stringID, String label, int iconID){
		MenuItem item  = menu.add(Menu.NONE, stringID, Menu.NONE, label);
		int size = menu.size() - 1;
		if(iconID != -1)
			menu.getItem(size).setIcon(iconID);
		menu.getItem(size).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM|MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		return item;
	}

	public void MessageYesNo(String message, final int type) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(message)
		.setCancelable(false)
		.setPositiveButton(getString(android.R.string.yes),
				new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {	
				switch (type) {
				case Encaissement.TYPE_DELETE_ENCAISSEMENT:
					if(!identifiantEncaissementDelete.equals("")){
						//si l'identifiant est lié à un avoir alors on le supprime de la liste des numerosFacture
						//récupération du numéros de l'avoir correspondant à l'encaissement de l'avoir
						ArrayList<String> list = Global.dbKDEncaisserFacture
								.getNumerosFactureFromEncaissement(Encaissement.getEncaissement(identifiantEncaissementDelete, codeClient));

						if(list != null && list.size() > 0){

							for(String num : list){
								Bundle b = Global.dbKDHistoDocuments.getDocumentFromNumDoc(num);
								if(b != null 
										&& b.getString(Global.dbKDHistoDocuments.FIELD_TYPEDOC) != null 
										&& b.getString(Global.dbKDHistoDocuments.FIELD_TYPEDOC).equals(Facture.TYPE_AVOIR)){
									numerosFacture.remove(num);
								}
							}						
						}		

						boolean success = Encaissement.deleteEncaissement(mContext, identifiantEncaissementDelete, codeClient, Encaissement.NON_VALIDE);				

						identifiantEncaissementDelete = "";

						if(success){							
							initListEncaissements();
							if(mFactures == null || mFactures.size() ==0 || mFactures.get(0).getNumDoc().equals(Encaissement.ENCAISSEMENT_NOFACTURE)){
								etMontantEntre.setText(Fonctions.GetFloatToStringFormatDanem(0, "0.00"));
							}else{
								float totalEncaissement = Encaissement.getTotalEncaissementFromList(mEncaissements);
								if(montantFactures > totalEncaissement){
									float reste = montantFactures - totalEncaissement;
									resteFacture = (float) (Math.round(reste*100.0)/100.0);
									initETMontantFactures(resteFacture);
								}else initETMontantFactures(0);
							}
							Fonctions.makeSpecialToast(mContext, R.string.success_delete_encaissement, Toast.LENGTH_SHORT, Gravity.CENTER);
						}else{
							//TODO toast un problème est survenu
						}
					}
					break;
				}

			}
		})
		.setNegativeButton(getString(android.R.string.no),
				new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}

	/**
	 * Récupération de la liste des encaissements pour les numéros de factures passés en paramètres
	 * @return
	 */
	private ArrayList<Encaissement> getListEncaissements(ArrayList<Facture> factures) {
		ArrayList<Encaissement> list = new ArrayList<Encaissement>();

		for(Facture facture : factures){
			ArrayList<Encaissement> encaissements = Encaissement.getEncaissementFromFacture(facture, codeClient, Encaissement.NON_VALIDE);			
			for(Encaissement e : encaissements){
				if(!Encaissement.containsEncaissement(list, e)) list.add(e);
			}
		}

		return list;
	}

	/**
	 * Enregistrement de l'encaissement
	 */
	private Encaissement saveEncaissement() {
		Encaissement encaissement = new Encaissement();
		encaissement.setTypePaiement(typePaiement);
		encaissement.setMontant((float) (Math.round(Fonctions.convertToFloat(etMontantEntre.getText().toString())*100.0)/100.0));
		encaissement.setDate(Fonctions.getYYYYMMDD());
		encaissement.setCodeClient(codeClient);
	 		
		if(dateCheque != null) encaissement.setDateCheque(dateCheque);
		
		if(sBanque.getSelectedItem() != null){
			String codeBanque = getKeyFromBanqueHashMapValue(hBanque, String.valueOf(sBanque.getSelectedItem()));
			if(codeBanque != null && !codeBanque.equals("")){
				encaissement.setCodeBanque(codeBanque);
				encaissement.setBanque(sBanque.getSelectedItem().toString());
			}
		}
		if(numerosFacture.size() == 0) numerosFacture.add(Encaissement.ENCAISSEMENT_NOFACTURE);

		
		if(etAgence.getText() != null) encaissement.setAgence(etAgence.getText().toString());
		if(etNumeroCompte.getText() != null) encaissement.setNumeroCompte(etNumeroCompte.getText().toString());
		if(etCheque.getText() != null) encaissement.setCheque(etCheque.getText().toString());
		encaissement = encaissement.saveEncaissement(numerosFacture, Preferences.getValue(this, Espresso.LOGIN, "0"));
		if(encaissement != null) idEncaissementsSave.add(encaissement.getIdentifiant());

		//on cache les champs, on fait apparaitre la liste
		displayListEncaissementsOrFields(true);

		return encaissement;
	}

	private void initListEncaissements() {
		mEncaissements = new ArrayList<Encaissement>();

		//récupération des encaissement et alimentation de la liste des encaissements
		if(mFactures.size() == 0) {
			Facture facture = new Facture();
			facture.setNumDoc(Encaissement.ENCAISSEMENT_NOFACTURE);
			mFactures.add(facture);
		}
		mEncaissements = getListEncaissements(mFactures);

		if(mEncaissements != null){	

			mAdapter = new EncaissementAdapter(this, R.layout.item_list_encaissement, mEncaissements, false);
			lvEncaissement.setAdapter(mAdapter);	

			if(mEncaissements.size() == 0){
				tvNoEncaissementsTitle.setVisibility(View.VISIBLE);
				lvEncaissement.setVisibility(View.GONE);
			}
			else {
				tvNoEncaissementsTitle.setVisibility(View.GONE);
				lvEncaissement.setVisibility(View.VISIBLE);
			}
		}else{
			if(mEncaissements.size() == 0){
				tvNoEncaissementsTitle.setVisibility(View.VISIBLE);
				lvEncaissement.setVisibility(View.GONE);
			}
			else{
				tvNoEncaissementsTitle.setVisibility(View.GONE);
				lvEncaissement.setVisibility(View.VISIBLE);
			}
		}

	}

	private boolean checkTypeSelected(){
		if(rbEspece.isChecked() || rbCheque.isChecked()) return true;
		else{
			return false;
		}
	}

	private boolean checkFieldFilled(){
		if(rbCheque.isChecked()){
			//on vérifie les champs
			if(String.valueOf(sBanque.getSelectedItem()) == null
					|| String.valueOf(sBanque.getSelectedItem()).equals("")) {
				Fonctions.makeSpecialToast(mContext, R.string.no_banque, Toast.LENGTH_SHORT, Gravity.CENTER);
				return false;
			}
	/*		if(isAcompte){
				if(etAgence.getText().toString().equals("")){
					Fonctions.makeSpecialToast(mContext, R.string.no_agence, Toast.LENGTH_SHORT, Gravity.CENTER);
					return false;
				}
				if(etNumeroCompte.getText().toString().equals("")){
					Fonctions.makeSpecialToast(mContext, R.string.no_numero_compte, Toast.LENGTH_SHORT, Gravity.CENTER);
					return false;
				}
			}*/
			if(etCheque.getText().toString().equals("")){
				Fonctions.makeSpecialToast(mContext, R.string.no_cheque, Toast.LENGTH_SHORT, Gravity.CENTER);
				return false;
			}
			if(etCheque.getText().toString().length()>8){
				Fonctions.makeSpecialToast(mContext, R.string.le_num_ro_de_ch_que_ne_doit_pas_exc_der_8_chiffres, Toast.LENGTH_SHORT, Gravity.CENTER);
				return false;
			}
			if(tvDate.getText().equals("")){
				Fonctions.makeSpecialToast(mContext, R.string.no_date, Toast.LENGTH_SHORT, Gravity.CENTER);
				return false;
			}

		}


		return true;
	}

	private boolean checkMontant(){
		float montant = 0;
		try{
			montant = Float.valueOf(etMontantEntre.getText().toString());

			if(montant < 0){
				Fonctions.makeSpecialToast(mContext, R.string.format_montant_negatif, Toast.LENGTH_SHORT, Gravity.CENTER);
				return false;
			}

			if(montant > montantFactures && numerosFacture.size() > 0 
					&& !numerosFacture.get(0).equals(Encaissement.ENCAISSEMENT_NOFACTURE)
					&& resteFacture == 0){
				Fonctions.makeSpecialToast(mContext, R.string.format_montant_out, Toast.LENGTH_SHORT, Gravity.CENTER);
				initETMontantFactures(montantFactures);
				return false;
			}

			if( resteFacture != 0 && montant > resteFacture){
				initETMontantFactures(resteFacture);
				Fonctions.makeSpecialToast(mContext, R.string.format_montant_out_reste, Toast.LENGTH_SHORT, Gravity.CENTER);
				return false;
			}
		}catch(NumberFormatException ex){
			Fonctions.makeSpecialToast(mContext, R.string.format_montant_incorrect, Toast.LENGTH_SHORT, Gravity.CENTER);
			return false;
		}
		return true;
	}

	private void displayListEncaissementsOrFields(boolean encaissements){
		if(encaissements){
			llSecondPartOne.setVisibility(View.GONE);
			llSecondPartTwo.setVisibility(View.VISIBLE);
		}else{
			llSecondPartOne.setVisibility(View.VISIBLE);
			llSecondPartTwo.setVisibility(View.GONE);
		}
	}

	private void displayTotal(){
		float total = 0;
		if(mEncaissements.size() > 0) total = Encaissement.getTotalEncaissementFromList(mEncaissements);
		if(total >= montantFactures) initETMontantFactures(0);
		else{
			initETMontantFactures(montantFactures-total);
			resteFacture = montantFactures-total;
		}
	}

	private String getKeyFromBanqueHashMapValue(LinkedHashMap<String,String> h, String value){

		if(h.containsValue(value)){
			for(Map.Entry<String,String> entry : h.entrySet()){
				if(value.equals(entry.getValue())) return entry.getKey();
			}
		}
		return null;
	}

	/**
	 * Réinitialisation des radiobutton
	 */
	private void initRadioGroup() {
		rgTypeEncaissement.clearCheck();
		rbCheque.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				//on sauvegarde le type 
				typePaiement = "CH";

				//on affiche la section du chèque
				if(isChecked){
					displayListEncaissementsOrFields(false);
				}
				else{
					displayListEncaissementsOrFields(true);
				}
			}
		});

		rbEspece.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				typePaiement = "ES";		
				if(isChecked){
					displayListEncaissementsOrFields(true);
				}
				else{
					displayListEncaissementsOrFields(false);
				}
			}
		});

	}

	private void showDate(int year, int month, int day) {
		//on met les valeurs sous le bon format
		String yearS = Integer.toString(year);
		String monthS = Integer.toString(month);
		String dayS = Integer.toString(day);

		if(month < 10) monthS = "0"+month;
		if(day<10) dayS = "0"+day;

		tvDate.setText(new StringBuilder().append(dayS).append("/")
				.append(monthS).append("/").append(yearS));
	}

	private void initDate() {
		Calendar calendar = Calendar.getInstance();
		year = calendar.get(Calendar.YEAR);

		month = calendar.get(Calendar.MONTH);
		day = calendar.get(Calendar.DAY_OF_MONTH);
		
		String Day = "";
		String Month = "";
		String Year = Integer.toString(year);
		
		//on met la date au bon format
		if(month+1 < 10) Month = "0"+Integer.toString(month+1);
		else Month = Integer.toString(month+1);
		
		if(day < 10) Day = "0"+Integer.toString(day);
		else Day = Integer.toString(day);
		dateCheque = Year+Month+Day;
		showDate(year, month+1, day);
	}

	public void showDatePickerDialog(View v) {
		dateFragment = new DatePickerFragment();
		dateFragment.show(getFragmentManager(), "datePicker");
	}

	class DatePickerFragment extends DialogFragment implements
	DatePickerDialog.OnDateSetListener {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Use the current date as the default date in the picker
			final Calendar c = Calendar.getInstance();
			int year = c.get(Calendar.YEAR);
			int month = c.get(Calendar.MONTH);
			int day = c.get(Calendar.DAY_OF_MONTH);

			// Create a new instance of DatePickerDialog and return it
			return new DatePickerDialog(getActivity(), this, year, month, day);
		}

		public void onDateSet(DatePicker view, int year, int month, int day) {

			if(mCalendar == null) mCalendar = Calendar.getInstance();
			mCalendar.set(Calendar.YEAR, year);
			mCalendar.set(Calendar.MONTH, month);
			mCalendar.set(Calendar.DAY_OF_MONTH, day);
			String Day = "";
			showDate(year, month+1, day);
			String Month = "";
			String Year = Integer.toString(year);
			
			//on met la date au bon format
			if(month+1 < 10) Month = "0"+Integer.toString(month+1);
			else Month = Integer.toString(month+1);
			
			if(day < 10) Day = "0"+Integer.toString(day);
			else Day = Integer.toString(day);
			
			dateCheque = Year+Month+Day;
		}
	}

	private void saveAvoirsAsEncaissement() {

		for(Facture facture : mFactures){
			if(facture != null && facture.getTypeFacture() != null && facture.getTypeFacture().equals(Facture.TYPE_AVOIR)){
				//on enregistre l'avoir en tant qu'encaissement
				Encaissement encaissement = new Encaissement();
				encaissement.setTypePaiement(facture.getTypeFacture());
				encaissement.setMontant((float) (Math.round(facture.getMontantTTC()*100.0)/100.0));
				encaissement.setDate(Fonctions.getYYYYMMDD());
				encaissement.setCodeClient(codeClient);

				if(numerosFacture.size() == 0) numerosFacture.add(Encaissement.ENCAISSEMENT_NOFACTURE);

				encaissement = encaissement.saveEncaissement(numerosFacture, Preferences.getValue(this, Espresso.LOGIN, "0"));
				idEncaissementsSave.add(encaissement.getIdentifiant());
			}
		}

	}

	/**
	 * Lance l'écran de validation pour l'enregistrement de l'acompte
	 */
	private void launchValidationActivity(String numEncaissement){
		Bundle b = new Bundle();
		b.putString(Espresso.CODE_CLIENT, codeClient);
		b.putString(Encaissement.NUMERO_ENCAISSEMENT, numEncaissement);

		Intent intent = new Intent(this, ValidationAcompte.class);
		intent.putExtras(b);
		startActivity(intent);
	}


}
