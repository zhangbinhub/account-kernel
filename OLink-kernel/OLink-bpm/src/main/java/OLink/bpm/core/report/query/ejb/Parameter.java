package OLink.bpm.core.report.query.ejb;

import OLink.bpm.base.dao.ValueObject;

/**
 * @hibernate.class table="T_QUERY_PARAMETER"
 * 
 */
public class Parameter extends ValueObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String id;
	
	private String name;
	
	private String defaultValue;
	
	private Query query;
	/**
	 * @hibernate.property column="DEFAULTVALUE"
	 */
	public String getDefaultValue() {
		return defaultValue;
	}
	/**
	 * @param defaultValue The defaultValue to set.
	 */
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
	
	/**
	 * @hibernate.id column="ID" generator-class="assigned"
	 */
	public String getId() {
		return id;
	}
	/**
	 * @param id The id to set.
	 */
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * @hibernate.property column="NAME"
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @hibernate.many-to-one column="QUERY_ID"
	 *                        class="Query"
	 * 
	 */
	public Query getQuery() {
		return query;
	}

	public void setQuery(Query query) {
		this.query = query;
	}
	
}
