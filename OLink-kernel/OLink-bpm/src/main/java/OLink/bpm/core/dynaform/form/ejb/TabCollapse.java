package OLink.bpm.core.dynaform.form.ejb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import OLink.bpm.core.macro.runner.AbstractRunner;
import OLink.bpm.core.macro.runner.IRunner;
import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.core.dynaform.document.ejb.Document;
import OLink.bpm.util.StringUtil;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.util.json.JsonUtil;

public class TabCollapse extends TabNormal {

	public TabCollapse(TabField field) {
		super(field);
	}

	protected final static String TAB_IAMGE = "img_";

	/**
	 * 
	 */
	private static final long serialVersionUID = 2483152110474408137L;

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
		
		// 获取打开的页签ID
		Collection<String> selecteds = getSelectedTabs(runner);
		// 添加页签HTML
		appendTabContentsHtml(html, doc, runner, webUser, selecteds);
		// 添加初始化脚本
		html.append(addScript(selecteds));

		return html.toString();
	}

	/**
	 * 获取选中的标签索引
	 * 
	 * @param tabs
	 * @param runner
	 * @return
	 * @throws Exception
	 */
	protected Collection<String> getSelecteds(IRunner runner) throws Exception {
		Collection<String> rtn = new ArrayList<String>();
		for (Iterator<?> iterator = getTabs().iterator(); iterator.hasNext();) {
			Tab tab = (Tab) iterator.next();
			StringBuffer suffix = new StringBuffer();
			suffix.append("Tab.").append(tab.getName());
			suffix.append(".hiddenScript");

			if (field.runBooleanScript(runner, suffix.toString(), tab.getHiddenScript())) {
				continue;
			}
			rtn.add(tab.getFormId());

			if (!Boolean.parseBoolean(field.getOpenAll())) {
				break;
			}
		}

		return rtn;
	}

	/**
	 * 获取选中的页签
	 * 
	 * @param runner
	 * @return
	 * @throws Exception
	 */
	public Collection<String> getSelectedTabs(IRunner runner) throws Exception {
		Collection<String> rtn = new ArrayList<String>();
		
		//勾选全部打开时，SelectedTabs为所有 2011-3-18  by Dolly
		if(field.getOpenAll()!=null && Boolean.parseBoolean(field.getOpenAll())){
			Collection<Tab> tabCols= field.getTabs();
			for(Iterator<Tab> iter=tabCols.iterator();iter.hasNext();){
				Tab tab = iter.next();
				rtn.add(tab.getFormId());
			}
		}else{
			Object result = runner.run(field.getScriptLable("SelectedScript"), StringUtil.dencodeHTML(field.getSelectedScript()));
	
			if (result != null && result instanceof String) {
				String[] tabNames = ((String) result).split(";");
				for (int i = 0; i < tabNames.length; i++) {
					Tab tab = getTabByName(tabNames[i]);
					// 非隐藏且选中
					if (!tab.isHidden(this, runner)) {
						rtn.add(tab.getFormId());
					}
				}
			}
		}
		if (rtn.isEmpty()) {
			if(getFirstShowTab(runner)!=null){
				rtn.add(getFirstShowTab(runner).getFormId());
			}
		}

		return rtn;
	}

	/**
	 * 添加script
	 * 
	 * @return 字符串的内容为重定的script
	 */
	private String addScript(Collection<String> selecteds) {
		StringBuffer script = new StringBuffer();
		script.append("<script language='JavaScript'>");

		Collection<Object> tabIdList = new ArrayList<Object>();
		for (Iterator<String> iterator = selecteds.iterator(); iterator.hasNext();) {
			String selected = iterator.next();
			tabIdList.add("'" + selected + "'");
		}

		script.append("defineCollapse('" + field.getId() + "', " + tabIdList.toString() + ");");
		script.append("</script>");

		return script.toString();
	}

	/**
	 * 加载折叠显示栏目
	 * 
	 * @param html
	 * @param contextPath
	 * @param tab
	 * @param index
	 * @throws Exception
	 */
	public void appendTabHeadHtml(StringBuffer html, IRunner runner, String contextPath, Tab tab, Collection<String> selecteds)
			throws Exception {
		StringBuffer suffix = new StringBuffer();
		suffix.append("Tab.").append(tab.getName());
		suffix.append(".hiddenScript");
		html.append("<div id='").append(TAB_LI + tab.getFormId()).append("'");
		boolean isHidden = field.runBooleanScript(runner, suffix.toString(), tab.getHiddenScript());
		if (isHidden) {
			html.append(" style='display:none'");
		}
		html.append(">");

		String imgId = TAB_IAMGE + tab.getFormId();

		html.append("<table width='100%' height='22' align='center' cellPadding='0' cellSpacing='0' id='" + tab.getFormId()
				+ "' onClick='toggleCollapse(\"" + field.getId() + "\", this.id)' class='margin'>");
		html.append("<tr>");
		String imagePath = contextPath + "/script/collapse/images";
		html.append("<td width='5' align='left'><img src='" + imagePath + "/left_l_bar.gif'/></td>");
		html.append("<td width='30' align='center' background='" + imagePath + "/left_bar.gif'>");
		// 打开关闭图片
		html.append("<img id='" + imgId + "' src='" + imagePath + "/");
		if (selecteds.contains(tab.getFormId())) {
			html.append("left_open");
		} else {
			html.append("left_dot");
		}
		html.append(".gif' /></td>");

		html.append("<td class='white' background='" + imagePath + "/left_bar.gif'>" + tab.getName() + "</td>");
		html.append("<td width='5' align='right'><img src='" + imagePath + "/left_r_bar.gif'/></td>");
		html.append("</tr>");
		html.append("</table>");
		html.append("</div>");
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
	public void appendTabContentsHtml(StringBuffer html, Document doc, IRunner runner, WebUser webUser, Collection<String> selecteds) {
		try {
			int index = 0;

			String contextPath = doc.get_params().getContextPath();
			String scriptUrl = contextPath + "/script/collapse/collapse.js";
			html.append("<script src='" + scriptUrl + "' type='text/javascript'></script>");

			for (Iterator<?> iterator = getTabs().iterator(); iterator.hasNext();) {
				Tab tab = (Tab) iterator.next();

				appendTabHeadHtml(html, runner, contextPath, tab, selecteds); // 折叠显示栏目

				StringBuffer suffix = new StringBuffer();
				suffix.append("Tab.").append(tab.getName());
				suffix.append("(").append(tab.getFormId()).append(")");
				suffix.append(".hiddenScript");

				// 折叠显示内容
				html.append("<div id='").append(TAB_CONTENT + tab.getFormId()).append("'");
				if (!selecteds.contains(tab.getFormId())) {
					html.append(" style='display:none'");
				}
				html.append(">");

				// 除第一个选中的Tab外，其他都默认隐藏
				if (selecteds.contains(tab.getFormId())) {
					if (!field.get_form().equals(tab.getForm())) {
						html.append(tab.toHtml(doc, runner, webUser, false));
					}
				} else {
					html.append(tab.toHtml(doc, runner, webUser, true));
				}
				html.append("</div>");

				index++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 是否已加载
	 * 
	 * @param loadedMap
	 * @param tab
	 * @return
	 */
	private boolean isLoaded(Map<?, ?> loadedMap, Tab tab) {
		if (loadedMap != null && loadedMap.containsKey(tab.getFormId())) {
			return ((Boolean) loadedMap.get(tab.getFormId())).booleanValue();
		}

		return false;
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
		String loadedJSON = doc.get_params().getParameterAsString(getName() + "_" + IS_LOADED);
		Map<?, ?> loadedMap = JsonUtil.toMap(loadedJSON);

		boolean isOneSelected = false; // 有一个tab被选中
		Tab firstShowTab = null; // 第一个显示的tab

		for (Iterator<?> iterator = getTabs().iterator(); iterator.hasNext();) {
			Tab tab = (Tab) iterator.next();
			boolean isHidden = false;
			if (isCalculateOnRefresh()) {
				StringBuffer suffix = new StringBuffer();
				suffix.append("Tab(").append(tab.getFormId()).append(")." + tab.getName());
				suffix.append(".hiddenScript");

				isHidden = this.field.runBooleanScript(runner, suffix.toString(), tab.getHiddenScript());

				if (isHidden) {
					// clear content fields
					Collection<FormField> fields = tab.getFields();
					for (Iterator<FormField> iter = fields.iterator(); iter.hasNext();) {
						FormField field = iter.next();
						if (field instanceof TabField) {
							// buffer.append(field.getRefreshScript(runner, doc,
							// webUser, isHidden));
							buffer.append(((TabField) field).toHidden(runner, doc, webUser));

						} else
							buffer.append(field.getRefreshScript(runner, doc, webUser, isHidden));
					}

					buffer.append("var oContent = document.getElementById('");
					buffer.append(TAB_CONTENT).append(tab.getFormId()).append("');");
					buffer.append("oContent.isloaded = false;\n");
					// hidden tab
					buffer.append("document.getElementById('");
					buffer.append(TAB_CONTENT).append(tab.getFormId());
					buffer.append("').style.display = 'none';\n");
					buffer.append("document.getElementById('");
					buffer.append(TAB_LI).append(tab.getFormId()).append("').style.display = 'none';\n");
				} else {
					// show tab
					buffer.append("document.getElementById('");
					buffer.append(TAB_LI).append(tab.getFormId()).append("').style.display = 'inline';\n");
					if (isLoaded(loadedMap, tab)) {
						buffer.append("document.getElementById('");
						buffer.append(TAB_CONTENT).append(tab.getFormId()).append("').style.display = 'inline';\n");
					} else if (Boolean.parseBoolean(field.getOpenAll())) {
						// buffer.append("document.getElementById('");
						// buffer.append(TAB_CONTENT).append(tab.getFormId()).append("').style.display
						// = 'inline';\n");
						buffer.append("toggleCollapse(\"" + field.getId() + "\", \"" + tab.getFormId() + "\");\n");
					}

					if (firstShowTab == null) {
						firstShowTab = tab;
					}
					// if (!isloaded) { // 设置为已加载
					buffer.append("var oContent = document.getElementById('");
					buffer.append(TAB_CONTENT).append(tab.getFormId()).append("');");
					buffer.append("oContent.isloaded = true;\n");
					// }
					if (tab.getFormId().equals(selectedId)) { // 刷新选中的Tab
						isOneSelected = true;

						Collection<FormField> fields = tab.getFields();

						for (Iterator<FormField> iter = fields.iterator(); iter.hasNext();) {
							// 重新刷新字段显示脚本
							FormField field = iter.next();
							// if (!isloaded) { // 是否已加载过的Tab
							if (field instanceof TabField) {
								buffer.append(field.getRefreshScript(runner, doc, webUser));
							} else
								buffer.append(field.getRefreshScript(runner, doc, webUser, isHidden));
							// } else {
							// if (field.isCalculateOnRefresh()) { // 是否重计算
							// buffer.append(field.getRefreshScript(
							// runner, doc, webUser));
							// }
							// }
						}
					}
				}
			}
		}

		// 如果当前选中的tab已隐藏，则显示第一个不隐藏的tab
		if (!isOneSelected && firstShowTab != null) {
			doc.get_params().setParameter(getName(), firstShowTab.getFormId());
			buffer.append(getRefreshScript(runner, doc, webUser));
		}

		String html = buffer.toString();

		return html;
	}

	public String getValueMapScript() {
		StringBuffer scriptBuffer = new StringBuffer();
		scriptBuffer.append("valuesMap['" + field.getName() + "']=getCollapseSelected('" + field.getId() + "');\n");
		scriptBuffer.append("valuesMap['" + field.getName() + "_" + IS_LOADED + "']=isCollapseLoaded('" + field.getId() + "');");

		return scriptBuffer.toString();
	}
}
