package OLink.bpm.core.dynaform.activity.ejb.type;

import OLink.bpm.core.dynaform.activity.ejb.Activity;
import OLink.bpm.core.dynaform.activity.ejb.ActivityType;

/**
 * 流程回撤操作
 * @author Happy
 *
 */
public class WorkFlowRetracement extends ActivityType {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4594499464207985911L;

	public WorkFlowRetracement(Activity act) {
		super(act);
	}

	public String getAfterAction() {
		return DOCUMENT_SHARE_JSP_NAMESPACE + "/success.jsp";
	}

	public String getBackAction() {
		return DOCUMENT_JSP_NAMESPACE + "/content.jsp";
	}

	public String getBeforeAction() {
		return DOCUMENT_NAMESPACE + "/retracement.action";
	}

	public String getButtonId() {
		return DOCUMENT_BUTTON_ID;
	}

	public String getDefaultClass() {
		return "button-document";
	}

	public String getDefaultOnClass() {
		return DOCUMENT_BUTTON_ON_CLASS;
	}

	public String getOnClickFunction() {
		return "ev_retracement(this, '" + act.getId() + "')";
	}

}
