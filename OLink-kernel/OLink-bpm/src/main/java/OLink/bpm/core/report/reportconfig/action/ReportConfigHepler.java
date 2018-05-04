package OLink.bpm.core.report.reportconfig.action;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;

import OLink.bpm.base.action.BaseHelper;
import OLink.bpm.core.dynaform.dts.datasource.ejb.DataSource;
import OLink.bpm.core.report.reportconfig.ejb.ReportConfigProcess;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.core.report.reportconfig.ejb.ReportConfig;

public class ReportConfigHepler extends BaseHelper<ReportConfig> {

	/**
	 * @SuppressWarnings 工厂方法不支持泛型
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings("unchecked")
	public ReportConfigHepler() throws ClassNotFoundException {
		super(ProcessFactory.createProcess(ReportConfigProcess.class));
	}

	// public Connection getConnection(String queryid)throws Exception
	// {
	// QueryProcess rp = (QueryProcess) (ProcessFactory
	// .createProcess(QueryProcess.class));
	// Query query=(Query)rp.doView(queryid);
	// DataSource dts =query.getDataSource();
	// DriverManager.registerDriver((Driver) Class.forName(
	// dts.getDriverClass()).newInstance());
	// Connection conn = DriverManager.getConnection(dts.getUrl(),
	// dts.getUsername(),
	// dts.getPassword());
	// return conn;
	// }
	public Connection getConnection(DataSource dts) throws Exception {
		DriverManager.registerDriver((Driver) Class.forName(
				dts.getDriverClass()).newInstance());
		Connection conn = DriverManager.getConnection(dts.getUrl(), dts
				.getUsername(), dts.getPassword());
		return conn;
	}

}
