package OLink.bpm.core.sysconfig.ejb;

public class EmailConfig implements java.io.Serializable {
	
	private static final long serialVersionUID = 825034774180942718L;
	
	/**
	 * 邮件发送主机key
	 */
	public static final String EMAIL_SEND_HOST = "host";
	/**
	 * 邮件发送地址key
	 */
	public static final String EMAIL_SEND_ADDRESS = "from";
	/**
	 * 邮件发送账号key
	 */
	public static final String EMAIL_USER= "user";
	/**
	 * 邮件发送密码key
	 */
	public static final String EMAIL_SEND_PASSWORD = "password";
	/**
	 * 邮件抄送地址key
	 */
	public static final String EMAIL_CC_ADDRESS = "bcc";
	/**
	 * 是否使用邮件客户端key
	 */
	public static final String USE_EMAIL_CLIENT = "USER_EMAIL_FUNCTION";
	/**
	 * 是否使用内部邮件key
	 */
	public static final String USE_INNER_EMAIL = "internal.mail";
	/**
	 * 邮件功能域key
	 */
	public static final String EMAIL_FUNCTION_DOAMIN = "domain";
	/**
	 * 垃圾箱key
	 */
	public static final String EMAIL_TRASH = "folder.trash";
	/**
	 * 发送箱key
	 */
	public static final String EMAIL_SENDER = "folder.sent";
	/**
	 * 草稿箱key
	 */
	public static final String EMAIL_DRAFT = "folder.drafts";
	/**
	 * 已删除key
	 */
	public static final String EMAIL_REMOVED = "folder.removed";
	/**
	 * 收取邮件服务器key
	 */
	public static final String EMAIL_RECEIVE_SERVER = "fetch.server";
	/**
	 * 收取邮件服务器端口key
	 */
	public static final String EMAIL_RECEIVE_SERVER_PORT = "fetch.server.port";
	/**
	 * 收取邮件协议key
	 */
	public static final String EMAIL_RECEIVE_PROTOCOL = "fetch.protocol";
	/**
	 * 是否需要证书(收邮件)key
	 */
	public static final String EMAIL_RECEIVE_NEED_CERTIFICATE = "fetch.ssl";
	/**
	 * 邮件发送服务器key
	 */
	public static final String EMAIL_SEND_SERVER = "smtp.server";
	/**
	 * 邮件发送服务器端口key
	 */
	public static final String EMAIL_SEND_SERVER_PORT = "smtp.server.port";
	/**
	 * 是否可带附件key
	 */
	public static final String EMAIL_ENABLE_ACCESSOORIES = "smtp.authenticated";
	/**
	 * 是否需要证书(发邮件)key
	 */
	public static final String EMAIL_SEND_NEED_CERTIFICATE = "smtp.ssl";
	
	private String sendHost;
	private String sendAddress;
	private String sendAccount;
	private String sendPassword;
	private String ccAddress;
	private String isUseClient;
	private String isUseInnerEmail;
	private String functionDomain;
	private String trash;
	private String sender;
	private String draft;
	private String removed;
	private String fetchServer;
	private String fetchServerPort;
	private String fetchProtocol;
	private String fetchssl;
	private String smtpServer;
	private String smtpServerPort;
	private String smtpAuthenticated;
	private String smtpssl;
	
	public String getSendHost() {
		return sendHost;
	}
	
	public void setSendHost(String sendHost) {
		this.sendHost = sendHost;
	}
	
	public String getSendAddress() {
		return sendAddress;
	}
	
	public void setSendAddress(String sendAddress) {
		this.sendAddress = sendAddress;
	}
	
	public String getSendAccount() {
		return sendAccount;
	}
	
	public void setSendAccount(String sendAccount) {
		this.sendAccount = sendAccount;
	}
	
	public String getSendPassword() {
		return sendPassword;
	}
	
	public void setSendPassword(String sendPassword) {
		this.sendPassword = sendPassword;
	}
	
	public String getCcAddress() {
		return ccAddress;
	}
	
	public void setCcAddress(String ccAddress) {
		this.ccAddress = ccAddress;
	}
	
	public String getIsUseClient() {
		return isUseClient;
	}
	
	public void setIsUseClient(String isUseClient) {
		this.isUseClient = isUseClient;
	}
	
	public String getIsUseInnerEmail() {
		return isUseInnerEmail;
	}
	
	public void setIsUseInnerEmail(String isUseInnerEmail) {
		this.isUseInnerEmail = isUseInnerEmail;
	}
	
	public String getFunctionDomain() {
		return functionDomain;
	}
	
	public void setFunctionDomain(String functionDomain) {
		this.functionDomain = functionDomain;
	}
	
	public String getTrash() {
		return trash;
	}
	
	public void setTrash(String trash) {
		this.trash = trash;
	}
	
	public String getSender() {
		return sender;
	}
	
	public void setSender(String sender) {
		this.sender = sender;
	}
	
	public String getDraft() {
		return draft;
	}
	
	public void setDraft(String draft) {
		this.draft = draft;
	}
	
	public String getRemoved() {
		return removed;
	}
	
	public void setRemoved(String removed) {
		this.removed = removed;
	}
	
	public String getFetchServer() {
		return fetchServer;
	}
	
	public void setFetchServer(String fetchServer) {
		this.fetchServer = fetchServer;
	}
	
	public String getFetchServerPort() {
		return fetchServerPort;
	}
	
	public void setFetchServerPort(String fetchServerPort) {
		this.fetchServerPort = fetchServerPort;
	}
	
	public String getFetchProtocol() {
		return fetchProtocol;
	}
	
	public void setFetchProtocol(String fetchProtocol) {
		this.fetchProtocol = fetchProtocol;
	}
	public String getFetchssl() {
		return fetchssl;
	}
	
	public void setFetchssl(String fetchssl) {
		this.fetchssl = fetchssl;
	}
	
	public String getSmtpServer() {
		return smtpServer;
	}
	
	public void setSmtpServer(String smtpServer) {
		this.smtpServer = smtpServer;
	}
	
	public String getSmtpServerPort() {
		return smtpServerPort;
	}
	
	public void setSmtpServerPort(String smtpServerPort) {
		this.smtpServerPort = smtpServerPort;
	}
	
	public String getSmtpAuthenticated() {
		return smtpAuthenticated;
	}
	
	public void setSmtpAuthenticated(String smtpAuthenticated) {
		this.smtpAuthenticated = smtpAuthenticated;
	}
	
	public String getSmtpssl() {
		return smtpssl;
	}
	
	public void setSmtpssl(String smtpssl) {
		this.smtpssl = smtpssl;
	}

}
