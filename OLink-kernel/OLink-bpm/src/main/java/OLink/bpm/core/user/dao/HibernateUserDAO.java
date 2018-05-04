package OLink.bpm.core.user.dao;

import java.util.Collection;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Expression;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.base.dao.HibernateBaseDAO;
import OLink.bpm.base.dao.ValueObject;
import OLink.bpm.core.domain.dao.DomainDAO;
import OLink.bpm.core.domain.dao.HibernateDomainDAO;
import OLink.bpm.core.domain.ejb.DomainVO;
import OLink.bpm.core.user.ejb.UserVO;

public class HibernateUserDAO extends HibernateBaseDAO<UserVO> implements UserDAO {
	public HibernateUserDAO(String voClassName) {
		super(voClassName);
	}

	/**
	 * @SuppressWarnings hibernate API不支持泛型
	 */
	@SuppressWarnings("unchecked")
	public UserVO login(String loginno, String domain) throws Exception {
		// String hql = "FROM " + _voClazzName + " as vo WHERE vo.loginno=:login
		// and vo.domainid=:domain";
		// Query query = currentSession().createQuery(hql);
		// query.setString("login", loginno);
		// query.setString("domainid", domain);
		Session session = currentSession();
		Criteria criteria = session.createCriteria(this._voClazzName);
		//change by lr change like to eq for app billities get higher 20161120
		criteria = criteria.add(Expression.eq("loginno", loginno));
		criteria = criteria.add(Expression.eq("domainid", domain));
		List<UserVO> list = criteria.list();
		if (list.isEmpty()) {
			return null;
		} else {
			return list.get(0);
		}
	}

	/**
	 * @SuppressWarnings hibernate API不支持泛型
	 */
	@SuppressWarnings("unchecked")
	public UserVO login(String loginno) throws Exception {
		String hql = "FROM " + _voClazzName + " as vo WHERE vo.loginno=:login";
		Query query = currentSession().createQuery(hql);
		query.setString("login", loginno);

		List<UserVO> list = query.list();
		if (list.isEmpty()) {
			return null;
		} else {
			return list.get(0);
		}
	}

	/**
	 * find the value object by primary key
	 *
	 * @SuppressWarnings hibernate API不支持泛型
	 * @param id
	 *            primary key
	 * @see OLink.bpm.base.dao.IDesignTimeDAO#find(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public ValueObject find(String id) throws Exception {
		Session session = currentSession();
		ValueObject rtn = null;
		if (id != null && id.length() > 0) {
			String hql = "FROM " + _voClazzName + " WHERE id='" + id + "'";
			Query query = session.createQuery(hql);
			query.setFirstResult(0);
			query.setMaxResults(1);
			List result = query.list();

			if (!result.isEmpty()) {
				rtn = (ValueObject) result.get(0);
			}
		}
		return rtn;

	}

	public Collection<UserVO> getDatasByRoleid(String parent, String domain) throws Exception {
		String hql = "FROM " + _voClazzName + " vo WHERE vo.userRoleSets.roleId like '%" + parent + "%'";

		ParamsTable params = new ParamsTable();
		if (domain != null)
			params.setParameter("domainId", domain);
		return getDatas(hql, params);
	}

	public Collection<UserVO> getDatasByDept(String parent, String domain) throws Exception {
		String hql = "FROM " + _voClazzName + " vo WHERE vo.userDepartmentSets.departmentId = '" + parent
				+ "' and vo.domainid ='" + domain + "'";
		return getDatas(hql);
	}

	public Collection<UserVO> getDatasByDept(String parent) throws Exception {
		String hql = "FROM " + _voClazzName + " vo WHERE vo.userDepartmentSets.departmentId = '" + parent + "'";
		return getDatas(hql);
	}

	public Collection<UserVO> queryHasMail(String application) throws Exception {
		return queryHasMail(application, null);
	}

	public Collection<UserVO> queryHasMail(String application, String domain) throws Exception {
		String hql = "FROM " + _voClazzName + " vo WHERE vo.email IS NOT NULL";
		if (application != null && application.trim().length() > 0) {
			hql += " AND vo.applicationid='" + application + "'";
		}
		if (domain != null && domain.trim().length() > 0) {
			hql += " and vo.domainid = '" + domain + "'";
		}
		return getDatas(hql);
	}

	/**
	 * @SuppressWarnings hibernate API不支持泛型
	 */
	@SuppressWarnings("unchecked")
	public boolean isEmpty() throws Exception {
		String hql = "FROM " + _voClazzName;
		Session session = currentSession();
		Query query = session.createQuery(hql);
		List rst = query.list();
		if (!rst.isEmpty()) {
			return false;
		} else
			return true;
	}

	public DataPackage<UserVO> queryByRoleId(String roleid) throws Exception {
		String hql = "FROM " + _voClazzName + " vo where vo.userRoleSets.roleId = '" + roleid + "'";
		return getDatapackage(hql, 1, Integer.MAX_VALUE);
	}

	/**
	 * @SuppressWarnings hibernate API不支持泛型
	 */
	@SuppressWarnings("unchecked")
	public Collection<UserVO> queryByDomain(String domainid, int page, int line) throws Exception {
		String hql = "FROM " + _voClazzName + " vo";
		if (domainid != null && domainid.trim().length() > 0) {
			hql += " WHERE vo.domainid='" + domainid + "'";
		}
		Query query = currentSession().createQuery(hql);
		query.setFirstResult((page - 1) * line);
		query.setMaxResults(line);
		return query.list();
	}

	public UserVO findByLoginno(String account, String domainid) throws Exception {
		String hql = "FROM " + _voClazzName + " vo WHERE vo.loginno ='" + account + "'" + " AND vo.domainid='"
				+ domainid + "'";
		return (UserVO) getData(hql);

	}

	public UserVO findByLoginnoAndDoaminName(String loginno, String domainName) throws Exception {
		DomainDAO domainDAO = new HibernateDomainDAO(DomainVO.class.getName());
		DomainVO domain = domainDAO.getDomainByName(domainName);
		return findByLoginno(loginno, domain.getId());
	}

	public DataPackage<UserVO> listLinkmen(ParamsTable params) throws Exception {
		String hql = "FROM " + _voClazzName + " vo where vo.telephone is not null";
		String _currpage = params.getParameterAsString("_currpage");
		String _pagelines = params.getParameterAsString("_pagelines");

		int page = (_currpage != null && _currpage.length() > 0) ? Integer.parseInt(_currpage) : 1;
		int lines = (_pagelines != null && _pagelines.length() > 0) ? Integer.parseInt(_pagelines) : Integer.MAX_VALUE;

		return getDatapackage(hql, params, page, lines);
	}

	// @SuppressWarnings("unchecked")
	public Collection<UserVO> queryUsersByName(String username, String domainid) throws Exception {
		String hql = "FROM " + _voClazzName + " vo WHERE vo.name = '" + username + "' AND vo.domainid='" + domainid
				+ "'";
		return getDatas(hql);
	}

	// @SuppressWarnings("unchecked")
	public Collection<UserVO> queryByProxyUserId(String proxyid) throws Exception {
		String hql = "FROM " + _voClazzName + " vo";
		if (proxyid != null && proxyid.trim().length() > 0) {
			hql += " WHERE vo.proxyUser='" + proxyid + "'";
		}

		return getDatas(hql);
	}

	/**
	 * 更新用户默认应用
	 *
	 * @param userid
	 *            用户ID
	 * @param defaultApplicationid
	 *            默认选择应用ID
	 * @throws Exception
	 */
	public void updateDefaultApplication(String userid, String defaultApplicationid) throws Exception {
		Session session = currentSession();

		String hqlUpdate = "update " + _voClazzName
				+ " vo set vo.defaultApplication = :defaultApplication where vo.id = :id";
		session.createQuery(hqlUpdate).setString("defaultApplication", defaultApplicationid).setString("id", userid)
				.executeUpdate();
	}

	public Collection<UserVO> queryByHQL(String hql) throws Exception {

		return getDatas(hql);
	}
}
