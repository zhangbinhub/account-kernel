package OLink.bpm.core.dynaform.form.ejb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import OLink.bpm.core.department.ejb.DepartmentProcess;
import OLink.bpm.core.department.ejb.DepartmentVO;
import OLink.bpm.core.dynaform.PermissionType;
import OLink.bpm.core.dynaform.document.ejb.Document;
import OLink.bpm.core.macro.runner.IRunner;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.util.StringUtil;
import OLink.bpm.core.dynaform.document.ejb.Item;
import OLink.bpm.core.table.constants.MobileConstant;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.util.HtmlEncoder;

public class DepartmentField extends FormField implements ValueStoreField {

	private static final long serialVersionUID = -8469984159928806643L;

	protected static String cssClass = "department-cmd";

	protected static final int DEFAULT_TYPE_FIRST_OPTION = 16;
	protected static final int DEFAULT_TYPE_DEPARTMENT_OF_USER = 256;

	/**
	 * 计算多值选项
	 * 
	 * @uml.property name="departmentLevel"
	 */
	protected int departmentLevel;

	protected boolean limitByUser;

	protected int defaultOptionType;

	protected boolean allowEmpty;

	/**
	 * 级联的部门Field ID, 即上一级部门
	 * 
	 */
	protected String relatedField;

	/**
	 * 设置是否连动关联字段
	 * 
	 * @return 是否连动关联字段
	 */
	public String getRelatedField() {
		return relatedField;
	}

	/**
	 * 设置是否连动关联字段
	 * 
	 * @param relatedField
	 *            是否连动关联字段
	 */
	public void setRelatedField(String relatedField) {
		this.relatedField = relatedField;
	}

	/**
	 * 返回多值选项脚本
	 * 
	 * @return Returns the departmentLevel.
	 * @uml.property name="departmentLevel"
	 */
	public int getDepartmentLevel() {
		return departmentLevel;
	}

	/**
	 * 设置多值选项脚本
	 * 
	 * @param optionsScript
	 *            The optionsScript to set.
	 * @uml.property name="optionsScript"
	 */
	public void setDepartmentLevel(int departmentLevel) {
		this.departmentLevel = departmentLevel;
	}

	/**
	 * 重新计算SelectField.
	 * 
	 * @roseuid 41DB89D700F9
	 */
	public void recalculate(IRunner runner, Document doc, WebUser webUser) throws Exception {
		getLog().debug("SelectField.recalculate");
		Collection<Option> deptOptions = getDepartmentOptions(doc, webUser).getOptions();
		boolean isClear = true;
		if (!deptOptions.isEmpty()) {
			for (Iterator<Option> iterator = deptOptions.iterator(); iterator.hasNext();) {
				Option option = iterator.next();
				String value = doc.getItemValueAsString(this.getName());
				if (option.getValue().equals(value)) {
					isClear = false;
					break;
				}
			}
		}
		if (isClear) {
			Item item = doc.findItem(this.getName());
			if (item != null) {
				item.setValue("");
			}
		}
	}

	public String toHtmlTxt(Document doc, IRunner runner, WebUser webUser) throws Exception {
		StringBuffer html = new StringBuffer();
		int displayType = getDisplayType(doc, runner, webUser);

		if (displayType == PermissionType.HIDDEN) {
			html.append("<select");
			html.append(" style='display:none");
			html.append("'");
			html.append(toAttr(doc, displayType));
			html.append(" " + toOtherpropsHtml());
			html.append(">");
			try {
				html.append(toOptionsHtml(doc, webUser));
				html.append("</select>");
			} catch (Exception e) {
				e.printStackTrace();
			}
			html.append(this.getHiddenValue());
			return html.toString();
		} else {
			if (doc != null) {
				html.append("<select");
				html.append(" style='display:");
				html.append(getTextType().equals("hidden") ? "none" : "inline");
				html.append("'");
				html.append(toAttr(doc, displayType));
				html.append(" " + toOtherpropsHtml());
				html.append(">");
				try {
					html.append(toOptionsHtml(doc, webUser));
					html.append("</select>");
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}
		return html.toString();
	}

	public String toAttr(Document doc, int displayType) {
		StringBuffer html = new StringBuffer();
		html.append(" id='" + getFieldId(doc) + "'");
		html.append(" name='" + getName() + "'");
		html.append(" name = '" + getName() + "'");
		html.append(" fieldType='" + getTagName() + "'");
		if (displayType == PermissionType.READONLY || getTextType().equals("readonly")) {
			html.append(" disabled ");
		} else if (displayType == PermissionType.DISABLED) {
			html.append(" disabled ");
		}
		if (isRefreshOnChanged()) {
			html.append(" onchange='dy_refresh(this.id)'");
		}

		return html.toString();
	}

	private String toOptionsHtml(Document doc, WebUser webUser) {
		StringBuffer htmlBuilder = new StringBuffer();
		try {
			Collection<Option> deptOptions = new ArrayList<Option>();
			// 是否允许空选项
			if (allowEmpty) {
				deptOptions.add(new Option("", ""));
			}
			deptOptions.addAll(getDepartmentOptions(doc, webUser).getOptions());

			// 设置默认值
			setDefaultValue(doc, deptOptions, webUser);

			// 输出选项HTML
			for (Iterator<Option> iterator = deptOptions.iterator(); iterator.hasNext();) {
				Option option = iterator.next();
				if (option.getValue() != null) {
					htmlBuilder.append("<option value=");
					htmlBuilder.append("\"");
					htmlBuilder.append(option.getValue());
					htmlBuilder.append("\"");
					if (option.isDef()) {
						htmlBuilder.append(" selected ");
					}
					htmlBuilder.append(" class='" + cssClass + "'");
					htmlBuilder.append(">");
					htmlBuilder.append(option.getOption());
					htmlBuilder.append("</option>");
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return htmlBuilder.toString();

	}

	public void setDefaultValue(Document doc, Collection<Option> deptOptions, WebUser webUser) {
		// 是否已有选项
		boolean isSelectedOne = false;
		for (Iterator<Option> iterator = deptOptions.iterator(); iterator.hasNext();) {
			Option option = iterator.next();
			if (option.isDef()) {
				isSelectedOne = true;
			}
		}

		// 设置默认选项
		if (!isSelectedOne) {
			Option option = null;
			Option defOption = null;
			switch (defaultOptionType) {
			case DEFAULT_TYPE_DEPARTMENT_OF_USER:
				Collection<DepartmentVO> departments = webUser.getDepartments();
				for (Iterator<Option> iterator = deptOptions.iterator(); iterator.hasNext();) {
					option = iterator.next();
					if (departments != null && !departments.isEmpty()) {
						DepartmentVO dept = departments.iterator().next();
						if (dept.getId().equals(option.getValue())) {
							defOption = option;
							option.setDef(true);
							break;
						}
					}
				}
				break;
			case DEFAULT_TYPE_FIRST_OPTION:
			default:
				// 第一个为默认选项
				if (!deptOptions.isEmpty()) {
					option = deptOptions.iterator().next();
					defOption = option;
					option.setDef(true);
				}
				break;
			}

			// 为文档设置默认值
			Item item = doc.findItem(getName());
			if (item != null && defOption != null) {
				item.setValue(defOption.getValue());
			}
		}
	}

	/**
	 * 
	 * 拿到的什来拿到对应的部门 0时返回一个null 此时会拿到关联的部门值，为1时就是最顶给部门 ，下一级为2-1；
	 * 
	 * @return list
	 * @throws Exception
	 */
	private Options getDepartmentOptions(Document doc, WebUser webUser) throws Exception {
		Collection<DepartmentVO> deptList = new ArrayList<DepartmentVO>();
		Options options = new Options();

		DepartmentProcess process = (DepartmentProcess) ProcessFactory.createProcess(DepartmentProcess.class);
		Collection<DepartmentVO> tempDeptList = new ArrayList<DepartmentVO>();
		if (getDepartmentLevel() < 0) {
			tempDeptList.addAll(process.queryByDomain(webUser.getDomainid()));
		} else {
			tempDeptList.addAll(process.getDepartmentByLevel(getDepartmentLevel(), _form.getApplicationid(), webUser.getDomainid()));
		}

		if (!StringUtil.isBlank(relatedField)) {
			String superiorId = doc.getItemValueAsString(relatedField);
			if (!StringUtil.isBlank(superiorId)) {
				for (Iterator<DepartmentVO> iterator = tempDeptList.iterator(); iterator.hasNext();) {
					DepartmentVO tempDepartmentVO = iterator.next();

					DepartmentVO currentTempDepartmentVO = tempDepartmentVO;
					// Recursive get superior department
					while (currentTempDepartmentVO.getSuperior() != null) {
						if (currentTempDepartmentVO.getSuperior().getId().equals(superiorId)) {
							if (isAllowedDepartment(tempDepartmentVO, webUser)) {
								deptList.add(tempDepartmentVO);
							}
						}
						currentTempDepartmentVO = currentTempDepartmentVO.getSuperior();
					}

				}
			}
		} else {
			for (Iterator<DepartmentVO> iterator = tempDeptList.iterator(); iterator.hasNext();) {
				DepartmentVO departmentVO = iterator.next();
				if (isAllowedDepartment(departmentVO, webUser)) {
					deptList.add(departmentVO);
				}
			}
		}

		for (Iterator<DepartmentVO> iterator = deptList.iterator(); iterator.hasNext();) {
			DepartmentVO departmentVO = iterator.next();
			options.add(departmentVO.getName(), departmentVO.getId(), isDefaultValue(doc, departmentVO.getId()));
		}

		return options;
	}

	/**
	 * Check the department whether to show by current user's department list
	 * 
	 * @param needCheckDepartment
	 * @param webUser
	 * @return true|false
	 * @throws Exception
	 */
	private boolean isAllowedDepartment(DepartmentVO needCheckDepartment, WebUser webUser) throws Exception {
		if (isLimitByUser()) {
			Collection<DepartmentVO> userDepartmentList = webUser.getDepartments();
			String lowerDepartmentListStr = webUser.getLowerDepartmentList();

			for (Iterator<DepartmentVO> iterator = userDepartmentList.iterator(); iterator.hasNext();) {
				DepartmentVO userDepartment = iterator.next();
				if (userDepartment.getLevel() < needCheckDepartment.getLevel()) {
					if (lowerDepartmentListStr.indexOf(needCheckDepartment.getId()) != -1) {
						return true;
					}
				} else if (userDepartment.getLevel() == needCheckDepartment.getLevel()) {
					if (userDepartment.getId().equals(needCheckDepartment.getId())) {
						return true;
					}
				} else if (userDepartment.getLevel() > needCheckDepartment.getLevel()) {
					DepartmentVO currentDepartment = userDepartment;
					while (currentDepartment.getSuperior() != null) {
//						if (userDepartment.getSuperior().getId().equals(needCheckDepartment.getId())) {
//							return true;
//						}
						
						//不索取上级没选定的部门
						if (userDepartment.getId().equals(needCheckDepartment.getId())) {
							return true;
						}
						currentDepartment = currentDepartment.getSuperior();
					}
				}
			}

			return false;
		} else {
			return true;
		}
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
	 * 返回模板描述下拉选项
	 * 
	 * @return java.lang.String
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
		template.append(" departmentlevel='" + getDepartmentLevel() + "'");
		template.append(" fileFiled='" + getRelatedField() + "'");
		template.append("/>");
		return template.toString();
	}

	public String toPrintHtmlTxt(Document doc, IRunner runner, WebUser webUser) throws Exception {
		StringBuffer html = new StringBuffer();
		int displayType = getPrintDisplayType(doc, runner, webUser);
		
		if (displayType == PermissionType.HIDDEN) {
			return this.getPrintHiddenValue();
		}

		// Iterator iter = getDepartmentOptions(doc,
		// webUser).getOptions().iterator();
		// while (iter.hasNext()) {
		// Option element = (Option) iter.next();
		// if (element.getValue() != null) {
		// html.append("<SPAN style=\"FONT-SIZE: 9pt\">");
		// html.append(element.getOption());
		// html.append("</SPAN>");
		// }
		// }

		if (!getTextType().equalsIgnoreCase("hidden")) {
			Item item = doc.findItem(this.getName());
			if (item != null && item.getValue() != null) {
				Object value = item.getValue();
				html.append("<SPAN style=\"FONT-SIZE: 9pt\">");
				// if (value instanceof Number) {
				// DecimalFormat format = new DecimalFormat(getNumberPattern());
				// html.append(format.format((Number) item.getValue()));
				// } else {
				DepartmentProcess process = (DepartmentProcess) ProcessFactory.createProcess(DepartmentProcess.class);
				DepartmentVO vo = (DepartmentVO) process.doView(value.toString());
				html.append(vo.getName());
				// }
				html.append("</SPAN>");
				
				html.append(printHiddenElement(doc));

			}
		}else{
			    html.append(printHiddenElement(doc));
		}
		return html.toString();

	}

	public boolean isRender(String destVal, String origVal) {
		if (departmentLevel == 0 || StringUtil.isBlank(relatedField)) {
			return super.isRender(destVal, origVal);
		}
		return true;
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

	/**
	 * 获取是否过滤用户
	 * 
	 * @return 是否过滤用户
	 */
	public boolean isLimitByUser() {
		return limitByUser;
	}

	/**
	 * 设置是否过滤用户
	 * 
	 * @param isLimitByUser
	 *            是否过滤用户
	 */
	public void setLimitByUser(boolean isLimitByUser) {
		this.limitByUser = isLimitByUser;
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
				html.append("<select");
				html.append(" style='display:");
				html.append(getTextType().equals("hidden") ? "none" : "inline");
				html.append(";width:100%'");
				html.append(toAttr(doc, displayType));
				html.append(">");
				try {
					html.append(toOptionsHtml(doc, webUser));
					html.append("</select>");
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}
		return html.toString();
	}

	/**
	 * 获取组件名
	 */
	public String getTagName() {
		return "SelectField";
	}

	public String getText(Document doc, IRunner runner, WebUser webUser) throws Exception {
		Options options = getDepartmentOptions(doc, webUser);
		for (Iterator<Option> iterator = options.getOptions().iterator(); iterator.hasNext();) {
			Option option = iterator.next();
			if (option.getValue().equals(doc.getItemValueAsString(getName()))) {
				return option.getOption();
			}
		}
		return "";
	}

	/**
	 * 获取默认下拉选项的类型
	 * 
	 * @return 下拉选项的类型
	 */
	public int getDefaultOptionType() {
		return defaultOptionType;
	}

	/**
	 * 设置下拉选项的类型
	 * 
	 * @param defaultOptionType
	 *            下拉选项的类型
	 */
	public void setDefaultOptionType(int defaultOptionType) {
		this.defaultOptionType = defaultOptionType;
	}

	/**
	 * 是否允许空值
	 * 
	 * @return
	 */
	public boolean isAllowEmpty() {
		return allowEmpty;
	}

	/**
	 * 设置是否允许空值
	 * 
	 * @param allowEmpty
	 *            是否允许空值
	 */
	public void setAllowEmpty(boolean allowEmpty) {
		this.allowEmpty = allowEmpty;
	}

}
