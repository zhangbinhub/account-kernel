package OLink.bpm.webservice.model;

public class SimpleDomain {
	/**
	 * 主键
	 * 
	 * @uml.property name="id"
	 */
	private String id;

	/**
	 * 部门名称
	 * 
	 * @uml.property name="name"
	 */
	private String name;

	/**
	 * 1:effective ;0:invalid
	 */
	private int status;

	/**
	 * 描述
	 * 
	 * @uml.property name="description"
	 */
	private String description;

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
	 * 获取部门名称
	 * 
	 * @return 部门名称
	 */
	public String getName() {
		return name;
	}

	/**
	 * 设置部门名称
	 * 
	 * @param name
	 *            部门名称
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 获取状态
	 * 
	 * @return 状态
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * 设置状态
	 * 
	 * @param status
	 *            状态
	 */
	public void setStatus(int status) {
		this.status = status;
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

}
