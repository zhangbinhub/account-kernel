package OLink.bpm.core.page.dao;

import java.util.Collection;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.core.dynaform.form.dao.FormDAO;
import OLink.bpm.core.page.ejb.Page;

public interface PageDAO extends FormDAO<Page> {
	Page findDefaultPage(String application) throws Exception;

	Page findByName(String name, String application) throws Exception;

	DataPackage<Page> getDatasExcludeMod(ParamsTable params, String application) throws Exception;

	Collection<Page> getPagesByApplication(String application) throws Exception;
}
