package OLink.bpm.core.dynaform.activity.ejb.type;

import OLink.bpm.core.dynaform.activity.ejb.Activity;
import OLink.bpm.core.dynaform.activity.ejb.ActivityType;


/**
 * Flex 带流程历史动态打印按钮类型
 * @author Happy
 *
 */
public class FlexPrintWithFlowHis extends ActivityType {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4572379811194928228L;

	public FlexPrintWithFlowHis(Activity act) {
		super(act);
	}

	public String getOnClickFunction() {
		return "ev_flexPrint(true)";
	}

	public String getDefaultClass() {
		return DOCUMENT_BUTTON_CLASS;
	}

	public String getButtonId() {
		return DOCUMENT_BUTTON_ID;
	}

	public String getAfterAction() {
		return BASE_ACTION;
	}

	public String getBackAction() {
		return BASE_ACTION;
	}

	public String getBeforeAction() {
		return BASE_ACTION;
	}

	public String getDefaultOnClass() {
		
		return DOCUMENT_BUTTON_ON_CLASS;
	}

}
