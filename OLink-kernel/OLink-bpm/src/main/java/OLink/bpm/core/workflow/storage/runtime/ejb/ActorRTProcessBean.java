package OLink.bpm.core.workflow.storage.runtime.ejb;

import java.util.Collection;

import OLink.bpm.base.dao.IRuntimeDAO;
import OLink.bpm.core.workflow.storage.runtime.dao.AbstractActorRTDAO;
import OLink.bpm.util.RuntimeDaoManager;
import OLink.bpm.base.ejb.AbstractRunTimeProcessBean;

public class ActorRTProcessBean extends AbstractRunTimeProcessBean<ActorRT>
		implements ActorRTProcess {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2839268794842601223L;

	public ActorRTProcessBean(String applicationId) {
		super(applicationId);
	}

	protected IRuntimeDAO getDAO() throws Exception {
		return new RuntimeDaoManager().getActorRtDAO(getConnection(),
				getApplicationId());
	}

	public Collection<ActorRT> queryByFlowStateRT(String stateId)
			throws Exception {
		return ((AbstractActorRTDAO) getDAO()).queryByForeignKey(
				"FLOWSTATERT_ID", stateId);
	}

	public Collection<ActorRT> queryByNodeRT(String nodeRTId) throws Exception {
		return ((AbstractActorRTDAO) getDAO()).queryByForeignKey("NODERT_ID",
				nodeRTId);
	}
}
