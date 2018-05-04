package OLink.bpm.core.deploy.application.dao;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import OLink.bpm.base.dao.HibernateBaseDAO;
import OLink.bpm.base.dao.ValueObject;
import OLink.bpm.core.deploy.application.ejb.ApplicationVO;
import org.hibernate.Query;
import org.hibernate.Session;

/**
 * @author nicholas
 */
public class HibernateApplicationDAO extends HibernateBaseDAO<ApplicationVO> implements ApplicationDAO {

	/**
	 * @uml.property name="appDomain_Cache"
	 */
	private static Map<String, ApplicationVO> appDomain_Cache = null;

	public HibernateApplicationDAO(String voClassName) {
		super(voClassName);
	}

	public ApplicationVO getApplicationByDomainName(String domainName) throws Exception {
		String hql = "FROM " + _voClazzName + " vo WHERE vo.domainName = '" + domainName + "'";
		return (ApplicationVO) getData(hql);
	}

	public Collection<ApplicationVO> getAllApplication() throws Exception {
		String hql = "FROM " + _voClazzName;
		return getDatas(hql);
	}

	public void create(ValueObject vo) throws Exception {
		try {
			Session session = currentSession();
			session.save(vo);
			refreshCache();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void remove(String id) throws Exception {
		Session session = currentSession();
		ValueObject vo = find(id);
		if (vo != null)
			session.delete(vo);
		refreshCache();
	}

	public void update(ValueObject vo) throws Exception {
		Session session = currentSession();
		session.merge(vo);
		refreshCache();
	}

	public void refreshCache() throws Exception {
		if (appDomain_Cache != null)
			appDomain_Cache.clear();
		else
			appDomain_Cache = new HashMap<String, ApplicationVO>();
		Collection<ApplicationVO> rs = getAllApplication();
		if (rs != null) {
			for (Iterator<ApplicationVO> iter = rs.iterator(); iter.hasNext();) {
				ApplicationVO vo = iter.next();
				if (vo.getDomainName() != null && vo.getDomainName().trim().length() > 0 && vo.getWelcomePage() != null
						&& vo.getWelcomePage().trim().length() > 0)
					appDomain_Cache.put(vo.getDomainName(), vo);
			}
		}

	}

	/**
	 * @return the appDomain_Cache
	 * @uml.property name="appDomain_Cache"
	 */
	public Map<String, ApplicationVO> getAppDomain_Cache() throws Exception {
		if (appDomain_Cache == null)
			refreshCache();
		return appDomain_Cache;
	}

	/**
	 * @SuppressWarnings Hibernate3.2版本不支持泛型
	 */
	@SuppressWarnings("unchecked")
	public boolean isEmpty() throws Exception {
		String hql = "select count(*) from " + _voClazzName;
		Session session = currentSession();
		Query query = session.createQuery(hql);

		List rst = query.list();

		Long amount;
		if (!rst.isEmpty()) {
			amount = (Long) rst.get(0);
			return amount.intValue() <= 0;
		} else
			return true;

	}

	/**
	 * @SuppressWarnings Hibernate3.2版本不支持泛型
	 */
	@SuppressWarnings("unchecked")
	public Collection<ApplicationVO> queryApplications(String userid, int page, int line) throws Exception {
		String hql = "FROM " + _voClazzName + " vo";
		if (userid != null && userid.trim().length() > 0) {
			hql += " WHERE vo.owners.id='" + userid + "'";
		}
		Query query = currentSession().createQuery(hql);
		query.setFirstResult((page - 1) * line);
		query.setMaxResults(line);
		return query.list();
	}

	/**
	 * @SuppressWarnings Hibernate3.2版本不支持泛型
	 */
	@SuppressWarnings("unchecked")
	public Collection<ApplicationVO> queryAppsByDomain(String domainId, int page, int line) throws Exception {
		String hql = "FROM " + _voClazzName + " vo";
		if (domainId != null && domainId.trim().length() > 0) {
			hql += " WHERE vo.domains.id='" + domainId + "' AND vo.activated = true";
		}
		Query query = currentSession().createQuery(hql);
		query.setFirstResult((page - 1) * line);
		query.setMaxResults(line);
		return query.list();
	}

	public ApplicationVO findBySIPAppKey(String appKey) throws Exception {
		String hql = "FROM " + _voClazzName + " vo WHERE vo.sipAppkey = '" + appKey + "'";
		return (ApplicationVO) getData(hql);
	}

	public ApplicationVO findByName(String name) throws Exception {
		String hql = "FROM " + _voClazzName + " vo WHERE vo.name = '" + name + "'";
		return (ApplicationVO) getData(hql);
	}

}
