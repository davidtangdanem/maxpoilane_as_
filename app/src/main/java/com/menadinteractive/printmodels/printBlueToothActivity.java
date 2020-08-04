package com.menadinteractive.printmodels;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

import com.menadinteractive.maxpoilane.AppBundle;
import com.menadinteractive.maxpoilane.BaseActivity;
import com.menadinteractive.maxpoilane.ExternalStorage;
import com.menadinteractive.maxpoilane.prefs_authent;
import com.menadinteractive.maxpoilane.R;
import com.menadinteractive.segafredo.communs.Fonctions;
import com.menadinteractive.segafredo.plugins.Espresso;

public class printBlueToothActivity extends BaseActivity {
	PrintModel currentPrintModel;
	Button btGoWithoutAdresse;
	Button btGoWithAdresse;
	String stringToPrint="";
	boolean m_isCoordClient;
	
	/** BlueTooth */
	private BluetoothAdapter mBluetoothAdapter =null;
	private static final int REQUEST_CONNECT_DEVICE = 325;
	
	public printBlueToothActivity()
	{
		
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bt);
        
        Bundle bundle = this.getIntent().getExtras();
      	stringToPrint =   getBundleValue(bundle,Espresso.TEXTTOPRINT); 
        initGUI();
        initListeners();
        initBT();
    }
    
    void initGUI()
    {
    	btGoWithoutAdresse=(Button) findViewById(R.id.buttonBTgo);
    	btGoWithAdresse=(Button) findViewById(R.id.buttonBTgoWithAddress);
    }
    void initListeners()
    {
    	btGoWithoutAdresse.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				printBTTicket(false);
			}
		});
    	
    	btGoWithAdresse.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				printBTTicket(true);
			}
		});
    }
    
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		changeIcon();
	}
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		StopBT();
	}
	public void setHeaderIcon(int resourceId){
		try{
			
			ImageView image_header = (ImageView) findViewById(R.id.image_header);
			if(resourceId == -1)
				image_header.setVisibility(View.GONE);
			else{
				image_header.setImageResource(resourceId);
				image_header.invalidate();
				image_header.setVisibility(View.VISIBLE);
			}
			
		}
		catch(Exception e){
			Log.d("TAG", e.toString());
		}
	}
    @Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		changeIcon();
		/*
		if (mTicketWriterManager != null) {
	    	// Only if the state is STATE_NONE, do we know that we haven't started already
	    	if (mTicketWriterManager.getState() == mTicketWriterManager.STATE_NONE) {
	    		// Start the Bluetooth chat services
	    		mTicketWriterManager.start();
	    	}
	    }
	    */
	}

    void changeIcon()
    {
		if(getConnectionState() == ImpressionBTService.STATE_CONNECTED){
			setHeaderIcon(R.drawable.printer_green);
		}
		else if(getConnectionState() == ImpressionBTService.STATE_NONE){
			setHeaderIcon(R.drawable.printer_red);
		}
		else if(getConnectionState() == ImpressionBTService.STATE_CONNECTING){
			setHeaderIcon(R.drawable.printer_orange);
		}
    }
    // The Handler that gets information back from the BluetoothService
    private final Handler mHandlerBT = new Handler() {
    	
        @Override
        public void handleMessage(Message msg) {        	
            switch (msg.what) {
            
            case ImpressionBTService.MESSAGE_STATE_CHANGE:
                Log.i(ImpressionBTService.TAG, "ACTIVITY MESSAGE_STATE_CHANGE: " + msg.arg1);
                
                changeIcon();
                break;
            }
            
        }
    };   
	private void launchDeviceSelection(){
/*		
		try {
			if (!mBluetoothAdapter.isEnabled()) {

		    }else{ 
		        mBluetoothAdapter.disable(); 
		    } 
			
			
			Thread.sleep(2000);
			
			mBluetoothAdapter.enable();
			mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		
		Intent scanDevices = new Intent(this, ScanBTDevicesActivity.class);
		startActivityForResult(scanDevices, REQUEST_CONNECT_DEVICE);
	}
    public int getConnectionState() {
		//return mTicketWriterManager.getState();
		return ImpressionBTService.getState();
	}
	
    private void initBT(){
  		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
  		
  		if (mBluetoothAdapter == null) {
  			
  		}
  		else{
  			if (mBluetoothAdapter.isEnabled()==false)
  			{
	  			mBluetoothAdapter.disable();
	  			mBluetoothAdapter.enable();
  			}
  		}
  		
  		changeIcon();
  		//mTicketWriterManager = new TicketWriterManager(this, mHandlerBT);
  	  }
      
      void printBTTicket(boolean isCoordClient)
      {

    			  
   		    		Log.d("TAG", "current state : "+getConnectionState());
  		    		 if ( (mBluetoothAdapter != null)  && (!mBluetoothAdapter.isEnabled()) ) {
  		    				
  		                 AlertDialog.Builder builder = new AlertDialog.Builder(printBlueToothActivity.this);
  		                 builder.setMessage("Votre Bluetooth est d�sactiv�. Veuillez l' activer")
  		                     .setIcon(android.R.drawable.ic_dialog_alert)
  		                     .setTitle("Activation Bluetooth")
  		                     .setCancelable( false )
  		                     .setPositiveButton("oui", new DialogInterface.OnClickListener() {
  		                     	public void onClick(DialogInterface dialog, int id) {
  		                     		
  		                     		Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
  		                     		startActivityForResult(enableIntent, 9630);			
  		                     	}
  		                     })
  		                     .setNegativeButton("non", new DialogInterface.OnClickListener() {
  		                     	public void onClick(DialogInterface dialog, int id) {
  		                     		//finishDialogNoBluetooth();
  		                     		dialog.dismiss();
  		                     	}
  		                     });
  		                 AlertDialog alert = builder.create();
  		                 alert.show();
  		 		    }		
  		    		
  		    		
  		    		
  		    		else if (getConnectionState() == ImpressionBTService.STATE_NONE) {
  		        		// Launch the DeviceListActivity to see devices and do scan
  		    			launchDeviceSelection();
  		    			
  		        	}
  		        	else if (getConnectionState() == ImpressionBTService.STATE_CONNECTED) {
  		        			
  		        		ImpressionBTService.print(getApplicationContext(), currentPrintModel, mHandlerBT,isCoordClient);
  		  
  		        		Fonctions.FurtiveMessageBox(getApplicationContext(),"print ready");
  		        		
  		        			/*
  		            		mTicketWriterManager.stop();
  		            		mTicketWriterManager.start();
  		            		launchDeviceSelection();
  		            		*/
  		            }
  		        	else if (getConnectionState() == ImpressionBTService.STATE_CONNECTING) {
  		        		// Launch the DeviceListActivity to see devices and do scan
  		    			//launchDeviceSelection();
  		        		Fonctions.FurtiveMessageBox(getApplicationContext(),"print conecting");
  		        	}
  		    		 changeIcon();
      }
      @Override
  	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
  		super.onActivityResult(requestCode, resultCode, data);
  		if(requestCode ==  REQUEST_CONNECT_DEVICE){
  			changeIcon();
  			// When DeviceListActivity returns with a device to connect
  			if (resultCode == Activity.RESULT_OK) {
  				// Get the device MAC address
  				String address = data.getExtras()
  						.getString(ScanBTDevicesActivity.EXTRA_DEVICE_ADDRESS);
  				
  				Log.d("TAG", "adresse : "+address);
  				// Get the BLuetoothDevice object
  				BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
  				
  			
  				// Attempt to connect to the device
  				//mTicketWriterManager.connect(device);
  				//Fonctions.FurtiveMessageBox(getApplicationContext(),"print ready");
		        	
  				 
  				//;, structAdrSociete societe,  structClient client, ArrayList<structLinCde> produits
  				/*printExchangeFile pef=new printExchangeFile();
  				if (pef.readExchangedFile(pathfile))
  				{
  					Fonctions.FurtiveMessageBox(getApplicationContext(), "PRINT");
  					currentPrintModel=new PrintModel(0,pef.getHdr(),pef.getSoc(),pef.getLines(),pef.getLinesInvent(),pef.getHdrInv());
  					currentPrintModel.isCoordClient=m_isCoordClient;
  					ImpressionBTService.triggerService(getApplicationContext(), currentPrintModel, device, mHandlerBT);
  				}
  				else
  				{
  					Fonctions.FurtiveMessageBox(getApplicationContext(), "probleme de format");
  				}
  				*/
  				currentPrintModel=new PrintModel(stringToPrint);
					 
				ImpressionBTService.triggerService(getApplicationContext(), currentPrintModel, device, mHandlerBT);
  			}
  		}


  	}
  	@Override
  	protected void onDestroy() {
  		// TODO Auto-generated method stub
  		super.onDestroy();
  	
  		StopBT();
  		/*
  		if (mTicketWriterManager != null)
  			mTicketWriterManager.stop();
  			*/
  	}
  	void StopBT()
  	{
  		if(ImpressionBTService.isLaunched)
  			ImpressionBTService.stopBT(getApplicationContext());
  		
  	}
}
