package OLink.bpm.core.workflow.storage.runtime.proxy.ejb;

import java.util.ArrayList;
import java.util.Collection;

import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.core.user.ejb.UserProcess;
import OLink.bpm.core.workflow.storage.runtime.proxy.dao.WorkflowProxyDAO;
import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.base.dao.IRuntimeDAO;
import OLink.bpm.base.ejb.AbstractRunTimeProcessBean;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.core.user.ejb.BaseUser;
import OLink.bpm.core.user.ejb.UserProcessBean;
import OLink.bpm.util.RuntimeDaoManager;

/**
 * @author Happy
 * 
 */
public class WorkflowProxyProcessBean extends AbstractRunTimeProcessBean<WorkflowProxyVO> implements
		WorkflowProxyProcess {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3862446754753478202L;

	public WorkflowProxyProcessBean(String applicationId) {
		super(applicationId);
	}

	@Override
	protected IRuntimeDAO getDAO() throws Exception {
		RuntimeDaoManager runtimeDao = new RuntimeDaoManager();
		WorkflowProxyDAO workflowProxyDAO = (WorkflowProxyDAO) runtimeDao.getWorkflowProxyDAO(getConnection(),
				getApplicationId());
		return workflowProxyDAO;
	}

	public void doRemove(String[] pks) throws Exception {
		if (pks != null) {
			for (int i = 0; i < pks.length; i++) {
				String id = pks[i];
				if (id.endsWith(";"))
					id = id.substring(0, id.length() - 1);
				doRemove(id);
			}
		}

	}

	@Override
	public DataPackage<WorkflowProxyVO> doQuery(ParamsTable params, WebUser user) throws Exception {
		if (getApplicationId() != null) {
			return ((WorkflowProxyDAO) this.getDAO()).queryByFilter(params, user);
		}
		return new DataPackage<WorkflowProxyVO>();
	}

	public Collection<WorkflowProxyVO> getDatasByAgent(WebUser user, String applicationid) throws Exception {
		return ((WorkflowProxyDAO) this.getDAO()).queryByAgent(user, applicationid).datas;
	}

	public boolean onlyCheckOnFlow(WorkflowProxyVO vo) throws Exception {
		long size = ((WorkflowProxyDAO) this.getDAO()).countByFlowAndOwner(vo);
		return size == 0;
	}

	public Collection<BaseUser> getAgentsByOwners(Collection<BaseUser> owners) throws Exception {
		if (owners == null || owners.isEmpty())
			return new ArrayList<BaseUser>();
		Collection<WorkflowProxyVO> list = getDatasByOwners(owners);
		Collection<BaseUser> rtn = new ArrayList<BaseUser>();
		UserProcess userProcess = new UserProcessBean();
		for (WorkflowProxyVO vo : list) {
			String[] agents = vo.getAgents().split(",");
			for (String agentid : agents) {
				rtn.add((BaseUser) userProcess.doView(agentid));
			}
		}
		return rtn;
	}

	public Collection<WorkflowProxyVO> getDatasByOwners(Collection<BaseUser> owners) throws Exception {

		return ((WorkflowProxyDAO) this.getDAO()).queryByOwners(owners).datas;
	}

}
