package OLink.bpm.core.dynaform.dts.excelimport.config.ejb;

import java.util.Collection;

import OLink.bpm.base.dao.DAOFactory;
import OLink.bpm.base.dao.IDesignTimeDAO;
import OLink.bpm.core.dynaform.dts.excelimport.config.dao.IMPMappingConfigDAO;
import OLink.bpm.base.ejb.AbstractDesignTimeProcessBean;

public class IMPMappingConfigProcessBean extends AbstractDesignTimeProcessBean<IMPMappingConfigVO> implements
		IMPMappingConfigProcess {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -792302886598953202L;

	protected IDesignTimeDAO<IMPMappingConfigVO> getDAO() throws Exception {
		return (IMPMappingConfigDAO) DAOFactory.getDefaultDAO(IMPMappingConfigVO.class.getName());
	}

	public Collection<IMPMappingConfigVO> getAllMappingConfig(String application) throws Exception {
		return ((IMPMappingConfigDAO) getDAO()).getAllMappingConfig(application);
	}

	public Object getMappingExcel(String id) throws Exception {
		return ((IMPMappingConfigDAO) getDAO()).getMappingExcel(id);
	}

}
