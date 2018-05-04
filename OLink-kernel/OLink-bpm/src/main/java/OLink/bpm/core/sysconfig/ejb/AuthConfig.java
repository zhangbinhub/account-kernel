package OLink.bpm.core.sysconfig.ejb;

import java.util.HashMap;
import java.util.Map;

public class AuthConfig implements java.io.Serializable {

	private static final long serialVersionUID = -5978551466014687626L;

	/**
	 * 身份验证模式key
	 */
	public static final String AUTHENTICATION_TYPE = "authentication.type";
	/**
	 * 登录验证方式key
	 */
	public static final String LOGIN_AUTHENTICATION = "login.authenticator";
	public static final Map<String, String> LOGINAUTH = new HashMap<String, String>();
	static {
		LOGINAUTH.put("default",
				"DefaultLoginAuthenticator");// 默认的登录验证
		LOGINAUTH.put("ldap",
				"LDAPLoginAuthenticator");// LDAP登录验证
	}
	/**
	 * 单点登录方式
	 */
	public static final String SSO_IMLEMENTATION = "sso.implementation";
	public static final Map<String, String> SSOAUTH = new HashMap<String, String>();
	static {
		SSOAUTH.put("cookie", "OLink.bpm.core.sso.CookieCasUserSSO");
		SSOAUTH.put("cas", "CasUserSSO");// cas服务器单点登录方式
	}
	/**
	 * 默认邮箱地址key
	 */
	public static final String SSO_DEFAULT_EMAIL = "sso.default.email";
	/**
	 * 默认用户密码key
	 */
	public static final String SSO_DEFAULT_PASSWORD = "sso.default.password";
	/**
	 * 首页重定向地址key
	 */
	public static final String SSO_REDIRECT = "sso.redirect";
	/**
	 * 注销重定向地址key
	 */
	public static final String SSO_LOGOUT_REDIRECT = "sso.logout.redirect";
	/**
	 * cas服务器的登录url的key
	 */
	public static final String CAS_SERVER_LOGIN_URL = "cas.server.login.url";
	/**
	 * cas服务器url的前缀key
	 */
	public static final String CAS_SERVER_URL_PREFIX = "cas.server.url.prefix";
	/**
	 * cas服务名key
	 */
	public static final String LOCAL_SERVER_NAME = "cas.local.server.name";
	/**
	 * AD的域key
	 */
	public static final String AD_DOMAIN_CONTROLLER = "jcifs.http.domainController";
	/**
	 * AD的默认域
	 */
	public static final String AD_DEFAULT_DOMAIN = "jcifs.smb.client.domain";
	/**
	 * 保存方式key
	 */
	public static final String SSO_INFO_SAVE_TYPE = "sso.info.save.type";
	/**
	 * 用户登录账号键名称key
	 */
	public static final String SSO_INFO_KEY_LOGINACCOUNT = "sso.info.key.loginAccount";
	/**
	 * 用户登录密码键名称key
	 */
	public static final String SSO_INFO_KEY_PASSWORD = "sso.info.key.password";
	/**
	 * 企业域键名称key
	 */
	public static final String SSO_INFO_KEY_DOMAINNAME = "sso.info.key.domainName";
	/**
	 * 用户邮箱键名称key
	 */
	public static final String SSO_INFO_KEY_EMAIL = "sso.info.key.email";
	/**
	 * 加密方式key
	 */
	public static final String SSO_INFO_DATA_ENCRYPTION = "sso.info.data.encryption";
	/**
	 * 是否启用手机短信验证
	 */
	public static final String SMS_AUTHENTICATE = "sms.authenticate";
	/**
	 * 手机短信验证码超时设置
	 */
	public static final String SMS_TIMEOUT = "sms.timeout";
	/**
	 * 手机短信的内容
	 */
	public static final String SMS_CONTENT = "sms.content";
	/**
	 * 手机短信的内容
	 */
	public static final String SMS_AFFECTMODE = "sms.affect.mode";
	/**
	 * 手机短信的内容
	 */
	public static final String SMS_STARTRANGEIP = "sms.start.range.ip";
	/**
	 * 手机短信的内容
	 */
	public static final String SMS_ENDRANGEIP= "sms.end.range.ip";
	

	private String authType;
	private String loginAuth;
	private String ssoAuth;
	private String ssoDefaultEmail;
	private String ssoDefaultPassword;
	private String ssoRedirect;
	private String ssoLogoutRedirect;
	private String casLoginUrl;
	private String casUrlPrefix;
	private String localServerName;
	private String ssoSaveType;
	private String ssoKeyLoginAccount;
	private String ssoKeyPassword;
	private String ssoKeyDomain;
	private String ssoKeyEmail;
	private String ssoDataEncryption;
	private String adDomainController;
	private String adDefaultDomain;
	/**
	 * 是否开启手机短信验证
	 */
	private String smsAuthenticate;
	/**
	 * 手机短信验证码的超时范围
	 */
	private String smsTimeout;
	/**
	 * 手机短信内容
	 */
	private String smsContent;
	/**
	 * 手机短信验证作用模式（all验证所有|match验证特定范围|exclude排除特定范围）
	 */
	private String smsAffectMode;
	/**
	 * 起始ip
	 */
	private String smsStartRangeIp;
	/**
	 * 结束ip
	 */
	private String smsEndRangeIp;
	

	public String getSmsAffectMode() {
		return smsAffectMode;
	}

	public void setSmsAffectMode(String smsAffectMode) {
		this.smsAffectMode = smsAffectMode;
	}

	public String getSmsStartRangeIp() {
		return smsStartRangeIp;
	}

	public void setSmsStartRangeIp(String smsStartRangeIp) {
		this.smsStartRangeIp = smsStartRangeIp;
	}

	public String getSmsEndRangeIp() {
		return smsEndRangeIp;
	}

	public void setSmsEndRangeIp(String smsEndRangeIp) {
		this.smsEndRangeIp = smsEndRangeIp;
	}

	public String getSmsAuthenticate() {
		if("true".equals(smsAuthenticate)){
			return smsAuthenticate;
		}
		return "false";
		
	}

	public void setSmsAuthenticate(String smsAuthenticate) {
		this.smsAuthenticate = smsAuthenticate;
	}

	public String getSmsTimeout() {
		return smsTimeout;
	}

	public void setSmsTimeout(String smsTimeout) {
		this.smsTimeout = smsTimeout;
	}

	public String getSmsContent() {
		return smsContent;
	}

	public void setSmsContent(String smsContent) {
		this.smsContent = smsContent;
	}

	public String getAuthType() {
		return authType;
	}

	public void setAuthType(String authType) {
		this.authType = authType;
	}

	public String getLoginAuth() {
		return loginAuth;
	}

	public void setLoginAuth(String loginAuth) {
		this.loginAuth = loginAuth;
	}

	public String getSsoAuth() {
		return ssoAuth;
	}

	public void setSsoAuth(String ssoAuth) {
		this.ssoAuth = ssoAuth;
	}

	public String getSsoDefaultEmail() {
		return ssoDefaultEmail;
	}

	public void setSsoDefaultEmail(String ssoDefaultEmail) {
		this.ssoDefaultEmail = ssoDefaultEmail;
	}

	public String getSsoDefaultPassword() {
		return ssoDefaultPassword;
	}

	public void setSsoDefaultPassword(String ssoDefaultPassword) {
		this.ssoDefaultPassword = ssoDefaultPassword;
	}

	public String getSsoRedirect() {
		return ssoRedirect;
	}

	public void setSsoRedirect(String ssoRedirect) {
		this.ssoRedirect = ssoRedirect;
	}

	public String getSsoLogoutRedirect() {
		return ssoLogoutRedirect;
	}

	public void setSsoLogoutRedirect(String ssoLogoutRedirect) {
		this.ssoLogoutRedirect = ssoLogoutRedirect;
	}

	public String getCasLoginUrl() {
		return casLoginUrl;
	}

	public void setCasLoginUrl(String casLoginUrl) {
		this.casLoginUrl = casLoginUrl;
	}

	public String getCasUrlPrefix() {
		return casUrlPrefix;
	}

	public void setCasUrlPrefix(String casUrlPrefix) {
		this.casUrlPrefix = casUrlPrefix;
	}

	public String getLocalServerName() {
		return localServerName;
	}

	public void setLocalServerName(String localServerName) {
		this.localServerName = localServerName;
	}

	public String getSsoSaveType() {
		return ssoSaveType;
	}

	public void setSsoSaveType(String ssoSaveType) {
		this.ssoSaveType = ssoSaveType;
	}

	public String getSsoKeyLoginAccount() {
		return ssoKeyLoginAccount;
	}

	public void setSsoKeyLoginAccount(String ssoKeyLoginAccount) {
		this.ssoKeyLoginAccount = ssoKeyLoginAccount;
	}

	public String getSsoKeyPassword() {
		return ssoKeyPassword;
	}

	public void setSsoKeyPassword(String ssoKeyPassword) {
		this.ssoKeyPassword = ssoKeyPassword;
	}

	public String getSsoKeyDomain() {
		return ssoKeyDomain;
	}

	public void setSsoKeyDomain(String ssoKeyDomain) {
		this.ssoKeyDomain = ssoKeyDomain;
	}

	public String getSsoKeyEmail() {
		return ssoKeyEmail;
	}

	public void setSsoKeyEmail(String ssoKeyEmail) {
		this.ssoKeyEmail = ssoKeyEmail;
	}

	public String getSsoDataEncryption() {
		return ssoDataEncryption;
	}

	public void setSsoDataEncryption(String ssoDataEncryption) {
		this.ssoDataEncryption = ssoDataEncryption;
	}

	public boolean isLdaptLogin() {
		return getLoginAuth("ldap").equals(this.getLoginAuth());
	}

	public static String getLoginAuth(String key) {
		return LOGINAUTH.get(key);
	}

	public static String getSsoAuth(String key) {
		return SSOAUTH.get(key);
	}

	public String getAdDomainController() {
		return adDomainController;
	}

	public void setAdDomainController(String adDomainController) {
		this.adDomainController = adDomainController;
	}

	public String getAdDefaultDomain() {
		return adDefaultDomain;
	}

	public void setAdDefaultDomain(String adDefaultDomain) {
		this.adDefaultDomain = adDefaultDomain;
	}

}
