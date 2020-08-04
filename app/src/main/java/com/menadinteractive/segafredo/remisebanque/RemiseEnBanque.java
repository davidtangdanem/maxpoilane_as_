package com.menadinteractive.segafredo.remisebanque;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.menadinteractive.maxpoilane.BaseActivity;
import com.menadinteractive.maxpoilane.R;
import com.menadinteractive.segafredo.adapter.ChequeAdapter;
import com.menadinteractive.segafredo.adapter.ChequeAdapter.OnChequeCheck;
import com.menadinteractive.segafredo.communs.Fonctions;
import com.menadinteractive.segafredo.communs.Global;
import com.menadinteractive.segafredo.db.Preferences;
import com.menadinteractive.segafredo.db.dbKD981RetourBanqueEnt.structRetourBanque;
import com.menadinteractive.segafredo.db.dbKD982RetourBanqueLin.structRetourBanqueLin;
import com.menadinteractive.segafredo.encaissement.Encaissement;
import com.menadinteractive.segafredo.plugins.Espresso;

/**
 * Activity permettant de férer les remises en banque
 * La liste des chèques enregsitrés en base de données apparaît
 * Lors de la sélection des chèques de la liste, les totaux se mettent à jour
 * @author Damien
 *
 */
public class RemiseEnBanque extends BaseActivity implements OnClickListener{

	String numRemiseBanque = null;

	ListView lvCheques;
	TextView tvTotalSelectionCheque, tvTotalEspece, tvTotalADeposer;

	ArrayList<Encaissement> mEncaissementsCheque;
	ArrayList<Encaissement> mEncaissementsEspece;
	ArrayList<String> identifiants;

	ArrayList<Cheque> mCheques;

	Spinner sListeBanqueDepot;

	ChequeAdapter mAdapter;
	Button bDeposer;
	String m_SelectBanque="";
	CheckBox cbEspece;
	String m_MontantEspece="";
	String m_MontantCheque="";
	String m_MontantDeposer="";
	EditText etEmail;
	EditText etFax;

	ArrayList<Bundle> idListBanque = null;

	Context mContext;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_remise_en_banque);

		idListBanque = new ArrayList<Bundle>();
		identifiants = new ArrayList<String>();

		mContext = this;

		initGUI();
		initListeners();
		//Récupération des encaissements enregistrés de type chèque
		mEncaissementsCheque = Encaissement.getEncaissementsFromTypePaiement(Encaissement.TYPE_CHEQUE);

		//Récupération des encaissements enregistrés de type espèce
		mEncaissementsEspece = Encaissement.getEncaissementsFromTypePaiement(Encaissement.TYPE_ESPECE);

		//Construction de la liste des chèques à partir de la liste des encaissements
		mCheques = Cheque.getChequeFromEncaissements(mEncaissementsCheque);

		//Récupération du total des encaissements de type espèce

		if(mCheques != null && mCheques.size() > 0) initChequeAdapter();

		initTotalEspece(false);
		initTotalAdeposer();

		fillBanque("");
	}

	/**
	 * Initialisation du total des espèces
	 */
	private void initTotalEspece(boolean mrecup) {

		float total = 0;
		for(Encaissement enc : mEncaissementsEspece){
			total += Math.round(enc.getMontant()*100.0)/100.0;
		}

		tvTotalEspece.setText(Fonctions.GetFloatToStringFormatDanem(total, "0.00"));
		if(mrecup==true)
		{
			m_MontantEspece=Fonctions.GetFloatToStringFormatDanem(total, "0.00");
		}
		else
			m_MontantEspece="";

	}
	private void initTotalAdeposer() {

		float total = 0;
		total=Fonctions.GetStringToFloatDanem(m_MontantEspece)+Fonctions.GetStringToFloatDanem(m_MontantCheque);
		tvTotalADeposer.setText(Fonctions.GetFloatToStringFormatDanem(total, "0.00"));
		m_MontantDeposer=Fonctions.GetFloatToStringFormatDanem(total, "0.00");

	}

	/**
	 * Initialisation des éléments graphiques de l'écran
	 */
	private void initGUI() {
		lvCheques = (ListView) findViewById(R.id.lvCheques);

		tvTotalSelectionCheque = (TextView) findViewById(R.id.tvTotalSelectionCheque);
		tvTotalEspece = (TextView) findViewById(R.id.tvTotalEspece);
		tvTotalADeposer = (TextView) findViewById(R.id.tvTotalADeposer);
		bDeposer =(Button) findViewById(R.id.bDeposer);
		cbEspece=(CheckBox) findViewById(R.id.cbEspece);

		etEmail=(EditText) findViewById(R.id.etEmail);
		etFax=(EditText) findViewById(R.id.etFax);

		sListeBanqueDepot = (Spinner) findViewById(R.id.sListeBanqueDepot);
	}

	/**
	 * Initiatilisation des listeners
	 */
	private void initListeners() {
		bDeposer.setOnClickListener(this);

		cbEspece = (CheckBox) 
				findViewById(R.id.cbEspece);
		cbEspece.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				if (cbEspece.isChecked()) {
					
					initTotalEspece(true);

				} else {
					initTotalEspece(false);
				}
				initTotalAdeposer();

			}
		});
	}

	/**
	 * Initialisation de l'adapter contenant la liste des chèques
	 */
	private void initChequeAdapter() {

		//on créé la liste
		mAdapter = new ChequeAdapter(this, R.layout.item_list_cheques, mCheques, new OnChequeCheck() {

			@Override
			public void onCheck(float total) {			
				displayTotalCheque(total);
				initTotalAdeposer();
			}

		});
		lvCheques.setAdapter(mAdapter);
	}


	private void displayTotalCheque(float total) {
		tvTotalSelectionCheque.setText(Fonctions.GetFloatToStringFormatDanem(total, "0.00"));
		m_MontantCheque=Fonctions.GetFloatToStringFormatDanem(total, "0.00");
	}

	@Override
	public void onClick(View v) {
		if (v==bDeposer)
		{					
			//on vérifie si une banque a bien été choisie
			if(!getListBanque().equals("")){
				if(Fonctions.GetStringToFloatDanem(m_MontantDeposer) > 0){
					//MessageYesNo(getString(R.string.reb_validation_remise));
					save();

					Intent intent = new Intent(mContext, RecapitulatifRemiseEnBanque.class);
					Bundle b = new Bundle();
					b.putString("num_remise", numRemiseBanque);
					b.putStringArrayList("id_encaissement", identifiants);
					intent.putExtras(b);
					startActivity(intent);
				}else{
					//montant doit etre supérieur à 0
					AlertMessage(getString(R.string.reb_aucunmontant));
				}
			}
			else AlertMessage(getString(R.string.reb_banque_depot_obligatoire));

		}

	}
	@Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 1)
		{
			if (resultCode == Activity.RESULT_OK) {
				if ( data != null ){
					if(cbEspece.isChecked()){
						initTotalEspece(true);
					}
					else
						initTotalEspece(false);
					initTotalAdeposer();

					Bundle extras = data.getExtras();
					if ( !extras.getString("listbanque").equals("") ){
						m_SelectBanque=extras.getString("listbanque");
						save();
						finish();
					}
				}	
			}
		}
	}

	void save ()
	{
		String stChequeNumcheque="";
		String stChequeDate="";
		String stChequeBanque="";
		String stChequeMontant="";
		String stSoucheEncaissement="";

		String stChequeTypePaiement="";
		String stChequeIdentifiant="";

		String stnumcde=Global.dbKDRetourBanqueEnt.GetNumRetourBanque(Preferences.getValue(this, Espresso.LOGIN, "0"));
		structRetourBanque banque =new structRetourBanque();
		banque.NUMCDE=stnumcde;
		banque.IDENTIFIANT=Preferences.getValue(this, Espresso.LOGIN, "0");
		banque.CODEBANQUE=getListBanque();
		banque.DATE=Fonctions.getYYYYMMDD();
		banque.MNT_TOTAL=m_MontantDeposer;
		banque.MNT_CHEQUE=m_MontantCheque;
		banque.MNT_ESPECE=m_MontantEspece;
		banque.ETAT="0";
		banque.EMAIL= etEmail.getText().toString();
		banque.FAX=etFax.getText().toString();

		numRemiseBanque = banque.NUMCDE;

		Global.dbKDRetourBanqueEnt.insertRetourBanque(banque);
//		if(!m_MontantEspece.equals(""))
//			Global.dbKDEncaissement.updateRemiseEnBanqueOfEncaissement_Espece(Encaissement.TYPE_ESPECE);

		if(mCheques != null && mCheques.size() > 0){
			for(Cheque cheque : mCheques){
				if(cheque.isChecked()) 
				{
					//on garde en mémoire l'id du chèque
					identifiants.add(cheque.getIdentifiant());
					
					stChequeNumcheque=cheque.getNumeroCheque();
					stChequeDate=cheque.getDateCheque();
					stChequeBanque=cheque.getLibelleBanque();
					stChequeMontant =Fonctions.GetFloatToStringFormatDanem(cheque.getMontant(), "0.00");
					stChequeTypePaiement=Encaissement.TYPE_CHEQUE;
					stChequeIdentifiant=cheque.getIdentifiant();
					stSoucheEncaissement=cheque.getNumeroSouche();

					structRetourBanqueLin banquelin =new structRetourBanqueLin();
					banquelin.NUMCDE=stnumcde;
					banquelin.IDENTIFIANT=Preferences.getValue(this, Espresso.LOGIN, "0");
					banquelin.NOCHEQUE=stChequeIdentifiant;
					banquelin.ETAT="0";
					banquelin.NUMCHEQUE=stChequeNumcheque;
					banquelin.MONTANT=stChequeMontant;
					banquelin.BANQUE=stChequeBanque;
					banquelin.DATE=stChequeDate;
					banquelin.SOUCHEENCAISSEMENT=stSoucheEncaissement;

					Global.dbKDRetourBanqueLin.insertRetourBanqueLin(banquelin);

					//Global.dbKDEncaissement.updateRemiseEnBanqueOfEncaissement_Cheque(stChequeIdentifiant, Encaissement.REB_SAVE);

				}
			}
		}
		
		//on garde en mémoire les id encaissement espèce si espèce coché
		//également en sharedpreferences en cas de plantage
		if(cbEspece.isChecked()){
			for(Encaissement e : mEncaissementsEspece){
				identifiants.add(e.getIdentifiant());
			}
		}
		
		Set<String> set = new HashSet<String>();
		for(String id : identifiants){
			set.add(id);
		}
		SharedPreferences sharedpreferences = getSharedPreferences("myPreferences", Context.MODE_PRIVATE);
		Editor editor = sharedpreferences.edit();
		editor.putStringSet("sp_identifiants", set);
		editor.commit();
	}

	void fillBanque(String selVal) {
		try {
			//selVal=Global.dbClient.getCodeTypeEtablissement(selVal);

			if (Global.dbParam.getRecord2s(Global.dbParam.PARAM_BANQUEREMISE,
					this.idListBanque,true) == true) {

				int pos = 0;
				String[] items = new String[idListBanque.size()];
				for (int i = 0; i < idListBanque.size(); i++) {

					items[i] = idListBanque.get(i).getString(
							Global.dbParam.FLD_PARAM_LBL);

					String codeRec = idListBanque.get(i).getString(
							Global.dbParam.FLD_PARAM_CODEREC);

					if (selVal.equals(codeRec)) {
						pos = i;
					}

				}

				Spinner spinner = (Spinner) findViewById(R.id.sListeBanqueDepot);


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

	String getListBanque(){
		String pays="";
		int pos = this.getSpinnerSelectedIdx(this, R.id.sListeBanqueDepot);
		if (pos > -1)
			try {
				pays = idListBanque.get(pos).getString(
						Global.dbParam.FLD_PARAM_CODEREC);
			} catch (Exception e1) {
				e1.printStackTrace();
			}

		return pays;
	}

	public void MessageYesNo(String message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(message)
		.setCancelable(false)
		.setPositiveButton(getString(android.R.string.yes),
				new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {	
				//on sauvegarde 
				
				save();

				Intent intent = new Intent(mContext, RecapitulatifRemiseEnBanque.class);
				Bundle b = new Bundle();
				b.putString("num_remise", numRemiseBanque);
				b.putStringArrayList("id_encaissement", identifiants);
				intent.putExtras(b);
				startActivity(intent);

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

	public void AlertMessage(String message) {

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(message)
		.setCancelable(false)
		.setPositiveButton(getString(android.R.string.ok),
				new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {

			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}


}
