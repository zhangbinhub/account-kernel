package OLink.bpm.core.dynaform.document.dql;

public interface SQLFunction {

	/**
	 * 
	 * 组成日期转换函数，转换字段field为数据日期
	 * 
	 * @param field
	 *            字段名
	 * @param patten
	 *            格式
	 * @return 组成日期转换函数后的字串
	 */
	String toDate(String field, String patten);

	/**
	 * 
	 * 组成字符串转换函数，转换字段field为数据为格式字符串
	 * 
	 * @param field
	 *            字段名
	 * @param patten
	 *            格式
	 * @return 转换字段field为数据为格式字符串
	 */
	String toChar(String field, String patten);

	/**
	 * 组成小写转换函数
	 * 
	 * @param s
	 * @return s字串小写转换后的字串
	 */
	String lower(String s);

	/**
	 * 组成大写转换函数
	 * 
	 * @param s
	 * @return s字串大写转换后的字串
	 */
	String upper(String s);

	/**
	 * 获取分页写法
	 * 
	 * @param sql
	 *            ,开始行号,结束行号
	 * @return 包含了分页写法的sql
	 */
	String getLimitString(String sql, int startline, int endline);

	/**
	 * 获取查询条件中为空值的写法
	 * 
	 * @param
	 * @return
	 */
	String getWhereClauseNullString(String coulumName);

}
