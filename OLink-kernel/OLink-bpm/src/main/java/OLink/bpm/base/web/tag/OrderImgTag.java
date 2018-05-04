package OLink.bpm.base.web.tag;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import eWAP.core.Tools;

/**
 * The page tag for sorting in list page.
 */
public class OrderImgTag extends TagSupport {
	/**
	 * The serial version uuid.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The field name.
	 */
	private String field;

	/**
	 * The sequence id.
	 */
	private String sid;

	/**
	 * The css name.
	 */
	private String css;

	/**
	 * The integer value of no sorting
	 */
	private static int SORT_NONE = 0;

	/**
	 * The integer value of sorting by ascend.
	 */
	private static int SORT_ASC = 1;

	/**
	 * The integer value of sorting by descend.
	 */
	private static int SORT_DESC = 2;

	/**
	 * Set the field name
	 * 
	 * @param field
	 *            The field to set.
	 */
	public void setField(String field) {
		this.field = field;
	}

	/**
	 * Get the css
	 * 
	 * @return the css
	 */
	public String getCss() {
		return css;
	}

	/**
	 * Set the css.
	 * 
	 * @param css
	 *            the css to set
	 */
	public void setCss(String css) {
		this.css = css;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.jsp.tagext.TagSupport#doStartTag()
	 */
	public int doStartTag() throws JspException {
		StringBuffer html = new StringBuffer();

		// initialize the tag sequence id.
		try {
			sid = Tools.getSequence();
		} catch (Exception ex) {
			throw new JspException(ex.getMessage());
		}

		// output the order tag hyperlink
		html.append("<a id='order" + sid + "' style='cursor:hand' ");
		if (css != null && css.trim().length() > 0)
			html.append("class=" + css);
		html.append(">");

		try {
			pageContext.getOut().print(html.toString());
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return EVAL_BODY_INCLUDE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.jsp.tagext.Tag#doEndTag()
	 */
	public int doEndTag() throws JspException {
		StringBuffer html = new StringBuffer();

		HttpServletRequest request = null;
		if (pageContext.getRequest() instanceof HttpServletRequest)
			request = (HttpServletRequest) pageContext.getRequest();

		// output the end of order tag of hyper link
		html.append("</a>");

		// output the image to distinguish the order.
		int sort = getSort();

		if (sort == SORT_ASC)
			outputAscImage(html, request);

		if (sort == SORT_DESC)
			outputDescImage(html, request);

		// out the order script.
		outputOrderScript(html);

		try {
			pageContext.getOut().print(html.toString());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return EVAL_PAGE;
	}

	/**
	 * Output the order script.
	 * 
	 * @param html
	 *            The html string buffer.
	 */
	private void outputOrderScript(StringBuffer html) {
		html.append("<input id='orderby" + sid
				+ "' type='hidden' name='_orderby1' value='"
				+ getCurrentOrder() + "'>");

		html.append("<input type='hidden' id='_orderby" + sid
				+ "' name='_orderby' value='" + getCurrentOrder() + "'/>");

		html.append("<script>document.getElementById('order" + sid
				+ "').onclick=new Function(\"document.getElementById('orderby" + sid
				+ "').value=\'" + getNextOrder()
				+ "\';if(document.getElementById('_orderby" + sid
				+ "').length>=1){document.getElementById('_orderby')[0].value=\'"
				+ getNextOrder() + "\';}else{document.getElementById('_orderby" + sid
				+ "').value=\'" + getNextOrder()
				+ "\';};document.forms[0].submit();\")</script>");
	}

	/**
	 * output the image of of order by desscend
	 * 
	 * @param html
	 *            The html string buffer.
	 * @param request
	 *            The http request.
	 */
	private void outputDescImage(StringBuffer html, HttpServletRequest request) {
		if (request != null) {
			html.append("<img src='" + request.getContextPath()
					+ "/resource/images/down.gif' >");
		} else {
			html.append("(D)");
		}
	}

	/**
	 * output the image of of order by ascend
	 * 
	 * @param html
	 *            The html string buffer.
	 * @param request
	 *            The http request.
	 */
	private void outputAscImage(StringBuffer html, HttpServletRequest request) {
		if (request != null) {
			html.append("<img src='" + request.getContextPath()
					+ "/resource/images/up.gif' >");
		} else {
			html.append("(A)");
		}
	}

	/**
	 * Get the sort Type.
	 * 
	 * @return The sort Type.
	 */
	private int getSort() {
		HttpServletRequest request = (HttpServletRequest) pageContext
				.getRequest();

		String[] orderbys = request.getParameterValues("_orderby1");
		int sort = SORT_NONE;

		for (int i = 0; orderbys != null && i < orderbys.length; i++) {
			String orderby = orderbys[i];
			if (orderby == null || orderby.equals(""))
				continue;

			if (orderby.trim().equalsIgnoreCase(field)) {
				sort = SORT_ASC;
			} else if (orderby.trim().equalsIgnoreCase(field + " desc")) {
				sort = SORT_DESC;
			}
		}
		return sort;
	}

	/**
	 * Get the current order.
	 * 
	 * @return The current order.
	 */
	private String getCurrentOrder() {
		int sort = getSort();
		String order = "";

		if (sort == SORT_NONE)
			order = "";

		if (sort == SORT_ASC)
			order = field;

		if (sort == SORT_DESC)
			order = field + " desc";

		return order;
	}

	/**
	 * Get the next order
	 * 
	 * @return The next order.
	 */
	private String getNextOrder() {
		int sort = getSort();
		String order = "";

		if (sort == SORT_NONE)
			order = field;

		if (sort == SORT_ASC)
			order = field + " desc";

		if (sort == SORT_DESC)
			order = "";

		return order;
	}

}
