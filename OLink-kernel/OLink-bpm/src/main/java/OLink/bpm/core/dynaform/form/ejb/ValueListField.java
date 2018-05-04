//Source file:
//C:\\Java\\workspace\\SmartWeb3\\src\\com\\cyberway\\dynaform\\form\\ejb\\InputField.java

package OLink.bpm.core.dynaform.form.ejb;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.core.dynaform.document.ejb.Document;
import OLink.bpm.core.dynaform.document.ejb.Item;
import OLink.bpm.core.macro.runner.AbstractRunner;
import OLink.bpm.core.macro.runner.IRunner;
import OLink.bpm.core.table.constants.MobileConstant;
import OLink.bpm.core.dynaform.PermissionType;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.util.HtmlEncoder;
import OLink.bpm.util.StringUtil;

public class ValueListField extends FormField {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6026558487493849890L;

	/**
	 * 实现ValueListField校验.
	 * 通过执行ValueListField校验脚本,校验ValueListField输入是否合法，如不合法时返回错误信息，合法时返回为空。
	 * ValueListField校验包括引用的校验库(ValidateLibs)与校验规则(ValidateRule).
	 * 
	 * @param runner
	 *            AbstractRunner(执行脚本接口类)
	 * @see AbstractRunner#run(String, String)
	 * @param doc
	 *            文档对象
	 * @return ValidateMessage
	 * 
	 */
	public ValidateMessage validate(IRunner runner, Document doc) throws Exception {
		Object result = null;

		if (getValidateRule() != null && getValidateRule().trim().length() > 0) {
			result = runner.run(getScriptLable("Validate"), StringUtil.dencodeHTML(getValidateRule()));
			//Item item = doc.findItem(this.getName());
			if (result instanceof String) {
				String rs = (String) result;
				if (rs != null && rs.trim().length() > 0) {
					ValidateMessage msg = new ValidateMessage();
					msg.setFieldname(this.getName());
					msg.setErrmessage(rs);
					return msg;
				}
			}
		}
		return null;
	}

	/**
	 * 根据流程节点设置对应ValueListField的显示类型不同,返回的结果字符串不同.
	 * 节点设置对应ValueListField的显示类型默认为MODIFY.
	 * 若节点设置对应ValueListField的显示类型为READONLY,Document的ITEM存放的值为只读.
	 * 并根据Form模版的ValueListField组件内容结合Document中的ITEM存放的值,返回重定义后的html.
	 * 
	 * 通过强化HTML标签及语法，表达ValueListField的布局、属性、事件、样式、值等。
	 * 若节点设置对应ValueListField的显示类型为HIDDEN返回"******"字符串.
	 * 
	 * @param doc
	 *            文档对象
	 * @see ParamsTable#params
	 * @see AbstractRunner#run(String, String)
	 * @return 重定义后的html为Form模版的ValueListField组件内容结合Document中的ITEM存放的值
	 * 
	 */
	public String toHtmlTxt(Document doc, IRunner runner, WebUser webUser) throws Exception {

		StringBuffer html = new StringBuffer();
		int displayType = getDisplayType(doc, runner, webUser);
		if (displayType == PermissionType.HIDDEN) {// 节点设置对应field隐藏
			return toHiddenHtml(doc);
		} else {
			if (doc == null) {

				html.append("<textarea");
				html.append(" id='" + getId() + "'");
				html.append(" name='" + getName() + "'");
				if (isRefreshOnChanged()) {
					html.append(" onblur='ev_recalculate()'");
				}
				html.append(toOtherpropsHtml());
				if (displayType == PermissionType.READONLY) {
					html.append(" disabled ");
				}
				html.append(" style='display:none'>");
				html.append("</textarea>");

			} else {
				html.append("<textarea");
				html.append(" id='" + getId() + "'");
				html.append(" name='" + getName() + "'");

				html.append(toOtherpropsHtml());

				if (displayType == PermissionType.READONLY) {
					html.append(" disabled ");
				}

				html.append(" style='display:none'>");
				Item item = doc.findItem(this.getName());
				if (item != null && item.getValue() != null) {
					html.append(item.getValue());
				}

				html.append("</textarea>");
			}
		}
		return html.toString();

	}

	/**
	 * 模板描述
	 */
	public String toTemplate() {
		StringBuffer template = new StringBuffer();
		template.append("<textarea'");
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
		template.append(">");
		return template.toString();
	}

	/**
	 * 值脚本重新计算
	 * 
	 * @roseuid 41DB89D700F9
	 */
	public void recalculate(IRunner runner, Document doc, WebUser webUser) throws Exception {
		getLog().debug("ValueListField.recalculate");
		runValueScript(runner, doc);
	}

	/**
	 * 创建一个新的ITEM
	 * 
	 * @param doc
	 *            Document(文档对象)
	 * @param value
	 *            item的值域
	 * @return ITEM对象
	 */
	public Item createItem(Document doc, Object value) {
		Item item = new Item();
		item.setName(getName());

		if (value != null && value instanceof String) {
			item.setVarcharvalue((String) value);
		} else {
			item.setVarcharvalue(null);
		}

		return item;
	}

	public String toPrintHtmlTxt(Document doc, IRunner runner, WebUser webUser) throws Exception {
		return null;
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
	 * @return 手机平台XML串
	 */

	public String toMbXMLText(Document doc, IRunner runner, WebUser webUser) throws Exception {
		StringBuffer html = new StringBuffer();
		int displayType = getDisplayType(doc, runner, webUser);
		String name = getDiscript();
		if (name == null || name.trim().length() <= 0)
			name = getName();
		html.append("<").append(MobileConstant.TAG_TEXTAREAFIELD).append("").append(" ").append(
				MobileConstant.ATT_LABEL).append(" = '").append(name + "'");
		html.append(" ").append(MobileConstant.ATT_NAME).append("='" + getName() + "'");

		if (displayType == PermissionType.READONLY) {
			html.append(" ").append(MobileConstant.ATT_DISABLED).append(" = 'true'");
		}
		if (displayType == PermissionType.HIDDEN) {
			html.append(" ").append(MobileConstant.ATT_HIDDEN).append(" ='true' ");
		}
		if (doc == null) {

			if (isRefreshOnChanged()) {
				html.append(" ").append(MobileConstant.ATT_REFRESH).append("='true'>");
			}
		} else {
			html.append(">");
			Item item = doc.findItem(this.getName());
			if (item != null && item.getValue() != null) {
				html.append(HtmlEncoder.encode(item.getValue() +""));
			}
		}
		html.append("</").append(MobileConstant.TAG_TEXTAREAFIELD).append(">");
		return html.toString();
	}
}
