package OLink.bpm.core.dynaform.activity.action;

import OLink.bpm.constans.Web;

public class ActivityRunTimeAction extends ActivityAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4421551010061913544L;

	public ActivityRunTimeAction() throws ClassNotFoundException {
		super();
	}

	public String getWebUserSessionKey() {
		return Web.SESSION_ATTRIBUTE_FRONT_USER;
	}
}
