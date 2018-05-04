package OLink.bpm.core.dynaform.activity.ejb.type;

import OLink.bpm.core.dynaform.activity.ejb.Activity;
import OLink.bpm.core.dynaform.activity.ejb.ActivityType;

public class Copy extends ActivityType {

	public Copy(Activity atc) {
		super(atc);
	}

	private static final long serialVersionUID = 1899989109269523704L;

	public String getButtonId() {
		return DOCUMENT_BUTTON_ID;

	}

	public String getDefaultClass() {
		return DOCUMENT_BUTTON_CLASS;
	}

	public String getOnClickFunction() {
		return "ev_action('" + act.getType() + "', '" + act.getId() + "')";

	}

	public String getAfterAction() {
		return DOCUMENT_JSP_NAMESPACE + "/content.jsp";
	}

	public String getBackAction() {
		return getAfterAction();
	}

	public String getBeforeAction() {
		return DOCUMENT_NAMESPACE + "/saveandcopy.action";
	}

	public String getDefaultOnClass() {
		
		return DOCUMENT_BUTTON_ON_CLASS;
	}

}
