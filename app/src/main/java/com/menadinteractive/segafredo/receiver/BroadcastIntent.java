package com.menadinteractive.segafredo.receiver;

import com.menadinteractive.segafredo.communs.Global;
import com.menadinteractive.segafredo.db.TableClient;
import com.menadinteractive.segafredo.db.TableClient.structClient;

import android.content.Context;
import android.content.Intent;

public class BroadcastIntent {
	public static void setCodeAlarm(Context context, String codeCli, String codeAlarm){
		TableClient.structClient cli=new structClient();
		
		Global.dbClient.getClient(codeCli, cli, new StringBuilder());
		cli.STATUT=codeAlarm;
		
		Global.dbClient.save(cli, true);
	}
}
