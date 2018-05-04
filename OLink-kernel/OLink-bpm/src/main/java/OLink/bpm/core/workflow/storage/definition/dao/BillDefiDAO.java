package OLink.bpm.core.workflow.storage.definition.dao;

import java.util.Collection;

import OLink.bpm.base.dao.IDesignTimeDAO;
import OLink.bpm.core.workflow.storage.definition.ejb.BillDefiVO;

public interface BillDefiDAO extends IDesignTimeDAO<BillDefiVO> {
	Collection<BillDefiVO> getBillDefiByModule(String moduleid)
			throws Exception;

	BillDefiVO findBySubject(String subject, String applicationId)
			throws Exception;
}
