package OLink.bpm.core.email.folder.ejb;

import java.util.Collection;

import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.base.ejb.IDesignTimeProcess;
import OLink.bpm.core.email.email.ejb.EmailUser;

public interface EmailFolderProcess extends IDesignTimeProcess<EmailFolder> {

	void initCreatDefaultMailFolder() throws Exception;

	EmailFolder getEmailFolderById(String folderid) throws Exception;

	EmailFolder getEmailFolderByOwnerId(String folderName, String ownerId) throws Exception;

	/**
	 * 根据文件名称判断该文件是否已创建
	 * 
	 * @param folderName
	 * @param ownerid
	 * @return
	 */
	boolean emailFolderIsCreate(String folderName, String ownerid) throws Exception;

	void doCreateEmailFolderByName(String folderName, String ownerId)
			throws Exception;
	
	Collection<EmailFolder> getSystemEmailFolders() throws Exception;
	
	EmailFolder getSystemEmailFolderByName(String folderName) throws Exception;
	
	DataPackage<EmailFolder> getPersonalEmailFolders(EmailUser user, ParamsTable params) throws Exception;

	void doRemoveEmailFolder(EmailFolder folder, EmailUser user) throws Exception;
	
	int getPersonalEmailFolderCount(String ownerid) throws Exception;
	
}
