package com.menadinteractive.agendanem;

import java.text.DateFormat;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
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

import com.menadinteractive.maxpoilane.AppBundle;
import com.menadinteractive.maxpoilane.BaseActivity;
import com.menadinteractive.maxpoilane.R;

public class AgendaListActivity extends BaseActivity {
	public static int REQUEST_CODE_SHOW_CALENDAR = 100;

	/** GUI */
	ListView listview;


	/** Adapters */
	SimpleCursorAdapter eventsAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initActionBar();
		setContentView(R.layout.activity_agenda_list);
		initGUI();

		String accesAutre=getAccesDirect();
		if (accesAutre.equals("")==false)
			insertCalendarEvent(AgendaViewActivity.REQUEST_CODE_INSERT_EVENT);
		
		refreshList();
	}

	String getAccesDirect()
	{
		try{
			return appBundle.getAccesDirect();
		}
		catch(Exception e)
		{
			return "";
		}
	}
	private void refreshList(){
		initCursorAdapter(getAllEvents(appBundle.getApplicationKey()));
		initListeners();
	}
	
	private void initGUI(){
		listview = (ListView) findViewById(R.id.listview);
	}

	private void initActionBar(){
		ActionBar actionBar = getActionBar();
		actionBar.setSubtitle(appBundle.getApplicationName());
		actionBar.setDisplayHomeAsUpEnabled(true);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		addMenu(menu, R.string.see_agenda, R.drawable.action_calendar_view);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId())
		{
		case(R.string.see_agenda):
			AgendaViewActivity.launchCalendar(REQUEST_CODE_SHOW_CALENDAR,this);
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
		if(requestCode == REQUEST_CODE_SHOW_CALENDAR || requestCode==AgendaViewActivity.REQUEST_CODE_INSERT_EVENT){
			refreshList();
		}
	}


	private void initListeners(){
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				promptEventAction(id);
			}
		});
	}
	
	
	protected void insertCalendarEvent(int requestCode){
		long startMillis = System.currentTimeMillis();
		Intent intent = new Intent(Intent.ACTION_INSERT);
		intent.setData(Events.CONTENT_URI);
		intent.putExtra(Events.TITLE, appBundle.getApplicationKey()+"[AUTRE] "+appBundle.getAccesDirect());
		intent.putExtra(Events.DTSTART, startMillis);
		intent.putExtra(Events.DTEND, startMillis + (60*60*1000));
		intent.putExtra(Events.EVENT_LOCATION,"");
		startActivityForResult(intent, requestCode);
		
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
		final CharSequence[] items = {getString(R.string.select_client),getString(R.string.edit_event), getString(R.string.delete_event), getString(R.string.show_client_on_map), getString(R.string.naviguate_client)};

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getEventTitle(eventID));
		builder.setItems(items, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				if(items[item].equals(getString(R.string.select_client))){
					Intent i = new Intent();
					Bundle b = new Bundle();
					b.putString(AppBundle.CodeClient, getCodeClientFromEvent(eventID));
					i.putExtras(b);
					setResult(RESULT_OK, i);
					finish();
				}
				else if(items[item].equals(getString(R.string.edit_event))){
					AgendaViewActivity.editCalendarEvent(eventID,AgendaListActivity.this);
				}
				else if(items[item].equals(getString(R.string.delete_event))){
					AgendaViewActivity.deleteCalendarEvent(eventID,AgendaListActivity.this);
					refreshList();
				}
				else if(items[item].equals(getString(R.string.show_client_on_map))){
					AgendaViewActivity.showMap( getAddresseClientFromEvent(eventID ),AgendaListActivity.this);
				}
				else if(items[item].equals(getString(R.string.naviguate_client))){
					AgendaViewActivity.naviguate( getAddresseClientFromEvent(eventID ),AgendaListActivity.this);
				}
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}
	
	

	/** Events */
	protected Cursor getAllEvents(String applicationKey){
		Cursor result = null;
		String[] PROJECTION=new String[] {
				CalendarContract.Events._ID,
				CalendarContract.Events.TITLE,
				CalendarContract.Events.DESCRIPTION,
				CalendarContract.Events.DTSTART,
				CalendarContract.Events.DTEND

		};
		String where = CalendarContract.Events.TITLE +" LIKE '%"+applicationKey+"[%'"
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
	
	public String getEventTitle(long eventID){
		String result = "";
		Cursor cursor = null;
		String[] PROJECTION=new String[] {
				CalendarContract.Events.TITLE

		};
		String where = CalendarContract.Events._ID +" = '"+eventID+"'"
				+" AND "+Events.DELETED+" = '0'";
		cursor = getContentResolver().query(CalendarContract.Events.CONTENT_URI, PROJECTION, where, null, CalendarContract.Events.DTSTART+" DESC");
		if(cursor != null && cursor.moveToFirst()){
			result = cursor.getString(cursor.getColumnIndex(CalendarContract.Events.TITLE));
			cursor.close();
		}
		return result;
	}
	
	public String getCodeClientFromEvent(long eventID){
		String result = "";
		Cursor cursor = null;
		String[] PROJECTION=new String[] {
				CalendarContract.Events.TITLE

		};
		String where = CalendarContract.Events._ID +" = '"+eventID+"'"
				+" AND "+Events.DELETED+" = '0'";
		cursor = getContentResolver().query(CalendarContract.Events.CONTENT_URI, PROJECTION, where, null, CalendarContract.Events.DTSTART+" DESC");
		if(cursor != null && cursor.moveToFirst()){
			result = cursor.getString(cursor.getColumnIndex(CalendarContract.Events.TITLE));
			cursor.close();
		}
		
		if(!result.equals("")){
			int start = result.indexOf("[");
			int end = result.indexOf("]");
			if(start != -1 && end != -1)
				result = result.substring(start+1, end);
		}
		
		return result;
		
	}
	
	public String getAddresseClientFromEvent(long eventID){
		String result = "";
		Cursor cursor = null;
		String[] PROJECTION=new String[] {
				Events.EVENT_LOCATION

		};
		String where = CalendarContract.Events._ID +" = '"+eventID+"'"
				+" AND "+Events.DELETED+" = '0'";
		cursor = getContentResolver().query(CalendarContract.Events.CONTENT_URI, PROJECTION, where, null, CalendarContract.Events.DTSTART+" DESC");
		if(cursor != null && cursor.moveToFirst()){
			result = cursor.getString(cursor.getColumnIndex(Events.EVENT_LOCATION));
			cursor.close();
		}
		
		return result;
		
	}




}
