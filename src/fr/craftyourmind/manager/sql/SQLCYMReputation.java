package fr.craftyourmind.manager.sql;

import java.sql.SQLException;
import java.sql.Statement;

import fr.craftyourmind.manager.CYMReputation;

public class SQLCYMReputation extends AbsSQL{

	private CYMReputation rep;
	
	public SQLCYMReputation(int action, CYMReputation rep) {
		super(action);
		this.rep = rep;
	}

	@Override
	protected AbsSQL initID() {
		rep.id = autoReputationID++; return this;
	}

	@Override
	protected void updateID() throws SQLException {
		autoReputationID = getId(T_REPUTATION);
	}

	@Override
	protected void create() throws SQLException {
		rep.id = getId(T_REPUTATION);
		String sql = updateRow.init(T_REPUTATION).add("id", rep.id).add("name", rep.name).add("descriptive", rep.descriptive).sqlInsertInto();
		create(T_CLAN, rep.name, sql);
	}

	@Override
	protected void save() throws SQLException {
		Statement state = cnx.createStatement();
		state.executeUpdate(updateRow.init(T_REPUTATION).add("name", rep.name).add("descriptive", rep.descriptive).sqlWhere("id", rep.id).sqlUpdate());
	}

	@Override
	protected void delete() throws SQLException {
		Statement state = cnx.createStatement();
		state.executeUpdate(updateRow.init(T_REPUTATION).sqlWhere("id", rep.id).sqlDelete());
		state.executeUpdate(updateRow.init(T_REPUTEPOINT).sqlWhere("idRepute", rep.id).sqlDelete());
		state.executeUpdate(updateRow.init(T_REPUTECLAN).sqlWhere("idRepute", rep.id).sqlDelete());
	}

}
