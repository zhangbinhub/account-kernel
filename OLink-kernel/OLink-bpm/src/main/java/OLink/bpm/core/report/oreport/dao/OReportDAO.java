package OLink.bpm.core.report.oreport.dao;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import OLink.bpm.core.report.basereport.dao.ReportDAO;

public interface OReportDAO extends ReportDAO{

	/**
	 * 将视图的DQL转换为SQL
	 * 
	 * @param dql 视图的DQL
	 * @return 视图的SQL
	 * @throws Exception
	 */
	String dqlParseSql(String dql) throws Exception;
	
	/**
	 * 将获取数据保存为ArrayList
	 * 
	 * @param sql 执行的SQL
	 * @return ArrayList对象
	 * @throws Exception
	 */
	Collection<Map<String, String>> getData(String sql, int max) throws Exception;
	
	/**
	 * 单列处理
	 * 
	 * @param column JSONObject对象的列
	 * @param tabName 查询表名(为视图SQL)
	 * @return 拼凑出来的SQL语句
	 * @throws Exception
	 */
	Map<String, String> getSingleAxisSqlAndBy(JSONObject column, String tabName) throws Exception;
	
	/**
	 * 多列处理
	 * 
	 * @param xcolumn X列的JSONObject对象
	 * @param ycolumn Y列的JSONObject对象
	 * @param tabName 表名(视图SQL)
	 * @return 拼凑的SQL语句
	 */
	String getMultipleAxisesSql(JSONObject xcolumn, JSONArray ycolumns, String tabName);
	//public String getMultipleAxisesSql(JSONObject xcolumn, List<Map<String, JSONObject>> nodupYAxis, String tabName);
	
	/**
	 * 拼凑gorup by和order by语句
	 * 
	 * @param xcolName X列名
	 * @param ycolumns 不重复的Y列Map
	 * @return group by和order by语句
	 */
	Map<String, String> getGroupAndOrderBy(JSONObject xcolumn, JSONArray ycolumns);
	//public String getGroupAndOrderBy(Set ycolumns);
	
	/**
	 * 获取日期函数
	 * 
	 * @param fx 日期函数
	 * @param column 列名
	 * @return 对应数据库日期函数
	 */
	String getDateFunc(String fx, String column);
	
	/**
	 * 获取日期函数与分组排序函数
	 * 
	 * @param fx 函数
	 * @param column 列名
	 * @return 对应数据库by子句函数
	 */
	String getBy(String fx, String column);
	
	/**
	 * 获取Filter的项
	 * @param tabName
	 * @param column
	 * @param goby
	 */
	List<Map<String, String>> getFilterItemsData(String tabName, JSONObject filter, String goby) throws Exception;
	
	/**
	 * 获取where条件子句
	 * @param whereFilters
	 * @return
	 */
	String getWhereFilterSql(JSONArray whereFilters);
	
	/**
	 * 获取having条件子句
	 * @param havingFilters
	 * @return
	 */
	String getHavingFilterSql(JSONArray havingFilters);
	
	/**
	 * 获得指定列最大值
	 * @param sql
	 * @return
	 * @throws Exception
	 */
	int getMaxColumnValue(String sql) throws Exception;

}
