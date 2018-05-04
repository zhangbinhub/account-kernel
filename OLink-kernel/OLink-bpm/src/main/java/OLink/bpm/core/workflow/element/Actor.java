package OLink.bpm.core.workflow.element;

public class Actor extends Element {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4385874887456936744L;
	public String type;
	public String namelist;

	public Actor(FlowDiagram owner) {
		super(owner);
		/*
		 * NodeActivity activity = new NodeActivity(owner); activity.id =
		 * Tools.getSequence(); activity.name = "test";
		 * 
		 * appendElement(activity);
		 */

		// NodeActivity activity = new NodeActivity(owner);
		// activity.id = Tools.getSequence();
		// activity.name = "test";

		// NodeActivity activity2 = new NodeActivity(owner);
		// activity2.id = Tools.getSequence();
		// activity2.name = "test2";
		// appendElement(activity);
		// appendElement(activity2);

	}

	public boolean removeSubElement(String id) {
		return false;
	}

	public void removeAllSubElement() {
	}

}
