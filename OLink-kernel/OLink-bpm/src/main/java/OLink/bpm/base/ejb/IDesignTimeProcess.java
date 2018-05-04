package OLink.bpm.base.ejb;

import java.io.Serializable;
import java.util.Collection;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.base.dao.ValueObject;
import OLink.bpm.core.user.action.WebUser;

/**
 * The base process interface.
 */
public interface IDesignTimeProcess<E> extends Serializable {
	/**
	 * 创建数据文档对象
	 * 
	 * @param object
	 *            数据文档对象.
	 * @throws Exception
	 */
	void doCreate(ValueObject object) throws Exception;

	/**
	 * 批量创建数据文档对象
	 * 
	 * @param vos
	 *            数据文档对象数组
	 * @throws Exception
	 */
	void doCreate(ValueObject[] vos) throws Exception;

	/**
	 * 批量创建数据文档对象
	 * 
	 * @param vos
	 *            数据文档对象集合
	 * @throws Exception
	 */
	void doCreate(Collection<ValueObject> vos) throws Exception;

	/**
	 * 创建数据文档对象
	 * 
	 * @param object
	 *            数据文档对象.
	 * @param user
	 *            当前登录用户.
	 * @throws Exception
	 */
	void doCreate(ValueObject object, WebUser user) throws Exception;

	/**
	 * 移除数据文档对象
	 * 
	 * @param pk
	 *            数据文档对象ID标识.
	 * @throws Exception
	 */
	void doRemove(String pk) throws Exception;

	/**
	 * 批量移除数据文档对象
	 * 
	 * @param pks
	 *            数据文档对象ID标识数组.
	 * @throws Exception
	 */
	void doRemove(String[] pks) throws Exception;

	/**
	 * 更新数据文档对象.
	 * 
	 * @param object
	 *            数据文档对象.
	 * @throws Exception
	 */
	void doUpdate(ValueObject object) throws Exception;

	/**
	 * 批量更新数据文档对象.
	 * 
	 * @param vos
	 *            数据文档对象数组.
	 * @throws Exception
	 */
	void doUpdate(ValueObject[] vos) throws Exception;

	/**
	 * 批量更新数据文档对象.
	 * 
	 * @param vos
	 *            数据文档对象集合.
	 * @throws Exception
	 */

	void doUpdate(Collection<ValueObject> vos) throws Exception;

	/**
	 * 更新数据文档对象.
	 * 
	 * @param object
	 *            数据文档对象.
	 * @param user
	 *            当前登录用户.
	 * @throws Exception
	 */
	void doUpdate(ValueObject object, WebUser user) throws Exception;

	/**
	 * 根据数据文档对象的ID标识查找数据文档对象.
	 * 
	 * @param pk
	 *            数据文档对象ID标识.
	 * @return 数据文档对象.
	 * @throws Exception
	 */
	ValueObject doView(String pk) throws Exception;

	/**
	 * 根据名称查找值对象
	 * 
	 * @param name
	 *            值对象名称
	 * @param application
	 *            软件ID
	 * @return 值对象
	 * @throws Exception
	 */
	ValueObject doViewByName(String name, String application) throws Exception;

	/**
	 * 根据条件列表查询数据文档对象记录.
	 * 
	 * @param params
	 *            条件列表.
	 * @param user
	 *            当前登录用户.
	 * @return 数据文档记录包装对象.
	 * @throws Exception
	 */
	DataPackage<E> doQuery(ParamsTable params, WebUser user) throws Exception;

	/**
	 * 根据条件列表查询数据文档对象记录.
	 * 
	 * @param params
	 *            条件列表.
	 * @return 数据文档记录包装对象.
	 * @throws Exception
	 */
	DataPackage<E> doQuery(ParamsTable params) throws Exception;

	/**
	 * 根据条件列表查询数据文档对象记录.
	 * 
	 * @param params
	 *            条件列表.
	 * @return 数据文档记录集合.
	 * @throws Exception
	 */
	Collection<E> doSimpleQuery(ParamsTable params) throws Exception;

	/**
	 * 根据条件列表查询数据文档对象记录.
	 * 
	 * @param params
	 *            条件列表.
	 * @param application
	 *            应用软件ID标识
	 * @return 数据文档记录集合.
	 * @throws Exception
	 */
	Collection<E> doSimpleQuery(ParamsTable params, String application) throws Exception;
	
	
	/**
	 * 根据编写的hql语句查询记录集合
	 * @param hql
	 * @return
	 * @throws Exception
	 */
	Collection<E>doQueryByHQL(String hql, int pageNo, int pageSize) throws Exception;

	/**
	 * 移除数据文档对象
	 * 
	 * @param vo
	 *            数据文档对象.
	 * @throws Exception
	 */
	void doRemove(ValueObject vo) throws Exception;
	
	
	/**
	 * 创建或更新对象
	 * @param vo
	 * @throws Exception
	 */
	void doCreateOrUpdate(ValueObject vo) throws Exception;

	/**
	 * Ajax检查name是否已近存在 param name return Boolean
	 */
	boolean checkExitName(String name, String application) throws Exception;
	/**
	 * 通过hql语句获得数据总数
	 * @param hql
	 * @return
	 * @throws Exception
	 */
	int doGetTotalLines(String hql) throws Exception;
	
	/**
	 * Checkout
	 * @param id
	 * @param user
	 * @throws Exception
	 */
	void doCheckout(String id, WebUser user) throws Exception;
	
	/**
	 * Checkin
	 * @param id
	 * @param user
	 * @throws Exception
	 */
	void doCheckin(String id, WebUser user) throws Exception;
}
