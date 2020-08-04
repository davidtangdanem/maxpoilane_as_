package com.menadinteractive.segafredo.materielclient;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

import com.menadinteractive.maxpoilane.BaseActivity;
import com.menadinteractive.printmodels.BluetoothConnectionInsecureExample;
import com.menadinteractive.printmodels.Z420ModelPretMachine;
import com.menadinteractive.printmodels.Z420ModelRetourMachine;
import com.menadinteractive.maxpoilane.R;
import com.menadinteractive.segafredo.carto.CartoMapActivity;
import com.menadinteractive.segafredo.communs.Fonctions;
import com.menadinteractive.segafredo.communs.Global;
import com.menadinteractive.segafredo.communs.assoc;
import com.menadinteractive.segafredo.communs.myListView;
import com.menadinteractive.segafredo.db.TableMaterielClient;
import com.menadinteractive.segafredo.db.TableSouches;
import com.menadinteractive.segafredo.db.dbKD451RetourMachineclient;
import com.menadinteractive.segafredo.db.dbKD452ComptageMachineclient;
import com.menadinteractive.segafredo.db.TableClient.structClient;

public class ListingMaterielClient extends BaseActivity implements OnClickListener{
	
	ArrayList<Bundle> cli;
	ArrayList<Bundle> pret;
	
	Handler handler;
	ImageButton ibPrintRetours,ibPrintPrets,ibAddPret;
 
	myListView lv;;
	myListView lv2;;
	Handler hPrintResultRet,hPrintResultPret;
	private ProgressDialog m_ProgressDialog = null; 
	boolean m_problemPrinter=false;//si on declare un probleme d'imprimante on laisse passer

	String m_codeclient;
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_materielclient);		
		Bundle bundle = this.getIntent().getExtras();
		m_codeclient = bundle.getString("codeclient");
		initGUI();
		setListeners();
	}

	void initGUI() {
		lv = (myListView) findViewById(R.id.listView1);
		lv2 = (myListView) findViewById(R.id.listView2);
		
 		ibPrintRetours= (ImageButton) findViewById(R.id.ibPrintRetours);
 		ibPrintPrets= (ImageButton) findViewById(R.id.ibPrintPrets);
 		ibAddPret= (ImageButton) findViewById(R.id.ibAddPrets);
 		
		hPrintResultRet=getHandlerPrintRet();
		hPrintResultPret=getHandlerPrintPret();
		handler =getHandler();
		
		DispInfoCli();
		
		PopulateList();
		PopulateListPret();
	}
	
	void setListeners()
	{
		ibPrintRetours.setOnClickListener(this);
		ibPrintPrets.setOnClickListener(this);
		ibAddPret.setOnClickListener(this);
		
	}
	Handler getHandlerPrintRet() {
		Handler h = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (m_ProgressDialog!=null) m_ProgressDialog.dismiss();
				super.handleMessage(msg);
				Bundle bGet = msg.getData();
				if (msg.what!=BluetoothConnectionInsecureExample.ERRORMSG_OK)
				{
				//	promptText("Problème d'impression",BluetoothConnectionInsecureExample.getErrMsg(msg.what), false);
					MessageYesNoRetour(BluetoothConnectionInsecureExample.getErrMsg(msg.what)+"\n\nAvez vous un problème bloquant qui vous empèche d'imprimer ?", 433, getString(R.string.probl_me_d_impression));
					
				}
				else
				{
					
					setPrintRetourOk();
					//finish();
				}
			}
		};
		return h;
	}
	Handler getHandlerPrintPret() {
		Handler h = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (m_ProgressDialog!=null) m_ProgressDialog.dismiss();
				super.handleMessage(msg);
				Bundle bGet = msg.getData();
				if (msg.what!=BluetoothConnectionInsecureExample.ERRORMSG_OK)
				{
				//	promptText("Problème d'impression",BluetoothConnectionInsecureExample.getErrMsg(msg.what), false);
					MessageYesNoPret(BluetoothConnectionInsecureExample.getErrMsg(msg.what)+"\n\nAvez vous un problème bloquant qui vous empèche d'imprimer ?", 433, getString(R.string.probl_me_d_impression));
					
				}
				else
				{
					
					setPrintPretOk();
					//finish();
				}
			}
		};
		return h;
	}
	void setPrintRetourOk()
	{
		TableSouches souche=new TableSouches(m_appState.m_db,this);
		String newsouche=souche.get(TableSouches.TYPEDOC_RETOURMACHINE, getLogin());
 
		
		dbKD451RetourMachineclient ret=new dbKD451RetourMachineclient(m_appState.m_db);
		
		if (ret.isPrintOk(m_codeclient,newsouche)==false)
		{
			ret.setPrint( m_codeclient,newsouche);
			souche.incNum(getLogin(), TableSouches.TYPEDOC_RETOURMACHINE);
		}
		structClient cli=new structClient();
		cli.CODE=m_codeclient;
		CartoMapActivity.updateVisite(cli, true ,this);
	}
	void setPrintPretOk()
	{
		TableSouches souche=new TableSouches(m_appState.m_db,this);
		String newsouche=souche.get(TableSouches.TYPEDOC_PRETMACHINE, getLogin());
 
		
		dbKD452ComptageMachineclient ret=new dbKD452ComptageMachineclient(m_appState.m_db);
		
		if (ret.isPrintOk(m_codeclient,newsouche)==false)
		{
			ret.setPrint( m_codeclient,newsouche);
			souche.incNum(getLogin(), TableSouches.TYPEDOC_PRETMACHINE );
		}
		structClient cli=new structClient();
		cli.CODE=m_codeclient;
		CartoMapActivity.updateVisite(cli, true ,this);
	}
	public void MessageYesNoRetour(String message, int type,String title) {
		final int m_type = type;
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(message)
		.setCancelable(false)
		.setTitle(title)
		.setPositiveButton(getString(R.string.Yes),
				new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				switch (m_type) {
				case 433://probleme d'imprimante
					m_problemPrinter=true;
					promptText(getString(R.string.probl_me_d_impression),
							getString(R.string.vous_pouvez_valider_le_document_sans_imprimer), 
								false);
					
					setPrintRetourOk();
					break;
				  
				}

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
	public void MessageYesNoPret(String message, int type,String title) {
		final int m_type = type;
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(message)
		.setCancelable(false)
		.setTitle(title)
		.setPositiveButton(getString(R.string.Yes),
				new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				switch (m_type) {
				case 433://probleme d'imprimante
					m_problemPrinter=true;
					promptText(getString(R.string.probl_me_d_impression),
							getString(R.string.vous_pouvez_valider_le_document_sans_imprimer), 
								false);
					
					setPrintPretOk();
					break;
				  
				}

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
					String numSZ=cli.get(id).getString(TableMaterielClient.FIELD_PLAQUESZ);
					String numserie=cli.get(id).getString(TableMaterielClient.FIELD_NUMSERIEFAB);
					String codemateriel=cli.get(id).getString(TableMaterielClient.FIELD_CODEART);
					String designation=cli.get(id).getString(TableMaterielClient.FIELD_LIBART);
					
					TableSouches souche=new TableSouches(m_appState.m_db,ListingMaterielClient.this);
					if (souche.get(TableSouches.TYPEDOC_RETOURMACHINE, getLogin()).equals(""))
					{
						promptText(getString(R.string.retour_mat_riel), getString(R.string.vous_n_tes_pas_autoris_faire_des_retours_mat_riel), false);
						return;
					}
					
					dbKD451RetourMachineclient ret=new dbKD451RetourMachineclient(m_appState.m_db);
					if (ret.getPieceNotSend(m_codeclient)!=null)
					{
						promptText(getString(R.string.nouveau_retour_impossible), getString(R.string.vous_avez_d_j_un_retour_envoyer_avant_d_en_faire_un_nouveau), false);
						return;
					}
					
					launchMenuCommentaireRetour(m_codeclient,numSZ,numserie,codemateriel,designation);
				
					break;
					
				case R.id.listView2:
					
					int  id2=bGet.getInt("position");
					String numSZ_pret=pret.get(id2).getString(Global.dbKDComptageMachineClient.FIELD_CMC_NUMSERIESZ);
					String numserie_pret=pret.get(id2).getString(Global.dbKDComptageMachineClient.FIELD_CMC_NUMSERIEFABRICANT);
					String codearticle_pret=pret.get(id2).getString(Global.dbKDComptageMachineClient.FIELD_CMC_CODEART);
					
					dbKD452ComptageMachineclient pret=new dbKD452ComptageMachineclient(m_appState.m_db);
					if (pret.getPieceNotSend(m_codeclient)!=null)
					{
						promptText( "Modification impossible", "Le prêt est déjà validé", false);
						return;
					}
				
					
					launchMenuPretCategorieMaterielClient(m_codeclient,numSZ_pret,numserie_pret,codearticle_pret);
				
					
					break;
				 
				}

			}
		};
		return h;
	}
	void launchMenuCommentaireRetour(String contactcli,String SZ,String numserie,String codemateriel ,String designation)
	{
		Intent intent = new Intent(this,		CommentaireRetourMaterielClient.class);
		Bundle b=new Bundle();
		b.putString("numSZ",SZ);
		b.putString("numSerie",numserie);
		b.putString("codeclient",m_codeclient);
		b.putString("codemateriel",codemateriel);
		b.putString("designation",designation);
		
		
		intent.putExtras(b);
		startActivityForResult(intent,1);
	}
	private void PopulateList() {
		
		 cli=Global.dbMaterielClient.getMaterielClientFilters(m_codeclient, "");
		ArrayList<assoc> assocs =new ArrayList<assoc>();
	 
		assocs.add(new  assoc(0,R.id.tvCode,TableMaterielClient.FIELD_CODEART));
		assocs.add(new  assoc(0,R.id.tvLibart,TableMaterielClient.FIELD_LIBART));
		assocs.add(new  assoc(0,R.id.tvPlaqueSZ, TableMaterielClient.FIELD_PLAQUESZ));		
		assocs.add(new  assoc(0,R.id.tvNumserie, TableMaterielClient.FIELD_NUMSERIEFAB));		
		assocs.add(new  assoc(1,R.id.iv2,dbKD451RetourMachineclient.FIELD_RMC_DATE));
		
		lv.attachValues(R.layout.item_list_materielclient, cli,assocs,handler);
		
	}
	private void PopulateListPret() {
		
		pret=	Global.dbKDComptageMachineClient.getListeMateriel( m_codeclient );
		{
			ArrayList<assoc> assocs =new ArrayList<assoc>();
			assocs.add(new  assoc(0,R.id.tvCode,Global.dbKDComptageMachineClient.FIELD_CMC_CODEART));
			assocs.add(new  assoc(0,R.id.tvLibart, Global.dbKDComptageMachineClient.FIELD_CMC_LBL_ART));
			assocs.add(new  assoc(0,R.id.tvPlaqueSZ, Global.dbKDComptageMachineClient.FIELD_CMC_NUMSERIESZ));		
			assocs.add(new  assoc(0,R.id.tvNumserie, Global.dbKDComptageMachineClient.FIELD_CMC_NUMSERIEFABRICANT));
			assocs.add(new  assoc(0,R.id.tvCategorie,Global.dbKDComptageMachineClient.FIELD_CMC_CATEGORIE));
			assocs.add(new  assoc(1,R.id.iv2,null));
			lv2.attachValues(R.layout.item_list_pretmaterielclient, pret,assocs,handler);
		}
	
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v==ibPrintRetours)
		{
			dbKD451RetourMachineclient ret=new dbKD451RetourMachineclient(m_appState.m_db);
			int counret=  ret.Count(m_codeclient) ;
			if (counret>0)
				launchPrintingRetour();
			else
			{
				promptText(getString(R.string.impression_des_retours_mat_riels), getString(R.string.aucun_retour_valid_), false);
			}
			return;
 		}
		if (v==ibPrintPrets)
		{
			dbKD452ComptageMachineclient pret=new dbKD452ComptageMachineclient(m_appState.m_db);
			int counret=  pret.Count(m_codeclient) ;
			if (counret>0)
				launchPrintingPret();
			else
			{
				promptText(getString(R.string.impression_des_pr_ts_mat_riels), getString(R.string.aucun_pr_t_valid_), false);
			}
			return;
 		}		
		if (v==ibAddPret)
		{
		 
			dbKD452ComptageMachineclient pret=new dbKD452ComptageMachineclient(m_appState.m_db);
			if (pret.getPieceNotSend(m_codeclient)!=null)
			{
				promptText( "Modification impossible", "Le prêt est déjà validé", false);
				return;
			}
			
			launchMenuPretCategorieMaterielClient(m_codeclient,"","","");
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		//on revient de la cde, si on a pris une commande on ne peut pas annuler le prospect
		//on recharge la fiche aussi car peut �tre modifiŽe dans la cde
	//	if(requestCode==1 )
		{
			PopulateList();
			PopulateListPret();
		}

		hideKeyb();

	}
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		addMenu(menu, R.string.commande_quitter,
				android.R.drawable.ic_menu_close_clear_cancel);
		//addMenu(menu, R.string.pret, android.R.drawable.ic_menu_add);
		 
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
				StringBuffer buff = new StringBuffer();
				ArrayList<String> ValueMessage=new ArrayList();
				switch (item.getItemId()) {
				 
				
				 
				case R.string.commande_quitter:
					boolean ret=isAllOperationOk();
					if (ret) returnOK();
				default:
					return super.onOptionsItemSelected(item);
				}
	}
	@Override
	public void onBackPressed() {
		boolean ret=isAllOperationOk();
		if (ret) returnOK();
	}
	boolean isAllOperationOk()
	{
		TableSouches souche=new TableSouches(m_appState.m_db,this);
		String currentsoucheRetour=souche.get(TableSouches.TYPEDOC_RETOURMACHINE, getLogin());
		String currentsouchePret=souche.get(TableSouches.TYPEDOC_PRETMACHINE, getLogin());
		
		dbKD452ComptageMachineclient pret=new dbKD452ComptageMachineclient(m_appState.m_db);
		dbKD451RetourMachineclient ret=new dbKD451RetourMachineclient(m_appState.m_db);
		if (ret.isPrintOk(m_codeclient,currentsoucheRetour) && pret.isPrintOk(m_codeclient, currentsouchePret))
		{
			return true;
		}
		
		if (ret.isPrintOk(m_codeclient,currentsoucheRetour)==false)
			promptText("Impression", getString(R.string.les_retours_n_ont_pas_t_imprim_s), false);
		
		if (pret.isPrintOk(m_codeclient,currentsouchePret)==false)
			promptText("Impression", getString(R.string.les_pr_ts_n_ont_pas_t_imprim_s), false);
		
		
		
		
		return false;
	}
	void launchMenuPretCategorieMaterielClient(String contactcli,String SZ,String numserie,String codearticle )
	{
		Intent intent = new Intent(this,PretCategorieMaterielClient.class);
		Bundle b=new Bundle();
		b.putString("numSZ",SZ);
		b.putString("numSerie",numserie);
		b.putString("codeclient",m_codeclient);
		b.putString("codearticle",codearticle);
		
		intent.putExtras(b);
		startActivityForResult(intent,1);
	}
	void launchPrintingRetour() {
	 
		m_ProgressDialog=ProgressDialog.show(this, getString(R.string.communication_avec_l_imprimante), getString(R.string.patientez_));

		String mac=getPrinterMacAddress();
		   BluetoothConnectionInsecureExample example = new BluetoothConnectionInsecureExample(hPrintResultRet);
              Z420ModelRetourMachine z=new Z420ModelRetourMachine(this);
              
       
            String   zplData=z.getRetourEx( m_codeclient );
	        example.sendZplOverBluetooth(mac,zplData,1);
		 
	
	}

	void launchPrintingPret() {
		 
		m_ProgressDialog=ProgressDialog.show(this, getString(R.string.communication_avec_l_imprimante), getString(R.string.patientez_));

		String mac=getPrinterMacAddress();
		   BluetoothConnectionInsecureExample example = new BluetoothConnectionInsecureExample(hPrintResultPret);
              Z420ModelPretMachine z=new Z420ModelPretMachine(this);
              
       
            String   zplData=z.getPretEx( m_codeclient );
	        example.sendZplOverBluetooth(mac,zplData,1);
		 
	
	}
	void DispInfoCli()
	{
		structClient cli=new structClient();
		Global.dbClient.getClient(m_codeclient, cli, new StringBuilder());
		float totalEncours = Fonctions.GetStringToFloatDanem(cli.MONTANTTOTALENCOURS);
		
		TextView tvCode,tvEnseigne;
		TextView tvVille;
		TextView tvCP;
		TextView tvMail;
		TextView tvTel;
		TextView tvRS;
		TextView tvAdr1;
		TextView tvAdr2,tvCliBloque,tvEncoursClient;
		
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
		
		tvRS.setText(cli.NOM.trim());
		tvMail.setText(cli.EMAIL);
		tvCP.setText(cli.CP);
		tvTel.setText(cli.TEL1);
		tvVille.setText(cli.VILLE);
		tvCode.setText(cli.CODE);
		tvEnseigne.setText(cli.ENSEIGNE.trim());
		tvAdr1.setText(cli.ADR1);
		tvAdr2.setText(cli.ADR2);
		tvEncoursClient.setText(Fonctions.GetFloatToStringFormatDanem(totalEncours, "0.00"));
		
	}
}
