package OLink.bpm.core.dynaform.dts.exp.columnmapping.ejb;

import java.util.Collection;

import OLink.bpm.base.ejb.IDesignTimeProcess;

public interface ColumnMappingProcess extends IDesignTimeProcess<ColumnMapping> {
	
	Collection<ColumnMapping> getColMapBytoName(String toName, String application) throws Exception;

}
