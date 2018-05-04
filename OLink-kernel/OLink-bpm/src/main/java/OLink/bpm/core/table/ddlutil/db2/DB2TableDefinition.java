package OLink.bpm.core.table.ddlutil.db2;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import OLink.bpm.core.table.alteration.ColumnDataTypeChange;
import OLink.bpm.core.table.alteration.ModelChange;
import OLink.bpm.core.table.alteration.TableDataTransferChange;
import OLink.bpm.core.table.ddlutil.AbstractTableDefinition;
import OLink.bpm.core.table.ddlutil.SQLBuilder;
import OLink.bpm.util.DbTypeUtil;

/**
 * 
 * @author Chris
 * 
 */
public class DB2TableDefinition extends AbstractTableDefinition {

	public DB2TableDefinition(Connection conn) {
		super(conn, new DB2Builder());
		this.schema = DbTypeUtil.getSchema(conn, DbTypeUtil.DBTYPE_DB2);
		_builder.setSchema(schema);
	}

	public void processChange(ColumnDataTypeChange change) throws SQLException {
		try {
			super.processChange(change);
		} catch (SQLException ex) {

			int index = -1;
			if ((index = ex.getMessage().indexOf("SQLSTATE=")) > 0) {
				String sqlstatus = ex.getMessage().substring(index + 9);
				sqlstatus = sqlstatus.substring(0, sqlstatus.indexOf(","));
				throw new SQLException(getMessage(sqlstatus, change));
			}
			throw ex;
		}
	}

	public void processChange(TableDataTransferChange change) throws SQLException {
		try {
			super.processChange(change);
		} catch (SQLException ex) {

			int index = -1;
			if ((index = ex.getMessage().indexOf("SQLSTATE=")) > 0) {
				String sqlstatus = ex.getMessage().substring(index + 9);
				sqlstatus = sqlstatus.substring(0, sqlstatus.indexOf(","));
				throw new SQLException(getMessage(sqlstatus, change));
			}
			throw ex;
		}
	}

	private String getMessage(String status, ModelChange change) {
		if (status.trim().equals("42821")) {
			return "[{*[Errors]*}]:  " + change.getTargetColumn().getFieldName()
					+ " {*[core.field.type.incompatible]*}!";
		} else if (status.trim().equals("55019")) {
			return "[{*[Errors]*}]:  " + change.getTargetColumn().getFieldName()
					+ " {*[core.field.type.incompatible]*}!";
		} else {
			return "[{*[Errors]*}]: SQLSTATUS = " + status;
		}
	}

	public int evaluateBatch(String sql, boolean continueOnError) throws SQLException {
		Statement statement = null;
		CallableStatement callst = null;
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

					if (command.startsWith("call")) {
						callst = conn.prepareCall(command);
						callst.execute();
					} else {
						int results = statement.executeUpdate(command);
						log.info("After execution, " + results + " row(s) have been changed");
					}
				} catch (SQLException ex) {
					if (continueOnError) {
						log.warn("SQL Command " + command + " failed with: " + ex.getMessage());
						errors++;
					} else {
						int index = -1;
						String sqlstatus = "";
						if ((index = ex.getMessage().indexOf("SQLSTATE=")) > 0) {
							sqlstatus = ex.getMessage().substring(index + 9);
							sqlstatus = sqlstatus.substring(0, sqlstatus.indexOf(","));
						}
						if (sqlstatus.trim().equals("42601")) {
							throw new SQLException("[{*[Errors]*}]:  " + " {*[core.create_field.type.incompatible]*}!");
						}
						throw ex;
					}
				}
			}
			log.info("Executed " + commandCount + " SQL command(s) with " + errors + " error(s)");
		} catch (SQLException ex) {
			throw ex;
		} finally {
			closeStatement(statement);
			closeStatement(callst);
		}
		return errors;
	}
}
