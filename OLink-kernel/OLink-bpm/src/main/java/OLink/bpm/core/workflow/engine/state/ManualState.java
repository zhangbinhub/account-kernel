package OLink.bpm.core.workflow.engine.state;

import OLink.bpm.core.workflow.FlowState;
import OLink.bpm.core.workflow.element.Node;
import OLink.bpm.core.workflow.engine.State;

public class ManualState extends AbstractState implements State {

	public ManualState(Node node) {
		super(node);
	}

	public int toInt() {
		return FlowState.RUNNING;
	}

}
