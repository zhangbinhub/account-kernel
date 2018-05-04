package OLink.bpm.core.domain.level.dao;

import java.util.Collection;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.base.dao.HibernateBaseDAO;
import OLink.bpm.core.domain.level.ejb.DomainLevelVO;
import org.hibernate.Query;

public class HibernateDomainLevelDAO extends HibernateBaseDAO<DomainLevelVO> implements DomainLevelDAO {

	public HibernateDomainLevelDAO(String voClassName) {
		super(voClassName);
	}

	public String getIdByName(String tempname, String application)
			throws Exception {
		String hql = "SELECT vo.id FROM " + _voClazzName
				+ " vo WHERE vo.name = '" + tempname + "'";

		Query query = currentSession().createQuery(hql);
		if (!query.list().isEmpty()) {
			return (String) query.list().get(0);
		} else
			return null;
	}
	
	public DomainLevelVO getRateByName(String tempname) throws Exception {
		String hql = "FROM " + this._voClazzName + " vo where vo.name= '"
				+ tempname + "'";
		ParamsTable params = new ParamsTable();
		Collection<DomainLevelVO> cols = getDatas(hql, params);
		if (cols != null && !cols.isEmpty())
			return (DomainLevelVO) cols.toArray()[0];
		return null;
	}

}
