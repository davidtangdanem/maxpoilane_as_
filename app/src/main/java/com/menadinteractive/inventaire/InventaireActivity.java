package com.menadinteractive.inventaire;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.menadinteractive.maxpoilane.BaseActivity;
import com.menadinteractive.printmodels.BluetoothConnectionInsecureExample;
import com.menadinteractive.printmodels.Z420ModelInventaire;
import com.menadinteractive.maxpoilane.R;
import com.menadinteractive.segafredo.communs.Fonctions;
import com.menadinteractive.segafredo.communs.Global;
import com.menadinteractive.segafredo.db.Preferences;
import com.menadinteractive.segafredo.db.TableSouches;
import com.menadinteractive.segafredo.db.dbKD543LinInventaire;
import com.menadinteractive.segafredo.db.dbKD543LinInventaire.structPassePlat;
import com.menadinteractive.segafredo.db.dbSiteProduit;
import com.menadinteractive.segafredo.plugins.Espresso;
import com.menadinteractive.segafredo.tasks.task_getStockTheoWSData;
import com.menadinteractive.segafredo.tasks.task_sendInventaireWSData;

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
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

public class InventaireActivity extends BaseActivity 
	implements OnItemSelectedListener
{
	private listAdapter m_adapter;

	boolean partielok=false;
	TextView tvTitre;

	boolean m_isPrinted=false;
	private ProgressDialog m_ProgressDialog = null;
	Handler m_taskhandler;
	final int LAUNCH_SAISIEQTE = 44;
	ListView myListView;
	int lvPosition;
	String m_soccode;
	int top = 0;
	int index = 0;
	ArrayList<Bundle> idFam = null;// les id qui servent a retrouver les pays
	String m_stFam = "" ;	//Filtre Fam (voir spinner)
	Handler hPrintResult;
	String numeroInventaire;	
	boolean m_problemPrinter=false;//si on declare un probleme d'imprimante on laisse passer
	ImageButton ibFilter;
	EditText etFilter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_inventaire); 

		idFam = new ArrayList<Bundle>();
		m_soccode=Preferences.getValue(this, Espresso.CODE_SOCIETE, "0");
		TableSouches souche=new TableSouches(m_appState.m_db, this);
		numeroInventaire=souche.get(TableSouches.TYPEDOC_INVENTAIRE,getLogin());

		
		initGUI();
		initListeners();
		fillFamille("");
		m_taskhandler=getHandler();
		
		  partielok=Preferences.getValueBoolean(this, Espresso.INVPARTIEL,false);
		
		if (isInvTermine()==false)
		{
			
	
			m_ProgressDialog=ProgressDialog.show(this, getString(R.string.r_ception_du_stock_th_orique), getString(R.string.patientez_));
			
			task_getStockTheoWSData task=new task_getStockTheoWSData(this, m_taskhandler);
			task.execute();
			
			Fonctions.WriteProfileString(this, Espresso.REG_DATEDERINV, Fonctions.getYYYYMMDD());
		}
	
	}

	void initGUI() {
		myListView = (ListView) findViewById(R.id.lv_4);
		myListView.setTextFilterEnabled(true);

		etFilter=(EditText)findViewById(R.id.etFilter);
		 
		ibFilter=(ImageButton)findViewById(R.id.ibFind);
		
		tvTitre = (TextView) findViewById(R.id.titre);
		
		tvTitre.setText("Inventaire Véhicule n°"+numeroInventaire);
	//	PopulateList();
	}

	void initListeners() {
		hPrintResult=getHandlerPrint();
		
		ibFilter.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
			 
			//	String filter=etFilter.getText().toString();
			//	PopulateList(  );
				promptText("CODE ARTICLE ou DESIGNATION" );
			}
		});
		etFilter.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				promptText("CODE ARTICLE ou DESIGNATION" );
			}
		});
		
	
		Spinner spinner = (Spinner) findViewById(R.id.spinnerFam);
		spinner.setOnItemSelectedListener(this);

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
					//promptText("Problème d'impression",BluetoothConnectionInsecureExample.getErrMsg(msg.what), false);
					MessageYesNo_printerproblem(BluetoothConnectionInsecureExample.getErrMsg(msg.what)+"\n\nAvez vous un problème bloquant qui vous empèche d'imprimer ?", 433, getString(R.string.probl_me_d_impression));
					
				}
				else
				{
					m_isPrinted = true;
					dbKD543LinInventaire inv=new dbKD543LinInventaire(m_appState.m_db);
					inv.saveHdrPrint();
					
					MessageYesNo("Transmettre l'iventaire vers le serveur ?",434,"Fin de l'inventaire");

				}
			}
		};
		return h;
	}
	public void MessageYesNo (String message, int type,String title) {
		final int m_type = type;
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(message)
		.setCancelable(false)
		.setTitle(title)
		.setPositiveButton(getString(R.string.Yes),
				new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				switch (m_type) {
				 
				case 434://probleme d'imprimante
					 synchro();
					break;
				}

			}
		})
		.setNegativeButton(getString(R.string.No),
				new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {

			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}
	private void PopulateList() {

		ArrayList<dbSiteProduit.structArt> colNames = new ArrayList<dbSiteProduit.structArt>();

		this.m_adapter = new listAdapter(this, R.layout.item_inventaire, colNames);

		myListView.setAdapter(this.m_adapter);
		String filter=etFilter.getText().toString();
		ArrayList<dbSiteProduit.structArt> arts =null;
		if (m_stFam.equals(getString(R.string.inventaire_resteainventorier))==true)
			arts=Global.dbProduit.getProduitsInventaire (
					false,true,"",filter);
			
		else
			if (m_stFam.equals(getString(R.string.d_j_inventori_s))==true)
				arts=Global.dbProduit.getProduitsInventaire(true,false, "",filter) ;
				
			else
				arts=Global.dbProduit.getProduitsInventaire(
					false,false,m_stFam,filter);

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
				v = vi.inflate(R.layout.item_inventaire, null);
			}

			dbSiteProduit.structArt o = items.get(position);

			if (o != null) {

	
				
				
				//RelativeLayout rl = (RelativeLayout) v.findViewById(R.id.rl_root);
				LinearLayout rl = (LinearLayout) v.findViewById(R.id.rl_root);
				LinearLayout ll = (LinearLayout) v.findViewById(R.id.ll_saisie);
				TextView tvCode = (TextView) v.findViewById(R.id.textCode); 
				tvCode.setText(o.CODART+" - PCB= "+o.PCB);
				
				TextView tvLbl = (TextView) v.findViewById(R.id.textLbl); 
				tvLbl.setText(o.NOMART1);
				
			
				ImageButton bOk = (ImageButton) v.findViewById(R.id.imageButtonValide);
			
				EditText etQte = (EditText) v.findViewById(R.id.editTextQte);
				//TextView tvQte = (TextView) v.findViewById(R.id.textViewQteVal);
				TextView tvQteTheo = (TextView) v.findViewById(R.id.textViewQteTheoVal);
				TextView tvTxTva = (TextView) v.findViewById(R.id.textViewTxPoidsVal);
				
				rl.setBackgroundColor(Color.TRANSPARENT);
				String valueTag=o.CODART;
				etQte.setTag(o.CODART);
				bOk.setTag(etQte);
				etQte.setText(o.QTEINV);
				items.get(position).QTEINV=etQte.getText().toString();
				bOk.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						if (isInvTermine()) 
						{
							promptText("Validation", "Impossible l'inventaire est terminé" ,false);
						}
						EditText et=(EditText)v.getTag();
						String qte=et.getText().toString();
						
						if(qte.equals("")) return;
						
						String codeart=et.getTag().toString();
						
						index = myListView.getFirstVisiblePosition();
						View vw = myListView.getChildAt(0);
						top = (vw == null) ? 0 : vw.getTop();
						save(codeart,Fonctions.convertToInt(qte),0 );
						PopulateList();
						myListView.setSelectionFromTop(index, top);
					}
				});
				etQte.setOnFocusChangeListener(new OnFocusChangeListener() {          

			        public void onFocusChange(View v, boolean hasFocus) {
			            if (!hasFocus) {
			        		EditText et=(EditText)v;
							String qte=et.getText().toString();
							
							if(qte.equals("")) return;
							
							String codeart=et.getTag().toString();
							
							index = myListView.getFirstVisiblePosition();
							View vw = myListView.getChildAt(0);
							top = (vw == null) ? 0 : vw.getTop();
							save(codeart,Fonctions.convertToInt(qte),0 );
					//		PopulateList();
					//		myListView.setSelectionFromTop(index, top);
			            }
			        }
			    });
				
				 if (o.QTEINV.equals(""))
				 {
			//		 tvQte.setText("");
					rl.setBackgroundColor(Color.YELLOW);
				//	ll.setVisibility(View.GONE);
				 }
				else
				{
				//	ll.setVisibility(View.VISIBLE);
			//		tvQte.setText(String.valueOf(o.QTEINV));
					rl.setBackgroundColor(Color.GREEN);
				}
					
				 
				 ll.setVisibility(View.VISIBLE);
				 
			//	 etQte.setText(String.valueOf(o.QTEINV));
			//	 etQte.setTag(o.CODART);
				 
				//	rl.setBackgroundColor(Color.GREEN);
					 
					
					valueTag+=";"+o.QTEINV+";"+"0";
				 
				
				rl.setTag(valueTag);
			}
			return v;
		}

	}

	

	 
	 
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == LAUNCH_VALIDATIONINVENTAIRE) {
			
			if (resultCode == Activity.RESULT_OK) {
				Bundle b = data.getExtras();
				String comment =b.getString("comment");
				String isagent=b.getString("isagent");
				String issignature=b.getString("issignature");
				
				dbKD543LinInventaire.structPassePlat lin = new dbKD543LinInventaire.structPassePlat();
				lin.FIELD_LIGNEINV_NUMDOC=numeroInventaire;
				lin.FIELD_LIGNEINV_TYPEPIECE=dbKD543LinInventaire.TYPEPIECE_HDR;
				lin.FIELD_LIGNEINV_DUO=isagent;
				lin.FIELD_LIGNEINV_REPCODE=getLogin();
				lin.FIELD_LIGNEINV_DEPOT=Preferences.getValue(this, Espresso.DEPOT, "1");
				lin.FIELD_LIGNEINV_DATE=Fonctions.getYYYYMMDDhhmmss();
				lin.FIELD_LIGNEINV_COMMENT1= comment;
				
				dbKD543LinInventaire inv=new dbKD543LinInventaire(m_appState.m_db);
				inv.saveHdr(lin);
				
	 
				launchPrinting();
			}
		}
			
	 

	}
	
	void save(String codeart,int qteInv,int qteTheo)
	{
		dbKD543LinInventaire.structPassePlat lin = new dbKD543LinInventaire.structPassePlat();
		lin.FIELD_LIGNEINV_SOCCODE=m_soccode;
		lin.FIELD_LIGNEINV_DATE=Fonctions.getYYYYMMDDhhmmss();
	
	
		lin.FIELD_LIGNEINV_REPCODE=Preferences.getValue(this, Espresso.LOGIN, "0");
		lin.FIELD_LIGNEINV_DEPOT=Preferences.getValue(this, Espresso.DEPOT, "1");
		lin.FIELD_LIGNEINV_NUMDOC=numeroInventaire;
		dbSiteProduit.structArt art = new dbSiteProduit.structArt();
		if (Global.dbProduit.getProduit(codeart, art,
				new StringBuilder())) {
			lin.FIELD_LIGNEINV_UV = art.UNVENTE;
			lin.FIELD_LIGNEINV_CODABAR=art.EAN;
			lin.FIELD_LIGNEINV_DESIGNATION=art.NOMART1;
			lin.FIELD_LIGNEINV_PROCODE= codeart;
			lin.FIELD_LIGNEINV_QTE=qteInv+"";
			lin.FIELD_LIGNEINV_QTETHEO=qteTheo+"";
			lin.FIELD_LIGNEINV_TYPEPIECE=dbKD543LinInventaire.TYPEPIECE_LIN;
			float fpoids=Fonctions.convertToFloat(art.POIDSBRUT)*
					Fonctions.convertToFloat(art.PCB)*
					Fonctions.convertToFloat(lin.FIELD_LIGNEINV_QTE);
			
			if (Global.dbKDLinInv.save(lin, lin.FIELD_LIGNEINV_PROCODE,
					new StringBuffer())) {

			} else {

			}
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK
				|| keyCode == KeyEvent.KEYCODE_HOME) {
			/*
			 * if( checkAll()==true) { if (saveCde()==true) finish(); }
			 */

			return false;
		}

		else
			return super.onKeyDown(keyCode, event);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		addMenu(menu, R.string.validation_de_l_inventaire,R.drawable.success48_optionmenu);
		addMenu(menu, R.string.inventaire_quit, android.R.drawable.ic_menu_close_clear_cancel);
		addMenu(menu, R.string.inventaire_connexion,android.R.drawable.ic_dialog_dialer);
		addMenu(menu, R.string.raz, R.drawable.basic1_020_bin_trash_delete);
		
		 
		return true;
	}

	boolean isInvTermine()
	{
		dbKD543LinInventaire inv=new dbKD543LinInventaire(m_appState.m_db);
		structPassePlat pp= inv.loadHdr();
		if (pp!=null)
			if (Fonctions.convertToBool(pp.FIELD_LIGNEINV_ISPRINT))
			{
				return true;
			}
		 
		return false;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

	
		case R.string.inventaire_quit:
			save();
			
			
			finish();
		
			return true;
		case R.string.validation_de_l_inventaire:
				if (isInvTermine() )
				{
					launchPrinting();
				
					return true;
				}
			
				
			//launchPrinting();
			save();
			
			dbKD543LinInventaire inv=new dbKD543LinInventaire(m_appState.m_db);
			if (inv.isInventaireTermine()==false && partielok==false)
			{
				promptText(getString(R.string.transmission_de_l_inventaire), getString(R.string.impossible_l_inventaire_n_est_pas_finalis_), false);
				return false;
			}
			
			List<structPassePlat> pp =inv.load();
			if (pp.size()>0)
			{
				Intent intent = new Intent(this, 	ValidationInventaire.class); 
				//intent.putExtras(b);
				startActivityForResult(intent, LAUNCH_VALIDATIONINVENTAIRE);
			}
			else {
				 promptText("Validation de l'inventaire","Aucune quantité saisie",false);
				 return false;
			}
			return true;
		case R.string.raz:
			if (isInvTermine() )
			{
				promptText("RAZ de l'inventaire","Impossible inventaire déjà validé",false); 
			}
			else {
				MessageYesNoRAZInv(getString(R.string.voulez_vous_vraiment_annuler_l_inventaire_et_effacer_ce_qui_a_t_saisi_));
				
			}
			
			return true;
		case R.string.inventaire_connexion:
			dbKD543LinInventaire inv2=new dbKD543LinInventaire(m_appState.m_db);
			structPassePlat pp2= inv2.loadHdr();
			if (pp2!=null)
				if (Fonctions.convertToBool(pp2.FIELD_LIGNEINV_ISPRINT))
				{
					synchro();
					return true;
				}
			 
			
			promptText(getString(R.string.transmission_de_l_inventaire), getString(R.string.impossible_l_inventaire_n_est_pas_finalis_), false);
			 
		    
	
			return true;
		
		default:
			return super.onOptionsItemSelected(item);
		}

	}

	boolean synchro()
	{
		save();
		
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
	
	void launchPrinting() {
		
		m_ProgressDialog=ProgressDialog.show(this, getString(R.string.communication_avec_l_imprimante), getString(R.string.patientez_));

		 
		String mac=getPrinterMacAddress();
		   BluetoothConnectionInsecureExample example = new BluetoothConnectionInsecureExample(hPrintResult);
              Z420ModelInventaire z=new Z420ModelInventaire(this);
              String   zplData=z.get ( );
	        example.sendZplOverBluetooth(mac,zplData,1);
		 
	
	}


	

	boolean save() {
		try {
		
			
				
			return true;
		} catch (Exception e) {
			// TODO: handle exception
		}
		return false;
	}

	

	 

	public void MessageYesNoRAZInv(String message) {
 
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(message)
				.setCancelable(false)
				.setPositiveButton(getString(android.R.string.yes),
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {
								dbKD543LinInventaire inv=new dbKD543LinInventaire(m_appState.m_db);
								inv.Reset("RAZ INV");
								promptText(getString(R.string.raz_inventaire), getString(R.string.annulation_de_l_inventaire_effective), true);
							 

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
	public void MessageYesNo_printerproblem(String message, int type,String title) {
		final int m_type = type;
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(message)
		.setCancelable(false)
		.setTitle(title)
		.setPositiveButton(getString(R.string.Yes),
				new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				switch (m_type) {
				case 433://probleme d'imprimante
					m_problemPrinter=true;
			/*		promptText(getString(R.string.probl_me_d_impression),
							getString(R.string.vous_pouvez_valider_le_document_sans_imprimer), 
								false);
					*/
					m_isPrinted = true;
					dbKD543LinInventaire inv=new dbKD543LinInventaire(m_appState.m_db);
					inv.saveHdrPrint();
					
					MessageYesNo("Transmettre l'iventaire vers le serveur ?",434,"Fin de l'inventaire");

					break;
				  
				}

			}
		})
		.setNegativeButton(getString(R.string.No),
				new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {

			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}
	public static class DatePickerFragment extends DialogFragment implements
			DatePickerDialog.OnDateSetListener {

		Handler DateLivr;
		public DatePickerFragment(Handler b)
		{
			DateLivr=b;
		}
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Use the current date as the default date in the picker
			final Calendar c = Calendar.getInstance();
			int year = c.get(Calendar.YEAR);
			int month = c.get(Calendar.MONTH)+1;
			int day = c.get(Calendar.DAY_OF_MONTH);

		    long time = c.getTimeInMillis();
		    
			DatePickerDialog dlg=new DatePickerDialog(getActivity(), this, year, month, day);
			//dlg.getDatePicker().setMinDate(time);
			
			return dlg;
		}

		public void onDateSet(DatePicker view, int year, int month, int day) {
			Message msg=new Message();
			msg.what=2;
			Bundle b=new Bundle();
			b.putString("date",Fonctions.ymd_to_YYYYMMDD(year, month+1, day));
			msg.setData(b);
			DateLivr.sendMessage(msg);
		}
	}

    void fillFamille(String selVal) {
		try {
			//if (Global.dbParam.getRecordsFiltreAllSoc(Global.dbParam.PARAM_FAM1,this.idFam, "1") == true) {
			if (Global.dbParam.getFamActives( this.idFam) == true) {

				//on inser la famille 'Anomalies'
				Bundle bundle = new Bundle();
			 
				
				//on inser la famille 'reste � inventorier'
				 bundle = new Bundle();
				bundle.putString(Global.dbParam.FLD_PARAM_CODEREC,getString(R.string.d_j_inventori_s));
				bundle.putString(Global.dbParam.FLD_PARAM_LBL, getString(R.string.d_j_inventori_s));
				bundle.putString(Global.dbParam.FLD_PARAM_COMMENT, getString(R.string.d_j_inventori_s));
				bundle.putString(Global.dbParam.FLD_PARAM_VALUE, getString(R.string.d_j_inventori_s));
				idFam.add(0,bundle);
				
				 bundle = new Bundle();
				bundle.putString(Global.dbParam.FLD_PARAM_CODEREC,getString(R.string.inventaire_resteainventorier));
				bundle.putString(Global.dbParam.FLD_PARAM_LBL, getString(R.string.inventaire_resteainventorier));
				bundle.putString(Global.dbParam.FLD_PARAM_COMMENT, getString(R.string.inventaire_resteainventorier));
				bundle.putString(Global.dbParam.FLD_PARAM_VALUE, getString(R.string.inventaire_resteainventorier));
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
				case 3: //send inv to srv ok
					m_ProgressDialog.dismiss();
					promptText(getString(R.string.connexion_au_serveur), getString(R.string.inventaire_envoy_avec_succ_s), true);
					
					break;
				case 4: //send inv to srv nok
					m_ProgressDialog.dismiss();
					promptText(getString(R.string.connexion_au_serveur), getString(R.string.envoi_de_l_inventaire_chou_veuillez_re_essayer), false);
					
					break;
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
    public void promptText(String title) {

  		boolean bres = false;
  	 
  		final Dialog dialog = new Dialog(this);
  		dialog.setContentView(R.layout.textentrvalertdialog);
  		dialog.setTitle(title);
  		final EditText inputLine = (EditText) dialog
  				.findViewById(R.id.my_edittext);
  		inputLine.setSingleLine(true);
  		 

  		Button okButton = (Button) dialog.findViewById(R.id.OKButton);
  		okButton.setOnClickListener(new View.OnClickListener() {
  			public void onClick(View arg0) {
  				dialog.dismiss();
  				String filter = inputLine.getText().toString();
  				etFilter.setText(filter);
  				PopulateList( );
  			}
  		});
  		Button cancelButton = (Button) dialog
  				.findViewById(R.id.CancelButton);
  		cancelButton.setOnClickListener(new View.OnClickListener() {
  			public void onClick(View arg0) {
  				dialog.dismiss();
  			}
  		});
  		 

  		dialog.getWindow().setSoftInputMode(
  				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
  		dialog.show();
  	}
}
