//Source file:
//C:\\Java\\workspace\\SmartWeb3\\src\\com\\cyberway\\dynaform\\form\\ejb\\SelectField.java

package OLink.bpm.core.dynaform.component.ejb;

import java.util.Iterator;

import OLink.bpm.core.dynaform.PermissionType;
import OLink.bpm.core.dynaform.document.ejb.Item;
import OLink.bpm.core.dynaform.form.ejb.CheckboxField;
import OLink.bpm.core.dynaform.form.ejb.Option;
import OLink.bpm.core.dynaform.form.ejb.Options;
import OLink.bpm.core.macro.runner.IRunner;
import OLink.bpm.util.HtmlEncoder;
import OLink.bpm.core.dynaform.document.ejb.Document;
import OLink.bpm.util.StringList;
import OLink.bpm.util.StringUtil;

public class ComponentCheckboxField extends CheckboxField {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3826081467057579521L;

	/**
	 * 通过动态脚本执行器生成checkbox
	 * 
	 * @param runner
	 *            动态脚本执行器
	 * @param doc
	 *            文档对象
	 * @param displayType
	 *            显示类型（READONLY,MODIFY,HIDDEN,DISABLED,PRINT）
	 * @return 生成后checkbox以html标记的形式
	 * @throws Exception
	 */
	public String runOptionsScript(IRunner runner, Document doc, int displayType) throws Exception {
		Object result = null;
		if (displayType == PermissionType.HIDDEN) {
			return "******";
		} else {
			if (getOptionsScript() != null && getOptionsScript().trim().length() > 0) {
				StringBuffer label = new StringBuffer();
				label.append(_form.getName()).append(".").append(getName()).append(".OptionsScript");

				result = runner.run(label.toString(), StringUtil.dencodeHTML(getOptionsScript()));

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

				if (options != null && doc !=null) {
					Object value = null;
					StringList valueList = null;
					StringBuffer html = new StringBuffer();
					Item item = doc.findItem(this.getName());

					if (item != null)
						value = item.getValue();
					if (value != null)
						valueList = new StringList((String) value, ';');

					if (doc != null) {
						Iterator<Option> iter = options.getOptions().iterator();
						while (iter.hasNext()) {
							Option element = iter.next();
							html.append("<input type='checkbox' value=");

							if (isRefreshOnChanged()) {
								html.append(" onclick='cp_refresh(this.name)'");
							}

							html.append("\"");
							html.append(HtmlEncoder.encode(element.getValue()));
							html.append("\"");
							html.append(" name='");
							html.append(this.getName());
							html.append("'");

							if (displayType == PermissionType.READONLY) {
								html.append(" disabled ");
							}

							if (valueList != null && element.getValue() != null) {
								if (valueList.indexOf(element.getValue()) >= 0) {
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
						}
						return html.toString();
					}
				}
			}
		}
		return "";
	}
}
