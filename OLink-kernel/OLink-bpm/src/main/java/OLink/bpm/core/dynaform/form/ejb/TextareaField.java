//Source file:
//C:\\Java\\workspace\\SmartWeb3\\src\\com\\cyberway\\dynaform\\form\\ejb\\InputField.java

package OLink.bpm.core.dynaform.form.ejb;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.core.dynaform.document.ejb.Item;
import OLink.bpm.core.macro.runner.AbstractRunner;
import OLink.bpm.core.macro.runner.IRunner;
import OLink.bpm.core.table.constants.MobileConstant;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.util.HtmlEncoder;
import OLink.bpm.core.dynaform.PermissionType;
import OLink.bpm.core.dynaform.document.ejb.Document;

/**
 * @author Marky
 */
public class TextareaField extends FormField implements ValueStoreField {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2705826322040773848L;

	protected static String cssClass = "textarea-cmd";

	/**
	 * @roseuid 41ECB66E012A
	 */
	public TextareaField() {

	}

	/**
	 * @roseuid 41ECB66E0152
	 */
	public void store() {

	}

	/**
	 * 根据TextareaField的显示类型不同,返回的结果字符串不同.
	 * 新建的Document,TextareaField的显示类型为默认的MODIFY。此时根据Form模版的TextareaField内容结合Document的Item的值,返回的字符串为重定义后的html.
	 * 还可以根据节点设置对应TextareaField的显示类型不同,返回的结果字符串不同.
	 * 1)节点设置对应TextareaField为隐藏时,返回"******"字符串.
	 * 2)若节点设置对应TextareaField为只读根据Form模版的多文档框(TextareaField)组件内容结合Document中的ITEM存放的值,
	 * 返回重定义后的html的多文本框Textarea，并且多文本框Textarea值为只读.
	 * 3)若节点设置对应TextareaField为DISABLED,返回重定义后的html的多文本框Textarea为DISABLED.
	 * 否则根据Form模版的多文档框(TextareaField)组件内容结合Document中的ITEM存放的值,返回重定义后的html的多文本框Textarea.
	 * 
	 * @param doc
	 *            文档对象
	 * @see FormField#getDisplayType(AbstractRunner,
	 *      Document, WebUser)
	 * @see ParamsTable#params
	 * @see AbstractRunner#run(String, String)
	 * @return 重定义后的html为Form模版的多文档框(TextareaField)组件内容结合Document中的ITEM存放的值,
	 * 
	 */
	public String toHtmlTxt(Document doc, IRunner runner, WebUser webUser) throws Exception {

		StringBuffer html = new StringBuffer();
		int displayType = getDisplayType(doc, runner, webUser);

		if (displayType == PermissionType.HIDDEN) {// 节点设置对应field隐藏
			return this.getHiddenValue();
		} else {
			html.append("<textarea");
			html.append(toAttr(doc, displayType));
			// 生成其他属性
			html.append(toOtherpropsHtml());
			html.append(">");
			html.append(toValue(doc));
			html.append("</textarea>");

		}
		return html.toString();
	}

	private String toAttr(Document doc, int displayType) {
		StringBuffer html = new StringBuffer();
		html.append(" id='" + getFieldId(doc) + "'");
		html.append(" name='" + getName() + "'");
		html.append(" fieldType='" + getTagName() + "'");
		if (isRefreshOnChanged()) {
			html.append(" onchange='dy_refresh(this.id)'");
		}
		html.append(" class='" + cssClass + "'");

		if (displayType == PermissionType.READONLY) {
			if (isBorderType()) {
				html.append(" readonly  style='overflow-y:hidden;overflow-x:hidden;border:0'");
			} else {
				html.append(" readonly ");
			}
		} else if (displayType == PermissionType.DISABLED) {
			html.append(" disabled ");
			//html.append(" readonly='readonly' style='color: #666' ");
		}

		return html.toString();
	}

	/**
	 * 获取TextareaField的值
	 * 
	 * @param doc
	 *            Document(文档对象)
	 * @return TextareaField的值
	 */
	public String toValue(Document doc) {
		StringBuffer html = new StringBuffer();
		// 生成值
		if (doc != null) {
			Item item = doc.findItem(this.getName());
			if (item != null && item.getValue() != null) {
				html.append(HtmlEncoder.encode(item.getValue() +""));
			}
		}
		return html.toString();
	}

	/**
	 * 
	 * Form模版的多文档框(TextareaField)组件内容结合Document中的ITEM存放的值,返回字符串为重定义后的打印html文本
	 * 
	 * @param doc
	 *            Document
	 * @param runner
	 *            AbstractRunner(执行脚本的接口类)
	 * @param params
	 *            参数
	 * @param user
	 *            webuser
	 * 
	 * @see AbstractRunner#run(String, String)
	 * @return 重定义后的打印html为Form模版的多文档框(TextareaField)组件内容结合Document中的ITEM存放的值
	 * @throws Exception
	 */
	public String toPrintHtmlTxt(Document doc, IRunner runner, WebUser webUser) throws Exception {
		StringBuffer html = new StringBuffer();

		if (doc != null) {
			int displayType = getPrintDisplayType(doc, runner, webUser);

			if (displayType == PermissionType.HIDDEN) {
				return this.getPrintHiddenValue();
			}

			Item item = doc.findItem(this.getName());

			if (item != null && item.getValue() != null) {
				html.append("<SPAN style=\"FONT-SIZE: 9pt\">");
				html.append(item.getValue() + "");
				html.append("</SPAN>");
				html.append(printHiddenElement(doc));
			}
		}
		return html.toString();
	}

	/**
	 * 返回模板描述多文本.
	 * 
	 * @return 字符串内容为描述多文本
	 * @roseuid 41E7917A033F
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
	 * 多文本框值脚本重新计算.
	 * 
	 * @roseuid 41DB89D700F9
	 */
	public void recalculate(IRunner runner, Document doc, WebUser webUser) throws Exception {
		getLog().debug("TextareaField.recalculate");
		runValueScript(runner, doc);
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
		String name = getDiscript();
		if (name == null || name.trim().length() <= 0) {
			name = getName();
		}
		html.append("<").append(MobileConstant.TAG_TEXTAREAFIELD).append("").append(" ").append(
				MobileConstant.ATT_LABEL).append(" = '").append(name + "'");
		html.append(" ").append(MobileConstant.ATT_NAME).append("='" + getName() + "'");
		if (displayType == PermissionType.READONLY || displayType == PermissionType.DISABLED) {
			html.append(" ").append(MobileConstant.ATT_DISABLED).append(" ='true' ");
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

	/**
	 * 根据Form模版的TextareaField组件内容结合Document中的ITEM存放的值,输出重定义后的html文本以网格显示
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

		if (displayType == PermissionType.HIDDEN) {// 节点设置对应field隐藏
			return this.getHiddenValue();
		} else {
			html.append("<textarea");
			html.append(toAttr(doc, displayType));
			html.append("style='width:100%'");
			html.append(">");
			html.append(toValue(doc));
			html.append("</textarea>");

		}
		return html.toString();
	}

	/**
	 * 获取组件名
	 * 
	 * @return 组件名
	 */
	public String getTagName() {
		return "TextAreaField";
	}

}
