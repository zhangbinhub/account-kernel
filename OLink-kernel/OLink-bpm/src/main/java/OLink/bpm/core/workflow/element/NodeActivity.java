package OLink.bpm.core.workflow.element;

public class NodeActivity extends Element {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4384479060324396639L;
	public String name;

	public NodeActivity(FlowDiagram owner) {
		super(owner);
	}

	public boolean removeSubElement(String id) {
		return false;
	}

	public void removeAllSubElement() {
	}

}
