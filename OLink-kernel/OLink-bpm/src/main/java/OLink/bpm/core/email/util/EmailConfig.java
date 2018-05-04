package OLink.bpm.core.email.util;

import OLink.bpm.core.email.runtime.mail.ConnectionProfile;
import OLink.bpm.util.StringUtil;
import OLink.bpm.util.property.PropertyUtil;

/**
 * 邮件配置信息
 * @author Tom
 *
 */
public class EmailConfig {
	
	//private static XmlParser xmlParser;
	private static ConnectionProfile profile;
	
	static {
		//URL xmlUrl = Thread.currentThread().getContextClassLoader().getResource("email-config.xml");
		//xmlParser = new XmlParser(xmlUrl.getFile());
		initConnectionProfile();
	}
	
	private EmailConfig() {
		
	}
	
	private static void initConnectionProfile() {
		profile = new ConnectionProfile();
		profile.setFetchPort(getString("fetch.server.port", "143"));
		profile.setFetchServer(getString("fetch.server", "127.0.0.1"));
		profile.setFetchSSL(getString("fetch.ssl", "false"));
		profile.setProtocol(getString("fetch.protocol", "imap"));
		profile.setShortName(getString("shortname", "localhost"));
		profile.setSmtpAuthenticated(getString("smtp.authenticated", "false"));
		profile.setSmtpPort(getString("smtp.server.port", "25"));
		profile.setSmtpServer(getString("smtp.server", "127.0.0.1"));
		profile.setSmtpSSL(getString("smtp.ssl", "false"));
		profile.setFolderNameSpace(getString("folder.namespace"));
	}
	
	public static String getString(String name) {
		String result = PropertyUtil.getByPropName("email", name);
		if (!StringUtil.isBlank(result)) {
			return result.trim();
		}
		return "";
	}
	
	public static String getString(String name, String defaultValue) {
		String result = getString(name);
		if (StringUtil.isBlank(result)) {
			result = defaultValue;
		}
		return result.trim();
	}
	
	public static int getInteger(String name, int defaultInt) {
		try {
			String result = getString(name);
			if (!StringUtil.isBlank(result)) {
				return Integer.parseInt(result);
			}
		} catch (Exception e) {
		}
		return defaultInt;
	}
	
	public static boolean getBoolean(String name, boolean defaultInt) {
		try {
			String result = getString(name);
			if (!StringUtil.isBlank(result)) {
				return Boolean.parseBoolean(result);
			}
		} catch (Exception e) {
		}
		return defaultInt;
	}

	public static String getEmailDomain() {
		String domain = getString("domain");
		if (StringUtil.isBlank(domain)) {
			return "localhost";
		}
		return domain;
	}
	
	/**
	 * 是否使用内部邮件功能-默认为true
	 * @return true 使用内部邮件功能 | false 不使用内部邮件功能
	 */
	public static boolean isInternalEmail() {
		return getBoolean("internal.mail", true);
	}
	
	public static ConnectionProfile getConnectionProfile() {
		return profile;
	}
	
	/**
	 * 判断系统是否使用邮件功能
	 * @return
	 */
	public static boolean isUserEmail() {
		String bool = PropertyUtil.getByPropName("email", "USER_EMAIL_FUNCTION");
		return Boolean.parseBoolean(bool);
	}
	
	public static void initEmailConfig() {
		if (EmailConfig.isInternalEmail()) {
			//垃圾箱
			Constants.DEFAULT_FOLDER_JUNK = "Junk";
			//发送箱
			Constants.DEFAULT_FOLDER_SENT = "Sent";
			//已删除
			Constants.DEFAULT_FOLDER_REMOVED = "Removed";
			//草稿箱
			Constants.DEFAULT_FOLDER_DRAFTS = "Drafts";
		} else {
			Constants.DEFAULT_FOLDER_JUNK = EmailConfig.getString("folder.trash");
			Constants.DEFAULT_FOLDER_SENT = EmailConfig.getString("folder.sent");
			Constants.DEFAULT_FOLDER_REMOVED = EmailConfig.getString("folder.removed");
			Constants.DEFAULT_FOLDER_DRAFTS = EmailConfig.getString("folder.drafts");
		}
		EmailConfig.initConnectionProfile();
	}
	
}
