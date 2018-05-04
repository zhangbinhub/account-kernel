package OLink.bpm.constans;

/**
 * The web variable.
 */
public class Web {
	public static final String DEFAULT_SHOWPASSWORD = "888888";

	public static final String CONTEXT_ATTRIBUTE_DSNAME = "DSNAME";

	public static final String SESSION_ATTRIBUTE_LINES = "LINES";

	/**
	 * 后台管理员
	 */
	public static final String SESSION_ATTRIBUTE_USER = "USER";

	public static final String SKIN_TYPE = "SKINTYPE";

	/**
	 * 前台用户
	 */
	public static final String SESSION_ATTRIBUTE_FRONT_USER = "FRONT_USER";
	/**
	 * 临时前台用户
	 */
	public static final String SESSION_ATTRIBUTE_TEMP_FRONT_USER = "TEMP_FRONT_USER";

	public static final String SESSION_ATTRIBUTE_APPLICATION = "APPLICATION";

	public static final String REQUEST_ATTRIBUTE_APPLICATION = "application";

	public static final String SESSION_ATTRIBUTE_DOMAIN = "DOMAIN";

	public static final String SESSION_ATTRIBUTE_ONLINEUSER = "ONLINEUSER";

	public static final String PAGE_ATTRIBUTE_LIST = "LIST";

	public static final String PAGE_ATTRIBUTE_ISEDIT = "ISEDIT";

	public static final String PAGE_ATTRIBUTE_COMMAND = "COMMAND";

	public static final String REQUEST_ATTRIBUTE_MPK = "MPK";

	public static final String REQUEST_ATTRIBUTE_TARGET = "TARGET";

	public static final String DEFAULT_PAGE = "1";

	public static final String DEFAULT_LINES_PER_PAGE = "10";

	public static final String INFINITUDE_LINES = "9999999";

	public static final String STRING_TRUE = "TRUE";

	public static final String ERROR_SUMMARY = "ERROR_SUMMARY";

	public static final String ERROR_DETAIL = "ERROR_DETAIL";

	public static final String PRODUCT_ID_NAME = "PRODUCTIDNAME";

	public static final String DOWNLOAD_BLOB_FILE_OBJECT = "BLOB_FILE_OBJECT";

	public static final String SESSION_ATTRIBUTE_CSS = "default.css";

	public static final String SESSION_ATTRIBUTE_SUBJECT = "SESSION_ATTRIBUTE_SUBJECT";

	public static final String SESSION_ATTRIBUTE_USERLANGUAGE = "USERLANGUAGE";

	public static final String SESSION_ATTRIBUTE_CHECKCODE = "CheckCode";
	
	public static final String SESSION_ATTRIBUTE_SMSCHECKCODE = "smsCheckCode";

	public static final String SESSION_ATTRIBUTE_PROXYUSER = "PROXYUSER";

	public static final String SESSION_ATTRIBUTE_DEBUG = "DEBUG";

	/** 用户通过桌面应用登录 */
	public static final String SESSION_ATTRIBUTE_LOGINBYAPP = "LOGINBYAPP";

	public final static String TOC_DIR = "toc";

	public final static String INDEX_DIR = "index";

	/** SSO配置 **/
	public static final String SSO_LOGINACCOUNT_ATTRIBUTE = "loginAccount";
	public static final String SSO_DOMAINNAME_ATTRIBUTE = "domainName";
	public static final String SSO_INFO_SAVE_TYEP = "sso.info.save.type";
	public static final String SSO_IMPLEMENTATION = "sso.implementation";
	public static final String SSO_REDIRECT = "sso.redirect";
	public static final String SSO_LOGOUT_REDIRECT = "sso.logout.redirect";
	public static final String SSO_INFO_KEY_LGOINACCOUNT = "sso.info.key.loginAccount";
	public static final String SSO_INFO_KEY_PASSWORD = "sso.info.key.password";
	public static final String SSO_INFO_KEY_DOMAINNAME = "sso.info.key.domainName";
	public static final String SSO_INFO_KEY_EMAIL = "sso.info.key.email";

	/** AD的SSO配置 **/
	public static final String SSO_AD_DOMAINCONTROLLER = "jcifs.http.domainController";
	public static final String SSO_AD_DEFAULTDOMAIN = "jcifs.smb.client.domain";

	/** 用户验证配置 **/
	public static final String LOGIN_AUTHENTICATOR = "login.authenticator";
	public static final String AUTHENTICATION_TYPE = "authentication.type";
	public static final String AUTHENTICATION_TYPE_DEFAULT = "default";
	public static final String AUTHENTICATION_TYPE_SSO = "sso";

	/** LDAP配置 **/
	public static final String LDAP_FACTORY = "ldap.factory";
	public static final String LDAP_SECURITY_PROTOCOL = "ldap.security.protocol";
	public static final String LDAP_AUTHENTICATION = "ldap.authentication";
	public static final String LDAP_SERVER_URL = "ldap.authentication";

	/** 树形视图根节点 **/
	public static final String TREEVIEW_ROOT_NODEID = "root";

}
