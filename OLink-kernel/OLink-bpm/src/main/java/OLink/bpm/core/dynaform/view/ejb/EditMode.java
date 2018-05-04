package OLink.bpm.core.dynaform.view.ejb;

import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.core.dynaform.document.ejb.Document;
import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.core.user.action.WebUser;

public interface EditMode {
	/**
	 * 获取查询语句
	 * 
	 * @param params
	 *            参数
	 * @param user
	 *            当前用户
	 * @param sDoc
	 *            查询文档
	 * @return 查询语句
	 */
	String getQueryString(ParamsTable params, WebUser user, Document sDoc) throws Exception;

	/**
	 * 获取文档数据包
	 * 
	 * @param params
	 *            参数
	 * @param user
	 *            当前用户
	 * @param sDoc
	 *            查询文档
	 * @return 文档数据包
	 * @throws Exception
	 */
	DataPackage<Document> getDataPackage(ParamsTable params, WebUser user, Document sDoc) throws Exception;

	/**
	 * 获取文档数据包
	 * 
	 * @param params
	 *            参数
	 * @param page
	 *            当前页码
	 * @param lines
	 *            每页显示的行数
	 * @param user
	 *            当前用户
	 * @param sDoc
	 *            查询文档
	 * @return 文档数据包
	 * @throws Exception
	 */
	DataPackage<Document> getDataPackage(ParamsTable params, int page, int lines, WebUser user, Document sDoc) throws Exception;

	/**
	 * 获取文档总行数
	 * 
	 * @param params
	 *            参数
	 * @param user
	 *            当前用户
	 * @param sDoc
	 *            查询文档
	 * @return 文档总行数
	 * @throws Exception
	 */
	long count(ParamsTable params, WebUser user, Document sDoc) throws Exception;

	/**
	 * 添加查询条件(暂时只能处理字符串条件)
	 * 
	 * @param name
	 *            名称
	 * @param val
	 *            值
	 */
	void addCondition(String name, String val);
	
	void addCondition(String name, String val, String operator);
}
