//Source file:
//C:\\Java\\workspace\\SmartWeb3\\src\\com\\cyberway\\dynaform\\form\\ejb\\SelectField.java

package OLink.bpm.core.dynaform.form.ejb;

import java.util.Iterator;

import OLink.bpm.core.dynaform.document.ejb.Document;
import OLink.bpm.core.dynaform.document.ejb.Item;
import OLink.bpm.core.macro.runner.AbstractRunner;
import OLink.bpm.core.macro.runner.IRunner;
import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.core.macro.runner.JavaScriptRunner;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.util.HtmlEncoder;
import OLink.bpm.util.StringUtil;
import OLink.bpm.core.table.constants.MobileConstant;
import OLink.bpm.util.StringList;
import OLink.bpm.core.dynaform.PermissionType;

/**
 * @author nicholas
 */
public class RadioField extends FormField implements ValueStoreField {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7218067956139893236L;

	protected static String cssClass = "radio-cmd";

	/**
	 * 计算多值选项
	 */
	protected String optionsScript;

	/**
	 * @roseuid 41ECB66D031D
	 */
	public RadioField() {

	}

	/**
	 * 返回多值选项脚本
	 * 
	 * @return Returns the optionsScript.
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
	 * 执行RadioField值勤脚本，重新计算.
	 * 
	 * @param runner
	 *            动态脚本执行器
	 * @see JavaScriptRunner#run(String, String)
	 * @param doc
	 *            文档对象
	 * @param displayType
	 *            Document显示类型
	 * @throws Exception
	 */
	public void recalculate(IRunner runner, Document doc, WebUser webUser) throws Exception {
		getLog().debug("RadioField.recalculate");
		runOptionsScript(runner, doc, webUser);
		runValueScript(runner, doc);
	}

	/**
	 * 
	 * Form模版的RadioField组件内容结合Document中的ITEM存放的值,返回字符串为重定义后的html，
	 * 
	 * @param doc
	 *            文档对象
	 * @param runner
	 *            动态脚本执行器
	 * @param webUser
	 *            webuser
	 * @see ParamsTable#params
	 * @see AbstractRunner#run(String, String)
	 * @return 重定义后的html为Form模版的Radio组件内容结合Document中的ITEM存放的值
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
	 * 根据打印时对应RadioField的显示类型不同,默认为MODIFY,返回的结果字符串不同.
	 * 若Document不为空且打印时对应RadioField的显示类型不为HIDDEN,
	 * <p>
	 * 并根据Form模版的RadioField组件内容结合Document中的ITEM存放的值,返回重定义后的打印html文本
	 * 通过强化HTML标签及语法，表达RadioField的布局、属性、事件、样式、等。
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
	 */
	public String toPrintHtmlTxt(Document doc, IRunner runner, WebUser webUser) throws Exception {
		Object result = null;
		StringBuffer html = new StringBuffer();

		int displayType = getPrintDisplayType(doc, runner, webUser);

		if (displayType == PermissionType.HIDDEN) {
			return this.getPrintHiddenValue();
		}

		if (doc != null) {
			if (getOptionsScript() != null && getOptionsScript().trim().length() > 0) {

				result = runner.run(getScriptLable("toPrintHtmlTxt"), StringUtil.dencodeHTML(getOptionsScript()));

				Options options = null;
				if (result != null && result instanceof String) {

					String[] strlst = ((String) result).split(";");
					options = new Options();
					for (int i = 0; i < strlst.length; i++) {
						options.add(strlst[i], strlst[i]);
					}
				} else if (result instanceof Options) {
					options = (Options) result;
				}

				if (options != null) {
					Object value = null;
					StringList valueList = null;
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
						if(val.length()>0){
							String temp = val.substring(0, val.length() - 1);
							val.setLength(0);
							val.append(temp);
						}
						
					}
					html.append(val);
					html.append("</SPAN>");
					html.append(printHiddenElement(doc));
					return html.toString();
				}
			}
		}
//		return printHiddenElement(doc);
		return "";
	}

	/**
	 * 获取模板描述单选项
	 * 
	 * @return 模板描述单选项
	 * @roseuid 41E7917A033F
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
	 * 根据RadioField的显示类型不同,返回的结果字符串不同.
	 * 新建的Document,RadioField的显示类型为默认的MODIFY。此时根据Form模版的RadioField内容结合Document的Item的值,返回的字符串为重定义后的html.
	 * 根据流程节点设置对应RadioField的显示类型不同,默认为MODIFY,返回的结果字符串不同.
	 * 1）若节点设置对应RadioField的显示类型为HIDDEN,返回"******".
	 * 2）若节点设置对应RadioField的显示类型为READONLY,Document的ITEM存放的值为只读.
	 * 3）若节点设置对应RadioField的显示类型为DISABLED,Document的ITEM存放的值为DISABLED.
	 * 
	 * 并根据Form模版的RadioField执行多值选项脚本内容结合Document中的ITEM存放的值,返回字符串为重定义后的html.
	 * 通过强化HTML的单值选项标签及语法，表达单值选项的布局、属性、事件、样式、等。
	 * 
	 * @param runner
	 *            动态脚本执行器
	 * @param doc
	 *            文档对象
	 * @param webUser
	 *            webUser
	 * @see JavaScriptRunner#run(String, String)
	 * @return 字符串内容定义后的html的单值选项标签及语法
	 * @throws Exception
	 */
	public String runOptionsScript(IRunner runner, Document doc, WebUser webUser) throws Exception {
		Object result = null;
		StringBuffer html = new StringBuffer();
		String style ="";
		int displayType = getDisplayType(doc, runner, webUser);
		if (displayType == PermissionType.HIDDEN) {
			//style ="style='display:none'";
			return this.getHiddenValue();
		}
			if (getOptionsScript() != null && getOptionsScript().trim().length() > 0) {

				result = runner.run(getScriptLable("OptionsScript"), StringUtil.dencodeHTML(getOptionsScript()));

				Options options = null;
				if (result != null && result instanceof String) {

					String[] strlst = ((String) result).split(";");
					options = new Options();
					for (int i = 0; i < strlst.length; i++) {
						options.add(strlst[i], strlst[i]);
					}
				} else if (result instanceof Options) {
					options = (Options) result;
				}
				if (options != null && doc !=null) {
					Object value = null;
					StringList valueList = null;
					Item item = doc.findItem(this.getName());

					if (item != null)
						value = item.getValue();
					if (value != null)
						valueList = new StringList((String) value, ';');

					if (doc != null) {
						Object defvalue = "";
						boolean isreadonly = false;

						Iterator<Option> iter = options.getOptions().iterator();
						while (iter.hasNext()) {
							Option element = iter.next();

							if ((this.getTextType() != null && this.getTextType().equalsIgnoreCase("READONLY"))
									|| (displayType == PermissionType.DISABLED)
									|| (displayType == PermissionType.READONLY)) {
								isreadonly = true;
							}

							html.append("<input type='radio' value=");
							html.append("\"");
							
							html.append(HtmlEncoder.encode(element.getValue()));
							html.append("\"");
							html.append(" name='");
							if (isreadonly) {
								html.append(this.getName() + "$forshow");
							} else {
								html.append(this.getName());
							}

							html.append("'");

							if (isRefreshOnChanged()) {
								html.append(" onclick='dy_refresh(this.id)'");
							}

							if (isreadonly) {
								html.append(" disabled ");
							}

							if (valueList != null && element.getValue() != null) {
								if (valueList.indexOf(element.getValue()) >= 0) {
									defvalue = element.getValue();
									html.append(" checked ");
								}
							} else {
								if (element.isDef()) {
									defvalue = element.getValue();
									html.append(" checked ");
								}
							}
							html.append(toOtherpropsHtml());
							html.append(" class='" + cssClass + "'");
							html.append(" "+style);
							html.append(">");
							html.append("<span "+style +">");
							html.append(element.getOption());
							html.append("</span>");
							html.append("</input>");

							if (this.getLayout() != null && this.getLayout().equalsIgnoreCase("vertical")) {
								html.append("<br>");
							}
						}
						if (isreadonly) {
							html.append("<input type='hidden' name='" + getName() + "' value='" + defvalue + "'>");
						}
					}
				}
			}
		
		return html.toString();
	}

	public boolean isRender(String destVal, String origVal) {
		if (optionsScript != null && optionsScript.trim().length() > 0) {
			return true;
		}
		return super.isRender(destVal, origVal);
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
		int displayType = getDisplayType(doc, runner, webUser);
		if (getOptionsScript() != null && getOptionsScript().trim().length() > 0) {
			Options options = getOptions(runner, doc);

			if (options != null && doc != null) {
				Object value = null;
				StringList valueList = null;
				Item item = doc.findItem(this.getName());

				if (item != null)
					value = item.getValue();
				if (value != null)
					valueList = new StringList((String) value, ';');

				if (doc != null) {
					//Object defvalue = "";
					String name = getDiscript();
					if (name == null || name.trim().length() <= 0) {
						name = getName();
					}
					html.append("<").append(MobileConstant.TAG_RADIOFIELD).append(" ").append("").append(
							MobileConstant.ATT_LABEL).append("='").append(name).append("'");

					html.append(" ").append(MobileConstant.ATT_NAME).append("='" + getName() + "'");

					if (isRefreshOnChanged()) {
						html.append(" ").append(MobileConstant.ATT_REFRESH).append("='true' ");
					}
					if ((this.getTextType() != null && this.getTextType().equalsIgnoreCase("READONLY"))
							|| displayType == PermissionType.DISABLED || displayType == PermissionType.READONLY) {
						html.append(" ").append(MobileConstant.ATT_READONLY).append("='true' ");
					}
					if (displayType == PermissionType.HIDDEN) {
						html.append(" ").append(MobileConstant.ATT_HIDDEN).append(" ='true' ");
					}

					html.append(">");
					Iterator<Option> iter = options.getOptions().iterator();
					int count = 0;
					boolean flag = true;
					while (iter.hasNext()) {
						Option element = iter.next();

						html.append("<").append(MobileConstant.TAG_OPTION).append(
								"  " + MobileConstant.ATT_VALUE + " = ");
						html.append("'");
						html.append(HtmlEncoder.encode(element.getValue()));
						html.append("'");
						if (flag) {
							if (valueList != null && element.getValue() != null) {
								if (valueList.indexOf(element.getValue()) >= 0) {
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
						html.append(">");
						html.append(HtmlEncoder.encode(element.getOption()));
						html.append("</").append(MobileConstant.TAG_OPTION).append(">");
						count++;
					}
					html.append("</").append(MobileConstant.TAG_RADIOFIELD).append(">");
				}
			}
		}
		return html.toString();
	}

	/**
	 * 根据Form模版的RadioField组件内容结合Document中的ITEM存放的值,输出重定义后的html文本以网格显示
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
			if (getOptionsScript() != null && getOptionsScript().trim().length() > 0) {
				Options options = getOptions(runner, doc);

				if (options != null && doc !=null) {
					Object value = null;
					StringList valueList = null;
					Item item = doc.findItem(this.getName());

					if (item != null)
						value = item.getValue();
					if (value != null)
						valueList = new StringList((String) value, ';');

					if (doc != null) {
						Object defvalue = "";
						boolean isreadonly = false;

						Iterator<Option> iter = options.getOptions().iterator();
						while (iter.hasNext()) {
							Option element = iter.next();

							if ((this.getTextType() != null && this.getTextType().equalsIgnoreCase("READONLY"))
									|| (displayType == PermissionType.DISABLED)
									|| (displayType == PermissionType.READONLY)) {
								isreadonly = true;
							}

							html.append("<input type='radio' value=");
							html.append("\"");
							html.append(HtmlEncoder.encode(element.getValue()));
							html.append("\"");

							// 生成ID， ID与Name相同
							String id = getFieldId(doc);
							if (isreadonly) {
								id += "$forshow";
							}
							// 修改部分
							html.append(" id='").append(id).append("'");
							html.append(" name='").append(id).append("'");
							// 修改部分结束
							html.append(" fieldType='" + getTagName() + "'");

							if (isRefreshOnChanged()) {
								html.append(" onclick='dy_refresh(this.id)'");
							}

							if (isreadonly) {
								html.append(" disabled ");
							}

							if (valueList != null && element.getValue() != null) {
								if (valueList.indexOf(element.getValue()) >= 0) {
									defvalue = element.getValue();
									html.append(" checked ");
								}
							} else {
								if (element.isDef()) {
									defvalue = element.getValue();
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
						if (isreadonly) {
							html.append("<input type='hidden' name='" + getName() + "' value='" + defvalue + "'>");
						}
					}
				}
			}
		}
		return html.toString();
	}

	private Options getOptions(IRunner runner, Document doc) throws Exception {
		Object result = null;
		result = runner.run(getScriptLable("OptionsScript"), StringUtil.dencodeHTML(getOptionsScript()));

		Options options = null;
		if (result != null && result instanceof String) {

			String[] strlst = ((String) result).split(";");
			options = new Options();
			for (int i = 0; i < strlst.length; i++) {
				options.add(strlst[i], strlst[i]);
			}
		} else if (result instanceof Options) {
			options = (Options) result;
		}
		return options;
	}

	/**
	 * 获取Form模版的RadioField的选项脚本，通过动态语言执行器执行脚本后生成Radio,并验证Radio的可见性,如果可见就显示正常,否则显示"******"
	 * 
	 * @param runner
	 *            动态脚本执行器
	 * @param doc
	 *            文档对象
	 * @param webUser
	 *            webUser
	 * @return 执行后生成的Radio
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
