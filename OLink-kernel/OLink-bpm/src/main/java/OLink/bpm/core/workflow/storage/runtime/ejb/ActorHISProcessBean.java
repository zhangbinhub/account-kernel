package OLink.bpm.core.workflow.storage.runtime.ejb;

import OLink.bpm.base.dao.IRuntimeDAO;
import OLink.bpm.util.RuntimeDaoManager;
import OLink.bpm.base.ejb.AbstractRunTimeProcessBean;

public class ActorHISProcessBean extends AbstractRunTimeProcessBean<ActorHIS>
		implements ActorHISProcess {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6320676638289535048L;

	public ActorHISProcessBean(String applicationId) {
		super(applicationId);
	}

	protected IRuntimeDAO getDAO() throws Exception {
		// return new OracleActorRTDAO(getConnection());
		// ApplicationVO app=getApplicationVO(getApplicationId());

		return new RuntimeDaoManager().getActorRtDAO(getConnection(),
				getApplicationId());
	}
}
