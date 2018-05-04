//Source file:
//C:\\Java\\workspace\\SmartWeb3\\src\\com\\cyberway\\dynaform\\form\\ejb\\SelectField.java

package OLink.bpm.core.dynaform.component.ejb;

import java.util.Iterator;

import OLink.bpm.core.dynaform.PermissionType;
import OLink.bpm.core.dynaform.form.ejb.Option;
import OLink.bpm.core.dynaform.form.ejb.Options;
import OLink.bpm.core.dynaform.form.ejb.RadioField;
import OLink.bpm.core.macro.runner.IRunner;
import OLink.bpm.util.HtmlEncoder;
import OLink.bpm.core.dynaform.document.ejb.Item;
import OLink.bpm.util.StringList;
import OLink.bpm.util.StringUtil;
import OLink.bpm.core.dynaform.document.ejb.Document;

public class ComponentRadioField extends RadioField {

	/**
	 * 
	 */
	private static final long serialVersionUID = 148660715924009621L;

	/**
	 * 根据当前组件内容结合参数,以及可执行动态脚本执行器,生成html标记中的Radio
	 * 
	 * @param runner
	 *            动态脚本执行器
	 * @param doc
	 *            文档对象
	 * @param displayType
	 *            显示类型(READONLY,MODIFY,HIDDEN,DISABLED,PRINT)
	 * @return 定义后html
	 * @throws Exception
	 */
	public String runOptionsScript(IRunner runner, Document doc, int displayType) throws Exception {
		Object result = null;
		StringBuffer html = new StringBuffer();
		if (displayType == PermissionType.HIDDEN) {
			return "******";
		} else {
			if (getOptionsScript() != null && getOptionsScript().trim().length() > 0) {
				StringBuffer label = new StringBuffer();
				label.append(this._form.getName()).append(".").append(getName()).append(".OptionsScript");

				result = runner.run(label.toString(), StringUtil.dencodeHTML(getOptionsScript()));

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
									|| (displayType == PermissionType.DISABLED)) {
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
								html.append(" onclick='cp_refresh(this.name)'");
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
							html.append("><font size=2>");
							html.append(element.getOption());
							html.append("</font></input>");

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
}
