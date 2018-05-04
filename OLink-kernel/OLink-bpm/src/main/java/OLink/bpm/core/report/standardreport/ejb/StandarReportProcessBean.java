package OLink.bpm.core.report.standardreport.ejb;

import java.util.Collection;
import java.util.Map;

import OLink.bpm.core.report.standardreport.dao.StandardReportDAO;
import OLink.bpm.util.RuntimeDaoManager;
import OLink.bpm.base.dao.IRuntimeDAO;
import OLink.bpm.base.ejb.AbstractRunTimeProcessBean;

public class StandarReportProcessBean extends AbstractRunTimeProcessBean<Object> implements StandarReportProcess {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7207241275326254086L;

	public StandarReportProcessBean(String applicationId) {
		super(applicationId);

	}

	protected IRuntimeDAO getDAO() throws Exception {
		return new RuntimeDaoManager().getReportDAO(getConnection(), getApplicationId());
	}

	public Collection<Map<String, String>> getSummaryReport(String formId, String startdate, String enddate, String[] columnName, String dbmethod)
			throws Exception {
		String sql = ((StandardReportDAO) getDAO()).getSql(formId, startdate, enddate, columnName, this
				.getApplicationId(), dbmethod);
		return ((StandardReportDAO) getDAO()).getSummaryReport(sql);
	}

	public int getReportRowsNum(String formId, String startdate, String enddate, String[] columnName, String dbmethod) throws Exception {
		String sql = ((StandardReportDAO) getDAO()).getSql(formId, startdate, enddate, columnName, this
				.getApplicationId(), dbmethod);
		return ((StandardReportDAO) getDAO()).getReportRowsNum(sql);
	}

}
