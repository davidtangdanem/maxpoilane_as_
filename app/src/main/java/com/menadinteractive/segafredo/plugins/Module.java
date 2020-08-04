package com.menadinteractive.segafredo.plugins;

import android.graphics.Rect;
import android.graphics.drawable.Drawable;

public class Module {
	String name;
	String nameId;
	int imageId;
	String hint;
	Drawable drawable;
	
	public Module(String name, int imageId) {
		super();
		this.name = name;
		this.imageId = imageId;
	}
	
	public Module(String name, Drawable d, int defaultImageId) {
		super();
		this.name = name;
		this.drawable = d;
		this.imageId = defaultImageId;
		if(d != null)
			d.setBounds(new Rect(0, 0, 96, 96));
	}
	
	public Module(String name, int imageId, String hint) {
		super();
		this.name = name;
		this.imageId = imageId;
		this.hint = hint;
	}
	public Module(String nameId, String name, int imageId, String hint) {
		super();
		this.name = name;
		this.imageId = imageId;
		this.hint = hint;
		this.nameId = nameId;
	}


	
	
	public Drawable getDrawable() {
		return drawable;
	}
	public void setDrawable(Drawable drawable) {
		this.drawable = drawable;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getImageId() {
		return imageId;
	}
	public void setImageId(int imageId) {
		this.imageId = imageId;
	}

	public String getHint() {
		return hint;
	}

	public void setHint(String hint) {
		this.hint = hint;
	}
	public String getNameId() {
		return nameId;
	}
	public void setNameId(String nameId) {
		this.nameId = nameId;
	}




}
