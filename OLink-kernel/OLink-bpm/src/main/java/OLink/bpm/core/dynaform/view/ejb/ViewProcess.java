package OLink.bpm.core.dynaform.view.ejb;

import java.util.Collection;
import java.util.Date;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.base.ejb.IDesignTimeProcess;
import OLink.bpm.core.dynaform.document.ejb.Document;

/**
 * 视图操作接口, 多种过滤视图方式,通过视图的查询导出Excel 过滤方式如下: 1.设计 : 通过设计相关联字段显示过滤视图,过滤字段分别为:
 * 用户,角色,部门,和所的有表单表单, 2.DQL :
 * 可运用自定义的DQL语句.DQL语句类似HQL语句,语法为:$formname=formname(模块表单名(以tlk_开头))+ 查询条件,
 * 在写DQL的同时也可以加系统变量为过滤条件,语法"$"+属性名如:$id,$formname等,具体可以参考文档的属性 3.SQL : 标准的SQL
 * 
 * 
 */
public interface ViewProcess extends IDesignTimeProcess<View> {
	/**
	 * 根据视图名与应用标识查询,返回视图对象
	 * 
	 * @param name
	 *            视图名
	 * @param application
	 *            应用标识
	 * @return 视图对象
	 * @throws Exception
	 */
	View getViewByName(String name, String application) throws Exception;
	
	/**
	 * 根据视图名与模块标识查询,返回是否视图对象
	 * 
	 * @param name
	 *            视图名
	 * @param module
	 *            模块标识
	 * @return 是否视图对象
	 * @throws Exception
	 */
	boolean get_existViewByNameModule(String name, String module)
			throws Exception;

	/**
	 * 根据应用标识查询，返回视图集合
	 * 
	 * @param application
	 *            应用标识
	 * @return 视图集合
	 * @throws Exception
	 */

	Collection<View> get_viewList(String application) throws Exception;

	/**
	 * 根据模块主键与应用标识查询，返回视图(view)的DataPackage.
	 * 
	 * @param moduleid
	 *            模块主键
	 * @param application
	 *            应用标识
	 * @return 视图(view)的DataPackage
	 * @throws Exception
	 */

	DataPackage<View> getViewsByModuleId(String moduleid, String application)
			throws Exception;

	/**
	 * 根据模块主键与应用标识查询，返回视图(view)集合
	 * 
	 * @param moduleid
	 *            模块主键
	 * @param application
	 *            应用标识
	 * @return 视图(view)集合
	 * @throws Exception
	 */

	Collection<View> getViewsByModule(String moduleid, String application)
			throws Exception;

	/**
	 * 把视图中显示的文档转换为Excel文件
	 * 
	 * @param user
	 *            用户
	 * @param viewid
	 *            视图标识
	 * @param params
	 *            参数 文件名称 {filename: 文件名, filedir: 文件所在目录, formfiled: 表单各字段值}
	 * @throws Exception
	 * @return 返回文件名
	 */
	String expDocToExcel(String viewid, WebUser user, ParamsTable params)
			throws Exception;

	/**
	 * 根据条件过滤视图 ,返回过滤视图(view)的过滤条件
	 * 
	 * @param view
	 *            视图
	 * @param params
	 *            参数
	 * @param user
	 *            用户
	 * @param sDoc
	 *            查询文档
	 * @param applicationid
	 *            用户标识
	 * @return (java.lang.String)过滤条件
	 */
	String getQueryString(View view, ParamsTable params, WebUser user,
						  Document sDoc);

	/**
	 * 删除多个视图对象
	 * 
	 * @param viewList
	 *            视图对象集合
	 * @throws Exception
	 */
	void doRemove(Collection<View> viewList) throws Exception;

	DataPackage<Document> getDataPackage(View view, ParamsTable params,
										 WebUser user, String applicationid, Date stDate, Date endDate, int lines) throws Exception;
	
	/**
	 * 根据View ID查找View名称
	 * @param viewid
	 * @return View名称
	 * @throws Exception
	 */
	String getViewNameById(String viewid) throws Exception;
	
}
