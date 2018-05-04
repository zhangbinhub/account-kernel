package OLink.bpm.core.workflow.engine.state;

import java.util.Collection;

import OLink.bpm.core.workflow.element.Node;
import OLink.bpm.core.workflow.engine.StateMachineUtil;
import OLink.bpm.core.workflow.storage.runtime.ejb.FlowStateRT;
import OLink.bpm.core.workflow.storage.runtime.ejb.NodeRT;
import OLink.bpm.core.workflow.storage.runtime.ejb.NodeRTProcess;
import OLink.bpm.core.workflow.storage.runtime.ejb.NodeRTProcessBean;
import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.core.user.ejb.BaseUser;

public abstract class AbstractState {
	protected Node node;

	public AbstractState(Node node) {
		this.node = node;
	}

	/**
	 * 当前节点状态切换
	 * 
	 * @param params
	 *            参数
	 * @param doc
	 *            文档
	 * @param flowVO
	 *            流程
	 * @param user
	 *            当前用户
	 * 
	 * @param flowOption
	 *            流程操作
	 * @return
	 * @throws Exception
	 */
	public NodeRT process(ParamsTable params, NodeRT origNodeRT, FlowStateRT instance, WebUser user, String flowOption)
			throws Exception {
		NodeRTProcess nodeRTProcess = new NodeRTProcessBean(instance
				.getApplicationid());
		NodeRT newNodeRT = nodeRTProcess.doCreate(params, origNodeRT, instance, node, flowOption,user);
		return newNodeRT;
	}

	public Collection<String> getPrincipalIdList(ParamsTable params, String domainId, String applicationid,BaseUser auditor)
			throws Exception {
		Collection<String> prinspalIdList = StateMachineUtil.getPrincipalIdList(params, node, domainId, applicationid, auditor);
		return prinspalIdList;
	}
}
