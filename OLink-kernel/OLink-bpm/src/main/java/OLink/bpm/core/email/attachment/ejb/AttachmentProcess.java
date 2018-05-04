package OLink.bpm.core.email.attachment.ejb;

import java.util.Collection;

import OLink.bpm.base.ejb.IDesignTimeProcess;
import OLink.bpm.core.email.email.ejb.Email;
import OLink.bpm.core.email.folder.ejb.EmailFolder;

public interface AttachmentProcess extends IDesignTimeProcess<Attachment> {

	Attachment getAttachment(String emailid, EmailFolder folder, String fileName) throws Exception;
	
	Collection<Attachment> getAttachmentsByEmail(Email email) throws Exception;
	
}
