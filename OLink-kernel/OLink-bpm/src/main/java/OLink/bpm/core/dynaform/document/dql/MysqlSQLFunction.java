package OLink.bpm.core.dynaform.document.dql;

public class MysqlSQLFunction extends AbstractSQLFunction implements
		SQLFunction {

	public String toChar(String field, String patten) {
		if (patten.trim().equalsIgnoreCase("yyyy-MM-dd")) {
			return "DATE_FORMAT(" + field + ", '%Y-%m-%d')";
		}
		return "DATE_FORMAT(" + field + ", '%Y-%m-%d %T')";
	}

	public String toDate(String field, String patten) {
		if (patten.trim().equalsIgnoreCase("yyyy-MM-dd")) {
			return "DATE_FORMAT(" + field + ", '%Y-%m-%d')";
		}
		return "DATE_FORMAT(" + field + ", '%Y-%m-%d %T')";
	}

	/**
	 * 
	 * 获取分页写法
	 * 
	 * @param sql
	 *            ,开始行号,结束行号
	 * @param patten
	 *            格式
	 * @return 包含了分页写法的sql
	 */
	public String getLimitString(String sql, int startline, int endline) {
		return "SELECT * FROM ( " + sql + ") TAB_ LIMIT " + (startline - 1)
				+ " , " + endline;
	}

	/**
	 * 获取查询条件中为空值的写法
	 * 
	 * @param
	 * @return
	 */
	public String getWhereClauseNullString(String columnName) {
		return "(" + columnName + " is null or " + columnName + "='')";
	}

}
