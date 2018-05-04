package OLink.bpm.core.report.query.dao;

import java.util.Collection;

import OLink.bpm.base.dao.HibernateBaseDAO;
import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.core.report.query.ejb.Parameter;

public class HibernateParameterDAO  extends HibernateBaseDAO<Parameter> implements ParameterDAO{
	
	public HibernateParameterDAO(String voClassName) {
		super(voClassName);
	}
	
	 public Collection<Parameter> getParamtersByQuery(String queryid,String application) throws Exception{
			String hql = "FROM "+_voClazzName+ " where query.id ='"+queryid+"'";
			   ParamsTable params=new ParamsTable();
			   params.setParameter("application",application);
			   return getDatas(hql, params);
	 }
}
