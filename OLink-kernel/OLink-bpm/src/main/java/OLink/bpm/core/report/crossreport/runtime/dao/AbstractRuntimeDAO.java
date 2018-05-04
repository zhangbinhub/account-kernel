package OLink.bpm.core.report.crossreport.runtime.dao;

import java.sql.Connection;

import OLink.bpm.core.report.basereport.dao.AbstractReportDAO;

public class AbstractRuntimeDAO extends AbstractReportDAO{

	public AbstractRuntimeDAO(Connection conn) throws Exception {
		super(conn);
	}

}
