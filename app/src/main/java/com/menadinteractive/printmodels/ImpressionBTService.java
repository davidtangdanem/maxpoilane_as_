package com.menadinteractive.printmodels;



import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

public class ImpressionBTService extends Service{
	public static String TAG = "ImpressionBTService";

	private static final UUID SerialPortServiceClass_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

	// Constants that indicate the current connection state
	public static final int STATE_NONE = 0;       // we're doing nothing
	public static final int STATE_LISTEN = 1;     // now listening for incoming connections
	public static final int STATE_CONNECTING = 2; // now initiating an outgoing connection
	public static final int STATE_CONNECTED = 3;  // now connected to a remote device

	// Message types sent 
	public static final int MESSAGE_STATE_CHANGE = 200;
	public static final int MESSAGE_READ = 201;
	public static final int MESSAGE_WRITE = 202;
	public static final int MESSAGE_DEVICE_NAME = 203;
	public static final int MESSAGE_TOAST = 204;	
	public static final int MESSAGE_TRIGGER_IMPRESSIONBTSERVICE=205;

	/** Member fields */
	private static BluetoothAdapter mAdapter;
	private static BluetoothDevice btDevice;
	private ConnectThread mConnectThread;
	private ConnectedThread mConnectedThread;
	private static int mState = STATE_NONE;
	public static boolean isLaunched = false;
	static Handler mHandler = null;
	static PrintModel currentPrintModel = null;
	public static String adressMac;

 
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	public static void stopBT(Context context){
		Intent intentService = new Intent(context, ImpressionBTService.class);	
		intentService.putExtra("stop", true);
		context.startService(intentService);
		
	}
	public static void triggerService(Context context, PrintModel printModel,BluetoothDevice device, Handler handler){
		Log.d(TAG, "trigger Service");
		//if(!isLaunched){
			Intent intentService = new Intent(context, ImpressionBTService.class);		
			context.startService(intentService);
		//}
 
		currentPrintModel = printModel;
		mHandler = handler;
		mAdapter = BluetoothAdapter.getDefaultAdapter();
		btDevice = device;

	}
	
	public static void print(Context context, PrintModel printModel, Handler handler,boolean isCoordClient){
 
		mHandler = handler;
		currentPrintModel = printModel;
		Intent intentService = new Intent(context, ImpressionBTService.class);		
		context.startService(intentService);
		//printSmth();
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(TAG, "onCreate");

		//handler = getHandler();

		isLaunched = true;
		start();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "onDestroy");
		isLaunched = false;
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		Log.d(TAG, "onStart");
		Bundle extras = intent.getExtras();
		if(extras != null){
		boolean stop = extras.getBoolean("stop", false);
		if(stop)
			start();
			this.stopSelf();
		}
		else if(mHandler != null){
			mHandler.sendEmptyMessage(MESSAGE_TRIGGER_IMPRESSIONBTSERVICE);
		
		
		
		
		if(getState() == STATE_NONE)
			connect(btDevice);
		else if(getState() == STATE_CONNECTED)
			printSmth( );
		}
	}

	private synchronized void setState(int state) {
		Log.d(TAG, "setState() " + mState + " -> " + state);
		mState = state;

		// Give the new state to the Handler so the UI Activity can update
		if(mHandler != null)
			mHandler.obtainMessage(MESSAGE_STATE_CHANGE, state, -1).sendToTarget();
	}

	public synchronized static int getState() {
		return mState;
	}

	/**
	 * Start the chat service. Specifically start AcceptThread to begin a
	 * session in listening (server) mode. Called by the Activity onResume() */
	public synchronized void start() {
		Log.d(TAG, "start");

		// Cancel any thread attempting to make a connection
		if (mConnectThread != null) {
			mConnectThread.cancel(); 
			mConnectThread = null;
		}

		// Cancel any thread currently running a connection
		if (mConnectedThread != null) {
			mConnectedThread.cancel(); 
			mConnectedThread = null;
		}

		setState(STATE_NONE);
	}


	/**
     * Write to the ConnectedThread in an unsynchronized manner
     * @param out The bytes to write
     * @see ConnectedThread#write(byte[])
     */
    public void write(byte[] out) {
        // Create temporary object
        ConnectedThread r;
        // Synchronize a copy of the ConnectedThread
        synchronized (this) {
            if (mState != STATE_CONNECTED) return;
            r = mConnectedThread;
        }
        // Perform the write unsynchronized
        r.write(out);
    }
	
	/**
	 * Start the ConnectThread to initiate a connection to a remote device.
	 * @param device  The BluetoothDevice to connect
	 */
	public synchronized void connect(BluetoothDevice device) {
		Log.d(TAG, "connect to: " + device);

		// Cancel any thread attempting to make a connection
		if (mState == STATE_CONNECTING) {
			if (mConnectThread != null) {mConnectThread.cancel(); mConnectThread = null;}
		}

		// Cancel any thread currently running a connection
		if (mConnectedThread != null) {mConnectedThread.cancel(); mConnectedThread = null;}

		// Start the thread to connect with the given device
		mConnectThread = new ConnectThread(device);
		mConnectThread.start();
		setState(STATE_CONNECTING);
	}

	/**
	 * Start the ConnectedThread to begin managing a Bluetooth connection
	 * @param socket  The BluetoothSocket on which the connection was made
	 * @param device  The BluetoothDevice that has been connected
	 */
	public synchronized void connected(BluetoothSocket socket, BluetoothDevice device) {
		Log.d(TAG, "connected");

		// Cancel the thread that completed the connection
		if (mConnectThread != null) {
			mConnectThread.cancel(); 
			mConnectThread = null;
		}

		// Cancel any thread currently running a connection
		if (mConnectedThread != null) {
			mConnectedThread.cancel(); 
			mConnectedThread = null;
		}

		// Start the thread to manage the connection and perform transmissions
		mConnectedThread = new ConnectedThread(socket);
		mConnectedThread.start();

		// Send the name of the connected device back to the UI Activity

		Message msg = mHandler.obtainMessage(MESSAGE_DEVICE_NAME);
		Bundle bundle = new Bundle();
		//bundle.putString(DEVICE_NAME, device.getName());
		msg.setData(bundle);
		if(mHandler != null)
		mHandler.sendMessage(msg);

		setState(STATE_CONNECTED);
		printSmth( );
		
		
	}
	
	private void printSmth( ){
		
		/*
		byte msgs[]= new byte[200];
        String s = "test message DANEM \r";
        s+="new lines !\r\r\r";
        msgs = s.getBytes();
        write(msgs);
        */
		
		write( convertExtendedAscii(currentPrintModel.get()) );
	}
	public byte[] convertExtendedAscii(String input)
	{
	        int length = input.length();
	        byte[] retVal = new byte[length];
	       
	        for(int i=0; i<length; i++)
	        {
	                  char c = input.charAt(i);
	                 
	                  if (c < 127)
	                  {
	                          retVal[i] = (byte)c;
	                  }
	                  else
	                  {
	                          retVal[i] = (byte)(c - 256);
	                  }
	        }
	       
	        return retVal;
	}
	/**
	 * Indicate that the connection attempt failed and notify the UI Activity.
	 */
	private void connectionFailed() {
		setState(STATE_NONE);

		// Send a failure message back to the Activity

		Message msg = mHandler.obtainMessage(MESSAGE_TOAST);
		Bundle bundle = new Bundle();
		//bundle.putString(TOAST, "Unable to connect device");
		msg.setData(bundle);
		if(mHandler != null)
		mHandler.sendMessage(msg);
	}

	/**
	 * Indicate that the connection was lost and notify the UI Activity.
	 */
	private void connectionLost() {
		setState(STATE_NONE);

		// Send a failure message back to the Activity

		Message msg = mHandler.obtainMessage(MESSAGE_TOAST);
		Bundle bundle = new Bundle();
		//bundle.putString(TOAST, "Device connection was lost");
		msg.setData(bundle);
		if(mHandler != null)
		mHandler.sendMessage(msg);
	}


	/**
	 * This thread runs while attempting to make an outgoing connection
	 * with a device. It runs straight through; the connection either
	 * succeeds or fails.
	 */
	private class ConnectThread extends Thread {
		private BluetoothSocket mmSocket;
		private final BluetoothDevice mmDevice;

		public ConnectThread(BluetoothDevice device) {
			mmDevice = device;
			BluetoothSocket tmp = null;

			// Get a BluetoothSocket for a connection with the
			// given BluetoothDevice
			try {
				tmp = device.createRfcommSocketToServiceRecord(SerialPortServiceClass_UUID);
			} catch (IOException e) {
				Log.e(TAG, "create() failed", e);
			}
			mmSocket = tmp;
		}

		public void run() {
			Log.i(TAG, "BEGIN mConnectThread");
			setName("ConnectThread");

			// Always cancel discovery because it will slow down a connection
			//mAdapter.cancelDiscovery();
			Method m;
				try {
				    m = btDevice.getClass().getMethod("createInsecureRfcommSocket", new Class[] { int.class });
				    mmSocket= (BluetoothSocket) m.invoke(btDevice, 1);
				} catch (SecurityException e1) {
				    e1.printStackTrace();
				} catch (NoSuchMethodException e1) {
				    e1.printStackTrace();
				} catch (IllegalArgumentException e) {
				    e.printStackTrace();
				} catch (IllegalAccessException e) {
				    e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
				
			// Make a connection to the BluetoothSocket
			try {
				// This is a blocking call and will only return on a
				// successful connection or an exception
				mmSocket.connect();
			} catch (IOException e) {
				// Start the connected thread
				
				connectionFailed();
				// Close the socket
				
				try {
					mmSocket.close();
				} catch (IOException e2) {
					Log.e(TAG, "unable to close() socket during connection failure", e2);
				}
				// Start the service over to restart listening mode
				//BluetoothSerialService.this.start();
				return;
				
			}

			// Reset the ConnectThread because we're done
			synchronized (ImpressionBTService.this) {
				mConnectThread = null;
			}

			// Start the connected thread
			connected(mmSocket, mmDevice);
		}

		public void cancel() {
			try {
				mmSocket.close();
			} catch (IOException e) {
				Log.e(TAG, "close() of connect socket failed", e);
			}
		}
	}

	/**
	 * This thread runs during a connection with a remote device.
	 * It handles all incoming and outgoing transmissions.
	 */
	private class ConnectedThread extends Thread {
		private final BluetoothSocket mmSocket;
		private final InputStream mmInStream;
		private final OutputStream mmOutStream;


		public ConnectedThread(BluetoothSocket socket) {
			Log.d(TAG, "create ConnectedThread");
			mmSocket = socket;
			InputStream tmpIn = null;
			OutputStream tmpOut = null;

			// Get the BluetoothSocket input and output streams
			try {
				tmpIn = socket.getInputStream();
				tmpOut = socket.getOutputStream();
			} catch (IOException e) {
				Log.e(TAG, "temp sockets not created", e);
			}

			mmInStream = tmpIn;
			mmOutStream = tmpOut;
		}

		public void run() {
			Log.i(TAG, "BEGIN mConnectedThread");
			byte[] buffer = new byte[1024];
			int bytes;

			// Keep listening to the InputStream while connected
			while (true) {
				try {
					// Read from the InputStream
					bytes = mmInStream.read(buffer);

					//FIXME : uncomment
					//mEmulatorView.write(buffer, bytes);




					// Send the obtained bytes to the UI Activity
					mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer).sendToTarget();

					String a = buffer.toString();
					a = "";
				} catch (IOException e) {
					Log.e(TAG, "disconnected", e);
					connectionLost();
					break;
				}
			}
		}

		/**
		 * Write to the connected OutStream.
		 * @param buffer  The bytes to write
		 */
		public void write(byte[] buffer) {
			try {
				mmOutStream.write(buffer);

				// Share the sent message back to the UI Activity
				//FIXME : uncomment
				//                mHandler.obtainMessage(BlueTerm.MESSAGE_WRITE, buffer.length, -1, buffer)
				//                        .sendToTarget();
			} catch (IOException e) {
				Log.e(TAG, "Exception during write", e);
			}
		}

		public void cancel() {
			try {
				mmSocket.close();
			} catch (IOException e) {
				Log.e(TAG, "close() of connect socket failed", e);
			}
		}
	}




	/*
	Handler getHandler(){
		Handler h = new Handler(){

			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);

				switch(msg.what) {
				case MESSAGE_TRIGGER_IMPRESSIONBTSERVICE:

				break;
				}
			}

		};
		return h;
	}
	 */

}
