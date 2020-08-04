package com.menadinteractive.segafredo.communs;

import android.graphics.Color;


public class assoc 
{

	 int typeCtrl;//0=tv,1=img
	 int idCtrl;
	 String dbFld;
	 boolean goneifempty;//que faire si vide, INVISIBLE, EMPTY
	 String bkgColor="";
	
	public assoc(int t,int i,String f)
	{
		 
		typeCtrl=t;
		idCtrl=i;
		dbFld=f;
		goneifempty=false;
		
	}
	public assoc(int t,int i,String f,boolean goneifempty)
	{
		 
		typeCtrl=t;
		idCtrl=i;
		dbFld=f;
		this.goneifempty=true;
		
	}
	public assoc(int t,int i,String f,String bgColor)
	{
		 
		typeCtrl=t;
		idCtrl=i;
		dbFld=f;
		goneifempty=false;
		bkgColor=bgColor;
	}
}