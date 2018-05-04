package OLink.bpm.core.dynaform.activity.ejb.type;

import OLink.bpm.core.dynaform.activity.ejb.ActivityType;
import OLink.bpm.core.dynaform.activity.ejb.Activity;

public class Print extends ActivityType {

	public Print(Activity act) {
		super(act);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -5425161592049326840L;

	public String getOnClickFunction() {
		return "ev_print(false, '" + act.getId() + "')";
	}

	public String getDefaultClass() {
		return DOCUMENT_BUTTON_CLASS;
	}

	public String getButtonId() {
		return DOCUMENT_BUTTON_ID;
	}

	public String getAfterAction() {
		//return BASE_ACTION;
		
		return DOCUMENT_SHARE_JSP_NAMESPACE + "/print.jsp";
	}

	public String getBackAction() {
		return BASE_ACTION;
	}

	public String getBeforeAction() {
		//return BASE_ACTION;
		return DOCUMENT_NAMESPACE + "/print.action";
	}

	public String getDefaultOnClass() {
		
		return DOCUMENT_BUTTON_ON_CLASS;
	}
	
}
