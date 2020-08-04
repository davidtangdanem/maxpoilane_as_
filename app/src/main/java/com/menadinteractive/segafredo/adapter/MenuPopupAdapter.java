package com.menadinteractive.segafredo.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.menadinteractive.maxpoilane.R;
/*
 * Icons sur http://www.iconfinder.com/search/?q=iconset%3Amacosxstyle
 */
public class MenuPopupAdapter extends ArrayAdapter<MenuPopupItem>{
	Context context;
	private LayoutInflater mInflater;

	static class Holder{
		TextView text1;
		ImageView iv1;
	}

	public MenuPopupAdapter(Context context, int textViewResourceId,
			List<MenuPopupItem> objects) {
		super(context,0, objects);
		this.context = context;
		this.mInflater = LayoutInflater.from(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final MenuPopupItem model = (MenuPopupItem)getItem(position);
		Holder holder;

		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.item_menu, null);
			holder = new Holder();
			holder.text1 = (TextView) convertView.findViewById(R.id.text1);
			holder.iv1 = (ImageView) convertView.findViewById(R.id.iv1);
			convertView.setTag(holder);
		}
		else {
			holder = (Holder)convertView.getTag();
		}
		holder.text1.setText(model.getLabelId());
		holder.iv1.setBackgroundResource(model.getDrawableId());
		return convertView;

	}
}
