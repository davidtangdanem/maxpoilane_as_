package com.menadinteractive.segafredo.contactcli;

import java.util.ArrayList;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputFilter;
import android.view.Gravity;
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
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

import com.menadinteractive.commande.commandeActivity;
import com.menadinteractive.maxpoilane.BaseActivity;
import com.menadinteractive.maxpoilane.R;
import com.menadinteractive.segafredo.client.GeolocalisationClient;
import com.menadinteractive.segafredo.client.MyLocation;
import com.menadinteractive.segafredo.client.ficheclient;
import com.menadinteractive.segafredo.client.task_geocodeClient;
import com.menadinteractive.segafredo.communs.Fonctions;
import com.menadinteractive.segafredo.communs.Global;
import com.menadinteractive.segafredo.db.Preferences;
import com.menadinteractive.segafredo.db.TableClient;
import com.menadinteractive.segafredo.db.TableContactcli;
import com.menadinteractive.segafredo.db.TableClient.structClient;
import com.menadinteractive.segafredo.encaissement.Encaissement;
import com.menadinteractive.segafredo.plugins.Espresso;
import com.menadinteractive.segafredo.synchro.SynchroService;

public class fichecontactcli extends BaseActivity 
implements OnItemSelectedListener
{

	TextView TextViewCode ;
	TextView TextViewNom ;
	TextView TextViewPrenom ;
	TextView TextViewMobile ;
	TextView TextViewEmail;
	TextView TextViewFonction ;
	TextView TextViewCommentaire;	
	
	String m_stvaleurDebut;
	String m_stvaleurFin;


	
	EditText EditCode;
	EditText EditNom;
	EditText EditPrenom;
	EditText EditMobile;
	EditText EditEmail;
	EditText EditCommentaire;
	ArrayList<Bundle> idFonction = null;
	int m_ilongueurComt=255;

	
	private ProgressDialog m_ProgressDialog = null; 
	
	
	/** Task */

	String m_contactcli="";
	String m_codeclient="";
	String m_Etat="";
	
	
	

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);

		SynchroService.setPaused(true);
		idFonction = new ArrayList<Bundle>();

		Bundle bundle = this.getIntent().getExtras();
		m_contactcli = bundle.getString("contactcli");
		m_codeclient = bundle.getString("codeclient");
		
		
		setContentView(R.layout.activity_ficheccontactcli);
		initActionBar();


		InitTextView();
		m_Etat=Global.dbContactcli.CONTACT_MODIFICATION;
		
		if(m_contactcli.equals(""))
		{
			m_Etat=Global.dbContactcli.CONTACT_CREATION;
			m_contactcli=Global.dbContactcli.GetNumcontact(m_codeclient);
		}
		
		
			
		Loadinformation(m_codeclient,m_contactcli);

		DispInfoCli();

	}
	void Loadinformation(String codeclient, String codecontactcli)
	{
		//cliToSave=new str;

		/*if(numprospect.equals(""))
		{
			numProspect=numprospect=Global.dbClient.GetCodeProspect(Preferences.getValue(this, Espresso.LOGIN, "0"));
			//tof: rechargement des données
			cliToSave.ZONE = m_stCurZone ;
			cliToSave.JOURPASSAGE = "J";
			findAdresse();
		}
		else
		{
			devinerAdresseAllowed=false;
			Global.dbClient .getClient(numprospect, cliToSave, new StringBuilder());
		}*/
		//m_stCurJourPassage = cliToSave.JOURPASSAGE ;
		cliToSave=new TableContactcli.structContactcli();

		Global.dbContactcli .getContact(codeclient, m_contactcli, cliToSave, new StringBuilder());
		setEditViewText(this,R.id.EditCodeContactcli,m_contactcli);


		this.setEditViewText(this,R.id.EditNom,cliToSave.NOM);
		this.setEditViewText(this,R.id.EditPrenom,cliToSave.PRENOM);
		this.setEditViewText(this,R.id.EditMobile,cliToSave.MOBILE);

		this.setEditViewText(this,R.id.Editemail,cliToSave.EMAIL);
		this.setEditViewText(this,R.id.EditCommentaire,cliToSave.COMMENTAIRE);

		String stflag=cliToSave.FLAG;
		fillFonction(Fonctions.GetStringDanem(cliToSave.CODEFONCTION));
		if(Global.dbContactcli.CONTACT_CREATION.equals(stflag))
			m_Etat=Global.dbContactcli.CONTACT_CREATION;
		
		m_stvaleurDebut=cliToSave.NOM+cliToSave.PRENOM+cliToSave.MOBILE+cliToSave.EMAIL+cliToSave.COMMENTAIRE+cliToSave.CODEFONCTION;
		

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
		actionBar.setSubtitle(R.string.contactcli_titre);
	}
	@Override
	protected void onStart() {
		super.onStart();

	}

	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();

		addMenu(menu, android.R.string.cancel, android.R.drawable.ic_menu_close_clear_cancel);		
		addMenu(menu, android.R.string.ok, android.R.drawable.ic_menu_save);
		addMenu(menu, R.string.supprimer, android.R.drawable.ic_menu_delete);
		

		return true;
	}



	private void record(ArrayList<String> ValueMessage, StringBuffer buff){


		if(ControleAvantSauvegarde(ValueMessage,buff)==true)
		{
			if(1==1 ){
				if (save(false,m_Etat,buff)==true)
				{
					//FurtiveMessageBox("prospect sauvegardÃ©");
					finish();
				}
				else
				{
					Fonctions.FurtiveMessageBox(this,buff.toString());
				}
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
					MessageYesNo(getString(R.string.suppression_contact));
					

					return true;
				case R.string.prospect_positionner:

				

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

	
	String getCodeClient()
	{
		try {
			String code=m_contactcli;
			return code;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
	String getFonction()
	{
		String pays="";
		int pos = this.getSpinnerSelectedIdx(this, R.id.spinnerFonction);
		if (pos > -1)
			try {
				pays = idFonction.get(pos).getString(
						Global.dbParam.FLD_PARAM_CODEREC);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		return pays;
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
	
	public boolean save(boolean temporary,String etat,StringBuffer stbuff)
	{
		try
		{


			cliToSave=new TableContactcli.structContactcli();

			cliToSave.CODECONTACT=m_contactcli;
			cliToSave.CODVRP=Preferences.getValue(this, Espresso.LOGIN, "0");
			cliToSave.CODECLIENT=m_contactcli;
			cliToSave.CODEFONCTION=getFonction();
			
			
			
			cliToSave.NOM=this.getEditViewText(this,R.id.EditNom);
			cliToSave.PRENOM=this.getEditViewText(this,R.id.EditPrenom);
			cliToSave.MOBILE=this.getEditViewText(this,R.id.EditMobile);
			cliToSave.EMAIL=this.getEditViewText(this,R.id.Editemail);
			String stcommentaire="";
			stcommentaire=Fonctions.ReplaceGuillemet(this.getEditViewText(this,R.id.EditCommentaire));
			
			cliToSave.COMMENTAIRE=stcommentaire;
			cliToSave.FLAG=etat;
			
			m_stvaleurFin=cliToSave.NOM+cliToSave.PRENOM+cliToSave.MOBILE+cliToSave.EMAIL+cliToSave.COMMENTAIRE+cliToSave.CODEFONCTION;
			

				if(!m_stvaleurFin.equals(m_stvaleurDebut) || etat.equals(Global.dbContactcli.CONTACT_SUPPRESSION))
				{
					save(etat);
				}
				
			
		

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
			String nom=this.getEditViewText(this,R.id.EditNom);
		 
			if(nom.length()<3)
			{
				
				stmessage=""+TextViewNom.getText().toString()+" "+getString(R.string.prospect_obligatoire) +"(3 car. minimum)";
				ValueMessage.add(stmessage);
				return false;
			
			}
			String prenom=this.getEditViewText(this,R.id.EditPrenom);
			 
			if(prenom.length()<3)
			{
				
				stmessage=""+TextViewPrenom.getText().toString()+" "+getString(R.string.prospect_obligatoire) +"(3 car. minimum)";
				ValueMessage.add(stmessage);
				return false;
			
			}
			//Fonction
			if(getFonction().equals(""))
			{
				
				stmessage=""+TextViewFonction.getText().toString()+" "+getString(R.string.prospect_obligatoire);
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
	
	void fillFonction(String selVal) {
		try {
			//selVal=Global.dbClient.getCodeTypeEtablissement(selVal);

			if (Global.dbParam.getRecord2s(Global.dbParam.PARAM_FCTCONTACT,
					this.idFonction,true) == true) {

				int pos = 0;
				String[] items = new String[idFonction.size()];
				for (int i = 0; i < idFonction.size(); i++) {

					items[i] = idFonction.get(i).getString(
							Global.dbParam.FLD_PARAM_LBL);

					String codeRec = idFonction.get(i).getString(
							Global.dbParam.FLD_PARAM_CODEREC);

					if (selVal.equals(codeRec)) {
						pos = i;
					}

				}

				Spinner spinner = (Spinner) findViewById(R.id.spinnerFonction);


				//ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				//		android.R.layout.simple_spinner_item, items);
				ArrayAdapter<String>  adapter = new ArrayAdapter<String>(this, R.layout.spinner_item_text, items);
				
				adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				spinner.setAdapter(adapter);

				spinner.setSelection(pos);

			}

		} catch (Exception ex) {

		}

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
		
		//Code
		TextViewCode = (TextView) findViewById(R.id.TexteCodeContactcli);				
		//Nom
		TextViewNom = (TextView) findViewById(R.id.TexteNom);
		//Prenom
		TextViewPrenom = (TextView) findViewById(R.id.TextePrenom);
		//Adresse 2
		TextViewMobile = (TextView) findViewById(R.id.TexteMobile);
		//Code postal
		TextViewEmail = (TextView) findViewById(R.id.TexteEmail);
		//Ville
		TextViewFonction = (TextView) findViewById(R.id.TexteFonction);
		//Commentaire
		TextViewCommentaire = (TextView) findViewById(R.id.TexteCommentaire);
		
		
		Spinner spFonction=(Spinner)this.findViewById(R.id.spinnerFonction);
		
		EditCode=(EditText)this.findViewById(R.id.EditCodeContactcli);
		EditNom=(EditText)this.findViewById(R.id.EditNom);
		EditPrenom=(EditText)this.findViewById(R.id.EditPrenom);
		EditMobile=(EditText)this.findViewById(R.id.EditMobile);
		EditEmail=(EditText)this.findViewById(R.id.Editemail);
		EditCommentaire=(EditText)this.findViewById(R.id.EditCommentaire);
		
		InputFilter[] FilterArrayCmt = new InputFilter[1];

		FilterArrayCmt[0] = new InputFilter.LengthFilter(
				m_ilongueurComt);
		EditCommentaire.setFilters(FilterArrayCmt);
		
			}




	private boolean save(String etat){
		StringBuffer buffer = new StringBuffer();
		if (!Global.dbContactcli.saveContactclient(cliToSave, m_contactcli,m_codeclient,etat, buffer))
		{
			Fonctions.FurtiveMessageBox(this, getString(R.string.unable_to_save)+":"+buffer.toString());
			return false;
		}
		

		Fonctions.FurtiveMessageBox(this, getString(R.string.save_successfull));

		return true;
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
				String stetat=m_Etat;
				if(stetat.equals(Global.dbContactcli.CONTACT_CREATION))
				{
					Global.dbContactcli.delete(m_contactcli,m_codeclient);
				}
				else
				{
					stetat=Global.dbContactcli.CONTACT_SUPPRESSION;
				
					if (save(true,stetat,new StringBuffer()))
					{	
	
						
					}
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
		tvCode.setText("Code client : "+cli.CODE);
		tvEnseigne.setText(cli.ENSEIGNE.trim());
		tvAdr1.setText(cli.ADR1);
		tvAdr2.setText(cli.ADR2);
		tvEncoursClient.setText(Fonctions.GetFloatToStringFormatDanem(totalEncours, "0.00"));
		
	}

}
