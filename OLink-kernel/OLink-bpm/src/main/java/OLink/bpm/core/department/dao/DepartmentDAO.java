package OLink.bpm.core.department.dao;

import java.util.Collection;

import OLink.bpm.base.dao.IDesignTimeDAO;
import OLink.bpm.core.department.ejb.DepartmentVO;

public interface DepartmentDAO extends IDesignTimeDAO<DepartmentVO> {

	Collection<DepartmentVO> getDatasByParent(String parent) throws Exception;

//	public abstract String getIdByName(String deptname, String application,
//			String domain) throws Exception;

	Collection<DepartmentVO> getAllDepartment(String application,
											  String domain) throws Exception;

	Collection<DepartmentVO> getDepartmentByLevel(int level,
												  String application, String domain) throws Exception;

	Collection<DepartmentVO> getDepartmentByName(String byName, String domain)
			throws Exception;

	Collection<DepartmentVO> getDepartmentByCode(String byCode, String domain)
			throws Exception;

	DepartmentVO getRootDepartmentByApplication(
			String application, String domain) throws Exception;

	long getChildrenCount(String parent) throws Exception;

	Collection<DepartmentVO> queryByDomain(String domain, int page,
										   int pagelines) throws Exception;

	DepartmentVO findByName(String name, String domain)
			throws Exception;
	
	/**
	 * 根据索引编号查找部门
	 * @param indexCode
	 * @return
	 * @throws Exception
	 */
	DepartmentVO findByIndexCode(String indexCode) throws Exception;
	
	/**
	 * 根据索引编号查找直属上级部门
	 * @param indexCode
	 * @return
	 * @throws Exception
	 */
	DepartmentVO findLineSuperiorByIndexCode(String indexCode) throws Exception;
	
	/**
	 * 根据索引编号查找直属下级部门的集合
	 * @param indexCode
	 * @return
	 * @throws Exception
	 */
	Collection<DepartmentVO> queryLineSubordinatesByIndexCode(String indexCode) throws Exception;
	
	/**
	 * 根据索引编号查找所有上级部门的集合
	 * @param indexCode
	 * @return
	 * @throws Exception
	 */
	Collection<DepartmentVO> queryAllSuperiorsByIndexCode(String indexCode) throws Exception;
	
	/**
	 * 根据索引编号查找所有下级部门的集合
	 * @param indexCode
	 * @return
	 * @throws Exception
	 */
	Collection<DepartmentVO> queryAllSubordinatesByIndexCode(String indexCode) throws Exception;
}
