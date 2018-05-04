package OLink.bpm.core.dynaform.pending.dao;

import java.sql.Connection;

import OLink.bpm.util.DbTypeUtil;

public class MysqlPendingDAO extends AbstractPendingDAO implements PendingDAO {

	public MysqlPendingDAO(Connection connection) {
		super(connection);
		dbType = "MY SQL: ";
		this.schema = DbTypeUtil.getSchema(connection, DbTypeUtil.DBTYPE_MYSQL);
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

		pagingSelect.append("SELECT * FROM (");
		pagingSelect.append(sql);
		pagingSelect.append(" ) AS TB LIMIT " + to + "," + lines);

		return pagingSelect.toString();
	}
}
