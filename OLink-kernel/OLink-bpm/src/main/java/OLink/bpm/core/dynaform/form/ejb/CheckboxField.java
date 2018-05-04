//Source file:
//C:\\Java\\workspace\\SmartWeb3\\src\\com\\cyberway\\dynaform\\form\\ejb\\SelectField.java

package OLink.bpm.core.dynaform.form.ejb;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import OLink.bpm.core.macro.runner.IRunner;
import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.core.dynaform.document.ejb.Item;
import OLink.bpm.core.table.constants.MobileConstant;
import OLink.bpm.core.dynaform.PermissionType;
import OLink.bpm.core.dynaform.document.ejb.Document;
import OLink.bpm.core.macro.runner.AbstractRunner;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.util.HtmlEncoder;
import OLink.bpm.util.StringList;
import OLink.bpm.util.StringUtil;
import org.apache.log4j.Logger;

/**
 * Checkbox组件
 * 
 * @author nicholas
 */
public class CheckboxField extends FormField implements ValueStoreField {
	protected final static Logger log = Logger.getLogger(CheckboxField.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = -3929760258362916386L;

	protected static String cssClass = "checkbox-cmd";

	/**
	 * 计算多值选项
	 */
	protected String optionsScript;

	/**
	 * 换行个数
	 */
	protected String newlineCount = "3";

	/**
	 * 构造函数
	 * 
	 * @roseuid 41ECB66D031D
	 */
	public CheckboxField() {

	}

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
	 * 执行CheckboxField值勤脚本,重新计算CheckboxField.
	 * 
	 * @roseuid 41DB89D700F9
	 */
	public void recalculate(IRunner runner, Document doc, WebUser webUser) throws Exception {
		log.debug("CheckboxField.recalculate");
		runValueScript(runner, doc);
		runOptionsScript(runner, doc, webUser);
	}

	/**
	 * 
	 * Form模版的复选框组件内容结合Document中的ITEM存放的值,返回字符串为重定义后的html，
	 * 通过强化HTML标签及语法，表达复选框(checkbox)的属性、事件、样式、值等。
	 * 
	 * @param doc
	 *            文档对象
	 * @see CheckboxField#runOptionsScript(AbstractRunner,
	 *      Document, WebUser)
	 * @see ParamsTable#params
	 * @see AbstractRunner#run(String, String)
	 * @return 重定义后的html的复选框
	 * 
	 */
	public String toHtmlTxt(Document doc, IRunner runner, WebUser webUser) {
		StringBuffer html = new StringBuffer();
		try {
			html.append(runOptionsScript(runner, doc, webUser));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return html.toString();
	}

	/**
	 * 生成模块描述
	 */
	public String toTemplate() {
		StringBuffer template = new StringBuffer();
		template.append("<input type='radio'");
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
		template.append("/>");
		return template.toString();

	}

	/**
	 * 根据CheckboxField的显示类型不同,返回的结果字符串不同.
	 * 新建的Document,CheckboxField的显示类型为默认的MODIFY。此时根据Form模版的CheckboxField内容结合Document的Item的值,返回的字符串为重定义后的html.
	 * 同时流程节点设置对应CheckboxField的显示类型不同,返回的结果字符串不同.
	 * 1)若节点设置对应CheckboxField的显示类型为MODIFY.执行多值选项脚本后并根据Form模版的CheckboxField内容结合Document的item值,返回重定义后的html
	 * 2)若节点设置对应CheckboxField的显示类型为DISABLED或READONLY,
	 * 并根据Form模版的CheckboxField内容结合Document的item值且ITEM存放的值被DISABLED,返回重定义后的html.
	 * 通过强化HTML标签及语法，表达CheckboxField的布局、属性、事件、样式、等。 否则返回"******"字符串.
	 * 
	 * @param runner
	 *            AbstractRunner(执行脚本接口类)
	 * @param doc
	 *            文档
	 * @param webUser
	 * @return 字符串内容为html多值选项标签和语法
	 * @throws Exception
	 */
	public String runOptionsScript(IRunner runner, Document doc, WebUser webUser) throws Exception {
		//Object result = null;
		int displayType = getDisplayType(doc, runner, webUser);

		if (displayType == PermissionType.HIDDEN) {
			return this.getHiddenValue();
		}
		else {
			if (getOptionsScript() != null && getOptionsScript().trim().length() > 0) {

				Options options = getOptions(runner, doc);

				if (options != null) {
					StringBuffer html = new StringBuffer();

					Collection<String> checkedList = getCheckedList(runner, doc);

					if (doc != null) {
						Iterator<Option> iter = options.getOptions().iterator();
						int index = 1;
						// 默认空值
						html.append("<input type='checkbox' value='' name='" + this.getName() + "' style='display:none' checked></input>");

						while (iter.hasNext()) {
							Option element = iter.next();
							html.append("<input type='checkbox' value=");

							html.append("'");
							html.append(HtmlEncoder.encode(element.getValue()));
							html.append("'");
							html.append(" name='");
							html.append(this.getName());
							html.append("'");
                            /**
                             * add by alex 只读或普通 -->
                             */
							if ((this.getTextType() != null && this.getTextType().equalsIgnoreCase("READONLY"))){
								html.append(" disabled='disabled' ");
							}
							/**
							 * <-- add by alex 只读或普通
							 */
							if (isRefreshOnChanged()) {
								html.append(" onclick='dy_refresh(this.name)' ");
							}

							if (displayType == PermissionType.DISABLED || displayType == PermissionType.READONLY) {
								html.append(" disabled ");
							}

							if (checkedList.size() > 0) {
								if (checkedList.contains(element.getValue())) {
									html.append(" checked ");
								}
							} else {
								if (element.isDef()) {
									html.append(" checked ");
								}
							}

							html.append(toOtherpropsHtml());
							html.append(" class='" + cssClass + "'");
							html.append(">");
							html.append(element.getOption());
							html.append("</input>");
							if (this.getLayout() != null && this.getLayout().equalsIgnoreCase("vertical")) {
								html.append("<br>");
							} 
							index++;
						}
						return html.toString();
					}
				}
			}
		}
		return "";
	}

	/**
	 * 根据文档的字段获取选中的列表
	 * 
	 * @param runner
	 *            动态语言执行器
	 * @param doc
	 *            文档
	 * @return 选中列表的集合
	 */
	public Collection<String> getCheckedList(IRunner runner, Document doc) {
		try {
			String checkedListStr = doc.getItemValueAsString(this.getName());

			if (!StringUtil.isBlank(checkedListStr)) {
				return Arrays.asList(checkedListStr.split(";"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return new ArrayList<String>();
	}

	/**
	 * 根据打印时对应CheckboxField的显示类型不同,默认为MODIFY,返回的结果字符串不同.
	 * 打印时对应CheckboxField的显示类型默认为MODIFY.
	 * 若Document不为空且打印时对应CheckboxField的显示类型不为HIDDEN,
	 * <p>
	 * 并根据Form模版的CheckboxField(多值选项组件)内容结合Document中的ITEM存放的值,返回重定义后的打印html文本.
	 * 否则为空字符串.
	 * 
	 * @param doc
	 *            Document
	 * @param runner
	 *            动态语言执行器
	 * @param params
	 *            参数
	 * @param user
	 *            webuser
	 * 
	 * @see AbstractRunner#run(String, String)
	 * @return Form模版的多值选项组件内容结合Document中的ITEM存放的值为重定义后的打印html
	 * @throws Exception
	 */
	public String toPrintHtmlTxt(Document doc, IRunner runner, WebUser webUser) throws Exception {
		//Object result = null;

		if (doc != null) {
			int displayType = getPrintDisplayType(doc, runner, webUser);
			if (displayType == PermissionType.HIDDEN) {
				return this.getPrintHiddenValue();
			}

			if (getOptionsScript() != null && getOptionsScript().trim().length() > 0) {

				//result = runner.run(getScriptLable("toPrintHtmlTxt"), StringUtil.dencodeHTML(getOptionsScript()));

				Options options = getOptions(runner, doc);

				if (options != null) {
					Object value = null;
					StringList valueList = null;
					StringBuffer html = new StringBuffer();
					Item item = doc.findItem(this.getName());

					if (item != null)
						value = item.getValue();
					if (value != null)
						valueList = new StringList((String) value, ';');

					Iterator<Option> iter = options.getOptions().iterator();
					html.append("<SPAN style=\"FONT-SIZE: 9pt\">");
					StringBuffer val = new StringBuffer();
					while (iter.hasNext()) {
						Option element = iter.next();

						if (valueList != null && element.getValue() != null) {
							if (valueList.indexOf(element.getValue()) >= 0) {
								if (this.getLayout() != null && this.getLayout().equalsIgnoreCase("vertical")) {
									val.append(element.getOption()).append("<br>");
								} else {
									val.append(element.getOption()).append(";");
								}
							}
						}
					}

					if (this.getLayout() != null && this.getLayout().equalsIgnoreCase("vertical")) {
						
						String temp = val.substring(0, val.length() - 1);
						val.setLength(0);
						val.append(temp);
					}
					html.append(val);
					html.append("</SPAN>");
					html.append( printHiddenElement(doc));
					return html.toString();
				}
			}
		}
		return "";
	}

	/**
	 * 
	 */
	public boolean isRender(String destVal, String origVal) {
		if (optionsScript != null && optionsScript.trim().length() > 0) {
			return true;
		}
		return super.isRender(destVal, origVal);
	}

	/**
	 * 生成手机所支持的标记,只提供给手机客端使用
	 */
	public String toMbXMLText(Document doc, IRunner runner, WebUser webUser) throws Exception {

		try {
			/*
			 * <CheckBoxField label=’Field Label’> <Option>Option Text1</Option>
			 * <Option>Option Text2</Option> </CheckBoxField>
			 */

			Object result = null;
			int displayType = getDisplayType(doc, runner, webUser);

			if (getOptionsScript() != null && getOptionsScript().trim().length() > 0) {

				result = runner.run(getScriptLable("OptionsScript"), StringUtil.dencodeHTML(getOptionsScript()));

				Options options = null;
				if (result != null && result instanceof String) {

					String[] strlst = ((String) result).split(";");
					options = new Options();
					for (int i = 0; i < strlst.length; i++) {
						String[] optstr = strlst[i].split(":");
						if (optstr.length >= 2) {
							options.add(optstr[0], optstr[1]);
						} else {
							options.add(strlst[i], strlst[i]);
						}
					}
				} else if (result instanceof Options) {
					options = (Options) result;
				}

				if (options != null) {
					StringBuffer xmlText = new StringBuffer();

					Collection<String> checkedList = getCheckedList(runner, doc);

					if (doc != null) {
						String name = this.getDiscript();

						if (name == null || name.trim().length() <= 0) {
							name = getName();
						}
						xmlText.append("<").append(MobileConstant.TAG_CHECKBOXFIELD).append(" ").append("").append(
								MobileConstant.ATT_LABEL).append("='").append(name).append("' ").append(MobileConstant.ATT_NAME)
								.append(" ='").append(getName()).append("'");

						if (isRefreshOnChanged()) {
							xmlText.append(" ").append(MobileConstant.ATT_REFRESH).append("='true' ");
						}

						if (displayType == PermissionType.DISABLED || displayType == PermissionType.READONLY) {
							xmlText.append(" ").append(MobileConstant.ATT_READONLY).append("='true'");
						}
						if (displayType == PermissionType.HIDDEN) {
							xmlText.append(" ").append(MobileConstant.ATT_HIDDEN).append(" ='true' ");
						}

						xmlText.append(">");

						Iterator<Option> iter = options.getOptions().iterator();
						int count = 0;
						while (iter.hasNext()) {

							Option element = iter.next();

							xmlText.append("<").append(MobileConstant.TAG_OPTION).append(" ").append(MobileConstant.ATT_VALUE)
									.append(" = ");

							xmlText.append("'");
							xmlText.append(HtmlEncoder.encode(element.getValue()));
							xmlText.append("'");
							if (checkedList.size() > 0) {
								if (checkedList.contains(element.getValue())) {
									xmlText.append(" ").append(MobileConstant.ATT_SELECTED).append("='" + count + "'");
								}
							} else {
								if (element.isDef()) {
									xmlText.append(" ").append(MobileConstant.ATT_SELECTED).append("='" + count + "'");
								}
							}
							xmlText.append(">");

							xmlText.append(HtmlEncoder.encode(element.getOption()));
							xmlText.append("</").append(MobileConstant.TAG_OPTION).append(">");
						}

						xmlText.append("</").append(MobileConstant.TAG_CHECKBOXFIELD).append(">");

						return xmlText.toString();
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return "";
	}

	protected String toHiddenHtml(Document doc) {
		StringBuffer builder = new StringBuffer();
		builder.append(super.toHiddenHtml(doc));
		builder.append("<input");
		builder.append(" name='").append(getName()).append("'");
		builder.append(" type='hidden'");
		builder.append(" value='");
		try {
			builder.append(doc.getItemValueAsString(getName()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		builder.append("' >");
		return builder.toString();
	}

	/**
	 * 获取选项
	 * 
	 * @param runner
	 *            动态语言执行器
	 * @param doc
	 *            文档标识
	 * @return 选项对象
	 * @throws Exception
	 */
	private Options getOptions(IRunner runner, Document doc) throws Exception {
		Object result = null;
		result = runner.run(getScriptLable("OptionsScript"), StringUtil.dencodeHTML(getOptionsScript()));

		Options options = null;
		if (result != null && result instanceof String) {

			String[] strlst = ((String) result).split(";");
			options = new Options();
			for (int i = 0; i < strlst.length; i++) {
				String[] optstr = strlst[i].split(":");
				if (optstr.length >= 2) {
					options.add(optstr[0], optstr[1]);
				} else {
					options.add(strlst[i], strlst[i]);
				}
			}
		} else if (result instanceof Options) {
			options = (Options) result;
		}
		return options;

	}

	public String toGridHtmlText(Document doc, IRunner runner, WebUser webUser) throws Exception {

		int displayType = getDisplayType(doc, runner, webUser);

		if (displayType == PermissionType.HIDDEN) {
			return this.getHiddenValue();
		} else {
			if (getOptionsScript() != null && getOptionsScript().trim().length() > 0) {
				Options options = getOptions(runner, doc);

				if (options != null) {
					StringBuffer html = new StringBuffer();

					Collection<String> checkedList = getCheckedList(runner, doc);

					if (doc != null) {
						Iterator<Option> iter = options.getOptions().iterator();
						while (iter.hasNext()) {
							Option element = iter.next();
							html.append("<input type='checkbox' value=");

							html.append("'");
							html.append(HtmlEncoder.encode(element.getValue()));
							html.append("'");
							html.append(" id='").append(getFieldId(doc)).append("'");
							html.append(" name='").append(getFieldId(doc)).append("'");
							html.append(" fieldType='" + getTagName() + "'");

							if (isRefreshOnChanged()) {
								html.append(" onclick='dy_refresh(this.id)' ");
							}

							if (displayType == PermissionType.DISABLED || displayType == PermissionType.READONLY) {
								html.append(" disabled ");
							}

							if (checkedList.size() > 0) {
								if (checkedList.contains(element.getValue())) {
									html.append(" checked ");
								}
							} else {
								if (element.isDef()) {
									html.append(" checked ");
								}
							}

							html.append(toOtherpropsHtml());
							html.append(" class='" + cssClass + "'");
							html.append(" text='" + element.getOption() + "'");
							html.append(">");
							html.append(element.getOption());
							html.append("</input>");
							if (this.getLayout() != null && this.getLayout().equalsIgnoreCase("vertical")) {
								html.append("<br>");
							}
						}
						return html.toString();
					}
				}
			}
		}
		return "";
	}

	public String getText(Document doc, IRunner runner, WebUser webUser) throws Exception {
		if (!StringUtil.isBlank(doc.getParentid()) && getDisplayType(doc, runner, webUser) == PermissionType.HIDDEN) {
			return this.getHiddenValue();
		}

		StringBuffer text = new StringBuffer();
		Options options = getOptions(runner, doc);
		if (options != null) {
			String value = doc.getItemValueAsString(getName());
			String[] values = value.split(";");
			if (values != null && values.length > 0) {
				for (Iterator<Option> iterator = options.getOptions().iterator(); iterator.hasNext();) {
					Option option = iterator.next();
					for (int i = 0; i < values.length; i++) {
						if (option.getValue().equals(values[i])) {
							text.append(option.getOption()).append(";");
						}
					}
				}
				if (text.lastIndexOf(";") != -1) {
					text.deleteCharAt(text.lastIndexOf(";"));
				}
			}
		}

		return text.toString();
	}

	public String getValueMapScript() {
		StringBuffer scriptBuffer = new StringBuffer();
		scriptBuffer.append("putToValuesMap(valuesMap");
		scriptBuffer.append(",'").append(this.getName());
		scriptBuffer.append("',getCheckedListStr('");
		scriptBuffer.append(this.getName()).append("')");
		scriptBuffer.append(");");

		return scriptBuffer.toString();
	}
}
