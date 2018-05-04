package OLink.bpm.core.email.email.dao;

import java.util.List;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.base.dao.HibernateBaseDAO;
import OLink.bpm.core.email.email.ejb.EmailUser;
import org.hibernate.Query;

public class HibernateEmailUserDAO extends HibernateBaseDAO<EmailUser> implements
		EmailUserDAO {

	public HibernateEmailUserDAO(String voClassName) {
		super(voClassName);
	}

	/**
	 * @SuppressWarnings hibernate3.2不支持泛型
	 */
	@SuppressWarnings("unchecked")
	public EmailUser queryEmailUser(String account, String domainid)
			throws Exception {
		StringBuffer hql = new StringBuffer();
		hql.append("from ").append(_voClazzName).append(" vo where vo.account = ? and vo.domainid = ?");
		Query query = currentSession().createQuery(hql.toString());
		query.setString(0, account);
		query.setString(1, domainid);
		List result = query.list();
		if (result.isEmpty()) {
			return null;
		}
		return (EmailUser) result.get(0);
	}

	/**
	 * @SuppressWarnings hibernate3.2不支持泛型
	 */
	@SuppressWarnings("unchecked")
	public EmailUser queryEmailUserByAccount(String account) throws Exception {
		StringBuffer hql = new StringBuffer();
		hql.append("from ").append(_voClazzName).append(" vo where vo.account = ?");
		Query query = currentSession().createQuery(hql.toString());
		query.setString(0, account);
		List result = query.list();
		if (result.isEmpty()) {
			return null;
		}
		return (EmailUser) result.get(0);
	}

	public DataPackage<EmailUser> queryEmailUsers(String domainid, ParamsTable params) throws Exception {
		StringBuffer hql = new StringBuffer();
		hql.append("from ").append(_voClazzName).append(" vo ");
		hql.append("where vo.domainid = '").append(domainid).append("' ");
		
		String _currpage = params.getParameterAsString("_currpage");
		String _pagelines = params.getParameterAsString("_pagelines");

		int page = (_currpage != null && _currpage.length() > 0) ? Integer.parseInt(_currpage) : 1;
		int lines = (_pagelines != null && _pagelines.length() > 0) ? Integer.parseInt(_pagelines) : 10;
		return getDatapackage(hql.toString(), params, page, lines);
	}

	/**
	 * @SuppressWarnings hibernate3.2不支持泛型
	 */
	@SuppressWarnings("unchecked")
	public EmailUser queryEmailUserByOwner(String ownerid, String domainid)
			throws Exception {
		StringBuffer hql = new StringBuffer();
		hql.append("from ").append(_voClazzName).append(" vo where vo.ownerid = ? and vo.domainid = ?");
		Query query = currentSession().createQuery(hql.toString());
		query.setString(0, ownerid);
		query.setString(1, domainid);
		List result = query.list();
		if (result.isEmpty()) {
			return null;
		}
		return (EmailUser) result.get(0);
	}
	
}
