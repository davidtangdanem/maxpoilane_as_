package com.menadinteractive.segafredo.tournee;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.maps.MapView.LayoutParams;
import com.menadinteractive.maxpoilane.BaseActivity;
import com.menadinteractive.maxpoilane.Debug;
import com.menadinteractive.maxpoilane.R;
import com.menadinteractive.segafredo.adapter.Log;
import com.menadinteractive.segafredo.adapter.SpinAdapter;
import com.menadinteractive.segafredo.adapter.TourneeOnDemandAdapter;
import com.menadinteractive.segafredo.carto.MenuPopup;
import com.menadinteractive.segafredo.communs.Fonctions;
import com.menadinteractive.segafredo.communs.Global;
import com.menadinteractive.segafredo.db.Preferences;
import com.menadinteractive.segafredo.db.TableClient;
import com.menadinteractive.segafredo.db.TableClient.structClient;
import com.menadinteractive.segafredo.plugins.Espresso;
import com.menadinteractive.segafredo.tasks.onDemandTask;

public class TourneeActivity extends BaseActivity implements OnItemClickListener{


	/** GUI */
	ListView lv_1;
	MenuPopup menuPopup;
	RelativeLayout parent;
	TextView tv_date;

	/** Handler */
	Handler handler;
	onDemandTask onDemandTask;

	/** Adapter */
	ArrayList<Log> tourneeData;
	TourneeOnDemandAdapter tourneeAdapter;
	String datelivr="";
	ArrayList<String> zones;
	SpinAdapter spinAdapter;
	int zoneSpinnerPosition = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initActionBar();
		setContentView(R.layout.activity_tournee);
		handler = getHandler();

		Bundle bundle = this.getIntent().getExtras();
		datelivr = bundle.getString("datelivr");
		
		initGUI();
		initModels();
		initListeners();

		handler.sendEmptyMessage(MESSAGE_CREATE_TASK_TOURNEE);
	}

	private void initActionBar(){
		ActionBar actionBar = getActionBar();
		//		setActionBarTitle(R.string.menu_Ligne, -1);
		actionBar.setDisplayHomeAsUpEnabled(true);

		setProgressBarIndeterminateVisibility(false);
	}

	private void initGUI(){
		lv_1 = (ListView) findViewById(R.id.lv_1);
		tv_date = (TextView) findViewById(R.id.tv_date);
		parent = ((RelativeLayout)lv_1.getParent());
		menuPopup = new MenuPopup(this,handler, null);
	}

	private void initModels(){
		tourneeData = new ArrayList<Log>();
		tourneeAdapter = new TourneeOnDemandAdapter(this, R.layout.item_list_tournee, tourneeData);
		lv_1.setAdapter(tourneeAdapter);
//		long datelivr=Preferences.getValueLong(TourneeActivity.this, Preferences.KEY_DATE_TOURNEE_ON_DEMAND, System.currentTimeMillis());
	//	tv_date.setText(getString(R.string.date_selectionnee)+" "+Preferences.getValue(TourneeActivity.this, Preferences.KEY_DATE_TOURNEE_ZONE, "")+" : " +Fonctions.getWEEKDAY_DD_MM_YYYY(datelivr));
		tv_date.setText(getString(R.string.date_selectionnee)+" "+Fonctions.YYYYMMDD_to_dd_mm_yyyy(datelivr));
		
		zones = Global.dbClient.getZones();
		spinAdapter = new SpinAdapter(this, android.R.layout.simple_spinner_item, zones);
	}

	private void initListeners(){
		lv_1.setOnItemClickListener(this);
		registerForContextMenu(lv_1);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, 0, 0, "Annuler la commande");
	
	}
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
	
		String cde_code=tourneeData.get((int)info.id).getTournee().cde_code;
		
		

		switch (item.getItemId()) {
		case 0:
			task_updateCde task=new task_updateCde(this, cde_code);
			task.execute();

			return true;
		
		default:
			return super.onContextItemSelected(item);
		}
	}	 



	/***********  Action Bar   **********/
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		addMenu(menu, R.string.selectionner_date, R.drawable.ic_action_calendar);
//		addMenu(menu, R.string.prevenir_par_email, R.drawable.action_email);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		case R.string.selectionner_date:
			finish();
			//if(handler != null)
			//	handler.sendEmptyMessage(MESSAGE_PICK_DATE);
			return true;
		case R.string.prevenir_par_email:
			if(handler != null)
				handler.sendEmptyMessage(MESSAGE_SEND_EMAIL);
			return true;
			
		default:
			return super.onOptionsItemSelected(item);
		}
	}



	/***********  Handler  **********/
	Handler getHandler(){
		Handler h = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);

				switch(msg.what) {
				case MESSAGE_PICK_DATE:
					chooseDateDialog(TourneeActivity.this.getString(R.string.selectionner_date));
					break;

				case MESSAGE_UPDATE_UI:
					setProgressBarIndeterminateVisibility(true);
					if(onDemandTask == null)
						setProgressBarIndeterminateVisibility(false);
					tourneeAdapter.notifyDataSetChanged();
					break;

				case MESSAGE_CREATE_TASK_TOURNEE:
					if(onDemandTask == null){
						tourneeData.clear();
						String date = datelivr;//Fonctions.getYYYYMMDD(Preferences.getValueLong(TourneeActivity.this, Preferences.KEY_DATE_TOURNEE_ON_DEMAND, System.currentTimeMillis()));
						String zone = Preferences.getValue(TourneeActivity.this, Preferences.KEY_DATE_TOURNEE_ZONE, "");
						onDemandTask = new onDemandTask(TourneeActivity.this, handler, tourneeData, date, zone);
						onDemandTask.execute();
						Toast.makeText(TourneeActivity.this, R.string.interrogation_temps_reel, Toast.LENGTH_SHORT).show();
					}
						handler.sendEmptyMessage(MESSAGE_UPDATE_UI);
					break;

				case MESSAGE_CLEAR_TASK_TOURNEE:
					onDemandTask = null;
					Debug.Log("TAG6", "MESSAGE_CLEAR_TASK_TOURNEE");
					break;


				case MESSAGE_SHOW_MENU_POPUP:
					String code = msg.getData().getString(TableClient.FIELD_CODE);
					structClient sc = Global.dbClient.load(code);
					showMenuPopup(sc);
					break;
				case MESSAGE_HIDE_MENU_POPUP:
					hideMenuPopup();
					break;
				case MESSAGE_SELECT_CLIENT_BACK_NEGOS:
					Intent i = new Intent();
					Bundle b = new Bundle();
//					b.putString(ExpressoPerfect.CodeClient, msg.getData().getString(AppBundle.CodeClient));
					b.putString(Espresso.CodeClient, msg.getData().getString(Espresso.CodeClient));
					i.putExtras(b);
					setResult(RESULT_OK, i);
					finish();
					break;

				case MESSAGE_NO_RESULT:
					Toast.makeText(TourneeActivity.this, R.string.pas_de_resultat, Toast.LENGTH_LONG).show();
					break;
				case MESSAGE_PLEASE_WAIT:
					Toast.makeText(TourneeActivity.this, R.string.merci_patienter, Toast.LENGTH_LONG).show();
					break;
				case MESSAGE_SEND_EMAIL:
					if(tourneeData != null && tourneeData.size()>0 && onDemandTask == null){
						StringBuffer emails = new StringBuffer();
						for(Log a : tourneeData){
							try{
								if(Integer.valueOf(a.getTournee().deltaAvecStockMini)<=0){
									String email = a.getClient().EMAIL;
									if(email != null && !email.equals(""))
										emails.append(email+";");
								}
							}
							catch(Exception e){

							}
						}
						
						String dateLivraison = Fonctions.getDD_MM_YYYY(Preferences.getValueLong(TourneeActivity.this, Preferences.KEY_DATE_TOURNEE_ON_DEMAND, System.currentTimeMillis()));
						Fonctions.sendEmailMsg2(TourneeActivity.this, "", "", emails.toString(), String.format(TourneeActivity.this.getString(R.string.email_body), dateLivraison ),TourneeActivity.this.getString(R.string.email_sujet));
					}
					else if(onDemandTask != null){
						handler.sendEmptyMessage(MESSAGE_PLEASE_WAIT);
					}
					break;
				}

			}
		};
		return h; 
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		final Log l = (Log)parent.getAdapter().getItem(position);
		final structClient client = (structClient) l.getClient();
		Toast.makeText(this, client.NOM, Toast.LENGTH_SHORT).show();
		//showMenuPopup(client);
		
		launchDetailCde(l.getTournee().cde_code);
	}
	void launchDetailCde(String cdeCode) {
/*		Intent i = new Intent(this, HistoCdeLinFragment.class);
		Bundle b = new Bundle();
		b.putString("numcde", cdeCode);
		i.putExtras(b);

		startActivityForResult(i, 222);
		*/
	}
	/**
	 * grand popup qui affiche Stock / Horaire / Naviguation / Appeler ...
	 * @param structClient
	 */
	public void showMenuPopup(structClient structClient){
		hideMenuPopup();
		menuPopup.updatePopup(structClient, true);

		RelativeLayout.LayoutParams mapParams = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, 
				LayoutParams.WRAP_CONTENT);
		mapParams.addRule(RelativeLayout.CENTER_VERTICAL);
		mapParams.addRule(RelativeLayout.CENTER_HORIZONTAL);

		Animation dialogEnter = AnimationUtils.loadAnimation(this, R.anim.dialog_enter);
		parent.addView(menuPopup.getView(), mapParams);
		menuPopup.getView().startAnimation(dialogEnter);
		//		parent.invalidate();
	}

	/**
	 * grand popup caché
	 */
	public void hideMenuPopup() {
		try{
			parent.removeView(menuPopup.getView());
		}
		catch (Exception e){
			Debug.StackTrace(e);
		}
	}
	
	
	
	
	public void chooseDateDialog(String message)
	{
		GregorianCalendar calendar = new java.util.GregorianCalendar(); 
		
		final String currentDateString = Fonctions.getYYYYMMDD(System.currentTimeMillis());
		
		calendar.setTimeInMillis(Preferences.getValueLong(TourneeActivity.this, Preferences.KEY_DATE_TOURNEE_ON_DEMAND, System.currentTimeMillis()));

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		LayoutInflater inflater = this.getLayoutInflater();
		View parent = inflater.inflate(R.layout.popup_date, null);
		builder.setView(parent);
		final DatePicker dp = (DatePicker) parent.findViewById(R.id.dp);
		final TimePicker tp = (TimePicker) parent.findViewById(R.id.tp);
		final TextView tv_heure_label = (TextView) parent.findViewById(R.id.tv_heure_label);
		final Spinner s_1 = (Spinner) parent.findViewById(R.id.s_1);
		s_1.setAdapter(spinAdapter);
		tp.setVisibility(View.GONE);
		tv_heure_label.setVisibility(View.GONE);



		// Inflate and set the layout for the dialog
		// Pass null as the parent view because its going in the dialog layout

		builder.setMessage(message)
		.setCancelable(false)
		.setPositiveButton(TourneeActivity.this.getString(R.string.Valider), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				GregorianCalendar savedCalendar = new java.util.GregorianCalendar();
				savedCalendar.set(dp.getYear(), dp.getMonth(), dp.getDayOfMonth(), tp.getCurrentHour(), tp.getCurrentMinute());
				
				String selectedDateString = Fonctions.getYYYYMMDD(savedCalendar.getTimeInMillis());
				
				long currentDate = Long.valueOf(currentDateString);
				long selectedDate = Long.valueOf(selectedDateString);
				
				Debug.Log("TAG6", "currentDate : "+currentDate+" - selectedDate : "+selectedDate);
				if(selectedDate >= currentDate){
					Preferences.setValueLong(TourneeActivity.this, Preferences.KEY_DATE_TOURNEE_ON_DEMAND, savedCalendar.getTimeInMillis());
					Preferences.setValue(TourneeActivity.this, Preferences.KEY_DATE_TOURNEE_ZONE, (String)s_1.getSelectedItem());
					
					tv_date.setText(TourneeActivity.this.getString(R.string.date_selectionnee)+" "+Preferences.getValue(TourneeActivity.this, Preferences.KEY_DATE_TOURNEE_ZONE, "")+" : "+Fonctions.getDD_MM_YYYY(Preferences.getValueLong(TourneeActivity.this, Preferences.KEY_DATE_TOURNEE_ON_DEMAND, System.currentTimeMillis())));
					if(handler != null)
						handler.sendEmptyMessage(MESSAGE_CREATE_TASK_TOURNEE);
				}
				else{
					Toast.makeText(TourneeActivity.this, TourneeActivity.this.getString(R.string.Selectionner_date_future), Toast.LENGTH_SHORT).show();
					if(handler != null)
						handler.sendEmptyMessage(MESSAGE_PICK_DATE);
				}
				//            		                     finish();
			}
		})
		.setNegativeButton(TourneeActivity.this.getString(R.string.Annuler), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {

			}
		});

		AlertDialog alert = builder.create();
		dp.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
		tp.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
		tp.setCurrentMinute(calendar.get(Calendar.MINUTE));
		alert.show();

	}
}
