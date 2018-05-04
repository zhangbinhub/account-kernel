package OLink.bpm.core.workflow.engine.state;

import OLink.bpm.core.workflow.element.Node;
import OLink.bpm.core.workflow.engine.State;

public class NullState extends AbstractState implements State {

	public NullState(Node node) {
		super(node);
	}

	public int toInt() {
		return Integer.MAX_VALUE;
	}

}
