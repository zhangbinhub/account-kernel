package pers.acp.message.email;

import pers.acp.message.exceptions.EmailException;
import pers.acp.tools.common.CommonTools;
import pers.acp.tools.file.common.FileCommon;
import org.apache.log4j.Logger;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.Message.RecipientType;
import javax.mail.internet.*;
import java.security.Security;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map.Entry;
import java.util.Properties;

public class EmailSender {

    private Logger log = Logger.getLogger(this.getClass());

    private String encode = CommonTools.getDefaultCharset();

    private EmailEntity emailEntity = null;

    /**
     * 构造函数
     *
     * @param emailEntity email实体对象
     */
    public EmailSender(EmailEntity emailEntity) {
        this.emailEntity = emailEntity;
    }

    /**
     * 邮件发送
     *
     * @return 成功或失败
     */
    public boolean doSend() {
        return doSend(null);
    }

    /**
     * 邮件发送
     *
     * @param charset 内容字符集，默认系统字符集
     * @return 成功或失败
     */
    public boolean doSend(String charset) {
        try {
            if (emailEntity == null) {
                throw new EmailException("mail entity is null");
            }
            Properties props = new Properties();
            if (emailEntity.isSSL()) {
                Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
                props.setProperty("mail.smtp.socketFactory.port", emailEntity.getMailPort() + "");
                props.setProperty("mail.smtp.starttls.enable", "true");
                props.setProperty("mail.smtp.ssl.enable", "true");
            }
            props.setProperty("mail.smtp.host", emailEntity.getMailHost());
            props.setProperty("mail.smtp.port", emailEntity.getMailPort() + "");
            props.setProperty("mail.transport.protocol", emailEntity.getMailTransportProtocol());
            props.setProperty("mail.smtp.auth", emailEntity.isMailSmtpAuth() + "");
            Session session = Session.getDefaultInstance(props, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(emailEntity.getUserName(), emailEntity.getPassword());
                }
            });
            session.setDebug(emailEntity.isDeBug());
            /* 创建邮件 */
            Message message = createMail(session, charset);
            /* 发送邮件 */
            Transport.send(message, message.getAllRecipients());
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    /**
     * 创建邮件
     *
     * @param session 回话
     * @param charset 字符集
     * @return 邮件实例
     */
    private MimeMessage createMail(Session session, String charset)
            throws Exception {
        if (!CommonTools.isNullStr(charset)) {
            encode = charset;
        }
        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(emailEntity.getSenderAddress()));
        if (emailEntity.getRecipientAddresses() == null || emailEntity.getRecipientAddresses().isEmpty()) {
            throw new EmailException("recipientAddresses is null or empty");
        }
        ArrayList<InternetAddress> addresses = new ArrayList<>();
        for (String recipientAddress : emailEntity.getRecipientAddresses()) {
            if (!CommonTools.isNullStr(recipientAddress)) {
                addresses.add(new InternetAddress(recipientAddress));
            }
        }
        message.addRecipients(RecipientType.TO, addresses.toArray(new InternetAddress[addresses.size()]));
        if (emailEntity.getRecipientCCAddresses() != null && !emailEntity.getRecipientCCAddresses().isEmpty()) {
            ArrayList<InternetAddress> addressesCC = new ArrayList<>();
            for (String recipientAddress : emailEntity.getRecipientCCAddresses()) {
                if (!CommonTools.isNullStr(recipientAddress)) {
                    addressesCC.add(new InternetAddress(recipientAddress));
                }
            }
            message.addRecipients(RecipientType.CC, addresses.toArray(new InternetAddress[addresses.size()]));
        }
        if (emailEntity.getRecipientBCCAddresses() != null && !emailEntity.getRecipientBCCAddresses().isEmpty()) {
            ArrayList<InternetAddress> addressesBCC = new ArrayList<>();
            for (String recipientAddress : emailEntity.getRecipientBCCAddresses()) {
                if (!CommonTools.isNullStr(recipientAddress)) {
                    addressesBCC.add(new InternetAddress(recipientAddress));
                }
            }
            message.addRecipients(RecipientType.BCC, addresses.toArray(new InternetAddress[addresses.size()]));
        }
        message.setSubject(emailEntity.getMailSubject());
        message.setSentDate(new Date());

        /* 正文主体 */
        MimeBodyPart content = new MimeBodyPart();

        /* 正文文本 */
        MimeBodyPart text = new MimeBodyPart();
        text.setContent(new String(emailEntity.getContent().getBytes(), encode), "text/html;charset=" + encode);
        MimeMultipart mmImage = new MimeMultipart();
        mmImage.addBodyPart(text);

        /* 图片 */
        if (emailEntity.getImages() != null && !emailEntity.getImages().isEmpty()) {
            /* 图片内容 */
            for (Entry<String, String> entry : emailEntity.getImages().entrySet()) {
                String cid = entry.getKey();
                String imagePath = FileCommon.getAbsPath(entry.getValue());
                MimeBodyPart image = new MimeBodyPart();
                DataHandler dh = new DataHandler(new FileDataSource(imagePath));
                image.setDataHandler(dh);
                image.setContentID(cid);
                mmImage.addBodyPart(image);
            }
            mmImage.setSubType("related");
        }
        content.setContent(mmImage);

        /* 附件 */
        if (emailEntity.getAttaches() != null && emailEntity.getAttaches().size() > 0) {
            MimeMultipart mmAttache = new MimeMultipart();
            /* 附件内容 */
            for (int i = 0; i < emailEntity.getAttaches().size(); i++) {
                String attachePath = FileCommon.getAbsPath(emailEntity.getAttaches().get(i));
                MimeBodyPart attach = new MimeBodyPart();
                DataHandler dh = new DataHandler(new FileDataSource(attachePath));
                attach.setDataHandler(dh);
                attach.setFileName(MimeUtility.encodeText(dh.getName(), encode, null));
                mmAttache.addBodyPart(attach);
            }
            mmAttache.setSubType("mixed");
            mmAttache.addBodyPart(content);
            message.setContent(mmAttache);
            message.saveChanges();
        } else {
            message.setContent(mmImage);
            message.saveChanges();
        }
        return message;
    }

    /**
     * 获取email实体对象
     *
     * @return email实体对象
     */
    public EmailEntity getEmailEntity() {
        return emailEntity;
    }

}
