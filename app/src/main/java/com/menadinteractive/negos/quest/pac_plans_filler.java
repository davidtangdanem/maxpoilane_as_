package com.menadinteractive.negos.quest;

import java.util.ArrayList;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.menadinteractive.maxpoilane.BaseActivity;
import com.menadinteractive.maxpoilane.R;
import com.menadinteractive.segafredo.communs.Fonctions;
import com.menadinteractive.segafredo.communs.Global;
import com.menadinteractive.segafredo.db.dbKD91EntPlan;
import com.menadinteractive.segafredo.db.dbKD92LinPlan;


/**
 * formulaire dynamique de remplissage du PAC
 * 
 * @author marcvouaux
 * 
 */
public class pac_plans_filler extends BaseActivity {

	LinearLayout relativeLayout;
	LinearLayout ll;
	ArrayList<dbKD91EntPlan.data> entPlans;
	ArrayList<dbKD92LinPlan.data>  tabVal ;//on parcourera ce tableau � la fin qui liera le controle � la donn�e
	
	String datekey;
	String codecli;
	String codesoc;
	String enseigne;
	
	createQuestForm formQuest;
	

	Button buttonSend,buttonKeep;
	
	public pac_plans_filler() {

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
	/*	case android.R.id.home:
			finish();
			return true;
*/
		default:
			return super.onOptionsItemSelected(item);
		}
	}


	@Override
	protected void onStart() {
		super.onStart();
//		setBackground(true);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pac_plans_filler);
		relativeLayout = (LinearLayout) 
				findViewById(R.id.rl_top);
	
		
		Bundle bundle = this.getIntent().getExtras();
		codecli= this.getBundleValue(bundle, "codecli");
		codesoc= this.getBundleValue(bundle, "codesoc");
		enseigne= this.getBundleValue(bundle, "enseigne");
		datekey= this.getBundleValue(bundle,"datekey");
		if (datekey.equals(""))//la clef, 1 questionnaire parclient et par date
			datekey=Fonctions.getYYYYMMDD();
		//V�roull� l'ecran
		setFinishOnTouchOutside(false);
		
		formQuest=new createQuestForm(this,relativeLayout,codecli,datekey,enseigne,codesoc);

		initGUI();
		initListeners();
	}
	
	
	
	void initGUI()
	{
		buttonKeep=(Button) findViewById(R.id.buttonKeep);
		buttonSend=(Button) findViewById(R.id.buttonSend);
	
	}
	
	void initListeners()
	{
		buttonKeep.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//save(0);
				end();
				
			}
		});
		
		buttonSend.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (formQuest.save(1)==false)
				{
					Fonctions.FurtiveMessageBox(Global.lastErrorMessage, m_appState);
					return;
				}
				end();			
			}
		});
	}
	
	void end()
	{
		finish();
	}
	
	/**
	 * on suve en quittant la fenetre
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				|| keyCode == KeyEvent.KEYCODE_HOME) {

	//		boolean bres = save();
	//		this.finish();
			return false;
		}	
		else
			return super.onKeyDown(keyCode, event);

	}
	

	
	//controel que les todo sont remplies
	boolean controlIfTodosAreOk()
	{
		try {
	
			for (int i=0;i<tabVal.size();i++)
			{

						
				String value="ok";
				Object obj=tabVal.get(i).object; 

				if (tabVal.get(i).TYPE.equals(dbKD92LinPlan.type_int))
				{
					value=formQuest.getEditViewText(((EditText)obj));
				}
				else
					if (tabVal.get(i).TYPE.equals(dbKD92LinPlan.type_float))
					{
						value=formQuest.getEditViewText(((EditText)obj));
					}
					else
						if (tabVal.get(i).TYPE.equals(dbKD92LinPlan.type_text))
						{
							value=formQuest.getEditViewText(((EditText)obj));
						}
				if (value.equals(""))
				{
					((EditText)obj).requestFocus();
					return false;
				}
			}
			
			return true;
		} catch (Exception e) {
			// TODO: handle exception
		}
		return true;
	}


}