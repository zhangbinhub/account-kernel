package OLink.bpm.base.web.tag;

import java.util.Enumeration;
import javax.servlet.ServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * The page tag for keep the search condition in list page.
 */
public class KeepConditionTag extends TagSupport {

	/**
	 * The serial version uuid.
	 */

	private static final long serialVersionUID = -1037551706889209753L;

	/**
	 * The form name
	 */
	private String form;

	/**
	 * Set the form name
	 * 
	 * @param form
	 *            The form to set.
	 */
	public void setForm(String form) {
		this.form = form;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.jsp.tagext.Tag#doEndTag()
	 */
	public int doEndTag() throws JspException {
		ServletRequest request = pageContext.getRequest();
		Enumeration<?> enm = request.getParameterNames();
		StringBuffer html = new StringBuffer();

		// Output the script header
		html.append("<script language='javascript'>");
		html.append("var cndtnform=document." + form + ";");

		// Output the java script of filling every field.
		while (enm.hasMoreElements()) {
			String name = (String) enm.nextElement();

			if (name.indexOf("_") >= 0) {
				String value = request.getParameter(name);
				if (value != null && value.trim().length() > 0) {
					html.append("if(!cndtnform." + name + ")\r\n");
					html.append("document.write(\"<input type='hidden' name='"
							+ name + "' value='" + value + "'>\");\r\n");
				}
			}
		}
		// Output the script rooter.
		html.append("</script>");

		try {
			pageContext.getOut().print(html.toString());
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return super.doEndTag();
	}
}
