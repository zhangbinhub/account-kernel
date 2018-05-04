package OLink.bpm.core.table.ddlutil.mysql;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import OLink.bpm.core.dynaform.document.dql.DQLASTUtil;
import OLink.bpm.core.table.alteration.ColumnDataTypeChange;
import OLink.bpm.core.table.alteration.ColumnRenameChange;
import OLink.bpm.core.table.model.Table;
import OLink.bpm.util.DbTypeUtil;
import OLink.bpm.core.table.ddlutil.SQLBuilder;
import OLink.bpm.core.table.ddlutil.AbstractTableDefinition;
import OLink.bpm.core.table.model.Column;
import org.apache.log4j.Logger;

/**
 * 
 * @author Chris
 * 
 */
public class MysqlTableDefinition extends AbstractTableDefinition {
	private static Logger log = Logger.getLogger(MysqlTableDefinition.class);

	public MysqlTableDefinition(Connection conn) {
		super(conn, new MysqlBuilder());
		this.schema = DbTypeUtil.getSchema(conn, DbTypeUtil.DBTYPE_MYSQL);
		_builder.setSchema(schema);
	}

	public void processChange(ColumnDataTypeChange change) throws SQLException {
		StringBuffer buffer = new StringBuffer();
		getSQLBuilder().setWriter(buffer);

		Table table = change.getTable();
		Column targetColumn = change.getTargetColumn();
		Column sourceColumn = change.getSourceColumn();
		Column tempColumn = sourceColumn;
		// boolean isDataExists = false;

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
						if ((ex.getMessage().indexOf("Incorrect table name")) > 0) {
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
		}
		return errors;
	}
}
