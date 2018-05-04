package OLink.bpm.core.report.reportconfig.ejb;

import java.util.Collection;
import java.util.Set;

import OLink.bpm.base.dao.DAOFactory;
import OLink.bpm.base.dao.IDesignTimeDAO;
import OLink.bpm.base.dao.PersistenceUtils;
import OLink.bpm.base.dao.ValueObject;
import OLink.bpm.base.ejb.AbstractDesignTimeProcessBean;
import OLink.bpm.core.report.reportconfig.dao.ReportConfigDAO;
import OLink.bpm.core.report.tablecolumn.ejb.TableColumn;
import org.apache.commons.beanutils.PropertyUtils;

public class ReportConfigProcessBean extends AbstractDesignTimeProcessBean<ReportConfig> implements ReportConfigProcess{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3030140173453045260L;

	protected IDesignTimeDAO<ReportConfig> getDAO() throws Exception {
		return (ReportConfigDAO) DAOFactory.getDefaultDAO(ReportConfig.class.getName());
	}
	
	public void doUpdate(ValueObject vo) throws Exception {
		try {
			PersistenceUtils.beginTransaction();

			ReportConfig po = (ReportConfig)getDAO().find(vo.getId());
			if (po != null) {
				Set<TableColumn> fields= po.getFields();
				PropertyUtils.copyProperties(po, vo);
				po.setFields(fields);
				getDAO().update(po);
			} else {
				getDAO().update(vo);
			}

			PersistenceUtils.commitTransaction();
		} catch (Exception e) {
			e.printStackTrace();
			PersistenceUtils.rollbackTransaction();
		}
	}
	
	public Collection<ReportConfig> getReportByModule(String moduleid, String application) throws Exception{
		return ((ReportConfigDAO) getDAO()).getReportByModule(moduleid, application);
	}
}
