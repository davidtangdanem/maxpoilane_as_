package com.menadinteractive.histo;

import java.io.File;
import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.menadinteractive.maxpoilane.BaseActivity;
import com.menadinteractive.printmodels.BluetoothConnectionInsecureExample;
import com.menadinteractive.printmodels.Z420ModelFacture;
import com.menadinteractive.maxpoilane.R;
import com.menadinteractive.segafredo.communs.Fonctions;
import com.menadinteractive.segafredo.communs.Global;
import com.menadinteractive.segafredo.communs.MyWS;
import com.menadinteractive.segafredo.communs.assoc;
import com.menadinteractive.segafredo.communs.myListView;
import com.menadinteractive.segafredo.db.dbKD543LinInventaire;
import com.menadinteractive.segafredo.db.dbKD729HistoDocuments;
import com.menadinteractive.segafredo.db.dbKD731HistoDocumentsLignes;
import com.menadinteractive.segafredo.db.dbSiteProduit;

public class HistoDocumentsLignesActivity extends BaseActivity implements OnClickListener{
	private ProgressDialog m_ProgressDialog = null;
	ArrayList<Bundle> histos;
	Handler handler;
	Handler hPrintResult;
	myListView lv;
	TextView tvNumDoc,tvTypeDoc,tvDateDoc,tvDateEch,tvRemise,tvMntHT,tvMntTVA,tvMntTTC;
	String m_date;
	String m_numDocForDuplication;//si on vient de la facture c'est son um�ro pour recevoir la duplication si besoin
	String m_typeDocForDuplication;
	String m_numdoc;
	String m_codecli;
	String typeDoc;
	String m_loadpdf;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_histodocumentslignes);
		 
		Bundle bundle = this.getIntent().getExtras();
		m_numdoc=getBundleValue(bundle, "numdoc");
		m_date=getBundleValue(bundle, "datedoc");
		m_codecli=getBundleValue(bundle, "codecli");
		m_numDocForDuplication=getBundleValue(bundle, "m_numDocForDuplication");
		typeDoc=getBundleValue(bundle, "typedoc"); 
		m_typeDocForDuplication=getBundleValue(bundle, "m_typeDocForDuplication");
		
		 m_loadpdf=m_date+"_MAX-Poilane_BL_"+m_numdoc+".pdf";
		initGUI();
		setListeners();
	}
	
	void initGUI() {
		lv = (myListView) findViewById(R.id.listView1);
		tvNumDoc=(TextView)findViewById(R.id.tvNumDoc);
		tvTypeDoc=(TextView)findViewById(R.id.tvTypeDoc);
		tvDateDoc=(TextView)findViewById(R.id.tvDateDoc);
		tvDateEch=(TextView)findViewById(R.id.tvDateEch);
		tvRemise=(TextView)findViewById(R.id.tvRemise);
		tvMntHT=(TextView)findViewById(R.id.tvMntHT);
		tvMntTVA=(TextView)findViewById(R.id.tvMntTVA);
		tvMntTTC=(TextView)findViewById(R.id.tvMntTTC);
		hPrintResult=getHandlerPrint();
	 		
		dbKD729HistoDocuments hd=new dbKD729HistoDocuments(m_appState.m_db);
		ArrayList<Bundle> bhisto=hd.get(m_codecli, m_numdoc,typeDoc);
		if (bhisto.size()>0)
		{
			tvNumDoc.setText(getBundleValue(bhisto.get(0),hd.FIELD_NUMDOC));
			typeDoc=getBundleValue(bhisto.get(0),hd.FIELD_TYPEDOC);
			tvTypeDoc.setText(getBundleValue(bhisto.get(0),hd.FIELD_TYPEDOC));
			tvDateDoc.setText(getBundleValue(bhisto.get(0),hd.FIELD_DATEDOC));
			tvDateEch.setText(getBundleValue(bhisto.get(0),hd.FIELD_DATEECH));
			tvRemise.setText(getBundleValue(bhisto.get(0),hd.FIELD_REMISE));
			tvMntHT.setText(getBundleValue(bhisto.get(0),hd.FIELD_MNTHT));
			tvMntTVA.setText(getBundleValue(bhisto.get(0),hd.FIELD_MNTTVA));
			tvMntTTC.setText(getBundleValue(bhisto.get(0),hd.FIELD_MNTTTC));
		}
		
		handler =getHandler();
		PopulateList();
	}
	
	void setListeners()
	{
	}
	
	private void PopulateList() {
		dbKD731HistoDocumentsLignes hd=new dbKD731HistoDocumentsLignes(m_appState.m_db);
		
		histos=hd.get(m_numdoc,m_codecli,typeDoc);
		ArrayList<assoc> assocs =new ArrayList<assoc>();
		
	 
		assocs.add(new  assoc(0,R.id.tvCodeart,hd.FIELD_CODEART));
		assocs.add(new  assoc(0,R.id.tvQte,hd.FIELD_QTE));
		assocs.add(new  assoc(0,R.id.tvQteGr,hd.FIELD_QTEGRAT));
		assocs.add(new  assoc(0,R.id.tvQteFact,hd.FIELD_QTEFACT));
		assocs.add(new  assoc(0,R.id.tvPU,hd.FIELD_PUNET));
		assocs.add(new  assoc(0,R.id.tvRemise,hd.FIELD_REMISE));
		assocs.add(new  assoc(0,R.id.tvMntHT,hd.FIELD_MNTHT));
		assocs.add(new  assoc(0,R.id.tvLblArt,dbSiteProduit.FIELD_PRO_NOMART1));
		assocs.add(new  assoc(1,R.id.iv1,"ICON"));
 
		
		lv.attachValues(R.layout.item_list_histodocumentslignes, histos,assocs,handler);
		
	}
	Handler getHandlerPrint() {
		Handler h = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (m_ProgressDialog!=null) m_ProgressDialog.dismiss();
				super.handleMessage(msg);
				Bundle bGet = msg.getData();
				if (msg.what!=BluetoothConnectionInsecureExample.ERRORMSG_OK)
				{
					 promptText("Problème d'impression",BluetoothConnectionInsecureExample.getErrMsg(msg.what), false);
			 		
				}
				else
				{
			 
				}
			}
		};
		return h;
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}
	
	Handler getHandler() {
		Handler h = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (m_ProgressDialog!=null) m_ProgressDialog.dismiss();
				
				super.handleMessage(msg);
				Bundle bGet = msg.getData();
				 
				switch (msg.what) {
				case R.id.iv2:
				 
					int  id=bGet.getInt("position");
				 
					break;
					
			

				}
				
			}
			
		};
		return h;
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		addMenu(menu, R.string.commande_pdf, R.drawable.bl);
		
		
		/*if (m_numDocForDuplication.equals(""))
			
		else
			addMenu(menu, R.string.dupliquer,android.R.drawable.stat_notify_sync);
		*/
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		
		case R.string.commande_pdf:
			
			String PATH = Environment.getExternalStorageDirectory() +File.separator+"download/"+m_loadpdf;
			File file = new File(PATH);
			
			if(file.exists()==false)
			{
				String url = MyWS.LOAD_WEB+m_loadpdf;
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse(url));
				startActivityForResult(i, 20000);
			}
			else
			{
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
				else
				Fonctions.FurtiveMessageBox(this, "Pas de PDF pour ce BL.");
				
				
			}
			
			
			return true;

		case R.string.commande_printduplicata:
			//Copy 
			// histoent ->histoduplicata
			Global.dbKDHistoDocuments.Copy_Duplicata(m_numdoc,m_codecli);
			
			// histoligne ->histoduplicata
			Global.dbKDHistoLigneDocuments.Copy_ligne_Duplicata(m_numdoc,m_codecli);
			
			MessageYesNo(getString(R.string.commande_duplicata),
					R.string.commande_printduplicata,"Résumé");
			
			return true;
		case R.string.dupliquer:
			dbKD731HistoDocumentsLignes lin=new dbKD731HistoDocumentsLignes(m_appState.m_db);
			if (lin.Duplicate(m_numdoc, m_numDocForDuplication, m_codecli, getLogin(),getSocCode(),typeDoc,m_typeDocForDuplication ))
			{
				promptText("Copie de pièce", "La pièce a été correctement copiée", true);
			}
			else
			{
				promptText("Copie de pièce", "Problème lors de la copie", false);
			}
			
			break;
		
		default:
			return super.onOptionsItemSelected(item);
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void MessageYesNo(String message, int type,String title) {
		final int m_type = type;
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(message)
		.setCancelable(false)
		.setTitle(title)
		.setPositiveButton(getString(android.R.string.yes),
				new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				switch (m_type) {
				case R.string.commande_printduplicata:
					launchPrinting();
					StringBuffer err = new StringBuffer();
					Global.dbKDHistoDocuments.delete_duplicata(m_numdoc, err);
					Global.dbKDHistoLigneDocuments.delete_ligne_duplicata(m_numdoc, err);
					
					break;
				
				}

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
	public void AlertMessage(String message) {

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(message)
		.setCancelable(false)
		.setPositiveButton(getString(android.R.string.ok),
				new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {

			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}
	void launchPrinting() {

		m_ProgressDialog=ProgressDialog.show(this, getString(R.string.communication_avec_l_imprimante), getString(R.string.patientez_));

		 
		String mac=getPrinterMacAddress();
		   BluetoothConnectionInsecureExample example = new BluetoothConnectionInsecureExample(hPrintResult);
              Z420ModelFacture z=new Z420ModelFacture(this);
              String   zplData=z.getFacture(m_numdoc,m_codecli,true,typeDoc);
	        example.sendZplOverBluetooth(mac,zplData,1);
		 
	
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if(requestCode==20000)
		{
			String PATH = Environment.getExternalStorageDirectory() +File.separator+"download/"+m_loadpdf;
			File file = new File(PATH);
			if( file.exists()==true)
			{
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
			
			else
				Fonctions.FurtiveMessageBox(this, "Pas de PDF pour ce BL.");
				
			
		}
			alertModeTest();
			PopulateList();
		}
	
	

}
