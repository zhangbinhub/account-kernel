package OLink.bpm.core.permission.ejb;

import java.util.Collection;
import java.util.Map;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.core.role.ejb.RoleVO;
import OLink.bpm.base.ejb.IDesignTimeProcess;

public interface PermissionProcess extends IDesignTimeProcess<PermissionVO> {
	/**
	 * 空实现
	 * 
	 * @return
	 * @throws Exception
	 */
	PermissionVO getAppDomain_Cache() throws Exception;

	/**
	 * 根据菜单和用户查询(PermissionVO)对象
	 * 
	 * @param resourceId
	 *            菜单标识
	 * @param userId
	 *            用户标识
	 * @return (PermissionVO)对象
	 * @throws Exception
	 */
	PermissionVO findByResouceAndUser(String resourceId, String userId)
			throws Exception;

	PermissionVO getPermissionByName(String name) throws Exception;

	/**
	 * 授权
	 * 
	 * @param _selects
	 * @param params1
	 * @param process
	 * @throws Exception
	 */
	void grantAuth(String[] _selectsResources, ParamsTable params1)
			throws Exception;

	/**
	 * 取消授权
	 * 
	 * @param _selects
	 * @param params1
	 * @param process
	 * @throws Exception
	 */
	void removeAuth(String[] _selectsResources, ParamsTable params1)
			throws Exception;

	/**
	 * 根据角色进行查询
	 * 
	 * @param roleId
	 *            角色ID
	 * @param resType
	 *            资源类型编号
	 * @return 角色集合
	 * @throws Exception
	 */
	Collection<PermissionVO> doQueryByRole(String roleId, int resType)
			throws Exception;

	/**
	 * 根据角色进行查询
	 * 
	 * @param roleId
	 *            角色ID
	 * @return 角色集合
	 * @throws Exception
	 */
	Collection<PermissionVO> doQueryByRole(String roleId)
			throws Exception;

	/**
	 * 检查某一类操作是否有权限
	 * 
	 * @param roles
	 *            角色集合
	 * @param resId
	 *            资源ID
	 * @param operCode
	 *            操作
	 * @param resType
	 * @return
	 * @throws Exception
	 */
	boolean check(Collection<RoleVO> roles, String resId, int operCode,
				  int resType) throws Exception;

	boolean check(String[] roles, String resId, int operCode, int resType)
			throws Exception;

	/**
	 * 检查具体的某一个操作是否有权限
	 * 
	 * @param roles
	 * @param resId
	 * @param operId
	 * @param resType
	 * @return
	 * @throws Exception
	 */
	boolean check(String[] roles, String resId, String operId,
				  int resType) throws Exception;

	boolean check(Collection<RoleVO> roles, String resId, String operId,
				  int resType) throws Exception;

	boolean check(String[] roles, String resId, int operCode,
				  int resType, boolean defaultValue) throws Exception;

	boolean check(Collection<RoleVO> roles, String resId, int operCode,
				  int resType, boolean defaultValue) throws Exception;

	Collection<PermissionVO> doQueryByRoleIdAndResName(String roleId,
													   String resName) throws Exception;

	void grantAuth(Map<String, Object> permissionMap, ParamsTable params1)
			throws Exception;

	Collection<String> getOperatonIdsByResourceAndRole(
			String resourceid, String roleId) throws Exception;

	/**
	 * 获取角色权限配置, JSON格式：
	 * {resourceid:{resourcename:资源名称,resourcetype:资源类型,operations:[操作1,操作2]}}
	 * 
	 * @return
	 * @throws Exception
	 */
	String getPermissionJSONByRole(String roleid) throws Exception;

	Collection<String> getResourceIdsByRole(String roleId)
			throws Exception;
}
