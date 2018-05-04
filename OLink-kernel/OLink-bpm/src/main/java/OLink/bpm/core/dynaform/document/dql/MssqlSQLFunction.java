package OLink.bpm.core.dynaform.document.dql;

public class MssqlSQLFunction extends AbstractSQLFunction implements
		SQLFunction {

	public String toChar(String field, String patten) {
		if (patten.trim().equals("yyyy-mm-dd")) {
			return "CONVERT(nvarchar(10), " + field + ", 120) ";
		}
		return "CONVERT(nvarchar(19), " + field + ", 120) ";
	}

	public String toDate(String field, String patten) {
		return "CONVERT(datetime, " + field + ", 120) ";
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
		return "SELECT * FROM ( " + sql + ") TAB_  " + startline + ","
				+ endline;
	}

}
