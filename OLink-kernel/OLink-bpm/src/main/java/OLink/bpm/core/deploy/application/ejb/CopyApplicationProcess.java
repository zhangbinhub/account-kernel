package OLink.bpm.core.deploy.application.ejb;

import OLink.bpm.base.ejb.IDesignTimeProcess;

/**
 * @see OLink.bpm.core.deploy.application.ejb.CopyApplicationProcessBena
 * @since jdk1.5
 * @author eshow
 * 
 */
public interface CopyApplicationProcess extends IDesignTimeProcess<ApplicationVO> {

	/**
	 * copy roles
	 * 
	 * @param applicationid
	 * @throws Exception
	 */
	void copyRole(String applicationid) throws Exception;

	/**
	 * copy menu
	 * 
	 * @param applicationid
	 * @throws Exception
	 */
	void copyMenu(String applicationid) throws Exception;

	/**
	 * copy Macrolibs
	 * 
	 * @param applicationid
	 * @throws Exception
	 */
	void copyMacrolibs(String applicationid) throws Exception;

	/**
	 * copy style css
	 * 
	 * @param applicationid
	 * @throws Exception
	 */
	void copyStylelibs(String applicationid) throws Exception;

	/**
	 * copy validatelibs
	 * 
	 * @param applicationid
	 * @throws Exception
	 */
	void copyValidatelibs(String applicationid) throws Exception;

	/**
	 * copy excel的导入配置
	 * 
	 * @param applicationid
	 * @throws Exception
	 */
	void copyExcelConf(String applicationid) throws Exception;

	/**
	 * copy reminder 提醒
	 * 
	 * @param applicationid
	 * @throws Exception
	 */
	void copyReminder(String applicationid) throws Exception;

	/**
	 * copy page 页
	 * 
	 * @param applicationid
	 * @throws Exception
	 */
	void copyPage(String applicationid) throws Exception;

	/**
	 * copy component 组件
	 * 
	 * @param applicationid
	 * @throws Exception
	 */
	void copyComponent(String applicationid) throws Exception;

	/**
	 * copy homepage
	 * 
	 * @param applicationid
	 * @throws Exception
	 */
	void copyHomepage(String applicationid) throws Exception;

	/**
	 * copy application datasource
	 * 
	 * @param applicationid
	 * @throws Exception
	 */
	void copyDataSource(String applicationid) throws Exception;

	/**
	 * copy workflow
	 * 
	 * @param applicationid
	 * @throws Exception
	 */
	void copyStatelabel(String applicationid) throws Exception;

	/**
	 * copy module
	 * 
	 * @param applicationid
	 * @throws Exception
	 */
	void copyModule(String applicationid) throws Exception;

	/**
	 * copy All( page,module,statelable,datasource,style.....)
	 * 
	 * @param applicationid
	 * @throws Exception
	 */
	void copyAll(String applicationid) throws Exception;
}
