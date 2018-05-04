package OLink.bpm.core.permission.dao;

import java.util.Collection;

import OLink.bpm.base.dao.DAOFactory;
import OLink.bpm.base.dao.HibernateBaseDAO;
import OLink.bpm.base.dao.ValueObject;
import OLink.bpm.core.permission.ejb.PermissionPackage;
import OLink.bpm.core.permission.ejb.PermissionVO;

public class HibernatePermissionDAO extends HibernateBaseDAO<PermissionVO>
		implements PermissionDAO {

	public HibernatePermissionDAO(String voClassName) {
		super(voClassName);
	}

	public void create(ValueObject vo) throws Exception {
		super.create(vo);
		PermissionPackage.clearCache();
	}

	public void remove(String id) throws Exception {
		super.remove(id);
		PermissionPackage.clearCache();
	}

	public void update(ValueObject vo) throws Exception {
		super.update(vo);
		PermissionPackage.clearCache();
	}

	public PermissionVO findByResouceAndUser(String resourceId, String userId)
			throws Exception {
		String hql = "FROM " + _voClazzName + " vo";
		hql += " WHERE vo.resourceId = '" + resourceId + "'";
		hql += " AND vo.user.id = '" + userId + "'";
		return (PermissionVO) getData(hql);
	}

	public PermissionVO getPermissionByName(String name) throws Exception {
		String hql = "FROM " + _voClazzName + " vo " + " WHERE vo.name = '"
				+ name + "'";
		return (PermissionVO) getData(hql);
	}

	public PermissionVO getPermissionByResourcesId(String resourcesId)
			throws Exception {
		String hql = "FROM " + _voClazzName + " vo "
				+ " WHERE vo.resources.id = '" + resourcesId + "'";
		return (PermissionVO) getData(hql);
	}

	@SuppressWarnings("unchecked")
	public Collection<String> getOperatonIdsByResourceAndRole(
			String resourceid, String roleId) throws Exception {
		String hql = "SELECT vo.operationId FROM "
			+ PermissionVO.class.getName() + " vo";
		hql += " WHERE vo.resId = '" + resourceid + "' AND vo.roleId = '"
			+ roleId + "'";
		return (Collection<String>) DAOFactory.getDefaultDAO(
				PermissionVO.class.getName()).getDatas(hql);
	}

	@SuppressWarnings("unchecked")
	public Collection<String> getResourceIdsByRole(String roleId)
			throws Exception {
		String hql = "SELECT vo.resId FROM " + PermissionVO.class.getName()
		+ " vo";
		hql += " WHERE vo.roleId = '" + roleId + "' GROUP BY vo.resId";
		return (Collection<String>) DAOFactory.getDefaultDAO(
				PermissionVO.class.getName()).getDatas(hql);
	}

	public Collection<PermissionVO> queryByRole(String roleId, int resType)
			throws Exception {
		String schema = getSchema();
		String sql = "SELECT p.* FROM " + schema + "T_PERMISSION p";
		sql += " INNER JOIN " + schema + "T_RES res ON p.RES_ID=res.ID";
		sql += " WHERE p.ROLE_ID='" + roleId + "' AND res.TYPE=" + resType + "";
		return getDatasBySQL(sql);
	}

	public Collection<PermissionVO> queryByRole(String roleId) throws Exception {
		String hql = "FROM " + PermissionVO.class.getName() + " vo";
		hql += " WHERE vo.roleId = '" + roleId + "'";
		return getDatas(hql);
	}

	public Collection<PermissionVO> queryByRoleAndResource(String roleId,
			String resourceId) throws Exception {
		String hql = "FROM " + PermissionVO.class.getName() + " vo";
		hql += " WHERE vo.roleId = '" + roleId + "' AND vo.resId='"
				+ resourceId + "'";
		return getDatas(hql);
	}

	public Collection<PermissionVO> queryByRoleIdAndResName(String roleId,
			String resName) throws Exception {
		String schema = getSchema();
		String sql = "SELECT p.* FROM " + schema + "T_PERMISSION p";
		sql += " WHERE p.ROLE_ID='" + roleId + "' AND p.RESNAME='" + resName
				+ "'";
		return this.getDatasBySQL(sql);
	}

}
