package OLink.bpm.core.usersetup.dao;


import java.util.List;

import OLink.bpm.base.dao.HibernateBaseDAO;
import OLink.bpm.base.dao.ValueObject;
import OLink.bpm.core.usersetup.ejb.UserSetupVO;
import org.hibernate.Query;
import org.hibernate.Session;

public class HibernateUserSetupDAO extends HibernateBaseDAO<UserSetupVO> implements UserSetupDAO {
	public HibernateUserSetupDAO(String voClassName) {
		super(voClassName);
	}
	
	@SuppressWarnings("unchecked")
	public ValueObject find(String id) throws Exception {
		Session session = currentSession();
		ValueObject rtn = null;
		if (id != null && id.length() > 0) {
			String hql = "FROM " + _voClazzName + " WHERE id='" + id + "'";
			Query query = session.createQuery(hql);
			query.setFirstResult(0);
			query.setMaxResults(1);
			List result = query.list();

			if (!result.isEmpty()) {
				rtn = (ValueObject) result.get(0);
			}
		}
		return rtn;
	}
}
