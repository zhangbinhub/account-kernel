package OLink.bpm.core.sysconfig.ejb;

public class LdapConfig implements java.io.Serializable {
	
	private static final long serialVersionUID = -406235282239220914L;
	
	/**
	 * ldap的url对应的key
	 */
	public static final String LDAP_URL = "ldap.url";
	/**
	 * ldap的baseDN
	 */
	public static final String LDAP_BASEDN = "ldap.baseDN";
	/**
	 * ldap是否池化(是否启动连接池)key
	 */
	public static final String LDAP_POOLED = "ldap.pooled";
	/**
	 * 用户id的key
	 */
	public static final String ID = "user.id";
	/**
	 * 用户登陆账号key
	 */
	public static final String LOGINNO = "user.loginno";
	/**
	 * 用户登录密码key
	 */
	public static final String LOGINPWD = "user.loginpwd";
	/**
	 * 用户名key
	 */
	public static final String NAME = "user.name";
	/**
	 * 用户邮箱key
	 */
	public static final String EMAIL = "user.email";
	/**
	 * 用户电话号码key
	 */
	public static final String TELEPHONE = "user.telephone";
	/**
	 * 用户的dn结构key
	 */
	public static final String DIRSTRUCTURE = "user.dirStructure";
	
	private String url;
	private String baseDN;
	private String pooled;
	private String id_;
	private String loginno_;
	private String loginpwd_;
	private String name_;
	private String email_;
	private String telephone_;
	private String dirStructure;
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getBaseDN() {
		return baseDN;
	}

	public void setBaseDN(String baseDN) {
		this.baseDN = baseDN;
	}

	public String getPooled() {
		return pooled;
	}

	public void setPooled(String pooled) {
		this.pooled = pooled;
	}

	public LdapConfig() {
	}

	public String getId_() {
		return id_;
	}

	public void setId_(String id) {
		id_ = id;
	}

	public String getLoginno_() {
		return loginno_;
	}

	public void setLoginno_(String loginno) {
		loginno_ = loginno;
	}

	public String getLoginpwd_() {
		return loginpwd_;
	}

	public void setLoginpwd_(String loginpwd) {
		loginpwd_ = loginpwd;
	}

	public String getName_() {
		return name_;
	}

	public void setName_(String name) {
		name_ = name;
	}

	public String getEmail_() {
		return email_;
	}

	public void setEmail_(String email) {
		email_ = email;
	}

	public String getTelephone_() {
		return telephone_;
	}

	public void setTelephone_(String telephone) {
		telephone_ = telephone;
	}

	public String getDirStructure() {
		return dirStructure;
	}

	public void setDirStructure(String dirStructure) {
		this.dirStructure = dirStructure;
	}
	
	public boolean isValidDirStructure() {
		if(this.dirStructure != null) {
			return this.dirStructure.matches("((\\w+?)[=](\\w+?|[?])[,])*?((\\w+?)[=](\\w+?|[?]))");
		}
		return false;
	}
	
	public boolean isValidBaseDN() {
		if(this.baseDN != null) {
			return this.baseDN.matches("((\\w+?)[=](\\w+?)[,])*?((\\w+?)[=](\\w+?))");
		}
		return false;
	}
}
