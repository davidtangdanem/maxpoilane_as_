package com.menadinteractive.segafredo.materielclient;

import java.util.ArrayList;

import org.w3c.dom.Text;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputFilter;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.menadinteractive.maxpoilane.BaseActivity;
import com.menadinteractive.printmodels.BluetoothConnectionInsecureExample;
import com.menadinteractive.printmodels.Z420ModelRetourMachine;
import com.menadinteractive.maxpoilane.R;
import com.menadinteractive.segafredo.communs.Fonctions;
import com.menadinteractive.segafredo.communs.Global;
import com.menadinteractive.segafredo.db.Preferences;
import com.menadinteractive.segafredo.db.TableContactcli;
import com.menadinteractive.segafredo.db.TableSouches;
import com.menadinteractive.segafredo.db.dbKD451RetourMachineclient.structRetourMarchineClient;
import com.menadinteractive.segafredo.plugins.Espresso;
import com.menadinteractive.segafredo.synchro.SynchroService;

public class CommentaireRetourMaterielClient extends BaseActivity 
implements OnItemSelectedListener{
	
	TextView TexteCodeart ;
	TextView Textelibart ;
	TextView TexteSZ ;
	TextView TexteNumeroserie ;
	TextView TextViewCommentaire;	
	
	Boolean m_isPrinted=false;
	EditText EditCodeart;
	EditText EditLibart;
	EditText EditSZ;
	EditText EditNumeroserie;
	EditText EditCommentaire;
	int m_ilongueurComt=255;
	String m_numsouche;


	
	/** Task */
	String m_codemateriel="";
	String m_designation="";
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
		m_codemateriel = bundle.getString("codemateriel");
		m_designation = bundle.getString("designation");
 		setContentView(R.layout.activity_retourmaterielclient);
		
 		initActionBar();

		InitTextView();
			
		Loadinformation();
	}
	
 
	void Loadinformation()
	{
		
		structRetourMarchineClient retour= new structRetourMarchineClient();
		StringBuffer stBuf=new StringBuffer();
		if (Global.dbKDRetourMachineClient.load(retour, m_codeclient, m_numSZ,m_numSerie, stBuf))
		{
			this.setEditViewText(this,R.id.EditCommentaire,retour.COMMENTAIRE);
			EditCommentaire.setEnabled(false);
			m_numsouche=retour.NUMDOC;
		}
		else
			m_numsouche="";
	
		setEditViewText(this,R.id.EditCodeart,m_codemateriel);
		
		this.setEditViewText(this,R.id.EditLibart,m_designation);
		this.setEditViewText(this,R.id.EditSZ,m_numSZ);
		this.setEditViewText(this,R.id.EditNumeroserie,m_numSerie);
		
		
	}
	
	
	

	private void initActionBar(){
		ActionBar actionBar = getActionBar();
		actionBar.setSubtitle(R.string.retourmateriel_titre);
	}
	@Override
	protected void onStart() {
		super.onStart();

	}

	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();

			
	 
	//	addMenu(menu, R.string.commande_print, R.drawable.print_icon);
		addMenu(menu,  R.string.Valider, android.R.drawable.ic_menu_save);
		addMenu(menu, android.R.string.cancel, android.R.drawable.ic_menu_close_clear_cancel);
		//addMenu(menu, R.string.supprimer, android.R.drawable.ic_menu_delete);
		

		return true;
	}



	private void record( ){

		if (m_numsouche.equals(""))
			save();
		returnOK();


	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
				StringBuffer buff = new StringBuffer();
				ArrayList<String> ValueMessage=new ArrayList();
				switch (item.getItemId()) {
			 
				 
					
				case R.string.commande_print:
					return true;
					 
				case R.string.Valider:
					if(ControleAvantSauvegarde(ValueMessage,buff)==true)
					{
						MessageYesNoPrint("Attention vous ne pourrez plus modifier ce retour","Sauvegarder le retour matériel ?");
					}
					return true;
					
				case android.R.string.cancel:
					Cancel();
					return true;

				 

				 
				default:
					return super.onOptionsItemSelected(item);
				}
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
	
	public boolean save( )
	{
		try
		{
			 

			StringBuffer stBuf=new StringBuffer();
			structRetourMarchineClient retour = new structRetourMarchineClient();
			
			retour.CODEREP = Preferences.getValue(this, Espresso.LOGIN, "0");
			retour.CODECLI = m_codeclient;
			String stcommentaire = "";
			stcommentaire = Fonctions.ReplaceGuillemet(this.getEditViewText(
					this, R.id.EditCommentaire));

			retour.COMMENTAIRE = stcommentaire;
			retour.DATE = Fonctions.getYYYYMMDD();
			retour.NUMSZ = m_numSZ;
			retour.NUMSERIE = m_numSerie;
			retour.CODEART=m_codemateriel;
			retour.DESIGNATION=EditLibart.getText().toString();
					
			TableSouches souche=new TableSouches(m_appState.m_db,this);
			String newsouche=souche.get(TableSouches.TYPEDOC_RETOURMACHINE, retour.CODEREP);
			retour.NUMDOC=newsouche;
			
			
			retour.SOCCODE = "1";
			
			Global.dbKDRetourMachineClient.save(retour, m_codeclient, m_numSZ,m_numSerie, stBuf);
			
				
		//	souche.incNum(retour.CODEREP, TableSouches.TYPEDOC_RETOURMACHINE);
		

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
			if(this.getEditViewText(this,R.id.EditCommentaire).equals(""))
			{
				
				promptText("Commentaire", "La saisie du commentaire est obligatoire", false);
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

	 
	 
	public void MessageYesNoPrint(String message, String title) {
	 
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(message)
		.setCancelable(false)
		.setTitle(title)
		.setPositiveButton(getString(android.R.string.yes),
				new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {				 
					//launchPrinting();
				record( );
				returnOK();
			}
		})
		.setNegativeButton(getString(android.R.string.no),
				new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {

			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}
	
	

}
