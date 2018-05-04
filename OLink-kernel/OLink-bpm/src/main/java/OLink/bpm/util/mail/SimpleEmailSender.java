package OLink.bpm.util.mail;

import java.util.Date;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import OLink.bpm.util.StringUtil;
import org.apache.log4j.Logger;

import com.sun.mail.smtp.SMTPTransport;

/**
 * 简单邮件发送者
 * @author Tom
 *
 */
public class SimpleEmailSender {

	private static final Logger LOG = Logger.getLogger(SimpleEmailSender.class);
	private Email email;
	private boolean debug;

	public SimpleEmailSender(Email email) {
		this(email, false);
	}

	public SimpleEmailSender(Email email, boolean debug) {
		this.email = email;
		this.debug = debug;
	}
	
	/**
	 * 发送文本类型邮件
	 * @return true 发送成功 | false 发送失败
	 */
	public boolean sendTextEmail() {
		SMTPTransport transport = null;
		try {
			if (getEmail() == null) {
				return false;
			}
			Session sendMailSession = Session.getInstance(email.getProperties(), email.getAuthenticator());
			sendMailSession.setDebug(isDebug());
			transport = (SMTPTransport) sendMailSession.getTransport("smtp");
			transport.connect();
			Message newMessage = new MimeMessage(sendMailSession);
			newMessage.setFrom(new InternetAddress(email.getFrom()));
			newMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(email.getTo()));
			if (!StringUtil.isBlank(email.getBcc())) {
				newMessage.addRecipient(Message.RecipientType.BCC, new InternetAddress(email.getBcc()));
			}
			if (!StringUtil.isBlank(email.getCc())) {
				newMessage.addRecipient(Message.RecipientType.CC, new InternetAddress(email.getCc()));
			}
			newMessage.setSubject(email.getSubject());
			newMessage.setSentDate(new Date());
			newMessage.setContent(email.getBody(), "text/plain; charset=utf-8");
			transport.sendMessage(newMessage, newMessage.getAllRecipients());
			return true;
		} catch (Exception e) {
			LOG.warn("######## Sent text e-mail error: " + e.getMessage() + " ########");
			e.printStackTrace();
		} finally {
			try {
				if (transport != null) {
					transport.close();
				}
			} catch (MessagingException e) {
				LOG.warn(e.toString());
			}
		}
		return false;
	}
	
	/**
	 * 发送Html类型的邮件
	 * @return true 发送成功 | false 发送失败
	 */
	public boolean sendHtmlEmail() {
		SMTPTransport transport = null;
		try {
			if (getEmail() == null) {
				return false;
			}
			Session sendMailSession = Session.getInstance(email.getProperties(), email.getAuthenticator());
			sendMailSession.setDebug(isDebug());
			transport = (SMTPTransport) sendMailSession.getTransport("smtp");
			transport.connect();
			Message newMessage = new MimeMessage(sendMailSession);
			newMessage.setFrom(new InternetAddress(email.getFrom()));
			newMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(email.getTo()));
			if (!StringUtil.isBlank(email.getBcc())) {
				newMessage.addRecipient(Message.RecipientType.BCC, new InternetAddress(email.getBcc()));
			}
			if (!StringUtil.isBlank(email.getCc())) {
				newMessage.addRecipient(Message.RecipientType.CC, new InternetAddress(email.getCc()));
			}
			newMessage.setSubject(email.getSubject());
			newMessage.setSentDate(new Date());
			// MiniMultipart类是一个容器类，包含MimeBodyPart类型的对象　　　 
			Multipart mainPart = new MimeMultipart();
			// 创建一个包含HTML内容的MimeBodyPart
			BodyPart html = new MimeBodyPart();
			// 设置HTML内容　　　 
			html.setContent(email.getBody(), "text/html; charset=utf-8");
			mainPart.addBodyPart(html);
			// 将MiniMultipart对象设置为邮件内容
			newMessage.setContent(mainPart);
			transport.sendMessage(newMessage, newMessage.getAllRecipients());
			return true;
		} catch (Exception e) {
			LOG.warn("######## Sent html e-mail error: " + e.getMessage() + " ########");
			e.printStackTrace();
		} finally {
			try {
				if (transport != null) {
					transport.close();
				}
			} catch (MessagingException e) {
				LOG.warn(e.getMessage());
			}
		}
		return false;
	}

	/**
	 * @return the email
	 */
	public Email getEmail() {
		return email;
	}

	/**
	 * @param email the email to set
	 */
	public void setEmail(Email email) {
		this.email = email;
	}

	/**
	 * @return the debug
	 */
	public boolean isDebug() {
		return debug;
	}

	/**
	 * @param debug the debug to set
	 */
	public void setDebug(boolean debug) {
		this.debug = debug;
	}
	
	public static void main(String[] args) {
		Email email = new Email("411238450@qq.com", "tom@teemlink.com", 
				"Test", "This is a test e-mail!", "smtp.qq.com", 
				"411238450", "********", "tom@teemlink.com", true);
		SimpleEmailSender sender = new SimpleEmailSender(email, true);
		sender.sendTextEmail();
		sender.sendHtmlEmail();
	}

}
