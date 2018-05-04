package OLink.bpm.core.deploy.module.dao;

import java.util.Collection;

import OLink.bpm.base.dao.IDesignTimeDAO;
import OLink.bpm.core.deploy.module.ejb.ModuleVO;

public interface ModuleDAO extends IDesignTimeDAO<ModuleVO> {
	Collection<ModuleVO> getModuleByApplication(String application)
			throws Exception;

	ModuleVO findByName(String name, String application) throws Exception;
}
