package OLink.bpm.core.multilanguage.ejb;

import OLink.bpm.base.dao.ValueObject;

/**
 * @hibernate.class table="T_MULTILANGUAGE"
 * @author nicholas
 */
public class MultiLanguage extends ValueObject {
	
	private static final long serialVersionUID = 5959512178292949555L;

	private String id;

	private int type;

	private String label;

	private String text;

	/**
	 * @hibernate.id column="ID" generator-class="assigned"
	 */
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @hibernate.property column="TEXT"
	 */
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	/**
	 * @hibernate.property column="TYPE"
	 */
	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	/**
	 * @hibernate.property column="LABEL"
	 */
	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}
}
