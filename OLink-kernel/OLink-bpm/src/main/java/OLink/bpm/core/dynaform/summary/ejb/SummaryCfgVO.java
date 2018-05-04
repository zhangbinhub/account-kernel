package OLink.bpm.core.dynaform.summary.ejb;

import java.util.ArrayList;


import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.base.dao.ValueObject;
import OLink.bpm.core.dynaform.document.ejb.Document;
import OLink.bpm.core.dynaform.form.ejb.ValidateMessage;
import OLink.bpm.core.macro.runner.IRunner;
import OLink.bpm.core.macro.runner.JavaScriptFactory;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.core.user.ejb.UserDefined;
import OLink.bpm.util.StringUtil;

/**
 * @author Happy
 *
 */
public class SummaryCfgVO extends ValueObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4163612089696620469L;
	
	/**
	 * 作用于首页的代办概要描述
	 */
	public static final int SCOPE_PENDING = 0;
	/**
	 * 作用于流程的通知模板
	 */
	public static final int SCOPE_NOTIFY = 1;
	
	/**
	 * 作用于流程的邮件提醒
	 */
	public static final int SCOPE_EMAIL = 2;
	
	/**
	 * 作用于流程的手机短信提醒
	 */
	public static final int SCOPE_SMS = 3;
	
	/**
	 *作用于流程的站内短信提醒 
	 */
	public static final int SCOPE_MESSAGE = 4;
	
	/**
	 * 作用于我的工作和流程监控的主题字段
	 */
	public static final int SCOPE_SUBJECT = 5;
	
	/**
	 * 作用于抄送代阅列表
	 */
	public static final int SCOPE_CIRCULATOR= 6;
	
	
	/**
	 * 标题
	 */
	private String title;
	
	/**
	 * 设计模式
	 */
	private String type;
	
	
	/**
	 * 关联表单的ID
	 */
	private String formId;
	
	/**
	 * 排序字段
	 */
	private String orderby;
	
	/**
	 * 样式
	 */
	private String style;
	
	/**
	 * 摘要字段
	 */
	private String fieldNames;
	
	/**
	 * 摘要脚本
	 */
	private String summaryScript;
	
	private UserDefined userDefined;
	
	/**
	 * 作用域
	 */
	private int scope;
	
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	

	public String getFormId() {
		return formId;
	}

	public void setFormId(String formId) {
		this.formId = formId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getFieldNames() {
		return fieldNames;
	}

	public void setFieldNames(String fieldNames) {
		this.fieldNames = fieldNames;
	}

	public String getSummaryScript() {
		return summaryScript;
	}

	public void setSummaryScript(String summaryScript) {
		this.summaryScript = summaryScript;
	}

	public String getOrderby() {
		return orderby;
	}

	public void setOrderby(String orderby) {
		this.orderby = orderby;
	}

	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}
	
	public int getScope() {
		return scope;
	}

	public void setScope(int scope) {
		this.scope = scope;
	}

	/**生成摘要文本
	 * @param doc
	 * @param user
	 * @return
	 * @throws Exception
	 */
	public String toSummay(Document doc, WebUser user) throws Exception {
		StringBuffer ItemValue = new StringBuffer();
		StringBuffer builder = new StringBuffer();
		if (getType() != null && getType().equals("00")) {
			if (!StringUtil.isBlank(fieldNames)) {
				String[] _fieldNames = fieldNames.split(";");
				for (int i = 0; i < _fieldNames.length; i++) {
					String fieldName = _fieldNames[i];
					if(fieldName !=null && fieldName.trim().length()>0){
						ItemValue.append(doc.getValueByField(fieldName)).append("    ");
					}
				}
				builder.append(ItemValue.toString() != null ? ItemValue.toString() : "" + " ");
			} else {
				throw new Exception("*[FieldNames is empty]*");
			}
			return builder.toString();
		} else if (getType() != null && getType().equals("01")) {
			IRunner runner = JavaScriptFactory.getInstance("", doc.getApplicationid());
			runner.initBSFManager(doc, new ParamsTable(), user, new ArrayList<ValidateMessage>());
			String js = (String) runner.run("Documment SummaryScript:"+doc.getId(), this.getSummaryScript());
			return js;
		}
		return "";

	}
	
	public String toText(Document doc) throws Exception {
		StringBuffer ItemValue = new StringBuffer();
		StringBuffer builder = new StringBuffer();
		if (getType() != null && getType().equals("00")) {
			if (!StringUtil.isBlank(fieldNames)) {
				String[] _fieldNames = fieldNames.split(";");
				for (int i = 0; i < _fieldNames.length; i++) {
					String fieldName = _fieldNames[i];
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
			String js = (String) runner.run(getText(doc), this.getSummaryScript());
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
	
	private String replace(String str) {
		str = str == null ? "" : str;
		str = str.replaceAll("\\<br>", "\n");
		str = str.replaceAll("\\<br/>", "\n");
		return str;
	}

	public UserDefined getUserDefined() {
		return userDefined;
	}

	public void setUserDefined(UserDefined userDefined) {
		this.userDefined = userDefined;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	

}
