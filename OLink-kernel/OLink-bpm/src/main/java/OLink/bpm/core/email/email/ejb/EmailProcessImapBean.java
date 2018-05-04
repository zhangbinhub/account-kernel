package OLink.bpm.core.email.email.ejb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.activation.FileDataSource;
import javax.mail.Folder;
import javax.mail.Message;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.base.dao.IDesignTimeDAO;
import OLink.bpm.base.dao.ValueObject;
import OLink.bpm.core.email.attachment.ejb.AttachmentProcess;
import OLink.bpm.core.email.folder.ejb.EmailFolder;
import OLink.bpm.base.ejb.AbstractDesignTimeProcessBean;
import OLink.bpm.core.email.attachment.ejb.Attachment;
import OLink.bpm.core.email.runtime.mail.ImapProtocolImpl;
import OLink.bpm.core.email.runtime.mail.ProtocolFactory;
import OLink.bpm.core.email.runtime.mail.Smtp;
import OLink.bpm.core.email.runtime.model.EmailHeader;
import OLink.bpm.core.email.runtime.model.EmailPart;
import OLink.bpm.core.email.runtime.parser.MessageParser;
import OLink.bpm.core.email.util.Constants;
import OLink.bpm.core.email.util.Utility;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.util.StringUtil;
import org.apache.log4j.Logger;

public class EmailProcessImapBean extends AbstractDesignTimeProcessBean<Email> implements
		EmailProcess {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4569714249064788780L;
	private transient ProtocolFactory protocolFactory;
	private static final Logger log = Logger.getLogger(EmailProcessImapBean.class);
	
	public EmailProcessImapBean(ProtocolFactory protocolFactory) throws Exception {
		this.protocolFactory = protocolFactory;
	}

	protected IDesignTimeDAO<Email> getDAO() throws Exception {
		return null;
	}

	public void doMoveTo(String[] ids, EmailFolder folder) throws Exception {
		throw new Exception("{*[core.email.internal.error]*}");
	}
	
	public void doMoveTo(String[] ids, EmailFolder folder, EmailFolder toFolder) throws Exception {
		String folderName = getFolderNameByEmailFolder(folder);
		ImapProtocolImpl protocol = (ImapProtocolImpl) protocolFactory.getImapProtocol(folderName);
		protocol.connect(Constants.CONNECT_TYPE_READ_WRITE);
		protocol.moveMessagesByUID(parseLongs(ids), getFolderNameByEmailFolder(toFolder));
		protocol.disconnect();
	}

	public void doMoveToOtherFolder(Email email, String otherFolderid)
			throws Exception {
		String folderName = getFolderNameByEmailFolder(email.getEmailFolder());
		ImapProtocolImpl protocol = (ImapProtocolImpl) protocolFactory.getImapProtocol(folderName);
		protocol.connect(Constants.CONNECT_TYPE_READ_WRITE);
		Folder folder = protocolFactory.getConnectionMetaHandler().getFolderByUID(parseLong(otherFolderid));
		protocol.moveMessageByUID(parseLong(email.getId()), folder.getFullName());
		protocol.disconnect();
	}

	public void doToRecy(String[] ids) throws Exception {
		throw new Exception("{*[core.email.internal.error]*}");
	}
	
	public void doToRecy(String[] ids, String folderid) throws Exception {
		ImapProtocolImpl protocol = (ImapProtocolImpl) protocolFactory.getImapProtocol(getFolderNameById(folderid));
		protocol.connect(Constants.CONNECT_TYPE_READ_WRITE);
		protocol.moveMessagesByUID(parseLongs(ids), Constants.DEFAULT_FOLDER_REMOVED);
		protocol.disconnect();
	}

	public void doUpdateMarkRead(String[] ids, boolean flag) throws Exception {
		throw new Exception("{*[core.email.internal.error]*}");
	}

	public void doUpdateRead(String messageId) throws Exception {
		throw new Exception("{*[core.email.internal.error]*}");
	}

	public DataPackage<Email> getEmailsByFolderId(String folderid, ParamsTable params)
			throws Exception {
		String _currpage = params.getParameterAsString("_currpage");
		String _pagelines = params.getParameterAsString("_pagelines");
		int page = (_currpage != null && _currpage.length() > 0) ? Integer.parseInt(_currpage) : 1;
		int lines = (_pagelines != null && _pagelines.length() > 0) ? Integer.parseInt(_pagelines) : 10;
		String folderName = getFolderNameById(folderid);
		ImapProtocolImpl protocol = (ImapProtocolImpl) protocolFactory.getImapProtocol(folderName);
		protocol.connect(Constants.CONNECT_TYPE_READ_WRITE);
		List<EmailHeader> result = protocol.fetchHeaders(page, lines, 0);
		DataPackage<Email> dataPackage = new DataPackage<Email>();
		dataPackage.rowCount = protocol.getTotalMessageCount();
		dataPackage.pageNo = page;
		dataPackage.linesPerPage = lines;
		Collection<Email> temp = new ArrayList<Email>();
		if (result != null) {
			for (Iterator<EmailHeader> it = result.iterator(); it.hasNext(); ) {
				EmailHeader eh = it.next();
				Email email = Email.valueOf(eh);
				if (email != null) {
					temp.add(email);
				}
			}
		}
		dataPackage.datas = temp;
		return dataPackage;
	}

	public DataPackage<Email> getEmailsByFolderUser(String folderid,
													ParamsTable params, EmailUser user) throws Exception {
		return getEmailsByFolderId(folderid, params);
	}

	public int getEmailCount(EmailFolder folder, EmailUser user)
			throws Exception {
		String folderName = getFolderNameByEmailFolder(folder);
		ImapProtocolImpl protocol = (ImapProtocolImpl) protocolFactory.getImapProtocol(folderName);
		protocol.connect(Constants.CONNECT_TYPE_READ_WRITE);
		return protocol.getTotalMessageCount();
	}

	public int getUnreadMessageCount(String folderid, EmailUser user)
			throws Exception {
		ImapProtocolImpl protocol = (ImapProtocolImpl) protocolFactory.getImapProtocol(getFolderNameById(folderid));
		protocol.connect(Constants.CONNECT_TYPE_READ_WRITE);
		return protocol.getUnreadMessageCount();
	}

	public boolean sendEmail(Email email, EmailUser user) throws Exception {
		return sendEmail(email, user, false);
	}

	public boolean sendEmail(Email email, EmailUser user, boolean self)
			throws Exception {
		try {
			if (self) {
				//IMAPFolder imapFolder = protocolFactory.getConnectionMetaHandler().getFolderByName(Constants.DEFAULT_FOLDER_SENT, Constants.SYSTEM_FOLDER_ID);
				//EmailFolder folder = EmailFolder.valueOf(imapFolder);
				//this.doSaveEmail(email, folder);
			}
			Smtp smtp = new Smtp(protocolFactory.getProfile(), protocolFactory.getAuth());
			//Map result = smtp.sendEmail(valueOf(email), false);
			smtp.sendEmail(valueOf(email), false);
			deleteAttachment(email);
			if (!StringUtil.isBlank(email.getId())) {
				ImapProtocolImpl protocol = (ImapProtocolImpl) protocolFactory.getImapProtocol(Constants.DEFAULT_FOLDER_DRAFTS);
				protocol.connect(Constants.CONNECT_TYPE_READ_WRITE);
				protocol.deletedMessageByUID(new long[]{Long.parseLong(email.getId())});
			}
			return true;
		} catch (Exception e) {
			log.warn(e);
			throw e;
		}
	}
	
	public OLink.bpm.core.email.runtime.model.Email valueOf(Email email) throws Exception {
		OLink.bpm.core.email.runtime.model.Email result = new OLink.bpm.core.email.runtime.model.Email();
		result.getBaseHeader().setBcc(Utility.stringToAddressArray(email.getEmailBody().getBcc()));
		result.getBaseHeader().setCc(Utility.stringToAddressArray(email.getEmailBody().getCc()));
		result.getBaseHeader().setFrom(Utility.stringToAddressArray(email.getEmailBody().getFrom()));
		result.getBaseHeader().setSubject(email.getEmailBody().getSubject());
		result.getBaseHeader().setTo(Utility.stringToAddressArray(email.getEmailBody().getTo()));
		EmailPart contentPart = new EmailPart();
		contentPart.setContent(email.getEmailBody().getContent(), "text/html;charset=utf-8");
		result.getParts().add(0, contentPart);
		if (email.getEmailBody().isMultipart()) {
			//AttachmentProcess aProcess = (AttachmentProcess) ProcessFactory.createProcess(AttachmentProcess.class);
			//Collection<Attachment> attachments = aProcess.getAttachmentsByEmail(email);
			Collection<Attachment> attachments = email.getEmailBody().getAttachments();
			for (Iterator<Attachment> it = attachments.iterator(); it.hasNext(); ) {
				Attachment attachment = it.next();
				EmailPart bodyPart = new EmailPart();
				String filename = attachment.getRealFileName();
				FileDataSource dataSource = new FileDataSource(attachment.getFileAllPath());
				bodyPart.setDataSource(dataSource);
				bodyPart.setFileName(filename);
				result.addPart(bodyPart);
			}
		}
		return result;
	}
	
	public String getFolderNameById(String id) {
		Folder folder = protocolFactory.getConnectionMetaHandler().getFolderByUID(parseLong(id));
		if (folder == null) {
			return "";
		}
		return folder.getFullName();
	}
	
	public String getFolderNameByEmailFolder(EmailFolder folder) {
		String folderName = folder.getName();
		if (StringUtil.isBlank(folderName)) {
			folderName = getFolderNameById(folder.getId());
		}
		return folderName;
	}

	private long parseLong(String str) {
		try {
			return Long.parseLong(str);
		} catch (Exception e) {
			return 0;
		}
	}
	
	private long[] parseLongs(String[] strs) {
		if (strs == null) {
			return new long[0];
		}
		long[] result = new long[strs.length];
		for (int i = 0; i < strs.length; i++) {
			result[i] = parseLong(strs[i]);
		}
		return result;
	}

	public void doUpdateMarkRead(String[] ids, boolean read, EmailFolder folder)
			throws Exception {
		String folderName = getFolderNameByEmailFolder(folder);
		ImapProtocolImpl protocol = (ImapProtocolImpl) protocolFactory.getImapProtocol(folderName);
		protocol.connect(Constants.CONNECT_TYPE_READ_WRITE);
		for (int i = 0; i < ids.length; i++) {
			protocol.markAsReadByUID(parseLong(ids[i]), read);
		}
	}

	public void doUpdateRead(String emailid, EmailFolder folder)
			throws Exception {
		String folderName = getFolderNameByEmailFolder(folder);
		ImapProtocolImpl protocol = (ImapProtocolImpl) protocolFactory.getImapProtocol(folderName);
		protocol.connect(Constants.CONNECT_TYPE_READ_WRITE);
		protocol.markAsReadByUID(parseLong(emailid));
	}
	
	@Override
	public ValueObject doView(String pk) throws Exception {
		throw new Exception("{*[core.email.internal.error]*}");
	}

	public Email getEmailByID(String id, EmailFolder folder) throws Exception {
		String folderName = getFolderNameByEmailFolder(folder);
		ImapProtocolImpl protocol = (ImapProtocolImpl) protocolFactory.getImapProtocol(folderName);
		protocol.connect(Constants.CONNECT_TYPE_READ_WRITE);
		Message message = protocol.getMessageByUID(parseLong(id));
		if (message != null) {
			Email email = Email.valueOf(MessageParser.parseMessage(message));
			if (email != null) {
				email.setId(id);
				email.getEmailFolder().setId(folder.getId());
				email.getEmailFolder().setName(folderName);
			}
			return email;
		}
		protocol.disconnect();
		return null;
	}
	
	public void doRemoveByFolder(String[] ids, EmailFolder folder)
		throws Exception {
		String folderName = getFolderNameByEmailFolder(folder);
		ImapProtocolImpl protocol = (ImapProtocolImpl) protocolFactory.getImapProtocol(folderName);
		protocol.connect(Constants.CONNECT_TYPE_READ_WRITE);
		protocol.deletedMessageByUID(parseLongs(ids));
		protocol.disconnect();
	}
	
	@Override
	public void doCreate(ValueObject vo) throws Exception {
		
	}
	
	@Override
	public void doUpdate(ValueObject vo) throws Exception {
		
	}
	
	public static void main(String[] args) throws Exception {
//		ConnectionProfile profile = EmailConfig.getConnectionProfile();
//		AuthProfile auth = new AuthProfile();
//		ConnectionMetaHandler handler = null;
//		auth.setUserName("");
//		auth.setPassword("");
//		ProtocolFactory factory = new ProtocolFactory(profile, auth, handler);
//		EmailProcessImapBean bean = new EmailProcessImapBean(factory);
		
//		ParamsTable params = new ParamsTable();
//		DataPackage package1 = bean.getEmailsByFolderId("1", params);
//		for(Iterator it = package1.datas.iterator(); it.hasNext(); ) {
//			EmailHeader eh = (EmailHeader) it.next();
//			System.out.println(eh.getDateString() + "  " + eh.getSubject());
//		}
		
//		Email email = new Email();
//		email.getEmailBody().setFrom("taowei160@163.com");
//		email.getEmailBody().setTo("411238450@qq.com");
//		email.getEmailBody().setContent("这是一封测试邮件！请注意查收。");
//		email.getEmailBody().setSubject("这是一封测试邮件！请注意查收。");
//		Set attachments = new HashSet();
//		Attachment att = new Attachment();
//		att.setRealFileName("android.txt");
//		att.setFileName("android.txt");
//		att.setPath("C:\\Users\\Tom\\Desktop\\资料管理");
//		attachments.add(att);
//		email.getEmailBody().setAttachments(attachments);
//		bean.sendEmail(email, null);
	}

	public void doSaveEmail(Email email, EmailFolder folder) throws Exception {
		if (folder == null) {
			throw new Exception("EmailFolder can't null");
		}
		String folderName = getFolderNameByEmailFolder(folder);
		ImapProtocolImpl protocol = (ImapProtocolImpl) protocolFactory.getImapProtocol(folderName);
		protocol.connect(Constants.CONNECT_TYPE_READ_WRITE);
		OLink.bpm.core.email.runtime.model.Email modelEmail = protocol.appendMessage(valueOf(email));
		if (modelEmail != null) {
			if (!StringUtil.isBlank(email.getId())) {
				protocol.deletedMessageByUID(new long[]{Long.parseLong(email.getId())});
			}
			email.setId(String.valueOf(modelEmail.getUid()));
			saveAttachment(email);
		}
	}
	
	private void saveAttachment(Email email) throws Exception {
		AttachmentProcess process = (AttachmentProcess) ProcessFactory.createProcess(AttachmentProcess.class);
		if (email.getEmailBody().isMultipart()) {
			for (Attachment att : email.getEmailBody().getAttachments()) {
				att.setEmailid(email.getId());
				att.setEmailBody(null);
				process.doUpdate(att);
			}
		}
	}
	
	private void deleteAttachment(Email email) throws Exception {
		AttachmentProcess process = (AttachmentProcess) ProcessFactory.createProcess(AttachmentProcess.class);
		if (email.getEmailBody().isMultipart()) {
			for (Attachment att : email.getEmailBody().getAttachments()) {
				att.setEmailid(email.getId());
				process.doRemove(att);
			}
		}
	}

}
