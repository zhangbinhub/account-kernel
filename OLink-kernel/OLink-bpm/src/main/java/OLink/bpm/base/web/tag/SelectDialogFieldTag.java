package OLink.bpm.base.web.tag;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * The page tag for common info selection.
 */
public class SelectDialogFieldTag extends TagSupport {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @uml.property  name="name"
	 */
	private String name;
	
	/**
	 * @uml.property  name="title"
	 */
	private String title;

	/**
	 * @uml.property  name="dataSource"
	 */
	private String dataSource;

	/**
	 * @uml.property  name="multiSelect"
	 */
	private String multiSelect;

	/**
	 * @uml.property  name="showColumns"
	 */
	private String showColumns;

	/**
	 * @uml.property  name="returnFields"
	 */
	private String returnFields;

	/**
	 * @uml.property  name="dialogWidth"
	 */
	private String dialogWidth;
	
	/**
	 * @uml.property  name="dialogHeight"
	 */
	private String dialogHeight;
	
	/**
	 * @uml.property  name="styleClass"
	 */
	private String styleClass;
	
	/**
	 * @uml.property  name="label"
	 */
	private String label;
	/*
	 * Tree; List;
	 */
	/**
	 * @uml.property  name="type"
	 */
	private String type;
	
	/**
	 * @uml.property  name="theme"
	 */
	private String theme;

	/**
	 * @return  the label
	 * @uml.property  name="label"
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @param label  the label to set
	 * @uml.property  name="label"
	 */
	public void setLabel(String label) {
		this.label = label;
	}
	
	/**
	 * @return  the theme
	 * @uml.property  name="theme"
	 */
	public String getTheme() {
		return theme;
	}

	/**
	 * @param theme  the theme to set
	 * @uml.property  name="theme"
	 */
	public void setTheme(String theme) {
		this.theme = theme;
	}

	/**
	 * @return  the type
	 * @uml.property  name="type"
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type  the type to set
	 * @uml.property  name="type"
	 */
	public void setType(String type) {
		this.type = type;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.jsp.tagext.Tag#doEndTag()
	 */
	public int doEndTag() throws JspException {
		StringBuffer html = new StringBuffer();
//		String contextPath = ((HttpServletRequest)pageContext.getRequest()).getContextPath();
//		String queryStr = "";
		
		html.append("<tr>");
		html.append("<td class=\"tdLabel\"><label for=\""+name+"\" class=\"label\">"+label+":</label></td>");
		html.append("<td>");
		html.append("<input type='text' name='"+name+"'>");
		html.append("<input type='button' name='btn' onClick=\"javascript:alert('ok')\">");

//		html.append("window.showModalDialog("+contextPath+"/frame.htm");
//		html.append("?title="+title);
//		html.append(","+queryStr);
//		html.append(",'font-size:9pt;");
//		html.append("dialogWidth:"+ dialogWidth + ";dialogHeight:" + dialogHeight + ";status:no;scroll=no;');");
//
//		if (multiSelect != null && multiSelect.equals(Web.STRING_TRUE)) {
//			html = html.append("true");
//		} else {
//			html = html.append("false");
//		}
//		html = html.append(")\">");
		html.append("</td>");
		
		try {
			pageContext.getOut().print(html.toString());
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return super.doEndTag();

	}

	
	/**
	 * @return  the dataSource
	 * @uml.property  name="dataSource"
	 */
	public String getDataSource() {
		return dataSource;
	}

	/**
	 * @param dataSource  the dataSource to set
	 * @uml.property  name="dataSource"
	 */
	public void setDataSource(String dataSource) {
		this.dataSource = dataSource;
	}

	
	/**
	 * @return  the multiSelect
	 * @uml.property  name="multiSelect"
	 */
	public String getMultiSelect() {
		return multiSelect;
	}

	/**
	 * @param multiSelect  the multiSelect to set
	 * @uml.property  name="multiSelect"
	 */
	public void setMultiSelect(String multiSelect) {
		this.multiSelect = multiSelect;
	}

	
	/**
	 * @return  the name
	 * @uml.property  name="name"
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name  the name to set
	 * @uml.property  name="name"
	 */
	public void setName(String name) {
		this.name = name;
	}

	
	/**
	 * @return  the returnFields
	 * @uml.property  name="returnFields"
	 */
	public String getReturnFields() {
		return returnFields;
	}

	/**
	 * @param returnFields  the returnFields to set
	 * @uml.property  name="returnFields"
	 */
	public void setReturnFields(String returnFields) {
		this.returnFields = returnFields;
	}

	
	/**
	 * @return  the showColumns
	 * @uml.property  name="showColumns"
	 */
	public String getShowColumns() {
		return showColumns;
	}

	/**
	 * @param showColumns  the showColumns to set
	 * @uml.property  name="showColumns"
	 */
	public void setShowColumns(String showColumns) {
		this.showColumns = showColumns;
	}

	
	/**
	 * @return  the dialogHeight
	 * @uml.property  name="dialogHeight"
	 */
	public String getDialogHeight() {
		return dialogHeight;
	}

	/**
	 * @param dialogHeight  the dialogHeight to set
	 * @uml.property  name="dialogHeight"
	 */
	public void setDialogHeight(String dialogHeight) {
		this.dialogHeight = dialogHeight;
	}

	
	/**
	 * @return  the dialogWidth
	 * @uml.property  name="dialogWidth"
	 */
	public String getDialogWidth() {
		return dialogWidth;
	}

	/**
	 * @param dialogWidth  the dialogWidth to set
	 * @uml.property  name="dialogWidth"
	 */
	public void setDialogWidth(String dialogWidth) {
		this.dialogWidth = dialogWidth;
	}

	
	/**
	 * @return  the title
	 * @uml.property  name="title"
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title  the title to set
	 * @uml.property  name="title"
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return  the styleClass
	 * @uml.property  name="styleClass"
	 */
	public String getStyleClass() {
		return styleClass;
	}

	/**
	 * @param styleClass  the styleClass to set
	 * @uml.property  name="styleClass"
	 */
	public void setStyleClass(String styleClass) {
		this.styleClass = styleClass;
	}
}
