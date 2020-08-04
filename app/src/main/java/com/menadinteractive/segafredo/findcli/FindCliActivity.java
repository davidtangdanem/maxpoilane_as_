package com.menadinteractive.segafredo.findcli;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.inputmethodservice.Keyboard;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.HideReturnsTransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.menadinteractive.commande.commandeActivity;
import com.menadinteractive.maxpoilane.BaseActivity;
import com.menadinteractive.maxpoilane.R;
import com.menadinteractive.segafredo.communs.Fonctions;
import com.menadinteractive.segafredo.communs.Global;
import com.menadinteractive.segafredo.communs.assoc;
import com.menadinteractive.segafredo.communs.myListView;
import com.menadinteractive.segafredo.db.SQLRequestHelper;
import com.menadinteractive.segafredo.db.TableClient;
import com.menadinteractive.segafredo.db.TableContactcli;
import com.menadinteractive.segafredo.db.dbKD101ClientVu;
import com.menadinteractive.segafredo.db.dbKD451RetourMachineclient;
import com.menadinteractive.segafredo.db.dbKD452ComptageMachineclient;
import com.menadinteractive.segafredo.db.dbKD83EntCde;
import com.menadinteractive.segafredo.db.dbKD981RetourBanqueEnt;
import com.menadinteractive.segafredo.db.dbKD98Encaissement;
import com.menadinteractive.segafredo.db.dbLog;
import com.menadinteractive.segafredo.db.dbKD83EntCde.structEntCde;
import com.menadinteractive.segafredo.tasks.task_sendWSData;



public class FindCliActivity extends BaseActivity implements OnClickListener

{
	ArrayList<Bundle> cli;
	Handler handler;
	ImageButton ibFind;
	EditText etFilter;
	EditText  etFilterTournee;
	TextView tvTitre;
	myListView lv;
	int top = 0;
	int index = 0;
	
	Handler hSync;
	int nbrDataToSend=0;
	ProgressBar pb;
	private ProgressDialog m_ProgressDialog = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);

		 
		setContentView(R.layout.activity_findcli);
		//MyDB.copyFile(MyDB.source,MyDB.dest);
 
		initGUI();
		setListeners();
		hSync=getHandlerSync();
	}

	void initGUI() {
		lv = (myListView) findViewById(R.id.listView1);
		etFilter= (EditText) findViewById(R.id.etFilter);
		etFilterTournee= (EditText) findViewById(R.id.etFilterTournee);
		pb=(ProgressBar)findViewById(R.id.pb1);
		pb.setVisibility(View.GONE);
	
		
		ibFind= (ImageButton) findViewById(R.id.ibFind);
		tvTitre=(TextView) findViewById(R.id.textViewTitre);
		
		Fonctions.setFont(this, tvTitre, Global.FONT_REGULAR);
		
		handler =getHandler();
		PopulateList(R.string.tri_par_tournee);
		
		 etFilter.setOnKeyListener(new OnKeyListener() {

		        @Override
		        public boolean onKey(View v, int keyCode, KeyEvent event) {
		        	
		        	if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN){
		    			PopulateList(R.string.tri_par_tournee);
		                return false;
		            }
		            return false;
		        }
		    });
//		etFilter.setText(Fonctions.GetProfileString(FindCliActivity.this, "lastcli", ""));
//		etFilter.setSelection(0,etFilter.getText().length());
		
	}
	
	void setListeners()
	{
	
		lv.requestFocus();
		ibFind.setOnClickListener(this);
	}
		
	Handler getHandler() {
		Handler h = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				Bundle bGet = msg.getData();
				switch (msg.what) {
				case R.id.listView1:
				case R.id.iv2:
				 
					int  id=bGet.getInt("position");
					String codecli=cli.get(id).getString(Global.dbClient.FIELD_CODE);
					Fonctions.WriteProfileString(FindCliActivity.this, "lastcli", cli.get(id).getString(Global.dbClient.FIELD_NOM));
					
					
					
					
					if( Global.dbKDVisite.isVisiteToday(codecli, Fonctions.getYYYYMMDD())==true)
				 	{
				 		
						MessageYesNo("Un BL a déjà été faite aujourd\'hui pour ce client : "+codecli+" ,voulez-vous en faire un autre ? " ,"Recherche client",codecli);
					}
					else
					{
						//Fonctions.FurtiveMessageBox(FindCliActivity.this, codecli);
						Global.dbLog.Insert("FindCli", "Selection client", "",  "Code client: " + codecli, "", "");
						launchMenuCli(codecli );
							
					}
					
					break;
				 
				}

			}
		};
		return h;
	}
	public void MessageYesNo(String message,String title,final String codeclient) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(message)
		.setCancelable(false)
		.setTitle(title)
		.setPositiveButton(getString(R.string.Yes),
				new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				Global.dbLog.Insert("FindCli", "Selection client 2eme BL", "",  "Code client: " + codeclient, "", "");
				launchMenuCli(codeclient );

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
	
	private void PopulateList(int tri) {
	 
		Log.d("TAG","populate findcli");
		 
		hideKeyb(etFilter);
		hideKeyb(etFilterTournee);
		
		
		String order="";
		String tourneePrincipal="";
		
		if (tri==R.string.tri_par_tournee)
		{
			tourneePrincipal=Fonctions.GetStringDanem(getLogin());
			String jourPassage = SQLRequestHelper.getCodeTournee(Fonctions.getYYYYMMDD());
			if(jourPassage.equals("1"))
			{
				order="  cast("+TableClient.FIELD_LUNDI+ " as Integer) ,"+TableClient.FIELD_NOM+" ";
				
			}
			if(jourPassage.equals("2"))
			{
				order=" cast("+TableClient.FIELD_MARDI+ " as Integer)  ,"+TableClient.FIELD_NOM+" ";
			}
			if(jourPassage.equals("3"))
			{
				order=" cast("+TableClient.FIELD_MERCREDI+ " as Integer) ,"+TableClient.FIELD_NOM+" ";
			}
			if(jourPassage.equals("4"))
			{
				order=" cast("+TableClient.FIELD_JEUDI+ " as Integer)  ,"+TableClient.FIELD_NOM+" ";
			}
			if(jourPassage.equals("5"))
			{
				order=" cast("+TableClient.FIELD_VENDREDI+ " as Integer) ,"+TableClient.FIELD_NOM+" ";
			}
			if(jourPassage.equals("6"))
			{
				order=" cast("+TableClient.FIELD_VENDREDI+ " as Integer) ,"+TableClient.FIELD_NOM+" ";
			}
			if(jourPassage.equals("7"))
			{
				order=" cast("+TableClient.FIELD_VENDREDI+ " as Integer) ,"+TableClient.FIELD_NOM+" ";
			}
			if(jourPassage.equals("0"))
			{
				order=" cast("+TableClient.FIELD_VENDREDI+ " as Integer) ,"+TableClient.FIELD_NOM+" ";
			}
			
			
		}
		if (tri==R.string.tri_par_code_tournée)
			order=TableClient.FIELD_CODEVRP+","+TableClient.FIELD_NOM;
		if (tri==R.string.tri_par_nom_client)
			order=TableClient.FIELD_NOM;	
		if (tri==R.string.tri_par_code_client)
			order=TableClient.FIELD_CODE;
		if (tri==R.string.tri_par_ville)
			order=TableClient.FIELD_VILLE;
		
		try {
			cli=Global.dbClient.getClientsFilters(  etFilter.getText().toString().trim(),order,tourneePrincipal,Fonctions.GetStringDanem(etFilterTournee.getText().toString()));
			ArrayList<assoc> assocs =new ArrayList<assoc>();
 
			assocs.add(new  assoc(0,R.id.tvCode,TableClient.FIELD_CODE,"isbloque"));
			assocs.add(new  assoc(0,R.id.tvNom,TableClient.FIELD_ENSEIGNE));
			assocs.add(new  assoc(0,R.id.tvRS,TableClient.FIELD_NOM));
			assocs.add(new  assoc(0,R.id.tvCP,TableClient.FIELD_CP));
			assocs.add(new  assoc(0,R.id.tvVille,TableClient.FIELD_VILLE));
			assocs.add(new  assoc(1,R.id.iv1,TableClient.FIELD_ICON, TableClient.FIELD_COULEUR));
			assocs.add(new  assoc(1,R.id.iv2,"ishisto"));
			assocs.add(new  assoc(1,R.id.iv3,"isfacdues"));
			assocs.add(new  assoc(1,R.id.iv4,"ismatos"));
			assocs.add(new  assoc(1,R.id.iv5,"isgeocoded"));
			
			lv.attachValues(R.layout.item_list_findcli, cli,assocs,handler);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v==ibFind)
		{
			PopulateList(R.string.tri_par_tournee);
			
			 
		}
		
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	 
			addMenu(menu, R.string.tri_par_tournee,-1);
			addMenu(menu, R.string.tri_par_code_tournée,-1);
			addMenu(menu, R.string.tri_par_nom_client,-1);
			addMenu(menu,R.string.tri_par_code_client,-1);
			addMenu(menu, R.string.tri_par_ville,-1);
			
			return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		
		case R.string.tri_par_tournee: 
		case R.string.tri_par_code_tournée: 
		case R.string.tri_par_nom_client: 
		case R.string.tri_par_code_client: 
		case R.string.tri_par_ville: 
			 PopulateList(item.getItemId());
			 break;
		default:
			return super.onOptionsItemSelected(item);
		}
		return super.onOptionsItemSelected(item);
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode==LAUNCH_FINDCLI)
		{
			PopulateList(R.string.tri_par_tournee);
		}
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
					
				}
				 
			}
		};
		return h;
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

		 
	 
}
