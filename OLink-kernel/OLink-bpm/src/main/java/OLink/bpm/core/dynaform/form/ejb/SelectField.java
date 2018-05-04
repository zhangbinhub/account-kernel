// Source file:
// C:\\Java\\workspace\\SmartWeb3\\src\\com\\cyberway\\dynaform\\form\\ejb\\SelectField.java

package OLink.bpm.core.dynaform.form.ejb;

import java.util.Iterator;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.core.dynaform.document.ejb.Item;
import OLink.bpm.core.macro.runner.AbstractRunner;
import OLink.bpm.core.macro.runner.IRunner;
import OLink.bpm.core.macro.runner.JavaScriptRunner;
import OLink.bpm.core.table.constants.MobileConstant;
import OLink.bpm.core.dynaform.PermissionType;
import OLink.bpm.core.dynaform.document.ejb.Document;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.util.HtmlEncoder;
import OLink.bpm.util.StringUtil;

/**
 * 下拉框组件
 * 
 * @author Marky
 */
public class SelectField extends FormField implements ValueStoreField {
	
	private static final long serialVersionUID = -8469984129928896643L;

	protected static String cssClass = "select-cmd";

	/**
	 * 计算多值选项
	 * 
	 * @uml.property name="optionsScript"
	 */
	protected String optionsScript;

	/**
	 * @roseuid 41ECB66D031D
	 */
	public SelectField() {

	}
/*
 * 

	protected boolean layer;

	public boolean isLayer() {
		return layer;
	}

	public void setLayer(boolean layer) {
		this.layer = layer;
	}
 * 
 */
	/**
	 * 获取多值选项脚本
	 * 
	 * @return 多值选项脚本
	 * @uml.property name="optionsScript"
	 */
	public String getOptionsScript() {
		return optionsScript;
	}

	/**
	 * 设置多值选项脚本
	 * 
	 * @param optionsScript
	 *            The optionsScript to set.
	 * @uml.property name="optionsScript"
	 */
	public void setOptionsScript(String optionsScript) {
		this.optionsScript = optionsScript;
	}

	/**
	 * 重新计算SelectField.
	 * 
	 * @roseuid 41DB89D700F9
	 */
	public void recalculate(IRunner runner, Document doc, WebUser webUser) throws Exception {
		getLog().debug("SelectField.recalculate");
		runValueScript(runner, doc);
		runOptionsScript(runner, doc);
	}

	/**
	 * 根据CheckboxField的显示类型不同,返回的结果字符串不同.
	 * 新建的Document,SelectField的显示类型为默认的MODIFY。
	 * 此时根据Form模版的SelectField内容结合Document的Item的值,返回的字符串为重定义后的html.
	 * 根据流程节点设置对应SelectField的显示类型不同,返回的结果字符串不同.
	 * 
	 * 1)节点设置对应SelectField的显示类型为MODIFY,
	 * 并根据Form模版的SelectField组件内容结合Document中的ITEM存放的值,返回重定义后的html.
	 * 2)节点设置对应SelectField的显示类型为HIDDEN,返回"******".
	 * 3)节点设置对应SelectField的显示类型为READONLY,Document的ITEM存放的值为只读.
	 * 并根据Form模版的SelectField组件内容结合Document中的ITEM存放的值,返回重定义后的html.
	 * 4)节点设置对应SelectField的显示类型为DISABLED,Document的ITEM存放的值为DISABLED.
	 * 并根据Form模版的SelectField组件内容结合Document中的ITEM存放的值,返回重定义后的html.
	 * 
	 * 通过强化HTML的单值选项标签及语法，表达列表框的布局、属性、事件等。
	 * 
	 * @param doc
	 *            文档对象
	 * @see ParamsTable#params
	 * @see AbstractRunner#run(String, String)
	 * @return 字符串
	 * 
	 */
	public String toHtmlTxt(Document doc, IRunner runner, WebUser webUser) throws Exception {
		StringBuffer html = new StringBuffer();
		int displayType = getDisplayType(doc, runner, webUser);
		if (displayType == PermissionType.HIDDEN) {
			return this.getHiddenValue();
		} else{
			if (doc != null) {
				try {
					html.append("<select");
					html.append(" " + toOtherpropsHtml());
					html.append(toAttr(doc, displayType));
					html.append(">");
					html.append(runOptionsScript(runner, doc));
					html.append("</select>");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return html.toString();
	}

	/**
	 * 根据Form模版的SelectField组件内容结合Document中的ITEM存放的值,输出重定义后的html文本以网格显示
	 * 
	 * @param runner
	 *            动态脚本执行器
	 * @param doc
	 *            文档对象
	 * @param webUser
	 *            webUser
	 * @return 重定义后的html文本
	 */
	public String toGridHtmlText(Document doc, IRunner runner, WebUser webUser) throws Exception {
		StringBuffer html = new StringBuffer();
		int displayType = getDisplayType(doc, runner, webUser);
		if (displayType == PermissionType.HIDDEN) {
			return this.getHiddenValue();
		} else {
			if (doc != null) {
				try {
					html.append("<select");
					html.append(toAttr(doc, displayType));
					html.append(">");
					html.append(runOptionsScript(runner, doc));
					html.append("</select>");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return html.toString();
	}

	private String toAttr(Document doc, int displayType) {
		StringBuffer html = new StringBuffer();

		html.append(" style='display:");
		html.append(getTextType().equals("hidden") ? "none" : "inline");
		html.append("'");
		html.append(" id='" + getFieldId(doc) + "'");
		html.append(" name = '" + getName() + "'");
		html.append(" fieldType='" + getTagName() + "'");
		if (isRefreshOnChanged()) {
			html.append(" onchange='dy_refresh(this.id)'");
		}
		if (displayType == PermissionType.READONLY || getTextType().equals("readonly")) {
			html.append(" disabled ");
		} else if (displayType == PermissionType.DISABLED) {
			html.append(" disabled ");
		}

		return html.toString();
	}

	/**
	 * @param tmpltStr
	 * @return FormField
	 * @roseuid 41ECB66D0381
	 */
	public FormField init(String tmpltStr) {
		return null;
	}

	/**
	 * 获取模板描述下拉选项
	 * 
	 * @return 模板描述下拉选项
	 * @roseuid 41E7917A033F
	 */
	public String toTemplate() {
		StringBuffer template = new StringBuffer();
		template.append("<select'");
		template.append(" className='" + this.getClass().getName() + "'");
		template.append(" id='" + getId() + "'");
		template.append(" name='" + getName() + "'");
		template.append(" formid='" + getFormid() + "'");
		template.append(" discript='" + getDiscript() + "'");
		template.append(" hiddenScript='" + getHiddenScript() + "'");
		template.append(" hiddenPrintScript='" + getHiddenPrintScript() + "'");
		template.append(" refreshOnChanged='" + isRefreshOnChanged() + "'");
		template.append(" validateRule='" + getValidateRule() + "'");
		template.append(" valueScript='" + getValueScript() + "'");
		template.append(" optionScript='" + getOptionsScript() + "'");
		template.append(" onRefresh='" + isCalculateOnRefresh() + "'");
		template.append("/>");
		return template.toString();

	}

	/**
	 * 返回执行多值选项脚本后重定义后的html，通过强化HTML标签及语法，表达下拉选项的布局、属性、事件、样式、等。
	 * 
	 * @param runner
	 *            AbstractRunner<code>(执行脚本接口类)</code>
	 * @see JavaScriptRunner#run(String, String)
	 * 
	 * @param doc
	 *            文档对象
	 * @return 字符串内容为重定义后的html的下拉选框标签及值
	 * @throws Exception
	 */
	public String runOptionsScript(IRunner runner, Document doc) throws Exception {
		return runOptionsScript(runner, doc, "HTML");
	}

	private String runOptionsScript(IRunner runner, Document doc, String stringType) throws Exception {
		StringBuffer html = new StringBuffer();
		Options options = getOptions(runner, doc);

		if (stringType.equals("HTML")) {
			if (options != null) {
				return toOptionForHtml(options, doc);
			}
		} else if (stringType.equals("XML")) {
			if (options != null) {
				return toOptionForXml(options, doc);
			} else {
				html.append("<").append(MobileConstant.TAG_OPTION).append(">");
				html.append("</").append(MobileConstant.TAG_OPTION).append(">");
			}
		}
		return html.toString();
	}

	private Options getOptions(IRunner runner, Document doc) throws Exception {
		Object result = null;
		Options options = null;
		if (getOptionsScript() != null && getOptionsScript().trim().length() > 0) {

			result = runner.run(getScriptLable("OptionsScript"), StringUtil.dencodeHTML(getOptionsScript()));
			if (result != null && result instanceof String) {

				String[] strlst = ((String) result).split(";");
				options = new Options();
				for (int i = 0; i < strlst.length; i++) {
					options.add(strlst[i], strlst[i]);
				}
			} else if (result instanceof Options) {
				options = (Options) result;
			}
		}
		return options;
	}

	/**
	 * 返回执行多值选项脚本后重定义后的xml，通过强化HTML标签及语法，表达下拉选项的布局、属性、事件、样式、等。
	 * 
	 * @param runner
	 *            AbstractRunner<code>(执行脚本接口类)</code>
	 * @see JavaScriptRunner#run(String, String)
	 * 
	 * @param doc
	 *            文档对象
	 * @return 字符串内容为重定义后的xml的下拉选框标签及值
	 * @throws Exception
	 */
	private String runOptionsScriptToXML(IRunner runner, Document doc) throws Exception {
		return runOptionsScript(runner, doc, "XML");
	}

	/**
	 * 根据打印时对应SelectField的显示类型不同,默认为MODIFY,返回的结果字符串不同.
	 * 若Document不为空且打印时对应SelectField的显示类型不为HIDDEN且字段类型不为HIDDEN,
	 * <P>
	 * 并根据Form模版的SelectField组件内容结合Document中的ITEM存放的值,返回重定义后的打印html文本. 否则为空字符串.
	 * 
	 * @param doc
	 *            Document(文档对象)
	 * @param runner
	 *            AbstractRunner(执行脚本的接口类)
	 * @param params
	 *            参数
	 * @param user
	 *            webuser
	 * 
	 * @see AbstractRunner#run(String, String)
	 * @return 重定义后的打印html为Form模版的SelectField组件内容结合Document中的ITEM存放的值
	 * @throws Exception
	 */
	public String toPrintHtmlTxt(Document doc, IRunner runner, WebUser webUser) throws Exception {
		String html = "";
		if (doc != null) {
			int displayType = getPrintDisplayType(doc, runner, webUser);

			if (displayType == PermissionType.HIDDEN) {
				html = this.getPrintHiddenValue();
			}else{
				html = getText(doc, runner, webUser)+printHiddenElement(doc);
			}
		}
//		else{
//			html = printHiddenElement(doc);
//		}
		return html;
	}

	private String toOptionForHtml(Options options, Document doc) {
		StringBuffer html = new StringBuffer();
		Object value = null;
		if (doc != null) {
			Item item = doc.findItem(this.getName());
			if (item != null)
				value = item.getValue();
		}
		Iterator<Option> iter = options.getOptions().iterator();
		while (iter.hasNext()) {
			Option element = iter.next();
			if (element.getValue() != null) {
				html.append("<option");
				if (value != null && element.getValue() != null) {
					if (value.equals(element.getValue())) {
						html.append(" selected ");
					}
				} else {
					if (element.isDef()) {
						html.append(" selected ");
					}
				}
				html.append(" class='" + cssClass + "'");
				html.append(" value='");
			}
			html.append(HtmlEncoder.encode(element.getValue()));
			html.append("'");
			html.append(">");
			html.append(element.getOption()).append("</option>");
		}
		return html.toString();
	}

	public boolean isRender(String destVal, String origVal) {
		if (optionsScript != null && optionsScript.trim().length() > 0) {
			return true;
		}
		return super.isRender(destVal, origVal);
	}

	public String toMbXMLText(Document doc, IRunner runner, WebUser webUser) throws Exception {
		StringBuffer xmlText = new StringBuffer();
		int displayType = getDisplayType(doc, runner, webUser);

		if (doc != null) {
			xmlText.append("<").append(MobileConstant.TAG_SELECTFIELD);
			xmlText.append(" ").append(MobileConstant.ATT_ID + "='" + getId() + "'");
			xmlText.append(" ").append(MobileConstant.ATT_NAME + "='" + getName() + "'");
			xmlText.append(" ").append(MobileConstant.ATT_LABEL).append("='").append(getName()).append("'");

			if (displayType == PermissionType.READONLY
					|| (getTextType() != null && getTextType().equalsIgnoreCase("readonly"))
					|| displayType == PermissionType.DISABLED) {
				xmlText.append(" ").append(MobileConstant.ATT_READONLY + "='true' ");
			}
			if (displayType == PermissionType.HIDDEN) {
				xmlText.append(" ").append(MobileConstant.ATT_HIDDEN).append(" ='true' ");
			}

			if (isRefreshOnChanged()) {
				xmlText.append(" ").append(MobileConstant.ATT_REFRESH).append(" ='true' ");
			}
			xmlText.append(">");
			try {
				xmlText.append(runOptionsScriptToXML(runner, doc));
				xmlText.append("</").append(MobileConstant.TAG_SELECTFIELD + ">");
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		return xmlText.toString();
	}

	private String toOptionForXml(Options options, Document doc) {
		StringBuffer html = new StringBuffer();

		Object value = null;
		if (doc != null) {
			Item item = doc.findItem(this.getName());
			if (item != null)
				value = item.getValue();
		}

		Iterator<Option> iter = options.getOptions().iterator();
		int count = 0;
		boolean flag = true;
		while (iter.hasNext()) {
			Option element = iter.next();
			if (element.getValue() != null) {
				html.append("<").append(MobileConstant.TAG_OPTION).append("");
				if (flag) {
					if (value != null && element.getValue() != null) {
						if (value.equals(element.getValue())) {
							html.append(" ").append(MobileConstant.ATT_SELECTED).append("='" + count + "'");
							flag = false;
						}
					} else {
						if (element.isDef()) {
							html.append(" ").append(MobileConstant.ATT_SELECTED).append("='" + count + "'");
							flag = false;
						}
					}
				}
				html.append(" ").append(MobileConstant.ATT_VALUE).append("='");

				html.append(HtmlEncoder.encode(element.getValue()));
				html.append("'");

				html.append(">");

				if (element.getOption() != null && !element.getOption().trim().equals(""))
					html.append(HtmlEncoder.encode(element.getOption()));
				else
					html.append("{*[Select]*}");
				html.append("</").append(MobileConstant.TAG_OPTION).append(">");
				count++;
			}
		}
		return html.toString();
	}

	/**
	 * 获取Form模版的SelectField的选项脚本，通过动态语言执行器执行脚本后生成下拉选项,并验证下拉选项的可见性,如果可见就显示正常,否则显示
	 * "******"
	 * 
	 * @param runner
	 *            AbstractRunner(执行脚本的接口类)
	 * @param doc
	 *            (Document)文档对象
	 * @param webUser
	 *            webUser
	 * @return 执行后生成的下拉选项
	 */
	public String getText(Document doc, IRunner runner, WebUser webUser) throws Exception {
		if (!StringUtil.isBlank(doc.getParentid()) && getDisplayType(doc, runner, webUser) == PermissionType.HIDDEN) {
			return this.getHiddenValue();
		}

		Options options = getOptions(runner, doc);
		if (options != null) {
			for (Iterator<Option> iterator = options.getOptions().iterator(); iterator.hasNext();) {
				Option option = iterator.next();
				if (option.getValue().equals(doc.getItemValueAsString(getName()))) {
					return option.getOption();
				}
			}
		}

		return "";
	}
}
