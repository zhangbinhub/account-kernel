package OLink.bpm.core.table.ddlutil.oracle;

import java.sql.Types;

import OLink.bpm.core.table.model.Table;
import OLink.bpm.core.table.ddlutil.SQLBuilder;
import OLink.bpm.core.table.model.Column;

/**
 * 
 * @author Chris
 * 
 */
public class OracleBuilder extends SQLBuilder {
	public OracleBuilder() {
		registerColumnType(Types.VARCHAR, "VARCHAR2(1000)");
		registerColumnType(Types.LONGVARCHAR, "VARCHAR2(4000)");
		registerColumnType(Types.NUMERIC, "NUMBER(22,5)");
		registerColumnType(Types.INTEGER, "NUMBER(10,0)");
		registerColumnType(Types.BIT, "NUMBER(1,0)");
		registerColumnType(Types.DATE, "DATE");
		registerColumnType(Types.TIMESTAMP, "TIMESTAMP(6)");
		registerColumnType(Types.CLOB, "CLOB");
		registerColumnType(Types.BLOB, "BLOB");
	}

	private void registerColumnType(int code, String name) {
		typeNames.put(Integer.valueOf(code), name);
	}

	public void findTable(Table table) {
		String tableName = table.getName().toUpperCase();
		findTable(tableName);
	}

	public void getTableDatas(Table table) {
		String tableName = getTableFullName(table);
		_writer.append("SELECT * FROM " + tableName);
		_writer.append(SQL_DELIMITER);
	}

	public void getColumnDatas(Table table, Column column) {

		_writer.append("SELECT * FROM " + table.getName().toUpperCase());
		_writer.append("  WHERE ISTMP = 0 AND  " + column.getName().toUpperCase());
		_writer.append(" is not null");
		_writer.append(SQL_DELIMITER);
	}

	public void findColumn(Table table, Column Column) {

		_writer.append("SELECT * FROM USER_TAB_COLS WHERE TABLE_NAME ='" + table.getName().toUpperCase());
		_writer.append("' AND  COLUMN_NAME ='" + Column.getName().toUpperCase());
		_writer.append("'");
		_writer.append(SQL_DELIMITER);

	}

	public void findTable(String tableName) {
		_writer.append("SELECT * FROM USER_TABLES WHERE TABLE_NAME ='" + tableName);
		_writer.append("'");
		_writer.append(SQL_DELIMITER);
	}

	public String getTableFullName(Table table) {
		if (schema != null && schema.trim().length() > 0) {
			String tableFullName = schema + "." + table.getName();
			return tableFullName;
		}
		return table.getName();
	}

	/**
	 * rename column
	 * 
	 * @param changedTable
	 * @param sourceColumn
	 * @param targetColumn
	 */
	public void columnRename(Table changedTable, Column sourceColumn, Column targetColumn) {
		String tableFullName = getTableFullName(changedTable);

		_writer.append("ALTER TABLE " + tableFullName);
		_writer.append(" RENAME COLUMN " + sourceColumn.getName());
		_writer.append(" TO " + targetColumn.getName());
		_writer.append(SQL_DELIMITER);
	}

}
