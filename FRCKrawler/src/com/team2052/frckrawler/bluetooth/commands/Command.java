package com.team2052.frckrawler.bluetooth.commands;

import java.io.Serializable;

import com.team2052.frckrawler.database.DBManager;

public abstract class Command implements Serializable {
	
	public abstract boolean execute(DBManager db);
}
