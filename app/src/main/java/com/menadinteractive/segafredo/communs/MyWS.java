package com.menadinteractive.segafredo.communs;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.CharBuffer;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;

import org.kobjects.base64.Base64;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.MarshalBase64;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.ksoap2.transport.HttpsTransportSE;
import org.kxml2.kdom.Element;
import org.kxml2.kdom.Node;

import com.menadinteractive.maxpoilane.Debug;
import com.menadinteractive.maxpoilane.ExternalStorage;
import com.menadinteractive.maxpoilane.app;
import com.menadinteractive.maxpoilane.R;
import com.menadinteractive.segafredo.db.Preferences;
import com.menadinteractive.segafredo.plugins.Espresso;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

public class MyWS {

	private static final String SOAP_ACTION = "http://www.zrws1.org/Ping";
	private static final String METHOD_NAME = "Ping";
	private static final String NAMESPACE = "http://www.zrws1.org/";
	//private static final String URL = "http://84.14.126.77/zrserveurws/zrservice.asmx";
	//private static final String URL = "http://sd99.danem.fr/zrserveurws/zrservice.asmx";
	//private static final String URL = "http://109.26.245.202/zrserveurws/zrservice.asmx";
	//
	public static final String SDMAJ = "SDMAJ.danem.fr/zrserveurws/maj";
	public static  String URL = WSSendURLIP();
	//public static final String LOAD_WEB = "http://sd8.danem.fr/max_poilane/pdf/CONVERTER/pdf/";
	public static final String LOAD_WEB = "http://maxpoilane.danem.fr/max_poilane/pdf/CONVERTER/pdf/";



	static String WSSendURLIP()
	{
		Context c=app.getAppContext();
		String ip=Preferences.getValue(c  , Espresso.IP, "0");
		return "http://"+ip+"/zrserveurws/zrservice.asmx";
	}
	
	public static int TIMEOUT=30000;

	//private static final String URL =
	//"http://84.14.126.79/ZRService/zrservice.asmx";
	// private static final String URL = "http://10.0.2.2:2390/zrservice.asmx";
	// private static final String URL =
	// "http://192.168.2.109/zrserveurws/zrservice.asmx";

	// private static final String URL =
	// "http://192.168.1.17:2390/ZRservic90000e.asmx";
	private static final String SOAP_ACTION2 = "http://www.zrws1.org/Bonjour";
	private static final String METHOD_NAME2 = "Bonjour";

	private static String STATDIR =  ExternalStorage.getFolderPath(ExternalStorage.STAT_FOLDER);
	

	private static String lastErrorMsg = "";

	public static String getLastErrorMsg() {
		return lastErrorMsg;
	}

	 

	private static Element buildAuthHeader(String user, String pwd) {
		Element h = new Element().createElement(NAMESPACE, "AuthHeader");
		Element username = new Element().createElement(NAMESPACE, "user");
		username.addChild(Node.TEXT, user);
		h.addChild(Node.ELEMENT, username);
		Element pass = new Element().createElement(NAMESPACE, "pass");
		pass.addChild(Node.TEXT, pwd);
		h.addChild(Node.ELEMENT, pass);

		return h;
	}

	 

	public static Result WSQueryZippedEx(String user, String pwd,
			String fonction, String scenario, String filtre, String Critere1,
			String Critere2, String Critere3, String Critere4, boolean PutInFile) {
		Result result = new Result();
		try {

//			fonction = "ExecExInsertHdrZip";
			SoapObject request = new SoapObject(NAMESPACE, fonction);

			request.addProperty("stUser", user);
			request.addProperty("stPwd", pwd);
			request.addProperty("stScenar", scenario);
			request.addProperty("stFilter", filtre);

			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			new MarshalBase64().register(envelope);
			envelope.encodingStyle = SoapEnvelope.ENC;
			envelope.dotNet = true;
			envelope.bodyOut = request;
			envelope.setOutputSoapObject(request);

			Debug.Log("start connecting : "+URL);
			HttpTransportSE androidHttpTransport = new HttpTransportSE(URL,TIMEOUT);

			androidHttpTransport.call("http://www.zrws1.org/" + fonction,
					envelope);


			SoapPrimitive resultSoap = (SoapPrimitive) envelope.getResponse();

			// byte[] compressedBytes = result.toString().getBytes();

			if(fonction.contains("Zip"))
				result.setContent(Decompress(resultSoap.toString()));
			else
				result.setContent(resultSoap.toString());
			result.setConnSuccess(true);
			return result;

		} catch (Exception e) {
			result.setExceptionMessage(e.getLocalizedMessage());
			result.connSuccess = false;
//			Debug.StackTrace(e);
			
			return result;

		}
	}
	/*
	 * stUser,stPwd,lon,lat,date,vitesse
	 */
	public static Result WSSendGPS(String user, String pwd,
			String fonction, String scenario, String filtre, String latitude,
			String longitude, String date, String vitesseKmH, boolean PutInFile) {
		Result result = new Result();
		try {

			fonction = "SUM_SendLocationCoordEx2";
			SoapObject request = new SoapObject(NAMESPACE, fonction);

			request.addProperty("stUser", user);
			request.addProperty("stPwd", pwd);
			request.addProperty("stScenar", scenario);
			request.addProperty("stFilter", filtre);
			request.addProperty("lon", longitude);
			request.addProperty("lat", latitude);
			request.addProperty("date", date);
			request.addProperty("vitesse", vitesseKmH);

			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			new MarshalBase64().register(envelope);
			envelope.encodingStyle = SoapEnvelope.ENC;
			envelope.dotNet = true;
			envelope.bodyOut = request;
			envelope.setOutputSoapObject(request);

			Debug.Log("start connecting : "+URL);
			HttpTransportSE androidHttpTransport = new HttpTransportSE(URL,TIMEOUT);

			androidHttpTransport.call("http://www.zrws1.org/" + fonction,
					envelope);


			SoapPrimitive resultSoap = (SoapPrimitive) envelope.getResponse();

			// byte[] compressedBytes = result.toString().getBytes();

			result.setContent(Decompress(resultSoap.toString()));
			result.setConnSuccess(true);
			return result;

		} catch (Exception e) {
			result.setExceptionMessage(e.getLocalizedMessage());
			result.connSuccess = false;
			Debug.StackTrace(e);
			return result;

		}
	}

	public static String Decompress(String zipText) throws IOException {
		int size = 0;
		byte[] gzipBuff = Base64.decode(zipText);

		ByteArrayInputStream memstream = new ByteArrayInputStream(gzipBuff, 4,
				gzipBuff.length - 4);
		GZIPInputStream gzin = new GZIPInputStream(memstream);

		final int buffSize = 8192;
		byte[] tempBuffer = new byte[buffSize];
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		while ((size = gzin.read(tempBuffer, 0, buffSize)) != -1) {
			baos.write(tempBuffer, 0, size);
		}
		byte[] buffer = baos.toByteArray();
		baos.close();

		return new String(buffer, "UTF-8");
	}

	public static byte[] fromGByteToByte(byte[] gbytes) {
		ByteArrayOutputStream baos = null;
		ByteArrayInputStream bais = new ByteArrayInputStream(gbytes);
		try {
			baos = new ByteArrayOutputStream();
			GZIPInputStream gzis = new GZIPInputStream(bais);
			byte[] bytes = new byte[1024];
			int len;
			while ((len = gzis.read(bytes)) > 0) {
				baos.write(bytes, 0, len);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return (baos.toByteArray());
	}

	/**
	 * 
	 * @author Marc VOUAUX
	 * @param user
	 * @param pwd
	 * @param fonction
	 * @param scenario
	 * @param filtre
	 * @param Critere1
	 * @param Critere2
	 * @param Critere3
	 * @param Critere4
	 * @return
	 * 
	 */
	public static String WSQuery(String user, String pwd, String fonction,
			String scenario, String filtre, String Critere1, String Critere2,
			String Critere3, String Critere4, boolean PutInFile) {
		try {

			fonction = "Exec";



			SoapObject request = new SoapObject(NAMESPACE, fonction);

			request.addProperty("stUser", user);
			request.addProperty("stPwd", pwd);
			request.addProperty("stScenar", scenario);
			request.addProperty("stFilter", filtre);

			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);


			envelope.dotNet = true;
			envelope.bodyOut = request;
			envelope.setOutputSoapObject(request);

			// /
			// envelope.dotNet=true;
			// envelope.headerOut=new Element[1];
			// envelope.headerOut[0]=buildAuthHeader(user,pwd);
			//
			HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
			androidHttpTransport.debug= true;
			androidHttpTransport.call("http://www.zrws1.org/" + fonction,
					envelope);

			/*androidHttpTransport.call(SOAP_ACTION,envelope);
		    SoapObject so = (SoapObject)envelope.bodyIn;
			 */
			SoapPrimitive resultString = (SoapPrimitive) envelope.getResponse();

			String Content;
			Content = resultString.toString();
			if (Content.equals("")) {
			} else {
				if (PutInFile == true) {
					String NameFileSansDate;
					String filtreForFile=filtre.replace("_", "-");
					NameFileSansDate = scenario + "_" + filtreForFile + "_" + Critere1
							+ "_" + Critere2 + "_" + Critere3 + "_" + Critere4;
					CreateFileWS(Content, NameFileSansDate);
				}

			}

			return Content;

		} catch (Exception e) {
			lastErrorMsg = "erreur conn";
			//lastErrorMsg = e.getLocalizedMessage();

			return "erreur conn";

		}
	}

	public static Result Bonjour(String user,String pwd)
	{
		Result result = new Result();

		try {
			String fonction="Bonjour";


			SoapObject request = new SoapObject(NAMESPACE, fonction);

			request.addProperty("stUser", user);
			request.addProperty("stPwd", pwd);


			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			envelope.dotNet = true;
			envelope.bodyOut = request;
			envelope.setOutputSoapObject(request);
			// /
			// envelope.dotNet=true;
			// envelope.headerOut=new Element[1];
			// envelope.headerOut[0]=buildAuthHeader(user,pwd);
			//
			HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

			androidHttpTransport.call("http://www.zrws1.org/" + fonction,
					envelope);
			SoapPrimitive resultString = (SoapPrimitive) envelope.getResponse();

			String Content;
			Content = resultString.toString();
			if (Content.equals("") ) 
			{
				result.setDanemMessage(Content);
				result.setContent(Content);
				result.setConnSuccess(false);
				return result;
			} 

			result.setContent(Content);
			result.setConnSuccess(true);
			return result;
		} catch (ClassCastException ecc) {

			result.setExceptionMessage(ecc.getLocalizedMessage());
			result.setDanemMessage("no Data");
			result.connSuccess = false;
			return result;
		} catch (Exception e) {
			result.setExceptionMessage(e.getLocalizedMessage());
			result.connSuccess = false;
			return result;
		}
	}
	public static Result Ping()
	{
		Result result = new Result();

		try {
			String fonction="Ping";


			SoapObject request = new SoapObject(NAMESPACE, fonction);



			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			envelope.dotNet = true;
			envelope.bodyOut = request;
			envelope.setOutputSoapObject(request);
			// /
			// envelope.dotNet=true;
			// envelope.headerOut=new Element[1];
			// envelope.headerOut[0]=buildAuthHeader(user,pwd);
			//
			HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

			androidHttpTransport.call("http://www.zrws1.org/" + fonction,
					envelope);
			SoapPrimitive resultString = (SoapPrimitive) envelope.getResponse();

			String Content;
			Content = resultString.toString();
			if (Content.equals("") ) 
			{
				result.setDanemMessage(Content);
				result.setContent(Content);
				result.setConnSuccess(false);
				return result;
			} 

			result.setContent(Content);
			result.setConnSuccess(true);
			return result;

		} catch (ClassCastException ecc) {

			result.setExceptionMessage(ecc.getLocalizedMessage());
			result.setDanemMessage("no Data");
			result.connSuccess = false;
			return result;

		} catch (Exception e) {
			result.setExceptionMessage(e.getLocalizedMessage());
			result.connSuccess = false;
			return result;

		}
	}
	
	
	
	public static Result WSQueryExpresso(String user, String pwd, String fonction,String datevis, String soc_code, String zone, boolean PutInFile) {

		Result result = new Result();

		try {



			SoapObject request = new SoapObject(NAMESPACE, fonction);

			request.addProperty("stUser", user);
			request.addProperty("stPwd", pwd);
			request.addProperty("datevis", datevis);
			request.addProperty("soc_code", soc_code);
			request.addProperty("zone", zone);

			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			envelope.dotNet = true;
			envelope.bodyOut = request;
			envelope.setOutputSoapObject(request);
			// /
			// envelope.dotNet=true;
			// envelope.headerOut=new Element[1];
			// envelope.headerOut[0]=buildAuthHeader(user,pwd);
			//
			HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

			androidHttpTransport.call("http://www.zrws1.org/" + fonction,
					envelope);
			SoapPrimitive resultString = (SoapPrimitive) envelope.getResponse();

			String Content;
			Content = resultString.toString();
			if (Content.equals("") || MyWS.ResultatReadFileWS(Content)==false) 
			{
				result.setDanemMessage(Content);
				result.setContent(Content);
				result.setConnSuccess(false);
				return result;
			} 
			else 
			{
			}

			result.setContent(Content);
			result.setConnSuccess(true);
			return result;

		} catch (ClassCastException ecc) {
			//on crée un fichier vide pour réponse rapide en cas de no data sur le serveur
			/*			String NameFileSansDateA;
			filtre=filtre.replace("_","-");
			NameFileSansDateA = scenario + "_" + filtre + "_" + Critere1
					+ "_" + Critere2 + "_" + Critere3 + "_" + Critere4;
			CreateFileWS("0;0", NameFileSansDateA);
			 */
			result.setExceptionMessage(ecc.getLocalizedMessage());
			result.setDanemMessage("no Data");
			result.connSuccess = false;

			return result;

		} catch (Exception e) {
			result.setExceptionMessage(e.getLocalizedMessage());
			result.connSuccess = false;
			return result;

		}
	}
	
	
	public static Result WSQueryEx(String user, String pwd, String fonction,
			String scenario, String filtre, String Critere1, String Critere2,
			String Critere3, String Critere4, boolean PutInFile) {

		Result result = new Result();

		try {



			SoapObject request = new SoapObject(NAMESPACE, fonction);

			request.addProperty("stUser", user);
			request.addProperty("stPwd", pwd);
			request.addProperty("stScenar", scenario);
			request.addProperty("stFilter", filtre);

			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			envelope.dotNet = true;
			envelope.bodyOut = request;
			envelope.setOutputSoapObject(request);
			// /
			// envelope.dotNet=true;
			// envelope.headerOut=new Element[1];
			// envelope.headerOut[0]=buildAuthHeader(user,pwd);
			//
			HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

			androidHttpTransport.call("http://www.zrws1.org/" + fonction,
					envelope);
			SoapPrimitive resultString = (SoapPrimitive) envelope.getResponse();

			String Content;
			Content = resultString.toString();
			if (Content.equals("") || MyWS.ResultatReadFileWS(Content)==false) 
			{
				result.setDanemMessage(Content);
				result.setContent(Content);
				result.setConnSuccess(false);
				return result;
			} else {
				if (PutInFile == true) {
					String NameFileSansDate;
					String filtreForFile=filtre.replace("_","-");
					NameFileSansDate = scenario + "_" + filtreForFile + "_" + Critere1
							+ "_" + Critere2 + "_" + Critere3 + "_" + Critere4;
					CreateFileWS(Content, NameFileSansDate);
				}

			}

			result.setContent(Content);
			result.setConnSuccess(true);
			return result;

		} catch (ClassCastException ecc) {
			//on crée un fichier vide pour réponse rapide en cas de no data sur le serveur
			/*			String NameFileSansDateA;
			filtre=filtre.replace("_","-");
			NameFileSansDateA = scenario + "_" + filtre + "_" + Critere1
					+ "_" + Critere2 + "_" + Critere3 + "_" + Critere4;
			CreateFileWS("0;0", NameFileSansDateA);
			 */
			result.setExceptionMessage(ecc.getLocalizedMessage());
			result.setDanemMessage("no Data");
			result.connSuccess = false;

			return result;

		} catch (Exception e) {
			result.setExceptionMessage(e.getLocalizedMessage());
			result.connSuccess = false;
			return result;

		}
	}

	/**
	 * return true si dois récupérer le fichier sinon false, si hash est egal ou
	 * si erreur de conn
	 * 
	 * @param user
	 * @param pwd
	 * @param scenario
	 * @param filtre
	 * @return
	 */
	public static String getHashMD5(String user, String pwd, String scenario,
			String filtre) {
		try {

			SoapObject request = new SoapObject(NAMESPACE, "GetHashMD5value");

			request.addProperty("stUser", user);
			request.addProperty("stPwd", pwd);
			request.addProperty("stScenar", scenario);
			request.addProperty("stFilter", filtre);

			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			envelope.dotNet = true;
			envelope.bodyOut = request;
			envelope.setOutputSoapObject(request);

			HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

			androidHttpTransport.call("http://www.zrws1.org/GetHashMD5value",
					envelope);
			SoapPrimitive resultString = (SoapPrimitive) envelope.getResponse();

			String Content;
			Content = resultString.toString();

			return Content;

		} catch (Exception e) {
			lastErrorMsg = "erreur conn";
			return "erreur conn";

		}
	}

	public static String WSSend(String user, String pwd, String fonction,
			String scenario, String trame ) {
		try {
			if (isModeTest()) return "";//si mode test on dit synchro ok sans avoir rien envoyé
			
			boolean ssl=Preferences.getValueBoolean(app.getAppContext(), Espresso.SSL,false);
			if (ssl)
				return  WSSendSSL(  user,   pwd,   fonction,
						  scenario,   trame ) ;
			
			SoapObject request = new SoapObject(NAMESPACE, fonction);

			request.addProperty("login", user);
			request.addProperty("pwd", pwd);
			request.addProperty("stLine", trame);

			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			envelope.dotNet = true;
			envelope.bodyOut = request;
			envelope.setOutputSoapObject(request);
			// /
			// envelope.dotNet=true;
			// envelope.headerOut=new Element[1];
			// envelope.headerOut[0]=buildAuthHeader(user,pwd);
			//c
			HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

			androidHttpTransport.call("http://www.zrws1.org/" + fonction,
					envelope);

			return getServiceResponse(envelope);
			//SoapPrimitive resultString = (SoapPrimitive) envelope.getResponse();
			//return resultString.toString();
			// to get the data
			// String resultData=result.getProperty(0).toString();
			// String[] results = (String[]) result;
			// tv.setText( ""+results[0]);

		} catch (Exception e) {
			lastErrorMsg = e.getLocalizedMessage();// e.getMessage();
			return lastErrorMsg;

		}
	}

	public static String WSSendSSL(String user, String pwd, String fonction,
			String scenario, String trame) {
		try {
			if (isModeTest()) return "";//si mode test on dit synchro ok sans avoir rien envoyé
			
			SoapObject request = new SoapObject(NAMESPACE, fonction);

			request.addProperty("login", user);
			request.addProperty("pwd", pwd);
			request.addProperty("stLine", trame);

			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			envelope.dotNet = true;
			envelope.bodyOut = request;
			envelope.setOutputSoapObject(request);
		
			allowAllSSL();
			String url=URL,host,path;
			url=url.replace("http://", "");
			host=Fonctions.GiveFld(url, 0, "/", true);//"horecamobile.segafredo.fr"
			path=url.replace(host, "");//"/zrserveurws/zrservice.asmx"
			
			HttpsTransportSE androidHttpTransport = new HttpsTransportSE(host,443, path, TIMEOUT);

			androidHttpTransport.call("http://www.zrws1.org/" + fonction,
					envelope);

			return getServiceResponse(envelope);
			//SoapPrimitive resultString = (SoapPrimitive) envelope.getResponse();
			//return resultString.toString();
			// to get the data
			// String resultData=result.getProperty(0).toString();
			// String[] results = (String[]) result;
			// tv.setText( ""+results[0]);

		} catch (Exception e) {
			lastErrorMsg = e.getLocalizedMessage();// e.getMessage();
			return lastErrorMsg;

		}
	}
	
	/************************************************************************/
	/*
	 * on synchronize les KD
	 * stFilterFlds= les champs clefs qui serviront � faire la synchro ex : cli_code;dat_idx01;dat_idx02
	 * destTable = nom de la table destination ex : kems_data
	 * data28 doit contenir le code du VRP qui transmet
	 */
	/************************************************************************/
	public static String WSSynchroKD(String user, String pwd, String fonction,
			String scenario, String trame, int KDtype,String stFilterFlds,String destTable) {
		try {
			if (isModeTest()) return "";//si mode test on dit synchro ok sans avoir rien envoyé
			
			boolean ssl=Preferences.getValueBoolean(app.getAppContext(), Espresso.SSL,false);
			if (ssl)
				return  WSSynchroKDSSL(  user,   pwd,   fonction,
						  scenario,   trame,   KDtype,  stFilterFlds,  destTable) ;
			SoapObject request = new SoapObject(NAMESPACE, fonction);

			request.addProperty("login", user);
			request.addProperty("pwd", pwd);
			request.addProperty("stLine", trame);
			request.addProperty("KDtype", KDtype);
			request.addProperty("stFilterFlds", stFilterFlds);
			request.addProperty("destTable", destTable);

			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			envelope.dotNet = true;
			envelope.bodyOut = request;
			envelope.setOutputSoapObject(request);
			// /
			// envelope.dotNet=true;
			// envelope.headerOut=new Element[1];
			// envelope.headerOut[0]=buildAuthHeader(user,pwd);
			//
			HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

			androidHttpTransport.call("http://www.zrws1.org/" + fonction,
					envelope);

			return getServiceResponse(envelope);

			// to get the data
			// String resultData=result.getProperty(0).toString();
			// String[] results = (String[]) result;
			// tv.setText( ""+results[0]);

		} catch (Exception e) {
			lastErrorMsg = e.getLocalizedMessage();// e.getMessage();
			return lastErrorMsg;

		}
			
	}
	
	public static boolean isModeTest()
	{
		boolean modetest=Preferences.getValueBoolean(app.getAppContext(), Espresso.PREF_MODETEST,false);
		return modetest;
	}
	public static String WSSynchroKDSSL(String user, String pwd, String fonction,
			String scenario, String trame, int KDtype,String stFilterFlds,String destTable) {
		try {
			 
			if (isModeTest()) return "";//si en mode test on dit que tt est ok
			
			SoapObject request = new SoapObject(NAMESPACE, fonction);

			request.addProperty("login", user);
			request.addProperty("pwd", pwd);
			request.addProperty("stLine", trame);
			request.addProperty("KDtype", KDtype);
			request.addProperty("stFilterFlds", stFilterFlds);
			request.addProperty("destTable", destTable);

			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			envelope.dotNet = true;
			envelope.bodyOut = request;
			envelope.setOutputSoapObject(request);
			
			allowAllSSL();
			String url=URL,host,path;
			url=url.replace("http://", "");
			host=Fonctions.GiveFld(url, 0, "/", true);//"horecamobile.segafredo.fr"
			path=url.replace(host, "");//"/zrserveurws/zrservice.asmx"
			
			HttpsTransportSE androidHttpTransport = new HttpsTransportSE(host,443, path, TIMEOUT);

			androidHttpTransport.call("http://www.zrws1.org/" + fonction,
					envelope);

			return getServiceResponse(envelope);

			// to get the data
			// String resultData=result.getProperty(0).toString();
			// String[] results = (String[]) result;
			// tv.setText( ""+results[0]);

		} catch (Exception e) {
			lastErrorMsg = e.getLocalizedMessage();// e.getMessage();
			return lastErrorMsg;

		}
			
			
	}
	/*
	 * dans la fonction WS EXEC decompile la chaine
	 */
	// retourne le nombre de record
	public static int get_EXEC_nbrRec(String chainesep) {
		try {
			String[] splitted1 = chainesep.split("\\|");
			String typeStr = splitted1[0];
			String valuesChaine = splitted1[1];

			String[] nbrStr = typeStr.split("\\;");
			String nbrrec = nbrStr[0];
			return Integer.parseInt(nbrrec);

		} catch (Exception ex) {

		}

		return -1;
	}

	// return le nombre de champ
	public static int get_EXEC_nbrFld(String chainesep) {
		try {
			String[] splitted1 = chainesep.split("\\|");
			String typeStr = splitted1[0];
			String valuesChaine = splitted1[1];

			String[] nbrStr = typeStr.split("\\;");
			String nbrfld = nbrStr[1];
			return Integer.parseInt(nbrfld);

		} catch (Exception ex) {

		}

		return -1;
	}

	// return l'entete
	public static String[] get_EXEC_Values2(String chainesep) {
		try {
			String[] splitted1 = chainesep.split("\\|");
			String typeStr = splitted1[0];
			String valuesChaine = splitted1[1];

			String[] valuesStr = valuesChaine.split("\\\n");
			return valuesStr;

		} catch (Exception ex) {

		}

		return null;
	}

	// return l'entete
	public static String[] get_EXECEXINSERT_Values(String chainesep) {
		try {

			String[] valuesStr = chainesep.split("\\\n");
			return valuesStr;

		} catch (Exception ex) {

		}

		return null;
	}

	public static void CreateFileWS(String chainesep, String NameFileSansDate) {

		DeleteFileExiste(NameFileSansDate);
		OpenFile();

		try {
			GregorianCalendar gc = new GregorianCalendar();
			SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyyMMdd");
			SimpleDateFormat Time_Format = new SimpleDateFormat("HHmmss");
			String stDate_Nomfile;

			stDate_Nomfile = NameFileSansDate + "_"
					+ dateFormat2.format(gc.getTime()) + "_"
					+ Time_Format.format(gc.getTime()) + "_.txt";

			final String TESTSTRING = new String(chainesep);

			File file = new File(STATDIR, stDate_Nomfile);
			file.createNewFile();

			FileWriter filewriter = new FileWriter(file, false);
			filewriter.write(TESTSTRING);
			filewriter.close();

		} catch (Exception ex) {

		}
	}

	public static void OpenFile() {
		try {
			boolean repDejaExistant = false;
			File sdcard = new File("/sdcard");
			String[] contenuSdCard = sdcard.list();
			int i = 0;
			while (i < contenuSdCard.length && !repDejaExistant) {
				if (contenuSdCard[i].compareTo("Negos") == 0)
					repDejaExistant = true;
				i++;
			}
			File rep = new File("/sdcard/Negos");
			if (!repDejaExistant)
				rep.mkdirs();
		} catch (Exception ex) {

		}
	}

	static void OpenFileUpload() {
		try {
			boolean repDejaExistant = false;
			File sdcard = new File("/sdcard");
			String[] contenuSdCard = sdcard.list();
			int i = 0;
			while (i < contenuSdCard.length && !repDejaExistant) {
				if (contenuSdCard[i].compareTo("Negos") == 0)
					repDejaExistant = true;
				i++;
			}
			File rep = new File("/sdcard/Negos/Upload");
			if (!repDejaExistant)
				rep.mkdirs();
		} catch (Exception ex) {

		}

	}

	static void DeleteFileExiste(String NameFileSansDate) {

		try {
			NameFileSansDate=NameFileSansDate.replace(" ", "");//au cas ou on ajoute ou enleve des espace dans les requetes
			File file = new File(STATDIR);
			File[] files = file.listFiles();
			for (int i = 0; i < files.length; i++) {
				String fileFound=files[i].getName();

				fileFound=fileFound.replace(" ", "");
				if (fileFound.startsWith(NameFileSansDate)) {
	
					boolean res=files[i].delete();
				}

			}
			/*MV 13/07/2012
			File fileSd = new File( ExternalStorage.root);
			File[] filesSD = fileSd.listFiles();
			for (int i = 0; i < filesSD.length; i++) {
				if (filesSD[i].getName().startsWith(NameFileSansDate)) {
					DateNomFile1 = filesSD[i].getName();
					filesSD[i].delete();
				}

			}
			 */
		} catch (Exception ex) {

		}

	}

	public static String WSMessageBox(String ChaineScenario, boolean Reussie,
			Activity act) {

		String stDate_Nomfile = "";
		String stDate = "";
		String stHeure = "";
		int posDate=6;
		int posHeure=7;

		try {
			if (Reussie == true) {

				GregorianCalendar gc = new GregorianCalendar();
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
				SimpleDateFormat Time_Format = new SimpleDateFormat("HHmmss");
				stDate_Nomfile = act.getString(R.string.wsmsg_maj)
						+ " "
						+ Fonctions
						.YYYYMMDDhhmmss_to_dd_mm_yyyy_hh_mm_ss(dateFormat
								.format(gc.getTime())
								+ Time_Format.format(gc.getTime()));

			} else {

				//	 posDate=7;
				// posHeure=8;

				File file = new File(STATDIR);
				File[] files = file.listFiles();
				for (int i = 0; i < files.length; i++) {
					// if(files[i].isDirectory()) {
					stDate_Nomfile = files[i].toString();
					if (files[i].getName().startsWith(ChaineScenario)) {
						stDate_Nomfile = files[i].getName();
						break;

					}


					// }

				}

				String[] arf = stDate_Nomfile.split(Pattern.quote("_"));

				stDate = arf[posDate];// 5
				stHeure = arf[posHeure];// 6

				stDate_Nomfile = act.getString(R.string.wsmsg_maj)
						+ " "
						+ Fonctions
						.YYYYMMDDhhmmss_to_dd_mm_yyyy_hh_mm_ss(stDate
								+ stHeure);
				stDate_Nomfile = stDate_Nomfile.replace(".txt", "");

			}
		} catch (Exception ex) {

		}

		return stDate_Nomfile;
	}

	/**
	 * en envoi vers le serveur , le ws ne r�pond jamais erreur, si pb il stock dans la table log
	 * @param envelope
	 * @return
	 */
	static String getServiceResponse(SoapSerializationEnvelope envelope) {
		try {

			// SoapObject resultString = (SoapObject)envelope.getResponse();
			SoapPrimitive resultString = (SoapPrimitive) envelope.getResponse();

			return resultString.toString();
		} catch (Exception ex) {
			return "";// ex.getLocalizedMessage();
		}
	}

	//GESTION DES RETOUR DES WS
	public static class Result {

		public boolean isConnSuccess() {
			return connSuccess;
		}

		public void setConnSuccess(boolean connSuccess) {
			this.connSuccess = connSuccess;
		}

		public boolean isCacheSuccess() {
			return cacheSuccess;
		}

		public void setCacheSuccess(boolean cacheSuccess) {
			this.cacheSuccess = cacheSuccess;
		}

		public String getExceptionMessage() {
			return exceptionMessage;
		}

		public void setExceptionMessage(String exceptionMessage) {
			this.exceptionMessage = exceptionMessage;
		}

		boolean connSuccess;
		boolean cacheSuccess;
		String exceptionMessage;
		String danemMessage;

		public String getDanemMessage() {
			return danemMessage;
		}

		public void setDanemMessage(String danemMessage) {
			this.danemMessage = danemMessage;
		}

		String content;

		public Result() {
			setConnSuccess(false);
			setCacheSuccess(false);
			setExceptionMessage("");
			setContent("");
			setDanemMessage("");
		}

		public String getContent() {
			return content;
		}

		public void setContent(String content) {
			this.content = content;
		}

		public boolean isDataRead() {
			if (this.isCacheSuccess())
				return true;
			if (this.isConnSuccess())
				return true;

			return false;
		}
	}

	public static Result ReadFileWS2(String user, String pwd, String fonction,
			String scenario, String filtre, String Critere1, String Critere2,
			String Critere3, String Critere4, boolean BForce, Activity act) {

		boolean bSucces = true;
		CharBuffer cbufBuffer = CharBuffer.allocate(4096);
		File file;
		GregorianCalendar gc = new GregorianCalendar();
		SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat Time_Format = new SimpleDateFormat("HHmmss");
		String stDate_Nomfile;
		String stNomfileOnlyDate;
		String stChaine;

		Result result = new Result();
		try {
			String filtreForFile=filtre.replace("_", "-");
			// stDate_Nomfile=scenario+"_"+filtre+"_"+Critere1+"_"+Critere2+"_"+Critere3+"_"+Critere4+"_"+dateFormat2.format(gc.getTime())+"_"+Time_Format.format(gc.getTime())+"_.txt";
			stChaine = scenario + "_" + filtreForFile + "_" + Critere1 + "_"
					+ Critere2 + "_" + Critere3 + "_" + Critere4;


			stNomfileOnlyDate = scenario + "_" + filtreForFile + "_" + Critere1 + "_"
					+ Critere2 + "_" + Critere3 + "_" + Critere4 + "_"
					+ dateFormat2.format(gc.getTime());
			boolean exist = false;
			String stValeurTrouver = "";
			file = new File(STATDIR);
			File[] files = file.listFiles();
			if (files != null) {
				for (int i = 0; i < files.length; i++) {
					if (files[i].getName().startsWith(stNomfileOnlyDate)) {
						stValeurTrouver = files[i].getName();
						exist = true;
						break;
					}

				}
			}

			// Forcage
			if (BForce == true) {
				result = MyWS.WSQueryEx(user, pwd, fonction, scenario, filtre,
						Critere1, Critere2, Critere3, Critere4, true);

				if (!result.isConnSuccess()) {
					bSucces = false;
				} else
					result.setDanemMessage(WSMessageBox(stChaine, true, act));

			} else {
				// si on a trouvé un fichier
				if (exist == true) {
					file = new File(STATDIR, stValeurTrouver);
					FileInputStream fileIS = new FileInputStream(file);
					BufferedReader buf = new BufferedReader(
							new InputStreamReader(fileIS));

					String TESTSTRING = "";
					String readStringInt = new String();
					while ((readStringInt = buf.readLine()) != null) {

						TESTSTRING += readStringInt;
						TESTSTRING += "\r\n";

					}
					result.setContent(TESTSTRING);
					result.setCacheSuccess(true);

					// récupération de la date et l'heure de connexion du
					// fichier
					result.setDanemMessage(WSMessageBox(stValeurTrouver, false,
							act));

				} else {
					result = MyWS.WSQueryEx(user, pwd, fonction, scenario,
							filtre, Critere1, Critere2, Critere3, Critere4,
							true);
					// r�cup�ration de la date et l'heure de connexion
					if (result.isConnSuccess() == false) {
						// si on trouve un fichier on le prend meme si pas du
						// jour
						bSucces = false;
					} else
						result.setDanemMessage(WSMessageBox(stChaine, true, act));

				}

			}

			if (bSucces == false) {
				//
				String stNomfileSansDate = scenario + "_" + filtreForFile + "_"
						+ Critere1 + "_" + Critere2 + "_" + Critere3 + "_"
						+ Critere4 + "_";// +dateFormat2.format(gc.getTime());

				file = new File(STATDIR);
				File[] files2 = file.listFiles();
				if (files2 != null) {
					for (int i = 0; i < files2.length; i++) {
						if (files2[i].getName().startsWith(stNomfileSansDate)) {
							stValeurTrouver = files[i].getName();

							file = new File(STATDIR, stValeurTrouver);
							FileInputStream fileIS = new FileInputStream(file);
							BufferedReader buf = new BufferedReader(
									new InputStreamReader(fileIS));

							String TESTSTRING = "";
							String readStringInt = new String();
							while ((readStringInt = buf.readLine()) != null) {

								TESTSTRING += readStringInt;
								TESTSTRING += "\r\n";

							}
							result.setContent(TESTSTRING);
							result.setCacheSuccess(true);

							// récupération de la date et l'heure de connexion
							// du fichier
							result.setDanemMessage(WSMessageBox(
									stValeurTrouver, false, act));
							bSucces=true;
							break;
						}

					}
				}

			}

		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		return result;
	}

	public static String ReadFileWS(String user, String pwd, String fonction,
			String scenario, String filtre, String Critere1, String Critere2,
			String Critere3, String Critere4, boolean BForce, Activity act) {
		String readString = null;
		boolean bSucces = true;
		CharBuffer cbufBuffer = CharBuffer.allocate(4096);
		File file;
		GregorianCalendar gc = new GregorianCalendar();
		SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat Time_Format = new SimpleDateFormat("HHmmss");
		String stDate_Nomfile;
		String stNomfileOnlyDate;
		String stChaine;

		try {
			String filtreForFile=filtre.replace("_", "-");
			// stDate_Nomfile=scenario+"_"+filtre+"_"+Critere1+"_"+Critere2+"_"+Critere3+"_"+Critere4+"_"+dateFormat2.format(gc.getTime())+"_"+Time_Format.format(gc.getTime())+"_.txt";
			stChaine = scenario + "_" + filtreForFile + "_" + Critere1 + "_"
					+ Critere2 + "_" + Critere3 + "_" + Critere4;
			stNomfileOnlyDate = scenario + "_" + filtreForFile + "_" + Critere1 + "_"
					+ Critere2 + "_" + Critere3 + "_" + Critere4 + "_"
					+ dateFormat2.format(gc.getTime());
			boolean exist = false;
			String stValeurTrouver = "";
			file = new File(STATDIR);
			File[] files = file.listFiles();
			if (files != null) {
				for (int i = 0; i < files.length; i++) {
					if (files[i].getName().startsWith(stNomfileOnlyDate)) {
						stValeurTrouver = files[i].getName();
						exist = true;
						break;
					}

				}
			}

			// Forcage
			if (BForce == true) {
				readString = MyWS.WSQuery(user, pwd, fonction, scenario,
						filtre, Critere1, Critere2, Critere3, Critere4, true);

				if (readString.equals("") || readString.equals("erreur conn")) {
					bSucces = false;
				} else
					Global.lastErrorMessage=WSMessageBox(stChaine, true,
							act);

			} else {
				// si on a trouvé un fichier
				if (exist == true) {
					file = new File(STATDIR, stValeurTrouver);
					FileInputStream fileIS = new FileInputStream(file);
					BufferedReader buf = new BufferedReader(
							new InputStreamReader(fileIS));

					String TESTSTRING = "";
					String readStringInt = new String();
					while ((readStringInt = buf.readLine()) != null) {

						TESTSTRING += readStringInt;
						TESTSTRING += "\r\n";

					}
					readString = TESTSTRING;

					// récupération de la date et l'heure de connexion du
					// fichier

					Global.lastErrorMessage=WSMessageBox(stValeurTrouver,
							false, act);
				} else {
					readString = MyWS.WSQuery(user, pwd, fonction, scenario,
							filtre, Critere1, Critere2, Critere3, Critere4,
							true);
					// r�cup�ration de la date et l'heure de connexion
					if (readString.equals("")
							|| readString.equals("erreur conn")) {
						// si on trouve un fichier on le prend meme si pas du
						// jour
						bSucces = false;
					} else
						Global.lastErrorMessage=WSMessageBox(stChaine,
								true, act);

				}

			}

			if (bSucces == false) {
				//
				String stNomfileSansDate = scenario + "_" + filtreForFile + "_"
						+ Critere1 + "_" + Critere2 + "_" + Critere3 + "_"
						+ Critere4 + "_";// +dateFormat2.format(gc.getTime());

				file = new File(STATDIR);
				File[] files2 = file.listFiles();
				if (files2 != null) {
					for (int i = 0; i < files2.length; i++) {
						if (files2[i].getName().startsWith(stNomfileSansDate)) {
							stValeurTrouver = files[i].getName();

							file = new File(STATDIR, stValeurTrouver);
							FileInputStream fileIS = new FileInputStream(file);
							BufferedReader buf = new BufferedReader(
									new InputStreamReader(fileIS));

							String TESTSTRING = "";
							String readStringInt = new String();
							while ((readStringInt = buf.readLine()) != null) {

								TESTSTRING += readStringInt;
								TESTSTRING += "\r\n";

							}
							readString = TESTSTRING;

							// récupération de la date et l'heure de connexion
							// du fichier

							Global.lastErrorMessage=WSMessageBox(
									stValeurTrouver, false, act);

							break;
						}

					}
				}
			}

		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		return readString;
	}

	public static boolean ResultatReadFileWS(String Result) {
		try {
			if (Fonctions.GetStringDanem(Result).equals("erreur conn")) {

				return false;
			}
			if (Fonctions.GetStringDanem(Result).equals("Error connexion")) {

				return false;
			}
			if (Fonctions.GetStringDanem(Result).equals("Acces refusé\r\n")) {

				return false;
			}
			if (Fonctions.GetStringDanem(Result).equals("Acces refusé")) {

				return false;
			}
		} catch (Exception ex) {
			return false;
		}
		return true;

	}
	public static String WSSendFile(String user,String pwd,String fonction,String scenario,String fileName, byte[] b)
	{
		try {

			SoapObject request = new SoapObject(NAMESPACE, fonction);

			request.addProperty("stUser",user);
			request.addProperty("stPwd",pwd);
			request.addProperty("stFile",fileName);
			request.addProperty("stScenar",scenario);
			//request.addProperty("buffer",buffer.getBytes("8859-1"));
			//Log.d("TAG", "buffer to send : "+buffer);
			//String s = "REL;HDR;1638;;0;;ok;04;REL;CPT;;2512;19000;;;;;;;16488;;;;;REL;CPT;;9326;67986425;;;;;;;67977099;;;;;";
			//byte[] b = buffer.getBytes();

			request.addProperty("buffer",Base64.encode(b));

			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
			envelope.dotNet=true;
			envelope.bodyOut=request;
			envelope.setOutputSoapObject(request);

			HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
			//HttpTransportSE androidHttpTransport = new HttpTransportSE(user.getFormattedUrl());


			androidHttpTransport.call("http://www.zrws1.org/"+fonction, envelope);

			return getServiceResponse( envelope );


		} 
		catch (Exception e) 
		{
			//lastErrorMsg=e.getLocalizedMessage();//e.getMessage();
			lastErrorMsg=e.toString();
			Log.e("TAG", "ERROR in SENDFILE : ("+fileName+")"+lastErrorMsg);
			return lastErrorMsg;

		}
	}


	public static String WSSendURLIP_SOCIETE()
	{
		String CheminURL = "http://84.14.126.77/negos/";
		String URL="http://84.14.126.77/";
		String Chemin="negos/";
		String Chemintwin="";

		try {


			String IPtwin = "";
			//mv les maj ne sont que chez DANEM, pas chez le client
			//donc on commente 
			/*			
	 				if (!Fonctions.GetStringDanem(   Global.dbTwin.loadNom(Global.dbTwin.TWIN_IP) ).equals("")  )
	 				{
	 					IPtwin=Fonctions.GetStringDanem(   Global.dbTwin.loadNom(Global.dbTwin.TWIN_IP) );
	 					URL="http://"+IPtwin+"/";
	 				}
	 				if (!Fonctions.GetStringDanem(   Global.dbTwin.loadNom(Global.dbTwin.TWIN_SOCIETE) ).equals("")  )
	 				{
	 					Chemin=Fonctions.GetStringDanem(   Global.dbTwin.loadNom(Global.dbTwin.TWIN_SOCIETE) );
	 					//Chemin="negos/"+Chemin+"/";
	 					Chemin="negos/";

	 				}
			 */
			CheminURL=URL+Chemin;

		} 
		catch (Exception e) 
		{

			return CheminURL;

		}
		return CheminURL;
	}
	public static boolean LastConnection(String dateRecuperere)
	{
		boolean bres=true;
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat Time_Format = new SimpleDateFormat("HH");
		String stDate_Nomfile;
		String stNomfileOnlyDate;
		String stDateLastConnect;
		String stDatedujour;

		GregorianCalendar gc = new GregorianCalendar();

		//Controle si meme DATE et heure "AAAAMMJJHH"

		try {
			stDatedujour=dateFormat.format(gc.getTime())+Time_Format.format(gc.getTime());

			if(dateRecuperere.length()>19)
			{
				dateRecuperere=Fonctions.GetStringDanem(Fonctions.Right(dateRecuperere, 19));
				stDateLastConnect=Fonctions.Mid(dateRecuperere, 6, 4)+Fonctions.Mid(dateRecuperere, 3, 2)+Fonctions.Mid(dateRecuperere, 0, 2)+Fonctions.Mid(dateRecuperere, 11, 2);
				if(stDatedujour.equals(stDateLastConnect))
				{
					bres=true;
				}
				else
					bres=false;
			}


		} 
		catch (Exception e) 
		{

			return bres;

		}



		return bres;

	}
	public static boolean isResultatReadFileWSOk(Activity act, String Result) {
		try {
			if (Fonctions.GetStringDanem(Result).equals(act.getString(R.string.wserror_erreurconn))) {

				return false;
			}
			if (Fonctions.GetStringDanem(Result).equals(act.getString(R.string.wserror_errorconnexion))) {

				return false;
			}
			if (Fonctions.GetStringDanem(Result).equals(act.getString(R.string.wserror_accesrefuscr))) {

				return false;
			}
			if (Fonctions.GetStringDanem(Result).equals(act.getString(R.string.wserror_accesrefus))) {

				return false;
			}
		} catch (Exception ex) {
			return false;
		}
		return true;

	}
	
	
	public static Result WSQueryEx(String user, String pwd, String fonction,
			String scenario, String filtre, String Critere1, String Critere2,
			String Critere3, String Critere4, boolean PutInFile,Activity act) {

		Result result = new Result();

		try {



			SoapObject request = new SoapObject(NAMESPACE, fonction);

			request.addProperty("stUser", user);
			request.addProperty("stPwd", pwd);
			request.addProperty("stScenar", scenario);
			request.addProperty("stFilter", filtre);

			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			envelope.dotNet = true;
			envelope.bodyOut = request;
			envelope.setOutputSoapObject(request);
			// /
			// envelope.dotNet=true;
			// envelope.headerOut=new Element[1];
			// envelope.headerOut[0]=buildAuthHeader(user,pwd);
			//
			HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

			androidHttpTransport.call("http://www.zrws1.org/" + fonction,
					envelope);
			SoapPrimitive resultString = (SoapPrimitive) envelope.getResponse();

			if (scenario.equals("GETFACTURESDUES"))
			{
				int i=0;
				i++;
			}
			String Content;
			Content = resultString.toString();
			if (Content.equals("") || MyWS.isResultatReadFileWSOk(act,  Content)==false) 
			{
				result.setDanemMessage(Content);
				result.setContent(Content);
				result.setConnSuccess(true);
				return result;
			} else {
				if (PutInFile == true) {
					String NameFileSansDate;
					String filtreForFile=filtre.replace("_","-");
					NameFileSansDate = scenario + "_" + filtreForFile + "_" + Critere1
							+ "_" + Critere2 + "_" + Critere3 + "_" + Critere4;
					CreateFileWS(Content, NameFileSansDate);
				}

			}

			result.setContent(Content);
			result.setConnSuccess(true);
			return result;

		} catch (ClassCastException ecc) {
			//on crée un fichier vide pour réponse rapide en cas de no data sur le serveur
/*			String NameFileSansDateA;
			filtre=filtre.replace("_","-");
			NameFileSansDateA = scenario + "_" + filtre + "_" + Critere1
					+ "_" + Critere2 + "_" + Critere3 + "_" + Critere4;
			CreateFileWS("0;0", NameFileSansDateA);
*/
			result.setExceptionMessage(ecc.getLocalizedMessage());
			result.setDanemMessage("no Data");
			result.connSuccess = false;
			
			return result;

		} catch (Exception e) {
			result.setExceptionMessage(e.getLocalizedMessage());
			result.connSuccess = false;
			return result;

		}
	}
	 

		public static Result WSQueryZippedExBorne(String user, String pwd,
				String scenario, String filtre, int start, int count) {
			Result result = new Result();
			try {
				boolean ssl=Preferences.getValueBoolean(app.getAppContext(), Espresso.SSL,false);
				if (ssl)
					return  WSQueryZippedExBorneSSL(  user,   pwd,
							  scenario,   filtre,   start,   count);
							
				String fonction = "ExecExInsertHdrZipBorne";
				SoapObject request = new SoapObject(NAMESPACE, fonction);

				request.addProperty("stUser", user);
				request.addProperty("stPwd", pwd);
				request.addProperty("stScenar", scenario);
				request.addProperty("stFilter", filtre);
				request.addProperty("start", start);
				request.addProperty("count", count);

				SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
						SoapEnvelope.VER11);
				new MarshalBase64().register(envelope);
				envelope.encodingStyle = SoapEnvelope.ENC;
				envelope.dotNet = true;
				envelope.bodyOut = request;
				envelope.setOutputSoapObject(request);

			 
				HttpTransportSE androidHttpTransport = new HttpTransportSE(URL,
						TIMEOUT);

				androidHttpTransport.call(NAMESPACE  + fonction, envelope);

				SoapPrimitive resultSoap = (SoapPrimitive) envelope.getResponse();

				// byte[] compressedBytes = result.toString().getBytes();

				result.setContent(Decompress(resultSoap.toString()));
				result.setConnSuccess(true);
				return result;

			} catch (EOFException e0) 	{//fichier vide veut pas dire ereur de connexion
		
				result.setExceptionMessage(e0.getLocalizedMessage());
				result.connSuccess = true;
				return result;
			} catch (Exception e) {
				result.setExceptionMessage(e.getLocalizedMessage());
				result.connSuccess = false;
				return result;

			}
		}
		
		public static Result WSQueryZippedExBorneSSL(String user, String pwd,
				String scenario, String filtre, int start, int count) {
			Result result = new Result();
			try {
				 
				String fonction = "ExecExInsertHdrZipBorne";
				SoapObject request = new SoapObject(NAMESPACE, fonction);

				request.addProperty("stUser", user);
				request.addProperty("stPwd", pwd);
				request.addProperty("stScenar", scenario);
				request.addProperty("stFilter", filtre);
				request.addProperty("start", start);
				request.addProperty("count", count);

				SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
						SoapEnvelope.VER11);
				new MarshalBase64().register(envelope);
				envelope.encodingStyle = SoapEnvelope.ENC;
				envelope.dotNet = true;
				envelope.bodyOut = request;
				envelope.setOutputSoapObject(request);
 
				allowAllSSL();
				String url=URL,host,path;
				url=url.replace("http://", "");
				host=Fonctions.GiveFld(url, 0, "/", true);//"horecamobile.segafredo.fr"
				path=url.replace(host, "");//"/zrserveurws/zrservice.asmx"
				
				HttpsTransportSE androidHttpTransport = new HttpsTransportSE(host,443, path, TIMEOUT);
				
				androidHttpTransport.call(NAMESPACE  + fonction, envelope);

				SoapPrimitive resultSoap = (SoapPrimitive) envelope.getResponse();

				// byte[] compressedBytes = result.toString().getBytes();

				result.setContent(Decompress(resultSoap.toString()));
				result.setConnSuccess(true);
				return result;

			} catch (EOFException e0) 	{//fichier vide veut pas dire ereur de connexion
		
				result.setExceptionMessage(e0.getLocalizedMessage());
				result.connSuccess = true;
				return result;
			} catch (Exception e) {
				result.setExceptionMessage(e.getLocalizedMessage());
				result.connSuccess = false;
				return result;

			}
		}
		private static TrustManager[] trustManagers;

		public static class _FakeX509TrustManager implements
		        javax.net.ssl.X509TrustManager {
		    private static final X509Certificate[] _AcceptedIssuers = new X509Certificate[] {};

		    public void checkClientTrusted(X509Certificate[] arg0, String arg1)
		            throws CertificateException {
		    }

		    public void checkServerTrusted(X509Certificate[] arg0, String arg1)
		            throws CertificateException {
		    }

		    public boolean isClientTrusted(X509Certificate[] chain) {
		        return (true);
		    }

		    public boolean isServerTrusted(X509Certificate[] chain) {
		        return (true);
		    }

		    public X509Certificate[] getAcceptedIssuers() {
		        return (_AcceptedIssuers);
		    }

		 

			 
		}
		public static void allowAllSSL() {

		    javax.net.ssl.HttpsURLConnection
		            .setDefaultHostnameVerifier(new HostnameVerifier() {
		               

						@Override
						public boolean verify(String hostname,
								SSLSession session) {
							// TODO Auto-generated method stub
							return true;
						}
		            });

		    javax.net.ssl.SSLContext context = null;

		    if (trustManagers == null) {
		        trustManagers = new javax.net.ssl.TrustManager[] { new _FakeX509TrustManager() };
		    }

		    try {
		        context = javax.net.ssl.SSLContext.getInstance("TLS");
		        context.init(null, trustManagers, new SecureRandom());
		    } catch (NoSuchAlgorithmException e) {
		        Log.e("allowAllSSL", e.toString());
		    } catch (KeyManagementException e) {
		        Log.e("allowAllSSL", e.toString());
		    }
		    javax.net.ssl.HttpsURLConnection.setDefaultSSLSocketFactory(context
		            .getSocketFactory());
		}
}
