package OLink.bpm.core.dynaform.view.dao;

import java.util.Collection;
import java.util.List;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.base.dao.HibernateBaseDAO;
import OLink.bpm.base.dao.ValueObject;
import OLink.bpm.core.dynaform.view.ejb.View;
import org.hibernate.Query;
import org.hibernate.Session;

public class HibernateViewDAO extends HibernateBaseDAO<View> implements ViewDAO {
	public HibernateViewDAO(String voClassName) {
		super(voClassName);
	}

	/**
	 * @SuppressWarnings Hibernate3.2版本不支持泛型
	 */
	@SuppressWarnings("unchecked")
	public ValueObject find(String id) throws Exception {
		Session session = currentSession();
		// session.clear();
		ValueObject rtn = null;
		if (id != null && id.length() > 0) {
			rtn = (ValueObject) session.get(Class.forName(_voClazzName), id);

			if (rtn == null || rtn.getId() == null) {
				String hql = "FROM " + _voClazzName + " WHERE id='" + id + "'";

				Query query = session.createQuery(hql);

				query.setFirstResult(0);
				query.setMaxResults(1);

				List result = query.list();

				if (!result.isEmpty()) {
					rtn = (ValueObject) result.get(0);
					session.load(rtn, rtn.getId());
				}

			}

		}

		return rtn;

	}

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
	public View findViewByName(String name, String application) throws Exception {
		String hql = "from " + _voClazzName + " vo where vo.name='"+name+"'";

		ParamsTable params = new ParamsTable();
		params.setParameter("application", application);

		Collection<View> coll = getDatas(hql, params);

		if (coll != null && !coll.isEmpty())
			return coll.iterator().next();
		else
			return null;
		// Session session = PersistenceUtils.currentSession();
		// List list = session.createQuery(hql).setString(0, name).list();
		//
		// if (list != null && !list.isEmpty()) {
		// return (View) list.get(0);
		// } else {
		// return null;
		// }
	}

	/**
	 * 根据模块主键与应用标识查询，返回视图(view)的DataPackage
	 * 
	 * @param moduleid
	 *            模块主键
	 * @param application
	 *            应用标识
	 * @return 视图(view)的DataPackage
	 * @throws Exception
	 */
	public DataPackage<View> getViewsByModuleId(String moduleid, String application) throws Exception {
		String hql = "FROM " + _voClazzName + " vo WHERE vo.module.id='" + moduleid + "'";
		ParamsTable params = new ParamsTable();
		params.setParameter("application", application);

		return this.getDatapackage(hql, params);
	}

	/**
	 * 根据模块主键与应用标识，返回视图(view)的集合
	 * 
	 * @param moduleid
	 *            模块主键
	 * @param application
	 *            应用标识
	 * @return 视图(view)的集合
	 * @throws Exception
	 */

	public Collection<View> getViewByModule(String moduleid, String application) throws Exception {
		String hql = "FROM " + _voClazzName + " vo WHERE vo.module.id='" + moduleid + "'";
		ParamsTable params = new ParamsTable();
		params.setParameter("application", application);
		return getDatas(hql, params);
	}
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
	public boolean existViewByNameModule(String name, String module)
			throws Exception {
		String hql = "FROM " + _voClazzName + " vo WHERE vo.module.id='" + module + "' and vo.name='" + name + "'";
		ParamsTable params = new ParamsTable();
		Collection<View> coll = getDatas(hql, params);
		return coll != null && !coll.isEmpty();
	}

	public String findViewNameById(String viewid) throws Exception {
		String hql = "SELECT vo.name FROM " + _voClazzName + " vo WHERE vo.id='" + viewid + "'";
		Object viewName = getData(hql);
		if (viewName != null) {
			return viewName.toString();
		}
		return null;
	}
}
