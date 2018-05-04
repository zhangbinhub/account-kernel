package OLink.bpm.core.email.attachment.ejb;

import java.util.Date;

import OLink.bpm.base.dao.ValueObject;
import OLink.bpm.core.email.util.Utility;
import OLink.bpm.core.email.email.ejb.EmailBody;
import OLink.bpm.core.email.runtime.model.EmailPart;
import OLink.bpm.core.email.util.AttachmentUtil;

/**
 * 邮件附件
 * @author Tom
 *
 */
public class Attachment extends ValueObject {

	private static final long serialVersionUID = 1L;

	private String id;

	private EmailBody emailBody;

	private String fileName;

	private String realFileName;

	private byte[] fileText;

	private String path;

	private Date createDate;
	
	private long size;
	
	private Object content;
	
	private String emailid;

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
		return emailBody;
	}

	/**
	 * @param emailBody the emailBody to set
	 */
	public void setEmailBody(EmailBody emailBody) {
		this.emailBody = emailBody;
	}

	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * @param fileName
	 *            the fileName to set
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * @return the realFileName
	 */
	public String getRealFileName() {
		return realFileName;
	}

	/**
	 * @param realFileName
	 *            the realFileName to set
	 */
	public void setRealFileName(String realFileName) {
		this.realFileName = realFileName;
	}

	/**
	 * @return the fileText
	 */
	public byte[] getFileText() {
		return fileText;
	}

	/**
	 * @param fileText
	 *            the fileText to set
	 */
	public void setFileText(byte[] fileText) {
		this.fileText = fileText;
	}

	/**
	 * @return the path
	 */
	public String getPath() {
		return path;
	}

	/**
	 * @param path
	 *            the path to set
	 */
	public void setPath(String path) {
		this.path = path;
	}

	/**
	 * @return the createDate
	 */
	public Date getCreateDate() {
		return createDate;
	}

	/**
	 * @param createDate
	 *            the createDate to set
	 */
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	
	public String getFileAllPath() {
//		if (getPath() != null) {
//			if (path.endsWith(File.separator)) {
//				return path + getFileName();
//			} else {
//				return path + File.separator + getFileName();
//			}
//		}
//		return "";
		try {
//			return AttachmentUtil.getAttachmentDir() + File.separator + getFileName();
			return AttachmentUtil.getAttachmentDir() + "/" + getFileName();
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * @return the size
	 */
	public long getSize() {
		return size;
	}

	/**
	 * @param size the size to set
	 */
	public void setSize(long size) {
		this.size = size;
	}
	
	public String getSizeString() {
		return Utility.sizeToHumanReadable(size);
	}

	/**
	 * @return the content
	 */
	public Object getContent() {
		return content;
	}

	/**
	 * @param content the content to set
	 */
	public void setContent(Object content) {
		this.content = content;
	}
	
	public static Attachment valueOf(EmailPart part) {
		if (part != null) {
			Attachment attachment = new Attachment();
			attachment.setContent(part.getContent());
			attachment.setRealFileName(part.getFileName());
			attachment.setFileName(part.getFileName());
			attachment.setSize(part.getSize());
			return attachment;
		}
		return null;
	}

	/**
	 * @return the emailid
	 */
	public String getEmailid() {
		return emailid;
	}

	/**
	 * @param emailid the emailid to set
	 */
	public void setEmailid(String emailid) {
		this.emailid = emailid;
	}

}
