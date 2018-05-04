package OLink.bpm.core.email.util;

import javax.mail.Folder;
import javax.servlet.http.HttpSession;

import OLink.bpm.constans.Web;
import OLink.bpm.core.email.email.ejb.EmailUser;
import OLink.bpm.util.StringUtil;
import OLink.bpm.util.property.MultiLanguageProperty;

public final class Constants {

	/**收件箱*/
	public final static String DEFAULT_FOLDER_INBOX = "INBOX";
	
	/**垃圾箱*/
	public static String DEFAULT_FOLDER_JUNK = "Junk";
	
	/**发送箱*/
	public static String DEFAULT_FOLDER_SENT = "Sent";
	
	/**已删除*/
	//public static String DEFAULT_FOLDER_TRASH = "TRASHBOX";
	public static String DEFAULT_FOLDER_REMOVED = "Removed";
	
	/**草稿箱*/
	public static String DEFAULT_FOLDER_DRAFTS = "Drafts";
	
	/**邮件系统默认箱所属者ID*/
	public final static String SYSTEM_FOLDER_ID = "0000-0000-00000000-0000-000000000000";
	
	static {
		if (!EmailConfig.isInternalEmail()) {
			DEFAULT_FOLDER_JUNK = EmailConfig.getString("folder.trash");
			DEFAULT_FOLDER_SENT = EmailConfig.getString("folder.sent");
			DEFAULT_FOLDER_REMOVED = EmailConfig.getString("folder.removed");
			DEFAULT_FOLDER_DRAFTS = EmailConfig.getString("folder.drafts");
		}
	}
	
	/**Protocol连接类型：只读 <br>POP3协议只能通过该类型打开Folder*/
	public final static int CONNECT_TYPE_READ_ONLY = Folder.READ_ONLY;
	
	/**Protocol连接类型：读与写 <br>IMAP协议可以通过该类型打开Folder*/
	public final static int CONNECT_TYPE_READ_WRITE = Folder.READ_WRITE;

	public static String getFolderDisplay(String folderName) {
//		core.email.folder.inbox = 收件箱
//		core.email.folder.junk = 垃圾箱
//		core.email.folder.sent = 发送箱
//		core.email.folder.removed = 已删除
//		core.email.folder.drafts = 草稿箱
		if (Constants.DEFAULT_FOLDER_INBOX.equals(folderName)) {
			return "{*[core.email.folder.inbox]*}";
		} else if (Constants.DEFAULT_FOLDER_DRAFTS.equals(folderName)) {
			return "{*[core.email.folder.drafts]*}";
		} else if (Constants.DEFAULT_FOLDER_SENT.equals(folderName)) {
			return "{*[core.email.folder.sent]*}";
		} else if (Constants.DEFAULT_FOLDER_REMOVED.equals(folderName)) {
			return "{*[core.email.folder.removed]*}";
		} else if (Constants.DEFAULT_FOLDER_JUNK.equals(folderName)) {
			return "{*[core.email.folder.junk]*}";
		} else {
			return folderName;
		}
	}
	
	public static boolean isSystemFolder(String folderName) {
		if (Constants.DEFAULT_FOLDER_INBOX.equals(folderName)) {
			return true;
		} else if (Constants.DEFAULT_FOLDER_DRAFTS.equals(folderName)) {
			return true;
		} else if (Constants.DEFAULT_FOLDER_SENT.equals(folderName)) {
			return true;
		} else if (Constants.DEFAULT_FOLDER_REMOVED.equals(folderName)) {
			return true;
		} else return Constants.DEFAULT_FOLDER_JUNK.equals(folderName);
	}
	
	public static String accountToEmailShowAddress(EmailUser user) {
		StringBuffer buffer = new StringBuffer();
		if (user != null) {
			String address = user.getAccount() + ("@") + EmailConfig.getEmailDomain();
			if (StringUtil.isBlank(user.getName())) {
				buffer.append(address);
			} else {
				buffer.append(user.getName());
				buffer.append(" <").append(address).append(">");
			}
		}
		return buffer.toString();
	}
	
	public static String emailShowAddressToAddress(String showAddress) {
		if (StringUtil.isBlank(showAddress)) {
			return "";
		}
		int start = showAddress.indexOf('<') + 1;
		int end = showAddress.lastIndexOf('>');
		if (end < 0) {
			end = showAddress.length();
		}
		if (start >= 0 && end > 0 && start < end) {
			return showAddress.substring(start, end).trim();
		}
		return "";
	}
	
	public static String emailAddress2Account(String address) {
		String temp = emailShowAddressToAddress(address);
		//if (temp.endsWith(EmailConfig.getEmailDomain())) {
			int index = temp.indexOf('@');
			if (index < 0) {
				index = temp.length();
			}
			return temp.substring(0, index);
		//}
		//return temp;
	}
	
	public static String getText(HttpSession session, String key) {
		try {
			String language = (String) session.getAttribute(Web.SESSION_ATTRIBUTE_USERLANGUAGE);
			return MultiLanguageProperty.getProperty(language, key, key);
		} catch (Exception e) {
			//log.warn("Load multilanguage " + key + "error: " + e.getMessage());
		}
		return key;
	}
	
	public static final String EMAIL_BASE_URL = "portal/share/email/index.jsp";
	
	public static void main(String[] args) throws Exception {
		EmailUser user = new EmailUser();
		user.setAccount("aaaa");
		user.setName("niuB");
		System.out.println(Constants.accountToEmailShowAddress(user));
		System.out.println(Constants.emailShowAddressToAddress("asdfadf <asdfas@qq.com>"));
		System.out.println(Constants.emailAddress2Account("asdfadf <asdfas@qq.com>"));
		System.out.println(Utility.stringToAddressArray("asdfadf <asdfas@qq.com>, asdfadf <asdfas@qq.com>")[0]);
	}
	
}
