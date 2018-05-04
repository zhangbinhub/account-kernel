package OLink.bpm.core.role.action;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.core.permission.ejb.PermissionPackage;
import OLink.bpm.core.role.ejb.RoleProcess;
import OLink.bpm.core.role.ejb.RoleVO;
import OLink.bpm.core.user.ejb.UserProcess;
import OLink.bpm.core.user.ejb.UserRoleSet;
import OLink.bpm.core.user.ejb.UserVO;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.util.StringUtil;

public class RoleHelper {

	public RoleHelper() throws Exception {
	}

	public void removeRole(String domainId, String application)
			throws Exception {
		RoleProcess rp = (RoleProcess) ProcessFactory
				.createProcess(RoleProcess.class);
		Collection<RoleVO> roleList = rp.getRolesByApplication(application);
		if (roleList != null)
			for (Iterator<RoleVO> rit = roleList.iterator(); rit.hasNext();) {
				RoleVO rv = rit.next();
				Collection<UserVO> users = rv.getUsersByDomain(domainId);
				if (users != null) {
					UserProcess process = (UserProcess) ProcessFactory
							.createProcess(UserProcess.class);
					for (Iterator<UserVO> it = users.iterator(); it.hasNext();) {
						UserVO user = it.next();
						Collection<UserRoleSet> oldroles = user
								.getUserRoleSets();
						Collection<UserRoleSet> roles = new HashSet<UserRoleSet>();
						if (oldroles != null) {
							for (Iterator<UserRoleSet> its = oldroles
									.iterator(); its.hasNext();) {
								UserRoleSet set = its.next();
								if (!rv.getId().equals(set.getRoleId()))
									roles.add(set);
							}
						}
						user.setUserRoleSets(roles);
						process.doUpdateWithCache(user);
					}
				}
			}
		PermissionPackage.clearCache();
	}

	public Collection<RoleVO> getRoleList(String application) throws Exception {
		RoleProcess cp = (RoleProcess) ProcessFactory
				.createProcess((RoleProcess.class));

		return cp.getRolesByApplication(application);
	}

	public Collection<RoleVO> getRoleList(String name, String application)
			throws Exception {
		RoleProcess cp = (RoleProcess) ProcessFactory
				.createProcess((RoleProcess.class));
		ParamsTable params = new ParamsTable();
		if (!StringUtil.isBlank(name))
			;
		{
			params.setParameter("sm_name", name);
		}
		params.setParameter("_orderby", "name");
		String[] apps = application.split(",");
		if (apps.length > 1) {
			application = apps[0];
		}
		return cp.doSimpleQuery(params, application);

	}

	public String getExpandPath(String selectedList) throws Exception {
		StringBuffer buffer = new StringBuffer();

		RoleProcess rp = (RoleProcess) ProcessFactory
				.createProcess(RoleProcess.class);

		if (selectedList != null && selectedList.trim().length() > 0) {
			String[] selecteds = selectedList.split(";");
			if (selecteds.length > 0) {
				buffer.append("[");
				for (int i = 0; i < selecteds.length; i++) {
					char prefix = selecteds[i].charAt(0);
					String id = selecteds[i]
							.substring(1, selecteds[i].length());
					switch (prefix) {

					case 'R':
						RoleVO role = (RoleVO) rp.doView(id);

						buffer.append("\""
								+ getPathStr(role.getApplicationid(), true)
								+ "\"");
						break;

					default:
						break;
					}
					buffer.append(",");

				}
				buffer.deleteCharAt(buffer.lastIndexOf(","));
				buffer.append("]");
			}
		}
		return buffer.toString();
	}

	private String getPathStr(String applicationid, boolean includeCurrent)
			throws Exception {
		StringBuffer buffer = new StringBuffer();
		RoleProcess process = (RoleProcess) ProcessFactory
				.createProcess(RoleProcess.class);
		Collection<RoleVO> roles = process.getRolesByApplication(applicationid);
		if (roles != null && !roles.isEmpty()) {
			for (Iterator<RoleVO> iterator = roles.iterator(); iterator
					.hasNext();) {
				RoleVO obj = iterator.next();
				buffer.append("/R" + obj.toString());
			}
		}
		return buffer.toString();
	}

	public Collection<UserVO> getRolesByUserName(String name, String application)
			throws Exception {
		RoleProcess process = (RoleProcess) ProcessFactory
				.createProcess(RoleProcess.class);
		RoleVO roles = process.doViewByName(name, application);
		Collection<UserVO> users = roles.getUsers();
		return users != null ? users : null;
	}

	public String queryRoleNamesByIds(String idString) throws Exception {
		RoleProcess ph = (RoleProcess) ProcessFactory
				.createProcess(RoleProcess.class);
		String[] idArray = idString.split(",");
		StringBuffer roleNameString = new StringBuffer();
		if (idArray.length > 0) {
			for (int i = 0; i < idArray.length; i++) {
				String id = idArray[i];
				RoleVO role = (RoleVO) ph.doView(id);
				roleNameString.append(role.getName()).append(",");
			}
			if (roleNameString.length() > 0
					&& roleNameString.toString().endsWith(",")) {
				roleNameString.setLength(roleNameString.length() - 1);
			}
		}
		return roleNameString.toString();
	}

}
