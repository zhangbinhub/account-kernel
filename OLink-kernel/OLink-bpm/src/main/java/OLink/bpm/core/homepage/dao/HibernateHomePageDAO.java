package OLink.bpm.core.homepage.dao;


import OLink.bpm.base.dao.HibernateBaseDAO;
import OLink.bpm.core.homepage.ejb.HomePage;

public class HibernateHomePageDAO extends HibernateBaseDAO<HomePage> implements HomePageDAO {

	public HibernateHomePageDAO(String voClassName) {
		super(voClassName);
	}
//
//	public Collection<HomePage> findByApplication(String applicationId) throws Exception {
//		String hql = "from " + this._voClazzName + " vo where vo.applicationid = '" + applicationId + "'";
//		return getDatas(hql);
//	}
//
//	public int queryCountByName(String name, String applicationid)
//			throws Exception {
//		String hql = "FROM " + _voClazzName + " WHERE name='" + name + "' and applicationid='" + applicationid + "'";
//		return getTotalLines(hql);
//	}

}
