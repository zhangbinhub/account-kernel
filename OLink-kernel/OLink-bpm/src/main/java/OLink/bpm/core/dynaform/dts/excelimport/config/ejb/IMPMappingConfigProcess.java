package OLink.bpm.core.dynaform.dts.excelimport.config.ejb;

import java.util.Collection;

import OLink.bpm.base.ejb.IDesignTimeProcess;

public interface IMPMappingConfigProcess extends IDesignTimeProcess<IMPMappingConfigVO> {
	Collection<IMPMappingConfigVO> getAllMappingConfig(String application) throws Exception;

	Object getMappingExcel(String id) throws Exception;

}
