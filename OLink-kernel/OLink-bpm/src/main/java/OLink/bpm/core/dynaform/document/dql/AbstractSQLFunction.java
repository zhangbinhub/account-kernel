package OLink.bpm.core.dynaform.document.dql;

public class AbstractSQLFunction {

	/**
	 * 
	 * 例： to_date('2003/07/09', 'yyyy/mm/dd') would return a date value of July
	 * 9, 2003. to_date('070903', 'MMDDYY') would return a date value of July 9,
	 * 2003. to_date('20020315', 'yyyymmdd') would return a date value of Mar
	 * 15, 2002.
	 * 
	 * 组成日期转换函数，转换字段field为数据日期
	 * 
	 * @param field
	 *            字段名
	 * @param patten
	 *            格式
	 * @return 组成日期转换函数后的字串
	 */
	public String toDate(String field, String patten) {
		return "TO_DATE(" + field + ", '" + patten + "')";
	}

	/**
	 * 例1： to_char(sysdate, 'yyyy/mm/dd'); would return '2003/07/09'
	 * to_char(sysdate, 'Month DD, YYYY'); would return 'July 09, 2003'
	 * to_char(sysdate, 'FMMonth DD, YYYY'); would return 'July 9, 2003'
	 * to_char(sysdate, 'MON DDth, YYYY'); would return 'JUL 09TH, 2003'
	 * to_char(sysdate, 'FMMON DDth, YYYY'); would return 'JUL 9TH, 2003'
	 * to_char(sysdate, 'FMMon ddth, YYYY'); would return 'Jul 9th, 2003'
	 * 
	 * 例2: to_char(1210.73, '9999.9') would return '1210.7' to_char(1210.73,
	 * '9,999.99') would return '1,210.73' to_char(1210.73, '$9,999.00') would
	 * return '$1,210.73' to_char(21, '000099') would return '000021'
	 * 
	 * 组成字符串转换函数，转换字段field为数据为格式字符串
	 * 
	 * @param field
	 *            字段名
	 * @param patten
	 *            格式
	 * @return 转换字段field为数据为格式字符串
	 */
	public String toChar(String field, String patten) {
		return "TO_CHAR(" + field + ", '" + patten + "')";
	}

	/**
	 * 组成小写转换函数
	 * 
	 * @param s
	 * @return s字串小写转换后的字串
	 */
	public String lower(String s) {
		return "lower(" + s + ")";
	}

	/**
	 * 组成大写转换函数
	 * 
	 * @param s
	 * @return s字串大写转换后的字串
	 */
	public String upper(String s) {
		return "upper(" + s + ")";
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
		return "select * from ( select row_.*, rownum rownum_ from ( " + sql
				+ ") row_ ) where rownum_ < " + endline + " and rownum_ >= "
				+ startline;
	}

	/**
	 * 获取查询条件中为空值的写法
	 * 
	 * @param
	 * @return
	 */
	public String getWhereClauseNullString(String columnName) {
		return columnName + " is null";
	}
}
