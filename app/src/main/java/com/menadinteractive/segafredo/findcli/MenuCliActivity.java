package com.menadinteractive.segafredo.findcli;

import java.io.File;
import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.menadinteractive.commande.commandeActivity;
import com.menadinteractive.maxpoilane.BaseActivity;
import com.menadinteractive.maxpoilane.R;
import com.menadinteractive.segafredo.carto.CartoMapActivity;
import com.menadinteractive.segafredo.communs.Fonctions;
import com.menadinteractive.segafredo.communs.GPS;
import com.menadinteractive.segafredo.communs.Global;
import com.menadinteractive.segafredo.communs.MyWS;
import com.menadinteractive.segafredo.communs.assoc;
import com.menadinteractive.segafredo.communs.myListView;
import com.menadinteractive.segafredo.db.Preferences;
import com.menadinteractive.segafredo.db.TableClient;
import com.menadinteractive.segafredo.db.TableClient.structClient;
import com.menadinteractive.segafredo.db.TableSouches;
import com.menadinteractive.segafredo.db.TableSouches.passeplat;
import com.menadinteractive.segafredo.db.dbKD729HistoDocuments;
import com.menadinteractive.segafredo.db.dbKD730FacturesDues;
import com.menadinteractive.segafredo.encaissement.ReglementActivity;
import com.menadinteractive.segafredo.materielclient.ListingMaterielClient;
import com.menadinteractive.segafredo.plugins.Espresso;





public class MenuCliActivity extends BaseActivity implements OnClickListener

{
	TextView tvCode,tvEnseigne;
	TextView tvVille;
	TextView tvCP;
	TextView tvMail;
	TextView tvTel;
	TextView tvRS;
	TextView tvAdr1;
	TextView tvAdr2,tvCliBloque,tvEncoursClient;
	structClient cli=null;
	String m_codecli;
	Handler handler;
	myListView lv;
	ArrayList<Bundle> menuitems;
	String m_fromCarto="";
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);

		 
		Bundle bundle = this.getIntent().getExtras();
		m_codecli=getBundleValue(bundle, "codecli");
		m_fromCarto=getBundleValue(bundle, "fromcarto");
		
		setContentView(R.layout.activity_menuclient);
 
		initGUI();
		setListeners();
		
		alertModeTest();
	}

	void initGUI() {
		lv = (myListView) findViewById(R.id.listView1);
		tvMail = (TextView) findViewById(R.id.textViewMail);
		tvRS = (TextView) findViewById(R.id.textViewRS);
		tvCP = (TextView) findViewById(R.id.textViewCP);
		tvTel = (TextView) findViewById(R.id.textViewTel);
		tvVille = (TextView) findViewById(R.id.textViewVille);
		tvAdr1 = (TextView) findViewById(R.id.textViewAdr1);
		tvAdr2 = (TextView) findViewById(R.id.textViewAdr2);
		tvCode = (TextView) findViewById(R.id.textViewCode);
		tvCliBloque = (TextView) findViewById(R.id.tvCliBloque);
		tvEnseigne = (TextView) findViewById(R.id.textViewEnseigne);
		tvEncoursClient = (TextView) findViewById(R.id.tvEncoursClient);
		
		cli=new structClient();
		Global.dbClient.getClient(m_codecli, cli, new StringBuilder());
		
		if (TableClient.isActif(cli)==false)
			tvCliBloque.setVisibility(View.VISIBLE);
		else
			tvCliBloque.setVisibility(View.GONE);
		//si le client n'est pas géocodé on le géocode en arriere plan
		if (cli.LAT==null || cli.LAT.equals(""))
		{
			String adress =cli.ADR1+" "+cli.ADR2+" "+cli.CP+" "+cli.VILLE+" "+cli.PAYS;
	
			geocode(adress);
		}
		
		tvRS.setText(cli.NOM.trim());
		tvMail.setText(cli.EMAIL);
		tvCP.setText(cli.CP);
		tvTel.setText(cli.TEL1);
		tvVille.setText(cli.VILLE);
		tvCode.setText("Code client : "+cli.CODE);
		tvEnseigne.setText(cli.ENSEIGNE.trim());
		tvAdr1.setText(cli.ADR1);
		tvAdr2.setText(cli.ADR2);
 
		lv = (myListView) findViewById(R.id.listView1);
		
		//on affiche l'encours client
		structClient client = new structClient();
		Global.dbClient.getClient(m_codecli, client, new StringBuilder());

		float totalEncours = Fonctions.GetStringToFloatDanem(client.MONTANTTOTALENCOURS);
		tvEncoursClient.setText(Fonctions.GetFloatToStringFormatDanem(totalEncours, "0.00"));
		tvEncoursClient.setVisibility(View.INVISIBLE);
		
		handler=getHandler();
		PopulateList();
	}
	
	void setListeners()
	{
	
		
		 
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}
		
	Bundle createItem(String lbl,String icon,String lbl2)
	{
		Bundle item=new Bundle();
		item.putString("lbl", lbl);
		item.putString("icon",icon);
		item.putString("lbl2",lbl2);
		return item;
	}
	private void PopulateList() {
		
		menuitems=new ArrayList<Bundle>();
		
		TableSouches souche=new TableSouches(m_appState.m_db,this);
		
		passeplat pp=new passeplat();
		if (souche.get(souche.TYPEDOC_FACTURE,getLogin(), pp) && TableClient.isActif(cli))
		{
			menuitems.add(createItem(getString(R.string.Facture), "facture",pp.NUMSOUCHE_COURANT));
		}
		if (souche.get(souche.TYPEDOC_BL,getLogin(), pp) && TableClient.isActif(cli))
		{
			menuitems.add(createItem(getString(R.string.BL), "bl",pp.NUMSOUCHE_COURANT) );
		}
		if (souche.get(souche.TYPEDOC_AVOIR,getLogin(), pp) && TableClient.isActif(cli))
		{
			menuitems.add(createItem(getString(R.string.Avoir), "avoir",pp.NUMSOUCHE_COURANT));
		}
		if (souche.get(souche.TYPEDOC_RETOUR,getLogin(),pp))
		{
			menuitems.add(createItem(getString(R.string.Retour), "retour",pp.NUMSOUCHE_COURANT));
		}
		
		//dbKD730FacturesDues dues=new dbKD730FacturesDues(m_appState.m_db);
		//if (dues.Count(m_codecli)>0)
		if (souche.get(souche.TYPEDOC_REGLEMENT,getLogin(),pp))
		{
			menuitems.add(createItem(getString(R.string.commande_reglement), "basic2_018_money_coins",pp.NUMSOUCHE_COURANT));
		}
		
		menuitems.add(createItem(getString(R.string.ficheclient), "basic2_105_user",""));
		
	
		dbKD729HistoDocuments hist=new dbKD729HistoDocuments(m_appState.m_db);
		if (hist.Count(m_codecli)>0)
			menuitems.add(createItem(getString(R.string.stats), "basic1_104_database_download",""));
		
		//if (m_fromCarto.equals("1")==false)
		//	menuitems.add(createItem(getString(R.string.Carto), "basic_2057_map",""));
		//menuitems.add(createItem(getString(R.string.contact), "contact",""));
		//menuitems.add(createItem(getString(R.string.rapportactivite), "rapport_de_visites",""));
		
		if (souche.get(souche.TYPEDOC_RETOURMACHINE,getLogin(),pp))
		{
			menuitems.add(createItem(getString(R.string.listemateriel), "basic2_292_tools_settings",pp.NUMSOUCHE_COURANT));
		}
		
 		ArrayList<assoc> assocs =new ArrayList<assoc>();
	 
		assocs.add(new  assoc(0,R.id.toptext,"lbl"));
		assocs.add(new  assoc(1,R.id.icon,"icon"));
		assocs.add(new  assoc(0,R.id.tvnum,"lbl2",true));
		
		lv.attachValues(R.layout.item_list_menuclient, menuitems,assocs,handler);
	}

	Handler getHandler() {
		Handler h = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				Bundle bGet = msg.getData();
				switch (msg.what) {
				case R.id.listView1:
				case R.id.icon:
				 
					int  id=bGet.getInt("position");
					String menu=menuitems.get(id).getString("lbl");
					if (menu.equals(getString(R.string.Carto)))
					{
						go_carto();
					}
					if (menu.equals(getString(R.string.ficheclient)))
					{	
						
						
						commandeActivity.launchFiche(MenuCliActivity.this,m_codecli,LAUNCH_FICHECLI);
					}
					 
					if (menu.equals(getString(R.string.stats)))
					{		
						launchStats(m_codecli, cli.SOC_CODE);
				 	}
					if (menu.equals(getString(R.string.Facture)))
					{					 
						
						if( Fonctions.GetStringDanem(cli.EMAIL).equals(""))
					 	{
					 		
							MessageYesNoNotEmal("Ce client n\'a pas d\'email, vous ne pouvez pas faire de BL." ,"BL",m_codecli);
						}
						else
						{
							launchCde(MenuCliActivity.this,TableSouches.TYPEDOC_FACTURE, m_codecli,cli.SOC_CODE);
								
						}
					}
					if (menu.equals(getString(R.string.BL)))
					{	
						launchCde(MenuCliActivity.this,TableSouches.TYPEDOC_BL, m_codecli,cli.SOC_CODE);
						
					}
					if (menu.equals(getString(R.string.Avoir)))
					{					 
						launchCde(MenuCliActivity.this,TableSouches.TYPEDOC_AVOIR, m_codecli,cli.SOC_CODE);
					}
					if (menu.equals(getString(R.string.Retour)))
					{					 
						launchCde(MenuCliActivity.this,TableSouches.TYPEDOC_RETOUR, m_codecli,cli.SOC_CODE);
					}
					if (menu.equals(getString(R.string.commande_reglement)))
					{					 
						launchReglement(m_codecli, cli.SOC_CODE);
					}
					
					
					if (menu.equals(getString(R.string.contact)))
					{					 
						commandeActivity.launchContact(MenuCliActivity.this,m_codecli,LAUNCH_CONTACT);
						
					}
					
					if (menu.equals(getString(R.string.rapportactivite)))
					{					 
						commandeActivity.launchRapport(MenuCliActivity.this,m_codecli,LAUNCH_RAPPORT);
						
					}
					if (menu.equals(getString(R.string.listemateriel)))
					{					 
						launchMaterielClient(m_codecli,LAUNCH_MATERIELCLI);
						
					}
					
					
					
					break;
				 
				}

			}
		};
		return h;
	}
	public void MessageYesNoNotEmal(String message,String title,final String codeclient) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(message)
		.setCancelable(false)
		.setTitle(title)
		.setPositiveButton(getString(android.R.string.ok),
				new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {

			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}
	void go_carto()
	{
		
//		if (controlIsInventaireEnCours()==true) return;
		
//		if(controlReapproDechargementEnCours()==true)
//			return;
		
		String login=Preferences.getValue(this, Espresso.LOGIN, "0");
		String socCode=Preferences.getValue(this, Espresso.CODE_SOCIETE, "0");
		String password=Preferences.getValue(this, Espresso.PASSWORD, "0");
		String ws_url="";
		String timerGps="30";
		String timerSync="300";
		String timerUpdateUI="60";
		
 
		Bundle b = new Bundle();
		b.putString(Espresso.LOGIN, login);
		b.putString(Espresso.CODE_SOCIETE, socCode);
		b.putString(Espresso.PASSWORD, password);
		b.putString(Espresso.WS_URL, ws_url);
		b.putString(Espresso.TIMER_MONITORING_GPS_SECONDS, timerGps);
		b.putString(Espresso.TIMER_SYNCHRO_SECONDS, timerSync);
		b.putString(Espresso.TIMER_UPDATE_PI_SECONDS, timerUpdateUI);
		b.putString(Espresso.CODE_CLIENT, m_codecli);
		Intent intent = new Intent(this,			CartoMapActivity.class);
		intent.putExtras(b);
		startActivityForResult(intent, 0);
		
	}
	
	void launchReglement(String codeCli, String codeSoc){
		Intent intent = new Intent(this, ReglementActivity.class);
		Bundle b = new Bundle();
		b.putString(Espresso.CODE_CLIENT, codeCli);
		b.putString(Espresso.CODE_SOCIETE, codeSoc);
		intent.putExtras(b);
		startActivityForResult(intent, 0);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		
		if(requestCode==2100)
		{
		 onBackPressed();
		 //finish();
			//Intent intent = new Intent(this,			FindCliActivity.class);

		//	startActivityForResult(intent, 0);
		}
		if(requestCode==20000)
		{
			String PATH = Environment.getExternalStorageDirectory() +File.separator+"download/pbarrondi.pdf";
			File file = new File(PATH);
			
			if ( PATH.contains("pdf") || PATH.contains("PDF"))
            {
                Uri path = Uri.fromFile(file);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(path, "application/pdf");
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); 

                try {
                startActivity(intent);
                } 
                catch (Exception e) {
                
                }
            	
            }
			
		}
			alertModeTest();
			PopulateList();
		}
	
	void geocode(final String adress)
	{
		try {
			new Thread() {

				@Override
				public void run() {
					Log.d("TAG", "geocode : "+adress);
					
					Looper myLooper = Looper.myLooper();
					Looper.prepare();
				 
					String adr=adress;
					GPS gps = new GPS( MenuCliActivity.this, adr);
					//initDialogCancel(gps);
					if(gps.isNetworkEnabled()){
						if(gps.canGetLocation()){
			 
							Log.d("TAG", Double.toString(gps.getLatitude()) +" , "+ Double.toString(gps.getLongitude() ));
					//		Fonctions.FurtiveMessageBox(this,Double.toString(gps.getLatitude()) +" , "+ Double.toString(gps.getLongitude() ));
							TableClient cli=new TableClient(m_appState.m_db);
							cli.updateLatLon(Double.toString(gps.getLatitude()) ,Double.toString(gps.getLongitude()),m_codecli    );
							
							gps.stopUsingGPS();
						}else{
							//sendResultFalse();
						}
					} 	
					
					Looper.loop();

				}
			}.start();
 
		} catch (Exception ex) {
			Log.e("tag",ex.getLocalizedMessage());
		}
		
	}
	
	
}
