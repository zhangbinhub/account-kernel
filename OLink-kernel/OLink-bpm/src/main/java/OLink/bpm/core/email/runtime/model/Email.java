package OLink.bpm.core.email.runtime.model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import OLink.bpm.core.email.util.EmailConfig;
import OLink.bpm.core.email.util.Utility;

/**
 * @author Tom
 *
 */
public class Email implements Serializable {
	
	private static final long serialVersionUID = 318395800499501554L;
	
	private Long uid;
	private List<EmailPart> parts = new ArrayList<EmailPart>();
	private EmailHeader baseHeader;
	private List<String> headers = new ArrayList<String>();
	private String bodyText;

	public List<EmailPart> getParts() {
		return parts;
	}

	public void setParts(List<EmailPart> parts) {
		this.parts = parts;
	}

	public boolean addHeader(String name, Object value) {
		headers.add(name + (char)6 + value);
		return true;
	}
	
	/**
	 * @return
	 */
	public List<String> getHeaders() {
		return headers;
	}

	/**
	 * @param list
	 */
	public void setHeaders(List<String> list) {
		headers = list;
	}

//	class HeaderPair {
//		String name;
//		Object value; 
//	}

	/**
	 * @return
	 */
	public EmailHeader getBaseHeader() {
		if (baseHeader == null) {
			baseHeader = new EmailHeader();
		}
		return baseHeader;
	}

	/**
	 * @param header
	 */
	public void setBaseHeader(EmailHeader header) {
		baseHeader = header;
	}
	/**
	 * @return
	 */
	public boolean isCcExists() {
		return (getBaseHeader().getCc() != null);
	}

	/**
	 * @return
	 */
	public boolean isDateExists() {
		return (getBaseHeader().getDate() != null);
	}

	public String getTo() {
		return Utility.addressArrToString(getBaseHeader().getTo());
	}

	public String getFrom() {
		String from = Utility.addressArrToString(getBaseHeader().getFrom());
		if (from.equals("")) {
			from = "-";
		}
		return from;
	}

	public String getCc() {
		return Utility.addressArrToString(getBaseHeader().getCc());
	}

	public Date getDate() {
		return getBaseHeader().getDate();
	}

	public String getSubject() {
		String subject = Utility.doCharsetCorrections(getBaseHeader().getSubject());
		if (subject == null || subject.length() == 0) {
			subject = "No Subject";
		}
		return subject;
	}

	/**
	 * @return
	 */
	public String getBodyText() {
		return bodyText;
	}

	/**
	 * @param string
	 */
	public void setBodyText(String string) {
		bodyText = string;
	}

	/**
	 * @return the uid
	 */
	public Long getUid() {
		return uid;
	}

	/**
	 * @param uid the uid to set
	 */
	public void setUid(Long uid) {
		this.uid = uid;
	}
	
	public void addPart(EmailPart part) {
		if (getParts() == null) {
			setParts(new ArrayList<EmailPart>());
		}
		if (part != null) {
			getParts().add(part);
		}
	}
	
	public Message toMessage(Session session) throws MessagingException, IOException {
		Address[] to = this.getBaseHeader().getTo();
		Address[] cc = this.getBaseHeader().getCc();
		Address[] bcc = this.getBaseHeader().getBcc();
		Address[] replyTo = this.getBaseHeader().getReplyTo();
		Boolean requestReceiptNotification = this.getBaseHeader().getRequestReceiptNotification();
		short priority = this.getBaseHeader().getPriority();
		short sensitivity = this.getBaseHeader().getSensitivity();

		MimeMessage mimeMsg = new MimeMessage(session);
		String subject = this.getBaseHeader().getSubject();
		
		Address[] froms = getBaseHeader().getFrom();
		if (froms != null && froms.length > 0) {
			mimeMsg.setFrom(froms[0]);
			if(requestReceiptNotification != null){
				mimeMsg.addHeader("Disposition-Notification-To", froms[0].toString());
			}
		}
		if (to != null) {
			mimeMsg.setRecipients(Message.RecipientType.TO, to);
		}
		if (cc != null) {
			mimeMsg.setRecipients(Message.RecipientType.CC, cc);
		}
		if (bcc != null) {
			mimeMsg.setRecipients(Message.RecipientType.BCC, bcc);
		}
		if (replyTo != null) {
			mimeMsg.setReplyTo(replyTo);
		}

		mimeMsg.setSentDate(new Date());
		if (subject == null || subject.length() == 0) {
			subject = "No subject";
		}

		if(priority > 0){
			mimeMsg.addHeader("X-Priority", String.valueOf(priority));
			mimeMsg.addHeader("X-MSMail-Priority", EmailPriority.toStringValue(priority));
		}

		if(sensitivity > 0){
			mimeMsg.addHeader("Sensitivity", EmailSensitivity.toStringValue(sensitivity));
		}

		String charset = EmailConfig.getString("charset", "UTF-8");
		
		mimeMsg.setSubject(MimeUtility.encodeText(subject, charset, null));
		List<EmailPart> parts = this.getParts();
		EmailPart bodyPart = parts.get(0);

		boolean isTextBody = (bodyPart.isHTMLText()) ? false : true;

		if (parts.size() == 1 && isTextBody) {
			mimeMsg.setText((String)bodyPart.getContent(), charset);
		} else {
			BodyPart bp = new MimeBodyPart();
			bp.setContent(bodyPart.getContent(), bodyPart.getContentType());
			bp.setHeader("Content-Type", bodyPart.getContentType());

			// attachments are added here. 
			MimeMultipart multipart = new MimeMultipart();
			multipart.addBodyPart(bp);

			// other attachments will follow
			MimeBodyPart attPart = null;
			EmailPart myPart = null;
			DataSource dataSource = null;
			String tmpContType = null;
			int pos = -1;
			for (int i=1; i < this.getParts().size(); i++) {
				myPart = this.getParts().get(i);
				attPart = new MimeBodyPart();
				
				dataSource = myPart.getDataSource();
				if (dataSource == null) {
					if (myPart.getContent() instanceof ByteArrayOutputStream) {
						ByteArrayOutputStream bos = (ByteArrayOutputStream)myPart.getContent();
						dataSource = new ByteArrayDataSource(bos.toByteArray(), myPart.getContentType(), myPart.getFileName());
						attPart.setDataHandler(new DataHandler(dataSource));
						bos.close();
					} else if (myPart.getContent() instanceof ByteArrayInputStream) {
						ByteArrayInputStream bis = (ByteArrayInputStream)myPart.getContent();
						ByteArrayOutputStream bos = new ByteArrayOutputStream();
						int j = -1;
						while ((j = bis.read()) != -1) {
							bos.write(j);
						}
						dataSource = new ByteArrayDataSource(bos.toByteArray(), myPart.getContentType(), myPart.getFileName());
						attPart.setDataHandler(new DataHandler(dataSource));
						bos.close();
						bis.close();
					} else {
						attPart.setContent(myPart.getContent(), myPart.getContentType());
					}
				} else {
					attPart.setDataHandler(new DataHandler(dataSource));
				}
				
				attPart.setDisposition(myPart.getDisposition());
				attPart.setFileName(MimeUtility.encodeText(myPart.getFileName(), charset, null));
				
				tmpContType = (myPart.getContentType() == null) ? "application/octet-stream" : myPart.getContentType();
				
				pos = tmpContType.indexOf(";");
				if (pos >= 0) {
					tmpContType = tmpContType.substring(0, pos);
				}
				attPart.setHeader("Content-Type", tmpContType);
				multipart.addBodyPart(attPart);
			}

			mimeMsg.setContent(multipart);
		}
		return mimeMsg;
	}

}
