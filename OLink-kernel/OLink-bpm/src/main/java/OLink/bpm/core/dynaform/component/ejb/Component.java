package OLink.bpm.core.dynaform.component.ejb;

import OLink.bpm.core.dynaform.form.ejb.Form;

/**
 * @hibernate.joined-subclass table="T_COMPONENT" dynamic-insert = "true"
 * @hibernate.joined-subclass-key column="ID"
 */
public class Component extends Form {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1934013133515711736L;
	/**
	 * @uml.property name="tagName"
	 */
	private String tagName;

	/**
	 * 获取组件名
	 * 
	 * @hibernate.property column="TAGNAME"
	 * @return 组件名
	 * @uml.property name="tagName"
	 */
	public String getTagName() {
		return tagName;
	}

	/**
	 * 设置组件名
	 * 
	 * @param tagName
	 *            组件名
	 * @uml.property name="tagName"
	 */
	public void setTagName(String tagName) {
		this.tagName = tagName;
	}
	
	public boolean equals(Object obj) {
		if (this == null)
			return false;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Form form = (Component) obj;
		if (this.getId() == null) {
			if (form.getId() != null)
				return false;
		} else if (!this.getId().equals(form.getId()))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}
	
	
	
}
