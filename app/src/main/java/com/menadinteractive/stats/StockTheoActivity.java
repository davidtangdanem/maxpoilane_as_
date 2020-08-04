package com.menadinteractive.stats;

import java.util.ArrayList;
import java.util.Calendar;

import android.R.bool;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.menadinteractive.commande.Printanem;
import com.menadinteractive.maxpoilane.BaseActivity;
import com.menadinteractive.printmodels.BluetoothConnectionInsecureExample;
import com.menadinteractive.printmodels.Z420ModelFacture;
import com.menadinteractive.printmodels.Z420ModelInventaire;
import com.menadinteractive.maxpoilane.R;
import com.menadinteractive.segafredo.communs.Fonctions;
import com.menadinteractive.segafredo.communs.Global;
import com.menadinteractive.segafredo.db.MyDB;
import com.menadinteractive.segafredo.db.Preferences;
import com.menadinteractive.segafredo.db.TableSouches;
import com.menadinteractive.segafredo.db.TableSouches.passeplat;
import com.menadinteractive.segafredo.db.dbKD543LinInventaire;
import com.menadinteractive.segafredo.db.dbKD543LinInventaire.structPassePlat;
import com.menadinteractive.segafredo.db.dbSiteProduit;
import com.menadinteractive.segafredo.plugins.Espresso;
import com.menadinteractive.segafredo.tasks.task_getStockTheoWSData;
import com.menadinteractive.segafredo.tasks.task_sendInventaireWSData;
import com.menadinteractive.segafredo.tasks.task_sendWSData;

public class StockTheoActivity extends BaseActivity 
	implements OnItemSelectedListener
{
	private listAdapter m_adapter;

	boolean partielok=false;
	TextView tvTitre;
	boolean m_isPrinted=false;
	private ProgressDialog m_ProgressDialog = null;
	Handler m_taskhandler;
 
	ListView myListView;
	int lvPosition;
	String m_soccode;
	int top = 0;
	int index = 0;
	ArrayList<Bundle> idFam = null;// les id qui servent a retrouver les pays
	String m_stFam = "" ;	//Filtre Fam (voir spinner)
 
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_stocktheo); 

		idFam = new ArrayList<Bundle>();
		m_soccode=Preferences.getValue(this, Espresso.CODE_SOCIETE, "0");
 		
	
		initGUI();
		initListeners();
		fillFamille("");
	  
		
		m_ProgressDialog=ProgressDialog.show(this, getString(R.string.r_ception_du_stock_th_orique), getString(R.string.patientez_));
		
		task_getStockTheoWSData task=new task_getStockTheoWSData(this, m_taskhandler);
		task.execute();
		
		Fonctions.WriteProfileString(this, Espresso.REG_DATEDERINV, Fonctions.getYYYYMMDD());
		
	}

	void initGUI() {
		myListView = (ListView) findViewById(R.id.lv_4);
		myListView.setTextFilterEnabled(true);

		tvTitre = (TextView) findViewById(R.id.titre);
		
		tvTitre.setText("Rapport stock");
		m_taskhandler=getHandler();
	//	PopulateList();
	}

	void initListeners() {
	 		Spinner spinner = (Spinner) findViewById(R.id.spinnerFam);
		spinner.setOnItemSelectedListener(this);

	}
	 
	private void PopulateList() {

		ArrayList<dbSiteProduit.structArt> colNames = new ArrayList<dbSiteProduit.structArt>();

		this.m_adapter = new listAdapter(this, R.layout.item_stocktheo, colNames);

		myListView.setAdapter(this.m_adapter);

		ArrayList<dbSiteProduit.structArt> arts =null;
		if (m_stFam.equals(getString(R.string.tous))==true)
			arts=Global.dbProduit.getProduitsStock (
				 "");
			
		else
		 
				arts=Global.dbProduit.getProduitsStock(
					 m_stFam);

		for (int v = 0; v < arts.size(); v++)
			colNames.add(arts.get(v));

	}

	private class listAdapter extends ArrayAdapter<dbSiteProduit.structArt> {

		private ArrayList<dbSiteProduit.structArt> items;

		public listAdapter(Context context, int textViewResourceId,
				ArrayList<dbSiteProduit.structArt> items) {
			super(context, textViewResourceId, items);
			this.items = items;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			if (v == null) {
				LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.item_stocktheo, null);
			}

			dbSiteProduit.structArt o = items.get(position);

			if (o != null) {

	 			TextView tvCode = (TextView) v.findViewById(R.id.textCode); 
				tvCode.setText(o.CODART );
				
				TextView tvLbl = (TextView) v.findViewById(R.id.textLbl); 
				tvLbl.setText(o.NOMART1);
 			
	 			TextView tvQteTheo = (TextView) v.findViewById(R.id.qte);
	 			tvQteTheo.setText("S.Theo: "+o.QTEINV);
			 
	 			if (Fonctions.convertToInt(o.QTEINV)<0)
	 				tvQteTheo.setTextColor(Color.RED);
	 			else
	 				tvQteTheo.setTextColor(Color.BLACK);
				 
		 
			}
			return v;
		}

	}

	
 

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
 		addMenu(menu, R.string.inventaire_quit, android.R.drawable.ic_menu_close_clear_cancel);
	 	
		 
		return true;
	}

	 
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

	
		case R.string.inventaire_quit:
		 
			
			
			finish();
		
			return true;
	 
		
		default:
			return super.onOptionsItemSelected(item);
		}

	}

	boolean synchro()
	{
	 
		
		dbKD543LinInventaire inv2=new dbKD543LinInventaire(m_appState.m_db);
		if (inv2.isInventaireTermine()==false  && partielok==false)
		{
			promptText(getString(R.string.transmission_de_l_inventaire), getString(R.string.impossible_l_inventaire_n_est_pas_finalis_), false);
			return false;
		}
		
	//	MessageYesNo(getString(R.string.commande_valider_msg),
	//			R.string.commande_valider);

		m_ProgressDialog=ProgressDialog.show(this, getString(R.string.envoi_de_l_inventaire_vers_le_serveur), getString(R.string.patientez_));
		
		
		task_sendInventaireWSData wsCde = new task_sendInventaireWSData(
				m_appState,m_taskhandler);
		wsCde.execute();
		
		
		return true;

	}
	
	 	

	 

 

    void fillFamille(String selVal) {
		try {
			//if (Global.dbParam.getRecordsFiltreAllSoc(Global.dbParam.PARAM_FAM1,this.idFam, "1") == true) {
			if (Global.dbParam.getFamActives( this.idFam) == true) {

				//on inser la famille 'Anomalies'
				Bundle bundle = new Bundle();
			 
				
				//on inser la famille 'reste � inventorier'
				 bundle = new Bundle();
				bundle.putString(Global.dbParam.FLD_PARAM_CODEREC,getString(R.string.tous));
				bundle.putString(Global.dbParam.FLD_PARAM_LBL, getString(R.string.tous));
				bundle.putString(Global.dbParam.FLD_PARAM_COMMENT, getString(R.string.tous));
				bundle.putString(Global.dbParam.FLD_PARAM_VALUE, getString(R.string.tous));
				idFam.add(0,bundle);
				
				 
				int pos = -1;
				String[] items = new String[idFam.size()];
				for (int i = 0; i < idFam.size(); i++) {

					items[i] = idFam.get(i).getString(
							Global.dbParam.FLD_PARAM_LBL);

					String codeRec = idFam.get(i).getString(
							Global.dbParam.FLD_PARAM_CODEREC);

					if (selVal.equals(codeRec)) {
						pos = i;
					}

				}


				Spinner spinner = (Spinner) findViewById(R.id.spinnerFam);

				ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
						android.R.layout.simple_spinner_item, items);
				adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				spinner.setAdapter(adapter);

				spinner.setSelection(pos);

			}

		} catch (Exception ex) {

		}

	}
    public void onItemSelected(AdapterView<?> parent, View view, 
            int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
    	m_stFam = idFam.get(pos).getString(
				Global.dbParam.FLD_PARAM_CODEREC);
    	//m_stFam = "" ;		//6546876546: DEBUGAGE
    	PopulateList() ;
    }
    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }
    
    Handler getHandler() {
		Handler h = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
		 
				switch (msg.what) {
			 
				case 1: //get stock theo srv ok
					m_ProgressDialog.dismiss();
					PopulateList();
					break;
				case 0: //get stock theo srv nok
					m_ProgressDialog.dismiss();
					promptText(getString(R.string.connexion_au_serveur), getString(R.string.impossible_de_r_cup_rer_le_stock_sur_le_serveur), true);
					 ;
				 	break;
				 
				}

			}
		};
		return h;
	}
}
