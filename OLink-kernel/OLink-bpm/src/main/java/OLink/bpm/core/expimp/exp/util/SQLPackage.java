package OLink.bpm.core.expimp.exp.util;

import java.util.ArrayList;
import java.util.Collection;

public class SQLPackage {

	private String sql;

	private String tableName;

	private Collection<String> resetColumn = new ArrayList<String>();

	public SQLPackage(String tableName, String sql) {
		setSql(sql);
		setTableName(tableName);
		addRestColumn("INSTANCEID");
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public Collection<String> getResetColumn() {
		return resetColumn;
	}

	public void setResetColumn(Collection<String> resetColumn) {
		this.resetColumn = resetColumn;
	}

	public void addRestColumn(String cloumnName) {
		getResetColumn().add(cloumnName);
	}
}
