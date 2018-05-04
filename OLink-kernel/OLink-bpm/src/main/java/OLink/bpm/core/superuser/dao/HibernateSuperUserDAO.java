package OLink.bpm.core.superuser.dao;

import java.util.Collection;
import java.util.List;

import OLink.bpm.base.dao.HibernateBaseDAO;
import OLink.bpm.core.superuser.ejb.SuperUserVO;
import org.hibernate.Query;
import org.hibernate.Session;

public class HibernateSuperUserDAO extends HibernateBaseDAO<SuperUserVO> implements SuperUserDAO {

	public HibernateSuperUserDAO(String voClassName) {
		super(voClassName);
	}

	/**
	 * 检查DAO返回是否为空
	 */
	public boolean isEmpty() throws Exception {
		String hql = "from " + _voClazzName;
		Session session = currentSession();
		Query query = session.createQuery(hql);
		query.setMaxResults(1);
		return query.list().isEmpty();
	}

	/**
	 * SuperUser 登录操作
	 * 
	 * @SuppressWarnings Hibernate3.2不支持泛型
	 * @param loginno
	 *            登录帐号名
	 */
	@SuppressWarnings("unchecked")
	public SuperUserVO login(String loginno) throws Exception {
		String hql = "FROM " + _voClazzName + " vo WHERE vo.loginno='" + loginno + "'";
		Query query = currentSession().createQuery(hql);
		List list = query.list();
		if (list.isEmpty()) {
			return null;
		} else {
			return (SuperUserVO) list.get(0);
		}
	}

	/**
	 * 根据Domain返回数据
	 * 
	 * @param domain
	 *            Domain的唯一标识
	 */
	public Collection<SuperUserVO> getDatasByDomain(String domain) throws Exception {
		String hql = "FROM " + _voClazzName + " vo WHERE vo.domain='" + domain + "'";
		return getDatas(hql);
	}

	/**
	 * 根据用户类型返回数据
	 * 
	 * @param userType
	 *            用户类型
	 * @return 数据集合
	 */
	public Collection<SuperUserVO> getDatasByType(int userType) throws Exception {
		String hql = "FROM " + _voClazzName + " vo WHERE vo.userType='" + userType + "'";
		return getDatas(hql);
	}

	/**
	 * 返回所有的用户填写的Email地址
	 * 
	 * @return 用户的Email集合
	 * 
	 */
	public Collection<SuperUserVO> queryHasMail() throws Exception {
		String hql = "FROM " + _voClazzName + " vo WHERE vo.email IS NOT NULL";
		return getDatas(hql);
	}

	/**
	 * 根据loginno获取管理员
	 * 
	 * @return 用户的Email集合
	 * 
	 */
	public SuperUserVO findByLoginno(String loginno) throws Exception {
		String hql = "FROM " + _voClazzName + " vo WHERE vo.loginno = '" + loginno + "'";
		return (SuperUserVO) getData(hql);
	}
}
