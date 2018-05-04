// Source file:
// C:\\Java\\workspace\\SmartWeb3\\src\\com\\cyberway\\dynaform\\form\\dao\\FormDAO.java

package OLink.bpm.core.dynaform.form.dao;

import java.util.Collection;

import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.base.dao.IDesignTimeDAO;
import OLink.bpm.core.dynaform.form.ejb.Form;
import OLink.bpm.base.action.ParamsTable;

/**
 * 
 * @author Marky
 * 
 */
public interface FormDAO<E> extends IDesignTimeDAO<E> {
	/**
	 * 根据表单名以及应用标识查询,返回表单对象.
	 * 
	 * @param formName
	 *            表单名
	 * @param application
	 *            应用标识
	 * @return 表单对象
	 */
	Form findByFormName(String formName, String application) throws Exception;

	/**
	 * 根据关联名以及应用标识查询,返回表单对象.
	 * 
	 * @param relationName
	 *            关联名
	 * @param application
	 *            应用标识
	 * @return 表单对象
	 */
	Form findFormByRelationName(String relationName, String application) throws Exception;

	/**
	 * 根据所属模块以及应用标识查询,返回相应表单集合.
	 * 
	 * @param application
	 *            应用标识
	 * @param 所属模块主键
	 * @return 表单集合
	 * @throws Exception
	 */
	Collection<E> getFormsByModule(String moduleid, String application) throws Exception;

	/**
	 * 根据所属Module以及应用标识, 返回查询表单集合.
	 * 
	 * @param moduleid
	 *            模块主键
	 * @param application
	 *            应用标识
	 * @return Search Form 集合
	 * @throws Exception
	 */
	Collection<E> getSearchFormsByModule(String moduleid, String application) throws Exception;

	/**
	 * 根据所属Module以及应用标识, 返回表单集合.
	 * 
	 * @param moduleid
	 *            模块主键
	 * @param application
	 *            应用标识
	 * @return Form 集合
	 * @throws Exception
	 */
	Collection<E> getRelatedFormsByModule(String moduleid, String application) throws Exception;

	/**
	 * 根据应用标识查询,返回相应查询表单集合.
	 * 
	 * @param application
	 *            应用标识
	 * @param appid
	 *            应用标识
	 * @return 查询表单集合
	 * @throws Exception
	 */

	Collection<E> getSearchFormsByApplication(String appid, String application) throws Exception;

	/**
	 * 根据参数条件以及应用标识查询,返回表单的DataPackage. DataPackage为一个封装类，此类封装了所得到的Form数据并分页。
	 * 
	 * @see DataPackage#datas
	 * @see DataPackage#getPageCount()
	 * @see DataPackage#getLinesPerPage()
	 * @see DataPackage#getPageNo()
	 * @see ParamsTable#params
	 * @param params
	 *            参数表
	 * @application 应用标识
	 * @return 表单的DataPackage
	 */
	DataPackage<E> queryForm(ParamsTable params, String application) throws Exception;
	
	/**
	 * 根据模块查找模板表单的集合
	 * @param application
	 * @return
	 * @throws Exception
	 */
	Collection<E> queryTemplateFormsByModule(String moduleid, String application) throws Exception;
	
	/**
	 * 根据模板获取普通(含映射)表单的集合
	 * @param moduleid
	 * @param application
	 * @return
	 * @throws Exception
	 */
	Collection<E> queryNormalFormsByModule(String moduleid, String application) throws Exception;

}
