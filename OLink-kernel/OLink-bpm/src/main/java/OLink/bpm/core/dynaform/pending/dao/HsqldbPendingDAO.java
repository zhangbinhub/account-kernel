package OLink.bpm.core.dynaform.pending.dao;

import java.sql.Connection;

import OLink.bpm.util.DbTypeUtil;

public class HsqldbPendingDAO extends AbstractPendingDAO implements PendingDAO {

	public HsqldbPendingDAO(Connection connection) {
		super(connection);
		dbType = "HypersonicSQL: ";
		this.schema = DbTypeUtil
				.getSchema(connection, DbTypeUtil.DBTYPE_HSQLDB);
	}

	/**
	 * 生成限制条件sql.
	 * 
	 * @param sql
	 *            sql语句
	 * @param page
	 *            当前页码
	 * @param lines
	 *            每页显示行数
	 * @return 生成限制条件sql语句字符串
	 */
	public String buildLimitString(String sql, int page, int lines) {
		if (lines == Integer.MAX_VALUE) {
			return sql;
		}

		int to = (page - 1) * lines;
		StringBuffer pagingSelect = new StringBuffer(100);
		int ind = sql.indexOf("ORDER BY");
		String orderby = "";
		if (ind > 0) {
			orderby = sql.substring(ind);
			sql = sql.substring(0, ind);
		}
		pagingSelect.append("SELECT LIMIT " + to + " " + lines + " * FROM (");
		pagingSelect.append(sql);
		pagingSelect.append(" ) AS TTTB " + orderby);

		return pagingSelect.toString();
	}
}
