package OLink.bpm.core.validate.repository.ejb;

import java.util.Date;

import OLink.bpm.base.dao.ValueObject;

/**
 * 校验库
 * 
 * @hibernate.class table="T_VALIDATEREPOSITORY"
 * 
 */
public class ValidateRepositoryVO extends ValueObject {

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
	 * 获取版本号
	 * 
	 * @hibernate.property column="VERSIONS"
	 * @return 版本号
	 */
	public int getVersion() {
		return version;
	}

	/**
	 * 设置版本号
	 * 
	 * @param version
	 *            版本号
	 */
	public void setVersion(int version) {
		this.version = version;
	}

	/**
	 * 获取验证库内容
	 * 
	 * @return 验证库内容
	 * @hibernate.property column="CONTENT" type = "text"
	 */
	public String getContent() {
		return content;
	}

	/**
	 * 设置验证库内容
	 * 
	 * @param content
	 *            验证库内容
	 */
	public void setContent(String content) {
		this.content = content;
	}

	/**
	 * 获取验证库标识
	 * 
	 * @hibernate.id column="ID" generator-class="assigned"
	 * @return 验证库标识
	 */
	public String getId() {
		return id;
	}

	/**
	 * 设置验证库标识
	 * 
	 * @return id 验证库标识
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * 获取验证库名称
	 * 
	 * @return 验证库名称
	 * @hibernate.property column="NAME"
	 */
	public String getName() {
		return name;
	}

	/**
	 * 设置验证库名称
	 * 
	 * @param name
	 *            验证库名称
	 */
	public void setName(String name) {
		this.name = name;
	}

}
