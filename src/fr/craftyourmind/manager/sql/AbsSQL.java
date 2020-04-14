package fr.craftyourmind.manager.sql;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import fr.craftyourmind.manager.Plugin;
import fr.craftyourmind.manager.sql.AbsSQLCYMCnx.AbsSQLUpdateRow;

public abstract class AbsSQL {

	static final int CREATE = 0;
	static final int SAVE = 1;
	static final int DELETE = 2;
	static final int CREATEREPUTE = 3;
	static final int UPDATEREPUTE = 4;
	static final int DELETEREPUTE = 5;
	
	static public String prefix;
	static public String T_CLAN;
	static public String T_PLAYER;
	static public String T_NPC;
	static public String T_REPUTATION;
	static public String T_REPUTEPOINT;
	static public String T_REPUTECLAN;
	
	public static int autoClanID;
	public static int autoPlayerID;
	public static int autoNpcID;
	public static int autoReputationID;
	
	protected AbsSQLUpdateRow updateRow;
	protected int action;
	protected int compteur = 0;
	protected Connection cnx;
	
	protected boolean errorCreate = false;
	
	public AbsSQL(int action) {
		this.action = action;
	}
	
	public static void init(Connection cnx) throws SQLException{
		autoClanID = getId(cnx, T_CLAN);
		autoPlayerID = getId(cnx, T_PLAYER);
		autoNpcID = getId(cnx, T_NPC);
	}
	
	protected void go(){ // MAJ Async
		SQLCYMManager.addQuery(this);
	}
	
	public void run() throws SQLException {
		try {
			this.updateRow = SQLCYMManager.minicnx.getUpdateRow();
			if(action == CREATE){ create(); if(errorCreate) updateID();}
			else if(action == SAVE) save();
			else if(action == DELETE) delete();
			else runNext();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	protected void runNext() throws SQLException {}
	
	protected static int getId(Connection cnx, String table) throws SQLException{
		Statement state = cnx.createStatement();
		return SQLCYMManager.minicnx.getAutoIncrement(state, table);
	}
	
	protected int getId(String table) throws SQLException{
		return getId(cnx, table);
	}
	
	protected void create(String table, String name, String sql) throws SQLException {
		Statement state = cnx.createStatement();
		try{
			state.executeUpdate(sql);
		} catch (SQLException e) {
			if(compteur < 10 && updateRow.isErrorPrimaryKey(e)){
				Plugin.log("Error id "+table+" "+name+" : "+compteur);
				compteur++;
				errorCreate = true;
				create();
			}else{
				Plugin.log("Error create "+table+" "+name+" : "+e.getErrorCode());
				compteur = 0;
			}
		}
	}
	
	protected abstract AbsSQL initID();
	protected abstract void updateID() throws SQLException;
	protected abstract void create()throws SQLException;
	protected abstract void save()throws SQLException;
	protected abstract void delete()throws SQLException;
	
}
