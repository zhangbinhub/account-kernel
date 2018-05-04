package OLink.bpm.core.page.dao;

import java.util.Collection;

import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.core.dynaform.form.dao.HibernateFormDAO;
import OLink.bpm.core.page.ejb.Page;
import OLink.bpm.base.action.ParamsTable;

public class HibernatePageDAO extends HibernateFormDAO<Page> implements PageDAO {

	public HibernatePageDAO(String voClassName) {
		super(voClassName);
	}

	public Page findDefaultPage(String application) throws Exception {
		String hql = "from " + _voClazzName + " vo where vo.defHomePage='1'";

		if (application != null && application.length() > 0) {
			hql += (" and applicationid = '" + application + "' ");
		}

		return (Page) getData(hql);
	}

	public Page findByName(String name, String application) throws Exception {
		String hql = "FROM " + _voClazzName + " vo WHERE name='" + name + "'";
		if (application != null && application.length() > 0) {
			hql += (" and applicationid = '" + application + "' ");
		}
		return (Page) getData(hql);
	}

	public DataPackage<Page> getDatasExcludeMod(ParamsTable params, String application)
			throws Exception {
		String hql = "FROM " + _voClazzName + " vo WHERE vo.module is null";

		String _currpage = params.getParameterAsString("_currpage");
		String _pagelines = params.getParameterAsString("_pagelines");

		int page = (_currpage != null && _currpage.length() > 0) ? Integer
				.parseInt(_currpage) : 1;
		int lines = (_pagelines != null && _pagelines.length() > 0) ? Integer
				.parseInt(_pagelines) : Integer.MAX_VALUE;

		if (application != null && application.length() > 0) {
			hql += (" and applicationid = '" + application + "' ");
		}

		return getDatapackage(hql, params, page, lines);
	}

	public Collection<Page> getPagesByApplication(String application)
			throws Exception {
		String hql = "FROM " + _voClazzName + " vo";
		ParamsTable params = new ParamsTable();
		params.setParameter("application", application);
		return getDatas(hql, params);
	}
}
