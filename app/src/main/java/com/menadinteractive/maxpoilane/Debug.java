package com.menadinteractive.maxpoilane;

public class Debug {
	private static boolean isDebugMode;
	private static String TAG ="TAG";

	public static void setDebug(boolean debugModeOn){
		isDebugMode = debugModeOn;
	}

	public static void Log(String msg){
		if(isDebugMode)
			android.util.Log.d(TAG, msg);
	}

	public static void Log(String tag, String msg){
		if(isDebugMode)
			android.util.Log.d(tag, msg);
	}

	public static void Log(String tag, String msg, boolean force){
		if(force)
			android.util.Log.d(tag, msg);
		else 
			Log(tag, msg);
	}

	public static void StackTrace(Exception e){
		if(isDebugMode)
			e.printStackTrace();
	}

	public static void StackTrace(Exception e, boolean force){
		if(force)
			e.printStackTrace();
		else
			StackTrace(e);
	}
}
