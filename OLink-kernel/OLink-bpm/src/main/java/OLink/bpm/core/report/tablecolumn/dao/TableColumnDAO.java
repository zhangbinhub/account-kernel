package OLink.bpm.core.report.tablecolumn.dao;

import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.base.dao.IDesignTimeDAO;
import OLink.bpm.core.report.tablecolumn.ejb.TableColumn;

public interface TableColumnDAO extends IDesignTimeDAO<TableColumn> {
	DataPackage<TableColumn> getFieldsByReportConfigAndType(String reportconfigid,
															String type, String application) throws Exception;
}
