package OLink.bpm.core.dynaform.form.ejb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.core.dynaform.document.ejb.Item;
import OLink.bpm.core.macro.runner.AbstractRunner;
import OLink.bpm.core.macro.runner.IRunner;
import OLink.bpm.core.table.constants.MobileConstant;
import OLink.bpm.core.user.ejb.UserProcess;
import OLink.bpm.core.user.ejb.UserVO;
import OLink.bpm.util.HtmlEncoder;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.util.StringUtil;
import OLink.bpm.core.dynaform.PermissionType;
import OLink.bpm.core.dynaform.document.ejb.Document;
import OLink.bpm.core.user.action.WebUser;

public class UserField extends FormField implements ValueStoreField {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7936079412655964741L;

	protected static String cssClass = "input-cmd";

	private String filterField;

	private boolean limitByUser;
	
	/**
	 * 限制选择用户的数量
	 */
	protected String limitSum;


	/**
	 * 是否限制用户列表,true: 是限制用户,则是定制时选择的角色下的用户,false: 不限制用户,带出系统所有用户
	 * 
	 * @return
	 */
	public boolean isLimitByUser() {
		return limitByUser;
	}

	/**
	 * 设置是否限制用户列表
	 * 
	 * @param limitByUser
	 *            boolean
	 */
	public void setLimitByUser(boolean limitByUser) {
		this.limitByUser = limitByUser;
	}

	/**
	 * 获取过滤条件
	 * 
	 * @return 过滤条件
	 */
	public String getFilterField() {
		return filterField;
	}

	/**
	 * 设置过滤条件
	 * 
	 * @param filterField
	 */
	public void setFilterField(String filterField) {
		this.filterField = filterField;
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
			Collection<Option> deptOptions = getUserOptions(doc, webUser).getOptions();
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
	 * 获取用户组件的下拉选项
	 * 
	 * @param doc
	 *            Document(文档对象)
	 * @param webUser
	 *            webUser
	 * @return 获取用户选项
	 * @throws Exception
	 */
	private Options getUserOptions(Document doc, WebUser webUser) throws Exception {
		Collection<UserVO> usertList = new ArrayList<UserVO>();
		Options options = new Options();
		UserProcess process = (UserProcess) ProcessFactory.createProcess(UserProcess.class);
		Collection<UserVO> tempUserList = null;
		if (getFilterField() != null && isLimitByUser() == false) {
			tempUserList = process.queryByDomain(webUser.getDomainid(), 1, Integer.MAX_VALUE);

			UserVO userVO = null;
			if (tempUserList != null) {
				for (Iterator<UserVO> iterator = tempUserList.iterator(); iterator.hasNext();) {
					userVO = iterator.next();
					usertList.add(userVO);
				}
			}
			options.add(new Option("", ""));
			for (Iterator<UserVO> iterator = usertList.iterator(); iterator.hasNext();) {
				UserVO userVO1 = iterator.next();
				options.add(userVO1.getName(), userVO1.getId(), isDefaultValue(doc, userVO1.getId()));
			}
		} else {
			DataPackage<UserVO> UserListTemp = process.doQueryByRoleId(getFilterField());
			Collection<UserVO> collection = UserListTemp.getDatas();
			UserVO userVO = null;
			if (collection != null) {
				for (Iterator<UserVO> iterator = collection.iterator(); iterator.hasNext();) {
					userVO = iterator.next();
					usertList.add(userVO);
				}
				options.add(new Option("", ""));
				for (Iterator<UserVO> iterator = usertList.iterator(); iterator.hasNext();) {
					UserVO userVO1 = iterator.next();
					options.add(userVO1.getName(), userVO1.getId(), isDefaultValue(doc, userVO1.getId()));
				}
			}
		}
		return options;
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
		// template.append(" departmentlevel='" + getDepartmentLevel() + "'");
		template.append(" fileFiled='" + getFilterField() + "'");
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
		StringBuffer html = new StringBuffer();
		int displayType = getDisplayType(doc, runner, webUser);

		if (displayType == PermissionType.HIDDEN) {
			return this.getHiddenValue();
		} else {
			// Text Field HTML
			if (getTextType().equalsIgnoreCase("hidden")) {
				html.append("<input type='hidden'");
			} else {
				html.append("<input type='text' readonly");
			}
			html.append(" style='width:100%'");
			if (isRefreshOnChanged()) {
				html.append(" onchange='dy_refresh(this.id)' ");
			}
			html.append(toAttr(doc, displayType));
		}
		return html.toString();
	}

	/**
	 * 
	 * Form模版的UserField内容结合Document中的ITEM存放的值,返回重定义后的html，
	 * 
	 * @param doc
	 *            文档对象
	 * @see ParamsTable#params
	 * @see AbstractRunner#run(String, String)
	 * @return 重定义后的html为Form模版的UserField内容结合Document中的ITEM存放的值,
	 * 
	 */
	public String toHtmlTxt(Document doc, IRunner runner, WebUser webUser) throws Exception {
		StringBuffer html = new StringBuffer();
		int displayType = getDisplayType(doc, runner, webUser);
		if (displayType == PermissionType.HIDDEN) {
			return this.getHiddenValue();
		} else {
			// Text Field HTML
			if (getTextType().equalsIgnoreCase("hidden")) {
				html.append("<input type='hidden'");
			} else {
				html.append("<input type='text' readonly "+(displayType==PermissionType.DISABLED? "disabled":""));
			}
			html.append(toAttr(doc, displayType));
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
		String textFieldId = getFieldId(doc) + "_text";
		String valueFieldId = getFieldId(doc);

		// 输入框部分
		html.append(" class='" + cssClass + "'");
		html.append(" id='" + textFieldId + "'");
		html.append(" name='" + getName() + "_text'");
		html.append(" fieldType='" + getTagName() + "'");
		if (doc != null) {
			html.append(" value='").append(getFieldText(doc)).append("'");
		}
		html.append(" />");

		// 按钮部分
		if (!getTextType().equalsIgnoreCase("hidden") && displayType != PermissionType.HIDDEN) {
			// User Select Settings
			String settings = "{textField:'" + textFieldId + "', valueField:'" + valueFieldId + "',limitSum:'"+this.getLimitSum()+"'";
			if (isRefreshOnChanged()) {
				settings += ", callback: dy_refresh";
			}
			if (displayType == PermissionType.READONLY || getTextType().equals("readonly")
					|| displayType == PermissionType.DISABLED) {
				settings += ", readonly:true ";
			}
			settings += "}";
			
			html.append("<input type='hidden' id='" + valueFieldId + "'");
			html.append(" name='" + getName() + "'");
			html.append(" value='" + getFieldValue(doc) + "'");
			html.append(" fieldType='" + getTagName() + "'>");
			// Select Button HTML
			String roleid = "";
			if (getFilterField() != null && isLimitByUser()) {
				roleid = getFilterField();
			}

			if (!getTextType().equals("readonly") && displayType == PermissionType.MODIFY) {
				html
						.append("<span style='margin-left:5px;color:#316AC5;cursor: pointer;' title='{*[Select]*}{*[User]*}' onclick=\"showUserSelectNoFlow('actionName', "
								+ settings + ",'" + roleid + "')\">{*[Add]*}</span>");
				html
						.append("<span style='margin-left:5px;color:#316AC5;cursor: pointer;' title='{*[Clear]*}' onclick='jQuery(\"#"
								+ valueFieldId
								+ "\").attr(\"value\",\"\");jQuery(\"#"
								+ textFieldId
								+ "\").attr(\"value\",\"\");'>{*[Clear]*}</span>");
			}
		}

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
			Item item = doc.findItem(this.getName());
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
			UserProcess process = (UserProcess) ProcessFactory.createProcess(UserProcess.class);

			String valueListStr = getFieldValue(doc);
			if (!StringUtil.isBlank(valueListStr)) {
				String[] values = valueListStr.split(";");
				if (values.length > 0) {
					for (int i = 0; i < values.length; i++) {
						UserVO user = (UserVO) process.doView(values[i]);
						if (user != null) {
							rtn.append(user.getName()).append(";");
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
		int isdisplay = getDisplayType(doc, runner, webUser);
		
		if (displayType == PermissionType.HIDDEN || isdisplay==PermissionType.HIDDEN) {
			if(!this.getHiddenValue().equals("")){
				return this.getHiddenValue();
			}else{
				return this.getPrintHiddenValue();
			}
		}

		if (!getTextType().equalsIgnoreCase("hidden")) {
			Item item = doc.findItem(this.getName());
			if (item != null && item.getValue() != null) {
				html.append("<SPAN style=\"FONT-SIZE: 9pt\">");
				html.append(getFieldText(doc));
				html.append("</SPAN>");
				html.append(printHiddenElement(doc));
			}
		} else {
			html.append(printHiddenElement(doc));
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
	public String getText(Document doc, IRunner runner, WebUser webUser) throws Exception {
		String fileFullName = getFieldText(doc);
		if (fileFullName != null) {
			return fileFullName;
		}
		return super.getText(doc, runner, webUser);
	}

	public String getLimitSum() {
		return limitSum;
	}

	public void setLimitSum(String limitSum) {
		this.limitSum = limitSum;
	}
}
