package OLink.bpm.core.dynaform.form.ejb;

import OLink.bpm.core.dynaform.document.ejb.Document;
import OLink.bpm.core.macro.runner.AbstractRunner;
import OLink.bpm.core.macro.runner.IRunner;
import OLink.bpm.util.StringUtil;
import OLink.bpm.core.user.action.WebUser;

/**
 * 代码编辑组件
 * 
 * @author XGY
 * 
 */
public class IncludeJsFile extends FormField {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2523261172025936144L;

	/**
	 * 空实现
	 */
	public ValidateMessage validate(IRunner bsf, Document doc) throws Exception {
		return null;
	}

	/**
	 * 返回模板描述计算文本
	 * 
	 * @return java.lang.String
	 * @roseuid 41E7917A033F
	 */
	public String toTemplate() {

		return null;
	}

	/**
	 * 
	 * Form模版的代码编辑组件内容结合Document中的ITEM存放的值, 返回字符串值为ITEM的值.
	 * 若代码编辑组件的valueScript为空，返回一个空字符串。
	 * 
	 * @param doc
	 *            文档对象(Document)
	 * @see AbstractRunner#run(String, String)
	 * @return 字符串内容为重定义后的html
	 */
	public String toHtmlTxt(Document doc, IRunner runner, WebUser webUser) throws Exception {
		String valueScript = this.getValueScript();

		if (valueScript != null && valueScript.trim().length() > 0) {
			try {
				if (valueScript != null && valueScript.trim().length() > 0) {
					Object result = runner.run(getScriptLable("toHtmlText"), valueScript);
					if (result instanceof String) {
						return (String) result;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return "";
	}

	/**
	 * 
	 * Form模版的代码编辑组件内容结合Document中的ITEM存放的值,返回字符串值为打印的ITEM的值.
	 * 
	 * @param doc
	 *            文档对象(Document)
	 * @param runner
	 *            动态执行脚本器
	 * @param params
	 *            参数
	 * @param user
	 *            webuser
	 * 
	 * @see CalctextField#toHtmlTxt(Document,
	 *      AbstractRunner, WebUser)
	 * @see AbstractRunner#run(String, String)
	 * @return 字符串值为打印的ITEM的值.
	 * @throws Exception
	 */
	public String toPrintHtmlTxt(Document doc, IRunner runner, WebUser webUser) throws Exception {
		return toHtmlTxt(doc, runner, webUser);
	}

	/**
	 * 空实现
	 */
	public String toMbXMLText(Document doc, IRunner runner, WebUser webUser) throws Exception {
		return "";
	}

	public String getRefreshScript(IRunner runner, Document doc, WebUser webUser, boolean isHidden) throws Exception {
		StringBuffer buffer = new StringBuffer();
		String divid = this.getName() + "_divid";
		String fieldHTML = "";
		if (!isHidden) {
			fieldHTML = StringUtil.encodeHTML(this.toHtmlTxt(doc, runner, webUser)); // 对HTML进行编码
			fieldHTML = fieldHTML.replaceAll("\"", "\\\\\"");
			fieldHTML = fieldHTML.replaceAll("\r\n", "");
		}
		buffer.append("refreshField(\"").append(divid).append("\",\"");
		String isDecode = "true"; // 是否反编码
		buffer.append(this.getName()).append("\",\"").append(fieldHTML).append("\", " + isDecode + ");");

		return buffer.toString();
	}
}
