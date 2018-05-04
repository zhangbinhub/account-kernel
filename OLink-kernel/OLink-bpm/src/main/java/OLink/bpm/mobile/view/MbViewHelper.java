package OLink.bpm.mobile.view;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.core.dynaform.activity.ejb.Activity;
import OLink.bpm.core.dynaform.activity.ejb.ActivityType;
import OLink.bpm.core.dynaform.document.ejb.Document;
import OLink.bpm.core.dynaform.form.ejb.ValidateMessage;
import OLink.bpm.core.dynaform.view.ejb.Column;
import OLink.bpm.core.dynaform.view.ejb.View;
import OLink.bpm.core.macro.runner.IRunner;
import OLink.bpm.core.macro.runner.JavaScriptFactory;
import OLink.bpm.core.privilege.res.ejb.ResVO;
import OLink.bpm.core.resource.ejb.ResourceType;
import OLink.bpm.core.table.constants.MobileConstant;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.util.HtmlEncoder;
import OLink.bpm.util.StringUtil;
import org.apache.log4j.Logger;

public class MbViewHelper {

	private static final Logger log = Logger.getLogger(MbViewHelper.class);
	
	private String application;
	
	public MbViewHelper(String application) {
		this.application = application;
	}
	
	public String toViewListMobileXml(boolean isDialogView, View view, Document parent, DataPackage<Document> datas, WebUser user, HttpServletRequest request, ParamsTable params) throws Exception {
		HttpSession session = request.getSession();
		String _viewid = view.getId();
		
		Collection<ValidateMessage> errors = new ArrayList<ValidateMessage>();
		String refresh = params.getParameterAsString("refresh");
		boolean isRefresh = refresh != null && refresh.trim().equals("true");
		IRunner jsrun = JavaScriptFactory.getInstance(session.getId(), getApplication());

		boolean isEdit = true;
		String editString = params.getParameterAsString("isedit");
		isEdit = editString == null ? true : Boolean.parseBoolean(editString);
		
		Collection<Column> columns = view.getColumns();
		
		StringBuffer buffer = new StringBuffer();
		
		if (isRefresh) {
			buffer.append(getHiddenFieldXml("refresh", "true"));
		}

		buffer.append(getHiddenFieldXml("_viewid", _viewid));
		buffer.append(getHiddenFieldXml("application", getApplication()));
		
		if (isDialogView) {
			String mapStr = params.getParameterAsString("_mapStr");
			buffer.append(getHiddenFieldXml("_mapStr", mapStr));
		}
		if (parent != null && parent.getId() != null) {
			buffer.append(getHiddenFieldXml("parentid", parent.getId()));
		}
		String isRelate = params.getParameterAsString("isRelate");
		if (isRelate != null && isRelate.trim().equals("true")) {
			buffer.append(getHiddenFieldXml("isRelate", "true"));
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
			String hiddenScript = act.getHiddenScript();
			if (!StringUtil.isBlank(hiddenScript)) {
				StringBuffer label = new StringBuffer();
				label.append("View").append("." + view.getName());
				label.append(".Activity(").append(act.getId());
				label.append(act.getName() + ")").append(".runHiddenScript");
				Object result = runner.run(label.toString(), act.getHiddenScript());// Run
				if (result instanceof Boolean) {
					isHidden = ((Boolean) result).booleanValue();
				}
			}

			boolean isStateToHidden = false;
			if (parent != null) {
				isStateToHidden = act.isStateToHidden(parent);
			}

			flag = (isHidden || isStateToHidden);
			if (flag) continue;
			
			if (act.getType() == ActivityType.EXPTOEXCEL) continue;
			
			if (!act.isHidden(runner, view, tdoc, user, ResVO.VIEW_TYPE)
					&& isEdit) {
				buffer.append("<").append(MobileConstant.TAG_ACTION).append(" ");
				buffer.append(MobileConstant.ATT_TYPE).append("='");
				buffer.append(act.getType());
				buffer.append("' ").append(MobileConstant.ATT_NAME);
				buffer.append("='{*[" + HtmlEncoder.encode(act.getName()) + "]*}' ");
				buffer.append("").append(MobileConstant.ATT_ID).append("='");
				buffer.append(act.getId());
				buffer.append("'>");
				buffer.append("</").append(MobileConstant.TAG_ACTION).append(">");
			}
		}
		if (isDialogView) {
			buffer.append("<").append(MobileConstant.TAG_ACTION).append(" ");
			buffer.append(MobileConstant.ATT_TYPE).append("='link' ");
			buffer.append(MobileConstant.ATT_NAME).append("='{*[OK]*}' ");
			buffer.append(">");
			buffer.append("</").append(MobileConstant.TAG_ACTION).append(">");
		}

		buffer.append("<").append(MobileConstant.TAG_TH).append(">");

		if (view != null) {
			if (view.getSearchForm() != null) {
				buffer.append("<").append(MobileConstant.TAG_ACTION).append(" ");
				buffer.append(MobileConstant.ATT_TYPE).append("='23'");
				buffer.append(" ").append(MobileConstant.ATT_NAME).append("='{*[Search]*}' ");
				buffer.append(MobileConstant.ATT_ID).append("=''>");
				buffer.append("</").append(MobileConstant.TAG_ACTION).append(">");
			}
			Collection<Column> col = view.getColumns();
			if (col != null) {
				Iterator<Column> its = col.iterator();
				while (its.hasNext()) {
					Column column = its.next();
					buffer.append("<").append(MobileConstant.TAG_TD).append(" ");
					buffer.append(MobileConstant.ATT_NAME).append(" = '' ");
					buffer.append(MobileConstant.ATT_WIDTH).append(" = '" + column.getWidth() + "'>");
					buffer.append(HtmlEncoder.encode(column.getName()));
					buffer.append("</").append(MobileConstant.TAG_TD).append(">");
				}
			}
		}
		buffer.append("</").append(MobileConstant.TAG_TH).append(">");

		if (datas == null) return buffer.toString();
		
		buffer.append(getPageActionXml(view, parent, datas));
		
		Collection<Document> col = datas.getDatas();
		if (col != null) {
			Iterator<Document> its = col.iterator();
			while (its.hasNext()) {
				Document doc = its.next();

				jsrun.initBSFManager(doc, params, user, errors);
				if (doc == null || doc.getId() == null) continue;
				Iterator<Column> iter = columns.iterator();
				try {
					buffer.append("<").append(MobileConstant.TAG_TR).append(" ");
					buffer.append(MobileConstant.ATT_ID).append(" = '" + doc.getId() + "'>");
					while (iter.hasNext()) {
						buffer.append("<").append(MobileConstant.TAG_TD).append(" ");
						Column column = iter.next();
						Object result = null;
						if (column.getType() != null && column.getType().equals(Column.COLUMN_TYPE_SCRIPT)) {
							StringBuffer label = new StringBuffer();
							label.append("DisplayView.").append(view.getId()).append(".Column.");
							label.append(column.getId()).append("runValueScript");
							result = jsrun.run(label.toString(), column.getValueScript());
						} else if (column.getType() != null && column.getType().equals(Column.COLUMN_TYPE_FIELD)) {
							result = doc.getValueByField(column.getFieldName());
						}
						if (column != null && column.getId() != null) {
							buffer.append(" ").append(MobileConstant.ATT_NAME).append(" = '" + HtmlEncoder.encode(column.getId()) + "' >");
						} else {
							buffer.append(" ").append(MobileConstant.ATT_NAME).append(" ='' >");
						}

						// DO NOT display null
						if (result == null) result = "";

						buffer.append(HtmlEncoder.encode((String) result));
						buffer.append("</").append(MobileConstant.TAG_TD).append(">");
					}

					buffer.append("</").append(MobileConstant.TAG_TR).append(">");
				} catch (Exception e) {
					log.warn(e.toString());
					throw e;
				}
			}
		}
		
		return buffer.toString();
	}

	public String getApplication() {
		return application;
	}

	public void setApplication(String application) {
		this.application = application;
	}
	
	public String getHiddenFieldXml(String name, String value) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("<").append(MobileConstant.TAG_HIDDENFIELD).append(" ");
		buffer.append(MobileConstant.ATT_NAME).append("='").append(name);
		buffer.append("'>").append(value);
		buffer.append("</").append(MobileConstant.TAG_HIDDENFIELD).append(">");
		return buffer.toString();
	}
	
	public String getParameterXml(String name, String value) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("<").append(MobileConstant.TAG_PARAMETER).append(" ");
		buffer.append(MobileConstant.ATT_NAME).append(" ='").append(name + "'>");
		buffer.append(HtmlEncoder.encode(value));
		buffer.append("</").append(MobileConstant.TAG_PARAMETER).append(">");
		return buffer.toString();
	}
	
	private String getPageActionXml(View view, Document parent, DataPackage<Document> datas) {
		StringBuffer buffer = new StringBuffer();
		int pages = datas.rowCount / datas.linesPerPage;
		
		if (datas.rowCount % datas.linesPerPage > 0) pages++;
		
		if (datas.pageNo < pages || datas.pageNo > 1) {
			buffer.append("<").append(MobileConstant.TAG_ACTION).append(" ");
			buffer.append(MobileConstant.ATT_NAME).append("='{*[Pagination]*}'>");
		}
		if (datas.pageNo < (pages)) {
			buffer.append("<").append(MobileConstant.TAG_ACTION).append(" ");
			buffer.append(MobileConstant.ATT_TYPE).append("='" + ResourceType.RESOURCE_TYPE_MOBILE + "'");
			buffer.append(" ").append(MobileConstant.ATT_NAME).append("='{*[NextPage]*}'>");
			buffer.append("<").append(MobileConstant.TAG_PARAMETER).append(" ");
			buffer.append(MobileConstant.ATT_NAME).append("='_viewid'>" + HtmlEncoder.encode(view.getId()) + "</").append(MobileConstant.TAG_PARAMETER).append(">");
			if (parent != null && !StringUtil.isBlank(parent.getId())) {
				buffer.append(getParameterXml("parentid", parent.getId()));
			}
			buffer.append(getParameterXml("_currpage", String.valueOf(datas.pageNo + 1)));
			buffer.append("</").append(MobileConstant.TAG_ACTION).append(">");
			///////////////////////////////////////
			buffer.append("<").append(MobileConstant.TAG_ACTION).append(" ");
			buffer.append(MobileConstant.ATT_TYPE).append("='" + ResourceType.RESOURCE_TYPE_MOBILE + "'");
			buffer.append(" ").append(MobileConstant.ATT_NAME).append("='{*[EndPage]*}'>");
			buffer.append(getParameterXml("_viewid", view.getId()));
			if (parent != null && !StringUtil.isBlank(parent.getId())) {
				buffer.append(getParameterXml("parentid", parent.getId()));
			}
			buffer.append(getParameterXml("_currpage", String.valueOf(pages)));
			buffer.append("</").append(MobileConstant.TAG_ACTION).append(">");
		}
		if (datas.pageNo > 1) {
			buffer.append("<").append(MobileConstant.TAG_ACTION).append(" ");
			buffer.append(MobileConstant.ATT_TYPE).append("='" + ResourceType.RESOURCE_TYPE_MOBILE + "'");
			buffer.append(" ").append(MobileConstant.ATT_NAME).append("='{*[FirstPage]*}'>");
			buffer.append(getParameterXml("_viewid", view.getId()));
			if (parent != null && !StringUtil.isBlank(parent.getId())) {
				buffer.append(getParameterXml("parentid", parent.getId()));
			}
			buffer.append(getParameterXml("_currpage", String.valueOf(1)));
			buffer.append("</").append(MobileConstant.TAG_ACTION).append(">");
			/////////////////////////////////////////////
			buffer.append("<").append(MobileConstant.TAG_ACTION).append(" ");
			buffer.append(MobileConstant.ATT_TYPE).append("='" + ResourceType.RESOURCE_TYPE_MOBILE + "'");
			buffer.append(" ").append(MobileConstant.ATT_NAME).append("='{*[PrevPage]*}'>");
			buffer.append(getParameterXml("_viewid", view.getId()));
			if (parent != null && !StringUtil.isBlank(parent.getId())) {
				buffer.append(getParameterXml("parentid", parent.getId()));
			}
			buffer.append(getParameterXml("_currpage", String.valueOf(datas.pageNo - 1)));
			buffer.append("</").append(MobileConstant.TAG_ACTION).append(">");
		}
		if (datas.pageNo < pages || datas.pageNo > 1) {
			buffer.append("</").append(MobileConstant.TAG_ACTION).append(">");
		}
		return buffer.toString();
	}
	
}
