package OLink.bpm.core.report.crossreport.runtime.action;

import java.util.Map;

import OLink.bpm.constans.Web;
import OLink.bpm.core.user.action.WebUser;


public class RuntimeFrontAction extends RuntimeAction{

	public RuntimeFrontAction() throws ClassNotFoundException {
		super();
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public WebUser getUser() throws Exception {
		Map<?, ?> session = getContext().getSession();

		WebUser user = null;

		if (session == null || session.get(Web.SESSION_ATTRIBUTE_FRONT_USER) == null)
			user = getAnonymousUser();
		else
			user = (WebUser) session.get(Web.SESSION_ATTRIBUTE_FRONT_USER);
			//user = (WebUser) session.get(Web.SESSION_ATTRIBUTE_USER);

		return user;
	}

}
