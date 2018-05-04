package OLink.bpm.core.report.wfdashboard.ejb;

import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.base.ejb.IRunTimeProcess;

public interface DashBoardProcess extends IRunTimeProcess<DashBoardVO> {
   
	String getSumWFChartStr(String domainid) throws Exception;
	
	String getSumStateLabelChartStr(String domainid, String flowid)throws Exception;
	
	DataPackage<DashBoardVO> getSumRole(String domainid, String flowid, int curPage)throws Exception;
	
	String getSumTimeChartStr(String domainid, String flowid)throws Exception;

}
