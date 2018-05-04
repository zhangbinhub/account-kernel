package OLink.bpm.core.report.tablecolumn.dao;

import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.base.dao.HibernateBaseDAO;
import OLink.bpm.core.report.tablecolumn.ejb.TableColumn;
import OLink.bpm.base.action.ParamsTable;

public class HibernateTableColumnDAO extends HibernateBaseDAO<TableColumn> implements
		TableColumnDAO {
	
	public HibernateTableColumnDAO(String voClassName) {
		super(voClassName);
	}

	public DataPackage<TableColumn> getFieldsByReportConfigAndType(String reportconfigid,
																   String type, String application) throws Exception {
		String hql = "from " + _voClazzName + " vo where vo.reportConfig.id='"
				+ reportconfigid + "' and vo.type='" + type + "'";
		ParamsTable params = new ParamsTable();
//		params.setParameter("application", application);
		return getDatapackage(hql, params);
	}
}
