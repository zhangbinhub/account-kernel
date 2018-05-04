package OLink.bpm.core.dynaform.dts.excelimport.config.action;

import java.util.Collection;

import OLink.bpm.core.dynaform.dts.excelimport.config.ejb.IMPMappingConfigProcess;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.base.action.BaseHelper;
import OLink.bpm.core.dynaform.dts.excelimport.config.ejb.IMPMappingConfigVO;

public class ImpHelper extends BaseHelper<IMPMappingConfigVO> {
	
	/**
	 * @SuppressWarnings 工厂方法不支持泛型
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings("unchecked")
	public ImpHelper() throws ClassNotFoundException {
		super(ProcessFactory.createProcess(IMPMappingConfigProcess.class));
	}

	public Collection<IMPMappingConfigVO> get_allMappingConfig(String application) throws Exception {

		IMPMappingConfigProcess proxy = (IMPMappingConfigProcess) ProcessFactory
				.createProcess((IMPMappingConfigProcess.class));
		return proxy.getAllMappingConfig(application);
	}
	
	public static Object get_MappingExcel(String id) throws Exception {
		IMPMappingConfigProcess proxy = (IMPMappingConfigProcess) ProcessFactory
				.createProcess(IMPMappingConfigProcess.class);
		return proxy.getMappingExcel(id);
	}


}
