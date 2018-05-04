package OLink.bpm.core.table.ddlutil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Iterator;

import OLink.bpm.core.dynaform.document.dql.DQLASTUtil;
import OLink.bpm.core.table.model.Column;
import OLink.bpm.core.table.model.Table;
import OLink.bpm.core.table.alteration.ColumnDataTransferChange;
import OLink.bpm.core.table.alteration.ColumnDataTypeChange;
import org.apache.log4j.Logger;

import OLink.bpm.core.table.alteration.AddColumnChange;
import OLink.bpm.core.table.alteration.AddTableChange;
import OLink.bpm.core.table.alteration.ColumnRenameChange;
import OLink.bpm.core.table.alteration.DropColumnChange;
import OLink.bpm.core.table.alteration.DropTableChange;
import OLink.bpm.core.table.alteration.ModelChange;
import OLink.bpm.core.table.alteration.TableDataTransferChange;
import OLink.bpm.core.table.alteration.TableRenameChange;

/**
 * @author nicholas
 */
public abstract class AbstractTableDefinition {
	protected static Logger log = Logger.getLogger(AbstractTableDefinition.class);

	protected Connection conn;

	protected String schema;

	protected SQLBuilder _builder;

	protected AbstractTableDefinition(Connection conn, SQLBuilder _builder) {
		this._builder = _builder;
		this.conn = conn;
	}

	public void processChanges(ChangeLog changeLog) throws Exception {
		Collection<ModelChange> changes = changeLog.getChanges();
		for (Iterator<ModelChange> iterator = changes.iterator(); iterator.hasNext();) {
			ModelChange change = iterator.next();
			invokeChangeHandler(change);
		}
	}

	protected void invokeChangeHandler(ModelChange change) throws Exception {
		Class<?> curClass = getClass();

		// find the handler for the change
		while ((curClass != null) && !Object.class.equals(curClass)) {

			Method method = null;
			try {
				try {
					method = curClass.getDeclaredMethod("processChange", change.getClass());
				} catch (NoSuchMethodException ex) {
					// we actually expect this one
				}

				if (method != null) {
					method.invoke(this, change);
					return;
				} else {
					curClass = curClass.getSuperclass();
				}
			} catch (InvocationTargetException ex) {
				if (ex.getTargetException() instanceof SQLException) {
					throw (SQLException) ex.getTargetException();
				} else {
					throw new Exception(ex.getTargetException());
				}
			}
		}
	}

	public void processChange(AddTableChange change) throws SQLException {
		StringBuffer buffer = new StringBuffer();
		getSQLBuilder().setWriter(buffer);
		getSQLBuilder().createTable(change.getTable(), false);

		String sql = getSQLBuilder().getSQL();
		evaluateBatch(sql, false);
	}

	public void processChange(AddColumnChange change) throws SQLException {
		StringBuffer buffer = new StringBuffer();
		getSQLBuilder().setWriter(buffer);
		getSQLBuilder().addColumn(change.getTable(), change.getTargetColumn());

		String sql = getSQLBuilder().getSQL();
		evaluateBatch(sql, false);
	}

	public void processChange(ColumnDataTransferChange change) throws SQLException {
		StringBuffer buffer = new StringBuffer();
		getSQLBuilder().setWriter(buffer);
		getSQLBuilder().columnDataCopy(change.getTable(), change.getSourceColumn(), change.getTargetColumn());

		String sql = getSQLBuilder().getSQL();
		evaluateBatch(sql, false);
	}

	public void processChange(ColumnDataTypeChange change) throws SQLException {
		StringBuffer buffer = new StringBuffer();
		getSQLBuilder().setWriter(buffer);

		Table table = change.getTable();
		Column targetColumn = change.getTargetColumn();
		Column sourceColumn = change.getSourceColumn();
		Column tempColumn = sourceColumn;
		//boolean isDataExists = false;

		try {
			tempColumn = (Column) sourceColumn.clone();
			log.debug(Integer.valueOf(sourceColumn.hashCode()));
			log.debug(Integer.valueOf(tempColumn.hashCode()));
			tempColumn.setName(DQLASTUtil.TEMP_PREFIX + sourceColumn.getName());
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		// modify column type need 4 step:
		// step1-->rename source column to temp column
		getSQLBuilder().columnRename(table, sourceColumn, tempColumn);

		// step2-->add target column
		getSQLBuilder().addColumn(table, targetColumn);

		// step3-->transfer data,from souce column to target column
		getSQLBuilder().columnDataCopy(table, tempColumn, targetColumn);

		// step4-->drop temp cloumn
		getSQLBuilder().dropColumn(table, tempColumn);

		String sql = getSQLBuilder().getSQL();
		evaluateBatch(sql, false);
	}

	public void processChange(ColumnRenameChange change) throws SQLException {
		StringBuffer buffer = new StringBuffer();
		getSQLBuilder().setWriter(buffer);
		getSQLBuilder().columnRename(change.getTable(), change.getSourceColumn(), change.getTargetColumn());

		String sql = getSQLBuilder().getSQL();
		evaluateBatch(sql, false);
	}

	public void processChange(DropColumnChange change) throws SQLException {
		StringBuffer buffer = new StringBuffer();
		getSQLBuilder().setWriter(buffer);
		getSQLBuilder().dropColumn(change.getTable(), change.getSourceColumn());

		String sql = getSQLBuilder().getSQL();
		evaluateBatch(sql, false);
	}

	public void processChange(DropTableChange change) throws SQLException {
		StringBuffer buffer = new StringBuffer();
		getSQLBuilder().setWriter(buffer);
		getSQLBuilder().dropTable(change.getTable());

		String sql = getSQLBuilder().getSQL();
		evaluateBatch(sql, false);
	}

	public void processChange(TableDataTransferChange change) throws SQLException {
		StringBuffer buffer = new StringBuffer();
		getSQLBuilder().setWriter(buffer);
		getSQLBuilder().tableDataCopy(change.getSourceTable(), change.getTargetTable());

		String sql = getSQLBuilder().getSQL();
		evaluateBatch(sql, false);
	}

	public void processChange(TableRenameChange change) throws SQLException {
		StringBuffer buffer = new StringBuffer();
		getSQLBuilder().setWriter(buffer);
		// rename table need 3 step:
		// step1--> create new table
//		getSQLBuilder().createTable(change.getTargetTable(), false);

		// step2--> transfer old table data to new table
//		getSQLBuilder().tableDataCopy(change.getChangedTable(), change.getTargetTable());
		getSQLBuilder().createTableAsSelect(change.getTargetTable(), change.getChangedTable(), false);
		
		// step3-->drop old table
		getSQLBuilder().dropTable(change.getChangedTable());

		String sql = getSQLBuilder().getSQL();
		evaluateBatch(sql, false);
	}

	public SQLBuilder getSQLBuilder() {
		return _builder;
	}

	public int evaluateBatch(String sql, boolean continueOnError) throws SQLException {
		Statement statement = null;
		int errors = 0;
		int commandCount = 0;

		// we tokenize the SQL along the delimiters, and we also make sure that
		// only delimiters
		// at the end of a line or the end of the string are used (row mode)
		try {
			statement = conn.createStatement();
			String[] commands = sql.split(SQLBuilder.SQL_DELIMITER);

			for (int i = 0; i < commands.length; i++) {
				String command = commands[i];

				if (command.trim().length() == 0) {
					continue;
				}
				commandCount++;

				try {
					log.info("executing SQL: " + command);

					int results = statement.executeUpdate(command);

					log.info("After execution, " + results + " row(s) have been changed");
				} catch (SQLException ex) {
					if (continueOnError) {
						log.warn("SQL Command " + command + " failed with: " + ex.getMessage());
						errors++;
					} else {
						throw ex;
					}
				}
			}
			log.info("Executed " + commandCount + " SQL command(s) with " + errors + " error(s)");
		} catch (SQLException ex) {
			throw ex;
		} finally {
			closeStatement(statement);
		}
		return errors;
	}

	protected void closeStatement(Statement statement) {
		if (statement != null) {
			try {
				statement.close();
			} catch (Exception e) {
				log.info("Ignoring exception that occurred while closing statement", e);
			}
		}
	}
}
