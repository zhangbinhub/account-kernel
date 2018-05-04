package OLink.bpm.core.page.ejb;

import java.util.Collection;

import OLink.bpm.base.dao.DAOFactory;
import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.core.dynaform.form.ejb.BaseFormProcessBean;
import OLink.bpm.core.page.dao.PageDAO;
import OLink.bpm.base.dao.IDesignTimeDAO;
import OLink.bpm.base.action.ParamsTable;

public class PageProcessBean extends BaseFormProcessBean<Page> implements PageProcess {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1504118574193272728L;

	protected IDesignTimeDAO<Page> getDAO() throws Exception {
		return (PageDAO) DAOFactory.getDefaultDAO(Page.class.getName());
	}

	public Page getDefaultPage(String application) throws Exception {
		return ((PageDAO) getDAO()).findDefaultPage(application);
	}

	public Page doViewByName(String name, String application) throws Exception {
		return ((PageDAO) getDAO()).findByName(name, application);
	}

	public DataPackage<Page> doListExcludeMod(ParamsTable params, String application)
			throws Exception {
		return ((PageDAO) getDAO()).getDatasExcludeMod(params, application);
	}

	public Collection<Page> getPagesByApplication(String application)
			throws Exception {
		return ((PageDAO) getDAO()).getPagesByApplication(application);
	}

	public Collection<Page> getTemplateFormsByModule(String moduleid,
			String application) throws Exception {
		return null;
	}
}
