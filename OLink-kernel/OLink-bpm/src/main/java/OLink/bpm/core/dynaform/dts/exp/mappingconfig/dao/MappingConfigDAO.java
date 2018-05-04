package OLink.bpm.core.dynaform.dts.exp.mappingconfig.dao;

import java.util.Collection;

import OLink.bpm.base.dao.IDesignTimeDAO;
import OLink.bpm.core.dynaform.dts.exp.mappingconfig.ejb.MappingConfig;

public interface  MappingConfigDAO extends IDesignTimeDAO<MappingConfig> {

	Collection<MappingConfig> getMCByTableName(String tableName, String application) throws Exception;
}
