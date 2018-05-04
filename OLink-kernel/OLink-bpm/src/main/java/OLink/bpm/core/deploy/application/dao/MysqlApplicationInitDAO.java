package OLink.bpm.core.deploy.application.dao;

import java.sql.Connection;

import OLink.bpm.core.table.model.Table;
import OLink.bpm.util.DbTypeUtil;
import OLink.bpm.core.table.ddlutil.mysql.MysqlTableDefinition;

public class MysqlApplicationInitDAO extends AbstractApplicationInitDAO {

	public MysqlApplicationInitDAO(Connection conn) throws Exception {
		super(conn);
		this.dbType = "MY SQL: ";
		this.schema = DbTypeUtil.getSchema(conn, DbTypeUtil.DBTYPE_MYSQL);
		this.definition = new MysqlTableDefinition(conn);
	}

	protected Table getDBTable(String tableName) {
		return DbTypeUtil.getTable(tableName, DbTypeUtil.DBTYPE_MYSQL, this.connection);
	}
}
