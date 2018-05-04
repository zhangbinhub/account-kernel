package OLink.bpm.core.email.attachment.ejb;

import java.util.Collection;
import java.util.Date;

import OLink.bpm.base.dao.DAOFactory;
import OLink.bpm.base.dao.IDesignTimeDAO;
import OLink.bpm.base.dao.ValueObject;
import OLink.bpm.base.ejb.AbstractDesignTimeProcessBean;
import OLink.bpm.core.email.attachment.dao.AttachmentDAO;
import OLink.bpm.core.email.email.ejb.Email;
import OLink.bpm.core.email.email.ejb.EmailBody;
import OLink.bpm.core.email.email.ejb.EmailBodyProcess;
import OLink.bpm.core.email.email.ejb.EmailBodyProcessBean;
import OLink.bpm.core.email.email.ejb.EmailProcess;
import OLink.bpm.core.email.folder.ejb.EmailFolder;
import OLink.bpm.core.email.folder.ejb.EmailFolderProcess;
import OLink.bpm.core.email.folder.ejb.EmailFolderProcessBean;
import OLink.bpm.core.email.util.AttachmentUtil;
import OLink.bpm.util.ProcessFactory;

public class AttachmentProcessBean extends AbstractDesignTimeProcessBean<Attachment> implements
		AttachmentProcess {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1933447807515757290L;

	protected IDesignTimeDAO<Attachment> getDAO() throws Exception {
		return (AttachmentDAO) DAOFactory.getDefaultDAO(Attachment.class.getName());
	}
	
	public static void main(String[] args) {
		try {
			System.out.println(new AttachmentProcessBean().doView(""));
			EmailBodyProcess rp = new EmailBodyProcessBean();
			EmailBody body = new EmailBody();
			body.setContent("----");
			rp.doCreate(body);
			
			System.out.println(rp.doView(body.getId()));
			
			EmailFolderProcess ep = new EmailFolderProcessBean();
			ep.initCreatDefaultMailFolder();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Attachment getAttachment(String emailid,
			EmailFolder folder, String fileName) throws Exception {
		EmailProcess process = (EmailProcess) ProcessFactory.createProcess(EmailProcess.class);
		Email email = (Email) process.doView(emailid);
		if (email != null
				&& email.getEmailBody().isMultipart()) {
			for (Attachment attachment : email.getEmailBody().getAttachments()) {
				if (attachment.getRealFileName().equals(fileName)) {
					return attachment;
				}
			}
		}
		return null;
	}
	
	@Override
	public void doCreate(ValueObject vo) throws Exception {
		((Attachment)vo).setCreateDate(new Date());
		super.doCreate(vo);
	}
	
	@Override
	public void doRemove(String pk) throws Exception {
		Attachment attachment = (Attachment) doView(pk);
		if (attachment != null) {
			doRemove(attachment);
		}
	}
	
	@Override
	public void doRemove(ValueObject obj) throws Exception {
		super.doRemove(obj);
		AttachmentUtil.removeAttachmentFile(((Attachment)obj).getFileName());
	}

	public Collection<Attachment> getAttachmentsByEmail(Email email)
			throws Exception {
		return ((AttachmentDAO)getDAO()).queryAttachmentByEmails(email);
	}

}
