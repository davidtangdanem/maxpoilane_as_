package com.menadinteractive.segafredo.db;

public class dbProspect {
	static public String TABLENAMEPROSPECT="T_NEGOS_CLIENTS";//"SITE_PROSPECT";
	
	/*public static String FIELD_CLI_SOC_CODE		    	="CLILABO";         //code soci�t� - remplir obligatoirement
	public static String FIELD_CLI_CODECLIENT			="CODECLIENT";      //code client
	public static String FIELD_CLI_CODELIVR		    	="CODELIVR";        // NON UTLISER - PAS DE ZONE D AFFICHAGE
	public static String FIELD_CLI_DATMAJ				="DATMAJ";
	public static String FIELD_CLI_DATCRE				="DATCRE";
	public static String FIELD_CLI_ANNLOG				="ANNLOG";          // NON UTLISER - PAS DE ZONE D AFFICHAGE
	public static String FIELD_CLI_CODEALARME			="CODEALARME";   //MV 7/12/2012 F=ferm�, S=pas interess� par le produit, C=fournit par un concurrent
	public static String FIELD_CLI_MOTDIRECTEUR	    	="MOTDIRECTEUR";   // NON UTLISER
	public static String FIELD_CLI_CIVILITE		    	="CIVILITE";//M,Mme
	public static String FIELD_CLI_RAISOC				="RAISOC"; //RS
	public static String FIELD_CLI_ADRES1				="ADRES1"; //ADR1 principal
	public static String FIELD_CLI_ADRES2				="ADRES2"; //ADR2 principal
	public static String FIELD_CLI_CPOST		    	="CPOST";  //Code postal principal
	public static String FIELD_CLI_VILLE				="VILLE";   // ville principal
	public static String FIELD_CLI_PAYS			    	="PAYS";    //Pays principal ( libell�)
	public static String FIELD_CLI_CONTACT		    	="CONTACT";  //Contact
	public static String FIELD_CLI_TELEP				="TELEP";     //T�l�phone
	public static String FIELD_CLI_FAX			    	="FAX";      //Fax
	public static String FIELD_CLI_SIRET			    ="SIRET"; //Siret
	public static String FIELD_CLI_CODEAPE		    	="CODEAPE";   //Code ape ou NAF
	public static String FIELD_CLI_CODEACTIVITE	    	="CODEACTIVITE";//secteur activit� , li� avec table PARAM -> CLIACTIV
	public static String FIELD_CLI_MODELIVR		    	="MODELIVR";//  NON UTLISER
	public static String FIELD_CLI_CONDILIVR			="CONDILIVR";//NON UTILISER
	public static String FIELD_CLI_CATCOMPT		    	="CATCOMPT";//categorie comptable renomme. Si 1 on affiche le TTC
	public static String FIELD_CLI_CATTARIF	    		="CATTARIF"; // code Tarif
	public static String FIELD_CLI_RELIQUAT		    	="RELIQUAT"; //NON UTLISER
	public static String FIELD_CLI_REMISE				="REMISE"; //remise client
	public static String FIELD_CLI_ESCOMPTE		    	="ESCOMPTE"; //escompte client
	public static String FIELD_CLI_CODEDEVISE			="CODEDEVISE";//NO UTILISER
	public static String FIELD_CLI_FAMREMISE			="FAMREMISE";//NON UTILISER
	public static String FIELD_CLI_MOYRGL				="MOYRGL";//NON UTILISER
	public static String FIELD_CLI_NOGRATUIT			="NOGRATUIT";//si 0 on v�rouille la qtegrat
	public static String FIELD_CLI_CODVRP				="CODVRP"; // code repr�sentant
	public static String FIELD_CLI_CODEREGION			="CODEREGION";//NON UTILISER
	public static String FIELD_CLI_FAM_CLIENT			="FAM_CLIENT"; // code client famille
	public static String FIELD_CLI_ADR_EMAIL			="ADR_EMAIL"; //email
	public static String FIELD_CLI_DATDERCDE			="DATDERCDE";
	public static String FIELD_CLI_COMMENT   			="COMMENTAIRE";//NO UTILISER
	public static String FIELD_CLI_ENSEIGNE   			="ENSEIGNE";//NO UTILISER
	public static String FIELD_CLI_CG_NUMPRINC   		="CG_NUMPRINC";//num�ro comptable
	public static String FIELD_CLI_CT_NUMPAYEUR   		="CT_NUMPAYEUR";//NON UTILISER
	public static String FIELD_CLI_CT_SITE   			="CT_SITE";// SIte
	public static String FIELD_CLI_NOTVA   				="NOTVA";// numero tva 
	public static String FIELD_CLI_CATALOGUE  			="CATALOGUE";// Catalogue - CODE - Filtre sp�cifique pour DLP
	public static String FIELD_CLI_CODETOURNEE   		="CODETOURNEE";//zone
	public static String FIELD_CLI_JOURPASSAGE   		="JOURPASSAGE";//Libell�
	public static String FIELD_CLI_TELEVENTE  			="TELEVENTE";//Libell�
	public static String FIELD_CLI_SOUSFAMILLECLIENT 	="SOUSFAMILLECLIENT";// code sous famille
	public static String FIELD_CLI_TYPE   				="TYPE";//P = prospect, M=Modifi�
	public static String FIELD_CLI_RATING   			="RATING";//valeur du client L,M,H ou 1,2,3
	public static String FIELD_CLI_LAT   				="LATITUDE";
	public static String FIELD_CLI_LON   				="LONGITUDE";
	public static String FIELD_FLAG		 				="FLAG";//permet de savoir si la ligne a été modifiée
*/
	public static String FIELD_CLI_CODECLIENT 				="CLI_CODECLIENT";
    public static String FIELD_CLI_RAISOC	  				="CLI_RAISOC";
	public static String FIELD_CLI_ENSEIGNE					="CLI_ENSEIGNE";
	public static String FIELD_CLI_ADR1  					="CLI_ADR1";
	public static String FIELD_CLI_ADR2	  					="CLI_ADR2";
	public static String FIELD_CLI_CP					  	="CLI_CP";
	public static String FIELD_CLI_VILLE				  	="CLI_VILLE";
	public static String FIELD_CLI_PAYS					  	="CLI_PAYS";
	public static String FIELD_CLI_TEL1	  					="CLI_TEL1";
	public static String FIELD_CLI_TEL2	 				 	="CLI_TEL2";
	public static String FIELD_CLI_FAX	 				 	="CLI_FAX";
	public static String FIELD_CLI_GSM	  					="CLI_GSM";
	public static String FIELD_CLI_EMAIL	  				="CLI_EMAIL";
	public static String FIELD_CLI_CODETYPE	 			 	="CLI_CODETYPE";
	public static String FIELD_CLI_SIRET	  				="CLI_SIRET";
	public static String FIELD_CLI_TVA_INTRA	 		 	="CLI_TVA_INTRA";
	public static String FIELD_CLI_GRPCLI  					="CLI_GRPCLI";
	public static String FIELD_CLI_CODEAGENT	 		 	="CLI_CODEAGENT";
	public static String FIELD_CLI_CODECIRCUIT	 		 	="CLI_CODECIRCUIT";
	public static String FIELD_CLI_JR_FERME	  				="CLI_JR_FERME";
	public static String FIELD_CLI_DATECREA	 			 	="CLI_DATECREA";
	public static String FIELD_CLI_COULEURSAV			  	="CLI_COULEURSAV";
	public static String FIELD_CLI_CODEMODERGLMT		  	="CLI_CODEMODERGLMT";
	public static String FIELD_CLI_MTTOTENCOURS	  			="CLI_MTTOTENCOURS";
	public static String FIELD_CLI_MTTOTFACDUES	  			="CLI_MTTOTFACDUES";
	public static String FIELD_CLI_MTTOTAVOIRDISPO		  	="CLI_MTTOTAVOIRDISPO";
	public static String FIELD_CLI_MTTOTPMTAVANCE		  	="CLI_MTTOTPMTAVANCE";
	public static String FIELD_CLI_TYPEETABL  				="CLI_TYPEETABL";
	public static String FIELD_CLI_FREETEXT	  				="CLI_FREETEXT";
	public static String FIELD_CLI_EXONERATIONTVA		  	="CLI_EXONERATIONTVA";
	public static String FIELD_CLI_ACTIF	  				="CLI_ACTIF";
	public static String FIELD_CLI_FLAG	  					="CLI_FLAG";
	public static String FIELD_CLI_LAT	  					="CLI_LAT";
	public static String FIELD_CLI_LON	  					="CLI_LON";
	

		  			
	public static final String KD_INSERT_STRING=
			"INSERT INTO "+
					TABLENAMEPROSPECT+
					" ("+	
		 FIELD_CLI_CODECLIENT 		+","+
		 FIELD_CLI_RAISOC	  		+","+
		 FIELD_CLI_ENSEIGNE			+","+
		 FIELD_CLI_ADR1  			+","+
		 FIELD_CLI_ADR2	  			+","+
		 FIELD_CLI_CP				+","+
		 FIELD_CLI_VILLE			+","+
		 FIELD_CLI_PAYS				+","+
		 FIELD_CLI_TEL1	  			+","+
		 FIELD_CLI_TEL2	 			+","+
		 FIELD_CLI_FAX	 			+","+
		 FIELD_CLI_GSM	  			+","+
		 FIELD_CLI_EMAIL	  		+","+
		 FIELD_CLI_CODETYPE	 		+","+
		 FIELD_CLI_SIRET	  		+","+
		 FIELD_CLI_TVA_INTRA	 	+","+
		 FIELD_CLI_GRPCLI  			+","+
		 FIELD_CLI_CODEAGENT	 	+","+
		 FIELD_CLI_CODECIRCUIT	 	+","+
		 FIELD_CLI_JR_FERME	  		+","+
		 FIELD_CLI_DATECREA	 		+","+
		 FIELD_CLI_COULEURSAV		+","+
		 FIELD_CLI_CODEMODERGLMT	+","+
		 FIELD_CLI_MTTOTENCOURS	  	+","+
		 FIELD_CLI_MTTOTFACDUES	  	+","+
		 FIELD_CLI_MTTOTAVOIRDISPO	+","+
		 FIELD_CLI_MTTOTPMTAVANCE	+","+
		 FIELD_CLI_TYPEETABL  		+","+
		 FIELD_CLI_FREETEXT	  		+","+
		 FIELD_CLI_EXONERATIONTVA	+","+
		 FIELD_CLI_ACTIF	  		+","+
		 FIELD_CLI_FLAG	  			+","+
		 FIELD_CLI_LAT	  			+","+
		 FIELD_CLI_LON   			+") VALUES ";

}
