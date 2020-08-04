package com.menadinteractive.maxpoilane;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Random;
import java.util.Set;

import com.menadinteractive.commande.commandeActivity;
import com.menadinteractive.echangestock.EchangeStockActivity;
import com.menadinteractive.inventaire.InventaireActivity;
import com.menadinteractive.maxpoilane.R;
import com.menadinteractive.segafredo.communs.Fonctions;
import com.menadinteractive.segafredo.communs.Global;
import com.menadinteractive.segafredo.db.MyDB;
import com.menadinteractive.segafredo.db.Preferences;
import com.menadinteractive.segafredo.db.TableClient;
import com.menadinteractive.segafredo.db.TableClient.structClient;
import com.menadinteractive.segafredo.db.TableContactcli;
import com.menadinteractive.segafredo.db.TableSouches;
import com.menadinteractive.segafredo.db.TableSouches.passeplat;
import com.menadinteractive.segafredo.db.dbKD;
import com.menadinteractive.segafredo.db.dbKD101ClientVu;
import com.menadinteractive.segafredo.db.dbKD451RetourMachineclient;
import com.menadinteractive.segafredo.db.dbKD451RetourMachineclient.structRetourMarchineClient;
import com.menadinteractive.segafredo.db.dbKD452ComptageMachineclient;
import com.menadinteractive.segafredo.db.dbKD543LinInventaire;
import com.menadinteractive.segafredo.db.dbKD83EntCde;
import com.menadinteractive.segafredo.db.dbKD83EntCde.structEntCde;
import com.menadinteractive.segafredo.db.dbKD981RetourBanqueEnt;
import com.menadinteractive.segafredo.db.dbKD981RetourBanqueEnt.structRetourBanque;
import com.menadinteractive.segafredo.db.dbKD98Encaissement;
import com.menadinteractive.segafredo.db.dbLog;
import com.menadinteractive.segafredo.db.dbSiteListeLogin;
import com.menadinteractive.segafredo.db.dbSiteListeLogin.structlistLogin;
import com.menadinteractive.segafredo.encaissement.Encaissement;
import com.menadinteractive.segafredo.encaissement.ReglementActivity;
import com.menadinteractive.segafredo.findcli.FindCliActivity;
import com.menadinteractive.segafredo.plugins.Espresso;
import com.menadinteractive.segafredo.rapportactivite.rapportactivite;
import com.menadinteractive.segafredo.remisebanque.RecapitulatifRemiseEnBanque;
import com.menadinteractive.segafredo.synchro.SynchroActivity;
import com.menadinteractive.segafredo.tasks.task_sendWSData;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class DashboardActivity extends BaseActivity {
	private listAdapter m_adapter;
	private ProgressDialog m_ProgressDialog = null;
	String codeagent;
	TextView tvCodeAgent,tvNomAgent,tvVersion,textViewNextBL;
	LinearLayout llSynchro;
	ListView myListView;
	TextView tvSynchro;
	TextView tvModeTest;
	Spinner spinnerLivreur;
	ArrayList<Bundle> idLivreur = null;// les id qui servent a retrouver les pays
	String m_stlivreur = "" ;	//Filtre livreur (voir spinner)

	
	Handler hSync;
	int nbrDataToSend=0;
	ProgressBar pb;
	
	boolean doubleBackToExitPressedOnce=false;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dashboard);
		startServiceLockApp(this);
		initGui();
		idLivreur = new ArrayList<Bundle>();

		initListerners();
		m_stlivreur=Preferences.getValue(this, Espresso.LIVREUR,"");
		
		fillLivreur(Fonctions.GetStringDanem(m_stlivreur));

		
		copyAssets();
		controlIfDocumentInBuffer();
		
		MyDB.copyFile(MyDB.source, MyDB.dest);
		hSync=getHandlerSync();
		Global.dbLog.Insert("Menu Principal", "Demarrage", "", "", "", "");
		
	}
	
	//permet de dÈmarrer le service si nÈcessaire
	public void startServiceLockApp(Context ctx){
		Intent intent = new Intent();
		intent.setAction("android.LAUNCH_LOCKAPP");
		intent.putExtra("msg", "1");
		ctx.sendBroadcast(intent);		

	}

	//si un document est dans le buffer cause plantage on doit le reprendre
	void controlIfDocumentInBuffer()
	{
		//on verifie si on a pas laiss� un piece non termin�e dans le buffer
		dbKD83EntCde cde=new dbKD83EntCde(m_appState.m_db);
		structEntCde structcde=new structEntCde();
		structcde=cde.getPieceNotSend();
		if (structcde!=null)
		{
			launchCde(this, structcde.TYPEDOC, structcde.CODECLI, structcde.SOCCODE);
			return;
		}

		dbKD451RetourMachineclient ret=new dbKD451RetourMachineclient(m_appState.m_db);
		structRetourMarchineClient structret=ret.getPieceNotPrint();
		if (structret!=null)
		{
			launchMaterielClient(structret.CODECLI,LAUNCH_MATERIELCLI);
			return;
		}

		//on controle si un encaissement est en cours (on compte les documents non imprimés)
		dbKD98Encaissement enc = new dbKD98Encaissement(m_appState.m_db);
		ArrayList<Encaissement> encaissements = enc.getEncaissementNotPrint();
		if(encaissements != null && encaissements.size() > 0){

			String codeClient = "";
			for(Encaissement e : encaissements){
				if(e.getCodeClient() != null && !e.getCodeClient().equals("")){
					codeClient = e.getCodeClient();
					break;
				}
			}

			structClient cli=new structClient();
			Global.dbClient.getClient(codeClient, cli, new StringBuilder());

			Intent intent = new Intent(this, ReglementActivity.class);
			Bundle b = new Bundle();
			b.putString(Espresso.CODE_CLIENT, codeClient);
			b.putString(Espresso.CODE_SOCIETE, cli.SOC_CODE);
			intent.putExtras(b);
			startActivityForResult(intent, 0);
		}

		//idem pour remise en banque
		dbKD981RetourBanqueEnt retour = new dbKD981RetourBanqueEnt(m_appState.m_db);
		ArrayList<structRetourBanque> remises = new ArrayList<dbKD981RetourBanqueEnt.structRetourBanque>();
		remises = retour.getRemiseNotPrint();
		if(remises != null && remises.size() > 0){
			SharedPreferences sharedpreferences = getSharedPreferences("myPreferences", Context.MODE_PRIVATE);
			Set<String> set = sharedpreferences.getStringSet("sp_identifiants", null);
			ArrayList<String> identifiants = new ArrayList<String>();

			if(set != null){
				for(String id : set){
					identifiants.add(id);
				}
				if(identifiants != null && identifiants.size() > 0 && remises.get(0).NUMCDE != null && !remises.get(0).NUMCDE.equals("")){
					Intent intent = new Intent(this, RecapitulatifRemiseEnBanque.class);
					Bundle b = new Bundle();
					b.putString("num_remise", remises.get(0).NUMCDE);
					b.putStringArrayList("id_encaissement", identifiants);
					intent.putExtras(b);
					startActivityForResult(intent, 0);
				}
			}
		}

	}


	void initGui()
	{
		tvModeTest=(TextView)findViewById(R.id.textViewModeTest);
		myListView=(ListView)findViewById(R.id.listView1);
		tvSynchro=(TextView)findViewById(R.id.tvsynchro);
		pb=(ProgressBar)findViewById(R.id.pb1);
		pb.setVisibility(View.GONE);
		spinnerLivreur=(Spinner)findViewById(R.id.spinnerLivreur);
		
		llSynchro=(LinearLayout)findViewById(R.id.llsynchro);
		llSynchro.setBackgroundColor(Color.LTGRAY);
		
		tvSynchro.setText(getDocNonTransmis());
		
		StringBuffer sbVer=new StringBuffer();
		 Global.curver=Fonctions.getVersion(this,sbVer);
		setTextViewText(this, R.id.textViewVersion,"v "+ sbVer.toString());

		majWithNewVersion(Global.curver);
		//on sauve la version actuelle
		Fonctions.WriteProfileString(this, "LASTVERSION", Global.curver+"");
		dispLoginInfo();
		PopulateList();
	}

	@Override
	public void onResume()
	{
		super.onResume();
		if (tvSynchro!=null)
		tvSynchro.setText(getDocNonTransmis());
		
		if (dbKD543LinInventaire.isTimeToDoInvent(this))
		{
			promptText("Inventaire", "Pensez à faire votre inventaire le dernier jour travaillé du mois",false); 
		}
	}
	//si on vient de changer de version on fait des contrôles particuliers
	void majWithNewVersion(int curver)
	{
		String lastver=Fonctions.GetProfileString(this, "LASTVERSION", Global.curver+"");
		if (Fonctions.convertToInt(lastver)==127  && curver==128)
		{
			TableSouches ts=new TableSouches(m_appState.m_db, this);
			ts.reset(Preferences.getValue(this, Espresso.LOGIN, "0"));
			Preferences.setValueBoolean(this, Espresso.PREF_SOUCHE,false);

			goSynchro(true);
			Fonctions.FurtiveMessageBox(this, "maj version 127 => 128");
		}
	}
	
	void dispLoginInfo()
	{
		dbSiteListeLogin login=new dbSiteListeLogin(m_appState.m_db);
		structlistLogin rep=new structlistLogin();
		login.getlogin(Preferences.getValue(this, Espresso.LOGIN, "0"), rep, new StringBuilder());
		codeagent=getLogin();
		setTextViewText(this, R.id.textViewCodeAgent,rep.LOGIN);
		setTextViewText(this, R.id.textViewNomAgent,rep.NOM);
		
		
		boolean modetest=Preferences.getValueBoolean(this, Espresso.PREF_MODETEST, false);
		if (modetest)
			tvModeTest.setVisibility(View.VISIBLE);
		else
			tvModeTest.setVisibility(View.GONE);
		
		String datejour = Fonctions.getDD_MM_YYYY();
		setTextViewText(this, R.id.textViewDate,datejour);
		
		TableSouches souche=new TableSouches(m_appState.m_db,this);
		//textViewNextBL
		TextView textNextBL=(TextView)findViewById(R.id.textViewNextBL);
		 textNextBL.setTextColor(Color.RED);
		
		passeplat pp=new passeplat();
		if (souche.get(souche.TYPEDOC_FACTURE,getLogin(), pp) )
		{
			
			setTextViewText(this, R.id.textViewNextBL,Fonctions.GetStringDanem(pp.NUMSOUCHE_COURANT));
		}
		else
			Fonctions.FurtiveMessageBox(DashboardActivity.this, "Veuillez faire une synchro");	
	
	}
	void initListerners()
	{
		
		
		registerForContextMenu(myListView);
		myListView.setOnItemClickListener(new OnItemClickListener() 
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {

				lineMenu o=(lineMenu)arg1.getTag();

				if (o.titre.equals(getString(R.string.menu_go)))
				{
					//		launchPrinting();
					//go();
					m_stlivreur=Fonctions.GetStringDanem(getlivreur());
					Preferences.setValue(DashboardActivity.this, Espresso.LIVREUR, m_stlivreur);	
				 if(Fonctions.GetStringDanem(m_stlivreur).equals("") ||  Fonctions.GetStringDanem(m_stlivreur).equals("xlivreur")  )
				 {
					Fonctions.FurtiveMessageBox(DashboardActivity.this, "Veuillez sélectionner un livreur.");					
				 }
				 else
				 {
					if(BSouche()==false)
					{ 
					}
					else
					 goFindCli();
						 
				 }
					
					
					return;
				}
				 
				if (o.titre.equals(getString(R.string.menu_param)))
				{
					goParams();
					return;
				}
				if (o.titre.equals(getString(R.string.Synchro)) )
				{
					goSynchro(false);
					return;
				}
				if (o.titre.equals(getString(R.string.menu_inventaire)))
				{
					 
					goInventaire();
					return;
				}
			/*	if (o.titre.equals(getString(R.string.menu_transfertStock)))
				{
					if (controlIsInventaireEnCours()==true) return;
					goTransfertStock();
					return;
				}
				*/
				if (o.titre.equals(getString(R.string.menu_catalogue)))
				{
					goCatalogue();
					return;
				}
				if (o.titre.equals(getString(R.string.menu_ech_entre_livreur)))
				{
					goEchangeStock();
					return;
				}
				if (o.titre.equals(getString(R.string.facturesdues)) )
				{
					if (controlIsInventaireEnCours()==true) return;
					
					LaunchFacturesDues();
					return;
				}
				if (o.titre.equals(getString(R.string.reb_title)) )
				{
					if(Global.dbKDRetourBanqueEnt.countRemiseEnBanqueNonTransmises() > 0) AlertMessage(getString(R.string.reb_non_transmises), false);
					else LaunchRemiseEnBanque();
					return;
				}
				if (o.titre.equals(getString(R.string.mes_rendez_vous)) )
				{
					if (controlIsInventaireEnCours()==true) return;
					
					rapportactivite.launchAgenda(DashboardActivity.this, "");
					return;
				}
				if (o.titre.equals(getString(R.string.statistiques)) )
				{
					myListView.showContextMenu(); 
					return;
				}
				//mes_rendez_vous
				Fonctions.FurtiveMessageBox(DashboardActivity.this,"En construction...");
			}
		}
				);
	}

	void goCatalogue()
	{

		Intent intent = new Intent(this,		commandeActivity.class);
		Bundle b=new Bundle();
		b.putString("codecli","");
		b.putString("soccode",Preferences.getValue(this, Espresso.CODE_SOCIETE, "0") );
		intent.putExtras(b);
		startActivityForResult(intent,88);

	}
	boolean controlIsInventaireEnCours()
	{
		if (Global.dbKDLinInv.isInventaireEnCours())
		{
			Fonctions._showAlert(getString(R.string.inventaire_encours), this);
			return true;
		}
		return false;
	}
	 
	void getListAgenda()
	{
		if (android.os.Build.VERSION.SDK_INT >= 14)
			Global.dbKDAgenda.getAllEvents( "SZF", m_appState);
	}
	void goSynchro(boolean autolaunch)
	{
		Intent i = new Intent(this, SynchroActivity.class);
		i.putExtra("autolaunch", autolaunch);
		startActivityForResult(i,LAUNCH_SYNCHRO);

		//	Intent i = new Intent(this, Mapview_polyline.class);
		//	startActivity(i);
	}
	int getRandom()
	{
		Random r = new Random();
		int Low = 1000;
		int High = 9999;
		int R = r.nextInt(High-Low) + Low;
		
		return R;
	}
	void goParams()
	{
		//Intent i = new Intent(this, prefs_authent.class);
		//startActivity(i);

		int r=getRandom();
		promptText("Saisissez le code pour la clé : "+r, R.string.menu_param, InputType.TYPE_CLASS_TEXT
				| InputType.TYPE_TEXT_VARIATION_PASSWORD,r);
	}
	void goInventaire()
	{
		Intent i0=new Intent(DashboardActivity.this,
				InventaireActivity.class);


		startActivityForResult(i0, LAUNCH_INVENTAIRE);
		//	promptText(getString(R.string.saisissez_le_mot_de_passe_administrateur), R.string.menu_inventaire, InputType.TYPE_CLASS_TEXT
		//			| InputType.TYPE_TEXT_VARIATION_PASSWORD);
	}
	boolean BSouche()
	{

		boolean bres=true;

		return bres;
	}
	void goEchangeStock()
	{
		Intent i0=new Intent(DashboardActivity.this,
				EchangeStockActivity.class);


		startActivityForResult(i0, LAUNCH_ECHANGESTOCK);
		//	promptText(getString(R.string.saisissez_le_mot_de_passe_administrateur), R.string.menu_inventaire, InputType.TYPE_CLASS_TEXT
		//			| InputType.TYPE_TEXT_VARIATION_PASSWORD);
	}
	 
	//	Intent i = new Intent(this, InventaireActivity.class);
	//	startActivity(i);
	//}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		String livreur=Preferences.getValue(DashboardActivity.this, Espresso.LIVREUR, "");
		
		fillLivreur(Fonctions.GetStringDanem(livreur));
		
		if (requestCode==LAUNCH_AGENDA)
		{
			if (resultCode==RESULT_OK)
			{

				String codeclient=data.getExtras().getString(AppBundle.CodeClient);
				if (codeclient.equals("")==false)
				{
					TableClient cli=new TableClient(m_appState.m_db);
					if (cli.getClient(codeclient, new structClient(),new StringBuilder()))
					{
						launchMenuCli(codeclient);
					}					
				}
			}
		}
		else if (requestCode==LAUNCH_PARAM)
		{
			//si checkbox souche on reinit les souches
			boolean souche=Preferences.getValueBoolean(this, Espresso.PREF_SOUCHE,false);

			//si on a coché reinit souche dans param ou si le code rep à changé
			//on réinit les souches

			if(BSouche()==false)
			{
				//promptText("SOUCHE","Le n° de souche doit être de 8 chiffres ", false);
			}
			else
			{
			//	TableSouches souche=new TableSouches(m_appState.m_db,DashboardActivity.this);
			//	souche.incNumSouche(Preferences.getValue(DashboardActivity.this, Espresso.LOGIN, "0"),"FA",stSouche);
				
				
				if (souche || codeagent.equals(getLogin())==false)
				{
					TableSouches ts=new TableSouches(m_appState.m_db, this);
					ts.reset(Preferences.getValue(this, Espresso.LOGIN, "0"));
					Preferences.setValueBoolean(this, Espresso.PREF_SOUCHE,false);
					//ts.reset(Preferences.getValue(this, Espresso.PREF_SSOUCHE, ""));

					Global.dbLog.Insert("Menu Principal", "Reset souche: ", "", "", "", "");
					
				//	goSynchro(true);
					
				
				}
				
				alertModeTest();
				 
				//si checkbox souche on reinit les souches
				boolean reset=Preferences.getValueBoolean(this, Espresso.PREF_RESET,false);
				if (reset)
				{
					dbKD kd=new dbKD();
					kd.clearAll(getDB());
					
					dbLog log=new dbLog(getDB());
					log.Clear(new StringBuilder());
					Preferences.setValueBoolean(this, Espresso.PREF_RESET,false);
					promptText("Reset de la base de données","Reset effectué", false);
				}


				//si checkbox souche on reinit les souches
				boolean resetlog=Preferences.getValueBoolean(this, Espresso.PREF_RESETLOG,false);
				if (resetlog)
				{

					dbLog log=new dbLog(getDB());
					log.Clear(new StringBuilder());
					Preferences.setValueBoolean(this, Espresso.PREF_RESETLOG,false);
					promptText("Reset des logs","Reset log effectué", false);
				}



			}
			
			
			
			
			dispLoginInfo();
		}
		else  
		{

			PopulateList();
			getListAgenda();
			
			//on le met ici car lancé avant le onresume
			getDocNonTransmis();
			if (nbrDataToSend>0)
			{
				task_sendWSData wsCde = new task_sendWSData(m_appState,hSync);
				wsCde.execute();
				pb.setVisibility(View.VISIBLE);
				Log.d("TAG", "start synchro");
				m_ProgressDialog = ProgressDialog.show(this,
						"Veuillez patienter...", "Envoi en cours...", true);
			}

		}
		dispLoginInfo();
	}
	
	
	private void PopulateList()
	{
		ArrayList<lineMenu> colNames = new ArrayList<lineMenu>();
		this.m_adapter = new listAdapter(this, R.layout.main_row, colNames);

		
		myListView.setAdapter(this.m_adapter);

		colNames.add(setMenuLine(R.string.menu_go,R.drawable.basic3_021_transport_delivery_van_shipping, ""));
		colNames.add(setMenuLine(R.string.menu_param,R.drawable.basic2_299_gear_settings, ""));
		colNames.add(setMenuLine(R.string.Synchro,R.drawable.basic1_102_wi_fi_wireless_router, ""));

		//colNames.add(setMenuLine(R.string.mes_rendez_vous,R.drawable.basic1_011_calendar, ""));

		//	colNames.add(setMenuLine(R.string.menu_catalogue,R.drawable.basic2_117_open_reading_book, ""));
		/*colNames.add(setMenuLine(R.string.facturesdues,R.drawable.basic2_018_money_coins, ""));

		TableSouches souche=new TableSouches(m_appState.m_db,this);		
		passeplat pp=new passeplat();

		if (souche.get(souche.TYPEDOC_REMISEBANQUE,getLogin(), pp))
			colNames.add(setMenuLine(R.string.reb_title,R.drawable.basic1_079_piggy_bank_saving, ""));


		if (souche.get(souche.TYPEDOC_ECHANGE,getLogin(), pp))
			colNames.add(setMenuLine(R.string.menu_ech_entre_livreur,R.drawable.basic1_116_user_group, ""));		

		if (souche.get(souche.TYPEDOC_INVENTAIRE,getLogin(), pp))
			colNames.add(setMenuLine(R.string.menu_inventaire,R.drawable.basic2_243_write_compose_new_pencil_paper, ""));		

		 */
		//colNames.add(setMenuLine(R.string.statistiques,R.drawable.basic2_235_graph_down_chart, ""));

	}

	class lineMenu
	{
		String titre;
		int icon;
		String hint;

		lineMenu()
		{

		}
	}

	lineMenu setMenuLine(int titre, int img, String hint)
	{
		lineMenu line=new lineMenu();
		line.titre=getString(titre);
		line.icon=img;
		line.hint=hint;

		return line;
	}



	private class listAdapter extends ArrayAdapter<lineMenu> {

		private ArrayList<lineMenu> items;

		public listAdapter(Context context, int textViewResourceId, ArrayList<lineMenu> items) {
			super(context, textViewResourceId, items);
			this.items = items;
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			if (v == null) {
				LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.main_row, null);
			}
			lineMenu o = items.get(position);

			if (o != null) {
				v.setTag(o);
				TextView tt = (TextView) v.findViewById(R.id.toptext);
				TextView bt = (TextView) v.findViewById(R.id.bottomtext);
				ImageView imgVw = (ImageView)v.findViewById(R.id.icon);


				Typeface face=Typeface.createFromAsset(getAssets(), Global.FONT_REGULAR);
				tt.setTypeface(face);

				imgVw.setImageResource(o.icon);
				if (tt != null) {
					tt.setText(o.titre);                            }
				if(bt != null){
					bt.setText(o.hint);
				}
			}
			return v;
		}
	}



	void goFindCli()
	{
		//if (controlIsInventaireEnCours()==true) return;

		 

		Intent intent = new Intent(this,			FindCliActivity.class);

		startActivityForResult(intent, 0);
	}

	private void copyAssets() {
		AssetManager assetManager = getAssets();
		String[] files = null;
		try {
			files = assetManager.list("");
		} catch (IOException e) {
			Log.e("tag", "Failed to get asset file list.", e);
		}catch(Exception ex)
		{

		}
		for(String filename : files) {
			InputStream in = null;
			OutputStream out = null;
			try {
				in = assetManager.open(filename);
				out = new FileOutputStream(ExternalStorage.getFolderPath(ExternalStorage.ROOT_FOLDER) + filename);
				copyFile(in, out);
				in.close();
				in = null;
				out.flush();
				out.close();
				out = null;
			} catch(IOException e) {
				Log.e("tag", "Failed to copy asset file: " + filename, e);
			} catch(Exception ex)
			{

			}
		}
	}
	private void copyFile(InputStream in, OutputStream out) throws IOException {
		byte[] buffer = new byte[1024];
		int read;
		while((read = in.read(buffer)) != -1){
			out.write(buffer, 0, read);
		}
	}

	boolean debloq(int key,String mdp)
	{
		int L=key-Fonctions.convertToInt(Fonctions.Left(key+"",1));
		int val=L*12345;
		String str=Fonctions.Mid(val+"",2, 4);
		if (Fonctions.convertToInt(str)==Fonctions.convertToInt(mdp))
			return true;
		return false;
	}
	public void promptText(String title, int cible, int inputType,final int key) {

		boolean bres = false;
		final int cibles=cible;
		final Dialog dialog = new Dialog(this);
		dialog.setContentView(R.layout.textentrvalertdialog);
		dialog.setTitle(title);
		final EditText inputLine = (EditText) dialog
				.findViewById(R.id.my_edittext);
		inputLine.setSingleLine(true);
		inputLine.setHint("Password");

		Button okButton = (Button) dialog.findViewById(R.id.OKButton);
		okButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				dialog.dismiss();
				String PasswordSiege = "aie";
		 
				if (inputLine.getText().toString().equals(PasswordSiege)
						|| debloq( key,  inputLine.getText().toString())) {
					Intent i0 = null;
					int id=0;
					if (cibles==R.string.menu_param)
					{
						Global.dbLog.Insert("Menu Principal", "Go Param", "", "", "", "");
						i0=new Intent(DashboardActivity.this,
								prefs_authent.class);
						id=LAUNCH_PARAM;
					}
					else if (cibles==R.string.menu_inventaire)
						i0=new Intent(DashboardActivity.this,
								InventaireActivity.class);
					 

					startActivityForResult(i0, id);

				}  
				else 
				{
					Fonctions.FurtiveMessageBox(DashboardActivity.this,getString(R.string.passworderrone));
				}
			}
		});
		Button cancelButton = (Button) dialog
				.findViewById(R.id.CancelButton);
		cancelButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				dialog.dismiss();
			}
		});
		inputLine.setInputType(inputType);

		dialog.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
		dialog.show();
	}

	public void MessageRecupCde(String message ) {

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(message)
		.setCancelable(false)
		.setPositiveButton(getString(android.R.string.yes),
				new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {


			}
		})
		.setNegativeButton(getString(android.R.string.no),
				new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {

			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}
	@Override
	public void onBackPressed() {
		if (doubleBackToExitPressedOnce) {
			super.onBackPressed();
			return;
		}

		this.doubleBackToExitPressedOnce = true;
		Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				doubleBackToExitPressedOnce=false;                       
			}
		}, 2000);
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
	 @Override
		public boolean onCreateOptionsMenu(Menu menu) {
			addMenu(menu, R.string.hotline,R.drawable.basic2_233_help);
		 
		
			 
			return true;
		}
	 
	 @Override
		public boolean onOptionsItemSelected(MenuItem item) {
			switch (item.getItemId()) {

		
			case R.string.hotline:
				Fonctions.sendEmailBaseDonneegPieceJointe(this,getLogin(),"hotline@danem.com","","","","Message adressé à la hotline de la part de MAX POILANE "+getLogin());
				
			
				return true;
			default:
				return super.onOptionsItemSelected(item);
			}
	 }
	
	String getDocNonTransmis()
	{
		String val="";
		nbrDataToSend=0;
		
		dbKD83EntCde cde=new dbKD83EntCde(getDB());
		int nbr_docvente=cde.Count();
		nbrDataToSend+=nbr_docvente;
		if (nbr_docvente>0)
		{
			val+=nbr_docvente+" doc(s) vente(s), ";
		}
		
/*		dbKD543LinInventaire inv=new dbKD543LinInventaire(getDB());
		int nbr_lininv=inv.Count(false);
		nbrDataToSend+=nbr_lininv;
		if (nbr_lininv>0)
		{
			val+=nbr_lininv+" ligne(s) d'inventaire, ";
		}
		
		dbKD546EchangeStock ech=new dbKD546EchangeStock(getDB());
		int nbr_linech=ech.Count(false);
		nbrDataToSend+=nbr_linech;
		if (nbr_linech>0)
		{
			val+=nbr_linech+" lignes echangées, ";
		}
		*/
		TableClient cli=new TableClient(getDB());
		int nbr_cli=cli.CountModified();
		nbrDataToSend+=nbr_cli;
		if (nbr_cli>0)
		{
			val+=nbr_cli+" client(s) modifié(s), ";
		}
		
		dbKD451RetourMachineclient rm=new dbKD451RetourMachineclient(getDB());
		int nbr_rm=rm.Count();
		nbrDataToSend+=nbr_rm;
		if (nbr_rm>0)
		{
			val+=nbr_rm+" matériel(s) retourné(s), ";
		}
		
		dbKD452ComptageMachineclient pm=new dbKD452ComptageMachineclient(getDB());
		int nbr_pm=pm.Count();
		nbrDataToSend+=nbr_pm;
		if (nbr_pm>0)
		{
			val+=nbr_pm+" matériel(s) prêté(s), ";
		}
		
		dbKD981RetourBanqueEnt rb=new dbKD981RetourBanqueEnt(getDB());
		int nbr_rb=rb.countRemiseEnBanqueNonTransmises();
		nbrDataToSend+=nbr_rb;
		if (nbr_rb>0)
		{
			val+=nbr_rb+" Remise en banque, ";
		}
		
		dbKD98Encaissement enc=new dbKD98Encaissement(getDB());
		int nbr_enc=enc.countNonTransmises();
		nbrDataToSend+=nbr_enc;
		if (nbr_enc>0)
		{
			val+=nbr_enc+" encaissement(s), ";
		}
		
		TableContactcli contact=new TableContactcli(getDB());
		int nbr_contact=contact.countNonTransmis();
		nbrDataToSend+=nbr_contact;
		if (nbr_contact>0)
		{
			val+=nbr_contact+" contact(s) modifié(s), ";
		}
		
		dbKD101ClientVu clivu=new dbKD101ClientVu(getDB());
		int nbr_clivu=clivu.countModified();
		nbrDataToSend+=nbr_clivu;
		if (nbr_clivu>0)
		{
			val+=nbr_clivu+" rapport(s) d'activité(s), ";
		}
		
		dbLog log=new dbLog(getDB());
		int nbr_log=log.Count();
		if (nbr_log>100)
		{
			val+=nbr_log+" logs, ";
		}
		
		val=Fonctions.Left(val, val.length()-2);
		if (val.equals("")==false)
		{
			val="A transmettre : "+val;
		}
		else
		{
			val="Aucune donnée à transmettre";
		}
		return val;
	}
	
	Handler getHandlerSync() {
		Handler h = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				 
				super.handleMessage(msg);
				Bundle bGet = msg.getData();
				if (msg.what==455)
				{
					pb.setVisibility(View.GONE);
					m_ProgressDialog.dismiss();
					Log.d("TAG", "stop synchro");
					if (tvSynchro!=null)
						tvSynchro.setText(getDocNonTransmis());
				}
				 
			}
		};
		return h;
	}
	
	 @Override
		public void onCreateContextMenu(ContextMenu menu, View v,
				ContextMenuInfo menuInfo) {
			super.onCreateContextMenu(menu, v, menuInfo);
			
			 
			menu.add(0, 1					,0,"Synthèse articles hebdo");
			menu.add(0, 2					,0,"Suivi stock hebdo");
			menu.add(0, 3					,0,"Mon stock");
			
	 }
	 
	 @Override
	 public boolean onContextItemSelected(MenuItem item) {
		  AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
 			switch (item.getItemId()) {
 			case 1:
		    	launchSyntheseHebdo();
		      break;
		    case 2:
		    	launchSuiviStock();
		      break;
		    case 3:
		    	launchStockTheo();
		      break;
		    default:
		      return super.onContextItemSelected(item);
		  }
		  return true;
		}
	 void fillLivreur(String selVal) {
			try {
				if (Global.dbParam.getLivreurActives(idLivreur)  == true) {

					Bundle bundle = new Bundle();



					bundle = new Bundle();
					bundle.putString(Global.dbParam.FLD_PARAM_CODEREC,getString(R.string.livreur_select));
					bundle.putString(Global.dbParam.FLD_PARAM_LBL, "Sélectionner livreur");
					bundle.putString(Global.dbParam.FLD_PARAM_COMMENT, getString(R.string.livreur_select));
					bundle.putString(Global.dbParam.FLD_PARAM_VALUE, getString(R.string.livreur_select));
					
					idLivreur.add(0,bundle);
					
					
					
					int pos = 0;
					String[] items = new String[idLivreur.size()];
					for (int i = 0; i < idLivreur.size(); i++) {

						items[i] = idLivreur.get(i).getString(
								Global.dbParam.FLD_PARAM_LBL);

						String codeRec = idLivreur.get(i).getString(
								Global.dbParam.FLD_PARAM_CODEREC);

						if (selVal.equals(codeRec)) {
							pos = i;
						}

					}


					Spinner spinner = (Spinner) findViewById(R.id.spinnerLivreur);

					ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
							android.R.layout.simple_spinner_item, items);
					adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					spinner.setAdapter(adapter);

					spinner.setSelection(pos);

				}

			} catch (Exception ex) {

			}

		}
	 
	 String getlivreur()
	 {
		String stlivreur="";
		int pos = this.getSpinnerSelectedIdx(this, R.id.spinnerLivreur);
		if (pos > -1)
		{
			try {
				stlivreur = idLivreur.get(pos).getString(
						Global.dbParam.FLD_PARAM_CODEREC);

			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}				
		}
			
		return stlivreur;			
	 }
}
