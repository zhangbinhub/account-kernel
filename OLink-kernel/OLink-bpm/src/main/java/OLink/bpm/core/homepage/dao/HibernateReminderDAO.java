package OLink.bpm.core.homepage.dao;

import java.util.Collection;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.base.dao.HibernateBaseDAO;
import OLink.bpm.core.homepage.ejb.Reminder;

public class HibernateReminderDAO extends HibernateBaseDAO<Reminder> implements
		ReminderDAO {
	public HibernateReminderDAO(String voClassName) {
		super(voClassName);
	}

	public Reminder findByForm(String formId, String applicationId)
			throws Exception {
		String hql = "from " + this._voClazzName + " vo where formId='"
				+ formId + "' and vo.applicationid = '" + applicationId + "'";
		return (Reminder) getData(hql);
	}

	public Collection<Reminder> findReminder(String id) throws Exception {
		String hql = "from " + this._voClazzName + " vo "
				+ "' and vo.homepageid = '" + id + "' order by id";
		return getDatas(hql);
	}

	public DataPackage<Reminder> getReminderByHomepage(ParamsTable params,
													   String homepageId, String applicationid, String _currpage,
													   String _pagelines) throws Exception {
		String hql = "FROM " + _voClazzName + " vo WHERE 1=1";

		int page = (_currpage != null && _currpage.length() > 0) ? Integer
				.parseInt(_currpage) : 1;
		int lines = (_pagelines != null && _pagelines.length() > 0) ? Integer
				.parseInt(_pagelines) : Integer.MAX_VALUE;

		if (applicationid != null && applicationid.length() > 0) {
			hql += (" and applicationid = '" + applicationid + "' ");
		}
		if (applicationid != null && applicationid.length() > 0) {
			hql += (" and homepage = '" + homepageId + "' ");
		}
		return getDatapackage(hql, params, page, lines);
	}

	public DataPackage<Reminder> getReminderByApplication(ParamsTable params,
			String applicationid, String _currpage, String _pagelines)
			throws Exception {
		String hql = "FROM " + _voClazzName + " vo WHERE 1=1 and homepage is null";
		int page = (_currpage != null && _currpage.length() > 0) ? Integer
				.parseInt(_currpage) : 1;
		int lines = (_pagelines != null && _pagelines.length() > 0) ? Integer
				.parseInt(_pagelines) : Integer.MAX_VALUE;

		if (applicationid != null && applicationid.length() > 0) {
			hql += (" and applicationid = '" + applicationid + "' ");
		}

		return getDatapackage(hql, params, page, lines);
	}
}
