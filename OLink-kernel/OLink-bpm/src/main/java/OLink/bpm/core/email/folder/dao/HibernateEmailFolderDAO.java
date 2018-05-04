package OLink.bpm.core.email.folder.dao;

import java.util.List;

import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.base.dao.HibernateBaseDAO;
import OLink.bpm.core.email.folder.ejb.EmailFolder;
import org.hibernate.Query;
import org.hibernate.Session;

import OLink.bpm.base.action.ParamsTable;

public class HibernateEmailFolderDAO extends HibernateBaseDAO<EmailFolder> implements
		EmailFolderDAO {

	public HibernateEmailFolderDAO(String voClassName) {
		super(voClassName);
	}
	
	/**
	 * @SuppressWarnings hibernate3.2不支持泛型
	 */
	@SuppressWarnings("unchecked")
	public EmailFolder queryMailFolderById(String folderid) throws Exception {
		String hql = "from " + _voClazzName + " vo where vo.id = ?";
		Session session = currentSession();
		Query query = session.createQuery(hql);
		query.setString(0, folderid);
		List list = query.list();
		if (list != null && !list.isEmpty()) {
			return (EmailFolder) list.get(0);
		}
		return null;
	}

	/**
	 * @SuppressWarnings hibernate3.2不支持泛型
	 */
	@SuppressWarnings("unchecked")
	public boolean judgeMailFolderIsCreate(String folderName, String ownerid) throws Exception {
		String hql = "select count(*) from " + _voClazzName + " vo where vo.name = ? and vo.ownerId = ?";
		try {
			Session session = currentSession();
			Query query = session.createQuery(hql);
			query.setString(0, folderName);
			query.setString(1, ownerid);
			List list = query.list();
			if (list != null && !list.isEmpty()) {
				Long count = (Long) list.get(0);
				if (count.intValue() > 0) {
					return true;
				}
			}
		} catch (Exception e) {
		}
		return false;
	}

	/**
	 * @SuppressWarnings hibernate3.2不支持泛型
	 */
	@SuppressWarnings("unchecked")
	public EmailFolder queryMailFolderByOwnerId(String folderName, String ownerId) throws Exception {
		String hql = "from " + _voClazzName + " vo where vo.ownerId = ? and vo.name= ?";
		Session session = currentSession();
		Query query = session.createQuery(hql);
		query.setString(0, ownerId);
		query.setString(1, folderName);
		List list = query.list();
		if (list != null && !list.isEmpty()) {
			return (EmailFolder) list.get(0);
		}
		return null;
	}

	public DataPackage<EmailFolder> queryPersonalEmailFolders(String ownerid, ParamsTable params)
			throws Exception {
		StringBuffer hql = new StringBuffer();
		hql.append("from " + _voClazzName + " vo where vo.ownerId ='").append(ownerid).append("'");
		hql.append(" order by vo.createDate desc");
		
		String _currpage = params.getParameterAsString("_currpage");
		String _pagelines = params.getParameterAsString("_pagelines");

		int page = (_currpage != null && _currpage.length() > 0) ? Integer.parseInt(_currpage) : 1;
		int lines = (_pagelines != null && _pagelines.length() > 0) ? Integer.parseInt(_pagelines) : 10;
		return getDatapackage(hql.toString(), params, page, lines);
	}
	
	public DataPackage<EmailFolder> getDatapackage(String hql, ParamsTable params, int page,
			int lines) throws Exception {
		DataPackage<EmailFolder> result = new DataPackage<EmailFolder>();
		result.rowCount = getTotalLines(hql);
		result.pageNo = page;
		result.linesPerPage = lines;

		if (result.pageNo > result.getPageCount()) {
			result.pageNo = 1;
			page = 1;
		}

		result.datas = getDatas(hql, page, lines);
		return result;
	}

	/**
	 * @SuppressWarnings API不支持泛型
	 */
	@SuppressWarnings("unchecked")
	public int queryPersonalEmailFolderCount(String ownerid) throws Exception {
		StringBuffer hql = new StringBuffer();
		hql.append("select count(*) from ").append(_voClazzName).append(" vo ");
		hql.append("where vo.ownerId = '").append(ownerid).append("'");
		Query query = currentSession().createQuery(hql.toString());
		List list = query.list();
		if (list != null && !list.isEmpty()) {
			Long count = (Long) list.get(0);
			return count.intValue();
		}
		return 0;
	}

}
