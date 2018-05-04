package OLink.bpm.core.dynaform.dts.exp.mappingconfig.action;

import java.util.Collection;

import OLink.bpm.base.action.BaseHelper;
import OLink.bpm.core.dynaform.dts.exp.mappingconfig.ejb.MappingConfigProcess;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.core.dynaform.dts.exp.mappingconfig.ejb.MappingConfig;

public class MappingConfigHelper extends BaseHelper<MappingConfig> {
	
	/**
	 * @SuppressWarnings 工厂方法不支持泛型
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings("unchecked")
	public MappingConfigHelper() throws ClassNotFoundException {
		super(ProcessFactory.createProcess(MappingConfigProcess.class));
	}

	public Collection<?> get_allMappingConifgs(String application)
			throws Exception {
		MappingConfigProcess dp = (MappingConfigProcess) ProcessFactory
				.createProcess((MappingConfigProcess.class));
		Collection<?> dc = dp.doSimpleQuery(null, application);
		return dc;
	}
}
