package OLink.bpm.core.usersetup.ejb;

import java.util.ArrayList;
import java.util.HashMap;

import OLink.bpm.base.dao.DAOFactory;
import OLink.bpm.base.dao.IDesignTimeDAO;
import OLink.bpm.base.ejb.AbstractDesignTimeProcessBean;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.core.usersetup.dao.UserSetupDAO;

public class UserSetupProcessBean extends
		AbstractDesignTimeProcessBean<UserSetupVO> implements UserSetupProcess {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2326039651240289683L;
	public final static HashMap<String, WebUser> _cache = new HashMap<String, WebUser>();

	public UserSetupVO getUserSetupByUserId(String uId) throws Exception {
		/** update by zb 2014-04-22 **/
		ArrayList<UserSetupVO> userSetup = (ArrayList<UserSetupVO>) this
				.getDAO().getDatasBySQL(
						"select * from T_USERSETUP where userid='" + uId + "'");
		if (userSetup.size() == 0) {
			return null;
		} else {
			return userSetup.get(0);
		}
	}

	// @SuppressWarnings("unchecked")
	protected IDesignTimeDAO<UserSetupVO> getDAO() throws Exception {
		return (UserSetupDAO) DAOFactory.getDefaultDAO(UserSetupVO.class
				.getName());
	}
}
