package OLink.bpm.core.user.dao;

import java.util.Collection;
import java.util.List;

import OLink.bpm.base.dao.HibernateBaseDAO;
import OLink.bpm.core.user.ejb.UserDefined;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Expression;

public class HibernateUserDefinedDAO extends HibernateBaseDAO<UserDefined> implements UserDefinedDAO{
	public HibernateUserDefinedDAO(String voClassName) {
		super(voClassName);
	}

	public Collection<UserDefined> findByApplication(String applicationId) throws Exception {
		String hql = "from " + this._voClazzName + " vo where vo.applicationid = '" + applicationId + "'";
		return getDatas(hql);
	}

	public int queryCountByName(String name, String applicationid)
			throws Exception {
		String hql = "FROM " + _voClazzName + " WHERE name='" + name + "' and applicationid='" + applicationid + "'";
		return getTotalLines(hql);
	}

	
	@SuppressWarnings("unchecked")
	public UserDefined login(String name) throws Exception {
	
		Session session = currentSession();
		Criteria criteria = session.createCriteria(this._voClazzName);
		criteria = criteria.add(Expression.like("name", name).ignoreCase());
		List<UserDefined> list = criteria.list();
		if (list.isEmpty()) {
			return null;
		} else {
			return list.get(0);
		}
	}
}
