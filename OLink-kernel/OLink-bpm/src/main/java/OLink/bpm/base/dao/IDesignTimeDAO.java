package OLink.bpm.base.dao;

import java.util.Collection;

import OLink.bpm.core.user.action.WebUser;
import org.hibernate.SessionFactory;

import OLink.bpm.base.action.ParamsTable;

/**
 * @author Jarod
 */
public interface IDesignTimeDAO<E> extends IBaseDAO {

	/**
	 * Get data Object
	 * 
	 * @param hql
	 * @return object
	 * @see IDesignTimeDAO#getData(String)
	 */
	Object getData(String hql) throws Exception;

	/**
	 * Get datas collection
	 * 
	 * @param hql
	 * @return collection
	 * @see IDesignTimeDAO#getDatas(String)
	 */
	Collection<E> getDatas(String hql) throws Exception;

	/**
	 * Get datas collection .
	 * 
	 * @param hql
	 * @param params
	 * @return collection
	 * @see IDesignTimeDAO#getDatas(String,
	 *      Object)
	 */
	Collection<E> getDatas(String hql, ParamsTable params) throws Exception;

	/**
	 * Get datas collection.
	 * 
	 * @param hql
	 * @param params
	 * @param page
	 * @param lines
	 * @return collection Get datas collection
	 * @see IDesignTimeDAO#getDatas(String,
	 *      Object, int, int)
	 */
	Collection<E> getDatas(String hql, ParamsTable params, int page, int lines) throws Exception;

	/**
	 * Get TotalLines
	 * 
	 * @param hql
	 * @return int
	 * 
	 * @see IDesignTimeDAO#getTotalLines(String)
	 */
	int getTotalLines(String hql) throws Exception;

	/**
	 * @param hql
	 * @param page
	 * @param lines
	 * @return Collection Get datas collection.
	 * @see IDesignTimeDAO#getDatas(String, int,
	 *      int)
	 */
	Collection<E> getDatas(String hql, int page, int lines) throws Exception;

	/**
	 * @param hql
	 * @return DataPackage Get the datapackage.
	 * 
	 * @see IDesignTimeDAO#getDatapackage(String)
	 */
	DataPackage<E> getDatapackage(String hql) throws Exception;

	/**
	 * @param hql
	 * @param page
	 * @param lines
	 * @return dataPackape
	 * 
	 * @see IDesignTimeDAO#getDatapackage(String,
	 *      int, int)
	 */
	DataPackage<E> getDatapackage(String hql, int page, int lines) throws Exception;

	/**
	 * 
	 * @see IDesignTimeDAO#getDatapackage(String,
	 *      Object)
	 */
	DataPackage<E> getDatapackage(String hql, ParamsTable params) throws Exception;

	/**
	 * Get the datapackage
	 * 
	 * @param hql
	 * @param params
	 *            Object
	 * @param page
	 *            int
	 * @param lines
	 *            int
	 * @return datapackage
	 * 
	 * @see IDesignTimeDAO#getDatapackage(String,
	 *      Object, int, int)
	 */
	DataPackage<E> getDatapackage(String hql, ParamsTable params, int page, int lines) throws Exception;

	/**
	 * 
	 * @see IDesignTimeDAO#create(ValueObject,
	 *      WebUser)
	 */
	void create(ValueObject vo, WebUser user) throws Exception;

	/**
	 * 
	 * @see IDesignTimeDAO#create(Object)
	 */
	void create(Object po) throws Exception;

	/**
	 * 
	 * @see IDesignTimeDAO#update(ValueObject,
	 *      WebUser)
	 */
	void update(ValueObject vo, WebUser user) throws Exception;

	/**
	 * @see IDesignTimeDAO#update(Object)
	 */
	void update(Object po) throws Exception;

	/**
	 * 
	 * 
	 * @see IDesignTimeDAO#query(ParamsTable)
	 */
	DataPackage<E> query(ParamsTable params) throws Exception;

	SessionFactory buildSessionFactory() throws Exception;

	/**
	 * 
	 * @see IDesignTimeDAO#find(String)
	 */
	ValueObject find(String id) throws Exception;

	ValueObject findByName(String name, String application) throws Exception;

	/**
	 * 
	 * @see IDesignTimeDAO#create(ValueObject)
	 */
	void create(ValueObject vo) throws Exception;

	/**
	 * (non-Javadoc)
	 * 
	 * @see IBaseDAO#update(ValueObject)
	 */
	void update(ValueObject vo) throws Exception;

	/**
	 * 
	 * @see IDesignTimeDAO#remove(String)
	 */
	void remove(String id) throws Exception;

	void remove(ValueObject obj) throws Exception;

	void remove(String ids[]) throws Exception;

	void remove(Collection<E> vos) throws Exception;

	/**
	 * query datapackage
	 * 
	 * @param params
	 *            ParamsTable
	 * @param user
	 *            WebUser
	 * @return datapackage
	 */
	DataPackage<E> query(ParamsTable params, WebUser user) throws Exception;

	/**
	 * Get datas collection by the Parameters
	 * 
	 * @param params
	 *            ParamsTable
	 * @see ParamsTable#params
	 * @see IDesignTimeDAO#simpleQuery(ParamsTable)
	 */
	Collection<E> simpleQuery(ParamsTable params) throws Exception;

	/**
	 * 根据SQL查询
	 * 
	 * @param sql
	 * @param params
	 * @param params
	 * @param page
	 * @param lines
	 * @return
	 * @throws Exception
	 */
	DataPackage<E> getDatapackageBySQL(String sql, ParamsTable params, int page, int lines) throws Exception;

	/**
	 * 获取hibernate配置文件的default_schema
	 * 
	 * @return
	 */
	String getSchema();

	Collection<E> getDatasBySQL(String sql, int page, int lines) throws Exception;

	Collection<E> getDatasBySQL(String sql) throws Exception;
	
	Collection<E> queryByHQL(String hql, int pageNo, int pageSize) throws Exception;

	int executeUpdate(String hql) throws Exception;
	
	/**
	 * chekcout
	 * @param id
	 * @param user
	 * @throws Exception
	 */
	void checkout(String id, WebUser user) throws Exception;
	
	/**
	 * checkin
	 * @param id
	 * @param user
	 * @throws Exception
	 */
	void checkin(String id, WebUser user) throws Exception;
}
