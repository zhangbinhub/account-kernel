package OLink.bpm.webservice.model;

public class SimpleRole {

	/**
	 * 主键
	 */
	private String id;

	/**
	 * 权限组名称
	 */
	private String name;

	/**
	 * 英文名
	 */
	private String engname;

	/**
	 * 软件名称
	 */
	private String applicationName;

	/**
	 * 英文名
	 * 
	 * @return 部门(java.lang.String)
	 * @hibernate.property column="ENGNAME"
	 * @roseuid 44C8C25D0057
	 */
	public String getEngname() {
		return engname;
	}

	/**
	 * 角色标识
	 * 
	 * @return 角色标识(java.lang.String)
	 * @hibernate.id column="ID" generator-class = "assigned"
	 * @roseuid 44C7A18C03A5
	 */
	public String getId() {
		return id;
	}

	/**
	 * 角色名
	 * 
	 * @return 角色名(java.lang.String)
	 * @hibernate.property column="NAME"
	 * @roseuid 44C8C25D035A
	 */
	public String getName() {
		return name;
	}

	/**
	 * 设置英文名
	 * 
	 * @param engname
	 * @roseuid 44C8C25D018D
	 */
	public void setEngname(String engname) {
		this.engname = engname;
	}

	/**
	 * 设置角色标识
	 * 
	 * @param id
	 *            角色标识
	 * @roseuid 44C7A18C03A6
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * 设置角色名
	 * 
	 * @param name
	 *            角色名
	 * @roseuid 44C8C25E009E
	 */
	public void setName(String name) {
		this.name = name;
	}

	public String getApplicationName() {
		return applicationName;
	}

	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}

}
