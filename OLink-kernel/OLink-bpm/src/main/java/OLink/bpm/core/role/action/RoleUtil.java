package OLink.bpm.core.role.action;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import OLink.bpm.core.role.ejb.RoleProcess;
import OLink.bpm.core.role.ejb.RoleVO;
import OLink.bpm.core.user.ejb.UserVO;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.util.StringUtil;
import OLink.bpm.util.web.DWRHtmlUtils;

public class RoleUtil {
	/**
	 * 
	 * @param selectFieldName
	 * @param type
	 * @param def
	 * @param application
	 * @return
	 */
	public String createRolesOptions(String selectFieldName, String def,
			String application) throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		RoleProcess bp = (RoleProcess) ProcessFactory
				.createProcess(RoleProcess.class);
		Collection<RoleVO> roleList = bp.getRolesByApplication(application);
		Iterator<RoleVO> it = roleList.iterator();
		while (it.hasNext()) {
			RoleVO vo = it.next();
			map.put(vo.getId(), vo.getName());
		}
		return DWRHtmlUtils.createOptions(map, selectFieldName, def);
	}

	public String createUserOptions(String selectFieldName, String def,
			String roleId) throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		RoleProcess bp = (RoleProcess) ProcessFactory
				.createProcess(RoleProcess.class);
		RoleVO vo = (RoleVO) bp.doView(roleId);
		if (vo != null) {
			Collection<UserVO> users = vo.getUsers();
			if (users != null && users.size() > 0) {
				for (Iterator<UserVO> iterator = users.iterator(); iterator.hasNext();) {
					UserVO user = iterator.next();
					map.put(user.getId(), user.getName());
				}
			}
		}
		return DWRHtmlUtils.createOptions(map, selectFieldName, def);
	}
	
	public String findRoleNames(String ids) throws Exception {
		if (!StringUtil.isBlank(ids)) {
			RoleProcess rp = (RoleProcess) ProcessFactory
					.createProcess(RoleProcess.class);
			String[] id = ids.split(",");
			StringBuffer names = new StringBuffer();
			for (int i = 0; i < id.length; i++) {
				RoleVO role = (RoleVO) rp.doView(id[i]);
				names.append(role.getName()).append(";");
			}
			return names.toString();
		}
		return "";
	}
}
