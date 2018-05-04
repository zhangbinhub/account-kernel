package OLink.bpm.core.table.ddlutil.mysql;

import java.sql.Types;

import OLink.bpm.core.table.ddlutil.SQLBuilder;
import OLink.bpm.core.table.model.Column;
import OLink.bpm.core.table.model.Table;

/**
 * 
 * @author Chris
 * 
 */
public class MysqlBuilder extends SQLBuilder {
	public MysqlBuilder() {
		registerColumnType(Types.VARCHAR, "VARCHAR(190)");
		registerColumnType(Types.LONGVARCHAR, "VARCHAR(4000)");
		registerColumnType(Types.NUMERIC, "DECIMAL(22,5)");
		registerColumnType(Types.INTEGER, "INT");
		registerColumnType(Types.BIT, "BIT(1)");
		registerColumnType(Types.DATE, "DATE");
		registerColumnType(Types.TINYINT, "TINYINT");
		registerColumnType(Types.BLOB, "MEDIUMBLOB");
		/**
		 * modified by alex -->
		 */
		//registerColumnType(Types.TIMESTAMP, "TIMESTAMP(14)");
		registerColumnType(Types.TIMESTAMP, "DATETIME");
		/**
		 * <-- modified by alex 
		 */
		registerColumnType(Types.CLOB, "MEDIUMTEXT");
		// registerColumnType(Types.CLOB, "TEXT");
	}

	private void registerColumnType(int code, String name) {
		typeNames.put(Integer.valueOf(code), name);
	}

	/**
	 * 用于修改表字段名及字段类型
	 * 
	 * @param changedTable
	 * @param srcColumnName
	 * @param targetColumn
	 */
	private void alterColumn(Table changedTable, String srcColumnName, Column targetColumn) {
		String tableFullName = getTableFullName(changedTable);
		String targetTypeName = getSqlTypeName(Integer.valueOf(targetColumn.getTypeCode()));

		_writer.append("ALTER TABLE " + tableFullName);
		_writer.append(" CHANGE " + srcColumnName);
		_writer.append(" " + targetColumn.getName() + " " + targetTypeName);
		_writer.append(SQL_DELIMITER);

	}

	public void columnRename(Table changedTable, Column sourceColumn, Column targetColumn) {
		alterColumn(changedTable, sourceColumn.getName(), targetColumn);

	}

	public void findColumn(Table table, Column column) {
		_writer.append("SELECT * FROM  INFORMATION_SCHEMA.COLUMNS ");
		_writer.append("WHERE upper(TABLE_SCHEMA)='" + schema.trim() + "'");
		_writer.append(" AND TABLE_NAME='" + table.getName().trim() + "'");
		_writer.append(" AND COLUMN_NAME='" + column.getName().trim() + "'");
		_writer.append(SQL_DELIMITER);
	}

	public void findTable(String tableName) {
		_writer.append("SELECT count(*) FROM INFORMATION_SCHEMA.TABLES ");
		_writer.append("WHERE TABLE_NAME='" + tableName + "' AND upper(TABLE_SCHEMA)='" + schema + "'");
		_writer.append(SQL_DELIMITER);
	}

	public void findTable(Table table) {
		String tablename = table.getName().toUpperCase().trim();
		findTable(tablename);
	}

	public void getColumnDatas(Table table, Column column) {
		_writer.append("SELECT * FROM " + table.getName().toUpperCase());
		_writer.append("  WHERE ISTMP = 0 AND  " + column.getName().toUpperCase());
		_writer.append(" IS NOT NULL");
		_writer.append(SQL_DELIMITER);

	}

	/**
	 * modify column stored data type
	 * 
	 * @param changedTable
	 * @param targetColumn
	 */
	public void modifyColumnDataType(Table changedTable, Column targetColumn) {
		String targetColumnName = targetColumn.getName();
		alterColumn(changedTable, targetColumnName, targetColumn);
	}

	public String getTableFullName(Table table) {
		if (schema != null && schema.trim().length() > 0) {
			String tableFullName = schema + "." + table.getName();
			return tableFullName;
		}
		return table.getName();
	}

	public void getTableDatas(Table table) {
		String tableName = getTableFullName(table);
		_writer.append("SELECT * FROM " + tableName);
		_writer.append(SQL_DELIMITER);

	}

}
