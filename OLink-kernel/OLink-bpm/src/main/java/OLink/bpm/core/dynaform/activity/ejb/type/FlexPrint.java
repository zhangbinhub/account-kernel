package OLink.bpm.core.dynaform.activity.ejb.type;

import OLink.bpm.core.dynaform.activity.ejb.ActivityType;
import OLink.bpm.core.dynaform.activity.ejb.Activity;


/**
 * Flex 动态打印按钮类型
 * @author Happy
 *
 */
public class FlexPrint extends ActivityType {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4429402952733648050L;

	public FlexPrint(Activity act) {
		super(act);
	}



	public String getOnClickFunction() {
		return "ev_flexPrint('"+act.getId()+"','"+act.getOnActionPrint()+"',false)";
	}

	public String getDefaultClass() {
		return DOCUMENT_BUTTON_CLASS;
	}

	public String getButtonId() {
		return DOCUMENT_BUTTON_ID;
	}

	public String getAfterAction() {
		return  "/portal/share/dynaform/printer/flexprint.jsp";
	}

	public String getBackAction() {
		return  DOCUMENT_JSP_NAMESPACE + "/content.jsp";
	}

	public String getBeforeAction() {
		return "/portal/dynaform/printer/flexprint.action";
	}

	public String getDefaultOnClass() {
		
		return DOCUMENT_BUTTON_ON_CLASS;
	}
}
