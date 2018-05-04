package OLink.bpm.util.mail;

import java.util.Properties;

import javax.mail.Authenticator;

/**
 * Email Object.
 */
public class Email {
	
	private String from;

	private String to;

	/**抄送*/
	private String cc;
	/**密抄送*/
	private String bcc;

	private String subject;

	private String body;

	private String host;

	private boolean validate;

	private String user;

	private String password;
	
	private String[] attachFileNames;

	public Email(String from, String to, String subject, String body,
			String host, String user, String password, String bcc,
			boolean validate) {
		this.from = from;
		this.to = to;
		this.subject = subject;
		this.body = body;
		this.host = host;
		this.user = user;
		this.password = password;
		this.bcc = bcc;
		this.validate = validate;
	}

	public Email(String from, String to, String subject, String body, 
			String host, String user, String password, String cc, String bcc, 
			String[] attachFileNames, boolean validate) {
		super();
		this.from = from;
		this.to = to;
		this.cc = cc;
		this.bcc = bcc;
		this.subject = subject;
		this.body = body;
		this.host = host;
		this.validate = validate;
		this.user = user;
		this.password = password;
		this.attachFileNames = attachFileNames;
	}
	
	public Properties getProperties() {
		Properties props = new Properties();
		props.put("mail.smtp.host", getHost());
		if (this.isValidate()) {
			props.put("mail.smtp.auth", "true");
		} else {
			props.put("mail.smtp.auth", "false");
		}
		// google-gmail的支持
		if (getHost().indexOf("smtp.gmail.com") >= 0) {
			props.setProperty("mail.smtp.socketFactory.class",
					"javax.net.ssl.SSLSocketFactory");
			props.setProperty("mail.smtp.socketFactory.fallback", "false");
			props.setProperty("mail.smtp.port", "465");
			props.setProperty("mail.smtp.socketFactory.port", "465");
		}
		return props;
	}
	
	public Authenticator getAuthenticator() {
		if (this.isValidate()) {
			return new OBPMAuthenticator(user, password);
		}
		return null;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public boolean isValidate() {
		return validate;
	}

	public void setValidate(boolean validate) {
		this.validate = validate;
	}

	/**
	 * @return Returns the body.
	 */
	public String getBody() {
		return body;
	}

	/**
	 * @return Returns the from.
	 */
	public String getFrom() {
		return from;
	}

	/**
	 * @return Returns the subject.
	 */
	public String getSubject() {
		return subject;
	}

	/**
	 * @return Returns the to.
	 */
	public String getTo() {
		return to;
	}

	/**
	 * @param body
	 *            The body to set.
	 */
	public void setBody(String body) {
		this.body = body;
	}

	/**
	 * @param from
	 *            The from to set.
	 */
	public void setFrom(String from) {
		this.from = from;
	}

	/**
	 * @param subject
	 *            The subject to set.
	 */
	public void setSubject(String subject) {
		this.subject = subject;
	}

	/**
	 * @param to
	 *            The to to set.
	 */
	public void setTo(String to) {
		this.to = to;
	}

	/**
	 * @return Returns the bcc.
	 */
	public String getBcc() {
		return bcc;
	}

	/**
	 * @return Returns the cc.
	 */
	public String getCc() {
		return cc;
	}

	/**
	 * @param bcc
	 *            The bcc to set.
	 */
	public void setBcc(String bcc) {
		this.bcc = bcc;
	}

	/**
	 * @param cc
	 *            The cc to set.
	 */
	public void setCc(String cc) {
		this.cc = cc;
	}

	/**
	 * @return the attachFileNames
	 */
	public String[] getAttachFileNames() {
		return attachFileNames;
	}

	/**
	 * @param attachFileNames the attachFileNames to set
	 */
	public void setAttachFileNames(String[] attachFileNames) {
		this.attachFileNames = attachFileNames;
	}
	
	/**
	 * 判断是否存在附件
	 * @return true 有附件 | false 没附件
	 */
	public boolean isHaveAttachment() {
		return !(attachFileNames == null || attachFileNames.length == 0);
	}
	
}
