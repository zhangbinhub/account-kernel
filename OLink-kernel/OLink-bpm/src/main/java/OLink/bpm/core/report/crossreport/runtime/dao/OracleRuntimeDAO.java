package OLink.bpm.core.report.crossreport.runtime.dao;

import java.sql.Connection;

public class OracleRuntimeDAO extends AbstractRuntimeDAO implements RuntimeDAO {

	public OracleRuntimeDAO(Connection conn) throws Exception {
		super(conn);
	}

}
