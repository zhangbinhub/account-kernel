package OLink.bpm.core.email.attachment.dao;

import java.util.Collection;

import OLink.bpm.base.dao.IDesignTimeDAO;
import OLink.bpm.core.email.attachment.ejb.Attachment;
import OLink.bpm.core.email.email.ejb.Email;

public interface AttachmentDAO extends IDesignTimeDAO<Attachment> {

	Collection<Attachment> queryAttachmentByEmails(Email email) throws Exception;
	
	int queryAttachmentCountByEmail(Email email) throws Exception;
	
}
