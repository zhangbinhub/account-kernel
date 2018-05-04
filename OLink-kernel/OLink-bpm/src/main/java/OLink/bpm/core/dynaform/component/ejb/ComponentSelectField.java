// Source file:
// C:\\Java\\workspace\\SmartWeb3\\src\\com\\cyberway\\dynaform\\form\\ejb\\SelectField.java

package OLink.bpm.core.dynaform.component.ejb;

import OLink.bpm.core.dynaform.form.ejb.SelectField;
import OLink.bpm.core.macro.runner.IRunner;
import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.core.dynaform.PermissionType;
import OLink.bpm.core.dynaform.document.ejb.Document;
import OLink.bpm.core.user.action.WebUser;

public class ComponentSelectField extends SelectField {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5855099929118585871L;

	/**
	 * 根据当前组件内容结合参数,以及可执行动态脚本执行器,生成html标记中的下拉列表
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
	 * @return 重定义后的html
	 */
	public String toHtmlTxt(ParamsTable params, WebUser user, IRunner runner, Document doc, int displayType) {
		StringBuffer html = new StringBuffer();
		//Item item = null;
		if (displayType == PermissionType.HIDDEN) {
			return "******";
		} else {
			if (doc != null) {
				//item = doc.findItem(this.getName());

				html.append("<select");
				html.append(" id='" + getId() + "'");
				html.append(" name='" + getName() + "'");
				html.append(toOtherpropsHtml());
				if (displayType == PermissionType.READONLY) {
					html.append(" disabled ");
				} else if (displayType == PermissionType.DISABLED) {
					html.append(" disabled ");
				}
				if (isRefreshOnChanged()) {
					html.append(" onchange='cp_refresh(this.name)'");
				}
				html.append(">");
				try {
					html.append(runOptionsScript(runner, doc));
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}
		return html.toString();
	}

}
