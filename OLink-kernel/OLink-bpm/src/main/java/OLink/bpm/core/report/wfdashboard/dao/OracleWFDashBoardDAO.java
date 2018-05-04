package OLink.bpm.core.report.wfdashboard.dao;

import java.sql.Connection;

import OLink.bpm.util.DbTypeUtil;

public class OracleWFDashBoardDAO extends AbstractWFDashBoardDAO implements WFDashBoardDAO {

	public OracleWFDashBoardDAO(Connection conn) throws Exception {
		super(conn);
		this.schema = DbTypeUtil.getSchema(conn, DbTypeUtil.DBTYPE_ORACLE);
	}

}
