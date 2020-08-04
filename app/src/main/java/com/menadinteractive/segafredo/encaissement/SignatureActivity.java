package com.menadinteractive.segafredo.encaissement;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import com.menadinteractive.maxpoilane.ExternalStorage;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Bundle;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class SignatureActivity  extends Activity{

	ExternalStorage externalStorage;
	/** Menu ID for the command to clear the window. */
	private static final int CLEAR_ID = Menu.FIRST;

	String mFileName;

	/** The view responsible for drawing the window. */
	SignatureView mView;


	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		externalStorage = new ExternalStorage(this, false);
		Bundle bundle = this.getIntent().getExtras();
		mFileName= bundle.getString("filename");

		// Create and attach the view that is responsible for painting.
		mView = new SignatureView(this, null);
		setContentView(mView);
		mView.requestFocus();



	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) 
	{

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if(mView.isFilled()){
				saveSignature();
				setResult(RESULT_OK);
			}
			else
				setResult(RESULT_CANCELED);
			finish(); 

			return super.onKeyDown(keyCode, event);
		}
		else
			return super.onKeyDown(keyCode, event);

	}
	@Override public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, CLEAR_ID, 0, "Clear");

		return super.onCreateOptionsMenu(menu);
	}



	@Override public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case CLEAR_ID:

			mView.clear();
			mView.setFilled(false);
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	void saveSignature()
	{
		mView.setDrawingCacheEnabled(true);
		Bitmap b = mView.getDrawingCache();



		File sdCard = Environment.getExternalStorageDirectory();
		File file = new File(externalStorage.getSignaturesFolder()+File.separator+ mFileName+".jpg");
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(file);
			Boolean res= b.compress(CompressFormat.JPEG, 20, fos);

		} catch (FileNotFoundException e1) {
			Toast.makeText(this,e1.getLocalizedMessage(),Toast.LENGTH_LONG).show();
			e1.printStackTrace();
		}




	}
}
