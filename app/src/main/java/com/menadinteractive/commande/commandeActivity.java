package com.menadinteractive.commande;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.menadinteractive.histo.HistoDocumentsActivity;
import com.menadinteractive.inventaire.ValidationInventaire;
import com.menadinteractive.maxpoilane.BaseActivity;
import com.menadinteractive.maxpoilane.DashboardActivity;
import com.menadinteractive.maxpoilane.ExternalStorage;
import com.menadinteractive.printmodels.BluetoothConnectionInsecureExample;
import com.menadinteractive.printmodels.Z420ModelFacture;
import com.menadinteractive.maxpoilane.R;
import com.menadinteractive.segafredo.carto.CartoMapActivity;
import com.menadinteractive.segafredo.carto.MenuPopup;
import com.menadinteractive.segafredo.client.ficheclient;
import com.menadinteractive.segafredo.communs.Fonctions;
import com.menadinteractive.segafredo.communs.Global;
import com.menadinteractive.segafredo.contactcli.ContactCliActivity;
import com.menadinteractive.segafredo.db.Preferences;
import com.menadinteractive.segafredo.db.TableClient;
import com.menadinteractive.segafredo.db.TableClient.structClient;
import com.menadinteractive.segafredo.db.TableSouches;
import com.menadinteractive.segafredo.db.dbKD545StockTheoSrv;
import com.menadinteractive.segafredo.db.dbKD730FacturesDues;
import com.menadinteractive.segafredo.db.dbKD83EntCde;
import com.menadinteractive.segafredo.db.dbKD83EntCde.structEntCde;
import com.menadinteractive.segafredo.db.dbKD84LinCde;
import com.menadinteractive.segafredo.db.dbSiteProduit;
import com.menadinteractive.segafredo.encaissement.ReglementActivity;
import com.menadinteractive.segafredo.encaissement.SignatureActivity;
import com.menadinteractive.segafredo.encaissement.SignatureView;
import com.menadinteractive.segafredo.plugins.Espresso;
import com.menadinteractive.segafredo.rapportactivite.rapportactivite;
import com.menadinteractive.segafredo.tasks.task_sendWSData;

public class commandeActivity extends BaseActivity 
implements OnItemSelectedListener
{
	String commentaire;
	String m_stemail;
	private listAdapter m_adapter;
	private ProgressDialog m_ProgressDialog = null;
	structClient structcli=new structClient();
//	boolean m_isPrinted = false;// est ce que l'on a "essayé" d'imprimer la cde
	ListView myListView;
	Button buttonSignature;
	LinearLayout llSig;
	boolean m_problemPrinter=false;//si on declare un probleme d'imprimante on laisse passer
	int top = 0;
	int index = 0;
	SignatureView mViewSignature;
	EditText etFilter;
	TextView tvTypeDoc;
	TextView textViewMntHTTitre;
	TextView textViewMntHTVal;
	
	
	boolean bInit=true;
	TextView tvStatus;
	boolean bpanier=false;
 
	int m_nbrPrint=0;

	ImageButton ibFilter,ibPanier,ibHabit;
	String heureDarrivee;
	String m_numcde;
	String m_codecli;
	String m_soccode;
	String m_typedoc;
	//TODO récupérer le bon taux à partir de params
	float m_dTVA = 0;

	Handler hPrintResult;
 
	ArrayList<Bundle> idFam = null;// les id qui servent a retrouver les pays
	String m_stFam = "" ;	//Filtre Fam (voir spinner)

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_commande); 

		bpanier=false;
		TableSouches souche=new TableSouches(m_appState.m_db,this);
		idFam = new ArrayList<Bundle>();

		Bundle bundle = this.getIntent().getExtras();
		m_codecli = getBundleValue(bundle,"codecli");
		m_soccode = getBundleValue(bundle,"soccode");
		m_typedoc = getBundleValue(bundle,"typedoc");
		m_numcde = getBundleValue(bundle,"numcde");
		Global.dbLog.Insert("CommandeActivity", "OnCreate", "", "Numcde:"
				+ m_numcde + " - Code client: " + m_codecli, "", "");
		
		
		Global.CODCLI_TEMP = m_codecli;
		if (m_codecli.equals(""))
			m_numcde="";
		else
		{
			if (m_numcde.equals(""))
				m_numcde =  souche.get(m_typedoc, Preferences.getValue(this, Espresso.LOGIN, "0"));
			TableClient cli=new TableClient(m_appState.m_db);
			cli.getClient(m_codecli, structcli,  new StringBuilder());
		}

		if (m_numcde.equals("") && m_codecli.equals("")==false) 
		{
			finish();
			return;
	
		}
		heureDarrivee=Fonctions.getYYYYMMDDhhmmss();
		initGUI();
		initListeners();
		fillFamille("");
		
		CalcAll();
	}

	void initGUI() {
 
		textViewMntHTTitre=(TextView)findViewById(R.id.textViewMntHTTitre);
		textViewMntHTVal=(TextView)findViewById(R.id.textViewMntHTVal);
		textViewMntHTVal.setVisibility(View.GONE);
		textViewMntHTTitre.setVisibility(View.GONE);
		
		// Create and attach the view that is responsible for painting.
		mViewSignature = new SignatureView(this, null);
		
		myListView = (ListView) findViewById(R.id.lv_4);
		myListView.setTextFilterEnabled(true);
		etFilter=(EditText)findViewById(R.id.etFilter);
		tvTypeDoc=(TextView)findViewById(R.id.textViewTypeDoc);
		tvStatus=(TextView)findViewById(R.id.tvStatus);
		ibFilter=(ImageButton)findViewById(R.id.ibFind);
		ibPanier=(ImageButton)findViewById(R.id.buttonPanier);
		ibHabit=(ImageButton)findViewById(R.id.buttonHabit);
		//ibPanier.setVisibility(View.VISIBLE);
		buttonSignature=(Button)findViewById(R.id.buttonSignature);
		//buttonSignature.setVisibility(View.GONE);
		llSig=(LinearLayout)findViewById(R.id.sig);;
		llSig.setVisibility(View.GONE);
		llSig.addView(mViewSignature);
		
		//MV 27/03/2015, semble ameliorer le scroll
		myListView.setCacheColorHint(Color.TRANSPARENT); // not sure if this is required for you. 
		myListView.setFastScrollEnabled(true);
		myListView.setScrollingCacheEnabled(false);
				
		tvTypeDoc.setText(structcli.NOM);
		m_stemail=Fonctions.GetStringDanem(structcli.EMAIL);
	//	m_dTVA = (float) Fonctions.GetStringToDoubleDanem(Global.dbParam
	//			.getLblAllSoc(Global.dbParam.PARAM_TVA, "V20"));

		PopulateList("",false,false);
 	}

	void initListeners() {
		hPrintResult=getHandler();
		
		myListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {

				index = myListView.getFirstVisiblePosition();
				View vw = myListView.getChildAt(0);
				top = (vw == null) ? 0 : vw.getTop();

				String codeart = arg1.getTag().toString();
				launchQtePrix(codeart);
				return;
			}
		});
	 
		Spinner spinner = (Spinner) findViewById(R.id.spinnerFam);
		spinner.setOnItemSelectedListener(this);

		ibFilter.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				bpanier=false;
				//buttonSignature.setVisibility(View.GONE);
				llSig.setVisibility(View.GONE);
				String filter=etFilter.getText().toString();
				PopulateList(filter,false,false);
				
			}
		});
		ibHabit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				 PopulateList("",false,true);
				 
			}
		});
		ibPanier.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				bpanier=true;
				etFilter.setVisibility(View.INVISIBLE);
				ibFilter.setVisibility(View.INVISIBLE);
				 PopulateList("",true,false);
				 CalcAll();
			}
		});
		
		buttonSignature.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				MessageYesNo(getString(R.string.commande_valider_msg),
						R.string.commande_valider,"Validation");
			}
		});
	}
	Handler getHandler() {
		Handler h = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (m_ProgressDialog!=null) m_ProgressDialog.dismiss();
				super.handleMessage(msg);
				Bundle bGet = msg.getData();
				if (msg.what!=BluetoothConnectionInsecureExample.ERRORMSG_OK)
				{
					
					MessageYesNo(BluetoothConnectionInsecureExample.getErrMsg(msg.what)+"\n\nAvez vous un problème bloquant qui vous empèche d'imprimer ?", 433, getString(R.string.probl_me_d_impression));
				}
				else
				{
					Global.dbKDEntCde.setPrintOK(m_numcde, m_codecli);
					//MessageYesNo("L'Impression s'est elle bien d�roul�e ?", 543, "Impression du document");
				}
					
			}
		};
		return h;
	}
	private void PopulateList(String filter,boolean isPanier,boolean isHabit) {

		ArrayList<dbSiteProduit.structArt> colNames = new ArrayList<dbSiteProduit.structArt>();

		this.m_adapter = new listAdapter(this, R.layout.item_commande2, colNames);

		myListView.setAdapter(this.m_adapter);

		ArrayList<dbSiteProduit.structArt> arts =null;
		//si recherhe filtre
		if (filter.equals("")==false)
		{
			arts=Global.dbProduit.getProduitsWithHisto(
					m_soccode,m_codecli, "", filter );
		}
		//si panier
		else if (isPanier /*m_stFam.equals(getString(R.string.commande_panier))*/==true)		
		{
			arts=Global.dbProduit.getProduitsCde(
					m_soccode, this.m_numcde);
		
		}
		//si tout
		else
			if (isHabit /*m_stFam.equals(getString(R.string.commande_tout))*/== true)			
			//	arts=Global.dbProduit.getProduits( m_soccode, "", "" );
				arts=Global.dbProduit.getProduitsWithHisto(
						m_soccode,m_codecli, "", "" );
		else//si famille
			//arts=Global.dbProduit.getProduits( 	m_soccode, m_stFam, "" );
			arts=Global.dbProduit.getProduitsWithHisto(
					m_soccode,m_codecli, m_stFam, "" );

		for (int v = 0; v < arts.size(); v++)
			colNames.add(arts.get(v));
		
		

	}

	private class listAdapter extends ArrayAdapter<dbSiteProduit.structArt> {

		private ArrayList<dbSiteProduit.structArt> items;

		public listAdapter(Context context, int textViewResourceId,
				ArrayList<dbSiteProduit.structArt> items) {
			super(context, textViewResourceId, items);
			this.items = items;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			if (v == null) {
				LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.item_commande2, null);
			}

			dbSiteProduit.structArt o = items.get(position);

			if (o != null) {


				 

				//RelativeLayout rl = (RelativeLayout) v.findViewById(R.id.rl_root);
				LinearLayout rl = (LinearLayout) v.findViewById(R.id.rl_root);
				LinearLayout ll = (LinearLayout) v.findViewById(R.id.ll_saisie);
				LinearLayout ll_taxes = (LinearLayout) v.findViewById(R.id.ll_taxes);
				LinearLayout ll_remises = (LinearLayout) v.findViewById(R.id.ll_remises);
				/*LinearLayout ll_taxes_1 = (LinearLayout) v.findViewById(R.id.ll_taxes_1);
				LinearLayout ll_taxes_2 = (LinearLayout) v.findViewById(R.id.ll_taxes_2);
				LinearLayout ll_taxes_3 = (LinearLayout) v.findViewById(R.id.ll_taxes_3);*/

				TextView tvCode = (TextView) v.findViewById(R.id.textCode);
				//if(!o.PCB.equals("")) tvCode.setText(o.CODART+" - PCB= "+o.PCB);
				//else tvCode.setText(o.CODART+" - PCB= 1");
				tvCode.setText(o.CODART);

				TextView tvDerDate = (TextView) v.findViewById(R.id.dateder);
				if( o.MAXDATE!=null &&   !o.MAXDATE.equals("")) tvDerDate.setText( "Der.: "+o.MAXDATE );
				else tvDerDate.setText("" );
				
				TextView tvLbl = (TextView) v.findViewById(R.id.textLbl); 
				tvLbl.setText(o.NOMART1);

				LinearLayout llPMF = (LinearLayout) v
						.findViewById(R.id.llPMF);

				TextView tvPrix = (TextView) v
						.findViewById(R.id.textViewPrixVal);
				TextView textViewTotHTTitre = (TextView) v
						.findViewById(R.id.textViewTotHTTitre);
				TextView textViewPrixTitre = (TextView) v
						.findViewById(R.id.textViewPrixTitre);
				
				
				TextView tvQteLivre = (TextView) v.findViewById(R.id.textViewQteLivre);
				TextView tvQteRetour = (TextView) v.findViewById(R.id.textViewQteRetour);
				TextView tvQteFacture = (TextView) v.findViewById(R.id.textViewQteFacture);
				
				TextView tvTotalHT = (TextView) v
						.findViewById(R.id.textViewTotHTVal);
				TextView tvPMF = (TextView) v.findViewById(R.id.textViewPMFVal);
				TextView tvTxTva = (TextView) v.findViewById(R.id.textViewTxTvaVal);

				TextView tv_taxe1 = (TextView) v.findViewById(R.id.textViewTaxe1);
				TextView tv_taxe2 = (TextView) v.findViewById(R.id.textViewTaxe2);
				TextView tv_taxe3 = (TextView) v.findViewById(R.id.textViewTaxe3);

				TextView tv_remise1 = (TextView) v.findViewById(R.id.textViewRemise1);
				TextView tv_remise2 = (TextView) v.findViewById(R.id.textViewRemise2);
				TextView tv_remise3 = (TextView) v.findViewById(R.id.textViewRemise3);
				if(bpanier==true)
				{
					ll_taxes.setVisibility(View.VISIBLE);
					textViewMntHTVal.setVisibility(View.VISIBLE);
					textViewMntHTTitre.setVisibility(View.VISIBLE);
					
					
					
				}
				else
				{
					textViewMntHTVal.setVisibility(View.GONE);
					textViewMntHTTitre.setVisibility(View.GONE);
					ll_taxes.setVisibility(View.GONE);
					
					
				}


				rl.setBackgroundColor(Color.TRANSPARENT);
				rl.setTag(o.CODART);
				dbKD84LinCde.structLinCde lin = new dbKD84LinCde.structLinCde();
				if (m_numcde.equals("")==false &&  Global.dbKDLinCde.load(lin, m_numcde, o.CODART, "0",
						new StringBuffer())) {
					rl.setBackgroundColor(Color.GREEN);
					ll.setVisibility(View.VISIBLE);
					tvPrix.setText(Fonctions.GetDoubleToStringFormatDanem(lin.PRIXMODIF,"0.00"));

					String stqteFact=String.valueOf((int)lin.QTECDE);					
					if (lin.QTEGR>0)
					{
						int qteFact=0;
						qteFact=(int)lin.QTECDE-(int)lin.QTEGR;
						stqteFact=Fonctions.getInToStringDanem(qteFact);
						
						//qte+="+"+String.valueOf((int)lin.QTEGR)+" off";
					}
					
					tvQteLivre.setText(String.valueOf((int)lin.QTECDE));
					tvQteRetour.setText(String.valueOf((int)lin.QTEGR));
					tvQteFacture.setText(stqteFact);
					

					//tvTotalHT.setText(Global.dbProduit.CalculLine(lin.QTECDE,Fonctions.convertToInt(lin.UV), lin.PRIXMODIF, 0));
				//	tvTotalHT.setText(Global.dbProduit.CalculLine(lin.QTECDE, lin.PRIXMODIF, 0));
					tvTotalHT.setText(Fonctions.GetDoubleToStringFormatDanem(lin.MNTTOTALHT,"0.00"));
					 
					tvTxTva.setText(Fonctions.GetDoubleToStringFormatDanem(lin.TAUX,"0.00")+"%");

					// PRIX MOYEN FACTUR�
					float pmf = CalcPrixMoyenAFacturer(lin.QTECDE,
							Fonctions.convertToInt(lin.UV), lin.QTEGR,
							Fonctions.convertToFloat(tvTotalHT.getText()
									.toString()));
					tvPMF.setText(String.valueOf(pmf));

					//TODO PRIX MOYEN FACTURE sans pcb
					if (lin.REMISEMODIF>0)
					{
						tv_remise1.setText("Remise: "+lin.REMISEMODIF+"%");
						tv_remise1.setVisibility(View.VISIBLE);
						ll_remises.setVisibility(View.VISIBLE);
					}
					else
					{
						tv_remise1.setVisibility(View.INVISIBLE);
						ll_remises.setVisibility(View.GONE);
					}
				} else{
					ll.setVisibility(View.GONE);
					//ll_taxes.setVisibility(View.GONE);
					ll_remises.setVisibility(View.GONE);
				}
			}
			return v;
		}

	}

	float CalcPrixMoyenAFacturer(float qte, int pcb, float qtegr, float mntLinHt) {
		float pmf = 0;
		try {
			pmf = (float) Fonctions.round(mntLinHt / (qte * pcb + qtegr * pcb),
					2);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return pmf;
	}

	void launchQtePrix(String codeart) {
		dbSiteProduit.structArt art = new dbSiteProduit.structArt();
		if (Global.dbProduit.getProduit(codeart, art, new StringBuilder())
				&& Global.dbKDEntCde.isPrintOk(m_numcde) == false) {

			Bundle b = new Bundle();
			b.putString("codeart", codeart);
			b.putString("lblart", art.NOMART1);
			//	b.putString("prix", art.PV_CONS);
			//	b.putString("qte", "");
			//	b.putString("grat", "");
			b.putString("numcde", m_numcde);
			b.putString("photoname", art.PHOTONAME);
			b.putString("typedoc", m_typedoc);
			b.putString("codcli", m_codecli);

			//tof: gestion TVA en fonction de l'article
			m_dTVA =  (float) Fonctions.GetStringToDoubleDanem(Global.dbParam
					.getLblAllSoc(Global.dbParam.PARAM_TVA, art.CODETVA));

			/*if(!Global.CODCLI_TEMP.equals("")){
				String taxe = Global.dbTarif.getTaxeOfTarif(Global.CODCLI_TEMP, art.CODART);
				m_dTVA = Float.parseFloat(taxe);
			}*/

			Intent intent = new Intent(commandeActivity.this,
					commandeInput.class); intent.putExtras(b);
					startActivityForResult(intent, LAUNCH_SAISIEQTE);


		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		hideKeyb();
		if(requestCode==10000)
		{
			
		}
		if (requestCode==LAUNCH_COMMENTAIRE)
		{
			if (resultCode==RESULT_OK)
			{
				commentaire=data.getExtras().getString("newvalue");
				Global.dbKDEntCde.saveComment(m_numcde, commentaire);
			}
		}
		if (requestCode==LAUNCH_EMAIL)
		{
			if (resultCode==RESULT_OK)
			{
				m_stemail=data.getExtras().getString("newvalue");
				Global.dbKDEntCde.saveEmail(m_numcde, m_stemail);
				Global.dbLog.Insert("CommandeActivity", "Save", "", "Numcde:"
						+ m_numcde + " - Code client: " + m_codecli, "", "");

				TableSouches souche=new TableSouches(m_appState.m_db,commandeActivity.this);
				String valeursouche=souche.SoucheNum(Preferences.getValue(commandeActivity.this, Espresso.LOGIN, "0"),m_typedoc);
				if(Fonctions.GetStringDanem(m_numcde).equals(valeursouche))
				{
					//incremente le numro de doc
					souche.incNum( Preferences.getValue(commandeActivity.this, Espresso.LOGIN, "0"),m_typedoc);

				}



				
				//on transfert la facture dans les factures dues
				if (m_typedoc.equals(TableSouches.TYPEDOC_FACTURE) || m_typedoc.equals(TableSouches.TYPEDOC_AVOIR))
				{
					dbKD83EntCde ent=new dbKD83EntCde(m_appState.m_db);
					structEntCde structent=new structEntCde();
					ent.load(structent, m_numcde, new StringBuffer(),false);
					dbKD730FacturesDues dues=new dbKD730FacturesDues(m_appState.m_db);
					dues.insert(structent);
				}
				
				DecrementeStock();
				boolean modetest=Preferences.getValueBoolean(this, Espresso.PREF_MODETEST, false);
				if (modetest)
				{
					
				}
				else
				{
					// on envoi la facture tt de suite
					task_sendWSData wsCde = new task_sendWSData(
							m_appState,null);
					wsCde.execute();

				}
				
				setResult(Activity.RESULT_OK, null);

				CartoMapActivity.updateVisite(structcli, true ,commandeActivity.this);	
				Quit();
			}
		}
		if (requestCode==LAUNCH_HISTO)
		{
			PopulateList("",true,false);
			 CalcAll();
		}
		//test
		if (requestCode == ACTIVITY_PRINT) {

			switch (resultCode) {
			case Activity.RESULT_CANCELED:
				Log.d("TAG", "onActivityResult, resultCode = CANCELED");
				break;
			case Activity.RESULT_FIRST_USER:
				Log.d("TAG", "onActivityResult, resultCode = FIRST_USER");
				break;
			case Activity.RESULT_OK:
				Log.d("TAG", "onActivityResult, resultCode = OK");
				break;
			}
		}

		if (requestCode == LAUNCH_SAISIEQTE) {
			
			if (m_codecli.equals("")==false && resultCode == Activity.RESULT_OK ) {
				bpanier=false;
				llSig.setVisibility(View.GONE);
				
				Bundle b = data.getExtras();
				Global.CODCLI_TEMP = m_codecli;


				saveLin(getLogin(), m_soccode, m_codecli, m_numcde, getBundleValue(b, "codeart"), getBundleValue(b, "lblart"), 
						 getBundleValue(b, "prix") , getBundleValue(b, "qte"), 
						 getBundleValue(b, "grat"), getBundleValue(b, "remise"), getBundleValue(b, "prixintit"), m_dTVA,m_typedoc,getBundleValue(b, "motif"));
				//PopulateList("",true,false);
				String filter=etFilter.getText().toString();
				PopulateList(filter,false,false);
				
				CalcAll();
				myListView.setSelectionFromTop(index, top);
			}
			else
			{
				
			}
		}

	}

	public static void saveLin(String rep,String m_soccode,String m_codecli,
			String m_numcde,String codeart,String lblart ,
			String pubrut, String qte,String grat,String remise,String prixinit,float tva,String typedoc,String motif)
	{
		dbKD84LinCde.structLinCde lin = new dbKD84LinCde.structLinCde();

		lin.TAXE1 = 0.0f;
		lin.TAXE2 = 0.0f;
		lin.TAXE3 = 0.0f;

		lin.SOCCODE=m_soccode;
		lin.CLICODE=m_codecli;
		lin.CDECODE = m_numcde;
 
		lin.DESIGNATION = lblart;
		lin.PROCODE = codeart;
		lin.PRIXMODIF = Fonctions.convertToFloat( pubrut);
		lin.QTECDE = Fonctions.convertToFloat(qte);
		lin.QTEGR = Fonctions.convertToFloat(grat);
		lin.REMISEMODIF=Fonctions.convertToFloat(remise);
		lin.DATE=Fonctions.getYYYYMMDDhhmmss();
		lin.MNTUNITHT = lin.PRIXMODIF;
		lin.TYPEPIECE=typedoc;
		lin.REPCODE =rep;
		lin.TAUX =      tva;
		lin.PRIXINITIAL =  prixinit;;
		lin.FIELD_LIGNECDE_LINCHOIX1=motif;
		
		//calcul du prix net
		float fRemCalc =  lin.PRIXMODIF-(float)Fonctions.round((lin.PRIXMODIF* (lin.REMISEMODIF) / 100 ),2);	
		lin.MNTUNITNETHT = fRemCalc;
		//lin.REMISEINITIAL =  String.format("%.02f", fRemCalc );
		//lin.REMISEINITIAL = lin.REMISEINITIAL.replace(",", ".");
		//lin.REMISEINITIAL = "0.0";
		
		dbSiteProduit.structArt art = new dbSiteProduit.structArt();
		if (Global.dbProduit.getProduit(lin.PROCODE, art,
				new StringBuilder())) {
			lin.UV = art.UNVENTE.trim();
			//TODO calcul du montant du produit
			lin.MNTTOTALHT =  (float)Fonctions.round( (lin.QTECDE-lin.QTEGR)*(lin.PRIXMODIF-(lin.PRIXMODIF* (lin.REMISEMODIF) / 100 )),2) ;
			//lin.MNTTOTALHT =  lin.QTECDE*(float)Fonctions.round( (lin.PRIXMODIF-(lin.PRIXMODIF* (lin.REMISEMODIF) / 100 )),2) ;

			
			lin.CODETVA=art.CODETVA;
			lin.MNTTVA=(float) Fonctions.round( ((lin.MNTTOTALHT*tva)/100),2);

			lin.MNTTOTALTTC= lin.MNTTOTALHT+lin.MNTTVA ;
			if (lin.QTECDE == 0 && lin.QTEGR==0) {
				Global.dbKDLinCde.delete(m_numcde, lin.PROCODE, "0",
						new StringBuffer(), "");
			} else {
				if ( Global.dbKDLinCde.save(lin, m_numcde, lin.PROCODE,
						"0", new StringBuffer())) {

				} else {

				}
			}

			 
		}
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK
				|| keyCode == KeyEvent.KEYCODE_HOME) {
			/*
			 * if( checkAll()==true) { if (saveCde()==true) finish(); }
			 */
			if (m_numcde.equals(""))
				finish();
			return false;
		}

		else
			return super.onKeyDown(keyCode, event);

	}

	boolean checkAll() {
		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (m_numcde.equals("")==false)
		{
			//addMenu(menu, R.string.commande_print, R.drawable.print_icon);
			addMenu(menu, R.string.commande_valider, android.R.drawable.ic_menu_add);

			addMenu(menu, R.string.commande_annuler,
					android.R.drawable.ic_menu_close_clear_cancel);

			//addMenu(menu, R.string.commande_fichecli,
					//R.drawable.action_select_client);
	//		addMenu(menu, R.string.commande_quest,
	//				android.R.drawable.ic_menu_help);
		//	addMenu(menu, R.string.commande_Historique,
			//		R.drawable.action_select_client);
			//addMenu(menu, R.string.comment,
				//	android.R.drawable.ic_menu_info_details);
//			addMenu(menu, R.string.commande_Cadencier,
	//				R.drawable.action_select_client);
		//	addMenu(menu, R.string.commande_reglement,
		//			android.R.drawable.ic_menu_help);
		}
		else
		{
			addMenu(menu, R.string.commande_quitter,
					android.R.drawable.ic_menu_close_clear_cancel);
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case R.string.commande_print: 
			if (Global.dbKDLinCde.Count_Numcde(m_numcde, false)==0)
			{
				AlertMessage(getString(R.string.commande_noLines));
				return true;
			}
			saveCde(false);
			MessageYesNo(RecapDocument(),
					R.string.commande_print,"Résumé");
			// saveCde();
			// launchPrinting(item.getItemId());
			return true;
		case R.string.commande_valider:
			bpanier=true;
			 PopulateList("",true,false);
			 CalcAll();
			 llSig.setVisibility(View.VISIBLE);
			
			saveCde(true);
			
		
			return true;
		case R.string.commande_annuler:
			if(SignatureExiste()==true)
				AlertMessage(getString(R.string.commande_impossibledelete2));
			else
			{
				if (Global.dbKDEntCde.isPrintOk(m_numcde)  == false)
					MessageYesNo(getString(R.string.commande_annuler_msg),
							R.string.commande_annuler,"Annulation");
				else
					AlertMessage(getString(R.string.commande_impossibledelete));
			}
				
			

			return true;
		case R.string.commande_quitter:
			//finish();
			Quit();
			return true;
		case R.string.commande_fichecli:
			launchFiche(this,m_codecli,LAUNCH_FICHECLI);
			return true;
		case R.string.commande_Historique:
			 launchHisto(this, m_codecli, m_soccode,m_numcde,m_typedoc);
			return true;
		case R.string.comment:
			dbKD83EntCde ent=new dbKD83EntCde(m_appState.m_db);
			structEntCde sent=new structEntCde();
			if (ent.load(sent, m_numcde, new StringBuffer(), false))
				commentaire=sent.COMMENTCDE;
			
			LaunchComment( commentaire,m_codecli,250 );
			return true;
		
		 
		case R.string.commande_quest:
			MenuPopup.launchQuest(this,m_codecli,"","");
			return true;
		case R.string.commande_reglement:
			//MenuPopup.launchQuest(this,m_codecli,"","");
			//TODO reglement
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
		//return super.onOptionsItemSelected(item);
	}

	public void launchHisto(Context c,String codecli,String soccode,String numcdeforduplication,String typedocforduplication)
	{
		Intent intent = new Intent(c,		HistoDocumentsActivity.class);
		Bundle b=new Bundle();
		b.putString(Espresso.CODE_CLIENT,codecli);
		b.putString(Espresso.CODE_SOCIETE,soccode);
		b.putString("m_numDocForDuplication",numcdeforduplication);
		b.putString("m_typeDocForDuplication",typedocforduplication);
		intent.putExtras(b);
		startActivityForResult (intent,LAUNCH_HISTO);
		
	}
	void Quit()
	{
		finish();
//		MenuPopup.launchQuest(this,m_codecli,"","");
//		finish();
/*		
		Intent intent = new Intent(this, ReglementActivity.class);
		Bundle b = new Bundle();
		b.putString(Espresso.CODE_CLIENT, m_codecli);
		b.putString(Espresso.CODE_SOCIETE, m_soccode);
		intent.putExtras(b);
		startActivity(intent);
		*/
	}

	//on recapitule le document pour l'utilisateur
	String RecapDocument()
	{
		String recap="";
		recap="Code client: "+structcli.CODE;
		recap+="\n\nRaison sociale: "+structcli.NOM;
		recap+="\n"+"Enseigne: "+structcli.ENSEIGNE;
				
		dbKD83EntCde ent=new dbKD83EntCde(m_appState.m_db);
		structEntCde structent=new structEntCde();
		ent.load(structent, m_numcde, new StringBuffer(),false);
		
		recap+="\n\nDate du document: "+Fonctions.YYYYMMDD_to_dd_mm_yyyy(structent.DATECDE);
		recap+="\n"+"Date d'échéance: "+Fonctions.YYYYMMDD_to_dd_mm_yyyy(structent.ECHEANCE);
		recap+="\n\n"+"Numéro de document: "+m_numcde;
		recap+="\n\n"+"Total HT: "+Fonctions.GetDoubleToStringFormatDanem(structent.MNTHT,"0.00");
		recap+="\nTotal TVA: "+Fonctions.GetDoubleToStringFormatDanem(structent.MNTTVA1+structent.MNTTVA2,"0.00");
		recap+="\nTotal TTC: "+Fonctions.GetDoubleToStringFormatDanem(structent.MNTTC,"0.00");
		recap+="\n\n\n\n\n"+getString(R.string.commande_print_msg);
		//Type de document
		//nombre de lignes
		//cumul des quantit�
		//montant TTC
		
		return recap;
	}
	
	
	 
	static public void launchFiche(Activity c,String codecli,int id) {
		Intent i = new Intent(c, ficheclient.class);
		Bundle b = new Bundle();
		b.putString("numProspect",  codecli);
		i.putExtras(b);

		c.startActivityForResult(i, id);
	}
	static public void launchContact(Activity c,String codecli,int id) {
		Intent i = new Intent(c, ContactCliActivity.class);
		
		Bundle b = new Bundle();
		b.putString("codeclient",  codecli);
		i.putExtras(b);

		c.startActivityForResult(i, id);
	}
	
	

	boolean saveCde(boolean okToSendToServer) {
		try {
			int cntLine=Global.dbKDLinCde.Count_Numcde(m_numcde, false);
			if (cntLine==0)
			{
			//	Fonctions.FurtiveMessageBox(this, getString(R.string.commande_aucunesaisie));
				
				return false;
			}

			dbKD83EntCde.structEntCde ent = new dbKD83EntCde.structEntCde();

			ent.SOCCODE = getSocCode();
			if (okToSendToServer)
				ent.SEND = "1"; // par défaut 1 pour envoyé
			else
				ent.SEND="0";
			ent.CODEREP = getLogin();
			
			ent.LIVREUR=Fonctions.GetStringDanem(Preferences.getValue(commandeActivity.this, Espresso.LIVREUR, ""));

			ent.TYPEDOC=m_typedoc;
	 		
			ent.CODECLI = m_codecli;
			ent.REFCDE = "";
			ent.EMAILCLI = m_stemail;
			ent.ADRLIV = "";
			ent.REMISE1 = 0; // Fonctions.GetStringToFloatDanem(this.getTextViewText(this,
			// R.id.TexteRemise));
			ent.ESCOMPTE = "";
			ent.DATELIVR=ent.DATECDE = Fonctions.getYYYYMMDD();


			ent.COMMENTCDE = commentaire;
			ent.MNTHT = Fonctions.GetStringToFloatDanem(getTextViewText(this,
					R.id.textViewMntHTVal));

			//TODO calcul correct du montant de la tva
			ent.MNTTVA1 = Fonctions.GetStringToFloatDanem(this.getTextViewText(
					this, R.id.textViewMntTVAVal));

			ent.MNTTC = Fonctions.GetStringToFloatDanem(this.getTextViewText(
					this, R.id.textViewMntTTCVal));

			ent.R_REGL = structcli.MODEREGLEMENT;
			ent.R_COND = "";
			ent.R_NBJOURS = "";
			ent.R_CODEREGL = "";
	 
			String regledecalcalule=Global.dbParam.getComment(Global.dbParam.PARAM_MODEREGLEMENT,ent.R_REGL);
		    if (regledecalcalule.equals("")==false)
		    {
	 	    	
	 	    	ent.ECHEANCE =Global.dbParam.calcDateEcheance(Fonctions.getYYYYMMDD(),regledecalcalule);
		     }
		    else
		    {
		    	ent.ECHEANCE =  Fonctions.getYYYYMMDD();
		    }
		    
		    
			ent.PORT = "";
			ent.DEPOT =  Preferences.getValue(this, Espresso.DEPOT, "1");
			ent.ETAT = "1";
			if (Global.dbKDEntCde.isPrintOk(m_numcde) ==false)
				ent.ISPRINT = "0";
			else
				ent.ISPRINT="1";
			StringBuffer sbVer = new StringBuffer();
			ent.VERSION = Fonctions.getInToStringDanem(Fonctions.getVersion(
					this, sbVer));// mv363

			ent.DATEHEUREDEBUT = heureDarrivee;//Fonctions.getYYYYMMDDhhmmss();
			ent.DATEHEUREFIN = Fonctions.getYYYYMMDDhhmmss();
			
			try {
				TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
				ent.IMEI = Fonctions.GetStringDanem(telephonyManager.getDeviceId());
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				ent.IMEI="";
				
				e.printStackTrace();
			}	
			try {
				//mac
				WifiManager manager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
				WifiInfo info = manager.getConnectionInfo();
				ent.MAC= Fonctions.GetStringDanem(info.getMacAddress());
			
			} catch (Exception e) {
				// TODO Auto-generated catch block
				ent.MAC="";
				
				e.printStackTrace();
			}

			if(Global.dbClient.getTypeClientValue(m_codecli).equals(Global.dbClient.CLI_TYPE_PROSPECT))
			{
				StringBuffer stBuf =new StringBuffer();
				Global.dbClient.saveUpdateTypeclient(Global.dbClient.CLI_TYPE_CLIENT,Fonctions.getYYYYMMDD(),m_codecli, stBuf);
			}
			if (!Global.dbKDEntCde.save(ent, m_numcde, new StringBuffer())) {

				return false;
			}

		
			
			 
					
			return true;
		} catch (Exception e) {
			// TODO: handle exception
		}
		return false;
	}

	/**
	 * calcul le mnt de la cde
	 */
	void CalcAll() {
		try {

			double dTotalHT = 0;
			double dMntTVA = 0;
			double dTotalTTC = 0;

			dbKD84LinCde.structLinCde lin = new dbKD84LinCde.structLinCde();
			StringBuffer stBuf = new StringBuffer();
			dTotalHT = Global.dbKDLinCde.CalculTotalHT(  m_numcde);

			//dMntTVA = Global.dbProduit.Calculpourcent(dTotalHT, m_dTVA);
			dMntTVA = Global.dbKDLinCde.CalculTotalTVA(m_numcde);
			dTotalTTC = dTotalHT + dMntTVA;

			this.setTextViewText(this, R.id.textViewMntHTVal,
					Fonctions.GetDoubleToStringFormatDanem(dTotalHT, "0.00"));
			this.setTextViewText(this, R.id.textViewMntTVAVal,
					Fonctions.GetDoubleToStringFormatDanem(dMntTVA, "0.00"));
			this.setTextViewText(this, R.id.textViewMntTTCVal,
					Fonctions.GetDoubleToStringFormatDanem(dTotalTTC, "0.00"));

			saveCde(false);
			
			int nbruv=Global.dbKDLinCde.CalculNbrUV(m_numcde);
			int nbrlin=Global.dbKDLinCde.Count_Numcde(m_numcde, false);
			tvStatus.setText(nbrlin+" ligne(s), "+nbruv+" Facturée(s)");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	void launchPrinting(boolean duplicata,int nbrPrint) {

		
		m_ProgressDialog=ProgressDialog.show(this, getString(R.string.communication_avec_l_imprimante), getString(R.string.patientez_));

		 
		String mac=getPrinterMacAddress();
		   BluetoothConnectionInsecureExample example = new BluetoothConnectionInsecureExample(hPrintResult);
              Z420ModelFacture z=new Z420ModelFacture(this);
              String   zplData=z.getFacture(m_numcde,m_codecli,duplicata,m_typedoc);
	        example.sendZplOverBluetooth(mac,zplData,nbrPrint);
	        
	        
	        launchPrintPreview(zplData);	
		 
	
	}

	void deleteCde() {
		if (Global.dbKDEntCde
				.deleteCde(m_numcde, m_codecli, new StringBuffer()) == false) {
			Fonctions.FurtiveMessageBox(this, "err");
		}
	}

	public void MessageYesNo(String message, int type,String title) {
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
					break;
				case R.string.commande_print:
					saveCde(false);
					choiceNbrPrint();
				//	launchPrinting(false);
					
				
					/*Intent intent = getSendToPrinterIntent(
		                    commandeActivity.this, new String[]{},
		                    0);*/

					// notify the activity on return (will need to ask the user for
					// approvel)
					//startActivityForResult(intent, ACTIVITY_PRINT);
					break;
				case R.string.commande_valider:
					if (saveCde(true)==false) return;
					Bundle bundle = new Bundle();
					bundle.putString("filename", "bl_"+m_numcde);

					 //save signature
					saveSignature();
					
					if(SignatureExiste()==true)
					{
						//Après signature
						//Email
						dbKD83EntCde ent=new dbKD83EntCde(m_appState.m_db);
						structEntCde sent=new structEntCde();
						m_stemail=Fonctions.GetStringDanem(structcli.EMAIL);
						if (ent.load(sent, m_numcde, new StringBuffer(), false))
							m_stemail=Fonctions.GetStringDanem(sent.EMAILCLI);
						
						LaunchEmail(m_stemail,m_codecli,250 );
					}
					else
						Fonctions.FurtiveMessageBox(commandeActivity.this, "Veuillez signer le BL.");
					
				
					
					
/*
					//incremente le numro de doc
					
					//on transfert la facture dans les factures dues
					if (m_typedoc.equals(TableSouches.TYPEDOC_FACTURE) || m_typedoc.equals(TableSouches.TYPEDOC_AVOIR))
					{
						dbKD83EntCde ent=new dbKD83EntCde(m_appState.m_db);
						structEntCde structent=new structEntCde();
						ent.load(structent, m_numcde, new StringBuffer(),false);
						dbKD730FacturesDues dues=new dbKD730FacturesDues(m_appState.m_db);
						dues.insert(structent);
					}
					
					DecrementeStock();
					// on envoi la facture tt de suite
					task_sendWSData wsCde = new task_sendWSData(
							m_appState,null);
					wsCde.execute();

					setResult(Activity.RESULT_OK, null);

					CartoMapActivity.updateVisite(structcli, true ,commandeActivity.this);	
					Quit();
					*/
					break;
				case R.string.commande_annuler:
					deleteCde();
					Global.dbLog.Insert("CommandeActivity", "Annuler", "", "Numcde:"
							+ m_numcde + " - Code client: " + m_codecli, "", "");
				
					setResult(Activity.RESULT_CANCELED, null);
					Quit();
					//finish();
					break;
				}

			}
		})
		.setNegativeButton(getString(R.string.No),
				new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {

			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}

	void DecrementeStock()
	{
		dbKD545StockTheoSrv theo=new dbKD545StockTheoSrv(getDB());
		theo.decrementeStock(m_numcde,this);
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

	public static class DatePickerFragment extends DialogFragment implements
	DatePickerDialog.OnDateSetListener {

		Handler DateLivr;
		public DatePickerFragment(Handler b)
		{
			DateLivr=b;
		}
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Use the current date as the default date in the picker
			final Calendar c = Calendar.getInstance();
			int year = c.get(Calendar.YEAR);
			int month = c.get(Calendar.MONTH)+1;
			int day = c.get(Calendar.DAY_OF_MONTH);

			long time = c.getTimeInMillis();

			DatePickerDialog dlg=new DatePickerDialog(getActivity(), this, year, month, day);
			//dlg.getDatePicker().setMinDate(time);

			return dlg;
		}

		public void onDateSet(DatePicker view, int year, int month, int day) {
			Message msg=new Message();
			msg.what=2;
			Bundle b=new Bundle();
			b.putString("date",Fonctions.ymd_to_YYYYMMDD(year, month+1, day));
			msg.setData(b);
			DateLivr.sendMessage(msg);
		}
	}

	 
	void fillFamille(String selVal) {
		try {
			//			if (Global.dbParam.getRecordsFiltreAllSoc(Global.dbParam.PARAM_FAM1,this.idFam, "1") == true) {
			if (Global.dbParam.getFamActives(idFam)  == true) {

				Bundle bundle = new Bundle();
/*				if (m_numcde.equals("")==false)
				{
					//on inser la famille 'panier'

					bundle.putString(Global.dbParam.FLD_PARAM_CODEREC,getString(R.string.commande_panier));
					bundle.putString(Global.dbParam.FLD_PARAM_LBL, getString(R.string.commande_panier));
					bundle.putString(Global.dbParam.FLD_PARAM_COMMENT, getString(R.string.commande_panier));
					bundle.putString(Global.dbParam.FLD_PARAM_VALUE, getString(R.string.commande_panier));
					idFam.add(0,bundle);
				}
				*/
				//on inser la famille 'panier'

				bundle = new Bundle();
				bundle.putString(Global.dbParam.FLD_PARAM_CODEREC,getString(R.string.commande_tout));
				bundle.putString(Global.dbParam.FLD_PARAM_LBL, "---");
				bundle.putString(Global.dbParam.FLD_PARAM_COMMENT, getString(R.string.commande_tout));
				bundle.putString(Global.dbParam.FLD_PARAM_VALUE, getString(R.string.commande_tout));
				idFam.add(0,bundle);
				
				
				int pos = 0;
				String[] items = new String[idFam.size()];
				for (int i = 0; i < idFam.size(); i++) {

					items[i] = idFam.get(i).getString(
							Global.dbParam.FLD_PARAM_LBL);

					String codeRec = idFam.get(i).getString(
							Global.dbParam.FLD_PARAM_CODEREC);

					if (selVal.equals(codeRec)) {
						pos = i;
					}

				}


				Spinner spinner = (Spinner) findViewById(R.id.spinnerFam);

				ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
						android.R.layout.simple_spinner_item, items);
				adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				spinner.setAdapter(adapter);

				spinner.setSelection(pos);

			}

		} catch (Exception ex) {

		}

	}
	public void onItemSelected(AdapterView<?> parent, View view, 
			int pos, long id) {
		// An item was selected. You can retrieve the selected item using
		// parent.getItemAtPosition(pos)
		m_stFam = idFam.get(pos).getString(
				Global.dbParam.FLD_PARAM_CODEREC);
		
		if (m_stFam.equals(getString(R.string.commande_tout)))
			m_stFam=getString(R.string.commande_tout);
			//return;
		//m_stFam = "" ;		//6546876546: DEBUGAGE
		PopulateList("",false,false) ;
	}
	public void onNothingSelected(AdapterView<?> parent) {
		// Another interface callback
	}

	//test impression
	private static int ACTIVITY_PRINT = 127;

	public static Intent getSendToPrinterIntent(Context ctx, String[] fileFullPaths, int indexToPrint){
		Intent sendIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);

		// This type means we can send either JPEG, or PNG
		sendIntent.setType("image/*");

		ArrayList<Uri> uris = new ArrayList<Uri>();

		//File fileIn = new File(fileFullPaths[indexToPrint]);
		File fileIn = new File("/sdcard/Pictures/Screenshots/Screenshot_2013-04-24-21-08-04.png");
		Uri u = Uri.fromFile(fileIn);
		uris.add(u);

		sendIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);

		return sendIntent;
	}

	/*public void printPDF(final PrintModel printModel, final File f){
		Uri printFileUri = Uri.fromFile(f);
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setPackage("com.dynamixsoftware.printershare");
		i.setDataAndType(printFileUri,"text/plain");
		startActivity(i);
	}*/
	public void printPDF(Uri uri){
		//Uri printFileUri = Uri.parse(path);
		String packageName = this.getPrinterSharePackageName();

		if(!packageName.equals("")){
			Intent i = new Intent(Intent.ACTION_VIEW);	
			i.setPackage(packageName);
			i.setDataAndType(uri,"text/plain");
			commandeActivity.this.startActivity(i);
		}
	}

	//on récupère le package printershare
	public String getPrinterSharePackageName(){
		ArrayList<String> list = this.getInstalledApps(false);
		for (String name : list) {
			if(name.contains("com.dynamixsoftware.printershare")) return name;
		}
		return "";
	}

	//on récupère la liste des packages
	public ArrayList<String> getInstalledApps(boolean getSysPackages) {     

		List<PackageInfo> packs = getPackageManager().getInstalledPackages(0);
		ArrayList<String> listePackageName = new ArrayList<String>();

		for(int i=0;i<packs.size();i++) {
			PackageInfo p = packs.get(i);

			if ((!getSysPackages) && (p.versionName == null)) {
				continue ;
			}
			//newInfo.pname = p.packageName;
			listePackageName.add(p.packageName);
		}

		return listePackageName;
	}

	/*private void generateA4Pdf(PrintModel printModel){		
		String pdfcontent = PDFWriterManager.getInstance().generatePDF(printModel);
		File f = outputToFile(printModel.getNumeroBonDeCommande()+".pdf",pdfcontent,"ISO-8859-1");
	}*/

	/*private File outputToFile(String fileName, String pdfContent, String encoding) {
		String PATH = Environment.getExternalStorageDirectory() + "/VDS/";
		try{
			File file = new File(PATH);
			file.mkdirs();
		}
		catch(Exception e){

		}
		File newFile = new File(PATH+"/"+fileName);
		try {
			newFile.createNewFile();
			try {
				FileOutputStream pdfFile = new FileOutputStream(newFile);
				pdfFile.write(pdfContent.getBytes(encoding));
				pdfFile.close();
			} catch(FileNotFoundException e) {
				//
			}
		} catch(IOException e) {
			//
		}
		return newFile;
	}*/
	static public void launchRapport(Activity c,String codecli,int id) {

		Intent i = new Intent(c, rapportactivite.class);


		Bundle b = new Bundle();

		b.putString("codeclient",  codecli);

		i.putExtras(b);



		c.startActivityForResult(i, id);

		}
	
	void choiceNbrPrint()
	{
		CharSequence[] array = {"1", "2", "3", "4"};
 

	 	Builder builder = new AlertDialog.Builder(this); 
	    builder.setTitle("Nombre d'exemplaires")
	            .setSingleChoiceItems(array, 0, null )
	            .setPositiveButton(R.string.Valider, new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int whichButton) {
	                dialog.dismiss();
	                int selectedPosition = ((AlertDialog)dialog).getListView().getCheckedItemPosition();
	                launchPrinting(false,selectedPosition+1); 
	            }
	        });
		
	    AlertDialog alert = builder.create();
		alert.show();
	    return ;
	}
	void saveSignature()
	{
		mViewSignature.setDrawingCacheEnabled(true);
		Bitmap b = mViewSignature.getDrawingCache();


		ExternalStorage externalStorage = new ExternalStorage(this, false);
		
		File sdCard = Environment.getExternalStorageDirectory();
		File file = new File(externalStorage.getSignaturesFolder()+File.separator+ "bl_"+m_numcde+".jpg");
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(file);
			Boolean res= b.compress(CompressFormat.JPEG, 20, fos);

		} catch (FileNotFoundException e1) {
			Toast.makeText(this,e1.getLocalizedMessage(),Toast.LENGTH_LONG).show();
			e1.printStackTrace();
		}




	}
	Boolean SignatureExiste()
	{
		boolean bres =false;
	//	mView.setDrawingCacheEnabled(true);
		try {
			
			mViewSignature.setDrawingCacheEnabled(true);
			Bitmap b = mViewSignature.getDrawingCache();
			
			if (mViewSignature.isFilled() ==true)
			{
				return true;
			}
			else
				return false;
		

		/*	ExternalStorage externalStorage = new ExternalStorage(this, false);
			
			File sdCard = Environment.getExternalStorageDirectory();
			File file = new File(externalStorage.getSignaturesFolder()+File.separator+ "bl_"+m_numcde+".jpg");
			if(file.exists())
			{
				bres=true;
			}*/
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return bres; 



	}
}
