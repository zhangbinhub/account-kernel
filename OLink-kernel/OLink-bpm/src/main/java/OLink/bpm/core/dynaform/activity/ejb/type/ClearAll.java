package OLink.bpm.core.dynaform.activity.ejb.type;

import OLink.bpm.core.dynaform.activity.ejb.Activity;
import OLink.bpm.core.dynaform.activity.ejb.ActivityType;

public class ClearAll extends ActivityType {

	public ClearAll(Activity act) {
		super(act);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -8325941963241466935L;

	public String getOnClickFunction() {
		return "ev_submit('" + act.getId() + "')";
	}

	public String getDefaultClass() {
		return VIEW_BUTTON_CLASS;
	}

	public String getButtonId() {
		return VIEW_BUTTON_ID;
	}

	public String getAfterAction() {
		return VIEW_NAMESPACE + "/displayView.action";
	}

	public String getBackAction() {
		return VIEW_NAMESPACE + "/displayView.action";
	}

	public String getBeforeAction() {
		return DOCUMENT_NAMESPACE + "/deleteAll.action?_formid="
				+ act.getOnActionForm();
	}

	public String getDefaultOnClass() {
		
		return DOCUMENT_BUTTON_ON_CLASS;
	}

}
