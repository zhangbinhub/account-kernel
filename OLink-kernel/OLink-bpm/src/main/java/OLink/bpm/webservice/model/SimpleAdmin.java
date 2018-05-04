package OLink.bpm.webservice.model;

public class SimpleAdmin {
	/**
	 * 主键
	 */
	private String id;

	/**
	 * 用户姓名
	 */
	private String name;

	/**
	 * 用户登陆名，UserID
	 */
	private String loginno;

	/**
	 * 密码，通过ＭＤ５加密后然后进行移位运算。
	 */
	private String loginpwd;

	/**
	 * 用户电子邮件
	 */
	private String email;

	/**
	 * 管理的软件名称列表
	 */
	private String[] applicationNames;

	/**
	 * 管理的企业域名称列表
	 */
	private String[] domainNames;

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

	public String getLoginno() {
		return loginno;
	}

	public void setLoginno(String loginno) {
		this.loginno = loginno;
	}

	public String getLoginpwd() {
		return loginpwd;
	}

	public void setLoginpwd(String loginpwd) {
		this.loginpwd = loginpwd;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String[] getApplicationNames() {
		return applicationNames;
	}

	public void setApplicationNames(String[] applicationNames) {
		this.applicationNames = applicationNames;
	}

	public String[] getDomainNames() {
		return domainNames;
	}

	public void setDomainNames(String[] domainNames) {
		this.domainNames = domainNames;
	}
}
