package com.menadinteractive.maxpoilane;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteException;

import com.menadinteractive.segafredo.communs.Global;
import com.menadinteractive.segafredo.db.MyDB;
import com.menadinteractive.segafredo.db.TableClient;
import com.menadinteractive.segafredo.db.TableContactcli;
import com.menadinteractive.segafredo.db.TableFermeture;
import com.menadinteractive.segafredo.db.TableHoraire;
import com.menadinteractive.segafredo.db.TableListeMateriel;
import com.menadinteractive.segafredo.db.TableMaterielClient;
import com.menadinteractive.segafredo.db.TableStatSyntheseArticlesHebdo;
import com.menadinteractive.segafredo.db.TableStock;
import com.menadinteractive.segafredo.db.TableSuiviStockHebdo;
import com.menadinteractive.segafredo.db.TableTarif;
import com.menadinteractive.segafredo.db.dbHistoFactureEnt;
import com.menadinteractive.segafredo.db.dbKD;
import com.menadinteractive.segafredo.db.dbKD100Visite;
import com.menadinteractive.segafredo.db.dbKD101ClientVu;
import com.menadinteractive.segafredo.db.dbKD102Agenda;
import com.menadinteractive.segafredo.db.dbKD451RetourMachineclient;
import com.menadinteractive.segafredo.db.dbKD452ComptageMachineclient;
import com.menadinteractive.segafredo.db.dbKD543LinInventaire;
import com.menadinteractive.segafredo.db.dbKD71User;
import com.menadinteractive.segafredo.db.dbKD729HistoDocuments;
import com.menadinteractive.segafredo.db.dbKD730FacturesDues;
import com.menadinteractive.segafredo.db.dbKD731HistoDocumentsLignes;
import com.menadinteractive.segafredo.db.dbKD83EntCde;
import com.menadinteractive.segafredo.db.dbKD84LinCde;
import com.menadinteractive.segafredo.db.dbKD91EntPlan;
import com.menadinteractive.segafredo.db.dbKD92LinPlan;
import com.menadinteractive.segafredo.db.dbKD93PlanFiller;
import com.menadinteractive.segafredo.db.dbKD981RetourBanqueEnt;
import com.menadinteractive.segafredo.db.dbKD982RetourBanqueLin;
import com.menadinteractive.segafredo.db.dbKD98Encaissement;
import com.menadinteractive.segafredo.db.dbKD99EncaisserFacture;
import com.menadinteractive.segafredo.db.dbLog;
import com.menadinteractive.segafredo.db.dbParam;
import com.menadinteractive.segafredo.db.dbSiteListeLogin;
import com.menadinteractive.segafredo.db.dbSiteProduit;
import com.menadinteractive.segafredo.db.dbSociete;

public class app extends Application{

	public MyDB  m_db;
	private static Context context;

	@Override 
	public final void onCreate()
	{
		super.onCreate();
		Debug.setDebug(true);
		openDB();
		context=getApplicationContext();
		
		Global.dbKDEntCde = new dbKD83EntCde(m_db);
		Global.dbKDLinCde = new dbKD84LinCde(m_db);
		Global.dbKDLinInv = new dbKD543LinInventaire (m_db);
		 
		Global.dbProduit = new dbSiteProduit(m_db);
		Global.dbClient = new TableClient(m_db);
		Global.dbContactcli = new TableContactcli(m_db);
		Global.dbTarif = new TableTarif(m_db);
		Global.dbMaterielClient = new TableMaterielClient(m_db);
		Global.dbListeMateriel = new TableListeMateriel(m_db);
		Global.dbHoraire = new TableHoraire(m_db);
		Global.dbFermeture = new TableFermeture(m_db);
		Global.dbParam = new dbParam(m_db);
		Global.dbKDIdent = new dbKD71User(m_db);
		Global.dbStock= new TableStock(m_db);
		Global.dbKDVisite = new dbKD100Visite(m_db);
		Global.dbKDClientVu = new dbKD101ClientVu(m_db);
		Global.dbKDRetourMachineClient = new dbKD451RetourMachineclient(m_db);
		Global.dbKDComptageMachineClient = new dbKD452ComptageMachineclient(m_db);
		Global.dbKDAgenda=new dbKD102Agenda(m_db);
		Global.dbLog=new dbLog(m_db);
		Global.dbSoc=new dbSociete(m_db);
		Global.dbKDEntPlan=new dbKD91EntPlan(m_db);
		Global.dbKDLinPlan=new dbKD92LinPlan(m_db);
		Global.dbKDFillPlan=new dbKD93PlanFiller(m_db);
		Global.dbKDEncaissement=new dbKD98Encaissement(m_db);
		Global.dbKDEncaisserFacture=new dbKD99EncaisserFacture(m_db);
		Global.dbKDHistoDocuments=new dbKD729HistoDocuments(m_db);
		Global.dbKDFacturesDues = new dbKD730FacturesDues(m_db);
		Global.dbKDHistoLigneDocuments=new dbKD731HistoDocumentsLignes(m_db);
		
		Global.dbKDRetourBanqueEnt = new dbKD981RetourBanqueEnt(m_db);
		Global.dbKDRetourBanqueLin = new dbKD982RetourBanqueLin(m_db);
		
 
		createTables();
		createIndex();

		MyDB.copyFile(MyDB.source,MyDB.dest);
		//MyDB.copyFile(MyDB.dest,MyDB.source);
	}

	public void createTables(){
		StringBuilder err=new StringBuilder();
		m_db.execSQL(dbParam.TABLE_CREATE,err);
		m_db.execSQL(dbSiteProduit.TABLE_CREATE,err);
		m_db.execSQL(dbKD.getTableCreateKD(),err);
		m_db.execSQL(dbKD.getTableCreateKDHisto(),err);
		m_db.execSQL(dbKD.getTableCreateKDDuplicata(),err);
		m_db.execSQL(dbKD.getTableCreateKDSave(),err);
		m_db.execSQL(TableClient.TABLE_CREATE,err);
		m_db.execSQL(TableContactcli.TABLE_CREATE,err);
		m_db.execSQL(TableMaterielClient.TABLE_CREATE,err);
		m_db.execSQL(TableTarif.TABLE_CREATE,err);
		m_db.execSQL(TableHoraire.TABLE_CREATE,err);
		m_db.execSQL(TableFermeture.TABLE_CREATE,err);
		m_db.execSQL(TableStock.TABLE_CREATE,err);
		m_db.execSQL(dbLog.TABLE_CREATE,err);
		m_db.execSQL(dbHistoFactureEnt.TABLE_CREATE,err);
		m_db.execSQL(dbSiteListeLogin.TABLE_CREATE,err);
		m_db.execSQL(TableStatSyntheseArticlesHebdo.TABLE_CREATE,err);
		m_db.execSQL(TableSuiviStockHebdo.TABLE_CREATE,err);	
		
		Debug.Log(err.toString());
	}
	
	public void createIndex(){
		StringBuilder err = new StringBuilder();
		//Index tarif
		m_db.execSQL(TableTarif.INDEX_CREATE,err);
		
		//Index client
		m_db.execSQL(TableClient.INDEX_CREATE_CODE_CLI,err);
		m_db.execSQL(TableClient.INDEX_CREATE_CODEVRP,err);
		m_db.execSQL(TableClient.INDEX_CREATE_JOUR_PASSAGE,err);
		m_db.execSQL(TableClient.INDEX_CREATE_NOM_CLI,err);
		m_db.execSQL(TableClient.INDEX_CREATE_ZONE,err);
		
		//Index produit
		m_db.execSQL(dbSiteProduit.INDEX1_CREATE,err);
		m_db.execSQL(dbSiteProduit.INDEX2_CREATE,err);
		m_db.execSQL(dbSiteProduit.INDEX_CODFAM_CREATE,err);
		
		//Index param
		m_db.execSQL(dbParam.INDEX_CREATE_CODEREC,err);
		
		Debug.Log(err.toString());
	}

	boolean openDB()
	{
		m_db=new MyDB(this.getApplicationContext());
		try
		{

			m_db.conn= m_db.getWritableDatabase();
		}
		catch(SQLiteException ex)
		{
			m_db.conn= m_db.getReadableDatabase();
		}
		return true;
	}

	 public static Context getAppContext() {
	        return app.context;
	    }

}
