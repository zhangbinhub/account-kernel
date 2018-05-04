package OLink.bpm.core.email.attachment.dao;

import java.util.Collection;
import java.util.List;

import OLink.bpm.base.dao.HibernateBaseDAO;
import OLink.bpm.core.email.attachment.ejb.Attachment;
import OLink.bpm.core.email.email.ejb.Email;
import org.hibernate.Query;

public class HibernateAttachmentDAO extends HibernateBaseDAO<Attachment> implements
		AttachmentDAO {

	public HibernateAttachmentDAO(String voClassName) {
		super(voClassName);
	}

	public Collection<Attachment> queryAttachmentByEmails(Email email) throws Exception {
		StringBuffer hql = new StringBuffer();
		hql.append("from ").append(_voClazzName).append(" vo ");
		//if (StringUtil.isBlank(email.getEmailBody().getId())) {
		//	hql.append("where vo.emailBody.id = '").append(email.getId()).append("' order by vo.sendDate desc");
		//} else {
			hql.append("where vo.emailBody.id = '").append(email.getEmailBody().getId()).append("' order by vo.sendDate desc");
		//}
		return getDatas(hql.toString());
	}

	/**
	 * @SuppressWarnings API不支持泛型
	 */
	@SuppressWarnings("unchecked")
	public int queryAttachmentCountByEmail(Email email) throws Exception {
		StringBuffer hql = new StringBuffer();
		hql.append("select count(*) from ").append(_voClazzName).append(" vo ");
		hql.append("where vo.emailBody.id = '").append(email.getEmailBody().getId()).append("'");
		Query query = currentSession().createQuery(hql.toString());
		List list = query.list();
		if (list == null || list.isEmpty()) {
			return 0;
		}
		Long count = (Long) list.get(0);
		return count.intValue();
	}
	
}
