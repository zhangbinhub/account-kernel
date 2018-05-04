package OLink.bpm.core.email.email.dao;

import OLink.bpm.base.dao.HibernateBaseDAO;
import OLink.bpm.core.email.email.ejb.EmailBody;

public class HibernateEmailBodyDAO extends HibernateBaseDAO<EmailBody> implements
		EmailBodyDAO {

	public HibernateEmailBodyDAO(String voClassName) {
		super(voClassName);
	}
}
