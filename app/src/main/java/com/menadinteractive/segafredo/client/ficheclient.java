package com.menadinteractive.segafredo.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.menadinteractive.commande.commandeActivity;
import com.menadinteractive.maxpoilane.BaseActivity;
import com.menadinteractive.maxpoilane.R;
import com.menadinteractive.segafredo.carto.MenuPopup;
import com.menadinteractive.segafredo.communs.Fonctions;
import com.menadinteractive.segafredo.communs.GPS;
import com.menadinteractive.segafredo.communs.Global;
import com.menadinteractive.segafredo.db.Preferences;
import com.menadinteractive.segafredo.db.SQLRequestHelper;
import com.menadinteractive.segafredo.db.TableClient;
import com.menadinteractive.segafredo.db.TableClient.structClient;
import com.menadinteractive.segafredo.plugins.Espresso;
import com.menadinteractive.segafredo.synchro.SynchroService;



public class ficheclient extends BaseActivity 
implements OnItemSelectedListener
{


	//String TypeEnv; //1-Creation client //2-Vivu
	String numProspect;
	String m_stCurZone ;
	String m_stCurJourPassage ;
	
	
	String m_stvaleurDebut;
	String m_stvaleurFin;
	
	Spinner spin_soc;
	ArrayList<String[]> arraySoc=null;
	TextView TextViewRaisonsocial ;
	TextView TextViewNom ;
	TextView TextViewAdresse1 ;
	TextView TextViewAdresse2 ;
	TextView TextViewcodepostal;
	TextView TextViewville ;
	TextView TextViewPays;	
	TextView TextViewTel;	
	TextView TextViewFax ;
	TextView TextViewContactgsm ;
	TextView TextViewEmail ;
	TextView TextViewSiret ;
	TextView TextViewCommentaire ;
	TextView TextViewlat ;
	TextView TextViewlon ;
	

	TextView TextViewCatCompta ;
	TextView TextViewTypePDV ;
	TextView TextViewNumTVA ;
	TextView TextViewZone ;
	TextView TextViewJourPassage ;

	TextView TexteEnseigne;
	TextView TexteType;
	TextView TexteGroupe;
	TextView TexteTypeEtablissement;
	TextView TexteAgent;
	TextView TexteJourFerm;
	TextView TexteSAV;
	TextView TexteModereglement;
	TextView TexteEncours;
	TextView TexteCircuit;
	
	TextView TexteFacturesdues;
	TextView TexteAvoirdispo;
	TextView TexteAvancepaie;
	
	EditText EditEncours;
	EditText EditFacturesdues;
	EditText EditAvoirdispo;
	EditText EditAvancepaie;
	EditText EditEnseigne;
	
	EditText EditLat;
	EditText EditLon;
	
	
	EditText Eville;
	EditText EAdresse1;
	EditText Ecodepostal;

	boolean m_bprendrecde=false;	
	ArrayList<Bundle> idPays = null;// les id qui servent a retrouver les pays

	ArrayList<Bundle> idListCatCompta = null;// categ compta pour la tva
	ArrayList<Bundle> idListTypePdv = null;// type de pdv
	ArrayList<Bundle> idListZone = null;// codetournee
	ArrayList<Bundle> idListJourPassage = null;// codetournee
	ArrayList<Bundle> idListTypeEtablissement = null;
	ArrayList<Bundle> idListType = null;// Type
	ArrayList<Bundle> idListGroupe = null;// Groupe
	ArrayList<Bundle> idListAgent= null;// Agent
	ArrayList<Bundle> idListJourFerm= null;// Jour Ferm
	ArrayList<Bundle> idListSAV= null;// SAV
	ArrayList<Bundle> idListModereglement= null;// mode reglement
	ArrayList<Bundle> idListCircuit= null;// Circuit
	
	private ProgressDialog m_ProgressDialog = null; 

	boolean bRaisonSocial=false ;
	boolean bNom=false ;
	boolean bAdresse1=false ;
	boolean bAdresse2 =false;
	boolean bcodepostal=false;
	boolean bville=false ;
	boolean bPays =false;
	boolean bTel=false;	
	boolean bFax =false;
	boolean bContactGSM=false ;
	boolean bEmail =false;
	boolean bSiret =false;
	boolean bCommentaire =false;

	boolean bCatCompta =false;
	boolean bTypePdv =false;
	boolean bZone=false;
	boolean bJourPassage=false;
	boolean bNumTVA=false;
	
	boolean bEnseigne=false;
	boolean bTypeclient=false;
	boolean bGroupeclient=false;
	boolean bJourFermeture=false;
	boolean bModereglement=false;
	boolean bSAV=false;
	
	
	
	boolean cancelAllowed;
	MyLocation whereAmI;

	boolean devinerAdresseAllowed=true;//on autorise le bouton que si c'est une creation sinon risque d'effacement de vraie adresse
	/** Task */
	task_geocodeClient gecodeClient = null;
	Handler handlerGeoCoder;
	Handler handlerRetrieveAddress;

	final int LAUNCH_ENTETECDE=33;
	final int LAUNCH_GEOLOCALISATION=724;
	
	String latitude="";
	String longitude="";

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);

		cancelAllowed=true;
		SynchroService.setPaused(true);

		handlerGeoCoder = getHandlerGeoCoder();
		handlerRetrieveAddress = getHandlerRetrieveAddress();

		idPays = new ArrayList<Bundle>();

		idListCatCompta= new ArrayList<Bundle>();
		idListTypePdv= new ArrayList<Bundle>();
		idListZone= new ArrayList<Bundle>();
		idListJourPassage= new ArrayList<Bundle>();
		idListType= new ArrayList<Bundle>();
		idListAgent= new ArrayList<Bundle>();
		idListJourFerm= new ArrayList<Bundle>();
		idListSAV= new ArrayList<Bundle>();
		idListModereglement= new ArrayList<Bundle>();
		idListCircuit= new ArrayList<Bundle>();
		idListGroupe= new ArrayList<Bundle>();
		idListTypeEtablissement= new ArrayList<Bundle>();

		Bundle bundle = this.getIntent().getExtras();
		numProspect = bundle.getString("numProspect");
		m_bprendrecde = Fonctions.convertToBool(getBundleValue(bundle,"prendrecde"));
		m_stCurZone =bundle.getString("curzone"); 

		setContentView(R.layout.activity_ficheclient);
		initActionBar();

		spin_soc= (Spinner) findViewById(R.id.spinnerCompany);
		fillSociete();

		InitTextView();

		Spinner spinner = (Spinner) findViewById(R.id.spinnerZone);
		spinner.setOnItemSelectedListener(this);


		Loadinformation(numProspect);


	}
	void Loadinformation(String numprospect)
	{
		cliToSave=new structClient();

		if(numprospect.equals(""))
		{
			numProspect=numprospect=Global.dbClient.GetCodeProspect(Preferences.getValue(this, Espresso.LOGIN, "0"));
			//tof: rechargement des données
			cliToSave.ZONE = m_stCurZone ;
			/*cliToSave.JOURPASSAGE = SQLRequestHelper.getCodeTournee(Fonctions
					.getYYYYMMDD());*/
			cliToSave.JOURPASSAGE = "J";
			findAdresse();
		}
		else
		{
			devinerAdresseAllowed=false;
			Global.dbClient .getClient(numprospect, cliToSave, new StringBuilder());
		}
		m_stCurJourPassage = cliToSave.JOURPASSAGE ;
		setEditViewText(this,R.id.EditCodeProspect,numProspect);


		this.setEditViewText(this,R.id.EditRaisonsocial,cliToSave.NOM);
		this.setEditViewText(this,R.id.EditNom,cliToSave.CONTACT_NOM);
		this.setEditViewText(this,R.id.EditContactemail,cliToSave.EMAIL);

		this.setEditViewText(this,R.id.EditAdresse1,cliToSave.ADR1);
		this.setEditViewText(this,R.id.EditAdresse2,cliToSave.ADR2);

		this.setEditViewText(this,R.id.EditCodepostal,cliToSave.CP);
		this.setEditViewText(this,R.id.EditVille,cliToSave.VILLE);
		fillPays(Fonctions.GetStringDanem(cliToSave.PAYS));
		this.setEditViewText(this,R.id.EditTelephone,cliToSave.TEL1);
		this.setEditViewText(this,R.id.EditFax,cliToSave.FAX);

		this.setEditViewText(this,R.id.EditSiret,cliToSave.SIRET);
		this.setEditViewText(this,R.id.EditNumTVA,cliToSave.NUMTVA);
		this.setEditViewText(this,R.id.EditCommentaire,cliToSave.COMMENT);

		fillZone(Fonctions.GetStringDanem(cliToSave.ZONE));
		fillJourPassage(Fonctions.GetStringDanem(cliToSave.JOURPASSAGE));
		fillCatCompta(Fonctions.GetStringDanem(cliToSave.CATCOMPT));
		fillTypePDV(Fonctions.GetStringDanem(cliToSave.ICON));
		fillTypeEtablissement(Fonctions.GetStringDanem(cliToSave.TYPEETABLISSEMENT));
		
		//spinnerTypeEtablissement
		this.setEditViewText(this,R.id.EditContactgsm,cliToSave.GSM);
		this.setEditViewText(this,R.id.EditEnseigne,cliToSave.ENSEIGNE);

		fillType(Fonctions.GetStringDanem(cliToSave.TYPE));
		fillGroupeclient(Fonctions.GetStringDanem(cliToSave.GROUPECLIENT));
		fillAgent(Fonctions.GetStringDanem(cliToSave.AGENT));
		fillCircuit(Fonctions.GetStringDanem(cliToSave.CIRCUIT));
		fillJourfermeture(Fonctions.GetStringDanem(cliToSave.JOURFERMETURE));
		fillModereglement(Fonctions.GetStringDanem(cliToSave.MODEREGLEMENT));
		fillSAV(Fonctions.GetStringDanem(cliToSave.TYPESAV));
		
		
		
		
		double dvaleur=0;
		String stvaleur="";
		dvaleur=Fonctions.GetStringToDoubleDanem(cliToSave.MONTANTTOTALENCOURS);
		stvaleur=Fonctions.GetDoubleToStringFormatDanem(dvaleur, "0.00");
		this.setEditViewText(this,R.id.EditEncours,stvaleur);
		
		dvaleur=Fonctions.GetStringToDoubleDanem(cliToSave.MONTANTTOTALFACTURESDUES);
		stvaleur=Fonctions.GetDoubleToStringFormatDanem(dvaleur, "0.00");
		this.setEditViewText(this,R.id.EditFacturesdues,stvaleur);
		
		dvaleur=Fonctions.GetStringToDoubleDanem(cliToSave.MONTANTTOTALAVOIR);
		stvaleur=Fonctions.GetDoubleToStringFormatDanem(dvaleur, "0.00");
		this.setEditViewText(this,R.id.EditAvoirdispo,stvaleur);
		
		dvaleur=Fonctions.GetStringToDoubleDanem(cliToSave.MONTANTTOTALPAIEMENT);
		stvaleur=Fonctions.GetDoubleToStringFormatDanem(dvaleur, "0.00");
		this.setEditViewText(this,R.id.EditAvancepaie,stvaleur);
		

		//on retient la longitude et latitude
		latitude=cliToSave.LAT;
		longitude=cliToSave.LON;
		this.setEditViewText(this,R.id.EditLat,latitude);
		this.setEditViewText(this,R.id.EditLon,longitude);
		m_stvaleurDebut=cliToSave.TYPESAV+cliToSave.NOM+cliToSave.CONTACT_NOM+cliToSave.ADR1+cliToSave.ADR2+cliToSave.CP+cliToSave.VILLE+cliToSave.TEL1+cliToSave.FAX+cliToSave.GSM+cliToSave.EMAIL+cliToSave.ENSEIGNE+cliToSave.SIRET+cliToSave.NUMTVA+cliToSave.JOURFERMETURE+cliToSave.MODEREGLEMENT+
				cliToSave.TYPEETABLISSEMENT+cliToSave.COMMENT+cliToSave.CIRCUIT;
		
		
		
		

	}
	
	void showAlertDlg()
	{
		AlertDialog alertDialog = new AlertDialog.Builder(this).create();
		alertDialog.setTitle(getString(R.string.prospect_problemenumauto));
		alertDialog.setButton(getString(android.R.string.ok), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {

				finish(0);

			} });
		alertDialog.show();

	}
	public void setNumProspect(String num)
	{
		numProspect=num;
		if (Fonctions.GetStringDanem(numProspect).equals(""))
		{

			numProspect=Global.dbClient.GetCodeProspect(Preferences.getValue(this, Espresso.LOGIN, "0"));
		}

		setEditViewText(this,R.id.EditCodeProspect,numProspect);
	}
	void showProgressDialog()
	{
		m_ProgressDialog = ProgressDialog.show(this,
				"Veuillez patienter", "traitement en cours", true);
	}
	public void hideProgressDialog()
	{
		if (m_ProgressDialog!=null)
			m_ProgressDialog.dismiss();
	}

	private void initActionBar(){
		ActionBar actionBar = getActionBar();
		actionBar.setSubtitle(R.string.prospect_titre);
	}
	@Override
	protected void onStart() {
		super.onStart();

	}

	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();

		addMenu(menu, android.R.string.cancel, android.R.drawable.ic_menu_delete);		
		addMenu(menu, android.R.string.ok, android.R.drawable.ic_menu_save);

		if (m_bprendrecde)
			addMenu(menu, R.string.prospect_prendrecde,android.R.drawable.ic_media_next);
	//	if (devinerAdresseAllowed)
	//		addMenu(menu, R.string.prospect_positionner,R.drawable.action_map);

		//addMenu(menu, R.string.commande_quest,android.R.drawable.ic_menu_help);
		//addMenu(menu, R.string.geocode_prospect,android.R.drawable.ic_menu_mylocation);
		return true;
	}



	private void record(ArrayList<String> ValueMessage, StringBuffer buff){


		if(ControleAvantSauvegarde(ValueMessage,buff)==true)
		{
			if(1==1 || gecodeClient == null){
				if (save(false,buff)==true)
				{
					//FurtiveMessageBox("prospect sauvegardÃ©");
				}
				else
				{
					Fonctions.FurtiveMessageBox(this,buff.toString());
				}
			}
			else{
				Fonctions.FurtiveMessageBox(this,getString(R.string.geocoding_in_progress)+getString(R.string.menu_veuillezpatienter));
			}
		}
		else
		{
			promptText("Impossible de sauvegarder", Fonctions.GetStringDanem( ValueMessage.get(0)), false);
		//	Fonctions.FurtiveMessageBox(this,Fonctions.GetStringDanem( ValueMessage.get(0) ));
		}
		//		this.finish();


	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		StringBuffer buff = new StringBuffer();
		ArrayList<String> ValueMessage=new ArrayList();
		switch (item.getItemId()) {
		case R.string.prospect_positionner:

			findAdresse();

			return true;
		case android.R.string.cancel:
			Cancel();
			return true;

		case  android.R.string.ok:
			record(ValueMessage, buff);
			return true;

		case R.string.prospect_prendrecde://si on veut prendre commande avant m�me de remplir les coord
			//on sauve le prospect (obligŽ pour que tt fonctionne)
			//on va en prise de cde
			//on revient et on contr™le
			//comme c'est une sauvegarde forcŽe on donne un nom au client par dŽfaut si non renseignŽ
			if(this.getEditViewText(this,R.id.EditRaisonsocial).equals(""))
			{
				this.setEditViewText(this, R.id.EditRaisonsocial, getString(R.string.prospect_nomdefaut)) ;
			}
			if (save(true,new StringBuffer()))
			{	

				launchCde();
			}
			break;
		/*case R.string.commande_quest:
			MenuPopup.launchQuest(this,numProspect,"","");
			break;*/
		case R.string.geocode_prospect:
			String adr1 = cliToSave.ADR1.trim();
			String adr2 = cliToSave.ADR2.trim();
			String cp = cliToSave.CP.trim();
			String ville = cliToSave.VILLE.trim();
			String pays = cliToSave.PAYS.trim();
			//String departement = getDepartement(cp);	
			String adress = adr1+" "+adr2+" "+cp+" "+ville+" "+pays;
			/*getLatLongFromAddress(adr1+" "+adr2+" "+ville+" "+departement);*/
			Intent i = new Intent(this, GeolocalisationClient.class);
			Bundle bundle = new Bundle();
			bundle.putString("adress_client", adress);
			i.putExtras(bundle);
			startActivityForResult(i, LAUNCH_GEOLOCALISATION);
			break;
		default:
			return super.onOptionsItemSelected(item);
		}
		return super.onOptionsItemSelected(item);
	}
	void launchCde()
	{
		Intent intent = new Intent(this,		commandeActivity.class);
		Bundle b=new Bundle();
		b.putString("codecli",numProspect);
		b.putString("soccode",getSelectedSocCode() );
		intent.putExtras(b);
		startActivityForResult(intent,LAUNCH_ENTETECDE);
	}
	void Cancel()
	{
		if (cancelAllowed==false)
		{
			Fonctions.FurtiveMessageBox(this,getString(R.string.prospect_impossibledannuler));
			return;
		}
		//si on a des cdes pour ce prospect on ne peut pas annuler
		/*		if (Global.dbKDEntCde.Count(getCodeClient())>0)
		{
			Fonctions.FurtiveMessageBox(this,getString(R.string.prospect_impossibledannuler));
			return ;
		}
		 */
		//si on a deja crŽe le prospect parcequ'on avait ŽtŽ en cde on l'efface
		if (Global.dbClient.load(getCodeClient()).CREAT.equals(Global.dbClient.CLI_CREATION) && Global.dbKDEntCde.Count(getCodeClient())==0)
			Global.dbClient.delete(getCodeClient());
		Global.dbLog.Insert("Fiche client","Cancel","","Code: "+getCodeClient(), "","");
		this.finish(0);

	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		//on revient de la cde, si on a pris une commande on ne peut pas annuler le prospect
		//on recharge la fiche aussi car peut �tre modifiŽe dans la cde
		if(requestCode==LAUNCH_ENTETECDE )
		{
			Loadinformation( this.numProspect );

			//si la cde a été passée on enleve le bouton cancel
			if (resultCode==Activity.RESULT_OK)
			{
				cancelAllowed=false;
			}
		}

		if(requestCode==LAUNCH_GEOLOCALISATION){
			if(resultCode==RESULT_OK){
				//TODO on get latitude et longitude et on enregistre
				if(data != null){
					Bundle b = data.getExtras();
					if(b != null && b.getBoolean("exists_result")){
						Double la = Double.parseDouble(b.getString("latitude"));
						Double lo = Double.parseDouble(b.getString("longitude"));
						cliToSave.LAT = la.toString();
						cliToSave.LON = lo.toString();
						this.setEditViewText(this,R.id.EditLat,la.toString());
						this.setEditViewText(this,R.id.EditLon,lo.toString());
					
						Global.dbClient.save(cliToSave, true);
						Toast.makeText(ficheclient.this, getString(R.string.toast_ficheclient_localisation_ok), Toast.LENGTH_LONG).show();
					}else{
						Toast.makeText(ficheclient.this, getString(R.string.toast_ficheclient_localisation_nok), Toast.LENGTH_LONG).show();
					}
				}
			}
		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) 
	{

		if (keyCode == KeyEvent.KEYCODE_BACK ||keyCode == KeyEvent.KEYCODE_HOME) {
			//Cancel();

			return false;
		}

		else
			return super.onKeyDown(keyCode, event);
	}

	String getZone()
	{
		String pays="";
		int pos = this.getSpinnerSelectedIdx(this, R.id.spinnerZone);
		if (pos > -1)
			try {
				pays = idListZone.get(pos).getString(
						Global.dbParam.FLD_PARAM_CODEREC);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		return pays;
	}
	String getJourPassage()
	{
		String pays="";
		int pos = this.getSpinnerSelectedIdx(this, R.id.spinnerJourPassage);
		if (pos > -1)
			try {
				pays = idListJourPassage.get(pos).getString(
						Global.dbParam.FLD_PARAM_CODEREC);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		return pays;
	}
	String getCodeClient()
	{
		try {
			String code=numProspect;
			return code;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
	String getPays()
	{
		String pays="";
		int pos = this.getSpinnerSelectedIdx(this, R.id.spinnerPays);
		if (pos > -1)
			try {
				pays = idPays.get(pos).getString(
						Global.dbParam.FLD_PARAM_CODEREC);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		return pays;
	}
	
	
	
	String getModereglement()
	{
		String modereglement="";
		int pos = this.getSpinnerSelectedIdx(this, R.id.spinnerModereglement);
		if (pos > -1)
			try {
				modereglement = idListModereglement.get(pos).getString(
						Global.dbParam.FLD_PARAM_CODEREC);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		return modereglement;
	}
	
	String getCircuit()
	{
		String circuit="";
		int pos = this.getSpinnerSelectedIdx(this, R.id.spinnerCircuit);
		if (pos > -1)
			try {
				circuit = idListCircuit.get(pos).getString(
						Global.dbParam.FLD_PARAM_CODEREC);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		return circuit;
	}

	String getAgent()
	{
		String agent="";
		int pos = this.getSpinnerSelectedIdx(this, R.id.spinnerAgent);
		if (pos > -1)
			try {
				agent = idListAgent.get(pos).getString(
						Global.dbParam.FLD_PARAM_CODEREC);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		return agent;
	}
	
	String getgroupementclient()
	{
		String groupementclient="";
		int pos = this.getSpinnerSelectedIdx(this, R.id.spinnerGroupe);
		if (pos > -1)
			try {
				groupementclient = idListGroupe.get(pos).getString(
						Global.dbParam.FLD_PARAM_CODEREC);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		return groupementclient;
	}
	
	String gettypeclient()
	{
		String typeclient="";
		int pos = this.getSpinnerSelectedIdx(this, R.id.spinnerType);
		if (pos > -1)
			try {
				typeclient = idListType.get(pos).getString(
						Global.dbParam.FLD_PARAM_CODEREC);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		return typeclient;
	}
	String gettypeEtablissement()
	{
		String typeclient="";
		int pos = this.getSpinnerSelectedIdx(this, R.id.spinnerTypeEtablissement);
		if (pos > -1)
			try {
				typeclient = idListTypeEtablissement.get(pos).getString(
						Global.dbParam.FLD_PARAM_CODEREC);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		return typeclient;
	}
	
	String getSAV()
	{
		String sav="";
		int pos = this.getSpinnerSelectedIdx(this, R.id.spinnerSAV);
		if (pos > -1)
			try {
				sav = idListSAV.get(pos).getString(
						Global.dbParam.FLD_PARAM_CODEREC);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		return sav;
	}
	String getJourfermeture()
	{
		String jourferm="";
		int pos = this.getSpinnerSelectedIdx(this, R.id.spinnerJourFerm);
		if (pos > -1)
			try {
				jourferm = idListJourFerm.get(pos).getString(
						Global.dbParam.FLD_PARAM_CODEREC);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		return jourferm;
	}
	
	

	void finish(int i)
	{
		SynchroService.setPaused(false);
		finish();
	}
	String getCatCompta()
	{
		String cattarif="";
		int pos = this.getSpinnerSelectedIdx(this, R.id.spinnerCatCompta);
		if (pos > -1)
			try {
				cattarif = idListCatCompta.get(pos).getString(
						Global.dbParam.FLD_PARAM_CODEREC);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		return cattarif;
	}	

	String getTypePDV()
	{
		String cattarif="";
		int pos = this.getSpinnerSelectedIdx(this, R.id.spinnerTypePDV);
		if (pos > -1)
			try {
				cattarif = idListTypePdv.get(pos).getString(
						Global.dbParam.FLD_PARAM_CODEREC);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		cattarif=Global.dbClient.getLblIcon(cattarif);
		return cattarif;
	}	
	/**
	 * Creation du menu
	 */
	static final private int MENU_ITEM = Menu.FIRST;

	TableClient.structClient cliToSave;

	public boolean save(boolean temporary,StringBuffer stbuff)
	{
		try
		{


			cliToSave=new TableClient.structClient();

			cliToSave.SOC_CODE=getSelectedSocCode();
			cliToSave.CODE=this.getEditViewText(this,R.id.EditCodeProspect);
			cliToSave.NOM=this.getEditViewText(this,R.id.EditRaisonsocial);
			cliToSave.CONTACT_NOM=this.getEditViewText(this,R.id.EditNom);
			cliToSave.TEL1=this.getEditViewText(this,R.id.EditTelephone);
			cliToSave.EMAIL=this.getEditViewText(this,R.id.EditContactemail);
			cliToSave.CODEVRP=Preferences.getValue(this, Espresso.LOGIN, "0");

			cliToSave.ADR1=this.getEditViewText(this,R.id.EditAdresse1);
			cliToSave.ADR2=this.getEditViewText(this,R.id.EditAdresse2);
			cliToSave.CP=this.getEditViewText(this,R.id.EditCodepostal);
			cliToSave.VILLE=this.getEditViewText(this,R.id.EditVille);
			cliToSave.PAYS=getPays();

			cliToSave.FAX=this.getEditViewText(this,R.id.EditFax);

			cliToSave.SIRET=this.getEditViewText(this,R.id.EditSiret);
			cliToSave.NUMTVA=this.getEditViewText(this,R.id.EditNumTVA);
			cliToSave.COMMENT=this.getEditViewText(this,R.id.EditCommentaire);

			//cliToSave.JOURPASSAGE=getJourPassage();
			cliToSave.JOURPASSAGE="J";
			cliToSave.ZONE="Z";//getZone();
			cliToSave.CATCOMPT=getCatCompta();
		//	cliToSave.ICON=getTypePDV();


			//pour les créations
		//	cliToSave.COULEUR=getSAV();//"#ececec";
		//	cliToSave.IMPORTANCE="";
			cliToSave.TOVISIT="1";
			
			//longitude et latitude
			cliToSave.LAT=latitude;
			cliToSave.LON=longitude;
			cliToSave.TYPESAV=getSAV();
			cliToSave.GSM=this.getEditViewText(this,R.id.EditContactgsm);
			cliToSave.ENSEIGNE=this.getEditViewText(this,R.id.EditEnseigne);
			cliToSave.TYPE=gettypeclient();
			cliToSave.GROUPECLIENT				=getgroupementclient();
			cliToSave.AGENT						=getAgent();
			cliToSave.CIRCUIT					=getCircuit();
			cliToSave.JOURFERMETURE				=getJourfermeture();
			cliToSave.MODEREGLEMENT				=getModereglement();
			cliToSave.MONTANTTOTALENCOURS		="";
			cliToSave.MONTANTTOTALFACTURESDUES	="";
			cliToSave.MONTANTTOTALAVOIR			="";
			cliToSave.MONTANTTOTALPAIEMENT		="";
			cliToSave.TYPEETABLISSEMENT			=gettypeEtablissement();
			cliToSave.FREETEXT					="";
			cliToSave.EXONERATION				="";
			cliToSave.ETAT						="";
			cliToSave.DATECREAT					=Fonctions.getYYYYMMDD();
			m_stvaleurFin=cliToSave.TYPESAV+cliToSave.NOM+cliToSave.CONTACT_NOM+cliToSave.ADR1+cliToSave.ADR2+cliToSave.CP+cliToSave.VILLE+cliToSave.TEL1+cliToSave.FAX+cliToSave.GSM+cliToSave.EMAIL+cliToSave.ENSEIGNE+cliToSave.SIRET+cliToSave.NUMTVA+cliToSave.JOURFERMETURE+cliToSave.MODEREGLEMENT+
					cliToSave.TYPEETABLISSEMENT+cliToSave.COMMENT+cliToSave.CIRCUIT;
			
			
			
           if(!m_stvaleurFin.equals(m_stvaleurDebut))
           {
        	  // if (temporary)
   			{
   				save();
   				ficheclient.this.finish();
   			}
   			//else
   			{
   				//	gecodeClient = new task_geocodeClient(ficheclient.this, handlerGeoCoder,null);
   				//	gecodeClient.execute(cliToSave);
   				//UI_updateProgressBar();
	
   			} 
           }
           else
        	   Cancel();
			

			Global.dbLog.Insert("Fiche client ","Save","","Client: "+this.getEditViewText(this,R.id.EditCodeProspect), "","");



			return true;

		}
		catch(Exception ex)
		{
			return false;

		}

	}

	public boolean ControleAvantSauvegarde(ArrayList<String> ValueMessage,StringBuffer stBuf)
	{
		boolean bres=true;

		String stmessage="";

		try
		{
			//si nouvelle fiche il faut le questionnaire sinon on bash
			if (Global.dbClient.isCreation(numProspect))
			{
				/*if (Global.dbKDFillPlan.isQuest(numProspect)==false)
				{
					stmessage=getString(R.string.quest_obligatoire);
					ValueMessage.add(stmessage);
					//		MenuPopup.launchQuest(this,numProspect,"","");
					return false;
				}*/
			}

	/*		if(this.getEditViewText(this,R.id.EditRaisonsocial).equals(""))
			{
				if(bRaisonSocial==true)
				{
					stmessage=""+TextViewRaisonsocial.getText().toString()+" "+getString(R.string.prospect_obligatoire);
					ValueMessage.add(stmessage);
					return false;
				}
			}
			//Enseigne
			if(getEnseigne().equals(""))
			{
				if(bEnseigne ==true)
				{
					{

						stmessage=""+TexteEnseigne.getText().toString()+" "+getString(R.string.prospect_obligatoire);
						ValueMessage.add(stmessage);
						return false;
					}
				}
			}
			
			if(this.getEditViewText(this,R.id.EditNom).equals(""))
			{
				if(bNom==true)
				{
					{

						stmessage=""+TextViewNom.getText().toString()+" "+getString(R.string.prospect_obligatoire);
						ValueMessage.add(stmessage);
						return false;
					}
				}
			}
			
			if(this.getEditViewText(this,R.id.EditAdresse1).equals(""))
			{
				if(bAdresse1==true)
				{
					{

						stmessage=""+TextViewAdresse1.getText().toString()+" "+getString(R.string.prospect_obligatoire);
						ValueMessage.add(stmessage);
						return false;
					}
				}
			}

			if(this.getEditViewText(this,R.id.EditCodepostal).equals(""))
			{
				if(bcodepostal==true)
				{
					{

						stmessage=""+TextViewcodepostal.getText().toString()+" "+getString(R.string.prospect_obligatoire);
						ValueMessage.add(stmessage);
						return false;
					}
				}

			}
			if(this.getEditViewText(this,R.id.EditVille).equals(""))
			{
				if(bville==true)
				{
					{

						stmessage=""+TextViewville.getText().toString()+" "+getString(R.string.prospect_obligatoire);
						ValueMessage.add(stmessage);
						return false;
					}
				}
			}


			if(this.getEditViewText(this,R.id.EditTelephone).equals(""))
			{
				if(bTel==true)
				{
					{

						stmessage=""+TextViewTel.getText().toString()+" "+getString(R.string.prospect_obligatoire);
						ValueMessage.add(stmessage);
						return false;
					}
				}
			}
			if(this.getEditViewText(this,R.id.EditFax).equals(""))
			{
				if(bFax==true)
				{
					{

						stmessage=""+TextViewFax.getText().toString()+" "+getString(R.string.prospect_obligatoire);
						ValueMessage.add(stmessage);
						return false;
					}
				}
			}
			if(this.getEditViewText(this,R.id.EditContactgsm).equals(""))
			{
				if(bContactGSM==true)
				{
					{

						stmessage=""+TextViewContactgsm.getText().toString()+" "+getString(R.string.prospect_obligatoire);
						ValueMessage.add(stmessage);
						return false;
					}
				}
			}
			if(this.getEditViewText(this,R.id.EditContactemail).equals(""))
			{
				if(bEmail==true)
				{
					{

						stmessage=""+TextViewEmail.getText().toString()+" "+getString(R.string.prospect_obligatoire);
						ValueMessage.add(stmessage);
						return false;
					}
				}
			}
			if(gettypeclient().equals(""))
			{
				if(bTypeclient ==true)
				{
					{

						stmessage=""+TexteType.getText().toString()+" "+getString(R.string.prospect_obligatoire);
						ValueMessage.add(stmessage);
						return false;
					}
				}
			}
			
			if(this.getEditViewText(this,R.id.EditSiret).equals(""))
			{
				if(bSiret==true)
				{
					{

						stmessage=""+TextViewSiret.getText().toString()+" "+getString(R.string.prospect_obligatoire);
						ValueMessage.add(stmessage);
						return false;
					}
				}
			}
			if(!this.getEditViewText(this,R.id.EditSiret).equals(""))
			{
				if(this.getEditViewText(this,R.id.EditSiret).length()==14)
				{
				}
				else
				{
					stmessage=getString(R.string.prospect_siret_14)+" "+TextViewSiret.getText().toString();
					ValueMessage.add(stmessage);
					return false;
				}
			}
			if(!this.getEditViewText(this,R.id.EditNumTVA).equals(""))
			{
				if(this.getEditViewText(this,R.id.EditNumTVA).length()==13)
				{
					if(Fonctions.Left(this.getEditViewText(this,R.id.EditNumTVA), 2).equals("FR")|| Fonctions.Left(this.getEditViewText(this,R.id.EditNumTVA), 2).equals("fr"))
					{
						
					}
					else
					{
						stmessage=getString(R.string.prospect_numtva_13)+" "+TextViewNumTVA.getText().toString();
						ValueMessage.add(stmessage);
						return false;
					}
				}
				else
				{
					stmessage=getString(R.string.prospect_numtva_13)+" "+TextViewNumTVA.getText().toString();
					ValueMessage.add(stmessage);
					return false;
				}
			
			}
			
			if(getgroupementclient().equals(""))
			{
				if(bGroupeclient ==true)
				{
					{

						stmessage=""+TexteGroupe.getText().toString()+" "+getString(R.string.prospect_obligatoire);
						ValueMessage.add(stmessage);
						return false;
					}
				}
			}
			if(getJourfermeture().equals(""))
			{
				if(bJourFermeture ==true)
				{
					{

						stmessage=""+TexteJourFerm.getText().toString()+" "+getString(R.string.prospect_obligatoire);
						ValueMessage.add(stmessage);
						return false;
					}
				}
			}
			if(getSAV().equals(""))
			{
				if(bSAV==true)
				{
					{

						stmessage=""+TexteSAV.getText().toString()+" "+getString(R.string.prospect_obligatoire);
						ValueMessage.add(stmessage);
						return false;
					}
				}
			}

			if(getModereglement().equals(""))
			{
				if(bModereglement==true)
				{
					{

						stmessage=""+TexteModereglement.getText().toString()+" "+getString(R.string.prospect_obligatoire);
						ValueMessage.add(stmessage);
						return false;
					}
				}
			}

			

			if(getTypePDV().equals(""))
			{
				if(bTypePdv ==true)
				{
					{

						stmessage=""+TextViewTypePDV.getText().toString()+" "+getString(R.string.prospect_obligatoire);
						ValueMessage.add(stmessage);
						return false;
					}
				}
			}
			
			if(getZone().equals(""))
			{
				if(bZone ==true)
				{
					{

						stmessage=""+TextViewZone.getText().toString()+" "+getString(R.string.prospect_obligatoire);
						ValueMessage.add(stmessage);
						return false;
					}
				}
			}
			if(getJourPassage().equals(""))
			{
				if(bJourPassage ==true)
				{
					{

						stmessage=""+TextViewJourPassage.getText().toString()+" "+getString(R.string.prospect_obligatoire);
						ValueMessage.add(stmessage);
						return false;
					}
				}
			}

			if(getCatCompta().equals(""))
			{
				if(bCatCompta ==true)
				{
					{

						stmessage=""+TextViewCatCompta.getText().toString()+" "+getString(R.string.prospect_obligatoire);
						ValueMessage.add(stmessage);
						return false;
					}
				}
			}
		*/


		}
		catch(Exception ex)
		{
			stBuf.setLength(0);
			stBuf.append(ex.getMessage());
			return false;

		}
		return bres;



	}
	void fillPays(String selVal) {
		try {
			if (Global.dbParam.getRecordsFiltreAllSoc(Global.dbParam.PARAM_PAYS,
					this.idPays, "1") == true) {

				int pos = 0;
				String[] items = new String[idPays.size()];
				for (int i = 0; i < idPays.size(); i++) {

					items[i] = idPays.get(i).getString(
							Global.dbParam.FLD_PARAM_LBL);

					String codeRec = idPays.get(i).getString(
							Global.dbParam.FLD_PARAM_CODEREC);

					if (selVal.equals(codeRec)) {
						pos = i;
					}

				}

				Spinner spinner = (Spinner) findViewById(R.id.spinnerPays);

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

	void fillCatCompta(String selVal) {
		try {
			if (Global.dbParam.getRecord2s(Global.dbParam.PARAM_CAT_COMPTA,
					this.idListCatCompta,false) == true) {

				int pos = 0;
				String[] items = new String[idListCatCompta.size()];
				for (int i = 0; i < idListCatCompta.size(); i++) {

					items[i] = idListCatCompta.get(i).getString(
							Global.dbParam.FLD_PARAM_LBL);

					String codeRec = idListCatCompta.get(i).getString(
							Global.dbParam.FLD_PARAM_CODEREC);

					if (selVal.equals(codeRec)) {
						pos = i;
					}

				}

				Spinner spinner = (Spinner) findViewById(R.id.spinnerCatCompta);


				//ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				//		android.R.layout.simple_spinner_item, items);
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
	
	void fillSAV(String selVal) {
		try {
		
			if (Global.dbParam.getRecord2s(Global.dbParam.PARAM_SAV,
					this.idListSAV,false) == true) {

				int pos = 0;
				String[] items = new String[idListSAV.size()];
				for (int i = 0; i < idListSAV.size(); i++) {

					items[i] = idListSAV.get(i).getString(
							Global.dbParam.FLD_PARAM_LBL);

					String codeRec = idListSAV.get(i).getString(
							Global.dbParam.FLD_PARAM_CODEREC);

					if (selVal.equals(codeRec)) {
						pos = i;
					}

				}

				Spinner spinner = (Spinner) findViewById(R.id.spinnerSAV);


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
	void fillModereglement(String selVal) {
		try {
		
			if (Global.dbParam.getRecord2s(Global.dbParam.PARAM_MODEREGLEMENT,
					this.idListModereglement,false) == true) {

				int pos = 0;
				String[] items = new String[idListModereglement.size()];
				for (int i = 0; i < idListModereglement.size(); i++) {

					items[i] = idListModereglement.get(i).getString(
							Global.dbParam.FLD_PARAM_LBL);

					String codeRec = idListModereglement.get(i).getString(
							Global.dbParam.FLD_PARAM_CODEREC);

					if (selVal.equals(codeRec)) {
						pos = i;
					}

				}

				Spinner spinner = (Spinner) findViewById(R.id.spinnerModereglement);


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
	void fillJourfermeture(String selVal) {
		try {
			
			if (Global.dbParam.getRecord2s(Global.dbParam.PARAM_JOURFERMETURE,
					this.idListJourFerm,true) == true) {

				int pos = 0;
				String[] items = new String[idListJourFerm.size()];
				for (int i = 0; i < idListJourFerm.size(); i++) {

					items[i] = idListJourFerm.get(i).getString(
							Global.dbParam.FLD_PARAM_LBL);

					String codeRec = idListJourFerm.get(i).getString(
							Global.dbParam.FLD_PARAM_CODEREC);

					if (selVal.equals(codeRec)) {
						pos = i;
					}

				}

				Spinner spinner = (Spinner) findViewById(R.id.spinnerJourFerm);


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
	void fillTypeEtablissement(String selVal) {
		try {
			
			if (Global.dbParam.getRecord2s(Global.dbParam.PARAM_TYPEETABLISSEMENT,
					this.idListTypeEtablissement,true) == true) {

				int pos = 0;
				String[] items = new String[idListTypeEtablissement.size()];
				for (int i = 0; i < idListTypeEtablissement.size(); i++) {

					items[i] = idListTypeEtablissement.get(i).getString(
							Global.dbParam.FLD_PARAM_LBL);

					String codeRec = idListTypeEtablissement.get(i).getString(
							Global.dbParam.FLD_PARAM_CODEREC);

					if (selVal.equals(codeRec)) {
						pos = i;
					}

				}

				Spinner spinner = (Spinner) findViewById(R.id.spinnerTypeEtablissement);


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
	void fillCircuit(String selVal) {
		try {
		
			if (Global.dbParam.getRecord2s(Global.dbParam.PARAM_CODECIRCUIT,
					this.idListCircuit,true) == true) {

				int pos = 0;
				String[] items = new String[idListCircuit.size()];
				for (int i = 0; i < idListCircuit.size(); i++) {

					items[i] = idListCircuit.get(i).getString(
							Global.dbParam.FLD_PARAM_LBL);

					String codeRec = idListCircuit.get(i).getString(
							Global.dbParam.FLD_PARAM_CODEREC);

					if (selVal.equals(codeRec)) {
						pos = i;
					}

				}

				Spinner spinner = (Spinner) findViewById(R.id.spinnerCircuit);


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
	void fillAgent(String selVal) {
		try {
			
			if (Global.dbParam.getRecord2s(Global.dbParam.PARAM_AGENT,
					this.idListAgent,false) == true) {

				int pos = 0;
				String[] items = new String[idListAgent.size()];
				for (int i = 0; i < idListAgent.size(); i++) {

					items[i] = idListAgent.get(i).getString(
							Global.dbParam.FLD_PARAM_LBL);

					String codeRec = idListAgent.get(i).getString(
							Global.dbParam.FLD_PARAM_CODEREC);

					if (selVal.equals(codeRec)) {
						pos = i;
					}

				}

				Spinner spinner = (Spinner) findViewById(R.id.spinnerAgent);


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
	void fillGroupeclient(String selVal) {
		try {
		
			if (Global.dbParam.getRecord2s(Global.dbParam.PARAM_GROUPEMENTCLIENT,
					this.idListGroupe,true) == true) {

				int pos = 0;
				String[] items = new String[idListGroupe.size()];
				for (int i = 0; i < idListGroupe.size(); i++) {

					items[i] = idListGroupe.get(i).getString(
							Global.dbParam.FLD_PARAM_LBL);

					String codeRec = idListGroupe.get(i).getString(
							Global.dbParam.FLD_PARAM_CODEREC);

					if (selVal.equals(codeRec)) {
						pos = i;
					}

				}

				Spinner spinner = (Spinner) findViewById(R.id.spinnerGroupe);


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
	void fillType(String selVal) {
		try {
			
			if (Global.dbParam.getRecord2s(Global.dbParam.PARAM_TYPECLI,
					this.idListType,true) == true) {

				int pos = 0;
				String[] items = new String[idListType.size()];
				for (int i = 0; i < idListType.size(); i++) {

					items[i] = idListType.get(i).getString(
							Global.dbParam.FLD_PARAM_LBL);

					String codeRec = idListType.get(i).getString(
							Global.dbParam.FLD_PARAM_CODEREC);

					if (selVal.equals(codeRec)) {
						pos = i;
					}

				}

				Spinner spinner = (Spinner) findViewById(R.id.spinnerType);


				//ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				//		android.R.layout.simple_spinner_item, items);
				ArrayAdapter<String>  adapter = new ArrayAdapter<String>(this, R.layout.spinner_item_text, items);
				spinner.setAdapter(adapter);
				
				//adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				
				
				spinner.setAdapter(adapter);

				spinner.setSelection(pos);

			}

		} catch (Exception ex) {

		}

	}
	
	
	void fillTypePDV(String selVal) {
		try {
			
			if (Global.dbParam.getRecord2s(Global.dbParam.PARAM_CLIACTIV,
					this.idListTypePdv,false) == true) {

				int pos = 0;
				String[] items = new String[idListTypePdv.size()];
				for (int i = 0; i < idListTypePdv.size(); i++) {

					items[i] = idListTypePdv.get(i).getString(
							Global.dbParam.FLD_PARAM_LBL);

					String codeRec = idListTypePdv.get(i).getString(
							Global.dbParam.FLD_PARAM_CODEREC);

					if (selVal.equals(codeRec)) {
						pos = i;
					}

				}

				Spinner spinner = (Spinner) findViewById(R.id.spinnerTypePDV);


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
	void fillZone(String selVal) {
		try {

			if (Global.dbParam.getRecord2s(Global.dbParam.PARAM_CODETOURNEE,
					this.idListZone,false) == true) {

				int pos = 0;
				String[] items = new String[idListZone.size()];
				for (int i = 0; i < idListZone.size(); i++) {

					items[i] = idListZone.get(i).getString(
							Global.dbParam.FLD_PARAM_LBL);

					String codeRec = idListZone.get(i).getString(
							Global.dbParam.FLD_PARAM_CODEREC);

					if (selVal.equals(codeRec)) {
						pos = i;
					}

				}

				Spinner spinner = (Spinner) findViewById(R.id.spinnerZone);


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
	void fillJourPassage(String selVal) {
		try {
			//tof: pour affichage nbr cli par jour
			Bundle b = new Bundle(); 
			Global.dbClient.CountCliParJourPassage(b, m_stCurZone ) ;

			if (Global.dbParam.getRecord2s(Global.dbParam.PARAM_JOURPASSAGE,
					this.idListJourPassage,false) == true) {

				int pos = 0;
				String[] items = new String[idListJourPassage.size()];
				for (int i = 0; i < idListJourPassage.size(); i++) {

					String codeRec = idListJourPassage.get(i).getString(
							Global.dbParam.FLD_PARAM_CODEREC);
					String stLbl =  idListJourPassage.get(i).getString(
							Global.dbParam.FLD_PARAM_LBL);
					String stLblAdd = b.getString(codeRec) ; 
					if( stLblAdd == null || stLblAdd == "null" )
						stLblAdd = "" ;
					else
						stLblAdd = " ("+stLblAdd+" PdV)" ;
					//items[i] = idListJourPassage.get(i).getString(
					//		Global.dbParam.FLD_PARAM_LBL);
					items[i] = stLbl +stLblAdd ;


					if (selVal.equals(codeRec)) {
						pos = i;
					}

				}

				Spinner spinner = (Spinner) findViewById(R.id.spinnerJourPassage); 


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
	 * rempli les sociï¿½tï¿½
	 */
	void fillSociete() {
		try {

			arraySoc=Global.dbSoc.getSocs(Preferences.getValue( ficheclient.this, Espresso.LOGIN, "0"));

			if (arraySoc.size() >0) {

				int pos = 0;
				String[] items = new String[arraySoc.size()];
				for (int i = 0; i < arraySoc.size(); i++) {

					items[i] = arraySoc.get(i)[1];



				}


				//ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				//		android.R.layout.simple_spinner_item, items);
				ArrayAdapter<String>  adapter = new ArrayAdapter<String>(this, R.layout.spinner_item_text, items);
				
				adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				spin_soc.setAdapter(adapter);

				spin_soc.setSelection(pos);


			}

		} catch (Exception ex) {

		}

	}

	/**
	 * renvoi l'index sï¿½lectionnï¿½ dans un spinner
	 * @param et
	 * @return
	 */
	public int getSpinnerSelectedIdx(Spinner et )
	{
		try
		{
			int pos=et.getSelectedItemPosition();
			return pos;

		}
		catch(Exception ex)
		{

		}
		return -1;
	}    
	String getSelectedSocCode()
	{
		int sel=this.getSpinnerSelectedIdx(spin_soc);
		if (sel==-1)
			return "";

		String result=arraySoc.get(sel)[0];

		return result;
	}

	void setObligatoire(EditText obj)
	{
		obj.setBackgroundColor(Color.RED);
	}


	void InitTextView()
	{
		//RS
		TextViewRaisonsocial = (TextView) findViewById(R.id.Texteraisonsocial);
		//Nom
		TextViewNom = (TextView) findViewById(R.id.TexteNom);
		//Adresse 1
		TextViewAdresse1 = (TextView) findViewById(R.id.TexteAdresse1);
		//Adresse 2
		TextViewAdresse2 = (TextView) findViewById(R.id.TexteAdresse2);
		//Code postal
		TextViewcodepostal = (TextView) findViewById(R.id.TexteCodepostal);
		//Ville
		TextViewville = (TextView) findViewById(R.id.TexteVille);
		//Pays
		TextViewPays = (TextView) findViewById(R.id.TextePays);
		//Tel
		TextViewTel = (TextView) findViewById(R.id.TexteTelephone);
		//Fax
		TextViewFax = (TextView) findViewById(R.id.TexteFax);
		//GSM
		TextViewContactgsm = (TextView) findViewById(R.id.TexteContactgsm);
		//Email
		TextViewEmail = (TextView) findViewById(R.id.TexteContactemail);
		//Enseigne
		TexteEnseigne= (TextView) findViewById(R.id.TexteEnseigne);
		//type client
		TexteType= (TextView) findViewById(R.id.TexteType);
		//Siret
		TextViewSiret = (TextView) findViewById(R.id.TexteSiret);
		//Groupe
		TexteGroupe= (TextView) findViewById(R.id.TexteGroupe);
		//N° tva
		TextViewNumTVA = (TextView) findViewById(R.id.TexteNumTVA);
		//type etablissement
		TexteTypeEtablissement= (TextView) findViewById(R.id.TexteTypeEtablissement);
		
		// Fermé
		TexteJourFerm= (TextView) findViewById(R.id.TexteJourFerm);
		//SAV
		TexteSAV= (TextView) findViewById(R.id.TexteSAV);
		//Mode reglement
		TexteModereglement= (TextView) findViewById(R.id.TexteModereglement);
		//Commentaire
		TextViewCommentaire = (TextView) findViewById(R.id.TexteCommentaire);
		// En cours
		TexteEncours= (TextView) findViewById(R.id.TexteEncours);
		
		//Circuit
		TexteCircuit= (TextView) findViewById(R.id.TexteCircuit);
		
		 TextViewlat = (TextView) findViewById(R.id.TexteLat);
		 TextViewlon = (TextView) findViewById(R.id.TexteLon);
		 EditLat=(EditText)this.findViewById(R.id.EditLat);
		 EditLon=(EditText)this.findViewById(R.id.EditLon);
			
		
		
		
		
		TextViewCatCompta= (TextView) findViewById(R.id.TexteCatCompta);
		TextViewTypePDV = (TextView) findViewById(R.id.TexteTypePDV);
		TextViewZone = (TextView) findViewById(R.id.TexteZone);
		TextViewJourPassage = (TextView) findViewById(R.id.TexteJourPassage);
		
		
		Spinner spType=(Spinner)this.findViewById(R.id.spinnerType);
		Spinner spGroupe=(Spinner)this.findViewById(R.id.spinnerGroupe);
		
		
		TexteAgent= (TextView) findViewById(R.id.TexteAgent);
		Spinner spAgent=(Spinner)this.findViewById(R.id.spinnerAgent);
		
		Spinner spJourFerm=(Spinner)this.findViewById(R.id.spinnerJourFerm);
		
		Spinner spSAV=(Spinner)this.findViewById(R.id.spinnerSAV);
		
		Spinner spModereglement=(Spinner)this.findViewById(R.id.spinnerModereglement);
		
		
		EditEncours=(EditText)this.findViewById(R.id.EditEncours);
		
		TexteFacturesdues= (TextView) findViewById(R.id.TexteFacturesdues);
		EditFacturesdues=(EditText)this.findViewById(R.id.EditFacturesdues);
		
		TexteAvoirdispo= (TextView) findViewById(R.id.TexteAvoirdispo);
		EditAvoirdispo=(EditText)this.findViewById(R.id.EditAvoirdispo);
		
		TexteAvancepaie= (TextView) findViewById(R.id.TexteAvancepaie);
		EditAvancepaie=(EditText)this.findViewById(R.id.EditAvancepaie);
		
		
		
		
		
		EditText ERaisonSocial=(EditText)this.findViewById(R.id.EditRaisonsocial);
		EditText EditNom=(EditText)this.findViewById(R.id.EditNom);
		EditText eContactgsm=(EditText)this.findViewById(R.id.EditContactgsm);
		EAdresse1=(EditText)this.findViewById(R.id.EditAdresse1);
		EditText EAdresse2=(EditText)this.findViewById(R.id.EditAdresse2);
		Ecodepostal=(EditText)this.findViewById(R.id.EditCodepostal);
		Eville=(EditText)this.findViewById(R.id.EditVille);
		EditEnseigne =(EditText)this.findViewById(R.id.EditEnseigne);
		Spinner spPays=(Spinner)this.findViewById(R.id.spinnerPays);

		Spinner spCatCompta=(Spinner)this.findViewById(R.id.spinnerCatCompta);
		Spinner spTypePDV=(Spinner)this.findViewById(R.id.spinnerTypePDV);
		Spinner spZone=(Spinner)this.findViewById(R.id.spinnerZone);
		Spinner spJourPassage=(Spinner)this.findViewById(R.id.spinnerJourPassage);
		Spinner spCircuit=(Spinner)this.findViewById(R.id.spinnerCircuit);
		
		EditText ETel=(EditText)this.findViewById(R.id.EditTelephone);
		EditText EFax=(EditText)this.findViewById(R.id.EditFax);
		EditText EEmail=(EditText)this.findViewById(R.id.EditContactemail);
		EditText ESiret=(EditText)this.findViewById(R.id.EditSiret);
		EditText ENumTVA=(EditText)this.findViewById(R.id.EditNumTVA);
		EditText ECommentaire=(EditText)this.findViewById(R.id.EditCommentaire);

		//Mettre les textview et EditText en GONE
		TextViewRaisonsocial.setVisibility(View.GONE);
		TextViewNom.setVisibility(View.GONE);
		TextViewAdresse1.setVisibility(View.GONE);
		TextViewAdresse2.setVisibility(View.GONE);
		TextViewcodepostal.setVisibility(View.GONE);
		TextViewville.setVisibility(View.GONE);
		TextViewPays.setVisibility(View.GONE);
		TextViewTel.setVisibility(View.VISIBLE);
		TextViewFax.setVisibility(View.GONE);
		TextViewEmail.setVisibility(View.GONE);
		TextViewSiret.setVisibility(View.GONE);
		TextViewCommentaire.setVisibility(View.GONE);

		TextViewCatCompta.setVisibility(View.GONE);
		TextViewTypePDV.setVisibility(View.GONE);
		TextViewZone.setVisibility(View.GONE);
		TextViewJourPassage.setVisibility(View.GONE);
		TextViewNumTVA.setVisibility(View.GONE);


		ERaisonSocial.setVisibility(View.GONE);
		EditNom.setVisibility(View.GONE);
		EAdresse1.setVisibility(View.GONE);
		EAdresse2.setVisibility(View.GONE);
		Ecodepostal.setVisibility(View.GONE);
		Eville.setVisibility(View.GONE);
		spPays.setVisibility(View.GONE);
		ETel.setVisibility(View.VISIBLE);
		EFax.setVisibility(View.GONE);
		EEmail.setVisibility(View.GONE);
		ESiret.setVisibility(View.GONE);
		ECommentaire.setVisibility(View.GONE);

		spCatCompta.setVisibility(View.GONE);
		spTypePDV.setVisibility(View.GONE);
		spZone.setVisibility(View.GONE);
		spJourPassage.setVisibility(View.GONE);
		ENumTVA.setVisibility(View.GONE);

//			bRaisonSocial=true ;
			ERaisonSocial.setVisibility(View.VISIBLE);
			//setObligatoire(ERaisonSocial);
			TextViewRaisonsocial.setVisibility(View.VISIBLE);
//			bNom=true ;
		//	EditNom.setVisibility(View.VISIBLE);
			//setObligatoire(EditNom);
			//TextViewNom.setVisibility(View.VISIBLE);
	
			bEnseigne=true;
			bTypeclient=true;
			bContactGSM=true ;
			bGroupeclient=true;
			bJourFermeture=true;
			bModereglement=true;
			bSAV=true;


			eContactgsm.setVisibility(View.VISIBLE);
			//setObligatoire(eContactgsm);
			TextViewContactgsm.setVisibility(View.VISIBLE);
			bAdresse1=true ;
			TextViewAdresse1.setVisibility(View.VISIBLE);
			EAdresse1.setVisibility(View.VISIBLE);
			//setObligatoire(EAdresse1);
			bAdresse2 =true;
			TextViewAdresse2.setVisibility(View.VISIBLE);
			EAdresse2.setVisibility(View.VISIBLE);
			bcodepostal=true;
			TextViewcodepostal.setVisibility(View.VISIBLE);
			Ecodepostal.setVisibility(View.VISIBLE);
//			setObligatoire(Ecodepostal);
			bville=true ;
			TextViewville.setVisibility(View.VISIBLE);
			Eville.setVisibility(View.VISIBLE);
//			setObligatoire(Eville);
			//bPays =true;
			TextViewPays.setVisibility(View.GONE);
			spPays.setVisibility(View.GONE);
			//bZone =true;
			//TextViewZone.setVisibility(View.VISIBLE);
			//spZone.setVisibility(View.VISIBLE);
			//bJourPassage =true;
			//TextViewJourPassage.setVisibility(View.VISIBLE);
			//spJourPassage.setVisibility(View.VISIBLE);
			//bNumTVA=true;	
			//TextViewNumTVA.setVisibility(View.VISIBLE);
			//ENumTVA.setVisibility(View.VISIBLE);
			bFax =true;
			TextViewFax.setVisibility(View.VISIBLE);
			EFax.setVisibility(View.VISIBLE);
			bEmail =true;
			TextViewEmail.setVisibility(View.VISIBLE);
			EEmail.setVisibility(View.VISIBLE);
			//bSiret =true;
			//TextViewSiret.setVisibility(View.VISIBLE);
			//ESiret.setVisibility(View.VISIBLE);
			//bCommentaire =true;
			TextViewCommentaire.setVisibility(View.VISIBLE);
			ECommentaire.setVisibility(View.VISIBLE);
	
			//	bCatCompta=true;
			//TextViewCatCompta.setVisibility(View.VISIBLE);
		//	spCatCompta.setVisibility(View.VISIBLE);
			//bTypePdv =true;
			//TextViewTypePDV.setVisibility(View.VISIBLE);
			//spTypePDV.setVisibility(View.VISIBLE);
	}




	/** Handler */
	Handler getHandlerGeoCoder(){
		Handler h = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);

				switch(msg.what) {
				case task_geocodeClient.MESSAGE_GEOCODING_FINISHED_FAIL:
					gecodeClient = null;
					UI_updateProgressBar();
					Fonctions.FurtiveMessageBox(ficheclient.this, getString(R.string.geocoding_failed)+" "+getString(R.string.check_address_and_connection));
					save();
					ficheclient.this.finish();
					break;
				case task_geocodeClient.MESSAGE_GEOCODING_FINISHED_SUCCESS:
					gecodeClient = null;
					UI_updateProgressBar();
					save();
					ficheclient.this.finish(0);
					break;
				case task_geocodeClient.MESSAGE_RETRIEVEADDRESS_SUCCESS:
					Eville.setText(cliToSave.VILLE);
					Ecodepostal.setText(cliToSave.CP);
					EAdresse1.setText(cliToSave.ADR1);
					//	gecodeClient.cancel(true);
					gecodeClient=null;
					break;
				}

			}
		};
		return h;
	}

	Handler getHandlerRetrieveAddress(){
		Handler h = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);

				switch(msg.what) {

				case 2:
				case 1:
					Location loc=whereAmI.getLocation(m_appState);
					cliToSave=new structClient();
					//Eville.setText(String.valueOf(loc.getLatitude()));
					gecodeClient = new task_geocodeClient(ficheclient.this, handlerGeoCoder,loc);
					gecodeClient.execute(cliToSave);

					UI_updateProgressBar();

					break;

				}

			}
		};
		return h;
	}

	void findAdresse()
	{
		whereAmI=new MyLocation();
		whereAmI.init(this, handlerRetrieveAddress);
	}

	private boolean save(){
		StringBuffer buffer = new StringBuffer();
		if (!Global.dbClient.saveProspect(cliToSave, numProspect, buffer))
		{
			Fonctions.FurtiveMessageBox(this, getString(R.string.unable_to_save)+":"+buffer.toString());
			return false;
		}



		Fonctions.FurtiveMessageBox(this, getString(R.string.save_successfull));

		return true;
	}

	private void UI_updateProgressBar(){
		if(gecodeClient !=null)
			setProgressBarIndeterminateVisibility(false);
		else
			setProgressBarIndeterminateVisibility(true);
	}

	public void onItemSelected(AdapterView<?> parent, View view, 
			int pos, long id) {
		switch (parent.getId()) 
		{         
		case R.id.spinnerZone:
			m_stCurZone = idListZone.get(pos).getString(
					Global.dbParam.FLD_PARAM_CODEREC);

			fillJourPassage(m_stCurJourPassage) ;

			break;              
		case R.id.spinnerJourPassage:
			m_stCurJourPassage = idListJourPassage.get(pos).getString(
					Global.dbParam.FLD_PARAM_CODEREC);


			break;              

		}    	
		// An item was selected. You can retrieve the selected item using
		// parent.getItemAtPosition(pos)
		//m_stFam = idFam.get(pos).getString(
		//		Global.dbParam.FLD_PARAM_CODEREC);
		//PopulateList() ;
	}
	public void onNothingSelected(AdapterView<?> parent) {
		// Another interface callback
	}

	 

}
