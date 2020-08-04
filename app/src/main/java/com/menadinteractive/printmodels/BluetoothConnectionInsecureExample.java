package com.menadinteractive.printmodels;

import org.apache.http.util.EncodingUtils;

import android.os.Handler;
import android.os.Looper;

import com.menadinteractive.maxpoilane.app;
import com.zebra.sdk.comm.BluetoothConnectionInsecure;
import com.zebra.sdk.comm.Connection;
import com.zebra.sdk.comm.ConnectionException;
import com.zebra.sdk.printer.PrinterStatus;
import com.zebra.sdk.printer.ZebraPrinter;
import com.zebra.sdk.printer.ZebraPrinterFactory;
import com.zebra.sdk.printer.ZebraPrinterLanguageUnknownException;
 
 
public class BluetoothConnectionInsecureExample {
	 
	Handler h;
  /*  public static void main(String[] args) {
        BluetoothConnectionInsecureExample example = new BluetoothConnectionInsecureExample();
 
        // The Bluetooth MAC address can be discovered, scanned, or typed in
        String theBtMacAddress = "00:22:58:36:4B:54";
        example.sendZplOverBluetooth(theBtMacAddress);
    }*/
 
	public BluetoothConnectionInsecureExample(Handler handle)
	{
		h=handle;
	}
    public void sendZplOverBluetooth(  final String theBtMacAddress ,final String texttoimpr,final int nbrPrint) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    // Instantiate insecure connection for given Bluetooth MAC Address.
                    Connection thePrinterConn = new BluetoothConnectionInsecure(theBtMacAddress);
 
 
                    // Verify the printer is ready to print
                    if (isPrinterReady(thePrinterConn)) {
 
 
                    // Initialize
                    Looper.prepare();
 
                    // Open the connection - physical connection is established here.
                    thePrinterConn.open();
 
                   
                    
                   // String cpclData = "! U1 SETLP 5 2 24 \r\n"+z.getFacture()+"\r\n";
                    
                    // Send the data to printer as a byte array.
                    for (int i=0;i<nbrPrint;i++)
                    	thePrinterConn.write( (texttoimpr.getBytes("ISO-8859-1")));
 
                    // Make sure the data got to the printer before closing the connection
                    Thread.sleep(1000);
 
                    // Close the insecure connection to release resources.
                    thePrinterConn.close();
 
                    Looper.myLooper().quit();
                }
            } catch (Exception e) {
                // Handle communications error here.
                e.printStackTrace();
            }
        }
    }).start();
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
    public final static int ERRORMSG_OK=0;
    public final static int ERRORMSG_PAUSED=1;
    public final static int ERRORMSG_DOOROPEN=2;
    public final static int ERRORMSG_PAPER=3;
    public final static int ERRORMSG_CANNOTPRINT=4;
    public final static int ERRORMSG_CONNECTION=5;
    public final static int ERRORMSG_SYNTAXERR=6;
    
    private boolean isPrinterReady(Connection thePrinterConn) {
        boolean isOK = false;
        try {
        	thePrinterConn.open();
            // Creates a ZebraPrinter object to use Zebra specific functionality like getCurrentStatus()
            ZebraPrinter printer = ZebraPrinterFactory.getInstance(thePrinterConn);
 
            PrinterStatus printerStatus = printer.getCurrentStatus();
            if (printerStatus.isReadyToPrint) {
            	if (h!=null)
            		h.sendEmptyMessage(ERRORMSG_OK);
                isOK = true;
            } else if (printerStatus.isPaused) {
            	if (h!=null)
            		h.sendEmptyMessage(ERRORMSG_PAUSED);
                System.out.println("Cannot Print because the printer is paused.");
            } else if (printerStatus.isHeadOpen) {
            	if (h!=null)
            		h.sendEmptyMessage(ERRORMSG_DOOROPEN);
                System.out.println("Cannot Print because the printer media door is open.");
            } else if (printerStatus.isPaperOut) {
            	if (h!=null)
            		h.sendEmptyMessage(ERRORMSG_PAPER);
                System.out.println("Cannot Print because the paper is out.");
            } else {
            	if (h!=null)
            		h.sendEmptyMessage(ERRORMSG_CANNOTPRINT);
                System.out.println("Cannot Print.");
            }
        } catch (ConnectionException e) {
        	if (h!=null)
        		h.sendEmptyMessage(ERRORMSG_CONNECTION);
            e.printStackTrace();
        } catch (ZebraPrinterLanguageUnknownException e) {
        	if (h!=null)
        		h.sendEmptyMessage(ERRORMSG_SYNTAXERR);
            e.printStackTrace();
        }
        return isOK;
    }
    
    static public String getErrMsg(int m)
    {
    	switch(m)
    	{
    	case ERRORMSG_PAUSED:
    		return "Imprimante en pause";
    	case ERRORMSG_CANNOTPRINT:
    		return "Impossible d'imprimer";
    	case ERRORMSG_CONNECTION:
    		return "Problème de connexion avec l'imprimante";
    	case ERRORMSG_OK:
    		return "";
    	case ERRORMSG_DOOROPEN:
    		return "Compartiment papier imprimante ouvert";
    	case ERRORMSG_PAPER:
    		return "Problème de papier";
    	case ERRORMSG_SYNTAXERR:
    		return "Problème de syntax dans l'impression";
    	}
    
    	return "Erreur non identifiée";
    }
}