package OLink.bpm.core.report.tablecolumn.ejb;

import OLink.bpm.base.dao.DAOFactory;
import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.base.dao.IDesignTimeDAO;
import OLink.bpm.core.report.tablecolumn.dao.TableColumnDAO;
import OLink.bpm.base.ejb.AbstractDesignTimeProcessBean;

public class TableColumnProcessBean extends AbstractDesignTimeProcessBean<TableColumn> implements TableColumnProcess{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8423385891800319310L;

	protected IDesignTimeDAO<TableColumn> getDAO() throws Exception {
		return (TableColumnDAO) DAOFactory.getDefaultDAO(TableColumn.class.getName());
	}
	
	public DataPackage<TableColumn> getFieldsByReportConfigAndType(String reportconfigid, String type, String application) throws Exception
	{
		return ((TableColumnDAO) getDAO()).getFieldsByReportConfigAndType(reportconfigid,type, application);
	}
}
