package OLink.bpm.core.resource.dao;

import java.util.Collection;
import java.util.Iterator;

import OLink.bpm.base.dao.ValueObject;
import OLink.bpm.core.dynaform.view.ejb.View;
import org.hibernate.Session;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.base.dao.HibernateBaseDAO;
import OLink.bpm.core.resource.ejb.ResourceVO;

public class HibernateResourceDAO extends HibernateBaseDAO<ResourceVO> implements ResourceDAO {
	/**
	 * @param voClassName
	 *            The value object class name.
	 */
	public HibernateResourceDAO(String voClassName) {
		super(voClassName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ResourceDAO#getFamilyTree(java.lang.String)
	 */
	public Collection<ResourceVO> getFamilyTree(String parent, String application) throws Exception {
		String hql = "from " + _voClazzName + " vo ";

		if (parent != null && parent.length() > 0)
			hql += " where vo.parentid = '" + parent + "'";

		hql += " order by vo.orderno,vo.superiorid";

		ParamsTable params = new ParamsTable();
		params.setParameter("application", application);
		return getDatas(hql, params);
	}

	public void create(ValueObject vo) throws Exception {
		try {
			super.create(vo);
		} catch (Exception e) {
			throw e;
		} finally {
		}
	}

	public void remove(String id) throws Exception {
		try {
			super.remove(id);
		} catch (Exception e) {
			throw e;
		} finally {
		}

	}

	public void update(ValueObject vo) throws Exception {
		try {
			super.update(vo);
		} catch (Exception e) {
			throw e;
		} finally {
		}
	}

	/**
	 * @SuppressWarnings hibernate3.2不支持泛型
	 */
	@SuppressWarnings("unchecked")
	public Collection<ResourceVO> getProtectResources(String application) throws Exception {
		String hql = "FROM " + _voClazzName + " vo ";
		hql += " WHERE vo.isprotected is true";
		hql += " AND vo.applicationid='" + application + "'";
		Session session = currentSession();
		return session.createQuery(hql).list();
	}

	public Collection<ResourceVO> getTopProtectResources(String application) throws Exception {
		String hql = "FROM " + _voClazzName + " vo ";
		hql += " WHERE vo.isprotected is true";
		hql += " AND vo.applicationid='" + application + "'";
		hql += " AND vo.superior is null";
		return getDatas(hql);
	}

	public Collection<ResourceVO> queryTopResources(String application) throws Exception {
		String hql = "FROM " + _voClazzName + " vo ";
		hql += " WHERE vo.applicationid='" + application + "'";
		hql += " AND vo.superior is null";
		return getDatas(hql);
	}

	public ResourceVO getResourceByViewId(String viewId, String application) throws Exception {

		String hql = "FROM " + _voClazzName + " vo where vo.applicationid='" + application + "'";
		hql += " AND vo.displayView='" + viewId + "'";
		return (ResourceVO) getData(hql);
	}

	/**
	 * 获得父级
	 * 
	 * @param parent
	 * @return
	 * @throws Exception
	 */
	public Collection<ResourceVO> getDatasByParent(String parent) throws Exception {
		//long start = System.currentTimeMillis();
		String hql = "FROM " + _voClazzName + " vo WHERE vo.superior = '" + parent + "'";
		Collection<ResourceVO> rtn = getDatas(hql, null);
		return rtn;
	}
	
	/**
	 * 删除所有引用视图对象集合vos中的视图对象的菜单
	 * 
	 * @param vos 视图对象集合
	 * @throws Exception
	 */
	public void removeByViewList(Collection<View> vos, String application) throws Exception {
		String hql = "Delete " + _voClazzName + " where resourceAction='01' ";
		hql += " and applicationid='" + application + "'";
		StringBuffer mids = new StringBuffer();
		for (Iterator<View> iterator = vos.iterator(); iterator.hasNext();) {
			View vo = iterator.next();
			mids.append("'").append(vo.getId()).append("',");
		}
		if (mids.length() > 0)
			mids.setLength(mids.length() - 1);
		if (vos.size() > 1) {
			hql += " and displayView  in (" + mids.toString() + ")";
		} else {
			hql += " and displayView  = " + mids.toString();
		}
		executeUpdate(hql);
	}

}
