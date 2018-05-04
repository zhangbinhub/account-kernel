package OLink.bpm.core.report.dataprepare.dao;

import java.util.Collection;

import OLink.bpm.base.dao.IDesignTimeDAO;
import OLink.bpm.core.report.dataprepare.ejb.DataPrepare;

public interface DataPrepareDAO extends IDesignTimeDAO<DataPrepare> {

	Collection<DataPrepare> getAllDataPrepareByApplication(String applicationid)throws Exception;
	
}
