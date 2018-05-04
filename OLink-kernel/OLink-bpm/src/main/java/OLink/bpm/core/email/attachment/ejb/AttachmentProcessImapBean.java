package OLink.bpm.core.email.attachment.ejb;

import java.util.Collection;

import javax.mail.Folder;
import javax.mail.Message;

import OLink.bpm.core.email.runtime.mail.ImapProtocolImpl;
import OLink.bpm.core.email.runtime.mail.ProtocolFactory;
import OLink.bpm.core.email.runtime.parser.MessageParser;
import OLink.bpm.base.dao.IDesignTimeDAO;
import OLink.bpm.base.ejb.AbstractDesignTimeProcessBean;
import OLink.bpm.core.email.email.ejb.Email;
import OLink.bpm.core.email.folder.ejb.EmailFolder;
import OLink.bpm.core.email.runtime.model.EmailPart;
import OLink.bpm.core.email.util.Constants;
import OLink.bpm.util.StringUtil;

public class AttachmentProcessImapBean extends AbstractDesignTimeProcessBean<Attachment>
		implements AttachmentProcess {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3358596562359189962L;
	private transient ProtocolFactory protocolFactory;
	
	public AttachmentProcessImapBean(ProtocolFactory protocolFactory) throws Exception {
		this.protocolFactory = protocolFactory;
	}
	
	protected IDesignTimeDAO<Attachment> getDAO() throws Exception {
		return null;
	}

	public Attachment getAttachment(String emailid, EmailFolder folder,
			String fileName) throws Exception {
		String folderName = getFolderNameByEmailFolder(folder);
		ImapProtocolImpl protocol = (ImapProtocolImpl) protocolFactory.getImapProtocol(folderName);
		if (protocol != null) {
			protocol.connect(Constants.CONNECT_TYPE_READ_ONLY);
			Message message = protocol.getMessageByUID(Long.parseLong(emailid));
			if (message != null) {
				EmailPart part = MessageParser.parseMessagePart(message, fileName);
				if (part != null) {
					return Attachment.valueOf(part);
				}
			}
		}
		return null;
	}
	
	public String getFolderNameByEmailFolder(EmailFolder folder) {
		String folderName = folder.getName();
		if (StringUtil.isBlank(folderName)) {
			folderName = getFolderNameById(folder.getId());
		}
		return folderName;
	}
	
	public String getFolderNameById(String id) {
		Folder folder = protocolFactory.getConnectionMetaHandler().getFolderByUID(Long.parseLong(id));
		return folder.getFullName();
	}

	public Collection<Attachment> getAttachmentsByEmail(Email email)
			throws Exception {
		return null;
	}

}
