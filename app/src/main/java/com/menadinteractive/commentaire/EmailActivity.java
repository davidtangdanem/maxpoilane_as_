package com.menadinteractive.commentaire;

import java.util.ArrayList;

import com.menadinteractive.maxpoilane.BaseActivity;
import com.menadinteractive.maxpoilane.R;
import com.menadinteractive.segafredo.communs.Fonctions;
import com.menadinteractive.segafredo.communs.Global;
import com.menadinteractive.segafredo.db.Preferences;
import com.menadinteractive.segafredo.db.TableClient.structClient;
import com.menadinteractive.segafredo.plugins.Espresso;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

public class EmailActivity extends BaseActivity implements OnClickListener{
	TextView tvLimit;
	EditText etFilter=null;
 
	String m_codeclient;
	String oldvalue;
	String m_emailClientInit="";
	int maxlen=0;
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		 
		setContentView(R.layout.activity_email);
		
		Bundle bundle = this.getIntent().getExtras();
		m_codeclient = bundle.getString("codeclient");
		oldvalue = bundle.getString("oldvalue");
		maxlen = bundle.getInt("maxlen");
		
		initGUI();
		setListeners();
		DispInfoCli();
	}

	void initGUI() {
		 
		etFilter= (EditText) findViewById(R.id.editTextComment);
		tvLimit= (TextView) findViewById(R.id.textViewLimit);
		
		
		etFilter.setText(Fonctions.GetStringDanem(oldvalue));
		
	}
	
	void updateLimitCounter()
	{
		if (etFilter==null) return ;
		int len=etFilter.getText().toString().length();
		tvLimit.setText(len+"/"+maxlen);
	}
	void setListeners()
	{
		
		
 
		
		etFilter.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				
			}
		});
	}
		
 

	@Override
	public void onClick(View v) {	 		
	}
	
	 
 
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();

		addMenu(menu, R.string.Valider, android.R.drawable.ic_menu_save);
		addMenu(menu, R.string.Annuler, android.R.drawable.ic_menu_close_clear_cancel);
		
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
				StringBuffer buff = new StringBuffer();
				ArrayList<String> ValueMessage=new ArrayList();
				switch (item.getItemId()) {
				case R.string.Valider:
					String val=etFilter.getText().toString();
					//boolean modetest=Preferences.getValueBoolean(this, Espresso.PREF_MODETEST, false);
					//if (modetest)
					{
					//	returnCancel();
					//	return true;	
					}
					//else
					{
						if(Fonctions.isValidEmail(val)==true)
						{
							StringBuffer stBuf =new StringBuffer();
							if(!Fonctions.GetStringDanem(val).equals(m_emailClientInit))
				            {
						    	Global.dbClient.saveUpdateEmail(Fonctions.GetStringDanem(val),Fonctions.getYYYYMMDD(),m_codeclient, stBuf);
								
				            }
							
							Bundle bundle = new Bundle();
							bundle.putString("newvalue", val);
							Intent intent=new Intent();
							intent.putExtras(bundle);
					
							returnOK(intent);
							
							/*if(!val.equals(""))
							{
								
							}*/
						
						}
							else
							Fonctions.FurtiveMessageBox(this, "Veuillez saisir un Email de destinataire valide.");
						
						
						
					}
					
 					return true;
				
				case R.string.Annuler:
					
					returnCancel();
					break;
				default:
					return super.onOptionsItemSelected(item);
				}
				return super.onOptionsItemSelected(item);
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
		m_emailClientInit=Fonctions.GetStringDanem(cli.EMAIL);
		
		tvEncoursClient.setText(Fonctions.GetFloatToStringFormatDanem(totalEncours, "0.00"));
		
	}

}
