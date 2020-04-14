package fr.craftyourmind.manager.sql;

import java.sql.SQLException;
import java.sql.Statement;

import fr.craftyourmind.manager.CYMPlayer;
import fr.craftyourmind.manager.CYMReputation;

public class SQLCYMPlayer extends AbsSQL{

	private CYMPlayer mp;
	private CYMReputation repute;
	
	public SQLCYMPlayer(int action, CYMPlayer mp) {
		super(action);
		this.mp = mp;
	}
	
	public SQLCYMPlayer(int action, CYMPlayer mp, CYMReputation repute) {
		super(action);
		this.mp = mp;
		this.repute = repute;
	}
	
	@Override
	protected void runNext() throws SQLException {
		if(action == CREATEREPUTE) createRepute();
		else if(action == UPDATEREPUTE) updateRepute();
	}

	@Override
	protected AbsSQL initID() {
		mp.id = autoPlayerID++; return this;
	}
	
	@Override
	protected void updateID() throws SQLException {
		autoPlayerID = getId(T_PLAYER);
	}
	
	@Override
	protected void create() throws SQLException {
		mp.id = getId(T_PLAYER);
		String sql = updateRow.init(T_PLAYER).add("id", mp.id).add("name", mp.name).add("level", mp.level).add("clan", ((mp.getClan() == null)?0:mp.getClan().id)).sqlInsertInto();
		create(T_PLAYER, mp.name, sql);
	}

	@Override
	protected void save() throws SQLException{
		Statement state = cnx.createStatement();
		state.executeUpdate(updateRow.init(T_PLAYER).add("name", mp.name).add("level", mp.level).add("clan", ((mp.getClan() == null)?0:mp.getClan().id)).sqlWhere("id", mp.id).sqlUpdate());
	}
	
	@Override
	protected void delete() throws SQLException {
		Statement state = cnx.createStatement();
		state.executeUpdate(updateRow.init(T_PLAYER).sqlWhere("id", mp.id).sqlDelete());
	}
	
	private void createRepute() throws SQLException{
		Statement state = cnx.createStatement();
		state.executeUpdate(updateRow.init(T_REPUTEPOINT).add("idRepute", repute.id).add("idPlayer", mp.id).add("points", mp.getReputePts(repute)).add("param", mp.getReputeParam(repute)).sqlInsertInto());
	}
	
	private void updateRepute() throws SQLException{
		Statement state = cnx.createStatement();
		state.executeUpdate(updateRow.init(T_REPUTEPOINT).add("points", mp.getReputePts(repute)).add("param", mp.getReputeParam(repute)).sqlWhere("idRepute", repute.id).sqlWhere("idPlayer", mp.id).sqlUpdate());
	}
	
}
