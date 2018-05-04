package OLink.bpm.core.dynaform.dts.excelimport.config.dao;

import java.util.Collection;

import OLink.bpm.base.dao.IDesignTimeDAO;
import OLink.bpm.core.dynaform.dts.excelimport.config.ejb.IMPMappingConfigVO;

public interface IMPMappingConfigDAO extends IDesignTimeDAO<IMPMappingConfigVO> {
	Collection<IMPMappingConfigVO> getAllMappingConfig(String application) throws Exception;

	Object getMappingExcel(String id) throws Exception;
}
