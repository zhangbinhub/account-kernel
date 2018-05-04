package OLink.bpm.core.dynaform.activity.ejb.type;

import OLink.bpm.core.dynaform.activity.ejb.Activity;
import OLink.bpm.core.dynaform.activity.ejb.ActivityType;

public class PrintView extends ActivityType {

	public PrintView(Activity act) {
		super(act);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -5425161592049326841L;

	public String getOnClickFunction() {
		return "ev_printview('" + act.getId() + "')";
	}

	public String getDefaultClass() {
		return DOCUMENT_BUTTON_CLASS;
	}

	public String getButtonId() {
		return DOCUMENT_BUTTON_ID;
	}

	public String getAfterAction() {
		//return BASE_ACTION;
		
		return VIEW_SHARE_JSP_NAMESPACE + "/printview.jsp";
	}

	public String getBackAction() {
		return BASE_ACTION;
	}

	public String getBeforeAction() {
		//return BASE_ACTION;
		return DOCUMENT_NAMESPACE + "/printview.action";
	}

	public String getDefaultOnClass() {
		
		return DOCUMENT_BUTTON_ON_CLASS;
	}
	
}
