package OLink.bpm.core.dynaform.activity.ejb.type;

import OLink.bpm.core.dynaform.activity.ejb.Activity;
import OLink.bpm.core.dynaform.activity.ejb.ActivityType;

public class DocumentQuery extends ActivityType {

	public DocumentQuery(Activity act) {
		super(act);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 3304912397283938128L;

	public String getOnClickFunction() {
		return "doQuery('" + act.getId() + "')";
	}

	public String getDefaultClass() {
		return VIEW_BUTTON_CLASS;
	}

	public String getButtonId() {
		return VIEW_BUTTON_ID;
	}

	public String getAfterAction() {
		return VIEW_JSP_NAMESPACE + "/detail.jsp";
	}

	public String getBackAction() {
		return VIEW_JSP_NAMESPACE + "/detail.jsp";
	}

	public String getBeforeAction() {
		return VIEW_NAMESPACE + "/displayView.action?_viewid=" + act.getOnActionView();
	}

	public String getDefaultOnClass() {
		
		return DOCUMENT_BUTTON_ON_CLASS;
	}
}
