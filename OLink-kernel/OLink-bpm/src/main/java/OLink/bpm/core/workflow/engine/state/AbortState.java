package OLink.bpm.core.workflow.engine.state;

import OLink.bpm.core.workflow.FlowState;
import OLink.bpm.core.workflow.engine.State;
import OLink.bpm.core.workflow.element.Node;

public class AbortState extends AbstractState implements State {

	public AbortState(Node node) {
		super(node);
	}

	public int toInt() {
		return FlowState.ABORT;
	}
}
