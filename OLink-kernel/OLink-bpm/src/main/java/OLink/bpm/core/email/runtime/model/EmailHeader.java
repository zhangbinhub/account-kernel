package OLink.bpm.core.email.runtime.model;

import java.io.Serializable;
import java.util.Date;

import javax.mail.Address;

/**
 * @author Tom
 */
public class EmailHeader implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private long emailUID;
	private Address[] bcc;
	private Address[] from;
	private Address[] to;
	private Address[] cc;
	private Address[] replyTo;
	private String subject;
	private Date date;
	private long size;
	private int nums;
	private boolean multipart;
	private Boolean unread;
	private Boolean requestReceiptNotification;
	private String receiptNotificationEmail;
	private short priority;
	private short sensitivity;

	// human readable portions. These are shown on jsps.
	private String sizeString;
	private String fromString;
	private String toString;
	private String ccString;
	private String dateString;
	
	private long folderid;

	/**
	 * Default constructor
	 */
	public EmailHeader() {
		super();
	}

	/**
	 * @return Returns the bcc.
	 */
	public Address[] getBcc() {
		return bcc;
	}

	/**
	 * @param bcc
	 *            The bcc to set.
	 */
	public void setBcc(Address[] bcc) {
		this.bcc = bcc;
	}

	/**
	 * @return Returns the cc.
	 */
	public Address[] getCc() {
		return cc;
	}

	/**
	 * @param cc
	 *            The cc to set.
	 */
	public void setCc(Address[] cc) {
		this.cc = cc;
	}

	/**
	 * @return Returns the date.
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * @param date
	 *            The date to set.
	 */
	public void setDate(Date date) {
		this.date = date;
	}

	/**
	 * @return Returns the from.
	 */
	public Address[] getFrom() {
		return from;
	}

	/**
	 * @param from
	 *            The from to set.
	 */
	public void setFrom(Address[] from) {
		this.from = from;
	}

	/**
	 * @return the nums
	 */
	public int getNums() {
		return nums;
	}

	/**
	 * @param nums
	 *            the nums to set
	 */
	public void setNums(int nums) {
		this.nums = nums;
	}

	/**
	 * @return Returns the multipart.
	 */
	public boolean isMultipart() {
		return multipart;
	}

	/**
	 * @param multipart
	 *            The multipart to set.
	 */
	public void setMultipart(boolean multipart) {
		this.multipart = multipart;
	}

	/**
	 * @return Returns the replyTo.
	 */
	public Address[] getReplyTo() {
		return replyTo;
	}

	/**
	 * @param replyTo
	 *            The replyTo to set.
	 */
	public void setReplyTo(Address[] replyTo) {
		this.replyTo = replyTo;
	}

	/**
	 * @return Returns the size.
	 */
	public long getSize() {
		return size;
	}

	/**
	 * @param size
	 *            The size to set.
	 */
	public void setSize(long size) {
		this.size = size;
	}

	/**
	 * @return Returns the subject.
	 */
	public String getSubject() {
		return subject;
	}

	/**
	 * @param subject
	 *            The subject to set.
	 */
	public void setSubject(String subject) {
		this.subject = subject;
	}

	/**
	 * @return Returns the to.
	 */
	public Address[] getTo() {
		return to;
	}

	/**
	 * @param to
	 *            The to to set.
	 */
	public void setTo(Address[] to) {
		this.to = to;
	}

	
	/**
	 * @return the sizeString
	 */
	public String getSizeString() {
		return sizeString;
	}

	/**
	 * @param sizeString the sizeString to set
	 */
	public void setSizeString(String sizeString) {
		this.sizeString = sizeString;
	}

	/**
	 * @return the fromString
	 */
	public String getFromString() {
		return fromString;
	}

	/**
	 * @param fromString the fromString to set
	 */
	public void setFromString(String fromString) {
		this.fromString = fromString;
	}

	/**
	 * @return the toString
	 */
	public String getToString() {
		return toString;
	}

	/**
	 * @param toString the toString to set
	 */
	public void setToString(String toString) {
		this.toString = toString;
	}

	/**
	 * @return the ccString
	 */
	public String getCcString() {
		return ccString;
	}

	/**
	 * @param ccString the ccString to set
	 */
	public void setCcString(String ccString) {
		this.ccString = ccString;
	}

	/**
	 * @return the dateString
	 */
	public String getDateString() {
		return dateString;
	}

	/**
	 * @param dateString the dateString to set
	 */
	public void setDateString(String dateString) {
		this.dateString = dateString;
	}

	/**
	 * @return
	 */
	public Boolean getUnread() {
		return unread;
	}

	/**
	 * @param boolean1
	 */
	public void setUnread(Boolean boolean1) {
		unread = boolean1;
	}

	/**
	 * @return
	 */
	public Boolean getRequestReceiptNotification() {
		return requestReceiptNotification;
	}

	/**
	 * @param boolean1
	 */
	public void setRequestReceiptNotification(Boolean requestReceiptNotification) {
		this.requestReceiptNotification = requestReceiptNotification;
	}

	/**
	 * @return
	 */
	public String getReceiptNotificationEmail() {
		return receiptNotificationEmail;
	}

	/**
	 * @param boolean1
	 */
	public void setReceiptNotificationEmail(String receiptNotificationEmail) {
		this.receiptNotificationEmail = receiptNotificationEmail;
	}

	/**
	 * @return
	 */
	public short getPriority() {
		return priority;
	}

	/**
	 * @param boolean1
	 */
	public void setPriority(short priority) {
		this.priority = priority;
	}

	/**
	 * @return
	 */
	public short getSensitivity() {
		return sensitivity;
	}

	/**
	 * @param boolean1
	 */
	public void setSensitivity(short sensitivity) {
		this.sensitivity = sensitivity;
	}

	/**
	 * @return the emailUID
	 */
	public long getEmailUID() {
		return emailUID;
	}

	/**
	 * @param emailUID
	 *            the emailUID to set
	 */
	public void setEmailUID(long emailUID) {
		this.emailUID = emailUID;
	}

	/**
	 * @return the folderid
	 */
	public long getFolderid() {
		return folderid;
	}

	/**
	 * @param folderid the folderid to set
	 */
	public void setFolderid(long folderid) {
		this.folderid = folderid;
	}

}
