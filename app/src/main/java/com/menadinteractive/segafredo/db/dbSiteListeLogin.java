package com.menadinteractive.segafredo.db;

import java.util.ArrayList;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.BaseColumns;

import com.menadinteractive.segafredo.communs.Fonctions;

public class dbSiteListeLogin extends DBMain{
	public String TABLENAME="SITE_LISTLOGIN";
	public static final String ID = BaseColumns._ID ;	
	public final String FIELD_LOGIN_SOC_CODE       ="SOC_CODE" ; 
	public final static String FIELD_LOGIN_LOGIN         ="LOGIN" ;                  
	public final String FIELD_LOGIN_NOM         ="NOM"         ;
	public final String FIELD_LOGIN_PASSWORD      ="PASSWORD"         ;
	public final String FIELD_LOGIN_IP      ="IP"         ;
	public final String FIELD_LOGIN_IP2      ="IP2"         ;
	public final String FIELD_LOGIN_IMAGES      ="IMAGES"         ;//chemins vers les images
	public final String FIELD_LOGIN_TYPE         ="TYPE"  ; //Si 9 : Compte DANEM   
	public final String FIELD_LOGIN_NOTAB         ="NOTAB"  ;    //NO tablette
	public final String FIELD_LOGIN_EMAIL         ="EMAIL"  ;    //email
	public final String FIELD_LOGIN_REGION         ="REGION"  ;    //region
	public final String FIELD_LOGIN_GSM        ="GSM"  ;    //gsm
	public final String FIELD_LOGIN_DATEVALID       ="DATEVALIDITE"  ;    //gsm
	
	
	String  m_stypeDanem="9";
	String  m_stypeAdmin="8";
	
	public static final String TABLE_CREATE=
			"CREATE TABLE [SITE_LISTLOGIN]("+
					ID + " integer primary key autoincrement, "+ 
					"		[SOC_CODE] [nvarchar](50) NOT NULL,"+
					"		[LOGIN] [nvarchar](50) NOT NULL,"+
					"		[NOM] [nvarchar](200) NULL,"+
					"		[PASSWORD] [nvarchar](50) NULL,"+
					"		[IP] [nvarchar](200) NULL,"+
					"		[IP2] [nvarchar](200) NULL,"+
					"		[IMAGES] [nvarchar](200) NULL,"+
					"		[TYPE] [nvarchar](10) NULL,"+
					"		[EMAIL] [nvarchar](150) NULL,"+
					"		[NOTAB] [nvarchar](10) NULL,"+
					"		[REGION] [nvarchar](10) NULL,"+
					"		[GSM] [nvarchar](20) NULL,"+
					"		[DATEVALIDITE] [nvarchar](8) NULL"+

					")";

	 static public class structlistLogin{
		
		public structlistLogin(){
			this.ID = -1;
		}
		public long ID;
		public String SOC_CODE; 
		public String LOGIN;                  
		public String NOM        ;
		public String PASSWORD        ;  
		public String IP        ;   
		public String IP2        ;   
		public String IMAGES        ;   
		public String TYPE  ;   
		public String NOTAB  ;  
		public String REGION  ;
		public String EMAIL;  		
		public String GSM  ;  
		
		
	
	}
	
	MyDB db;
	public dbSiteListeLogin(MyDB _db)
	{
		super();
		db=_db;
	}

	
	public boolean Clear(StringBuilder err)
	{

		try
		{
			String query="DROP TABLE IF EXISTS "+TABLENAME;
			//db.conn.delete(TABLENAME, null, null);
			db.execSQL(query, err);


			db.execSQL(TABLE_CREATE,err);



		}
		catch(Exception ex)
		{
			err.append(ex.getMessage());
			return false;	
		}
		return true;

	}

	public int Count()
	{

		try
		{
			String query="select count(*) from "+TABLENAME;
			Cursor cur=db.conn.rawQuery(query, null);
			if (cur.moveToNext())
			{
				int i= cur.getInt(0);
				if (cur!=null)
					cur.close();//MV 26/03/2015
				return i;
			}
			if (cur!=null)
				cur.close();//MV 26/03/2015
			return 0;
		}
		catch(Exception ex)
		{
			return -1;
		}

	}


	public boolean getlogin(String login,String Password,structlistLogin rep,StringBuilder err)
	{	
		try
		{
			String query="";
			query="select * FROM "+
					TABLENAME+
					" where "+
					this.FIELD_LOGIN_LOGIN+
					"='"+login+"' AND "+
					this.FIELD_LOGIN_PASSWORD+
					"='"+Password+"' COLLATE NOCASE"
					;


			Cursor cur=db.conn.rawQuery(query, null);
			if (cur.moveToNext())
			{
			
				rep.SOC_CODE    =giveFld(cur,FIELD_LOGIN_SOC_CODE          );
				rep.LOGIN       =giveFld(cur,FIELD_LOGIN_LOGIN         );
				rep.NOM         =giveFld(cur,FIELD_LOGIN_NOM);
				rep.PASSWORD    =giveFld(cur,FIELD_LOGIN_PASSWORD      );
				rep.IP    =giveFld(cur,FIELD_LOGIN_IP      );
				rep.IP2    =giveFld(cur,FIELD_LOGIN_IP2      );
				rep.IMAGES    =giveFld(cur,FIELD_LOGIN_IMAGES      );
				rep.TYPE        =giveFld(cur,FIELD_LOGIN_TYPE          );
				rep.NOTAB        =giveFld(cur,FIELD_LOGIN_NOTAB        );
				rep.EMAIL        =giveFld(cur,FIELD_LOGIN_EMAIL        );
				rep.GSM        =giveFld(cur,FIELD_LOGIN_GSM        );
				
				
				
				
				
				cur.close();
				return true;
			}
			if (cur!=null)
				cur.close();//MV 26/03/2015

		}
		catch(Exception ex)
		{

		}

		return false;
	}
	
	public String getNoTab(String login)
	{	
		String Notablette="";
		try
		{
			String query="";
			query="select * FROM "+
					TABLENAME+
					" where "+
					this.FIELD_LOGIN_LOGIN+
					"='"+login+"' COLLATE NOCASE"
					;


			Cursor cur=db.conn.rawQuery(query, null);
			if (cur.moveToNext())
			{
			
				
				Notablette        =Fonctions.GetStringDanem(giveFld(cur,FIELD_LOGIN_NOTAB        ));
				
				
				
				
				cur.close();
			
			}
			if (cur!=null)
				cur.close();//MV 26/03/2015

		}
		catch(Exception ex)
		{
			Notablette="";

		}

		return Notablette;
	}
	
	public boolean getlogin(String login,structlistLogin rep,StringBuilder err)
	{	
		try
		{
			String query="";
			query="select * FROM "+
					TABLENAME+
					" where "+this.FIELD_LOGIN_LOGIN+"='"+login+"' COLLATE NOCASE";

			Cursor cur=db.conn.rawQuery(query, null);
			if (cur.moveToNext())
			{
			
				rep.SOC_CODE    =giveFld(cur,FIELD_LOGIN_SOC_CODE          );
				rep.LOGIN       =giveFld(cur,FIELD_LOGIN_LOGIN         );
				rep.NOM         =giveFld(cur,FIELD_LOGIN_NOM);
				rep.PASSWORD    =giveFld(cur,FIELD_LOGIN_PASSWORD      );
				rep.IP    =giveFld(cur,FIELD_LOGIN_IP      );
				rep.IP2    =giveFld(cur,FIELD_LOGIN_IP2      );
				rep.IMAGES    =giveFld(cur,FIELD_LOGIN_IMAGES      );
				rep.TYPE        =giveFld(cur,FIELD_LOGIN_TYPE          );
				rep.NOTAB        =giveFld(cur,FIELD_LOGIN_NOTAB         );
				rep.EMAIL        =giveFld(cur,FIELD_LOGIN_REGION         );
				rep.GSM        =giveFld(cur,FIELD_LOGIN_GSM        );
				rep.REGION        =giveFld(cur,FIELD_LOGIN_REGION        );
				
				
				
				
				
				cur.close();
				return true;
			}

			if (cur!=null)
				cur.close();//MV 26/03/2015
		}
		catch(Exception ex)
		{

		}

		return false;
	}
	
	//l'id est il admin ?
	public boolean isAdmin(String login)
	{
		structlistLogin rep=new structlistLogin();
		StringBuilder err=new StringBuilder();
		getlogin( login, rep, err);
		
		if (Fonctions.GetStringDanem(rep.TYPE).equals(m_stypeAdmin)
				|| Fonctions.GetStringDanem(rep.TYPE).equals(m_stypeDanem))
			return true;
		
		return false;
	}
	public String getLbl(String codeRec)
	{
		try
		{
			String query="SELECT * FROM "+
					TABLENAME+
					" WHERE "+
					
					this.FIELD_LOGIN_LOGIN+
					"="+
					"'"+codeRec+"' COLLATE NOCASE";

			String prm_lbl		="";

			Cursor cur=db.conn.rawQuery(query, null);
			while (cur.moveToNext())
			{
				prm_lbl		=giveFld(cur,this.FIELD_LOGIN_NOM);

				cur.close();
				return prm_lbl;
			}
			if (cur!=null)
				cur.close();//MV 26/03/2015
		}
		catch(Exception ex)
		{
			return "";
		}

		return "";
	}
	
	public String getLblNom(String codeRec)
	{
		try
		{
			String query="SELECT * FROM "+
					TABLENAME+
					" WHERE "+
					
					this.FIELD_LOGIN_LOGIN+
					"="+
					"'"+codeRec+"' COLLATE NOCASE";

			String prm_lbl		="";

			Cursor cur=db.conn.rawQuery(query, null);
			while (cur.moveToNext())
			{
				prm_lbl		=giveFld(cur,this.FIELD_LOGIN_NOM);

				cur.close();
				return prm_lbl;
			}
			if (cur!=null)
				cur.close();//MV 26/03/2015
		}
		catch(Exception ex)
		{
			return codeRec;
		}

		return codeRec;
	}
	public String getLblImages(String codeRec)
	{
		try
		{
			String query="SELECT * FROM "+
					TABLENAME+
					" WHERE "+
					
					this.FIELD_LOGIN_LOGIN+
					"="+
					"'"+codeRec+"' COLLATE NOCASE";

			String prm_lbl		="";

			Cursor cur=db.conn.rawQuery(query, null);
			while (cur.moveToNext())
			{
				prm_lbl		=giveFld(cur,this.FIELD_LOGIN_IMAGES);

				cur.close();
				return prm_lbl;
			}
			if (cur!=null)
				cur.close();//MV 26/03/2015
		}
		catch(Exception ex)
		{
			return codeRec;
		}

		return codeRec;
	}

	public boolean loadLogin(ArrayList<Bundle> liste, StringBuffer stBuf)
	{
		boolean bres=false;
		
		try
		{
			
			String m_login="";
			String m_nom="";
			liste.clear();
			
			
			String query="SELECT "+this.FIELD_LOGIN_LOGIN+","+this.FIELD_LOGIN_NOM
					+" FROM "+
					TABLENAME+ " ORDER BY "+this.FIELD_LOGIN_NOM ;
	
			Bundle bundleRep = new Bundle();
			bundleRep.putString(this.FIELD_LOGIN_LOGIN,"");
			bundleRep.putString(this.FIELD_LOGIN_NOM   ,"Cliquez ici");
			liste.add(bundleRep);
			Cursor cur=db.conn.rawQuery (query,null);
			while (cur.moveToNext())
			{
				bres=true;
				m_login   			=this.giveFld(cur,this.FIELD_LOGIN_LOGIN     		);
				m_nom      			=this.giveFld(cur,this.FIELD_LOGIN_NOM         		 );
			
				  bundleRep = new Bundle();
				bundleRep.putString(this.FIELD_LOGIN_LOGIN, m_login);
				bundleRep.putString(this.FIELD_LOGIN_NOM   ,m_nom);
				liste.add(bundleRep);
				
				
			
	
			}
			if (cur!=null)
				cur.close();//MV 26/03/2015
			if (liste.size()==0) return false;
	
		}
		catch(Exception ex)
		{
			return false;
		}
		
		return true;
	}

	public  boolean isValid(String login )
	{
		try
		{
			String query="select * from "+TABLENAME+
					" where "+FIELD_LOGIN_LOGIN+"='"+login+"'";
			
			Cursor cur=db.conn.rawQuery(query, null);
			if (cur.moveToNext())
			{
				String datevalidite=Fonctions.GetStringDanem(cur.getString(cur.getColumnIndex(this.FIELD_LOGIN_DATEVALID))).trim();
				
				if (cur!=null)
					cur.close();//MV 26/03/2015
				if (datevalidite.equals("")) return true;
				if (Fonctions.convertToLong( datevalidite)
						<Fonctions.convertToLong( Fonctions.getYYYYMMDD()))
					return false;
				
				return true;				
			}
			if (cur!=null)
				cur.close();//MV 26/03/2015
			return true;
		}
		catch(Exception ex)
		{
			return true;
		}
	}
   
}
