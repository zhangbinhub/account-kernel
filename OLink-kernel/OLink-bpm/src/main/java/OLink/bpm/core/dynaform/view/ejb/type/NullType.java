package OLink.bpm.core.dynaform.view.ejb.type;

import java.util.HashMap;
import java.util.Map;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.core.dynaform.view.ejb.Column;
import OLink.bpm.core.dynaform.view.ejb.View;
import OLink.bpm.core.dynaform.view.ejb.ViewType;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.core.dynaform.document.ejb.Document;

public class NullType implements ViewType {
	protected View view;

	public NullType(View view) {
		this.view = view;
	}

	public DataPackage<Document> getViewDatas(ParamsTable params, WebUser user, Document doc) throws Exception {
		return new DataPackage<Document>();
	}

	public int intValue() {
		return Integer.MAX_VALUE;
	}

	public Map<String, Column> getColumnMapping() {
		return new HashMap<String, Column>();
	}

	public DataPackage<Document> getViewDatasPage(ParamsTable params, int page, int lines, WebUser user, Document doc)
			throws Exception {
		return new DataPackage<Document>();
	}

	public long countViewDatas(ParamsTable params, WebUser user, Document doc) throws Exception {
		return 0;
	}

	public DataPackage<Document> getViewDatas(ParamsTable params, int page,
			int lines, WebUser user, Document sdoc) throws Exception {
		return new DataPackage<Document>();
	}
}
