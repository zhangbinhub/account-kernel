package OLink.bpm.base.dao;

import java.io.Serializable;

/**
 * The base value object.
 */
public abstract class ValueObject implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @uml.property  name="sortId"
	 */
	protected String sortId;

	/**
	 * @uml.property name="applicationid"
	 */
	protected String applicationid;

	/**
	 * @uml.property name="dominid"
	 */
	protected String domainid;

	/**
	 * @uml.property name="id"
	 */
	protected String id;

	protected int version;

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	/**
	 * @hibernate.id column="ID" generator-class="assigned"
	 * @uml.property name="id"
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 * @uml.property name="id"
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return java.lang.String
	 * @hibernate.property column="SORTID"
	 * @uml.property name="sortId"
	 */
	public String getSortId() {
		return sortId;
	}

	/**
	 * @param sortId
	 *            the sortId to set
	 * @uml.property name="sortId"
	 */
	public void setSortId(String sortId) {
		this.sortId = sortId;
	}

	/**
	 * @return java.lang.String
	 * @hibernate.property column="APPLICATIONID"
	 * @uml.property name="applicationid"
	 */
	public String getApplicationid() {
		return applicationid;
	}

	/**
	 * @param applicationid
	 *            the applicationid to set
	 * @uml.property name="applicationid"
	 */
	public void setApplicationid(String applicationid) {
		this.applicationid = applicationid;
	}

	public String getDomainid() {
		return domainid;
	}

	public void setDomainid(String domainid) {
		this.domainid = domainid;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
	
	
}
