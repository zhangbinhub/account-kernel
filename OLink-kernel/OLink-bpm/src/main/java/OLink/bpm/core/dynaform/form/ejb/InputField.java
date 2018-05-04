//Source file:
//C:\\Java\\workspace\\SmartWeb3\\src\\com\\cyberway\\dynaform\\form\\ejb\\InputField.java

package OLink.bpm.core.dynaform.form.ejb;

import java.text.DecimalFormat;
import java.text.ParseException;

import OLink.bpm.core.dynaform.PermissionType;
import OLink.bpm.core.macro.runner.AbstractRunner;
import OLink.bpm.core.macro.runner.IRunner;
import OLink.bpm.core.table.constants.MobileConstant;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.util.HtmlEncoder;
import OLink.bpm.core.dynaform.document.ejb.Item;
import OLink.bpm.core.dynaform.document.ejb.Document;
import OLink.bpm.util.StringUtil;
import eWAP.core.Tools;


/**
 * @author Marky
 */
public class InputField extends FormField implements ValueStoreField {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6019940535384883667L;

	/**
	 * @roseuid 41ECB66E012A
	 */
	protected static String cssClass = "input-cmd";

	/**
	 * @uml.property name="dialogView"
	 */
	protected String dialogView = "";

	/**
	 * @uml.property name="suggest"
	 */
	protected String suggest = ""; //

	/**
	 * @uml.property name="popToChoice"
	 */
	protected boolean popToChoice;
	/**
	 * @uml.property name="fiedlketevent"
	 */
	protected String fieldkeyevent = "";

	/**
	 * @uml.property name="numberPattern"
	 */
	protected String numberPattern = ""; // 数字显示模式

	/**
	 * InputField构造函数
	 * 
	 */
	public InputField() {

	}

	/**
	 * 获取键盘事件类型字符串 ，为了实现只用键盘快速输入.两种事件类型:1.回车键(EnterKey),2.Tab(TabKey)
	 * 
	 * @return 键盘事件类型字符串
	 * @uml.property name="fieldkeyevent"
	 */
	public String getFieldkeyevent() {
		return fieldkeyevent;
	}

	/**
	 * 设置键盘事件类型字符串 ，为了实现只用键盘快速输入.两种事件类型:1.回车键(EnterKey),2.Tab(TabKey)
	 * 
	 * @param fieldkeyevent
	 *            表单字列键盘事件类型字符串
	 * @uml.property name="fieldkeyevent"
	 */
	public void setFieldkeyevent(String fieldkeyevent) {
		this.fieldkeyevent = fieldkeyevent;
	}

	/**
	 * @roseuid 41ECB66E0152
	 */
	public void store() {

	}

	/**
	 * 根据includeField的显示类型不同,返回的结果字符串不同.
	 * 新建的Document,includeField的显示类型为默认的MODIFY。
	 * 此时根据Form模版的includeField内容结合Document的Item的值,返回的字符串为重定义后的html.
	 * 根据Document流程节点设置对应includeField的显示类型不同,返回的结果字符串不同.
	 * 1)若节点设置对应includeField的显示类型为MODIFY与新建的Document时，返回的字符串一样。
	 * 2)若节点设置对应includeField的显示类型为READONLY,Document的ITEM存放的值为只读.
	 * 3)若节点设置对应includeField的显示类型为DISABLED,Document的ITEM存放的值为DISABLED.
	 * 根据Form模版的文档框内容结合Document中的ITEM存放的值,返回重定义后的html，
	 * 通过强化HTML标签及语法，表达文档框的布局、属性、事件、样式、值等。 若节点设置对应includeField的显示类型为HIDDEN,返回空.
	 * 
	 * @param doc
	 *            文档对象
	 * @return 重定义后的html为Form模版的文档框内容结合Document中的ITEM存放的值,
	 * 
	 */
	public String toHtmlTxt(Document doc, IRunner runner, WebUser webUser) throws Exception {
		StringBuffer html = new StringBuffer();

		int displayType = getDisplayType(doc, runner, webUser);
		// 添加Field
		if (displayType == PermissionType.HIDDEN) {// 节点设置对应field隐藏
			return this.getHiddenValue();
		} else {
			html.append(toStart());

			// 改变onKeyDown属性
			if (getFieldkeyevent() == null || getFieldkeyevent().trim().length() == 0
					|| getFieldkeyevent().equalsIgnoreCase("Tabkey")) {
				// 类型为数字时
				if (this.getFieldtype().equals("VALUE_TYPE_NUMBER")) {
					//update by zb 2013-7-2
					html.append(" onKeyUp='resetWhenNonNumeric(this);'");
				}
			} else if (getFieldkeyevent().equalsIgnoreCase("Enterkey")) {
				html.append(" onKeyDown='setEnter();");
				if (this.getFieldtype().equals("VALUE_TYPE_NUMBER")) {
					html.append("resetWhenNonNumeric(this);");
				}
				html.append("'");
			}

			html.append(toAttr(doc, displayType));
			// 添加其他属性
			html.append(toOtherpropsHtml());

			html.append(">");
		}

		return html.toString();
	}

	public String toStart() {
		StringBuffer html = new StringBuffer();
		if (!StringUtil.isBlank(getTextType())) {
			if (getTextType().equalsIgnoreCase("text")) {
				html.append("<input type='text'  ");
			} else if (getTextType().equalsIgnoreCase("readonly")) {
				if (isBorderType()) {
					html.append("<input type='text' readonly tabIndex='-1'  style='border:0;'");
				} else {
					html.append("<input type='text' readonly tabIndex='-1' ");
				}
			} else if (getTextType().equalsIgnoreCase("hidden")) {
				html.append("<input type='hidden'");
			} else if (getTextType().equalsIgnoreCase("password")) {
				html.append("<input type='password'");
			} else {
				html.append("<input type='text' ");
			}
		} else {
			html.append("<input type='text' ");
		}

		// html.append(" onkeyup=");
		// html.append("\"this.value=this.value.replace(/[^\\d\\.\\,]/g,'')\"");
		// }
		return html.toString();
	}

	/**
	 * 生成属性
	 * 
	 * @param doc
	 * @param runner
	 * @param webUser
	 * @return
	 * @throws Exception
	 */
	private String toAttr(Document doc, int displayType) throws Exception {
		StringBuffer html = new StringBuffer();

		html.append(" id='" + getFieldId(doc) + "'");
		html.append(" name='" + getName() + "'");
		html.append(" fieldType='" + getTagName() + "'");

		if (isRefreshOnChanged()) {
			html.append(" onchange='dy_refresh(this.id)'");
		}

		html.append(" class='" + cssClass + "'");

		if (doc != null) {
			Item item = doc.findItem(this.getName());

			// 数字类型与文本类型取值
			if (item != null && item.getValue() != null) {
				Object value = item.getValue();
				if (value instanceof Number) {
					DecimalFormat format = new DecimalFormat(getNumberPattern());
					html.append(" value='" + format.format(item.getValue()) + "'");
				} else {
					if (value instanceof String) {
						String valueStr = HtmlEncoder.encode(value + "");
						valueStr = valueStr.replaceAll("'", "");
						valueStr = valueStr != null && !valueStr.equals("null") ? valueStr : "";
						if (!getTextType().equalsIgnoreCase("password")) {
							html.append(" title='").append(valueStr).append("'");
						}
						html.append(" value='").append(valueStr).append("'");
					} else {
						if (!getTextType().equalsIgnoreCase("password")) {
							html.append(" title='").append(value).append("'");
						}
						html.append(" value='").append(value).append("'");
					}
				}
			}
		}

		// 节点设置对应field只读或关闭
		if (displayType == PermissionType.READONLY) {
			if (isBorderType()) {
				html.append(" readonly tabIndex='-1' style='border:0'");
			} else {
				html.append(" readonly tabIndex='-1' ");
			}
		} else if (displayType == PermissionType.DISABLED)
			html.append(" disabled tabIndex='-1'");

		return html.toString();
	}

	public String toGridHtmlText(Document doc, IRunner runner, WebUser webUser) throws Exception {
		StringBuffer html = new StringBuffer();

		int displayType = getDisplayType(doc, runner, webUser);
		// 添加Field
		if (displayType == PermissionType.HIDDEN) {// 节点设置对应field隐藏
			return this.getHiddenValue();
		} else {
			html.append(toStart());
			// 改变onKeyDown属性
			// html.append(" onKeyDown='if (event.keyCode == 13){doAfterEdit(\""
			// + getFieldId(doc) + "\")};' ");
			html.append(toAttr(doc, displayType));
			html.append("style='width:100%'");
			// 添加其他属性
			// html.append(toOtherpropsHtml());

			html.append(">");
		}

		return html.toString();
	}
	
	/**
	 * 根据打印时对应InputField的显示类型不同,默认为MODIFY,返回的结果字符串不同.
	 * <P>
	 * 若Document不为空且打印时对应InputField的显示类型不为HIDDEN且字段类型不为HIDDEN,
	 * 并根据Form模版的文本框组件内容结合Document中的ITEM存放的值,返回重定义后的打印html文本. 否则为空字符串.
	 * 
	 * @param doc
	 *            Document
	 * @param runner
	 *            AbstractRunner(执行脚本的接口类)
	 * @param params
	 *            参数
	 * @param user
	 *            webuser
	 * 
	 * @see AbstractRunner#run(String, String)
	 * @return 字符串
	 * @throws Exception
	 */
	public String toPrintHtmlTxt(Document doc, IRunner runner, WebUser webUser) throws Exception {
		StringBuffer html = new StringBuffer();

		if (doc != null) {
			int displayType = getPrintDisplayType(doc, runner, webUser);

			if (displayType == PermissionType.HIDDEN) {
				return this.getPrintHiddenValue();
			}

			if (!getTextType().equalsIgnoreCase("hidden")) {
				Item item = doc.findItem(this.getName());
				if (item != null && item.getValue() != null) {
					Object value = item.getValue();
					html.append("<SPAN style=\"FONT-SIZE: 9pt\">");
					if (value instanceof Number) {
						DecimalFormat format = new DecimalFormat(getNumberPattern());
						html.append(format.format(item.getValue()));
					} else {
						html.append(HtmlEncoder.encode((String) item.getValue()));
					}
					html.append("</SPAN>");
					if (value instanceof Number) {
						DecimalFormat format = new DecimalFormat(getNumberPattern());
						html.append("<input type='hidden' name=\"" + item.getName() + "\" value=\""
								+ format.format(item.getValue()) + "\" />");
					} else {
						html.append("<input type='hidden' name=\"" + item.getName() + "\" value=\"" + item.getValue()
								+ "\" />");
					}

				}
			} else {
				html.append(printHiddenElement(doc));
			}
		}
		return html.toString();
	}

	/**
	 * 返回模板描述文本
	 * 
	 * @return java.lang.String
	 * @roseuid 41E7917A033F
	 */
	public String toTemplate() {
		StringBuffer template = new StringBuffer();
		template.append("<input type='" + this.getTextType() + "'");
		template.append(" class='" + cssClass + "'");
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
		template.append(" broderType='" + isBorderType() + "'");
		template.append(">");
		return template.toString();
	}

	/**
	 * 执行inputField值勤脚本,重新计算inputField.
	 * 
	 * @roseuid 41DB89D700F9
	 */
	public void recalculate(IRunner runner, Document doc, WebUser webUser) throws Exception {
		getLog().debug("InputField.recalculate");
		runValueScript(runner, doc);
	}

	/**
	 * 根据InputField名称，值类型与值创建相应的Item.
	 * 
	 * @param doc
	 *            Document 对象
	 * @return item
	 * @throws ParseException
	 * @roseuid 41EBD62F00BE
	 */
	public Item createItem(Document doc, Object value) {
		Item item = doc.findItem(getName());

		if (item == null) {
			item = new Item();
			try {
				item.setId(Tools.getSequence());
			} catch (Exception e) {
				e.printStackTrace();
			}
			item.setName(getName());
		}

		item.setName(getName());
		item.setType(this.getFieldtype());
		Object objValue = value;
		if (getFieldtype().equals(Item.VALUE_TYPE_NUMBER)) {
			if (value != null && ((String) value).trim().length() > 0) {
				DecimalFormat format = new DecimalFormat(getNumberPattern());
				try {
					objValue = format.parseObject((String) value);
				} catch (ParseException e) {
					getLog().warn(e.getMessage());
				}
			}
		}

		if (objValue != null) {
			item.setValue(objValue);
		}

		return item;
	}

	/**
	 * 获取显示值
	 */
	public String getText(Document doc, IRunner runner, WebUser webUser) throws Exception {
		if (!StringUtil.isBlank(doc.getParentid())) {
			int displayType = getDisplayType(doc, runner, webUser);
			if (displayType == PermissionType.HIDDEN) {
				return this.getHiddenValue();
			}
		}

		DecimalFormat format = new DecimalFormat(getNumberPattern());
		if (getFieldtype().equals(Item.VALUE_TYPE_NUMBER)) {
			return format.format(doc.getItemValueAsDouble(getName()));
		} else {
			return doc.getItemValueAsString(getName());
		}

	}

	/**
	 * 返回可以弹出一个对话视图文本框.
	 * 
	 * @hibernate.property column="dialogView"
	 * @uml.property name="dialogView"
	 */
	public String getDialogView() {
		return dialogView;
	}

	/**
	 * 设置可以弹出一个对话视图文本框
	 * 
	 * @param dialogView
	 * @uml.property name="dialogView"
	 */

	public void setDialogView(String dialogView) {
		this.dialogView = dialogView;
	}

	/**
	 * @hibernate.property column="popToChoice"
	 * @uml.property name="popToChoice"
	 */
	public boolean isPopToChoice() {
		return popToChoice;
	}

	/**
	 * @param popToChoice
	 *            the popToChoice to set
	 * @uml.property name="popToChoice"
	 */
	public void setPopToChoice(boolean popToChoice) {
		this.popToChoice = popToChoice;
	}

	/**
	 * 返回数字显示模式
	 * 
	 * @return 数字显示模式
	 * @uml.property name="numberPattern"
	 */
	public String getNumberPattern() {
		return numberPattern.trim().length() > 0 ? numberPattern : "##.##";
	}

	/**
	 * 设置数字显示模式
	 * 
	 * @param numberPattern
	 * @uml.property name="numberPattern"
	 */
	public void setNumberPattern(String numberPattern) {
		this.numberPattern = numberPattern;
	}

	/**
	 * @hibernate.property column="suggest"
	 * @uml.property name="suggest"
	 */
	public String getSuggest() {
		return suggest;
	}

	/**
	 * @param suggest
	 * @uml.property name="suggest"
	 */

	public void setSuggest(String suggest) {
		this.suggest = suggest;
	}

	public String toMbXMLText(Document doc, IRunner runner, WebUser webUser) throws Exception {
		StringBuffer xmlText = new StringBuffer();
		int displayType = getDisplayType(doc, runner, webUser);

		// 添加Field
		if (getTextType() != null) {
			if (getTextType().equalsIgnoreCase("readonly")) {
				xmlText.append("<").append(MobileConstant.TAG_TEXTFIELD).append(" ")
						.append(MobileConstant.ATT_READONLY).append("='true' ");
			} else if (getTextType().equalsIgnoreCase("hidden")) {
				xmlText.append("<").append(MobileConstant.TAG_TEXTFIELD).append(" ").append(MobileConstant.ATT_TYPE)
						.append("='hidden'");
			} else if (getTextType().equalsIgnoreCase("password")) {
				xmlText.append("<").append(MobileConstant.TAG_TEXTFIELD).append(" ").append(MobileConstant.ATT_TYPE)
						.append("='password'");
			} else {
				xmlText.append("<").append(MobileConstant.TAG_TEXTFIELD).append(" ");
			}
			String name = getDiscript();

			if (name == null || name.trim().length() <= 0) {
				name = getName();
			}
			xmlText.append(" ").append(MobileConstant.ATT_LABEL).append("='").append(name).append("'");

			if (displayType == PermissionType.READONLY || displayType == PermissionType.DISABLED) {
				xmlText.append(" ").append(MobileConstant.ATT_DISABLED).append("='true' ");
			}
			if (displayType == PermissionType.HIDDEN) {
				xmlText.append(" ").append(MobileConstant.ATT_HIDDEN).append(" ='true' ");
			}

			if (isRefreshOnChanged()) {
				xmlText.append(" ").append(MobileConstant.ATT_REFRESH).append("='true' ");
			}

			xmlText.append(" ").append(MobileConstant.ATT_NAME).append("='" + getName() + "'>");

			if (doc != null) {
				Item item = doc.findItem(this.getName());

				if (item != null && item.getValue() != null) {
					Object value = item.getValue();
					if (value instanceof Number) {
						DecimalFormat format = new DecimalFormat(getNumberPattern());
						xmlText.append(format.format(item.getValue()));
					} else {
						if ((value instanceof String) && value.toString().trim().equals("")) {
						} else {
							String valueStr = HtmlEncoder.encode(value + "");
							xmlText.append(valueStr);
						}
					}
				}
			}

			xmlText.append("</").append(MobileConstant.TAG_TEXTFIELD).append(">");
		}
		return xmlText.toString();
	}

	public String getTagName() {
		if (getFieldtype().equals(Item.VALUE_TYPE_NUMBER)) {
			return "NumberField";
		}
		return super.getTagName();
	}

}
