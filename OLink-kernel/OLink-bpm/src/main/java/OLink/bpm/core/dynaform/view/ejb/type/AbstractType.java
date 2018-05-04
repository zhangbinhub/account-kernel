package OLink.bpm.core.dynaform.view.ejb.type;

import java.util.HashMap;
import java.util.Map;

import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.constans.Web;
import OLink.bpm.core.dynaform.document.ejb.Document;
import OLink.bpm.core.dynaform.view.ejb.Column;
import OLink.bpm.core.dynaform.view.ejb.EditMode;
import OLink.bpm.core.dynaform.view.ejb.View;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.util.StringUtil;

public abstract class AbstractType {
	protected View view;

	protected Map<String, Column> columnMapping;

	public AbstractType(View view) {
		this.view = view;
	}

	public long countViewDatas(ParamsTable params, WebUser user, Document sdoc) throws Exception {
		EditMode editMode = view.getEditModeType();
		addConditionToMode(editMode, user, params);
		long count = editMode.count(params, user, sdoc);

		return count;
	}

	public DataPackage<Document> getViewDatas(ParamsTable params, WebUser user, Document sdoc) throws Exception {
		String _currpage = params.getParameterAsString("_currpage");
		String _pagelines = view.getPagelines();

		// 分页参数
		int page = (_currpage != null && _currpage.length() > 0) ? Integer.parseInt(_currpage) : 1;
		int lines = (_pagelines != null && _pagelines.length() > 0) ? Integer.parseInt(_pagelines) : Integer
				.parseInt(Web.DEFAULT_LINES_PER_PAGE);

		return getViewDatas(params, page, lines, user, sdoc);
	}

	public DataPackage<Document> getViewDatas(ParamsTable params, int page, int lines, WebUser user, Document sdoc)
			throws Exception {
		EditMode editMode = view.getEditModeType();
		addConditionToMode(editMode, user, params);
		if (view.isPagination()) {
			return editMode.getDataPackage(params, page, lines, user, sdoc);
		} else {
			return editMode.getDataPackage(params, user, sdoc);
		}
	}

	public DataPackage<Document> getViewDatasPage(ParamsTable params, int page, int lines, WebUser user, Document sdoc)
			throws Exception {
		EditMode editMode = view.getEditModeType();
		addConditionToMode(editMode, user, params);
		return editMode.getDataPackage(params, page, lines, user, sdoc);
	}

	protected void addConditionToMode(EditMode editMode, WebUser user, ParamsTable params) throws Exception {
		// 获取参数
		String parentid = params.getParameterAsString("parentid");
		boolean isRelate = params.getParameterAsBoolean("isRelate");
		if (!StringUtil.isBlank(parentid) && isRelate) {
			editMode.addCondition("PARENT", parentid); // 添加父文档查询条件
		}
	}

	public abstract int intValue();

	public String toHtml(ParamsTable params, WebUser user) throws Exception {
		return "";
	}

	public Map<String, Column> getColumnMapping() {
		return new HashMap<String, Column>();
	}
}
