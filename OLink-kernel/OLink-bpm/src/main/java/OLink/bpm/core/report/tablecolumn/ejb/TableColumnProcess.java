package OLink.bpm.core.report.tablecolumn.ejb;

import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.base.ejb.IDesignTimeProcess;

public interface TableColumnProcess extends IDesignTimeProcess<TableColumn> {
	
	DataPackage<TableColumn> getFieldsByReportConfigAndType(String reportconfigid,
															String type, String application) throws Exception;
}
