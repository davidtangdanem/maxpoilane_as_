package com.menadinteractive.segafredo.tournee;

import java.util.Calendar;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.menadinteractive.segafredo.communs.MyWS;
import com.menadinteractive.segafredo.db.Preferences;
import com.menadinteractive.segafredo.plugins.Espresso;

public class task_updateCde extends AsyncTask<String, Integer, Integer> {

	String scenar="UPDATE";
		Activity parent;
		String numcde;
	
		
		static Calendar lastSync=null;
		public task_updateCde(Activity act,String ncde) {
			super();
	
			numcde=ncde;
			parent=act;
			

		}
		
		@Override
		protected Integer doInBackground(String... params) {
			try {
				String user=Preferences.getValue(parent, Espresso.LOGIN, "0");
				String pwd=Preferences.getValue(parent, Espresso.PASSWORD, "0");
				
				String update="update kems_data set dat_data07='-1' where dat_type=83 and cde_code='"+numcde+"'";
				String resultWS=MyWS.WSSend(user,pwd,"UpdateSrvTableNonSec","UPDATE",update);
				//si le resultat est ok on efface la cde
				if (resultWS.equals(""))
	  			{


				}
				else
				{
					return 0;
				}

			} catch (Exception e) {
				return 0;
				// Toast.makeText(getApplicationContext(), "Update error!",
				// Toast.LENGTH_LONG).show();
			}

			return 1;
		}
		@Override
		protected void onPreExecute() {
			
		}
		@Override
		protected void onPostExecute(Integer resultValue) {
			Log.d("TAG", "onPostExecute");

			try {
				if (resultValue == 1) {
					
							
				}
				else
				{
					
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		
		
}
