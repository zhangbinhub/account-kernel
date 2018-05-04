package OLink.bpm.core.email.email.dao;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.base.dao.IDesignTimeDAO;
import OLink.bpm.core.email.email.ejb.Email;
import OLink.bpm.core.email.email.ejb.EmailUser;

public interface EmailDAO extends IDesignTimeDAO<Email> {

	DataPackage<Email> queryMessageByFolderId(String folderid, ParamsTable params) throws Exception;
	
	void updateRead(String emailId) throws Exception;
	
	void moveToOtherFolder(Email email, String otherFolderId) throws Exception;
	
	void updateMarkRead(String[] ids) throws Exception;
	
	int queryUnreadMessageCount(String folderid, EmailUser user) throws Exception;
	
	int queryEmailCount(String folderid, EmailUser user) throws Exception;
}
