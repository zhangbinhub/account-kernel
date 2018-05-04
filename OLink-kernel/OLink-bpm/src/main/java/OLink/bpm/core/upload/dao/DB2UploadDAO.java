package OLink.bpm.core.upload.dao;

import java.sql.Connection;

import OLink.bpm.core.dynaform.document.dql.DB2SQLFunction;
import OLink.bpm.util.DbTypeUtil;

public class DB2UploadDAO extends AbstractUploadDAO implements UploadDAO{

	public DB2UploadDAO(Connection conn, String applicationId)
			throws Exception {
		super(conn, applicationId);
		dbType = "DB2: ";
		this.schema = DbTypeUtil.getSchema(conn, DbTypeUtil.DBTYPE_DB2);
		sqlFuction = new DB2SQLFunction();
	}

}
