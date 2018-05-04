package OLink.bpm.core.dynaform.dts.exp.columnmapping.dao;

import java.util.Collection;

import OLink.bpm.core.dynaform.dts.exp.columnmapping.ejb.ColumnMapping;
import OLink.bpm.base.dao.IDesignTimeDAO;

public interface ColumnMappingDAO extends IDesignTimeDAO<ColumnMapping> {

	Collection<ColumnMapping> getColMapBytoName(String toName, String application) throws Exception;
}
