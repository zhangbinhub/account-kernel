package OLink.bpm.base.web.tag;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import OLink.bpm.base.dao.DataPackage;

/**
 * The page tag for page navigation in list page.
 */
public class PageNavTag extends TagSupport {

	private static final long serialVersionUID = 6338111746579488137L;

	/**
	 * @uml.property name="dpName"
	 */
	private String dpName;
	/**
	 * @uml.property name="css"
	 */
	private String css;

	/**
	 * @param datapackage
	 * @uml.property name="dpName"
	 */
	public void setDpName(String dpName) {
		this.dpName = dpName;
	}

	/**
	 * @return the css
	 * @uml.property name="css"
	 */
	public String getCss() {
		return css;
	}

	/**
	 * @param css
	 *            the css to set
	 * @uml.property name="css"
	 */
	public void setCss(String css) {
		this.css = css;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.jsp.tagext.Tag#doEndTag()
	 */
	public int doEndTag() throws JspException {
		DataPackage<?> bean = (DataPackage<?>) pageContext.getRequest().getAttribute(
				dpName);
		int currentPage = 0;
		int pageCount = 0;
		if (bean != null) {
			currentPage = bean.getPageNo();
			pageCount = bean.getPageCount();
		}

		StringBuffer html = new StringBuffer();

		if (currentPage > 1) {
			if (css != null && css.trim().length() > 0) {
				html = html
						.append("<a class="
								+ css
								+ " href='javascript:showFirstPage()'>{*[FirstPage]*}</a>&nbsp;");

				html = html
						.append("<a class="
								+ css
								+ " href='javascript:showPreviousPage()'>{*[PrevPage]*}</a>&nbsp;");
			} else {
				html = html
						.append("<a  href='javascript:showFirstPage()'>{*[FirstPage]*}</a>&nbsp;");
			}

		}

		if (currentPage < pageCount) {
			if (css != null && css.trim().length() > 0) {
				html = html
						.append("<a class="
								+ css
								+ " href='javascript:showNextPage()'>{*[NextPage]*}</a>&nbsp;");
				html = html
						.append("<a class="
								+ css
								+ " href='javascript:showLastPage()'>{*[EndPage]*}</a>&nbsp;");
			} else {
				html = html
						.append("<a href='javascript:showNextPage()'>{*[NextPage]*}</a>&nbsp;");
				html = html
						.append("<a href='javascript:showLastPage()'>{*[EndPage]*}</a>&nbsp;");
			}
		}


		html = html.append("{*[InPage]*}").append(currentPage).append(
				"{*[Page]*}/{*[Total]*}").append(pageCount).append(
				"{*[Pages]*}&nbsp;");

		try {
			pageContext.getOut().print(html.toString());
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return super.doEndTag();
	}
}
