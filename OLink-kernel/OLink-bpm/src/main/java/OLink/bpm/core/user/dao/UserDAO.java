package OLink.bpm.core.user.dao;

import java.util.Collection;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.base.dao.IDesignTimeDAO;
import OLink.bpm.core.user.ejb.UserVO;

public interface UserDAO extends IDesignTimeDAO<UserVO> {

	UserVO login(String loginno, String domain)
			throws Exception;

	Collection<UserVO> getDatasByRoleid(String parent,
										String domain) throws Exception;

	Collection<UserVO> getDatasByDept(String parent,
									  String domain) throws Exception;

	Collection<UserVO> queryHasMail(String application) throws Exception;

	boolean isEmpty() throws Exception;

	Collection<UserVO> queryHasMail(String application, String domain)
			throws Exception;

	DataPackage<UserVO> queryByRoleId(String roleid)
			throws Exception;

	Collection<UserVO> queryByDomain(String domainid, int page, int line)
			throws Exception;

	UserVO login(String no) throws Exception;

	Collection<UserVO> getDatasByDept(String parent) throws Exception;

	UserVO findByLoginno(String loginno, String domainid)
			throws Exception;

	DataPackage<UserVO> listLinkmen(ParamsTable params)
			throws Exception;

	Collection<UserVO> queryUsersByName(String username,
										String domainid) throws Exception;

	Collection<UserVO> queryByProxyUserId(String proxyid)
			throws Exception;

	/**
	 * 更新用户默认应用
	 * 
	 * @param userid
	 *            用户ID
	 * @param defaultApplicationid
	 *            默认选择应用ID
	 * @throws Exception
	 */
	void updateDefaultApplication(String userid,
								  String defaultApplicationid) throws Exception;
	
	Collection<UserVO> queryByHQL(String hql) throws Exception;
}
