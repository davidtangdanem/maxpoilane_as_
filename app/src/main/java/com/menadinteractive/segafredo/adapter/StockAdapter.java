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
import com.menadinteractive.segafredo.db.TableStock.structStock;

public class StockAdapter  extends ArrayAdapter<structStock>{
	Context context;
	private LayoutInflater mInflater;

	static class Holder{
		TextView text1;
		TextView text2;
		TextView stock;
		ImageView iv1;
	}

	public StockAdapter(Context context, int textViewResourceId,
			List<structStock> objects) {
		super(context,0, objects);
		this.context = context;
		this.mInflater = LayoutInflater.from(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final structStock model = (structStock)getItem(position);
		Holder holder;

		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.item_stock, null);
			holder = new Holder();
			holder.text1 = (TextView) convertView.findViewById(R.id.text1);
			holder.text2 = (TextView) convertView.findViewById(R.id.text2);
			holder.stock = (TextView) convertView.findViewById(R.id.stock);
			holder.iv1 = (ImageView) convertView.findViewById(R.id.iv1);
			convertView.setTag(holder);
		}
		else {
			holder = (Holder)convertView.getTag();
		}
		holder.text1.setText(model.PRO_LABEL);
		holder.text2.setText(model.PRO_CODE);
		holder.stock.setText(model.STOCK);
		return convertView;

	}
}
