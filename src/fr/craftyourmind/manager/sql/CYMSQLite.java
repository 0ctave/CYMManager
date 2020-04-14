package fr.craftyourmind.manager.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map.Entry;

public class CYMSQLite extends AbsSQLCYMCnx {

	private String path;
	private String name_bd;
	
	public CYMSQLite(String path, String name_bd) {
		try {
			this.path = path;
			this.name_bd = name_bd;
			Class.forName("org.sqlite.JDBC");
			DriverManager.getConnection("jdbc:sqlite:"+path+name_bd+".sqlite");
		} catch (Exception e) { e.printStackTrace(); }
	}
	@Override
	public Connection getCnx() throws SQLException {
		return DriverManager.getConnection("jdbc:sqlite:"+path+name_bd+".sqlite");
	}
	@Override
	public AbsSQLCYMCnx addInt(String i) { fields.put(i, "INTEGER"); return this; }
	@Override
	public AbsSQLCYMCnx addVarC(String var, int i) { fields.put(var, "VARCHAR"); return this; }
	@Override
	public AbsSQLCYMCnx addLong(String l) { fields.put(l, "INTEGER"); return this; }
	@Override
	public AbsSQLCYMCnx addFloat(String f) { fields.put(f, "FLOAT"); return this; }
	@Override
	public AbsSQLCYMCnx addBool(String b) { fields.put(b, "BOOL"); return this; }
	@Override
	public AbsSQLCYMCnx addTxt(String t) { fields.put(t, "TEXT"); return this; }
	@Override
	public AbsSQLCYMCnx addDefaultValue(String value) { defaultValue = value; return this; }
	@Override
	public String sqlCreate() {
		String sql = "CREATE TABLE IF NOT EXISTS "+table+" (";
		boolean isAutoInc = !ai.isEmpty();
		int size = fields.size();
		int i = 0;
		for(Entry<String, String> entry : fields.entrySet()){
			sql += entry.getKey()+" "+entry.getValue()+" NOT NULL";
			if(isAutoInc && !pks.isEmpty() && pks.contains(entry.getKey())){ sql += " PRIMARY KEY"; pks.remove(entry.getKey());}
			if(!ai.isEmpty() && ai.equals(entry.getKey())){ sql += " AUTOINCREMENT"; ai = "";}
			i++;
			if(i != size) sql += ",";
		}
		if(!isAutoInc && !pks.isEmpty()){
			sql += ", PRIMARY KEY ("+pks.get(0);
			for(int ii = 1 ; ii < pks.size() ; ii++) sql += ", "+pks.get(ii);
			sql += ")";
		}
		sql += ")";
		return sql;
	}
	@Override
	public int getAutoIncrement(Statement state, String table) throws SQLException {
		ResultSet rs = state.executeQuery("SELECT * FROM sqlite_sequence WHERE name = '"+table+"'");
		if(!rs.next()) return 1;
		return rs.getInt("seq")+1;
	}
	@Override
	public String sqlAlterTableAdd() {
		String key = "";
		String value = "";
		for(Entry<String, String> entry : fields.entrySet()){
			key = entry.getKey();
			value = entry.getValue();
		}
		return "ALTER TABLE "+table+" ADD COLUMN "+key+" "+value+" "+((defaultValue.isEmpty())?"":" NOT NULL DEFAULT '"+defaultValue+"'");
	}
	@Override
	public void sqlAlterTableChange(Statement state, String name, String newname, String type) throws SQLException{
		
	}
	@Override
	public void sqlAlterTablePrimaryKeys(Statement state) throws SQLException {
		String sql = "ALTER TABLE "+table+" RENAME TO oXHFcGcd04oXHFcGcd04_"+table; state.executeUpdate(sql);
		state.executeUpdate(sqlCreate());
		sql = "INSERT INTO "+table+" SELECT ";
		int size = fields.size();
		int i = 0;
		for(Entry<String, String> entry : fields.entrySet()){
			sql += entry.getKey();
			i++;
			if(i != size) sql += ",";
		}
		sql += " FROM oXHFcGcd04oXHFcGcd04_"+table; state.executeUpdate(sql);
		sql = "DROP TABLE oXHFcGcd04oXHFcGcd04_"+table; state.executeUpdate(sql);
	}
	@Override
	public void errorCreate(SQLException e) {
		if(!e.getMessage().startsWith("duplicate column name"))
			e.printStackTrace();
	}
	@Override
	public AbsSQLUpdateRow getUpdateRow() { return new SQLiteUpdateRow(); }

	// **********************************************
	
	private class SQLiteUpdateRow extends AbsSQLUpdateRow{
		
		@Override
		public AbsSQLUpdateRow add(String field, String t) {
			fields.put(field, "'"+t.replace("'", "''")+"'");
			return this;
		}
		@Override
		public String sqlInsertIgnoreInto() {
			ignore = " OR IGNORE ";
			return sqlInsertInto();
		}
		@Override
		public boolean isErrorPrimaryKey(SQLException e) {
			return e.getMessage().startsWith("PRIMARY KEY must be unique");
			
		}
	}
}