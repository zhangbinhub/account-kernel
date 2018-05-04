package OLink.bpm.core.email.email.ejb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.base.dao.DAOFactory;
import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.base.dao.IDesignTimeDAO;
import OLink.bpm.base.dao.ValueObject;
import OLink.bpm.core.email.email.dao.EmailDAO;
import OLink.bpm.core.email.folder.ejb.EmailFolder;
import OLink.bpm.core.email.folder.ejb.EmailFolderProcess;
import OLink.bpm.core.email.util.Constants;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.base.ejb.AbstractDesignTimeProcessBean;
import OLink.bpm.util.StringUtil;
import org.apache.commons.beanutils.PropertyUtils;

public class EmailProcessBean extends AbstractDesignTimeProcessBean<Email> implements
		EmailProcess {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1427896596014401743L;

	protected IDesignTimeDAO<Email> getDAO() throws Exception {
		return (EmailDAO) DAOFactory.getDefaultDAO(Email.class.getName());
	}

	public void doMoveToOtherFolder(Email message, String otherFolderId)
			throws Exception {
		//((EmailDAO)getDAO()).moveToOtherFolder(message, otherFolderId);
		EmailFolderProcess folderProcess = (EmailFolderProcess) ProcessFactory.createProcess(EmailFolderProcess.class);
		EmailFolder mailFolder = (EmailFolder) folderProcess.doView(otherFolderId);
		if (mailFolder == null) {
			throw new Exception("Invalid folder id!");
		}
		message.setEmailFolder(mailFolder);
		super.doUpdate(message);
	}
	
	@Override
	public void doUpdate(ValueObject vo) throws Exception {
		Email email = (Email) vo;
		EmailBody body = email.getEmailBody();
		if (body != null && !StringUtil.isBlank(body.getId())) {
			EmailBodyProcess bodyProcess = (EmailBodyProcess) ProcessFactory.createProcess(EmailBodyProcess.class);
			//body.setSendDate(new Date());
			bodyProcess.doUpdate(body);
		}
		super.doUpdate(vo);
	}

	public void doUpdateMarkRead(String[] ids, boolean flag) throws Exception {
		if (ids == null || ids.length == 0) {
			return;
		}
		for (int i = 0; i < ids.length; i++) {
			Email email = (Email) doView(ids[i]);
			if (email != null) {
				email.setRead(flag);
				doUpdate(email);
			}
		}
	}

	public void doUpdateRead(String messageId) throws Exception {
		((EmailDAO)getDAO()).updateRead(messageId);
	}

	public DataPackage<Email> getEmailsByFolderId(String folderid, ParamsTable params)
			throws Exception {
		return ((EmailDAO)getDAO()).queryMessageByFolderId(folderid, params);
	}

	private void doCreateFormSendBoxAndToInbox(Email email, EmailUser user) throws Exception {
		try {
			Email inboxEmail = new Email();
			EmailBodyProcess ebp = (EmailBodyProcess) ProcessFactory.createProcess(EmailBodyProcess.class);
			email.getEmailBody().setSendDate(new Date());
			if (StringUtil.isBlank(email.getEmailBody().getId())) {
				ebp.doCreate(email.getEmailBody());
			} else {
				ebp.doUpdate(email.getEmailBody());
			}
			PropertyUtils.copyProperties(inboxEmail, email);
			sendEmailToUser(inboxEmail, user);
			//EmailFolderProcess efp = (EmailFolderProcess) ProcessFactory.createProcess(EmailFolderProcess.class);
			//EmailFolder send = efp.getSystemEmailFolderByName(Constants.DEFAULT_FOLDER_SENT);
			email.setRead(true);
			//email.setEmailFolder(send);
			email.setEmailUser(user);
			if (StringUtil.isBlank(email.getId())) {
				doCreate(email);
			} else {
				//super.doUpdate(email);
				super.doRemove(email);
				email.setId(null);
				super.doCreate(email);
			}
		} catch (Exception e) {
			if (StringUtil.isBlank(email.getId())
					&& !StringUtil.isBlank(email.getEmailBody().getId())) {
				EmailBodyProcess ebp = (EmailBodyProcess) ProcessFactory.createProcess(EmailBodyProcess.class);
				ebp.doRemove(email.getEmailBody());
			}
			throw e;
		}
	}

	public void doToRecy(String[] ids) throws Exception {
		EmailFolderProcess efp = (EmailFolderProcess) ProcessFactory.createProcess(EmailFolderProcess.class);
		EmailFolder recyFolder = efp.getSystemEmailFolderByName(Constants.DEFAULT_FOLDER_REMOVED);
		if (recyFolder == null) {
			// 邮件系统还没初始化
			throw new Exception("E-mail system not initialized!");
		}
		for (int i = 0; i < ids.length; i++) {
			Email email = (Email) doView(ids[i]);
			email.setEmailFolder(recyFolder);
			super.doUpdate(email);
		}
	}

	public int getUnreadMessageCount(String folderid, EmailUser user) throws Exception {
		return ((EmailDAO)getDAO()).queryUnreadMessageCount(folderid, user);
	}

	public void doMoveTo(String[] ids, EmailFolder folder) throws Exception {
		String folderid = folder.getId();
		if (ids == null || ids.length == 0
				|| folderid == null || folderid.trim().equals("")) {
			return;
		}
		for (int i = 0; i < ids.length; i++) {
			Email email = (Email) doView(ids[i]);
			if (email != null) {
				doMoveToOtherFolder(email, folderid);
			}
		}
	}

	public DataPackage<Email> getEmailsByFolderUser(String folderid,
			ParamsTable params, EmailUser user) throws Exception {
		params.setParameter("emailUserid", user.getId());
		return ((EmailDAO)getDAO()).queryMessageByFolderId(folderid, params);
	}

	public boolean sendEmail(Email email, EmailUser user) throws Exception {
		return sendEmail(email, user, false);
	}

	public boolean sendEmail(Email email, EmailUser user, boolean self) throws Exception {
		try {
			if (self) {
				doCreateFormSendBoxAndToInbox(email, user);
			} else {
				if (!StringUtil.isBlank(email.getId())) {
					super.doUpdate(email);
				}
				Email inboxEmail = new Email();
				EmailBodyProcess ebp = (EmailBodyProcess) ProcessFactory.createProcess(EmailBodyProcess.class);
				email.getEmailBody().setSendDate(new Date());
				if (StringUtil.isBlank(email.getEmailBody().getId())) {
					ebp.doCreate(email.getEmailBody());
				}// else {
				//	ebp.doUpdate(email.getEmailBody());
				//}
				PropertyUtils.copyProperties(inboxEmail, email);
				sendEmailToUser(inboxEmail, user);
				if (!StringUtil.isBlank(email.getId())) {
					super.doRemove(email);
				}
			}
			return true;
		} catch (Exception e) {
			if (StringUtil.isBlank(email.getId())
					&& !StringUtil.isBlank(email.getEmailBody().getId())) {
				EmailBodyProcess ebp = (EmailBodyProcess) ProcessFactory.createProcess(EmailBodyProcess.class);
				ebp.doRemove(email.getEmailBody());
			}
			throw e;
		}
	}
	
	private void sendEmailToUser(Email email, EmailUser sendUser) throws Exception {
		EmailFolderProcess efp = (EmailFolderProcess) ProcessFactory.createProcess(EmailFolderProcess.class);
		EmailFolder ifo = efp.getSystemEmailFolderByName(Constants.DEFAULT_FOLDER_INBOX);
		EmailUserProcess eup = (EmailUserProcess) ProcessFactory.createProcess(EmailUserProcess.class);
		email.setId(null);
		email.setEmailFolder(ifo);
		email.setRead(false);
		//email.getEmailBody().setSendDate(new Date());
		Collection<ValueObject> receivers = new ArrayList<ValueObject>();
		for (Iterator<String> it = email.getReceiver().iterator(); it.hasNext(); ) {
			String receiver = it.next();
			Email temp = new Email();
			PropertyUtils.copyProperties(temp, email);
			EmailUser user = eup.getEmailUser(receiver, sendUser.getDomainid());
			if (user == null) {
				throw new Exception("找不到收件人!");
			}
			temp.setEmailUser(user);
			receivers.add(temp);
		}
		super.doCreate(receivers);
	}

	public int getEmailCount(EmailFolder folder, EmailUser user)
			throws Exception {
		return ((EmailDAO)getDAO()).queryEmailCount(folder.getId(), user);
	}

	@Override
	public void doCreate(ValueObject vo) throws Exception {
		Email email = (Email) vo;
		if (email.getEmailBody() == null || 
				StringUtil.isBlank(email.getEmailBody().getId())) {
			EmailBodyProcess ebp = (EmailBodyProcess) ProcessFactory.createProcess(EmailBodyProcess.class);
			ebp.doCreate(email.getEmailBody());
		}
		super.doCreate(vo);
	}

	public void doToRecy(String[] ids, String folderid) throws Exception {
		doToRecy(ids);
	}

	public void doUpdateMarkRead(String[] ids, boolean read, EmailFolder folder)
			throws Exception {
		doUpdateMarkRead(ids, read);
	}

	public void doUpdateRead(String emailid, EmailFolder folder)
			throws Exception {
		doUpdateMarkRead(new String[]{emailid}, true);
	}

	public void doMoveTo(String[] ids, EmailFolder folder, EmailFolder other)
			throws Exception {
		doMoveTo(ids, other);
	}

	public Email getEmailByID(String id, EmailFolder folder) throws Exception {
		return (Email) super.doView(id);
	}

	public void doRemoveByFolder(String[] ids, EmailFolder folder)
			throws Exception {
		super.doRemove(ids);
	}

	public void doSaveEmail(Email email, EmailFolder folder) throws Exception {
		if (StringUtil.isBlank(email.getId())) {
			this.doCreate(email);
		} else {
			this.doUpdate(email);
		}
	}
}
