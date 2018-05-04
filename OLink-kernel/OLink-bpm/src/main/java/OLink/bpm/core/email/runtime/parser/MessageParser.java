package OLink.bpm.core.email.runtime.parser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.mail.Header;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;

import OLink.bpm.core.email.runtime.model.Email;
import OLink.bpm.core.email.runtime.model.EmailHeader;
import OLink.bpm.core.email.runtime.model.EmailPart;
import OLink.bpm.core.email.runtime.model.EmailSensitivity;
import OLink.bpm.core.email.util.Utility;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Tom
 * 
 */
public class MessageParser {

	private static final Log log = LogFactory.getLog(MessageParser.class);

	/**
	 * 
	 */
	public MessageParser() {
		super();
	}

	public static final Email parseMessage(Message message) {
		return parseMessage(message, false);
	}

	/**
	 * 
	 * @param message
	 * @param loadPartBody
	 * @return
	 * @SuppressWarnings API不支持泛型
	 */
	public static final Email parseMessage(Message message, boolean loadPartBody) {
		Email email = new Email();
		// get base headers
		try {
			EmailHeader header = new EmailHeader();
			header.setFrom(message.getFrom());
			header.setTo(message.getRecipients(Message.RecipientType.TO));
			header.setCc(message.getRecipients(Message.RecipientType.CC));
			header.setBcc(message.getRecipients(Message.RecipientType.BCC));
			header.setReplyTo(message.getReplyTo());
			header.setDate(message.getSentDate());
			header.setSize(message.getSize());
			header.setSubject(Utility.decodeText(message.getSubject()));
			header.setUnread(!message.isSet(javax.mail.Flags.Flag.SEEN));

			// now set the human readables.
			header.setDateString(Utility.getDateToString(header.getDate()));
			header.setFromString(Utility.addressArrToString(header.getFrom()));
			header.setToString(Utility.addressArrToString(header.getTo()));
			header.setCcString(Utility.addressArrToString(header.getCc()));
			header.setSizeString(Utility.sizeToHumanReadable(header.getSize()));

			setHeaders(message, header);

			email.setBaseHeader(header);
		} catch (Exception e) {
			log.error(e.toString());
		}

		List<EmailPart> parts = new ArrayList<EmailPart>();
		parts = fetchParts(message, parts, loadPartBody);
		if (parts != null) {
			EmailPart part = null;
			for (int i = 0; i < parts.size(); i++) {
				part = parts.get(i);
				part.setId(i);
			}
		}
		email.setParts(parts);

		// store all headers
		/*
		 * try { Enumeration en = message.getAllHeaders(); String name, val =
		 * ""; Object tmp = null; while (en.hasMoreElements()) { tmp =
		 * en.nextElement(); name = (tmp == null) ? "" : tmp.toString(); tmp =
		 * message.getHeader(name); val = (tmp == null) ? "" : tmp.toString();
		 * email.addHeader(name, val); } } catch (MessagingException e1) {
		 * log.error ("Exception occured while parsing the message generic all
		 * headers", e1); }
		 */
		try {
			Enumeration<?> en = message.getAllHeaders();
			String name, val = "";
			Header tmp = null;
			while (en.hasMoreElements()) {
				tmp = (Header) en.nextElement();
				name = tmp.getName();
				val = tmp.getValue();
				/*
				 * name = (tmp == null) ? "" : tmp.toString(); tmp =
				 * message.getHeader(name); val = (tmp == null) ? "" :
				 * tmp.toString();
				 */
				email.addHeader(name, val);
			}
		} catch (MessagingException e1) {
			log.error(e1.toString());
		}
		return email;
	}

	/**
	 * 收取邮件各个部分体
	 * 
	 * @param part
	 * @param parts
	 * @param loadPartBody
	 *            是否加载邮件内容
	 * @return
	 */
	private static List<EmailPart> fetchParts(Part part, List<EmailPart> parts, boolean loadPartBody) {
		if (part == null) {
			return null;
		}
		try {
			if (!part.isMimeType("text/rfc822-headers") && part.isMimeType("text/*")) {
				try {
					EmailPart myPart = new EmailPart();
					myPart.setSize(part.getSize());
					myPart.setContentType(part.getContentType());
					
					myPart.setFileName(Utility.decodeText(part.getFileName()));
					myPart.setDisposition(Utility.decodeText(part.getDisposition()));
					Object pContent;
					try {
						pContent = part.getContent();
					} catch (UnsupportedEncodingException e) {
						pContent = "Message has an illegal encoding. " + e.getLocalizedMessage();
					}
					if (pContent != null) {
						myPart.setContent(Utility.decodeText(pContent.toString()));
					} else {
						myPart.setContent("Illegal content");
					}
					parts.add(myPart);
				} catch (Exception e) {
					log.error("Part is mimeType text/rfc822-headers and is mimeType text/* but exception occured", e);
				}
			} else if (part.isMimeType("multipart/*")) {
				try {
					Multipart mp = (Multipart) part.getContent();
					int count = mp.getCount();
					for (int i = 0; i < count; i++) {
						fetchParts(mp.getBodyPart(i), parts, loadPartBody);
					}
				} catch (Exception e) {
					log.error("Part is mimeType multipart/* but exception occured", e);
				}
			} else if (part.isMimeType("message/rfc822")) {
				fetchParts((Part) part.getContent(), parts, loadPartBody);
			} else {
				try {
					EmailPart myPart = new EmailPart();
					myPart.setSize(part.getSize());
					myPart.setContentType(part.getContentType());
					myPart.setFileName((part.getFileName() == null) ? "rfc822.txt" : Utility.decodeText(part.getFileName()));
					myPart.setDisposition(Utility.decodeText(part.getDisposition()));
					String headContentID[] = part.getHeader("Content-ID");
					if (headContentID != null) {
						myPart.setContentId(headContentID[0]);
					}
					if (loadPartBody) {
						InputStream is = null;
						ByteArrayOutputStream baos = null;
						try {
							is = part.getInputStream();
							baos = new ByteArrayOutputStream();
							int byteCount = 0;
							while ((byteCount = is.read()) != -1) {
								baos.write(byteCount);
							}
							myPart.setContent(baos);
						} catch (Exception e) {
							log.error(e.toString());
						} finally {
							if (baos != null) {
								baos.close();
							}
							if (is != null) {
								is.close();
							}
						}
					}
					parts.add(myPart);
				} catch (Exception e) {
					log.error(e.toString());
				}
			}
		} catch (MessagingException e) {
			log.error("fetchParts", e);
		} catch (IOException e) {
			log.error("fetchParts", e);
		} finally {

		}
		return parts;
	}

	/**
	 * 根据邮件和附件名称获取附件输出流
	 * 
	 * @param part
	 * @param fileName
	 * @return
	 */
	public static final EmailPart parseMessagePart(Part part, String fileName) {
		EmailPart result = null;
		try {
			if (part.isMimeType("multipart/*")) {
				try {
					Multipart mp = (Multipart) part.getContent();
					int count = mp.getCount();
					for (int i = 0; i < count; i++) {
						result = parseMessagePart(mp.getBodyPart(i), fileName);
						if (result != null) {
							return result;
						}
					}
				} catch (Exception e) {
					log.error("Part is mimeType multipart/* but exception occured", e);
				}
			} else if (part.isMimeType("message/rfc822")) {
				result = parseMessagePart((Part) part.getContent(), fileName);
			} else {
				InputStream is = null;
				ByteArrayOutputStream baos = null;
				try {
					String name = (part.getFileName() == null) ? "rfc822.txt" : part.getFileName();
					if (!fileName.equals(Utility.decodeText(name))) {
						return null;
					}
					result = new EmailPart();
					result.setSize(part.getSize());
					result.setContentType(part.getContentType());
					result.setFileName(name);
					result.setDisposition(part.getDisposition());
					String headContentID[] = part.getHeader("Content-ID");
					if (headContentID != null) {
						result.setContentId(headContentID[0]);
					}
					is = part.getInputStream();
					baos = new ByteArrayOutputStream();
					int byteCount = 0;
					while ((byteCount = is.read()) != -1) {
						baos.write(byteCount);
					}
					result.setContent(baos);
				} catch (Exception e) {
					log.error(e.toString());
				} finally {
					if (baos != null) {
						baos.close();
					}
					if (is != null) {
						is.close();
					}
				}
			}
		} catch (Exception e) {
			log.error(e.toString());
		}
		return result;
	}

	/**
	 * @param email
	 * @param header
	 * @SuppressWarnings API不支持泛型
	 */
	//@SuppressWarnings("unchecked")
	public static void setHeaders(Message email, EmailHeader header) throws MessagingException {
		Enumeration<?> msgHeaders = email.getAllHeaders();
		Header msgHeader;
		String key;
		String value;
		while (msgHeaders.hasMoreElements()) {
			msgHeader = (Header) msgHeaders.nextElement();
			key = msgHeader.getName().toLowerCase();
			if (key.equals("disposition-notification-to")) {
				value = msgHeader.getValue().trim();
				if (value != null && value.length() > 0) {
					header.setRequestReceiptNotification(true);
					header.setReceiptNotificationEmail(value);
				}
			} else if (key.equals("x-priority")) {
				value = msgHeader.getValue().trim();
				try {
					header.setPriority(Short.valueOf(value).shortValue());
				} catch (Exception e) {
				}
			} else if (key.equals("x-msmail-priority")) {
				if (header.getPriority() == 0) {
					value = msgHeader.getValue().trim();
					try {
						header.setPriority(Short.valueOf(value).shortValue());
					} catch (Exception e) {
					}
				}
			} else if (key.equals("sensitivity")) {
				value = msgHeader.getValue().trim();
				try {
					header.setSensitivity(EmailSensitivity.valueOf(value));
				} catch (Exception e) {
					log.warn("Sensitivity exception!");
				}
			}
		}
	}
}
