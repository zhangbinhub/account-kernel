package OLink.bpm.core.report.wfdashboard.dao;

import java.sql.Connection;

import OLink.bpm.util.DbTypeUtil;

public class HSQLWFDashBoardDAO extends AbstractWFDashBoardDAO implements WFDashBoardDAO{

	public HSQLWFDashBoardDAO(Connection conn) throws Exception {
		super(conn);
		this.schema = DbTypeUtil.getSchema(conn, DbTypeUtil.DBTYPE_HSQLDB);
	}

}
