//Source file:
//C:\\Java\\workspace\\SmartWeb3\\src\\com\\cyberway\\dynaform\\form\\ejb\\InputField.java

package OLink.bpm.core.dynaform.component.ejb;

import java.text.SimpleDateFormat;
import java.util.Date;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.core.dynaform.document.ejb.Document;
import OLink.bpm.core.macro.runner.JavaScriptFactory;
import OLink.bpm.core.dynaform.PermissionType;
import OLink.bpm.core.dynaform.document.ejb.Item;
import OLink.bpm.core.dynaform.form.ejb.InputField;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.util.HtmlEncoder;

public class ComponentInputField extends InputField {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5749426513470185934L;

	/**
	 * 根据当前组件内容结合参数,以及可执行动态脚本执行器 ,生成重定义后的文本框,有多种类型(普通,密码,只读)
	 * 
	 * @param params
	 *            参数
	 * @param user
	 *            webuser
	 * @param runner
	 *            动态脚本执行器
	 * @param doc
	 *            文档对象
	 * @param displayType
	 *            显示类型(READONLY,MODIFY,HIDDEN,DISABLED,PRINT)
	 * @return
	 */
	public String toHtmlTxt(ParamsTable params, WebUser user, JavaScriptFactory runner, Document doc, int displayType) {
		StringBuffer html = new StringBuffer();
		// 添加Field
		if (displayType == PermissionType.HIDDEN) {// 节点设置对应field隐藏
			return "******";
		} else {
			if (getTextType() != null) {
				if (getTextType().equalsIgnoreCase("text")) {
					html.append("<input type='text'");
				} else if (getTextType().equalsIgnoreCase("readonly")) {
					html.append("<input type='text' readonly");
				} else if (getTextType().equalsIgnoreCase("hidden")) {
					html.append("<input type='hidden'");
				} else if (getTextType().equalsIgnoreCase("password")) {
					html.append("<input type='password'");
				} else {
					html.append("<input type='text'");
				}

				html.append(" id='" + getId() + "'");
				html.append(" name='" + getName() + "'");
				if (isRefreshOnChanged()) {
					html.append(" onchange='cp_refresh(this.name)'");
				}
				html.append(" class='" + cssClass + "'");

				if (doc != null) {
					Item item = doc.findItem(this.getName());
					if (item != null && item.getValue() != null) {
						Object value = item.getValue();
						if (value instanceof Double) {
							html.append(" value=\"" + item.getValue() + "\"");
						} else if (value instanceof Date) {
							Date d = (Date) value;
							SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
							html.append(" value=\"" + format.format(d) + "\"");
						} else {
							html.append(" value=\"" + HtmlEncoder.encode(item.getValue() + "") + "\"");
						}
					}
				}
				if (displayType == PermissionType.READONLY) {// 节点设置对应field只读或关闭
					html.append(" readonly ");
				} else if (displayType == PermissionType.DISABLED) {
					html.append(" disabled ");
				}

				html.append(toOtherpropsHtml());
				html.append(">");
				if (getDialogView() != null && getDialogView().trim().length() > 0) {
					if (displayType != PermissionType.DISABLED && displayType != PermissionType.READONLY) {// 设置对话框按钮field关闭
						if (isPopToChoice()) {
							html.append("<input type='button' onclick='ViewHelper.convertValuesMapToPage("
									+ "cp_getFormid(),\"" + getDialogView() + "\",\"" + getName() + "\",\""
									+ user.getId() + "\",cp_getValuesMap(),function(text){showViewDialog(text,\""
									+ getDialogView() + "\",\"" + this.getName() + "\")})' value='{*[...]*}' ");
						} else {
							html
									.append("<input type='button' onclick='ViewHelper.displayViewHtml("
											+ "cp_getFormid(),\""
											+ getDialogView()
											+ "\",\""
											+ getName()
											+ "\",\""
											+ user.getId()
											+ "\",cp_getValuesMap(),function(text){src2(text,\"Choice\")})' value='{*[...]*}' ");
						}
						html.append("/>");
					}
				}

			}
		}
		return html.toString();
	}
}
