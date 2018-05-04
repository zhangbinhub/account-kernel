package OLink.bpm.core.homepage.ejb;

import OLink.bpm.base.dao.DAOFactory;
import OLink.bpm.base.dao.IDesignTimeDAO;
import OLink.bpm.core.homepage.dao.HomePageDAO;
import OLink.bpm.base.ejb.AbstractDesignTimeProcessBean;

public class HomePageProcessBean extends AbstractDesignTimeProcessBean<HomePage> implements HomePageProcess {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8441522680088647665L;
//
	protected IDesignTimeDAO<HomePage> getDAO() throws Exception {
		return (HomePageDAO) DAOFactory.getDefaultDAO(HomePage.class.getName());
	}

}
