package OLink.bpm.core.department.ejb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import OLink.bpm.base.dao.DAOFactory;
import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.base.dao.IDesignTimeDAO;
import OLink.bpm.base.dao.PersistenceUtils;
import OLink.bpm.base.dao.ValueObject;
import OLink.bpm.base.ejb.AbstractDesignTimeProcessBean;
import OLink.bpm.core.department.dao.DepartmentDAO;
import OLink.bpm.core.permission.ejb.PermissionPackage;
import eWAP.core.Tools;

public class DepartmentProcessBean extends AbstractDesignTimeProcessBean<DepartmentVO> implements DepartmentProcess {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8039123698415823890L;

	/*
	 * (non-Javadoc)根据参数VO创建新的VO对象,
	 * 
	 * @see AbstractDesignTimeProcessBean#doCreate(ValueObject)
	 */
	public void doCreate(ValueObject vo) throws Exception {
		try {
			PersistenceUtils.beginTransaction();
			if (vo.getId() == null || vo.getId().trim().length() == 0) {
				vo.setId(Tools.getSequence());
			}
			if(((DepartmentVO)vo).getSuperior() != null){
				((DepartmentVO)vo).setIndexCode(((DepartmentVO)vo).getSuperior().getIndexCode()+"_"+vo.getId());
			}
			if (vo.getSortId() == null || vo.getSortId().trim().length() == 0) {
				vo.setSortId(Tools.getTimeSequence());
			}

			getDAO().create(vo);
			PersistenceUtils.commitTransaction();
		} catch (Exception e) {
			PersistenceUtils.rollbackTransaction();
			e.printStackTrace();
			throw e;
		}
		PermissionPackage.clearCache();
	}

	/*
	 * 移除一条数据,根据pk值 (non-Javadoc)
	 * 
	 * @see AbstractDesignTimeProcessBean#doRemove(java.lang.String)
	 */
	public void doRemove(String pk) throws Exception {
		// 检查是否是根部门

		// 检查是否有下级部门
		DepartmentVO tempDepartment;
		Collection<DepartmentVO> subs = ((DepartmentDAO) getDAO()).getDatasByParent(pk);
		if (subs != null && !subs.isEmpty()) {
			throw new DepartmentException("{*[core.department.hassub]*}");
		}
		tempDepartment = (DepartmentVO) getDAO().find(pk);
		if (tempDepartment.getUsers().size() > 0) {
			throw new DepartmentException("{*[core.department.hasuser]*}");
		}

		super.doRemove(pk);
		PermissionPackage.clearCache();
	}

	/*
	 * 根据VO更新一条数据, (non-Javadoc)
	 * 
	 * @see AbstractDesignTimeProcessBean#doUpdate(ValueObject)
	 */
	public void doUpdate(ValueObject vo) throws Exception {
		try {
			PersistenceUtils.beginTransaction();
			if(((DepartmentVO)vo).getSuperior() != null){
				((DepartmentVO)vo).setIndexCode(((DepartmentVO)vo).getSuperior().getIndexCode()+"_"+vo.getId());
			}
			DepartmentVO po = (DepartmentVO) getDAO().find(vo.getId());
			if (po != null) {
				if (po.getSuperior() != null && ((DepartmentVO) vo).getSuperior() != null
						&& !po.getSuperior().getId().equals((((DepartmentVO) vo).getSuperior()).getId())) {
					changLevel((DepartmentVO) vo);
				}
				getDAO().update(vo);
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

	public Collection<DepartmentVO> doSimpleQuery(ParamsTable params) throws Exception {
		return getDAO().simpleQuery(params);
	}

	public Collection<DepartmentVO> getDatasByParent(String parent) throws Exception {
		return ((DepartmentDAO) getDAO()).getDatasByParent(parent);
	}

//	public String getIdByName(String deptname, String application, String domain) throws Exception {
//		return ((DepartmentDAO) getDAO()).getIdByName(deptname, application, domain);
//	}

	protected IDesignTimeDAO<DepartmentVO> getDAO() throws Exception {
		return (DepartmentDAO) DAOFactory.getDefaultDAO(DepartmentVO.class.getName());
	}

	public DepartmentVO getRootDepartmentByApplication(String application, String domain) throws Exception {
		return ((DepartmentDAO) getDAO()).getRootDepartmentByApplication(application, domain);
	}

	public Map<String, String> deepSearchDepartmentTree(Collection<DepartmentVO> cols, DepartmentVO startNode,
			String excludeNodeId, int deep) throws Exception {
		Map<String, String> list = new LinkedHashMap<String, String>();

		String prefix = "|------------------------------------------------";
		if (startNode != null) {
			list.put(startNode.getId(), prefix.substring(0, deep * 2) + startNode.getName());
		}

		Iterator<DepartmentVO> iter = cols.iterator();
		while (iter.hasNext()) {
			DepartmentVO vo = iter.next();
			if (startNode == null) {
				if (vo.getSuperior() == null) {
					if (vo.getId() != null && !vo.getId().equals(excludeNodeId)) {
						Map<String, String> tmp = deepSearchDepartmentTree(cols, vo, excludeNodeId, deep + 1);
						list.putAll(tmp);
					}
				}
			} else {
				if (vo.getSuperior() != null && vo.getSuperior().getId().equals(startNode.getId())) {
					if (vo.getId() != null && !vo.getId().equals(excludeNodeId)) {
						Map<String, String> tmp = deepSearchDepartmentTree(cols, vo, excludeNodeId, deep + 1);
						list.putAll(tmp);
					}
				}
			}
		}
		return list;
	}

	public Collection<DepartmentVO> getUnderDeptList(String deptid) throws Exception {
		return getUnderDeptList(deptid, Integer.MAX_VALUE);
	}

	public Collection<DepartmentVO> getUnderDeptList(String deptid, int maxDeep) throws Exception {
		return getUnderDeptList(deptid, maxDeep, false);
	}

	/**
	 * 获取下级部门列表
	 * 
	 * @param deptid
	 * @param maxDeep
	 * @param isExcludeSelf
	 *            是否将同级部门排除在外
	 * @return
	 * @throws Exception
	 */
	public Collection<DepartmentVO> getUnderDeptList(String deptid, int maxDeep, boolean isExcludeSelf)
			throws Exception {
		Collection<DepartmentVO> colls = new ArrayList<DepartmentVO>();
		if (maxDeep <= 0) {
			return colls;
		}

		Iterator<DepartmentVO> itmp = getDatasByParent(deptid).iterator();
		while (itmp.hasNext()) {
			DepartmentVO dept = itmp.next();
			if (dept.getSuperior() != null && dept.getSuperior().getId().equals(deptid)) {
				colls.add(dept);
				colls.addAll(getUnderDeptList(dept.getId(), --maxDeep, isExcludeSelf));
			} else if (!isExcludeSelf && dept.getId() != null && dept.getId().equals(deptid)) {
				colls.add(dept);
				colls.addAll(getUnderDeptList(dept.getId(), --maxDeep, isExcludeSelf));
			}
		}

		return colls;
	}

	public Collection<DepartmentVO> getSuperiorDeptListExcludeCurrent(String depid) throws Exception {
		HashSet<DepartmentVO> result = new HashSet<DepartmentVO>();
		DepartmentVO dep = (DepartmentVO) doView(depid);
		if (dep != null) {
			while (dep.getSuperior() != null) {
				dep = dep.getSuperior();
				result.add(dep);
			}
		}
		return result;

	}

	public Collection<DepartmentVO> getSuperiorDeptList(String depid) throws Exception {
		HashSet<DepartmentVO> result = new HashSet<DepartmentVO>();
		DepartmentVO dep = (DepartmentVO) doView(depid);
		if (dep != null) {
			result.add(dep);
			while (dep.getSuperior() != null) {
				dep = dep.getSuperior();
				result.add(dep);
			}
		}
		return result;

	}

	private void changLevel(DepartmentVO vo) throws Exception {
		Collection<DepartmentVO> colls = new ArrayList<DepartmentVO>();
		Collection<DepartmentVO> departmentlist = ((DepartmentDAO) getDAO()).getAllDepartment(vo.getApplicationid(), vo
				.getDomain().getId());
		Iterator<DepartmentVO> itmp = departmentlist.iterator();
		while (itmp.hasNext()) {
			DepartmentVO dept = itmp.next();
			if (dept.getSuperior() != null && dept.getSuperior().getId().equals(vo.getId())) {
				dept.setLevel(vo.getLevel() + 1);
				getDAO().update(dept);
				colls.add(dept);
			}
		}

		if (colls != null) {
			Iterator<DepartmentVO> iter = colls.iterator();
			while (iter.hasNext()) {
				DepartmentVO dept = iter.next();
				if (dept != null) {
					changLevel(dept);
				}
			}
		}
	}

	public Collection<DepartmentVO> getDepartmentByLevel(int level, String application, String domain) throws Exception {
		return ((DepartmentDAO) getDAO()).getDepartmentByLevel(level, application, domain);
	}

	public Collection<DepartmentVO> getDepartmentByName(String byName, String domain) throws Exception {
		return ((DepartmentDAO) getDAO()).getDepartmentByName(byName, domain);
	}

	public Collection<DepartmentVO> getDepartmentByCode(String byCode, String domain) throws Exception {
		return ((DepartmentDAO) getDAO()).getDepartmentByCode(byCode, domain);
	}

	public long getChildrenCount(String parent) throws Exception {
		return ((DepartmentDAO) getDAO()).getChildrenCount(parent);
	}

	public Collection<String> getUpperDeptIds(String deptid) throws Exception {
		return getUpperDeptIds(deptid, 0);
	}

	public Collection<String> getUpperDeptIds(String deptid, int level) throws Exception {
		Collection<String> result = new ArrayList<String>();
		if (level < 0) {
			level = 0;
		}

		DepartmentVO dept = (DepartmentVO) doView(deptid);
		while (dept != null && dept.getLevel() > level) {
			dept = dept.getSuperior();
			result.add(dept.getId());
		}
		return result;
	}

	public ValueObject doView(String pk) throws Exception {
		return super.doView(pk);
	}

	public Collection<DepartmentVO> queryByDomain(String domain) throws Exception {
		return queryByDomain(domain, 1, Integer.MAX_VALUE);
	}

	private Collection<DepartmentVO> queryByDomain(String domain, int page, int pagelines) throws Exception {
		return ((DepartmentDAO) getDAO()).queryByDomain(domain, page, pagelines);
	}

	public Collection<DepartmentVO> queryByUser(String userId) throws Exception {
		String sql = "SELECT vo.* FROM " + getDAO().getSchema() + "T_DEPARTMENT" + " vo";
		sql += " WHERE vo.ID in (select s.DEPARTMENTID from " + getDAO().getSchema() + "T_USER_DEPARTMENT_SET s";
		sql += " WHERE s.USERID='" + userId + "')";

		return getDAO().getDatasBySQL(sql);
	}

	public Collection<DepartmentVO> doQueryAllSubordinatesByIndexCode(
			String indexCode) throws Exception {
		return ((DepartmentDAO) getDAO()).queryAllSubordinatesByIndexCode(indexCode);
	}

	public Collection<DepartmentVO> doQueryAllSuperiorsByIndexCode(
			String indexCode) throws Exception {
		return ((DepartmentDAO) getDAO()).queryAllSuperiorsByIndexCode(indexCode);
	}

	public Collection<DepartmentVO> doQueryLineSubordinatesByIndexCode(
			String indexCode) throws Exception {
		return ((DepartmentDAO) getDAO()).queryLineSubordinatesByIndexCode(indexCode);
	}

	public DepartmentVO findByIndexCode(String indexCode) throws Exception {
		return ((DepartmentDAO) getDAO()).findByIndexCode(indexCode);
	}

	public DepartmentVO findLineSuperiorByIndexCode(String indexCode)
			throws Exception {
		return ((DepartmentDAO) getDAO()).findLineSuperiorByIndexCode(indexCode);
	}
}
