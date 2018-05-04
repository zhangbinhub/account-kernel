package OLink.bpm.core.homepage.ejb;

import java.util.ArrayList;

import OLink.bpm.base.dao.ValueObject;
import OLink.bpm.core.macro.runner.IRunner;
import OLink.bpm.core.user.ejb.UserDefined;
import OLink.bpm.util.StringUtil;
import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.core.dynaform.document.ejb.Document;
import OLink.bpm.core.dynaform.form.ejb.ValidateMessage;
import OLink.bpm.core.macro.runner.JavaScriptFactory;

/**
 * @hibernate.class table="T_REMINDER"
 */
public class Reminder extends ValueObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1295517077066494982L;

	public static final String REMINDER_TYPE_DESC = "00";

	public final static String REMINDER_TYPE_CODE = "01";

	private String title;

	private String id;

	private String moduleid;

	private String orderby;

	private String formId;

	private HomePage homepage;

	private UserDefined userDefined;
	
	private String summaryFieldNames;

	private String type = "00";

	private String filterScript;

	/**
	 * 设置页面的样式
	 */

	private String style;

	/**
	 * @hibernate.property column="TITLE"
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @hibernate.property column="TYPE"
	 */
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @hibernate.property column="FILTERSCRIPT"
	 */
	public String getFilterScript() {
		return filterScript;
	}

	public void setFilterScript(String filterScript) {
		this.filterScript = filterScript;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @hibernate.property column="FORMID"
	 */
	public String getFormId() {
		return formId;
	}

	public void setFormId(String formId) {
		this.formId = formId;
	}

	/**
	 * @hibernate.id column="ID" generator-class="assigned"
	 * @uml.property name="id"
	 */
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @hibernate.property column="ORDERBY"
	 */
	public String getOrderby() {
		return orderby;
	}

	public void setOrderby(String orderby) {
		this.orderby = orderby;
	}

	/**
	 * @hibernate.property column="SUMMARYFIELDNAMES"
	 */
	public String getSummaryFieldNames() {
		return summaryFieldNames;
	}

	public void setSummaryFieldNames(String summaryFieldNames) {
		this.summaryFieldNames = summaryFieldNames;
	}

	/**
	 * @hibernate.property column="MODULEID"
	 */
	public String getModuleid() {
		return moduleid;
	}

	public void setModuleid(String moduleid) {
		this.moduleid = moduleid;
	}

	public String toSummay(Document doc) throws Exception {
		StringBuffer ItemValue = new StringBuffer();
		StringBuffer builder = new StringBuffer();
		if (getType() != null && getType().equals("00")) {
			if (!StringUtil.isBlank(summaryFieldNames)) {
				String[] fieldNames = summaryFieldNames.split(";");
				for (int i = 0; i < fieldNames.length; i++) {
					String fieldName = fieldNames[i];
					if(fieldName !=null && fieldName.trim().length()>0){
						ItemValue.append(doc.getValueByField(fieldName)).append("&#160;&#160;&#160;");
					}
				}
				builder.append(ItemValue.toString() != null ? ItemValue.toString() : "" + " ");
			} else {
				throw new Exception("*[Culum is empty]*");
			}
			return builder.toString();
		} else if (getType() != null && getType().equals("01")) {
			IRunner runner = JavaScriptFactory.getInstance("", getApplicationid());
			runner.initBSFManager(doc, new ParamsTable(), null, new ArrayList<ValidateMessage>());
			String js = (String) runner.run(getText(doc), this.getFilterScript());
			return js;
		}
		return "";

	}

	public String toText(Document doc) throws Exception {
		StringBuffer ItemValue = new StringBuffer();
		StringBuffer builder = new StringBuffer();
		if (getType() != null && getType().equals("00")) {
			if (!StringUtil.isBlank(summaryFieldNames)) {
				String[] fieldNames = summaryFieldNames.split(";");
				for (int i = 0; i < fieldNames.length; i++) {
					String fieldName = fieldNames[i];
					if(fieldName !=null && fieldName.trim().length()>0){
						ItemValue.append(doc.getValueByField(fieldName)).append("&#160;&#160;&#160;");
					}
				}
				builder.append(ItemValue.toString() != null ? ItemValue.toString() : "" + " ");
			} else {
				throw new Exception("*[Culum is empty]*");
			}
			return replace(builder.toString());
		} else if (getType() != null && getType().equals("01")) {
			IRunner runner = JavaScriptFactory.getInstance("", getApplicationid());
			runner.initBSFManager(doc, new ParamsTable(), null, new ArrayList<ValidateMessage>());
			String js = (String) runner.run(getText(doc), this.getFilterScript());
			return js;
		}
		return "";
	}

	public String getText(Document doc) throws Exception {
		StringBuffer builder = new StringBuffer();
		builder.append("REMINDER.").append(title);
		builder.append(".Column(").append(getId()).append(")." + title);
		return builder.toString();
	}

	/**
	 * @hibernate.property column="STYLE"
	 */
	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	/**
	 * @return
	 * @hibernate.many-to-one class="HomePage"
	 *                        column="HOMEPAGE"
	 */
	public HomePage getHomepage() {
		return homepage;
	}

	public void setHomepage(HomePage homepage) {
		this.homepage = homepage;
	}
	
	private String replace(String str) {
		str = str == null ? "" : str;
		str = str.replaceAll("\\<br>", "\n");
		str = str.replaceAll("\\<br/>", "\n");
		return str;
	}
	
	public static void main(String[] args) {
//		String str = "加班申请<br>申请人：testuser<br>申请时间：Fri Sep 10 00:00:00 CST 2010<br>加班总时间：0<br>加班内容：asdfasdfasdf<br>&#160;&#160;&#160;";
	}

	public UserDefined getUserDefined() {
		return userDefined;
	}

	public void setUserDefined(UserDefined userDefined) {
		this.userDefined = userDefined;
	}
}
