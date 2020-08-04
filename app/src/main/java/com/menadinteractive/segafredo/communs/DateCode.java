package com.menadinteractive.segafredo.communs;

/**
 * conversion date heure en base 36 qui permet de faire tenir le numcde sur 6 car
 * @author marcvouaux
 *
 */
public class DateCode {

	int Annee;
	int Mois;
	int Jour;
	int Heure;
	int Minute;
	int Seconde;
	
	public DateCode()
	{
		Annee=2004;
		Mois=1;
		Jour=1;
		Heure=0;
		Minute=0;
		Seconde=0;

		String datetime=Fonctions.getYYYYMMDDhhmmss();

		
		Annee=Fonctions.convertToInt(Fonctions.Mid(datetime,0, 4));
		Mois=Fonctions.convertToInt(Fonctions.Mid(datetime,4, 2));
		Jour=Fonctions.convertToInt(Fonctions.Mid(datetime,6, 2));
		Heure=Fonctions.convertToInt(Fonctions.Mid(datetime,8, 2));
		Minute=Fonctions.convertToInt(Fonctions.Mid(datetime,10, 2));
		Seconde=Fonctions.convertToInt(Fonctions.Mid(datetime,12, 2));


	}
	
	public String ToCode() 
	{
			/*

			Convertis une date en code dont le format est le suivant:
			
			  On part sur la base 36:
				0 = 0 
				1 = 1
				2 = 2
				...
				A = 10
				B = 11
				...
				Z = 35

			  1er caractere : Annee (0 - Z : 2000 - 2035)
			  2nd caractere : Mois  (1 - C : Janvier - Decembre)
			  3em caractere : Jour  (1 - ? : jour du mois)
			  
				L'heure, la minute et la seconde sont exprim�s en dixaines de secondes
				sur 3 caracteres :		
				
			  caractere 4 � 6: dixaines de secondes du jour ( 0 - 8640 : 000 - 6O0 )



		  */
			String finaldate="";
			

			int iannee=Annee-2000;
			char cannee=ToCode(iannee);
			finaldate+=cannee;

			char cmois=ToCode(Mois);
			finaldate+=cmois;
			
			char cjour=ToCode(Jour);
			finaldate+=cjour;

			//On r�cup�re le nombre de seconde dans la journ�e:

			int jour_secondes=(Heure*(60*60))+(Minute*60)+Seconde;
			//en fait on veut le nombre de 3 secondes : on divise par 3
			jour_secondes=(int)(jour_secondes/3);

			//On passe le nombre de dixaines de secondes en base 36:

			div_t result_3=new div_t();
			result_3.div(jour_secondes,(36*36));
			int u3=result_3.quot;//Premier chiffre du nombre (Centaine)
			div_t result_2=new div_t();
			result_2.div(result_3.rem,36);
			int u2=result_2.quot;//Second chiffre du nombre (Dixaine)
			int u1=result_2.rem;//Unit� 

			char cu3=ToCode(u3);
			finaldate+=cu3;

			char cu2=ToCode(u2);
			finaldate+=cu2;

			char cu1=ToCode(u1);
			finaldate+=cu1;

			return finaldate;
	        
			
	}
	public class div_t
	{
	    public int quot;
	    public int rem;

	    void div(double val,double divider)
	    {
	    	quot=(int)val/(int)divider;
	    	rem= (int)val%(int)divider;
	    }


	}
	char ToCode(int num)//Convertis un chiffre <=35 en base 36
	{
		if((num>35)||(num<0))return 0;
		char c=0;
		if(num>9)c=(char)((num-10)+(int)('A'));
		else c=(char)(num+(int)('0'));

		return c;
	}
	
}
