package OLink.bpm.core.user.ejb;

import java.util.Collection;
import java.util.Map;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.base.dao.ValueObject;
import OLink.bpm.base.ejb.IDesignTimeProcess;
import OLink.bpm.core.user.action.WebUser;

/**
 * 
 * 用户操作接口
 * 
 */
public interface UserProcess extends IDesignTimeProcess<UserVO> {
	/**
	 * 创建一个用户对象
	 * 
	 * @param ValueObject
	 */
	void doCreate(ValueObject vo) throws Exception;

	/**
	 * 移除一个用户对象
	 * 
	 * @param pk
	 */
	void doRemove(String pk) throws Exception;

	/**
	 * 更新一个用户对象
	 */
	void doUpdate(ValueObject vo) throws Exception;

	/**
	 * 修改用户密码
	 * 
	 * @param id
	 *            用户标识
	 * @param oldPwd
	 *            旧密码
	 * @param newPwd
	 *            新密码
	 * @throws Exception
	 */
	void changePwd(String id, String oldPwd, String newPwd)
			throws Exception;

	/**
	 * 用户登陆
	 * 
	 * @param no
	 *            用户名
	 * @param password
	 *            用户密码
	 * @param domain
	 *            域名
	 * @return 返回用户(UserVO)对象
	 * @throws Exception
	 */
	UserVO login(String no, String password, String domain)
			throws Exception;

	/**
	 * 用户登陆
	 * 
	 * @param no
	 *            用户名
	 * @param domain
	 *            域名
	 * @return 返回用户(UserVO)对象
	 * @throws Exception
	 */
	UserVO login(String no, String domain) throws Exception;

	/**
	 * 获得上级部门下的用户
	 * 
	 * @param parent
	 *            部门标识
	 * 
	 * @param domainId
	 *            域标识
	 * @return 用户(UserVO)对象集合
	 * @throws Exception
	 */
	Collection<UserVO> getDatasByDept(String parent,
									  String domainId) throws Exception;

	/**
	 * 获得上级部门下的用户
	 * 
	 * @param parent
	 *            部门标识
	 * @return 用户(UserVO)对象集合
	 * @throws Exception
	 */
	Collection<UserVO> getDatasByDept(String parent)
			throws Exception;

	/**
	 * 获得上级角色下的用户
	 * 
	 * @param parent
	 *            上级角色标识
	 * @param domainId
	 *            域标识
	 * @return 用户(UserVO)对象集合
	 * @throws Exception
	 */
	Collection<UserVO> getDatasByGroup(String parent,
									   String domainId) throws Exception;

	/**
	 * 针对某个用户更新
	 * 
	 * @param vo
	 *            用户对象
	 * @throws Exception
	 */
	void doPersonalUpdate(ValueObject vo) throws Exception;

	/**
	 * 初始化用户对象
	 * 
	 * @param userid
	 * @return
	 * @throws Exception
	 */
	WebUser getWebUserInstance(String userid) throws Exception;

	/**
	 * 创建一个用户对象
	 * 
	 * @param user
	 * @return
	 * @throws Exception
	 */
	UserVO createUser(UserVO user) throws Exception;

	/**
	 * 获取应用下用户的Email地址
	 * 
	 * @param application
	 *            应用标识
	 * @return Email地址集合
	 * @throws Exception
	 */
	Collection<UserVO> doQueryHasMail(String application)
			throws Exception;

	/**
	 * 是否为空
	 * 
	 * @return
	 * @throws Exception
	 */
	boolean isEmpty() throws Exception;

	/**
	 * 获得域下所有用户
	 * 
	 * @param domainid
	 *            域标识
	 * @param page
	 *            页码
	 * @param line
	 *            记录
	 * @return 用户(UserVO)集合
	 * @throws Exception
	 */
	Collection<UserVO> queryByDomain(String domainid, int page, int line)
			throws Exception;

	/**
	 * 获得某个指定角色下的所有用户
	 * 
	 * @param roleid
	 *            角色标识
	 * @return 用户数据集合
	 * @throws Exception
	 */
	DataPackage<UserVO> doQueryByRoleId(String roleid)
			throws Exception;

	/**
	 * 根据参数条件以及应用标识,返回用户的DataPackage .
	 * <p>
	 * DataPackage为一个封装类，此类封装了所得到的User数据并分页。
	 * 
	 * @see DataPackage#datas
	 * @see DataPackage#getPageCount()
	 * @see DataPackage#getLinesPerPage()
	 * @see DataPackage#getPageNo()
	 * 
	 * @param params
	 *            参数类
	 * @param user
	 *            用户
	 * @return
	 * @throws Exception
	 */
	DataPackage<UserVO> listUsers(ParamsTable params,
								  WebUser user) throws Exception;

	/**
	 * 登陆
	 * 
	 * @param loginno
	 *            登陆名
	 * @return
	 * @throws Exception
	 */
	UserVO login(String loginno) throws Exception;

	/**
	 * 获得默认值应用标识
	 * 
	 * @param userid
	 *            用户标识
	 * @return 应用标识
	 * @throws Exception
	 */
	String getDefaultApplicationId(String userid) throws Exception;

	/**
	 * 深度查询用户树
	 * 
	 * @param cols
	 *            所有用户列表
	 * @param startNode
	 *            开始节点
	 * @param excludeNodeId
	 *            排除节点
	 * @param deep
	 *            深度
	 * @return 用户树
	 * @throws Exception
	 */
	Map<String, String> deepSearchTree(Collection<?> cols,
									   UserVO startNode, String excludeNodeId, int deep) throws Exception;

	/**
	 * 根据参数条件以及应用标识,返回用户的DataPackage .
	 * <p>
	 * DataPackage为一个封装类，此类封装了所得到的User数据并分页。
	 * 
	 * @see DataPackage#datas
	 * @see DataPackage#getPageCount()
	 * @see DataPackage#getLinesPerPage()
	 * @see DataPackage#getPageNo()
	 * 
	 * @param params
	 *            参数类
	 * @param user
	 *            用户
	 * @return 用户的DataPackage
	 * @throws Exception
	 */
	DataPackage<UserVO> queryUsersExcept(ParamsTable params, WebUser user)
			throws Exception;

	/**
	 * 获得用户(UserVO)对象
	 * 
	 * @param loginno
	 *            登陆名
	 * @param domainid
	 *            域标识
	 * @return 用户(UserVO)对象
	 * @throws Exception
	 */
	UserVO getUserByLoginno(String loginno, String domainid)
			throws Exception;

	/**
	 * 获得联系电话不为空的数据集
	 * 
	 * @param params
	 * @return
	 * @throws Exception
	 */
	DataPackage<UserVO> listLinkmen(ParamsTable params) throws Exception;

	/**
	 * 获得用户(UserVO)的标识, 多条时,默认用(;)分开
	 * 
	 * @param username
	 *            用户名
	 * @param domainid
	 *            域标识
	 * @return 用户(UserVO)的标识,多条时,默认用(;)分开
	 * @throws Exception
	 */
	String queryUserIdsByName(String username, String domainid)
			throws Exception;

	/**
	 * 根据帐号获取用户标识
	 * 
	 * @param account
	 *            用户帐号
	 * @param domainid
	 *            域标识
	 * @return 用户标识
	 * @throws Exception
	 */
	String findUserIdByAccount(String account, String domainid)
			throws Exception;

	/**
	 * 查询代理用户集合
	 * 
	 * @param proxyid
	 *            用户标识
	 * @return
	 * @throws Exception
	 */
	Collection<UserVO> queryByProxyUserId(String proxyid)
			throws Exception;

	/**
	 * 不清空缓存的更新方法
	 * 
	 * @param vo
	 * @throws Exception
	 */
	void doUpdateWithCache(ValueObject vo) throws Exception;

	/**
	 * 更新用户默认选择的应用
	 * 
	 * @param userid
	 *            用户标识
	 * @param defaultApplicationid
	 *            用户标识
	 * @throws Exception
	 */
	void doUpdateDefaultApplication(String userid,
									String defaultApplicationid) throws Exception;

	/**
	 * 获取用户对象的下级对象
	 * 
	 * @param userId
	 *            用户标识
	 * @return 下级用户对象
	 * @throws Exception
	 */
	Collection<UserVO> getUnderList(String userId) throws Exception;

	/**
	 * 获取用户对象的下级对象
	 * 
	 * @param userId
	 *            用户标识
	 * @param maxDeep
	 *            最大深度值
	 * @return 下级用户对象
	 * @throws Exception
	 */
	Collection<UserVO> getUnderList(String userId, int maxDeep)
			throws Exception;

	/**
	 * 获取用户上级列表
	 * 
	 * @param userId
	 *            用户id
	 * @param maxDeep
	 *            查询深度
	 * @return 上级用户对象集
	 * @throws Exception
	 */
	Collection<UserVO> getSuperiorList(String userId)
			throws Exception;

	/**
	 * 获取指定部门所有用户
	 * 
	 * @param dptid
	 *            部门ID
	 * @return 返回指定部门下的所有用户对象的集合
	 */
	Collection<UserVO> queryByDepartment(String deptId) throws Exception;

	/**
	 * 获取指定部门之外的所有用户
	 * 
	 * @param params
	 *            参数表
	 * @param dptid
	 *            部门ID
	 * @return 返回指定部门下的所有用户对象的集合
	 */
	DataPackage<UserVO> queryOutOfDepartment(ParamsTable params,
											 String deptid) throws Exception;

	/**
	 * 为指定部门添加用户
	 * 
	 * @param userids
	 *            数组：用户要添加的用户集合
	 * @param deptid
	 *            部门id
	 */
	void addUserToDept(String[] userids, String deptid) throws Exception;

	/**
	 * 获取指定角色之外的所有用户
	 * 
	 * @param params
	 *            参数表
	 * @param roleid
	 *            角色ID
	 * @return 返回指定部门下的所有用户对象的集合
	 */
	DataPackage<UserVO> queryOutOfRole(ParamsTable params, String roleid)
			throws Exception;

	/**
	 * 为指定角色添加用户
	 * 
	 * @param userids
	 *            数组：用户要添加的用户集合
	 * @param roleid
	 *            角色id
	 */
	void addUserToRole(String[] userids, String roleid) throws Exception;

	/**
	 * /** 获取指定角色下的所有用户
	 * 
	 * @param roleid
	 *            角色ID
	 * @return 返回指定角色下的所有用户对象的集合
	 */
	Collection<UserVO> queryByRole(String roleId) throws Exception;

	/**
	 * 获取指定部门并角色的所有用户
	 * 
	 * @param dptid
	 *            部门ID
	 * @param roleid
	 *            角色ID
	 * @return 返回指定部门并角色的所有用户对象的集合
	 */
	Collection<UserVO> queryByDptIdAndRoleId(String deptId, String roleId)
			throws Exception;

	/**
	 * 获取当前域下面的所有用户
	 * 
	 * @return 返回当前域下面的所有用户对象的集合
	 */
	Collection<UserVO> queryByDomain(String domainid) throws Exception;

	UserVO getUserByLoginnoAndDoaminName(final String loginno,
										 final String domainName) throws Exception;
	
	Collection<UserVO> doQueryByHQL(String hql) throws Exception;
}
