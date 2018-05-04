package OLink.bpm.util.mail;

import java.util.Date;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.log4j.Logger;

import OLink.bpm.util.StringUtil;

/**
 * 带附件的邮件发送者
 * 
 * @author Tom
 * 
 */
public class MultiEmailSender {

	private static final Logger LOG = Logger.getLogger(MultiEmailSender.class);
	private Email email;
	private boolean debug;

	public MultiEmailSender(Email email) {
		this(email, false);
	}

	public MultiEmailSender(Email email, boolean debug) {
		this.email = email;
		this.debug = debug;
	}

	/**
	 * 发送带附件的邮件
	 * 
	 * @return true 发送成功 | false 发送失败
	 */
	public boolean sendMultiEmail() {
		Transport trans = null;
		try {
			if (getEmail() == null) {
				return false;
			}
			Session session = null;
			Properties props = email.getProperties();
			session = Session.getDefaultInstance(props, email.getAuthenticator());
			session.setDebug(isDebug());

			Message msg = new MimeMessage(session);
			Address from_address = new InternetAddress(email.getFrom());
			msg.setFrom(from_address);

			InternetAddress[] address = { new InternetAddress(email.getTo()) };
			msg.setRecipients(Message.RecipientType.TO, address);
			if (!StringUtil.isBlank(email.getBcc())) {
				msg.addRecipient(Message.RecipientType.BCC,
						new InternetAddress(email.getBcc()));
			}
			if (!StringUtil.isBlank(email.getCc())) {
				msg.addRecipient(Message.RecipientType.CC, new InternetAddress(email.getCc()));
			}
			msg.setSubject(email.getSubject());
			Multipart mp = new MimeMultipart();
			MimeBodyPart mbp = new MimeBodyPart();
			mbp.setContent(email.getBody(), "text/html;charset=UTF-8");
			mp.addBodyPart(mbp);
			if (email.isHaveAttachment()) {// 有附件
				for (int i = 0; i < email.getAttachFileNames().length; i++) {
					mbp = new MimeBodyPart();
					String filename = email.getAttachFileNames()[i]; // 选择出每一个附件名
					FileDataSource fds = new FileDataSource(filename); // 得到数据源
					mbp.setDataHandler(new DataHandler(fds)); // 得到附件本身并至入BodyPart
					mbp.setFileName(fds.getName()); // 得到文件名同样至入BodyPart
					mp.addBodyPart(mbp);
				}
			}
			msg.setContent(mp); // Multipart加入到邮件
			msg.setSentDate(new Date()); // 设置邮件头的发送日期
			// 发送邮件
			msg.saveChanges();
			trans = session.getTransport("smtp");
			trans.connect();
			trans.sendMessage(msg, msg.getAllRecipients());
			return true;
		} catch (AddressException e) {
			LOG.warn("######## Sent multi e-mail error: " + e.getMessage() + " ########");
			e.printStackTrace();
		} catch (MessagingException e) {
			LOG.warn("######## Sent multi e-mail error: " + e.getMessage() + " ########");
			e.printStackTrace();
		}  finally {
			try {
				if (trans != null) {
					trans.close();
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
	 * @param email
	 *            the email to set
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
	 * @param debug
	 *            the debug to set
	 */
	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public static void main(String[] args) {
		Email email = new Email("tom@teemlink.com", "411238450@qq.com", "Test",
				"This is a test e-mail!", "teemlink.com", "tom", "*******",
				"tom@teemlink.com", null, null, true);
		String[] atts = new String[] { "G:\\zhiliao\\android.txt",
				"G:\\zhiliao\\fileico.png" };
		email.setAttachFileNames(atts);
		MultiEmailSender sender = new MultiEmailSender(email, true);
		sender.sendMultiEmail();
	}

}
