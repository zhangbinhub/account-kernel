package OLink.bpm.core.dynaform.form.ejb;

import java.io.Serializable;
import java.util.Collection;

import OLink.bpm.core.macro.runner.IRunner;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.core.dynaform.document.ejb.Document;
import OLink.bpm.util.StringUtil;

/**
 * @author nicholas
 */
public class Tab implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -223077910802191469L;

	/**
	 * @uml.property name="name"
	 */
	private String name;

	/**
	 * @uml.property name="hiddenScript"
	 */
	private String hiddenScript;

	/**
	 * @uml.property name="formId"
	 */
	private String formId;

	private String hiddenPrintScript;

	/**
	 * 
	 */
	private Form form;

	private boolean calculateOnRefresh;

	private boolean refreshOnChanged;

	/**
	 * 是否重计算
	 * 
	 * @return
	 */
	public boolean isCalculateOnRefresh() {
		return calculateOnRefresh;
	}

	/**
	 * 设置是否重计算
	 * 
	 * @param calculateOnRefresh
	 */
	public void setCalculateOnRefresh(boolean calculateOnRefresh) {
		this.calculateOnRefresh = calculateOnRefresh;
	}

	/**
	 * 是否刷新
	 * 
	 * @return
	 */
	public boolean isRefreshOnChanged() {
		return refreshOnChanged;
	}

	/**
	 * 是否刷新
	 * 
	 * @param refreshOnChanged
	 */
	public void setRefreshOnChanged(boolean refreshOnChanged) {
		this.refreshOnChanged = refreshOnChanged;
	}

	/**
	 * 获取关联的表单标识
	 * 
	 * @return 关联的表单标识
	 * @uml.property name="formId"
	 */
	public String getFormId() {
		return formId;
	}

	/**
	 * 设置 关联的表单标识
	 * 
	 * @param formId
	 *            关联的表单标识
	 * @uml.property name="formId"
	 */
	public void setFormId(String formId) {
		this.formId = formId;
	}

	/**
	 * 获取TAB的名字
	 * 
	 * @return the name
	 * @uml.property name="name"
	 */
	public String getName() {
		return name;
	}

	/**
	 * 设置 TAB的名字
	 * 
	 * @param name
	 *            the name to set
	 * @uml.property name="name"
	 */
	public void setName(String name) {
		this.name = StringUtil.dencodeHTML(name);
	}

	/**
	 * 获取TAB的隐藏脚本
	 * 
	 * @return TAB的隐藏脚本
	 * @uml.property name="hiddenScript"
	 */
	public String getHiddenScript() {
		return hiddenScript;
	}

	/**
	 * 设置TAB的隐藏脚本
	 * 
	 * @param hiddenScript
	 *            TAB的隐藏脚本
	 * @uml.property name="hiddenScript"
	 */
	public void setHiddenScript(String hiddenScript) {
		this.hiddenScript = StringUtil.dencodeHTML(hiddenScript);
	}

	public boolean isHidden(TabNormal tabMode, IRunner runner) throws Exception {
		StringBuffer suffix = new StringBuffer();
		suffix.append("Tab.").append(getName());
		suffix.append(".hiddenScript");

		return tabMode.field.runBooleanScript(runner, suffix.toString(), getHiddenScript());
	}

	/**
	 * 获取TAB打印时隐藏脚本
	 * 
	 */
	public String getHiddenPrintScript() {
		return hiddenPrintScript;
	}

	/**
	 * 设置TAB打印时隐藏脚本
	 */
	public void setHiddenPrintScript(String hiddenPrintScript) {
		this.hiddenPrintScript = StringUtil.dencodeHTML(hiddenPrintScript);
	}

	public String toHtml(Document doc, IRunner runner, WebUser webUser, boolean hiddenAll) throws Exception {
		return getForm().toCalctext(doc, runner, webUser, hiddenAll, false);// 不打印版权信息
	}

	public String toMbXML(Document doc, IRunner runner, WebUser webUser, boolean hiddenAll) throws Exception {
		if (hiddenAll)
			return "";
		return getForm().toXMLCalctext(doc, runner, webUser, hiddenAll);
	}

	public String toPrintHtml(Document doc, IRunner runner, WebUser webUser) throws Exception {
		return getForm().toPrintCalctext(doc, runner, webUser);
	}

	public Collection<FormField> getFields() throws Exception{//修改 by  XGY throws Exception
		return getForm().getAllFields();
	}

	public Form getForm() throws Exception{//修改 by  XGY throws Exception
		if (this.form == null) {
			try {
				FormProcess fp = (FormProcess) ProcessFactory.createProcess(FormProcess.class);
				Form form = (Form) fp.doView(formId);

				this.form = form != null ? form : new Form();
			} catch (Exception e) {
				throw e;
			}
		}
		return this.form;
	}
}
