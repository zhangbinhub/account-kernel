package OLink.bpm.core.report.query.dao;

import java.util.Collection;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.base.dao.HibernateBaseDAO;
import OLink.bpm.core.report.query.ejb.Query;

public class HibernateQueryDAO extends HibernateBaseDAO<Query> implements QueryDAO {
	public HibernateQueryDAO(String voClassName) {
		super(voClassName);
	}

	public Collection<Query> get_queryStringList(String moduleid, String application) throws Exception {
		String hql = "FROM " + _voClazzName + " where  module ='" + moduleid + "'";
		   ParamsTable params=new ParamsTable();
		   params.setParameter("application",application);
		   return getDatas(hql, params);
	}

	public Collection<Query> get_queryByAppId(String application)
			throws Exception {
		String hql = "FROM " + _voClazzName ;
		   ParamsTable params=new ParamsTable();
		   params.setParameter("application",application);
		   return getDatas(hql, params);
	}

}
