package OLink.bpm.core.homepage.dao;

import java.util.Collection;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.base.dao.IRuntimeDAO;
import OLink.bpm.core.homepage.ejb.Reminder;

public interface ReminderDAO extends IRuntimeDAO {
	Reminder findByForm(String formId, String applicationId)
			throws Exception;

	Collection<Reminder> findReminder(String id) throws Exception;

	DataPackage<Reminder> getReminderByApplication(ParamsTable params,
												   String applicationid, String _currpage, String _pagelines)
			throws Exception;

	DataPackage<Reminder> getReminderByHomepage(ParamsTable params,
												String homepageId, String applicationid, String _currpage,
												String _pagelines) throws Exception;
}
