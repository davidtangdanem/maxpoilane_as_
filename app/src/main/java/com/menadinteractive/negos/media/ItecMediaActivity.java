package com.menadinteractive.negos.media;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TabHost;

import com.menadinteractive.maxpoilane.BaseActivity;
import com.menadinteractive.maxpoilane.ExternalStorage;
import com.menadinteractive.maxpoilane.R;
import com.menadinteractive.segafredo.communs.Global;
import com.menadinteractive.segafredo.db.Preferences;
import com.menadinteractive.segafredo.plugins.Espresso;

public class ItecMediaActivity extends BaseActivity implements OnItemClickListener, OnClickListener{
	/** GUI */
	GridView gridview;

	WebView webView;
	/** Models */

	String  numOperation;
	String pattern;
	String mediaType;

	File[] files;
	ArrayList<File> models;
	
	String m_codecli;
	String m_soccode;
	
	/** Adapter */
	MediaAdapter adapter;
	
	public static int INTENT_SIGNATURE_REQUEST_CODE = 3000;
	public static int INTENT_MEDIA_REQUEST_CODE = 3001;
	public static int INTENT_PICTURE_REQUEST_CODE = 3002;
	public static int INTENT_RECORD_AUDIO_REQUEST_CODE = 3003;
	public static int INTENT_RAPPORT_DAHSBOARD = 3004;
	public static int INTENT_RAPPORT_QUESTION = 3005;
	
	String fileName="";
	TabHost mTabHost;

	/** Itec Extras */
	public static String EXTRA_ITEC_NUM_OPERATION = "numOperation";
	public static String EXTRA_ITEC_LIN_OPERATION = "linOperation";
	public static String EXTRA_ITEC_PATTERN 	  = "pattern";
	public static String EXTRA_ITEC_MEDIA_TYPE 	  = "mediaType";
	public static String EXTRA_ITEC_ID_ENQUETE 	  = "idEnq";
	public static String EXTRA_ITEC_ID_QUESTION = "idQuest";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.itec_layout_media);
	
		/** Extract Intent*/
		extractBundle();
		
		/** GUI */
		initGUI();
		
		
		
		/** Models */
		initModels();
		
		/** Adapters */
		initAdapters();
		
		/** Listeners */
		initListeners();
		
		fillGUI();
		
	}

	private void refreshAll(){
		initAdapterModels();
		initAdapters();
		initListeners();
		fillGUI();
	}
	public boolean onCreateOptionsMenu(Menu menu) {

		addMenu(menu, R.string.media_add, android.R.drawable.ic_input_add);
		
		
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.string.media_add:
			launch();
		return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	private void initGUI(){
		gridview = (GridView) findViewById(R.id.gridview);	
		webView=(WebView) findViewById(R.id.webView1);
		
	//	setHeaderTitle("MEDIA");
		loadWebView();
	}
	
	private void extractBundle(){
		/** extract bundle */
		Bundle bundle = getIntent().getExtras();
		if(bundle != null){

			numOperation = bundle.getString(EXTRA_ITEC_NUM_OPERATION);
			pattern = bundle.getString(EXTRA_ITEC_PATTERN);
			mediaType = bundle.getString(EXTRA_ITEC_MEDIA_TYPE);
			m_codecli = bundle.getString("codecli");
			m_soccode = bundle.getString("codesoc");
		}	
	}
	
	void loadWebView()
	{
		String url="http://sd3.danem.fr/negoscloud/packages/com.danem.gallery/remotegallerytest.aspx?p=~/docs_customers/ferrero/";
		webView.postInvalidate();
		if (url.contains(".aspx"))
		{
			url+="&c="+m_codecli;
		}
		else
			url=url+ExternalStorage.MEDIA_FOLDER+m_codecli;
		
//		url="http://88.190.55.54/negoscloud/packages/com.danem.gallery/remotegallery.aspx?p=~/docs_customers/gratien&c=4973";
//		url="http://88.190.55.54/negoscloud/packages/com.danem.gallery/remotegallery.aspx";
//		url="http://192.168.2.140/negosweb/packages/com.danem.gallery/remotegallerytest.aspx";
		//gestion de l'erreur
		webView.setWebViewClient(new WebViewClient() {
		    @Override
		    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
		           
		            view.setVisibility(View.INVISIBLE);
		            super.onReceivedError(view, errorCode, description, failingUrl);
		    }
		 });
		
		webView.loadUrl(url); 
	
	
	}
	private void initModels(){

		
		initAdapterModels();

	}
	
	/*
	 * on efface les photos non valide 
	 * 
	 */
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
	
	}
	
	private void initAdapterModels(){
	
			files = getAllFiles(pattern,ExternalStorage.MEDIA_FOLDER);
		
			
		models = new ArrayList<File>(Arrays.asList(files));
	}
	
	private void initAdapters(){
		adapter = new MediaAdapter(this, R.layout.itec_item_media, models);
		
		gridview.setAdapter(adapter);
	}
	
	private void initListeners(){
		gridview.setOnItemClickListener(this);
		
	}
	
	private void fillGUI(){
		adapter.notifyDataSetChanged();
	}
	
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		File item = (File)parent.getAdapter().getItem(position);
		promptAction(item);
		
	}
	

	private void promptAction(final File f){
		final CharSequence[] items = {"supprimer", "voir"};

		AlertDialog.Builder  builder = new AlertDialog.Builder(this);
		builder.setTitle("Action : "+f.getAbsolutePath());
		builder.setItems(items, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				if(items[item].equals("supprimer")){
					f.delete();
					refreshAll();
				}
				else if(items[item].equals("voir")){
					if(mediaType != null ){
						if(mediaType.equals(ItecMediaFile.PREFIX_AUDIO)){
							viewAudio(f);
						}
						else{
							viewImage(f);
						}
							
					}
				}

			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}
	 public File getMoreRecentFile(ArrayList<File> files){
		 //		 Global.getDD_MM_YYYY_MM_SS(model.lastModified())
		 if(files == null)
			 return null;
		 try{
			 File f = files.get(0);
			 for(int i = 0; i<files.size(); i++){
				 if(files.get(i).lastModified() > f.lastModified())
				 {
					 f = files.get(i);
					
				 }
			 }

			 return f;
		 }
		 catch(Exception e){
			 return null;
		 }
	 }
	 
	 void launch()
	 {
		 if(mediaType != null){
				if(mediaType.equals(ItecMediaFile.PREFIX_CROQUIS)){
					String stId = ItecMediaFile.generateStId(numOperation, pattern, mediaType);
					int index = ItecMediaFile.getIndex(getMoreRecentFile(models));
				 fileName = ItecMediaFile.generateFileName(stId, ItecMediaFile.PREFIX_CROQUIS, pad(index+1, 4),"");
					
					
					Log.d("requete", "index = "+index);
			//		launchSignature(fileName);
				}
				else if(mediaType.equals(ItecMediaFile.PREFIX_PREOPE)){
					String stId = ItecMediaFile.generateStId(numOperation, pattern, mediaType);
					int index = ItecMediaFile.getIndex(getMoreRecentFile(models));
					//String fileName = ItecMediaFile.generateFileName(stId, ItecMediaFile.PREFIX_PREOPE, pad(index+1, 4), ItecMediaFile.EXT_JPG);
					 fileName = ItecMediaFile.generateFileName2(
							numOperation,
							pattern,
							Preferences.getValue( this, Espresso.LOGIN, "0"),
							m_codecli,
							m_soccode,
							pad(index+1, 4),
							ItecMediaFile.EXT_JPG
							
							);
					launchCamera(fileName);
				}
				else if(mediaType.equals(ItecMediaFile.PREFIX_POSTOPE)){
					String stId = ItecMediaFile.generateStId(numOperation, pattern, mediaType);
					int index = ItecMediaFile.getIndex(getMoreRecentFile(models));
				//	String fileName = ItecMediaFile.generateFileName(stId, ItecMediaFile.PREFIX_POSTOPE, pad(index+1, 4), ItecMediaFile.EXT_JPG);
					 fileName = ItecMediaFile.generateFileName2(
							numOperation,
							pattern,
							Preferences.getValue( this, Espresso.LOGIN, "0"),
							m_codecli,
							m_soccode,
							pad(index+1, 4),
							ItecMediaFile.EXT_JPG
							
							);
					launchCamera(fileName);
				}
				else if(mediaType.equals(ItecMediaFile.PREFIX_PHOTOCLIENT)){
					String stId = ItecMediaFile.generateStId(numOperation, pattern, mediaType);
					int index = ItecMediaFile.getIndex(getMoreRecentFile(models));
				//	String fileName = ItecMediaFile.generateFileName(stId, Fonctions.getYYYYMMDDhhmmss(), pad(index+1, 4), ItecMediaFile.EXT_JPG);
					
					 fileName = ItecMediaFile.generateFileName2(
							numOperation,
							pattern,
							Preferences.getValue( this, Espresso.LOGIN, "0"),
							m_codecli,
							m_soccode,
							pad(index+1, 4),
							ItecMediaFile.EXT_JPG
							
							);
					launchCamera(fileName);
				}
				else if(mediaType.equals(ItecMediaFile.PREFIX_PHOTOPRODUIT)){
					int index = ItecMediaFile.getIndex(getMoreRecentFile(models));
					 fileName = ItecMediaFile.generateFileName2(
							numOperation,
							pattern,
							Preferences.getValue( this, Espresso.LOGIN, "0"),
							m_codecli,
							m_soccode,
							pad(index+1, 4),
							ItecMediaFile.EXT_JPG
							
							);
					launchCamera(fileName);
				}
				
				else if(mediaType.equals(ItecMediaFile.PREFIX_AUDIO)){
					launchAudioRecord("");
				}
			}
	 }

	
/*	
	 protected void launchSignature(String fileName){
		 Intent i = new Intent( this, SignatureActivity.class);
			Bundle bundle = new Bundle();
			bundle.putString("filename",fileName);
			i.putExtras(bundle);
			startActivityForResult(i, INTENT_SIGNATURE_REQUEST_CODE);
	 }

	 */
	
	 
	 protected void launchAudioRecord(String path){
		 Intent intent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION); 
		 startActivityForResult(intent, INTENT_RECORD_AUDIO_REQUEST_CODE);
	 }
	
	 
	
	 protected void launchCamera(String path){
			try{
				File sdCard = Environment.getExternalStorageDirectory();
				File imageFile = new File(sdCard,ExternalStorage.MEDIA_FOLDER+path);
				Uri imageFileUri = Uri.fromFile(imageFile);
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				intent.putExtra(MediaStore.EXTRA_OUTPUT, imageFileUri); 
				intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0); 
				intent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, 800*600); 
				startActivityForResult(intent, INTENT_PICTURE_REQUEST_CODE);
			}
			catch(Exception e){
				Log.e("TAG",e.toString());
			}
		}
	
	 protected void viewImage(File f){
		

		 Intent intent = new Intent();  
		 intent.setAction(android.content.Intent.ACTION_VIEW);  

		 //intent.setData(Uri.fromFile(file));
		 intent.setDataAndType(Uri.fromFile(f), "image/*");  
		 startActivity(intent);  

	 }


	 protected void viewAudio(File f){
		 Intent intent = new Intent();  
		 intent.setAction(android.content.Intent.ACTION_VIEW);  

		 //intent.setData(Uri.fromFile(file));
		 intent.setDataAndType(Uri.fromFile(f), "audio/*");  
		 startActivity(intent);  
	 }
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == 0) {
//			kemsHdr = getDatabase().getKemsDataTable().getDbDK10HdrOpe().getHdrOpe(this, hdr.NUM_OPERATION);
		}

		else if (requestCode == INTENT_SIGNATURE_REQUEST_CODE) {
			if (resultCode == RESULT_OK) {
				refreshAll();
			}
			else{
				Log.d("TAG", "CROQUIS NOK");
			}
		}
		else if(requestCode == INTENT_PICTURE_REQUEST_CODE){
			try {
				Log.d("TAG", "picure");
				File sdCard = Environment.getExternalStorageDirectory();
				File photo=new File(sdCard,ExternalStorage.MEDIA_FOLDER+ fileName);
				String filenameSrc=sdCard+ExternalStorage.MEDIA_FOLDER+ fileName;
				//MV on reduit la taille de limage
				if (photo.exists())
				{
					
					Bitmap image2=BitmapFactory.decodeFile(filenameSrc);
					
					int newWidth=640;
					int oldWidth=image2.getWidth();
					float ratio=(float)newWidth/oldWidth;
					int newHeight=(int)(image2.getHeight()*ratio);
							
					Bitmap resized = Bitmap.createScaledBitmap(image2, newWidth, newHeight, true);
					photo.createNewFile();
					FileOutputStream ostream = new FileOutputStream(photo);
					resized.compress(CompressFormat.JPEG, 100, ostream);
					ostream.close(); 
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			refreshAll();
		}
/*		else if(requestCode == INTENT_PICTURE_REQUESTIDENT_CODE){
			Log.d("TAG", "picure");
			File sdCard = Environment.getExternalStorageDirectory();
			
			File imageFile = new File(sdCard,ExternalStorage.DANEM_MEDIA_IDENT_FOLDER);
			File imageFile2 = new File(sdCard,ExternalStorage.DANEM_MEDIA_FOLDER);
			
			MyDB.copyFile(imageFile, imageFile2);
		
			refreshAll();
		}
		*/
		else if(requestCode == INTENT_RECORD_AUDIO_REQUEST_CODE){
			try{
				Uri savedUri = data.getData();
				
				File ff = new File(savedUri.toString());
				
				
				String stId = ItecMediaFile.generateStId(numOperation, pattern, mediaType);
				String pattern = ItecMediaFile.generateRegex(stId, ItecMediaFile.PREFIX_AUDIO);
				File[] files = getAllFiles(pattern,ExternalStorage.MEDIA_FOLDER);
				int index = ItecMediaFile.getIndex(getMoreRecentFile(new ArrayList<File>(Arrays.asList(files))));
				String fileName = ItecMediaFile.generateFileName(stId, ItecMediaFile.PREFIX_AUDIO, pad(index+1, 4),ItecMediaFile.EXT_AUDIO);
				
				File dest = new File(Environment.getExternalStorageDirectory(), ExternalStorage.MEDIA_FOLDER+fileName);
				copyFile(savedUri, dest);
				
				
				}
				catch(Exception e){
					
				}
			refreshAll();
		}

		

	}
	
	 protected void copyFile(Uri inUri, File out){
			InputStream inStream = null;
			OutputStream outStream = null;
		 
		    	try{
		 
		 
		 
		    	    inStream = getContentResolver().openInputStream(inUri);
		    	    outStream = new FileOutputStream(out);
		 
		    	    byte[] buffer = new byte[1024];
		 
		    	    int length;
		    	    //copy the file content in bytes 
		    	    while ((length = inStream.read(buffer)) > 0){
		 
		    	    	outStream.write(buffer, 0, length);
		 
		    	    }
		 
		    	    inStream.close();
		    	    outStream.close();
		 
		 
		    	    Log.d("TAG","File is copied successful!");
		 
		    	}catch(IOException e){
		    	    e.printStackTrace();
		    	    Log.e("TAG", e.toString());
		    	}
		}
	 


	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK
				|| keyCode == KeyEvent.KEYCODE_HOME) {
			 
			deleteBadFiles();
			   finish();
			   
			return false;
		}

		else
			return super.onKeyDown(keyCode, event);

	}
	public static String pad(int c) {
		 if (c >= 10)
			 return String.valueOf(c);
		 else
			 return "0" + String.valueOf(c);
	 }
	 private  static File createDirectory(String dir){
		 String PATH = Environment.getExternalStorageDirectory() +"/"+dir;
		 File f = new File(PATH);
		 f.mkdirs(); 

		 return f;

	 }
	 public File[] getAllFiles(final String pattern,String dir){
		 return createDirectory(dir).listFiles(new FilenameFilter() {
			    @Override
			    public boolean accept(File dir, String name) {
			        return name.contains(pattern);
			    }
			});
	 }
	void deleteBadFiles()
	{
		File[] mfiles = getAllFiles(pattern,ExternalStorage.MEDIA_FOLDER);
		for (int i=0;i<mfiles.length;i++)
		{
			File f=new File(mfiles[i].getAbsolutePath());
			 long size=f.length();
			 if (size>Global.maxMediaFileSize)
			 {
				 f.delete();
			 }
		}
	}
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		
	}
	 protected static String pad(int c, int nbDigits) {
		 String result = "";
		 try{
		 result = String.valueOf(c);
		 if(result.length() < nbDigits){
			 int delta = nbDigits - result.length();
			 for (int i =0; i<delta; i++){
				 result = "0"+result;
			 }
		 }
		 }catch(Exception e){
			 return "";
		 }
		 
		 return result; 
		 
	 }
}
