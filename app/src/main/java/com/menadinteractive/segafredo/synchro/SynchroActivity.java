package com.menadinteractive.segafredo.synchro;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.menadinteractive.maxpoilane.BaseActivity;
import com.menadinteractive.maxpoilane.ExternalStorage;
import com.menadinteractive.maxpoilane.app;
import com.menadinteractive.maxpoilane.R;
import com.menadinteractive.segafredo.communs.Fonctions;
import com.menadinteractive.segafredo.communs.Global;
import com.menadinteractive.segafredo.communs.MyWS;
import com.menadinteractive.segafredo.db.Preferences;
import com.menadinteractive.segafredo.db.TableSouches;
import com.menadinteractive.segafredo.db.TableStatSyntheseArticlesHebdo;
import com.menadinteractive.segafredo.db.TableSuiviStockHebdo;
import com.menadinteractive.segafredo.db.dbKD545StockTheoSrv;
import com.menadinteractive.segafredo.db.dbKD729HistoDocuments;
import com.menadinteractive.segafredo.db.dbKD730FacturesDues;
import com.menadinteractive.segafredo.db.dbKD731HistoDocumentsLignes;
import com.menadinteractive.segafredo.db.dbParam;
import com.menadinteractive.segafredo.db.dbSiteListeLogin;
import com.menadinteractive.segafredo.plugins.Espresso;

public class SynchroActivity extends BaseActivity implements OnClickListener {
	ListView myListView;
	boolean ssl;
	String login, password;
	MySimpleArrayAdapter myAdapter;
	ArrayList<Bundle> list;
	int nbrError=0;//nbr de scripts en erreur de connexion lors d'une synchro
	Button bGO;
	private ProgressDialog m_ProgressDialog = null;
	String scenario = "";
	ArrayList<String> script = new ArrayList<String>();

	final String scenario_clients = "GETCLIENTS";// "GETSECTEUR2";//"EXPRESSO";
	final String scenario_produits = "GETARTICLES2";
	final String scenario_horaires = "GETEXPRESSOHORAIRES";
	final String scenario_fermetures = "GETEXPRESSOFERMETURES";
//	final String scenario_stock = "GETEXPRESSOSTOCK";
	final String scenario_param = "GETPARAM";
//	final String scenario_societe = "GETSOCIETE";
	final String scenario_quest = "GETPLAN";
	final String scenario_tarif = "GETTARIFS";
	final String scenario_contactcli = "GETCONTACTS";
	final String scenario_souches = "GETSOUCHES";
	final String scenario_histodocent = "GETHISTODOCENT";
	final String scenario_facturesdues = "GETFACTURESDUES";
	final String scenario_histodoclin = "GETHISTODOCLIN";
	final String scenario_agents= "GETAGENTS";
	final String scenario_updateapp = "UPDATEAPP";
	//final String scenario_materielclient = "GETMATERIELCLIENT";
	//final String scenario_listemachine = "GETLISTEMACHINE";
	//final String scenario_stocktheo = "GETSTOCKTHEO";
	//final String scenario_statarthebdo = "GETSTATARTHEBDO";
	//final String scenario_stockhebdo = "GETSTOCKHEBDO";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_synchro);

		login = Preferences.getValue(this, Espresso.LOGIN, "0");
		password = Preferences.getValue(this, Espresso.PASSWORD, "0");
		ssl=Preferences.getValueBoolean(this, Espresso.SSL,false);
		
		initVars();
		initGui();
		initListeners();
	}

	void initGui() {
		myListView = (ListView) findViewById(R.id.listView1);
		bGO = (Button) findViewById(R.id.buttonGO);

		myListView.setAdapter(myAdapter);
		
		Bundle bundle = this.getIntent().getExtras();
		boolean autolaunch =getBundleValueBool(bundle, "autolaunch") ; 
		if (autolaunch)
		{
			goSync();
		}
	}

	protected void initVars() {
		list = new ArrayList<Bundle>();
		myAdapter = new MySimpleArrayAdapter(this, list);

		list.add(createItem(scenario_contactcli, getString(R.string.contacts),
				Global.dbContactcli.Count() + ""));
		list.add(createItem(scenario_clients, getString(R.string.clients),
				Global.dbClient.Count() + ""));
		list.add(createItem(scenario_produits, getString(R.string.produits),
				Global.dbProduit.Count() + ""));
		list.add(createItem(scenario_param, getString(R.string.param_tres),
				Global.dbParam.Count() + ""));
	//	list.add(createItem(scenario_societe, getString(R.string.info_soci_t_),
	//			Global.dbSoc.Count() + ""));
		list.add(createItem(scenario_tarif, getString(R.string.tarif),
				Global.dbTarif.Count() + ""));
	//	list.add(createItem(scenario_materielclient, getString(R.string.listematerielclient),
	//			Global.dbMaterielClient.Count() + ""));
		
	//	list.add(createItem(scenario_listemachine, getString(R.string.listemachine),
	//			Global.dbListeMateriel.Count() + ""));
		
	

		TableSouches souche = new TableSouches(m_appState.m_db, this);
		list.add(createItem(scenario_souches, getString(R.string.souches),
				souche.Count() + ""));

		dbSiteListeLogin agents = new dbSiteListeLogin(m_appState.m_db);
		list.add(createItem(scenario_agents, getString(R.string.agents),
				agents.Count() + ""));
		
		dbKD729HistoDocuments hfe = new dbKD729HistoDocuments(m_appState.m_db);
		list.add(createItem(scenario_histodocent, getString(R.string.histodoc),
				hfe.Count() + ""));

		dbKD731HistoDocumentsLignes hdl = new dbKD731HistoDocumentsLignes(
				m_appState.m_db);
		list.add(createItem(scenario_histodoclin,
				getString(R.string.histodoclin), hdl.Count() + ""));

		dbKD730FacturesDues fd = new dbKD730FacturesDues(m_appState.m_db);
		list.add(createItem(scenario_facturesdues,
				getString(R.string.facturesdues), fd.Count() + ""));
		
	//	dbKD545StockTheoSrv st = new dbKD545StockTheoSrv(m_appState.m_db);
	//	list.add(createItem(scenario_stocktheo,
	//			getString(R.string.stock_th_orique), st.Count(false) + ""));
		
	//	TableStatSyntheseArticlesHebdo sah = new TableStatSyntheseArticlesHebdo(m_appState.m_db);
	//	list.add(createItem(scenario_statarthebdo,
	//			getString(R.string.synth_se_articles_hebdo), sah.Count() + ""));
		
	//	TableSuiviStockHebdo ssh = new TableSuiviStockHebdo(m_appState.m_db);
	//	list.add(createItem(scenario_stockhebdo,
	//			getString(R.string.suivi_stock_hebdo), ssh.Count() + ""));
	}

	void initListeners() {
		bGO.setOnClickListener(this);
	}

	Bundle createItem(String scenario, String lbl, String count) {
		Bundle b = new Bundle();
		b.putString("scenario", scenario);
		b.putString("lbl", lbl);
		b.putString("counttable", count);
		return b;
	}

	void infoItem(String scenario, int count) {
		for (int i = 0; i < list.size(); i++) {
			String courant = list.get(i).getString("scenario");
			if (courant.equals(scenario)) {
				list.get(i).putInt("countreceive", count);
				break;
			}

		}
	}

	public class MySimpleArrayAdapter extends ArrayAdapter<Bundle> {
		private final Context context;
		ArrayList<Bundle> values;

		public MySimpleArrayAdapter(Context context, ArrayList<Bundle> values) {
			super(context, R.layout.item_synchro, values);
			this.context = context;
			this.values = values;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View rowView = inflater.inflate(R.layout.item_synchro, parent,
					false);

			Bundle o = values.get(position);
			String lbl = o.getString("lbl");
			String counttable = o.getString("counttable");
			int countreceive = getBundleValueInt(o, "countreceive");

			if (o != null) {
				TextView tt = (TextView) rowView.findViewById(R.id.text1);
				TextView tvcounttable = (TextView) rowView
						.findViewById(R.id.textcounttable);
				TextView tvcountreceive = (TextView) rowView
						.findViewById(R.id.textcountreceive);

				tt.setText(lbl);
				tvcounttable.setText(getString(R.string.dans_la_base_actuelle_)
						+ counttable);

				tvcountreceive.setText(getString(R.string.re_u) + countreceive);
			}

			return rowView;
		}
	}

	@Override
	public void onClick(View v) {
		if (v == bGO) {
			goSync();
		}
	}

	void goSync()
	{
		bGO.setEnabled(false);
		script.add(scenario_param);
		script.add(scenario_updateapp);
		
		//script.add(scenario_stockhebdo);
		//script.add(scenario_statarthebdo);
		//script.add(scenario_stocktheo);
		script.add(scenario_facturesdues);
		script.add(scenario_produits);			
		script.add(scenario_clients);
	//	script.add(scenario_societe);
		script.add(scenario_contactcli);
		script.add(scenario_tarif);
	//	script.add(scenario_materielclient);
	//	script.add(scenario_listemachine);			
		script.add(scenario_souches);
		script.add(scenario_agents);			
		script.add(scenario_histodocent);
		script.add(scenario_histodoclin);
		
		getWSData(login, password);
	}
	private Runnable progress = new Runnable() {
		@Override
		public void run() {
			m_ProgressDialog = ProgressDialog.show(SynchroActivity.this,
					"Veuillez patienter...", "Synchro en cours...", true);
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};
	/**
	 * @author Marc VOUAUX cette fonction permet au thread d'intÔøΩragir avec
	 *         l'interface grace ÔøΩ runOnUiThread(returnRes);
	 */
	private Runnable returnRes = new Runnable() {

		@Override
		public void run() {
			bGO.setEnabled(true);
			
			if (nbrError>0)
				promptText(getString(R.string.r_sultat_de_la_synchro),nbrError+getString(R.string._erreur_s_),false);
			else
				promptText(getString(R.string.r_sultat_de_la_synchro),getString(R.string.synchronisation_r_ussie_),true);
		}
	};

	String scenarioEncours = "";
	private Runnable changeMessage = new Runnable() {
		@Override
		public void run() {
			// Log.v(TAG, strCharacters);
			// on suprime DANEM.FR des messages par sécurité
			if (scenarioEncours != null)
				if (scenarioEncours.contains("danem.fr"))
					scenarioEncours = scenarioEncours.replace(".danem.fr", "");

			m_ProgressDialog.setMessage(scenarioEncours);

			myAdapter.notifyDataSetChanged();
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};

	void receive(boolean alreadyInThread) {
		if (alreadyInThread == false)
			runOnUiThread(progress);

		try {
			// Thread.sleep(1000);

			nbrError=0;
			int nbr = script.size();
			for (int i = 0; i < nbr; i++) {
				Thread.sleep(500);
				scenario = script.get(i);

				scenarioEncours = getString(R.string.sync_msgreception)
						+ scenario;
				runOnUiThread(changeMessage);

				//
				if (scenario.equals(scenario_updateapp))
				{
					if (downloadVersionMaj(login))
					{
						doMajProgram();
						//break;
					}
				}
				else
				{
					if (scenario.equals(scenario_clients))
					{
						if(Global.dbClient. CountModified()>0)
						{
							
						}
						else
							getBorne();
						
					}
					else
						getBorne();					
				}

			}

		} catch (Exception e) {

			Log.e("tag", e.getMessage());
		}
		// dismiss the progressdialog
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// dismiss the progressdialog
		if (alreadyInThread == false)
		{
			m_ProgressDialog.dismiss();
		
		}
		runOnUiThread(returnRes);
	}

	boolean getBorne() {
		int STEP = 1000;
		String LineComplete = "";
		int incr = 0;
		int nbrLine = 0, nbrLineTotal = 0;
		String insert = "";

		Date interestingDate = new Date();
		ArrayList<String[]> arrLines = new ArrayList<String[]>();
		long time = (new Date()).getTime() - interestingDate.getTime();
		do {

			MyWS.Result resultWS = new MyWS.Result();
			if (ssl==false)
				resultWS = MyWS.WSQueryZippedExBorne(login, password, scenario, "",
					incr, STEP);
			else
				resultWS = MyWS.WSQueryZippedExBorneSSL(login, password, scenario, "",
						incr, STEP);
			
			if (resultWS.isConnSuccess() == false) {
				scenarioEncours = getString(R.string.errconnect);
				scenarioEncours = resultWS.getExceptionMessage();
				runOnUiThread(changeMessage);
				Log.d("TAG",
						scenario + " -> Error packet: " + String.valueOf(incr));
				
				nbrError++;
				return false;
			}

			String[] lines = MyWS
					.get_EXECEXINSERT_Values(resultWS.getContent());
			nbrLine = Fonctions.convertToInt(lines[0]);
			// insert=lines[1];
			// for (int l=2;l<lines.length;l++)
			// LineComplete+=lines[l]+"\n";
			arrLines.add(lines);
			Log.d("TAG",
					scenario + " -> Recu: " + String.valueOf(incr + nbrLine));
			incr += STEP;

			nbrLineTotal += nbrLine;
		} while (nbrLine == STEP);

		if (scenario.equals(scenario_produits))
			Global.dbProduit.Clear(new StringBuilder());
		if (scenario.equals(scenario_clients))
			Global.dbClient.clear(new StringBuilder());
		if (scenario.equals(scenario_contactcli))
			Global.dbContactcli.clear(new StringBuilder());
	//	if (scenario.equals(scenario_societe))
	//		Global.dbSoc.Clear(new StringBuilder());
		if (scenario.equals(scenario_param))
			Global.dbParam.Clear(new StringBuilder());
		if (scenario.equals(scenario_tarif))
			Global.dbTarif.clear(new StringBuilder());
		/*if (scenario.equals(scenario_materielclient))
			Global.dbMaterielClient.clear(new StringBuilder());
		if (scenario.equals(scenario_listemachine))
			Global.dbListeMateriel.clear(new StringBuilder());
		*/
		if (scenario.equals(scenario_souches)) {
			TableSouches souche = new TableSouches(m_appState.m_db, this);
			souche.clear(new StringBuilder());
		}
		if (scenario.equals(scenario_agents)) {
			dbSiteListeLogin agent = new dbSiteListeLogin(m_appState.m_db);
			agent.Clear(new StringBuilder());
		}
		if (scenario.equals(scenario_histodocent)) {
			dbKD729HistoDocuments hfe = new dbKD729HistoDocuments(
					m_appState.m_db);
			hfe.clear(new StringBuilder());
		}
		if (scenario.equals(scenario_histodoclin)) {
			dbKD731HistoDocumentsLignes hdl = new dbKD731HistoDocumentsLignes(
					m_appState.m_db);
			hdl.clear(new StringBuilder());
		}
		if (scenario.equals(scenario_facturesdues)) {
			dbKD730FacturesDues fd = new dbKD730FacturesDues(m_appState.m_db);
			fd.clear(new StringBuilder());
		}
		
		/*if (scenario.equals(scenario_stocktheo)) {
			dbKD545StockTheoSrv fd = new dbKD545StockTheoSrv(m_appState.m_db);
			fd.clear(false,new StringBuilder());
		}
		if (scenario.equals(scenario_statarthebdo)) {
			TableStatSyntheseArticlesHebdo sah = new TableStatSyntheseArticlesHebdo(m_appState.m_db);
			sah.clear( new StringBuilder());
		}
		
		if (scenario.equals(scenario_stockhebdo)) {
			TableSuiviStockHebdo ssh = new TableSuiviStockHebdo(m_appState.m_db);
			ssh.clear( new StringBuilder());
		}*/
		infoItem(scenario, nbrLineTotal);
		ArrayList<String> timeIntegre = new ArrayList<String>();

		scenarioEncours = getString(R.string.sync_msgintegration) + scenario;
		runOnUiThread(changeMessage);

		boolean b=true;
		if (nbrLineTotal>0)
			b = integreWSDataArray(arrLines, STEP, timeIntegre, m_appState);

		return b;
	}

	public boolean integreWSDataArray(ArrayList<String[]> arr, int step,
			ArrayList<String> timeelapse, app myapp) {

		if(arr==null || arr.get(0).length==0) 
			return true;
		Date interestingDate = new Date();

		StringBuilder err = new StringBuilder();
		myapp.m_db.execSQL("BEGIN", err);

		for (int line = 0; line < arr.size(); line++) {
			String[] extract = arr.get(line);

			String stInsert = extract[1];
			for (int i = 2; i < extract.length; i++) {
				String query = extract[i];
				myapp.m_db.execSQL(stInsert + query, err);
			}
		}

		myapp.m_db.execSQL("COMMIT", err);

		long time = (new Date()).getTime() - interestingDate.getTime();
		timeelapse.add(String.valueOf(time));
		// FurtiveMessageBox("DonnÔøΩes intÔøΩgrÔøΩes");

		return true;
	}

	String getWSData(String _user, String _pwd) {

		try {
			new Thread() {

				@Override
				public void run() {
					nbrError=0;
					receive(false);

				}
			}.start();

			return "";

		} catch (Exception ex) {

		}
		return "";
	}
	
	String getWaitingNewVersion(String user)
	{
		dbParam dbVer=new dbParam(m_appState.m_db);
		int serverVer=Fonctions.convertToInt(dbVer.getLblAllSoc(dbVer.PARAM_MINVER,"VER",user));
		if (serverVer>0 && Fonctions.getVersion(this,new StringBuffer())!=serverVer)
		{
			String filename ="maxpoilane_rel_"+serverVer+".apk";
			
			return filename;
		}
		return "";
	
	}
	//recup la maj de l'appli
		boolean downloadVersionMaj(String user)
		{
		//	Fonctions.getHHTTPFile("","toto.apk");
			String filename=getWaitingNewVersion(user);
		 
			if (filename.equals("")==false)
			{
			 
				if (Fonctions.IsFileExist(ExternalStorage.getFolderPath(ExternalStorage.DOWNLOAD_FOLDER),filename)==false)
				{
					Fonctions.DeleteFiles(ExternalStorage.getFolderPath(ExternalStorage.DOWNLOAD_FOLDER),filename);
					String pathDest=ExternalStorage.getFolderPath(ExternalStorage.DOWNLOAD_FOLDER)+filename;
					
					
					
						
					String url="http://sdmaj.danem.fr/zrserveurws/maj/maxpoilane/" +filename;
					Log.d("TAG","start DL :"+url);
					
					int ret=Fonctions.getHHTTPFile(url,filename);
					if (ret==1) return true;
					else return false;
				 
				}
				else
				{
					Log.d("TAG","MAJ  deja recue, en attente sur le storage");
					return true;
				}
			}
			return false;
		}
		
		boolean doMajProgram( )
		{
			String file=getWaitingNewVersion(login);
			String dir=ExternalStorage.getFolderPath(ExternalStorage.DOWNLOAD_FOLDER);
			if (Fonctions.IsFileExist(dir,file))
			{
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setDataAndType(Uri.fromFile(new File(dir+file
						)),
						"application/vnd.android.package-archive");
				startActivity(intent);
				
				return true;
			}
			return false;
		}
		
		
}
