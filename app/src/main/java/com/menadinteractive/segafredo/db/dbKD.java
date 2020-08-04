package com.menadinteractive.segafredo.db;

import android.provider.BaseColumns;

/**
 * 
 * @author Marc VOUAUX
 * La classe KEMS_général, d'ou vont dériver toutes les vues sur KD
 */

public class dbKD extends DBMain {
	public static final String KDSYNCHRO_RESET="0";
	public static final String KDSYNCHRO_UPDATE="1";
	public static final String KDSYNCHRO_DELETE="2";
	
	public dbKD() {
		super();
	}    

	public  static final String TABLENAME = "kems_data";
	public  static final String TABLENAME_SYNC = "kems_data_sync";
	//    public  static final String TABLENAME_TEMP = "kems_tmpdata";
	public  static final String TABLENAME_TEMP2 = "kems_data";
	public  static final String TABLENAME_TEST = "kems_testdata";
	public  static final String TABLENAME_HISTO = "kems_histodata";
	public  static final String TABLENAME_DUPLICATA = "kems_duplicatadata";
	public  static final String TABLENAME_SAVE = "kems_savedata";
	
	

	public static final String ID = BaseColumns._ID ;
	public static final String fld_kd_soc_code="soc_code";
	public static final String fld_kd_cli_code="cli_code";
	public static final String fld_kd_pro_code="pro_code";      
	public static final String fld_kd_vis_code="vis_code";
	public static String fld_kd_cde_code="cde_code";
	public static String fld_kd_dat_type="dat_type";
	public static String fld_kd_dat_idx01="dat_idx01";
	public static String fld_kd_dat_idx02="dat_idx02";
	public static String fld_kd_dat_idx03="dat_idx03";
	public static String fld_kd_dat_idx04="dat_idx04";
	public static String fld_kd_dat_idx05="dat_idx05";
	public static String fld_kd_dat_idx06="dat_idx06";
	public static String fld_kd_dat_idx07="dat_idx07";
	public static String fld_kd_dat_idx08="dat_idx08";
	public static String fld_kd_dat_idx09="dat_idx09";
	public static String fld_kd_dat_idx10="dat_idx10";
	public static String fld_kd_dat_data01="dat_data01";
	public static String fld_kd_dat_data02="dat_data02";
	public static String fld_kd_dat_data03="dat_data03";
	public static String fld_kd_dat_data04="dat_data04";
	public static String fld_kd_dat_data05="dat_data05";
	public static String fld_kd_dat_data06="dat_data06";
	public static String fld_kd_dat_data07="dat_data07";
	public static String fld_kd_dat_data08="dat_data08";
	public static String fld_kd_dat_data09="dat_data09";
	public static String fld_kd_dat_data10="dat_data10";
	public static String fld_kd_dat_data11="dat_data11";
	public static String fld_kd_dat_data12="dat_data12";
	public static String fld_kd_dat_data13="dat_data13";
	public static String fld_kd_dat_data14="dat_data14";
	public static String fld_kd_dat_data15="dat_data15";
	public static String fld_kd_dat_data16="dat_data16";
	public static String fld_kd_dat_data17="dat_data17";

	public static String fld_kd_dat_data18="dat_data18";
	public static String fld_kd_dat_data19="dat_data19";
	public static String fld_kd_dat_data20="dat_data20";
	public static String fld_kd_dat_data21="dat_data21";
	public static String fld_kd_dat_data22="dat_data22";
	public static String fld_kd_dat_data23="dat_data23";
	public static String fld_kd_dat_data24="dat_data24";
	public static String fld_kd_dat_data25="dat_data25";
	public static String fld_kd_dat_data26="dat_data26";
	public static String fld_kd_dat_data27="dat_data27";
	public static String fld_kd_dat_data28="dat_data28";
	public static String fld_kd_dat_data29="dat_data29";
	public static String fld_kd_dat_data30="dat_data30";

	public static String fld_kd_val_data31="val_data31";
	public static String fld_kd_val_data32="val_data32";
	public static String fld_kd_val_data33="val_data33";
	public static String fld_kd_val_data34="val_data34";
	public static String fld_kd_val_data35="val_data35";
	public static String fld_kd_val_data36="val_data36";
	public static String fld_kd_val_data37="val_data37";
	public static String fld_kd_val_data38="val_data38";
	public static String fld_kd_val_data39="val_data39";

	public static String getTableCreateKD()
	{
		return TABLE_CREATE_KD;
	}

	public static String getTableCreateKDTmp()
	{
		String query=TABLE_CREATE_KD;
		query=query.replace(TABLENAME, TABLENAME_TEMP2);
		return query;
	}
	public static String getTableCreateKDHisto()
	{
		String query=TABLE_CREATE_KD;
		query=query.replace(TABLENAME, TABLENAME_HISTO);
		return query;
	}
	public static String getTableCreateKDDuplicata()
	{
		String query=TABLE_CREATE_KD;
		query=query.replace(TABLENAME, TABLENAME_DUPLICATA);
		return query;
	}
	public static String getTableCreateKDSave()
	{
		String query=TABLE_CREATE_KD;
		query=query.replace(TABLENAME, TABLENAME_SAVE);
		return query;
	}
	public static final String KD_INSERT_STRING=
			"INSERT INTO "+
					TABLENAME+
					" ("+
					fld_kd_soc_code  +","+
					fld_kd_cli_code  +","+
					fld_kd_pro_code  +","+
					fld_kd_vis_code  +","+
					fld_kd_cde_code  +","+
					fld_kd_dat_type  +","+
					fld_kd_dat_idx01 +","+
					fld_kd_dat_idx02 +","+
					fld_kd_dat_idx03 +","+
					fld_kd_dat_idx04 +","+
					fld_kd_dat_idx05 +","+
					fld_kd_dat_idx06 +","+
					fld_kd_dat_idx07 +","+
					fld_kd_dat_idx08 +","+
					fld_kd_dat_idx09 +","+
					fld_kd_dat_idx10 +","+
					fld_kd_dat_data01+","+
					fld_kd_dat_data02+","+
					fld_kd_dat_data03+","+
					fld_kd_dat_data04+","+
					fld_kd_dat_data05+","+
					fld_kd_dat_data06+","+
					fld_kd_dat_data07+","+
					fld_kd_dat_data08+","+
					fld_kd_dat_data09+","+
					fld_kd_dat_data10+","+
					fld_kd_dat_data11+","+
					fld_kd_dat_data12+","+
					fld_kd_dat_data13+","+
					fld_kd_dat_data14+","+
					fld_kd_dat_data15+","+
					fld_kd_dat_data16+","+
					fld_kd_dat_data17+","+
					fld_kd_dat_data18+","+
					fld_kd_dat_data19+","+
					fld_kd_dat_data20+","+
					fld_kd_dat_data21+","+
					fld_kd_dat_data22+","+
					fld_kd_dat_data23+","+
					fld_kd_dat_data24+","+
					fld_kd_dat_data25+","+
					fld_kd_dat_data26+","+
					fld_kd_dat_data27+","+
					fld_kd_dat_data28+","+
					fld_kd_dat_data29+","+
					fld_kd_dat_data30+","+
					fld_kd_val_data31+","+
					fld_kd_val_data32+","+
					fld_kd_val_data33+","+
					fld_kd_val_data34+","+
					fld_kd_val_data35+","+
					fld_kd_val_data36+","+
					fld_kd_val_data37+","+
					fld_kd_val_data38+","+
					fld_kd_val_data39+") VALUES ";
	
	
	public static final String KD_SAVE_INSERT_STRING=
			"INSERT INTO "+
					TABLENAME_SAVE+
					" ("+
					fld_kd_soc_code  +","+
					fld_kd_cli_code  +","+
					fld_kd_pro_code  +","+
					fld_kd_vis_code  +","+
					fld_kd_cde_code  +","+
					fld_kd_dat_type  +","+
					fld_kd_dat_idx01 +","+
					fld_kd_dat_idx02 +","+
					fld_kd_dat_idx03 +","+
					fld_kd_dat_idx04 +","+
					fld_kd_dat_idx05 +","+
					fld_kd_dat_idx06 +","+
					fld_kd_dat_idx07 +","+
					fld_kd_dat_idx08 +","+
					fld_kd_dat_idx09 +","+
					fld_kd_dat_idx10 +","+
					fld_kd_dat_data01+","+
					fld_kd_dat_data02+","+
					fld_kd_dat_data03+","+
					fld_kd_dat_data04+","+
					fld_kd_dat_data05+","+
					fld_kd_dat_data06+","+
					fld_kd_dat_data07+","+
					fld_kd_dat_data08+","+
					fld_kd_dat_data09+","+
					fld_kd_dat_data10+","+
					fld_kd_dat_data11+","+
					fld_kd_dat_data12+","+
					fld_kd_dat_data13+","+
					fld_kd_dat_data14+","+
					fld_kd_dat_data15+","+
					fld_kd_dat_data16+","+
					fld_kd_dat_data17+","+
					fld_kd_dat_data18+","+
					fld_kd_dat_data19+","+
					fld_kd_dat_data20+","+
					fld_kd_dat_data21+","+
					fld_kd_dat_data22+","+
					fld_kd_dat_data23+","+
					fld_kd_dat_data24+","+
					fld_kd_dat_data25+","+
					fld_kd_dat_data26+","+
					fld_kd_dat_data27+","+
					fld_kd_dat_data28+","+
					fld_kd_dat_data29+","+
					fld_kd_dat_data30+","+
					fld_kd_val_data31+","+
					fld_kd_val_data32+","+
					fld_kd_val_data33+","+
					fld_kd_val_data34+","+
					fld_kd_val_data35+","+
					fld_kd_val_data36+","+
					fld_kd_val_data37+","+
					fld_kd_val_data38+","+
					fld_kd_val_data39+") VALUES ";
	
	
	




	private static final String TABLE_CREATE_KD =
			"  CREATE TABLE "+TABLENAME+" ( "+
					ID + " integer primary key autoincrement, "+ 
					" soc_code	varchar(5)		NULL, "+
					" cli_code	varchar(20) 	NULL, "+
					" pro_code 	varchar(20) 	NULL, "+
					" vis_code 	varchar(20) 	NULL, "+
					" cde_code 	varchar(20) 	NULL, "+
					" dat_type 	INTEGER		 	NULL,  "+
					" dat_idx01 	varchar(20) 	NULL, "+ 
					" dat_idx02 	varchar(20) 	NULL, "+
					" dat_idx03 	varchar(20) 	NULL,  "+
					" dat_idx04 	varchar(20) 	NULL, "+
					" dat_idx05 	varchar(20) 	NULL, "+
					" dat_idx06 	varchar(20) 	NULL, "+
					" dat_idx07 	varchar(20) 	NULL, "+
					" dat_idx08 	varchar(20) 	NULL, "+
					" dat_idx09 	varchar(20) 	NULL, "+
					" dat_idx10 	varchar(20) 	NULL, "+
					" dat_data01 	varchar(255) 	NULL, "+
					" dat_data02 	varchar(255)	NULL,	 "+
					" dat_data03 	varchar(255)	NULL,	 "+
					" dat_data04 	varchar(255)	NULL,	 "+
					" dat_data05 	varchar(255)	NULL,	 "+
					" dat_data06 	varchar(255)	NULL,	 "+
					" dat_data07 	varchar(255)	NULL,	 "+
					" dat_data08 	varchar(255)	NULL, "+
					" dat_data09 	varchar(255)	NULL, "+
					" dat_data10 	varchar(255)	NULL, "+
					" dat_data11 	varchar(255)	NULL, "+
					" dat_data12 	varchar(255)	NULL, "+
					" dat_data13 	varchar(255)	NULL, "+
					" dat_data14 	varchar(255)	NULL, "+
					" dat_data15 	varchar(255)	NULL, "+
					" dat_data16 	varchar(255)	NULL, "+
					" dat_data17 	varchar(255)	NULL, "+
					" dat_data18 	varchar(255)	NULL, "+
					" dat_data19 	varchar(255)	NULL, "+
					" dat_data20 	varchar(255)	NULL, "+
					" dat_data21 	varchar(255)	NULL, "+
					" dat_data22 	varchar(255) 	NULL, "+
					" dat_data23 	varchar(255)	NULL,	 "+
					" dat_data24 	varchar(255)	NULL,	 "+
					" dat_data25 	varchar(255)	NULL,	 "+
					" dat_data26 	varchar(255)	NULL,	 "+
					" dat_data27 	varchar(255)	NULL,	 "+
					" dat_data28 	varchar(255)	NULL,	 "+
					" dat_data29 	varchar(255)	NULL, "+
					" dat_data30 	varchar(255)	NULL, "+
					" val_data31 	float		NULL," +
					" val_data32 	float		NULL," +
					" val_data33 	float		NULL," +
					" val_data34 	float		NULL," +
					" val_data35 	float		NULL," +
					" val_data36 	float		NULL," +
					" val_data37 	float		NULL," +
					" val_data38 	float		NULL," +
					" val_data39 	float		NULL"+
					" );";    

	public String getFieldId(){
		return ID;
	}

	public String getFullFieldName(String field){
		return TABLENAME+"."+field;
	}
	/**
	 * on efface le type de donn�es type
	 * @author Marc VOUAUX
	 * @param db
	 * @param type
	 * @param table
	 * @param err
	 * @return
	 */
	protected boolean clear(MyDB db, int type,String table,StringBuilder err)
	{
		try
		{
			String query="delete from "+
					table+
					" where "+
					fld_kd_dat_type+"="+type;


			return db.execSQL(query, err); 



		}
		catch(Exception ex)
		{
			err.append(ex.getMessage());
			return false;
		}	
	}

	public boolean clearAll(MyDB db )
	{
		try
		{
			String query="delete from "+
					TABLENAME;
				
			db.execSQL(query, new StringBuilder()); 

			query="delete from "+
					TABLENAME_HISTO;
				
			db.execSQL(query, new StringBuilder()); 

			
			query="delete from "+
					TABLENAME_DUPLICATA;
				
			 
				
			return db.execSQL(query, new StringBuilder()); 

		}
		catch(Exception ex)
		{
		 
			return false;
		}	
	}


}