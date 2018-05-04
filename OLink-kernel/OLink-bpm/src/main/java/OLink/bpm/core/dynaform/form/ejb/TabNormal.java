package OLink.bpm.core.dynaform.form.ejb;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import OLink.bpm.core.macro.runner.AbstractRunner;
import OLink.bpm.core.macro.runner.IRunner;
import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.core.dynaform.document.ejb.Document;
import OLink.bpm.core.table.constants.MobileConstant;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.util.HtmlEncoder;
import OLink.bpm.util.StringUtil;
import OLink.bpm.util.json.JsonUtil;
import eWAP.core.ResourcePool;

public class TabNormal implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6274054997243800672L;

	protected final static String TAB_CONTENT = "content_";

	protected final static String TAB_HREF = "href_";

	protected final static String TAB_LI = "li_";

	protected final static String IS_LOADED = "isloaded";

	protected TabField field;

	public TabNormal(TabField field) {
		this.field = field;
	}

	public String getName() {
		return "tab_" + field.getId();
	}

	public boolean isCalculateOnRefresh() {
		return true;
	}

	public ValidateMessage validate(IRunner runner, Document doc) throws Exception {
		return null;
	}

	/**
	 * 获取模板描述页签
	 * 
	 * @return 模板描述页签
	 * @roseuid 41E7917A033F
	 */
	public String toTemplate() {
		StringBuffer template = new StringBuffer();
		template.append("<IMG id=" + field.getId() + "");
		template.append("src=\""+ResourcePool.getRootpath()+"/core/dynaform/form/formeditor/buttonimage/v1/tag.gif\"");
		template.append("className=\"TabField\"");
		template.append("type=\"tabfield\"");
		String[] exculdes = new String[] { "form", "fields" };
		template.append("relStr= \"" + JsonUtil.collection2Json(getTabs(), exculdes) + "\"");
		template.append("\\>");
		return template.toString();
	}

	/**
	 * 
	 * Form模版的TagField(页签)内容结合Document中的ITEM存放的值,返回重定义后的html，
	 * 
	 * @param doc
	 *            文档对象
	 * @see ParamsTable#params
	 * @see AbstractRunner#run(String, String)
	 * @return 重定义后的html为Form模版的TabField(页签)内容结合Document中的ITEM存放的值,
	 * 
	 */
	public String toHtmlTxt(Document doc, IRunner runner, WebUser webUser) throws Exception {
		StringBuffer html = new StringBuffer();
		Tab selectedTab = getSelectedTab(runner);

		appendTabsHtml(html, runner);
		appendTabContentsHtml(html, doc, runner, webUser, selectedTab);
		html.append(addScript(selectedTab));

		return html.toString();
	}

	/**
	 * 获取tab组件的集合
	 * 
	 * @return tab组件的集合
	 * @uml.property name="tabs"
	 */
	public Collection<Tab> getTabs() {
		return field.getTabs();
	}

	/**
	 * 获取tab组件的集合,设置tab点击事件并输出的页面
	 * 
	 * @param html
	 *            字符串
	 * @param tabs
	 *            tab组件的集合
	 * @param runner
	 *            AbstractRunner(执行脚本的接口类)
	 * @return
	 * @throws Exception
	 */
	public void appendTabsHtml(StringBuffer html, IRunner runner) throws Exception {
		// <DIV>
		html.append("<DIV id=\"" + field.getId() + "\" class=\"basictab\">");
		// <ul>
		html.append("<ul>");
		for (Iterator<Tab> iterator = getTabs().iterator(); iterator.hasNext();) {
			Tab tab = iterator.next();
			// <li>
			html.append("<li id='").append(TAB_LI).append(tab.getFormId()).append("'");
			if (tab.isHidden(this, runner)) {
				html.append(" style='display:none'");
			}
			html.append(">");
			// <a>
			html.append("<a  style='cursor:pointer'");
			if (tab.isRefreshOnChanged()) {
				html.append(" callback='dy_refresh(\"" + tab.getFormId() + "\")'");
			}
			html.append(" id='").append(tab.getFormId()).append("'");
			html.append(" rel='").append(TAB_CONTENT).append(tab.getFormId()).append("'");
			html.append(" title='" + tab.getName() + "'");
			html.append(">");
			html.append(tab.getName());
			html.append("</a>");
			// </a>
			html.append("</li>");
			// </li>
		}
		// </ul>
		html.append("</ul>");
		// </DIV>
		html.append("</DIV>");
	}

	/**
	 * 根据名称获取Tab
	 * 
	 * @param tabName
	 *            页签名称
	 * @return
	 */
	protected Tab getTabByName(String tabName) {
		for (Iterator<Tab> iterator = getTabs().iterator(); iterator.hasNext();) {
			Tab tab = iterator.next();
			if (tab.getName().equals(tabName)) {
				return tab;
			}
		}
		return null;
	}

	/**
	 * 获取选中的页签
	 * 
	 * @param runner
	 * @return
	 * @throws Exception
	 */
	public Tab getSelectedTab(IRunner runner) throws Exception {
		Object result = runner.run(field.getScriptLable("SelectedScript"), StringUtil.dencodeHTML(field.getSelectedScript()));
		if (result != null && result instanceof String) {
			String tabName = (String) result;
			Tab tab = getTabByName(tabName);
			if (!tab.isHidden(this, runner)) {
				return tab;
			}
		}

		return getFirstShowTab(runner);
	}

	/**
	 * 获取第一个显示的页签
	 * 
	 * @param runner
	 * @return
	 * @throws Exception
	 */
	protected Tab getFirstShowTab(IRunner runner) throws Exception {
		for (Iterator<Tab> iterator = getTabs().iterator(); iterator.hasNext();) {
			Tab tab = iterator.next();
			if (!tab.isHidden(this, runner)) {
				return tab;
			}
		}

		return null;
	}

	/**
	 * 初始化TAB的内容,除第一个选中的Tab外，其他都默认隐藏 每点击一个tab自动刷新文档内容
	 * 
	 * @param html
	 *            html 字符串
	 * @param tabs
	 *            tab组件的集合
	 * @param doc
	 *            document(文档对象)
	 * @param runner
	 *            AbstractRunner(执行脚本的接口类)
	 * @param webUser
	 *            webUser
	 * @param selectedIndex
	 *            初始化显示tab
	 */
	public void appendTabContentsHtml(StringBuffer html, Document doc, IRunner runner, WebUser webUser, Tab selectedTab) {
		html.append("<DIV class=\"tabcontainer\" id='tabcontainer'>");
		try {
			for (Iterator<Tab> iterator = getTabs().iterator(); iterator.hasNext();) {
				Tab tab = iterator.next();
				html.append("<div id='").append(TAB_CONTENT).append(tab.getFormId()).append("'");
				html.append(" class=\"tabcontent\"");
				if (tab.isHidden(this, runner)) {
					html.append(" style='display:none'");
				}
				html.append(">");
				// 只加载选中的Tab
				if (selectedTab != null && tab.getFormId().equals(selectedTab.getFormId())) {
					if (!field.get_form().equals(tab.getForm())) {
						html.append(tab.toHtml(doc, runner, webUser, false));
					}
				} else {
					html.append(tab.toHtml(doc, runner, webUser, true));
				}
				html.append("</div>");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		html.append("</DIV>");
	}

	public void appendTabContentsPrintHtml(StringBuffer html, IRunner runner, Collection<Tab> tabs, Document doc, WebUser webUser) {
		int index = 0;
		try {
			html.append("<DIV>");
			for (Iterator<Tab> iterator = tabs.iterator(); iterator.hasNext();) {
				Tab tab = iterator.next();
				StringBuffer suffix = new StringBuffer();
				suffix.append("Tab.").append(tab.getName());
				suffix.append(".hiddenPrintScript");

				if (field.runBooleanScript(runner, suffix.toString(), tab.getHiddenPrintScript())) {
					continue;
				}
				html.append("<div id=\"" + field.getId() + index + "\">");
				// 防止死循环,避免Tab Field包含了所在的Form而造成死循环
				if (!field.get_form().getId().equals(tab.getFormId())) {
					html.append(tab.toPrintHtml(doc, runner, webUser));
				}
				html.append("</div>");
				index++;
			}

			html.append("</DIV>");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 添加script
	 * 
	 * @return 字符串的内容为重定的script
	 */
	private String addScript(Tab selectedTab) {
		StringBuffer script = new StringBuffer();
		script.append("<script language='JavaScript'>");
		String tabId = selectedTab != null ? selectedTab.getFormId() : "";
		script.append("ddtabmenu.definemenu(\"" + field.getId() + "\", '" + tabId + "');");
		script.append("</script>");

		return script.toString();
	}

	/**
	 * 获取所有页签表单
	 * 
	 * @return 页签表单
	 * @throws Exception
	 */
	public Collection<Form> getForms() throws Exception {
		Collection<Form> rtn = new ArrayList<Form>();
		for (Iterator<Tab> iterator = getTabs().iterator(); iterator.hasNext();) {
			Tab tab = iterator.next();
			Form form = tab.getForm();
			if (form != null) {
				rtn.add(form);
			}
		}
		return rtn;
	}

	/**
	 * 根据打印时对应TabField的显示类型不同,默认为MODIFY,返回的结果字符串不同.
	 * 若Document不为空且打印时对应TabField的显示类型不为HIDDEN,
	 * <p>
	 * 并根据Form模版的TabField组件内容结合Document中的ITEM存放的值,返回重定义后的打印html文本
	 * 通过强化HTML标签及语法，表达TabField的布局、属性、事件、样式、等。
	 * 
	 * @param doc
	 *            Document(文档对象)
	 * @param runner
	 *            动态脚本执行器
	 * @param params
	 *            参数
	 * @param user
	 *            webuser
	 * @SuppressWarnings JsonUtil.toCollection 方法返回对象集类型不定
	 * @see AbstractRunner#run(String, String)
	 * @return 打印重定义后的打印html为Form模版的文本框组件内容结合Document中的ITEM存放的值
	 * @throws Exception
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public String toPrintHtmlTxt(Document doc, IRunner runner, WebUser webUser) throws Exception {
		StringBuffer html = new StringBuffer();

		try {
			Collection<Tab> tabs = (Collection) JsonUtil.toCollection(field.getRelStr(), Tab.class);
			html.append("<DIV id=\"" + field.getId() + "\">");
			html.append("</DIV>");
			appendTabContentsPrintHtml(html, runner, tabs, doc, webUser);
			return html.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 获取页签刷新脚本
	 * 
	 * @param runner
	 *            动态脚本执行器
	 * @param doc
	 *            文档对象
	 * @param webUser
	 *            webUser
	 * @return 页签刷新脚本
	 */
	public String getRefreshScript(IRunner runner, Document doc, WebUser webUser) throws Exception {
		StringBuffer buffer = new StringBuffer();

		// 参数中获取选中的tabId
		String selectedId = doc.get_params().getParameterAsString(getName());
		boolean isloaded = doc.get_params().getParameterAsBoolean(getName() + "_" + IS_LOADED);

		boolean isOneSelected = false; // 有一个tab被选中
		Tab firstShowTab = null; // 第一个显示的tab

		Collection<Object> hiddenList = new ArrayList<Object>();
		Collection<Object> showList = new ArrayList<Object>();
		for (Iterator<Tab> iterator = getTabs().iterator(); iterator.hasNext();) {
			Tab tab = iterator.next();
			boolean isHidden = false;
			if (isCalculateOnRefresh()) {
				isHidden = tab.isHidden(this, runner);

				if (isHidden) {
					hiddenList.add("'" + tab.getFormId() + "'");
				} else {
					showList.add("'" + tab.getFormId() + "'");

					if (firstShowTab == null) {
						firstShowTab = tab;
					}

					if (!isloaded) { // 设置为已加载 (控制全刷新、或按需刷新)
						buffer.append("document.getElementById('");
						buffer.append(TAB_CONTENT).append(tab.getFormId()).append("').isloaded = true;\n;");
					}

					if (tab.getFormId().equals(selectedId)) {// 刷新选中的Tab
						isOneSelected = true;

						// 已加载仍需要重计算
						Collection<FormField> fields = tab.getFields();
						for (Iterator<FormField> iter = fields.iterator(); iter.hasNext();) {
							FormField field = iter.next();
							// 重新刷新字段显示脚本
							if (field instanceof TabField) {
								buffer.append(field.getRefreshScript(runner, doc, webUser));
							} else {
								buffer.append(field.getRefreshScript(runner, doc, webUser, isHidden));
							}
						}
					}
				}
			}
		}

		// 如果当前选中的tab已隐藏，则显示第一个不隐藏的tab
		if (!isOneSelected && firstShowTab != null) {
			doc.get_params().setParameter(getName(), firstShowTab.getFormId());
			return getRefreshScript(runner, doc, webUser);
		}

		// 显示隐藏Tab脚本
		if (!showList.isEmpty()) {
			buffer.append("ddtabmenu.showMenus('").append(field.getId()).append("', " + showList + ");\n");
		}

		// 选中页签脚本
		if (!StringUtil.isBlank(selectedId)) {
			buffer.append("ddtabmenu.showsubmenuById('").append(field.getId()).append("', '");
			buffer.append(selectedId);
			buffer.append("');\n");
		}

		if (!hiddenList.isEmpty()) {
			buffer.append("ddtabmenu.hideMenus('").append(field.getId()).append("', " + hiddenList + ");\n");
		}

		return buffer.toString();
	}

	public boolean isRender(String destVal, String origVal) {
		return true;
	}

	/**
	 * 用于为手机平台XML串生成
	 * 
	 * @param runner
	 *            动态脚本执行器
	 * @param doc
	 *            文档对象
	 * @param webUser
	 *            webUser
	 * @return 手机平台XML串生成
	 */
	public String toMbXMLText(Document doc, IRunner runner, WebUser webUser) throws Exception {
		StringBuffer html = new StringBuffer();
		int index = 0;
		html.append("<" + MobileConstant.TAG_TAB + ">");
		//Collection tabs = getTabs();
		for (Iterator<Tab> iterator = getTabs().iterator(); iterator.hasNext();) {
			Tab tab = iterator.next();
			StringBuffer suffix = new StringBuffer();
			suffix.append("Tab.").append(tab.getName());
			suffix.append("(").append(tab.getFormId()).append(")");
			suffix.append(".hiddenPrintScript");
			//String temp = tab.getHiddenPrintScript();
			boolean isHidden = field.runBooleanScript(runner, suffix.toString(), tab.getHiddenPrintScript());
			String tabXml = tab.toMbXML(doc, runner, webUser, isHidden);
			if (!isHidden && tabXml != null && tabXml.trim().length() > 0) {
				html.append("<" + MobileConstant.TAG_TABLI);
				if (field.isRefreshOnChanged()) {
					html.append(" " + MobileConstant.ATT_REFRESH + "='true' ");
				}
				html.append(" " + MobileConstant.ATT_NAME + " ='");
				html.append(HtmlEncoder.encode(tab.getName()));
				html.append("' >");
				html.append(tabXml);
				html.append("</" + MobileConstant.TAG_TABLI + ">");
			}
			index++;
		}
		html.append("</" + MobileConstant.TAG_TAB + ">");
		index = 0;
		return html.toString();
	}

	public String getValueMapScript() {
		StringBuffer scriptBuffer = new StringBuffer();
		scriptBuffer.append("valuesMap['" + field.getName() + "'] = ddtabmenu.getSelected('" + field.getId() + "');");
		scriptBuffer.append("valuesMap['" + field.getName() + "_" + IS_LOADED + "'] = ddtabmenu.isloaded('" + field.getId()
				+ "');");

		return scriptBuffer.toString();
	}
}
