package OLink.bpm.core.dynaform.dts.exp.mappingconfig.ejb;

import java.util.Collection;

import OLink.bpm.base.ejb.IDesignTimeProcess;

public interface MappingConfigProcess extends IDesignTimeProcess<MappingConfig> {
	
Collection<MappingConfig> getMCByTableName(String tableName, String application) throws Exception;

}
