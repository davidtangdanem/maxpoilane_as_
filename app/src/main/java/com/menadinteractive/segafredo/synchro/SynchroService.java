package com.menadinteractive.segafredo.synchro;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TreeMap;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.menadinteractive.maxpoilane.Debug;
import com.menadinteractive.maxpoilane.ExternalStorage;
import com.menadinteractive.maxpoilane.app;
import com.menadinteractive.maxpoilane.R;
import com.menadinteractive.segafredo.communs.Fonctions;
import com.menadinteractive.segafredo.communs.Global;
import com.menadinteractive.segafredo.communs.MyParser;
import com.menadinteractive.segafredo.communs.MyWS;
import com.menadinteractive.segafredo.db.MyDB;
import com.menadinteractive.segafredo.db.Preferences;
import com.menadinteractive.segafredo.db.TableClient;
import com.menadinteractive.segafredo.db.TableContactcli;
import com.menadinteractive.segafredo.db.dbLog;
import com.menadinteractive.segafredo.db.dbProspect;
import com.menadinteractive.segafredo.plugins.Espresso;

public class SynchroService extends BaseService{
	/** Handler & Manager */
	private Handler handler;
	public static boolean isLaunched = false;
	static boolean paused = false;

	protected app m_appState;
	//	ArrayList<String> scenarios;

 
	/** Scenario */
	private static String currentScenario="";
	final String scenario_clients = "EXPRESSO";//"GETSECTEUR2";//"EXPRESSO";
	final String scenario_produits = "GETARTICLES2";
	final String scenario_horaires = "GETEXPRESSOHORAIRES";
	final String scenario_fermetures = "GETEXPRESSOFERMETURES";
	final String scenario_stock = "GETEXPRESSOSTOCK";
	final String scenario_param = "GETPARAM";
	final String scenario_societe = "GETSOCIETE";
	final String scenario_quest = "GETPLAN";
	final String scenario_tarif = "GETTARIFS";
	final String scenario_contactcli = "GETCONTACTS";
	final String scenario_materielclient = "GETMATERIELCLIENT";
	final String scenario_listemachine = "GETLISTEMACHINE";
	
	
	
	


	TreeMap<String, String> scenarioFonctions;
	public static boolean updateUI_PI = false;
	public static boolean closeall = false;
	public static boolean allSynchro = false;
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	static Intent m_intentService =null;
	public static void triggerService(Context context, boolean updatePI, boolean _allSynchro){
		Debug.Log("TriggerService Synchro");

		if(!isLaunched && !paused){
			m_intentService= new Intent(context, SynchroService.class);

			context.startService(m_intentService);
			updateUI_PI = updatePI;
			allSynchro = _allSynchro;
			closeall=false;
		}
		else{
			//mv		context.stopService(m_intentService);
			//mv		closeall=true;
			//mv		updateUI_PI=false;
			Debug.Log("Service already running on "+currentScenario);
		}
	}

	public static boolean isServiceRunning(){
		return isLaunched;
	}

	private void initScenrios(boolean allScenario){
		
		//scenarios = new ArrayList<String>();
		scenarioFonctions = new TreeMap<String, String>();
		if(allScenario){
			scenarioFonctions.put(scenario_clients, "ExecExInsertHdrZip");
			scenarioFonctions.put(scenario_contactcli, "ExecExInsertHdrZip");
			
			//scenarioFonctions.put(scenario_clients, "ExecExInsertHdrZip");
			scenarioFonctions.put(scenario_horaires, "ExecExInsertHdrZip");
			scenarioFonctions.put(scenario_produits, "ExecExInsertHdrZip");
			scenarioFonctions.put(scenario_fermetures, "ExecExInsertHdrZip");
			scenarioFonctions.put(scenario_stock, "ExecExInsertHdrZip");
			scenarioFonctions.put(scenario_param, "ExecExInsertHdrZip");
			scenarioFonctions.put(scenario_societe, "ExecExInsertHdrZip");
			scenarioFonctions.put(scenario_quest, "ExecExInsertHdrZip");
			scenarioFonctions.put(scenario_tarif, "ExecExInsertHdrZip");
			scenarioFonctions.put(scenario_materielclient, "ExecExInsertHdrZip");
			scenarioFonctions.put(scenario_listemachine, "ExecExInsertHdrZip");
			
			
			
			
			//scenarios.add(scenario_clients);
			//scenarios.add(scenario_horaires);
			//scenarios.add(scenario_fermetures);
			//scenarios.add(scenario_stock);
		}else{
			scenarioFonctions.put(scenario_clients, "ExecExInsertHdrZip");
			scenarioFonctions.put(scenario_contactcli, "ExecExInsertHdrZip");
			
		}

	}
	@Override
	public void onCreate() {
		super.onCreate();
		Debug.Log("Service On Create");
		isLaunched = true;
		m_appState = ((app)getApplicationContext());
		initScenrios(allSynchro);
		if(handler == null)
			handler = getHandler();

		handler.sendEmptyMessage(MESSAGE_PROCESS_NEXT_SCENARIO);
 
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		isLaunched = false;
		broadcastUIProgressBar();
		if(updateUI_PI){
			broadcastUIUpdatePI();
			updateUI_PI = false;
		}
		Debug.Log("Service synchro On Destroy");

	}
	public static boolean isPaused() {
		return paused;
	}

	public static void setPaused(boolean paused) {
		SynchroService.paused = paused;
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);

	}
	private void stopService(){
		stopSelf();
	}


	/** Handler */
	Handler getHandler(){
		return new Handler(){
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);

				switch(msg.what) {
				case MESSAGE_PROCESS_NEXT_SCENARIO:
					broadcastUIProgressBar();
					if(scenarioFonctions.size()>0){
						String nextScenar = scenarioFonctions.firstKey();
						String nextFonction = scenarioFonctions.remove(nextScenar);
						currentScenario = nextScenar;
						Debug.Log("TAG6","process next scenario/function (SynchroService): "+nextScenar+"/"+nextFonction, true);
						//						WS_onThread("POWERD", "RED", "ExecExInsertHdr", scenario_fermetures);
						String login = Preferences.getValue(SynchroService.this, Espresso.LOGIN, "0");
						String password = Preferences.getValue(SynchroService.this, Espresso.PASSWORD, "0");
						WS_onThread(login, password, nextFonction, nextScenar);
					}
					else{
						stopSelf();
					}
					//					broadcastUIProgressBar();
					break;

					//				case MESSAGE_INVALIDATE_MAP:
					//					if(mapView != null)
					//						mapView.invalidate();
					//				break;
				}

			}
		};
	}



	private void WS_onThread(final String user, final String pwd, final String wsFonction, final String scenario){
		try
		{
			new Thread( ){

				@Override
				public void run() 
				{
					this.setName("Thread_"+scenario);
					//WS(user, pwd, wsFonction, scenario);
				}
			}.start();			 


		}
		catch(Exception ex)
		{

		}
	}
	private void WS(String user, String pwd, String wsFonction, String scenario){
		StringBuilder err=new StringBuilder();
		String filtre="";
		sendMediasFiles(user,pwd);
		getKemsLogWs(user, pwd);
		//avant de recevoir les clients , on envoi les prospects
		if (scenario.equals(scenario_clients))
		{
			//String error=getProspectForWs(user,pwd);
			String error="";
			
			//si error ou si prospect pas encore transmis on bloque
			if (!error.equals("") )
			{
				if(handler != null)
					handler.sendEmptyMessage(MESSAGE_PROCESS_NEXT_SCENARIO);
				return;
			}
			error=getUpdateCliForWS( user, pwd);
			//si error ou si prospect pas encore transmis on bloque
			if (!error.equals("") /*|| Global.dbClient.CountProspect()>0*/)
			{
				if(handler != null)
					handler.sendEmptyMessage(MESSAGE_PROCESS_NEXT_SCENARIO);
				return;
			}
			filtre = " and soc_code='"+Preferences.getValue(SynchroService.this, Espresso.CODE_SOCIETE, "0")+"' and CODEVRP='"+Preferences.getValue(SynchroService.this, Espresso.LOGIN, "0")+"'";
		}
		if (scenario.equals(scenario_contactcli))
		{
			String error=getContactcliForWs(user,pwd);
			//si error ou si prospect pas encore transmis on bloque
			if (!error.equals("") )
			{
				if(handler != null)
					handler.sendEmptyMessage(MESSAGE_PROCESS_NEXT_SCENARIO);
				return;
			}
			 error=getUpdateCliForWS( user, pwd);
			//si error ou si contact pas encore transmis on bloque
			if (!error.equals("") /*|| Global.dbClient.CountProspect()>0*/)
			{
				if(handler != null)
					handler.sendEmptyMessage(MESSAGE_PROCESS_NEXT_SCENARIO);
				return;
			}
			 error=getDeleteContactcliForWS(user,pwd);
			//si error ou si prospect pas encore transmis on bloque
			if (!error.equals("") )
			{
				if(handler != null)
					handler.sendEmptyMessage(MESSAGE_PROCESS_NEXT_SCENARIO);
				return;
			}
			filtre = " and CODEVRP='"+Preferences.getValue(SynchroService.this, Espresso.LOGIN, "0")+"'";
		}
		if(scenario.equals(scenario_param)){
			filtre = " and soc_code='"+Preferences.getValue(SynchroService.this, Espresso.CODE_SOCIETE, "0")+"'";
		}
		if(scenario.equals(scenario_produits)){
			filtre = " and soc_code='"+Preferences.getValue(SynchroService.this, Espresso.CODE_SOCIETE, "0")+"'";
		}
		if (scenario.equals(scenario_societe))
		{
			filtre = " and code='"+Preferences.getValue(SynchroService.this, Espresso.CODE_SOCIETE, "0")+"'";

		}
		if(scenario.equals(scenario_tarif)){
			filtre = " and COD_CLI in (select CODECLIENT from T_NEGOS_CLIENTS where CODVRP = '"+Preferences.getValue(SynchroService.this, Espresso.LOGIN, "0")+"')";
		}
		if(scenario.equals(scenario_materielclient)){
			
			filtre = " and PAR_CODVRP='"+Preferences.getValue(SynchroService.this, Espresso.LOGIN, "0")+"'";			
		}
		if(scenario.equals(scenario_listemachine)){
			
			filtre = " ";			
		}
		
		MyWS.Result resultWS=new MyWS.Result();

		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		String currentDateandTime = sdf.format(new Date());

		Log.i("TIME", "Time 1 :"+currentDateandTime);
		resultWS=MyWS.WSQueryZippedEx(user,pwd,wsFonction,scenario,filtre,"","","","",false);
		currentDateandTime = sdf.format(new Date());
		Log.i("TIME", "Time 2 :"+currentDateandTime);
		Debug.Log("TAG6",resultWS.toString());
		Debug.Log("TAG5",resultWS.isConnSuccess()+"");
		Debug.Log("TAG6",resultWS.getContent());
		if (resultWS.isConnSuccess()==false)
		{
			if(handler != null)
				handler.sendEmptyMessage(MESSAGE_PROCESS_NEXT_SCENARIO);
		}
		else{
			if (scenario==scenario_clients)
				Global.dbClient.clear(err);
			else if (scenario==scenario_contactcli)
				Global.dbContactcli.clear(err);
			else if (scenario==scenario_horaires)
				Global.dbHoraire.clear(err);
			else if (scenario==scenario_produits)
				Global.dbProduit.Clear(err);
			else if (scenario==scenario_fermetures)
				Global.dbFermeture.clear(err);
			else if (scenario==scenario_stock)
				Global.dbStock.clear(err);
			else if (scenario==scenario_param)
				Global.dbParam.Clear(err);
			else if (scenario==scenario_societe)
				Global.dbSoc.Clear(err);
			else if (scenario==scenario_quest)
			{
				Global.dbKDEntPlan.Clear(err);
				Global.dbKDLinPlan.Clear(err);
			}else if (scenario==scenario_tarif)
			{
				Global.dbTarif.clear(err);
			}
			else if (scenario==scenario_materielclient)
			{
				Global.dbMaterielClient.clear(err);
			}
			else if (scenario==scenario_listemachine)
			{
				Global.dbListeMateriel.clear(err);
			}	

			ArrayList<String> timeIntegre=new ArrayList<String>();
			integreWSData(resultWS.getContent(),timeIntegre);

		}
	}

	/**
	 * @author Marc VOUAUX
	 * integre les données WS
	 * @param data
	 * @return
	 * true
	 */
	boolean integreWSData(String data,ArrayList<String> timeelapse)
	{

		Date interestingDate = new Date();

		StringBuilder err= new StringBuilder();
		String [] values=MyWS.get_EXECEXINSERT_Values(data);

		m_appState.m_db.execSQL("BEGIN",err);
		String stInsert=values[0];
		for (int i=1;i<values.length;i++)
		{
			String query=values[i];
			m_appState.m_db.execSQL(stInsert+query, err);
			//			Debug.Log("requete", stInsert+query);
		}


		m_appState.m_db.execSQL("COMMIT",err);	

		long time=(new Date()).getTime() - interestingDate.getTime();
		timeelapse.add(String.valueOf(time));
		//	FurtiveMessageBox("Donn�es int�gr�es");

		Debug.Log("TAG5","Donnees integrees : "+err.toString());

			//	MyDB.copyFile(MyDB.source,MyDB.dest);

		if(handler != null)
			handler.sendEmptyMessage(MESSAGE_PROCESS_NEXT_SCENARIO);
		return true;
	}

	/**
	 * recup les prospects pour les envoyer au serveur
	 * @return
	 */
	String getProspectForWs(String user,String pwd)
	{
		try
		{
			dbProspect dbSiteClient=new dbProspect();
			//la requete d'insertion du KD
			//si on est sur un identifiant de test (qui contient test) on change la tabgle cible

			{
				boolean bFind=false;
				//la requete d'insertion du KD
				String queryInsert=dbSiteClient.KD_INSERT_STRING;
				//on cherche les entetes de commandes 
				String queryHdr="SELECT * FROM "+
						Global.dbClient.TABLENAME+
						" where "+
						" ( "+
						Global.dbClient.FIELD_CREAT +
						"='"+ Global.dbClient.CLI_CREATION+ "' ) "	;					

				String hdr="";
				Cursor cur=this.m_appState.m_db.conn.rawQuery(queryHdr,null);
				while (cur.moveToNext())
				{
					String stLat=MyDB.controlFld(Global.dbClient.giveFld(cur, Global.dbClient.FIELD_LAT));
					String stLon=MyDB.controlFld(Global.dbClient.giveFld(cur, Global.dbClient.FIELD_LON));

					//transforme lat et lon en flottant pour le serveur
					if (!Fonctions.GetStringDanem( stLat ).contains("."))
					{
						float lat= ((float)Fonctions.convertToFloat(stLat))/1000000;
						float lon= ((float)Fonctions.convertToFloat(stLon))/1000000;

						stLat=String.valueOf(lat);
						stLon=String.valueOf(lon);
					}

					bFind=true;
					hdr+=getString(R.string.separateur_morpion)+"(";
					hdr+="'"+MyDB.controlFld(Global.dbClient.giveFld(cur, Global.dbClient.FIELD_SOC_CODE   ))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbClient.giveFld(cur, Global.dbClient.FIELD_CODE  ))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbClient.giveFld(cur, Global.dbClient.FIELD_CODE  ))+"',";
					hdr+="'"+MyDB.controlFld("")+"',";
					hdr+="'"+MyDB.controlFld("")+"',";
					hdr+="'"+MyDB.controlFld("")+"',";
					hdr+="'"+MyDB.controlFld(Global.dbClient.giveFld(cur, Global.dbClient.FIELD_STATUT ))+"',";
					hdr+="'"+MyDB.controlFld("")+"',";
					hdr+="'"+MyDB.controlFld("")+"',";
					hdr+="'"+MyDB.controlFld(Global.dbClient.giveFld(cur, Global.dbClient.FIELD_NOM ))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbClient.giveFld(cur, Global.dbClient.FIELD_ADR1 ))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbClient.giveFld(cur,Global.dbClient.FIELD_ADR2))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbClient.giveFld(cur,Global.dbClient.FIELD_CP ))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbClient.giveFld(cur, Global.dbClient.FIELD_VILLE ))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbClient.giveFld(cur, Global.dbClient.FIELD_PAYS ))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbClient.giveFld(cur, Global.dbClient.FIELD_CONTACT_NOM ))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbClient.giveFld(cur, Global.dbClient.FIELD_TEL1))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbClient.giveFld(cur, Global.dbClient.FIELD_FAX))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbClient.giveFld(cur, Global.dbClient.FIELD_SIRET))+"',";
					hdr+="'"+MyDB.controlFld("")+"',";
					hdr+="'"+Global.dbClient.getCodeIcon(MyDB.controlFld(Global.dbClient.giveFld(cur, Global.dbClient.FIELD_ICON)))+"',";
					hdr+="'"+MyDB.controlFld("")+"',";
					hdr+="'"+MyDB.controlFld("")+"',";
					hdr+="'"+MyDB.controlFld(Global.dbClient.giveFld(cur,Global.dbClient.FIELD_CATCOMPT))+"',";
					hdr+="'"+MyDB.controlFld("")+"',";
					hdr+="'"+MyDB.controlFld("")+"',";
					hdr+="'"+MyDB.controlFld("")+"',";
					hdr+="'"+MyDB.controlFld("")+"',";
					hdr+="'"+MyDB.controlFld("")+"',";
					hdr+="'"+MyDB.controlFld("")+"',";
					hdr+="'"+MyDB.controlFld("")+"',";
					hdr+="'"+MyDB.controlFld("")+"',";
					hdr+="'"+MyDB.controlFld(user)+"',";
					hdr+="'"+MyDB.controlFld("")+"',";
					hdr+="'"+MyDB.controlFld("")+"',";
					hdr+="'"+MyDB.controlFld(Global.dbClient.giveFld(cur, Global.dbClient.FIELD_EMAIL))+"',";
					hdr+="'"+MyDB.controlFld("")+"',";
					hdr+="'"+MyDB.controlFld(Global.dbClient.giveFld(cur, Global.dbClient.FIELD_COMMENT))+"',";
					hdr+="'"+MyDB.controlFld("")+"',";
					hdr+="'"+MyDB.controlFld("")+"',";
					hdr+="'"+MyDB.controlFld(Global.dbClient.giveFld(cur, Global.dbClient.FIELD_CODE))+"',";
					hdr+="'"+MyDB.controlFld("")+"',";
					hdr+="'"+MyDB.controlFld(Global.dbClient.giveFld(cur, Global.dbClient.FIELD_NUMTVA))+"',";		
					hdr+="'"+MyDB.controlFld("")+"',";								
					hdr+="'"+MyDB.controlFld(Global.dbClient.giveFld(cur, Global.dbClient.FIELD_ZONE))+"',";					
					hdr+="'"+MyDB.controlFld(Global.dbClient.giveFld(cur, Global.dbClient.FIELD_JOURPASSAGE))+"',";					
					hdr+="'"+MyDB.controlFld("")+"',";					
					hdr+="'"+MyDB.controlFld("")+"',";
					hdr+="'"+MyDB.controlFld(Global.dbClient.giveFld(cur, Global.dbClient.FIELD_IMPORTANCE))+"',";
					hdr+="'"+MyDB.controlFld(stLat)+"',";
					hdr+="'"+MyDB.controlFld(stLon)+"',";
					hdr+="'"+"N"+/*MyDB.controlFld(Global.dbClient.giveFld(cur, dbSiteClient.FIELD_CLI_TYPE))+*/"')";//on envoi N a la place du P sinon en r�cuperant le fichier client on va recevoir encore que c'est un prospect





				}
				if (bFind)
				{
					StringBuffer buferr=new StringBuffer();
					String resultWS=MyWS.WSSend(user,pwd,"UpdateSrvTableHdrNonSec","UPDATE",queryInsert+hdr );
					//si le resultat est ok on efface la cde
					if (resultWS.equals(""))
					{


					}
					else
					{
						return "err";
						//FurtiveMessageBox(buferr.toString());
					}
				}
			}


			return "";
		}
		catch(Exception ex)
		{
			return "err";
		}


	}

	//construit la chaine a envoyer au WS pour modif
	public String getUpdateCliForWS(String user,String pwd)
	{
		String completeString="";
		try {
			String query="SELECT * FROM "+
					Global.dbClient.TABLENAME+
					" where "+
					" ( "+
					Global.dbClient.FIELD_CREAT +
					"='"+ Global.dbClient.CLI_MODIFICATION+ "' ) "	;					

			Cursor cur=this.m_appState.m_db.conn.rawQuery(query,null);
			if (cur!=null)
			{
				while(cur.moveToNext())
				{
					String code=cur.getString(cur.getColumnIndex(Global.dbClient.FIELD_CODE));
					TableClient.structClient cli=new TableClient.structClient();
					Global.dbClient.getClient(code, cli, new StringBuilder());

					//transforme lat et lon en flottant pour le serveur
					if (!Fonctions.GetStringDanem( cli.LAT ).contains("."))
					{
						float lat= ((float)Fonctions.convertToFloat(cli.LAT))/1000000;
						float lon= ((float)Fonctions.convertToFloat(cli.LON))/1000000;



						cli.LAT=String.valueOf(lat);
						cli.LON=String.valueOf(lon);
					}

					String update=
							"UPDATE "+
									dbProspect.TABLENAMEPROSPECT+
									" set "+
									dbProspect.FIELD_CLI_RAISOC+"='"+ 		MyDB.controlFld(cli.NOM.trim())  +"',"+
									dbProspect.FIELD_CLI_ENSEIGNE+"='"+ 	MyDB.controlFld(cli.ENSEIGNE.trim())    +"',"+
									dbProspect.FIELD_CLI_ADR1+"='"+ 		MyDB.controlFld(cli.ADR1.trim())  +"',"+
									dbProspect.FIELD_CLI_ADR2+"='"+	 		MyDB.controlFld(cli.ADR2.trim())  +"',"+
									dbProspect.FIELD_CLI_CP+"='"+ 			MyDB.controlFld(cli.CP.trim())  +"',"+
									dbProspect.FIELD_CLI_VILLE+"='"+ 		MyDB.controlFld(cli.VILLE.trim())  +"',"+
									dbProspect.FIELD_CLI_TEL1+"='"+ 		MyDB.controlFld(cli.TEL1.trim())  +"',"+
									dbProspect.FIELD_CLI_FAX+"='"+ 			MyDB.controlFld(cli.FAX.trim())  +"',"+
									dbProspect.FIELD_CLI_GSM+"='"+ 			MyDB.controlFld(cli.GSM.trim())  +"',"+
									dbProspect.FIELD_CLI_EMAIL+"='"+  		MyDB.controlFld(cli.EMAIL.trim()) +"',"+
									dbProspect.FIELD_CLI_CODETYPE+"='"+  	MyDB.controlFld(cli.TYPE.trim()) +"',"+
									dbProspect.FIELD_CLI_SIRET+"='"+ 		MyDB.controlFld(cli.SIRET.trim())  +"',"+
									dbProspect.FIELD_CLI_TVA_INTRA+"='"+ 	MyDB.controlFld(cli.NUMTVA.trim())  +"',"+
									dbProspect.FIELD_CLI_GRPCLI+"='"+  		MyDB.controlFld(cli.GROUPECLIENT.trim()) +"',"+
									dbProspect.FIELD_CLI_JR_FERME+"='"+ 	MyDB.controlFld(cli.JOURFERMETURE.trim())  +"',"+
									dbProspect.FIELD_CLI_CODEAGENT+"='"+ 		user  +"',"+
									dbProspect.FIELD_CLI_COULEURSAV+"='"+ 	MyDB.controlFld(cli.COULEUR.trim())  +"',"+
									dbProspect.FIELD_CLI_LAT+"='"+ 			MyDB.controlFld(cli.LAT.trim())  +"',"+
									dbProspect.FIELD_CLI_LON+"='"+ 			MyDB.controlFld(cli.LON.trim())  +"',"+
									dbProspect.FIELD_CLI_FLAG+"='"+			Global.dbClient.FLAG_UPDATE+"'"+
									" where "+
									dbProspect.FIELD_CLI_CODECLIENT+"='"+cli.CODE+"'";

					completeString+=update+getString(R.string.separateur_morpion);

				}


				StringBuffer buferr=new StringBuffer();
				String resultWS=MyWS.WSSend(user,pwd,"UpdateSrvTableNonSec","UPDATE",completeString );
				//si le resultat est ok on efface la cde
				if (resultWS.equals(""))
				{


				}
				else
				{
					return "err";
					//FurtiveMessageBox(buferr.toString());
				}

			}
			return "";
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "err";

	}
	public String getContactcliForWs(String user,String pwd)
	{
		try
		{
			
			//la requete d'insertion du KD
			//si on est sur un identifiant de test (qui contient test) on change la tabgle cible

			{
				boolean bFind=false;
				//la requete d'insertion du KD
				String queryInsert=Global.dbContactcli.KD_INSERT_STRING;
				//on cherche les entetes de commandes 
				String queryHdr="SELECT * FROM "+
						Global.dbContactcli.TABLENAME+
						" where "+
						" ( "+
						Global.dbContactcli.FIELD_FLAG +
						"='"+ Global.dbContactcli.CONTACT_CREATION+ "' ) "	;					

				String hdr="";
				Cursor cur=this.m_appState.m_db.conn.rawQuery(queryHdr,null);
				while (cur.moveToNext())
				{
					
					
					bFind=true;
					hdr+=getString(R.string.separateur_morpion)+"(";
					hdr+="'"+MyDB.controlFld(Global.dbContactcli.giveFld(cur, Global.dbContactcli.FIELD_CODECONTACT   ))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbContactcli.giveFld(cur, Global.dbContactcli.FIELD_CODECLIENT  ))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbContactcli.giveFld(cur, Global.dbContactcli.FIELD_NOM  ))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbContactcli.giveFld(cur, Global.dbContactcli.FIELD_PRENOM ))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbContactcli.giveFld(cur, Global.dbContactcli.FIELD_MOBILE ))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbContactcli.giveFld(cur, Global.dbContactcli.FIELD_EMAIL ))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbContactcli.giveFld(cur,Global.dbContactcli.FIELD_CODEFONCTION))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbContactcli.giveFld(cur,Global.dbContactcli.FIELD_COMMENTAIRE ))+"',";
					hdr+="'C')";
				
				}
				if (bFind)
				{
					StringBuffer buferr=new StringBuffer();
					String resultWS=MyWS.WSSend(user,pwd,"UpdateSrvTableHdrNonSec","UPDATE",queryInsert+hdr );
					//si le resultat est ok on efface la cde
					if (resultWS.equals(""))
					{


					}
					else
					{
						return "err";
						//FurtiveMessageBox(buferr.toString());
					}
				}
			}


			return "";
		}
		catch(Exception ex)
		{
			return "err";
		}


	}
	//construit la chaine a envoyer au WS pour modif
		public String getUpdateContactcliForWS(String user,String pwd)
		{
			String completeString="";
			try {
				String query="SELECT * FROM "+
						Global.dbContactcli.TABLENAME+
						" where "+
						" ( "+
						Global.dbContactcli.FIELD_FLAG +
						"='"+ Global.dbContactcli.CONTACT_MODIFICATION+ "' ) "	;					

				Cursor cur=this.m_appState.m_db.conn.rawQuery(query,null);
				if (cur!=null)
				{
					while(cur.moveToNext())
					{
						String code=cur.getString(cur.getColumnIndex(Global.dbContactcli.FIELD_CODECONTACT));
						String codecli=cur.getString(cur.getColumnIndex(Global.dbContactcli.FIELD_CODECLIENT));
						TableContactcli.structContactcli cli=new TableContactcli.structContactcli();
						Global.dbContactcli.getContact(codecli,code, cli, new StringBuilder());
						String update=
								"UPDATE "+
										Global.dbContactcli.TABLENAME_SRV+
										" set "+
										
										Global.dbContactcli.FIELD_SRV_NOM+"='"+ 			MyDB.controlFld(cli.NOM.trim())  +"',"+
										Global.dbContactcli.FIELD_SRV_PRENOM+"='"+	 		MyDB.controlFld(cli.PRENOM.trim())  +"',"+
										Global.dbContactcli.FIELD_SRV_MOBILE+"='"+ 			MyDB.controlFld(cli.MOBILE.trim())  +"',"+
										Global.dbContactcli.FIELD_SRV_EMAIL+"='"+ 			MyDB.controlFld(cli.EMAIL.trim())  +"',"+
										Global.dbContactcli.FIELD_SRV_COMMENTAIRE+"='"+ 	MyDB.controlFld(cli.COMMENTAIRE.trim())  +"',"+
										Global.dbContactcli.FIELD_SRV_CODEFONCTION+"='"+ 	MyDB.controlFld(cli.CODEFONCTION.trim())  +"',"+
										Global.dbContactcli.FIELD_SRV_FLAG+"='M'"+
										
																				" where "+
										Global.dbContactcli.FIELD_SRV_CODECLIENT+"='"+cli.CODECLIENT+"' and "+Global.dbContactcli.FIELD_SRV_CODECONTACT+"='"+cli.CODECONTACT+"' ";

						completeString+=update+getString(R.string.separateur_morpion);

					}


					StringBuffer buferr=new StringBuffer();
					String resultWS=MyWS.WSSend(user,pwd,"UpdateSrvTableNonSec","UPDATE",completeString );
					//si le resultat est ok on efface la cde
					if (resultWS.equals(""))
					{


					}
					else
					{
						return "err";
						//FurtiveMessageBox(buferr.toString());
					}

				}
				return "";
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return "err";

		}
		
		//construit la chaine a envoyer au WS pour modif
		public String getDeleteContactcliForWS(String user,String pwd)
		{
			String completeString="";
			try {
				String query="SELECT * FROM "+
						Global.dbContactcli.TABLENAME
						+ " where " + " ( " + Global.dbContactcli.FIELD_FLAG + "='"
					+ Global.dbContactcli.CONTACT_SUPPRESSION + "'   )";

				Cursor cur=this.m_appState.m_db.conn.rawQuery(query,null);
				if (cur!=null)
				{
					while(cur.moveToNext())
					{
						String code=cur.getString(cur.getColumnIndex(Global.dbContactcli.FIELD_CODECONTACT));
						String codecli=cur.getString(cur.getColumnIndex(Global.dbContactcli.FIELD_CODECLIENT));
						TableContactcli.structContactcli cli=new TableContactcli.structContactcli();
						Global.dbContactcli.getContact(codecli,code, cli, new StringBuilder());
						String update=
								"UPDATE "+
										Global.dbContactcli.TABLENAME_SRV+
										" set "+
										Global.dbContactcli.FIELD_SRV_FLAG+"='"+ 	MyDB.controlFld(cli.FLAG.trim())  +"'"+
									
																				" where "+
										Global.dbContactcli.FIELD_SRV_CODECLIENT+"='"+cli.CODECLIENT+"' and "+Global.dbContactcli.FIELD_SRV_CODECONTACT+"='"+cli.CODECONTACT+"' ";

						completeString+=update+getString(R.string.separateur_morpion);

					}


					StringBuffer buferr=new StringBuffer();
					String resultWS=MyWS.WSSend(user,pwd,"UpdateSrvTableNonSec","UPDATE",completeString);
					//si le resultat est ok on efface la cde
					if (resultWS.equals(""))
					{


					}
					else
					{
						return "err";
						//FurtiveMessageBox(buferr.toString());
					}

				}
				return "";
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return "err";

		}
				
		
	String getKemsLogWs(String user,String pwd)
	{
		try
		{

			//la requete d'insertion du KD
			//si on est sur un identifiant de test (qui contient test) on change la tabgle cible

			{
				boolean bFind=false;
				//la requete d'insertion du KD
				String queryInsert=dbLog.INSERT_STRING;
				//on cherche les entetes de commandes 
				String queryHdr="SELECT * FROM "+
						Global.dbLog.TABLENAME;					

				String hdr="";
				Cursor cur=this.m_appState.m_db.conn.rawQuery(queryHdr,null);
				while (cur.moveToNext())
				{

					bFind=true;
					hdr+=getString(R.string.separateur_morpion)+"(";
					hdr+="'"+MyDB.controlFld(Global.dbLog.giveFld(cur, Global.dbLog.FLD_LOG_DATE  		 ))+"',";
					hdr+="'"+Global.curver+"',";
					hdr+="'"+MyDB.controlFld(Global.dbLog.giveFld(cur, Global.dbLog.FLD_LOG_ECRAN  		))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbLog.giveFld(cur, Global.dbLog.FLD_LOG_FONCTION  	))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbLog.giveFld(cur, Global.dbLog.FLD_LOG_PARAMS  	 ))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbLog.giveFld(cur, Global.dbLog.FLD_LOG_MESSAGE  	))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbLog.giveFld(cur, Global.dbLog.FLD_LOG_EXCEPTION   ))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbLog.giveFld(cur, Global.dbLog.FLD_LOG_COMPLEMENT ))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbLog.giveFld(cur, Global.dbLog.FLD_LOG_USERID		))+"')";



				}
				if (bFind)
				{
					StringBuffer buferr=new StringBuffer();
					String resultWS=MyWS.WSSend(user,pwd,"UpdateSrvTableHdrNonSec","UPDATE",queryInsert+hdr );
					//si le resultat est ok on efface la cde
					if (resultWS.equals(""))
					{
						Global.dbLog.Clear(new StringBuilder());

					}
					else
					{
						return "err";
						//FurtiveMessageBox(buferr.toString());
					}
				}
			}


			return "";
		}
		catch(Exception ex)
		{
			return "err";
		}


	}
	String sendMediasFiles(String user,String pwd)
	{
		try
		{
			ExternalStorage externalStorage = new ExternalStorage(this,false);

			File signatures = new File (externalStorage.getFolderPath(externalStorage.MEDIA_FOLDER)) ;
			File[]files_sig = signatures.listFiles();

			if(files_sig==null)
				return "";
			for(File f: files_sig)
			{
				if(f.isDirectory())
					continue ;

				//si fichier trop gros on l'efface
				long size=f.length();
				if (size<=Global.maxMediaFileSize)
				{
					String NameImage;

					//MV BUG pourquoi RIGHT ?
					String filename=f.getName();
					//	NameImage=Fonctions.GetStringDanem(Fonctions.Right(f.getName(), externalStorage.getMediasFolder().length()));
					//	NameImage=Fonctions.GetStringDanem(Fonctions.Right(, externalStorage.getMediasFolder().length()));


					//si envoi ok on efface le fichier 
					String resultWS=MyWS.WSSendFile(user,pwd,"SendFile","SEND_MEDIASFILES",f.getName(), MyParser.convertFileToByteArray(f) );
					if ( resultWS.equals("true") )
					{

						f.delete() ;
					}
				}
				else
				{
					try {

						f.delete();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

			return "";
		}
		catch(Exception ex)
		{

		}

		return "";
	}
}
