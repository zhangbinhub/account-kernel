package OLink.bpm.core.workflow.storage.runtime.dao;

import java.sql.Connection;

/**
 * 
 * @author Chris
 * 
 */
public class HsqldbCirculatorDAO extends AbstractCirculatorDAO implements CirculatorDAO {

	public HsqldbCirculatorDAO(Connection conn) throws Exception {
		super(conn);
		dbTag = "HypersonicSQL: ";
		if (conn != null) {
			this.schema = "public".toUpperCase();
		}
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
