package OLink.bpm.core.role.ejb;

import java.util.Collection;

import OLink.bpm.base.dao.DAOFactory;
import OLink.bpm.base.dao.IDesignTimeDAO;
import OLink.bpm.base.dao.PersistenceUtils;
import OLink.bpm.base.dao.ValueObject;
import OLink.bpm.core.permission.ejb.PermissionPackage;
import OLink.bpm.core.role.dao.RoleDAO;
import OLink.bpm.base.ejb.AbstractDesignTimeProcessBean;
import org.apache.commons.beanutils.PropertyUtils;

public class RoleProcessBean extends AbstractDesignTimeProcessBean<RoleVO> implements RoleProcess {
	private static final long serialVersionUID = -6347281773885869778L;

	public void doCreate(ValueObject vo) throws Exception {
		super.doCreate(vo);
		PermissionPackage.clearCache();
	}

	public void doRemove(String pk) throws Exception {
		// 检查是否有下属用户
		RoleVO role = (RoleVO) getDAO().find(pk);
		if (role.getUsers() != null && !role.getUsers().isEmpty()) {
			throw new Exception("(" + role.getName() + "){*[core.role.cannotremove]*}");
		}

		try {
			PersistenceUtils.beginTransaction();
			getDAO().remove(pk);
			PersistenceUtils.commitTransaction();
		} catch (Exception e) {
			PersistenceUtils.rollbackTransaction();
		}

		PermissionPackage.clearCache();
	}

	public void doUpdate(ValueObject vo) throws Exception {
		try {
			PersistenceUtils.beginTransaction();

			RoleVO po = (RoleVO) getDAO().find(vo.getId());
			if (po != null) {
				PropertyUtils.copyProperties(po, vo);
				getDAO().update(po);
			} else {
				getDAO().update(vo);
			}

			PersistenceUtils.commitTransaction();
		} catch (Exception e) {
			e.printStackTrace();
			PersistenceUtils.rollbackTransaction();
		}
		PermissionPackage.clearCache();
	}

	//@SuppressWarnings("unchecked")
	protected IDesignTimeDAO<RoleVO> getDAO() throws Exception {
		return (RoleDAO) DAOFactory.getDefaultDAO(RoleVO.class.getName());
	}

	/**
	 * 部门不再与角色关联，此方法将在下一版本删除
	 */
	public Collection<RoleVO> getRolesByDepartment(String deptid) throws Exception {
		return (((RoleDAO) getDAO())).getRolesByDepartment(deptid);
	}

	/**
	 * 部门不再与角色关联，此方法将在下一版本删除
	 */
	public Collection<RoleVO> getRolesByDepartments(String[] deptids) throws Exception {
		return (((RoleDAO) getDAO())).getRolesByDepartments(deptids);
	}

	/**
	 * 部门不再与角色关联，此方法将在下一版本删除
	 */
	public Collection<RoleVO> getRolesByDepartments(Collection<String> deptids) throws Exception {
		String[] dpetArray = deptids.toArray(new String[deptids.size()]);
		return (((RoleDAO) getDAO())).getRolesByDepartments(dpetArray);
	}

	/**
	 * 根据应用标识返回角色组别
	 * 
	 * @param applicatrionid
	 *            应用标识
	 * @return 角色组的集合
	 */
	public Collection<RoleVO> getRolesByApplication(String applicationid) throws Exception {
		return (((RoleDAO) getDAO())).getRolesByApplication(applicationid);
	}

	public RoleVO doViewByName(String name, String applicationid) throws Exception {
		return ((RoleDAO) getDAO()).findByName(name, applicationid);
	}

	public Collection<RoleVO> queryByUser(String userId) throws Exception {
		String sql = "SELECT vo.* FROM " + getDAO().getSchema() + "T_ROLE" + " vo";
		sql += " WHERE vo.ID in (select s.ROLEID from " + getDAO().getSchema() + "T_USER_ROLE_SET s";
		sql += " WHERE s.USERID='" + userId + "')";

		return getDAO().getDatasBySQL(sql);
	}

	public RoleVO findByRoleNo(String roleNo, String applicationid) throws Exception {
		return ((RoleDAO) getDAO()).findByRoleNo(roleNo, applicationid);
	}

	public Collection<RoleVO> getRolesByapplicationids(String applicationids) throws Exception {
		return ((RoleDAO) getDAO()).getRolesByapplicationids(applicationids);
	}
}
