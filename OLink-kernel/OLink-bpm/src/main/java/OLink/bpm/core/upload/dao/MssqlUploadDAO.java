package OLink.bpm.core.upload.dao;

import java.sql.Connection;

import OLink.bpm.core.dynaform.document.dql.MssqlSQLFunction;
import OLink.bpm.util.DbTypeUtil;

public class MssqlUploadDAO  extends AbstractUploadDAO implements UploadDAO{

	public MssqlUploadDAO(Connection conn, String applicationId)
			throws Exception {
		super(conn, applicationId);
		dbType = "MS SQL Server: ";
		this.schema = DbTypeUtil.getSchema(conn, DbTypeUtil.DBTYPE_MSSQL);
		sqlFuction = new MssqlSQLFunction();
	}

}
