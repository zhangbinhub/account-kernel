//Source file: C:\\Java\\workspace\\SmartWeb3\\src\\com\\cyberway\\dynaform\\form\\ejb\\InputField.java

package OLink.bpm.core.dynaform.form.ejb;

import java.io.Serializable;

import OLink.bpm.constans.Environment;
import OLink.bpm.core.dynaform.document.ejb.Item;
import OLink.bpm.core.macro.runner.IRunner;
import OLink.bpm.util.StringUtil;
import OLink.bpm.core.dynaform.document.ejb.Document;
import OLink.bpm.core.user.action.WebUser;

/**
 * @author nicholas
 */
public class Textpart implements FormElement, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1227998221872724851L;

	// private static String cssClass = "textpart-cmd";

	/**
	 * @uml.property name="text"
	 */
	private String text;

	/**
	 * @roseuid 41ECB66E012A
	 */
	public Textpart() {

	}

	/**
	 * @return boolean
	 * @roseuid 41ECB66E013E
	 */
	public boolean validate() {
		return true;
	}

	/**
	 * @roseuid 41ECB66E0152
	 */
	public void store() {

	}

	/**
	 * @param doc
	 * @return java.lang.String
	 * @roseuid 41ECB66E015C
	 */
	public String toHtmlTxt(Document doc, IRunner runner, WebUser webUser) {
		return text;
	}

	public String runOptionsScript() {
		return null;
	}

	public String toTemplate() {
		return text;
	}

	public Item createItem(Object value) {
		return null;
	}

	/**
	 * 返回文本内容
	 * 
	 * @return Returns the text.
	 * @uml.property name="text"
	 */
	public String getText() {
		if (text == null) {
			text = "";
		}
		return text;
	}

	/**
	 * 设置文本内容
	 * 
	 * @param text
	 *            The text to set.
	 * @uml.property name="text"
	 */
	public void setText(String text) {
		this.text = text;
	}

	public String toPrintHtmlTxt(Document doc, IRunner runner, WebUser webUser)
			throws Exception {
		return text;
	}

	public String toMbXMLText(Document doc, IRunner runner, WebUser webUser)
			throws Exception {
		return text;
	}

	public String toPdfHtmlTxt(Document doc, IRunner runner, WebUser webUser)
			throws Exception {
		if (!StringUtil.isBlank(text)
				&& text.toUpperCase().indexOf("<IMG ") > -1) {
			if (text.toLowerCase().contains(" src=")) {
				if (!text.toLowerCase().startsWith(
						"http:")) {
					int index = text.toLowerCase()
							.indexOf(" src=");
					if (index > -1) {
						String end = text
								.substring(index + 6);
						String baseUrl = Environment
								.getInstance().getBaseUrl();
						if (baseUrl != null
								&& end.startsWith("/")
								&& baseUrl.endsWith("/")) {
							baseUrl = baseUrl.substring(0,
									baseUrl.length() - 1);
						}
						return text.substring(0,
								index + 6)
								+ baseUrl + end;
					}
				}
			}
			return "";
		}
		if (!StringUtil.isBlank(text)
				&& text.toUpperCase().startsWith("<TABLE ")) {
			if (!text.toLowerCase().contains(" style=")) {
				int index = text.toLowerCase().indexOf("<table ");
				if (index > -1) {
					return text.substring(0, index + 7)
							+ "style=\"table-layout:fixed;word-break:break-strict\" "
							+ text.substring(index + 7);
				}
			} else {
				int index = text.toLowerCase().indexOf(" style=");
				if (index > -1) {
					return text.substring(0, index + 8)
							+ "table-layout:fixed;word-break:break-strict;"
							+ text.substring(index + 8);
				}
			}
		}
		return text;
	}
}
/*
 * FormField TextpartField.init(java.lang.String){ return null; }
 */
