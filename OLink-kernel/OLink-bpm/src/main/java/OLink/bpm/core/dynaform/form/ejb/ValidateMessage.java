/*
 * Created on 2005-2-8
 *
 * Window - Preferences - Java - Code Style - Code Templates
 */
package OLink.bpm.core.dynaform.form.ejb;

/**
 *         to Window - Preferences - Java - Code Style - Code Templates
 */
public class ValidateMessage {

	/**
	 * @uml.property name="fieldname"
	 */
	private String fieldname;

	/**
	 * @uml.property name="errmessage"
	 */
	private String errmessage;

	public ValidateMessage() {

	}

	/**
	 * 构造方法,初始化 Field名字和错误信息
	 * 
	 * @param fieldname
	 *            Field名字
	 * @param errmessage
	 *            错误信息
	 */
	public ValidateMessage(String fieldname, String errmessage) {
		this.fieldname = fieldname;
		this.errmessage = errmessage;
	}

	/**
	 * 获取错误信息
	 * 
	 * @return 错误信息
	 * @uml.property name="errmessage"
	 */
	public String getErrmessage() {
		return errmessage;
	}

	/**
	 * 设置错误信息
	 * 
	 * @param errmessage
	 *            错误信息
	 * @uml.property name="errmessage"
	 */
	public void setErrmessage(String errmessage) {
		this.errmessage = errmessage;
	}

	/**
	 * 获取Field名字
	 * 
	 * @return Field名字
	 * @uml.property name="fieldname"
	 */
	public String getFieldname() {
		return fieldname;
	}

	/**
	 * 设置Field名字
	 * 
	 * @param fieldname
	 *            Field名字
	 * @uml.property name="fieldname"
	 */
	public void setFieldname(String fieldname) {
		this.fieldname = fieldname;
	}
}
