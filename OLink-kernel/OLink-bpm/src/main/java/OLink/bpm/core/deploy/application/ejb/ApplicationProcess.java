package OLink.bpm.core.deploy.application.ejb;

import java.util.Collection;
import java.util.Map;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.base.ejb.IDesignTimeProcess;
import OLink.bpm.core.user.action.WebUser;

public interface ApplicationProcess extends IDesignTimeProcess<ApplicationVO> {
	/**
	 * 根据域名查询应用
	 * 
	 * @param domainName
	 *            域名
	 * @return 应用对象
	 * @throws Exception
	 */
	ApplicationVO getApplicationByDomainName(String domainName) throws Exception;

	/**
	 * 获取域下的应用的集合保存(key: 企业域名称 value:应用)
	 * 
	 * @return 应用的集合
	 * @throws Exception
	 */
	Map<String, ApplicationVO> getAppDomain_Cache() throws Exception;

	/**
	 * 判断是否为空
	 * 
	 * @return
	 * @throws Exception
	 */
	boolean isEmpty() throws Exception;

	/**
	 * 根据管理员用户获取应用集合,并进行分页
	 * 
	 * @param userid
	 *            管理员用户
	 * @param page
	 *            页
	 * @param line
	 *            显示记录条数
	 * @return 应用集合<java.util.Collection>
	 * @throws Exception
	 */
	Collection<ApplicationVO> queryApplications(String userid, int page, int line) throws Exception;

	/**
	 * 根据域获取域下的所有用户,并进行分页
	 * 
	 * @param domainId
	 *            域标识
	 * @param page
	 *            页码
	 * @param line
	 *            显示记录
	 * @return 应用集合<java.util.Collection>
	 * @throws Exception
	 */
	Collection<ApplicationVO> queryAppsByDomain(String domainId, int page, int line) throws Exception;

	/**
	 * 根据管理员用户获取应用
	 * 
	 * @param userid
	 *            管理员用户标识
	 * @return 应用集合<java.util.Collection>
	 * @throws Exception
	 */
	Collection<ApplicationVO> queryApplications(String userid) throws Exception;

	/**
	 * 根据域标识获取所属域下的所有应用
	 * 
	 * @param domainId
	 *            域标识
	 * @return 应用集合<java.util.Collection>
	 * @throws Exception
	 */
	Collection<ApplicationVO> queryByDomain(String domainId) throws Exception;

	/**
	 * 根据阿里软件标识查询应用
	 * 
	 * @param appKey
	 *            阿里软件ID
	 * @return 应用对象
	 * @throws Exception
	 */
	ApplicationVO findBySIPAppKey(String appKey) throws Exception;

	/**
	 * 根据应用名获取应用
	 * 
	 * @param name
	 *            应用名
	 * @return 应用对象
	 * @throws Exception
	 */
	ApplicationVO doViewByName(String name) throws Exception;

	/**
	 * 添加开发者到应用
	 * 
	 * @param developerIds
	 *            所属开发者的标识
	 * @param id
	 *            应用标识
	 * @throws Exception
	 */
	void addDevelopersToApplication(String[] developerIds, String id) throws Exception;

	/**
	 * 从应用中移除开发者
	 * 
	 * @param developerIds
	 *            开发用户
	 * @param id
	 *            应用标识
	 * @throws Exception
	 */
	void removeDevelopersFromApplication(String[] developerIds, String id) throws Exception;

	/**
	 * 根据域开发者用户获取应用
	 * 
	 * @param developerId
	 *            开发者用户
	 * @return 应用集合
	 * @throws Exception
	 */
	Collection<ApplicationVO> getApplicationsByDeveloper(String developerId) throws Exception;

	/**
	 * 根据域管理员用户获取应用
	 * 
	 * @param domainAdminId
	 *            域管理员用户
	 * @return 应用集合
	 * @throws Exception
	 */
	Collection<ApplicationVO> getApplicationsByDoaminAdmin(String domainAdminId) throws Exception;

	/**
	 * 获取没有添加到域管理的应用
	 * 
	 * @param params
	 *            参数
	 * @return 应用的数据集
	 * @throws Exception
	 */
	DataPackage<ApplicationVO> getUnjoinApplication(ParamsTable params) throws Exception;

	/**
	 * 获取默认应用
	 * 
	 * @param defaultApplicationid
	 *            默认应用ID
	 * @param domainid
	 *            企业域ID
	 * @return
	 * @throws Exception
	 */
	ApplicationVO getDefaultApplication(String defaultApplicationid, WebUser user) throws Exception;
}
