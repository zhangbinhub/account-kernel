package OLink.bpm.base.web.tag;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import OLink.bpm.util.OBPMDispatcher;

public class UrlTag extends TagSupport {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5253126593180991689L;
	private String value;

	private String id;

	public int doStartTag() throws JspException {
		HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
		String val = getValue();
		String contextPath = request.getContextPath();
		String url = new OBPMDispatcher().getDispatchURL(contextPath + "/portal/dispatch" + val, request, pageContext
				.getResponse());
		try {
			pageContext.getOut().write(url);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return EVAL_PAGE;

	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}
}