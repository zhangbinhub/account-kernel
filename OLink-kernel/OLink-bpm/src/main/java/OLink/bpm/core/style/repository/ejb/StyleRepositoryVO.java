package OLink.bpm.core.style.repository.ejb;

import java.util.Date;

import OLink.bpm.base.dao.ValueObject;

/**
 * 样式库
 * 
 * @hibernate.class table="T_STYLEREPOSITORY"
 * @author Marky
 * 
 */
public class StyleRepositoryVO extends ValueObject

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

	/**
	 * 版本号
	 */
	private int version;

	/**
	 * 最后修改日期
	 */

	private Date lastmodifytime;

	/**
	 * 返回最后修改日期. 此为记录表单修改的最后日期.
	 * 
	 * @hibernate.property column="LASTMODIFYTIME"
	 * @return 最后的修改日期
	 */
	public Date getLastmodifytime() {
		return lastmodifytime;
	}

	/**
	 * 设置最后的修改日期. 此为记录表单修改的最后日期.
	 * 
	 * @param lastmodifytime
	 *            最后的修改日期
	 */
	public void setLastmodifytime(Date lastmodifytime) {
		this.lastmodifytime = lastmodifytime;
	}

	/**
	 * 返回样式库版本
	 * 
	 * @hibernate.property column="VERSIONS"
	 * @return 样式库版本
	 */
	public int getVersion() {
		return version;
	}

	/**
	 * 设置样式库版本
	 * 
	 * @param version
	 */
	public void setVersion(int version) {
		this.version = version;
	}

	/**
	 * 返回样式库脚本内容
	 * 
	 * @hibernate.property column="CONTENT" type = "text"
	 */
	public String getContent() {
		return content;
	}

	/**
	 * 设置样式库脚本内容
	 * 
	 * @param content
	 *            样式库脚本内容
	 */
	public void setContent(String content) {
		this.content = content;
	}

	/**
	 * 主键
	 * 
	 * @hibernate.id column="ID" generator-class="assigned"
	 * @return Returns the id.
	 */
	public String getId() {
		return id;
	}

	/**
	 * Set 主键
	 * 
	 * @param id
	 *            主键
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * 返回样式库名
	 * 
	 * @hibernate.property column="NAME"
	 */
	public String getName() {
		return name;
	}

	/**
	 * 设置样式库名
	 * 
	 * @param name
	 *            样式库名
	 */
	public void setName(String name) {
		this.name = name;
	}

}
