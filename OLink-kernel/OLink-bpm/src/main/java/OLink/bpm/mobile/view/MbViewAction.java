package OLink.bpm.mobile.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import OLink.bpm.core.dynaform.view.ejb.View;
import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.core.dynaform.form.ejb.Form;
import OLink.bpm.core.dynaform.view.ejb.ViewType;
import OLink.bpm.core.macro.runner.IRunner;
import OLink.bpm.constans.Web;
import org.apache.log4j.Logger;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.core.dynaform.document.ejb.Document;
import OLink.bpm.core.dynaform.form.ejb.FormField;
import OLink.bpm.core.dynaform.form.ejb.ValidateMessage;
import OLink.bpm.core.dynaform.view.action.ViewAction;
import OLink.bpm.core.macro.runner.JavaScriptFactory;
import OLink.bpm.core.table.constants.MobileConstant;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.util.HtmlEncoder;
import OLink.bpm.util.StringUtil;

import com.opensymphony.webwork.ServletActionContext;

public class MbViewAction extends ViewAction {

	private static final long serialVersionUID = 1876974711850496098L;
	private static final Logger LOG = Logger.getLogger(MbViewAction.class);

	private String _currpage;
	private String _mapStr;

	public MbViewAction() throws ClassNotFoundException {
		super();
	}

	public String doDisplaySearchForm() throws Exception {
		try {
			view = (View) process.doView(_viewid);
			if (view != null) {
				setContent(view);
				toSearchFormXml();
			} else {
				throw new Exception("View id is null!");
			}
		} catch (Exception e) {
			this.addFieldError("SystemError", e.getMessage());
			LOG.warn(e);
			return ERROR;
		}
		return SUCCESS;
	}

	private void toSearchFormXml() throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpSession session = request.getSession();
		WebUser user = getUser();
		ParamsTable params = new ParamsTable();
		params.setParameter("_viewid", _viewid);
		params.setParameter("_mapStr", _mapStr);
		params.setParameter("parentid", parent);
		Document tdoc = parent != null ? parent : new Document();
		IRunner runner = JavaScriptFactory.getInstance(request.getSession().getId(), getApplication());
		runner.initBSFManager(tdoc, params, user, new ArrayList<ValidateMessage>());
		Form searchForm = view.getSearchForm();
		if (searchForm != null) {
			Document searchDoc = searchForm.createDocument(params, user);
			String xmlText = searchForm.toMbXML(searchDoc, params, user, new ArrayList<ValidateMessage>(), getEnvironment());
			session.setAttribute("toXml", xmlText);
		}
	}

	public String getWebUserSessionKey() {
		return Web.SESSION_ATTRIBUTE_FRONT_USER;
	}

	public String doDialogView() throws Exception {
		return doDisplayView(true);
	}

	public String doDisplayView() throws Exception {
		return doDisplayView(false);
	}

	private String doDisplayView(boolean isDialogView) throws Exception {
		try {
			ParamsTable params = getParams();
			view = (View) process.doView(_viewid);
			if (view == null) throw new Exception("View id is null or view is not exist!");
			
			Document searchDocument = getSearchDocument(view);
			// 设置Action属性
			setContent(view);
			setParent(null);
			setCurrentDocument(searchDocument);
			changeOrderBy(params);
			// 分页参数
			int page = 1;
			if (!StringUtil.isBlank(_currpage)) {
				try {
					page = Integer.parseInt(_currpage);
				} catch (Exception e) {
					_currpage = "1";
					LOG.warn(e);
				}
			} else {
				_currpage = "1";
			}
			view.setPagination(true);
			ViewType viewType = view.getViewTypeImpl();
			DataPackage<Document> datas = viewType.getViewDatasPage(params, page, get_pagelines(), getUser(), searchDocument);
			if (datas == null) datas = new DataPackage<Document>();
			toViewListXml(isDialogView, datas);
		} catch (Exception e) {
			this.addFieldError("SystemError", e.getMessage());
			e.printStackTrace();
			return ERROR;
		}
		return SUCCESS;
	}

	private void toViewListXml(boolean isDialogView, DataPackage<Document> datas) throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpSession session = request.getSession();
		StringBuffer buffer = new StringBuffer();
		String title = view.getDescription();
		if (StringUtil.isBlank(title) || title.trim().equals("null")) title = view.getName();
		title = " (" + datas.pageNo + "/" + datas.getPageCount() + ")  " + title;
		buffer.append("<").append(MobileConstant.TAG_VIEW).append(" ");
		buffer.append(MobileConstant.ATT_TITLE).append("='" + HtmlEncoder.encode(title) + "' ");
		
		String refresh = params.getParameterAsString("refresh");
		boolean isRefresh = refresh != null && refresh.trim().equals("true");
		if (isRefresh) {
			buffer.append(MobileConstant.ATT_REFRESH).append("='true' ");
		}
		if (view.getReadonly().booleanValue()) {
			buffer.append(MobileConstant.ATT_READONLY).append("='true' ");
		}
		buffer.append(">");
		
		MbViewHelper helper = new MbViewHelper(getApplication());
		buffer.append(helper.getHiddenFieldXml("_currpage", _currpage));
		buffer.append(helper.toViewListMobileXml(isDialogView, view, parent, datas, getUser(), request, params));
		
		compareSearchParams(buffer, view, params);
		buffer.append("</").append(MobileConstant.TAG_VIEW).append(">");
		session.setAttribute("toXml", buffer.toString());
	}
	
	/**
	 * 比较和添加查询字段值
	 * @param xml
	 * @param view
	 * @param params
	 */
	private void compareSearchParams(StringBuffer xml, View view, ParamsTable params) {
		if (view == null) return;
		Form searchForm = view.getSearchForm();
		if (searchForm == null) return;
		//String xmlString = xml.toString();
		for (Iterator<String> it = params.getParameterNames(); it.hasNext(); ) {
			String key = it.next();
			FormField field = searchForm.findFieldByName(key);
			if (field == null) continue;
			String value = params.getParameterAsString(key);
			if (!StringUtil.isBlank(value) 
					/*&& xmlString.indexOf(key) < 0*/
					&& value.indexOf(">") < 0 
					&& value.indexOf("<") < 0) {
				xml.append("<").append(MobileConstant.TAG_HIDDENFIELD).append(" ");
				xml.append(MobileConstant.ATT_NAME).append("='" + key + "'>");
				xml.append(value);
				xml.append("</").append(MobileConstant.TAG_HIDDENFIELD).append(">");
			}
		}
	}

	public String get_currpage() {
		return _currpage;
	}

	public void set_currpage(String _currpage) {
		this._currpage = _currpage;
	}
	
	public String get_mapStr() {
		return _mapStr;
	}

	public void set_mapStr(String str) {
		_mapStr = str;
	}

	public String getApplication() {
		//if (!StringUtil.isBlank(application)) {
		//	return application;
		//}
		return application;
	}

	private Map<String, List<String>> fieldErrors;

	public void addFieldError(String fieldname, String message) {
		List<String> thisFieldErrors = getFieldErrors().get(fieldname);

		if (thisFieldErrors == null) {
			thisFieldErrors = new ArrayList<String>();
			this.fieldErrors.put(fieldname, thisFieldErrors);
		}
		thisFieldErrors.add(message);
	}

	public Map<String, List<String>> getFieldErrors() {
		if (fieldErrors == null) fieldErrors = new HashMap<String, List<String>>();
		return fieldErrors;
	}

	/**
	 * @SuppressWarnings API支持泛型Map(String, List(String))
	 */
	@SuppressWarnings("unchecked")
	public void setFieldErrors(Map fieldErrors) {
		this.fieldErrors = fieldErrors;
	}
	
	public int get_pagelines() {
		Integer _pagelines = (Integer) ServletActionContext.getRequest().getSession().getAttribute("_pagelines");
		if (_pagelines != null) return _pagelines.intValue();
		return 5;
	}
	
}
