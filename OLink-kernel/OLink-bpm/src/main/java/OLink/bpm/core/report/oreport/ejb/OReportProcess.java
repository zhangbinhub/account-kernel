package OLink.bpm.core.report.oreport.ejb;


import java.util.Collection;
import java.util.List;
import java.util.Map;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.base.ejb.IRunTimeProcess;
import OLink.bpm.core.user.action.WebUser;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public interface OReportProcess extends IRunTimeProcess<Object> {
	
	/**
	 * 获得定制图表数据
	 * 
	 * @param jo json相关参数
	 * @param user 对应用户
	 * @return JSON字符串
	 * @throws Exception
	 */
	Collection<Map<String, String>> getCustomizeOReportData(String viewid, String domainid, JSONObject xcolumn, JSONArray ycolumns, JSONArray filter, WebUser user, ParamsTable params) throws Exception;
	
	/**
	 * 获取过滤器项
	 * @param viewid 视图ID
	 * @param domainid 域ID
	 * @param xcolumn X轴
	 * @param ycolumns Y轴
	 * @param filter 过滤器JSONObject对象
	 * @param user 用户
	 * @return JSON字符串
	 * @throws Exception
	 */
	String getFilterItems(String viewid, String domainid, JSONObject xcolumn, JSONArray ycolumns, JSONObject filter, WebUser user, ParamsTable params) throws Exception;
	
	/**
	 * 获得指定列最大值
	 * @param sql
	 * @return
	 * @throws Exception
	 */
	int getMaxColumnValue(String sql) throws Exception;
	
	/**
	 * 获取所有不重复并经过排序的值
	 * @param data
	 * @param yCount
	 * @return
	 */
	List<String[]> getNoDupContent(Collection<Map<String, String>> data, JSONObject xCol, JSONArray yCols);
	
	/**
	 * 单个列构造成两个列
	 * @param xCol
	 * @param yCols
	 * @return
	 */
	Map<String, Object> singleColumnHandle(JSONObject xCol, JSONArray yCols);
	
}
