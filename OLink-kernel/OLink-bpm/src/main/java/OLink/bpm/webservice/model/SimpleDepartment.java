package OLink.bpm.webservice.model;

public class SimpleDepartment {

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
	 * 英文名称
	 * 
	 * @uml.property name="engname"
	 */
	private String engname;

	/**
	 * @uml.property name="code"
	 */
	private String code;

	/**
	 * @uml.property name="level"
	 */
	private int level;

	/**
	 * 企业域名称
	 */
	private String domainName;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEngname() {
		return engname;
	}

	public void setEngname(String engname) {
		this.engname = engname;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public String getDomainName() {
		return domainName;
	}

	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}

}
