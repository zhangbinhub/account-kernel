package OLink.bpm.webservice.model;

import java.util.Collection;
import java.util.Date;

public class SimpleApplication {
	private String id;

	private String name;

	private String description;

	private Collection<?> developerNames;

	private Date createDate;

	private int registerCount;

	/**
	 * 获取标识
	 * 
	 * @return 标识
	 */
	public String getId() {
		return id;
	}

	/**
	 * 设置标识
	 * 
	 * @param id
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * 获取名称
	 * 
	 * @return 名称
	 */
	public String getName() {
		return name;
	}

	/**
	 * 设置名称
	 * 
	 * @param name
	 *            名称
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 获取描述
	 * 
	 * @return 描述
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * 设置描述
	 * 
	 * @param description
	 *            描述
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * 获取创建日期
	 * 
	 * @return 创建日期
	 */
	public Date getCreateDate() {
		return createDate;
	}

	/**
	 * 设置创建日期
	 * 
	 * @param createDate
	 *            创建日期
	 */
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	/**
	 * 获取注册数
	 * 
	 * @return 注册数
	 */
	public int getRegisterCount() {
		return registerCount;
	}

	/**
	 * 设置注册数
	 * 
	 * @param registerCount
	 *            注册数
	 */
	public void setRegisterCount(int registerCount) {
		this.registerCount = registerCount;
	}

	/**
	 * 获取开发人员列表
	 * 
	 * @return 开发人员列表
	 */
	public Collection<?> getDeveloperNames() {
		return developerNames;
	}

	/**
	 * 设置开发人员列表
	 * 
	 * @param developerNames
	 *            开发人员列表
	 */
	public void setDeveloperNames(Collection<?> developerNames) {
		this.developerNames = developerNames;
	}

}
