package OLink.bpm.core.privilege.res.dao;

import OLink.bpm.base.dao.HibernateBaseDAO;
import OLink.bpm.core.privilege.res.ejb.ResVO;

public class HibernateResDAO extends HibernateBaseDAO<ResVO> implements
		ResDAO {
	public HibernateResDAO(String voClassName) {
		super(voClassName);
	}

}
