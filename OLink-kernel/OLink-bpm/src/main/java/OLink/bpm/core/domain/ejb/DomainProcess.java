package OLink.bpm.core.domain.ejb;

import java.util.Collection;

import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.base.ejb.IDesignTimeProcess;

/**
 * 
 * 域管理, 通过extends(IDesignTimeProcess) 实现,域对应用同时进行了管理.
 */
public interface DomainProcess extends IDesignTimeProcess<DomainVO> {

	/**
	 * 获取域对象
	 * 
	 * @param 域名
	 * @return 域对象(#DomainVO)
	 * @throws Exception
	 */
	DomainVO getDomainByName(String tempname) throws Exception;
	
	/**
	 * 获取域对象
	 * 
	 * @param 域名
	 * @return 域对象(#DomainVO)
	 * @throws Exception
	 */
	DomainVO getDomainByDomainName(String name) throws Exception;

	/**
	 * 获取域对象的集合
	 * 
	 * @param userid
	 *            用户标识
	 * @param page
	 *            页码
	 * @param line
	 *            记录数
	 * @return 域对象集合(java.utll.Collection)
	 * @throws Exception
	 */
	Collection<DomainVO> queryDomains(String userId, int page, int line) throws Exception;

	/**
	 * 根据参数条件以及应用标识,返回表单的DataPackage .
	 * <p>
	 * DataPackage为一个封装类，此类封装了所得到的Domain数据并分页。
	 * 
	 * @see DataPackage#datas
	 * @see DataPackage#getPageCount()
	 * @see DataPackage#getLinesPerPage()
	 * @see DataPackage#getPageNo()
	 * @param manager
	 *            管理员名
	 * @param page
	 *            页码
	 * @param line
	 *            记录数
	 * @return 域对象的数据集合
	 * @throws Exception
	 */
	DataPackage<DomainVO> queryDomainsByManager(String managerName, int page, int line) throws Exception;

	/**
	 * 根据参数条件以及应用标识,返回表单的DataPackage .
	 * <p>
	 * DataPackage为一个封装类，此类封装了所得到的Domain数据并分页。
	 * 
	 * @see DataPackage#datas
	 * @see DataPackage#getPageCount()
	 * @see DataPackage#getLinesPerPage()
	 * @see DataPackage#getPageNo()
	 * @param name
	 *            域名
	 * @param page
	 *            页码
	 * @param line
	 *            记录数
	 * @return 域对象的数据集合
	 * @throws Exception
	 */
	DataPackage<DomainVO> queryDomainsByName(String name, int page, int line) throws Exception;

	/**
	 * 根据参数条件以及应用标识,返回表单的DataPackage .
	 * <p>
	 * DataPackage为一个封装类，此类封装了所得到的Domain数据并分页。
	 * 
	 * @see DataPackage#datas
	 * @see DataPackage#getPageCount()
	 * @see DataPackage#getLinesPerPage()
	 * @see DataPackage#getPageNo()
	 * @param manager
	 *            管理员名
	 * @param name
	 *            域名
	 * @param page
	 *            页码
	 * @param line
	 *            记录数
	 * @return 域对象的数据集合
	 * @throws Exception
	 */
	DataPackage<DomainVO> queryDomainsByManagerAndName(String managerName, String name, int page, int line)
			throws Exception;

	/**
	 * 获取所有域对象的集合
	 * 
	 * @return 域对象的集合(java.utll.Collection)
	 * @throws Exception
	 */
	Collection<DomainVO> getAllDomain() throws Exception;

	/**
	 * 根据管理员登录名和域名称查询域
	 * 
	 * @param managerLoginno
	 *            -管理员登录名
	 * @param name
	 *            -域名称
	 * @param page
	 *            -页
	 * @param line
	 *            -行
	 * @return
	 * @throws Exception
	 */
	DataPackage<DomainVO> queryDomainsbyManagerLoginnoAndName(String manager, String name, int page, int line)
			throws Exception;

}
