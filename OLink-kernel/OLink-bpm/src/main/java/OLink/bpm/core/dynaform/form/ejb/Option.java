/*
 * Created on 2005-2-9
 *
 * Window - Preferences - Java - Code Style - Code Templates
 */
package OLink.bpm.core.dynaform.form.ejb;


/**
 *         to Window - Preferences - Java - Code Style - Code Templates
 */
public class Option {

	/**
	 * @uml.property name="option"
	 */
	private String option;
	/**
	 * @uml.property name="value"
	 */
	private String value;
	/**
	 * @uml.property name="def"
	 */
	private boolean def;

	public Option() {
		super();
	}

	/**
	 * 构造方法
	 * 
	 * @param option
	 *            真实值
	 * @param value
	 *            保存值
	 */
	public Option(String option, String value) {
		this.option = option;
		this.value = value;
	}

	/**
	 * 构造方法
	 * 
	 * @param option
	 *            显示值
	 * @param value
	 *            真实值
	 * @param def
	 *            是否选中
	 */
	public Option(String option, String value, boolean def) {
		this.option = option;
		this.value = value;
		this.def = def;
	}

	/**
	 * @return Returns the def.
	 * @uml.property name="def"
	 */
	public boolean isDef() {
		return def;
	}

	/**
	 * @param def
	 *            The def to set.
	 * @uml.property name="def"
	 */
	public void setDef(boolean def) {
		this.def = def;
	}

	/**
	 * 获取显示值
	 * 
	 * @return 显示值
	 * @uml.property name="option"
	 */
	public String getOption() {
		return option;
	}

	/**
	 * 设置显示值
	 * 
	 * @param option
	 *            显示值
	 * @uml.property name="option"
	 */
	public void setOption(String option) {
		this.option = option;
	}

	/**
	 * 获取真实值
	 * 
	 * @return Returns the value.
	 * @uml.property name="value"
	 */
	public String getValue() {
		return value;
	}

	/**
	 * 设置真实值
	 * 
	 * @param value
	 *            真实值
	 * @uml.property name="value"
	 */
	public void setValue(String value) {
		this.value = value;
	}

	public boolean equals(Object anObject) {
		if (this == null)
			return false;
		if (anObject == null)
			return false;
		if (getClass() != anObject.getClass())
			return false;
		Option o = (Option) anObject;
		if (o.option != null && this.option != null) {
			return o.option.equals(this.option);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}
	
	

}
