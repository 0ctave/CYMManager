package fr.craftyourmind.manager.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map.Entry;

import com.mysql.jdbc.MysqlErrorNumbers;

public class CYMMySQL extends AbsSQLCYMCnx {
	
	private String base;
	private String login;
	private String pass;
	private String host;
	
	public CYMMySQL(String base, String login, String pass, String host) {
		this.base = base;
		this.login = login;
		this.pass = pass;
		this.host = host;
	}
	@Override
	public Connection getCnx() throws SQLException {
		return DriverManager.getConnection("jdbc:mysql://"+host+"/"+ base, login, pass);
	}
	@Override
	public AbsSQLCYMCnx addInt(String i) { fields.put(i, "int(11)"); return this; }
	@Override
	public AbsSQLCYMCnx addVarC(String var, int i) { fields.put(var, "varchar("+i+")"); return this; }
	@Override
	public AbsSQLCYMCnx addLong(String l) { fields.put(l, "bigint(20)"); return this; }
	@Override
	public AbsSQLCYMCnx addFloat(String f) { fields.put(f, "float"); return this; }
	@Override
	public AbsSQLCYMCnx addBool(String b) { fields.put(b, "tinyint(1)"); return this; }
	@Override
	public AbsSQLCYMCnx addTxt(String t) { fields.put(t, "text"); return this; }
	@Override
	public AbsSQLCYMCnx addDefaultValue(String value) { defaultValue = value; return this; }
	@Override
	public String sqlCreate() {
		String sql = "CREATE TABLE IF NOT EXISTS "+table+" (";
		boolean isAutoInc = !ai.isEmpty();
		int size = fields.size();
		int i = 0;
		for(Entry<String, String> entry : fields.entrySet()){
			sql += "`"+entry.getKey()+"` "+entry.getValue()+" NOT NULL";
			if(!ai.isEmpty() && ai.equals(entry.getKey())){ sql += " AUTO_INCREMENT"; ai = "";}
			i++;
			if(i != size) sql += ",";
		}
		if(!pks.isEmpty()){
			sql += ", PRIMARY KEY ("+pks.get(0);
			for(int ii = 1 ; ii < pks.size() ; ii++) sql += ", "+pks.get(ii);
			sql += ")";
		}
		sql += ") ENGINE=InnoDB DEFAULT CHARSET=utf8";
		if(isAutoInc) sql += " AUTO_INCREMENT=1";
		sql += " ;";
		return sql;
	}
	@Override
	public int getAutoIncrement(Statement state, String table) throws SQLException {
		ResultSet rs = state.executeQuery("SHOW TABLE STATUS LIKE '"+table+"'");
		rs.next();
		return rs.getInt("Auto_increment");
	}
	@Override
	public String sqlAlterTableAdd() {
		String key = "";
		String value = "";
		for(Entry<String, String> entry : fields.entrySet()){
			key = entry.getKey();
			value = entry.getValue();
		}
		return "ALTER IGNORE TABLE "+table+" ADD `"+key+"` "+value+" NOT NULL"+((defaultValue.isEmpty())?"":" DEFAULT '"+defaultValue+"'");
	}
	@Override
	public void sqlAlterTableChange(Statement state, String name, String newname, String type) throws SQLException {
		state.executeUpdate("ALTER TABLE "+table+" CHANGE `"+name+"` `"+newname+"` "+type+" NOT NULL");
	}
	@Override
	public void sqlAlterTablePrimaryKeys(Statement state) throws SQLException {
		String sql = "ALTER TABLE "+table+" DROP PRIMARY KEY , ADD PRIMARY KEY (";
		sql += pks.get(0);
		for(int i = 1 ; i < pks.size() ; i++) sql += ", "+pks.get(i);
		sql += ")";
		state.executeUpdate(sql);
	}
	@Override
	public void errorCreate(SQLException e) {
		if(e.getErrorCode() != MysqlErrorNumbers.ER_DUP_FIELDNAME && e.getErrorCode() != MysqlErrorNumbers.ER_BAD_FIELD_ERROR)
			e.printStackTrace();
	}
	@Override
	public AbsSQLUpdateRow getUpdateRow() { return new MySQLUpdateRow(); }
	
	// **********************************************
	
	private class MySQLUpdateRow extends AbsSQLUpdateRow{

		@Override
		public boolean isErrorPrimaryKey(SQLException e) {
			return e.getErrorCode() == MysqlErrorNumbers.ER_DUP_ENTRY; // ERROR 1062 Duplicata primary key
		}
	}
}