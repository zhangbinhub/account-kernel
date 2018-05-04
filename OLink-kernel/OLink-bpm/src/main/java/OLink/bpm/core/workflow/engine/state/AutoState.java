package OLink.bpm.core.workflow.engine.state;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.core.superuser.ejb.SuperUserProcess;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.core.workflow.FlowState;
import OLink.bpm.core.workflow.element.Node;
import OLink.bpm.core.workflow.engine.AutoAuditJob;
import OLink.bpm.core.workflow.engine.AutoAuditJobManager;
import OLink.bpm.core.workflow.engine.State;
import OLink.bpm.core.workflow.storage.runtime.ejb.FlowStateRT;
import OLink.bpm.core.workflow.storage.runtime.ejb.NodeRT;
import OLink.bpm.core.workflow.storage.runtime.ejb.NodeRTProcess;
import OLink.bpm.core.workflow.storage.runtime.ejb.NodeRTProcessBean;
import OLink.bpm.util.ProcessFactory;

public class AutoState extends AbstractState implements State {

	public AutoState(Node node) {
		super(node);
	}

	public NodeRT process(ParamsTable params, NodeRT origNodeRT, FlowStateRT instance, WebUser user,
						  String flowOption) throws Exception {
		NodeRTProcess nodeRTProcess = new NodeRTProcessBean(instance.getApplicationid());
		SuperUserProcess superUserProcess = (SuperUserProcess) ProcessFactory.createProcess(SuperUserProcess.class);

		WebUser admin = new WebUser(superUserProcess.getDefaultAdmin()); // 系统用户
		admin.setDomainid(instance.getDocument().getId());
		// admin.setApplicationid(doc.getApplicationid());
		AutoAuditJobManager.addJob(new AutoAuditJob(instance, node.id, admin));// 添加Job
		NodeRT newNodeRT = nodeRTProcess.doCreate(params, origNodeRT, instance, node, flowOption, admin);
		return newNodeRT;
	}

	public int toInt() {
		return FlowState.AUTO;
	}
}
