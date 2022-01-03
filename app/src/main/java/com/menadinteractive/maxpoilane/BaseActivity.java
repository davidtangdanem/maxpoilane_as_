package com.menadinteractive.maxpoilane;

import java.util.Random;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.maps.MapActivity;
import com.menadinteractive.commande.commandeActivity;
import com.menadinteractive.commentaire.CommentaireActivity;
import com.menadinteractive.commentaire.EmailActivity;
import com.menadinteractive.histo.FacturesDuesActivity;
import com.menadinteractive.histo.HistoDocumentsActivity;
import com.menadinteractive.histo.HistoDocumentsLignesActivity;
import com.menadinteractive.printmodels.printPreviewActivity;
import com.menadinteractive.maxpoilane.R;
import com.menadinteractive.segafredo.receiver.SynchroReceiver;
import com.menadinteractive.segafredo.remisebanque.RemiseEnBanque;
import com.menadinteractive.segafredo.communs.Fonctions;
import com.menadinteractive.segafredo.communs.Global;
import com.menadinteractive.segafredo.db.MyDB;
import com.menadinteractive.segafredo.db.Preferences;
import com.menadinteractive.segafredo.findcli.MenuCliActivity;
import com.menadinteractive.segafredo.materielclient.ListingMaterielClient;
import com.menadinteractive.segafredo.plugins.Espresso;

import com.menadinteractive.stats.StockTheoActivity;
import com.menadinteractive.stats.SuiviStockHebdoActivity;
import com.menadinteractive.stats.SyntheseArtHebdoActivity;

public class BaseActivity extends Activity{
	/** Constants */
	public static final int MESSAGE_CLIENT_SELECTED_ON_MAP = 204;
	public static final int MESSAGE_SHOW_POPUP = 205;
	public static final int MESSAGE_ACTION_BAR_PROGRESS_BAR = 206;
	public static final int MESSAGE_PUSH_GPS_COORDINATE = 207;
	public static final int MESSAGE_UPDATE_PI = 208;
	public static final int MESSAGE_UPDATE_PI_ARG1_FORCE = 1;
	public static final int MESSAGE_FIND_NEAREST_POI = 209;
	public static final int MESSAGE_SHOW_MENU_POPUP = 210;
	public static final int MESSAGE_HIDE_MENU_POPUP = 211;
	public static final int MESSAGE_SELECT_CLIENT_BACK_NEGOS = 212;
	public static final int MESSAGE_UPDATE_LIST_TOURNEE = 213;
	public static final int MESSAGE_CLEAR_TASK_GEOCODE = 214;
	public static final int MESSAGE_CREATE_TASK_GEOCODE = 215;
	public static final int MESSAGE_ANIMATE_TO = 216;
	public static final int MESSAGE_PROSPECT_BACK_NEGOS = 217;
	
	
	public static final int MESSAGE_PICK_DATE = 2000;
	public static final int MESSAGE_UPDATE_UI = 2001;
	public static final int MESSAGE_CREATE_TASK_TOURNEE = 2002;
	public static final int MESSAGE_CLEAR_TASK_TOURNEE = 2003;
	public static final int MESSAGE_NO_RESULT = 2004;
	public static final int MESSAGE_SEND_EMAIL = 2005;
	public static final int MESSAGE_PLEASE_WAIT = 2006;
	
	public static final int LAUNCH_TOURNEE = 500;
	public static final int LAUNCH_TOURNEE_LISTEBYDAY = 501;
	public static final int LAUNCH_FICHECLI = 539;
	public static final int LAUNCH_CADENCIER = 540;
	public static final int LAUNCH_CONTACT = 541;
	public static final int LAUNCH_RAPPORT = 542;
	public static final int LAUNCH_AGENDA= 543;
	public static final int LAUNCH_MATERIELCLI = 544;
	public static final int LAUNCH_PRINT= 545;	
	public static final int LAUNCH_PRETMATERIELCLI = 546;
	public static final int LAUNCH_PARAM = 547;
	public static final int LAUNCH_SYNCHRO = 548;
	public static final int LAUNCH_SAISIEQTE = 549;
	public static final int LAUNCH_VALIDATIONINVENTAIRE=550;
	public static final int LAUNCH_INVENTAIRE=551;
	public static final int LAUNCH_ECHANGESTOCK=552;
	public static final int LAUNCH_VALIDATIONECHANGE=553;
	public static final int LAUNCH_HISTO=554;
	public static final int LAUNCH_COMMENTAIRE=555;
	public static final int LAUNCH_REMISEBANK=556;
	public static final int LAUNCH_PRINTPREVIEW=558;
	public static final int LAUNCH_EMAIL=559;
	public static final int LAUNCH_FINDCLI=560;
	
	
	
	
	/** Models */
	protected Uri intentUri = null;
	protected String intentAction = null;
	protected long itemId = -1;
	protected AppBundle appBundle;
	
	protected app m_appState;
	
	int curver;
	public int minver = 0;
	 
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
//		outState.putLong("id", id);
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
	  super.onConfigurationChanged(newConfig);
	}
	
	/**
	 * recuperation securis�e d'un bundle
	 * @author Marc VOUAUX
	 * @param bu
	 * @param key
	 * @return
	 */
	protected String getBundleValue(Bundle bu, String key)
	{
		String val="";
		try
		{
			val = bu.getString(key);
			if (val==null) val="";
			return val;
		}
		catch(Exception ex)
		{
			val="";	
		}
		return val;
	}
	protected int getBundleValueInt(Bundle bu, String key)
	{
		int val=0;
		try
		{
			val = bu.getInt(key);
			 
			return val;
		}
		catch(Exception ex)
		{
			val=-1;
		}
		return val;
	}
	protected boolean getBundleValueBool(Bundle bu, String key)
	{
		boolean val=false;
		try
		{
			val = bu.getBoolean(key);
			 
			return val;
		}
		catch(Exception ex)
		{
			val=false;
		}
		return val;
	}
	protected MenuItem addMenu(Menu menu, int stringID, int iconID){
		MenuItem item  = menu.add(Menu.NONE, stringID, Menu.NONE, stringID);
		int size = menu.size() - 1;
		if(iconID != -1)
			menu.getItem(size).setIcon(iconID);
		menu.getItem(size).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM|MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		return item;
	}
	 
	 
	protected MenuItem addMenu(Menu menu, int stringID, String label, int iconID){
		MenuItem item  = menu.add(Menu.NONE, stringID, Menu.NONE, label);
		int size = menu.size() - 1;
		if(iconID != -1)
			menu.getItem(size).setIcon(iconID);
		menu.getItem(size).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM|MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		return item;
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);  
		
		m_appState = ((app)getApplicationContext());
		extractIntent();
	}

	private void extractIntent(){
		Intent intent = getIntent();
		if(intent != null){
			intentUri = intent.getData();
			intentAction = intent.getAction();
			if(intentUri != null)
				itemId = ContentUris.parseId(intentUri);

			Bundle b = intent.getExtras();
			appBundle = new AppBundle(b);


			if(intentUri != null)
				Debug.Log(intentUri.toString());

			Debug.Log("itemId : "+itemId);
			Debug.Log("action : "+intentAction);
			if(b != null){
				Debug.Log("LOGIN : "+b.getString(Espresso.LOGIN));
				Debug.Log("SOC CODE : "+b.getString(Espresso.CODE_SOCIETE));
				Debug.Log("PASSWORD : "+b.getString(Espresso.PASSWORD));
				Debug.Log("WS_URL : "+b.getString(Espresso.WS_URL));
				Debug.Log("TIMER_MONITORING_GPS_SECONDS : "+b.getString(Espresso.TIMER_MONITORING_GPS_SECONDS));
				Debug.Log("TIMER_SYNCHRO_SECONDS : "+b.getString(Espresso.TIMER_SYNCHRO_SECONDS));
				Debug.Log("TIMER_UPDATE_PI_SECONDS : "+b.getString(Espresso.TIMER_UPDATE_PI_SECONDS));
				//Debug.Log("SOUCHE : "+b.getString(Espresso.PREF_SSOUCHE));
				
			}


		}
	}
	
	protected Cursor find(String query){
		return m_appState.m_db.conn.rawQuery(query, null);
	}
/*
	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	} */
	protected boolean controlMinVer(boolean displayMsg) {

		String _minver = Global.dbParam.getLblAllSoc(Global.dbParam.PARAM_MINVER,
				"VER");
		minver = Fonctions.convertToInt(_minver);
		if (minver > curver) {
			if (displayMsg)
				Fonctions.FurtiveMessageBox(this,this.getString(R.string.newversion) + " ("
						+ minver + ")");
			return true;
		}
		return false;
	}
	
	protected void alertModeTest()
	{
		boolean modetest=Preferences.getValueBoolean(this, Espresso.PREF_MODETEST,false);
		if (modetest==true)
		{
			promptText("MODE TEST","Attention en mode test vous perdrez toutes vos factures", false);
		}
	}
	
	public static Drawable getAppIcon(Context context){
		Drawable result = null;
		try{
			result =context.getPackageManager().getApplicationIcon("com.menadinteractive.negos");
		} catch (Exception e) {
			Debug.StackTrace(e);
		}
		return result;
	}
	
	/** Set Alarms */
	public void setAlarmSynchro(long intervalMS) {
		Debug.Log("interval synchro : "+intervalMS/1000+ " sec");
		if(intervalMS >0){
		AlarmManager am;
		am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		
		
//		Intent i = new Intent();
//		i.setAction(SynchroReceiver.ACTION);
////		StringBuffer sbVer=new StringBuffer();
////		int version = getVersion(context, sbVer);
////		i.putExtra(VERSION, version);
////		Debug.Log("intent received : version : "+version);
////		context.sendBroadcast(i);
//		
//		
//		
//		
////		Intent intent = new Intent(this, TimeAlarm.class);
//		PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0,
//				i, PendingIntent.FLAG_CANCEL_CURRENT);
//		am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),
//				(5 * 1000), pendingIntent);
		
		am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),
				intervalMS, SynchroReceiver.getPendingIntent(this));
		
		}
		else{
			cancelAlarmSynchro();
		}
	}
	
	public void cancelAlarmSynchro(){
		AlarmManager am;
		am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		am.cancel(SynchroReceiver.getPendingIntent(this));
	}
	
	
	/** Set GPS Monitoring */
	/*
	 * On ne récupère les info GPS que lorsque l'utilisateur est sur la carto, à intervalle donnée, contrairement à la Synchro des 
	 * données qui se fait même si l'utilisateur n'est pas sur la carto
	 */
	public void setGPSSynchro(Handler h, int messageWhat){
		if(!h.hasMessages(messageWhat)){
			String timerGPS = Preferences.getValue(this, Espresso.TIMER_MONITORING_GPS_SECONDS, "0");
			if(!Fonctions.isEmptyOrNull(timerGPS) && !timerGPS.equals("0")){
				long timerGPSms = Long.valueOf(timerGPS)*1000;
//				h.sendEmptyMessageAtTime(messageWhat, timerGPSms);
				h.sendEmptyMessageDelayed(messageWhat, timerGPSms);
			}
		}
		else{

		}
	}
	
	
	public void cancelGPSSynchro(Handler h, int messageWhat){
		if(h.hasMessages(messageWhat))
			h.removeMessages(messageWhat);
	}
//	public void setGPSSynchro(long whenMS, Handler h, int messageWhat){
//		if(!h.hasMessages(messageWhat)){
//			h.sendEmptyMessageAtTime(messageWhat, whenMS);
//		}
//		else{
//			
//		}
//	}
	
	/** Update PI on UI*/
	public void setUISynchro(Handler h, int messageWhat){
		if(!h.hasMessages(messageWhat)){
			String timerUI = Preferences.getValue(this, Espresso.TIMER_UPDATE_PI_SECONDS, "0");
			if(!Fonctions.isEmptyOrNull(timerUI) && !timerUI.equals("0")){
				long timerGPSms = Long.valueOf(timerUI)*1000;
//				h.sendEmptyMessageAtTime(messageWhat, timerGPSms);
				h.sendEmptyMessageDelayed(messageWhat, timerGPSms);
			}
		}
		else{

		}
	}
	
	public void cancelUISynchro(Handler h, int messageWhat){
		if(h.hasMessages(messageWhat))
			h.removeMessages(messageWhat);
	}
	
	/** Find nearest POI */
	public void setFindNearestPOI(Handler h, int messageWhat){
		if(!h.hasMessages(messageWhat)){
			String timer = Preferences.getValue(this, Espresso.TIMER_FIND_NEAREST_POINT_SECONDS, Espresso.TIMER_FIND_NEAREST_POINT_SECONDS_DEFAULT);
			if(!Fonctions.isEmptyOrNull(timer) && !timer.equals("0")){
				long timerMS = Long.valueOf(timer)*1000;
				h.sendEmptyMessageDelayed(messageWhat, timerMS);
			}
		}
		else{

		}
	}
	
	public void cancelFindNearestPOI(Handler h, int messageWhat){
		if(h.hasMessages(messageWhat))
			h.removeMessages(messageWhat);
	}
	
	protected int getRandomColor(){
		// http://stackoverflow.com/questions/4246351/creating-random-colour-in-java

		Random rand = new Random();
		float r = rand.nextFloat();
		float g = rand.nextFloat();
		float b = rand.nextFloat();
		int randomColor = Color.rgb((int)(r*255), (int)(g*255), (int)(b*255));
		return randomColor;
	}
	
	protected Drawable convertToGrayscale(Drawable drawable)
	{
		ColorMatrix matrix = new ColorMatrix();
		matrix.setSaturation(0);

		ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);

		drawable.setColorFilter(filter);

		return drawable;
	}
	
	
	static String packageName = "com.menadinteractive.segafredo";
	
	public static String getStringVersion(Context c, StringBuffer sbVer){
		String result ="";
		int v = 0;
		try {
			v = c.getPackageManager().getPackageInfo(packageName, 0).versionCode;
			String version=c.getPackageManager().getPackageInfo(packageName, 0).versionName;

			String ver=String.valueOf(v)+"."+version;
			sbVer.append(ver);
			result = sbVer.toString();
		} catch (NameNotFoundException e) {
			// Huh? Really?
		}
		
		
		
		return result;
	}
	
	public static int getVersion(Context c, StringBuffer sbVer) {
		try {
			String version = getStringVersion(c, sbVer);
			version=version.replace(".", "");
			return Integer.parseInt(version);

		} catch (Exception e) {
			// Huh? Really?
		}
		return 0;
	}
	
	public void setTextViewText(Activity act,int rID, String val)
	    {
	    	try
	    	{
	    		TextView et=(TextView)act.findViewById(rID);
	    		et.setText(val);
	    	}
	    	catch(Exception ex)
	    	{
	    	
	    	}
	    }
	    
	protected String getTextViewText(Activity act,int rID )
	{
		try
		{
			TextView et=(TextView)act.findViewById(rID);
			return et.getText().toString();
		}
		catch(Exception ex)
		{

		}
		return "";
	}
	protected void setEditViewText(Activity act,int rID, String val)
	{
		try
		{
			EditText et=(EditText)act.findViewById(rID);
			setEditViewText(et,val);
		}
		catch(Exception ex)
		{

		}
	}
	protected void setEditViewText(EditText et,String val)
	{
		try
		{
			et.setText(val);
		}
		catch(Exception ex)
		{

		}
	}
	public int getSpinnerSelectedIdx(Activity act,int rID )
	{
		try
		{
			Spinner et=(Spinner)act.findViewById(rID);
			int pos=et.getSelectedItemPosition();
			return pos;

		}
		catch(Exception ex)
		{

		}
		return -1;
	}  
	public String getSpinnerValue(Spinner et )
	{
		try
		{
		
			String val=et.getSelectedItem().toString();
			return val;
		}
		catch(Exception ex)
		{

		}
		return "";
	}      
	public int getSpinnerSelectedIdx(Spinner et )
	{
		try
		{
			
			int pos=et.getSelectedItemPosition();
			return pos;

		}
		catch(Exception ex)
		{

		}
		return -1;
	}  
	protected String getEditViewText(Activity act,int rID )
	{
		try
		{
			EditText et=(EditText)act.findViewById(rID);
			return et.getText().toString();
		}
		catch(Exception ex)
		{

		}
		return "";
	}
	
	public void launchCde(Activity act,String typedoc,String m_codeCli,String m_socCode )
	{
		 launchCde(  act,  typedoc,  m_codeCli,  m_socCode,"" );
	}
	public void launchCde(Activity act,String typedoc,String m_codeCli,String m_socCode,String numcde )
	{
		Intent intent = new Intent(act,		commandeActivity.class);
		Bundle b=new Bundle();
		b.putString("codecli",m_codeCli);
		b.putString("soccode",m_socCode);
		b.putString("typedoc",typedoc);
		b.putString("numcde",numcde);
		intent.putExtras(b);
		//act.startActivityForResult(intent,Fonctions.convertToInt(typedoc));
		act.startActivityForResult(intent,2100);
	}
	public void launchPrintPreview( String data )
	{
		Intent intent = new Intent(this,		printPreviewActivity.class);
		Bundle b=new Bundle();
		b.putString("data",data);
 
		intent.putExtras(b);
		 startActivityForResult(intent,LAUNCH_PRINTPREVIEW);
	}
	public String getLogin()
	{
	
		return Preferences.getValue( this, Espresso.LOGIN, "0");
	
	}
	public String getSocCode()
	{
	
		return Preferences.getValue( this, Espresso.CODE_SOCIETE, "0");
	
	}
	public   void LaunchFacturesDues( )
	{
		Intent intent = new Intent(this,			FacturesDuesActivity.class);
 
		 startActivityForResult(intent, 0);
	}
	public void launchMenuCli(String codecli )
	{
		Intent intent = new Intent(this,		MenuCliActivity.class);
		Bundle b=new Bundle();
		b.putString("codecli",codecli);

		intent.putExtras(b);
		//startActivity(intent);
		startActivityForResult(intent, LAUNCH_FINDCLI);
	}
	public void launchStockTheo(  )
	{
		Intent intent = new Intent(this,StockTheoActivity.class);
	 
		startActivity(intent);
	}
	public void launchSuiviStock(  )
	{
		Intent intent = new Intent(this,SuiviStockHebdoActivity.class);
	 
		startActivity(intent);
	}
	public void launchSyntheseHebdo(  )
	{
		Intent intent = new Intent(this,SyntheseArtHebdoActivity.class);
	 
		startActivity(intent);
	}
	public void launchStats(String codeCli, String codeSoc){
		Intent intent = new Intent(this, HistoDocumentsActivity.class);
		Bundle b = new Bundle();
		b.putString(Espresso.CODE_CLIENT, codeCli);
		b.putString(Espresso.CODE_SOCIETE, codeSoc);
		b.putString("m_numDocForDuplication", "");
		intent.putExtras(b);
		startActivityForResult(intent, 0);
	}
 
	public void launchStatsDetail(String datedoc,String numdoc,String codecli ,String m_numDocForDuplication,String typedoc,String typeDocForDuplication){
		Intent intent = new Intent(this, HistoDocumentsLignesActivity.class);
		Bundle b = new Bundle();
		b.putString("numdoc", numdoc);
		b.putString("codecli", codecli);
		b.putString("m_numDocForDuplication", m_numDocForDuplication);
		b.putString("datedoc", datedoc);
		b.putString("typedoc", typedoc);
		
		b.putString("m_typeDocForDuplication", typeDocForDuplication);
 //m_typeDocForDuplication
		intent.putExtras(b);
		startActivityForResult(intent, 0);
	}
	
	public void hideKeyb(EditText et)
	{
		getWindow().setSoftInputMode(
			    WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
	}
	public void hideKeyb()
	{
		getWindow().setSoftInputMode(
			    WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
			);
	}
	protected String getPrinterMacAddress()
	{
	  
	     
		   
		    //    String theBtMacAddress = "00:22:58:36:4B:54";SF
		     	String theBtMacAddress = "00:03:7A:25:23:7E";//DS
		     	theBtMacAddress=Fonctions.StringToMAC(Preferences.getValue(this, Espresso.MACPRINTER, "0"));
		     	if (theBtMacAddress.equals(""))
		     	{
		     		Fonctions.FurtiveMessageBox(this, "MAC ADRESS ERROR : "+theBtMacAddress);
		     		return "";
		     	}
		     	return theBtMacAddress;
	}
	static public void HHMM_to_TimePicker(Activity act,int rID,String hour)
	{
		try
		{
			if (hour.length()!=4) return;

			TimePicker dp=(TimePicker)act.findViewById(rID);

			int Hour=Integer.parseInt(   hour.substring(0,2));
			int Minut=Integer.parseInt(   hour.substring(2,4));

			dp.setCurrentHour(Hour);
			dp.setCurrentMinute(Minut);

		}
		catch(Exception ex)
		{

		}
		return ;
	}
	static public void HHMM_to_TimePicker(TimePicker dp,String hour)
	{
		try
		{
			if (hour.length()!=4) return;


			int Hour=Integer.parseInt(   hour.substring(0,2));
			int Minut=Integer.parseInt(   hour.substring(2,4));

			dp.setCurrentHour(Hour);
			dp.setCurrentMinute(Minut);

		}
		catch(Exception ex)
		{

		}
		return ;
	}
	static public String TimePicker_to_HHMM(Activity act,int rID)
	{
		try
		{

			TimePicker dp=(TimePicker)act.findViewById(rID);

			int hour=dp.getCurrentHour();




			int minute =dp.getCurrentMinute();


			String sthour=String.valueOf(hour);
			sthour="0"+sthour;
			int len=sthour.length();
			sthour=sthour.substring(len-2, len);


			String stMinute=String.valueOf(minute);
			stMinute="0"+stMinute;
			len=stMinute.length();
			stMinute=stMinute.substring(len-2, len);


			return sthour+stMinute;
		}
		catch(Exception ex)
		{

		}
		return "";
	}
	
	public void promptText(String title, String text,final boolean finishAct) {

		boolean bres = false;
	 
			final Dialog dialog = new Dialog(this);
			dialog.setContentView(R.layout.msgdialog);
			dialog.setTitle(title);
			final TextView tv = (TextView) dialog
					.findViewById(R.id.my_edittext);
			tv.setSingleLine(false);
			tv.setText(text);
 
			
			Button okButton = (Button) dialog.findViewById(R.id.OKButton);
			okButton.setOnClickListener(new View.OnClickListener() {
				public void onClick(View arg0) {
					dialog.dismiss();
					if (finishAct) returnOK();
				}
			});
			 
 
			
			 
			dialog.show();
	}
	
	public   void LaunchRemiseEnBanque()
	{
		Intent intent = new Intent(this,RemiseEnBanque.class);
		startActivityForResult(intent, LAUNCH_REMISEBANK);
	}
	public   void LaunchComment(String oldComment,String codecli,int maxlen)
	{
		Bundle b=new Bundle();
		b.putString("codeclient", codecli);
		b.putString("oldvalue", oldComment);
		b.putInt("maxlen", maxlen);
		Intent intent = new Intent(this,CommentaireActivity.class);
		intent.putExtras(b);
		startActivityForResult(intent, LAUNCH_COMMENTAIRE);
	}
	public   void LaunchEmail(String oldEmail,String codecli,int maxlen)
	{
		Bundle b=new Bundle();
		b.putString("codeclient", codecli);
		b.putString("oldvalue", oldEmail);
		b.putInt("maxlen", maxlen);
		Intent intent = new Intent(this,EmailActivity.class);
		intent.putExtras(b);
		startActivityForResult(intent, LAUNCH_EMAIL);
	}
	protected void returnOK(Intent ri)
	{
		setResult(RESULT_OK,ri);
		finish();
	}
	protected void returnOK()
	{
		setResult(RESULT_OK);
		finish();
	}
	 
	protected void returnCancel()
	{
		setResult(RESULT_CANCELED);
		finish();
	}
	
	 public void launchMaterielClient(String codecli,int id) {

			Intent i = new Intent(this, ListingMaterielClient.class);


			Bundle b = new Bundle();

			b.putString("codeclient",  codecli);

			i.putExtras(b);

			
			startActivityForResult(i, id);

		}
	 
	 public MyDB getDB()
	 {
		 return m_appState.m_db;
	 }
}
