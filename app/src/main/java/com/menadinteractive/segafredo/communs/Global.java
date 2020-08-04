package com.menadinteractive.segafredo.communs;

import com.menadinteractive.segafredo.carto.ClientItem;
import com.menadinteractive.segafredo.db.TableClient;
import com.menadinteractive.segafredo.db.TableContactcli;
import com.menadinteractive.segafredo.db.TableFermeture;
import com.menadinteractive.segafredo.db.TableHoraire;
import com.menadinteractive.segafredo.db.TableListeMateriel;
import com.menadinteractive.segafredo.db.TableMaterielClient;
import com.menadinteractive.segafredo.db.TableStock;
import com.menadinteractive.segafredo.db.TableTarif;
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
import com.menadinteractive.segafredo.db.dbSiteProduit;
import com.menadinteractive.segafredo.db.dbSociete;

public class Global {
	public static int curver;
	
	static public long maxMediaFileSize=715000;//taille max du fichier photo transferable au serveur

	static public TableClient dbClient;
	static public TableContactcli dbContactcli;
	static public TableTarif dbTarif;
	static public TableMaterielClient dbMaterielClient;
	static public TableListeMateriel dbListeMateriel;
	
	static public dbKD83EntCde dbKDEntCde;
	static public dbKD451RetourMachineclient dbKDRetourMachineClient;
	static public dbKD452ComptageMachineclient dbKDComptageMachineClient;
	
	
	
	static public dbKD84LinCde dbKDLinCde;
	static public dbKD98Encaissement dbKDEncaissement;
	static public dbKD99EncaisserFacture dbKDEncaisserFacture;
	static public dbKD543LinInventaire dbKDLinInv;
 
	
	static public dbSiteProduit dbProduit;
	static public TableHoraire dbHoraire;
	static public TableFermeture dbFermeture;
	static public dbParam dbParam;
	static public dbSociete dbSoc;
	static public dbKD71User dbKDIdent;
	static public TableStock dbStock;
	static public dbKD100Visite dbKDVisite;
	static public dbKD101ClientVu dbKDClientVu;
	static public dbKD102Agenda dbKDAgenda;

	static public dbKD91EntPlan dbKDEntPlan;
	static public dbKD92LinPlan dbKDLinPlan;
	static public dbKD93PlanFiller dbKDFillPlan;
	static public dbKD729HistoDocuments dbKDHistoDocuments;
	static public dbKD731HistoDocumentsLignes dbKDHistoLigneDocuments;
	
	static public dbKD730FacturesDues dbKDFacturesDues;
	
	static public dbKD981RetourBanqueEnt dbKDRetourBanqueEnt;
	static public dbKD982RetourBanqueLin dbKDRetourBanqueLin;
	
	static public String lastLinCommentSaved="";//dernier commentaire sauvegardé, pour garder un meme commenre ligne entre 2 saisies
	static public String lastErrorMessage;
	//static public String livreuractif;
	
//	static public structClient AXE_Client;
//	static public dbKD71User.structIdentifiant AXE_Ident;

	static public dbLog dbLog;
	public static ClientItem clientSelectedOnMap = null;
	
	static public String version;
	
	static public String CODCLI_TEMP;
	
 
	
	static public String FONT_REGULAR="fonts/opensansregular.ttf";
	static public String FONT_BOLD="fonts/opensansbold.ttf";
	static public String FONT_PRINT="fonts/couriernewregular.ttf";
	
}
