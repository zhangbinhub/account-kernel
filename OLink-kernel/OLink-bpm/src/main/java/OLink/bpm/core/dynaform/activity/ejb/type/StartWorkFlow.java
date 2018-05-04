package OLink.bpm.core.dynaform.activity.ejb.type;

import OLink.bpm.core.dynaform.activity.ejb.Activity;
import OLink.bpm.core.dynaform.activity.ejb.ActivityType;
import OLink.bpm.core.dynaform.PermissionType;

public class StartWorkFlow extends ActivityType {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7069431626851422819L;

	/**
	 * 
	 */

	public StartWorkFlow(Activity act) {
		super(act);
	}
	
	public String toHtml(int permissionType) {
		htmlBuilder = new StringBuffer();
		if (permissionType == PermissionType.MODIFY) {
			addDefaultButton();
		}

		return htmlBuilder.toString();
	}
	
	public String getOnClickFunction() {
		return "startWorkFlow("+act.getType()+", '" + act.getId() + "','" + act.getEditMode() + "','{*[Start]*}{*[Workflow]*}')";
	}

	public String getDefaultClass() {
		return "button-document";
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
		return DOCUMENT_NAMESPACE + "/startWorkFlow.action";
	}

	public String getDefaultOnClass() {

		return DOCUMENT_BUTTON_ON_CLASS;
	}

}
