package OLink.bpm.core.dynaform.activity.ejb.type;

import OLink.bpm.core.dynaform.activity.ejb.ActivityType;
import OLink.bpm.core.dynaform.activity.ejb.Activity;

public class DocumentUpdate extends ActivityType {

	public DocumentUpdate(Activity act) {
		super(act);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -9022536030969777318L;

	

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
		return DOCUMENT_NAMESPACE + "/save.action";
	}

	public String getDefaultOnClass() {
		
		return DOCUMENT_BUTTON_ON_CLASS;
	}

}
