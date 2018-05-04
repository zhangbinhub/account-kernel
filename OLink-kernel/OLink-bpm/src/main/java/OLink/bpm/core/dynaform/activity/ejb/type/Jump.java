package OLink.bpm.core.dynaform.activity.ejb.type;

import OLink.bpm.core.dynaform.activity.ejb.Activity;
import OLink.bpm.core.dynaform.activity.ejb.ActivityType;

/**
 * 跳转类型操作对象
 * @author Happy
 *
 */
public class Jump extends ActivityType {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4026601896630074752L;
	


	public Jump(Activity act) {
		super(act);
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

	public String getButtonId() {
		return DOCUMENT_BUTTON_ID;
	}

	public String getDefaultClass() {
		return DOCUMENT_BUTTON_CLASS;
	}

	public String getDefaultOnClass() {
		return DOCUMENT_BUTTON_ON_CLASS;
	}

	public String getOnClickFunction() {
		return "ev_Jump('" + act.getId() + "',"+this.act.getJumpType()+",'"+this.act.getTargetList()+"')";
	}

}
