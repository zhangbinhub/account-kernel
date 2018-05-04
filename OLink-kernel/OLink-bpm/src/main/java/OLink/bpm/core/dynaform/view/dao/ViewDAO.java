package OLink.bpm.core.dynaform.view.dao;

import java.util.Collection;

import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.core.dynaform.view.ejb.View;
import OLink.bpm.base.dao.IDesignTimeDAO;

public interface ViewDAO extends IDesignTimeDAO<View> {
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
	View findViewByName(String name, String application)
			throws Exception;
	
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
	boolean existViewByNameModule(String name, String module)
			throws Exception;

	/**
	 * 根据模块主键与应用标识查询，返回视图的DataPackage
	 * @see DataPackage#datas
	 * @param moduleid
	 *            模块主键
	 * @param application
	 *            应用标识
	 * @return 视图的DataPackage
	 * @throws Exception
	 */

	DataPackage<View> getViewsByModuleId(String moduleid, String application)
			throws Exception;

	/**
	 * 根据模块主键与应用标识查询，返回视图(view)的集合
	 * 
	 * @param moduleid
	 *            模块主键
	 * @param application
	 *            应用标识
	 * @return 视图(view)的集合
	 * @throws Exception
	 */

	Collection<View> getViewByModule(String moduleid, String application)
			throws Exception;
	
	String findViewNameById(String viewid) throws Exception;
	
}
