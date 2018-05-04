package OLink.bpm.core.dynaform.activity.ejb.type;

import OLink.bpm.core.dynaform.PermissionType;
import OLink.bpm.core.dynaform.activity.ejb.Activity;
import OLink.bpm.core.dynaform.activity.ejb.ActivityType;

public class WorkFlowProcess extends ActivityType {

	public WorkFlowProcess(Activity act) {
		super(act);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 468670635828212529L;

	public String toHtml(int permissionType) {
		htmlBuilder = new StringBuffer();
		if (permissionType == PermissionType.MODIFY) {
			addDefaultButton();
		}

		return htmlBuilder.toString();
	}
	
	public String getOnClickFunction() {
		String flowShowType = act.getFlowShowType();
		if (flowShowType != null && flowShowType.equals("ST02")){
			return "showFlowSelect(this, '" + act.getId() + "','{*[Submit]*}{*[Flow]*}')";
		}
		return "ev_validation(this, '" + act.getId() + "')";
	}

	public String getDefaultClass() {
		return "button-document";
	}

	public String getButtonId() {
		return DOCUMENT_BUTTON_ID;
	}

	public String getAfterAction() {
		return DOCUMENT_SHARE_JSP_NAMESPACE + "/success.jsp";
	}

	public String getBackAction() {
		return DOCUMENT_JSP_NAMESPACE + "/content.jsp";
	}

	public String getBeforeAction() {
		return DOCUMENT_NAMESPACE + "/flow.action";
	}

	public String getDefaultOnClass() {

		return DOCUMENT_BUTTON_ON_CLASS;
	}

}
