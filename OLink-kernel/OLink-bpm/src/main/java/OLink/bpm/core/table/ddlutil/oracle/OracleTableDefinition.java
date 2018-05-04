package OLink.bpm.core.table.ddlutil.oracle;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import OLink.bpm.core.table.ddlutil.SQLBuilder;
import OLink.bpm.core.table.ddlutil.AbstractTableDefinition;
import OLink.bpm.util.DbTypeUtil;
import org.apache.log4j.Logger;

/**
 * 
 * @author Chris
 * 
 */
public class OracleTableDefinition extends AbstractTableDefinition {
	 protected static Logger log = Logger.getLogger(OracleTableDefinition.class);

	public OracleTableDefinition(Connection conn) {
		super(conn, new OracleBuilder());
		this.schema = DbTypeUtil.getSchema(conn, DbTypeUtil.DBTYPE_ORACLE);
		_builder.setSchema(schema);
	}

	public int evaluateBatch(String sql, boolean continueOnError)
			throws SQLException {
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

					log.info("After execution, " + results
							+ " row(s) have been changed");
				} catch (SQLException ex) {
					if (continueOnError) {
						log.warn("SQL Command " + command + " failed with: "
								+ ex.getMessage());
						errors++;
					} else {
						if ((ex.getMessage().indexOf("ORA-00911")) > 0) {
							throw new SQLException(
									"[{*[Errors]*}]:  "
											+ " {*[core.create_field.type.incompatible]*}!");
						}
						throw ex;
					}
				}
			}
			log.info("Executed " + commandCount + " SQL command(s) with "
					+ errors + " error(s)");
		} catch (SQLException ex) {
			throw ex;
		} finally {
			closeStatement(statement);
		}
		return errors;
	}
}
