package OLink.bpm.webservice.model;

public class SimpleUser {
	public final static int USER_TYPE_DOMAINUSER = 0;

	public final static int USER_TYPE_DEVELOPER = 1;

	public final static int USER_TYPE_DOMAINADMIN = 2;

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
	 * 电话号码
	 */
	private String telephone;

	/**
	 * 登录的域名称
	 */
	private String domainName;

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
	 *            标识
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * 获取用户姓名
	 * 
	 * @return 用户姓名
	 */
	public String getName() {
		return name;
	}

	/**
	 * 设置用户姓名
	 * 
	 * @param name
	 *            用户姓名
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 获取用户登陆名
	 * 
	 * @return 用户登陆名
	 */
	public String getLoginno() {
		return loginno;
	}

	/**
	 * 设置用户登陆名
	 * 
	 * @param loginno
	 *            用户登陆名
	 */
	public void setLoginno(String loginno) {
		this.loginno = loginno;
	}

	/**
	 * 获取用户登陆密码
	 * 
	 * @return 用户登陆密码
	 */
	public String getLoginpwd() {
		return loginpwd;
	}

	/**
	 * 设置用户登陆密码
	 * 
	 * @param loginpwd
	 *            用户登陆密码
	 */
	public void setLoginpwd(String loginpwd) {
		this.loginpwd = loginpwd;
	}

	/**
	 * 获取用户电邮
	 * 
	 * @return 用户电邮
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * 设置用户电邮
	 * 
	 * @param email
	 *            用户电邮
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * 获取用户电话
	 * 
	 * @return 用户电话
	 */
	public String getTelephone() {
		return telephone;
	}

	/**
	 * 设置用户电话
	 * 
	 * @param telephone
	 *            用户电话
	 */
	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	/**
	 * 获取域名
	 * 
	 * @return 域名
	 */
	public String getDomainName() {
		return domainName;
	}

	/**
	 * 设置域名
	 * 
	 * @param domainName
	 *            域名
	 */
	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}
}
