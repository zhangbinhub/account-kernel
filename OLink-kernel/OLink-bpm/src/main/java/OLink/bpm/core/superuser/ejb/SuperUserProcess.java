package OLink.bpm.core.superuser.ejb;

import java.util.Collection;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.base.dao.ValueObject;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.base.ejb.IDesignTimeProcess;

/**
 * 
 * 管理员的接口类,在属管理员的用户的接口类
 */
public interface SuperUserProcess extends IDesignTimeProcess<SuperUserVO> {
	/**
	 * 创建管理员
	 */
	void doCreate(ValueObject vo) throws Exception;

	/**
	 * 移除管理员
	 */
	void doRemove(String pk) throws Exception;

	/**
	 * 更新管理员
	 */
	void doUpdate(ValueObject vo) throws Exception;

	/**
	 * 修改管理员用户密码
	 * 
	 * @param id
	 * @param oldPwd
	 * @param newPwd
	 * @throws Exception
	 */
	void changePwd(String id, String oldPwd, String newPwd) throws Exception;

	/**
	 * SuperUser 登录操作
	 * 
	 * @param loginno
	 *            管理员登录帐号名
	 * @param password
	 *            管理员登录密码
	 * @return SuperUserVO
	 */
	SuperUserVO login(String no, String password) throws Exception;

	/**
	 * 根据loginno获取管理员
	 * 
	 * @param no
	 *            管理员登录帐号名
	 * @return SuperUserVO
	 * @throws Exception
	 */

	SuperUserVO login(String no) throws Exception;

	/**
	 * 根据Domain返回数据
	 * 
	 * @param domain
	 *            Domain的唯一标识
	 */
	Collection<SuperUserVO> getDatasByDomain(String domain) throws Exception;

	/**
	 * 获取管理员的类型
	 * 
	 * @param userType
	 *            用户类型
	 * @return 所属类型的用户列表<java.util.Collection>
	 * @throws Exception
	 */
	Collection<SuperUserVO> getDatasByType(int userType) throws Exception;

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
	 * 返回所有的用户填写的Email地址
	 * 
	 * @return 用户的Email集合<java.util.Collection>
	 * @throws Exception
	 */
	Collection<SuperUserVO> doQueryHasMail() throws Exception;

	/**
	 * 管理员判断是否为空
	 * 
	 * @return boolean
	 * @throws Exception
	 */
	boolean isEmpty() throws Exception;

	/**
	 * 获得默认的管理员用户(admin为默认的不管理员)
	 * 
	 * @return SuperUserVO
	 * @throws Exception
	 */
	SuperUserVO getDefaultAdmin() throws Exception;

	/**
	 * 查询管理员
	 * 
	 * @param loginno
	 *            管理员名
	 * @return SuperUserVO
	 * @throws Exception
	 */
	SuperUserVO doViewByLoginno(String loginno) throws Exception;

	/**
	 * 根据应用查找未加入的开发者
	 * 
	 * @param params
	 *            查询参数
	 * @return 开发者数据包
	 * @throws Exception
	 */
	DataPackage<SuperUserVO> getUnjoinedDeveloperList(ParamsTable params) throws Exception;

	/**
	 * 根据参数条件以及应用标识,返回已加入的开发者的DataPackage
	 * 
	 * @param params
	 *            查询参数
	 * @return 已加入的开发者数据包<DataPackage>
	 * @throws Exception
	 */
	DataPackage<SuperUserVO> getJoinedDeveloperList(ParamsTable params) throws Exception;

	/**
	 * 根据参数条件以及应用标识,返回管理员的用户的DataPackage
	 * 
	 * @param params
	 * @return 管理员的用户<DataPackage>
	 * @throws Exception
	 */
	DataPackage<SuperUserVO> getUnJoinedAdminList(ParamsTable params) throws Exception;
}
