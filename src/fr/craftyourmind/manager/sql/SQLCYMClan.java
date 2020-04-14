package fr.craftyourmind.manager.sql;

import java.sql.SQLException;
import java.sql.Statement;

import fr.craftyourmind.manager.CYMClan;
import fr.craftyourmind.manager.CYMReputation;

public class SQLCYMClan extends AbsSQL{
	
	private CYMClan mc;
	private CYMReputation repute;

	public SQLCYMClan(int action, CYMClan mc) {
		super(action);
		this.mc = mc;
	}
	
	public SQLCYMClan(int action, CYMClan mc, CYMReputation repute) {
		super(action);
		this.mc = mc;
		this.repute = repute;
	}


	@Override
	protected void runNext() throws SQLException {
		if(action == CREATEREPUTE) createRepute();
		else if(action == UPDATEREPUTE) updateRepute();
		else if(action == DELETEREPUTE) deleteRepute();
	}

	@Override
	protected AbsSQL initID() {
		mc.id = autoClanID++; return this;
	}
	
	@Override
	protected void updateID() throws SQLException {
		autoClanID = getId(T_CLAN);
	}
	
	@Override
	protected void create() throws SQLException {
		mc.id = getId(T_CLAN);
		String sql = updateRow.init(T_CLAN).add("id", mc.id).add("name", mc.name).add("archi", mc.archi).add("dateCreation", mc.dateCreation).add("penalty", mc.penalty).add("chef", ((mc.chef == null)?0:mc.chef.id)).sqlInsertInto();
		create(T_CLAN, mc.name, sql);
	}

	@Override
	protected void save() throws SQLException {
		Statement state = cnx.createStatement();
		String sql = updateRow.init(T_CLAN).add("name", mc.name).add("archi", mc.archi).add("dateCreation", mc.dateCreation).add("penalty", mc.penalty).add("chef", ((mc.chef == null)?0:mc.chef.id)).sqlWhere("id", mc.id).sqlUpdate();
		state.executeUpdate(sql);
	}

	@Override
	protected void delete() throws SQLException {
		Statement state = cnx.createStatement();
		state.executeUpdate(updateRow.init(T_CLAN).sqlWhere("id", mc.id).sqlDelete());
	}

	private void createRepute() throws SQLException{
		Statement state = cnx.createStatement();
		state.executeUpdate(updateRow.init(T_REPUTECLAN).add("idRepute", repute.id).add("idClan", mc.id).add("points", mc.getReputePts(repute)).add("param", mc.getReputeParam(repute)).sqlInsertInto());
	}
	
	private void updateRepute() throws SQLException{
		Statement state = cnx.createStatement();
		state.executeUpdate(updateRow.init(T_REPUTECLAN).add("points", mc.getReputePts(repute)).add("param", mc.getReputeParam(repute)).sqlWhere("idRepute", repute.id).sqlWhere("idClan", mc.id).sqlUpdate());
	}
	
	private void deleteRepute() throws SQLException{
		Statement state = cnx.createStatement();
		state.executeUpdate(updateRow.init(T_REPUTECLAN).sqlWhere("idRepute", repute.id).sqlWhere("idClan", mc.id).sqlDelete());
	}
}
