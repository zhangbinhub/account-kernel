package OLink.bpm.core.workflow.storage.runtime.proxy.dao;

import java.util.Collection;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.core.workflow.storage.runtime.proxy.ejb.WorkflowProxyVO;
import OLink.bpm.base.dao.IRuntimeDAO;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.core.user.ejb.BaseUser;

/**
 * @author Happy
 *
 */
public interface WorkflowProxyDAO extends IRuntimeDAO {
	
	DataPackage<WorkflowProxyVO> queryByFilter(ParamsTable params, WebUser user) throws Exception;
	
	/**
	 * 根据用户获取用户的流程代理信息
	 * @param user
	 * @return
	 * @throws Exception
	 */
	DataPackage<WorkflowProxyVO> queryByAgent(WebUser user, String applicationid) throws Exception;
	
	DataPackage<WorkflowProxyVO> queryByOwners(Collection<BaseUser> owners) throws Exception;
	
	long countByFlowAndOwner(WorkflowProxyVO vo) throws Exception;
	
	


}
