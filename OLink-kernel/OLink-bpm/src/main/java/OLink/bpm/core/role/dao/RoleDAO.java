package OLink.bpm.core.role.dao;

import java.util.Collection;

import OLink.bpm.core.role.ejb.RoleVO;
import OLink.bpm.base.dao.IDesignTimeDAO;

public interface RoleDAO extends IDesignTimeDAO<RoleVO> {
	Collection<RoleVO> getRoleByName(String byName, String application) throws Exception;

	Collection<RoleVO> getRolesByDepartment(String deptid) throws Exception;

	Collection<RoleVO> getRolesByDepartments(String[] deptids) throws Exception;

	Collection<RoleVO> getRolesByApplication(String applicationId) throws Exception;

	RoleVO findByName(String name, String applicationid) throws Exception;
	
	RoleVO findByRoleNo(String roleNo, String applicationid) throws Exception;
	
	Collection<RoleVO> getRolesByapplicationids(String applicationids)throws Exception;
}
