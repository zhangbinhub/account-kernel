//Source file:
//C:\\Java\\workspace\\SmartWeb3\\src\\com\\cyberway\\dynaform\\form\\ejb\\InputField.java

package OLink.bpm.core.dynaform.component.ejb;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.core.dynaform.PermissionType;
import OLink.bpm.core.dynaform.document.ejb.Document;
import OLink.bpm.core.dynaform.document.ejb.Item;
import OLink.bpm.core.dynaform.form.ejb.TextareaField;
import OLink.bpm.core.macro.runner.JavaScriptFactory;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.util.HtmlEncoder;

public class ComponentTextareaField extends TextareaField {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7164010393815333370L;

	/**
	 * 根据当前组件内容结合参数,以及可执行动态脚本执行器,生成html标记中的多行文本框（textarea）
	 * 
	 * @param params
	 *            参数
	 * @param user
	 *            webuse r
	 * @param runner
	 *            动态脚本执行器
	 * @param doc
	 *            文档对象
	 * @param displayType
	 *            显示类型(READONLY,MODIFY,HIDDEN,DISABLED,PRINT)
	 * @return 重定义后的html
	 */
	public String toHtmlTxt(ParamsTable params, WebUser user, JavaScriptFactory runner, Document doc, int displayType) {

		StringBuffer html = new StringBuffer();
		if (displayType == PermissionType.HIDDEN) {// 节点设置对应field隐藏
			return "******";
		} else {
			if (doc == null) {

				html.append("<textarea");
				html.append(" id='" + getId() + "'");
				html.append(" name='" + getName() + "'");
				if (isRefreshOnChanged()) {
					html.append(" onchange='cp_refresh(this.name)'");
				}
				html.append(toOtherpropsHtml());
				if (displayType == PermissionType.READONLY) {
					html.append(" readonly ");
				} else if (displayType == PermissionType.DISABLED) {
					html.append(" disabled ");
				}
				html.append(" class='" + cssClass + "'");
				html.append(">");
				html.append("</textarea>");

			} else {
				html.append("<textarea");
				html.append(" id='" + getId() + "'");
				html.append(" name='" + getName() + "'");

				html.append(toOtherpropsHtml());

				if (displayType == PermissionType.READONLY) {
					html.append(" readonly ");
				} else if (displayType == PermissionType.DISABLED) {
					html.append(" disabled ");
				}

				html.append(" class='" + cssClass + "'");
				html.append(">");
				Item item = doc.findItem(this.getName());
				if (item != null && item.getValue() != null) {
					html.append(HtmlEncoder.encode(item.getValue() + ""));
				}

				html.append("</textarea>");
			}
		}
		return html.toString();

	}

}
