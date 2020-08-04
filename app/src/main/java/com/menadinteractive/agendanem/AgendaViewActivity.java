package com.menadinteractive.agendanem;

import java.text.DateFormat;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.SimpleCursorAdapter.ViewBinder;
import android.widget.TextView;

import com.menadinteractive.maxpoilane.BaseActivity;
import com.menadinteractive.maxpoilane.R;

public class AgendaViewActivity extends BaseActivity {
	public static int REQUEST_CODE_SHOW_CALENDAR = 100;
	public static int REQUEST_CODE_INSERT_EVENT = 101;

	/** GUI */
	ListView listview;


	/** Adapters */
	SimpleCursorAdapter eventsAdapter;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initActionBar();
		setContentView(R.layout.activity_agenda_view);
		initGUI();
		refreshList();
		
	}
	
	private void refreshList(){
		initCursorAdapter(getClientEvents(appBundle.getApplicationKey(), appBundle.getCodeClient()));
		initListeners();
		
	}

	private void initGUI(){
		listview = (ListView) findViewById(R.id.listview);
		
	}
	


	private void initActionBar(){
		ActionBar actionBar = getActionBar();
		actionBar.setSubtitle(appBundle.getApplicationName()+" - "+appBundle.getNameClient());
		actionBar.setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		addMenu(menu, R.string.see_agenda, R.drawable.action_calendar_view);
		addMenu(menu, R.string.take_appointment, R.drawable.action_calendar_add);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId())
		{
		case(R.string.see_agenda):
			launchCalendar(REQUEST_CODE_SHOW_CALENDAR,AgendaViewActivity.this);
		return true;
		case(R.string.take_appointment):    
			insertCalendarEvent(REQUEST_CODE_INSERT_EVENT);
		return true;
		case android.R.id.home:
			this.finish();
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
//		if(requestCode == REQUEST_CODE_SHOW_CALENDAR){
//			refreshList();
//		}
//		else if(requestCode == REQUEST_CODE_INSERT_EVENT){
//			refreshList();
//		}
		
		refreshList();
	}

	private void initListeners(){
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				promptEventAction(id);
			}
		});
		
		
	}
	private void initCursorAdapter(Cursor cursor){


		String[] columns = new String[] {CalendarContract.Events.TITLE, CalendarContract.Events.DESCRIPTION, CalendarContract.Events.DTSTART, CalendarContract.Events.DTEND};
		int[] to = new int[] {R.id.tv_title, R.id.tv_description, R.id.rl_top, R.id.tv_enddate};
		if(eventsAdapter != null){
			eventsAdapter.changeCursor(cursor);
			eventsAdapter.notifyDataSetChanged();
		}
		else{
			eventsAdapter = new SimpleCursorAdapter(this, R.layout.item_event, cursor, columns, to);


			setViewBinder(eventsAdapter);

			listview.setAdapter(eventsAdapter);
		}
		eventsAdapter.notifyDataSetChanged();

	}

	private void setViewBinder(SimpleCursorAdapter adapter){
		adapter.setViewBinder(new ViewBinder() {

			@Override
			public boolean setViewValue(View aView, Cursor aCursor, int aColumnIndex) {
				boolean result = false;
				if (aColumnIndex == aCursor.getColumnIndex( CalendarContract.Events.TITLE)) {
					String title = aCursor.getString(aColumnIndex);
					TextView textView = (TextView) aView;
					textView.setText(title);
					result = true;
				}
				else if (aColumnIndex == aCursor.getColumnIndex( CalendarContract.Events.DESCRIPTION)) {
					String description = aCursor.getString(aColumnIndex);
					TextView textView = (TextView) aView;
					textView.setText(description);
					result = true;
				}
				else if (aColumnIndex == aCursor.getColumnIndex( CalendarContract.Events.DTSTART)) {
					long start = aCursor.getLong(aColumnIndex);
					RelativeLayout rl = (RelativeLayout) aView;
					ImageView picto = (ImageView) rl.findViewById(R.id.iv_picto);
					TextView textView = (TextView) rl.findViewById(R.id.tv_startdate);
					
					long now = System.currentTimeMillis();
					if(start > now)
						picto.setBackgroundResource(R.drawable.picto_calendar_new);
					else
						picto.setBackgroundResource(R.drawable.picto_calendar_old);
					//Affichage date : http://javatechniques.com/blog/dateformat-and-simpledateformat-examples/
					
					textView.setText(getString(R.string.from)+" "+DateFormat.getDateTimeInstance().format(start));
					result = true;
				}
				else if (aColumnIndex == aCursor.getColumnIndex( CalendarContract.Events.DTEND)) {
					long end = aCursor.getLong(aColumnIndex);
					TextView textView = (TextView) aView;
					textView.setText("       "+getString(R.string.to)+"       "+DateFormat.getDateTimeInstance().format(end));
					result = true;
				}






				return result;

			}
		});
	}



	private void promptEventAction(final long eventID){
		final CharSequence[] items = {getString(R.string.edit_event), getString(R.string.delete_event), getString(R.string.show_client_on_map), getString(R.string.naviguate_client)};

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(appBundle.getNameClient());
		builder.setItems(items, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				if(items[item].equals(getString(R.string.edit_event))){
					editCalendarEvent(eventID,AgendaViewActivity.this);
				}
				else if(items[item].equals(getString(R.string.delete_event))){
					deleteCalendarEvent(eventID,AgendaViewActivity.this);
					refreshList();
				}
				else if(items[item].equals(getString(R.string.show_client_on_map))){
					showMap(appBundle.getAdresseClient(),AgendaViewActivity.this);
				}
				else if(items[item].equals(getString(R.string.naviguate_client))){
					naviguate(appBundle.getAdresseClient(),AgendaViewActivity.this);
				}
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}



	/** Events */
	protected Cursor getClientEvents(String applicationKey, String codeClient){
		Cursor result = null;
		String[] PROJECTION=new String[] {
				CalendarContract.Events._ID,
				CalendarContract.Events.TITLE,
				CalendarContract.Events.DESCRIPTION,
				CalendarContract.Events.DTSTART,
				CalendarContract.Events.DTEND

		};
		String where = CalendarContract.Events.TITLE +" LIKE '%"+applicationKey+"["+codeClient+"]%'"
				+" AND "+Events.DELETED+" = '0'";
		result = getContentResolver().query(CalendarContract.Events.CONTENT_URI, PROJECTION, where, null, CalendarContract.Events.DTSTART+" DESC");


		//		if(result != null && result.moveToFirst()){
		//			while(result.isAfterLast() == false)
		//			{
		//				Debug.Log(result.getString(result.getColumnIndex(CalendarContract.Events.TITLE)));
		//
		//				result.moveToNext();
		//			}
		//			result.close();
		//
		//		}


		return result;
	}
	
	static public   void launchCalendar(int requestCode,Activity c){
		//    	Intent intent = new Intent(Intent.ACTION_VIEW);
		//    	intent.setData(Calendars.CONTENT_URI);
		//    	startActivity(intent);
		//    	
		// http://developer.android.com/guide/topics/providers/calendar-provider.html#intent-view
		long startMillis = System.currentTimeMillis();
		Uri.Builder builder = CalendarContract.CONTENT_URI.buildUpon();
		builder.appendPath("time");
		ContentUris.appendId(builder, startMillis);
		Intent intent = new Intent(Intent.ACTION_VIEW)
		.setData(builder.build());
		c.startActivityForResult(intent,requestCode);
	}

	protected static void viewCalendarEvent(long eventID,Activity a){
		//http://developer.android.com/guide/topics/providers/calendar-provider.html#intents
		//http://developer.android.com/guide/topics/providers/calendar-provider.html#intent-view

		Uri uri = ContentUris.withAppendedId(Events.CONTENT_URI, eventID);
		Intent intent = new Intent(Intent.ACTION_VIEW).setData(uri);
		a.startActivity(intent);
	}

	protected static void editCalendarEvent(long eventID,Activity a){
		//http://developer.android.com/guide/topics/providers/calendar-provider.html#intents
		//http://developer.android.com/guide/topics/providers/calendar-provider.html#intent-view

		Uri uri = ContentUris.withAppendedId(Events.CONTENT_URI, eventID);
		Intent intent = new Intent(Intent.ACTION_EDIT).setData(uri);
		a.startActivity(intent);
	}
	
	protected static void deleteCalendarEvent(long eventID,Activity a){
		//http://developer.android.com/guide/topics/providers/calendar-provider.html#delete-event
		Uri uri = ContentUris.withAppendedId(Events.CONTENT_URI, eventID);
		int rows = a.getContentResolver().delete(uri, null, null);
		//Debug.Log("nb rows deleted : "+rows);
		
		
//		ContentResolver cr = getContentResolver();
//		Uri deleteUri = null;
//		deleteUri = ContentUris.withAppendedId(Events.CONTENT_URI, eventID);  // this gets an error = > Failed to get type for: content://com.android.calendar (Unknown URL content://com.android.calendar)
//		
//		//deleteUri = ContentUris.withAppendedId(CalendarContract.Calendars.CONTENT_URI, eventID); 
//		int rows = cr.delete(deleteUri, null, null);
		
		
	}
	
	protected void insertCalendarEvent(int requestCode){
		long startMillis = System.currentTimeMillis();
		Intent intent = new Intent(Intent.ACTION_INSERT);
		intent.setData(Events.CONTENT_URI);
		intent.putExtra(Events.TITLE, appBundle.getApplicationKey()+"["+appBundle.getCodeClient()+"] "+appBundle.getNameClient());
		intent.putExtra(Events.DTSTART, startMillis);
		intent.putExtra(Events.DTEND, startMillis + (60*60*1000));
		intent.putExtra(Events.EVENT_LOCATION, appBundle.getAdresseClient());
		startActivityForResult(intent, requestCode);
		
	}
	protected static void showMap(String address,Activity a){
		Intent intent1 = new Intent(Intent.ACTION_VIEW,
				Uri.parse("geo:0,0?q=" + address));
		a.startActivity(intent1);
	}

	protected static  void naviguate(String address,Activity a){
		Intent intent = new Intent(Intent.ACTION_VIEW,
				Uri.parse("google.navigation:q=" + address));
		a.startActivity(intent);
	}
}
