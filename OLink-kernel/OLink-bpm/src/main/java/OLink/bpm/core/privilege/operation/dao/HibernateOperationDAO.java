package OLink.bpm.core.privilege.operation.dao;

import java.util.List;

import OLink.bpm.base.dao.HibernateBaseDAO;
import org.hibernate.Query;
import org.hibernate.Session;

import OLink.bpm.core.privilege.operation.ejb.OperationVO;

public class HibernateOperationDAO extends HibernateBaseDAO<OperationVO> implements OperationDAO {
	public HibernateOperationDAO(String voClassName) {
		super(voClassName);
	}

	public boolean isEmpty(String applicationId) throws Exception {
		String hql = "FROM " + _voClazzName + " vo WHERE vo.applicationid='" + applicationId + "'";
		Session session = currentSession();
		Query query = session.createQuery(hql);
		List<?> rst = query.list();
		return rst.isEmpty();
	}

	public int getTotal() throws Exception {
		String hql = "FROM " + _voClazzName;
		return getTotalLines(hql);
	}
}
