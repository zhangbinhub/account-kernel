package OLink.bpm.core.workflow.storage.runtime.proxy.ejb;

import java.util.Collection;

import OLink.bpm.base.ejb.IRunTimeProcess;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.core.user.ejb.BaseUser;

/**
 * @author Happy
 *
 */
public interface WorkflowProxyProcess extends IRunTimeProcess<WorkflowProxyVO> {

	/**
	 * 根据代理人获取流程代理信息
	 * @param user
	 * @return
	 * @throws Exception
	 */
	Collection<WorkflowProxyVO> getDatasByAgent(WebUser user, String applicationid) throws Exception;
	
	/**
	 * 根据所有者获取流程代理信息
	 * @param user
	 * @return
	 * @throws Exception
	 */
	Collection<WorkflowProxyVO> getDatasByOwners(Collection<BaseUser> owners) throws Exception;

	/**
	 * 流程的唯一校验 
	 * @param flowId
	 * @return
	 * @throws Exception
	 */
	boolean onlyCheckOnFlow(WorkflowProxyVO vo) throws Exception;
	
	/**
	 * 获取代理人
	 * @param owners
	 * @return
	 */
	Collection<BaseUser> getAgentsByOwners(Collection<BaseUser> owners) throws Exception;
	
	void doRemove(String[] pks) throws Exception;
}
