package OLink.bpm.core.report.crossreport.definition.dao;

import OLink.bpm.base.dao.HibernateBaseDAO;
import OLink.bpm.core.report.crossreport.definition.ejb.CrossReportVO;

public class HibernateCrossReportDAO  extends HibernateBaseDAO<CrossReportVO> implements CrossReportDAO{
	
	public HibernateCrossReportDAO(String voClassName) {
		super(voClassName);
	}


}
