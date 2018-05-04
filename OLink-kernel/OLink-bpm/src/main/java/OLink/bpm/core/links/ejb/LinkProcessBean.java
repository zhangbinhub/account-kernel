package OLink.bpm.core.links.ejb;

import OLink.bpm.base.dao.DAOFactory;
import OLink.bpm.base.dao.IDesignTimeDAO;
import OLink.bpm.core.links.dao.LinkDAO;
import OLink.bpm.base.ejb.AbstractDesignTimeProcessBean;

/**
 * @author Happy
 *
 */
public class LinkProcessBean extends AbstractDesignTimeProcessBean<LinkVO> implements
		LinkProcess {

	/**
	 * 
	 */
	private static final long serialVersionUID = 658247972879997267L;

	public LinkProcessBean() {
	}

	protected IDesignTimeDAO<LinkVO> getDAO() throws Exception {
		return (LinkDAO) DAOFactory.getDefaultDAO(LinkVO.class.getName());
	}

}
