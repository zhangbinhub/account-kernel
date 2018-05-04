package OLink.bpm.core.email.runtime.mail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;
import javax.net.ssl.SSLException;

import OLink.bpm.core.email.util.EmailConfig;
import OLink.bpm.util.mail.OBPMAuthenticator;
import OLink.bpm.core.email.runtime.model.EmailPriority;
import OLink.bpm.core.email.runtime.model.EmailSensitivity;
import OLink.bpm.core.email.runtime.model.ByteArrayDataSource;
import OLink.bpm.core.email.runtime.model.Email;
import OLink.bpm.core.email.runtime.model.EmailPart;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sun.mail.smtp.SMTPMessage;

/**
 * @author Tom
 *
 */
public class Smtp {
	
	private static Log log = LogFactory.getLog(Smtp.class);
	private Session session = null;
	private ConnectionProfile profile;
	private AuthProfile auth;

	public Smtp(ConnectionProfile profile, AuthProfile auth) {
		Properties props = new Properties();
		this.profile = profile;
		this.auth = auth;

		//if (log.isDebugEnabled()) {
			props.setProperty("mail.debug", "true");
		//	System.setProperty("javax.net.debug", "all");
		//}

		if (profile.getSmtpSSL() != null && profile.getSmtpSSL().toLowerCase().equals("true")) {
			props.put("mail.smtps.host", profile.getSmtpServer());
			props.put("mail.smtps.port", Integer.toString(profile.getISmtpPort()));
		} else {
			props.put("mail.smtp.host", profile.getSmtpServer());
			props.put("mail.smtp.port", Integer.toString(profile.getISmtpPort()));
		}

		
		if (profile.getSmtpAuthenticated() != null && profile.getSmtpAuthenticated().equals("true")) {
			if (profile.getSmtpSSL() != null && profile.getSmtpSSL().toLowerCase().equals("true")) {
				props.setProperty("mail.smtps.auth", "true");
			} else {
				props.setProperty("mail.smtp.auth", "true");
			}
			OBPMAuthenticator authenticator = new OBPMAuthenticator(auth.getUserName(), auth.getPassword());
			session = Session.getInstance(props, authenticator);
		} else {
			session = Session.getInstance(props, null);
		}

		if (log.isDebugEnabled()) {
			session.setDebug(true);
		}
	}

	/**
	 * 
	 * @param msg
	 * @param simulate 模拟
	 * @return
	 * @throws Exception
	 * @SuppressWarnings 邮件部分API不支持泛型
	 */
	@SuppressWarnings("unchecked")
	public HashMap sendEmail(Email msg, boolean simulate) throws Exception {
		Address from = msg.getBaseHeader().getFrom()[0];
		if (from == null) {
			String address = auth.getUserName() + ("@") + EmailConfig.getEmailDomain();
			from = new InternetAddress(address);
		}
		Address[] to = msg.getBaseHeader().getTo();
		Address[] cc = msg.getBaseHeader().getCc();
		Address[] bcc = msg.getBaseHeader().getBcc();
		Address[] replyTo = msg.getBaseHeader().getReplyTo();
		Boolean requestReceiptNotification = msg.getBaseHeader().getRequestReceiptNotification();
		short priority = msg.getBaseHeader().getPriority();
		short sensitivity = msg.getBaseHeader().getSensitivity();

		SMTPMessage mimeMsg = new SMTPMessage(session);
		String subject = msg.getBaseHeader().getSubject();
		
		mimeMsg.setFrom(from);
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

		if(requestReceiptNotification != null){
			mimeMsg.addHeader("Disposition-Notification-To", from.toString());
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
		List<EmailPart> parts = msg.getParts();
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
			for (int i=1; i < msg.getParts().size(); i++) {
				myPart = msg.getParts().get(i);
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

			// setting the content and finished
			mimeMsg.setContent(multipart);
		}
		mimeMsg.saveChanges();

		// we are sending the message and generating a sent report on the fly.
		HashMap out = new HashMap();
		//out.put("msg", mimeMsg);
		if (!simulate) {// sinulate 模拟
			try {
				mimeMsg.setSendPartial(true);
				mimeMsg.setSentDate(new Date());
				this.sendEmail(mimeMsg);
				//Address[] sent = mimeMsg.getAllRecipients();
				//out.put("sent", sent);
			} catch (SendFailedException sex) {
				//Address[] sent = sex.getValidSentAddresses();
				//Address[] invalid = sex.getInvalidAddresses();
				//Address[] fail = sex.getValidUnsentAddresses();
				throw new Exception(sex.getMessage());
			} catch (Exception ex) {
				// a bugfix for google mail. 
				if (ex.getCause() instanceof SSLException) {
					log.error("an SSL exception occured. try to go on." + ex);
					//Address[] sent = mimeMsg.getAllRecipients();
					throw ex;
					//out.put("sent", sent);
				} else {
					throw ex;
				}
			}
		}
		return out;
	}
	
	public void sendEmail(Message message) throws Exception {
		if (profile.getSmtpSSL() != null && profile.getSmtpSSL().toLowerCase().equals("true")) {
			Transport transport = null;
			try {
				transport = session.getTransport("smtps");
				transport.connect(profile.getSmtpServer(), auth.getUserName(), auth.getPassword());
				transport.sendMessage(message, message.getAllRecipients());
			} catch (Exception ex) {
				if (ex.getCause() != null) {
					if (ex.getCause() instanceof SSLException) {
						log.error("An SSL exception occured. try to go on." + ex);
					} else {
						throw ex;
					}
				} else {
					throw ex;
				}
			} finally {
				if (transport != null && transport.isConnected()) {
					transport.close();
				}
			}
		} else {
			Transport.send(message);
		}
	}
	
	public static void main(String[] args) {
		try {
			
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
}
