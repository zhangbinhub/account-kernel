package OLink.bpm.core.deploy.application.dao;

import java.sql.Connection;

import OLink.bpm.core.table.ddlutil.db2.DB2TableDefinition;
import OLink.bpm.core.table.model.Table;
import OLink.bpm.util.DbTypeUtil;

public class DB2ApplicationInitDAO extends AbstractApplicationInitDAO {

	public DB2ApplicationInitDAO(Connection conn) throws Exception {
		super(conn);
		this.dbType = "DB2: ";
		this.schema = DbTypeUtil.getSchema(conn, DbTypeUtil.DBTYPE_DB2);
		this.definition = new DB2TableDefinition(conn);
	}

	protected Table getDBTable(String tableName) {
		return DbTypeUtil.getTable(tableName, DbTypeUtil.DBTYPE_DB2, this.connection);
	}
}
