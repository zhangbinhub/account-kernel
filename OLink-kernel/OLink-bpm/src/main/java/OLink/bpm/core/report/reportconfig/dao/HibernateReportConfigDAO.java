package OLink.bpm.core.report.reportconfig.dao;

import java.util.Collection;

import OLink.bpm.base.dao.HibernateBaseDAO;
import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.core.report.reportconfig.ejb.ReportConfig;

public class HibernateReportConfigDAO extends HibernateBaseDAO<ReportConfig> implements ReportConfigDAO{
	
	public HibernateReportConfigDAO(String voClassName) {
		super(voClassName);
	}
	
	public Collection<ReportConfig> getReportByModule(String moduleid, String application) throws Exception{
		String hql= "FROM " + _voClazzName + " vo WHERE vo.module.id='"
		 +moduleid+"'";
		   ParamsTable params=new ParamsTable();
		   params.setParameter("application",application);
		   return getDatas(hql, params);
}
}
