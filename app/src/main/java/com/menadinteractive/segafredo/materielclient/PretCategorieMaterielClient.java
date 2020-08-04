package com.menadinteractive.segafredo.materielclient;

import java.util.ArrayList;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

import com.danem.lib.IntentIntegrator;
import com.danem.lib.IntentResult;
import com.menadinteractive.maxpoilane.BaseActivity;
import com.menadinteractive.maxpoilane.R;
import com.menadinteractive.segafredo.communs.Fonctions;
import com.menadinteractive.segafredo.communs.Global;
import com.menadinteractive.segafredo.db.Preferences;
import com.menadinteractive.segafredo.db.TableContactcli;
import com.menadinteractive.segafredo.db.TableSouches;
import com.menadinteractive.segafredo.db.dbKD451RetourMachineclient.structRetourMarchineClient;
import com.menadinteractive.segafredo.db.dbKD452ComptageMachineclient.structComptageMachineClient;
import com.menadinteractive.segafredo.plugins.Espresso;
import com.menadinteractive.segafredo.synchro.SynchroService;

public class PretCategorieMaterielClient extends BaseActivity 
implements OnItemSelectedListener{

	TextView TexteSZ ;

	TextView TexteCodeart ;
	TextView Textelibart ;
	TextView TexteNumeroserie ;
	TextView TextViewCommentaire;	
	TextView TextViewCategorie;
	TextView TextViewProduit;
 	
	EditText EditCodeart;
	EditText EditLibart;
	EditText EditSZ;
	EditText EditNumeroserie;
	EditText EditCommentaire;
	int m_ilongueurComt=255;
	
	
	ArrayList<Bundle> idListCategorie = null;// categ compta pour la tva
	ArrayList<Bundle> idListProduit = null;// type de pdv
	
	Spinner spinCategorie;
	Spinner spinProduit;
	
	private ProgressDialog m_ProgressDialog = null; 
	
	
	/** Task */
	String m_codearticle="";
	String m_numSZ="";
	String m_numSerie="";
	String m_codeclient="";

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);

		SynchroService.setPaused(true);

		Bundle bundle = this.getIntent().getExtras();
		m_numSZ = bundle.getString("numSZ");
		m_numSerie = bundle.getString("numSerie");	
		m_codeclient = bundle.getString("codeclient");
		m_codearticle = bundle.getString("codearticle");
		
		idListCategorie= new ArrayList<Bundle>();
		idListProduit= new ArrayList<Bundle>();
		
		
		setContentView(R.layout.activity_pretmaterielclient);
		initActionBar();


		InitTextView();
		
			
		Loadinformation();

		
		Spinner etCategorie = (Spinner) this
				.findViewById(R.id.spinnerCategorie);
		etCategorie.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int position, long id) {
				if(position>-1)
				{
					fillProduit("", idListCategorie.get(position).getString(
							Global.dbParam.FLD_PARAM_CODEREC));
				
				}
			
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});

	}
	void Loadinformation()
	{
		
		StringBuffer stBuf=new StringBuffer();
		
		
		structComptageMachineClient pret= new structComptageMachineClient();
		
		Global.dbKDComptageMachineClient.load(pret, m_codeclient, m_numSZ,m_numSerie, stBuf);
		this.setEditViewText(this,R.id.EditSZ,m_numSZ);
		this.setEditViewText(this,R.id.EditNumeroserie,m_numSerie);
		
		this.setEditViewText(this,R.id.EditCommentaire,pret.COMMENTAIRE);
		fillCategorie(pret.CATEGORIE);
		fillProduit(pret.CODEART,pret.CATEGORIE);
		
		

	}
	
	
	void showProgressDialog()
	{
		m_ProgressDialog = ProgressDialog.show(this,
				"Veuillez patienter", "traitement en cours", true);
	}
	public void hideProgressDialog()
	{
		if (m_ProgressDialog!=null)
			m_ProgressDialog.dismiss();
	}

	private void initActionBar(){
		ActionBar actionBar = getActionBar();
		actionBar.setSubtitle(R.string.pretmateriel_titre);
	}
	@Override
	protected void onStart() {
		super.onStart();

	}

	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();

		addMenu(menu, android.R.string.ok, android.R.drawable.ic_menu_save);
		addMenu(menu, android.R.string.cancel, android.R.drawable.ic_menu_close_clear_cancel);		
		addMenu(menu, R.string.supprimer, android.R.drawable.ic_menu_delete);
		
		addMenu(menu, R.string.pretmateriel_scan, R.drawable.scanner);		
		

		

		return true;
	}



	private void record(ArrayList<String> ValueMessage, StringBuffer buff){


		if(ControleAvantSauvegarde(ValueMessage,buff)==true)
		{
			if(1==1 ){
				
				MessageYesNo(getString(R.string.pretmateriel_yesno));
				
				
			}
			
		}
		else
			Fonctions.FurtiveMessageBox(this,Fonctions.GetStringDanem( ValueMessage.get(0) ));
	


	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
				StringBuffer buff = new StringBuffer();
				ArrayList<String> ValueMessage=new ArrayList();
				switch (item.getItemId()) {
				case R.string.supprimer:
					MessageYesNoSuppression(getString(R.string.suppression_pretmateriel));
					

					return true;
				case R.string.prospect_positionner:
					return true;
				case R.string.pretmateriel_scan:
					IntentIntegrator.initiateScan(PretCategorieMaterielClient.this);
				

					return true;
				case android.R.string.cancel:
					Cancel();
					return true;

				case  android.R.string.ok:
					record(ValueMessage, buff);
					return true;

				case R.string.prospect_prendrecde://si on veut prendre commande avant m�me de remplir les coord
					//on sauve le prospect (obligŽ pour que tt fonctionne)
					//on va en prise de cde
					//on revient et on contr™le
					//comme c'est une sauvegarde forcŽe on donne un nom au client par dŽfaut si non renseignŽ
					if(this.getEditViewText(this,R.id.EditRaisonsocial).equals(""))
					{
						this.setEditViewText(this, R.id.EditRaisonsocial, getString(R.string.prospect_nomdefaut)) ;
					}
					
					break;
				/*case R.string.commande_quest:
					MenuPopup.launchQuest(this,numProspect,"","");
					break;*/
				case R.string.geocode_prospect:
					
					break;
				default:
					return super.onOptionsItemSelected(item);
				}
				return super.onOptionsItemSelected(item);
	}
	
	void Cancel()
	{
	
		
		//si on a deja crŽe le prospect parcequ'on avait ŽtŽ en cde on l'efface
	//	if (Global.dbClient.load(getCodeClient()).CREAT.equals(Global.dbClient.CLI_CREATION) && Global.dbKDEntCde.Count(getCodeClient())==0)
		//	Global.dbClient.delete(getCodeClient());
		this.finish(0);

	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		//on récupère ce qui a été scanné
		if(requestCode == IntentIntegrator.REQUEST_CODE)
		{
			IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
			if (scanResult != null) {
				Log.d("TAG", "qrResult : "+scanResult.getContents());
				EditSZ.setText(scanResult.getContents());
				
				
			}
			else{
				//scanResultTextView.setText("no result");
			}

		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) 
	{

		if (keyCode == KeyEvent.KEYCODE_BACK ||keyCode == KeyEvent.KEYCODE_HOME) {
			Cancel();

			return false;
		}

		else
			return super.onKeyDown(keyCode, event);
	}

	
	
	void finish(int i)
	{
		SynchroService.setPaused(false);
		finish();
	}
		
	/**
	 * Creation du menu
	 */
	static final private int MENU_ITEM = Menu.FIRST;

	TableContactcli.structContactcli cliToSave;
	
	public boolean save(boolean temporary,StringBuffer stbuff)
	{
		try
		{

			String stcommentaire = "";
			stcommentaire = Fonctions.ReplaceGuillemet(this.getEditViewText(
					this, R.id.EditCommentaire));

			
			StringBuffer stBuf=new StringBuffer();
			structComptageMachineClient pret = new structComptageMachineClient();
			
			pret.CODEREP = Preferences.getValue(this, Espresso.LOGIN, "0");
			pret.CODECLI = m_codeclient;
			pret.DATE = Fonctions.getYYYYMMDD();
			pret.NUMSERIESZ = EditSZ.getText().toString();
			pret.NUMSERIEFABRICANT = EditNumeroserie.getText().toString();
			pret.CATEGORIE=getCategorie();
			pret.CODEART=getProduit();
			pret.LBL_ART=getProduitLbl();
			pret.COMMENTAIRE = stcommentaire;
			pret.SOCCODE = "1";
			
			TableSouches souche=new TableSouches(m_appState.m_db,this);
			String newsouche=souche.get(TableSouches.TYPEDOC_PRETMACHINE, pret.CODEREP);
			pret.NUMSOUCHE=newsouche;
			
			Global.dbKDComptageMachineClient.save(pret, m_codeclient, EditSZ.getText().toString(),EditNumeroserie.getText().toString(), stBuf);
			

			return true;

		}
		catch(Exception ex)
		{
			return false;

		}

	}

	public boolean ControleAvantSauvegarde(ArrayList<String> ValueMessage,StringBuffer stBuf)
	{
		boolean bres=true;

		String stmessage="";

		try
		{
			if(this.getEditViewText(this,R.id.EditSZ).equals(""))
			{
				
				stmessage=""+TexteSZ.getText().toString()+" "+getString(R.string.prospect_obligatoire);
				ValueMessage.add(stmessage);
				return false;
			
			}
			String prefix=Fonctions.Left(this.getEditViewText(this,R.id.EditSZ),2);
			String suffix=Fonctions.Mid(this.getEditViewText(this,R.id.EditSZ),2,40);
			if (prefix.toUpperCase().equals("SZ")==false)
			{
				stmessage="La plaque doit commencer par SZ";
				ValueMessage.add(stmessage);
				return false;
			}
			if (Fonctions.isNumeric(suffix)==false)
			{
				stmessage="Il ne doit y avoir que des chiffres après le préfix SZ";
				ValueMessage.add(stmessage);
				return false;
			}
			
			if(this.getEditViewText(this,R.id.EditNumeroserie).equals(""))
			{
				
				stmessage=""+TexteNumeroserie.getText().toString()+" "+getString(R.string.prospect_obligatoire);
				ValueMessage.add(stmessage);
				return false;
			
			}
			
		}
		catch(Exception ex)
		{
			stBuf.setLength(0);
			stBuf.append(ex.getMessage());
			return false;

		}
		return bres;



	}
	
	

	/**
	 * renvoi l'index sï¿½lectionnï¿½ dans un spinner
	 * @param et
	 * @return
	 */
	public int getSpinnerSelectedIdx(Spinner et )
	{
		try
		{
			int pos=et.getSelectedItemPosition();
			return pos;

		}
		catch(Exception ex)
		{

		}
		return -1;
	}    
	


	void InitTextView()
	{
		TexteCodeart = (TextView) findViewById(R.id.TexteCodeart);				
		Textelibart = (TextView) findViewById(R.id.Textelibart);
		TexteSZ = (TextView) findViewById(R.id.TexteSZ);
		TexteNumeroserie = (TextView) findViewById(R.id.TexteNumeroserie);
		TextViewCommentaire = (TextView) findViewById(R.id.TexteCommentaire);
		
		
		EditCodeart=(EditText)this.findViewById(R.id.EditCodeart);
		EditLibart=(EditText)this.findViewById(R.id.EditLibart);
		EditSZ=(EditText)this.findViewById(R.id.EditSZ);
		EditNumeroserie=(EditText)this.findViewById(R.id.EditNumeroserie);
		EditCommentaire=(EditText)this.findViewById(R.id.EditCommentaire);
		
		InputFilter[] FilterArrayCmt = new InputFilter[1];

		FilterArrayCmt[0] = new InputFilter.LengthFilter(
				m_ilongueurComt);
		EditCommentaire.setFilters(FilterArrayCmt);
		
			}




	
	private void UI_updateProgressBar(){
		
	}

	public void onItemSelected(AdapterView<?> parent, View view, 
			int pos, long id) {
		switch (parent.getId()) 
		{         
		            
		     

		}    	
		// An item was selected. You can retrieve the selected item using
		// parent.getItemAtPosition(pos)
		//m_stFam = idFam.get(pos).getString(
		//		Global.dbParam.FLD_PARAM_CODEREC);
		//PopulateList() ;
	}
	public void onNothingSelected(AdapterView<?> parent) {
		// Another interface callback
	}

	 
	public void MessageYesNo(String message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(message)
		.setCancelable(false)
		.setPositiveButton(getString(android.R.string.yes),
				new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {	
				
				if (save(true,new StringBuffer()))
				{	

					
				}
				finish();
			}
		})
		.setNegativeButton(getString(android.R.string.no),
				new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}
	public void MessageYesNoSuppression(String message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(message)
		.setCancelable(false)
		.setPositiveButton(getString(android.R.string.yes),
				new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {	
				StringBuffer err=new StringBuffer();
				Global.dbKDComptageMachineClient.deleteComptage(m_codeclient, m_numSZ, m_numSerie, err);
				finish();
			}
		})
		.setNegativeButton(getString(android.R.string.no),
				new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}
	void fillCategorie(String selVal) {
		try {
			if (Global.dbParam.getRecord2s(Global.dbParam.PARAM_FAM3,
					this.idListCategorie,false) == true) {

				int pos = 0;
				String[] items = new String[idListCategorie.size()];
				for (int i = 0; i < idListCategorie.size(); i++) {

					items[i] = idListCategorie.get(i).getString(
							Global.dbParam.FLD_PARAM_LBL);

					String codeRec = idListCategorie.get(i).getString(
							Global.dbParam.FLD_PARAM_CODEREC);

					if (selVal.equals(codeRec)) {
						pos = i;
					}

				}

				Spinner spinner = (Spinner) findViewById(R.id.spinnerCategorie);


				//ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				//		android.R.layout.simple_spinner_item, items);
				//ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				//		android.R.layout.simple_spinner_item, items);
				ArrayAdapter<String>  adapter = new ArrayAdapter<String>(this, R.layout.spinner_item_text, items);
				
				adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				spinner.setAdapter(adapter);

				spinner.setSelection(pos);

			}

		} catch (Exception ex) {

		}

	}void fillProduit(String selVal,String stCategorie) {
		try {
			if(!stCategorie.equals(""))
			{
				this.idListProduit=Global.dbListeMateriel.getListeMaterielFilters(stCategorie, "");
				

				int pos = 0;
				String[] items = new String[idListProduit.size()];
				for (int i = 0; i < idListProduit.size(); i++) {

					items[i] = idListProduit.get(i).getString(
							Global.dbListeMateriel.FIELD_LM_NOMMACHINE);

					String codeRec = idListProduit.get(i).getString(
							Global.dbListeMateriel.FIELD_LM_CODEMACHINE);

					if (selVal.equals(codeRec)) {
						pos = i;
					}

				}

				Spinner spinner = (Spinner) findViewById(R.id.spinnerProduit);


				ArrayAdapter<String>  adapter = new ArrayAdapter<String>(this, R.layout.spinner_item_text, items);
				
				adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				spinner.setAdapter(adapter);

				spinner.setSelection(pos);

			}

				
			
			
		} catch (Exception ex) {

		}

	}
	String getCategorie()
	{
		String categorie="";
		int pos = this.getSpinnerSelectedIdx(this, R.id.spinnerCategorie);
		if (pos > -1)
			try {
				categorie = idListCategorie.get(pos).getString(
						Global.dbParam.FLD_PARAM_CODEREC);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		return categorie;
	}	
	String getProduit()
	{
		String produit="";
		int pos = this.getSpinnerSelectedIdx(this, R.id.spinnerProduit);
		if (pos > -1)
			try {
				produit = idListProduit.get(pos).getString(
						Global.dbListeMateriel.FIELD_LM_CODEMACHINE);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		return produit;
	}	
	String getProduitLbl()
	{
		String produit="";
		int pos = this.getSpinnerSelectedIdx(this, R.id.spinnerProduit);
		if (pos > -1)
			try {
				produit = idListProduit.get(pos).getString(
						Global.dbListeMateriel.FIELD_LM_NOMMACHINE);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		return produit;
	}	



}
