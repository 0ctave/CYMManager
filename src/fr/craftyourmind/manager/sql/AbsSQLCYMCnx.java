package fr.craftyourmind.manager.sql;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public abstract class AbsSQLCYMCnx {
	
	protected String table = "";
	protected Map<String, String> fields = new LinkedHashMap<String, String>();
	protected List<String> pks = new ArrayList<String>();
	protected String ai = "";
	protected String defaultValue = "";
	
	public abstract Connection getCnx() throws SQLException;

	public AbsSQLCYMCnx init(String table){
		this.table = table;
		fields.clear();
		pks.clear();
		ai = "";
		defaultValue = "";
		return this;
	}

	public AbsSQLCYMCnx addPK(String pk) {
		pks.add(pk);
		return this;
	}

	public AbsSQLCYMCnx addAI(String ai) {
		this.ai = ai;
		return this;
	}

	public abstract AbsSQLCYMCnx addInt(String i);

	public abstract AbsSQLCYMCnx addVarC(String var, int i);

	public abstract AbsSQLCYMCnx addLong(String l);
	
	public abstract AbsSQLCYMCnx addFloat(String f);
	
	public abstract AbsSQLCYMCnx addBool(String b);
	
	public abstract AbsSQLCYMCnx addTxt(String t);
	
	public abstract AbsSQLCYMCnx addDefaultValue(String value);

	public abstract String sqlCreate();

	public abstract int getAutoIncrement(Statement state, String table) throws SQLException;

	public abstract String sqlAlterTableAdd();
	@Deprecated
	public abstract void sqlAlterTableChange(Statement state, String name, String newname, String type) throws SQLException;
	
	public abstract void sqlAlterTablePrimaryKeys(Statement state) throws SQLException;
	
	public abstract void errorCreate(SQLException e);
	
	
	public abstract AbsSQLUpdateRow getUpdateRow();
	
	// **********************************************
	
	public abstract class AbsSQLUpdateRow{
		
		protected String table = "";
		protected Map<String, String> fields = new LinkedHashMap<String, String>();
		protected Map<String, String>  wheres = new LinkedHashMap<String, String>();
		protected String ignore = " ";
		
		public AbsSQLUpdateRow init(String table){
			this.table = table;
			fields.clear();
			wheres.clear();
			ignore = " ";
			return this;
		}

		public AbsSQLUpdateRow add(String field, int i) { fields.put(field, i+""); return this; }

		public AbsSQLUpdateRow add(String field, String t) { fields.put(field, "\""+t+"\""); return this; }

		public AbsSQLUpdateRow add(String field, boolean b) { fields.put(field, ""+((b)?1:0)); return this; }

		public AbsSQLUpdateRow add(String field, long l) { fields.put(field, l+""); return this; }
		
		public AbsSQLUpdateRow add(String field, float f) { fields.put(field, "\""+f+"\""); return this; }

		public AbsSQLUpdateRow sqlWhere(String field, int i) { wheres.put(field, i+""); return this; }

		public AbsSQLUpdateRow sqlWhere(String field, String t) { wheres.put(field, "\""+t+"\""); return this; }
		
		public String sqlInsertInto() {
			StringBuilder sql = new StringBuilder("INSERT").append(ignore).append("INTO ").append(table).append(" (");
			StringBuilder tmp = new StringBuilder();
			int size = fields.size();
			int i = 0;
			for(Entry<String, String> entry : fields.entrySet()){
				sql.append("`"+entry.getKey()).append("`");
				tmp.append(entry.getValue());
				i++;
				if(i != size){ sql.append(","); tmp.append(",");}
			}
			sql.append(") VALUES (").append(tmp).append(");");
			return sql.toString();
		}
		
		public String sqlInsertIgnoreInto() {
			ignore = " IGNORE ";
			return sqlInsertInto();
		}
		
		public String sqlUpdate() {
			StringBuilder sql = new StringBuilder("UPDATE ").append(table).append(" SET ");
			int size = fields.size();
			int i = 0;
			for(Entry<String, String> entry : fields.entrySet()){
				sql.append("`").append(entry.getKey()).append("` = ").append(entry.getValue());
				i++;
				if(i != size) sql.append(",");
			}
			sql.append(" WHERE ");
			size = wheres.size();
			i = 0;
			for(Entry<String, String> entry : wheres.entrySet()){
				sql.append("`").append(entry.getKey()).append("` = ").append(entry.getValue());
				i++;
				if(i != size) sql.append(" AND ");
			}
			sql.append(";");
			return sql.toString();
		}

		public String sqlDelete() {
			StringBuilder sql = new StringBuilder("DELETE FROM ").append(table).append(" WHERE ");
			int size = wheres.size();
			int i = 0;
			for(Entry<String, String> entry : wheres.entrySet()){
				sql.append("`").append(entry.getKey()).append("` = ").append(entry.getValue());
				i++;
				if(i != size) sql.append(" AND ");
			}
			sql.append(";");
			return sql.toString();
		}
		
		public abstract boolean isErrorPrimaryKey(SQLException e);
	}
}