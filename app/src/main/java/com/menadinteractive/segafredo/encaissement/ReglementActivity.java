package com.menadinteractive.segafredo.encaissement;

import java.util.ArrayList;
import java.util.Collections;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.menadinteractive.maxpoilane.BaseActivity;
import com.menadinteractive.printmodels.BluetoothConnectionInsecureExample;
import com.menadinteractive.printmodels.Z420ModelEncaissement;
import com.menadinteractive.printmodels.Z420ModelReleveFacture;
import com.menadinteractive.maxpoilane.R;
import com.menadinteractive.segafredo.adapter.EncaissementAdapter;
import com.menadinteractive.segafredo.adapter.FacturesAdapter;
import com.menadinteractive.segafredo.adapter.FacturesAdapter.OnFactureCheck;
import com.menadinteractive.segafredo.carto.CartoMapActivity;
import com.menadinteractive.segafredo.communs.Fonctions;
import com.menadinteractive.segafredo.communs.Global;
import com.menadinteractive.segafredo.db.Preferences;
import com.menadinteractive.segafredo.db.TableClient.structClient;
import com.menadinteractive.segafredo.db.TableSouches;
import com.menadinteractive.segafredo.db.dbKD730FacturesDues;
import com.menadinteractive.segafredo.db.dbKD98Encaissement;
import com.menadinteractive.segafredo.plugins.Espresso;
import com.menadinteractive.segafredo.remisebanque.Cheque;

public class ReglementActivity extends BaseActivity {

	public final static String NUMEROS_FACTURE = "numerosFacture";

	String commentaire="";
	Handler hPrintResult;
	private ProgressDialog m_ProgressDialog = null;
	TextView tvNomClient, tvCodeClient, tvTotalFactures, tvTotalSelection;
	ListView lvFactures, lvEncaissement;
	CheckBox cbSolde;
	Button bRegler;

	structClient client;

	FacturesAdapter mAdapter;
	EncaissementAdapter mAdapterEncaissement;

	ArrayList<Facture> mFactures = null;
	ArrayList<Encaissement> mEncaissements = null;

	Context mContext;
	ReglementActivity mActivity;

	String identifiantEncaissementDelete = "", numSouche = null;
	String codeClient = "", mNumeroSouche = "";

	float totalSelection = 0;

	boolean m_isPrinted = false;
	boolean m_isPrintedReleveFacture = false;
	boolean m_problemPrinter = false;

	Handler hPrintResultEncaissement;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reglement);

		mContext = this;
		mActivity = this;

		//on récupère les informations du bundle
		Intent intent = getIntent();
		Bundle b = new Bundle();
		codeClient = null;
		if(intent != null) b = intent.getExtras();
		if(b != null){
			codeClient = b.getString(Espresso.CODE_CLIENT);
		}

		initGui();
		initListeners();

		//on charge le client
		client = Global.dbClient.load(codeClient);

		initClient();	
	}

	@Override
	protected void onResume() {
		super.onResume();
		initListEncaissementsFactures();
		//On affiche le total des factures
		displayTotalFactures();
		if(cbSolde.isChecked()) cbSolde.setChecked(false);
	}

	/**
	 * Initialisation des listeners
	 */
	private void initListeners() {
		hPrintResultEncaissement=getHandlerPrintEncaissement();

		cbSolde.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					for(Facture facture : mFactures){
						if(!facture.getIsEnded())facture.setIsChecked(true);
						if(facture.getTypeFacture().equals(Facture.TYPE_AVOIR)) facture.setIsChecked(false);
					}
					mAdapter.notifyDataSetChanged();
				}else {
					for(Facture facture : mFactures){
						facture.setIsChecked(false);
					}
					mAdapter.notifyDataSetChanged();
				}

			}
		});

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

		lvFactures.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				CheckBox cb = (CheckBox) view.findViewById(R.id.cbFacture);

				if(cb.isChecked() == true) cb.setChecked(false);
				else cb.setChecked(true);

				mAdapter.notifyDataSetChanged();

			}
		});

		bRegler.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if(totalSelection > 0)
				{
					//MessageYesNo(getString(R.string.valider_reglement), R.string.commande_valider);
				}
				else if(totalSelection < 0){
					//total ne doit pas etre négatif
					Fonctions.makeSpecialToast(mContext, R.string.encaissement_montant_negatif, Toast.LENGTH_SHORT, Gravity.CENTER);	
					return;
				}

				//on check si le montant total est supérieur à l'encours + les factures du jours si oui on jette
				structClient client = new structClient();
				Global.dbClient.getClient(codeClient, client, new StringBuilder());

				dbKD730FacturesDues due=new dbKD730FacturesDues(m_appState.m_db);
 				float f=due.sumLocalFA(codeClient);
				float totalEncoursClient = Fonctions.GetStringToFloatDanem(client.MONTANTTOTALENCOURS)+f;

				if(totalSelection <= totalEncoursClient){
					boolean isAvoirChecked = false;

					if(!isAvoirChecked)
						launchEncaissement(false);
				}else{
					//encours du client dépassé
					AlertMessage(getString(R.string.encours_du_client_depasse)+" Encours: "
					+Fonctions.GetFloatToStringFormatDanem(totalEncoursClient, "0.00"), false);
				}
			}
		});

	}

	/**
	 * Initialisation des éléments graphiques
	 */
	private void initGui() {
		tvNomClient = (TextView) findViewById(R.id.tvNomClient);
		lvFactures = (ListView) findViewById(R.id.lvFactures);
		tvCodeClient = (TextView) findViewById(R.id.tvCodeClient);	
		tvTotalFactures = (TextView) findViewById(R.id.tvTotalFactures);
		tvTotalSelection = (TextView) findViewById(R.id.tvTotalSelection);
		cbSolde = (CheckBox) findViewById(R.id.cbSolde);
		lvEncaissement = (ListView) findViewById(R.id.lvEncaissement);
		bRegler = (Button) findViewById(R.id.bRegler);
	}

	/**
	 * Initialise le nom du client
	 */
	private void initClient() {
		if(client != null){
			tvNomClient.setText(client.NOM);
			tvCodeClient.setText(client.CODE);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {


		addMenu(menu, R.string.commande_valider, android.R.drawable.ic_menu_add);
		addMenu(menu, R.string.commande_print,R.drawable.print_icon);
		addMenu(menu, R.string.encaissement_print_facture,R.drawable.print_icon);
		addMenu(menu, R.string.commande_annuler,R.drawable.action_close);
		addMenu(menu, R.string.enregistrer_acompte, android.R.drawable.ic_menu_save);
		addMenu(menu, R.string.comment, android.R.drawable.ic_menu_info_details);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case R.string.comment:
			LaunchComment(commentaire, codeClient, 250);
			return true;
			
		case R.string.enregistrer_acompte:
			 if (bRegler.getVisibility()==View.INVISIBLE)
			 {
				 
			 }
			 else
				 launchEncaissement(true);
			return true;
		case R.string.commande_print:
			MessageYesNo(getString(R.string.commande_print), R.string.commande_print);
			return true;
		case R.string.encaissement_print_facture:
			MessageYesNo(getString(R.string.encaissement_print_facture), R.string.encaissement_print_facture);
			return true;
		case R.string.commande_valider:
			if(mEncaissements != null && mEncaissements.size() > 0){
				if (Global.dbKDEncaissement.isPrintOk(codeClient)  == false && m_problemPrinter==false ) {
					AlertMessage(getString(R.string.commande_notPrinted), false);
					return true;
				}
				validerEncaissement();
				//				if(m_isPrinted || m_problemPrinter) validerEncaissement();
				//				else AlertMessage(getString(R.string.commande_notPrinted), false);
			}
			else AlertMessage(getString(R.string.commande_noLines), false);
			return true;
		case R.string.commande_annuler:
			if(mEncaissements != null && mEncaissements.size() > 0) {
				if (Global.dbKDEncaissement.isPrintOk(codeClient)  == false){
					MessageYesNo(getString(R.string.commande_annuler_msg), R.string.commande_annuler);
				}else{
					AlertMessage(getString(R.string.annulation_impossible), false);
				}
			}
			else finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
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
				case R.string.commande_print:
					//on envoie l'impression
					launchPrinting();
					break;
				case R.string.encaissement_print_facture:
					//on envoie l'impression
					launchPrintingReleveFacture();
					break;
				case Encaissement.TYPE_DELETE_ENCAISSEMENT:
					if(!identifiantEncaissementDelete.equals("") && numSouche == null){
						boolean success = Encaissement.deleteEncaissement(mContext, identifiantEncaissementDelete, codeClient, Encaissement.NON_VALIDE);
						identifiantEncaissementDelete = "";
						initListEncaissementsFactures();
						if(success) Fonctions.makeSpecialToast(mContext, R.string.success_delete_encaissement, Toast.LENGTH_SHORT, Gravity.CENTER);
					}else{
						AlertMessage(getString(R.string.annulation_impossible), false);
					}
					break;
				case R.string.commande_annuler:

					if(numSouche == null){
						//suppression des encaissements par la liste
						int mEncaissementsLength = mEncaissements.size();
						int cpt = 0;
						for(Encaissement e : mEncaissements){
							boolean success = Encaissement.deleteEncaissement(mContext, e.getIdentifiant(), codeClient, Encaissement.NON_VALIDE);
							if(success) cpt++;
						}

						if(cpt == mEncaissementsLength){
							Fonctions.makeSpecialToast(mContext, R.string.success_delete_encaissement, Toast.LENGTH_SHORT, Gravity.CENTER);
							finish();
						}else {
							Fonctions.makeSpecialToast(mContext, R.string.erreur_survenu, Toast.LENGTH_SHORT, Gravity.CENTER);
						}
					}else{
						AlertMessage(getString(R.string.annulation_impossible), false);
					}
					break;
				case R.string.commande_valider:
					//validerEncaissement();
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
	 * Affiche les différents totaux en haut de l'écran
	 */
	private void displayTotaux(float total){
		tvTotalSelection.setText(Fonctions.GetDoubleToStringFormatDanem(Math.round(total*100.0)/100.0, "0.00"));
		totalSelection = (float) (Math.round(total*100.0)/100.0);
	}

	private void displayTotalFactures(){
		if(mFactures != null && mFactures.size() > 0){
			float total = 0;
			for(Facture facture : mFactures){
				//				if(!facture.isFactureDue){
				//					if(facture.getTypeFacture().equals(Facture.TYPE_AVOIR)){
				//						total -= Math.round(facture.getMontantTTC()*100.0)/100.0;
				//					}else total += Math.round(facture.getMontantDu()*100.0)/100.0;
				//				}
				//				else total += Math.round(facture.getMontantDu()*100.0)/100.0;

				if(facture.getTypeFacture() != null && facture.getTypeFacture().equals(Facture.TYPE_AVOIR)) total -= Math.round(facture.getMontantTTC()*100.0)/100.0;
				else total += Math.round(facture.getMontantTTC()*100.0)/100.0;
			}
			tvTotalFactures.setText(Fonctions.GetDoubleToStringFormatDanem(Math.round(total*100.0)/100.0, "0.00"));
		}
	}

	private void launchEncaissement(boolean isAcompte){
		//on récupère les numéros de factures qui ont été sélectionnées
		ArrayList<String> list = new ArrayList<String>();
		for(Facture facture : mFactures){
			if(facture.getIsChecked()) list.add(facture.getNumDoc());
		}

		Intent intent = new Intent(mContext, EncaissementActivity.class);
		Bundle b = new Bundle();
		b.putString(Espresso.CODE_CLIENT, client.CODE);
		b.putString(Espresso.CODE_SOCIETE, client.SOC_CODE);
		if(!isAcompte) b.putStringArrayList(NUMEROS_FACTURE, list);
		else b.putStringArrayList(NUMEROS_FACTURE, new ArrayList<String>());
		intent.putExtras(b);
		startActivity(intent);
	}

	private void initListEncaissementsFactures() {
		mEncaissements = new ArrayList<Encaissement>();
		mFactures = new ArrayList<Facture>();

		//on récupère les factures venant de kems83
		mFactures = Facture.getFacturesFromDB(client.CODE);

		//	if(mFactures.size() > 0) mFactures.get(0).setMontantDu((float) 106.01);

		//on tri la liste par ordre décroissant
		Collections.sort(mFactures, Facture.Comparators.DATE);

		//récupération des encaissement et alimentation de la liste des encaissements
		//on créé une facture avec NOFACTURE en numéro de document pour récupérer les encaissements libre (sans factures associées)
		//problème l'encaissement libre n'est pas lié à une facture et donc nécessite un lien avec le code client
		Facture noFacture = new Facture();
		noFacture.setNumDoc(Encaissement.ENCAISSEMENT_NOFACTURE);
		mFactures.add(noFacture);
		mEncaissements = getListEncaissements(mFactures, codeClient, Encaissement.NON_VALIDE);

		//on filtre les factures qui sont deja payés par rapport aux encaissements
		mFactures = Encaissement.getFactureNonRegleFromListFactures(mFactures);

		//on attribut les couleurs 
		Encaissement.attributeColorToEncaissementFacture(mEncaissements, mFactures);

		if(mEncaissements != null){
			mAdapterEncaissement = new EncaissementAdapter(this, R.layout.item_list_encaissement, mEncaissements, true);
			lvEncaissement.setAdapter(mAdapterEncaissement);
		}

		//on retire la facture de type nofacture de la liste
		for(Facture fac : mFactures){
			if(fac.getNumDoc().equals(Encaissement.ENCAISSEMENT_NOFACTURE)) mFactures.remove(fac);
		}

		//on créé la liste
		mAdapter = new FacturesAdapter(this, R.layout.item_lv_factures, mFactures, new OnFactureCheck() {

			@Override
			public void onCheck(float total) {			
				displayTotaux(total);
				mAdapter.notifyDataSetChanged();
			}
		});
		lvFactures.setAdapter(mAdapter);

	}

	/**
	 * Récupération de la liste des encaissements pour les numéros de factures passés en paramètre
	 * @return
	 */
	private ArrayList<Encaissement> getListEncaissements(ArrayList<Facture> factures, String codeClient, String validation) {
		ArrayList<Encaissement> list = new ArrayList<Encaissement>();

		for(Facture facture : factures){
			ArrayList<Encaissement> encaissements = Encaissement.getEncaissementFromFacture(facture, codeClient, validation);			
			for(Encaissement e : encaissements){
				if(!Encaissement.containsEncaissement(list, e)) list.add(e);
			}
		}

		return list;
	}

	//	private boolean saveEncaissementAvoir(){
	//
	//		Encaissement encaissement;
	//		int cpt = 0;
	//		for(Facture fac : mFactures){		
	//			if(fac.getTypeFacture().equals(Facture.TYPE_AVOIR) && fac.getIsChecked()){
	//				encaissement = new Encaissement();
	//				encaissement.setTypePaiement(fac.getTypeFacture());
	//				encaissement.setMontant(fac.getMontantDu());
	//				encaissement.setDate(Fonctions.getYYYYMMDD());
	//
	//				ArrayList<String> numerosFacture = new ArrayList<String>();
	//				for(Facture facture : mFactures){
	//					if(facture.getIsChecked() && !facture.getTypeFacture().equals(Facture.TYPE_AVOIR)) numerosFacture.add(facture.getNumDoc());
	//				}
	//
	//				TableSouches souche=new TableSouches(m_appState.m_db,this);
	//				String numEncaissement =  souche.get(TableSouches.TYPEDOC_REGLEMENT, Preferences.getValue(this, Espresso.LOGIN, "0"));
	//				souche.incNum(Preferences.getValue(this, Espresso.LOGIN, "0"), TableSouches.TYPEDOC_REGLEMENT);
	//				encaissement.setIdentifiant(numEncaissement);
	//				encaissement = encaissement.saveEncaissement(numerosFacture);
	//				cpt++;
	//			}
	//		}
	//
	//		if(cpt > 0) return true;
	//		else return false;
	//	}

	void launchPrintingReleveFacture(){

		//si des factures sont coch�es alors on imprime les factures
		ArrayList<Facture> facturesAImprimer = new ArrayList<Facture>();

		for(Facture f : mFactures){
			if(f.getIsChecked() == true) facturesAImprimer.add(f);
		}

		if(facturesAImprimer.size() > 0){
			m_ProgressDialog=ProgressDialog.show(this, getString(R.string.communication_avec_l_imprimante), getString(R.string.patientez_));
			String mac=getPrinterMacAddress();
			BluetoothConnectionInsecureExample example = new BluetoothConnectionInsecureExample(hPrintResultEncaissement);
			Z420ModelReleveFacture model=new Z420ModelReleveFacture(this);
			String   zplData=model.getReleveFacture(facturesAImprimer, codeClient, Preferences.getValue(this, Espresso.LOGIN, "0"));
			if(zplData != null) example.sendZplOverBluetooth(mac,zplData,1);
			m_isPrintedReleveFacture = true;
		}else{
			//rien à imprimer
			AlertMessage(getString(R.string.rien_a_imprimer), false);
		}
	}

	void launchPrinting() {

		if(mEncaissements != null && mEncaissements.size() > 0){

			if(numSouche == null){
				TableSouches souche=new TableSouches(m_appState.m_db,this);
				numSouche =  souche.get(TableSouches.TYPEDOC_REGLEMENT, Preferences.getValue(this, Espresso.LOGIN, "0"));			
			}

			String numerosEncaissement  = "NumEnc:";

			int cpt = 0;
			int lenghtEnc = mEncaissements.size();
			for(Encaissement enc : mEncaissements){
				//on update les encaissements pour enregistrer le numero de souche
//				Global.dbKDEncaisserFacture.updateNumeroSoucheEncaissement(numSouche, enc.getIdentifiant());
				Global.dbKDEncaisserFacture.updateNumeroSoucheEncaissementEx(numSouche, enc.getListNumerosFactures());
				Global.dbKDEncaissement.updateNumeroSoucheEncaissement(numSouche, enc.getIdentifiant());
				enc.setNumeroSouche(numSouche);

				if(lenghtEnc == cpt) numerosEncaissement += enc.getIdentifiant();
				else numerosEncaissement += enc.getIdentifiant()+";";
				cpt++;
			}

			

			m_ProgressDialog=ProgressDialog.show(this, getString(R.string.communication_avec_l_imprimante), getString(R.string.patientez_));

			//Récupération des encaissements de type chèques à partir de la liste des encaissements
			ArrayList<Encaissement> encaissementsCheque = Encaissement.getEncaissementsFromTypePaiement(Encaissement.TYPE_CHEQUE, mEncaissements);

			//Construction de la liste des chèques à partir de la liste des encaissements
			ArrayList<Cheque> cheques = Cheque.getChequeFromEncaissements(encaissementsCheque);

			String mac=getPrinterMacAddress();
			BluetoothConnectionInsecureExample example = new BluetoothConnectionInsecureExample(hPrintResultEncaissement);
			Z420ModelEncaissement model=new Z420ModelEncaissement(this);
			String   zplData=model.getEncaissement(mEncaissements, cheques, codeClient, Preferences.getValue(this, Espresso.LOGIN, "0"),commentaire);
			if(zplData != null) example.sendZplOverBluetooth(mac,zplData,1);
		}else{
			//rien à imprimer
			AlertMessage(getString(R.string.rien_a_imprimer), false);
		}
	}

	private void validerEncaissement() {

		boolean problem = false;
		//on check l'impression
		if(Global.dbKDEncaissement.isPrintOk(codeClient)){
			//on parcourt encaissement pour récupérer le numéro de souche
			for(Encaissement enc : mEncaissements){
				if(mNumeroSouche.equals("")) mNumeroSouche = enc.getNumeroSouche();
				if(!mNumeroSouche.equals(enc.getNumeroSouche())) problem = true;
			}

			//on fait l'update
			if(!problem) Encaissement.updateValidation(mNumeroSouche, Encaissement.VALIDE,commentaire);
			else AlertMessage("Un problème est survenu!", false);
			m_isPrinted = false;
			numSouche = null;
			
			structClient cli=new structClient();
			cli.CODE=codeClient;
			CartoMapActivity.updateVisite(cli, true ,this);
			AlertMessage(getString(R.string.ce_document_sera_transmis_lors_du_retour_au_menu_principal), true);
		}else{
			AlertMessage(getString(R.string.commande_notPrinted), false);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK
				|| keyCode == KeyEvent.KEYCODE_HOME) {
			/*
			 * if( checkAll()==true) { if (saveCde()==true) finish(); }
			 */
			return false;
		}

		else
			return super.onKeyDown(keyCode, event);

	}

	public void AlertMessage(String message, final boolean quit) {

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(message)
		.setCancelable(false)
		.setPositiveButton(getString(android.R.string.ok),
				new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				if(quit) finish();
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}

	void setPrintOk()
	{
		dbKD98Encaissement reg=new dbKD98Encaissement(m_appState.m_db);
		

		if (reg.isPrintOk(codeClient)==false && numSouche != null)
		{
			reg.setPrint(codeClient,numSouche);
			//une fois imprimé on ne peux plus modifier
			bRegler.setVisibility(View.INVISIBLE);
			lvEncaissement.setEnabled(false);
			
			TableSouches souche=new TableSouches(m_appState.m_db,this);
			souche.incNum(Preferences.getValue(this, Espresso.LOGIN, "0"), TableSouches.TYPEDOC_REGLEMENT);

		}
	}

	Handler getHandlerPrintEncaissement() {
		Handler h = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (m_ProgressDialog!=null) m_ProgressDialog.dismiss();
				super.handleMessage(msg);
				Bundle bGet = msg.getData();
				if (msg.what!=BluetoothConnectionInsecureExample.ERRORMSG_OK)
				{				
					MessageYesNoPrint(BluetoothConnectionInsecureExample.getErrMsg(msg.what)+"\n\nAvez-vous un problème bloquant qui vous empèche d'imprimer ?", 433, getString(R.string.probl_me_d_impression));
				}
				else
				{
					setPrintOk();
					m_isPrinted = true;
				}

			}
		};
		return h;
	}

	public void MessageYesNoPrint(String message, int type,String title) {
		final int m_type = type;
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(message)
		.setCancelable(false)
		.setTitle(title)
		.setPositiveButton(getString(R.string.Yes),
				new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				switch (m_type) {
				case 433://probleme d'imprimante
					m_problemPrinter=true;
					promptText(getString(R.string.probl_me_d_impression),
							getString(R.string.vous_pouvez_valider_le_document_sans_imprimer), 
							false);

					setPrintOk();
					break;

				}

			}
		})
		.setNegativeButton(getString(R.string.No),
				new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				m_isPrinted = false;
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode==LAUNCH_COMMENTAIRE)
		{
			if (resultCode==RESULT_OK)
			{
				commentaire=data.getExtras().getString("newvalue");
				 
			}
		}
	}
}
