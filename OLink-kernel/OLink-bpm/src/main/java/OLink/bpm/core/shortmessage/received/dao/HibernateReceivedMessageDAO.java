package OLink.bpm.core.shortmessage.received.dao;

import java.util.Calendar;
import java.util.Collection;

import OLink.bpm.base.dao.HibernateBaseDAO;
import OLink.bpm.core.shortmessage.received.ejb.ReceivedMessageVO;
import org.hibernate.Query;
import org.hibernate.Session;

public class HibernateReceivedMessageDAO extends HibernateBaseDAO<ReceivedMessageVO> implements
		ReceivedMessageDAO {

	public HibernateReceivedMessageDAO(String voClassName) {
		super(voClassName);
	}

	public void create(Object po) throws Exception {
		Session session = currentSession();
		session.save(po);
	}

	public ReceivedMessageVO getMessageByReplyCode(String replyCode,
												   String recvtel) throws Exception {
		String hql = "FROM " + _voClazzName + " vo where replyCode = '"
				+ replyCode + "' and receiver = '" + recvtel + "'";
		return (ReceivedMessageVO) getData(hql);
	}

	/**
	 * hibernate3.2不支持泛型
	 */
	@SuppressWarnings("unchecked")
	public Collection<ReceivedMessageVO> queryUnReadMessage() throws Exception {
		String hql = "FROM " + _voClazzName + " vo where status=0 and created >=? order by created";
		Session session = currentSession();
		session.flush();
		Query query = session.createQuery(hql);
		Calendar cld = Calendar.getInstance();
		cld.add(Calendar.DAY_OF_MONTH, -10);
		cld.set(Calendar.HOUR_OF_DAY, 0);
		cld.set(Calendar.MINUTE,0);
		cld.set(Calendar.SECOND, 0);
    	query.setDate(0,cld.getTime());
		query.setFirstResult(0);
		query.setFetchSize(10);
		return query.list();
	}
}