package OLink.bpm.core.helper.ejb;

import OLink.bpm.base.dao.DAOFactory;
import OLink.bpm.base.dao.IDesignTimeDAO;
import OLink.bpm.base.ejb.AbstractDesignTimeProcessBean;
import OLink.bpm.core.helper.dao.HelperDAO;

public class HelperProcessBean extends AbstractDesignTimeProcessBean<HelperVO> implements HelperProcess{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5697810186004525595L;

	protected IDesignTimeDAO<HelperVO> getDAO() throws Exception {
		return (HelperDAO) DAOFactory.getDefaultDAO(HelperVO.class.getName());
	}

	public HelperVO getHelperByName(String urlname, String application) throws Exception
	{
		return (((HelperDAO)getDAO())).getHelperByName(urlname, application);
	
	}
	
}
