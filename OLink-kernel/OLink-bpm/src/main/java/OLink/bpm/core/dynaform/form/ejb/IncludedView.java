package OLink.bpm.core.dynaform.form.ejb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.base.dao.HibernateSQLUtils;
import OLink.bpm.base.ejb.IDesignTimeProcess;
import OLink.bpm.core.dynaform.view.ejb.Column;
import OLink.bpm.core.dynaform.view.ejb.View;
import OLink.bpm.core.macro.runner.AbstractRunner;
import OLink.bpm.core.macro.runner.IRunner;
import OLink.bpm.core.resource.ejb.ResourceType;
import OLink.bpm.util.*;
import OLink.bpm.core.dynaform.document.ejb.Document;
import OLink.bpm.core.dynaform.document.ejb.DocumentProcess;
import OLink.bpm.core.dynaform.document.ejb.DocumentProcessBean;
import OLink.bpm.core.dynaform.view.ejb.ViewProcess;
import OLink.bpm.core.table.constants.MobileConstant;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.util.Debug;
import OLink.bpm.util.HtmlEncoder;
import OLink.bpm.util.ProcessFactory;

/**
 * 引入视图组件
 * 
 * @author nicholas
 */
public class IncludedView extends IncludedElement {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1517041188945769061L;

	public View view;

	public IncludedView(IncludeField field) {
		super(field);
	}

	public String toHtmlTxt(Document doc, IRunner runner, WebUser webUser) throws Exception {
		String html = "";
		try {

			view = (View) getValueObject(runner);

			// if (view != null && field.is()) {
			// html = toIntegrateHtml(doc, runner);
			if (view != null) {
				html = toFrameHtml(doc, runner, webUser);
			}

			// "isEnabled=ture" means that hidden the view when data not exit
			if (field.isEnabled()) {
				if (isEmpty(doc, runner)) {
					html = "";
				}
			}
		} catch (Exception e) {
			Debug.println(e.getMessage());
			e.printStackTrace();
		}

		return html;
	}

	private String toFrameHtml(Document doc, IRunner runner, WebUser webUser) throws Exception {
		return toFrameXml(doc, runner, webUser, "HTML");
	}

	private String toFrameXml(Document doc, IRunner runner, WebUser webUser, String type) throws Exception {
		StringBuffer html = new StringBuffer();
		if (type.equals("HTML")) {
			html.append("<iframe id='" + view.getId()
					+ "' name='display_view' width='100%' frameborder='0' ");//height='325px'
			html.append(" src='");
			if(WebUser.TYPE_BG_USER.equals(webUser.getType()))//如果是后台用户(预览)
				html.append(field.getContextPath(doc) + "/core/dynaform/view/");
			else//前台用户
				html.append(field.getContextPath(doc) + "/portal/dynaform/view/");
			// 根据视图打开类型选择action
			html.append("displayView.action");
			
			html.append("?application=" + view.getApplicationid());
			html.append("&_viewid=" + view.getId());
			html.append("&_opentype=" + view.getOpenType());
			html.append("&parentid=");
			html.append(!StringUtil.isBlank(doc.getId()) ? doc.getId() : "@");
			html.append("&isRelate=").append(field.isRelate()); // 是否有父子关系
			html.append("&divid=");
			html.append(field.getName() + "_divid");
			if (field.getEditMode(runner, doc, webUser).equals("false"))
				html.append("&isedit=false");
			if (field.isRefreshOnChanged()) {
				html.append("&refreshparent=true");
			}
			html.append("' ");

			if (field.isRefreshOnChanged()) {
				html.append(" onload='dy_refresh(\"" + field.getName() + "\")'");
			}
			html.append("></iframe>");
		} else if (type.equals("XML")) {
			html.append("<").append(MobileConstant.TAG_ACTION).append(" ").append(MobileConstant.ATT_TYPE).append(
					"='" + ResourceType.RESOURCE_TYPE_MOBILE + "'>");
			html.append("<").append(MobileConstant.TAG_PARAMETER).append(" ").append(MobileConstant.ATT_NAME).append(
					"='_viewid'>" + HtmlEncoder.encode(view.getId()) + "</").append(MobileConstant.TAG_PARAMETER)
					.append(">");

			if (doc.getId() != null && doc.getId().trim().length() > 0) {
				html.append("<").append(MobileConstant.TAG_PARAMETER).append(" ").append(MobileConstant.ATT_NAME)
						.append("='");
				if (field.isRelate()) { // 是否有父子关系
					html.append("isRelate'>" + field.isRelate() + "</").append(MobileConstant.TAG_PARAMETER).append(
					">");
					html.append("<").append(MobileConstant.TAG_PARAMETER).append(" ").append(MobileConstant.ATT_NAME)
					.append("='");
					html.append("parentid");
				} else {
					html.append("parentid");
				}
				html.append("'>" + HtmlEncoder.encode(doc.getId()) + "</").append(MobileConstant.TAG_PARAMETER).append(
						">");
			}
			if (field.isRefreshOnChanged()) {
				html.append("<").append(MobileConstant.TAG_PARAMETER).append(" ").append(MobileConstant.ATT_NAME)
						.append("='refresh'>true</").append(MobileConstant.TAG_PARAMETER).append(">");
			}
			html.append("</").append(MobileConstant.TAG_ACTION).append(">");
		}
		return html.toString();
	}

	private String toIntegrateHtml(Document doc, IRunner runner, WebUser webUser, int pagelines) throws Exception {
		StringBuffer html = new StringBuffer();
		StringBuffer labelBuffer = new StringBuffer();
		labelBuffer.append(field.getScriptLable("toHtmlTxt"));

		if (view != null) {
			html.append("<table width='100%' class='display_view-table' pageid='" + field.getId() + "'>");
			html.append("<tr class='ptable-header'>");

			appendHeadTD(html, runner);///////////

			html.append("</tr>");

			try {
				DataPackage<Document> datas = getDatas(doc, webUser, pagelines);
				if (datas != null) {
					if (datas.datas == null || datas.datas.size() == 0) {
						return "";
					} else {
						Collection<ValidateMessage> errors = new ArrayList<ValidateMessage>();
						for (Iterator<Document> iter = datas.datas.iterator(); iter.hasNext();) {
							Document data = iter.next();
							// 内部注册
							runner.initBSFManager(data, doc.get_params(), webUser, errors);

							html.append("<tr class=\"table-tr\">");
							appendDataTD(html, labelBuffer, data, runner, webUser);//////////
							html.append("</tr>");
						}
					}
				}
				html.append("</table>");
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			} finally {
				// 执行完内部注册后，重新注册外部文档
				runner.initBSFManager(doc, doc.get_params(), webUser, new ArrayList<ValidateMessage>());
			}
		}
		return html.toString();
	}

	private DataPackage<Document> getDatas(Document doc, WebUser user, int pagelines) throws Exception {
		DocumentProcess dp = new DocumentProcessBean(view.getApplicationid());
		ViewProcess viewProcess = (ViewProcess) ProcessFactory.createProcess(ViewProcess.class);

		DataPackage<Document> datas = null;
		String parentid = (String) field.getDocParameter(doc, "_docid");
		boolean isRelate = field.isRelate();

		if (StringUtil.isBlank(parentid)) {
			parentid = doc.getId();
		}

		ParamsTable params = doc.get_params();
		String[] fields = view.getOrderFieldAndOrderTypeArr();
		params.setParameter("_sortCol", fields);
		HibernateSQLUtils sqlUtils = new HibernateSQLUtils();
		if (view.getEditMode().equals(View.EDIT_MODE_DESIGN)) {
			String sql = viewProcess.getQueryString(view, params, user, doc);
			if (!StringUtil.isBlank(parentid) && isRelate) {
				sql = sqlUtils.appendCondition(sql, "PARENT = '" + parentid + "'");
			}
			datas = dp.queryBySQLPage(sql, params, 1, pagelines, doc.getDomainid());
		} else if (view.getEditMode().equals(View.EDIT_MODE_CODE_DQL)) {
			String dql = viewProcess.getQueryString(view, params, user, doc);
			if (!StringUtil.isBlank(parentid) && isRelate) {
				dql += " and $parent.$id = '" + parentid + "'";
			}
			datas = dp.queryByDQLPage(dql, params, 1, pagelines, doc.getDomainid());

		} else if (view.getEditMode().equals(View.EDIT_MODE_CODE_SQL)) {
			String sql = viewProcess.getQueryString(view, params, user, doc);
			if (!StringUtil.isBlank(parentid) && isRelate) {
				sql = sqlUtils.appendCondition(sql, "PARENT = '" + parentid + "'");
			}
			datas = dp.queryBySQLPage(sql, params, 1, pagelines, doc.getDomainid());
		}

		return datas;
	}

	private void appendHeadTD(StringBuffer html, IRunner runner) {
		Collection<Column> columns = view.getColumns();
		for (Iterator<Column> iter = columns.iterator(); iter.hasNext();) {
			Column clm = iter.next();
			if(!clm.isHiddenColumn(runner)){
				html.append("<td class='column-head'").append(
						clm.getWidth() != null && clm.getWidth().trim().length() > 0 ? " width='" + clm.getWidth() + "'"
								: "");
				html.append(">");
				html.append(clm.getName());
				html.append("</td>");
			}
		}
	}

	private void appendDataTD(StringBuffer html, StringBuffer label, Document doc, IRunner runner, WebUser webUser)
			throws Exception {
		Collection<Column> columns = view.getColumns();
		for (Iterator<Column> iter2 = columns.iterator(); iter2.hasNext();) {
			Column col = iter2.next();
			if(!col.isHiddenColumn(runner)){
				html.append("<td class='column-td'>");
				html.append(col.getText(doc, runner, webUser));
				html.append("</td>");
			}
		}
	}

	/**
	 * 
	 * Form模版的includeField组件内容结合Document中的ITEM存放的值,返回字符串为重定义后的打印html文本.
	 * 
	 * @see IncludeField#toHtmlTxt(ParamsTable,
	 *      WebUser, AbstractRunner)
	 * @param params
	 *            参数
	 * @param user
	 *            webuser
	 * @param runner
	 *            AbstractRunner(执行脚本的接口类)
	 * @see AbstractRunner#run(String, String)
	 * @param doc
	 *            Document
	 * @return Form模版的includeField组件内容结合Document中的ITEM存放的值为重定义后的打印html
	 * @throws Exception
	 */
	public String toPrintHtmlTxt(Document doc, IRunner runner, WebUser webUser) throws Exception {
		StringBuffer html = new StringBuffer();
		view = (View) getValueObject(runner);

		if (view != null) {
			html.append("<link rel=\"stylesheet\"");
			html.append(" href=\"" + field.getContextPath(doc) + "/resource/css/style.jsp?styleid=" + getStyleId(view)
					+ "\"/>");
			html.append(toIntegrateHtml(doc, runner, webUser, Integer.MAX_VALUE));
		}

		return html.toString();
	}

	public String getStyleId(View view) {
		if (view.getStyle() != null) {
			return view.getStyle().getId();
		}
		return "";
	}

	public boolean isEmpty(Document doc, IRunner runner) throws Exception {
		DocumentProcess dp = new DocumentProcessBean(doc.getApplicationid());
		String filterScript = view.getFilterScript();

		StringBuffer label = new StringBuffer();
		label.append("View(").append(view.getId()).append(")." + view.getName()).append(".filterScript");

		Object result = runner.run(label.toString(), filterScript);
		if (result != null && result instanceof String) {
			String dql = (String) result;
			String parentid = (String) field.getDocParameter(doc, "_docid");

			if (parentid != null && parentid.trim().length() > 0 && field.isRelate()) {
				dql = "(" + dql + ") and ($parent.$id='" + parentid + "')";
			}

			DataPackage<?> datas = dp.queryByDQL(dql, doc.getDomainid());

			return datas.datas.isEmpty();
		}

		return false;
	}

	/**
	 * @SuppressWarnings 工厂方法不支持泛型
	 */
	@SuppressWarnings("unchecked")
	public IDesignTimeProcess getProcess() throws Exception {
		return ProcessFactory.createProcess(ViewProcess.class);
	}

	public String toXMLTxt(Document doc, IRunner runner, WebUser webUser) throws Exception {
		String html = "";
		try {

			view = (View) getValueObject(runner);
			html = toFrameXml(doc, runner, webUser, "XML");
			if (field.isEnabled()) {
				if (isEmpty(doc, runner)) {
					html = "";
				}
			}
		} catch (Exception e) {
			Debug.println(e.getMessage());
			e.printStackTrace();
		}

		return html;
	}
}
