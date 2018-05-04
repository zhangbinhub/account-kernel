package OLink.bpm.core.macro.repository.ejb;

import OLink.bpm.base.dao.ValueObject;

/**
 * 宏脚本库
 * 
 * @hibernate.class table="T_REPOSITORY"
 */
public class RepositoryVO extends ValueObject

{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String id;

	/*
	 * 脚本内容
	 */
	private String content;

	/*
	 * 名称
	 */
	private String name;

	private int version;

	/**
	 * @hibernate.property column="VERSIONS"
	 * @return
	 */
	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	/**
	 * @hibernate.property column="CONTENT" type = "text"
	 */
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	/**
	 * @hibernate.id column="ID" generator-class="assigned"
	 * @return Returns the id.
	 */
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @hibernate.property column="NAME"
	 */
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
