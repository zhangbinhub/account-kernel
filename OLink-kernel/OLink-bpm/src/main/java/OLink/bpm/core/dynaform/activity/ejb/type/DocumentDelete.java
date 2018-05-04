package OLink.bpm.core.dynaform.activity.ejb.type;

import OLink.bpm.core.dynaform.activity.ejb.ActivityType;
import OLink.bpm.core.dynaform.activity.ejb.Activity;

public class DocumentDelete extends ActivityType {

	public DocumentDelete(Activity act) {
		super(act);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 3934714556361444621L;

	public String getOnClickFunction() {
		return "doRemove('" + act.getId() + "')";
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
		return DOCUMENT_NAMESPACE + "/delete.action";
	}

	public String getDefaultOnClass() {
		
		return DOCUMENT_BUTTON_ON_CLASS;
	}

}
