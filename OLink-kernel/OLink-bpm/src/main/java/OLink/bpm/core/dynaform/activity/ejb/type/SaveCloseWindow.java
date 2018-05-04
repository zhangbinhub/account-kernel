package OLink.bpm.core.dynaform.activity.ejb.type;

import OLink.bpm.core.dynaform.activity.ejb.ActivityType;
import OLink.bpm.core.dynaform.activity.ejb.Activity;

public class SaveCloseWindow extends ActivityType {

	public SaveCloseWindow(Activity act) {
		super(act);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 90175457607017688L;

	public String getOnClickFunction() {
		return "doSave('" + act.getType() + "', '" + act.getId() + "')";
	}

	public String getDefaultClass() {
		return DOCUMENT_BUTTON_CLASS;
	}

	public String getButtonId() {
		return DOCUMENT_BUTTON_ID;
	}

	public String getAfterAction() {
		return DOCUMENT_SHARE_JSP_NAMESPACE + "/close.jsp";
	}

	public String getBackAction() {
		return DOCUMENT_SHARE_JSP_NAMESPACE + "/close.jsp";
		
	}

	public String getBeforeAction() {
		return DOCUMENT_NAMESPACE + "/saveclose.action";
	}

	public String getDefaultOnClass() {
		
		return DOCUMENT_BUTTON_ON_CLASS;
	}

}
