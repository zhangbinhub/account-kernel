package OLink.bpm.desktop.view;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import OLink.bpm.base.action.BaseAction;
import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.constans.Web;
import OLink.bpm.core.dynaform.activity.ejb.Activity;
import OLink.bpm.core.dynaform.activity.ejb.ActivityType;
import OLink.bpm.core.dynaform.document.ejb.Document;
import OLink.bpm.core.dynaform.form.ejb.Form;
import OLink.bpm.core.dynaform.form.ejb.ValidateMessage;
import OLink.bpm.core.dynaform.view.action.ViewAction;
import OLink.bpm.core.dynaform.view.ejb.Column;
import OLink.bpm.core.dynaform.view.ejb.View;
import OLink.bpm.core.macro.runner.IRunner;
import OLink.bpm.core.macro.runner.JavaScriptFactory;
import OLink.bpm.core.table.constants.MobileConstant;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.util.HtmlEncoder;
import OLink.bpm.util.StringUtil;
import com.opensymphony.xwork.Action;
import org.apache.log4j.Logger;

import com.opensymphony.webwork.ServletActionContext;

public class DViewAction extends ViewAction {

	private static final long serialVersionUID = 1876974711850496098L;
	private static final Logger LOG = Logger.getLogger(DViewAction.class);

	private String _currpage;

	private String _mapStr;

	public String get_mapStr() {
		return _mapStr;
	}

	public void set_mapStr(String str) {
		_mapStr = str;
	}

	public DViewAction() throws ClassNotFoundException {
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
			return Action.ERROR;
		}
		return Action.SUCCESS;
	}

	private void toSearchFormXml() throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();
		// HttpSession session = request.getSession();
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
			request.setAttribute("toXml", xmlText);
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
			if (view != null) {
				Document searchDocument = getSearchDocument(view);
				// 设置Action属性
				setContent(view);
				setParent(null);
				setCurrentDocument(searchDocument);

				changeOrderBy(params);

				String _currpage = params.getParameterAsString("_currpage");
				// 分页参数
				int page = (_currpage != null && _currpage.length() > 0) ? Integer.parseInt(_currpage) : 1;

				view.setPagination(true);
				DataPackage<Document> datas = view.getViewTypeImpl().getViewDatasPage(params, page, 5, getUser(), searchDocument);
				if (datas == null) {
					datas = new DataPackage<Document>();
				}
				toViewListXml(isDialogView, datas);
			} else {
				throw new Exception("View id is null or view is not exist!");
			}
		} catch (Exception e) {
			StringBuffer msg = new StringBuffer(e.getMessage() == null ? "" : e.getMessage());
			int length = msg.length();
			if (length > 40) {
				msg = new StringBuffer();
				while (length < e.getMessage().length()) {
					if (length + 40 < e.getMessage().length()) {
						msg.append(e.getMessage().substring(length, length + 40) + "\n");
					} else {
						msg.append(e.getMessage().substring(length, e.getMessage().length()));
					}
					length += 40;
				}
			}
			this.addFieldError("SystemError", msg.toString());
			LOG.warn(e);
			return Action.ERROR;
		}
		return Action.SUCCESS;
	}

	private void toViewListXml(boolean isDialogView, DataPackage<Document> datas) throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpSession session = request.getSession();
		WebUser user = getUser();

		Collection<ValidateMessage> errors = new HashSet<ValidateMessage>();
		ParamsTable params = getParams();
		String refresh = params.getParameterAsString("refresh");
		boolean isRefresh = refresh != null && refresh.trim().equals("true");
		IRunner jsrun = JavaScriptFactory.getInstance(params.getSessionid(), getApplication());

		Collection<Column> columns = view.getColumns();
		String title = view.getDescription();
		if (title == null || title.trim().length() <= 0 || title.trim().equals("null"))
			title = view.getName();
		//title = " (" + datas.pageNo + "/" + datas.getPageCount() + ")  " + title;
		StringBuffer buffer = new StringBuffer();
		buffer.append("<").append(MobileConstant.TAG_VIEW).append(" ").append(MobileConstant.ATT_TITLE).append(
				"='" + HtmlEncoder.encode(title) + "' ");
		if (isRefresh) {
			buffer.append(MobileConstant.ATT_REFRESH).append("='true' ");
		}
		if (view.getReadonly().booleanValue()) {
			buffer.append(MobileConstant.ATT_READONLY).append("='true' ");
		}
		buffer.append(">");
		if (isRefresh)
			buffer.append("<").append(MobileConstant.TAG_HIDDENFIELD).append(" ").append(MobileConstant.ATT_NAME).append(
					"='refresh'>true</").append(MobileConstant.TAG_HIDDENFIELD).append(">");

		buffer.append("<").append(MobileConstant.TAG_HIDDENFIELD).append(" ").append(MobileConstant.ATT_NAME).append(
				"='_viewid'>" + _viewid + "</").append(MobileConstant.TAG_HIDDENFIELD).append(">");
		if (isDialogView) {
			String mapStr = params.getParameterAsString("_mapStr");
			buffer.append("<").append(MobileConstant.TAG_HIDDENFIELD).append(" ").append(MobileConstant.ATT_NAME).append(
					"='_mapStr'>" + mapStr + "</").append(MobileConstant.TAG_HIDDENFIELD).append(">");
		}
		if (parent != null && parent.getId() != null) {
			buffer.append("<").append(MobileConstant.TAG_HIDDENFIELD).append(" ").append(MobileConstant.ATT_NAME).append(
					"='parentid'>" + parent.getId() + "</").append(MobileConstant.TAG_HIDDENFIELD).append(">");
		}
		String isRelate = params.getParameterAsString("isRelate");
		if (isRelate != null && isRelate.trim().equals("true")) {
			buffer.append("<").append(MobileConstant.TAG_HIDDENFIELD).append(" ").append(MobileConstant.ATT_NAME).append(
					"='isRelate'>true</").append(MobileConstant.TAG_HIDDENFIELD).append(">");
		}
		Document tdoc = parent != null ? parent : new Document();
		boolean flag = false;
		IRunner runner = JavaScriptFactory.getInstance(session.getId(), view.getApplicationid());
		runner.initBSFManager(tdoc, params, user, new ArrayList<ValidateMessage>());

		Collection<Activity> activities = view.getActivitys();
		if (activities == null) {
			activities = new ArrayList<Activity>();
		}

		Iterator<Activity> aiter = activities.iterator();
		while (aiter.hasNext()) {
			Activity act = aiter.next();
			boolean isHidden = false;
			if ((act.getHiddenScript()) != null && (act.getHiddenScript()).trim().length() > 0) {
				StringBuffer label = new StringBuffer();
				label.append("View").append("." + view.getName()).append(".Activity(").append(act.getId()).append(
						act.getName() + ")").append(".runHiddenScript");

				Object result = runner.run(label.toString(), act.getHiddenScript());// Run
				// the
				// Script
				if (result != null && result instanceof Boolean) {
					isHidden = ((Boolean) result).booleanValue();
				}
			}

			boolean isStateToHidden = false;
			if (parent != null) {
				isStateToHidden = act.isStateToHidden(parent);
			}

			flag = (isHidden || isStateToHidden);

			if (!flag) {
				//if (act.getType() != ActivityType.EXPTOEXCEL) {
				if (act.getType() == ActivityType.DOCUMENT_DELETE
						|| act.getType() == ActivityType.DOCUMENT_CREATE) {
					buffer.append("<").append(MobileConstant.TAG_ACTION).append(" ").append(MobileConstant.ATT_TYPE).append("='");
					buffer.append(act.getType());
					buffer.append("' ").append(MobileConstant.ATT_NAME).append(
							"='{*[" + HtmlEncoder.encode(act.getName()) + "]*}' ");
					buffer.append("").append(MobileConstant.ATT_ID).append("='");
					buffer.append(act.getId());
					buffer.append("'>");
					buffer.append("</").append(MobileConstant.TAG_ACTION).append(">");
					//break;
				}
			}
		}
		if (isDialogView) {
			buffer.append("<").append(MobileConstant.TAG_ACTION).append(" ").append(MobileConstant.ATT_TYPE).append("='link' ")
					.append(MobileConstant.ATT_NAME).append("='{*[OK]*}' ");
			buffer.append(">");
			buffer.append("</").append(MobileConstant.TAG_ACTION).append(">");
		}

		buffer.append("<").append(MobileConstant.TAG_HIDDENFIELD).append(" ").append(MobileConstant.ATT_NAME).append(
			"='application'>"+application+"</").append(MobileConstant.TAG_HIDDENFIELD).append(">");
		
		buffer.append("<").append(MobileConstant.TAG_TABLE).append(">");
		
		buffer.append("<").append(MobileConstant.TAG_TH).append(">");

		if (view != null) {
//			if (view.getSearchForm() != null) {
//				buffer.append("<").append(MobileConstant.TAG_ACTION).append(" ").append(MobileConstant.ATT_TYPE).append("='23'");
//				buffer.append(" ").append(MobileConstant.ATT_NAME).append("='{*[Search]*}' ");
//				buffer.append("").append(MobileConstant.ATT_ID).append("=''>");
//				buffer.append("</").append(MobileConstant.TAG_ACTION).append(">");
//			}
			Collection<Column> col = view.getColumns();
			if (col != null) {
				Iterator<Column> its = col.iterator();
				if (its != null) {
					while (its.hasNext()) {
						Column column = its.next();
						buffer.append("<").append(MobileConstant.TAG_TD).append(" ").append(MobileConstant.ATT_NAME).append(
								" = '' ").append(MobileConstant.ATT_WIDTH).append(" = '" + column.getWidth() + "'>");
						buffer.append(getString2Table(HtmlEncoder.encode(column.getName())) + "</").append(MobileConstant.TAG_TD).append(">");
					}
				}
			}
		}
		buffer.append("</").append(MobileConstant.TAG_TH).append(">");

		int total = 1;
		
		if (datas != null) {
			Collection<Document> col = datas.getDatas();
			if (col != null) {
				Iterator<Document> its = col.iterator();
				while (its.hasNext()) {

					Document doc = its.next();

					jsrun.initBSFManager(doc, params, user, errors);
					Iterator<Column> iter = columns.iterator();
					if (doc != null && doc.getId() != null) {
						try {
							buffer.append("<").append(MobileConstant.TAG_TR).append(" ").append(MobileConstant.ATT_ID).append(
									" = '" + doc.getId()+";"+doc.getFormid() + "'>");
							while (iter.hasNext()) {
								buffer.append("<").append(MobileConstant.TAG_TD).append(" ");
								Column column = iter.next();
								Object result = null;
								if (column.getType() != null && column.getType().equals(Column.COLUMN_TYPE_SCRIPT)) {
									StringBuffer label = new StringBuffer();
									label.append("DisplayView.").append(view.getId()).append(".Column.").append(column.getId())
											.append("runValueScript");
									result = jsrun.run(label.toString(), column.getValueScript());
								} else if (column.getType() != null && column.getType().equals(Column.COLUMN_TYPE_FIELD)) {
									result = doc.getValueByField(column.getFieldName());
								}
								if (column != null && column.getId() != null) {
									buffer.append(" ").append(MobileConstant.ATT_NAME).append(
											" = '" + HtmlEncoder.encode(column.getId()) + "' >");
								} else {
									buffer.append(" ").append(MobileConstant.ATT_NAME).append(" ='' >");
								}

								if (result == null) { // DO NOT display null
									result = "";
								}

								buffer.append(getString2Table(HtmlEncoder.encode((String) result)));
								buffer.append("</").append(MobileConstant.TAG_TD).append(">");
							}

							buffer.append("</").append(MobileConstant.TAG_TR).append(">");
						} catch (Exception e) {
							LOG.warn(e.toString());
							throw e;
						}
					}
				}
			}
			total = datas.getPageCount();
		}

		buffer.append("</").append(MobileConstant.TAG_TABLE).append(">");
		
		buffer.append("<" + MobileConstant.TAG_PAGE + " " + MobileConstant.ATT_TOTAL + "='" + total + "' " + MobileConstant.ATT_CURRPAGE + "='" + datas.pageNo + "'>").append("</" + MobileConstant.TAG_PAGE + ">");
		
		buffer.append("</").append(MobileConstant.TAG_VIEW).append(">");
		request.setAttribute("toXml", buffer.toString());
	}

	public String get_currpage() {
		return _currpage;
	}

	public void set_currpage(String _currpage) {
		this._currpage = _currpage;
	}

	public String getApplication() {
		if (application != null && application.trim().length() > 0) {
			return application;
		} else {
			return (String) BaseAction.getContext().getSession().get(Web.SESSION_ATTRIBUTE_APPLICATION);
		}
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
		if (fieldErrors == null)
			fieldErrors = new HashMap<String, List<String>>();
		return fieldErrors;
	}

	/**
	 * @SuppressWarnings API支持泛型Map(String, List(String))
	 */
	@SuppressWarnings("unchecked")
	public void setFieldErrors(Map fieldErrors) {
		this.fieldErrors = fieldErrors;
	}
	
	public String getString2Table(String str) {
		if (StringUtil.isBlank(str)) {
			return " ";
		}
		return str;
	}
	
	/**
	 * @SuppressWarnings API支持泛型
	 */
	@SuppressWarnings("unchecked")
	public void setApplication(String application) {
		this.application = application;
		String appId = (String) BaseAction.getContext().getSession().get(Web.SESSION_ATTRIBUTE_APPLICATION);
		if (StringUtil.isBlank(application)) {
			this.application = appId;
		} else {
			if (!application.equals(appId)) {
				BaseAction.getContext().getSession().put(Web.SESSION_ATTRIBUTE_APPLICATION, application);
			}
		}
	}
	
}
