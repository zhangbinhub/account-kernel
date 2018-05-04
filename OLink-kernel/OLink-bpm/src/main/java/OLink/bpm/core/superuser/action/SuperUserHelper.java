package OLink.bpm.core.superuser.action;

import java.util.HashMap;
import java.util.Map;

import OLink.bpm.core.superuser.ejb.SuperUserProcess;
import OLink.bpm.core.superuser.ejb.SuperUserVO;
import OLink.bpm.util.StringUtil;
import OLink.bpm.core.superuser.ejb.SuperUserProcessBean;
import eWAP.core.Tools;

public class SuperUserHelper {

	public Map<String, String> getUserTypeList() throws Exception {

		Map<String, String> map = new HashMap<String, String>();
		// map.put(WebUser.ADMINSTRATORS, "Administrators");
		// map.put(WebUser.DOMAINS, "Domain Administrators");
		// map.put(WebUser.DEVELOPERS, "Developers");
		// map.put("aa", "aa");
		return map;
	}

	public String getUserNameById(String userId) throws Exception {
		SuperUserProcessBean sup = new SuperUserProcessBean();
		String userName = "";

		if (StringUtil.isBlank(userId)) {
			SuperUserVO supVo = (SuperUserVO) sup.doView(userId);
			userName = supVo.getName();
		}

		return userName;
	}

	public SuperUserVO initSipUser(SuperUserProcess sprocess, String userid, String username) throws Exception {
		SuperUserVO user = new SuperUserVO();
		user.setLoginno(userid);
		user.setName(username);
		user.setDomainAdmin(true);
		user.setDomainPermission(SuperUserVO.NORMAL_DOMAIN);
		user.setId(Tools.getSequence());
		user.setLoginpwd("");
		user.setStatus(1);
		try {
			sprocess.doCreate(user);
		} catch (Exception ex) {
			throw ex;
		}
		return user;
	}
}
