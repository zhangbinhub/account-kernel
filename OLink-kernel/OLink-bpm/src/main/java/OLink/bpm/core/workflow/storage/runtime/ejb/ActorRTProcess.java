package OLink.bpm.core.workflow.storage.runtime.ejb;

import java.util.Collection;

import OLink.bpm.base.ejb.IRunTimeProcess;

public interface ActorRTProcess extends IRunTimeProcess<ActorRT> {
	/**
	 * 
	 * @param stateId
	 * @return
	 * @throws Exception
	 */
	Collection<ActorRT> queryByFlowStateRT(String stateId)
			throws Exception;

	Collection<ActorRT> queryByNodeRT(String nodeRTId) throws Exception;
}
