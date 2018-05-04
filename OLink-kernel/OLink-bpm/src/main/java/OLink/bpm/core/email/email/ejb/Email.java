package OLink.bpm.core.email.email.ejb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import OLink.bpm.base.dao.ValueObject;
import OLink.bpm.core.email.folder.ejb.EmailFolder;
import OLink.bpm.core.email.runtime.model.EmailHeader;
import OLink.bpm.core.email.runtime.parser.HTMLMessageParser;
import OLink.bpm.util.StringUtil;
import OLink.bpm.core.email.attachment.ejb.Attachment;
import OLink.bpm.core.email.runtime.model.EmailPart;

public class Email extends ValueObject {

	private static final long serialVersionUID = 1L;

	private String id;
	private EmailBody emailBody;
	private boolean read;
	private EmailFolder emailFolder;
	private Date readDate;
	private long emailId;
	private boolean reply;
	private boolean forward;
	private EmailUser emailUser;

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the emailBody
	 */
	public EmailBody getEmailBody() {
		if (emailBody == null) {
			emailBody = new EmailBody();
		}
		return emailBody;
	}

	/**
	 * @param emailBody the emailBody to set
	 */
	public void setEmailBody(EmailBody emailBody) {
		this.emailBody = emailBody;
	}

	/**
	 * @return the read
	 */
	public boolean isRead() {
		return read;
	}

	/**
	 * @param read
	 *            the read to set
	 */
	public void setRead(boolean read) {
		this.read = read;
	}

	/**
	 * @return the emailFolder
	 */
	public EmailFolder getEmailFolder() {
		if (emailFolder == null) {
			emailFolder = new EmailFolder();
		}
		return emailFolder;
	}

	/**
	 * @param emailFolder the emailFolder to set
	 */
	public void setEmailFolder(EmailFolder emailFolder) {
		this.emailFolder = emailFolder;
	}

	/**
	 * @return the readDate
	 */
	public Date getReadDate() {
		return readDate;
	}

	/**
	 * @param readDate
	 *            the readDate to set
	 */
	public void setReadDate(Date readDate) {
		this.readDate = readDate;
	}

	/**
	 * @return the emailId
	 */
	public long getEmailId() {
		return emailId;
	}

	/**
	 * @param emailId the emailId to set
	 */
	public void setEmailId(long emailId) {
		this.emailId = emailId;
	}

	/**
	 * @return the reply
	 */
	public boolean isReply() {
		return reply;
	}

	/**
	 * @param reply the reply to set
	 */
	public void setReply(boolean reply) {
		this.reply = reply;
	}

	/**
	 * @return the forward
	 */
	public boolean isForward() {
		return forward;
	}

	/**
	 * @param forward the forward to set
	 */
	public void setForward(boolean forward) {
		this.forward = forward;
	}

	/**
	 * @return the emailUser
	 */
	public EmailUser getEmailUser() {
		return emailUser;
	}

	/**
	 * @param emailUser the emailUser to set
	 */
	public void setEmailUser(EmailUser emailUser) {
		this.emailUser = emailUser;
	}
	
	public Collection<String> getReceiver() {
		Collection<String> result = new ArrayList<String>();
		if (!StringUtil.isBlank(emailBody.getTo())) {
			addReceiver(result, emailBody.getTo());
		}
		if (!StringUtil.isBlank(emailBody.getCc())) {
			addReceiver(result, emailBody.getCc());
		}
		if (!StringUtil.isBlank(emailBody.getBcc())) {
			addReceiver(result, emailBody.getBcc());
		}
		return result;
	}
	
	private void addReceiver(Collection<String> coll, String receiver) {
		String[] receivers = receiver.split(";");
		if (receivers == null || receivers.length == 1) {
			receivers = receiver.split(",");
		}
		for (int i = 0; i < receivers.length; i++) {
			String temp = receivers[i];
			if (!StringUtil.isBlank(temp)) {
				coll.add(temp);
			}
		}
	}
	
	public static Email valueOf(EmailHeader header) {
		Email email = null;
		if (header != null) {
			email = new Email();
			email.setId(String.valueOf(header.getEmailUID()));
			email.setRead(!header.getUnread());
			email.getEmailBody().setMultipart(header.isMultipart());
			email.getEmailBody().setCc(header.getCcString());
			email.getEmailBody().setSendDate(header.getDate());
			email.getEmailBody().setFrom(header.getFromString());
			email.getEmailBody().setTo(header.getToString());
			email.getEmailBody().setSubject(header.getSubject());
			email.getEmailFolder().setId(String.valueOf(header.getFolderid()));
		}
		return email;
	}
	
	/**
	 * 运行时邮件对象转换成平台邮件实体
	 * @param email
	 * @return
	 */
	public static Email valueOf(OLink.bpm.core.email.runtime.model.Email email) {
		Email result = null;
		if (email != null) {
			result = new Email();
			result.setRead(!email.getBaseHeader().getUnread());
			result.getEmailBody().setCc(email.getCc());
			result.getEmailBody().setSendDate(email.getDate());
			result.getEmailBody().setFrom(email.getFrom());
			result.getEmailBody().setTo(email.getTo());
			result.getEmailBody().setSubject(email.getSubject());
			if (email.getParts() != null && !email.getParts().isEmpty()) {
				EmailPart part = email.getParts().get(0);
				result.getEmailBody().setContent(HTMLMessageParser.toContent(part.getContent().toString()));
				for (int i = 1; i < email.getParts().size(); i++) {
					part = email.getParts().get(i);
					if (!part.isHTMLText() && !part.isPlainText()) {
						Attachment attachment = new Attachment();
						attachment.setRealFileName(part.getFileName());
						attachment.setFileName(part.getFileName());
						attachment.setSize(part.getSize());
						result.getEmailBody().addAttachment(attachment);
					} else {
						if (part.isHTMLText()) {
							result.getEmailBody().setContent(part.getContent().toString());
						}
					}
				}
			}
		}
		return result;
	}
	
	public boolean isMultipart() {
		return getEmailBody().isMultipart();
	}
	
}
