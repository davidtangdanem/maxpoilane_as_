package com.menadinteractive.commande;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.menadinteractive.maxpoilane.BaseActivity;
import com.menadinteractive.maxpoilane.ExternalStorage;
import com.menadinteractive.maxpoilane.R;
import com.menadinteractive.segafredo.communs.Fonctions;
import com.menadinteractive.segafredo.communs.Global;
import com.menadinteractive.segafredo.communs.assoc;
import com.menadinteractive.segafredo.communs.myListView;
import com.menadinteractive.segafredo.db.TableClient;
import com.menadinteractive.segafredo.db.TableSouches;
import com.menadinteractive.segafredo.db.TableTarif.structTarif;
import com.menadinteractive.segafredo.db.dbKD545StockTheoSrv;
import com.menadinteractive.segafredo.db.dbKD731HistoDocumentsLignes;
import com.menadinteractive.segafredo.db.dbParam;
import com.menadinteractive.segafredo.db.dbKD84LinCde.structLinCde;
import com.menadinteractive.segafredo.db.dbSiteProduit.structArt;

public class commandeInput extends BaseActivity  {
	ArrayList<Bundle> histos;
	Handler handler;
	myListView lv;
	
	String m_codeart;
	String m_lblart;
 
	
	//String m_prix;
	String m_qte,m_remise;
	String m_grat;
 
	String m_numcde;
	String m_photo;
 
	
	String m_codcli;
	String m_prix_pcb;
	String m_prix_unitaire;
	structArt m_art;
	structTarif m_tarif;
	
	int stockCourant;

	TextView tvTitre;
	TextView tvCodeart;
	TextView tvBarcode;
	TextView tvStock;
	EditText etQte;
	EditText etGrat;
	EditText etPrix;
	EditText etPUNet;
	EditText etTotNet;
	EditText etRemise;
 

	ImageButton bOk;
	ImageButton bCancel;
 
	ImageView imgPetite;
	String m_typedoc="";
	Spinner spinEtat;
 
	boolean prixspecial=false;
	LinearLayout llEtat, llGrat;

	structLinCde line;
	ArrayList<Bundle> idEtat     = null;// les id qui servent a retrouver les pays
	ArrayList<Bundle> idFamRemise = null;// les id qui servent a retrouver les pays


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cde_inputdlg2);   

		Bundle bundle = this.getIntent().getExtras();
		m_codeart =getBundleValue(bundle, "codeart") ; 
		m_lblart =getBundleValue(bundle, "lblart") ;
 
		m_qte =getBundleValue(bundle, "qte") ;
		m_remise =getBundleValue(bundle, "remise") ;
		m_grat =getBundleValue(bundle, "grat") ;
		m_numcde =getBundleValue(bundle, "numcde") ;
		m_photo=getBundleValue(bundle, "photoname") ;
		m_typedoc=getBundleValue(bundle, "typedoc") ;
		
		m_codcli=getBundleValue(bundle, "codcli") ;

		line=new structLinCde();
		idEtat = new ArrayList<Bundle>();
		idFamRemise = new ArrayList<Bundle>();

		m_art=new structArt();
		Global.dbProduit.getProduit(m_codeart, m_art, new StringBuilder());

		

		initGui();
		initListeners();

 
		DispImage(imgPetite);
		etQte.setFocusable(true);
		etQte.requestFocus();

	 	}

	void initGui()
	{
		lv = (myListView) findViewById(R.id.lvhisto);
		tvTitre=(TextView)findViewById(R.id.textViewTitre);
		tvTitre.setText( m_lblart);

		tvCodeart=(TextView)findViewById(R.id.textViewCodeart);
		tvCodeart.setText(m_codeart );
		
		tvBarcode=(TextView)findViewById(R.id.textViewBarcode);
		tvBarcode.setText( m_art.EAN);
		
		tvStock=(TextView)findViewById(R.id.tvStock);
/*		dbKD543LinInventaire.structPassePlat pp=new structPassePlat();
		Global.dbKDLinInv.load(pp, m_codeart, new StringBuffer(),
				Preferences.getValue(this, Espresso.DEPOT,"0"),
				Preferences.getValue(this, Espresso.LOGIN,"0"),
				Preferences.getValue(this, Espresso.CODE_SOCIETE,"0")
				);
		tvStock.setText(getString(R.string.stock_)+pp.FIELD_LIGNEINV_QTETHEO);
*/
		dbKD545StockTheoSrv stktheo=new dbKD545StockTheoSrv(getDB());
		com.menadinteractive.segafredo.db.dbKD545StockTheoSrv.structPassePlat ent=new com.menadinteractive.segafredo.db.dbKD545StockTheoSrv.structPassePlat();
		stktheo.load(ent, m_codeart, new StringBuffer(), "", "", "");
		tvStock.setText(getString(R.string.stock_)+ent.FIELD_LIGNEINV_QTETHEO);
		stockCourant=Fonctions.convertToInt(ent.FIELD_LIGNEINV_QTETHEO);
		
		llGrat=(LinearLayout)findViewById(R.id.llGrat);
		llEtat=(LinearLayout)findViewById(R.id.llEtat);
		
		etPrix=(EditText)findViewById(R.id.editTextPrix);
		etQte=(EditText)findViewById(R.id.editTextQte);
		etGrat=(EditText)findViewById(R.id.editTextQteGrat);
		etRemise=(EditText)findViewById(R.id.editTextRemise);
		etPUNet=(EditText)findViewById(R.id.editTextPrixNet);
		etTotNet=(EditText)findViewById(R.id.editTextMntNet);
	
		
		spinEtat=(Spinner)findViewById(R.id.spinnerEtat);
		if (m_typedoc.equals(TableSouches.TYPEDOC_BL) || 
				m_typedoc.equals(TableSouches.TYPEDOC_RETOUR))
			llEtat.setVisibility(View.VISIBLE);
		
		etRemise.setFilters(new InputFilter[]{ new InputFilterMinMaxFloat(0f, 35.f)});
		//TODO remplir avec la structTarif récupérer à partir du produit et du client
		/*etPrix.setText(calcPrixUInitial());
		etPrix.setEnabled(false);

		etPrixPCB.setText(calcPrixPCB());
		etPrixPCB.setEnabled(false);*/
		String tarif="0";
		m_tarif=new structTarif();
		
		 etPrix.setTextColor(Color.BLACK);
	//	tvStock.setVisibility(View.INVISIBLE);
		if(!m_codeart.equals("")){
			if(!m_codcli.equals(""))
			{
				String codetarif="";
				TableClient cli=new TableClient(m_appState.m_db);
				
				codetarif= cli.getClientCodeTarif(m_codcli);
				if (Global.dbTarif.getTarif(m_codcli,codetarif, m_codeart, m_tarif, new StringBuilder()))
				{
					prixspecial=true;
					tarif=m_tarif.PX_NET;
					etRemise.setEnabled(false);
					 etPrix.setBackgroundColor(Color.MAGENTA);
					
				}
				else
				{
					etRemise.setEnabled(true);
				 
					tarif=m_art.PV_CONS;
				}
			}
			else
			{
				etRemise.setEnabled(true);
				tarif=m_art.PV_CONS;
			}
				//Global.dbTarif.getTarifWithoutClient(Integer.parseInt(m_codeart), m_tarif, new StringBuilder());
		}
		
		//m_prix_unitaire = calculateUnitPrice(tarif, m_art.PCB);
		//m_prix_unitaire = "0.00";
		m_prix_unitaire=m_prix_pcb = convertPrice(tarif);
		
		etPrix.setText(m_prix_unitaire);
		etPrix.setEnabled(false);
		etPUNet.setEnabled(false);
		etTotNet.setEnabled(false);
	
	 
		Log.i("", "qte : "+m_qte);
		String qte = m_qte.split(".")[0];
		etQte.setText(qte);
		etGrat.setText(m_grat);
	 

		bOk=(ImageButton)findViewById(R.id.imageButtonOk);
		bCancel=(ImageButton)findViewById(R.id.imageButtonCancel);

		 
		imgPetite=(ImageView)findViewById(R.id.imageViewPetite);
	 
		if (m_numcde.equals(""))
		{
			etGrat.setVisibility(View.GONE);
			etQte.setVisibility(View.GONE);
			bOk.setVisibility(View.GONE);

		}

		if (Global.dbKDLinCde.load(line, m_numcde, m_codeart, "", new StringBuffer()))
		{
			etQte.setText((int)line.QTECDE+"");
			etGrat.setText((int)line.QTEGR+"");
			etPrix.setText( line.PRIXMODIF+"");
			m_prix_unitaire=m_prix_pcb= (line.PRIXMODIF)+"";
			etRemise.setText( line.REMISEMODIF+"");
		}

		if (Fonctions.GetStringDanem(m_art.ISGRAT).equals("O")==false)
		{
			llGrat.setVisibility(View.GONE);
		}
		else
			llGrat.setVisibility(View.VISIBLE);

		fillEtat(line.FIELD_LIGNECDE_LINCHOIX1);
	 
	 
		handler =getHandler();
		PopulateList();
		etRemise.setVisibility(View.INVISIBLE);
		etPrix.setEnabled(false);
		
		
	}
	private void PopulateList() {
		dbKD731HistoDocumentsLignes hd=new dbKD731HistoDocumentsLignes(m_appState.m_db);
		
		histos=hd.getHistoArt(m_codeart, m_codcli);
		ArrayList<assoc> assocs =new ArrayList<assoc>();
	 
		assocs.add(new  assoc(0,R.id.tvDate,"datedoc"));
		assocs.add(new  assoc(0,R.id.tvQte,hd.FIELD_QTE));
		assocs.add(new  assoc(0,R.id.tvPU,hd.FIELD_PUNET));
		assocs.add(new  assoc(0,R.id.tvRemise,hd.FIELD_REMISE));
		assocs.add(new  assoc(0,R.id.tvMntHT,hd.FIELD_MNTHT));
		//assocs.add(new  assoc(0,R.id.tvLblArt,dbSiteProduit.FIELD_PRO_NOMART1));
		assocs.add(new  assoc(1,R.id.iv1,"ICON"));
 
		
		lv.attachValues(R.layout.item_list_histoart, histos,assocs,handler);
		
		if (histos.size()>0)
		{
			String remise=etRemise.getText().toString();
			if (remise.equals("") && 	  (prixspecial==false))
				etRemise.setText(histos.get(0).getString(hd.FIELD_REMISE));
		}
	}
	Handler getHandler() {
		Handler h = new Handler() {
			@Override
			public void handleMessage(Message msg) {
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
/*
	String calcPrixPCB()
	{
		float calc=
				Fonctions.convertToFloat(getPrixApplique())*Fonctions.convertToFloat(m_art.PCB);

		return Fonctions.round(calc, 2)+"";
	}
	String calcPrixUInitial()
	{
		float calc=
				Fonctions.convertToFloat(m_art.PV_CONS);

		return Fonctions.round(calc, 2)+"";
	}
*/
	/*
	//on applique le prix en fonction de la remise
	String getPrixApplique()
	{
		String prix=m_art.PV_CONS;

		float remise=Fonctions.convertToFloat(getRemise());
		double prixNet=Fonctions.convertToFloat(prix)-Global.dbProduit.Calculpourcent(Fonctions.convertToFloat(prix), remise);
		prixNet=Fonctions.round(prixNet, 2);
		prix=prixNet+"";

		return prix;
	}
*/	String getRemise()
	{
		try {
			 
			return etRemise.getText().toString();
		} catch (Exception e) {
			return "0";
		}
	}


	/**
	 * Permet de calculer le prix unitaire du produit
	 * @return String
	 */
/*	String calculateUnitPrice(String price, String pcb){
		double pricePCB = 0;
		double PCB = 0;
		double priceUnit = 0;
		price = price.replace(",", ".");
		try{
			pricePCB = Fonctions.round(Double.parseDouble(price), 2);
			PCB = Double.parseDouble(pcb);
			priceUnit = pricePCB/PCB;
			priceUnit = Fonctions.round(priceUnit, 2);
		}catch(Exception ex){

		}
		return Double.toString(priceUnit);
	}
*/
	/**
	 * Permet de convertir un prix (X.XX)
	 * @return String
	 */
	String convertPrice(String price){
		double priceReturn = 0;
		price = price.replace(",", ".");
		try{
			priceReturn = Fonctions.round(Double.parseDouble(price), 2);
		}catch(Exception ex){
			
		}
		return Fonctions.GetDoubleToStringFormatDanem(priceReturn,"0.00");
	}

	String getQte()
	{
		return etQte.getText().toString();
	}
	String getQteGrat()
	{
		return etGrat.getText().toString();
	}
	
	void validation()
	{
		Intent result = new Intent();
		Bundle b = new Bundle();
		b.putString("qte", getQte());
		b.putString("grat", getQteGrat());
		//b.putString("prix", getPrixApplique());
		//b.putString("prixintit", calcPrixUInitial() );
		b.putString("prix", m_prix_pcb);
		b.putString("prixintit", m_prix_unitaire );
		b.putString("codcli", m_codcli);
		b.putString("codeart", m_codeart);
		b.putString("lblart", m_lblart);
		b.putString("remise", getRemise() );

		int etatSel=    getSpinnerSelectedIdx(spinEtat);
		if (etatSel==0)
			b.putString("motif", "");
		else
		{
			String val=idEtat.get (etatSel).getString(dbParam.FLD_PARAM_CODEREC);
			b.putString("motif", val);
		}
		result.putExtras(b);
		setResult(Activity.RESULT_OK, result);

		finish();
	}
	
	void initListeners()
	{
		bOk.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				String codeclirep=Fonctions.Right(m_codcli, 6);
				int motif=getSpinnerSelectedIdx(spinEtat);
				
				//si BL ou RT et regule de stock livreur (codecli 9999) et motif pas renseigné on bloque
				if ( (m_typedoc.equals(TableSouches.TYPEDOC_BL) || m_typedoc.equals(TableSouches.TYPEDOC_RETOUR)) 
						&& codeclirep.equals("999999")
						&& motif==0)			
				{
					return;
				}
				
				
				int iQteFact = 0 ;
				iQteFact=Fonctions.GetStringToIntDanem(getQte())-Fonctions.GetStringToIntDanem(getQteGrat());
				if(iQteFact<0)
				{
					
					MessageYesNo("Vous avez saisie plus de retour que livraison", "Attention risque d\'erreur ! Vérifier votre saisie");
					
				}
				else
				validation();
				
					
				
				
				
				//si autre doc que retour et stock insuffisant on alerte
				/*if (m_typedoc.equals(TableSouches.TYPEDOC_RETOUR)==false && stockCourant-(Fonctions.convertToInt(etQte.getText().toString()) +Fonctions.convertToInt(etGrat.getText().toString()))<0 )
				{
					MessageYesNo(getString(R.string.la_quantit_livrer_est_sup_rieur_au_stock_th_orique_continuer_), "Attention stock");
				}
				else */
					
 			}
		});

		bCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();

			}
		});

		imgPetite.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				viewFullSizeImage();

			}
		});

		 

	 
	}

	void DispImage(ImageView img)
	{

		String fullPath = ExternalStorage.getFolderPath(ExternalStorage.ROOT_FOLDER);
		File jpgFile = new File(fullPath + m_photo+".jpg");
		 
		
		Bitmap thumbnail = null;
		// if (jpgFile.exists()) //on enleve le test car ca
		// fait trop ralentir la liste
		// on laisse la gestion de l'erreur au catch
		{
			// Log.d("TAG", "file exist " + photoname);
			try {
				thumbnail = BitmapFactory.decodeStream(
						new FileInputStream(jpgFile), null,
						null);

				// else
				// thumbnail = defaultThumbnailBitmap;

			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				//img.setBackgroundResource(R.drawable.basic1_016_search_zoom_find);
				img.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
				e.printStackTrace();
				Log.d("TAG",
						"error decoding" + e.toString());
				
				return;
			}
		}

		img.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
		img.setImageBitmap(thumbnail);

	}

	private void viewFullSizeImage(){

		String fullPath = ExternalStorage.getFolderPath(ExternalStorage.ROOT_FOLDER);

		//		File jpgFile = new File(fullPath + CommandeState.currentProduct.PHOTONAME+ ".jpg");
		File jpgFile = new File(fullPath + m_photo+".jpg");

		if(jpgFile.exists()){
			Intent intent = new Intent();  
			intent.setAction(android.content.Intent.ACTION_VIEW);  

			//intent.setData(Uri.fromFile(file));
			intent.setDataAndType(Uri.fromFile(jpgFile), "image/*");  
			startActivity(intent);  
		}

	}

	void fillEtat(String selVal) {
		try {
			//				if (Global.dbParam.getRecordsFiltreAllSoc(Global.dbParam.PARAM_FAM1,this.idFam, "1") == true) {
			if (Global.dbParam.getRecord2s(Global.dbParam.PARAM_FAM4, idEtat,true) == true) {

				Bundle bundle = new Bundle();

				int pos = 0;
				String[] items = new String[idEtat.size()];
				for (int i = 0; i < idEtat.size(); i++) {

					items[i] = idEtat.get(i).getString(
							Global.dbParam.FLD_PARAM_LBL);

					String codeRec = idEtat.get(i).getString(
							Global.dbParam.FLD_PARAM_CODEREC);

					if (selVal.equals(codeRec)) {
						pos = i;
					}

				}

				ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
						android.R.layout.simple_spinner_item, items);
				adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				spinEtat.setAdapter(adapter);

				spinEtat.setSelection(pos);

			}

		} catch (Exception ex) {

		}

	}
	
	public void MessageYesNo(String message, String title) {
		 
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(message)
		.setCancelable(false)
		.setTitle(title)
		.setPositiveButton(getString(R.string.Yes),
				new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				 validation();
		 
			}
		});
		/*
		.setNegativeButton(getString(R.string.No),
				new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {

			}
		});*/
		AlertDialog alert = builder.create();
		alert.show();
	}
	
	public class InputFilterMinMaxFloat implements InputFilter {

	    private float min, max;

	    public InputFilterMinMaxFloat(float min, float max) {
	        this.min = min;
	        this.max = max;
	    }

	  

	    @Override
	    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {   
	        try {
	        	String decimalpart=Fonctions.GiveFld(dest.toString(), 1, ".",true);
	        	if (decimalpart.length()>=2)
	        		return "";
	            float input = Float.parseFloat(dest.toString() + source.toString());
	            if (isInRange(min, max, input))
	                return null;
	        } catch (NumberFormatException nfe) { }     
	        return "";
	    }

	    private boolean isInRange(float a, float b, float c) {
	        return b > a ? c >= a && c <= b : c >= b && c <= a;
	    }
	}
}
