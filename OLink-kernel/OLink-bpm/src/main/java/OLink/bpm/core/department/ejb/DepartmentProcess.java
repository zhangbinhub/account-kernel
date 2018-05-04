package OLink.bpm.core.department.ejb;

import java.util.Collection;
import java.util.Map;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.base.ejb.IDesignTimeProcess;

public interface DepartmentProcess extends IDesignTimeProcess<DepartmentVO> {
	/**
	 * 根据标识移除记录
	 * 
	 * @param pk
	 *            主键标识
	 */
	void doRemove(String pk) throws Exception;

	/**
	 * 根据参数查询部门对象的集合
	 * 
	 * @param ParamsTable
	 * @see IDesignTimeProcess#doSimpleQuery(ParamsTable)
	 * @return 部门集合
	 */
	Collection<DepartmentVO> doSimpleQuery(ParamsTable params) throws Exception;

	/**
	 * 获取部门的下级部门
	 * 
	 * @param parent
	 *            部门标识
	 * @return 部门集合
	 * @throws Exception
	 */
	Collection<DepartmentVO> getDatasByParent(String parent) throws Exception;

	/**
	 * 该方法是通过参数(部门名字,应用标识,域标识) 返回部门标识
	 * 
	 * @param deptname
	 *            部门名字
	 * @param application
	 *            应用标识
	 * @param domain
	 *            域标识
	 * @return 以字符串类型返回部门的标识
	 * @throws Exception
	 */
	//public abstract String getIdByName(String deptname, String application, String domain) throws Exception;

	/**
	 * 根据部门的集合生成部门并以树(Tree)形式的集合.
	 * 
	 * @param cols
	 *            部门的集合,
	 * @param startNode
	 *            部门的开始节点
	 * @param excludeNodeId
	 *            不包括的节点
	 * @param deep
	 *            深入级别
	 * @return 部门以树(Tree)形式的集合(java.util.Map)
	 * @throws Exception
	 */
	Map<String, String> deepSearchDepartmentTree(Collection<DepartmentVO> cols, DepartmentVO startNode, String excludeNodeId, int deep)
			throws Exception;

	/**
	 * 根据部门标识获取部门对象后,生成部门以下的集合
	 * 
	 * @param depid
	 *            部门标识
	 * @return 部门集合
	 * @throws Exception
	 */
	Collection<DepartmentVO> getUnderDeptList(String depid) throws Exception;

	/**
	 * 根据部门标识获取部门对象后,深度生成部门以下的集合
	 * 
	 * @param deptid
	 *            部门标识
	 * @param maxDeep
	 *            最大深度部门级别
	 * @return 部门集合
	 * @throws Exception
	 */
	Collection<DepartmentVO> getUnderDeptList(String deptid, int maxDeep) throws Exception;

	/**
	 * 获取部门的上级,并以集合的形式返回
	 * 
	 * @param depid
	 *            部门标识
	 * @return 部门集合
	 * @throws Exception
	 */
	Collection<DepartmentVO> getSuperiorDeptList(String depid) throws Exception;

	/**
	 * 根据参数获取部门的集合
	 * 
	 * @param level
	 *            部门级别
	 * @param application
	 *            应用标识
	 * @param domain
	 *            域标识
	 * @return 部门集合
	 * @throws Exception
	 */
	Collection<DepartmentVO> getDepartmentByLevel(int level, String application, String domain) throws Exception;

	/**
	 * 获取最顶级部门对象
	 * 
	 * @param application
	 *            应用标识
	 * @return 部门对象
	 * @throws Exception
	 */
	DepartmentVO getRootDepartmentByApplication(String application, String domain) throws Exception;

	/**
	 * 根据部门名字获得部门对象
	 * 
	 * @param byName
	 *            部门名
	 * @param domain
	 *            域标识
	 * @return 部门集合
	 * @throws Exception
	 */
	Collection<DepartmentVO> getDepartmentByName(String byName, String domain) throws Exception;

	/**
	 * 根据部门代码获得部门对象
	 * 
	 * @param byCode
	 *            部门代码
	 * @param domain
	 *            域标识
	 * @return 部门集合
	 * @throws Exception
	 */
	Collection<DepartmentVO> getDepartmentByCode(String byCode, String domain) throws Exception;

	/**
	 * 查询出部门的个数
	 * 
	 * @param parent
	 *            部门标识
	 * @return 部门数量
	 * @throws Exception
	 */
	long getChildrenCount(String parent) throws Exception;

	/**
	 * 根据参数查询上级部门集合
	 * 
	 * @param deptid
	 *            部门标识,
	 * @return 上级部门集合
	 * @throws Exception
	 */
	Collection<String> getUpperDeptIds(String deptid) throws Exception;

	/**
	 * 根据参数查询部门集合
	 * 
	 * @param deptid
	 *            部门标识
	 * @param level
	 *            部门级别
	 * @return 部门集合
	 * @throws Exception
	 */
	Collection<String> getUpperDeptIds(String deptid, int level) throws Exception;

	/**
	 * 查询域下的所的部门
	 * 
	 * @param domain
	 *            域标识
	 * @return 部门集合
	 * @throws Exception
	 */
	Collection<DepartmentVO> queryByDomain(String domain) throws Exception;

	/**
	 * 获取当前部门的上级部门集合,不包括当前部门
	 * 
	 * @param depid
	 *            部门标识
	 * @return 部门集合
	 * @throws Exception
	 */
	Collection<DepartmentVO> getSuperiorDeptListExcludeCurrent(String depid) throws Exception;

	/**
	 * 根据部门标识获取部门对象后,深度生成部门以下的集合
	 * 
	 * @param deptid
	 *            部门标识
	 * @param maxDeep
	 *            最大深度部门级别
	 * @param isExcludeSelf
	 *            是否排除同级（自身）部门
	 * @return 部门集合
	 * @throws Exception
	 */
	Collection<DepartmentVO> getUnderDeptList(String id, int maxDeep, boolean isExcludeSelf) throws Exception;

	/**
	 * 根据用户标识获取用户所属的所有部门的集合
	 * 
	 * @param userId
	 *            用户标识
	 * @return 部门集合
	 * @throws Exception
	 */
	Collection<DepartmentVO> queryByUser(String userId) throws Exception;
	
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
	Collection<DepartmentVO> doQueryLineSubordinatesByIndexCode(String indexCode) throws Exception;
	
	/**
	 * 根据索引编号查找所有上级部门的集合
	 * @param indexCode
	 * @return
	 * @throws Exception
	 */
	Collection<DepartmentVO> doQueryAllSuperiorsByIndexCode(String indexCode) throws Exception;
	
	/**
	 * 根据索引编号查找所有下级部门的集合
	 * @param indexCode
	 * @return
	 * @throws Exception
	 */
	Collection<DepartmentVO> doQueryAllSubordinatesByIndexCode(String indexCode) throws Exception;

}
