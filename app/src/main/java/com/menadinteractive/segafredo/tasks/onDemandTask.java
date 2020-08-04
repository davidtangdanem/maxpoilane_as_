package com.menadinteractive.segafredo.tasks;

import java.util.ArrayList;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;

import com.menadinteractive.maxpoilane.Debug;
import com.menadinteractive.segafredo.adapter.Log;
import com.menadinteractive.segafredo.communs.Fonctions;
import com.menadinteractive.segafredo.communs.Global;
import com.menadinteractive.segafredo.communs.MyWS;
import com.menadinteractive.segafredo.db.Preferences;
import com.menadinteractive.segafredo.plugins.Espresso;
import com.menadinteractive.segafredo.tournee.TourneeActivity;

public class onDemandTask extends AsyncTask<Void, Void, Integer>{
	Context context;
	Handler handler;
	ArrayList<Log> tourneeData;
	String login;
	String password;
	String socCode;
	String date = "";
	String zone = "";

	public onDemandTask(Context context, Handler handler){
		super();
		this.context = context;
		this.handler = handler;
		login = Preferences.getValue(context, Espresso.LOGIN, "0");
		password = Preferences.getValue(context, Espresso.PASSWORD, "0");
		//		Global.AXE_Ident.SOCIETE = Preferences.getValue(context, ExpressoPerfect.CODE_SOCIETE, "");
		socCode = Preferences.getValue(context, Espresso.CODE_SOCIETE, "");
		date = Fonctions.getYYYYMMDD(System.currentTimeMillis());
		
		
	}

	public onDemandTask(Context context, Handler handler, ArrayList<Log> tourneeData){
		this(context,handler);
		this.tourneeData = tourneeData;
	}

	public onDemandTask(Context context, Handler handler, ArrayList<Log> tourneeData, String date, String zone){
		this(context,handler, tourneeData);
		this.date = date;
		this.zone = zone;
	}

	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
	}

	@Override
	protected Integer doInBackground(Void... params) {
		//		WS(login, password, "Exec", "GETTOURNEE", " and datevis='20121115' and soc_code='"+socCode+"' ");
		//		WS(login, password, "EXPRESSO_getTournee", "EXPRESSO", " and datevis='20121114' and soc_code='4000' ");
		return WS(login, password, "EXPRESSO_getTournee", date, socCode, zone);
	}

	@Override
	protected void onPostExecute(Integer result) {
		if(handler != null){
			handler.sendEmptyMessage(TourneeActivity.MESSAGE_CLEAR_TASK_TOURNEE);
			handler.sendEmptyMessage(TourneeActivity.MESSAGE_UPDATE_UI);
			
			if(result == 0){
				handler.sendEmptyMessage(TourneeActivity.MESSAGE_NO_RESULT);
			}
		}
	}


	private int WS(String user, String pwd, String wsFonction, String datevis, String soc_code, String zone){
		Debug.Log("TAG6","WS");
		Debug.Log("TAG6","user:"+user+" - password : "+password+" - fonction : "+wsFonction+" - datevis : "+datevis+" - soc_code : "+soc_code);
		int result = 0;

		StringBuilder err=new StringBuilder();

		MyWS.Result resultWS=new MyWS.Result();
		//		resultWS=MyWS.WSQueryEx(user,pwd,wsFonction,scenario,filtre,"","","","",false);

		//		String datevis = "20121114";
		//		String soc_code = "";
		resultWS = MyWS.WSQueryExpresso(user, pwd, wsFonction, datevis, soc_code, zone, false);

		Debug.Log("TAG6",resultWS.toString());
		Debug.Log("TAG6","success ?"+resultWS.isConnSuccess()+"");
		Debug.Log("TAG6",resultWS.getContent());
		if (resultWS.isConnSuccess()==false)
		{

		}
		else{
			Debug.Log("Integrate : "+resultWS.getContent());
			integreWSData(resultWS.getContent());
			result = 1;
		}
		return result;
	}

	private boolean integreWSData(String data){
		boolean result = false;
		String[] rawData = MyWS.get_EXEC_Values2(data);
		try{
			for(int i =0; i< rawData.length; i++){
				structTournee tournee = new structTournee(rawData[i]);
				Log log = new Log();
				log.setTournee(tournee);
				
				log.setClient(Global.dbClient.load(tournee.clicode));
				log.getClient().COULEUR = tournee.color;

				log.setHoraire(Global.dbHoraire.load(tournee.clicode));
				log.setFermetures(Global.dbFermeture.load(tournee.clicode));
				log.setOpened(false);
				log.setFermeture(false);
				//				boolean isDjaVisite = Global.dbKDVisite.isDejaVisite(client.CODE, jourPassage);
				//				Debug.Log("TAG3", "is Dejà visite : "+isDjaVisite);
				//				data.setDejaVisite(false);

				tourneeData.add(log);
				Debug.Log("TAG6", tournee.toString());
			}
		}
		catch(Exception e){
			Debug.StackTrace(e);
		}
		return result;
	}





	/** Struct Tournée envoyé depuis le webservice */
	public class structTournee{
		public String clicode;
		public String consodepuisdervis; 
		public String stock_lastvis;
		public String stock_mini;
		public String datelastvis;
		public String consoparsemaine;
		public String deltaAvecStockMini;
		public String color;
		public String mntht;
		public String cde_code;
		
		final String SEP_FLD = ";";

		public structTournee(){
			this.clicode = "";
			this.consodepuisdervis="";
			this.stock_lastvis="";
			this.stock_mini="";
			this.datelastvis="";
			this.consoparsemaine="";
			this.deltaAvecStockMini="";
			this.color="";
			mntht="";
			cde_code="";
		}

		public structTournee(String trame){
			this();
			this.clicode = Fonctions.GiveFld(trame, 0, SEP_FLD, true);
			this.consodepuisdervis = Fonctions.GiveFld(trame, 1, SEP_FLD, false);
			this.stock_lastvis = Fonctions.GiveFld(trame, 2, SEP_FLD, false);
			this.stock_mini = Fonctions.GiveFld(trame, 3, SEP_FLD, false);
			this.datelastvis = Fonctions.GiveFld(trame, 4, SEP_FLD, false);
			this.consoparsemaine = Fonctions.GiveFld(trame, 5, SEP_FLD, false);
			this.deltaAvecStockMini = Fonctions.GiveFld(trame, 6, SEP_FLD, false);
			this.color = Fonctions.GiveFld(trame, 7, SEP_FLD, false);
			this.mntht = Fonctions.GiveFld(trame, 8, SEP_FLD, false);
			cde_code= Fonctions.GiveFld(trame, 9, SEP_FLD, false);
		}

		@Override
		public String toString() {
			return "structTournee [clicode=" + clicode + ", consodepuisdervis="
					+ consodepuisdervis + ", stock_lastvis=" + stock_lastvis
					+ ", stock_mini=" + stock_mini + ", datelastvis="
					+ datelastvis + ", consoparsemaine=" + consoparsemaine
					+ ", deltaAvecStockMini=" + deltaAvecStockMini + ", color="
					+ color + ", mntht="+mntht+ "+, cde_code="+cde_code+ "]";
		}


	}


}
