package OLink.bpm.core.workflow.engine.state;

import OLink.bpm.core.workflow.FlowState;
import OLink.bpm.core.workflow.element.Node;
import OLink.bpm.core.workflow.engine.State;

public class TerminateState extends AbstractState implements State {

	public TerminateState(Node node) {
		super(node);
	}

	public int toInt() {
		return FlowState.TERMINAT;
	}

}
