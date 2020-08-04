package com.menadinteractive.inventaire;

import java.io.File;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.menadinteractive.maxpoilane.BaseActivity;
import com.menadinteractive.maxpoilane.ExternalStorage;
import com.menadinteractive.maxpoilane.R;
import com.menadinteractive.segafredo.communs.Fonctions;
import com.menadinteractive.segafredo.db.dbKD543LinInventaire;
import com.menadinteractive.segafredo.db.dbKD543LinInventaire.structPassePlat;
import com.menadinteractive.segafredo.encaissement.Encaissement;
import com.menadinteractive.segafredo.encaissement.SignatureActivity;
import com.menadinteractive.segafredo.plugins.Espresso;
import com.menadinteractive.segafredo.tasks.task_sendInventaireWSData;

/**
 * Classe de validation de l'enregistrement d'un acompte, enregsitrement du nom d'utilisateur
 * et de la signature + impression
 * @author Damien
 *
 */

public class ValidationInventaire extends BaseActivity{
	
	ImageView ivSignatureAgent;
	ImageView ivSignatureDuo;
	EditText etComment;
	CheckBox cbAgent;
	Boolean m_isSignAgent,m_isSignDuo;
	String mCodeInv;
	
	Bitmap myBitmap;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_validation_inventaire);
		
		m_isSignAgent=false;
		m_isSignDuo=false;
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
		LoadSignatureAgent();
		LoadSignatureDuo();
	}
	
	/**
	 * Initialisation des éléments graphiques
	 */
	private void initGUI(){
		ivSignatureAgent = (ImageView) findViewById(R.id.ivSignatureAgent);
		ivSignatureDuo = (ImageView) findViewById(R.id.ivSignatureDuo);
		
		etComment = (EditText) findViewById(R.id.etComment);
		cbAgent= (CheckBox) findViewById(R.id.checkBoxAgentGen);
		
		dbKD543LinInventaire inv=new dbKD543LinInventaire(m_appState.m_db);
	
		structPassePlat lin=	inv.loadHdr();
		if (lin!=null)
		{
			etComment.setText(lin.FIELD_LIGNEINV_COMMENT1);
			cbAgent.setChecked(Fonctions.convertToBool(lin.FIELD_LIGNEINV_DUO));
		}
	}
	
	private void initListeners(){
		ivSignatureAgent.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Bundle bundle = new Bundle();
				bundle.putString("filename", "inventaireagent_"+mCodeInv);

				Intent i = new Intent(ValidationInventaire.this, SignatureActivity.class);
				i.putExtras(bundle);
				startActivityForResult(i,10000);				
			}
		});
		ivSignatureDuo.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Bundle bundle = new Bundle();
				bundle.putString("filename", "inventaireduo_"+mCodeInv);

				Intent i = new Intent(ValidationInventaire.this, SignatureActivity.class);
				i.putExtras(bundle);
				startActivityForResult(i,10000);				
			}
		});
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		addMenu(menu, R.string.inventaire_print,R.drawable.print_icon);
		addMenu(menu, R.string.Annuler, android.R.drawable.ic_menu_close_clear_cancel);
		 
		
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

	 
		case R.string.inventaire_print:
			if (1==0 && m_isSignDuo==false && cbAgent.isChecked())
			{
				promptText("Information", "Il manque la signature de l'agent général", false);
			    return super.onOptionsItemSelected(item);
			}
			if (1==0 && m_isSignAgent==false )
			{
				promptText("Information", "Il manque la signature de l'agent", false);
			    return super.onOptionsItemSelected(item);
			}
			MessageYesNoValider("Vous ne pourrez plus faire de modification. Validez quand même ?");
			
			return true;
		 
		case R.string.Annuler:
			returnCancel();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}

	}

	void LoadSignatureAgent()
	{
		try{
			ExternalStorage externalStorage = new ExternalStorage(this, false);
			//File sdCard = Environment.getExternalStorageDirectory();
			File file = new File(externalStorage.getSignaturesFolder()+File.separator+ "inventaireagent_"+mCodeInv+".jpg");
			if(file.exists()){
				m_isSignAgent=true;
				//Bitmap r = BitmapFactory.decodeFile(file.getAbsolutePath());
				//			Drawable r = (Drawable)BitmapDrawable.createFromPath(file.getAbsolutePath());
				//			iv_signatureclient.setBackgroundDrawable(r);
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inSampleSize = 1;
				myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
				ivSignatureAgent.setImageBitmap(Bitmap.createScaledBitmap(myBitmap, 120, 120, false));
				myBitmap = null;
			}
		}
		catch(Exception e){

		}
	}
	void LoadSignatureDuo()
	{
		try{
			ExternalStorage externalStorage = new ExternalStorage(this, false);
			//File sdCard = Environment.getExternalStorageDirectory();
			File file = new File(externalStorage.getSignaturesFolder()+File.separator+ "inventaireduo_"+mCodeInv+".jpg");
			if(file.exists()){
				m_isSignDuo=true;
				//Bitmap r = BitmapFactory.decodeFile(file.getAbsolutePath());
				//			Drawable r = (Drawable)BitmapDrawable.createFromPath(file.getAbsolutePath());
				//			iv_signatureclient.setBackgroundDrawable(r);
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inSampleSize = 1;
				myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
				ivSignatureDuo.setImageBitmap(Bitmap.createScaledBitmap(myBitmap, 120, 120, false));
				myBitmap = null;
			}
		}
		catch(Exception e){

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
								Intent i=new Intent();
								Bundle b=new Bundle();
								b.putString("comment", etComment.getText().toString());
								b.putString("isagent", cbAgent.isChecked()+"");
								b.putString("issignature", m_isSignAgent+"");
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
}
