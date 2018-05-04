package OLink.bpm.core.email.email.action;

import OLink.bpm.core.email.attachment.ejb.Attachment;
import OLink.bpm.core.email.email.ejb.Email;
import OLink.bpm.core.email.email.ejb.EmailProcess;
import OLink.bpm.core.email.email.ejb.EmailUserProcess;
import OLink.bpm.core.email.folder.ejb.EmailFolder;
import OLink.bpm.core.email.folder.ejb.EmailFolderProcess;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.core.email.util.Constants;
import OLink.bpm.core.email.util.EmailProcessUtil;
import OLink.bpm.core.email.util.Utility;
import OLink.bpm.core.email.email.ejb.EmailUser;
import OLink.bpm.core.email.util.EmailConfig;
import OLink.bpm.util.StringUtil;
import OLink.bpm.core.user.action.WebUser;

public class EmailHelper {

	public String getEmailTitleName(String folderid) {
		StringBuffer html = new StringBuffer();
		return html.toString();
	}
	
	public String getEmailTitleName(EmailFolder folder) {
		StringBuffer html = new StringBuffer();
		return html.toString();
	}
	
	public int getUnreadMessageCount(String folderid, WebUser webUser) {
		try {
			EmailProcess emailProcess = (EmailProcess) EmailProcessUtil.createProcess(EmailProcess.class, webUser);
			return emailProcess.getUnreadMessageCount(folderid, webUser.getEmailUser());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	public boolean isShowUnreadCount(String folderid, WebUser webUser) {
		try {
			EmailFolderProcess folderProcess = (EmailFolderProcess) EmailProcessUtil.createProcess(EmailFolderProcess.class, webUser);
			EmailFolder folder = folderProcess.getEmailFolderById(folderid);
			if (folder != null && Constants.DEFAULT_FOLDER_INBOX.equals(folder.getName())) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean isShowSender(String folderid, WebUser webUser) {
		try {
			EmailFolderProcess folderProcess = (EmailFolderProcess) EmailProcessUtil.createProcess(EmailFolderProcess.class, webUser);
			EmailFolder folder = folderProcess.getEmailFolderById(folderid);
			if (folder != null && Constants.DEFAULT_FOLDER_DRAFTS.equals(folder.getName())) {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}
	
	public String getShortContent(String content) {
		return content;
	}
	
	public int getEmailAttachmentCount(Email email) {
		if (email != null && email.getEmailBody() != null) {
			return email.getEmailBody().getAttachments().size();
		}
		return 0;
	}
	
	public String getEmailFirstAttachmentHtml(Email email) {
		StringBuffer html = new StringBuffer();
		int count = getEmailAttachmentCount(email);
		if (email.getEmailBody().isMultipart()) {
			Attachment attachment = email.getEmailBody().getAttachments().iterator().next();
			html.append("<tr>\n");
			html.append("\t\t\t\t\t<td align=\"right\">{*[Attachment]*}&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;:</td>\n");
			html.append("\t\t\t\t\t<td>").append(count).append("&nbsp;个&nbsp;(&nbsp;<a href=\"javascript:void(0);\" id=\"attLink\">");
			html.append(attachment.getRealFileName());
			html.append("</a>&nbsp;)</td>\n");
			html.append("\t\t\t\t</tr>");
		}
		return html.toString();
	}
	
	public String getEmailAccount(WebUser webUser) throws SecurityException {
		if (webUser.getEmailUser() != null) {
			return Utility.getEmailAddress(webUser.getEmailUser().getAccount());
		} else {
			throw new SecurityException("Can't find email user!");
		}
	}
	
	public int getEmailCount(EmailFolder folder, WebUser webUser) {
		try {
			EmailProcess emailProcess = (EmailProcess) EmailProcessUtil.createProcess(EmailProcess.class, webUser);
			return emailProcess.getEmailCount(folder, webUser.getEmailUser());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	public String showAccount(EmailUser user) {
		return Constants.accountToEmailShowAddress(user);
	}
	
	public String showAccountName(String address) {
		return "";
	}
	
	public String showEmailAddress(String accouts, WebUser user) {
		if (!EmailConfig.isInternalEmail()) {
			return accouts;
		}
		String[] address = accouts.split(";");
		if (address == null || address.length == 1) {
			address = accouts.split(",");
		}
		StringBuffer buffer = new StringBuffer();
		try {
			EmailUserProcess process = (EmailUserProcess) ProcessFactory.createProcess(EmailUserProcess.class);
			for (int i = 0; i < address.length; i++) {
				String addr = address[i];
				EmailUser emailUser = process.getEmailUser(addr, user.getDomainid());
				if (emailUser != null) {
					addr = Constants.accountToEmailShowAddress(emailUser);
					if (StringUtil.isBlank(addr)) {
						addr = address[i];
					}
					if (buffer.length() > 1) {
						buffer.append(";").append(addr);
					} else {
						buffer.append(addr);
					} 
				} else {
					if (!Utility.isBlank(addr)) {
						buffer.append(addr).append("@").append(EmailConfig.getEmailDomain());
					}
				}
			}
		} catch (Exception e) {
			
		}
		return buffer.toString();
	}
	
	public String decoder(String str) {
		if (!StringUtil.isBlank(str)) {
			str = str.replaceAll("\\*", "\\+");
		}
		return str;
	}
	
	public String encoder(String str) {
		if (!StringUtil.isBlank(str)) {
			str = str.replaceAll("\\+", "\\*");
		}
		return str;
	}
	
}
