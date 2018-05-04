package OLink.bpm.core.role.dao;

import java.util.Collection;

import OLink.bpm.base.dao.HibernateBaseDAO;
import OLink.bpm.core.role.ejb.RoleVO;
import OLink.bpm.base.action.ParamsTable;

public class HibernateRoleDAO extends HibernateBaseDAO<RoleVO> implements RoleDAO {
	public HibernateRoleDAO(String voClassName) {
		super(voClassName);
	}

	/**
	 * 因Role的名字是不允许重复的，该方法在下一版本删除
	 */
	public Collection<RoleVO> getRoleByName(String byName, String application) throws Exception {
		String hql = "FROM " + this._voClazzName + " vo where vo.name =" + "'" + byName + "'";
		ParamsTable params = new ParamsTable();
		params.setParameter("application", application);
		return getDatas(hql, params);
	}

	/**
	 * 部门不再与角色关联，此方法将在下一版本删除
	 */
	public Collection<RoleVO> getRolesByDepartment(String deptid) throws Exception {
		String hql = "FROM " + this._voClazzName + " vo";
		hql += " WHERE vo.department.id='" + deptid + "'";
		return getDatas(hql);
	}

	/**
	 * 部门不再与角色关联，此方法将在下一版本删除
	 */
	public Collection<RoleVO> getRolesByDepartments(String[] deptids) throws Exception {
		StringBuffer buffer = new StringBuffer();
		Collection<RoleVO> rtn = null;
		if (deptids.length > 0) {
			buffer.append("(");
			for (int i = 0; i < deptids.length; i++) {
				String deptid = deptids[i];
				buffer.append("'" + deptid + "',");
			}
			buffer.deleteCharAt(buffer.lastIndexOf(","));
			buffer.append(")");
			String hql = "FROM " + this._voClazzName + " vo";
			hql += " WHERE vo.department.id in " + buffer.toString();
			rtn = getDatas(hql);
		}
		return rtn;
	}

	public Collection<RoleVO> getRolesByApplication(String applicationId) throws Exception {
		Collection<RoleVO> roles = null;

		String hql = "FROM " + this._voClazzName + " vo";
		hql += " WHERE vo.applicationid = '" + applicationId + "' order by vo.name";
		roles = getDatas(hql);

		return roles;
	}

	public RoleVO findByName(String name, String applicationid) throws Exception {
		String hql = "FROM " + this._voClazzName + " vo";
		hql += " WHERE vo.name='" + name + "' AND vo.applicationid = '" + applicationid + "'";
		return (RoleVO) getData(hql);
	}

	public RoleVO findByRoleNo(String roleNo, String applicationid) throws Exception {
		String hql = "FROM " + this._voClazzName + " vo";
		hql += " WHERE vo.roleNo='" + roleNo + "' AND vo.applicationid = '" + applicationid + "'";
		return (RoleVO)getData(hql);
	}

	public Collection<RoleVO> getRolesByapplicationids(String applicationids) throws Exception {
		Collection<RoleVO> roles = null;
		String hql = "FROM " + this._voClazzName + " vo";
		hql += " WHERE vo.applicationid in(" + applicationids + ")";
		roles = getDatas(hql);
		return roles;
	}
}
