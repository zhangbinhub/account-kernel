package OLink.bpm.core.upload.dao;

import java.sql.Connection;

import OLink.bpm.core.dynaform.document.dql.MysqlSQLFunction;
import OLink.bpm.util.DbTypeUtil;

public class MysqlUploadDAO extends AbstractUploadDAO implements UploadDAO {

	public MysqlUploadDAO(Connection conn, String applicationId)
			throws Exception {
		super(conn, applicationId);
		dbType = "MY SQL: ";
		this.schema = DbTypeUtil.getSchema(conn, DbTypeUtil.DBTYPE_MYSQL);
		sqlFuction = new MysqlSQLFunction();
	}

}
