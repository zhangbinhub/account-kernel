package OLink.bpm.core.workflow.engine.state;

import OLink.bpm.core.workflow.engine.State;
import OLink.bpm.core.workflow.storage.runtime.ejb.FlowStateRT;
import OLink.bpm.core.workflow.storage.runtime.ejb.NodeRTProcess;
import OLink.bpm.core.workflow.storage.runtime.ejb.NodeRTProcessBean;
import OLink.bpm.core.workflow.FlowState;
import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.core.workflow.element.Node;
import OLink.bpm.core.workflow.storage.runtime.ejb.NodeRT;

public class SuspendState extends AbstractState implements State {

	public SuspendState(Node node) {
		super(node);
	}

	public NodeRT process(ParamsTable params, NodeRT origNodeRT, FlowStateRT instance, WebUser user,
						  String flowOption) throws Exception {
		NodeRTProcess nodeRTProcess = new NodeRTProcessBean(instance.getApplicationid());
		NodeRT newNodeRT = nodeRTProcess.doCreate(params, origNodeRT, instance, node, flowOption, user);

		return newNodeRT;
	}

	public int toInt() {
		return FlowState.SUSPEND;
	}
}
