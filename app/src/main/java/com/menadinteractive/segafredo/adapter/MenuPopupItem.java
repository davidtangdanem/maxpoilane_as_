package com.menadinteractive.segafredo.adapter;

public class MenuPopupItem {
	int labelId;
	int drawableId;


	public MenuPopupItem(int labelId, int drawableId) {
		super();
		this.labelId = labelId;
		this.drawableId = drawableId;
	}
	public int getLabelId() {
		return labelId;
	}
	public void setLabelId(int labelId) {
		this.labelId = labelId;
	}
	public int getDrawableId() {
		return drawableId;
	}
	public void setDrawableId(int drawableId) {
		this.drawableId = drawableId;
	}


}
