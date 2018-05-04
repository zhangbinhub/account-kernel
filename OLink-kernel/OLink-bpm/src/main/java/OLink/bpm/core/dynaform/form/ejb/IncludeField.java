//Source file:
//C:\\Java\\workspace\\SmartWeb3\\src\\com\\cyberway\\dynaform\\form\\ejb\\InputField.java

package OLink.bpm.core.dynaform.form.ejb;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.core.dynaform.view.ejb.View;
import OLink.bpm.core.macro.runner.AbstractRunner;
import OLink.bpm.core.macro.runner.IRunner;
import OLink.bpm.core.macro.runner.JavaScriptFactory;
import OLink.bpm.core.dynaform.PermissionType;
import OLink.bpm.core.dynaform.document.ejb.Document;
import OLink.bpm.core.user.action.WebUser;

/**
 * @author nicholas
 * @uml.dependency supplier="IncludedElement"
 *                 stereotypes="Omondo::Import"
 */
public class IncludeField extends FormField {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7324947435225302640L;

	public static final String INCLUDE_TYPE_VIEW = "0";

	public static final String INCLUDE_TYPE_PAGE = "1";

	/**
	 * @uml.property name="includeType"
	 */
	protected String includeType;

	/**
	 * @uml.property name="integratePage"
	 */
	protected boolean integratePage;

	/**
	 * @uml.property name="enabled"
	 */
	protected boolean enabled;

	/**
	 * 是否关联, 是则为父子关系, 非则没有关系
	 */
	protected boolean relate = true;

	private IncludedElement element;

	/**
	 * @roseuid 41ECB66E012A
	 */
	public IncludeField() {

	}

	public ValidateMessage validate(IRunner bsf, Document doc) throws Exception {
		return null;
	}

	/**
	 * 根据includeField的显示类型不同,返回的结果字符串不同.
	 * 新建的Document,includeField的显示类型为默认的MODIFY。此时根据Form模版的includeField内容结合Document的Item的值,返回的字符串为重定义后的html.
	 * 根据流程节点设置对应includeField的显示类型不同,返回的结果字符串各不同.
	 * 1)若节点设置对应includeField为DISABLED,返回重定义后的html的includeField为DISABLED.
	 * 否则重定义后的html为根据设置includeField组件的includeType属性的值类型与enabled属性的值来决定定义的内容。
	 * <p>
	 * include值类型两种常量为：INCLUDE_TYPE_VIEW(值为"0")，INCLUDE_TYPE_PAGE(值为"1").
	 * enabled属性的值为true|false.默认为false.
	 * <p>
	 * 有三种不同的返回值：
	 * <p>
	 * 1）includeField组件的includeType属性设置为view而enabled属性设置为true时，
	 * 若相应的Document没有数据记录时，返回重定义后的html为空字符.
	 * 否则Form模版的includeField组件内容结合Document中的ITEM存放的值,返回重定义后的html.
	 * <p>
	 * 2) includeField组件的includeType属性设置为view而enabled属性设置为false时,
	 * Form模版的includeField组件内容结合Document中的ITEM存放的值,返回重定义后的html.
	 * <p>
	 * <p>
	 * 3）includeField组件的includeType属性设置为pag时，若没有找到相应的page,返回空字符.
	 * 否则page模版的includeField组件内容结合Document中的ITEM存放的值,返回重定义后的html.
	 * <p>
	 * 通过强化HTML标签及语法，表达includeType的布局、属性、事件、样式、等。
	 * 
	 * @param doc
	 *            文档对象
	 * @see ParamsTable#params
	 * @see AbstractRunner#run(String, String)
	 * @return 重定义后的html
	 * 
	 */
	public String toHtmlTxt(Document doc, IRunner runner, WebUser webUser) throws Exception {
		int displayType = getDisplayType(doc, runner, webUser);
		if (displayType == PermissionType.HIDDEN) {
			return this.getHiddenValue();
		} else
			return element.toHtmlTxt(doc, runner, webUser);
	}

	public String toPrintHtmlTxt(Document doc, IRunner runner, WebUser webUser) throws Exception {
		return element.toPrintHtmlTxt(doc, runner, webUser);
	}

	public String getEditMode(IRunner runner, Document doc, WebUser webUser) throws Exception {
		int displayType = getDisplayType(doc, runner, webUser);

		String editmode = "true";
		if (displayType == PermissionType.DISABLED || displayType == PermissionType.READONLY) {
			editmode = "false";
		}
		return editmode;
	}

	/**
	 * 返回模板描述IncludeField
	 * 
	 * @return java.lang.String
	 * @roseuid 41E7917A033F
	 */
	public String toTemplate() {
		StringBuffer template = new StringBuffer();
		template.append("<span'");
		template.append(" className='" + this.getClass().getName() + "'");
		template.append(" id='" + getId() + "'");
		template.append(" name='" + getName() + "'");
		template.append(" formid='" + getFormid() + "'");
		template.append(" discript='" + getDiscript() + "'");
		template.append(" refreshOnChanged='" + isRefreshOnChanged() + "'");
		template.append(" validateRule='" + getValidateRule() + "'");
		template.append(" hiddenScript='" + getHiddenScript() + "'");
		template.append("/>");
		return template.toString();
	}

	/**
	 * 获取是否整合到页面. true为整合到页面，false为不整合到页面,默认为不整合到页面
	 * 
	 * @return true or false.
	 * @uml.property name="integratePage"
	 */
	public boolean isIntegratePage() {
		return integratePage;
	}

	/**
	 * 设置是否整合到页面. true为整合到页面，false为不整合到页面,默认为不整合到页面.
	 * 
	 * @param integratePage
	 *            boolean
	 * @uml.property name="integratePage"
	 */
	public void setIntegratePage(boolean integratePage) {
		this.integratePage = integratePage;
	}

	/**
	 * 返回include Type 类型:（0:'View',1:'Page'）.
	 * 
	 * @return include Type 类型:（0:'View',1:'Page'）
	 * @uml.property name="includeType"
	 */

	public String getIncludeType() {
		return includeType;
	}

	/**
	 * 返回没有Document时是否显示VIEW, true为不显示,默认显示
	 * 
	 * @return
	 * @uml.property name="enabled"
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * 设置没有Document时是否显示VIEW, true为不显示,默认显示
	 * 
	 * @param enabled
	 * @uml.property name="enabled"
	 */

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	/**
	 * 设置 include Type 类型:（0:'View',1:'Page'）
	 * 
	 * @param includeType
	 * @uml.property name="includeType"
	 */
	public void setIncludeType(String includeType) {
		if (includeType.equals(INCLUDE_TYPE_PAGE)) {
			element = new IncludedPage(this);
		} else if (includeType.equals(INCLUDE_TYPE_VIEW)) {
			element = new IncludedView(this);
		}
		this.includeType = includeType;
	}

	/**
	 * 获取包含的视图
	 * 
	 * @return
	 * @throws Exception
	 */
	public View getIncludeView() throws Exception {
		if (includeType.equals(INCLUDE_TYPE_VIEW)) {
			IRunner runner = JavaScriptFactory.getInstance("", get_form().getApplicationid());
			IncludedElement element = new IncludedView(this);
			return (View) element.getValueObject(runner);
		}

		return null;
	}

	public String toMbXMLText(Document doc, IRunner runner, WebUser webUser) throws Exception {
		return element.toXMLTxt(doc, runner, webUser);
	}

	public boolean isRelate() {
		return relate;
	}

	public void setRelate(boolean relate) {
		this.relate = relate;
	}

	/**
	 * 重新计算
	 * 
	 */
	public void recalculate(IRunner runner, Document doc, WebUser webUser) throws Exception {
		getLog().debug("IncludeField.recalculate");
		runValueScript(runner, doc);
	}
	
	public String getRefreshScript(IRunner runner, Document doc, WebUser webUser) throws Exception {
		return super.getRefreshScript(runner, doc, webUser);
	}
}
