package com.menadinteractive.negos.quest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.graphics.Color;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;

import com.menadinteractive.segafredo.communs.Fonctions;
import com.menadinteractive.segafredo.communs.Global;
import com.menadinteractive.segafredo.db.Preferences;
import com.menadinteractive.segafredo.db.dbKD91EntPlan;
import com.menadinteractive.segafredo.db.dbKD92LinPlan;
import com.menadinteractive.segafredo.db.dbKD93PlanFiller;
import com.menadinteractive.segafredo.plugins.Espresso;

public class createQuestForm {

	Activity parentActivity;
	LinearLayout parentLayout;
	LinearLayout ll;
	String codecli;
	String datekey;
	String enseigne;
	String codesoc;
	
	int fontsize_titre=22;//applique un ratio au police pour petits ecrans
	int fontsize_lblquest=14;
	
	ArrayList<dbKD91EntPlan.data> entPlans;
	ArrayList<dbKD92LinPlan.data>  tabVal ;//on parcourera ce tableau � la fin qui liera le controle � la donn�e

	
	public createQuestForm(Activity act,LinearLayout pll,String codeclient,String date,String ens,String soc)
	{
		enseigne=ens;
		codesoc=soc;
		codecli=codeclient;
		datekey=date;
		parentActivity=act;
		parentLayout=pll;
		
		tabVal=new ArrayList<dbKD92LinPlan.data> ();
		entPlans=Global.dbKDEntPlan.loadActive("",enseigne,codesoc);

		ll=addLinearLayout(0);
		parentLayout.addView(ll);
		ll.setPadding(10, 50, 10, 0);
		TableLayout tl =addTab();
		ll.addView(tl);
		
		for (int i=0;i<entPlans.size();i++)
		{
			initiatePlan(entPlans.get(i).CODEPLAN);
			initiateQuestions(entPlans.get(i),tl);
		}
		
		
	}
	
	public boolean save(int send)
	{
		boolean result=false;
		try {
			//on fait un premier passage pour vérifier les zones obligatoires
			//on efface les donn�es eventuellement deja existante avant de tout resauver
			for (int k=0;k<tabVal.size();k++)
			{
				String value="";
				Object obj=tabVal.get(k).object; 
		/*		if (tabVal.get(k).TYPE.equals(dbKD92LinPlan.type_bool))
				{
					value=String.valueOf( getCheckBoxValue ((CheckBox)obj));
				}
				else*/
					if (tabVal.get(k).TYPE.equals(dbKD92LinPlan.type_note5))
					{
						value=String.valueOf( getRatingBarValue ((RatingBar)obj));
					}
					else
						if (tabVal.get(k).TYPE.equals(dbKD92LinPlan.type_int))
						{
							value=getEditViewText(((EditText)obj));
							
							if (  Fonctions.GetStringDanem(tabVal.get(k).ISTODO).equals("1"))
							{
								if (Fonctions.convertToInt(value)==0)
								{
									((EditText)obj).requestFocus();
									Global.lastErrorMessage=tabVal.get(k).LBL+" est obligatoire";
									return false;
								}
							}
						}
						else
							if (tabVal.get(k).TYPE.equals(dbKD92LinPlan.type_float))
							{
								value=getEditViewText(((EditText)obj));
								if (  Fonctions.GetStringDanem(tabVal.get(k).ISTODO).equals("1"))
								{
									if (Fonctions.convertToFloat(value)==0)
									{
										((EditText)obj).requestFocus();
										Global.lastErrorMessage=tabVal.get(k).LBL+" est obligatoire";
										return false;
									}
								}
							}
							else
								if (tabVal.get(k).TYPE.equals(dbKD92LinPlan.type_text))
								{
									value=getEditViewText(((EditText)obj));

									if (  Fonctions.GetStringDanem(tabVal.get(k).ISTODO).equals("1"))
									{
										if (value.equals(""))
										{
											Global.lastErrorMessage=tabVal.get(k).LBL+" est obligatoire";
											((EditText)obj).requestFocus();
											return false;
										}
									}
								}
								else
									if (tabVal.get(k).TYPE.equals(dbKD92LinPlan.type_choix) ||
											tabVal.get(k).TYPE.equals(dbKD92LinPlan.type_bool))
									{
										value=getSpinnerValue((Spinner)obj);

										if (  Fonctions.GetStringDanem(tabVal.get(k).ISTODO).equals("1"))
										{
											if (value.equals(""))
											{
												Global.lastErrorMessage=tabVal.get(k).LBL+" est obligatoire";
												((EditText)obj).requestFocus();
												return false;
											}
										}
									}
			
					
				}
			
			
			for (int i=0;i<tabVal.size();i++)
			{

						
				String value="";
				Object obj=tabVal.get(i).object; 
				/*if (tabVal.get(i).TYPE.equals(dbKD92LinPlan.type_bool))
				{
					value=String.valueOf( getCheckBoxValue ((CheckBox)obj));
				}
				else*/
					if (tabVal.get(i).TYPE.equals(dbKD92LinPlan.type_note5))
					{
						value=String.valueOf( getRatingBarValue ((RatingBar)obj));
					}
					else
						if (tabVal.get(i).TYPE.equals(dbKD92LinPlan.type_int))
						{
							value=getEditViewText(((EditText)obj));
						}
						else
							if (tabVal.get(i).TYPE.equals(dbKD92LinPlan.type_float))
							{
								value=getEditViewText(((EditText)obj));
							}
							else
								if (tabVal.get(i).TYPE.equals(dbKD92LinPlan.type_text))
								{
									value=getEditViewText(((EditText)obj));
								}
								else
									if (tabVal.get(i).TYPE.equals(dbKD92LinPlan.type_choix) ||
											tabVal.get(i).TYPE.equals(dbKD92LinPlan.type_bool))
									{
										value=getSpinnerValue((Spinner)obj) ;
									}
				
				dbKD93PlanFiller.data data=new dbKD93PlanFiller.data();
				data.CODECLI=codecli;
				data.CODEPLAN=tabVal.get(i).CODEPLAN;
				data.CODEQUEST=tabVal.get(i).CODEQUEST;
				data.CODEREP=Preferences.getValue( parentActivity, Espresso.LOGIN, "0");
				data.DATECREAT=Fonctions.getYYYYMMDDhhmmss();
				data.DATEMODIF=Fonctions.getYYYYMMDDhhmmss();
				data.DATEKEY=datekey;
				data.LBL=tabVal.get(i).LBL;
				data.REPONSE=value;
				data.SEND=String.valueOf(send);
				data.TYPE=tabVal.get(i).TYPE;
				data.CODESOC=Preferences.getValue( parentActivity, Espresso.CODE_SOCIETE, "0");

				result=Global.dbKDFillPlan.save(data);
				
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	/*
	 * on cree un questionnaire
	 */
	public void initiatePlan(String text)
	{
		TextView tv2 = new TextView(parentActivity);

		tv2.setText(text);
		tv2.setTextSize(fontsize_titre);
			tv2.setLayoutParams(new LayoutParams(
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
			
			ll.addView(tv2);
	}
	
	/**
	 * on cr�e les questions du questionnaire "it�r�"
	 * @param entPlan
	 * @param tl
	 */
	public void initiateQuestions(dbKD91EntPlan.data entPlan,TableLayout tl)
	{
		//oncharge les lignes du plan
		ArrayList<dbKD92LinPlan.data>  tabValTemp= Global.dbKDLinPlan.load(entPlan.CODEPLAN);
		for (int i=0;i<tabValTemp.size();i++)
			tabVal.add(tabValTemp.get(i));
			
		TableRow trTitre =addRow();
		
	//	trTitre.addView(addColImageView(tabVal.get(i).TYPE));
		trTitre.addView(addColLbl(/*getString(R.string.plan_filler_lbl)+*/entPlan.LBL, Color.RED,fontsize_titre,1)) ;
		tl.addView(trTitre,new TableLayout.LayoutParams(
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
		

		
		for (int i=0;i<tabValTemp.size();i++)
		{
			dbKD93PlanFiller.data data=new dbKD93PlanFiller.data();
			//on va recharger la r�ponse, si elle existe deja
			boolean reponse=Global.dbKDFillPlan.load(data, codecli, datekey, tabValTemp.get(i).CODEQUEST,tabValTemp.get(i).CODEPLAN);
			
			//on cr�e un ligne pour la nvelle question
			TableRow tr = addRow();
		
			//on met le libell� de la question
			tr.addView(addColLbl(tabValTemp.get(i).LBL, Color.BLACK,fontsize_lblquest,1));
			
			//on ajoute l'objet de saisie de la question
			Object obj;
			if (tabValTemp.get(i).TYPE.equals(dbKD92LinPlan.type_bool))
			{
				obj=addColSpinner("OUI;NON",data.REPONSE);;//addColCheckBox();
				tr.addView((Spinner)obj);
				if (reponse)
				{
				//	setCheckBoxValue((CheckBox)obj,Fonctions.convertToInt(data.REPONSE));
				}
			}
			else if (tabValTemp.get(i).TYPE.equals(dbKD92LinPlan.type_choix))
			{
				obj=addColSpinner(tabValTemp.get(i).CHOIX,data.REPONSE);
				tr.addView((Spinner)obj);
				if (reponse)
				{
					//setCheckBoxValue((Spinner)obj,Fonctions.convertToInt(data.REPONSE));
				}
			}
			else if (tabValTemp.get(i).TYPE.equals(dbKD92LinPlan.type_note5))
			{
				LinearLayout lly=new LinearLayout(parentActivity);
				lly.setLayoutParams(new TableLayout.LayoutParams(
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
								
				obj=addRatingBar(5);
				
				//on passe par un linear
				LinearLayout ll=new LinearLayout(parentActivity);
				LayoutParams prm=new LayoutParams(
						android.view.ViewGroup.LayoutParams.MATCH_PARENT,
						android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
				
				ll.setLayoutParams(prm);
				ll.addView((RatingBar)obj);
				
				tr.addView(ll);
				//tr.addView(lly);
				if (reponse)
				{
					setRatingBarValue((RatingBar)obj,Fonctions.convertToInt(data.REPONSE));
				}
			}
			else
			{
				obj=addColEditText(tabValTemp.get(i).TYPE);
				
				//si type texte on attend la prochaine ligne pour ajouter l'edit
				//sinon on ajoute le controle
				if (tabValTemp.get(i).TYPE.equals(dbKD92LinPlan.type_text))
				{
					tr.addView(addColLbl("", Color.BLACK,fontsize_lblquest,1));
				}
				else //si type numerique ou float
				{
					((EditText)obj).setWidth(100);
				
					tr.addView((EditText)obj);
				}
				if (reponse)
				{
					this.setEditViewText((EditText)obj, data.REPONSE);
				}
			}
			tabValTemp.get(i).object=obj;
			
			tl.addView(tr,new TableLayout.LayoutParams(
					android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
					android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
			
			//si type texte on cr�e un ligne sp�ciale avec l'edit
			if (tabValTemp.get(i).TYPE.equals(dbKD92LinPlan.type_text))
			{
				//on cr�e un ligne pour la nvelle question
				TableRow tr2 = addRow();
				
				//tr2.addView(addColLbl("", Color.BLACK,fontsize_lblquest,1));
				((EditText)obj).setGravity(Gravity.TOP);
				View v=(EditText)obj;
				LayoutParams layprm=new LayoutParams();
				layprm.span=3;
				v.setLayoutParams(layprm);
			
				tr2.addView(v);
				
				
				
				tl.addView(tr2,new TableLayout.LayoutParams(
						android.view.ViewGroup.LayoutParams.FILL_PARENT,
						android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
			}
			
			
		}
	
	}
	public String getEditViewText(EditText et)
	{
		try
		{

			return et.getText().toString();
		}
		catch(Exception ex)
		{

		}
		return "";
	}
	public int getCheckBoxValue( CheckBox et )
	{
		try
		{

			boolean b=et.isChecked();
			if (b) 
				return 1;
			else
				return 0;

		}
		catch(Exception ex)
		{

		}
		return 0;
	}
	public int getRatingBarValue( RatingBar et )
	{
		try
		{

			int b=(int)et.getRating();
			return b;

		}
		catch(Exception ex)
		{

		}
		return 0;
	}
	public LinearLayout addLinearLayout(int id)
	{
		LinearLayout ll=new LinearLayout(parentActivity);
		ll.setLayoutParams(new LayoutParams(
				android.view.ViewGroup.LayoutParams.MATCH_PARENT,
				android.view.ViewGroup.LayoutParams.FILL_PARENT));
		ll.setOrientation(LinearLayout.VERTICAL);
		ll.setId(id);
		
		return ll;
	}
	//ajoute un tableau
	public TableLayout addTab() {
		TableLayout tl = new TableLayout(parentActivity);
		
		tl.setLayoutParams(new LayoutParams(
				android.view.ViewGroup.LayoutParams.MATCH_PARENT,
				android.view.ViewGroup.LayoutParams.FILL_PARENT));
		tl.setStretchAllColumns(true);//ca fout le bordel avec les stars
		tl.setHorizontalScrollBarEnabled(true);
		tl.setVerticalScrollBarEnabled(true);
		return tl;
	}

		
	//ajoute une ligne
	public TableRow addRow() {
		TableRow tr = new TableRow(parentActivity);
		tr.setLayoutParams(new LayoutParams(
				android.view.ViewGroup.LayoutParams.MATCH_PARENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT));

		//tr.setAlpha(19);
		return tr;
	}

	//creation de textview
	public TextView addColLbl(String text, int color,int fontSize,int span) {

		TextView tv2 = new TextView(parentActivity);

	
		tv2.setText(text);
		tv2.setTextSize(fontSize);
		LayoutParams prm=new LayoutParams();
		//prm.span=span;
		prm.width=android.view.ViewGroup.LayoutParams.MATCH_PARENT;
		prm.height=android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
		prm.weight=1;
		
		tv2.setLayoutParams(prm);

		if (color != -1)
			tv2.setTextColor(color);

		return tv2;

	}
	
	//creation de la checkbox
	public CheckBox addColCheckBox() {

		CheckBox tv2 = new CheckBox(parentActivity);

	
		tv2.setLayoutParams(new LayoutParams(
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT));

	
		return tv2;

	}
	//ajoute un spinner avec choix en separateur
	public Spinner addColSpinner(String choix,String selVal) {

		Spinner tv2 = new Spinner(parentActivity);

		fillChoix(tv2, choix, selVal);
	
		tv2.setLayoutParams(new LayoutParams(
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT));

	
		return tv2;

	}
	//creation des etoiles
	public RatingBar addRatingBar(int nbrstars) {

		
			RatingBar tv2 = new RatingBar(parentActivity);

			tv2.setNumStars(nbrstars);
			tv2.setMax(nbrstars);
			tv2.setStepSize((float)1);
			
			
			
		//	LayoutParams prm=new LayoutParams();
		//	prm.weight=1;
		//	tv2.setLayoutParams(prm);
		
			return tv2;

	}
	//creation de l'edit
	public EditText addColEditText(String type) {

		LayoutParams prm=new LayoutParams();
		//prm.span=span;
		prm.width=android.view.ViewGroup.LayoutParams.MATCH_PARENT;
		prm.height=android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
		prm.weight=1;
		
		
		
		EditText tv2 = new EditText(parentActivity);

		if (type.equals(dbKD92LinPlan.type_text))
		{
			tv2.setTextSize(fontsize_lblquest);
			tv2.setLines(4);
			tv2.setSingleLine(false);
		}
		if (type.equals(dbKD92LinPlan.type_int))
		{
			tv2.setInputType(InputType.TYPE_CLASS_NUMBER);
		}
		if (type.equals(dbKD92LinPlan.type_float))
		{
			tv2.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
		}
		
/*		tv2.setLayoutParams(new LayoutParams(
				android.view.ViewGroup.LayoutParams.MATCH_PARENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
*/
		tv2.setLayoutParams(prm);
	
		return tv2;

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
	void SetCheckBoxValue(Activity act,int rID,int val )
	{
		try
		{
			CheckBox et=(CheckBox)act.findViewById(rID);

			setCheckBoxValue(et,val);

		}
		catch(Exception ex)
		{

		}
	}
	
	public void setCheckBoxValue(CheckBox et,int val)
	{
		try
		{
			boolean b=false;
			if (val==1) 
				b=true;
			else
				b=false;
	
			et.setChecked(b);
		}
		catch(Exception ex)
		{

		}
	}
	public void setRatingBarValue(RatingBar et,int val)
	{
		try
		{
			boolean b=false;
			if (val==1) 
				b=true;
			else
				b=false;
	
			et.setRating(val);
		}
		catch(Exception ex)
		{

		}
	}
	public void setCheckBoxValue(CheckBox et,boolean b)
	{
		try
		{

	
			et.setChecked(b);
		}
		catch(Exception ex)
		{

		}
	}
	
	void fillChoix(Spinner spinner, String Choix, String selVal) {
		try {
				int pos = 0;
				
				Choix=";"+Choix;//on ajoute le blanc ici pour eviter à l'utilisateur sur le serveur d'y penser
				List<String> items = Arrays.asList(Choix.split(";"));
				String[] arrChoix=new String[items.size()];				
				items.toArray(arrChoix);
				
				if ( selVal==null || selVal.equals(""))
					pos=0;
				else
				{
					for (int i=0;i<items.size();i++)
					{
						if (items.get(i).toString().equals(selVal))
						{
							pos=i;
						}
					}
				}
				
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.parentActivity,
						android.R.layout.simple_spinner_item, arrChoix);
				adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				spinner.setAdapter(adapter);

				spinner.setSelection(pos);
		} catch (Exception ex) {

		}

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
}

