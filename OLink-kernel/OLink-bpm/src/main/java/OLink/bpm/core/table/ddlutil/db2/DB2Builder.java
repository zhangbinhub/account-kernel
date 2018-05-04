package OLink.bpm.core.table.ddlutil.db2;

import java.sql.Types;

import OLink.bpm.core.table.model.Table;
import OLink.bpm.core.table.ddlutil.SQLBuilder;
import OLink.bpm.core.table.model.Column;

/**
 * 
 * @author Chris
 * 
 */
public class DB2Builder extends SQLBuilder {
	public DB2Builder() {
		registerColumnType(Types.VARCHAR, "VARCHAR(255)");
		registerColumnType(Types.LONGVARCHAR, "VARCHAR(4000)");
		registerColumnType(Types.NUMERIC, "DECIMAL(22,5)");
		registerColumnType(Types.INTEGER, "DECIMAL(10,0)");
		registerColumnType(Types.BIT, "DECIMAL(1,0)");
		registerColumnType(Types.DATE, "DATE");
		registerColumnType(Types.TIMESTAMP, "TIMESTAMP");
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

		_writer.append("SELECT * FROM " + getTableFullName(table));
		_writer.append("  WHERE ISTMP = 0 AND  " + column.getName().toUpperCase());
		_writer.append(" is not null");
		_writer.append(SQL_DELIMITER);
	}

	public void findColumn(Table table, Column Column) {

		_writer.append("SELECT * FROM SYSIBM.COLUMNS WHERE TABLE_NAME ='" + table.getName().toUpperCase());
		_writer.append("' AND  COLUMN_NAME ='" + Column.getName().toUpperCase());
		_writer.append("' AND TABLE_SCHEMA='" + schema + "'");
		_writer.append(SQL_DELIMITER);

	}

	public void findTable(String tableName) {
		_writer.append("SELECT * FROM SYSIBM.TABLES WHERE TABLE_NAME ='" + tableName);
		_writer.append("' AND TABLE_SCHEMA='" + schema + "'");
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

		addColumn(changedTable, targetColumn);
		columnDataCopy(changedTable, sourceColumn, targetColumn);
		dropColumn(changedTable, sourceColumn);

		// String tableFullName = getTableFullName(changedTable);
		// _writer.append("ALTER TABLE " + tableFullName);
		// _writer.append(" RENAME COLUMN " + sourceColumn.getName());
		// _writer.append(" TO " + targetColumn.getName());
		// _writer.append(SQL_DELIMITER);
	}

	/**
	 * modify column stored data type
	 * 
	 * @param changedTable
	 * @param targetColumn
	 */
	public void modifyColumnDataType(Table changedTable, Column targetColumn) {
		String tableFullName = getTableFullName(changedTable);
		String targetColumnName = targetColumn.getName();
		String targetTypeName = getSqlTypeName(Integer.valueOf(targetColumn.getTypeCode()));

		_writer.append("ALTER TABLE " + tableFullName);
		_writer.append(" ALTER COLUMN " + targetColumnName);
		_writer.append(" SET DATA TYPE " + targetTypeName);
		_writer.append(SQL_DELIMITER);
	}

	/**
	 * rename table
	 * 
	 * @param changedTable
	 * @param targetTable
	 * @return
	 */
	public void tableRename(Table changedTable, Table targetTable) {
		String changedTableName = getTableFullName(changedTable);
		String targetTableName = getTableFullName(targetTable);

		_writer.append("RENAME TABLE " + changedTableName);
		_writer.append(" TO " + targetTableName);
		_writer.append(SQL_DELIMITER);

	}

	/**
	 * drop column
	 * 
	 * @param changedTable
	 * @param sourceColumn
	 */
	public void dropColumn(Table changedTable, Column sourceColumn) {
		String tableFullName = getTableFullName(changedTable);

		_writer.append("alter table " + tableFullName);
		_writer.append(" drop column " + sourceColumn.getName());
		_writer.append(SQL_DELIMITER);
		_writer.append("call SYSPROC.ADMIN_CMD('REORG TABLE " + tableFullName + "')");
		_writer.append(SQL_DELIMITER);
	}
}
