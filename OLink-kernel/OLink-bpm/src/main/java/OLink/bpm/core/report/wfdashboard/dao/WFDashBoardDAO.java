package OLink.bpm.core.report.wfdashboard.dao;

import java.util.Collection;

import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.core.report.wfdashboard.ejb.DashBoardVO;
import OLink.bpm.core.report.basereport.dao.ReportDAO;

public interface WFDashBoardDAO  extends ReportDAO{
	
	Collection<DashBoardVO> getSumWfData(String application, String domainid)throws Exception;
	
	Collection<DashBoardVO> getSumStableLabelData(String application, String domainid, String flowid)throws Exception;
	
	DataPackage<DashBoardVO> getSumRole(String application, String domainid, String flowid, int curPage)throws Exception;
	
	Collection<DashBoardVO> getSumTimeData(String application, String domainid, String flowid)throws Exception;


}
