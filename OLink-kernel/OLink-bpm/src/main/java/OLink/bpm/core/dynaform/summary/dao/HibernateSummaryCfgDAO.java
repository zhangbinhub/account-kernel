package OLink.bpm.core.dynaform.summary.dao;


import java.util.Collection;
import java.util.List;

import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.base.dao.HibernateBaseDAO;
import OLink.bpm.core.dynaform.summary.ejb.SummaryCfgVO;
import OLink.bpm.util.StringUtil;
import org.hibernate.Query;
import org.hibernate.Session;

import OLink.bpm.base.action.ParamsTable;
import flex.messaging.io.ArrayList;

/**
 * @author Happy
 *
 */
public class HibernateSummaryCfgDAO extends HibernateBaseDAO<SummaryCfgVO> implements SummaryCfgDAO {

	public HibernateSummaryCfgDAO(String valueObjectName) {
		super(valueObjectName);
	}

	@SuppressWarnings("unchecked")
	public Collection<SummaryCfgVO> queryByFormId(String formId) throws Exception {
		Session session = currentSession();
		Collection<SummaryCfgVO> rtn = new ArrayList();
		if (formId != null && formId.length() > 0) {
			String hql = "FROM " + _voClazzName + " WHERE FORMID = '" + formId+"'";
			Query query = session.createQuery(hql);
			query.setFirstResult(0);
			query.setMaxResults(Integer.MAX_VALUE);

			List<SummaryCfgVO> result = query.list();
			if (!result.isEmpty()) {
				rtn.addAll(result);
			}
		}
		return rtn;
	}

	public SummaryCfgVO findByFormIdAndScope(String formId, int scope)
			throws Exception {
		Session session = currentSession();
		SummaryCfgVO rtn = null;
		if (!StringUtil.isBlank(formId)) {
			String hql = "FROM " + _voClazzName + " WHERE FORMID = '" + formId+"' AND SCOPE="+scope;
			Query query = session.createQuery(hql);
			query.setFirstResult(0);
			query.setMaxResults(1);

			@SuppressWarnings("unchecked")
			List<SummaryCfgVO> result = query.list();
			if (!result.isEmpty()) {
				rtn = (SummaryCfgVO) result.toArray()[0];
			}
		}
		
		return rtn;
	}

	public DataPackage<SummaryCfgVO> queryHomePageSummaryCfgs(ParamsTable params)
			throws Exception {
		String hql = "FROM " + _voClazzName + " WHERE SCOPE=0 OR SCOPE=6";
		String _currpage = params.getParameterAsString("_currpage");
		String _pagelines = params.getParameterAsString("_pagelines");

		int page = (_currpage != null && _currpage.length() > 0) ? Integer.parseInt(_currpage) : 1;
		int lines = (_pagelines != null && _pagelines.length() > 0) ? Integer.parseInt(_pagelines) : Integer.MAX_VALUE;

		return getDatapackage(hql, params, page, lines);
	}

	@SuppressWarnings("unchecked")
	public boolean isExistWithSameTitle(String title, String applicationId)
			throws Exception {
		Session session = currentSession();
		Long count= Long.valueOf(0);
		
		String hql = "select count(*) FROM " + _voClazzName + " WHERE TITLE = '" + title +"' AND APPLICATIONID = '" + applicationId + "'";
		
		Query query = session.createQuery(hql);
		
		List rst = query.list();
		
		if(!rst.isEmpty()){
			count = (Long)rst.get(0);
		}
		return count>0;
	}

}
