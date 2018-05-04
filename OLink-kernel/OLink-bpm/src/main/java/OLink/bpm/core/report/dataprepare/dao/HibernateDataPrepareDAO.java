package OLink.bpm.core.report.dataprepare.dao;

import java.util.Collection;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.base.dao.HibernateBaseDAO;
import OLink.bpm.core.report.dataprepare.ejb.DataPrepare;

public class HibernateDataPrepareDAO extends HibernateBaseDAO<DataPrepare> implements DataPrepareDAO{

	public HibernateDataPrepareDAO(String voClassName) {
		super(voClassName);
	}
	
	public Collection<DataPrepare> getAllDataPrepareByApplication(String applicationid)throws Exception{
		String hql = "FROM " + _voClazzName;
		   ParamsTable params=new ParamsTable();
		   params.setParameter("application",applicationid);
		   return getDatas(hql, params);
	}
}
