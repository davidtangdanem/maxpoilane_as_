package com.menadinteractive.segafredo.communs;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;

public class ApplicationLauncher {
	/** Liens Markets */
	//static final String CALCULATRICE_MARKET_URL = "market://details?id=net.tecnotopia.SimpleCalculator";
	//static final String CALCULATRICE_MARKET_URL = "https://play.google.com/store/apps/details?id=de.underflow.calc";
	static final String CALCULATRICE_MARKET_URL = "https://play.google.com/store/apps/details?id=net.tecnotopia.SimpleCalculator";
	
	//static final String ANDEXPLORER_MARKET_URL = "https://play.google.com/store/apps/details?id=lysesoft.andexplorer&hl=fr";
	//static final String ANDEXPLORER_MARKET_URL = "https://play.google.com/store/apps/details?id=lysesoft.andexplorer";
	static final String ANDEXPLORER_MARKET_URL = "market://details?id=lysesoft.andexplorer";
	
 	
	static final String ANDROFRAIS_MARKET_URL = "market://details?id=ndf.pkg";
	
	static final String QUICKSUPPORT_MARKET_URL = "market://details?id=com.teamviewer.quicksupport.market.samsung&hl=fr";
	
	
	/** Les intents des différentes applications */
	private static Intent calculatrice = new Intent()
		.setClassName("net.tecnotopia.SimpleCalculator", 
			"net.tecnotopia.SimpleCalculator.SimpleCalculatorActivity");
	
	private static Intent andexplorer = new Intent()
	.setClassName("lysesoft.andexplorer", 
		"lysesoft.andexplorer.SplashActivity");
	
	private static Intent quicksupport = new Intent()
	.setClassName("lysesoft.andexplorer", 
		"lysesoft.andexplorer.SplashActivity");

	/*private static Intent calculatrice = new Intent()
	.setClassName("de.underflow.calc", 
		"de.underflow.calc.SimpleCalculatorActivity");*/
	private static Intent androFrais = new Intent().setClassName("ndf.pkg", "ndf.pkg.main");
	
	
	private static Intent agendanem = new Intent().setClassName("com.menadinteractive.agendanem", "com.menadinteractive.agendanem.DashboardActivity");
	private static Intent questionem = new Intent().setClassName("com.menadinteractive.quest", "com.menadinteractive.quest.QuestionemActivity");
	private static Intent expressoPerfect = new Intent().setClassName("com.menadinteractive.expressoperfect", "com.menadinteractive.expressoperfect.DashboardActivity");
	
	/** Fonctions de vérification de la présence de l'application sur le device*/
	public static boolean isAndroFraisAvailable(Context context){
		return isApplicationAvailable(context, androFrais);
	}
	
	public static boolean isCalculatriceAvaible(Context context){
		return isApplicationAvailable(context, calculatrice);
	}
	
	public static boolean isAndexplorerAvaible(Context context){
		return isApplicationAvailable(context, andexplorer);
	}
	public static boolean isQuicksupportAvaible(Context context){
		return isApplicationAvailable(context, andexplorer);
	}
	
	public static boolean isAgendanemAvailable(Context context){
		return isApplicationAvailable(context, agendanem);
	}
	public static boolean isQuestionemAvailable(Context context){
		return isApplicationAvailable(context, questionem);
	}
	public static boolean isExpressoPerfectAvailable(Context context){
		return isApplicationAvailable(context, expressoPerfect);
	}
	private static boolean isApplicationAvailable(Context context, Intent application){
		List<ResolveInfo> list = context.getPackageManager().queryIntentActivities(application,   
				PackageManager.MATCH_DEFAULT_ONLY);  
		return list.size() > 0; 
	}
	
	/** Fonctions de lancement de market */
	public static void downloadCalculatrice(Context context){
		downloadApplication(context, CALCULATRICE_MARKET_URL);
	}
	
	public static void downloadAndExplorer(Context context){
		downloadApplication(context, ANDEXPLORER_MARKET_URL);
	}
	
	public static void downloadQuickSupport(Context context){
		downloadApplication(context, QUICKSUPPORT_MARKET_URL);
	}
 
 
	
	public static void downloadAndroFrais(Context context){
		downloadApplication(context, ANDROFRAIS_MARKET_URL);
	}
	
	public static void downloadApplication(Context context, String marketURL){
		Intent intent = new Intent( Intent.ACTION_VIEW,
				Uri.parse(marketURL));
				context.startActivity(intent);
	}
	
	/** Fonctions de lancement d'applications */
	public static void launchCalculatrice(Context context){
		launchApplication(context, calculatrice);
	}
	
	public static void launchAndExplorer(Context context){
		launchApplication(context, andexplorer);
	}
	
	public static void launchAndroFrais(Context context){
		launchApplication(context, androFrais);
	}
	
	private static void launchApplication(Context context, Intent application){
		context.startActivity(application);
	}
}
