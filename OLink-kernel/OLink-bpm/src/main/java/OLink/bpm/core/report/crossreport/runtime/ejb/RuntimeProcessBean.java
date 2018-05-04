package OLink.bpm.core.report.crossreport.runtime.ejb;

import java.sql.Connection;

import OLink.bpm.base.dao.IRuntimeDAO;
import OLink.bpm.base.ejb.AbstractRunTimeProcessBean;
import OLink.bpm.util.RuntimeDaoManager;

public class RuntimeProcessBean extends AbstractRunTimeProcessBean<Object> implements RuntimeProcess{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2313728753180088046L;

	public RuntimeProcessBean(String applicationId) {
		super(applicationId);
	}

	protected IRuntimeDAO getDAO() throws Exception {
		return new RuntimeDaoManager().getCrossReportDAO(getConnection(),getApplicationId()); 
	}

	public Connection getRuntimeConn()throws Exception
	{
		return this.getConnection();
	}
}
