package OLink.bpm.core.dynaform.activity.ejb.type;

import OLink.bpm.core.dynaform.activity.ejb.ActivityType;
import OLink.bpm.core.dynaform.activity.ejb.Activity;

public class NullType extends ActivityType {

	public NullType(Activity act) {
		super(act);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 6026837196484129755L;

	public String toHtml(int permissionType) {
		return "";
	}

	public String getOnClickFunction() {
		throw new UnsupportedOperationException();
	}

	public String getDefaultClass() {
		throw new UnsupportedOperationException();
	}

	public String getButtonId() {
		throw new UnsupportedOperationException();
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
