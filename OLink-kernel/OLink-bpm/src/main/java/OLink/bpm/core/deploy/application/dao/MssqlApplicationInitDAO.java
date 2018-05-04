package OLink.bpm.core.deploy.application.dao;

import java.sql.Connection;

import OLink.bpm.core.table.ddlutil.mssql.MssqlTableDefinition;
import OLink.bpm.core.table.model.Table;
import OLink.bpm.util.DbTypeUtil;

public class MssqlApplicationInitDAO extends AbstractApplicationInitDAO {

	public MssqlApplicationInitDAO(Connection conn) throws Exception {
		super(conn);
		this.dbType = "MS SQL Server: ";
		this.schema = DbTypeUtil.getSchema(conn, DbTypeUtil.DBTYPE_MSSQL);
		this.definition = new MssqlTableDefinition(conn);
	}

	protected Table getDBTable(String tableName) {
		return DbTypeUtil.getTable(tableName, DbTypeUtil.DBTYPE_MSSQL, this.connection);
	}
}
