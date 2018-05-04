package OLink.bpm.core.table.ddlutil.hsql;

import java.sql.Types;

import OLink.bpm.core.table.model.Column;
import OLink.bpm.core.table.model.Table;
import OLink.bpm.core.table.ddlutil.SQLBuilder;

/**
 * 
 * @author Chris
 * 
 */
public class HsqldbBuilder extends SQLBuilder {
	public HsqldbBuilder() {
		registerColumnType(Types.VARCHAR, "VARCHAR(200)");
		registerColumnType(Types.LONGVARCHAR, "VARCHAR(4000)");
		registerColumnType(Types.NUMERIC, "DECIMAL(22,5)");
		registerColumnType(Types.INTEGER, "INTEGER");
		registerColumnType(Types.BIT, "BIT");
		registerColumnType(Types.DATE, "DATE");
		registerColumnType(Types.TIMESTAMP, "TIMESTAMP");
		registerColumnType(Types.CLOB, "CLOB");
		registerColumnType(Types.BLOB, "BLOB");
	}

	private void registerColumnType(int code, String name) {
		typeNames.put(Integer.valueOf(code), name);
	}

	public void columnRename(Table changedTable, Column sourceColumn, Column targetColumn) {
		// //step1:add a column
		// addColumn(changedTable, targetColumn);
		// //step2:copy datas from sourceColumn to targetColumn
		// columnDataCopy(changedTable, sourceColumn,
		// targetColumn);
		// //step3:drop the column sourceColumn
		// dropColumn(changedTable, sourceColumn);

		_writer.append("ALTER TABLE " + getTableFullName(changedTable));
		_writer.append(" ALTER COLUMN " + sourceColumn.getName().toUpperCase());
		_writer.append(" RENAME TO " + targetColumn.getName().toUpperCase());
		_writer.append(SQL_DELIMITER);
	}

	public void findColumn(Table table, Column column) {
		_writer.append("SELECT * FROM  INFORMATION_SCHEMA.SYSTEM_COLUMNS ");
		_writer.append("WHERE TABLE_SCHEMA='" + schema.trim());
		_writer.append("' AND TABLE_NAME='" + table.getName().trim() + "'");
		_writer.append("' AND COLUMN_NAME='" + column.getName().trim() + "'");
		_writer.append(SQL_DELIMITER);
	}

	public void findTable(String tableName) {
		_writer.append("SELECT * FROM INFORMATION_SCHEMA.SYSTEM_TABLES ");
		_writer.append("WHERE TABLE_NAME='" + tableName + "'");
		_writer.append(SQL_DELIMITER);
	}

	public void findTable(Table table) {
		String tablename = table.getName().toUpperCase().trim();
		findTable(tablename);
	}

	public void getColumnDatas(Table table, Column column) {
		_writer.append("SELECT * FROM " + getTableFullName(table));
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
		String tableFullName = getTableFullName(changedTable);
		String targetColumnName = targetColumn.getName();
		String targetTypeName = getSqlTypeName(Integer.valueOf(targetColumn.getTypeCode()));

		_writer.append("ALTER TABLE " + tableFullName);
		_writer.append(" ALTER " + targetColumnName);
		_writer.append(" " + targetTypeName);
		_writer.append(SQL_DELIMITER);
	}

	public String getTableFullName(Table table) {
		if (schema != null && schema.trim().length() > 0) {
			String tableFullName = schema + "." + table.getName();
			return tableFullName.toUpperCase();
		}
		return table.getName().toUpperCase();
	}

	public void getTableDatas(Table table) {
		String tableName = getTableFullName(table);
		_writer.append("SELECT * FROM " + tableName);
		_writer.append(SQL_DELIMITER);

	}

}
