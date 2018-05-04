package OLink.bpm.core.deploy.application.dao;

import java.sql.Connection;

import OLink.bpm.core.table.ddlutil.hsql.HsqldbTableDefinition;
import OLink.bpm.core.table.model.Table;
import OLink.bpm.util.DbTypeUtil;

public class HsqldbApplicationInitDAO extends AbstractApplicationInitDAO {

	public HsqldbApplicationInitDAO(Connection conn) throws Exception {
		super(conn);
		this.dbType = "HypersonicSQL: ";
		this.schema = DbTypeUtil.getSchema(conn, DbTypeUtil.DBTYPE_HSQLDB);
		this.definition = new HsqldbTableDefinition(conn);
	}

	protected Table getDBTable(String tableName) {
		return DbTypeUtil.getTable(tableName, DbTypeUtil.DBTYPE_HSQLDB, this.connection);
	}

}
