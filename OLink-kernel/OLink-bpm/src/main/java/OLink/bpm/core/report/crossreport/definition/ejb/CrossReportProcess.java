package OLink.bpm.core.report.crossreport.definition.ejb;

import OLink.bpm.base.ejb.IDesignTimeProcess;

public interface CrossReportProcess  extends IDesignTimeProcess<CrossReportVO> {

	String getAllCrossReportVO(String applicationid, String moduleid, String flag, String userid) throws Exception;
	
	String getCrossReportVO(String id) throws Exception;
}
