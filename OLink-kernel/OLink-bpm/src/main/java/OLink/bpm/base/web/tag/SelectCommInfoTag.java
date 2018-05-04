package OLink.bpm.base.web.tag;

import OLink.bpm.constans.Web;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * The page tag for common info selection.
 */
public class SelectCommInfoTag extends TagSupport {
    
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @uml.property  name="field"
	 */
	private String field;

	/**
	 * @uml.property  name="type"
	 */
	private String type;

	/**
	 * @uml.property  name="multiSelect"
	 */
	private String multiSelect;

	/**
	 * @param field  The field
	 * @uml.property  name="field"
	 */
	public void setField(String field) {
		this.field = field;
	}
	/**
	 * @param multiSelect  The multi-select
	 * @uml.property  name="multiSelect"
	 */
	public void setMultiSelect(String multiSelect) {
		this.multiSelect = multiSelect;
	}
	/**
	 * @param type  The type.
	 * @uml.property  name="type"
	 */
	public void setType(String type) {
		this.type = type;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.jsp.tagext.Tag#doEndTag()
	 */
	public int doEndTag() throws JspException {
		StringBuffer html = new StringBuffer();
		
		html = html
				.append("<input type='button' name='bttnCommInfo' value='' class='srchbt' onClick=\"javascript:");
		html = html.append(field);
		html = html.append(".value = ");
		html = html.append(field);
		html = html.append(".value + selectCommInfo('");
		html = html.append(type);
		html = html.append("',");
		if (multiSelect != null && multiSelect.equals(Web.STRING_TRUE)) {
			html = html.append("true");
		} else {
			html = html.append("false");
		}
		html = html.append(")\">");
		
		try {
			pageContext.getOut().print(html.toString());
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return super.doEndTag();
	}
}
