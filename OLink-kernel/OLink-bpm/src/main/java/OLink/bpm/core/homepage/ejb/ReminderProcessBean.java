package OLink.bpm.core.homepage.ejb;

import java.util.Collection;

import OLink.bpm.base.dao.DAOFactory;
import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.base.dao.IDesignTimeDAO;
import OLink.bpm.base.ejb.AbstractDesignTimeProcessBean;
import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.core.homepage.dao.ReminderDAO;

public class ReminderProcessBean extends AbstractDesignTimeProcessBean<Reminder>
		implements ReminderProcess {

	/**
	 * 
	 */
	private static final long serialVersionUID = -859213022776422932L;

	/**
	 * @SuppressWarnings getDefaultDAO得到的process不确定
	 */
	@SuppressWarnings("unchecked")
	protected IDesignTimeDAO<Reminder> getDAO() throws Exception {
		return (IDesignTimeDAO<Reminder>) DAOFactory.getDefaultDAO(Reminder.class.getName());
	}

	public Reminder doViewByForm(String formId, String applicationId)
			throws Exception {
		return ((ReminderDAO) getDAO()).findByForm(formId, applicationId);
	}
	
	public Collection<Reminder> doViewById(String id) throws Exception {
		return ((ReminderDAO) getDAO()).findReminder(id);
	}

	public DataPackage<Reminder> getReminderByApplication(ParamsTable params,
														  String applicationid, String _currpage, String _pagelines)
			throws Exception {
		return ((ReminderDAO) getDAO()).getReminderByApplication(params,
				applicationid, _currpage, _pagelines);
	}

	public DataPackage<Reminder> getReminderByHomepage(ParamsTable params,
			String homepageId, String applicationid, String _currpage,
			String _pagelines) throws Exception {
		return ((ReminderDAO) getDAO()).getReminderByHomepage(params,
				homepageId, applicationid, _currpage, _pagelines);
	}
}
