package OLink.bpm.core.upload.dao;

import java.sql.Connection;

import OLink.bpm.core.dynaform.document.dql.HsqldbSQLFunction;
import OLink.bpm.util.DbTypeUtil;

public class HsqldbUploadDAO extends AbstractUploadDAO implements UploadDAO {

	public HsqldbUploadDAO(Connection conn, String applicationId)
			throws Exception {
		super(conn, applicationId);
		dbType = "HypersonicSQL: ";
		this.schema = DbTypeUtil.getSchema(conn, DbTypeUtil.DBTYPE_HSQLDB);
		sqlFuction = new HsqldbSQLFunction();
	}

}
