package com.menadinteractive.histo;

import java.util.ArrayList;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.menadinteractive.maxpoilane.BaseActivity;
import com.menadinteractive.maxpoilane.R;
import com.menadinteractive.segafredo.carto.MenuPopup;
import com.menadinteractive.segafredo.communs.Fonctions;
import com.menadinteractive.segafredo.communs.Global;
import com.menadinteractive.segafredo.communs.assoc;
import com.menadinteractive.segafredo.communs.myListView;
import com.menadinteractive.segafredo.db.TableClient;
import com.menadinteractive.segafredo.db.dbKD729HistoDocuments;
import com.menadinteractive.segafredo.db.dbKD730FacturesDues;
import com.menadinteractive.segafredo.findcli.FindCliActivity;
import com.menadinteractive.segafredo.plugins.Espresso;

public class FacturesDuesActivity extends BaseActivity implements OnClickListener {
	
	ArrayList<Bundle> histos;
	Handler handler;
	ImageButton ibFind;
	EditText etFilter;
	TextView tvTitre;
	myListView lv;
	 
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_facturesdues);
		 
		Bundle bundle = this.getIntent().getExtras();
 
		
		initGUI();
		setListeners();
	}
	
	void initGUI() {
		lv = (myListView) findViewById(R.id.listView1);
		etFilter= (EditText) findViewById(R.id.etFilter);
		ibFind= (ImageButton) findViewById(R.id.ibFind);
		tvTitre=(TextView) findViewById(R.id.textViewTitre);
		
		Fonctions.setFont(this, tvTitre, Global.FONT_REGULAR);
		
		handler =getHandler();
		PopulateList(0);
	}
	
	void setListeners()
	{
	
		
		ibFind.setOnClickListener(this);
	}
	
	private void PopulateList(int tri) {
		dbKD730FacturesDues hd=new dbKD730FacturesDues(m_appState.m_db);
		String filter="";
		
		if (tri==R.string.tri_par_client)
			filter=TableClient.FIELD_NOM;
		if (tri==R.string.tri_par_date_de_cr_ation || tri==0)
			filter=dbKD730FacturesDues.FIELD_DATEDOC;
		if (tri==R.string.tri_par_num_ro_de_pi_ce)
			filter=dbKD730FacturesDues.FIELD_NUMDOC;
		
		histos=hd.get(filter);
		
		
		ArrayList<assoc> assocs =new ArrayList<assoc>();
		assocs.add(new  assoc(myListView.TYPE_TEXTVIEW,R.id.tvRSCli,TableClient.FIELD_NOM));
		assocs.add(new  assoc(myListView.TYPE_TEXTVIEW,R.id.tvNomCli,TableClient.FIELD_ENSEIGNE));
		assocs.add(new  assoc(myListView.TYPE_TEXTVIEW,R.id.tvNumDoc,hd.FIELD_NUMDOC));
		assocs.add(new  assoc(myListView.TYPE_TEXTVIEW,R.id.tvNumCli,hd.FIELD_CODECLIENT));
		assocs.add(new  assoc(myListView.TYPE_TEXTVIEW,R.id.tvDateDoc,hd.FIELD_DATEDOC));
		assocs.add(new  assoc(myListView.TYPE_TEXTVIEW,R.id.tvDateEch,hd.FIELD_DATEECH));
		assocs.add(new  assoc(myListView.TYPE_TEXTVIEW_FLOAT2,R.id.tvMnt,hd.FIELD_MNTTTC));

		assocs.add(new  assoc(myListView.TYPE_TEXTVIEW_FLOAT2,R.id.tvMntDu,hd.FIELD_MNTDU ));
		assocs.add(new  assoc(myListView.TYPE_IMGVIEW,R.id.iv1,"ICON"));
 
		
		lv.attachValues(R.layout.item_list_facturesdues, histos,assocs,handler);
		
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
					String codecli=histos.get(id).getString(dbKD730FacturesDues.FIELD_CODECLIENT);
				 
					
					//Fonctions.FurtiveMessageBox(FindCliActivity.this, codecli);
				//	Fonctions.WriteProfileString(FacturesDuesActivity.this, "lastcli", cli.get(id).getString(Global.dbClient.FIELD_NOM));
					launchMenuCli(codecli);
				 
					break;
				 
				}

			}
		};
		return h;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	 
			addMenu(menu, R.string.tri_par_client,-1);
			addMenu(menu,R.string.tri_par_num_ro_de_pi_ce,-1);
			addMenu(menu, R.string.tri_par_date_de_cr_ation,-1);
			
			return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case R.string.tri_par_client: 
		case R.string.tri_par_date_de_cr_ation: 
		case R.string.tri_par_num_ro_de_pi_ce: 
			 PopulateList(item.getItemId());
			 break;
		default:
			return super.onOptionsItemSelected(item);
		}
		return super.onOptionsItemSelected(item);
	}

}
