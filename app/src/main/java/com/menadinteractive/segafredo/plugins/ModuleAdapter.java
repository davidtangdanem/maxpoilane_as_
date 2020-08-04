package com.menadinteractive.segafredo.plugins;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.menadinteractive.maxpoilane.R;


public class ModuleAdapter extends ArrayAdapter<Module> {
	private LayoutInflater mInflater;
	Context context;
	int layoutId;
	static class Holder{
		TextView textView;
		TextView hintView = null;
	}


	public ModuleAdapter(Context context, int textViewResourceId,
			List<Module> objects) {
		super(context,0, objects);
		this.layoutId = textViewResourceId;
		this.context = context;
		this.mInflater = LayoutInflater.from(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final Module module = getItem(position);
		Holder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(layoutId, parent, false);
			holder = new Holder();
			holder.textView = (TextView) convertView.findViewById(R.id.moduleName);
			if(layoutId == R.layout.module_item_with_hint)
				holder.hintView = (TextView) convertView.findViewById(R.id.moduleHint);
			convertView.setTag(holder);
		}
		else {
			holder = (Holder)convertView.getTag();
		}

		if(module.getDrawable() != null)
			holder.textView.setCompoundDrawables(null, module.getDrawable(), null, null);
		else
			holder.textView.setCompoundDrawablesWithIntrinsicBounds(0, module.getImageId(), 0, 0);
		
		holder.textView.setCompoundDrawablePadding(0);
		holder.textView.setText(module.getName());
		holder.textView.setPadding(0, 0, 0, 0);

		//if(hintView != null && module.getHint() != null && module.getHint().length() >0 && !module.equals(""))
		if(holder.hintView != null)
			holder.hintView.setText(module.getHint());
		return convertView;

	}

}
