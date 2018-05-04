package OLink.bpm.core.email.email.ejb;

import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.core.email.folder.ejb.EmailFolder;
import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.base.ejb.IDesignTimeProcess;

public interface EmailProcess extends IDesignTimeProcess<Email> {

	DataPackage<Email> getEmailsByFolderId(String folderid, ParamsTable params) throws Exception;
	
	DataPackage<Email> getEmailsByFolderUser(String folderid, ParamsTable params, EmailUser user) throws Exception;
	
	void doUpdateRead(String emailid) throws Exception;
	
	void doUpdateRead(String emailid, EmailFolder folder) throws Exception;
	
	void doMoveToOtherFolder(Email email, String otherFolderid) throws Exception;
	
	void doMoveTo(String[] ids, EmailFolder folder) throws Exception;
	
	void doMoveTo(String[] ids, EmailFolder folder, EmailFolder other) throws Exception;
	
	void doUpdateMarkRead(String[] ids, boolean read) throws Exception;
	
	void doUpdateMarkRead(String[] ids, boolean read, EmailFolder folder) throws Exception;
	
	void doToRecy(String[] ids) throws Exception;
	
	void doToRecy(String[] ids, String folderid) throws Exception;
	
	int getUnreadMessageCount(String folderid, EmailUser user) throws Exception;
	
	/**
	 * 只发送一封邮件
	 * @param email
	 * @return
	 * @throws Exception
	 */
	boolean sendEmail(Email email, EmailUser user) throws Exception;
	
	boolean sendEmail(Email email, EmailUser user, boolean self) throws Exception;
	
	int getEmailCount(EmailFolder folder, EmailUser user) throws Exception;
	
	Email getEmailByID(String id, EmailFolder folder) throws Exception;
	
	void doRemoveByFolder(String[] ids, EmailFolder folder) throws Exception;
	
	void doSaveEmail(Email email, EmailFolder folder) throws Exception;
	
}
