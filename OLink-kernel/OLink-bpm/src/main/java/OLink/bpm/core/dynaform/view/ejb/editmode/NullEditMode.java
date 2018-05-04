package OLink.bpm.core.dynaform.view.ejb.editmode;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.core.dynaform.view.ejb.EditMode;
import OLink.bpm.core.dynaform.view.ejb.View;
import OLink.bpm.core.dynaform.document.ejb.Document;
import OLink.bpm.core.user.action.WebUser;

/**
 * 
 * @author nicholas zhen
 * 
 */
public class NullEditMode extends AbstractEditMode implements EditMode {

	public NullEditMode(View view) {
		super(view);
	}

	public String getQueryString(ParamsTable params, WebUser user, Document sDoc) {
		return "";
	}

	public DataPackage<Document> getDataPackage(ParamsTable params, WebUser user, Document doc) throws Exception {
		return new DataPackage<Document>();
	}

	public DataPackage<Document> getDataPackage(ParamsTable params, int page, int lines, WebUser user, Document doc) throws Exception {
		return new DataPackage<Document>();
	}

	public long count(ParamsTable params, WebUser user, Document doc) throws Exception {
		return 0;
	}
}
