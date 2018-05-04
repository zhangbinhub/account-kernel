package OLink.bpm.core.email.folder.ejb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.base.dao.DAOFactory;
import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.base.dao.IDesignTimeDAO;
import OLink.bpm.core.email.email.ejb.EmailProcess;
import OLink.bpm.util.StringUtil;
import OLink.bpm.base.ejb.AbstractDesignTimeProcessBean;
import OLink.bpm.core.email.util.Constants;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.core.email.email.ejb.EmailUser;
import OLink.bpm.core.email.folder.dao.EmailFolderDAO;

public class EmailFolderProcessBean extends AbstractDesignTimeProcessBean<EmailFolder> implements
		EmailFolderProcess {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4673836509540552104L;

	public EmailFolderProcessBean() {
		try {
			initCreatDefaultMailFolder();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	protected IDesignTimeDAO<EmailFolder> getDAO() throws Exception {
		return (EmailFolderDAO) DAOFactory.getDefaultDAO(EmailFolder.class.getName());
	}

	public void doCreateEmailFolderByName(String folderName, String ownerId)
			throws Exception {
		EmailFolder folder = new EmailFolder();
		folder.setOwnerId(ownerId);
		folder.setCreateDate(new Date());
		folder.setName(folderName);
		folder.setDisplayName(Constants.getFolderDisplay(folderName));
		super.doCreate(folder);
	}

	public void initCreatDefaultMailFolder() throws Exception {
		if (!emailFolderIsCreate(Constants.DEFAULT_FOLDER_DRAFTS, Constants.SYSTEM_FOLDER_ID)) {
			doCreateEmailFolderByName(Constants.DEFAULT_FOLDER_DRAFTS, Constants.SYSTEM_FOLDER_ID);
		}
		if (!emailFolderIsCreate(Constants.DEFAULT_FOLDER_INBOX, Constants.SYSTEM_FOLDER_ID)) {
			doCreateEmailFolderByName(Constants.DEFAULT_FOLDER_INBOX, Constants.SYSTEM_FOLDER_ID);
		}
		if (!emailFolderIsCreate(Constants.DEFAULT_FOLDER_JUNK, Constants.SYSTEM_FOLDER_ID)) {
			doCreateEmailFolderByName(Constants.DEFAULT_FOLDER_JUNK, Constants.SYSTEM_FOLDER_ID);
		}
		if (!emailFolderIsCreate(Constants.DEFAULT_FOLDER_SENT, Constants.SYSTEM_FOLDER_ID)) {
			doCreateEmailFolderByName(Constants.DEFAULT_FOLDER_SENT, Constants.SYSTEM_FOLDER_ID);
		}
		
		if (!emailFolderIsCreate(Constants.DEFAULT_FOLDER_REMOVED, Constants.SYSTEM_FOLDER_ID)) {
			doCreateEmailFolderByName(Constants.DEFAULT_FOLDER_REMOVED, Constants.SYSTEM_FOLDER_ID);
		}
	}

	public EmailFolder getEmailFolderById(String folderid) throws Exception {
		return ((EmailFolderDAO)getDAO()).queryMailFolderById(folderid);
	}

	public EmailFolder getEmailFolderByOwnerId(String folderName, String ownerId) throws Exception {
		return ((EmailFolderDAO)getDAO()).queryMailFolderByOwnerId(folderName, ownerId);
	}

	public boolean emailFolderIsCreate(String folderName, String ownerid) throws Exception {
		return ((EmailFolderDAO)getDAO()).judgeMailFolderIsCreate(folderName, ownerid);
	}

	public DataPackage<EmailFolder> getPersonalEmailFolders(EmailUser user, ParamsTable params) throws Exception {
		return ((EmailFolderDAO)getDAO()).queryPersonalEmailFolders(user.getId(), params);
	}
	
	public Collection<EmailFolder> getSystemEmailFolders() throws Exception {
		Collection<EmailFolder> result = new ArrayList<EmailFolder>();
		if (!StringUtil.isBlank(Constants.DEFAULT_FOLDER_INBOX)) {
			result.add(getEmailFolderByOwnerId(Constants.DEFAULT_FOLDER_INBOX, Constants.SYSTEM_FOLDER_ID));
		}
		if (!StringUtil.isBlank(Constants.DEFAULT_FOLDER_DRAFTS)) {
			result.add(getEmailFolderByOwnerId(Constants.DEFAULT_FOLDER_DRAFTS, Constants.SYSTEM_FOLDER_ID));
		}
		if (!StringUtil.isBlank(Constants.DEFAULT_FOLDER_SENT)) {
			result.add(getEmailFolderByOwnerId(Constants.DEFAULT_FOLDER_SENT, Constants.SYSTEM_FOLDER_ID));
		}
		if (!StringUtil.isBlank(Constants.DEFAULT_FOLDER_REMOVED)) {
			result.add(getEmailFolderByOwnerId(Constants.DEFAULT_FOLDER_REMOVED, Constants.SYSTEM_FOLDER_ID));
		}
		if (!StringUtil.isBlank(Constants.DEFAULT_FOLDER_JUNK)) {
			result.add(getEmailFolderByOwnerId(Constants.DEFAULT_FOLDER_JUNK, Constants.SYSTEM_FOLDER_ID));
		}
		return result;
	}

	public EmailFolder getSystemEmailFolderByName(String folderName)
			throws Exception {
		return getEmailFolderByOwnerId(folderName, Constants.SYSTEM_FOLDER_ID);
	}
	
	public void doRemoveEmailFolder(EmailFolder folder, EmailUser user) throws Exception {
		if (folder != null && !StringUtil.isBlank(folder.getId())) {
			EmailProcess ep = (EmailProcess) ProcessFactory.createProcess(EmailProcess.class);
			int count = ep.getEmailCount(folder, user);
			if (count > 0) {
				throw new Exception("文件夹存在邮件，不能被删除！");
			}
			super.doRemove(folder);
		}
	}

	public int getPersonalEmailFolderCount(String ownerid)
			throws Exception {
		return ((EmailFolderDAO)getDAO()).queryPersonalEmailFolderCount( ownerid);
	}

}
