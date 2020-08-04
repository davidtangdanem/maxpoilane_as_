package com.menadinteractive.histo;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.menadinteractive.maxpoilane.BaseActivity;
import com.menadinteractive.printmodels.BluetoothConnectionInsecureExample;
import com.menadinteractive.printmodels.Z420ModelEncaissement;
import com.menadinteractive.printmodels.Z420ModelEncaissementDuplicata;
import com.menadinteractive.maxpoilane.R;
import com.menadinteractive.segafredo.communs.Fonctions;
import com.menadinteractive.segafredo.communs.Global;
import com.menadinteractive.segafredo.communs.assoc;
import com.menadinteractive.segafredo.communs.myListView;
import com.menadinteractive.segafredo.contactcli.fichecontactcli;
import com.menadinteractive.segafredo.db.Preferences;
import com.menadinteractive.segafredo.db.TableSouches;
import com.menadinteractive.segafredo.db.dbKD543LinInventaire;
import com.menadinteractive.segafredo.db.dbKD729HistoDocuments;
import com.menadinteractive.segafredo.db.dbKD84LinCde;
import com.menadinteractive.segafredo.db.TableClient.structClient;
import com.menadinteractive.segafredo.encaissement.Encaissement;
import com.menadinteractive.segafredo.plugins.Espresso;
import com.menadinteractive.segafredo.remisebanque.Cheque;

public class HistoDocumentsActivity extends BaseActivity implements OnClickListener {
	
	ArrayList<Bundle> histos;
	Handler handler;
	ImageButton ibFind;
	EditText etFilter;
	TextView tvTitre;
	myListView lv;
	String m_codecli;
	String m_numDocForDuplication;//si on vient de la facture c'est son um�ro pour recevoir la duplication si besoin
	String m_typeDocForDuplication;
	Handler hPrintResultRGDuplic;
	private ProgressDialog m_ProgressDialog = null; 
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_histodocuments);
		 
		Bundle bundle = this.getIntent().getExtras();
		m_codecli=getBundleValue(bundle, Espresso.CODE_CLIENT);
		m_numDocForDuplication=getBundleValue(bundle, "m_numDocForDuplication");
		m_typeDocForDuplication=getBundleValue(bundle, "m_typeDocForDuplication");
		
		
		initGUI();
		setListeners();
		DispInfoCli();
	}
	
	void initGUI() {
		lv = (myListView) findViewById(R.id.listView1);
		etFilter= (EditText) findViewById(R.id.etFilter);
		ibFind= (ImageButton) findViewById(R.id.ibFind);
		tvTitre=(TextView) findViewById(R.id.textViewTitre);
		
		Fonctions.setFont(this, tvTitre, Global.FONT_REGULAR);
		
		handler =getHandler();
		PopulateList();
		hPrintResultRGDuplic=getHandlerPrintRG();
		
	}
	Handler getHandlerPrintRG() {
		Handler h = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (m_ProgressDialog!=null) m_ProgressDialog.dismiss();
				super.handleMessage(msg);
				Bundle bGet = msg.getData();
				if (msg.what!=BluetoothConnectionInsecureExample.ERRORMSG_OK)
				{
				//	promptText("Problème d'impression",BluetoothConnectionInsecureExample.getErrMsg(msg.what), false);
					promptText(getString(R.string.probl_me_d_impression),BluetoothConnectionInsecureExample.getErrMsg(msg.what), false);
					
				}
				else
				{
					
				 
				}
			}
		};
		return h;
	}
	void setListeners()
	{
	
		
		ibFind.setOnClickListener(this);
	}
	
	private void PopulateList() {
		dbKD729HistoDocuments hd=new dbKD729HistoDocuments(m_appState.m_db);
		
		histos=hd.get(m_codecli,"","");
		ArrayList<assoc> assocs =new ArrayList<assoc>();
	 
		assocs.add(new  assoc(0,R.id.tvTypeDoc,hd.FIELD_TYPEDOC));
		assocs.add(new  assoc(0,R.id.tvNumDoc,hd.FIELD_NUMDOC));
		assocs.add(new  assoc(0,R.id.tvDateDoc,hd.FIELD_DATEDOC));
		assocs.add(new  assoc(0,R.id.tvDateEch,hd.FIELD_DATEECH));
		assocs.add(new  assoc(0,R.id.tvMnt,hd.FIELD_MNTTTC));
		assocs.add(new  assoc(1,R.id.iv1,"ICON"));
 
		
		lv.attachValues(R.layout.item_list_histodocuments, histos,assocs,handler);
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
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
					dbKD729HistoDocuments hde=new dbKD729HistoDocuments(m_appState.m_db);
					String codedoc=histos.get(id).getString(hde.FIELD_NUMDOC);
					String codecli=histos.get(id).getString(hde.FIELD_CODECLIENT);
					String typedoc=histos.get(id).getString(hde.FIELD_TYPEDOC);
					String datedoc=histos.get(id).getString(hde.FIELD_DATEDOC);
					datedoc=Fonctions.dd_mm_yyyy_TO_yyyymmdd(datedoc);
					
					
					if (typedoc.equals(TableSouches.TYPEDOC_REGLEMENT))
					{
						MessageYesNoDuplicateRG(getString(R.string.imprimer_un_duplicata_du_r_glement_),codedoc);
					}
					else {
						if (isAllowedToDuplicate(typedoc))
							launchStatsDetail(datedoc,codedoc,codecli,m_numDocForDuplication,typedoc,m_typeDocForDuplication);
						else
							launchStatsDetail(datedoc,codedoc,codecli,"",typedoc,m_typeDocForDuplication);
					
					}
					
					break;
				 
				}

			}
		};
		return h;
	}
	
	/*on autorise les duplication
	FA->FA
	FA->AV
	AV->FA
	BL->RT
	RT->BL	
	BL->BL
	*/
	boolean isAllowedToDuplicate(String typeDoc)
	{
		dbKD84LinCde lin=new dbKD84LinCde(m_appState.m_db);
		//si la commande a d�j� �t� commenc�e, on ne peut pas dupliquer
		if (lin.Count_Numcde(m_numDocForDuplication, false)>0)
		{
			return false;
		}
		if (m_typeDocForDuplication.equals(TableSouches.TYPEDOC_FACTURE) && typeDoc.equals(TableSouches.TYPEDOC_FACTURE))
			return true;
		if (m_typeDocForDuplication.equals(TableSouches.TYPEDOC_AVOIR) && typeDoc.equals(TableSouches.TYPEDOC_FACTURE))
			return true;
		if (m_typeDocForDuplication.equals(TableSouches.TYPEDOC_FACTURE) && typeDoc.equals(TableSouches.TYPEDOC_AVOIR))
			return true;
		if (m_typeDocForDuplication.equals(TableSouches.TYPEDOC_BL) && typeDoc.equals(TableSouches.TYPEDOC_RETOUR))
			return true;
		if (m_typeDocForDuplication.equals(TableSouches.TYPEDOC_BL) && typeDoc.equals(TableSouches.TYPEDOC_BL))
			return true;
		if (m_typeDocForDuplication.equals(TableSouches.TYPEDOC_RETOUR) && typeDoc.equals(TableSouches.TYPEDOC_BL))
			return true;
		
		return false;
	}
	
	void DispInfoCli()
	{
		structClient cli=new structClient();
		Global.dbClient.getClient(m_codecli, cli, new StringBuilder());
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
		tvEncoursClient.setVisibility(View.INVISIBLE);
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		//on revient de la cde, si on a pris une commande on ne peut pas annuler le prospect
		//on recharge la fiche aussi car peut �tre modifiŽe dans la cde
		if(resultCode==RESULT_OK )
		{
			returnOK();
		}

		hideKeyb();

	}
	
	public void MessageYesNoDuplicateRG(String message,final  String numsouche) {
		 
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(message)
				.setCancelable(false)
				.setPositiveButton(getString(android.R.string.yes),
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {
								printRGDuplicata(  numsouche);
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
	
	void printRGDuplicata(String numsouche)
	{
		m_ProgressDialog=ProgressDialog.show(this, getString(R.string.communication_avec_l_imprimante), getString(R.string.patientez_));

		 
		String mac=getPrinterMacAddress();
		BluetoothConnectionInsecureExample example = new BluetoothConnectionInsecureExample(hPrintResultRGDuplic);
		Z420ModelEncaissementDuplicata model=new Z420ModelEncaissementDuplicata(this);
		String   zplData=model.get(m_codecli, numsouche);
		if(zplData != null) example.sendZplOverBluetooth(mac,zplData,1);
	}
}
