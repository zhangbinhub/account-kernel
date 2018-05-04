package OLink.bpm.core.deploy.application.dao;

import java.sql.Connection;

import OLink.bpm.core.table.ddlutil.oracle.OracleTableDefinition;
import OLink.bpm.core.table.model.Table;
import OLink.bpm.util.DbTypeUtil;
import org.apache.log4j.Logger;

public class OracleApplicationInitDAO extends AbstractApplicationInitDAO {
	Logger log = Logger.getLogger(OracleApplicationInitDAO.class);

	// private Connection connection;

	public OracleApplicationInitDAO(Connection conn) throws Exception {
		super(conn);
		this.dbType = "ORACLE: ";
		this.schema = DbTypeUtil.getSchema(conn, DbTypeUtil.DBTYPE_ORACLE);
		this.definition = new OracleTableDefinition(conn);
	}

	protected Table getDBTable(String tableName) {
		return DbTypeUtil.getTable(tableName, DbTypeUtil.DBTYPE_ORACLE, this.connection);
	}
}
