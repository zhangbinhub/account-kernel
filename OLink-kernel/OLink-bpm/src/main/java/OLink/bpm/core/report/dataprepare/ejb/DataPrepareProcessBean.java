package OLink.bpm.core.report.dataprepare.ejb;

import java.util.Collection;

import OLink.bpm.base.dao.DAOFactory;
import OLink.bpm.base.dao.IDesignTimeDAO;
import OLink.bpm.base.ejb.AbstractDesignTimeProcessBean;
import OLink.bpm.core.report.dataprepare.dao.DataPrepareDAO;

public class DataPrepareProcessBean extends AbstractDesignTimeProcessBean<DataPrepare> implements DataPrepareProcess{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1896445506124980724L;

	protected IDesignTimeDAO<DataPrepare> getDAO() throws Exception {
		return (DataPrepareDAO) DAOFactory.getDefaultDAO(DataPrepare.class.getName());
	}
	
	public Collection<DataPrepare> getAllDataPrepareByApplication(String applicationid)throws Exception{
		 return ((DataPrepareDAO) getDAO()).getAllDataPrepareByApplication(applicationid);
	}
}
