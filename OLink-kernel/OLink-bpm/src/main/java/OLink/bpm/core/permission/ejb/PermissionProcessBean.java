package OLink.bpm.core.permission.ejb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import OLink.bpm.base.dao.DAOFactory;
import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.base.dao.IDesignTimeDAO;
import OLink.bpm.base.dao.PersistenceUtils;
import OLink.bpm.core.privilege.operation.ejb.OperationProcess;
import OLink.bpm.core.privilege.res.ejb.ResProcess;
import OLink.bpm.core.role.ejb.RoleProcess;
import OLink.bpm.core.role.ejb.RoleVO;
import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.base.ejb.AbstractDesignTimeProcessBean;
import OLink.bpm.core.permission.dao.PermissionDAO;
import OLink.bpm.core.privilege.res.ejb.ResVO;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.util.StringUtil;
import OLink.bpm.core.privilege.operation.ejb.OperationVO;
import eWAP.core.Tools;

public class PermissionProcessBean extends
		AbstractDesignTimeProcessBean<PermissionVO> implements
		PermissionProcess {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8675975273714552185L;

	protected IDesignTimeDAO<PermissionVO> getDAO() throws Exception {
		return (PermissionDAO) DAOFactory.getDefaultDAO(PermissionVO.class
				.getName());
	}

	public PermissionVO getAppDomain_Cache() throws Exception {
		return null;
	}

	public PermissionVO findByResouceAndUser(String resourceId, String userId)
			throws Exception {
		return ((PermissionDAO) getDAO()).findByResouceAndUser(resourceId,
				userId);
	}

	public PermissionVO getPermissionByName(String name) throws Exception {
		return ((PermissionDAO) getDAO()).getPermissionByName(name);
	}

	public Collection<PermissionVO> doQueryByRoleIdAndResName(String roleId,
			String resName) throws Exception {

		return ((PermissionDAO) getDAO()).queryByRoleIdAndResName(roleId, resName);
	}

	public Collection<PermissionVO> doQueryByRole(String roleId, int resType)
			throws Exception {
		return ((PermissionDAO) getDAO()).queryByRole(roleId, resType);
	}

	public Collection<PermissionVO> doQueryByRole(String roleId)
			throws Exception {
		return ((PermissionDAO) getDAO()).queryByRole(roleId);
	}

	public Collection<PermissionVO> doQueryByRoleAndResource(String roleId,
			String resourceId) throws Exception {
		return ((PermissionDAO) getDAO()).queryByRoleAndResource(roleId, resourceId);
	}

	public Collection<String> getResourceIdsByRole(String roleId)
			throws Exception {

		return ((PermissionDAO) getDAO()).getResourceIdsByRole(roleId);
	}

	public Collection<String> getOperatonIdsByResourceAndRole(
			String resourceid, String roleId) throws Exception {
		return ((PermissionDAO) getDAO()).getOperatonIdsByResourceAndRole(resourceid, roleId);
	}

	/**
	 * 获取角色权限配置, JSON格式： {resourceid: [{'operationid': 操作ID1,
	 * 'resourcetype':资源类型1, 'resourcename':资源名称1, 'allow': 是否允许使用操作},
	 * {'operationid': 操作ID2, 'resourcetype':资源类型2, 'resourcename':资源名称2,
	 * 'allow': 是否允许使用操作}]}
	 * 
	 * @return
	 * @throws Exception
	 */
	public String getPermissionJSONByRole(String roleid) throws Exception {
		Collection<String> resourceIds = getResourceIdsByRole(roleid);
		StringBuilder builder = new StringBuilder();
		builder.append("{");
		if (resourceIds != null && !resourceIds.isEmpty()) {
			for (Iterator<String> iterator = resourceIds.iterator(); iterator
					.hasNext();) {
				String resourceId = iterator.next();

				Collection<PermissionVO> permissionList = doQueryByRoleAndResource(
						roleid, resourceId);
				if (!StringUtil.isBlank(resourceId)) {
					builder.append("'").append(resourceId).append("':");
					if (permissionList != null && !permissionList.isEmpty()) {
						builder.append("[");
						for (Iterator<PermissionVO> iterator2 = permissionList
								.iterator(); iterator2.hasNext();) {
							PermissionVO permissionVO = iterator2.next();
							builder.append("{");
							builder.append("'resourcename':'").append(
									permissionVO.getResName()).append("',");
							builder.append("'resourcetype':").append(
									permissionVO.getResType()).append(",");
							builder.append("'operationid':'").append(
									permissionVO.getOperationId()).append("',");
							builder.append("'allow':");
							if (PermissionVO.TYPE_ALLOW == permissionVO
									.getType()) {
								builder.append(true);
							} else {
								builder.append(false);
							}
							builder.append("},");
						}
						builder.deleteCharAt(builder.lastIndexOf(","));
						builder.append("]");
					}
					builder.append(",");
				}
			}
			builder.deleteCharAt(builder.lastIndexOf(","));
		}
		builder.append("}");

		return builder.toString();
	}

	public boolean check(Collection<RoleVO> roles, String resId, int operCode,
						 int resType) throws Exception {
		return check(roles, resId, operCode, resType, false);//update true to false by zb 2014-05-19
	}

	public boolean check(String[] roles, String resId, int operCode, int resType)
			throws Exception {
		return check(roles, resId, operCode, resType, true);
	}

	public boolean check(Collection<RoleVO> roles, String resId, int operCode,
			int resType, boolean defaultValue) throws Exception {
		if (roles != null && !roles.isEmpty()) {
			for (Iterator<RoleVO> iterator = roles.iterator(); iterator
					.hasNext();) {
				RoleVO role = iterator.next();
				boolean isAllow = check(role.getId(), resId, operCode,
						defaultValue);
				if (isAllow) {
					return true;
				}
			}
			return false;
		}

		return defaultValue;
	}

	public boolean check(String[] roles, String resId, int operCode,
			int resType, boolean defaultValue) throws Exception {
		if (roles != null && roles.length > 0) {
			for (int i = 0; i < roles.length; i++) {
				boolean isAllow = check(roles[i], resId, operCode, defaultValue);
				if (isAllow) {
					return true;
				}
			}

			return false;
		}

		return defaultValue;
	}

	public boolean check(String[] roles, String resId, String operId,
			int resType, boolean defaultValue) throws Exception {
		if (roles != null && roles.length > 0) {
			for (int i = 0; i < roles.length; i++) {
				boolean isAllow = check(roles[i], resId, operId, defaultValue);
				if (isAllow) {
					return true;
				}
			}
			return false;
		}

		return defaultValue;
	}

	public boolean check(Collection<RoleVO> roles, String resId, String operId,
			int resType, boolean defaultValue) throws Exception {
		if (roles != null && !roles.isEmpty()) {
			for (Iterator<RoleVO> iterator = roles.iterator(); iterator
					.hasNext();) {
				RoleVO role = iterator.next();
				boolean isAllow = check(role.getId(), resId, operId,
						defaultValue);
				if (isAllow) {
					return true;
				}
			}
			return false;
		}

		return defaultValue;
	}

	public boolean check(String[] roles, String resId, String operId,
			int resType) throws Exception {
		return check(roles, resId, operId, resType, true);
	}

	public boolean check(Collection<RoleVO> roles, String resId, String operId,
			int resType) throws Exception {
		return check(roles, resId, operId, resType, true);
	}

	/**
	 * 
	 * @param roleId
	 *            角色ID
	 * @param res
	 *            资源ID
	 * @param operCode
	 *            操作编码
	 * @param resType
	 *            资源类型
	 * @return
	 * @throws Exception
	 */
	public boolean check(String roleId, String resId, int operCode,
			boolean defaultValue) throws Exception {
		ParamsTable params = new ParamsTable();
		params.setParameter("t_roleId", roleId);
		params.setParameter("t_resId", resId);

		Collection<PermissionVO> permissions = doSimpleQuery(params);
		if (!permissions.isEmpty()) {
			// 资源设定了权限
			for (Iterator<PermissionVO> iterator = permissions.iterator(); iterator
					.hasNext();) {
				PermissionVO permissionVO = iterator.next();
				// if (operCode == permissionVO.getOperationCode()) {
				return PermissionVO.TYPE_ALLOW == permissionVO.getType();
				// }
			}

			return false;
		}

		return defaultValue;
	}

	/**
	 * 检查资源对应的操作是否有权限，默认没设置的情况下所有操作为都，当选择某些操作允许后，其他操作为不允许
	 * 
	 * @param roleId
	 *            角色ID
	 * @param resId
	 *            资源ID
	 * @param operId
	 *            操作ID
	 * @param defaultValue
	 * @return
	 * @throws Exception
	 */
	public boolean check(String roleId, String resId, String operId,
			boolean defaultValue) throws Exception {
		ParamsTable params = new ParamsTable();
		params.setParameter("t_roleId", roleId);
		params.setParameter("t_resId", resId);

		// 相关资源没设定权限则为通过
		Collection<PermissionVO> permissions = doSimpleQuery(params);
		if (!permissions.isEmpty()) {
			for (Iterator<PermissionVO> iterator = permissions.iterator(); iterator
					.hasNext();) {
				PermissionVO permissionVO = iterator.next();
				if (permissionVO.getOperationId().equals(operId)) {
					return PermissionVO.TYPE_ALLOW == permissionVO.getType();
				}
			}

			return false;
		}

		return defaultValue;
	}

	@SuppressWarnings("unchecked")
	public void grantAuth(Map<String, Object> permissionMap, ParamsTable params1)
			throws Exception {
//		RoleProcess roleProcess = (RoleProcess) ProcessFactory
//				.createProcess(RoleProcess.class);
		OperationProcess operationProcess = (OperationProcess) ProcessFactory
				.createProcess(OperationProcess.class);
		String roleid = params1.getParameterAsString("roleid");
		String applicationid = params1.getParameterAsString("applicationid");
		List<String> roleList = new ArrayList<String>();
		roleList.add(roleid);
		String rolesSelected = params1.getParameterAsString("rolesSelected");
		if (rolesSelected != null) {
			String[] roles = rolesSelected.split(";");
			for (int i = 0; i < roles.length; i++) {
				if (!StringUtil.isBlank(roles[i])) {
					roleList.add(roles[i]);
				}
			}
		}
		// System.out.println("grantAuth: " + permissionMap);

		try {
			PersistenceUtils.beginTransaction();
			// 清空角色所有权限
			for (int i = 0; i < roleList.size(); i++) {
				ParamsTable params = new ParamsTable();
				params.setParameter("s_role_id", roleList.get(i));
				DataPackage<PermissionVO> datas = getDAO().query(params);
				if (datas.rowCount > 0) {
					for (Iterator<PermissionVO> iter = datas.datas.iterator(); iter
							.hasNext();) {
						PermissionVO permission = iter.next();
						getDAO().remove(permission);
					}
				}
			}

			if (permissionMap != null && !permissionMap.isEmpty()) {
				for (int k = 0; k < roleList.size(); k++) {
					// 重新为角色赋权
					for (Iterator<Entry<String, Object>> iterator = permissionMap
							.entrySet().iterator(); iterator.hasNext();) {
						Entry<String, Object> entry = iterator.next();
						String resourceid = entry.getKey();
						Object[] operationPermissions = (Object[]) entry
								.getValue();

						// 遍历资源选择的操作
						for (int i = 0; i < operationPermissions.length; i++) {
							Map operationPermission = (Map) operationPermissions[i];

							Integer resourcetype = (Integer) operationPermission
									.get("resourcetype");
							String resourcename = (String) operationPermission
									.get("resourcename");
							String operationid = (String) operationPermission
									.get("operationid");
							boolean allow = (Boolean) operationPermission
									.get("allow");

							OperationVO operationVO = operationProcess
									.doViewByResource(operationid, resourceid,
											resourcetype);

							if (operationVO == null || operationVO.getCode() == null) {
								System.out.println("operationVO is null");
							}
							
							
							PermissionVO pv = new PermissionVO();
							pv.setId(Tools.getSequence());
							pv.setResId(resourceid);
							pv.setResName(resourcename);
							pv.setResType(Integer.valueOf(resourcetype));
							pv.setOperationId(operationVO.getId());
							pv.setOperationCode(operationVO.getCode());
							if (allow) { // 赋予禁止或者允许权限
								pv.setType(PermissionVO.TYPE_ALLOW);
							} else {
								pv.setType(PermissionVO.TYPE_FORBID);
							}

							pv.setApplicationid(applicationid);
							pv.setRoleId(roleList.get(k));
							getDAO().create(pv);
						}
					}
				}
			}

			PersistenceUtils.commitTransaction();
		} catch (Exception e) {
			PersistenceUtils.rollbackTransaction();
			e.printStackTrace();
			throw e;
		}

	}

	/**
	 * 授权
	 */
	public void grantAuth(String[] _selectsResources, ParamsTable params1)
			throws Exception {
		try {
			ResProcess rprocess = (ResProcess) ProcessFactory
					.createProcess(ResProcess.class);
			OperationProcess operationProcess = (OperationProcess) ProcessFactory
					.createProcess(OperationProcess.class);

			String roleid = params1.getParameterAsString("roleid");
			String applicationid = params1
					.getParameterAsString("applicationid");

			PersistenceUtils.beginTransaction();
			// 清空选中资源的所有权限
			for (int i = 0; i < _selectsResources.length; i++) {
				ParamsTable params = new ParamsTable();
				params.setParameter("s_RES_ID", _selectsResources[i]);
				params.setParameter("s_role_id", roleid);

				DataPackage<PermissionVO> datas = getDAO().query(params);

				if (datas.rowCount > 0) {
					for (Iterator<PermissionVO> iterator = datas.datas
							.iterator(); iterator.hasNext();) {
						PermissionVO permission = iterator.next();
						getDAO().remove(permission);
					}
				}
			}

			// 重新添加选中资源权限
			for (int i = 0; i < _selectsResources.length; i++) {
				String resourceid = _selectsResources[i];
				String operationidstr = params1.getParameterAsString(resourceid
						+ "_selects");
				String type = params1.getParameterAsString(resourceid
						+ "_resourcesType");
				ResVO resourcesVO = (ResVO) rprocess.doView(resourceid);

				String[] operationids = operationidstr.split(";");
				for (int j = 0; j < operationids.length; j++) {
					String operaiontid = operationids[j];
					OperationVO operationVO = (OperationVO) operationProcess
							.doView(operaiontid);

					PermissionVO pv = new PermissionVO();
					pv.setId(Tools.getSequence());
					pv.setResId(resourcesVO.getId());
					pv.setResName(resourcesVO.getName());
					pv.setOperationId(operationVO.getId());
					pv.setOperationCode(operationVO.getCode());

					if (type != null) {
						// 设置允许或禁止
						pv.setType(Integer.parseInt(type));
					}
					pv.setApplicationid(applicationid);
					pv.setRoleId(roleid);
					getDAO().create(pv);
				}
			}

			PersistenceUtils.commitTransaction();
		} catch (Exception e) {
			PersistenceUtils.rollbackTransaction();
			e.printStackTrace();
			throw e;
		}

	}

	/**
	 * 取消授权
	 */
	public void removeAuth(String[] _selectsResources, ParamsTable params1)
			throws Exception {
		try {
			PersistenceUtils.beginTransaction();

			String roleid = params1.getParameterAsString("roleid");
			RoleProcess roleProcess = (RoleProcess) ProcessFactory
					.createProcess(RoleProcess.class);
			RoleVO roleVO = (RoleVO) roleProcess.doView(roleid);

			Collection<PermissionVO> permissions = roleVO.getPermission();

			for (int i = 0; i < _selectsResources.length; i++) {
				String resourcesid = _selectsResources[i];
				for (Iterator<PermissionVO> iterator = permissions.iterator(); iterator
						.hasNext();) {
					PermissionVO permission = iterator.next();
					if (permission.getResId() != null
							&& permission.getResId().equals(resourcesid)) {
						getDAO().remove(permission);
					}
				}
			}

			PersistenceUtils.commitTransaction();
		} catch (Exception e) {
			PersistenceUtils.rollbackTransaction();
			e.printStackTrace();
			throw e;
		}

	}
}
