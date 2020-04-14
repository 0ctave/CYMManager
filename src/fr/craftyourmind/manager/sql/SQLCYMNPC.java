package fr.craftyourmind.manager.sql;

import java.sql.SQLException;
import java.sql.Statement;

import fr.craftyourmind.manager.CYMNPC;
import fr.craftyourmind.manager.Plugin;

public class SQLCYMNPC extends AbsSQL{

	private CYMNPC npc;
	
	public SQLCYMNPC(int action, CYMNPC npc) {
		super(action);
		this.npc = npc;
	}
	
	@Override
	protected AbsSQL initID() {
		npc.id = autoNpcID++; return this;
	}
	
	@Override
	protected void updateID() throws SQLException {
		autoNpcID = getId(T_NPC);
	}
	
	@Override
	protected void create() throws SQLException {
		try{
		Statement state = cnx.createStatement();
		String sql = updateRow.init(T_NPC).add("id", npc.id).add("name", npc.name).add("urlSkin", npc.urlSkin).add("isBlock", npc.isBlock).add("world", ((npc.world == null)?"":npc.world.getName())).add("x", npc.x).add("y", npc.y).add("z", npc.z).add("text", npc.text).add("distanceDrawIcon", npc.distanceDrawIcon).sqlInsertInto();
		state.executeUpdate(sql);
		} catch (Exception e) {
			Plugin.log("Error create npc id:"+npc.id);
		}
	}

	@Override
	protected void save() throws SQLException {
		Statement state = cnx.createStatement();
		String sql = updateRow.init(T_NPC).add("name", npc.name).add("urlSkin", npc.urlSkin).add("isBlock", npc.isBlock).add("world", ((npc.world == null)?"":npc.world.getName())).add("x", npc.x).add("y", npc.y).add("z", npc.z).add("text", npc.text).add("distanceDrawIcon", npc.distanceDrawIcon).sqlWhere("id", npc.id).sqlUpdate();
		state.executeUpdate(sql);
	}

	@Override
	protected void delete() throws SQLException {
		Statement state = cnx.createStatement();
		state.executeUpdate(updateRow.init(T_NPC).sqlWhere("id", npc.id).sqlDelete());
	}

}
