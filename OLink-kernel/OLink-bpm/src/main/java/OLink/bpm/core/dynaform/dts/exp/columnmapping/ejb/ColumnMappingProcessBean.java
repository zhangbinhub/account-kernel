package OLink.bpm.core.dynaform.dts.exp.columnmapping.ejb;

import java.util.Collection;

import OLink.bpm.base.dao.DAOFactory;
import OLink.bpm.base.dao.IDesignTimeDAO;
import OLink.bpm.base.ejb.AbstractDesignTimeProcessBean;
import OLink.bpm.core.dynaform.dts.exp.columnmapping.dao.ColumnMappingDAO;

public class ColumnMappingProcessBean extends AbstractDesignTimeProcessBean<ColumnMapping> implements ColumnMappingProcess{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -293753905158314015L;

	protected IDesignTimeDAO<ColumnMapping> getDAO() throws Exception {
		return (ColumnMappingDAO) DAOFactory.getDefaultDAO(ColumnMapping.class.getName());
	}

	public Collection<ColumnMapping> getColMapBytoName(String toName, String application) throws Exception {
	      
		return ((ColumnMappingDAO)getDAO()).getColMapBytoName(toName, application);
	}

}
