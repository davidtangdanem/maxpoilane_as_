package com.menadinteractive.segafredo.tasks;

import java.util.ArrayList;
import java.util.Date;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import com.menadinteractive.segafredo.communs.Fonctions;
import com.menadinteractive.segafredo.communs.Global;
import com.menadinteractive.segafredo.communs.MyWS;
import com.menadinteractive.segafredo.db.MyDB;
import com.menadinteractive.segafredo.db.Preferences;
import com.menadinteractive.segafredo.db.dbKD545StockTheoSrv;
import com.menadinteractive.segafredo.plugins.Espresso;

public class task_getStockTheoWSData extends AsyncTask<Void, Void, Integer>{
	Context context;

	String login;
	String password;
	Handler m_handler;

	public task_getStockTheoWSData(Context context,Handler h){
		super();
		this.context = context;

		login = Preferences.getValue(context, Espresso.LOGIN, "0");
		password = Preferences.getValue(context, Espresso.PASSWORD, "0");

		m_handler=h;
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
		//return WS(login, password, "EXPRESSO_getTournee", date, socCode, zone);
	
		
		return 	getBorne(login,password,"GETSTOCKTHEO");
	}

	@Override
	protected void onPostExecute(Integer result) {
	 
		m_handler.sendEmptyMessage(result);
	}

	int getBorne(String login,String password,String scenario) {
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
			resultWS = MyWS.WSQueryZippedExBorne(login, password, scenario, "",
					incr, STEP);

			if (resultWS.isConnSuccess() == false) {
			 
				Log.d("TAG",
						scenario + " -> Error packet: " + String.valueOf(incr));
				
				 
				return 0;
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
		
		dbKD545StockTheoSrv mvt=new dbKD545StockTheoSrv(Global.dbParam.getDB());
		mvt.clear(false, new StringBuilder());
		boolean b=true;
		if (nbrLineTotal>0)
			b = integreWSDataArray(arrLines, STEP,  Global.dbParam.getDB());
		
		if (b) return 1;
		return 0;
	}
	public boolean integreWSDataArray(ArrayList<String[]> arr, int step,MyDB db
			 ) {

		if(arr==null || arr.get(0).length==0) 
			return true;
		 

		StringBuilder err = new StringBuilder();
		 db.execSQL("BEGIN", err);

		for (int line = 0; line < arr.size(); line++) {
			String[] extract = arr.get(line);

			String stInsert = extract[1];
			for (int i = 2; i < extract.length; i++) {
				String query = extract[i];
				 db.execSQL(stInsert + query, err);
			}
		}

		 db.execSQL("COMMIT", err);

	 	return true;
	}
}
