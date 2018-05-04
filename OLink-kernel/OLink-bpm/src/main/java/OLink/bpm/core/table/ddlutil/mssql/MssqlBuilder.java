package OLink.bpm.core.table.ddlutil.mssql;

import java.sql.Types;
import java.util.Collection;
import java.util.Iterator;

import OLink.bpm.core.table.model.Table;
import OLink.bpm.core.table.ddlutil.SQLBuilder;
import OLink.bpm.core.table.model.Column;

/**
 * 
 * @author Chris
 * 
 */
public class MssqlBuilder extends SQLBuilder {
	public MssqlBuilder() {
		registerColumnType(Types.VARCHAR, "NVARCHAR(200)");
		registerColumnType(Types.LONGVARCHAR, "NVARCHAR(4000)");
		registerColumnType(Types.NUMERIC, "NUMERIC(22,5)");
		registerColumnType(Types.INTEGER, "NUMERIC(10,0)");
		registerColumnType(Types.BIT, "BIT");
		registerColumnType(Types.DATE, "SMALLDATETIME");
		registerColumnType(Types.TIMESTAMP, "DATETIME");
		registerColumnType(Types.CLOB, "NTEXT");
		registerColumnType(Types.BLOB, "VARBINARY");
	}

	private void registerColumnType(int code, String name) {
		typeNames.put(Integer.valueOf(code), name);
	}

	public void findTable(Table table) {
		String tableName = table.getName().toUpperCase();
		findTable(tableName);
	}

	public void createTableAsSelect(Table table, Table changedTable, boolean dropFirst) {
		String tableFullName = getTableFullName(table);

		_writer = new StringBuffer();
		_writer.append("select " + getColumnNameString(table) + " into " +tableFullName);
		_writer.append(" from " + changedTable.getName());
		_writer.append(SQL_DELIMITER);
	}
	
	private String getColumnNameString(Table table) {
		StringBuffer buffer = new StringBuffer();

		Collection<Column> columns = table.getColumns();
		if (columns != null && !columns.isEmpty()) {
			for (Iterator<Column> iter = columns.iterator(); iter.hasNext();) {
				Column column = iter.next();

				buffer.append(column.getName());
				buffer.append(",");
			}
			buffer.deleteCharAt(buffer.length() - 1);
		}

		return buffer.toString();
	}
	
	public void getColumnDatas(Table table, Column column) {

		_writer.append("SELECT * FROM " + table.getName().toUpperCase());
		_writer.append("  WHERE ISTMP = 0 AND  "
				+ column.getName().toUpperCase());
		_writer.append(" IS NOT NULL");
		_writer.append(SQL_DELIMITER);
	}

	public void findColumn(Table table, Column Column) {
		_writer.append("SELECT * FROM DBO.SYSCOLUMNS WHERE ID =("
				+"SELECT ID FROM DBO.SYSOBJECTS WHERE NAME ='"+table.getName().toUpperCase()+"')" );
		_writer
				.append(" AND  NAME ='"
						+ Column.getName().toUpperCase());
		_writer.append("'");
		_writer.append(SQL_DELIMITER);

	}

	public void findTable(String tableName) {
		_writer.append("SELECT * FROM DBO.SYSOBJECTS WHERE NAME ='"
				+ tableName);
		_writer.append("'");
		_writer.append(SQL_DELIMITER);
	}
	
	/**
	 * rename column
	 * 
	 * @param changedTable
	 * @param sourceColumn
	 * @param targetColumn
	 */
	public void columnRename(Table changedTable, Column sourceColumn,
			Column targetColumn) {
		String tableFullName = getTableFullName(changedTable);
		
		_writer.append("EXEC SP_RENAME '"+tableFullName);
		_writer.append(".[" + sourceColumn.getName());
		_writer.append("]', '" + targetColumn.getName()+"', 'COLUMN'");
		_writer.append(SQL_DELIMITER);
// step1-->add target column
// addColumn(changedTable, targetColumn);

		// step2-->transfer data,from souce column to target column
// columnDataCopy(changedTable, sourceColumn, targetColumn);

		// step3-->drop temp cloumn
// dropColumn(changedTable, sourceColumn);
	}

	public String getTableFullName(Table table) {
		if (schema != null && schema.trim().length() > 0) {
			String tableFullName = "DBO." + table.getName();
			return tableFullName;
		}
		return table.getName();
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

		_writer.append("EXEC SP_RENAME '" + changedTableName);
		_writer.append("', '" + targetTableName+"'");
		_writer.append(SQL_DELIMITER);
		
// _writer.append("SELECT * INTO "+targetTableName+" FROM "+changedTableName);
// _writer.append(SQL_DELIMITER);
//		
// dropTable(changedTable);
	}

	public void getTableDatas(Table table) {
		String tableName = getTableFullName(table);
		_writer.append("SELECT * FROM " + tableName);
		_writer.append(SQL_DELIMITER);
	}
}
