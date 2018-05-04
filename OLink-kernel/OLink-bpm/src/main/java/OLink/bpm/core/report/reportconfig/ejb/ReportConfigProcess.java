package OLink.bpm.core.report.reportconfig.ejb;

import java.util.Collection;

import OLink.bpm.base.ejb.IDesignTimeProcess;

public interface ReportConfigProcess extends IDesignTimeProcess<ReportConfig> {
	Collection<ReportConfig> getReportByModule(String moduleid, String application) throws Exception;
}
