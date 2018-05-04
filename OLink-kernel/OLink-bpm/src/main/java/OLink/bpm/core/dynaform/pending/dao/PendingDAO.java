package OLink.bpm.core.dynaform.pending.dao;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.base.dao.ValueObject;
import OLink.bpm.core.workflow.storage.runtime.dao.FlowStateRTDAO;
import OLink.bpm.base.dao.IRuntimeDAO;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.core.dynaform.pending.ejb.PendingVO;

public interface PendingDAO extends IRuntimeDAO {

	DataPackage<PendingVO> queryByFilter(ParamsTable params, WebUser user)
			throws Exception;

	long countByFilter(ParamsTable params, WebUser user)
			throws Exception;

	FlowStateRTDAO getStateRTDAO();

	void setStateRTDAO(FlowStateRTDAO stateRTDAO);

	void remove(ValueObject vo) throws Exception;
}
