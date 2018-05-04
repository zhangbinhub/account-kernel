package OLink.bpm.core.report.reportconfig.dao;

import java.util.Collection;

import OLink.bpm.base.dao.IDesignTimeDAO;
import OLink.bpm.core.report.reportconfig.ejb.ReportConfig;

public interface ReportConfigDAO extends IDesignTimeDAO<ReportConfig> {
	
	Collection<ReportConfig> getReportByModule(String moduleid, String application) throws Exception;
}
