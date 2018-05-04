package OLink.bpm.core.report.standardreport.dao;

import java.util.Collection;
import java.util.Map;

import OLink.bpm.core.report.basereport.dao.ReportDAO;

public interface StandardReportDAO  extends ReportDAO {

	Collection<Map<String, String>> getSummaryReport(String sql) throws Exception;

	String getSql(String formId, String startdate, String enddate,
				  String[] columnName, String application, String dbmethod) throws Exception;

	int getReportRowsNum(String sql) throws Exception;
}
