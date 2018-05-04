package OLink.bpm.core.homepage.ejb;

import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.base.ejb.IDesignTimeProcess;
import OLink.bpm.core.user.action.WebUser;

public interface ReminderProcess extends IDesignTimeProcess<Reminder> {

	DataPackage<Reminder> doQuery(ParamsTable params, WebUser user)
			throws Exception;

	Reminder doViewByForm(String formId, String applicationId)
			throws Exception;

	DataPackage<Reminder> getReminderByApplication(ParamsTable params,
												   String applicationid, String _currpage, String _pagelines)
			throws Exception;

	DataPackage<Reminder> getReminderByHomepage(ParamsTable params,
												String homepageId, String applicationid, String _currpage,
												String _pagelines) throws Exception;
}
