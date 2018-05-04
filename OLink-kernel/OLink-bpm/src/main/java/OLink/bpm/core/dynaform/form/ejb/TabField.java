package OLink.bpm.core.dynaform.form.ejb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.core.macro.runner.AbstractRunner;
import OLink.bpm.core.macro.runner.IRunner;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.util.json.JsonUtil;
import OLink.bpm.core.dynaform.document.ejb.Document;
import OLink.bpm.util.StringUtil;
import eWAP.core.ResourcePool;

/**
 * 页签(TAB)组件
 * 
 * @author nicholas
 */
public class TabField extends FormField {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6274054997243800672L;

	protected String relStr = "";

	private String showMode;

	public String openAll;

	private String selectedScript;

	protected TabNormal tabMode = new TabNormal(this);
	/**
	 * 页签集合
	 */
	protected Collection<Tab> tabs;

	public String getName() {
		return "tab_" + getId();
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
		template.append("<IMG id=" + this.getId() + "");
		template.append("src=\""+ResourcePool.getRootpath()+"/core/dynaform/form/formeditor/buttonimage/v1/tag.gif\"");
		template.append("className=\"TabField\"");
		template.append("type=\"tabfield\"");
		String[] exculdes = new String[] { "form", "fields" };
		template.append("relStr= \"" + JsonUtil.collection2Json(tabs, exculdes) + "\"");
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
		return tabMode.toHtmlTxt(doc, runner, webUser);
	}

	/**
	 * 获取tab组件的集合
	 * @SuppressWarnings JsonUtil.toCollection 方法返回对象集类型不定
	 * @return tab组件的集合
	 * @uml.property name="tabs"
	 */
	@SuppressWarnings("unchecked")
	public Collection<Tab> getTabs() {
		try {
			if (tabs == null) {
				return (Collection)JsonUtil.toCollection(relStr, Tab.class);
			}
		} catch (Exception e) {
			e.printStackTrace();
			tabs = new ArrayList<Tab>();
		}

		return tabs;
	}

	public String toHidden(IRunner runner, Document doc, WebUser webUser) throws Exception {
		StringBuffer buffer = new StringBuffer();
		for (Iterator<Tab> iterator = getTabs().iterator(); iterator.hasNext();) {
			Tab tab = iterator.next();
			buffer.append("var oContent = document.getElementById('");
			buffer.append(TabNormal.TAB_CONTENT).append(tab.getFormId()).append("');");
			buffer.append("oContent.isloaded = false;\n");
			// hidden tab
			buffer.append("document.getElementById('");
			buffer.append(TabNormal.TAB_LI).append(tab.getFormId());
			buffer.append("').style.display = 'none';\n");
		}

		return buffer.toString();
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
	 * @return the relStr
	 * @uml.property name="relStr"
	 */
	public String getRelStr() {
		return relStr;
	}

	/**
	 * @param relStr
	 *            the relStr to set
	 * @uml.property name="relStr"
	 */
	public void setRelStr(String relStr) {
		this.relStr = relStr;
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
	 * 
	 * @see AbstractRunner#run(String, String)
	 * @return 打印重定义后的打印html为Form模版的文本框组件内容结合Document中的ITEM存放的值
	 * @throws Exception
	 * @throws Exception
	 */
	public String toPrintHtmlTxt(Document doc, IRunner runner, WebUser webUser) throws Exception {
		return tabMode.toPrintHtmlTxt(doc, runner, webUser);
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
		return tabMode.getRefreshScript(runner, doc, webUser);
	}
	
	public boolean isRender(String destVal, String origVal) {
		return true;
	}

	public String getValueMapScript() {
		return tabMode.getValueMapScript();
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
		return tabMode.toMbXMLText(doc, runner, webUser);
	}

	public String getShowMode() {
		return showMode;
	}

	public void setShowMode(String showMode) {
		if (!StringUtil.isBlank(showMode) && showMode.equals("1")) {
			tabMode = new TabCollapse(this);
		} else {
			tabMode = new TabNormal(this);
		}

		this.showMode = showMode;
	}

	public String getOpenAll() {
		return openAll;
	}

	public void setOpenAll(String openAll) {
		this.openAll = openAll;
	}

	public String getSelectedScript() {
		return selectedScript;
	}

	public void setSelectedScript(String selectedScript) {
		this.selectedScript = selectedScript;
	}
}
