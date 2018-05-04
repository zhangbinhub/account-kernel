package OLink.bpm.core.deploy.module.ejb;

import java.util.Collection;
import java.util.Map;

import OLink.bpm.base.ejb.IDesignTimeProcess;

public interface ModuleProcess extends IDesignTimeProcess<ModuleVO> {
	/**
	 * 根据参数,传入的深度搜索模块集合,遍历集合并以树形方式显示
	 * 
	 * @param cols
	 *            模块集合
	 * @param applicationId
	 *            应用标识
	 * @param startNode
	 *            开始节点
	 * @param excludeNodeId
	 *            不包括的节点
	 * @param deep
	 *            深入级别
	 * @return 模块的集合(java.util.Map)
	 * @throws Exception
	 */
	Map<String, String> deepSearchModuleTree(Collection<ModuleVO> cols,
											 String applicationId, ModuleVO startNode, String excludeNodeId,
											 int deep) throws Exception;

	/**
	 * 根据参数,传入的模块集合,遍历集合并以树形方式显示
	 * 
	 * @param cols
	 *            模块集合
	 * @param applicationId
	 *            应用标识
	 * @param startNode
	 *            开始节点
	 * @param excludeNodeId
	 *            不包括的节点
	 * @return 模块的集合(java.util.Map)
	 * @throws Exception
	 */
	Map<String, String> deepSearchModuleTree(Collection<ModuleVO> cols,
											 String applicationId, ModuleVO startNode, String excludeNodeId)
			throws Exception;

	/**
	 * 
	 * @param applicationId
	 * @param moduleId
	 * @return
	 * @throws Exception
	 */
	String[] deepSearchModuleid(String applicationId, String moduleId)
			throws Exception;

	/**
	 * 根据参数,传入的模块集合,生成列表的形式显示
	 * 
	 * @param colls
	 *            模块集合
	 * @param startNode
	 *            开始节点
	 * @param excludeNodeId
	 *            不包括的节点
	 * @param deep
	 *            深入级别
	 * @return 模块的集合(java.util.Collection)
	 * @throws Exception
	 */
	Collection<ModuleVO> deepSearchModule(Collection<ModuleVO> colls,
										  ModuleVO startNode, String excludeNodeId, int deep)
			throws Exception;

	/**
	 * 查询应用下所有的模块
	 * 
	 * @param application
	 *            应用标识
	 * @return 模块的集合
	 * @throws Exception
	 */
	Collection<ModuleVO> getModuleByApplication(String application)
			throws Exception;

	/**
	 * 移除模块,根据相应的参数移除模块并移除模块下的所有子级(表单,视图,流程)
	 * 
	 * @param moduleid
	 *            模块标识
	 * @param application
	 *            应用标识
	 * @throws Exception
	 */
	void deleteModule(String moduleid, String application)
			throws Exception;

	/**
	 * 批处理移除模块,根据相应的参数移除模块并移除模块下的所有子级(表单,视图,流程)
	 * 
	 * @param moduleids
	 *            模块标识集
	 * @param application
	 *            应用标识
	 * @throws Exception
	 */
	void deleteModules(String[] moduleids, String application)
			throws Exception;

	/**
	 * 根据模块的名称查询模块
	 * 
	 * @param name
	 *            模块名
	 * @param application
	 *            应用标识
	 * @return 模块对象
	 * @throws Exception
	 */
	ModuleVO getModuleByName(String name, String application)
			throws Exception;

	/**
	 * 根据模块标识查询模块的下级所有的模块对象,
	 * 
	 * @param moduleId
	 *            模块标识
	 * @param maxDeep
	 *            最大深入级别
	 * @return 模块集合
	 * @throws Exception
	 */
	Collection<ModuleVO> getUnderModuleList(String moduleId, int maxDeep)
			throws Exception;

	Collection<ModuleVO> getUnderModuleList(
			Collection<ModuleVO> moduleList, String moduleId, int maxDeep)
			throws Exception;
}
