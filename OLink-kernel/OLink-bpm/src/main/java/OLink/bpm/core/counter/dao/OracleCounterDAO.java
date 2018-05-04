package OLink.bpm.core.counter.dao;

import java.sql.Connection;

import OLink.bpm.util.DbTypeUtil;

public class OracleCounterDAO extends AbstractCounterDAO implements CounterDAO {
	
	public OracleCounterDAO(Connection conn) throws Exception {
		super(conn);
		this.schema = DbTypeUtil.getSchema(conn, DbTypeUtil.DBTYPE_ORACLE);
	}

}
