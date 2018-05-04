package OLink.bpm.core.macro.repository.dao;

import OLink.bpm.core.macro.repository.ejb.RepositoryVO;

public interface RepositoryDAO {

	RepositoryVO getRepositoryByName(String name, String application) throws Exception;
	
	/**
	 * 根据名称，判断在该应用中是否已经存在相同名称的库
	 * @param id 
	 * @param name
	 * @param application
	 * @return 存在返回true,否则返回false
	 * @throws Exception
	 */
	boolean isMacroNameExist(String id, String name, String application)throws Exception;
}
