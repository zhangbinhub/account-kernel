package OLink.bpm.core.domain.dao;

import java.util.Collection;

import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.base.dao.HibernateBaseDAO;
import OLink.bpm.core.domain.ejb.DomainVO;
import OLink.bpm.util.StringUtil;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Expression;

public class HibernateDomainDAO extends HibernateBaseDAO<DomainVO> implements DomainDAO {

	public HibernateDomainDAO(String voClassName) {
		super(voClassName);
	}

	public String getIdByName(String tempname, String application) throws Exception {
		String hql = "SELECT vo.id FROM " + _voClazzName + " vo WHERE vo.name = '" + tempname + "'";

		Query query = currentSession().createQuery(hql);
		if (!query.list().isEmpty()) {
			return (String) query.list().get(0);
		} else
			return null;
	}

	/**
	 * @SuppressWarnings Hibernate3.2不支持泛型
	 */
	@SuppressWarnings("unchecked")
	public DomainVO getDomainByName(String tempname) throws Exception {
		// String hql = "FROM " + this._voClazzName + " vo where vo.name =: '" +
		// tempname + "'";
		Session session = currentSession();
		Criteria criteria = session.createCriteria(this._voClazzName);
		Criterion criterion1 = Expression.like("name", tempname).ignoreCase();
		criteria = criteria.add(criterion1);
		Collection<DomainVO> cols = criteria.list();
		// ParamsTable params = new ParamsTable();
		// Collection cols = getDatas(hql, params);
		if (cols != null && !cols.isEmpty())
			return (DomainVO) cols.toArray()[0];
		return null;
	}

	/**
	 * 根据UserID查找Domain列表
	 * 
	 * @param userid
	 * @param page
	 * @param line
	 * @return
	 * @throws Exception
	 * @SuppressWarnings Hibernate3.2不支持泛型
	 */
	@SuppressWarnings("unchecked")
	public Collection<DomainVO> queryDomains(String userid, int page, int line) throws Exception {
		String hql = "FROM " + this._voClazzName + " vo WHERE vo.users.id='" + userid + "'";
		Query query = currentSession().createQuery(hql);
		query.setFirstResult((page - 1) * line);
		query.setMaxResults(line);
		return query.list();
	}
	
	/**
	 * @SuppressWarnings Hibernate3.2不支持泛型
	 */
	@SuppressWarnings("unchecked")
	public DataPackage<DomainVO> queryDomainsByManager(String manager, int page, int line) throws Exception {
		String hql = "FROM " + this._voClazzName + " vo WHERE vo.users.name LIKE '%" + manager + "%'";
		Query query = currentSession().createQuery(hql);
		DataPackage<DomainVO> pkg = new DataPackage<DomainVO>();
		pkg.setRowCount(query.list().size());
		query.setFirstResult((page - 1) * line);
		query.setMaxResults(line);
		pkg.setDatas(query.list());
		pkg.setLinesPerPage(line);
		pkg.setPageNo(page);

		return pkg;
	}

	/**
	 * @SuppressWarnings Hibernate3.2不支持泛型
	 */
	@SuppressWarnings("unchecked")
	public Collection<DomainVO> getAllDomain() throws Exception {
		String hql = "FROM " + this._voClazzName + " vo";
		Query query = currentSession().createQuery(hql);
		return query.list();
	}

	/**
	 * @SuppressWarnings Hibernate3.2不支持泛型
	 */
	@SuppressWarnings("unchecked")
	public DataPackage<DomainVO> queryDomainsByName(String name, int page, int line) throws Exception {
		String hql = "FROM " + this._voClazzName + " vo WHERE vo.name LIKE '%" + name + "%'";
		Query query = currentSession().createQuery(hql);
		DataPackage<DomainVO> pkg = new DataPackage<DomainVO>();
		pkg.setRowCount(query.list().size());
		query.setFirstResult((page - 1) * line);
		query.setMaxResults(line);
		pkg.setDatas(query.list());
		pkg.setLinesPerPage(line);
		pkg.setPageNo(page);
		return pkg;
	}

	/**
	 * 根据管理员名称和域名称查询域
	 * 
	 * @param manager
	 *            -管理员名称
	 * @param name
	 *            -域名称
	 * @param page
	 *            -页
	 * @param line
	 *            -行
	 * @return
	 * @throws Exception
	 * @SuppressWarnings Hibernate3.2不支持泛型
	 */
	@SuppressWarnings("unchecked")
	public DataPackage<DomainVO> queryDomainsbyManagerAndName(String manager, String name, int page, int line)
			throws Exception {
		String hql = "FROM " + this._voClazzName + " vo WHERE vo.users.name LIKE '%" + manager + "%'"
				+ " and vo.name LIKE '%" + name + "%'";
		Query query = currentSession().createQuery(hql);
		DataPackage<DomainVO> pkg = new DataPackage<DomainVO>();
		pkg.setRowCount(query.list().size());
		query.setFirstResult((page - 1) * line);
		query.setMaxResults(line);
		pkg.setDatas(query.list());
		pkg.setLinesPerPage(line);
		pkg.setPageNo(page);
		return pkg;
	}

	/**
	 * 根据管理员登录名和域名称查询域
	 * 
	 * @param managerLoginno
	 *            -管理员登录名
	 * @param name
	 *            -域名称
	 * @param page
	 *            -页
	 * @param line
	 *            -行
	 * @return
	 * @throws Exception
	 */
	public DataPackage<DomainVO> queryDomainsbyManagerLoginnoAndName(String managerLoginno, String name, int page,
			int line) throws Exception {
		StringBuffer buffer = new StringBuffer();
		buffer.append("FROM " + this._voClazzName + " vo");
		if (!StringUtil.isBlank(managerLoginno)) {
			buffer.append(" WHERE vo.users.loginno LIKE '%" + managerLoginno + "%'");
		}
		if (!StringUtil.isBlank(name)) {
			if (buffer.toString().indexOf("WHERE") >= 0) {
				buffer.append(" and vo.name LIKE '%" + name + "%'");
			} else {
				buffer.append(" WHERE vo.name LIKE '%" + name + "%'");
			}
		}
		return getDatapackage(buffer.toString(), page, line);
	}

}
