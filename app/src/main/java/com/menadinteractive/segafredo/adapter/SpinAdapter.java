package com.menadinteractive.segafredo.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class SpinAdapter extends ArrayAdapter<String> {

    static class Holder{
            TextView text1;
    }
    
    Context context;
    ArrayList<String> data;
    private LayoutInflater mInflater;


    public SpinAdapter(Context context, int textViewResourceId,
                    List<String> objects) {
            super(context,0, objects);
            this.context = context;
            this.mInflater = LayoutInflater.from(context);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
            final String model = (String)getItem(position);
            Holder holder;


            if (convertView == null) {
                    convertView = mInflater.inflate(android.R.layout.simple_spinner_item, null);

                    holder = new Holder();
                    holder.text1 = (TextView) convertView.findViewById(android.R.id.text1);
                    convertView.setTag(holder);
            }
            else {
                    holder = (Holder)convertView.getTag();
            }
            holder.text1.setText(model);
            holder.text1.setGravity(Gravity.CENTER_HORIZONTAL);
            return convertView;

    }
    
    @Override
    public View getDropDownView(int position, View convertView,	
                    ViewGroup parent) {
            final String model = (String)getItem(position);
            Holder holder;


            if (convertView == null) {
                    convertView = mInflater.inflate(android.R.layout.simple_spinner_dropdown_item, null);

                    holder = new Holder();
                    holder.text1 = (TextView) convertView.findViewById(android.R.id.text1);
                    convertView.setTag(holder);
            }
            else {
                    holder = (Holder)convertView.getTag();
            }
            holder.text1.setText(model);
            return convertView;
    }


}
