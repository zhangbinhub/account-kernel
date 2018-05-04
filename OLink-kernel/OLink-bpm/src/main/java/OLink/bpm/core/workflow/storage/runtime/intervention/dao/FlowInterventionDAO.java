package OLink.bpm.core.workflow.storage.runtime.intervention.dao;

import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.base.dao.IRuntimeDAO;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.core.workflow.storage.runtime.intervention.ejb.FlowInterventionVO;

/**
 * @author Happy
 *
 */
public interface FlowInterventionDAO extends IRuntimeDAO {
	
	DataPackage<FlowInterventionVO> queryByFilter(ParamsTable params, WebUser user) throws Exception;


}
