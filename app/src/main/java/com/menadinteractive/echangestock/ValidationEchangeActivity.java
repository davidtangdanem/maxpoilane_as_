package com.menadinteractive.echangestock;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.menadinteractive.maxpoilane.BaseActivity;
import com.menadinteractive.maxpoilane.R;
import com.menadinteractive.segafredo.db.dbKD543LinInventaire;
import com.menadinteractive.segafredo.db.dbKD543LinInventaire.structPassePlat;
import com.menadinteractive.segafredo.db.dbSiteListeLogin;

/**
 * Classe de validation de l'enregistrement d'un acompte, enregsitrement du nom d'utilisateur
 * et de la signature + impression
 * @author Damien
 *
 */

public class ValidationEchangeActivity extends BaseActivity{
	
	String commentaire;
	EditText etComment;
	ArrayList<Bundle> idListAgent= null;// Agent
 
	String mCodeInv;
	
 
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_validation_echangestock);
		
		idListAgent= new ArrayList<Bundle>();
		//on récupère les infos venant du bundle pour l'encaissement
		Bundle b = null;
		if(getIntent() != null) b = getIntent().getExtras();
		if(b != null) {
			mCodeInv = b.getString("numinv");
			 
		}
		
		initGUI();
		initListeners();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		 
	}
	
	/**
	 * Initialisation des éléments graphiques
	 */
	private void initGUI(){
		etComment = (EditText) findViewById(R.id.etComment);
//		etComment.setEnabled(false);
		etComment.setTextColor(Color.BLACK);
	//	etComment.setInputType(InputType.TYPE_NULL); 
		
		
		dbKD543LinInventaire inv=new dbKD543LinInventaire(m_appState.m_db);
	
		structPassePlat lin=	inv.loadHdr();
		if (lin!=null)
		{
			etComment.setText(lin.FIELD_LIGNEINV_COMMENT1);
 		}
		 fillAgent("");
	}
	
	private void initListeners(){
		etComment.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				LaunchComment( commentaire,"",250 );
				
			}
		});
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		addMenu(menu, R.string.inventaire_print,R.drawable.print_icon);
		addMenu(menu, R.string.Annuler, android.R.drawable.ic_menu_close_clear_cancel);
		addMenu(menu, R.string.comment, android.R.drawable.ic_menu_info_details);
		 
		
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.string.comment:
		 			
			LaunchComment( commentaire,"",250 );
			return true;
	 
		case R.string.inventaire_print:
			 
			
			MessageYesNoValider("Vous ne pourrez plus faire de modification. Validez quand même ?");
			
			return true;
		 
		case R.string.Annuler:
			returnCancel();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}

	}
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode==LAUNCH_COMMENTAIRE)
		{
			if (resultCode==RESULT_OK)
			{
				commentaire=data.getExtras().getString("newvalue");
				etComment.setText(commentaire);
				hideKeyb();
			}
		}
	}
 
	public void MessageYesNoValider(String message) {
		 
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(message)
				.setCancelable(false)
				.setPositiveButton(getString(android.R.string.yes),
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {
								//launchPrinting();
								String agent=getAgent();
								if (agent.equals(""))
								{
									promptText("Information", "Sélectionnez l'agent qui reçoit vos marchandises", false);
									return;
								}
								Intent i=new Intent();
								Bundle b=new Bundle();
								b.putString("comment", etComment.getText().toString());
								b.putString("agent", agent);
								//b.putString("isagent", cbAgent.isChecked()+"");
				 
								i.putExtras(b);
								returnOK(i);

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
	String getAgent()
	{
		String agent="";
		int pos = this.getSpinnerSelectedIdx(this, R.id.spinnerAgent);
		if (pos > -1)
			try {
				agent = idListAgent.get(pos).getString(
						dbSiteListeLogin.FIELD_LOGIN_LOGIN);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		return agent;
	}
	void fillAgent(String selVal) {
		try {
			dbSiteListeLogin login=new dbSiteListeLogin(m_appState.m_db);
			
			if (login.loadLogin( 
					this.idListAgent,new StringBuffer()) == true) {

				int pos = 0;
				String[] items = new String[idListAgent.size()];
				for (int i = 0; i < idListAgent.size(); i++) {

					items[i] = idListAgent.get(i).getString(
							login.FIELD_LOGIN_NOM);

					String codeRec = idListAgent.get(i).getString(
							login.FIELD_LOGIN_LOGIN);

					if (selVal.equals(codeRec)) {
						pos = i;
					}

				}

				Spinner spinner = (Spinner) findViewById(R.id.spinnerAgent);


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
}
