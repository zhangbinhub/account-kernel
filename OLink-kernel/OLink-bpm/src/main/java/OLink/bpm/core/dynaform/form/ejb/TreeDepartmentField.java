package OLink.bpm.core.dynaform.form.ejb;

import java.util.Collection;
import java.util.Iterator;

import OLink.bpm.core.dynaform.document.ejb.Item;
import OLink.bpm.core.macro.runner.IRunner;
import OLink.bpm.core.table.constants.MobileConstant;
import OLink.bpm.core.department.ejb.DepartmentProcess;
import OLink.bpm.core.dynaform.PermissionType;
import OLink.bpm.core.dynaform.document.ejb.Document;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.util.HtmlEncoder;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.core.department.ejb.DepartmentVO;
import OLink.bpm.util.StringUtil;

public class TreeDepartmentField extends FormField implements ValueStoreField{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7936079412655964741L;

	protected static String cssClass = "department-cmd";
	private String textFieldId="";//真实值
	private String valueFieldId="";//id

	/**
	 * 限制选择部门的数量
	 */
	protected String limit;
	/**
	 * 获得限制选择部门的数量
	 * @return
	 */
	public String getLimit() {
		return limit;
	}
	/**
	 * 设置限制选择部门的数量
	 * @param limit
	 */
	public void setLimit(String limit) {
		this.limit = limit;
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
		StringBuffer xmlText = new StringBuffer();
		int displayType = getDisplayType(doc, runner, webUser);

		if (doc != null) {
			xmlText.append("<").append(MobileConstant.TAG_SELECTFIELD);
			xmlText.append(" ").append(MobileConstant.ATT_ID + "='" + getId() + "'");
			xmlText.append(" ").append(MobileConstant.ATT_NAME + "='" + getName() + "'");
			xmlText.append(" ").append(MobileConstant.ATT_LABEL).append("='").append(getName()).append("'");

			if (displayType == PermissionType.READONLY || (getTextType() != null && getTextType().equalsIgnoreCase("readonly"))
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
				xmlText.append(toOptionXML(doc, webUser));
				xmlText.append("</").append(MobileConstant.TAG_SELECTFIELD + ">");
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		return xmlText.toString();
	}

	private String toOptionXML(Document doc, WebUser webUser) {
		StringBuffer html = new StringBuffer();
		try {
			Collection<Option> deptOptions = getDepartmentOptions(doc, webUser).getOptions();
			Object value = null;
			if (doc != null) {
				Item item = doc.findItem(this.getName());
				if (item != null)
					value = item.getValue();
			}

			Iterator<Option> iter = deptOptions.iterator();
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
		} catch (Exception e) {
			e.printStackTrace();
		}
		html.append("<").append(MobileConstant.TAG_OPTION).append(">");
		html.append("</").append(MobileConstant.TAG_OPTION).append(">");
		return html.toString();
	}

	/**
	 * 获取模板描述
	 * 
	 * @return 模板描述
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
		template.append("/>");
		return template.toString();
	}

	/**
	 * 根据Form模版的UserField组件内容结合Document中的ITEM存放的值,输出重定义后的html文本以网格显示
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
		return toHtmlTxt(doc,runner,webUser);
	}

	/**
	 * Form模版的动态组件内容结合Document中的ITEM存放的值,返回字符串为重定义后的生成html文本
	 * 
	 * @param doc
	 *            Document(文档对象)
	 * @param runner
	 *            动态脚本执行器
	 * @param webUser
	 *            webUser
	 * @return 重定义后的生成html文本
	 * @throws Exception
	 */
	public String toHtmlTxt(Document doc, IRunner runner, WebUser webUser) throws Exception {
		StringBuffer html = new StringBuffer();
		int displayType = getDisplayType(doc, runner, webUser);
		textFieldId = getFieldId(doc) + "_text";
		valueFieldId = getFieldId(doc);
		if (displayType == PermissionType.HIDDEN) {
			return this.getHiddenValue();
		} else {
			if (doc != null) {
				// Text Field HTML
				if (getTextType().equalsIgnoreCase("hidden")) {
					html.append("<input type='hidden'");
				} else {
					html.append("<input type='text' readonly");
				}
				html.append(toAttr(doc, displayType));
				html.append(" value='");
				html.append(getFieldText(doc));
				html.append("'");
				html.append("/>");
				try {
					// 按钮部分
					if (!getTextType().equalsIgnoreCase("hidden")) {
						// User Select Settings
						String settings = "{textField:'"+textFieldId+"', valueField:'"+valueFieldId+"',limit:'"+limit+"'";
						if (isRefreshOnChanged()) {
							settings += ", callback: dy_refresh";
						}
						if (displayType == PermissionType.READONLY || getTextType().equals("readonly")
								|| displayType == PermissionType.DISABLED) {
							settings += ", readonly:true ";
						}
						settings += "}";

						html.append("<input type='hidden' id='" +valueFieldId+ "' name='" + getName() + "'");
						html.append(" value='");
						html.append(getFieldValue(doc));
						html.append("'");
						html.append("/>");
						
						if (displayType == PermissionType.READONLY || getTextType().equals("readonly")
								|| displayType == PermissionType.DISABLED) {//当为只读是，不允许点击。modify by Dolly 2011-3-21
							html.append("<input type='button' title='{*[Readonly]*}{*[State]*}' class='button_searchdel3'/>");
						}else{
							html.append("<input type='button' style='cursor: pointer;' title='click to select...' onclick=\"showDepartmentSelect('actionName', " + settings + ")\" class='button_searchdel3'/>");
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}
		return html.toString();
	}

	/**
	 * 生成控件相关属性
	 * 
	 * @param doc
	 * @param displayType
	 * @return
	 */
	public String toAttr(Document doc, int displayType) {
		StringBuffer html = new StringBuffer();
		html.append(" id='"+textFieldId+"'");
		html.append(" fieldType='" + getTagName() + "'");
		html.append(" class='" + cssClass + "'");
		return html.toString();
	}

	/**
	 * 获取表单域真实值
	 * 
	 * @param doc
	 * @return
	 */
	protected String getFieldValue(Document doc) {
		String rtn = "";
		if (doc != null) {
			Item item = doc.findItem(getName());
			// 文本类型取值
			if (item != null && item.getValue() != null) {
				Object value = item.getValue();
				if (StringUtil.isBlank((String) value)) {
				} else {
					String valueStr = HtmlEncoder.encode(value +"");
					valueStr = valueStr != null && !valueStr.equals("null") ? valueStr : "";
					rtn = valueStr;
				}
			}
		}
		return rtn;
	}
	
	/**
	 * 获取表单域显示值
	 * 
	 * @param doc
	 * @return
	 */
	protected String getFieldText(Document doc) {
		StringBuffer rtn = new StringBuffer();
		try {
			DepartmentProcess process = (DepartmentProcess) ProcessFactory.createProcess(DepartmentProcess.class);

			String valueListStr = getFieldValue(doc);
			if (!StringUtil.isBlank(valueListStr)) {
				String[] values = valueListStr.split(";");
				if (values.length > 0) {
					for (int i = 0; i < values.length; i++) {
						DepartmentVO department = (DepartmentVO) process.doView(values[i]);
						if (department != null) {
							rtn.append(department.getName()).append(";");
						}
					}
					rtn.deleteCharAt(rtn.lastIndexOf(";"));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rtn.toString();
	}

	/**
	 * @打印
	 */
	public String toPrintHtmlTxt(Document doc, IRunner runner, WebUser webUser) throws Exception {
		StringBuffer html = new StringBuffer();
		int displayType = getPrintDisplayType(doc, runner, webUser);

		if (displayType == PermissionType.HIDDEN) {
			return this.getPrintHiddenValue();
		}

		if (!getTextType().equalsIgnoreCase("hidden")) {
			Item item = doc.findItem(this.getName());
			if (item != null && item.getValue() != null) {
				html.append("<SPAN style=\"FONT-SIZE: 9pt\">");
				html.append(getFieldText(doc));
				html.append("</SPAN>");
				html.append(printHiddenElement(doc));
			}
		}
		return html.toString();

	}

	/**
	 * If the option value is default, return true.
	 * 
	 * @param doc
	 * @param optionValue
	 * @return true or false
	 */
	private boolean isDefaultValue(Document doc, String optionValue) {
		Item item = doc.findItem(getName());
		return item != null && optionValue.equals(item.getValue());
	}
	
	/**
	 * 获取字段显示值
	 * 
	 * @param doc
	 * @return
	 * @throws Exception
	 */
	public String getText(Document doc, IRunner runner, WebUser webUser)
	throws Exception {
	String fileFullName = getFieldText(doc);
	if (fileFullName!=null) {
			return fileFullName;
	}
	return super.getText(doc, runner, webUser);
	}
	
	/**
	 * 获取部门组件的下拉选项
	 * 
	 * @param doc
	 *            Document(文档对象)
	 * @param webUser
	 *            webUser
	 * @return 获取用户选项
	 * @throws Exception
	 */
	private Options getDepartmentOptions(Document doc, WebUser webUser) throws Exception {
		Options options = new Options();

		DepartmentProcess process = (DepartmentProcess) ProcessFactory.createProcess(DepartmentProcess.class);
		Collection<DepartmentVO> tempDeptList = null;
			tempDeptList = process.queryByDomain(webUser.getDomainid());

		for (Iterator<DepartmentVO> iterator = tempDeptList.iterator(); iterator.hasNext();) {
			DepartmentVO departmentVO = iterator.next();
			options.add(departmentVO.getName(), departmentVO.getId(), isDefaultValue(doc, departmentVO.getId()));
		}

		return options;
	}
}
