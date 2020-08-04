package com.menadinteractive.printmodels;


import com.menadinteractive.maxpoilane.BaseActivity;
import com.menadinteractive.maxpoilane.R;
import com.menadinteractive.segafredo.communs.Global;

import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class printPreviewActivity extends BaseActivity{

	ImageView ivPrint;
	String data;
	public printPreviewActivity() {

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_printpreview);

		Bundle bundle = this.getIntent().getExtras();
		data=getBundleValue(bundle, "data");
		
		initGUI();
		initListeners();

	}

	void initGUI() {
		Typeface face=Typeface.createFromAsset(getAssets(), Global.FONT_PRINT);
		
		ivPrint=(ImageView)findViewById(R.id.image_header);
		TextView tvPreview=(TextView)findViewById(R.id.textViewPreview);
		tvPreview.setTypeface(face);
		
		tvPreview.setText(data);
		
	}

	void initListeners() {
		ivPrint.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.d("TAG", "printPreviewActivity->ivPrint.setOnClickListener");
				returnOK();
			}
		});
	}

}
