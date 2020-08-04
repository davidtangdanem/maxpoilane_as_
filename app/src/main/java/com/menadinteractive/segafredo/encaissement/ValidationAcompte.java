package com.menadinteractive.segafredo.encaissement;

import java.io.File;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.menadinteractive.maxpoilane.BaseActivity;
import com.menadinteractive.maxpoilane.ExternalStorage;
import com.menadinteractive.maxpoilane.R;
import com.menadinteractive.segafredo.plugins.Espresso;

/**
 * Classe de validation de l'enregistrement d'un acompte, enregsitrement du nom d'utilisateur
 * et de la signature + impression
 * @author Damien
 *
 */

public class ValidationAcompte extends BaseActivity{
	
	ImageView ivSignature;
	RelativeLayout rlSignature;
	EditText etNomClient;
	Button bValiderAcompte;
	TextView tvSignatureTitle;
	String mCodeClient, mNumeroEncaissement;
	
	Bitmap myBitmap;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_validation_acompte);
		
		//on récupère les infos venant du bundle pour l'encaissement
		Bundle b = null;
		if(getIntent() != null) b = getIntent().getExtras();
		if(b != null) {
			mCodeClient = b.getString(Espresso.CODE_CLIENT);
			mNumeroEncaissement = b.getString(Encaissement.NUMERO_ENCAISSEMENT);
		}
		
		initGUI();
		initListeners();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		//LoadSignatureClient();
	}
	
	/**
	 * Initialisation des éléments graphiques
	 */
	private void initGUI(){
		ivSignature = (ImageView) findViewById(R.id.ivSignature);
		rlSignature = (RelativeLayout) findViewById(R.id.rlSignature);
		etNomClient = (EditText) findViewById(R.id.etNomClient);
		bValiderAcompte = (Button) findViewById(R.id.bValiderAcompte);
		tvSignatureTitle = (TextView) findViewById(R.id.tvSignatureTitle);
		ivSignature.setVisibility(View.GONE);
		rlSignature.setVisibility(View.GONE);
		tvSignatureTitle.setVisibility(View.GONE);
			
	}
	
	private void initListeners(){
		rlSignature.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//Pas de signature
				/*Bundle bundle = new Bundle();
				bundle.putString("filename", mNumeroEncaissement+"_encaissement");

				Intent i = new Intent(ValidationAcompte.this, SignatureActivity.class);
				i.putExtras(bundle);
				startActivityForResult(i,10000);	
				*/
			}
		});
		
		bValiderAcompte.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//on modifie l'encaissement deja enregistré pour y ajouter le chemin de la 
				//signature ainsi que le nom du signataire
				String nomClient =  etNomClient.getText().toString();
				//String filenameSignature = mNumeroEncaissement+"_encaissement"+".jpg";
				String filenameSignature ="";
				
				//TODO traiter le cas où la modification ne fonctionne pas
				boolean update = Encaissement.updateForEncaissementAcompte(mNumeroEncaissement, nomClient, filenameSignature);
				
				//TODO ajouter un startactivityforresult
				finish();				
			}
		});
	}
	
	void LoadSignatureClient()
	{
		try{
			ExternalStorage externalStorage = new ExternalStorage(this, false);
			//File sdCard = Environment.getExternalStorageDirectory();
			File file = new File(externalStorage.getSignaturesFolder()+File.separator+ mNumeroEncaissement+"_encaissement"+".jpg");
			if(file.exists()){
				//Bitmap r = BitmapFactory.decodeFile(file.getAbsolutePath());
				//			Drawable r = (Drawable)BitmapDrawable.createFromPath(file.getAbsolutePath());
				//			iv_signatureclient.setBackgroundDrawable(r);
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inSampleSize = 1;
				myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
				ivSignature.setImageBitmap(Bitmap.createScaledBitmap(myBitmap, 120, 120, false));
				myBitmap = null;
			}
		}
		catch(Exception e){

		}
	}

}
