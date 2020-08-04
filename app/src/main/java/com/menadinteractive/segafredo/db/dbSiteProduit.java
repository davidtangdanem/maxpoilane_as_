package com.menadinteractive.segafredo.db;

import java.util.ArrayList;

import android.database.Cursor;
import android.provider.BaseColumns;
import android.util.Log;

import com.menadinteractive.maxpoilane.R;
import com.menadinteractive.segafredo.communs.Fonctions;
import com.menadinteractive.segafredo.communs.Global;

public class dbSiteProduit extends DBMain {
	public static String TABLENAME = "SITE_PRODUIT2";
	public static String INDEXNAME_CODFAM = "INDEX_CODFAM";
	public static final String ID = BaseColumns._ID;
	public final String FIELD_PRO_SOC_CODE = "SOC_CODE"; // code soci�t�
	public final static String FIELD_PRO_CODART = "CODART"; // code article
	public final static String FIELD_PRO_CODFAM = "CODFAM"; // code famille -
															// lien table param
															// - FAM1
	public final String FIELD_PRO_CODMARQUE = "CODMARQUE"; // code marque
	public final String FIELD_PRO_CODGRP = "CODGRP"; // code gamme - lien table
														// param - GAM
	public final String FIELD_PRO_CODESGR = "CODSGR"; // code sous famille -
														// lien table param -
														// FAM2
	public final static String FIELD_PRO_NOMART1 = "NOMART1"; // libelle
	public final String FIELD_PRO_NOMART2 = "NOMART2"; // NO UTILISER
	public final String FIELD_PRO_PV_CONS = "PV_CONS"; // prix
	public final String FIELD_PRO_TARIF = "TARIF"; // Prix d'achat
	public final String FIELD_PRO_TARIFMIN = "TARIFMIN"; // NO UTILISER
	public final String FIELD_PRO_TARIFMAX = "TARIFMAX "; // NO UTILISER
	public final String FIELD_PRO_CODETHE = "CODTHE"; // [code Goupe de choix -
														// lien table param et
														// siteConfigurateur -
														// GCHOIX (pour med)]
	public final String FIELD_PRO_CODECOLLECT = "CODCOLLECT"; // [Code
																// d'interdiction
																// ( 1 ou 0
																// )(pour med)]
	public final String FIELD_PRO_CODEMOD = "CODMOD"; // NO UTILISER
	public final String FIELD_PRO_CODELIN = "CODELIN"; // NO UTILISER
	public final static String FIELD_PRO_EAN = "EAN"; // EAN13
	public final String FIELD_PRO_ETATSUIVI = "ETATSUIVI";// 0123
	public final String FIELD_PRO_PCB = "PCB"; // Conditionnement
	public final String FIELD_PRO_EAN14 = "EAN14"; // EAN14
	public final String FIELD_PRO_CONC = "CONC"; // NO UTILISER
	public final String FIELD_PRO_DIM = "DIM"; // NO UTILISER
	public final String FIELD_PRO_RAYON = "RAYON"; // NO UTILISER
	public final String FIELD_PRO_UNIVERS = "UNIVERS";// code univers - lien
														// table param - UNIV
	public final String FIELD_PRO_AFFLIBRE1 = "AFFLIBRE1";// [Modele de
															// r�f�rence
															// (pour med)]
	public final String FIELD_PRO_AFFLIBRE2 = "AFFLIBRE2";// [Annexe
															// produit(pour
															// med)]
	public final String FIELD_PRO_QTEMAX = "QTEMAX";// NO UTILISER
	public final String FIELD_PRO_UNPOIDS = "UNPOIDS";// NO UTILISER
	public final String FIELD_PRO_POIDSBRUT = "POIDSBRUT"; // NO UTILISER
	public final String FIELD_PRO_POIDSNET = "POIDSNET";// NO UTILISER
	public final String FIELD_PRO_UNVENTE = "UNVENTE";// NO UTILISER
	public final String FIELD_PRO_SUISSTK = "SUISSTK";// NO UTILISER
	public final String FIELD_PRO_NOMENC = "NOMENC";// NO UTILISER
	public final String FIELD_PRO_EMPLAC = "EMPLAC";// ORDRE
	public final String FIELD_PRO_QTEMINI = "QTEMINI";// contr�le sur la
														// quantit� mini
	public final String FIELD_PRO_TAILLE = "TAILLE";// NO UTILISER
	public final String FIELD_PRO_COULEUR = "COULEUR";// NO UTILISER
	public final String FIELD_PRO_NOCASE = "NOCASE";// NO UTILISER
	public final String FIELD_PRO_ACTIF_CDE = "ACTIF_CDE"; // Si N alors l Edit
															// Qte et le bouton
															// Valider est non
															// visible
	public final String FIELD_PRO_LBL_GENERIC = "LBL_GENERIC";// NO UTILISER
	public final String FIELD_PRO_RACCOURCI = "RACCOURCI";// Code article
	public final String FIELD_PRO_DATEDISPO = "DATEDISPO";// date
	public final String FIELD_PRO_PHOTONAME = "PHOTONAME";// Code article
	public final String FIELD_PRO_FAMTARIF = "FAMTARIF"; // codetarif article
	public final String FIELD_PRO_FAMREMISE = "FAMREMISE";// TABLE PRODFAMREMISE
															// DANS PARAM, pour
															// CHOIX DE REMISES
	public final String FIELD_PRO_CATALOGUE_AUTO = "CATALOGUE_AUTO";// G1 - P
																	// li� a
																	// la table
																	// client du
																	// champ
																	// catalogue(DLP)

	public final String FIELD_PRO_FAMART7 = "FAMART7";// code famille7 - lien
														// table param - FAM7
	public final String FIELD_PRO_SITUATION = "SITUATION";// (DLP) Affichage du
															// produit si il
															// contient CAT ou
															// NS

	public final String FIELD_PRO_DLC = "DLC"; // DLC
	public final String FIELD_PRO_PRODUITNOUVEAU = "PRODUITNOUVEAU"; // Produit
																		// noouveau,
																		// juste
																		// un
																		// tri
	public final String FIELD_PRO_PRODUITENPROMO = "PRODUITENPROMO"; // juste un
																		// tri
	public final String FIELD_PRO_PRODUITADEGAGER = "PRODUITADEGAGER"; // produit
																		// a
																		// degager
																		// ,
																		// juste
																		// un
																		// tri

	public final String FIELD_PRO_COMMENT1ACTIF = "CMT1ACTIF";// O/N activation
																// du
																// commentaire
																// ligne 1
	public final String FIELD_PRO_COMMENT1MINUSONLY = "CMT1MINUS";// O/N le
																	// commentaire
																	// 1 doit
																	// �tre
																	// saisi en
																	// minuscule
	public final String FIELD_PRO_COMMENT1LNG = "CMT1LNG";// longueur
															// autoris�e
	public final String FIELD_PRO_COMMENT2ACTIF = "CMT2ACTIF";// O/N activation
																// du
																// commentaire
																// ligne 2
	public final String FIELD_PRO_COMMENT2LNG = "CMT2LNG";// longueur
															// autoris�e
	public final String FIELD_PRO_CODEPANACHAGE = "CODEPANACH";// le code qui
																// regroupe les
																// articles
																// entre eux
																// pour les
																// remise qt�
																// panach�es
	// public final String FIELD_PRO_ENSEIGNE ="ENSEIGNE" ;
	public final String FIELD_PRO_HAUTEUR = "HAUTEUR"; // Hauteur
	public final String FIELD_PRO_LARGEUR = "LARGEUR"; // Largeur
	public final String FIELD_PRO_LONGUEUR = "LONGUEUR"; // Longueur
	public final String FIELD_PRO_WEBFICHE = "WEBFICHE";
	public final static String FIELD_PRO_CODETVA = "PRM_TVA_CODE";// "CODETVA" ;
															// //Gestion mutlti
															// TVA (ajout tof le
															// 19/08/13)
	public final String FIELD_PRO_VOLUME = "VOLUME"; // Volume
	public final String FIELD_PRO_RL = "RL"; // RL - Si N alors non relev�
	public final String FIELD_PRO_ISGRAT = "ISGRAT"; // si O (Oui) alors gratuit
														// autoris�, 
	// public final String FIELD_PRO_PRM_TVA_CODE ="PRM_TVA_CODE" ; //CODE tva a
	// rechercher dans param

	public static final String TABLE_CREATE = "CREATE TABLE IF NOT EXISTS [SITE_PRODUIT2]("
			+ ID
			+ " integer primary key autoincrement, "
			+ "		[SOC_CODE] [nvarchar](50) NOT NULL,"
			+ "		[CODART] [nvarchar](50) NOT NULL,"
			+ "		[CODFAM] [nvarchar](30) NULL,"
			+ "		[CODMARQUE] [nvarchar](30) NULL,"
			+ "		[CODGRP] [nvarchar](30) NULL,"
			+ "		[CODSGR] [nvarchar](30) NULL,"
			+ "		[NOMART1] [nvarchar](100) NULL,"
			+ "		[NOMART2] [nvarchar](100) NULL,"
			+ "		[PV_CONS] [nvarchar](50) NULL,"
			+ "		[TARIF] [nvarchar](50) NULL,"
			+ "		[TARIFMIN] [nvarchar](50) NULL,"
			+ "		[TARIFMAX] [nvarchar](50) NULL,"
			+ "		[CODTHE] [nvarchar](30) NULL,"
			+ "		[CODCOLLECT] [nvarchar](30) NULL,"
			+ "		[CODMOD] [nvarchar](30) NULL,"
			+ "		[CODELIN] [nvarchar](30) NULL,"
			+ "		[EAN] [nvarchar](30) NULL,"
			+ "		[ETATSUIVI] [nvarchar](20) NULL,"
			+ "		[PCB] [nvarchar](20) NULL,"
			+ "		[EAN14] [nvarchar](30) NULL,"
			+ "		[CONC] [nvarchar](2) NULL,"
			+ "		[DIM] [nvarchar](30) NULL,"
			+ "		[RAYON] [nvarchar](30) NULL,"
			+ "		[UNIVERS][nvarchar](30) NULL,"
			+ "		[AFFLIBRE1] [nvarchar](255) NULL,"
			+ "		[AFFLIBRE2] [nvarchar](255) NULL,"
			+ "		[QTEMAX] [nvarchar](30) NULL,"
			+ "		[UNPOIDS] [float] NULL,"
			+ "		[POIDSBRUT] [nvarchar](30) NULL,"
			+ "		[POIDSNET] [nvarchar](30) NULL,"
			+ "		[UNVENTE] [nvarchar](30) NULL,"
			+ "		[SUISSTK] [nvarchar](30) NULL,"
			+ "		[NOMENC] [nvarchar](30) NULL,"
			+ "		[EMPLAC] [nvarchar](250) NULL,"
			+ "		[QTEMINI] [nvarchar](30) NULL,"
			+ "		[TAILLE] [nvarchar](30) NULL,"
			+ "		[COULEUR] [nvarchar](30) NULL,"
			+ "		[NOCASE] [nvarchar](30) NULL,"
			+ "		[ACTIF_CDE] [nvarchar](5) NULL,"
			+ "		[LBL_GENERIC][nvarchar](250) NULL,"
			+ "		[RACCOURCI] [nvarchar](30) NULL,"
			+ "		[DATEDISPO] [nvarchar](15) NULL,"
			+ "		[PHOTONAME] [nvarchar](100) NULL,"
			+ "		[FAMTARIF] [nvarchar](50) NULL,"
			+ "		[FAMREMISE] [nvarchar](50) NULL,"
			+ "		[CATALOGUE_AUTO] [nvarchar](100) NULL,"
			+ "		[FAMART7] [nvarchar](50) NULL,"
			+ "		[SITUATION] [nvarchar](50) NULL,"
			+ "		[DLC] [nvarchar](15) NULL,"
			+ "		[PRODUITNOUVEAU] [nvarchar](2) NULL,"
			+ "		[PRODUITENPROMO] [nvarchar](2) NULL,"
			+ "		[PRODUITADEGAGER] [nvarchar](2) NULL,"
			+

			"		[CMT1ACTIF] [nvarchar](1) NULL,"
			+ "		[CMT1MINUS] [nvarchar](1) NULL,"
			+ "		[CMT2ACTIF] [nvarchar](1) NULL,"
			+ "		[CMT1LNG] [nvarchar](4) NULL,"
			+ "		[CMT2LNG] [nvarchar](4) NULL,"
			+ "		[CODEPANACH] [nvarchar](20) NULL,"
			+ "		[HAUTEUR] [nvarchar](30) NULL,"
			+ "		[LARGEUR] [nvarchar](30) NULL,"
			+ "		[LONGUEUR] [nvarchar](30) NULL,"
			+ "		[WEBFICHE] [nvarchar](30) NULL,"
			+ "		[ISGRAT] [nvarchar](1) NULL,"
			+ "		[PRM_TVA_CODE] [nvarchar](10) NULL" +

			")";

	public static final String INDEX1_CREATE = "CREATE INDEX IF NOT EXISTS ixEAN ON "
			+ TABLENAME + " (" + FIELD_PRO_EAN + ")";

	public static final String INDEX2_CREATE = "CREATE INDEX IF NOT EXISTS ixCodeart ON "
			+ TABLENAME + " (" + FIELD_PRO_CODART + ")";

	public static final String INDEX_CODFAM_CREATE = "CREATE INDEX IF NOT EXISTS ["
			+ INDEXNAME_CODFAM
			+ "] "
			+ "ON ["
			+ TABLENAME
			+ "] (["
			+ FIELD_PRO_CODFAM + "])";

	static public class structArt {

		public structArt() {
			this.ID = -1;
		}

		public long ID;
		public String SOC_CODE;
		public String CODART;
		public String CODFAM;
		public String CODMARQUE;
		public String CODGRP;
		public String CODSGR;
		public String NOMART1;
		public String NOMART2;
		public String PV_CONS;
		public String TARIF;
		public String TARIFMIN;
		public String TARIFMAX;
		public String CODTHE;
		public String CODCOLLECT;
		public String CODMOD;
		public String CODELIN;
		public String EAN;
		public String ETATSUIVI;
		public String PCB;
		public String EAN14;
		public String CONC;
		public String DIM;
		public String RAYON;
		public String UNIVERS;
		public String AFFLIBRE1;
		public String AFFLIBRE2;
		public String QTEMAX;
		public String UNPOIDS;
		public String POIDSBRUT;
		public String POIDSNET;
		public String UNVENTE;
		public String SUISSTK;
		public String NOMENC;
		public String EMPLAC;
		public String QTEMINI;
		public String TAILLE;
		public String COULEUR;
		public String NOCASE;
		public String ACTIF_CDE;
		public String LBL_GENERIC;
		public String RACCOURCI;
		public String DATEDISPO;
		public String PHOTONAME;
		public String FAMTARIF;
		public String FAMREMISE;
		public String CATALOGUE_AUTO;
		public String ENSEIGNE;
		public String FAMART7;
		public String SITUATION;
		public String DLC;
		public String PRODUITNOUVEAU;
		public String PRODUITENPROMO;
		public String PRODUITADEGAGER;

		public String CMT1ACTIF;
		public String CMT1MINUS;
		public String CMT2ACTIF;

		public String CMT1LNG;
		public String CMT2LNG;

		public String CODEPANACH;
		public String HAUTEUR;
		public String LARGEUR;
		public String LONGUEUR;
		public String WEBFICHE;
		public String CODETVA;
		public String ISGRAT;
		public String MAXDATE;//si jointure hdr histo
		public String QTEINV;
	}

	MyDB db;

	public dbSiteProduit(MyDB _db) {
		super();
		db = _db;
	}

	public boolean Clear(StringBuilder err) {

		try {
			String query = "DROP TABLE IF EXISTS " + TABLENAME;
			// db.conn.delete(TABLENAME, null, null);
			db.execSQL(query, err);

			db.execSQL(TABLE_CREATE, err);

		} catch (Exception ex) {
			err.append(ex.getMessage());
			return false;
		}
		return true;

	}

	public int Count() {

		try {
			String query = "select count(*) from " + TABLENAME;
			Cursor cur = db.conn.rawQuery(query, null);
			if (cur.moveToNext()) {
				return cur.getInt(0);
			}
			return 0;
		} catch (Exception ex) {
			return -1;
		}

	}

	public boolean getProduit(String code, structArt art, StringBuilder err) {
		try {
			String query = "";
			query = "select * FROM " + TABLENAME + " where "
					+ this.FIELD_PRO_CODART + "='" + code + "' ";

			Cursor cur = db.conn.rawQuery(query, null);
			if (cur.moveToNext()) {
				art=fillStructArt(cur,art);
				 
				cur.close();
				return true;
			}

		} catch (Exception ex) {

		}

		return false;
	}
 
	// renvoi toutes les lignes de la cde numcde
	public ArrayList<structArt> getProduitsCde(String soc_code, String numcde) {
		ArrayList<structArt> arts = new ArrayList<dbSiteProduit.structArt>();

		try {

			String query = "";
			query = "select pro.* FROM " + TABLENAME + " as pro "
					+ " INNER JOIN " + dbKD84LinCde.TABLENAME + " as lin "
					+ " on " + Global.dbKDLinCde.FIELD_LIGNECDE_PROCODE + "="
					+ this.FIELD_PRO_CODART + " where " + " pro."
					+ FIELD_PRO_SOC_CODE + "='" + soc_code + "'" + " and "
					+ Global.dbKDLinCde.fld_kd_dat_type + "="
					+ Global.dbKDLinCde.KD_TYPE + " and "
					+ Global.dbKDLinCde.FIELD_LIGNECDE_CDECODE + "='" + numcde
					+ "'" +

				" ORDER BY CAST("+this.FIELD_PRO_EMPLAC+" "+" as Integer) ";


			Cursor cur = db.conn.rawQuery(query, null);
			while (cur != null && cur.moveToNext()) {
				structArt art = new structArt();
			 
				art=fillStructArt(cur,art);
				arts.add(art);
			}
			cur.close();

		} catch (Exception ex) {

		}

		return arts;
	}

	// renvoi toutes les lignes deja inv
	public ArrayList<structArt> getProduitsInventaire(boolean dejavu,boolean pasvu,String codefam,String filtre) {
		ArrayList<structArt> arts = new ArrayList<dbSiteProduit.structArt>();

		try {

			String query = "",queryadd="";;
			if (filtre.equals("") == false) {
				queryadd = " AND (" + FIELD_PRO_CODART + " like '%" + filtre
						+ "%' OR " + FIELD_PRO_NOMART1 + " like '%" + filtre
						+ "%' ) ";
			}
			query = "select pro.*,"
					+ " inv."+dbKD543LinInventaire.FIELD_LIGNEINV_QTE
					+ " FROM " + TABLENAME + " as pro "
					+ " inner JOIN " + dbKD545StockTheoSrv.TABLENAME
					+ " as lin " + " on "
					+ " lin."+Global.dbKDLinInv.FIELD_LIGNEINV_PROCODE + "="
					+ this.FIELD_PRO_CODART + " and "
					+  " lin."+dbKD545StockTheoSrv.fld_kd_dat_type + "="
					+  dbKD545StockTheoSrv.KD_TYPE 
					+" left join "+dbKD543LinInventaire.TABLENAME +" inv on "
					+" inv."+dbKD543LinInventaire.FIELD_LIGNEINV_PROCODE+"=lin."+dbKD545StockTheoSrv.FIELD_LIGNEINV_PROCODE
					+" and inv."+dbKD543LinInventaire.fld_kd_dat_type+"="+  dbKD543LinInventaire.KD_TYPE
					+ " where "   + FIELD_PRO_ACTIF_CDE + "<>'N' FILTREFAM FILTREDEJAINV "  + queryadd
					+" order by "+FIELD_PRO_CODART;

			if (pasvu)
			{
				query=query.replace("FILTREFAM", "");
				query=query.replace("FILTREDEJAINV", " and  (inv."+dbKD543LinInventaire.FIELD_LIGNEINV_QTE+"  is null or inv."+dbKD543LinInventaire.FIELD_LIGNEINV_QTE+" ='' ) " );
			}
			else if (dejavu)
			{
				query=query.replace("FILTREFAM", "");
				query=query.replace("FILTREDEJAINV", " and  (inv."+dbKD543LinInventaire.FIELD_LIGNEINV_QTE+"  is not null and inv."+dbKD543LinInventaire.FIELD_LIGNEINV_QTE+" <>'' ) " );
			}
			else if (codefam.equals("")==false)
			{
				query=query.replace("FILTREDEJAINV", "");
				query=query.replace("FILTREFAM", " and "+FIELD_PRO_CODFAM+"='"+codefam+"' ");
			}
			Cursor cur = db.conn.rawQuery(query, null);
			while (cur != null && cur.moveToNext()) {
				structArt art = new structArt();

				art=fillStructArt(cur,art);
				art.QTEINV = giveFld(cur, dbKD543LinInventaire.FIELD_LIGNEINV_QTE);
				arts.add(art);
			}
			cur.close();

		} catch (Exception ex) {
			Log.e("TAG",ex.getLocalizedMessage());
		}

		return arts;
	}
	public ArrayList<structArt> getProduitsEchangeStock(boolean dejavu,boolean pasvu,String codefam) {
		ArrayList<structArt> arts = new ArrayList<dbSiteProduit.structArt>();

		try {

			String query = "";
			query = "select pro.*,"
					+ " inv."+dbKD546EchangeStock.FIELD_QTE
					+ " FROM " + TABLENAME + " as pro "
					+ " inner JOIN " + dbKD545StockTheoSrv.TABLENAME
					+ " as lin " + " on "
					+ " lin."+Global.dbKDLinInv.FIELD_LIGNEINV_PROCODE + "="
					+ this.FIELD_PRO_CODART + " and "
					+  " lin."+dbKD545StockTheoSrv.fld_kd_dat_type + "="
					+  dbKD545StockTheoSrv.KD_TYPE 
					+" left join "+dbKD546EchangeStock.TABLENAME +" inv on "
					+" inv."+dbKD546EchangeStock.FIELD_PROCODE+"=lin."+dbKD545StockTheoSrv.FIELD_LIGNEINV_PROCODE
					+" and inv."+dbKD546EchangeStock.fld_kd_dat_type+"="+  dbKD546EchangeStock.KD_TYPE
					+ " where "   + FIELD_PRO_ACTIF_CDE + "<>'N' FILTREFAM FILTREDEJAINV " 
					+" order by "+FIELD_PRO_CODART;

			if (pasvu)
			{
				query=query.replace("FILTREFAM", "");
				query=query.replace("FILTREDEJAINV", " and  (inv."+dbKD546EchangeStock.FIELD_QTE+"  is null or inv."+dbKD543LinInventaire.FIELD_LIGNEINV_QTE+" ='' ) " );
			}
			else if (dejavu)
			{
				query=query.replace("FILTREFAM", "");
				query=query.replace("FILTREDEJAINV", " and  (inv."+dbKD546EchangeStock.FIELD_QTE+"  is not null and inv."+dbKD543LinInventaire.FIELD_LIGNEINV_QTE+" <>'' ) " );
			}
			else if (codefam.equals("")==false)
			{
				query=query.replace("FILTREDEJAINV", "");
				query=query.replace("FILTREFAM", " and "+FIELD_PRO_CODFAM+"='"+codefam+"' ");
			}
			Cursor cur = db.conn.rawQuery(query, null);
			while (cur != null && cur.moveToNext()) {
				structArt art = new structArt();

				art=fillStructArt(cur,art);
				art.QTEINV = giveFld(cur, dbKD543LinInventaire.FIELD_LIGNEINV_QTE);
				arts.add(art);
			}
			cur.close();

		} catch (Exception ex) {
			Log.e("TAG",ex.getLocalizedMessage());
		}

		return arts;
	}
	public ArrayList<structArt> getProduitsStock( String codefam) {
		ArrayList<structArt> arts = new ArrayList<dbSiteProduit.structArt>();

		try {

			String query = "";
			query = "select pro.*,"
					+ " lin."+dbKD545StockTheoSrv.FIELD_LIGNEINV_QTETHEO
					+ " FROM " + TABLENAME + " as pro "
					+ " inner JOIN " + dbKD545StockTheoSrv.TABLENAME
					+ " as lin " + " on "
					+ " lin."+dbKD545StockTheoSrv.FIELD_LIGNEINV_PROCODE + "="
					+ this.FIELD_PRO_CODART + " and "
					+  " lin."+dbKD545StockTheoSrv.fld_kd_dat_type + "="
					+  dbKD545StockTheoSrv.KD_TYPE 
					 
					+ " where "   + FIELD_PRO_ACTIF_CDE + "<>'N' FILTREFAM   " 
					+" order by "+FIELD_PRO_CODART;

			if (codefam.equals("")==false)
			{
				 
				query=query.replace("FILTREFAM", " and "+FIELD_PRO_CODFAM+"='"+codefam+"' ");
			}
			else
				query=query.replace("FILTREFAM", "");
			
			Cursor cur = db.conn.rawQuery(query, null);
			while (cur != null && cur.moveToNext()) {
				structArt art = new structArt();

				art=fillStructArt(cur,art);
				art.QTEINV = giveFld(cur, dbKD545StockTheoSrv.FIELD_LIGNEINV_QTETHEO);
				arts.add(art);
			}
			cur.close();

		} catch (Exception ex) {
			Log.e("TAG",ex.getLocalizedMessage());
		}

		return arts;
	}
	 
	/**
	 * retourne une liste de produits repondants aux criteres
	 * 
	 * @param soc_code
	 * @param fldcritere
	 * @param critere
	 * @return
	 */
	public ArrayList<structArt> getProduits(String soc_code, String codefam,
			String filtre) {
		ArrayList<structArt> arts = new ArrayList<dbSiteProduit.structArt>();

		try {
			String queryadd = "";
			if (codefam.equals("") == false)
				queryadd = " AND " + FIELD_PRO_CODFAM + "='" + codefam + "' ";
			if (filtre.equals("") == false) {
				queryadd = " AND (" + FIELD_PRO_CODART + " like '%" + filtre
						+ "%' OR " + FIELD_PRO_NOMART1 + " like '%" + filtre
						+ "%' ) ";
			}

			String query = "";
			query = "select * FROM " + TABLENAME + " where "
					+ FIELD_PRO_SOC_CODE + "='" + soc_code + "'" + queryadd
					+ " and " + FIELD_PRO_ACTIF_CDE + "<>'N'" + " order by "
					+ FIELD_PRO_NOCASE;

			Cursor cur = db.conn.rawQuery(query, null);
			while (cur != null && cur.moveToNext()) {
				structArt art = new structArt();
				art=fillStructArt(cur,art);
				
				arts.add(art);
			}
			cur.close();

		} catch (Exception ex) {

		}

		return arts;
	}

	public ArrayList<structArt> getProduitsWithHisto(String soc_code, String codecli, String codefam,
			String filtre) {
		ArrayList<structArt> arts = new ArrayList<dbSiteProduit.structArt>();

		try {
			String queryadd = "";
			if (codefam.equals("") == false && !codefam.equals("Gamme habituelle"))
				queryadd = " AND " + FIELD_PRO_CODFAM + "='" + codefam + "' ";
			if (filtre.equals("") == false) {
				queryadd = " AND (" + FIELD_PRO_CODART + " like '%" + filtre
						+ "%' OR " + FIELD_PRO_NOMART1 + " like '%" + filtre
						+ "%' ) ";
			}

			String query = "";
			query = "select "+
					  "art."+FIELD_PRO_CODART+","+
					  "art."+FIELD_PRO_PCB+","+
					  "art."+FIELD_PRO_CODETVA+","+
					  "art."+FIELD_PRO_PHOTONAME+","+
					  "art."+FIELD_PRO_PCB+","+
					  "art."+FIELD_PRO_NOMART1+","+
					  "max(hdr."+dbKD729HistoDocuments.FIELD_DATEDOC+") maxdate "+
					
					" FROM " + TABLENAME + " art "+
					" left join "+dbKD731HistoDocumentsLignes.TABLENAME_HISTO+" lin "+
					" on lin."+dbKD731HistoDocumentsLignes.fld_kd_dat_type+"="+dbKD731HistoDocumentsLignes.KD_TYPE+
					" and art."+FIELD_PRO_CODART+"=lin."+dbKD731HistoDocumentsLignes.FIELD_CODEART+
					" and lin."+dbKD731HistoDocumentsLignes.FIELD_CODECLIENT+"='"+codecli+"' "+
					" left join "+dbKD729HistoDocuments.TABLENAME_HISTO+" hdr "+
					" on hdr."+dbKD729HistoDocuments.fld_kd_dat_type+"="+dbKD729HistoDocuments.KD_TYPE+
					" and lin."+dbKD731HistoDocumentsLignes.FIELD_NUMDOC+"=hdr."+dbKD729HistoDocuments.FIELD_NUMDOC+
					" and hdr."+dbKD729HistoDocuments.FIELD_TYPEDOC+"='"+TableSouches.TYPEDOC_FACTURE+"'"
					+" where "
					+ " art."+FIELD_PRO_SOC_CODE + "='" + soc_code + "'" + queryadd
					+ " and " + FIELD_PRO_ACTIF_CDE + "<>'N'" +
					" group by "+
					 "art."+FIELD_PRO_CODART+","+
					  "art."+FIELD_PRO_PCB+","+
					  "art."+FIELD_PRO_CODETVA+","+
					  "art."+FIELD_PRO_PHOTONAME+","+
					  "art."+FIELD_PRO_NOMART1+" "+
					" ORDER BY CAST("+"art."+FIELD_PRO_EMPLAC+" "+" as Integer) ";

			if (queryadd.equals("")  )
			{
				if(codefam.equals("Gamme habituelle") || codefam.equals(""))
				{
					
				}else
				query=query.replace("left join", "inner join");
			}
			
			Cursor cur = db.conn.rawQuery(query, null);
			while (cur != null && cur.moveToNext()) {
				structArt art = new structArt();
				art=fillStructArt(cur,art);
				art.MAXDATE =Fonctions.YYYY_MM_DD_to_dd_mm_yyyy( giveFld(cur, "maxdate"));
				arts.add(art);
			}
			cur.close();

		} catch (Exception ex) {
			Log.e("TAG",ex.getLocalizedMessage());
		}

		return arts;
	}

	structArt fillStructArt(Cursor cur,structArt art)
	{
	 
		art.SOC_CODE = giveFld(cur, FIELD_PRO_SOC_CODE);
		art.CODART = giveFld(cur, FIELD_PRO_CODART);
		art.CODFAM = giveFld(cur, FIELD_PRO_CODFAM);
		art.CODMARQUE = giveFld(cur, FIELD_PRO_CODMARQUE);
		art.CODGRP = giveFld(cur, FIELD_PRO_CODGRP);
		art.CODSGR = giveFld(cur, FIELD_PRO_CODESGR);
		art.NOMART1 = giveFld(cur, FIELD_PRO_NOMART1);
		art.NOMART2 = giveFld(cur, FIELD_PRO_NOMART2);
		art.PV_CONS = giveFld(cur, FIELD_PRO_PV_CONS);
		art.TARIF = giveFld(cur, FIELD_PRO_TARIF);
		art.TARIFMIN = giveFld(cur, FIELD_PRO_TARIFMIN);
		art.TARIFMAX = giveFld(cur, FIELD_PRO_TARIFMAX);
		art.CODTHE = giveFld(cur, FIELD_PRO_CODETHE);
		art.CODMOD = giveFld(cur, FIELD_PRO_CODEMOD);
		art.CODELIN = giveFld(cur, FIELD_PRO_CODELIN);
		art.EAN = giveFld(cur, FIELD_PRO_EAN);
		art.ETATSUIVI = giveFld(cur, FIELD_PRO_ETATSUIVI);
		art.PCB = giveFld(cur, FIELD_PRO_PCB);
		art.EAN14 = giveFld(cur, FIELD_PRO_EAN14);
		art.CONC = giveFld(cur, FIELD_PRO_CONC);
		art.DIM = giveFld(cur, FIELD_PRO_DIM);
		art.RAYON = giveFld(cur, FIELD_PRO_RAYON);
		art.UNIVERS = giveFld(cur, FIELD_PRO_UNIVERS);
		art.AFFLIBRE1 = giveFld(cur, FIELD_PRO_AFFLIBRE1);
		art.AFFLIBRE2 = giveFld(cur, FIELD_PRO_AFFLIBRE2);
		art.QTEMAX = giveFld(cur, FIELD_PRO_QTEMAX);
		art.UNPOIDS = giveFld(cur, FIELD_PRO_UNPOIDS);
		art.POIDSBRUT = giveFld(cur, FIELD_PRO_POIDSBRUT);
		art.POIDSNET = giveFld(cur, FIELD_PRO_POIDSNET);
		art.UNVENTE = giveFld(cur, FIELD_PRO_UNVENTE);
		art.SUISSTK = giveFld(cur, FIELD_PRO_SUISSTK);
		art.NOMENC = giveFld(cur, FIELD_PRO_NOMENC);
		art.EMPLAC = giveFld(cur, FIELD_PRO_EMPLAC);
		art.QTEMINI = giveFld(cur, FIELD_PRO_QTEMINI);
		art.TAILLE = giveFld(cur, FIELD_PRO_TAILLE);
		art.COULEUR = giveFld(cur, FIELD_PRO_COULEUR);
		art.NOCASE = giveFld(cur, FIELD_PRO_NOCASE);
		art.ACTIF_CDE = giveFld(cur, FIELD_PRO_ACTIF_CDE);
		art.LBL_GENERIC = giveFld(cur, FIELD_PRO_LBL_GENERIC);
		art.RACCOURCI = giveFld(cur, FIELD_PRO_RACCOURCI);
		art.DATEDISPO = giveFld(cur, FIELD_PRO_DATEDISPO);
		art.PHOTONAME = giveFld(cur, FIELD_PRO_PHOTONAME);
		art.FAMTARIF = giveFld(cur, FIELD_PRO_FAMTARIF);
		art.FAMREMISE = giveFld(cur, FIELD_PRO_FAMREMISE);
		art.CATALOGUE_AUTO = giveFld(cur, FIELD_PRO_CATALOGUE_AUTO);
		art.FAMART7 = giveFld(cur, FIELD_PRO_FAMART7); // Createur
		art.SITUATION = giveFld(cur, FIELD_PRO_SITUATION);
		art.DLC = giveFld(cur, FIELD_PRO_DLC);
		art.PRODUITNOUVEAU = giveFld(cur, FIELD_PRO_PRODUITNOUVEAU);
		art.PRODUITENPROMO = giveFld(cur, FIELD_PRO_PRODUITENPROMO);
		art.PRODUITADEGAGER = giveFld(cur, FIELD_PRO_PRODUITADEGAGER);

		// art.ENSEIGNE =giveFld(cur,FIELD_PRO_ENSEIGNE );
		art.CMT1ACTIF = giveFld(cur, this.FIELD_PRO_COMMENT1ACTIF);
		art.CMT1MINUS = giveFld(cur, this.FIELD_PRO_COMMENT1MINUSONLY);
		art.CMT2ACTIF = giveFld(cur, this.FIELD_PRO_COMMENT2ACTIF);

		art.CMT1LNG = giveFld(cur, this.FIELD_PRO_COMMENT1LNG);
		art.CMT2LNG = giveFld(cur, this.FIELD_PRO_COMMENT2LNG);
		art.CODEPANACH = giveFld(cur, this.FIELD_PRO_CODEPANACHAGE);
		art.HAUTEUR = giveFld(cur, this.FIELD_PRO_HAUTEUR);
		art.LARGEUR = giveFld(cur, this.FIELD_PRO_LARGEUR);
		art.LONGUEUR = giveFld(cur, this.FIELD_PRO_LONGUEUR);
		art.WEBFICHE = giveFld(cur, this.FIELD_PRO_WEBFICHE);
		art.CODETVA = giveFld(cur, this.FIELD_PRO_CODETVA);
		art.ISGRAT = giveFld(cur, this.FIELD_PRO_ISGRAT);
		
		return art;
	}
	public String CalculLine(float qte, int pcb, double dPrix, double dRemise) {
		return Fonctions.GetDoubleToStringFormatDanem(
				CalculLineDouble(qte * pcb, dPrix, dRemise), "0.00");

	}

	public String CalculLine(float qte, double dPrix, double dRemise) {
		return Fonctions.GetDoubleToStringFormatDanem(
				CalculLineDouble(qte, dPrix, dRemise), "0.00");

	}

	public double CalculLineDouble(float qte, double dPrix, double dRemise) {
		double dprixInt = dPrix;
		if (qte > 0) {
			// DT version 3.61 round 2
			dprixInt = dPrix
					- Fonctions.round(Calculpourcent(dPrix, dRemise), 2);
			 dprixInt=dprixInt*qte;
			// dprixInt=dprixInt-dRemise;

		} else {
			dprixInt = dPrix * qte;

		}

		return dprixInt;

	}

	public double Calculpourcent(double dPrix, double dRemise) {
		double dResult = (dPrix * dRemise) / 100;

		return dResult;
	}
	public String getLibelle(String code)
	{	
		String libelle="";
		
		try
		{
			String query="";
			query="select * FROM "+
					TABLENAME+
					" where "+
					dbSiteProduit.FIELD_PRO_CODART+
					"='"+code+"'";


			Cursor cur=db.conn.rawQuery(query, null);
			if (cur.moveToNext())
			{
				libelle=giveFld(cur,FIELD_PRO_NOMART1           );
				
			}
			if (cur != null )
				cur.close();

		}
		catch(Exception ex)
		{
			libelle="";
		}

		return libelle;
	}
	public String getUniteVente(String code)
	{	
		String libelle="";
		
		try
		{
			String query="";
			query="select * FROM "+
					TABLENAME+
					" where "+
					dbSiteProduit.FIELD_PRO_CODART+
					"='"+code+"'";


			Cursor cur=db.conn.rawQuery(query, null);
			if (cur.moveToNext())
			{
				libelle=giveFld(cur,FIELD_PRO_UNVENTE           );
				
			}
			if (cur != null )
				cur.close();

		}
		catch(Exception ex)
		{
			libelle="";
		}

		return libelle;
	}
	public String getTaux(String code)
	{	
		String libelle="";
		
		try
		{
			String query="";
			query="select * FROM "+
					TABLENAME+
					" where "+
					dbSiteProduit.FIELD_PRO_CODART+
					"='"+code+"'";


			Cursor cur=db.conn.rawQuery(query, null);
			if (cur.moveToNext())
			{
				libelle=giveFld(cur,FIELD_PRO_CODETVA           );
				
			}
			if (cur != null )
				cur.close();

		}
		catch(Exception ex)
		{
			libelle="";
		}

		return libelle;
	}
	
	

}
