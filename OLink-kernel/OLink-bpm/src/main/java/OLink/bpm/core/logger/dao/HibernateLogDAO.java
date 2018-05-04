package OLink.bpm.core.logger.dao;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.base.dao.HibernateBaseDAO;
import OLink.bpm.core.logger.ejb.LogVO;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.util.StringUtil;

public class HibernateLogDAO extends HibernateBaseDAO<LogVO> implements LogDAO {

	public HibernateLogDAO(String valueObjectName) {
		super(valueObjectName);
	}

	public DataPackage<LogVO> queryLog(ParamsTable params, WebUser user)
			throws Exception {
		return query(params, user);
	}

	public DataPackage<LogVO> queryLog(ParamsTable params, WebUser user, String domain)
			throws Exception {
		StringBuffer hql = new StringBuffer();
		hql.append("FROM ").append(_voClazzName).append(" vo");
		//if (user.isDomainAdmin()) {
			hql.append(" WHERE (").append("vo.domainid = '").append(domain + "'");
			//hql.append(" OR ").append("vo.superUser = '").append(user.getId() + "')");
			hql.append(")");
		//}
		String operator = params.getParameterAsString("sm_operator");
		String date = params.getParameterAsString("sm_date");
		String ip = params.getParameterAsString("sm_ip");
		if (!StringUtil.isBlank(operator)) {
			if (hql.indexOf("WHERE") >= 0) {
				hql.append(" AND");
			} else {
				hql.append(" WHERE");
			}
			hql.append(" (vo.operator LIKE '%").append(operator.trim()).append("%')");
		}
		if (!StringUtil.isBlank(ip)) {
			if (hql.indexOf("WHERE") >= 0) {
				hql.append(" AND");
			} else {
				hql.append(" WHERE");
			}
			hql.append(" (vo.ip LIKE '%").append(ip.trim()).append("%')");
		}
		if (!StringUtil.isBlank(date)) {
			if (hql.indexOf("WHERE") >= 0) {
				hql.append(" AND");
			} else {
				hql.append(" WHERE");
			}
			hql.append(" (vo.date LIKE '%").append(date.trim()).append("%')");
		} 
		hql.append(" ORDER BY vo.date DESC");
		String _currpage = params.getParameterAsString("_currpage");
		String _pagelines = params.getParameterAsString("_pagelines");

		int page = (_currpage != null && _currpage.length() > 0) ? Integer.parseInt(_currpage) : 1;
		int lines = (_pagelines != null && _pagelines.length() > 0) ? Integer.parseInt(_pagelines) : 10;
		return getDatapackage(hql.toString(), page, lines);
	}
	
}
