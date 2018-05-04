package OLink.bpm.core.links.dao;

import OLink.bpm.base.dao.HibernateBaseDAO;
import OLink.bpm.core.links.ejb.LinkVO;

/**
 * @author Happy
 *
 */
public class HibernateLinkDAO extends HibernateBaseDAO<LinkVO> implements LinkDAO {

	public HibernateLinkDAO(String valueObjectName) {
		super(valueObjectName);
	}

	public HibernateLinkDAO() {
	}

}
