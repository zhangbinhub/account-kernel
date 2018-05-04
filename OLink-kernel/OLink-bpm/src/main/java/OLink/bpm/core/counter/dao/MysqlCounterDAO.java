package OLink.bpm.core.counter.dao;

import java.sql.Connection;

import OLink.bpm.util.DbTypeUtil;
/**
 * 
 * @author Chris
 *
 */
public class MysqlCounterDAO extends AbstractCounterDAO implements CounterDAO {

	public MysqlCounterDAO(Connection conn) throws Exception {
		super(conn);
		dbType="MY SQL: ";
		this.schema = DbTypeUtil.getSchema(conn, DbTypeUtil.DBTYPE_MYSQL);
	}

	
}
