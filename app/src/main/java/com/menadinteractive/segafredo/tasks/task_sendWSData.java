package com.menadinteractive.segafredo.tasks;

import java.io.File;
import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import com.menadinteractive.maxpoilane.ExternalStorage;
import com.menadinteractive.maxpoilane.R;
import com.menadinteractive.segafredo.communs.Fonctions;
import com.menadinteractive.segafredo.communs.Global;
import com.menadinteractive.segafredo.communs.MyParser;
import com.menadinteractive.segafredo.communs.MyWS;
import com.menadinteractive.segafredo.db.MyDB;
import com.menadinteractive.segafredo.db.Preferences;
import com.menadinteractive.segafredo.db.TableClient;
import com.menadinteractive.segafredo.db.TableContactcli;
import com.menadinteractive.segafredo.db.dbKD;
import com.menadinteractive.segafredo.db.dbKD732InfoClientComplementaires;
import com.menadinteractive.segafredo.db.dbLog;
import com.menadinteractive.segafredo.db.dbProspect;
import com.menadinteractive.segafredo.plugins.Espresso;

public class task_sendWSData extends AsyncTask<Void, Void, Integer>{
	Context context;
	Handler hSync;
	String login;
	String password;
	final String scenar_sendsignature = "SEND_SIGNATURE";
	int nbrerr=0;
	ExternalStorage externalStorage;
	private static String STATDIR =  ExternalStorage.getFolderPath(ExternalStorage.SIGNATURE_FOLDER2);
	
	
	private static task_sendWSData INSTANCE = new task_sendWSData();
	
	private task_sendWSData()
	{
		
	}
	public static task_sendWSData getInstance()
	{	return INSTANCE;
	}
	
	
	public task_sendWSData(Context context,Handler h){
		super();
		this.context = context;
		
		hSync=h;
		login = Preferences.getValue(context, Espresso.LOGIN, "0");
		password = Preferences.getValue(context, Espresso.PASSWORD, "0");
	
		
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
 
		
		//getReapproDechargementForWS(login,password);
		
		//les synchro qui ont des pb si lancé en meme temps que le module
		//risque d'envoi pendant le création
		if (hSync!=null)
		{
			getRetourMachineClientForWS(login,password);
			getComptageMachineClientForWS(login,password);
		
			if (nbrerr>=2)
				return 1;
			
			getEncaissementForWS(login,password);
			getEncaissementFactureForWS(login,password);
			getRemiseBanqueForWS(login,password);
			
					
			 
			getUpdateCliForWS(login, password);
		 
			
			String errorcontact=getContactcliForWs(login,password);
			if (errorcontact.equals("") /*|| Global.dbClient.CountProspect()>0*/)
			{
				errorcontact = getUpdateContactcliForWS(login, password);
				if (errorcontact.equals("") /*|| Global.dbClient.CountProspect()>0*/)
				{
			//		getDeleteContactcliForWS(login, password);
				}
				
				
			}
			
			if(getDeleteClientvuForWS(login, password)==1)
		 	 getCliVuForWS(login,password);
			
			//18/12 on ne recupere pas pour le moment car non utilisé  getAgendaForWS(login,password);
			//getQuestForWS(login,password);
			 
			if (getSynchroDataForWs( dbKD732InfoClientComplementaires.KD_TYPE,dbKD732InfoClientComplementaires.FIELD_CODECLIENT,  
					" ("+	dbKD732InfoClientComplementaires.FIELD_CODECLIENT+"<>'')", login,password,"UPDATE" ))
			{
				dbKD732InfoClientComplementaires info=new dbKD732InfoClientComplementaires(Global.dbParam.getDB());
				info.clear(new StringBuilder());
			}
		}
		
		getKemsLogWs(login, password);
		CopyCde_Kems_savedata();
		ListeSaveCdes_Delete();
		sendSignatureFiles(login, password);
		return getCommandeForWS(login,password);
	}

	@Override
	protected void onPostExecute(Integer result) {
		if (hSync!=null)
		{
			Message msg=new Message();
			msg.what=455;
			hSync.sendMessage(msg);
		}
	}
	String sendSignatureFiles(String login,String password) {
		try {
			// externalStorage = new ExternalStorage(this, false);

			
				// Signatures
				File signatures = new File(
						STATDIR);
				File[] files_sig = signatures.listFiles();

				if (files_sig == null)
					return "";
				for (File f : files_sig) {
					if (f.isDirectory())
						continue;

					String resultWS = MyWS.WSSendFile(login, password, "SendFile",
							scenar_sendsignature, f.getName(),
							MyParser.convertFileToByteArray(f));
					if (resultWS.equals("true")) {
						f.delete();
					}
				}
			

			return "";
		} catch (Exception ex) {

		}

		return "";
	}

	 String getQuestForWS(String user,String pwd)
		{
			String stReturnOK="";
			try
			{
				String datekey=Fonctions.getYYYYMMDD();
				//la requete d'insertion du KD
				String queryInsert=dbKD.KD_INSERT_STRING;
				//si on est sur un identifiant de test (qui contient test) on change la tabgle cible
				String ident=Preferences.getValue( context, Espresso.LOGIN, "0");;
				ident=ident.toLowerCase();
				if (ident.contains("test"))
				{
					queryInsert=queryInsert.replace(dbKD.TABLENAME, dbKD.TABLENAME_TEST);
				}
					//on cherche les entetes de commandes 
					String queryHdr="SELECT * FROM "+
							dbKD.TABLENAME+
							" where "+
							" ( "+
							dbKD.fld_kd_dat_type+
							"="+
							Global.dbKDFillPlan.KD_TYPE+
							" and "+
							Global.dbKDFillPlan.FIELD_SEND+
							"='1'"+
							" and "+
							Global.dbKDFillPlan.FIELD_DATEKEY+
							"<>'"+ datekey+ "'"+
							" ) ";					
	
					String hdr="";
					Cursor cur=Global.dbParam.getDB().conn.rawQuery(queryHdr,null);
					while (cur.moveToNext())
					{
						hdr+=context.getString(R.string.separateur_morpion)+"(";
						hdr+="'"+MyDB.controlFld(Global.dbKDFillPlan.giveFld(cur, dbKD.fld_kd_soc_code  ))+"',";
						hdr+="'"+MyDB.controlFld(Global.dbKDFillPlan.giveFld(cur, dbKD.fld_kd_cli_code  ))+"',";
						hdr+="'"+MyDB.controlFld(Global.dbKDFillPlan.giveFld(cur, dbKD.fld_kd_pro_code  ))+"',";
						hdr+="'"+MyDB.controlFld(Global.dbKDFillPlan.giveFld(cur, dbKD.fld_kd_vis_code  ))+"',";
						hdr+="'"+MyDB.controlFld(Global.dbKDFillPlan.giveFld(cur, dbKD.fld_kd_cde_code  ))+"',";
						hdr+="'"+MyDB.controlFld(Global.dbKDFillPlan.giveFld(cur, dbKD.fld_kd_dat_type  ))+"',";
						hdr+="'"+MyDB.controlFld(Global.dbKDFillPlan.giveFld(cur, dbKD.fld_kd_dat_idx01 ))+"',";
						hdr+="'"+MyDB.controlFld(Global.dbKDFillPlan.giveFld(cur, dbKD.fld_kd_dat_idx02 ))+"',";
						hdr+="'"+MyDB.controlFld(Global.dbKDFillPlan.giveFld(cur, dbKD.fld_kd_dat_idx03 ))+"',";
						hdr+="'"+MyDB.controlFld(Global.dbKDFillPlan.giveFld(cur, dbKD.fld_kd_dat_idx04 ))+"',";
						hdr+="'"+MyDB.controlFld(Global.dbKDFillPlan.giveFld(cur, dbKD.fld_kd_dat_idx05 ))+"',";
						hdr+="'"+MyDB.controlFld(Global.dbKDFillPlan.giveFld(cur, dbKD.fld_kd_dat_idx06 ))+"',";
						hdr+="'"+MyDB.controlFld(Global.dbKDFillPlan.giveFld(cur, dbKD.fld_kd_dat_idx07 ))+"',";
						hdr+="'"+MyDB.controlFld(Global.dbKDFillPlan.giveFld(cur, dbKD.fld_kd_dat_idx08 ))+"',";
						hdr+="'"+MyDB.controlFld(Global.dbKDFillPlan.giveFld(cur, dbKD.fld_kd_dat_idx09 ))+"',";
						hdr+="'"+MyDB.controlFld(Global.dbKDFillPlan.giveFld(cur, dbKD.fld_kd_dat_idx10 ))+"',";
						hdr+="'"+MyDB.controlFld(Global.dbKDFillPlan.giveFld(cur, dbKD.fld_kd_dat_data01))+"',";
						hdr+="'"+MyDB.controlFld(Global.dbKDFillPlan.giveFld(cur, dbKD.fld_kd_dat_data02))+"',";
						hdr+="'"+MyDB.controlFld(Global.dbKDFillPlan.giveFld(cur, dbKD.fld_kd_dat_data03))+"',";
						hdr+="'"+MyDB.controlFld(Global.dbKDFillPlan.giveFld(cur, dbKD.fld_kd_dat_data04))+"',";
						hdr+="'"+MyDB.controlFld(Global.dbKDFillPlan.giveFld(cur, dbKD.fld_kd_dat_data05))+"',";
						hdr+="'"+MyDB.controlFld(Global.dbKDFillPlan.giveFld(cur, dbKD.fld_kd_dat_data06))+"',";
						hdr+="'"+MyDB.controlFld(Global.dbKDFillPlan.giveFld(cur, dbKD.fld_kd_dat_data07))+"',";
						hdr+="'"+MyDB.controlFld(Global.dbKDFillPlan.giveFld(cur, dbKD.fld_kd_dat_data08))+"',";
						hdr+="'"+MyDB.controlFld(Global.dbKDFillPlan.giveFld(cur, dbKD.fld_kd_dat_data09))+"',";
						hdr+="'"+MyDB.controlFld(Global.dbKDFillPlan.giveFld(cur, dbKD.fld_kd_dat_data10))+"',";
						hdr+="'"+MyDB.controlFld(Global.dbKDFillPlan.giveFld(cur, dbKD.fld_kd_dat_data11))+"',";
						hdr+="'"+MyDB.controlFld(Global.dbKDFillPlan.giveFld(cur, dbKD.fld_kd_dat_data12))+"',";
						hdr+="'"+MyDB.controlFld(Global.dbKDFillPlan.giveFld(cur, dbKD.fld_kd_dat_data13))+"',";
						hdr+="'"+MyDB.controlFld(Global.dbKDFillPlan.giveFld(cur, dbKD.fld_kd_dat_data14))+"',";
						hdr+="'"+MyDB.controlFld(Global.dbKDFillPlan.giveFld(cur, dbKD.fld_kd_dat_data15))+"',";
						hdr+="'"+MyDB.controlFld(Global.dbKDFillPlan.giveFld(cur, dbKD.fld_kd_dat_data16))+"',";
						hdr+="'"+MyDB.controlFld(Global.dbKDFillPlan.giveFld(cur, dbKD.fld_kd_dat_data17))+"',";
						hdr+="'"+MyDB.controlFld(Global.dbKDFillPlan.giveFld(cur, dbKD.fld_kd_dat_data18))+"',";
						hdr+="'"+MyDB.controlFld(Global.dbKDFillPlan.giveFld(cur, dbKD.fld_kd_dat_data19))+"',";
						hdr+="'"+MyDB.controlFld(Global.dbKDFillPlan.giveFld(cur, dbKD.fld_kd_dat_data20))+"',";
						hdr+="'"+MyDB.controlFld(Global.dbKDFillPlan.giveFld(cur, dbKD.fld_kd_dat_data21))+"',";
						hdr+="'"+MyDB.controlFld(Global.dbKDFillPlan.giveFld(cur, dbKD.fld_kd_dat_data22))+"',";
						hdr+="'"+MyDB.controlFld(Global.dbKDFillPlan.giveFld(cur, dbKD.fld_kd_dat_data23))+"',";
						hdr+="'"+MyDB.controlFld(Global.dbKDFillPlan.giveFld(cur, dbKD.fld_kd_dat_data24))+"',";
						hdr+="'"+MyDB.controlFld(Global.dbKDFillPlan.giveFld(cur, dbKD.fld_kd_dat_data25))+"',";
						hdr+="'"+MyDB.controlFld(Global.dbKDFillPlan.giveFld(cur, dbKD.fld_kd_dat_data26))+"',";
						hdr+="'"+MyDB.controlFld(Global.dbKDFillPlan.giveFld(cur, dbKD.fld_kd_dat_data27))+"',";
						hdr+="'"+MyDB.controlFld(Global.dbKDFillPlan.giveFld(cur, dbKD.fld_kd_dat_data28))+"',";
						hdr+="'"+MyDB.controlFld(Global.dbKDFillPlan.giveFld(cur, dbKD.fld_kd_dat_data29))+"',";
						hdr+="'"+MyDB.controlFld(Global.dbKDFillPlan.giveFld(cur, dbKD.fld_kd_dat_data30))+"',";
						hdr+="'"+MyDB.controlFld(Global.dbKDFillPlan.giveFld(cur, dbKD.fld_kd_val_data31))+"',";
						hdr+="'"+MyDB.controlFld(Global.dbKDFillPlan.giveFld(cur, dbKD.fld_kd_val_data32))+"',";
						hdr+="'"+MyDB.controlFld(Global.dbKDFillPlan.giveFld(cur, dbKD.fld_kd_val_data33))+"',";
						hdr+="'"+MyDB.controlFld(Global.dbKDFillPlan.giveFld(cur, dbKD.fld_kd_val_data34))+"',";
						hdr+="'"+MyDB.controlFld(Global.dbKDFillPlan.giveFld(cur, dbKD.fld_kd_val_data35))+"',";
						hdr+="'"+MyDB.controlFld(Global.dbKDFillPlan.giveFld(cur, dbKD.fld_kd_val_data36))+"',";
						hdr+="'"+MyDB.controlFld(Global.dbKDFillPlan.giveFld(cur, dbKD.fld_kd_val_data37))+"',";
						hdr+="'"+MyDB.controlFld(Global.dbKDFillPlan.giveFld(cur, dbKD.fld_kd_val_data38))+"',";
						hdr+="'"+MyDB.controlFld(Global.dbKDFillPlan.giveFld(cur, dbKD.fld_kd_val_data39))+"')";
	
					}
					StringBuffer buferr=new StringBuffer();
					StringBuilder err=new StringBuilder();
	
					String resultWS=MyWS.WSSend(user,pwd,"UpdateSrvTableHdrNonSec","UPDATE",queryInsert+hdr);
					//si le resultat est ok on efface la cde
					if (resultWS.equals(""))
					{
						stReturnOK ="VALIDEOK";
						Global.dbKDFillPlan.deleteSent()	;
					}
					else
					{
						nbrerr++;
			//			FurtiveMessageBox(buferr.toString());
					}
	
				
	
	
	
			}
			catch(Exception ex)
			{
				return "";
			}
	
			return stReturnOK;
		}


	int getCommandeForWS(String user,String pwd)
	{
		try
		{
			//la requete d'insertion du KD
			String queryInsert=dbKD.KD_INSERT_STRING;
			//si on est sur un identifiant de test (qui contient test) on change la tabgle cible
			String ident=user;
			ident=ident.toLowerCase();
			if (ident.contains("test"))
			{
				queryInsert=queryInsert.replace(dbKD.TABLENAME, dbKD.TABLENAME_TEST);
			}


			ArrayList<String> tabCde=new ArrayList<String>();
			int nbrcde=ListeCdesAEnvoyer(tabCde);

			//on va faire une trame par cde, pour valider l'envoi cde par cde
			for (int i=0;i<tabCde.size();i++)
			{


				//on cherche les entetes de commandes 
				String queryHdr="SELECT * FROM "+
						dbKD.TABLENAME+
						" where "+
						" ( "+
						dbKD.fld_kd_dat_type+
						"="+
						Global.dbKDEntCde.KD_TYPE+
						" or "+
						dbKD.fld_kd_dat_type+
						"="+
						Global.dbKDLinCde.KD_TYPE+
						
						" ) "+ 
						" and "+
						Global.dbKDEntCde.FIELD_ENTCDE_CODECDE+
						"="+
						"'"+tabCde.get(i)+"' ORDER BY "+dbKD.fld_kd_dat_type+" DESC";					

				String hdr="";
				Cursor cur=Global.dbParam.getDB().conn.rawQuery(queryHdr,null);
				while (cur.moveToNext())
				{
					hdr+=context.getString(R.string.separateur_morpion)+"(";
					hdr+="'"+MyDB.controlFld(Global.dbKDEntCde.giveFld(cur, dbKD.fld_kd_soc_code  ))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEntCde.giveFld(cur, dbKD.fld_kd_cli_code  ))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEntCde.giveFld(cur, dbKD.fld_kd_pro_code  ))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEntCde.giveFld(cur, dbKD.fld_kd_vis_code  ))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEntCde.giveFld(cur, dbKD.fld_kd_cde_code  ))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEntCde.giveFld(cur, dbKD.fld_kd_dat_type  ))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEntCde.giveFld(cur, dbKD.fld_kd_dat_idx01 ))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEntCde.giveFld(cur, dbKD.fld_kd_dat_idx02 ))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEntCde.giveFld(cur, dbKD.fld_kd_dat_idx03 ))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEntCde.giveFld(cur, dbKD.fld_kd_dat_idx04 ))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEntCde.giveFld(cur, dbKD.fld_kd_dat_idx05 ))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEntCde.giveFld(cur, dbKD.fld_kd_dat_idx06 ))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEntCde.giveFld(cur, dbKD.fld_kd_dat_idx07 ))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEntCde.giveFld(cur, dbKD.fld_kd_dat_idx08 ))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEntCde.giveFld(cur, dbKD.fld_kd_dat_idx09 ))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEntCde.giveFld(cur, dbKD.fld_kd_dat_idx10 ))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEntCde.giveFld(cur, dbKD.fld_kd_dat_data01))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEntCde.giveFld(cur, dbKD.fld_kd_dat_data02))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEntCde.giveFld(cur, dbKD.fld_kd_dat_data03))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEntCde.giveFld(cur, dbKD.fld_kd_dat_data04))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEntCde.giveFld(cur, dbKD.fld_kd_dat_data05))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEntCde.giveFld(cur, dbKD.fld_kd_dat_data06))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEntCde.giveFld(cur, dbKD.fld_kd_dat_data07))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEntCde.giveFld(cur, dbKD.fld_kd_dat_data08))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEntCde.giveFld(cur, dbKD.fld_kd_dat_data09))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEntCde.giveFld(cur, dbKD.fld_kd_dat_data10))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEntCde.giveFld(cur, dbKD.fld_kd_dat_data11))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEntCde.giveFld(cur, dbKD.fld_kd_dat_data12))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEntCde.giveFld(cur, dbKD.fld_kd_dat_data13))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEntCde.giveFld(cur, dbKD.fld_kd_dat_data14))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEntCde.giveFld(cur, dbKD.fld_kd_dat_data15))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEntCde.giveFld(cur, dbKD.fld_kd_dat_data16))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEntCde.giveFld(cur, dbKD.fld_kd_dat_data17))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEntCde.giveFld(cur, dbKD.fld_kd_dat_data18))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEntCde.giveFld(cur, dbKD.fld_kd_dat_data19))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEntCde.giveFld(cur, dbKD.fld_kd_dat_data20))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEntCde.giveFld(cur, dbKD.fld_kd_dat_data21))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEntCde.giveFld(cur, dbKD.fld_kd_dat_data22))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEntCde.giveFld(cur, dbKD.fld_kd_dat_data23))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEntCde.giveFld(cur, dbKD.fld_kd_dat_data24))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEntCde.giveFld(cur, dbKD.fld_kd_dat_data25))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEntCde.giveFld(cur, dbKD.fld_kd_dat_data26))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEntCde.giveFld(cur, dbKD.fld_kd_dat_data27))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEntCde.giveFld(cur, dbKD.fld_kd_dat_data28))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEntCde.giveFld(cur, dbKD.fld_kd_dat_data29))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEntCde.giveFld(cur, dbKD.fld_kd_dat_data30))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEntCde.giveFld(cur, dbKD.fld_kd_val_data31))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEntCde.giveFld(cur, dbKD.fld_kd_val_data32))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEntCde.giveFld(cur, dbKD.fld_kd_val_data33))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEntCde.giveFld(cur, dbKD.fld_kd_val_data34))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEntCde.giveFld(cur, dbKD.fld_kd_val_data35))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEntCde.giveFld(cur, dbKD.fld_kd_val_data36))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEntCde.giveFld(cur, dbKD.fld_kd_val_data37))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEntCde.giveFld(cur, dbKD.fld_kd_val_data38))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEntCde.giveFld(cur, dbKD.fld_kd_val_data39))+"')";



				}


				String resultWS=MyWS.WSSend(user,pwd,"UpdateSrvTableHdrNonSec","UPDATE",queryInsert+hdr);
				//si le resultat est ok on efface la cde
				if (resultWS.equals(""))
				{
					StringBuilder errV=new StringBuilder();
					StringBuffer  errB=new StringBuffer();
					Global.dbKDEntCde.deleteCde( tabCde.get(i),"",errB)	;
					Global.dbKDLinCde.deleteNumcde(tabCde.get(i), errB);
				}
				else
				{
					nbrerr++;
			//		Fonctions.FurtiveMessageBox(buferr.toString());
				}

			}



		}
		catch(Exception ex)
		{
			return 0;
		}

		return 1;
	}
	
	int getRetourMachineClientForWS(String user,String pwd)
	{
		try
		{
			//la requete d'insertion du KD
			String queryInsert=dbKD.KD_INSERT_STRING;
			//si on est sur un identifiant de test (qui contient test) on change la tabgle cible
			String ident=user;
			ident=ident.toLowerCase();
			if (ident.contains("test"))
			{
				queryInsert=queryInsert.replace(dbKD.TABLENAME, dbKD.TABLENAME_TEST);
			}

 
				String queryHdr="SELECT * FROM "+
						dbKD.TABLENAME+
						" where "+
						" ( "+
						dbKD.fld_kd_dat_type+
						"="+
						Global.dbKDRetourMachineClient.KD_TYPE+") ";
					/*	"  and "+
						Global.dbKDRetourMachineClient.FIELD_RMC_SENT+" is null";
*/
				String hdr="";
				Cursor cur=Global.dbParam.getDB().conn.rawQuery(queryHdr,null);
				while (cur.moveToNext())
				{
					hdr+=context.getString(R.string.separateur_morpion)+"(";
					hdr+="'"+MyDB.controlFld(Global.dbKDRetourMachineClient.giveFld(cur, dbKD.fld_kd_soc_code  ))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDRetourMachineClient.giveFld(cur, dbKD.fld_kd_cli_code  ))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDRetourMachineClient.giveFld(cur, dbKD.fld_kd_pro_code  ))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDRetourMachineClient.giveFld(cur, dbKD.fld_kd_vis_code  ))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDRetourMachineClient.giveFld(cur, dbKD.fld_kd_cde_code  ))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDRetourMachineClient.giveFld(cur, dbKD.fld_kd_dat_type  ))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDRetourMachineClient.giveFld(cur, dbKD.fld_kd_dat_idx01 ))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDRetourMachineClient.giveFld(cur, dbKD.fld_kd_dat_idx02 ))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDRetourMachineClient.giveFld(cur, dbKD.fld_kd_dat_idx03 ))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDRetourMachineClient.giveFld(cur, dbKD.fld_kd_dat_idx04 ))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDRetourMachineClient.giveFld(cur, dbKD.fld_kd_dat_idx05 ))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDRetourMachineClient.giveFld(cur, dbKD.fld_kd_dat_idx06 ))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDRetourMachineClient.giveFld(cur, dbKD.fld_kd_dat_idx07 ))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDRetourMachineClient.giveFld(cur, dbKD.fld_kd_dat_idx08 ))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDRetourMachineClient.giveFld(cur, dbKD.fld_kd_dat_idx09 ))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDRetourMachineClient.giveFld(cur, dbKD.fld_kd_dat_idx10 ))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDRetourMachineClient.giveFld(cur, dbKD.fld_kd_dat_data01))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDRetourMachineClient.giveFld(cur, dbKD.fld_kd_dat_data02))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDRetourMachineClient.giveFld(cur, dbKD.fld_kd_dat_data03))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDRetourMachineClient.giveFld(cur, dbKD.fld_kd_dat_data04))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDRetourMachineClient.giveFld(cur, dbKD.fld_kd_dat_data05))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDRetourMachineClient.giveFld(cur, dbKD.fld_kd_dat_data06))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDRetourMachineClient.giveFld(cur, dbKD.fld_kd_dat_data07))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDRetourMachineClient.giveFld(cur, dbKD.fld_kd_dat_data08))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDRetourMachineClient.giveFld(cur, dbKD.fld_kd_dat_data09))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDRetourMachineClient.giveFld(cur, dbKD.fld_kd_dat_data10))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDRetourMachineClient.giveFld(cur, dbKD.fld_kd_dat_data11))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDRetourMachineClient.giveFld(cur, dbKD.fld_kd_dat_data12))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDRetourMachineClient.giveFld(cur, dbKD.fld_kd_dat_data13))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDRetourMachineClient.giveFld(cur, dbKD.fld_kd_dat_data14))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDRetourMachineClient.giveFld(cur, dbKD.fld_kd_dat_data15))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDRetourMachineClient.giveFld(cur, dbKD.fld_kd_dat_data16))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDRetourMachineClient.giveFld(cur, dbKD.fld_kd_dat_data17))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDRetourMachineClient.giveFld(cur, dbKD.fld_kd_dat_data18))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDRetourMachineClient.giveFld(cur, dbKD.fld_kd_dat_data19))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDRetourMachineClient.giveFld(cur, dbKD.fld_kd_dat_data20))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDRetourMachineClient.giveFld(cur, dbKD.fld_kd_dat_data21))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDRetourMachineClient.giveFld(cur, dbKD.fld_kd_dat_data22))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDRetourMachineClient.giveFld(cur, dbKD.fld_kd_dat_data23))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDRetourMachineClient.giveFld(cur, dbKD.fld_kd_dat_data24))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDRetourMachineClient.giveFld(cur, dbKD.fld_kd_dat_data25))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDRetourMachineClient.giveFld(cur, dbKD.fld_kd_dat_data26))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDRetourMachineClient.giveFld(cur, dbKD.fld_kd_dat_data27))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDRetourMachineClient.giveFld(cur, dbKD.fld_kd_dat_data28))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDRetourMachineClient.giveFld(cur, dbKD.fld_kd_dat_data29))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDRetourMachineClient.giveFld(cur, dbKD.fld_kd_dat_data30))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDRetourMachineClient.giveFld(cur, dbKD.fld_kd_val_data31))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDRetourMachineClient.giveFld(cur, dbKD.fld_kd_val_data32))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDRetourMachineClient.giveFld(cur, dbKD.fld_kd_val_data33))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDRetourMachineClient.giveFld(cur, dbKD.fld_kd_val_data34))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDRetourMachineClient.giveFld(cur, dbKD.fld_kd_val_data35))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDRetourMachineClient.giveFld(cur, dbKD.fld_kd_val_data36))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDRetourMachineClient.giveFld(cur, dbKD.fld_kd_val_data37))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDRetourMachineClient.giveFld(cur, dbKD.fld_kd_val_data38))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDRetourMachineClient.giveFld(cur, dbKD.fld_kd_val_data39))+"')";



				}


				String resultWS=MyWS.WSSend(user,pwd,"UpdateSrvTableHdrNonSec","UPDATE",queryInsert+hdr);
				//si le resultat est ok on efface la cde
				if (resultWS.equals(""))
				{
					StringBuffer err= new StringBuffer();
					//Global.dbKDRetourMachineClient.setSentToServer( )	;
					Global.dbKDRetourMachineClient.deleteAll(new StringBuffer());
				}
				else
				{
					nbrerr++;
			//		Fonctions.FurtiveMessageBox(buferr.toString());
				}



		}
		catch(Exception ex)
		{
			return 0;
		}

		return 1;
	}
	//
	int getEncaissementForWS(String user,String pwd)
	{
		try
		{
			//la requete d'insertion du KD
			String queryInsert=dbKD.KD_INSERT_STRING;
			//si on est sur un identifiant de test (qui contient test) on change la tabgle cible
			String ident=user;
			ident=ident.toLowerCase();
			if (ident.contains("test"))
			{
				queryInsert=queryInsert.replace(dbKD.TABLENAME, dbKD.TABLENAME_TEST);
			}

				//on cherche les entetes de commandes 
				String queryHdr="SELECT * FROM "+
						dbKD.TABLENAME+
						" where "+
						" ( "+
						dbKD.fld_kd_dat_type+
						"="+
						Global.dbKDEncaissement.KD_TYPE+
						" and "+
						dbKD.fld_kd_dat_data11+
						"='0'"+
						" ) ";					

				String hdr="";
				Cursor cur=Global.dbParam.getDB().conn.rawQuery(queryHdr,null);
				while (cur.moveToNext())
				{
					hdr+=context.getString(R.string.separateur_morpion)+"(";
					hdr+="'"+MyDB.controlFld(Global.dbKDEncaissement.giveFld(cur, dbKD.fld_kd_soc_code  ))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEncaissement.giveFld(cur, dbKD.fld_kd_cli_code  ))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEncaissement.giveFld(cur, dbKD.fld_kd_pro_code  ))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEncaissement.giveFld(cur, dbKD.fld_kd_vis_code  ))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEncaissement.giveFld(cur, dbKD.fld_kd_cde_code  ))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEncaissement.giveFld(cur, dbKD.fld_kd_dat_type  ))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEncaissement.giveFld(cur, dbKD.fld_kd_dat_idx01 ))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEncaissement.giveFld(cur, dbKD.fld_kd_dat_idx02 ))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEncaissement.giveFld(cur, dbKD.fld_kd_dat_idx03 ))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEncaissement.giveFld(cur, dbKD.fld_kd_dat_idx04 ))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEncaissement.giveFld(cur, dbKD.fld_kd_dat_idx05 ))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEncaissement.giveFld(cur, dbKD.fld_kd_dat_idx06 ))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEncaissement.giveFld(cur, dbKD.fld_kd_dat_idx07 ))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEncaissement.giveFld(cur, dbKD.fld_kd_dat_idx08 ))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEncaissement.giveFld(cur, dbKD.fld_kd_dat_idx09 ))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEncaissement.giveFld(cur, dbKD.fld_kd_dat_idx10 ))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEncaissement.giveFld(cur, dbKD.fld_kd_dat_data01))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEncaissement.giveFld(cur, dbKD.fld_kd_dat_data02))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEncaissement.giveFld(cur, dbKD.fld_kd_dat_data03))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEncaissement.giveFld(cur, dbKD.fld_kd_dat_data04))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEncaissement.giveFld(cur, dbKD.fld_kd_dat_data05))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEncaissement.giveFld(cur, dbKD.fld_kd_dat_data06))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEncaissement.giveFld(cur, dbKD.fld_kd_dat_data07))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEncaissement.giveFld(cur, dbKD.fld_kd_dat_data08))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEncaissement.giveFld(cur, dbKD.fld_kd_dat_data09))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEncaissement.giveFld(cur, dbKD.fld_kd_dat_data10))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEncaissement.giveFld(cur, dbKD.fld_kd_dat_data11))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEncaissement.giveFld(cur, dbKD.fld_kd_dat_data12))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEncaissement.giveFld(cur, dbKD.fld_kd_dat_data13))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEncaissement.giveFld(cur, dbKD.fld_kd_dat_data14))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEncaissement.giveFld(cur, dbKD.fld_kd_dat_data15))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEncaissement.giveFld(cur, dbKD.fld_kd_dat_data16))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEncaissement.giveFld(cur, dbKD.fld_kd_dat_data17))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEncaissement.giveFld(cur, dbKD.fld_kd_dat_data18))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEncaissement.giveFld(cur, dbKD.fld_kd_dat_data19))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEncaissement.giveFld(cur, dbKD.fld_kd_dat_data20))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEncaissement.giveFld(cur, dbKD.fld_kd_dat_data21))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEncaissement.giveFld(cur, dbKD.fld_kd_dat_data22))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEncaissement.giveFld(cur, dbKD.fld_kd_dat_data23))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEncaissement.giveFld(cur, dbKD.fld_kd_dat_data24))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEncaissement.giveFld(cur, dbKD.fld_kd_dat_data25))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEncaissement.giveFld(cur, dbKD.fld_kd_dat_data26))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEncaissement.giveFld(cur, dbKD.fld_kd_dat_data27))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEncaissement.giveFld(cur, dbKD.fld_kd_dat_data28))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEncaissement.giveFld(cur, dbKD.fld_kd_dat_data29))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEncaissement.giveFld(cur, dbKD.fld_kd_dat_data30))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEncaissement.giveFld(cur, dbKD.fld_kd_val_data31))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEncaissement.giveFld(cur, dbKD.fld_kd_val_data32))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEncaissement.giveFld(cur, dbKD.fld_kd_val_data33))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEncaissement.giveFld(cur, dbKD.fld_kd_val_data34))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEncaissement.giveFld(cur, dbKD.fld_kd_val_data35))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEncaissement.giveFld(cur, dbKD.fld_kd_val_data36))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEncaissement.giveFld(cur, dbKD.fld_kd_val_data37))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEncaissement.giveFld(cur, dbKD.fld_kd_val_data38))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEncaissement.giveFld(cur, dbKD.fld_kd_val_data39))+"')";



				}


				String resultWS=MyWS.WSSend(user,pwd,"UpdateSrvTableHdrNonSec","UPDATE",queryInsert+hdr);
				//si le resultat est ok on efface la cde
				if (resultWS.equals(""))
				{
					
					Global.dbKDEncaissement.updateEtatOfEncaissement()	;

				}
				else
				{
					nbrerr++;
			//		Fonctions.FurtiveMessageBox(buferr.toString());
				}



		}
		catch(Exception ex)
		{
			return 0;
		}

		return 1;
	}
	int getEncaissementFactureForWS(String user,String pwd)
	{
		try
		{
			//la requete d'insertion du KD
			String queryInsert=dbKD.KD_INSERT_STRING;
			//si on est sur un identifiant de test (qui contient test) on change la tabgle cible
			String ident=user;
			ident=ident.toLowerCase();
			if (ident.contains("test"))
			{
				queryInsert=queryInsert.replace(dbKD.TABLENAME, dbKD.TABLENAME_TEST);
			}

				//on cherche les entetes de commandes 
				String queryHdr="SELECT * FROM "+
						dbKD.TABLENAME+
						" where "+
						" ( "+
						dbKD.fld_kd_dat_type+
						"="+
						Global.dbKDEncaisserFacture.KD_TYPE+
						" and "+
						dbKD.fld_kd_dat_data11+
						"='0'"+
						" ) ";					

				String hdr="";
				Cursor cur=Global.dbParam.getDB().conn.rawQuery(queryHdr,null);
				while (cur.moveToNext())
				{
					hdr+=context.getString(R.string.separateur_morpion)+"(";
					hdr+="'"+MyDB.controlFld(Global.dbKDEncaisserFacture.giveFld(cur, dbKD.fld_kd_soc_code  ))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEncaisserFacture.giveFld(cur, dbKD.fld_kd_cli_code  ))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEncaisserFacture.giveFld(cur, dbKD.fld_kd_pro_code  ))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEncaisserFacture.giveFld(cur, dbKD.fld_kd_vis_code  ))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEncaisserFacture.giveFld(cur, dbKD.fld_kd_cde_code  ))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEncaisserFacture.giveFld(cur, dbKD.fld_kd_dat_type  ))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEncaisserFacture.giveFld(cur, dbKD.fld_kd_dat_idx01 ))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEncaisserFacture.giveFld(cur, dbKD.fld_kd_dat_idx02 ))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEncaisserFacture.giveFld(cur, dbKD.fld_kd_dat_idx03 ))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEncaisserFacture.giveFld(cur, dbKD.fld_kd_dat_idx04 ))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEncaisserFacture.giveFld(cur, dbKD.fld_kd_dat_idx05 ))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEncaisserFacture.giveFld(cur, dbKD.fld_kd_dat_idx06 ))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEncaisserFacture.giveFld(cur, dbKD.fld_kd_dat_idx07 ))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEncaisserFacture.giveFld(cur, dbKD.fld_kd_dat_idx08 ))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEncaisserFacture.giveFld(cur, dbKD.fld_kd_dat_idx09 ))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEncaisserFacture.giveFld(cur, dbKD.fld_kd_dat_idx10 ))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEncaisserFacture.giveFld(cur, dbKD.fld_kd_dat_data01))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEncaisserFacture.giveFld(cur, dbKD.fld_kd_dat_data02))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEncaisserFacture.giveFld(cur, dbKD.fld_kd_dat_data03))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEncaisserFacture.giveFld(cur, dbKD.fld_kd_dat_data04))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEncaisserFacture.giveFld(cur, dbKD.fld_kd_dat_data05))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEncaisserFacture.giveFld(cur, dbKD.fld_kd_dat_data06))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEncaisserFacture.giveFld(cur, dbKD.fld_kd_dat_data07))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEncaisserFacture.giveFld(cur, dbKD.fld_kd_dat_data08))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEncaisserFacture.giveFld(cur, dbKD.fld_kd_dat_data09))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEncaisserFacture.giveFld(cur, dbKD.fld_kd_dat_data10))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEncaisserFacture.giveFld(cur, dbKD.fld_kd_dat_data11))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEncaisserFacture.giveFld(cur, dbKD.fld_kd_dat_data12))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEncaisserFacture.giveFld(cur, dbKD.fld_kd_dat_data13))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEncaisserFacture.giveFld(cur, dbKD.fld_kd_dat_data14))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEncaisserFacture.giveFld(cur, dbKD.fld_kd_dat_data15))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEncaisserFacture.giveFld(cur, dbKD.fld_kd_dat_data16))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEncaisserFacture.giveFld(cur, dbKD.fld_kd_dat_data17))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEncaisserFacture.giveFld(cur, dbKD.fld_kd_dat_data18))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEncaisserFacture.giveFld(cur, dbKD.fld_kd_dat_data19))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEncaisserFacture.giveFld(cur, dbKD.fld_kd_dat_data20))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEncaisserFacture.giveFld(cur, dbKD.fld_kd_dat_data21))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEncaisserFacture.giveFld(cur, dbKD.fld_kd_dat_data22))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEncaisserFacture.giveFld(cur, dbKD.fld_kd_dat_data23))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEncaisserFacture.giveFld(cur, dbKD.fld_kd_dat_data24))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEncaisserFacture.giveFld(cur, dbKD.fld_kd_dat_data25))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEncaisserFacture.giveFld(cur, dbKD.fld_kd_dat_data26))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEncaisserFacture.giveFld(cur, dbKD.fld_kd_dat_data27))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEncaisserFacture.giveFld(cur, dbKD.fld_kd_dat_data28))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEncaisserFacture.giveFld(cur, dbKD.fld_kd_dat_data29))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEncaisserFacture.giveFld(cur, dbKD.fld_kd_dat_data30))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEncaisserFacture.giveFld(cur, dbKD.fld_kd_val_data31))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEncaisserFacture.giveFld(cur, dbKD.fld_kd_val_data32))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEncaisserFacture.giveFld(cur, dbKD.fld_kd_val_data33))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEncaisserFacture.giveFld(cur, dbKD.fld_kd_val_data34))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEncaisserFacture.giveFld(cur, dbKD.fld_kd_val_data35))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEncaisserFacture.giveFld(cur, dbKD.fld_kd_val_data36))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEncaisserFacture.giveFld(cur, dbKD.fld_kd_val_data37))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEncaisserFacture.giveFld(cur, dbKD.fld_kd_val_data38))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEncaisserFacture.giveFld(cur, dbKD.fld_kd_val_data39))+"')";



				}


				String resultWS=MyWS.WSSend(user,pwd,"UpdateSrvTableHdrNonSec","UPDATE",queryInsert+hdr);
				//si le resultat est ok on efface la cde
				if (resultWS.equals(""))
				{
					
					Global.dbKDEncaisserFacture.updateEtatOfEncaissementFacture()	;

				}
				else
				{
					nbrerr++;
			//		Fonctions.FurtiveMessageBox(buferr.toString());
				}



		}
		catch(Exception ex)
		{
			return 0;
		}

		return 1;
	}
	
	int getRemiseBanqueForWS(String user,String pwd)
	{
		try
		{
			//la requete d'insertion du KD
			String queryInsert=dbKD.KD_INSERT_STRING;
			//si on est sur un identifiant de test (qui contient test) on change la tabgle cible
			String ident=user;
			ident=ident.toLowerCase();
			if (ident.contains("test"))
			{
				queryInsert=queryInsert.replace(dbKD.TABLENAME, dbKD.TABLENAME_TEST);
			}

				//on cherche les entetes de commandes 
				String queryHdr="SELECT * FROM "+
						dbKD.TABLENAME+
						" where "+
						" ( "+
						dbKD.fld_kd_dat_type+
						"="+
						Global.dbKDRetourBanqueEnt.KD_TYPE+
						" or "+
						dbKD.fld_kd_dat_type+
						"="+
						Global.dbKDRetourBanqueLin.KD_TYPE+
						
						" ) ";					

				String hdr="";
				Cursor cur=Global.dbParam.getDB().conn.rawQuery(queryHdr,null);
				while (cur.moveToNext())
				{
					hdr+=context.getString(R.string.separateur_morpion)+"(";
					hdr+="'"+MyDB.controlFld(Global.dbKDRetourBanqueEnt.giveFld(cur, dbKD.fld_kd_soc_code  ))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDRetourBanqueEnt.giveFld(cur, dbKD.fld_kd_cli_code  ))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDRetourBanqueEnt.giveFld(cur, dbKD.fld_kd_pro_code  ))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDRetourBanqueEnt.giveFld(cur, dbKD.fld_kd_vis_code  ))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDRetourBanqueEnt.giveFld(cur, dbKD.fld_kd_cde_code  ))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDRetourBanqueEnt.giveFld(cur, dbKD.fld_kd_dat_type  ))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDRetourBanqueEnt.giveFld(cur, dbKD.fld_kd_dat_idx01 ))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDRetourBanqueEnt.giveFld(cur, dbKD.fld_kd_dat_idx02 ))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDRetourBanqueEnt.giveFld(cur, dbKD.fld_kd_dat_idx03 ))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDRetourBanqueEnt.giveFld(cur, dbKD.fld_kd_dat_idx04 ))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDRetourBanqueEnt.giveFld(cur, dbKD.fld_kd_dat_idx05 ))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDRetourBanqueEnt.giveFld(cur, dbKD.fld_kd_dat_idx06 ))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDRetourBanqueEnt.giveFld(cur, dbKD.fld_kd_dat_idx07 ))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDRetourBanqueEnt.giveFld(cur, dbKD.fld_kd_dat_idx08 ))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDRetourBanqueEnt.giveFld(cur, dbKD.fld_kd_dat_idx09 ))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDRetourBanqueEnt.giveFld(cur, dbKD.fld_kd_dat_idx10 ))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDRetourBanqueEnt.giveFld(cur, dbKD.fld_kd_dat_data01))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDRetourBanqueEnt.giveFld(cur, dbKD.fld_kd_dat_data02))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDRetourBanqueEnt.giveFld(cur, dbKD.fld_kd_dat_data03))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDRetourBanqueEnt.giveFld(cur, dbKD.fld_kd_dat_data04))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDRetourBanqueEnt.giveFld(cur, dbKD.fld_kd_dat_data05))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDRetourBanqueEnt.giveFld(cur, dbKD.fld_kd_dat_data06))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDRetourBanqueEnt.giveFld(cur, dbKD.fld_kd_dat_data07))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDRetourBanqueEnt.giveFld(cur, dbKD.fld_kd_dat_data08))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDRetourBanqueEnt.giveFld(cur, dbKD.fld_kd_dat_data09))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDRetourBanqueEnt.giveFld(cur, dbKD.fld_kd_dat_data10))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDRetourBanqueEnt.giveFld(cur, dbKD.fld_kd_dat_data11))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDRetourBanqueEnt.giveFld(cur, dbKD.fld_kd_dat_data12))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDRetourBanqueEnt.giveFld(cur, dbKD.fld_kd_dat_data13))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDRetourBanqueEnt.giveFld(cur, dbKD.fld_kd_dat_data14))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDRetourBanqueEnt.giveFld(cur, dbKD.fld_kd_dat_data15))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDRetourBanqueEnt.giveFld(cur, dbKD.fld_kd_dat_data16))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDRetourBanqueEnt.giveFld(cur, dbKD.fld_kd_dat_data17))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDRetourBanqueEnt.giveFld(cur, dbKD.fld_kd_dat_data18))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDRetourBanqueEnt.giveFld(cur, dbKD.fld_kd_dat_data19))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDRetourBanqueEnt.giveFld(cur, dbKD.fld_kd_dat_data20))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDRetourBanqueEnt.giveFld(cur, dbKD.fld_kd_dat_data21))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDRetourBanqueEnt.giveFld(cur, dbKD.fld_kd_dat_data22))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDRetourBanqueEnt.giveFld(cur, dbKD.fld_kd_dat_data23))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDRetourBanqueEnt.giveFld(cur, dbKD.fld_kd_dat_data24))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDRetourBanqueEnt.giveFld(cur, dbKD.fld_kd_dat_data25))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDRetourBanqueEnt.giveFld(cur, dbKD.fld_kd_dat_data26))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDRetourBanqueEnt.giveFld(cur, dbKD.fld_kd_dat_data27))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDRetourBanqueEnt.giveFld(cur, dbKD.fld_kd_dat_data28))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDRetourBanqueEnt.giveFld(cur, dbKD.fld_kd_dat_data29))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDRetourBanqueEnt.giveFld(cur, dbKD.fld_kd_dat_data30))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDRetourBanqueEnt.giveFld(cur, dbKD.fld_kd_val_data31))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDRetourBanqueEnt.giveFld(cur, dbKD.fld_kd_val_data32))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDRetourBanqueEnt.giveFld(cur, dbKD.fld_kd_val_data33))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDRetourBanqueEnt.giveFld(cur, dbKD.fld_kd_val_data34))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDRetourBanqueEnt.giveFld(cur, dbKD.fld_kd_val_data35))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDRetourBanqueEnt.giveFld(cur, dbKD.fld_kd_val_data36))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDRetourBanqueEnt.giveFld(cur, dbKD.fld_kd_val_data37))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDRetourBanqueEnt.giveFld(cur, dbKD.fld_kd_val_data38))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDRetourBanqueEnt.giveFld(cur, dbKD.fld_kd_val_data39))+"')";



				}


				String resultWS=MyWS.WSSend(user,pwd,"UpdateSrvTableHdrNonSec","UPDATE",queryInsert+hdr);
				//si le resultat est ok on efface la cde
				if (resultWS.equals(""))
				{
					
					//Global.dbKDEncaisserFacture.DeleteOfEncaissementFacture()	;--> les lignes d'encaisserFacture sont supprimés en meme 
					//temps que les lignes d'encaissements				
					Global.dbKDEncaissement.DeleteOfEncaissement()	;
					Global.dbKDRetourBanqueEnt.DeleteRetourBanqueEnt()	;
					Global.dbKDRetourBanqueLin.DeleteRetourBanqueLin()	;								

				}
				else
				{
					nbrerr++;
			//		Fonctions.FurtiveMessageBox(buferr.toString());
				}



		}
		catch(Exception ex)
		{
			return 0;
		}

		return 1;
	}
	
	int getComptageMachineClientForWS(String user,String pwd)
	{
		try
		{
			//la requete d'insertion du KD
			String queryInsert=dbKD.KD_INSERT_STRING;
			//si on est sur un identifiant de test (qui contient test) on change la tabgle cible
			String ident=user;
			ident=ident.toLowerCase();
			if (ident.contains("test"))
			{
				queryInsert=queryInsert.replace(dbKD.TABLENAME, dbKD.TABLENAME_TEST);
			}

				//on cherche les entetes de commandes 
				String queryHdr="SELECT * FROM "+
						dbKD.TABLENAME+
						" where "+
						" ( "+
						dbKD.fld_kd_dat_type+
						"="+
						Global.dbKDComptageMachineClient.KD_TYPE+
						" ) ";					

				String hdr="";
				Cursor cur=Global.dbParam.getDB().conn.rawQuery(queryHdr,null);
				while (cur.moveToNext())
				{
					hdr+=context.getString(R.string.separateur_morpion)+"(";
					hdr+="'"+MyDB.controlFld(Global.dbKDLinInv.giveFld(cur, dbKD.fld_kd_soc_code  ))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDLinInv.giveFld(cur, dbKD.fld_kd_cli_code  ))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDLinInv.giveFld(cur, dbKD.fld_kd_pro_code  ))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDLinInv.giveFld(cur, dbKD.fld_kd_vis_code  ))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDLinInv.giveFld(cur, dbKD.fld_kd_cde_code  ))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDLinInv.giveFld(cur, dbKD.fld_kd_dat_type  ))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDLinInv.giveFld(cur, dbKD.fld_kd_dat_idx01 ))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDLinInv.giveFld(cur, dbKD.fld_kd_dat_idx02 ))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDLinInv.giveFld(cur, dbKD.fld_kd_dat_idx03 ))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDLinInv.giveFld(cur, dbKD.fld_kd_dat_idx04 ))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDLinInv.giveFld(cur, dbKD.fld_kd_dat_idx05 ))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDLinInv.giveFld(cur, dbKD.fld_kd_dat_idx06 ))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDLinInv.giveFld(cur, dbKD.fld_kd_dat_idx07 ))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDLinInv.giveFld(cur, dbKD.fld_kd_dat_idx08 ))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDLinInv.giveFld(cur, dbKD.fld_kd_dat_idx09 ))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDLinInv.giveFld(cur, dbKD.fld_kd_dat_idx10 ))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDLinInv.giveFld(cur, dbKD.fld_kd_dat_data01))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDLinInv.giveFld(cur, dbKD.fld_kd_dat_data02))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDLinInv.giveFld(cur, dbKD.fld_kd_dat_data03))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDLinInv.giveFld(cur, dbKD.fld_kd_dat_data04))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDLinInv.giveFld(cur, dbKD.fld_kd_dat_data05))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDLinInv.giveFld(cur, dbKD.fld_kd_dat_data06))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDLinInv.giveFld(cur, dbKD.fld_kd_dat_data07))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDLinInv.giveFld(cur, dbKD.fld_kd_dat_data08))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDLinInv.giveFld(cur, dbKD.fld_kd_dat_data09))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDLinInv.giveFld(cur, dbKD.fld_kd_dat_data10))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDLinInv.giveFld(cur, dbKD.fld_kd_dat_data11))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDLinInv.giveFld(cur, dbKD.fld_kd_dat_data12))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDLinInv.giveFld(cur, dbKD.fld_kd_dat_data13))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDLinInv.giveFld(cur, dbKD.fld_kd_dat_data14))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDLinInv.giveFld(cur, dbKD.fld_kd_dat_data15))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDLinInv.giveFld(cur, dbKD.fld_kd_dat_data16))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDLinInv.giveFld(cur, dbKD.fld_kd_dat_data17))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDLinInv.giveFld(cur, dbKD.fld_kd_dat_data18))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDLinInv.giveFld(cur, dbKD.fld_kd_dat_data19))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDLinInv.giveFld(cur, dbKD.fld_kd_dat_data20))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDLinInv.giveFld(cur, dbKD.fld_kd_dat_data21))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDLinInv.giveFld(cur, dbKD.fld_kd_dat_data22))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDLinInv.giveFld(cur, dbKD.fld_kd_dat_data23))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDLinInv.giveFld(cur, dbKD.fld_kd_dat_data24))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDLinInv.giveFld(cur, dbKD.fld_kd_dat_data25))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDLinInv.giveFld(cur, dbKD.fld_kd_dat_data26))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDLinInv.giveFld(cur, dbKD.fld_kd_dat_data27))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDLinInv.giveFld(cur, dbKD.fld_kd_dat_data28))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDLinInv.giveFld(cur, dbKD.fld_kd_dat_data29))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDLinInv.giveFld(cur, dbKD.fld_kd_dat_data30))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDLinInv.giveFld(cur, dbKD.fld_kd_val_data31))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDLinInv.giveFld(cur, dbKD.fld_kd_val_data32))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDLinInv.giveFld(cur, dbKD.fld_kd_val_data33))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDLinInv.giveFld(cur, dbKD.fld_kd_val_data34))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDLinInv.giveFld(cur, dbKD.fld_kd_val_data35))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDLinInv.giveFld(cur, dbKD.fld_kd_val_data36))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDLinInv.giveFld(cur, dbKD.fld_kd_val_data37))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDLinInv.giveFld(cur, dbKD.fld_kd_val_data38))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDLinInv.giveFld(cur, dbKD.fld_kd_val_data39))+"')";



				}


				String resultWS=MyWS.WSSend(user,pwd,"UpdateSrvTableHdrNonSec","UPDATE",queryInsert+hdr);
				//si le resultat est ok on efface la cde
				if (resultWS.equals(""))
				{
					StringBuffer err= new StringBuffer();
					Global.dbKDComptageMachineClient.deleteAll(err)	;

				}
				else
				{
					nbrerr++;
			//		Fonctions.FurtiveMessageBox(buferr.toString());
				}



		}
		catch(Exception ex)
		{
			return 0;
		}

		return 1;
	}
	 
	int getCliVuForWS(String user,String pwd)
	{
		try
		{
			//la requete d'insertion du KD
			String queryInsert=dbKD.KD_INSERT_STRING;
			//si on est sur un identifiant de test (qui contient test) on change la tabgle cible
			String ident=user;
			ident=ident.toLowerCase();
			if (ident.contains("test"))
			{
				queryInsert=queryInsert.replace(dbKD.TABLENAME, dbKD.TABLENAME_TEST);
			}


			ArrayList<String> tabCde=new ArrayList<String>();
			
			//TOF: Envoi en un coup, puis suppression de toutes les lignes
			//int nbrcde=ListeCdesAEnvoyer(tabCde);

			//on va faire une trame par cde, pour valider l'envoi cde par cde
			//for (int i=0;i<tabCde.size();i++)
			{


				//on cherche les entetes de commandes 
				String queryHdr="SELECT * FROM "+
						dbKD.TABLENAME+
						" where "+
						" ( "+
						dbKD.fld_kd_dat_type+
						"="+
						Global.dbKDClientVu.KD_TYPE+
						
						" ) "+ 
						" ORDER BY "+dbKD.fld_kd_dat_type+" DESC";					

				String hdr="";
				Cursor cur=Global.dbParam.getDB().conn.rawQuery(queryHdr,null);
				while (cur.moveToNext())
				{
					hdr+=context.getString(R.string.separateur_morpion)+"(";
					hdr+="'"+MyDB.controlFld(Global.dbKDEntCde.giveFld(cur, dbKD.fld_kd_soc_code  ))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEntCde.giveFld(cur, dbKD.fld_kd_cli_code  ))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEntCde.giveFld(cur, dbKD.fld_kd_pro_code  ))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEntCde.giveFld(cur, dbKD.fld_kd_vis_code  ))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEntCde.giveFld(cur, dbKD.fld_kd_cde_code  ))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEntCde.giveFld(cur, dbKD.fld_kd_dat_type  ))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEntCde.giveFld(cur, dbKD.fld_kd_dat_idx01 ))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEntCde.giveFld(cur, dbKD.fld_kd_dat_idx02 ))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEntCde.giveFld(cur, dbKD.fld_kd_dat_idx03 ))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEntCde.giveFld(cur, dbKD.fld_kd_dat_idx04 ))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEntCde.giveFld(cur, dbKD.fld_kd_dat_idx05 ))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEntCde.giveFld(cur, dbKD.fld_kd_dat_idx06 ))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEntCde.giveFld(cur, dbKD.fld_kd_dat_idx07 ))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEntCde.giveFld(cur, dbKD.fld_kd_dat_idx08 ))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEntCde.giveFld(cur, dbKD.fld_kd_dat_idx09 ))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEntCde.giveFld(cur, dbKD.fld_kd_dat_idx10 ))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEntCde.giveFld(cur, dbKD.fld_kd_dat_data01))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEntCde.giveFld(cur, dbKD.fld_kd_dat_data02))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEntCde.giveFld(cur, dbKD.fld_kd_dat_data03))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEntCde.giveFld(cur, dbKD.fld_kd_dat_data04))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEntCde.giveFld(cur, dbKD.fld_kd_dat_data05))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEntCde.giveFld(cur, dbKD.fld_kd_dat_data06))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEntCde.giveFld(cur, dbKD.fld_kd_dat_data07))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEntCde.giveFld(cur, dbKD.fld_kd_dat_data08))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEntCde.giveFld(cur, dbKD.fld_kd_dat_data09))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEntCde.giveFld(cur, dbKD.fld_kd_dat_data10))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEntCde.giveFld(cur, dbKD.fld_kd_dat_data11))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEntCde.giveFld(cur, dbKD.fld_kd_dat_data12))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEntCde.giveFld(cur, dbKD.fld_kd_dat_data13))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEntCde.giveFld(cur, dbKD.fld_kd_dat_data14))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEntCde.giveFld(cur, dbKD.fld_kd_dat_data15))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEntCde.giveFld(cur, dbKD.fld_kd_dat_data16))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEntCde.giveFld(cur, dbKD.fld_kd_dat_data17))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEntCde.giveFld(cur, dbKD.fld_kd_dat_data18))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEntCde.giveFld(cur, dbKD.fld_kd_dat_data19))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEntCde.giveFld(cur, dbKD.fld_kd_dat_data20))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEntCde.giveFld(cur, dbKD.fld_kd_dat_data21))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEntCde.giveFld(cur, dbKD.fld_kd_dat_data22))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEntCde.giveFld(cur, dbKD.fld_kd_dat_data23))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEntCde.giveFld(cur, dbKD.fld_kd_dat_data24))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEntCde.giveFld(cur, dbKD.fld_kd_dat_data25))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEntCde.giveFld(cur, dbKD.fld_kd_dat_data26))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEntCde.giveFld(cur, dbKD.fld_kd_dat_data27))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEntCde.giveFld(cur, dbKD.fld_kd_dat_data28))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEntCde.giveFld(cur, dbKD.fld_kd_dat_data29))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEntCde.giveFld(cur, dbKD.fld_kd_dat_data30))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEntCde.giveFld(cur, dbKD.fld_kd_val_data31))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEntCde.giveFld(cur, dbKD.fld_kd_val_data32))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEntCde.giveFld(cur, dbKD.fld_kd_val_data33))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEntCde.giveFld(cur, dbKD.fld_kd_val_data34))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEntCde.giveFld(cur, dbKD.fld_kd_val_data35))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEntCde.giveFld(cur, dbKD.fld_kd_val_data36))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEntCde.giveFld(cur, dbKD.fld_kd_val_data37))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEntCde.giveFld(cur, dbKD.fld_kd_val_data38))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbKDEntCde.giveFld(cur, dbKD.fld_kd_val_data39))+"')";



				}


				String resultWS=MyWS.WSSend(user,pwd,"UpdateSrvTableHdrNonSec","UPDATE",queryInsert+hdr);
				//si le resultat est ok on efface la cde
				if (resultWS.equals(""))
				{
					StringBuilder errV=new StringBuilder();
					StringBuffer  errB=new StringBuffer();
					Global.dbKDClientVu.deleteAll( )	;
				}
				else
				{
					nbrerr++;
			//		Fonctions.FurtiveMessageBox(buferr.toString());
				}

			}



		}
		catch(Exception ex)
		{
			return 0;
		}

		return 1;
	}
	
	int getAgendaForWS(String user,String pwd)
	{
		try {
			//la requete d'insertion du KD
			String queryInsert=dbKD.KD_INSERT_STRING;
			//si on est sur un identifiant de test (qui contient test) on change la tabgle cible
			String ident=user;
			ident=ident.toLowerCase();
			if (ident.contains("test"))
			{
				queryInsert=queryInsert.replace(dbKD.TABLENAME, dbKD.TABLENAME_TEST);
			}

			// on cherche les entetes
			String queryHdr = "SELECT * FROM " + dbKD.TABLENAME + " where "
					+ " ( " + dbKD.fld_kd_dat_type + "="
					+ Global.dbKDAgenda.KD_TYPE + " ) ";
			// on efface sur le serveur l'equivalent en periode, de ce que l'on
			// envoi

			String hdr = Global.dbKDAgenda.queryDeleteFromOnServer(context)
					+ context.getString(R.string.separateur_morpion);
			Cursor cur = Global.dbParam.getDB().conn.rawQuery(queryHdr, null);
			while (cur.moveToNext()) {
				hdr += queryInsert + "(";
				hdr += "'"
						+ MyDB.controlFld(Global.dbKDAgenda.giveFld(cur,
								dbKD.fld_kd_soc_code)) + "',";
				hdr += "'"
						+ MyDB.controlFld(Global.dbKDAgenda.giveFld(cur,
								dbKD.fld_kd_cli_code)) + "',";
				hdr += "'"
						+ MyDB.controlFld(Global.dbKDAgenda.giveFld(cur,
								dbKD.fld_kd_pro_code)) + "',";
				hdr += "'"
						+ MyDB.controlFld(Global.dbKDAgenda.giveFld(cur,
								dbKD.fld_kd_vis_code)) + "',";
				hdr += "'"
						+ MyDB.controlFld(Global.dbKDAgenda.giveFld(cur,
								dbKD.fld_kd_cde_code)) + "',";
				hdr += "'"
						+ MyDB.controlFld(Global.dbKDAgenda.giveFld(cur,
								dbKD.fld_kd_dat_type)) + "',";
				hdr += "'"
						+ MyDB.controlFld(Global.dbKDAgenda.giveFld(cur,
								dbKD.fld_kd_dat_idx01)) + "',";
				hdr += "'"
						+ MyDB.controlFld(Global.dbKDAgenda.giveFld(cur,
								dbKD.fld_kd_dat_idx02)) + "',";
				hdr += "'"
						+ MyDB.controlFld(Global.dbKDAgenda.giveFld(cur,
								dbKD.fld_kd_dat_idx03)) + "',";
				hdr += "'"
						+ MyDB.controlFld(Global.dbKDAgenda.giveFld(cur,
								dbKD.fld_kd_dat_idx04)) + "',";
				hdr += "'"
						+ MyDB.controlFld(Global.dbKDAgenda.giveFld(cur,
								dbKD.fld_kd_dat_idx05)) + "',";
				hdr += "'"
						+ MyDB.controlFld(Global.dbKDAgenda.giveFld(cur,
								dbKD.fld_kd_dat_idx06)) + "',";
				hdr += "'"
						+ MyDB.controlFld(Global.dbKDAgenda.giveFld(cur,
								dbKD.fld_kd_dat_idx07)) + "',";
				hdr += "'"
						+ MyDB.controlFld(Global.dbKDAgenda.giveFld(cur,
								dbKD.fld_kd_dat_idx08)) + "',";
				hdr += "'"
						+ MyDB.controlFld(Global.dbKDAgenda.giveFld(cur,
								dbKD.fld_kd_dat_idx09)) + "',";
				hdr += "'"
						+ MyDB.controlFld(Global.dbKDAgenda.giveFld(cur,
								dbKD.fld_kd_dat_idx10)) + "',";
				hdr += "'"
						+ MyDB.controlFld(Global.dbKDAgenda.giveFld(cur,
								dbKD.fld_kd_dat_data01)) + "',";
				hdr += "'"
						+ MyDB.controlFld(Global.dbKDAgenda.giveFld(cur,
								dbKD.fld_kd_dat_data02)) + "',";
				hdr += "'"
						+ MyDB.controlFld(Global.dbKDAgenda.giveFld(cur,
								dbKD.fld_kd_dat_data03)) + "',";
				hdr += "'"
						+ MyDB.controlFld(Global.dbKDAgenda.giveFld(cur,
								dbKD.fld_kd_dat_data04)) + "',";
				hdr += "'"
						+ MyDB.controlFld(Global.dbKDAgenda.giveFld(cur,
								dbKD.fld_kd_dat_data05)) + "',";
				hdr += "'"
						+ MyDB.controlFld(Global.dbKDAgenda.giveFld(cur,
								dbKD.fld_kd_dat_data06)) + "',";
				hdr += "'"
						+ MyDB.controlFld(Global.dbKDAgenda.giveFld(cur,
								dbKD.fld_kd_dat_data07)) + "',";
				hdr += "'"
						+ MyDB.controlFld(Global.dbKDAgenda.giveFld(cur,
								dbKD.fld_kd_dat_data08)) + "',";
				hdr += "'"
						+ MyDB.controlFld(Global.dbKDAgenda.giveFld(cur,
								dbKD.fld_kd_dat_data09)) + "',";
				hdr += "'"
						+ MyDB.controlFld(Global.dbKDAgenda.giveFld(cur,
								dbKD.fld_kd_dat_data10)) + "',";
				hdr += "'"
						+ MyDB.controlFld(Global.dbKDAgenda.giveFld(cur,
								dbKD.fld_kd_dat_data11)) + "',";
				hdr += "'"
						+ MyDB.controlFld(Global.dbKDAgenda.giveFld(cur,
								dbKD.fld_kd_dat_data12)) + "',";
				hdr += "'"
						+ MyDB.controlFld(Global.dbKDAgenda.giveFld(cur,
								dbKD.fld_kd_dat_data13)) + "',";
				hdr += "'"
						+ MyDB.controlFld(Global.dbKDAgenda.giveFld(cur,
								dbKD.fld_kd_dat_data14)) + "',";
				hdr += "'"
						+ MyDB.controlFld(Global.dbKDAgenda.giveFld(cur,
								dbKD.fld_kd_dat_data15)) + "',";
				hdr += "'"
						+ MyDB.controlFld(Global.dbKDAgenda.giveFld(cur,
								dbKD.fld_kd_dat_data16)) + "',";
				hdr += "'"
						+ MyDB.controlFld(Global.dbKDAgenda.giveFld(cur,
								dbKD.fld_kd_dat_data17)) + "',";
				hdr += "'"
						+ MyDB.controlFld(Global.dbKDAgenda.giveFld(cur,
								dbKD.fld_kd_dat_data18)) + "',";
				hdr += "'"
						+ MyDB.controlFld(Global.dbKDAgenda.giveFld(cur,
								dbKD.fld_kd_dat_data19)) + "',";
				hdr += "'"
						+ MyDB.controlFld(Global.dbKDAgenda.giveFld(cur,
								dbKD.fld_kd_dat_data20)) + "',";
				hdr += "'"
						+ MyDB.controlFld(Global.dbKDAgenda.giveFld(cur,
								dbKD.fld_kd_dat_data21)) + "',";
				hdr += "'"
						+ MyDB.controlFld(Global.dbKDAgenda.giveFld(cur,
								dbKD.fld_kd_dat_data22)) + "',";
				hdr += "'"
						+ MyDB.controlFld(Global.dbKDAgenda.giveFld(cur,
								dbKD.fld_kd_dat_data23)) + "',";
				hdr += "'"
						+ MyDB.controlFld(Global.dbKDAgenda.giveFld(cur,
								dbKD.fld_kd_dat_data24)) + "',";
				hdr += "'"
						+ MyDB.controlFld(Global.dbKDAgenda.giveFld(cur,
								dbKD.fld_kd_dat_data25)) + "',";
				hdr += "'"
						+ MyDB.controlFld(Global.dbKDAgenda.giveFld(cur,
								dbKD.fld_kd_dat_data26)) + "',";
				hdr += "'"
						+ MyDB.controlFld(Global.dbKDAgenda.giveFld(cur,
								dbKD.fld_kd_dat_data27)) + "',";
				hdr += "'"
						+ MyDB.controlFld(Global.dbKDAgenda.giveFld(cur,
								dbKD.fld_kd_dat_data28)) + "',";
				hdr += "'"
						+ MyDB.controlFld(Global.dbKDAgenda.giveFld(cur,
								dbKD.fld_kd_dat_data29)) + "',";
				hdr += "'"
						+ MyDB.controlFld(Global.dbKDAgenda.giveFld(cur,
								dbKD.fld_kd_dat_data30)) + "',";
				hdr += "'"
						+ MyDB.controlFld(Global.dbKDAgenda.giveFld(cur,
								dbKD.fld_kd_val_data31)) + "',";
				hdr += "'"
						+ MyDB.controlFld(Global.dbKDAgenda.giveFld(cur,
								dbKD.fld_kd_val_data32)) + "',";
				hdr += "'"
						+ MyDB.controlFld(Global.dbKDAgenda.giveFld(cur,
								dbKD.fld_kd_val_data33)) + "',";
				hdr += "'"
						+ MyDB.controlFld(Global.dbKDAgenda.giveFld(cur,
								dbKD.fld_kd_val_data34)) + "',";
				hdr += "'"
						+ MyDB.controlFld(Global.dbKDAgenda.giveFld(cur,
								dbKD.fld_kd_val_data35)) + "',";
				hdr += "'"
						+ MyDB.controlFld(Global.dbKDAgenda.giveFld(cur,
								dbKD.fld_kd_val_data36)) + "',";
				hdr += "'"
						+ MyDB.controlFld(Global.dbKDAgenda.giveFld(cur,
								dbKD.fld_kd_val_data37)) + "',";
				hdr += "'"
						+ MyDB.controlFld(Global.dbKDAgenda.giveFld(cur,
								dbKD.fld_kd_val_data38)) + "',";
				hdr += "'"
						+ MyDB.controlFld(Global.dbKDAgenda.giveFld(cur,
								dbKD.fld_kd_val_data39)) + "')"
						+ context.getString(R.string.separateur_morpion);

			}
			if (cur!=null)
				cur.close();//MV 26/03/2015
			StringBuffer buferr = new StringBuffer();
			
			String resultWS = MyWS.WSSend(user, pwd, "UpdateSrvTableNonSec",
					"UPDATE", hdr);
			// si le resultat est ok on efface la cde
			if (resultWS.equals("")) {

			} else {
				//FurtiveMessageBox(Global.lastErrorMessage);
				nbrerr++;
			}

			
		} catch (Exception ex) {
			return 0;
		}

		return 1;
	}
	
	int getDeleteClientvuForWS(String user,String pwd)
	{
		try {
			//la requete d'insertion du KD
			String queryInsert=dbKD.KD_INSERT_STRING;
			//si on est sur un identifiant de test (qui contient test) on change la tabgle cible
			String ident=user;
			ident=ident.toLowerCase();
			if (ident.contains("test"))
			{
				queryInsert=queryInsert.replace(dbKD.TABLENAME, dbKD.TABLENAME_TEST);
			}

			// on cherche les entetes
			String queryHdr = "SELECT * FROM " + dbKD.TABLENAME + " where "
					+ " ( " + dbKD.fld_kd_dat_type + "="
					+ Global.dbKDClientVu.KD_TYPE + " ) ";
			// on efface sur le serveur l'equivalent en periode, de ce que l'on
			// envoi
			String hdr="";
			Cursor cur = Global.dbParam.getDB().conn.rawQuery(queryHdr, null);
			while (cur.moveToNext()) {
				 hdr += Global.dbKDClientVu.queryDeleteFromOnServer(context,Global.dbKDClientVu.giveFld(cur,
						dbKD.fld_kd_cli_code),Global.dbKDClientVu.giveFld(cur,
								dbKD.fld_kd_dat_data02),user)
						+ context.getString(R.string.separateur_morpion);
				

			}
			if (cur!=null)
				cur.close();//MV 26/03/2015
			StringBuffer buferr = new StringBuffer();
			
			String resultWS = MyWS.WSSend(user, pwd, "UpdateSrvTableNonSec",
					"UPDATE", hdr);
			// si le resultat est ok on efface la cde
			if (resultWS.equals("")) {
				return 1;

			} else {
				 
				//FurtiveMessageBox(Global.lastErrorMessage);
			}

			
		} catch (Exception ex) {
			return 0;
		}

		return 1;
	}
	
	/**
	 * on stock dans un tableau les cdes � transmettre
	 * @author Marc VOUAUX
	 * @return
	 */
	int ListeCdesAEnvoyer(ArrayList<String> tabCde)
	{

		int i=0;
		try {
			//on cherche les entetes de commandes 
			String queryHdr="SELECT * FROM "+
					dbKD.TABLENAME+
					" where "+
					dbKD.fld_kd_dat_type+
					"="+
					Global.dbKDEntCde.KD_TYPE+" and "+Global.dbKDEntCde.FIELD_ENTCDE_SEND+"='1'  ";

			i = 0;
			String hdr="";
			Cursor cur=   Global.dbParam.getDB().conn.rawQuery(queryHdr,null);
			while (cur.moveToNext())
			{
				String numCde=Global.dbKDEntCde.giveFld(cur, dbKD.fld_kd_cde_code );
				
				//Verifier si il y a des ligne
				if(Global.dbKDLinCde.Count_Numcde(numCde,false)>0)
				{
					tabCde.add(numCde);
					i++;
				}
				
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return i;
	}
	
	int ListeCdesASauvegarder(ArrayList<String> tabCde)
	{

		int i=0;
		try {
			//on cherche les entetes de commandes 
			String queryHdr="SELECT * FROM "+
					dbKD.TABLENAME+
					" where "+
					dbKD.fld_kd_dat_type+
					"="+
					Global.dbKDEntCde.KD_TYPE+" and "+Global.dbKDEntCde.FIELD_ENTCDE_SEND+"='1'  ";

			i = 0;
			String hdr="";
			Cursor cur=   Global.dbParam.getDB().conn.rawQuery(queryHdr,null);
			while (cur.moveToNext())
			{
				String numCde=Global.dbKDEntCde.giveFld(cur, dbKD.fld_kd_cde_code );
				
				//Verifier si il y a des ligne
				if(Global.dbKDLinCde.Count_Numcde(numCde,false)>0)
				{
					tabCde.add(numCde);
					i++;
				}
				
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return i;
	}
	int ListeSaveCdes_Delete() {

		int i = 0;
		try {
			// on cherche les entetes de commandes
			String queryHdr = "SELECT * FROM " + dbKD.TABLENAME_SAVE + " where "
					+ dbKD.fld_kd_dat_type + "=" + Global.dbKDEntCde.KD_TYPE;

			i = 0;
			String hdr = "";
			Cursor cur = Global.dbParam.getDB().conn.rawQuery(queryHdr, null);
			while (cur.moveToNext()) {
				String dateVis= Global.dbKDEntCde.giveFld(cur,
						Global.dbKDEntCde.FIELD_ENTCDE_DATECDE);
				if(Fonctions.GetStringToIntDanem(dateVis)<Fonctions.GetStringToIntDanem(Fonctions.getYYYYMMDD_MOINS(10)))
				{
					
					String numCde = Global.dbKDEntCde.giveFld(cur,
							dbKD.fld_kd_cde_code);
					
					String codecli= Global.dbKDEntCde.giveFld(cur,
							Global.dbKDEntCde.FIELD_ENTCDE_CODECLI);
					
					StringBuffer err = new StringBuffer();
					Global.dbKDEntCde.delete_savecde(numCde, codecli, err);
					
					
					// Verifier si il y a des ligne
					Global.dbKDLinCde.deleteNumcde_Savecde(numCde, err);
						
				}
				
			}
			if (cur!=null)
				cur.close();//MV 26/03/2015
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return i;
	}
	
	String CopyCde_Kems_savedata() {
		String stReturnOK = "";
		StringBuilder err1 = new StringBuilder();
		
		try {
			// la requete d'insertion du kd_save
			String queryInsert = dbKD.KD_SAVE_INSERT_STRING;
			
			ArrayList<String> tabCde = new ArrayList<String>();
			int nbrcde = ListeCdesASauvegarder(tabCde);

			// on va faire une trame par cde, pour valider l'envoi cde par cde
			for (int i = 0; i < tabCde.size(); i++) {
				
				

				// on cherche les entetes de commandes
				String queryHdr = "SELECT * FROM " + dbKD.TABLENAME + " where "
						+ " ( " 
						+ dbKD.fld_kd_dat_type + "=" + Global.dbKDLinCde.KD_TYPE
						+ " or " + dbKD.fld_kd_dat_type + "="
						+ Global.dbKDEntCde.KD_TYPE + " ) " + " and "
						+ Global.dbKDEntCde.FIELD_ENTCDE_CODECDE + "=" + "'"
						+ tabCde.get(i) + "' ORDER BY " + dbKD.fld_kd_dat_type
						+ " DESC";
				

				

				String hdr = "";
				
				Cursor cur = Global.dbParam.getDB().conn.rawQuery(queryHdr, null);
				while (cur.moveToNext()) {
					hdr = /*getString(R.string.separateur_morpion)*/  "(";
					hdr += "'"
							+ MyDB.controlFld(Global.dbKDEntCde.giveFld(cur,
									dbKD.fld_kd_soc_code)) + "',";
					hdr += "'"
							+ MyDB.controlFld(Global.dbKDEntCde.giveFld(cur,
									dbKD.fld_kd_cli_code)) + "',";
					hdr += "'"
							+ MyDB.controlFld(Global.dbKDEntCde.giveFld(cur,
									dbKD.fld_kd_pro_code)) + "',";
					hdr += "'"
							+ MyDB.controlFld(Global.dbKDEntCde.giveFld(cur,
									dbKD.fld_kd_vis_code)) + "',";
					hdr += "'"
							+ MyDB.controlFld(Global.dbKDEntCde.giveFld(cur,
									dbKD.fld_kd_cde_code)) + "',";
					hdr += "'"
							+ MyDB.controlFld(Global.dbKDEntCde.giveFld(cur,
									dbKD.fld_kd_dat_type)) + "',";
					hdr += "'"
							+ MyDB.controlFld(Global.dbKDEntCde.giveFld(cur,
									dbKD.fld_kd_dat_idx01)) + "',";
					hdr += "'"
							+ MyDB.controlFld(Global.dbKDEntCde.giveFld(cur,
									dbKD.fld_kd_dat_idx02)) + "',";
					hdr += "'"
							+ MyDB.controlFld(Global.dbKDEntCde.giveFld(cur,
									dbKD.fld_kd_dat_idx03)) + "',";
					hdr += "'"
							+ MyDB.controlFld(Global.dbKDEntCde.giveFld(cur,
									dbKD.fld_kd_dat_idx04)) + "',";
					hdr += "'"
							+ MyDB.controlFld(Global.dbKDEntCde.giveFld(cur,
									dbKD.fld_kd_dat_idx05)) + "',";
					hdr += "'"
							+ MyDB.controlFld(Global.dbKDEntCde.giveFld(cur,
									dbKD.fld_kd_dat_idx06)) + "',";
					hdr += "'"
							+ MyDB.controlFld(Global.dbKDEntCde.giveFld(cur,
									dbKD.fld_kd_dat_idx07)) + "',";
					hdr += "'"
							+ MyDB.controlFld(Global.dbKDEntCde.giveFld(cur,
									dbKD.fld_kd_dat_idx08)) + "',";
					hdr += "'"
							+ MyDB.controlFld(Global.dbKDEntCde.giveFld(cur,
									dbKD.fld_kd_dat_idx09)) + "',";
					hdr += "'"
							+ MyDB.controlFld(Global.dbKDEntCde.giveFld(cur,
									dbKD.fld_kd_dat_idx10)) + "',";
					hdr += "'"
							+ MyDB.controlFld(Global.dbKDEntCde.giveFld(cur,
									dbKD.fld_kd_dat_data01)) + "',";
					hdr += "'"
							+ MyDB.controlFld(Global.dbKDEntCde.giveFld(cur,
									dbKD.fld_kd_dat_data02)) + "',";
					hdr += "'"
							+ MyDB.controlFld(Global.dbKDEntCde.giveFld(cur,
									dbKD.fld_kd_dat_data03)) + "',";
					hdr += "'"
							+ MyDB.controlFld(Global.dbKDEntCde.giveFld(cur,
									dbKD.fld_kd_dat_data04)) + "',";
					hdr += "'"
							+ MyDB.controlFld(Global.dbKDEntCde.giveFld(cur,
									dbKD.fld_kd_dat_data05)) + "',";
					hdr += "'"
							+ MyDB.controlFld(Global.dbKDEntCde.giveFld(cur,
									dbKD.fld_kd_dat_data06)) + "',";
					hdr += "'"
							+ MyDB.controlFld(Global.dbKDEntCde.giveFld(cur,
									dbKD.fld_kd_dat_data07)) + "',";
					hdr += "'"
							+ MyDB.controlFld(Global.dbKDEntCde.giveFld(cur,
									dbKD.fld_kd_dat_data08)) + "',";
					hdr += "'"
							+ MyDB.controlFld(Global.dbKDEntCde.giveFld(cur,
									dbKD.fld_kd_dat_data09)) + "',";
					hdr += "'"
							+ MyDB.controlFld(Global.dbKDEntCde.giveFld(cur,
									dbKD.fld_kd_dat_data10)) + "',";
					hdr += "'"
							+ MyDB.controlFld(Global.dbKDEntCde.giveFld(cur,
									dbKD.fld_kd_dat_data11)) + "',";
					hdr += "'"
							+ MyDB.controlFld(Global.dbKDEntCde.giveFld(cur,
									dbKD.fld_kd_dat_data12)) + "',";
					hdr += "'"
							+ MyDB.controlFld(Global.dbKDEntCde.giveFld(cur,
									dbKD.fld_kd_dat_data13)) + "',";
					hdr += "'"
							+ MyDB.controlFld(Global.dbKDEntCde.giveFld(cur,
									dbKD.fld_kd_dat_data14)) + "',";
					hdr += "'"
							+ MyDB.controlFld(Global.dbKDEntCde.giveFld(cur,
									dbKD.fld_kd_dat_data15)) + "',";
					hdr += "'"
							+ MyDB.controlFld(Global.dbKDEntCde.giveFld(cur,
									dbKD.fld_kd_dat_data16)) + "',";
					hdr += "'"
							+ MyDB.controlFld(Global.dbKDEntCde.giveFld(cur,
									dbKD.fld_kd_dat_data17)) + "',";
					hdr += "'"
							+ MyDB.controlFld(Global.dbKDEntCde.giveFld(cur,
									dbKD.fld_kd_dat_data18)) + "',";
					hdr += "'"
							+ MyDB.controlFld(Global.dbKDEntCde.giveFld(cur,
									dbKD.fld_kd_dat_data19)) + "',";
					hdr += "'"
							+ MyDB.controlFld(Global.dbKDEntCde.giveFld(cur,
									dbKD.fld_kd_dat_data20)) + "',";
					hdr += "'"
							+ MyDB.controlFld(Global.dbKDEntCde.giveFld(cur,
									dbKD.fld_kd_dat_data21)) + "',";
					hdr += "'"
							+ MyDB.controlFld(Global.dbKDEntCde.giveFld(cur,
									dbKD.fld_kd_dat_data22)) + "',";
					hdr += "'"
							+ MyDB.controlFld(Global.dbKDEntCde.giveFld(cur,
									dbKD.fld_kd_dat_data23)) + "',";
					hdr += "'"
							+ MyDB.controlFld(Global.dbKDEntCde.giveFld(cur,
									dbKD.fld_kd_dat_data24)) + "',";
					hdr += "'"
							+ MyDB.controlFld(Global.dbKDEntCde.giveFld(cur,
									dbKD.fld_kd_dat_data25)) + "',";
					hdr += "'"
							+ MyDB.controlFld(Global.dbKDEntCde.giveFld(cur,
									dbKD.fld_kd_dat_data26)) + "',";
					hdr += "'"
							+ MyDB.controlFld(Global.dbKDEntCde.giveFld(cur,
									dbKD.fld_kd_dat_data27)) + "',";
					hdr += "'"
							+ MyDB.controlFld(Global.dbKDEntCde.giveFld(cur,
									dbKD.fld_kd_dat_data28)) + "',";
					hdr += "'"
							+ MyDB.controlFld(Global.dbKDEntCde.giveFld(cur,
									dbKD.fld_kd_dat_data29)) + "',";
					hdr += "'"
							+ MyDB.controlFld(Global.dbKDEntCde.giveFld(cur,
									dbKD.fld_kd_dat_data30)) + "',";
					hdr += "'"
							+ MyDB.controlFld(Global.dbKDEntCde.giveFld(cur,
									dbKD.fld_kd_val_data31)) + "',";
					hdr += "'"
							+ MyDB.controlFld(Global.dbKDEntCde.giveFld(cur,
									dbKD.fld_kd_val_data32)) + "',";
					hdr += "'"
							+ MyDB.controlFld(Global.dbKDEntCde.giveFld(cur,
									dbKD.fld_kd_val_data33)) + "',";
					hdr += "'"
							+ MyDB.controlFld(Global.dbKDEntCde.giveFld(cur,
									dbKD.fld_kd_val_data34)) + "',";
					hdr += "'"
							+ MyDB.controlFld(Global.dbKDEntCde.giveFld(cur,
									dbKD.fld_kd_val_data35)) + "',";
					hdr += "'"
							+ MyDB.controlFld(Global.dbKDEntCde.giveFld(cur,
									dbKD.fld_kd_val_data36)) + "',";
					hdr += "'"
							+ MyDB.controlFld(Global.dbKDEntCde.giveFld(cur,
									dbKD.fld_kd_val_data37)) + "',";
					hdr += "'"
							+ MyDB.controlFld(Global.dbKDEntCde.giveFld(cur,
									dbKD.fld_kd_val_data38)) + "',";
					hdr += "'"
							+ MyDB.controlFld(Global.dbKDEntCde.giveFld(cur,
									dbKD.fld_kd_val_data39)) + "')";

					if(!hdr.equals(""))
						Global.dbParam.getDB().conn.execSQL(queryInsert + hdr);
						//Global.dbParam.getDB().conn.execSQL(queryInsert + hdr, err1);
					
				}
				if (cur!=null)
					cur.close();//MV 26/03/2015
			
				
				
				
			}

		} catch (Exception ex) {
			return "";
		}

		return stReturnOK;
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
				Cursor cur= Global.dbParam.getDB().conn.rawQuery(queryHdr,null);
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
					hdr+=context.getString(R.string.separateur_morpion)+"(";
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
					String resultWS=MyWS.WSSend(user,pwd,"UpdateSrvTableHdrNonSec","UPDATE",queryInsert+hdr);
					//si le resultat est ok on efface la cde
					if (resultWS.equals(""))
		  			{
						Global.dbClient.deFlagProspects();
	
					}
					else
					{
						nbrerr++;
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

			Cursor cur=Global.dbParam.getDB().conn.rawQuery(query,null);
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
									//dbProspect.FIELD_CLI_COULEURSAV+"='"+ 	MyDB.controlFld(cli.TYPESAV.trim())  +"',"+
									//dbProspect.FIELD_CLI_CODEMODERGLMT+"='"+ 	MyDB.controlFld(cli.MODEREGLEMENT.trim())  +"',"+
									//dbProspect.FIELD_CLI_TYPEETABL+"='"+ 	MyDB.controlFld(cli.TYPEETABLISSEMENT.trim())  +"',"+								
									//dbProspect.FIELD_CLI_CODECIRCUIT+"='"+ 	MyDB.controlFld(cli.CIRCUIT.trim())  +"',"+								
									
									dbProspect.FIELD_CLI_LAT+"='"+ 			MyDB.controlFld(cli.LAT.trim())  +"',"+
									dbProspect.FIELD_CLI_LON+"='"+ 			MyDB.controlFld(cli.LON.trim())  +"',"+
									dbProspect.FIELD_CLI_FLAG+"='"+			Global.dbClient.FLAG_UPDATE+"'"+
									" where "+
									dbProspect.FIELD_CLI_CODECLIENT+"='"+cli.CODE+"'";
							 
				
					 
				
					completeString+=update+context.getString(R.string.separateur_morpion);
				}
				

				StringBuffer buferr=new StringBuffer();
				String resultWS=MyWS.WSSend(user,pwd,"UpdateSrvTableNonSec","UPDATE",completeString);
				//si le resultat est ok on efface la cde
				if (resultWS.equals(""))
	  			{
					Global.dbClient.deFlagProspects();

				}
				else
				{
					nbrerr++;
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
				Cursor cur=Global.dbParam.getDB().conn.rawQuery(queryHdr,null);
				while (cur.moveToNext())
				{
					
					bFind=true;
					hdr+=context.getString(R.string.separateur_morpion)+"(";
					hdr+="'"+MyDB.controlFld(Global.dbLog.giveFld(cur, Global.dbLog.FLD_LOG_DATE  		 ))+"',";
					hdr+="'"+Global.curver+"',";
					hdr+="'"+MyDB.controlFld(Global.dbLog.giveFld(cur, Global.dbLog.FLD_LOG_ECRAN  		))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbLog.giveFld(cur, Global.dbLog.FLD_LOG_FONCTION  	))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbLog.giveFld(cur, Global.dbLog.FLD_LOG_PARAMS  	 ))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbLog.giveFld(cur, Global.dbLog.FLD_LOG_MESSAGE  	))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbLog.giveFld(cur, Global.dbLog.FLD_LOG_EXCEPTION   ))+"',";
					hdr+="'"+MyDB.controlFld(Global.dbLog.giveFld(cur, Global.dbLog.FLD_LOG_COMPLEMENT ))+"',";
					hdr+="'"+user+"')";
					


				}
				if (bFind)
				{
					StringBuffer buferr=new StringBuffer();
					String resultWS=MyWS.WSSend(user,pwd,"UpdateSrvTableHdrNonSec","UPDATE",queryInsert+hdr);
					//si le resultat est ok on efface la cde
					if (resultWS.equals(""))
		  			{
						Global.dbLog.Clear(new StringBuilder());
	
					}
					else
					{
						nbrerr++;
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
				Cursor cur=Global.dbParam.getDB().conn.rawQuery(queryHdr,null);
				while (cur.moveToNext())
				{
					
					
					bFind=true;
					hdr+=context.getString(R.string.separateur_morpion)+"(";
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
					String resultWS=MyWS.WSSend(user,pwd,"UpdateSrvTableHdrNonSec","UPDATE",queryInsert+hdr);
					//si le resultat est ok on efface la cde
					if (resultWS.equals(""))
					{
						Global.dbContactcli.reset(Global.dbContactcli.CONTACT_CREATION);

					}
					else
					{
						nbrerr++;
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
					"='"+ Global.dbContactcli.CONTACT_MODIFICATION+ "' ) or "	+
					" ( "+
					Global.dbContactcli.FIELD_FLAG +
					"='"+ Global.dbContactcli.CONTACT_SUPPRESSION+ "' ) "	;

			Cursor cur=Global.dbParam.getDB().conn.rawQuery(query,null);
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
									Global.dbContactcli.FIELD_SRV_FLAG+"='"+ 	MyDB.controlFld(cli.FLAG.trim())  +"' "+
									
																			" where "+
									Global.dbContactcli.FIELD_SRV_CODECLIENT+"='"+cli.CODECLIENT+"' and "+Global.dbContactcli.FIELD_SRV_CODECONTACT+"='"+cli.CODECONTACT+"' ";

					completeString+=update+context.getString(R.string.separateur_morpion);

				}


				StringBuffer buferr=new StringBuffer();
				String resultWS=MyWS.WSSend(user,pwd,"UpdateSrvTableNonSec","UPDATE",completeString);
				//si le resultat est ok on efface la cde
				if (resultWS.equals(""))
				{

					Global.dbContactcli.reset(Global.dbContactcli.CONTACT_MODIFICATION);
					Global.dbContactcli.reset(Global.dbContactcli.CONTACT_SUPPRESSION);
				}
				else
				{
					nbrerr++;
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
		
	 
	/**
	 * on envoi les donn�es synchronisable. cad lesdonn�es qui montent vers
	 * le serveur et qui redescendent tout de suite apres pour que la synchro
	 * soit compl�te
	 * 
	 * @return
	 */

	
	
	  public boolean getSynchroDataForWs(int KDType, String filterFlds,
			String filter, String user, String pwd, String scenario ) {
		try {
			// la requete d'insertion du KD
			String queryInsert = dbKD.KD_INSERT_STRING;
			// si on est sur un identifiant de test (qui contient test) on
			// change la tabgle cible
			String ident = user;
			ident = ident.toLowerCase();
			queryInsert = queryInsert.replace(dbKD.TABLENAME,
					dbKD.TABLENAME_SYNC);
			/*
			 * if (ident.contains("test")) {
			 * queryInsert=queryInsert.replace(dbKD.TABLENAME,
			 * dbKD.TABLENAME_TEST); }
			 */


			// on cherche les entetes de commandes
			String queryHdr = "SELECT * FROM " + dbKD.TABLENAME + " where "
					+ " ( ( " + dbKD.fld_kd_dat_type + "=" + KDType + " AND "
					+ filter + "))";

			// dbKD.fld_kd_dat_data15+
			// "='"+1+"' ))";

			String hdr = "";
			Cursor cur = Global.dbParam.getDB().conn.rawQuery(queryHdr, null);
			while (cur.moveToNext()) {

				hdr += context.getString(R.string.separateur_morpion) + "(";
				hdr += "'"
						+ MyDB.controlFld(Global.dbKDEntCde.giveFld(cur,
								dbKD.fld_kd_soc_code)) + "',";
				hdr += "'"
						+ MyDB.controlFld(Global.dbKDEntCde.giveFld(cur,
								dbKD.fld_kd_cli_code)) + "',";
				hdr += "'"
						+ MyDB.controlFld(Global.dbKDEntCde.giveFld(cur,
								dbKD.fld_kd_pro_code)) + "',";
				hdr += "'"
						+ MyDB.controlFld(Global.dbKDEntCde.giveFld(cur,
								dbKD.fld_kd_vis_code)) + "',";
				hdr += "'"
						+ MyDB.controlFld(Global.dbKDEntCde.giveFld(cur,
								dbKD.fld_kd_cde_code)) + "',";
				hdr += "'"
						+ MyDB.controlFld(Global.dbKDEntCde.giveFld(cur,
								dbKD.fld_kd_dat_type)) + "',";
				hdr += "'" + user + "',";
				hdr += "'"
						+ MyDB.controlFld(Global.dbKDEntCde.giveFld(cur,
								dbKD.fld_kd_dat_idx02)) + "',";
				hdr += "'"
						+ MyDB.controlFld(Global.dbKDEntCde.giveFld(cur,
								dbKD.fld_kd_dat_idx03)) + "',";
				hdr += "'"
						+ MyDB.controlFld(Global.dbKDEntCde.giveFld(cur,
								dbKD.fld_kd_dat_idx04)) + "',";
				hdr += "'"
						+ MyDB.controlFld(Global.dbKDEntCde.giveFld(cur,
								dbKD.fld_kd_dat_idx05)) + "',";
				hdr += "'"
						+ MyDB.controlFld(Global.dbKDEntCde.giveFld(cur,
								dbKD.fld_kd_dat_idx06)) + "',";
				hdr += "'"
						+ MyDB.controlFld(Global.dbKDEntCde.giveFld(cur,
								dbKD.fld_kd_dat_idx07)) + "',";
				hdr += "'"
						+ MyDB.controlFld(Global.dbKDEntCde.giveFld(cur,
								dbKD.fld_kd_dat_idx08)) + "',";
				hdr += "'"
						+ MyDB.controlFld(Global.dbKDEntCde.giveFld(cur,
								dbKD.fld_kd_dat_idx09)) + "',";
				hdr += "'"
						+ MyDB.controlFld(Global.dbKDEntCde.giveFld(cur,
								dbKD.fld_kd_dat_idx10)) + "',";
				hdr += "'"
						+ MyDB.controlFld(Global.dbKDEntCde.giveFld(cur,
								dbKD.fld_kd_dat_data01)) + "',";
				hdr += "'"
						+ MyDB.controlFld(Global.dbKDEntCde.giveFld(cur,
								dbKD.fld_kd_dat_data02)) + "',";
				hdr += "'"
						+ MyDB.controlFld(Global.dbKDEntCde.giveFld(cur,
								dbKD.fld_kd_dat_data03)) + "',";
				hdr += "'"
						+ MyDB.controlFld(Global.dbKDEntCde.giveFld(cur,
								dbKD.fld_kd_dat_data04)) + "',";
				hdr += "'"
						+ MyDB.controlFld(Global.dbKDEntCde.giveFld(cur,
								dbKD.fld_kd_dat_data05)) + "',";
				hdr += "'"
						+ MyDB.controlFld(Global.dbKDEntCde.giveFld(cur,
								dbKD.fld_kd_dat_data06)) + "',";
				hdr += "'"
						+ MyDB.controlFld(Global.dbKDEntCde.giveFld(cur,
								dbKD.fld_kd_dat_data07)) + "',";
				hdr += "'"
						+ MyDB.controlFld(Global.dbKDEntCde.giveFld(cur,
								dbKD.fld_kd_dat_data08)) + "',";
				hdr += "'"
						+ MyDB.controlFld(Global.dbKDEntCde.giveFld(cur,
								dbKD.fld_kd_dat_data09)) + "',";
				hdr += "'"
						+ MyDB.controlFld(Global.dbKDEntCde.giveFld(cur,
								dbKD.fld_kd_dat_data10)) + "',";
				hdr += "'"
						+ MyDB.controlFld(Global.dbKDEntCde.giveFld(cur,
								dbKD.fld_kd_dat_data11)) + "',";
				hdr += "'"
						+ MyDB.controlFld(Global.dbKDEntCde.giveFld(cur,
								dbKD.fld_kd_dat_data12)) + "',";
				hdr += "'"
						+ MyDB.controlFld(Global.dbKDEntCde.giveFld(cur,
								dbKD.fld_kd_dat_data13)) + "',";
				hdr += "'"
						+ MyDB.controlFld(Global.dbKDEntCde.giveFld(cur,
								dbKD.fld_kd_dat_data14)) + "',";
				hdr += "'"
						+ MyDB.controlFld(Global.dbKDEntCde.giveFld(cur,
								dbKD.fld_kd_dat_data15)) + "',";
				hdr += "'"
						+ MyDB.controlFld(Global.dbKDEntCde.giveFld(cur,
								dbKD.fld_kd_dat_data16)) + "',";
				hdr += "'"
						+ MyDB.controlFld(Global.dbKDEntCde.giveFld(cur,
								dbKD.fld_kd_dat_data17)) + "',";
				hdr += "'"
						+ MyDB.controlFld(Global.dbKDEntCde.giveFld(cur,
								dbKD.fld_kd_dat_data18)) + "',";
				hdr += "'"
						+ MyDB.controlFld(Global.dbKDEntCde.giveFld(cur,
								dbKD.fld_kd_dat_data19)) + "',";
				hdr += "'"
						+ MyDB.controlFld(Global.dbKDEntCde.giveFld(cur,
								dbKD.fld_kd_dat_data20)) + "',";
				hdr += "'"
						+ MyDB.controlFld(Global.dbKDEntCde.giveFld(cur,
								dbKD.fld_kd_dat_data21)) + "',";
				hdr += "'"
						+ MyDB.controlFld(Global.dbKDEntCde.giveFld(cur,
								dbKD.fld_kd_dat_data22)) + "',";
				hdr += "'"
						+ MyDB.controlFld(Global.dbKDEntCde.giveFld(cur,
								dbKD.fld_kd_dat_data23)) + "',";
				hdr += "'"
						+ MyDB.controlFld(Global.dbKDEntCde.giveFld(cur,
								dbKD.fld_kd_dat_data24)) + "',";
				hdr += "'"
						+ MyDB.controlFld(Global.dbKDEntCde.giveFld(cur,
								dbKD.fld_kd_dat_data25)) + "',";
				hdr += "'"
						+ MyDB.controlFld(Global.dbKDEntCde.giveFld(cur,
								dbKD.fld_kd_dat_data26)) + "',";
				hdr += "'"
						+ MyDB.controlFld(Global.dbKDEntCde.giveFld(cur,
								dbKD.fld_kd_dat_data27)) + "',";
				hdr += "'"
						+ MyDB.controlFld(Global.dbKDEntCde.giveFld(cur,
								dbKD.fld_kd_dat_data28)) + "',";
				hdr += "'"
						+ MyDB.controlFld(Global.dbKDEntCde.giveFld(cur,
								dbKD.fld_kd_dat_data29)) + "',";
				hdr += "'"
						+ MyDB.controlFld(Global.dbKDEntCde.giveFld(cur,
								dbKD.fld_kd_dat_data30)) + "',";
				hdr += "'"
						+ MyDB.controlFld(Global.dbKDEntCde.giveFld(cur,
								dbKD.fld_kd_val_data31)) + "',";
				hdr += "'"
						+ MyDB.controlFld(Global.dbKDEntCde.giveFld(cur,
								dbKD.fld_kd_val_data32)) + "',";
				hdr += "'"
						+ MyDB.controlFld(Global.dbKDEntCde.giveFld(cur,
								dbKD.fld_kd_val_data33)) + "',";
				hdr += "'"
						+ MyDB.controlFld(Global.dbKDEntCde.giveFld(cur,
								dbKD.fld_kd_val_data34)) + "',";
				hdr += "'"
						+ MyDB.controlFld(Global.dbKDEntCde.giveFld(cur,
								dbKD.fld_kd_val_data35)) + "',";
				hdr += "'"
						+ MyDB.controlFld(Global.dbKDEntCde.giveFld(cur,
								dbKD.fld_kd_val_data36)) + "',";
				hdr += "'"
						+ MyDB.controlFld(Global.dbKDEntCde.giveFld(cur,
								dbKD.fld_kd_val_data37)) + "',";
				hdr += "'"
						+ MyDB.controlFld(Global.dbKDEntCde.giveFld(cur,
								dbKD.fld_kd_val_data38)) + "',";
				hdr += "'"
						+ MyDB.controlFld(Global.dbKDEntCde.giveFld(cur,
								dbKD.fld_kd_val_data39)) + "')";

			}
			cur.close();
			if (!hdr.equals("")) {
				StringBuffer buferr = new StringBuffer();
				String resultWS = MyWS.WSSynchroKD(user, pwd, "synchroKD",
						scenario, queryInsert + hdr, KDType, filterFlds,
						dbKD.TABLENAME);
				// si le resultat est ok on deflag les donn�es � envoyer
				if (resultWS.equals("")) {
					return true;
				} else {
					nbrerr++;
					// FurtiveMessageBox(buferr.toString());
					return false;
				}
			}
			return true;
		} catch (Exception ex) {

		}

		return false;
	}

}
