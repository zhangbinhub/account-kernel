package OLink.bpm.core.permission.dao;

import java.util.Collection;

import OLink.bpm.base.dao.IDesignTimeDAO;
import OLink.bpm.core.permission.ejb.PermissionVO;

public interface PermissionDAO extends IDesignTimeDAO<PermissionVO> {
	PermissionVO findByResouceAndUser(String resourceId, String userId)
			throws Exception;

	PermissionVO getPermissionByName(String name) throws Exception;

	PermissionVO getPermissionByResourcesId(String resourcesId)
			throws Exception;

	Collection<PermissionVO> queryByRoleIdAndResName(String roleId,
													 String resName) throws Exception;

	Collection<PermissionVO> queryByRole(String roleId, int resType)
			throws Exception;

	Collection<PermissionVO> queryByRole(String roleId) throws Exception;

	Collection<PermissionVO> queryByRoleAndResource(String roleId,
													String resourceId) throws Exception;

	Collection<String> getResourceIdsByRole(String roleId)
			throws Exception;

	Collection<String> getOperatonIdsByResourceAndRole(
			String resourceid, String roleId) throws Exception;

}
