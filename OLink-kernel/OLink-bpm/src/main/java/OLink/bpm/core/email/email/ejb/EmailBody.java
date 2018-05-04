package OLink.bpm.core.email.email.ejb;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import OLink.bpm.base.dao.ValueObject;
import OLink.bpm.core.email.attachment.ejb.Attachment;
import OLink.bpm.util.StringUtil;

public class EmailBody extends ValueObject {

	private static final long serialVersionUID = 1L;

	private String id;
	private String subject;
	private String content;
	private String from;
	private String to;
	private String cc;
	private String bcc;
	private Date sendDate;
	private Set<Attachment> attachments;
	private boolean multipart;

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
	 * @return the subject
	 */
	public String getSubject() {
		return subject;
	}

	/**
	 * @param subject
	 *            the subject to set
	 */
	public void setSubject(String subject) {
		this.subject = subject;
	}

	/**
	 * @return the content
	 */
	public String getContent() {
		return content;
	}

	/**
	 * @param content the content to set
	 */
	public void setContent(String content) {
		this.content = content;
	}

	/**
	 * @return the from
	 */
	public String getFrom() {
		return from;
	}

	/**
	 * @param from
	 *            the from to set
	 */
	public void setFrom(String from) {
		this.from = from;
	}

	/**
	 * @return the to
	 */
	public String getTo() {
		return to;
	}

	/**
	 * @param to
	 *            the to to set
	 */
	public void setTo(String to) {
		this.to = to;
	}

	/**
	 * @return the cc
	 */
	public String getCc() {
		return cc;
	}

	/**
	 * @param cc
	 *            the cc to set
	 */
	public void setCc(String cc) {
		this.cc = cc;
	}

	/**
	 * @return the bcc
	 */
	public String getBcc() {
		return bcc;
	}

	/**
	 * @param bcc
	 *            the bcc to set
	 */
	public void setBcc(String bcc) {
		this.bcc = bcc;
	}

	/**
	 * @return the sendDate
	 */
	public Date getSendDate() {
		return sendDate;
	}

	/**
	 * @param sendDate the sendDate to set
	 */
	public void setSendDate(Date sendDate) {
		this.sendDate = sendDate;
	}

	/**
	 * @return the attachments
	 */
	public Set<Attachment> getAttachments() {
		if (attachments == null) {
			attachments = new HashSet<Attachment>();
		}
		return attachments;
	}
	
	public void addAttachment(Attachment attachment) {
		if (attachment != null) {
			attachment.setEmailBody(this);
			getAttachments().add(attachment);
		}
	}

	/**
	 * @param attachments the attachments to set
	 */
	public void setAttachments(Set<Attachment> attachments) {
		this.attachments = attachments;
	}

	/**
	 * 判断邮件是否存在邮件
	 * @return the multipart
	 */
	public boolean isMultipart() {
		if (multipart) return multipart;

		this.multipart = !(attachments == null || attachments.isEmpty());
		return multipart;
	}

	/**
	 * @param multipart the multipart to set
	 */
	public void setMultipart(boolean multipart) {
		this.multipart = multipart;
	}
	
	public boolean isSentBcc() {
		return !StringUtil.isBlank(bcc);
	}
	
	public boolean isSentCc() {
		return !StringUtil.isBlank(cc);
	}

}
