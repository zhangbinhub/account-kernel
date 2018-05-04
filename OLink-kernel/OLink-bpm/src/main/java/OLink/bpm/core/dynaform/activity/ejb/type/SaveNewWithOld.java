package OLink.bpm.core.dynaform.activity.ejb.type;

import OLink.bpm.core.dynaform.activity.ejb.Activity;
import OLink.bpm.core.dynaform.activity.ejb.ActivityType;

public class SaveNewWithOld extends ActivityType {

	public SaveNewWithOld(Activity act) {
		super(act);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 8560477229110242596L;

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
		return DOCUMENT_JSP_NAMESPACE + "/content.jsp";
	}

	public String getBackAction() {
		return DOCUMENT_JSP_NAMESPACE + "/content.jsp";
	}

	public String getBeforeAction() {
		return DOCUMENT_NAMESPACE + "/savenewwithold.action";
	}

	public String getDefaultOnClass() {
		
		return DOCUMENT_BUTTON_ON_CLASS;
	}
}
